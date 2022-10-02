package com.tv.yuvipepmediaserver.utils;

import java.io.File;

import org.springframework.http.MediaType;
import javax.activation.FileTypeMap;

import com.amazonaws.services.s3.model.ObjectMetadata;

public class Utils {
    public static MediaType getContentType(String fileName) {
        File file = new File(fileName);
        MediaType mediaType = MediaType.ALL;
        if (fileName != null && fileName.contains(".svg")) {
            mediaType = MediaType.parseMediaType("image/svg+xml");
        } else if (fileName != null && fileName.contains(".mp4")) {
            mediaType = MediaType.parseMediaType("video/mp4");
        } else if (fileName != null && fileName.contains(".pdf")) {
            mediaType = MediaType.parseMediaType("application/pdf");
        } else if (fileName != null && fileName.contains(".webm")) {
            mediaType = MediaType.parseMediaType("video/webm");
        } else {
            mediaType = MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file));
        }
        return mediaType;
    }

    public static MediaType getContentType(ObjectMetadata s3objectData) {
        return MediaType.valueOf(s3objectData.getContentType());
    }
}
