package com.messenger.messengerclient.Models.Communication.RestAPI;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Entities.Message;
import com.messenger.messengerclient.Models.Entities.Subscription;
import javafx.application.Platform;
import okhttp3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import retrofit2.Call;
import retrofit2.Retrofit;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.LinkedList;

public class ConnectionManager {

    static private final OkHttpClient client;
    static private final RestAPIFace restAPIFace;
    static{
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        client = new OkHttpClient.Builder()
                .cookieJar(new MemoryCookieJar())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:2024/")
                .client(client)
                .addConverterFactory(JSONConverterFactory.create())
                .build();

        restAPIFace = retrofit.create(RestAPIFace.class);

    }

    @FunctionalInterface
    public interface CallBack{void invoke(long threadId);}

    static public void initNotificationConnection(CallBack callBack){
        Request request = new Request.Builder()
                .url("ws://localhost:2024/listen")
                .build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("opening websocket: "+response.body());
                webSocket.send(Application.getUserToken());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                System.out.println("got notification in thread "+text);
                System.out.println(text);
                try {
                    Platform.runLater(()->callBack.invoke(Long.parseLong(text)));
                }catch (NumberFormatException ex){
                    System.out.println(ex.getMessage());
                }
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("websocket closing because of:"+reason);
            }
            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("can't open websocket, got:"+response);
            }
        };

        client.newWebSocket(request, listener);
    }

    static public void sendMessage(String content,Long threadId){
        try {
        JSONObject request = new JSONObject();
        request.put("content",content);
        Call<JSONObject> call = restAPIFace.sendMessage(Application.getUserToken(),threadId,request);
        System.out.println("Message sent reply:"+call.execute().body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static String tryAuthorize(String username, String password){
        try {
            JSONObject request = new JSONObject();
            request.put("requestDescription","Authorization");
            request.put("authType","password");
            request.put("username",username);
            request.put("password",password);

            JSONObject reply = null;
            reply = restAPIFace.auth(request).execute().body();
            System.out.println("Authorization reply:"+reply);

            if((reply.get("status")).equals("OK")) {
                System.out.println("token: "+(String) reply.get("token"));
                return (String) reply.get("token");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public static LinkedList<Subscription> getSubscriptions(){
        try {
            JSONObject reply = restAPIFace.getSubscriptions(Application.getUserToken()).execute().body();
            System.out.println(restAPIFace.getSubscriptions(Application.getUserToken()).execute().message());
            System.out.println("reply on getSubscriptions: "+reply);
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
        try {
            JSONObject reply = restAPIFace.subscribe(Application.getUserToken(),topicId).execute().body();
            System.out.println("reply on subscribe:"+reply);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void unsubscrube(Long topicId){
        try{
            JSONObject reply = restAPIFace.unsubscribe(Application.getUserToken(),topicId).execute().body();
            System.out.println("reply on unsubscribe: "+reply);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
    public static Message getLastMessage (Long topicId){
        try {
            JSONObject reply = restAPIFace.getLastMessage(topicId).execute().body();
            if(((String)reply.get("status")).equals("OK")) {
                Message message = getMessage(reply);
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
            JSONObject reply = restAPIFace.getFirstMessage(topicId).execute().body();
            if(((String)reply.get("status")).equals("OK")) {
                Message message = getMessage(reply);
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

            JSONObject reply = restAPIFace.getMessages(id).execute().body();

            System.out.println("reply on getMessages by"+id+": "+reply);

            JSONArray messagesJSONArray = (JSONArray) reply.get("messages");
            JSONObject parentMessageJSON = (JSONObject) reply.get("parentMessage");
            if(parentMessageJSON == null) return null;
            LinkedList<Message> messages = new LinkedList<>();
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
            JSONObject reply = restAPIFace.read(Application.getUserToken(),topicId).execute().body();
            System.out.println("reply on set topic "+topicId+" read: "+reply);
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
