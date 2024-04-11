package com.messenger.messengerclient.Models.Entities;

public class Subscription {
    private Long topicId;
    private Long lastReadTime;
    private String topicText;
    private boolean isNotification = false;

    public Subscription(Long topicId, Long lastReadTime, String topicText, boolean isNotification){
        this.topicId = topicId;
        this.lastReadTime = lastReadTime;
        this.topicText = topicText;
        this.isNotification = isNotification;
    }

    public Long getTopicId() {
        return topicId;
    }
    public Long getLastReadTime() {
        return lastReadTime;
    }
    public String getTopicText() {
        return topicText;
    }
    public void setTopicText(String topicText) {
        this.topicText = topicText;
    }
    public void setNotification(){isNotification = true;}
    public boolean isNotification() {return isNotification;}

    @Override
    public int hashCode() {
        return topicId.hashCode();
    }
}
