package com.tv.yuvipepmediaserver.video;

import java.util.Optional;

import com.tv.yuvipepmediaserver.model.UserStats;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserStatsRepo extends MongoRepository<UserStats, String> {

  // public Video findByFirstName(String firstName);
  // public List<Video> findByLastName(String lastName);
  @Query("{ 'userId' : ?0}")
  public Optional<UserStats> findByUserId(ObjectId userId);

}