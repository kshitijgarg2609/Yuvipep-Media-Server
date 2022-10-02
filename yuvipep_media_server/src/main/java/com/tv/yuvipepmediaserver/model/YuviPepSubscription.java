package com.tv.yuvipepmediaserver.model;

import java.util.Date;

public class YuviPepSubscription {
	Boolean status;
	Date startDate;
	Integer days;
	String amount;
	String savedAmount;

	YuviPepSubscription() {

	}
		
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Integer getDays() {
		return days;
	}
	public void setDays(Integer days) {
		this.days = days;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSavedAmount() {
		return savedAmount;
	}
	public void setSavedAmount(String savedAmount) {
		this.savedAmount = savedAmount;
	}

	@Override
	public String toString() {
		return "YuviPepSubscription [amount=" + amount + ", days=" + days + ", savedAmount=" + savedAmount
				+ ", startDate=" + startDate + ", status=" + status + "]";
	}
	
	
}
