package com.messenger.messengerclient;

import com.messenger.messengerclient.Models.Communication.Configuration;
import com.messenger.messengerclient.Models.Communication.Connection;
import com.messenger.messengerclient.Models.Communication.NotificationConnection;
import com.messenger.messengerclient.Models.Messenger;
import com.messenger.messengerclient.Models.UI;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private Stage stage;
    private static Connection connection;
    private static NotificationConnection notificationConnection;
    private static final Messenger messenger = new Messenger();
    private static String userToken;

    @Override
    public void start(Stage stage) throws IOException {

        Configuration.configure();
        connection = new Connection(Configuration.getSocket(9000));
        notificationConnection = new NotificationConnection(Configuration.getSocket(9001));

        UI.showAuthorizationWindow();

    }

    public static void main(String[] args) {
        launch();
    }
    public static Messenger getMessenger(){return messenger;}
    public static Connection getConnection(){return connection;}
    public static NotificationConnection getNotificationConnection(){return notificationConnection;}
    public static void setUserToken(String token){userToken = token;}
    public static String getUserToken(){return userToken;}
}