package com.messenger.messengerclient.Models;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Controllers.MessengerController;
import com.messenger.messengerclient.Models.Communication.ConnectionActor;
import com.messenger.messengerclient.Models.Communication.NotificationConnection;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import com.messenger.messengerclient.Models.Entities.User;
import javafx.application.Platform;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;

public class Messenger {
    User user;
    LinkedList<Subscription> subscriptions;
    HashMap<Long,Long> lastReadTime;
    MessengerController messengerController;
     Thread notificationThread;
     MutableBoolean notificationThreadCloseFlag = new MutableBoolean(true);
     NotificationConnection notificationConnection;
    public interface NotificationCallBack{
        public void onNotificationCallBack(Long topicId);
    }
    public void initNotificationConnection(){
        JSONObject request = new JSONObject();
        request.put("token", Application.getUserToken());
        try {
            notificationConnection = new NotificationConnection(InetAddress.getByName("localhost"),9001, Application.getUserToken());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            //System.out.println("STAGE 1");
            notificationConnection.alive();
            Long reply = notificationConnection.getReply();
            System.out.println("Notification init:"+reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        notificationThread = new Thread(() -> {
                try {
                    while (true) {
                        Long notification = notificationConnection.getReply();
                        System.out.println("NEW NOTIFICATION" + notification);
                        Platform.runLater(() -> {
                            messengerController.updateMessagesByNotification(notification);
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
            }
        });
        notificationThread.start();
    }

    public void close(){
        notificationThread.interrupt();
        System.out.println("Flag:"+notificationThreadCloseFlag);
    }


    public void meow(long thread){
        System.out.println("proxy on thread "+thread);
    }


    public void setMessengerController(MessengerController controller){
        this.messengerController = controller;
    }

    public void updateSubscriptions(){
        subscriptions = ConnectionActor.getSubscriptions();
    }

    public void sendMessage(Long topicId, String content){
        ConnectionActor.sendMessage(content,topicId);
        if(!messengerController.getCurrentThread().equals(topicId)) messengerController.goToTopic(topicId);
    }

    public void setTopicRead(Long topicId){
        ConnectionActor.setTopicRead(topicId);
    }

    public LinkedList<Message> getMessages(long topicId){
        return ConnectionActor.getMessagesByTopic(topicId);
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
        return ConnectionActor.getFirstMessage(topicId);
    }
    public Message getLastMessage(Long topicId){
        return ConnectionActor.getLastMessage(topicId);
    }
    public void unsubscribe(Long topicId){
        ConnectionActor.unsubscrube(topicId);
        updateSubscriptions();
        messengerController.updateTopics();
        //watchNow(topicId);
        messengerController.goToTopic(topicId);
    }
    public void subscribe(Long topicId){
        ConnectionActor.subscribe(topicId);
        updateSubscriptions();
        messengerController.updateTopics();
        //watchNow(topicId);
        messengerController.goToTopic(topicId);
    }

    public LinkedList<Subscription> getSubscriptions(){
        return subscriptions;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
