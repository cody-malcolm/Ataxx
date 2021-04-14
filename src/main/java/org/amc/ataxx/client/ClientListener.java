package org.amc.ataxx.client;

import javafx.stage.Stage;
import org.amc.Utils;
import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Listens for and manages Server responses
 */
public class ClientListener extends Thread {
    /** The User associated with the controller */
    private User user;

    private GameView view;

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
        view = GameView.getInstance();
        view.setClientListener(this);
        this.splashController = splashController;
    }

    private boolean establishConnection() {
        boolean connected = false;
        int attempts = 1;
        while (!connected && attempts < 10 && !this.abortConnectionAttempt) {
            try {
                this.socket = new Socket(HOST, PORT);
                connected = true;
            } catch (UnknownHostException e) {
                System.err.println("Attempt " + attempts++ + ": Unknown host: " + HOST);
                Utils.sleep(1250);
            } catch (IOException e) {
                System.err.println("Attempt " + attempts++ + ": Error when attempting to connect to " + HOST);
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
            System.err.println("Unable to connect to " + HOST);
            return;
        }

        if (!performHandshake()) {
            System.err.println("Error during handshake process");
            return;
        }
        this.splashController.disableConnect();
        boolean disconnected = false;
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
        // could make this conditional on response if the server later sends information before a game is requested
        if (!showingGameScene) {
            showingGameScene = showGameScene();
        }
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
//        updateScene(); ??
        String[] args = response.split("\\\\");

        char activePlayer = args[4].charAt(0);
        char key = args[5].charAt(0);
        this.user.setBoard(args[3]);


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

    /**
     * Given a square that was clicked on, gives the information to the User.
     *
     * @param square the square that was clicked on
     */
    public void processMouseClick(Pair<Integer, Integer> square) {
        String move = user.clicked(square);
        if (null != move) {
            sendRequest("MOVE\\" + move);
        } else {
            String board = user.getBoard();
            Pair<Integer, Integer> source = user.getSource();
            ArrayList<Pair<Integer, Integer>> steps = GameLogic.getSteps(board, source);
            ArrayList<Pair<Integer, Integer>> jumps = GameLogic.getJumps(board, source);
            view.renderBoard(board);
            view.applyHighlighting(source, steps, jumps, user.getKey());
        }
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
