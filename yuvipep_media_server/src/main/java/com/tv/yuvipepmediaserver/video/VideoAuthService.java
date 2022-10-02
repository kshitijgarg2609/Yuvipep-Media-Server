package com.tv.yuvipepmediaserver.video;

import java.util.Optional;

import com.tv.yuvipepmediaserver.model.*;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoAuthService {
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	VideoRepo videoRepo;

	@Autowired
	UserRepo userRepo;
    
	@Autowired
	UserStatsRepo userStatsRepo;
    
	public Boolean hasAuth(String userId, String videoId) {
		logger.debug("UserID: [{}]", userId);
        
		Optional<Video> video = videoRepo.findById(videoId);
		if(video.isEmpty()) {
			return false;
		}
		if(video.get().getIsPremium() == false) {
			return true;
		}

		Optional<UserStats> user = userStatsRepo.findByUserId(new ObjectId(userId));
		logger.debug("User Stats: [{}]", user.get());
		if(video.get().getIsPremium() == true) {
			logger.debug("Course Subscriptions: [{}]", user.get().getCourseSubscription());
            
			if(!user.get().getCourseSubscription().isEmpty()) {
				for(CourseSubscription subscription : user.get().getCourseSubscription()) {
                    logger.debug("Subscription ID: [{}]  Video Id: [{}}]", subscription.getId(), video.get().getCategoryId());

					if(subscription.getIsActive() && subscription.getId().equals(video.get().getCategoryId())) {
						return true;
					}
				}
			}
			logger.debug("YuviPep Subscriptions: [{}]", user.get().getYuviPepSubscription());
			if(!user.get().getYuviPepSubscription().isEmpty()) {
				for(YuviPepSubscription subscription : user.get().getYuviPepSubscription()) {
					if(subscription.getStatus()) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
