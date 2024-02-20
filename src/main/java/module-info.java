module com.messenger.messengerclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.messenger.messengerclient to javafx.fxml;
    exports com.messenger.messengerclient;
    exports com.messenger.messengerclient.Controllers;
    opens com.messenger.messengerclient.Controllers to javafx.fxml;
    exports com.messenger.messengerclient.Models;
    opens com.messenger.messengerclient.Models to javafx.fxml;
}