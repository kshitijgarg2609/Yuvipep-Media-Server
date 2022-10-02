package com.tv.yuvipepmediaserver.video;

import org.apache.tomcat.util.http.parser.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.tv.yuvipepmediaserver.config.BucketName;
import com.tv.yuvipepmediaserver.utils.Utils;

import static com.tv.yuvipepmediaserver.video.VideoConstants.*;

@Service
public class VideoStreamService {

    @Value("${video.folder.path:/opt/learning-app/videos/}")
    private String videoBasePath;

    @Autowired
    AmazonS3 s3client;
    static ConcurrentHashMap<File,String> map = new ConcurrentHashMap<>();
    final static long status_timeout=5000;
    final static long download_update_timeout=5000;
    final static long client_update_timeout=4200;
    static TreeMap<Long,Set<File>> status_scheduler = new TreeMap<>();
    static HashMap<File,Long> file_time_entry = new HashMap<>();
    static TreeMap<Long,Set<File>> file_scheduler = new TreeMap<>();
    static SynchronousQueue<VideoDownloadCommand> sq = new SynchronousQueue<>(true);
    static File download_dir = new File(System.getProperty("user.dir"),"download_dir");
    static Thread loop = new Thread()
    {
        public void run()
        {
            while(true)
            {
                try
                {
                    VideoDownloadCommand vdc = sq.poll();
                    if(vdc!=null)
                    {
                        if(vdc.getDownloadStatus().equals(DownloadStatus.not_started) && !map.containsKey(vdc.getFile()))
                        {
                            map.put(vdc.getFile(),DownloadStatus.in_progress);
                            long ntime=forwardTime(download_update_timeout);
                            file_time_entry.put(vdc.getFile(),ntime);
                            addFileSchedulerEntry(ntime, vdc.getFile());
                        }
                        else if(vdc.getDownloadStatus().equals(DownloadStatus.in_progress))
                        {
                            updateStatus(vdc.getFile());
                        }
                        else if(vdc.getDownloadStatus().equals(DownloadStatus.download_fail))
                        {
                            updateStatusFail(vdc.getFile());
                        }
                        else if(vdc.getDownloadStatus().equals(DownloadStatus.download_pass))
                        {
                            updateStatusPass(vdc.getFile());
                        }
                    }
                    //process update
                    Long fst_file_ent=file_scheduler.firstKey();
                    if(fst_file_ent!=null && System.currentTimeMillis()>=fst_file_ent.longValue())
                    {
                        Map.Entry<Long,Set<File>> fst_ent=file_scheduler.pollFirstEntry();
                        for(File f : fst_ent.getValue())
                        {
                            file_time_entry.remove(f);
                            map.put(f,DownloadStatus.download_fail);
                            long ntime=forwardTime(download_update_timeout);
                            addStatusSchedulerEntry(ntime,f);
                        }
                    }
                    //status update
                    Long fst_status_ent=status_scheduler.firstKey();
                    if(fst_status_ent!=null && System.currentTimeMillis()>=fst_status_ent.longValue())
                    {
                        Map.Entry<Long,Set<File>> fst_ent=status_scheduler.pollFirstEntry();
                        for(File f : fst_ent.getValue())
                        {
                            map.remove(f);
                        }
                    }
                }
                catch(Exception e)
                {

                }
            }
        }
        void updateStatus(File f)
        {
            if(file_time_entry.containsKey(f))
            {
                removeFileSchedulerEntry(file_time_entry.get(f),f);
                long ntime=forwardTime(download_update_timeout);
                file_time_entry.put(f,ntime);
                addFileSchedulerEntry(ntime, f);
            }
        }
        void updateStatusFail(File f)
        {
            if(file_time_entry.containsKey(f))
            {
                removeFileSchedulerEntry(file_time_entry.remove(f),f);
            }
            map.put(f,DownloadStatus.download_fail);
            long ntime=forwardTime(status_timeout);
            addStatusSchedulerEntry(ntime,f);
        }
        void updateStatusPass(File f)
        {
            if(file_time_entry.containsKey(f))
            {
                removeFileSchedulerEntry(file_time_entry.remove(f),f);
            }
            map.put(f,DownloadStatus.download_pass);
        }
        void addFileSchedulerEntry(Long ptime,File f)
        {
            if(file_scheduler.containsKey(ptime))
            {
                Set<File> set = file_scheduler.getOrDefault(ptime,new HashSet<>());
                set.add(f);
                file_scheduler.put(ptime,set);
            }
        }
        void removeFileSchedulerEntry(Long ptime,File f)
        {
            if(file_scheduler.containsKey(ptime))
            {
                Set<File> set = file_scheduler.remove(ptime);
                set.remove(f);
                if(set.size()>0)
                {
                    file_scheduler.put(ptime, set);
                }
            }
        }
        void addStatusSchedulerEntry(Long ptime,File f)
        {
            if(status_scheduler.containsKey(ptime))
            {
                Set<File> set = status_scheduler.getOrDefault(ptime,new HashSet<>());
                set.add(f);
                status_scheduler.put(ptime,set);
            }
        }
        long forwardTime(long n)
        {
            return (System.currentTimeMillis()+n);
        }
    }
    ;
    static
    {
        try
        {
            if(!download_dir.exists())
            {
                download_dir.mkdir();
            }
            for(File ff : download_dir.listFiles())
            {
                ff.delete();
            }
        }
        catch(Exception e){}
        loop.start();
    }
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Prepare the content.
     *
     * @param fileName String.
     * @param fileType String.
     * @param range    String.
     * @return ResponseEntity.
     */
    public ResponseEntity<byte[]> prepareContent(String fileName, String range) {
        long rangeStart = 0;
        long rangeEnd;
        byte[] data;
        Long fileSize;
        String fullFileName = fileName;
        logger.debug("Filepath : {}/{}", getFilePath(), fullFileName);
        try {
            fileSize = getFileSize(fullFileName);
            logger.debug("FileSize : {} Range: {}", fileSize, range);
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(Utils.getContentType(getFilePath() + "/" + fullFileName))
                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(readByteRange(fullFileName, rangeStart, fileSize - 1)); // Read the object and convert it
                                                                                      // as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(fullFileName, rangeStart, rangeEnd);
        } catch (IOException e) {
            logger.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .contentType(Utils.getContentType(getFilePath() + "/" + fullFileName))
                .body(data);

    }

    public byte[] readByteRange(String filename, long start, long end) throws IOException {

        Path path = Paths.get(getFilePath(), filename);
        try (InputStream inputStream = (Files.newInputStream(path));
                ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[BYTE_RANGE];
            int nRead;
            inputStream.skip(start);

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                bufferedOutputStream.write(data, 0, nRead);
            }
            bufferedOutputStream.flush();
            byte[] result = new byte[(int) (end - start) + 1];
            System.arraycopy(bufferedOutputStream.toByteArray(), 0, result, 0, result.length);
            return result;
        }
        // try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(
        // path, EnumSet.of(StandardOpenOption.READ))) {
        // logger.debug("File Size: {}", fileChannel.size());
        // MappedByteBuffer mappedByteBuffer = fileChannel
        // .map(FileChannel.MapMode.READ_ONLY, start, end);

        // if (mappedByteBuffer != null) {
        // byte[] bytesBuffer = new byte[mappedByteBuffer.remaining()];
        // mappedByteBuffer.get(bytesBuffer);
        // return bytesBuffer;
        // }
        // }

        // return null;
    }

