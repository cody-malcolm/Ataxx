package org.amc.ataxx.client;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class Controller {
    /** The ClientListener that listens for updates from server */
    protected ClientListener listener;
    final protected Stage stage;

    public Controller(Stage stage) {
        this.stage = stage;
        stage.setTitle("Welcome to Ataxx!");
    }

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

    protected void setScene(Scene scene) {
        Platform.runLater(()-> {
            this.stage.setScene(scene);
        });
    }

    public void showStage() {
        stage.show(); // maybe .showAndWait()?
    }
}
