package com.messenger.messengerclient.Models.Communication;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Connection{


    private Boolean isRunning = true;
    private UDPSafeSocket udpSafeSocket;
    private JSONParser jsonParser = new JSONParser();
    public Connection(InetAddress inetAddress, int port){
        try {
            udpSafeSocket = new UDPSafeSocket(new DatagramSocket(),inetAddress,port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject getReply() throws IOException{
        try {
            return  (JSONObject) jsonParser.parse(new String(udpSafeSocket.get()).trim());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendRequest(JSONObject jsonObject) throws IOException {
        udpSafeSocket.send(jsonObject.toJSONString().getBytes());
    }

}

