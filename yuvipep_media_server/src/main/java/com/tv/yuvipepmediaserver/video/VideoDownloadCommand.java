package com.tv.yuvipepmediaserver.video;

import java.io.File;

public class VideoDownloadCommand {
    private File file;
    private String type;
    private String downloadStatus;
    VideoDownloadCommand(File file)
    {
        this(file,DownloadStatus.not_started);
    }
    VideoDownloadCommand(File file,String downloadStatus)
    {
        this(file, "videos",downloadStatus);
    }
    VideoDownloadCommand(File file,String type,String downloadStatus)
    {
        setFile(file);
        setType(type);
        setDownloadStatus(downloadStatus);
    }

    public void setFile(File file)
    {
        this.file=file;
    }
    public File getFile()
    {
        return this.file;
    }

    public void setType(String type)
    {
        this.type=type;
    }
    public String getType()
    {
        return this.type;
    }

    public void setDownloadStatus(String downloadStatus)
    {
        this.downloadStatus=downloadStatus;
    }
    
    String getDownloadStatus()
    {
        return this.downloadStatus;
    }
}
