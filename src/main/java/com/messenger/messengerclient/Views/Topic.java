package com.messenger.messengerclient.Views;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Topic extends VBox {
    private Text topic;
    private Text message;
    public Topic(String topic, String lastMessage){
        Text topicLabel = new Text(topic);
        Text topicMessage = new Text(lastMessage);
        topicLabel.setFont(new Font("System Bold", 14));
        this.topic = topicLabel;
        this.message = topicMessage;
        this.getChildren().add(topicLabel);
        this.getChildren().add(topicMessage);
        this.paddingProperty().set(new Insets(5));
    }

    public Text getMessage() {
        return message;
    }

    public Text getTopic() {
        return topic;
    }
}