    /**
     * Get the filePath.
     *
     * @return String.
     */
    private String getFilePath() {
        return new File(videoBasePath).getAbsolutePath();
    }

    /**
     * Content length.
     *
     * @param fileName String.
     * @return Long.
     */
    public Long getFileSize(String fileName) {
        return Optional.ofNullable(fileName)
                .map(file -> Paths.get(getFilePath(), file))
                .map(this::sizeFromFile)
                .orElse(0L);
    }

    /**
     * Getting the size from the path.
     *
     * @param path Path.
     * @return Long.
     */
    private Long sizeFromFile(Path path) {
        try {
            return Files.size(path);
        } catch (IOException ioException) {
            logger.error("Error while getting the file size", ioException);
        }
        return 0L;
    }

    public ResponseEntity<StreamingResponseBody> prepareContent(String fileName, String range, String type) {
        long rangeStart = 0;
        long rangeEnd;
        StreamingResponseBody data;
        Long fileSize;
        ObjectMetadata metadata;
        try {
            S3Object object = s3client.getObject(BucketName.YUVIPEP_CONTENT.toString(), type + "/" + fileName);
            S3ObjectInputStream finalObject = object.getObjectContent();
            metadata = object.getObjectMetadata();
            fileSize = object.getObjectMetadata().getContentLength();

            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(Utils.getContentType(metadata))
                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(readByteRange(finalObject, rangeStart, fileSize - 1)); // Read the object and convert it
                                                                                     // as bytes
            }
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(finalObject, rangeStart, rangeEnd);
        } catch (Exception e) {
            data = null;
            logger.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .contentType(Utils.getContentType(metadata))
                .body(data);
    }

