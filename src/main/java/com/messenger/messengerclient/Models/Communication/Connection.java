package com.messenger.messengerclient.Models.Communication;

import java.io.*;
import java.net.Socket;

public class Connection{


    private Boolean isRunning = true;
    private final Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ObjectInputStream objectInputStream = null;
    private ObjectOutputStream objectOutputStream = null;
    protected BufferedInputStream inputStream;
    protected BufferedOutputStream outputStream;

    public Connection(Socket socket){
        this.socket = socket;
        try {
            //inputStream = new BufferedInputStream(socket.getInputStream());
            //outputStream = new BufferedOutputStream(socket.getOutputStream());
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getReply() throws IOException{
        if(objectInputStream == null) objectInputStream = new ObjectInputStream(inputStream);
        Object request = null;
        try {
            request = objectInputStream.readObject();
        } catch (ClassNotFoundException ignored) {;
            System.out.println(ignored.getMessage());
        }
        return request;
    }

    public void sendRequest(Object object) throws IOException {
        if(objectOutputStream == null) objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
    }
    public boolean hasServerReply(){

        try {
            if(inputStream.available() != 0)
                System.out.println("state: "+inputStream.available());
            return inputStream.available() > 0;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

