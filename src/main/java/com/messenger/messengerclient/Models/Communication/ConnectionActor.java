package com.messenger.messengerclient.Models.Communication;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

public class ConnectionActor {

    private Connection connection;

    public ConnectionActor(Connection connection){
        this.connection = connection;
    }


    public void sendMessage(String content,Long threadId){
        JSONObject request = new JSONObject();
        request.put("requestDescription","SendMessage");
        request.put("content",content);
        request.put("threadId",threadId);
        try {
            connection.sendRequest(request);
            System.out.println(connection.getReply());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public interface NotificationCallBack{
        public void onNotificationCallBack(Long topicId, String firstMessage, boolean isNotification);
    }
    public Thread initNotificationsConnection(String token, NotificationCallBack a){
        JSONObject request = new JSONObject();
        request.put("token", token);
        NotificationConnection notificationConnection = Application.getNotificationConnection();
        try {
            System.out.println("STAGE 1");
            notificationConnection.sendRequest(request);
            JSONObject reply = (JSONObject) notificationConnection.getReply();
            System.out.println("Notification init:"+reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Thread(() -> {
            while (true) {
                try {
                    JSONObject notification = (JSONObject) notificationConnection.getReply();
                    System.out.println("NEW NOTIFICATION"+notification);
                    Platform.runLater(() -> a.onNotificationCallBack((Long) notification.get("threadId"), (String)notification.get("firstMessage"), (boolean)notification.get("isNotification")));

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public String tryAuthorize(String username, String password){
        JSONObject request = new JSONObject();
        request.put("requestDescription","Authorization");
        request.put("authType","password");
        request.put("username",username);
        request.put("password",password);
        JSONObject reply = null;
        try {
            connection.sendRequest(request);
            reply = (JSONObject) connection.getReply();
            System.out.println("reply on authorization: "+reply );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(((String)reply.get("status")).equals("OK"))
            return (String)reply.get("token");
        return null;
    }
    public LinkedList<Subscription> getSubscriptions(){
        try {

            JSONObject request = new JSONObject();
            request.put("requestDescription","GetSubscriptions");
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();
            System.out.println("reply got:"+reply);
            if(reply == null) return null;
            JSONArray subscriptionsJSON = (JSONArray) reply.get("subscriptions");
            LinkedList<Subscription> subscriptions = new LinkedList<>();
            for(Object object : subscriptionsJSON){
                JSONObject subscriptionObject = (JSONObject) object;
                Long topic = (Long) subscriptionObject.get("topic");
                Long lastReadTime = (Long) subscriptionObject.get("lastReadTime");
                String topicText = (String) subscriptionObject.get("firstMessage");
                boolean isNotification = (boolean) subscriptionObject.get("isNotification");
                subscriptions.add(new Subscription(topic,lastReadTime,topicText,isNotification));
            }
            return subscriptions;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Message getLastMessage (Long topicId){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription", "GetLastMessage");
            request.put("threadId", topicId);
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();
            if(((String)reply.get("status")).equals("OK")) {
                Message message = getMessage((JSONObject) reply);
                //message.setParentMessageId(topicId);
                return message;
            }else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public LinkedList<Message> getMessagesByTopic(Long id){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription","GetThread");
            request.put("threadId",id);
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();

            JSONArray messagesJSONArray = (JSONArray) reply.get("messages");
            JSONObject parentMessageJSON = (JSONObject) reply.get("parentMessage");

            LinkedList<Message> messages = new LinkedList<>();
            if(parentMessageJSON.get("id") == null) return null;
            Message parentMessage = getMessage(parentMessageJSON);
            messages.add(parentMessage);

            for(Object messageJSON_ : messagesJSONArray){
                Message message = getMessage((JSONObject) messageJSON_);
                message.setParentMessageId(id);
                messages.add(message);
            }
            return messages;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setTopicRead(Long topicId) {
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription", "Read");
            request.put("threadId", topicId);
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();
            if(!((String)reply.get("status")).equals("OK")){
                System.out.println(reply);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Message getMessage(JSONObject messageJSON) {
        Message message = new Message();
        message.setMessageId((Long)messageJSON.get("id"));
        message.setDatetime((Long)messageJSON.get("dateTime"));
        message.setParentMessageId((Long)messageJSON.get("parentMessage"));
        message.setContent((String)messageJSON.get("content"));
        message.setQuotes((int)messageJSON.get("quotes"));
        message.setAuthorUsername((String)messageJSON.get("author"));
        return message;
    }
}
