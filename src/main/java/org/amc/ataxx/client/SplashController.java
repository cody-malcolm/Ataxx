package org.amc.ataxx.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.amc.Utils;

import java.io.IOException;

public class SplashController extends Controller {
    @FXML
    private Button connectButton;
    @FXML
    private Button gameButton;
    @FXML
    private Button spectateButton;

    public SplashController(Stage stage) {
        super(stage);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("splash.fxml"));

            loader.setController(this);

            setScene(new Scene(loader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @FXML
    private void initialize() {
        connectButton.setOnAction(event -> connectClick());
        gameButton.setOnAction(event -> gameClick());
        spectateButton.setOnAction(event -> spectateClick());
    }

    /**
     * Handles a click of the connect button.
     */
    public void connectClick() {
        String username = "Anonymous"; // TODO pull from a field in the View
        if (Utils.verifyUsername(username)) {
            this.listener = new ClientListener(username, this.stage);
            Main.setListener(this.listener);
            this.listener.start();
            // TODO need to decide how to prevent multiple "connect" clicks - disable, hide, or flag
        } else {
            // TODO prompt user to correct username
        }
    }

    /**
     * Handles a click of the "Find Game" button
     */
    public void gameClick() {
        sendRequest("GAME");
    }

    public void spectateClick() {
        sendRequest("SPEC\\0000"); // TODO get gameID from UI
    }
}
