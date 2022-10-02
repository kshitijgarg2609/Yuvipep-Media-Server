package com.tv.yuvipepmediaserver.video;

import com.tv.yuvipepmediaserver.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User, String> {

  // public Video findByFirstName(String firstName);
  // public List<Video> findByLastName(String lastName);

}