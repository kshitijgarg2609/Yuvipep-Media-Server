package com.tv.yuvipepmediaserver.service;

import java.io.IOException;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Component
public class FileStore {
  Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private AmazonS3 amazonS3;

  @Cacheable("images")
  public byte[] download(String path, String key) throws IOException {
    S3Object object = amazonS3.getObject(path, key);
    S3ObjectInputStream objectContent = object.getObjectContent();
    try {
      logger.debug("Downloading from [{}]: [{}]", path, key);
      byte[] byteArray = IOUtils.toByteArray(objectContent);
      return byteArray;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to download the file", e);
    } finally {
      object.close();
      objectContent.close();
    }
  }

  public PutObjectResult upload(String path, String filePath, MultipartFile multipartFile) throws IOException {
    try {
      ObjectMetadata data = new ObjectMetadata();
      data.setContentType(multipartFile.getContentType());
      data.setContentLength(multipartFile.getSize());
      logger.debug("Uploading content: [{}]", multipartFile);
      PutObjectResult objectResult = amazonS3.putObject(path, filePath, multipartFile.getInputStream(), data);
      logger.debug("Uploaded content: [{}]", objectResult);

      return objectResult;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to upload the file", e);
    }
  }

  public void remove(String path, String filePath) throws IOException {
    try {
      logger.debug("Deleting content: {}", filePath);
      amazonS3.deleteObject(path, filePath);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to remove the file", e);
    }
  }

  public void removeFiles(String path, String[] filePaths) throws IOException {
    try {
      logger.debug("Deleting contents: [{}]", filePaths);
      DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(path).withKeys(filePaths);
      amazonS3.deleteObjects(deleteRequest);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to remove files", e);
    }
  }

  public ObjectMetadata getMetaData(String path, String key) throws IOException {
    S3Object object = amazonS3.getObject(path, key);
    try {
      ObjectMetadata metadata = object.getObjectMetadata();
      return metadata;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to download the file", e);
    } finally {
      object.close();
    }
  }

}