package com.messenger.messengerclient.Models.Communication;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

public class ConnectionActor {

    private static Connection connection;

    public static void setConnection(Connection connection_){connection = connection_;}


    public static void sendMessage(String content,Long threadId){
        JSONObject request = new JSONObject();
        request.put("token", Application.getUserToken());
        request.put("requestDescription","SendMessage");
        request.put("content",content);
        request.put("threadId",threadId);
        try {
            connection.sendRequest(request);
            //System.out.println("MEOW"+connection.hasServerReply());
            System.out.println(connection.getReply());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static String tryAuthorize(String username, String password){
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
    public static LinkedList<Subscription> getSubscriptions(){
        try {

            JSONObject request = new JSONObject();
            request.put("token", Application.getUserToken());
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
    public static void subscribe(Long topicId){
        JSONObject request = new JSONObject();
        request.put("token", Application.getUserToken());
        request.put("requestDescription","Subscribe");
        request.put("threadId",topicId);
        try {
            connection.sendRequest(request);
            connection.getReply();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void unsubscrube(Long topicId){
        JSONObject request = new JSONObject();
        request.put("token", Application.getUserToken());
        request.put("requestDescription","Unsubscribe");
        request.put("threadId",topicId);
        try {
            connection.sendRequest(request);
            connection.getReply();
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static Message getLastMessage (Long topicId){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription", "GetLastMessage");
            request.put("threadId", topicId);
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();
            if(((String)reply.get("status")).equals("OK")) {
                System.out.println(reply.toJSONString());
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

    public static Message getFirstMessage (Long topicId){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription", "GetFirstMessage");
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

    public static LinkedList<Message> getMessagesByTopic(Long id){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription","GetThread");
            request.put("threadId",id);
            connection.sendRequest(request);
            JSONObject reply = (JSONObject) connection.getReply();

            JSONArray messagesJSONArray = (JSONArray) reply.get("messages");
            JSONObject parentMessageJSON = (JSONObject) reply.get("parentMessage");
            if(parentMessageJSON == null) return null;
            LinkedList<Message> messages = new LinkedList<>();
           // if(parentMessageJSON.get("id") == null) return null;
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

    public static void setTopicRead(Long topicId) {
        try {
            JSONObject request = new JSONObject();
            request.put("token", Application.getUserToken());
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
        message.setQuotes((Long)messageJSON.get("quotes"));
        message.setAuthorUsername((String)messageJSON.get("author"));
        return message;
    }
}
