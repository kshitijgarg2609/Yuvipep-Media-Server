package com.tv.yuvipepmediaserver.image;

import com.tv.yuvipepmediaserver.model.Image;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<Image, String> {

}
