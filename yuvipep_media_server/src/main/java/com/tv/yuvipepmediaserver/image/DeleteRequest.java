package com.tv.yuvipepmediaserver.image;

import java.util.List;

public class DeleteRequest {
  private String path;
  private List<String> files;

  DeleteRequest() {

  }

  public String getPath() {
      return path;
  }

  public void setPath(String path) {
      this.path = path;
  }

  public List<String> getFiles() {
      return files;
  }
  
  public void setFiles(List<String> files) {
      this.files = files;
  }
}
