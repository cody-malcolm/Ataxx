package org.amc.ataxx.client;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SplashView {
    /** static variable instance of the class */
    private static SplashView instance = null;

    /**
     * Constructor
     */
    private SplashView(){}

    /**
     * Creates (if necessary) and returns an instance of the class
     */
    public synchronized static SplashView getInstance(){
        if (instance == null){
            instance = new SplashView();
        }

        return instance;
    }

    public void disableConnect(TextField usernameField, Button connectButton, Button gameButton, HBox spectateBox, Button spectateButton) {
        usernameField.setEditable(false);
        connectButton.setDisable(true);
        gameButton.setVisible(true);
        spectateBox.setVisible(true);
        spectateButton.setVisible(true);
    }
}
