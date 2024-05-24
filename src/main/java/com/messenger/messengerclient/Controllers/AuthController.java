package com.messenger.messengerclient.Controllers;

import com.messenger.messengerclient.Application;
import com.messenger.messengerclient.Models.Communication.ConnectionActor;
import com.messenger.messengerclient.Models.Entities.User;
import com.messenger.messengerclient.Models.UI;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class AuthController implements Controller {

    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField loginField;
    @FXML
    Button loginBtn;

    EventHandler<MouseEvent> btnOnClick = event -> {
        if(tryAuthorize())
            UI.showMessenger();
    };

    public boolean tryAuthorize(){
        String token = ConnectionActor.tryAuthorize(loginField.getText(),passwordField.getText());
        Application.setUserToken(token);
        if(Application.getUserToken() != null) {
            User user = new User();
            user.setUserName(loginField.getText());
            Application.getMessenger().setUser(user);
            System.out.println("vse ok v avtorizacii");
            return true;
        }
        return false;
    }
    public void initialize(){
        loginBtn.setOnMouseClicked(btnOnClick);
    }
}
