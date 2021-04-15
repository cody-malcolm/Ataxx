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
    /** Flag to indicate if the Game Scene is being shown yet */
    private boolean showingGameScene = false;
    /** The Stage the Application is displaying on */
    final private Stage stage;
    /** The Controller for the Game scene */
    private GameController gameController;
    /** The Controller for the Splash scene */
    private SplashController splashController;
    /** The socket used for communications with the server */
    private Socket socket = null;
    /** The Reader for the input stream */
    private BufferedReader in = null;
    /** The Writer for the output stream */
    private PrintWriter out = null;
    /** The Host IP */
    private static String HOST = "localhost";
    /** The port to use */
    final private static int PORT = 25436;
    /** The access code to establish a connection */
    final private String ACCESS_CODE = "arstdhneio";
    /** Flag to indicate if the connection attempt should be aborted due to Application closure */
    private boolean abortConnectionAttempt = false;

    /**
     * Constructor for ClientListener
     *
     * @param username client's username
     * @param stage the stage the application is using
     * @param host server's IP
     * @param splashController the Controller for the splash screen
     */
    public ClientListener(String username, Stage stage, String host, SplashController splashController) {
        // set host if one was entered
        if (!"".equals(host)) {
            this.HOST = host;
        }
        // initialize user
        this.user = new User(username, '0');
        // store params
        this.stage = stage;
        this.splashController = splashController;
    }

    /**
     * Method for establishing the connection with the server
     *
     * @return true if connected to server
     */
    private boolean establishConnection() {
        // initialize flag and attempt counter
        boolean connected = false;
        int attempts = 1;

        while (!connected && attempts < 20 && !this.abortConnectionAttempt) {
            try { // to connect
                this.socket = new Socket(HOST, PORT);
                connected = true;
            } catch (UnknownHostException e) {
                String message = "Attempt " + attempts++ + ": Unknown host: " + HOST;
                splashController.giveFeedback(message);
                System.err.println(message);
                Utils.sleep(1250); // wait to try again
            } catch (IOException e) {
                String message = "Attempt " + attempts++ + ": Error when attempting to connect to " + HOST;
                splashController.giveFeedback(message);
                System.err.println(message);
                Utils.sleep(1250); // wait to try again
            }
        }
        return connected;
    }

    /**
     * Method performing something similar to TCP handshake
     * @return true if successful
     */
    private boolean performHandshake() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return false;
        }
        // send the required details to the server
        out.println("CODE\\" + ACCESS_CODE);
        out.println("NAME\\" + this.user.getUsername());
        return true;
    }

    /**
     * This is what is executed by the thread after we call start() in Main
     */
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

        // user is connected now so disable connect button and update flag
        this.splashController.disableConnect();
        boolean disconnected = false;
        splashController.giveFeedback("Successfully connected to " + HOST);

        while (!disconnected) {
            try {
                disconnected = processResponse(in.readLine()); // read in responses
            } catch (SocketException e) {
                disconnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handling server responses
     *
     * @param response from the server
     * @return false if the response was not null
     */
    private boolean processResponse(String response) {

        if (null == response) {
            return true;
        }

        String type = response.substring(0, response.indexOf('\\'));

        // parse the type of response and send to appropriate handler
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

    /**
     * Getting gameInfo from the server. Acts as a trigger to start a new game, if the game is finished.
     * Info responses are only sent when someone (player or spectator) joins the game, or a replay is requested by
     * both players.
     *
     * @param response from the server
     */
    private void handleInfoResponse(String response) {
        String[] args = response.split("\\\\");
        // update display names and gameId
        user.setDisplayNames(args[0], args[1]);
        user.setGameId(args[2]);

        // start a new game if requested by both players
        if (user.getGameFinished()) {
            user.prepNewGame();
        }
    }

    /**
     * Initializing GameController
     *
     * @return true
     */
    private boolean showGameScene() {
        gameController = new GameController(this.user, this);
        return true;
    }

    /**
     * Handles a chat message
     *
     * @param message the message to display, with 'd'efault styling
     */
    private void handleChatMessage(String message) {
        gameController.processMessage(message, 'd');
    }

    /**
     * Handles an error message
     *
     * @param message the message to display, with 'b'old styling
     */
    private void handleErrorMessage(String message) {
        gameController.processMessage(message, 'b');
    }

    /**
     * Handles a normal message
     *
     * @param message the message to display, 'i'talicized
     */
    private void handleNormalMessage(String message) {
        gameController.processMessage(message, 'i');
    }

    /**
     * Handles game response from the server
     *
     * @param response the game response
     */
    private void handleGameResponse(String response) {
        // split the response
        String[] args = response.split("\\\\");

        // get the active player and this user's key (server sole source of authority)
        char activePlayer = args[4].charAt(0);
        char key = args[5].charAt(0);

        // update the board
        this.user.setBoard(args[3]);

        // update the game status trackers
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

        if (args[2].equals("none")) { // args2 is the move requested, is none at start of game and for resign
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

    /**
     * Sending request to the server
     *
     * @param request the request to send
     */
    public void sendRequest(String request) {
        // guard to prevent NullPointerException when in process of attempting to connect
        if (null != out) {
            out.println(request);
        }
    }

    /**
     * Getter for stage
     *
     * @return this.stage
     */

    public Stage getStage() {
        return this.stage;
    }

    /**
     * Closing the socket
     */
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
