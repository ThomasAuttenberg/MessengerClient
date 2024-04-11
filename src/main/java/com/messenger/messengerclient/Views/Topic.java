package com.messenger.messengerclient.Views;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Topic extends VBox {

    private Text topic;
    private Text message;
    private boolean unread = false;
    private long topicId;
    private boolean isNotification = false;
    public Topic(String topic, String lastMessage,long topicId){
        Text topicLabel = new Text(topic);
        Text topicMessage = new Text(lastMessage);
        topicLabel.setFont(new Font("System Bold", 14));
        this.topic = topicLabel;
        this.topicId = topicId;
        this.message = topicMessage;
        this.getChildren().add(topicLabel);
        this.getChildren().add(topicMessage);
        this.paddingProperty().set(new Insets(5));
        setRead();
    }

    public Text getMessage() {
        return message;
    }
    public Text getTopic() {
        return topic;
    }
    public boolean isUnread() {return unread;}
    public void setRead() {
        unread = false;
        this.setStyle("-fx-background-color: #545454;");
        this.setOnMouseEntered(event -> this.setStyle("-fx-background-color: #666666;"));
        this.setOnMouseExited(event -> this.setStyle("-fx-background-color: #545454;"));
    }
    public void setPicked(){
        this.setStyle("-fx-background-color: #a6a6a6;");
        this.setOnMouseEntered(event -> this.setStyle("-fx-background-color: #c2c1c1;"));
        this.setOnMouseExited(event -> this.setStyle("-fx-background-color: #a6a6a6;"));
    }
    public void setUnread() {
        unread = true;
        this.setStyle("-fx-background-color: #333333;");
        this.setOnMouseEntered(event -> this.setStyle("-fx-background-color: #c2c1c1;"));
        this.setOnMouseExited(event -> this.setStyle("-fx-background-color: #333333;"));
    }
    public long getTopicId(){return topicId;}
    public void setTopicId(long topicId){this.topicId = topicId;}

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }
}
