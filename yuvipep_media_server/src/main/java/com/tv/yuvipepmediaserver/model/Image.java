package com.tv.yuvipepmediaserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "Images")
public class Image {
  @Id
  String id;

  @Field(name = "name")
  String name;

  @Field(name = "s3Path")
  String s3Path;

  Image() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getS3Path() {
    return s3Path;
  }

  public void setS3Path(String s3Path) {
    this.s3Path = s3Path;
  }

  @Override
  public String toString() {
    return "Image [id=" + id + ", name=" + name + ", s3Path=" + s3Path + "]";
  }

}
