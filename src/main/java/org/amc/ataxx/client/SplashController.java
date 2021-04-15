package org.amc.ataxx.client;

import javafx.application.Platform;
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

/**
 * The Controller for the welcome Splash screen
 */
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
    private Label feedback;

    /** The view the Controller sends UI updates to */
    private SplashView view;

    /**
     * Constructor for SplashController
     *
     * @param stage The stage to display
     */
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
     * Initialize the event handlers
     */
    @FXML
    private void initialize() {
        // set click handlers
        connectButton.setOnAction(event -> connectClick());
        gameButton.setOnAction(event -> gameClick());
        spectateButton.setOnAction(event -> spectateClick());

        // event handler to display feedback on valid/invalid usernames
        usernameField.textProperty().addListener(event -> {
            if (Utils.verifyUsername(usernameField.getText())) {
                usernameField.getStyleClass().clear(); // prevents surplus of valid/invalid classes
                usernameField.getStyleClass().addAll("text-input", "text-field", "input", "valid");
                feedback.setText("");
            } else {
                usernameField.getStyleClass().clear();
                usernameField.getStyleClass().addAll("text-input", "text-field", "input", "invalid");
                feedback.setText("");
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
            view.promptForNewUsername(feedback);
        }
    }

    /**
     * Handles a click of the "Find Game" button
     */
    public void gameClick() {
        sendRequest("GAME");
    }

    /**
     * Sends a spectate request for the game with the ID requested
     */
    public void spectateClick() {
        sendRequest("SPEC\\" + gameIDField.getText());
    }

    /**
     * Updates the UI when the user is connected
     */
    public void disableConnect() {
        view.disableConnect(usernameField, connectButton, gameButton, spectateBox, spectateButton);
    }

    /**
     * Updates the UI with feedback for the user
     *
     * @param message the message to display to the user
     */
    public void giveFeedback(String message) {
        Platform.runLater(()-> {
            this.feedback.setText(message);
        });
    };

    /**
     * Disables the game/spectate buttons and ID field when user is waiting for an opponent
     */
    public void disableButtons() {
        view.disableButtons(gameButton, gameIDField, spectateButton);
    }
}