    public StreamingResponseBody readByteRange(S3ObjectInputStream inputStream, long start, long end) {

        StreamingResponseBody body = null;

        try {
            inputStream.skip(start);

            body = outputStream -> {
                int numberOfBytesToWrite = 0;
                byte[] data = new byte[BYTE_RANGE];
                while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, numberOfBytesToWrite);
                }
                outputStream.flush();
                byte[] result = new byte[(int) (end - start) + 1];
                outputStream.write(result, 0, result.length);
                // System.arraycopy(outputStream., 0, result, 0, result.length);

                inputStream.close();
            };
        } catch (IOException ex) {
            logger.error("Error While Loading", ex);
        }
        // try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(
        // path, EnumSet.of(StandardOpenOption.READ))) {
        // logger.debug("File Size: {}", fileChannel.size());
        // MappedByteBuffer mappedByteBuffer = fileChannel
        // .map(FileChannel.MapMode.READ_ONLY, start, end);

        // if (mappedByteBuffer != null) {
        // byte[] bytesBuffer = new byte[mappedByteBuffer.remaining()];
        // mappedByteBuffer.get(bytesBuffer);
        // return bytesBuffer;
        // }
        // }

        return body;
    }
    //download function
    boolean downloadFileFromAmazonS3(String fileName, String type, File dest)
    {
        try
        {
            S3Object object = s3client.getObject(BucketName.YUVIPEP_CONTENT.toString(), type+"/"+fileName);
            S3ObjectInputStream finalObject = object.getObjectContent();
            FileOutputStream fos = new FileOutputStream(dest);
            byte arr[] = new byte[BYTE_RANGE];
            int rlen;
            long snp=System.currentTimeMillis();
            while((rlen=finalObject.read(arr,0,BYTE_RANGE))>0)
            {
                if((System.currentTimeMillis()-snp)>=client_update_timeout)
                {
                    sq.put(new VideoDownloadCommand(dest,DownloadStatus.in_progress));
                    snp=System.currentTimeMillis();
                }
                fos.write(arr,0,rlen);
            }
            fos.close();
            object.close();
            return true;
        }
        catch(Exception e)
        {

        }
        return false;
    }
    //File Streaming
    public ResponseEntity<StreamingResponseBody> prepareStreamFileContent(String fileName, String range, String type) {
        long rangeStart = 0;
        long rangeEnd;
        StreamingResponseBody data=null;
        Long fileSize;
        logger.info("check #####################");
        File fff = new File(download_dir,fileName);
        try
        {
            if(!map.containsKey(fff))
            {
                sq.put(new VideoDownloadCommand(fff));
                if(downloadFileFromAmazonS3(fileName, "videos",fff))
                {
                    sq.put(new VideoDownloadCommand(fff,DownloadStatus.download_pass));
                }
                else
                {
                    sq.put(new VideoDownloadCommand(fff,DownloadStatus.download_fail));
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                }
            }
            String res=null;
            while((res=map.get(fff))!=null && res.equals(DownloadStatus.in_progress))
            {
                Thread.sleep(client_update_timeout);
            }
            res=map.get(fff);
            if(res.equals(DownloadStatus.download_fail))
            {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
            fileSize=fff.length();
            if (range == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .contentType(Utils.getContentType(fff.getAbsolutePath()))
                        .header(CONTENT_LENGTH, String.valueOf(fileSize))
                        .body(readByteRange(fff.toPath(), rangeStart, fileSize - 1)); // Read the object and convert it
                                                                                     // as bytes
            }
            logger.debug("################## check range :- "+range);
            String[] ranges = range.split("-");
            rangeStart = Long.parseLong(ranges[0].substring(6));
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            } else {
                rangeEnd = fileSize - 1;
            }
            if (fileSize < rangeEnd) {
                rangeEnd = fileSize - 1;
            }
            data = readByteRange(fff.toPath(), rangeStart, rangeEnd);
        } catch (Exception e) {
            data = null;
            logger.error("Exception while reading the file {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
        }
        String contentLength = String.valueOf((rangeEnd - rangeStart) + 1);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(ACCEPT_RANGES, BYTES)
                .header(CONTENT_LENGTH, contentLength)
                .header(CONTENT_RANGE, BYTES + " " + rangeStart + "-" + rangeEnd + "/" + fileSize)
                .contentType(Utils.getContentType(fff.getAbsolutePath()))
                .body(data);
    }
    public StreamingResponseBody readByteRange(Path path, long start, long end) {

        StreamingResponseBody body = null;

        try {
            FileChannel fc = FileChannel.open(path,StandardOpenOption.READ).position(start);
            body = outputStream -> {
                ByteBuffer bb = ByteBuffer.allocate(BYTE_RANGE);
                while(fc.read(bb)>0)
                {
                    outputStream.write(bb.array(),0,bb.position());
                    bb.clear();
                }
                outputStream.flush();
                byte[] result = new byte[(int) (end - start) + 1];
                outputStream.write(result, 0, result.length);
                // System.arraycopy(outputStream., 0, result, 0, result.length);

                fc.close();
            };
        } catch (IOException ex) {
            logger.error("Error While Loading", ex);
        }
        // try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(
        // path, EnumSet.of(StandardOpenOption.READ))) {
        // logger.debug("File Size: {}", fileChannel.size());
        // MappedByteBuffer mappedByteBuffer = fileChannel
        // .map(FileChannel.MapMode.READ_ONLY, start, end);

        // if (mappedByteBuffer != null) {
        // byte[] bytesBuffer = new byte[mappedByteBuffer.remaining()];
        // mappedByteBuffer.get(bytesBuffer);
        // return bytesBuffer;
        // }
        // }

        return body;
    }
}
