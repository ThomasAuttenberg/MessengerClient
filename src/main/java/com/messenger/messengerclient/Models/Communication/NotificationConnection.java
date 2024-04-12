package com.messenger.messengerclient.Models.Communication;

import com.messenger.messengerclient.Models.MutableBoolean;

import java.io.IOException;
import java.net.Socket;

public class NotificationConnection extends Connection{

    public NotificationConnection(Socket socket) {
        super(socket);
    }

    public Object getReply(MutableBoolean stopFlag) throws IOException {
        while (stopFlag.getValue()) {
            if (hasServerReply())
              return super.getReply();
        }
        System.out.println("NOTIFICATION END");
        return null;
    }
}
