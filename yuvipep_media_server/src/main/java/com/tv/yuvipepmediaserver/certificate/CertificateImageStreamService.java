package com.tv.yuvipepmediaserver.certificate;

import java.io.File;
import java.nio.file.Files;

import com.tv.yuvipepmediaserver.config.BucketName;
import com.tv.yuvipepmediaserver.service.FileStore;
import com.tv.yuvipepmediaserver.utils.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CertificateImageStreamService {

    @Value("${certificate.folder.path:/opt/learning-app/certificates/}")
    private String imageBasePath;

    @Autowired
    FileStore fileStoreService;

    public ResponseEntity<byte[]> getFileContent(String name, String type) {
        try {
            String fileName = imageBasePath + name;
            byte[] content = fileStoreService.download(BucketName.YUVIPEP_CONTENT.toString(), type + "/" + name);

            return ResponseEntity.ok()
                    .contentType(Utils.getContentType(fileName))
                    .header("Content-Disposition", "inline; filename=" + name)
                    .body(content);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
        }

    }
}
