package com.messenger.messengerclient.Controllers;

import com.messenger.messengerclient.Models.UI;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class NavigationController implements Controller {
    public TextField idField;
    public Button actionButton;
    private MessengerController controller;

    @Override
    public void initialize() {
        controller = (MessengerController) UI.getController();
        Pattern pattern = Pattern.compile("[0-9]*");

        // Создаем UnaryOperator, который фильтрует вводимые символы
        UnaryOperator<TextFormatter.Change> filter = change -> {
            if (pattern.matcher(change.getControlNewText()).matches()) {
                return change;
            } else {
                return null;
            }
        };
        idField.setTextFormatter(new TextFormatter<>(filter));

        actionButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                boolean incorrectInput = false;
                try {
                    controller.goToTopic(Long.parseLong(idField.getText()));
                }catch (NumberFormatException ignored){
                    incorrectInput = true;
                }
                if(!incorrectInput) UI.hideNavigationMenu();
            }
        });
    }
}
