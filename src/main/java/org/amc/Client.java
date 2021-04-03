package org.amc;

import javafx.application.Application;
import javafx.stage.Stage;

public class Client extends Application {
    private static ClientListener listener;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setListener(ClientListener listener) {
        Client.listener = listener;
    }

    @Override
    public void start(Stage primaryStage) {
        new SplashController(primaryStage).showStage();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (null != Client.listener) {
            listener.closeSocket();
        }
    }
}
