package org.amc.ataxx.client;

import javafx.stage.Stage;
import org.amc.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Listens for and manages Server responses and user mouse clicks
 */
public class ClientListener extends Thread {
    /** The User associated with the controller */
    private User user;

    private boolean showingGameScene = false;
    final private Stage stage;
    private GameController gameController;
    private SplashController splashController;

    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    private static String HOST = "localhost";
    final private static int PORT = 25436;

    /** The access code to establish a connection */
    final private String ACCESS_CODE = "arstdhneio";
    private boolean abortConnectionAttempt = false;

    public ClientListener(String username, Stage stage, String host, SplashController splashController) {
        if (!"".equals(host)) {
            this.HOST = host;
        }
        this.user = new User(username, '0');
        this.stage = stage;
        this.splashController = splashController;
    }

    private boolean establishConnection() {
        boolean connected = false;
        int attempts = 1;
        while (!connected && attempts < 20 && !this.abortConnectionAttempt) {
            try {
                this.socket = new Socket(HOST, PORT);
                connected = true;
            } catch (UnknownHostException e) {
                String message = "Attempt " + attempts++ + ": Unknown host: " + HOST;
                splashController.giveFeedback(message);
                System.err.println(message);
                Utils.sleep(1250);
            } catch (IOException e) {
                String message = "Attempt " + attempts++ + ": Error when attempting to connect to " + HOST;
                splashController.giveFeedback(message);
                System.err.println(message);
                Utils.sleep(1250);
            }
        }
        return connected;
    }

    private boolean performHandshake() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return false;
        }

        out.println("CODE\\" + ACCESS_CODE);
        out.println("NAME\\" + this.user.getUsername());
        return true;
    }

    @Override
    public void run() {
        if (!establishConnection()) {
            String message = "Unable to connect to " + HOST;
            splashController.giveFeedback(message);
            System.err.println(message);
            return;
        }

        if (!performHandshake()) {
            String message = "Error during handshake process";
            splashController.giveFeedback(message);
            System.err.println(message);
            return;
        }

        this.splashController.disableConnect();
        boolean disconnected = false;
        splashController.giveFeedback("Successfully connected to " + HOST);
        while (!disconnected) {
            try {
                disconnected = processResponse(in.readLine());
            } catch (SocketException e) {
                disconnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean processResponse(String response) {

        if (null == response) {
            return true;
        }

//        System.out.println(response); // just during development

        String type = response.substring(0, response.indexOf('\\'));

        if (type.regionMatches(true, 0, "GAME", 0, 4)) {
            handleGameResponse(response);
        } else if (type.regionMatches(true, 0, "MSG", 0, 3)) {
            handleNormalMessage(response.split("\\\\")[1]);
        } else if (type.regionMatches(true, 0, "CHAT", 0, 4)) {
            handleChatMessage(response.substring(response.indexOf('\\')+1));
        } else if (type.regionMatches(true, 0, "ERR", 0, 3)) {
            handleErrorMessage(response.split("\\\\")[1]);
        } else if (type.regionMatches(true, 0, "INFO", 0, 4)) {
            handleInfoResponse(response.substring(response.indexOf('\\')+1));
        }

        return false;
    }

    private void handleInfoResponse(String response) {
        String[] args = response.split("\\\\");
        user.setDisplayNames(args[0], args[1]);
        user.setGameId(args[2]);
        if (user.getGameFinished()) {
            user.prepNewGame();
        }
    }

    private boolean showGameScene() {
        gameController = new GameController(this.user, this);
        return true;
    }

    private void handleChatMessage(String message) {
        gameController.processMessage(message, 'd');
    }

    private void handleErrorMessage(String message) {
        gameController.processMessage(message, 'b');
    }

    private void handleNormalMessage(String message) {
        gameController.processMessage(message, 'i');
    }

    private void handleGameResponse(String response) {
        // could make this conditional on response if the server later sends information before a game is requested

//        updateScene(); ??
        String[] args = response.split("\\\\");

        char activePlayer = args[4].charAt(0);
        char key = args[5].charAt(0);
        this.user.setBoard(args[3]);
        boolean gameActive = args[7].equals("true");
        boolean gameFinished = args[8].equals("true");
        this.user.setGameActive(gameActive);
        this.user.setGameFinished(gameFinished);

        if (!gameActive) {
            splashController.giveFeedback("Waiting for opponent...");
            splashController.disableButtons();
            return;
        }

        if (!showingGameScene) {
            showingGameScene = showGameScene();
        }

        if (args[2].equals("none")) {
            this.gameController.refreshBoard(args[3], activePlayer, key, this.user.getDisplayNames(), this.user.getGameId());
        } else {
            gameController.handleMove(args[1], args[2], args[3], activePlayer, key, this.user.getDisplayNames());
        }

        gameController.updateBoard(args[3]);

        char winner = args[6].charAt(0);
        if (winner != '-') {
            gameController.winnerDetermined(winner);
        }

    }

    public void sendRequest(String request) {
        // guard to prevent NullPointerException when in process of attempting to connect
        if (null != out) {
            out.println(request);
        }
    }

    public Stage getStage() {
        return this.stage;
    }

    public void closeSocket() {
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            this.abortConnectionAttempt = true;
        }
    }
}
