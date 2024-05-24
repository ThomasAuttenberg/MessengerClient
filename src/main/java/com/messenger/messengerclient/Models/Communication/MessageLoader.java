package com.messenger.messengerclient.Models.Communication;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Entities.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.LinkedList;

public class MessageLoader {

    public static LinkedList<Message> getMessagesByTopic(long id){
        try {
            Connection connection = Application.getConnection();
            JSONObject request = new JSONObject();
            request.put("requestDescription","GetThread");
            request.put("threadId",id);
            //connection.sendRequest(id);
            JSONObject reply = (JSONObject) connection.getReply();

            JSONArray messagesJSONArray = (JSONArray) reply.get("messages");
            JSONObject parentMessageJSON = (JSONObject) reply.get("parentMessage");

            LinkedList<Message> messages = new LinkedList<>();
            Message parentMessage = getMessage(parentMessageJSON);
            parentMessage.setMessageId((Long)parentMessageJSON.get("parentMessageId"));
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

    private static Message getMessage(JSONObject messageJSON) {
        Message message = new Message();
        message.setMessageId((Long)messageJSON.get("id"));
        message.setDatetime((Long)messageJSON.get("dateTime"));
        message.setContent((String)messageJSON.get("content"));
        message.setQuotes((int)messageJSON.get("quotes"));
        message.setAuthorUsername((String)messageJSON.get("author"));
        return message;
    }

}
