package com.messenger.messengerclient.Controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MessengerController {
    @FXML
    public Button addTopicButton;
    @FXML
    VBox topicList;
    @FXML
    ScrollPane murlika;

    public void init(Parent topic){
        addTopicButton.setOnMouseEntered(event -> addTopicButton.setStyle("-fx-background-color: #8a8a8a;"));
        addTopicButton.setOnMouseExited(event -> addTopicButton.setStyle("-fx-background-color: grey;"));
        topicList.getChildren().add(topic);
        topic.setStyle("-fx-background-color: #545454;");
        topic.setOnMouseEntered(event -> topic.setStyle("-fx-background-color: #666666;"));
        topic.setOnMouseExited(event -> topic.setStyle("-fx-background-color: #545454;"));
        topic.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("meow");
            }
        });
    }
}
