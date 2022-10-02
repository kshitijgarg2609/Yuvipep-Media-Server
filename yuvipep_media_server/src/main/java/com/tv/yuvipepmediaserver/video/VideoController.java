package com.tv.yuvipepmediaserver.video;

import java.util.List;
import java.util.Optional;

import com.tv.yuvipepmediaserver.model.User;
import com.tv.yuvipepmediaserver.model.UserStats;
import com.tv.yuvipepmediaserver.model.Video;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoController {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private VideoStreamService videoStreamService;
    @Autowired
    private VideoAuthService videoAuthService;

    public VideoController() {

    }

    @GetMapping("/stream/{fileName:.+}")
    public Mono<ResponseEntity<StreamingResponseBody>> streamVideo(
            @RequestHeader(value = "Range", required = false) String httpRangeList,
            @PathVariable("fileName") String fileName,
            @RequestParam("videoId") String videoId,
            @RequestParam("userId") String userId) {

        try {
            // if (!videoAuthService.hasAuth(userId, videoId)) {
            // return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
            // }
            //prepareStreamFileContent
            return Mono.just(videoStreamService.prepareStreamFileContent(fileName, httpRangeList,"videos"));
            //return Mono.just(videoStreamService.prepareStreamContent(fileName, httpRangeList,"videos"));
            //return Mono.just(videoStreamService.prepareContent(fileName, httpRangeList,"videos"));
        } catch (Exception ex) {
            logger.error("Error Loading Video: [{}]", ex.getMessage(), ex);
            return Mono.just(new ResponseEntity<>(HttpStatus.FORBIDDEN));
        }

    }
}