package org.amc;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Player extends Thread {
    /** The Socket this connection uses */
    final private Socket socket;
    /** The Reader for the client input */
    final private BufferedReader requestInput;
    /** The Writer for the server output */
    final private PrintWriter responseOutput;
    /** The client's IP address */
    final private String clientIP;
    /** The client's self-identified alias (appended with their IP) */
    private String username;
    /** The access code to establish a connection */
    final private String ACCESS_CODE = "arstdhneio";
    /** The ID of the game the player is playing */
    private String gameID = null;
    /** The key to represent whether the player is Player '1' or '2' */
    private char key;

    /**
     * Constructor for Player. Stores the socket parameter, establishes the input and output streams for the connection,
     * stores the client's IP, and reads in the first line of the request (which is the access code)
     *
     * @param socket The clientSocket that has been accepted by the server
     * @throws IOException when there is a failure to get the InputStream or OutputStream for the socket
     */
    public Player(Socket socket) throws IOException {
        // copy the socket to instance fields
        this.socket = socket;

        // store the client's IP address for logging purposes
        this.clientIP = socket.getInetAddress().toString();

        // initialize the BufferedReader and PrintWriter that will be used to communicate with client
        requestInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        responseOutput = new PrintWriter(socket.getOutputStream(), true);

        // log the establishment of the connection
        log("Establishing connection with " + clientIP.substring(1));
    }

    /**
     * Takes the given input message and copies it to both a 'log.txt' file and the console. The `log.txt` is
     * prepended with a timestamp. If the file 'log.txt' is not found, it is created.
     *
     * @param message The message to log.
     */
    private void log(String message) {
        try {
            // create the File
            File file = new File("log.txt");

            // initialize the PrintWriter variable
            PrintWriter output;
            synchronized (this) {
                // if there is already a log.txt file
                if (file.exists()) {
                    // initialize the PrintWriter to append to the existing file
                    output = new PrintWriter(new FileOutputStream(file, true));
                } else {
                    // initialise the PrintWriter to append to a new file
                    output = new PrintWriter(file);
                }

                // append the message to the file and close the PrintWriter
                output.append(String.valueOf(new Date())).append(": ").append(message).append("\r\n");
                output.close();
            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        // print the message to the console
        System.out.println(message);
    }

    /**
     * This method is automatically invoked when the server Thread associated with "this" is started. It TODO
     */
    @Override
    public void run() {
        // authentication flag and number of authentication attempts
        boolean authenticated = false;
        int attempts = 0;

        // initialize variable for input
        String input = null;

        // TODO put authentication and identification in helper methods
        while (!authenticated && attempts < 3) {
            // read in a line of input
            try {
                input = requestInput.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // initialize array for each word of input
            String[] args;

            if (null != input) {
                // split the input into its component pieces
                args = input.split("\\\\");

                // if it's a code request
                if (args[0].equals("CODE") && args.length > 1) {
                    authenticated = args[1].equals(ACCESS_CODE);
                }
            }
            attempts++;
        }

        // if the client can't authenticate, close the connection
        if (!authenticated) {
            terminateConnection();
        }

        input = null;
        boolean identified = false;

        // get the username
        while (!identified) {
            // read in a line of input
            try {
                input = requestInput.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // initialize array for each word of input
            String[] args;

            if (null != input) {
                // split the input into its component pieces
                args = input.split("\\\\");

                // if it's a name request
                if (args[0].equals("NAME") && args.length > 1) {
                    this.username = args[1];
                    identified = true;
                }
            }
        }

        boolean terminateConnection = false;
        while (!terminateConnection) {
            terminateConnection = handleRequest();
        }

        terminateConnection();
    }

    /**
     * Closes the input and output streams, logs the termination of the connection, and closes the socket.
     */
    private void terminateConnection() {
        try {
            // close the InputStream, OutputStream, and socket connection
            requestInput.close();
            responseOutput.close();
            log("Terminating connection with " + username + clientIP);
            socket.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Responsible for sorting the request type and calling the appropriate handler(s). The handler or handlers
     * process the request and return the response. Concatenates the response and sends the complete response to the
     * client.
     */
    private boolean handleRequest() {
        String input = null;
        try {
            input = requestInput.readLine();
        } catch(IOException e) {
            log("There was an error when reading the socket associated with " + username + clientIP);
            return true;
        }
        if (null == input) {
            return true;
        }

        // log the request and client making the request
        log(input + " request from " + this.username + this.clientIP);

        String[] request = input.split("\\\\");

        return processRequest(request);
    }

    private boolean processRequest(String[] request) {
        String type = request[0];
        String arg = null;
        if (request.length > 1) {
            arg = request[1];
        }

        String oldBoard = null;
        if (null != this.gameID) {
            oldBoard = Game.getGame(this.gameID).getBoard();
        } else {
            oldBoard = Board.INITIAL_BOARD;
        }

        String move = "none";
        // accepted types are "GAME", "CHAT", "SPEC", "MOVE", "RESN", and "CLSE"
        if (type.regionMatches(true, 0, "move", 0, 4)) {
            handleMoveRequest(arg);
            move = arg;
        } else if (type.regionMatches(true, 0, "chat", 0, 4)) {
            handleChatRequest(arg);
        } else if (type.regionMatches(true, 0, "game", 0, 4)) {
            handleGameRequest();
        } else if (type.regionMatches(true, 0, "spec", 0, 4)) {
            handleSpectateRequest(arg);
            return false;
        } else if (type.regionMatches(true, 0, "resn", 0, 4)) {
            handleResignRequest();
        } else if (type.regionMatches(true, 0, "clse", 0, 4)) {
            return true;
        } else {
            handleUnknown();
        }

        if (null != this.gameID) {
            updateClients(oldBoard, move, Game.getGame(this.gameID));
        }

        return false;
    }

    private void handleMoveRequest(String move) {
        if (null != gameID) {
            Game game = Game.getGame(gameID);
            game.applyMove(move, this.key);
        }
    }

    private void handleChatRequest(String message) {
        // TODO
    }

    private void handleResignRequest() {
        if (null != gameID) {
            Game game = Game.getGame(gameID);
            game.handleResign(this.key);
        }
    }

    private void handleSpectateRequest(String arg) {
        if (null == this.gameID) {
            // TODO
        }
    }

    private void updateClients(String old, String move, Game game) {
        String newBoard = game.getBoard();
        char activePlayer = game.getActivePlayer();
        Player opponent = game.getPlayer(this.key == '1' ? 1 : 0);
        if (null != opponent) {
            opponent.sendGameState(old, move, newBoard, activePlayer);
        }
        this.sendGameState(old, move, newBoard, activePlayer);
    }

    // move can be "0123" format or "none"
    private void sendGameState(String old, String move, String newBoard, char activePlayer) {
        StringBuilder response = new StringBuilder();
        response.append("GAME\\")
                .append(old).append("\\")
                .append(move).append("\\")
                .append(newBoard).append("\\")
                .append(activePlayer).append("\\")
                .append(this.key);

        log("Sending response '" + response.toString() + " to " + this.username + this.clientIP);
        responseOutput.println(response.toString());
    }

    private void handleGameRequest() {
        // reject game requests if player is already playing
        if (null == this.gameID) {
            Game game = Game.getAvailableGame();
            this.gameID = game.getID();
            this.key = game.addPlayer(this);
        }
    }

    /**
     * This method returns a String of the following format: '404\Command not found'
     */
    private void handleUnknown() {
        responseOutput.println("404\\Command not found");
    }

    public String getUsername() {
        return this.username;
    }
}
