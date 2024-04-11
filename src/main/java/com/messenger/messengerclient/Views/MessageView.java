package com.messenger.messengerclient.Views;

import com.messenger.messengerclient.Models.Entities.Message;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MessageView extends BorderPane {
    public Text username = new Text();
    public Text messageText = new Text();
    public Text quotes = new Text();
    {
        this.setStyle("-fx-background-color: grey;");
        username.setFont(Font.font("System", FontWeight.BOLD, 12));
        quotes.setFont(Font.font("System", FontWeight.BOLD, 10));
        BorderPane.setAlignment(quotes, Pos.CENTER_RIGHT);
        BorderPane.setAlignment(messageText, Pos.CENTER_LEFT);
        BorderPane.setMargin(quotes,new Insets(5,5,5,5));
        BorderPane.setMargin(username, new Insets(5,10,5,10));
        BorderPane.setMargin(messageText, new Insets(5,10,10,10));
        this.setTop(username);
        this.setCenter(messageText);
        this.setBottom(quotes);
    }

    public MessageView(Message message){
        this.username.setText(message.getAuthorUsername());
        this.messageText.setText(message.getContent());
        this.quotes.setText("Quotes: "+String.valueOf(message.getQuotes()));
    }
}
