package com.tv.yuvipepmediaserver.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.tv.yuvipepmediaserver.config.BucketName;
import com.tv.yuvipepmediaserver.image.ImageRepository;
import com.tv.yuvipepmediaserver.model.Image;
import com.tv.yuvipepmediaserver.utils.Utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StreamService {

  Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  FileStore fileStoreService;

  @Autowired
  ImageRepository imageRepository;

  public ResponseEntity<byte[]> getFileContent(String name, String type) {
    try {
      byte[] content = fileStoreService.download(BucketName.YUVIPEP_CONTENT.toString(),
          type + "/" + name);
      ObjectMetadata metadata = fileStoreService.getMetaData(BucketName.YUVIPEP_CONTENT.toString(),
          type + "/" + name);
      return ResponseEntity.ok()
          .contentType(Utils.getContentType(metadata))
          .header("Content-Disposition", "inline; filename=" + name)
          .body(content);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
    }

  }

  public ResponseEntity<byte[]> getFileContent(String imageId) {
    try {
      logger.debug("getFileContent :", imageId);
      Optional<Image> imageFromDB = imageRepository.findById(imageId);
      if (imageFromDB.isPresent()) {
        String path = imageFromDB.get().getS3Path();
        String imageName = imageFromDB.get().getName();
        logger.debug("getFileContent Is Present [{}]", imageFromDB.get());

        byte[] content = fileStoreService.download(BucketName.YUVIPEP_CONTENT.toString(),
            path);
        ObjectMetadata metadata = fileStoreService.getMetaData(BucketName.YUVIPEP_CONTENT.toString(),
            path);
        return ResponseEntity.ok()
            .contentType(Utils.getContentType(metadata))
            .header("Content-Disposition", "inline; filename=" + imageName)
            .body(content);

      } else {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image Not Found".getBytes());

      }

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
    }

  }

  public ResponseEntity<Map<String, String>> uploadContent(MultipartFile file, String folder) throws IOException {
    Pattern pattern = Pattern.compile("([^\\s]+(\\.(?i)(jpg|JPG|jpeg|JPEG|png|PNG))$)");
    Matcher matcher = pattern.matcher(file.getOriginalFilename());
    Integer fileSizeLimit = 5 * 1024 * 1024;
    Map<String, String> content = new HashMap<>();
    if (!matcher.find() || file.getSize() > fileSizeLimit) {
      if (!matcher.find()) {
        content.put("message", "Wrong file format. Please upload image again");    
      } else {
        content.put("message", "Please upload image less than 5 MB");
      }
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(content);
    }
    String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename().toLowerCase().replaceAll(" ", "-");

    logger.debug("filename: " + fileName);

    try {
        fileStoreService.upload(BucketName.YUVIPEP_CONTENT.toString(), folder + fileName, file);
        content.put("status", "200");
        content.put("message", "Your file has been uploaded successfully!");
        content.put("flieName", fileName);
        return ResponseEntity.ok()
        .body(content);
    } catch (Exception ex) {
        ex.printStackTrace();
        content.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(content);
    }

  }

  public ResponseEntity<Map<String, String>> deleteContent(String fileName, String folder) throws IOException {
    Map<String, String> content = new HashMap<>();
    try {
        fileStoreService.remove(BucketName.YUVIPEP_CONTENT.toString(), folder + fileName);
        content.put("status", "200");
        content.put("message", "Your file has been deleted successfully!");
        content.put("flieName", fileName);
        return ResponseEntity.ok()
        .body(content);
    } catch (Exception ex) {
        ex.printStackTrace();
        content.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(content);
    }

  }

  public ResponseEntity<Map<String, String>> deleteContents(List<String> fileNames, String folder) throws IOException {
    Map<String, String> content = new HashMap<>();
    try {
      fileNames.replaceAll(fileName -> folder + fileName);
      String[] fileNameArray = fileNames.toArray(new String[fileNames.size()]);
      fileStoreService.removeFiles(BucketName.YUVIPEP_CONTENT.toString(), fileNameArray);
      content.put("status", "200");
      content.put("message", "Your files are deleted successfully!");
      return ResponseEntity.ok()
      .body(content);
    } catch (Exception ex) {
        ex.printStackTrace();
        content.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(content);
    }
  }

}
