package org.amc.ataxx.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    /** Listens for and manages Server responses and user mouse clicks */
    private static ClientListener listener;

    /**
     * Main method. Starts the user interface for the client
     *
     * @param args None
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Static method setting the clientListener
     * @param listener clientListener for server responses
     */
    public static void setListener(ClientListener listener) {
        Main.listener = listener;
    }

    /** Method starting the application
     * @param primaryStage primary stage of the application
     */
    @Override
    public void start(Stage primaryStage) {
        new SplashController(primaryStage).showStage();
    }

    /**
     * Method closing the client window and closing the ClientListener for server responses
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        if (null != Main.listener) {
            listener.sendRequest("CLSE");
            listener.closeSocket();
        }
    }
}
