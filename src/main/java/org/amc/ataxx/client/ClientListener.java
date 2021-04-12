package org.amc.ataxx.client;

import javafx.stage.Stage;
import org.amc.Utils;
import org.javatuples.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

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

    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    final private static String HOST = "localhost"; // TODO later the connect screen should let user enter an IP
    final private static int PORT = 25436;

    /** The access code to establish a connection */
    final private String ACCESS_CODE = "arstdhneio";
    private boolean abortConnectionAttempt = false;

    public ClientListener(String username, Stage stage) {
        this.user = new User(username, '0');
        this.stage = stage;
        view = GameView.getInstance();
        view.setClientListener(this);
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
        if (!showingGameScene) {
            showingGameScene = showGameScene();
        }
        if (null == response) {
            return true;
        }

        System.out.println(response); // just during development

        String type = response.substring(0, response.indexOf('\\'));

        if (type.regionMatches(true, 0, "GAME", 0, 4)) {
            handleGameResponse(response);
        } else if (type.regionMatches(true, 0, "MSG", 0, 3)) {
            handleNormalMessage(response.split("\\\\")[1]);
        } else if (type.regionMatches(true, 0, "CHAT", 0, 4)) {
            handleChatMessage(response.substring(response.indexOf('\\')+1));
        } else if (type.regionMatches(true, 0, "ERR", 0, 3)) {
            handleErrorMessage(response.split("\\\\")[1]);
        }

        return false;
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

        if (args[2].equals("none")) {
            gameController.refreshBoard(args[3], activePlayer, key);
        } else {
            gameController.handleMove(args[1], args[2], args[3], activePlayer, key);
        }
        gameController.updateBoard(args[3]);

        String winner = args[6];
        if (!winner.equals("-")) {
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
