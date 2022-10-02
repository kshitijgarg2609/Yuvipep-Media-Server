package com.tv.yuvipepmediaserver.model;

import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videos")
public class Video {

    @Id
    public String id;

    private String categoryId;
    private String title;
    private String experimentId;
    private String thumbnail;
    private String duration;
    private String streamUrl;
    private String description;
    private Integer noOfViews;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
    private Boolean isActive;
    private String pdf;
    private List<String> topicCovered;
    private Boolean isPremium;

    Video() {

    }
    public void setId(String id) {
        this.id = id;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setDuration(String duration) {
        this.duration = duration;
    }
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    public void setIsPremium(Boolean isPremium) {
        this.isPremium = isPremium;
    }
    public void setNoOfViews(Integer noOfViews) {
        this.noOfViews = noOfViews;
    }
    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setTopicCovered(List<String> topicCovered) {
        this.topicCovered = topicCovered;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getId() {
        return id;
    }
    
    public String getCategoryId() {
        return categoryId;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public String getDescription() {
        return description;
    }
    public String getDuration() {
        return duration;
    }
    public String getExperimentId() {
        return experimentId;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public Boolean getIsPremium() {
        return isPremium;
    }
    public Integer getNoOfViews() {
        return noOfViews;
    }
    public String getPdf() {
        return pdf;
    }
    public String getStreamUrl() {
        return streamUrl;
    }
    public String getThumbnail() {
        return thumbnail;
    }
    public String getTitle() {
        return title;
    }
    public List<String> getTopicCovered() {
        return topicCovered;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public String getUpdatedBy() {
        return updatedBy;
    }


    
}
