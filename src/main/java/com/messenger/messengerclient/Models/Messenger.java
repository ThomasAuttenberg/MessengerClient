package com.messenger.messengerclient.Models;

import com.messenger.messengerclient.Controllers.MessengerController;
import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Views.Topic;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Messenger {
    Scene scene;
    VBox rootBox;
    {
        rootBox = new VBox();

    }
    public void show() throws IOException {
        //
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("messenger2.fxml"));
        scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        Topic myElement = new Topic("Meow","Mur");
        //myElement.getTopic().setStyle("-fx-fill: white;");
        Topic myElement2 = new Topic("Meow1","Mur1");
        System.out.println(myElement);
        MessengerController controller = ((MessengerController)loader.getController());
        controller.init(myElement);
        controller.init(myElement2);
    }
}
