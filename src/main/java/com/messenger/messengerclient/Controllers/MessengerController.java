package com.messenger.messengerclient.Controllers;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Communication.ConnectionActor;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import com.messenger.messengerclient.Views.MessageView;
import com.messenger.messengerclient.Views.Topic;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.LinkedList;

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
        Message lastMessage = Application.getMessenger().getLastMessage(subscription);
        Topic topic = new Topic(subscription.getTopicText(), lastMessage.getContent(),subscription.getTopicId());;
        if(subscription.isNotification()) topic.setNotification(true);
        topic.setOnMouseClicked(event -> {
            if(event.getButton() != MouseButton.PRIMARY) return;
            if(topic.isUnread()){
                topic.setRead();
                ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
                connectionActor.setTopicRead(topic.getTopicId());
            }
            goToTopic(subscription.getTopicId());
            Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
        });
        topic.setOnContextMenuRequested(event -> System.out.println("meow"));
        if (lastMessage.getDatetime() > subscription.getLastReadTime())
            topic.setUnread();
        topics.put(subscription.getTopicId(), topic);
        topicList.getChildren().add(topic);
    }

    public void addMessage(Message message){
        MessageView messageView = new MessageView(message);
        messageView.messageText.setWrappingWidth(messagesScrollPane.getWidth()-50);
        messagesScrollPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                messageView.messageText.setWrappingWidth(messagesScrollPane.getWidth()-50);
            }
        });
        messageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(event.getButton() != MouseButton.PRIMARY) return;
                goToTopic(message.getMessageId());
            }
        });
        messagesVBox.getChildren().add(messageView);
    }

    public synchronized void updateMessagesByNotification(Long topicId, String firstMessage, boolean isNotification) {
        if(currentTopic == topicId){
            ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
            addMessage(connectionActor.getLastMessage(topicId));
            connectionActor.setTopicRead(topicId);
            if(messagesScrollPane.vvalueProperty().get() > 0.999)
                messagesScrollPane.setVvalue(1.0);
            return;
        }
        if(topics.containsKey(topicId)){
            topics.get(topicId).setUnread();
        }else {
            if (isNotification) {
                Subscription subscription = new Subscription(topicId, 0L, firstMessage, true);
                addSubscription(subscription);
            }
        }
    }

    public void initialize(){

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
              if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
                  textArea.appendText("\n");
                  event.consume();
              }else{
                  ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
                  connectionActor.sendMessage(textArea.getText(),currentTopic);
                 // addMessage(connectionActor.getLastMessage(currentTopic));
                  textArea.clear();
                  event.consume();
                  //Platform.runLater(() -> {messagesScrollPane.requestLayout(); messagesScrollPane.setVvalue(1.0);});

              }
          }
        });
        addTopicButton.setOnMouseClicked(event -> Application.getMessenger().updateSubscriptions());
        goBackButton.setOnMouseClicked(event -> {goToTopic(previousTopic);});

        messagesScrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                System.out.println(newValue);
            }
        });

        ConnectionActor connectionActor = new ConnectionActor(Application.getNotificationConnection());
        notificationThread = connectionActor.initNotificationsConnection(Application.getUserToken(), this::updateMessagesByNotification);
        notificationThread.start();


    }
    private void goToTopic(long topicId){
        if(pickedTopic != null && pickedTopic.isNotification() && topicId != currentTopic) {
            topics.remove(currentTopic);
            topicList.getChildren().remove(pickedTopic);
        }
        if(topicId == -1){
            currentTopic = -1;
            messagesVBox.getChildren().clear();
            //pickedTopic = null;
            pickTopic(null);
        }else {
            messagesVBox.getChildren().clear();
            LinkedList<Message> messages = Application.getMessenger().getMessages(topicId);
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
                    isTopicInList = true;
                    break;
                }
            }
            if(!isTopicInList) pickTopic(null);
            //Platform.runLater(() -> messagesScrollPane.setVvalue(1.0));
        }
    }

}
