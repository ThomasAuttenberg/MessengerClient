package com.messenger.messengerclient.Controllers;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Communication.RestAPI.ConnectionManager;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import com.messenger.messengerclient.Models.UI;
import com.messenger.messengerclient.Views.MessageView;
import com.messenger.messengerclient.Views.Topic;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MessengerController implements Controller {
    @FXML
    public Button addTopicButton;
    @FXML
    public ScrollPane messagesScrollPane;
    @FXML
    public VBox messagesVBox;
    @FXML
    public TextArea textArea;
    @FXML
    public Button goBackButton;
    public Button subscribeButton;
    private long currentTopic;
    private long previousTopic;
    private Topic pickedTopic = null;
    private final HashMap<Long, Topic> topics = new HashMap<>();
    private Thread notificationThread;
    @FXML
    VBox topicList;
    @FXML
    ScrollPane murlika;



    public void pickTopic(Topic topic){

        if(pickedTopic != null) pickedTopic.setRead();
        pickedTopic = topic;
        if(topic != null) pickedTopic.setPicked();
    }

    public void addSubscription(Subscription subscription){
        Message lastMessage = Application.getMessenger().getLastMessage(subscription.getTopicId());
        Topic topic = new Topic(subscription.getTopicText(), lastMessage.getContent(),subscription.getTopicId());;
        if(subscription.isNotification()) topic.setNotification(true);
        topic.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                if (event.getButton() != MouseButton.PRIMARY) return;
                if (topic.isUnread()) {
                    topic.setRead();
                    Application.getMessenger().setTopicRead(topic.getTopicId());
                }
                goToTopic(subscription.getTopicId());
                Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
            }
        });
        topic.setOnContextMenuRequested(event -> {
            MenuItem menuItem = new MenuItem("Отписаться");
            menuItem.setOnAction(event1 -> Application.getMessenger().unsubscribe(topic.getTopicId()));
            ContextMenu contextMenu = new ContextMenu();
            contextMenu.getItems().add(menuItem);
            contextMenu.show(topic,event.getScreenX(),event.getScreenY());

        });
        if (lastMessage.getDatetime() > subscription.getLastReadTime())
            topic.setUnread();
        topics.put(subscription.getTopicId(), topic);
        topicList.getChildren().add(topic);
    }

    public void addMessage(Message message){
        MessageView messageView = new MessageView(message);
        messageView.messageText.setWrappingWidth(messagesScrollPane.getWidth()-50);
        messageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() != MouseButton.PRIMARY) return;
                goToTopic(message.getMessageId());
            }
        });
        boolean needsToScroll = messagesScrollPane.getVvalue() > 0.95;
        messagesVBox.getChildren().add(messageView);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(() -> {
            Platform.runLater(() -> {
                if(needsToScroll) messagesScrollPane.setVvalue(1.0);
            });
        }, 100, TimeUnit.MILLISECONDS);

    }

    public void updateMessagesByNotification(Long topicId) {

        if(topics.containsKey(topicId)) {
            topics.get(topicId).setUnread();
            topics.get(topicId).setMessage(ConnectionManager.getLastMessage(topicId).getContent());
        }
        if(currentTopic == topicId){
            Topic topic = topics.get(currentTopic);
            if(topic != null) topic.setPicked();
            addMessage(Application.getMessenger().getLastMessage(topicId));
            Application.getMessenger().setTopicRead(topicId);
            if(messagesScrollPane.vvalueProperty().get() > 0.999)
                messagesScrollPane.setVvalue(1.0);
        }else {
            Subscription subscription = new Subscription(topicId, 0L, ConnectionManager.getFirstMessage(topicId).getContent(), true);
            addSubscription(subscription);
        }
    }

    public void initialize(){
        subscribeButton.setVisible(false);
        subscribeButton.setOnMouseClicked(event -> Application.getMessenger().subscribe(currentTopic));

        Application.getMessenger().setMessengerController(this);
        Application.getMessenger().updateSubscriptions();
        LinkedList<Subscription> subscriptions = Application.getMessenger().getSubscriptions();
        if(subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                addSubscription(subscription);
            }
        }
        messagesVBox.paddingProperty().set(new Insets(5,5,5,5));
        messagesVBox.setSpacing(5);
        textArea.setWrapText(true);
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
          if(event.getCode() == KeyCode.ENTER) {
              if (event.isShiftDown()) {
                  textArea.appendText("\n");
                  event.consume();
              }else{
                  Application.getMessenger().sendMessage(currentTopic,textArea.getText());
                  //updateMessagesByNotification(currentTopic);
                  textArea.clear();
                  event.consume();
              }
          }
        });
        addTopicButton.setOnMouseClicked(event -> UI.showNavigationMenu());
        goBackButton.setOnMouseClicked(event -> {goToTopic(previousTopic);});

        messagesScrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
            }
        });

        Application.getMessenger().initNotificationConnection();


    }
    public void goToTopic(long topicId){
        if(pickedTopic != null && pickedTopic.isNotification() && topicId != currentTopic) {
            topics.remove(currentTopic);
            topicList.getChildren().remove(pickedTopic);
        }
        if(topicId == -1){
            currentTopic = -1;
            messagesVBox.getChildren().clear();
            subscribeButton.setVisible(false);
            //pickedTopic = null;
            pickTopic(null);
        }else {
            messagesVBox.getChildren().clear();
            LinkedList<Message> messages = Application.getMessenger().getMessages(topicId);
            if(messages == null){
                goToTopic(-1);
                return;
            }
            if(!subscribeButton.isVisible()) subscribeButton.setVisible(true);
            previousTopic = messages.getFirst().getParentMessageId();
            currentTopic = messages.getFirst().getMessageId();
            for (Message message : messages) {
                addMessage(message);
            }
            boolean isTopicInList = false;
            for(Node node : topicList.getChildren()){
                Topic topicInList = (Topic) node;
                if(topicInList.getTopicId() == topicId){
                    pickTopic(topicInList);
                    subscribeButton.setVisible(false);
                    isTopicInList = true;
                    break;
                }
            }
            if(!isTopicInList) pickTopic(null);
        }
    }

    public void updateTopics() {
        topics.clear();
        topicList.getChildren().clear();
        LinkedList<Subscription> subscriptions = Application.getMessenger().getSubscriptions();
        if(subscriptions != null) {
            for (Subscription subscription : subscriptions) {
                addSubscription(subscription);
            }
        }
    }
}
