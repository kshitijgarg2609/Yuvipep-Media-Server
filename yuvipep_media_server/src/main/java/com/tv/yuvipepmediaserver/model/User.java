package com.tv.yuvipepmediaserver.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "User")
public class User {

    @Id
    public String id;

    private String email;
    private String countryCode;
    private String mobile;
    private String name;
    private Boolean isActive;

    User() {

    }
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public String getDescription() {
        return mobile;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    
    @Override
    public String toString() {
        return "User [countryCode=" + countryCode + ", email=" + email + ", id=" + id + ", isActive=" + isActive
                + ", mobile=" + mobile + ", name=" + name + "]";
    }
}
