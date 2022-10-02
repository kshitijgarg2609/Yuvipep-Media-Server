package com.tv.yuvipepmediaserver.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "UserStats")
public class UserStats {

    public String userId;
    private List<CourseSubscription> courseSubscription;

    private List<YuviPepSubscription> yuviPepSubscription;

    UserStats() {

    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    public List<CourseSubscription> getCourseSubscription() {
        return courseSubscription;
    }
    
    public void setCourseSubscription(List<CourseSubscription> courseSubscription) {
        this.courseSubscription = courseSubscription;
    }
    public List<YuviPepSubscription> getYuviPepSubscription() {
        return yuviPepSubscription;
    }
    public void setYuviPepSubscription(List<YuviPepSubscription> yuviPepSubscription) {
        this.yuviPepSubscription = yuviPepSubscription;
    }
    @Override
    public String toString() {
        return "UserStats [courseSubscription=" + courseSubscription + ", userId=" + userId + "]";
    }
    
}
