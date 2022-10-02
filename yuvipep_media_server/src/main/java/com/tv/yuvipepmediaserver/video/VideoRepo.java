package com.tv.yuvipepmediaserver.video;

import com.tv.yuvipepmediaserver.model.Video;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepo extends MongoRepository<Video, String> {

  // public Video findByFirstName(String firstName);
  // public List<Video> findByLastName(String lastName);

}