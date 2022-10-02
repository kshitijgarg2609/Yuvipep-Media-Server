package com.tv.yuvipepmediaserver.model;

import org.springframework.data.annotation.Id;

public class CourseSubscription {
	@Id
	private String id;

	private String title;

	private Boolean isActive;

	CourseSubscription() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "CourseSubscription [id=" + id + ", isActive=" + isActive + ", title=" + title + "]";
	}

	

	
}
