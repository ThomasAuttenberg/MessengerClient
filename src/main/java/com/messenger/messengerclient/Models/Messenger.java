package com.messenger.messengerclient.Models;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Controllers.MessengerController;
import com.messenger.messengerclient.Models.Communication.ConnectionActor;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import com.messenger.messengerclient.Models.Entities.User;

import java.util.HashMap;
import java.util.LinkedList;

public class Messenger {
    User user;
    LinkedList<Subscription> subscriptions;
    HashMap<Long,Long> lastReadTime;
    MessengerController messengerController;
     Thread notificationThread;
     MutableBoolean notificationThreadCloseFlag = new MutableBoolean(true);

    public void initNotificationConnection(){
        ConnectionActor connectionActor = new ConnectionActor(Application.getNotificationConnection());
        notificationThread = connectionActor.initNotificationsConnection(Application.getUserToken(), notificationThreadCloseFlag, messengerController::updateMessagesByNotification);
        notificationThread.start();
    }
    public void close(){
        notificationThreadCloseFlag.setValue(false);
        System.out.println("Flag:"+notificationThreadCloseFlag);
    }

    public void setMessengerController(MessengerController controller){
        this.messengerController = controller;
    }

    public void updateSubscriptions(){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        subscriptions = connectionActor.getSubscriptions();
    }

    public LinkedList<Message> getMessages(long topicId){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        return connectionActor.getMessagesByTopic(topicId);
    }

    public boolean hasNewMessages(Subscription subscription){
        subscription.getTopicId();
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        Message lastMessage;
        if((lastMessage = connectionActor.getLastMessage(subscription.getTopicId())) == null){
            return false;
        }else{
            if(subscription.getLastReadTime() < lastMessage.getDatetime())
                return true;
            else
                return false;
        }
    }
    public Message getLastMessage(Long topicId){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        return connectionActor.getLastMessage(topicId);
    }
    public void unsubscribe(Long topicId){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        connectionActor.unsubscrube(topicId);
        updateSubscriptions();
        messengerController.updateTopics();
    }
    public void subscribe(Long topicId){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        connectionActor.subscribe(topicId);
        updateSubscriptions();
        messengerController.updateTopics();
    }

    public LinkedList<Subscription> getSubscriptions(){
        return subscriptions;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
