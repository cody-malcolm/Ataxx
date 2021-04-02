package org.amc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Listens for and manages Server responses
 */
public class ClientListener extends Thread {

    private Controller controller;

    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;

    final private static String HOST = "localhost"; // TODO later the connect screen should let user enter an IP
    final private static int PORT = 25436;

    private String alias = "Anonymous"; // "Anonymous" in case String passed to constructor is somehow null

    /** The access code to establish a connection */
    final private String ACCESS_CODE = "arstdhneio";

    public ClientListener(Controller controller, String username) {
        this.controller = controller;
        this.alias = username;
    }

    private boolean establishConnection() {
        boolean connected = false;
        int attempts = 1;
        while (!connected && attempts < 10) {
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
        out.println("NAME\\" + this.alias);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean processResponse(String response) {
        if (null == response) {
            return true;
        }

        System.out.println(response);

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

    private void handleChatMessage(String message) {
        controller.processMessage(message, 'd');
    }

    private void handleErrorMessage(String message) {
        controller.processMessage(message, 'b');
    }

    private void handleNormalMessage(String message) {
        controller.processMessage(message, 'i');
    }

    private void handleGameResponse(String response) {

        String[] args = response.split("\\\\");

        char activePlayer = args[4].charAt(0);
        char key = args[5].charAt(0);

        if (args[2].equals("none")) {
            controller.refreshBoard(args[3], activePlayer, key);
        } else {
            controller.handleMove(args[1], args[2], args[3], activePlayer, key);
        }
        controller.updateBoard(args[3]);

        String winner = args[6];
        if (!winner.equals("-")) {
            controller.winnerDetermined(winner);
        }
    }

    public void sendRequest(String request) {
        // guard to prevent NullPointerException when in process of attempting to connect
        if (null != out) {
            out.println(request);
        }
    }
}
