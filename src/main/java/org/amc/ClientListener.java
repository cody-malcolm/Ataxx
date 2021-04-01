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

        String type = response.substring(0, response.indexOf('\\'));

        if (type.regionMatches(true, 0, "GAME", 0, 4)) {
            handleGameResponse(response);
        }

        return false;
    }

    private void handleGameResponse(String response) {

        String[] args = response.split("\\\\");

        char activePlayer = args[4].charAt(0);
        char key = args[5].charAt(0);

        if (args[2].equals("none")) {
            controller.refreshBoard(args[3], activePlayer, key);
            controller.highlightSquares(args[3]);
        } else {
            controller.updateBoard(args[1], args[2], args[3], activePlayer, key);
        }
    }

    public void sendRequest(String request) {
        // guard to prevent NullPointerException when in process of attempting to connect
        if (null != out) {
            out.println(request);
        }
    }
}
