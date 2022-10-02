package com.tv.yuvipepmediaserver.video;

import java.io.File;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class VideoFileInfo {
    private Long fileSize;
    private ObjectMetadata metaData;
    private File downloadedFile;
    VideoFileInfo(ObjectMetadata metaData,File downloadPath)
    {
        setMetaData(metaData);
        setdownloadedFile(downloadedFile);
    }
    public void setFileSize(Long fileSize)
    {
        this.fileSize=fileSize;
    }
    public Long getFileSize()
    {
        return this.fileSize;
    }

    public void setMetaData(ObjectMetadata metaData)
    {
        this.metaData=metaData;
        setFileSize(metaData.getContentLength());
    }
    public ObjectMetadata getMetaData()
    {
        return this.metaData;
    }

    public void setdownloadedFile(File downloadedFile)
    {
        this.downloadedFile=downloadedFile;
    }
    public File getdownloadedFile()
    {
        return this.downloadedFile;
    }
}
