package com.messenger.messengerclient.Models;

import com.messenger.messengerclient.Controllers.MessengerController;
import com.messenger.messengerclient.Models.Communication.RestAPI.ConnectionManager;
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
    //ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
     //Thread notificationThread;
     //MutableBoolean notificationThreadCloseFlag = new MutableBoolean(true);

    /*public void initNotificationConnection(){
        ConnectionActor connectionActor = new ConnectionActor(Application.getNotificationConnection());
        notificationThread = connectionActor.initNotificationsConnection(Application.getUserToken(), notificationThreadCloseFlag, messengerController::updateMessagesByNotification);
        notificationThread.start();
    }
    public void close(){
        notificationThreadCloseFlag.setValue(false);
        System.out.println("Flag:"+notificationThreadCloseFlag);
    }*/
    public void meow(long thread){
        System.out.println("proxy on thread "+thread);
    }
    public void initNotificationConnection(){
        ConnectionManager.initNotificationConnection(messengerController::updateMessagesByNotification);
    }
    public void setMessengerController(MessengerController controller){
        this.messengerController = controller;
    }

    public void updateSubscriptions(){
        subscriptions = ConnectionManager.getSubscriptions();
    }

    public void sendMessage(Long topicId, String content){
        ConnectionManager.sendMessage(content,topicId);
    }

    public void setTopicRead(Long topicId){
        ConnectionManager.setTopicRead(topicId);
    }

    public LinkedList<Message> getMessages(long topicId){
        return ConnectionManager.getMessagesByTopic(topicId);
    }

    /*public boolean hasNewMessages(Subscription subscription){
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
    */
    public Message getFirstMessage(Long topicId){
        return ConnectionManager.getFirstMessage(topicId);
    }
    public Message getLastMessage(Long topicId){
        return ConnectionManager.getLastMessage(topicId);
    }
    public void unsubscribe(Long topicId){
        ConnectionManager.unsubscrube(topicId);
        updateSubscriptions();
        messengerController.updateTopics();
    }
    public void subscribe(Long topicId){
        ConnectionManager.subscribe(topicId);
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
