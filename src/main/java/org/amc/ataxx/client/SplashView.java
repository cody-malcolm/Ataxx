package org.amc.ataxx.client;

import javafx.scene.control.Button;

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

    public void disableConnect(Button connectButton) {
        connectButton.setDisable(true);
    }

    public void promptForNewUsername() {
    }
}
