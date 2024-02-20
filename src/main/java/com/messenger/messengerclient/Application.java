package com.messenger.messengerclient;

import com.messenger.messengerclient.Models.Messenger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class Application extends javafx.application.Application {
    private Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        //scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        Button btn = (Button) scene.lookup("#loginBtn");
        btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                hide();
                Messenger messenger = new Messenger();
                try {
                    messenger.show();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public void hide(){
        stage.hide();
    }

    public static FXMLLoader getResource(String name){
        return  new FXMLLoader(Application.class.getResource(name));
    }

    public static void main(String[] args) {
        launch();
    }
}