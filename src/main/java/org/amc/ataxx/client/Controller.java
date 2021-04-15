package org.amc.ataxx.client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Abstract superclass for the Controllers
 */
public abstract class Controller {
    /** The ClientListener that listens for updates from server */
    protected ClientListener listener;
    /** The stage to display the scenes on */
    final protected Stage stage;

    /**
     * Constructor for Controller
     *
     * @param stage The Stage for the Client Application
     */
    public Controller(Stage stage) {
        this.stage = stage;
        stage.setTitle("Welcome to Ataxx!");
    }

    /**
     * Constructor for Controller
     *
     * @param stage The Stage for the Client Application
     * @param listener The ClientListener the Controller will communicate with
     */
    public Controller(Stage stage, ClientListener listener) {
        this.stage = stage;
        this.listener = listener;
        Platform.runLater(() -> {
            stage.setTitle("Ataxx!!!");
        });
    }

    /**
     * Sends the request to the listener (provided one exists)
     *
     * @param request the request to send
     */
    protected void sendRequest(String request) {
        // guard against no listener
        if (null != listener) {
            listener.sendRequest(request);
        }
    }

    /**
     * Updates the scene
     *
     * @param scene The new scene to display
     */
    protected void setScene(Scene scene) {
        Platform.runLater(()-> {
            this.stage.setScene(scene);
        });
    }

    /**
     * Shows the stage
     */
    public void showStage() {
        stage.show();
    }
}
