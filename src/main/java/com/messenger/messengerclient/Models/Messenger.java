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
    public Message getLastMessage(Subscription subscription){
        ConnectionActor connectionActor = new ConnectionActor(Application.getConnection());
        return connectionActor.getLastMessage(subscription.getTopicId());
    }

    public LinkedList<Subscription> getSubscriptions(){
        return subscriptions;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
