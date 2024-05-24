package com.messenger.messengerclient.Models.Communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class NotificationConnection{

    DatagramSocket datagramSocket;
    String authToken;
    //DatagramPacket datagramPacket = new DatagramPacket(new byte[1024],1024);
    //ByteBuffer byteBuffer = ByteBuffer.wrap(datagramPacket.getData());
    {
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public NotificationConnection(InetAddress address,int port, String authToken) {
        datagramSocket.connect(address,port);
        this.authToken = authToken;
    }

    public Long getReply() throws IOException {
        System.out.println("GOT REPLY FROM CONNECTION");
        DatagramPacket datagramPacket_ = new DatagramPacket(new byte[8],8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(datagramPacket_.getData());
        datagramSocket.receive(datagramPacket_);
        return byteBuffer.getLong();
    }
    public void alive(){
        try {
            DatagramPacket datagramPacket = new DatagramPacket(authToken.getBytes(),authToken.getBytes().length);
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
