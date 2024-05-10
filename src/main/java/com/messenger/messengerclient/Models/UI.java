package com.messenger.messengerclient.Models;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Controllers.Controller;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UI {
    private static Stage stage;
    private static Controller controller;
    private static Stage navigationMenu;
    public static void showMessenger(){
        if(stage != null) stage.close();

        FXMLLoader loader = new FXMLLoader(Application.class.getResource("messenger2.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            System.out.println("REALLY HERE");
            throw new RuntimeException(e);
        }
        stage.setTitle("Messenger");
        //stage = new Stage();
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.setOnCloseRequest(event -> Application.getMessenger().close());
        stage.show();

        controller = ((Controller) loader.getController());
    }
    public static void showAuthorizationWindow(){
        if(stage != null) stage.close();
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load(), 320, 240);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = new Stage();
        stage.setTitle("Authorization");
        stage.setScene(scene);
        stage.show();

        controller = ((Controller) loader.getController());
    }
    public static Controller getController(){
        return controller;
    }

    public static void showNavigationMenu() {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("navigationView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = new Stage();
        stage.setTitle("Navigation");
        stage.setScene(scene);
        stage.show();
        navigationMenu = stage;
    }

    public static void hideNavigationMenu() {
        navigationMenu.hide();
    }
}
