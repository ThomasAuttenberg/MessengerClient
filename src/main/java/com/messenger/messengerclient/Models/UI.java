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
    public static void showMessenger(){
        if(stage != null) stage.hide();

        FXMLLoader loader = new FXMLLoader(Application.class.getResource("messenger2.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            System.out.println("REALLY HERE");
            throw new RuntimeException(e);
        }
        System.out.println("");
        //stage = new Stage();
        stage.setScene(scene);
        stage.setMinWidth(500);
        stage.setMinHeight(500);
        stage.show();

        controller = ((Controller) loader.getController());
    }
    public static void showAuthorizationWindow(){
        if(stage != null) stage.hide();
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load(), 320, 240);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage = new Stage();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        controller = ((Controller) loader.getController());
    }
    public static Controller getController(){
        return controller;
    }
}
