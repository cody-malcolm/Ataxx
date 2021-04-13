package org.amc.ataxx.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private static ClientListener listener;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setListener(ClientListener listener) {
        Main.listener = listener;
    }

    @Override
    public void start(Stage primaryStage) {
        new SplashController(primaryStage).showStage();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (null != Main.listener) {
            listener.sendRequest("RESN");
            listener.closeSocket();
        }
    }
}
