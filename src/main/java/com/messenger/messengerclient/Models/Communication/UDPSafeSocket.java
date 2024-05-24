package com.messenger.messengerclient.Models.Communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Timer;

public class UDPSafeSocket {
    DatagramSocket datagramSocket;
    HashSet<Integer> ACKs;
    private int MTU = 500;
    private int responseTimeout = 1000;
    private byte[][] packetsBucket;
    private Timer timer;

    UDPSafeSocket(DatagramSocket datagramSocket, InetAddress remoteAddress, int remotePort){
        this.datagramSocket = datagramSocket;
        datagramSocket.connect(remoteAddress,remotePort);
    }


    private void sendAck(){
        ByteBuffer acks = ByteBuffer.wrap(new byte[(2+packetsBucket.length)*(4)]); // 4 bytes per packet index + 4 bytes for ACK-code
        acks.putInt(1);
        for(int i = 0; i<packetsBucket.length; i++){
            if(packetsBucket[i] != null) acks.putInt(i+1);
        }
        acks.putInt(-1);
        try {
            datagramSocket.send(new DatagramPacket(acks.array(),acks.array().length));
        } catch (IOException e) {
            System.out.println("Can't connect to server");
        }
    }

    public byte[] accept() {
        DatagramPacket datagramPacket = new DatagramPacket(new byte[1024],1024);
        try {
            datagramSocket.setSoTimeout(responseTimeout);

            boolean allThePacketsReceived;
            do {
                allThePacketsReceived = true;
                datagramSocket.receive(datagramPacket);
                byte[] bytes = datagramPacket.getData();
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                int currentPacket = byteBuffer.getInt();
                System.out.println("Accepting packet "+currentPacket +":"+new String(bytes).trim());
                if(packetsBucket == null){
                    int packetsNum = byteBuffer.getInt();
                    packetsBucket = new byte[packetsNum][];
                }else{
                    byteBuffer.getInt();
                }
                int packetSize = byteBuffer.getInt();
                //System.out.println("packetSize"+packetSize);
                packetsBucket[currentPacket-1] = Arrays.copyOfRange(bytes,12,12+packetSize);
                //System.out.println("copied"+new String(packetsBucket[currentPacket-1]) );

                for (byte[] packet : packetsBucket) {
                    if (packet == null) {
                        allThePacketsReceived = false;
                        break;
                    }
                }

            }while (!allThePacketsReceived);
            sendAck();
            byte[] allTheData = new byte[packetsBucket[0].length*(packetsBucket.length-1)+packetsBucket[packetsBucket.length-1].length];
            ByteBuffer buffer = ByteBuffer.wrap(allTheData);
            for (byte[] packet : packetsBucket) {
                buffer.put(packet);
            }
            return buffer.array();
        } catch (SocketTimeoutException e) {
            sendAck();
            return accept();
        }catch (IOException e){
            System.out.println("Can't connect to server");
        }
        return null;
    }

    public byte[] get(){
        packetsBucket = null;
        return accept();
    }

    public void send(byte[] data) throws IOException {
        int packetNum = data.length / MTU + (data.length % MTU == 0? 0 : 1);
        System.out.println("Delivering for "
                +datagramSocket.getInetAddress().getHostAddress()
                +":"+datagramSocket.getPort()
                +" ["+packetNum+" packets per "+MTU +" bytes]"
        );
        ACKs = new HashSet<>();
        packetsBucket = new byte[packetNum][];

        for(int i = 0; i<packetNum; i++){
            int lastByteIndex = i*MTU+MTU;
            int firstByteIndex = i*MTU;
            if(i == packetNum-1 && i*MTU+MTU > data.length)
                lastByteIndex = data.length;
            byte[] packetData = Arrays.copyOfRange(data, firstByteIndex, lastByteIndex);
            packetsBucket[i] = packetData;
        }
        _send();
    }

    private void _send() throws IOException {
        boolean allThePacketsReceived = true;
        for(int i = 0; i<packetsBucket.length; i++){
            if(!ACKs.contains(i+1)){
                allThePacketsReceived = false;
                ByteBuffer buffer = ByteBuffer.wrap(new byte[packetsBucket[i].length+12]);
                buffer.putInt(i+1);
                buffer.putInt(packetsBucket.length);
                buffer.putInt(packetsBucket[i].length);
                System.out.println("packet length: "+packetsBucket[i].length);
                buffer.put(packetsBucket[i]);
                datagramSocket.send(new DatagramPacket(buffer.array(),buffer.array().length));
            }
        }
        System.out.println("allThePacketsReceived? "+allThePacketsReceived);

        if(allThePacketsReceived) {
            return;
        }else {
            datagramSocket.setSoTimeout(responseTimeout);
            DatagramPacket datagramPacket = new DatagramPacket(new byte[1024], 1024);
            try {
                datagramSocket.receive(datagramPacket);
                acceptACK(datagramPacket.getData());
            }catch (SocketTimeoutException exception){
                _send();
            }
        }
    }

    /*

    Code 1: ACK

     */
    static int ACK_CODE = 1;
    public void acceptACK(byte[] bytes) {
        if(bytes.length < 8) return;
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        //Getting code:
        int code = buffer.getInt();
        if(code != ACK_CODE) return;
        for(int i = 1; i<bytes.length/4; i++){
            int packetNum = buffer.getInt();
            if(packetNum == -1) break;
            System.out.println("GOT ACK FOR "+packetNum);
            ACKs.add(packetNum);
        }
        try {
            _send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
