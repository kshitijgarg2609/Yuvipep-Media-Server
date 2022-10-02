package com.tv.yuvipepmediaserver.config;

public enum BucketName {
  YUVIPEP_CONTENT("yuvipep-media-content");

  private final String bucketName;

  /**
   * @param text
   */
  BucketName(final String bucketName) {
    this.bucketName = bucketName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return bucketName;
  }

}
