package org.amc.ataxx.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.amc.Utils;

import java.io.IOException;

public class SplashController extends Controller {
    @FXML
    private TextField gameIDField;
    @FXML
    private HBox spectateBox;
    @FXML
    private Button connectButton;
    @FXML
    private Button gameButton;
    @FXML
    private Button spectateButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField hostIPField;
    @FXML
    private Label usernameFeedback;

    private SplashView view;

    public SplashController(Stage stage) {
        super(stage);
        view = SplashView.getInstance();
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

        usernameField.textProperty().addListener(event -> {
            if (Utils.verifyUsername(usernameField.getText())) {
                usernameField.getStyleClass().clear();
                usernameField.getStyleClass().addAll("text-input", "text-field", "input", "valid");
            } else {
                usernameField.getStyleClass().clear();
                usernameField.getStyleClass().addAll("text-input", "text-field", "input", "invalid");
            }
        });
    }

    /**
     * Handles a click of the connect button.
     */
    public void connectClick() {
        String username = usernameField.getText();
        String hostIP = hostIPField.getText();
        if (Utils.verifyUsername(username)) {
            this.listener = new ClientListener(username, this.stage, hostIP, this);
            Main.setListener(this.listener);
            this.listener.start();
        } else {
            view.promptForNewUsername(usernameFeedback);
        }
    }

    /**
     * Handles a click of the "Find Game" button
     */
    public void gameClick() {
        sendRequest("GAME");
    }

    public void spectateClick() {
        sendRequest("SPEC\\" + gameIDField.getText()); // TODO get gameID from UI
    }

    public void disableConnect() {
        view.disableConnect(usernameField, connectButton, gameButton, spectateBox, spectateButton);
    }

    public void handleUsernameChange(ActionEvent event) {
        System.out.println(event);
    }
}
