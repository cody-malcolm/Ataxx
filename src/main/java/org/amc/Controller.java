package org.amc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.javatuples.Pair;

import java.util.ArrayList;

public class Controller {
    // probably changed to 2nd view later
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label messages;
    @FXML
    private TextField chat;

    /** The User associated with the controller */
    private User user;
    /** The ClientListener that listens for updates from server */
    private ClientListener listener;

    /**
     * Handles a click of the connect button.
     */
    public void connect() {
        // TODO Needs to verify username - can't be "", "-", or contain a '\' - also should have a reasonable max characters
        // user.setUsername(username);
        this.listener = new ClientListener(this, user.getUsername());
        listener.start();
    }

    /**
     * Handles a click of the "Find Game" button
     */
    public void game() {
        sendRequest("GAME");
    }

    /**
     * Handles a click of the "Resign" button
     */
    public void resign() {
        sendRequest("RESN");
    }

    /**
     * Handles a click of the "Disconnect" button
     */
    public void disconnect() {
        sendRequest("CLSE");
    }

    /**
     * Handles the initialization of the Client-side Application
     */
    public void initialize() {
        // set the controller for the static Views class
        Views.setController(this);

        // initialize the User
        user = new User(this);
        user.setKey('4'); // TODO review this, used to be '1', should be something other than 1/2 before game starts I think

        // initialize the Canvas
        Views.createCanvas(borderPane);
    }

    /**
     * Given a square that was clicked on, gives the information to the User.
     *
     * @param square the square that was clicked on
     */
    public void processMouseClick(Pair<Integer, Integer> square) {
        user.clicked(square);
    }

    /**
     * Requests a move be performed.
     *
     * @param source the source square
     * @param dest the destination square
     */
    public void requestMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest) {
        StringBuilder request = new StringBuilder();
        request.append("MOVE")
                .append("\\")
                .append(source.getValue0())
                .append(source.getValue1())
                .append(dest.getValue0())
                .append(dest.getValue1());

        sendRequest(request.toString());
    }

    /**
     * Sends the request to the listener (provided one exists)
     *
     * @param request the request to send
     */
    private void sendRequest(String request) {
        // guard against no listener
        if (null != listener) {
            listener.sendRequest(request);
        }
    }

    /**
     * Re-renders the board with the given game state, and updates the key and activePlayer stored by the User.
     * @param board the board state to render
     * @param activePlayer the active player
     * @param key the User's key
     */
    public void refreshBoard(String board, char activePlayer, char key) {
        Views.renderBoard(board);
        user.setKey(key);
        user.setActivePlayer(activePlayer);
        highlightSquares(board);
    }

    /**
     * Asks the view to render the legal move indicators.
     */
    public void highlightSquares(String board) {
        Pair<Integer, Integer> source = user.getSource();
        if (null != source) {
            ArrayList<Pair<Integer, Integer>> steps = Board.getSteps(board, source);
            ArrayList<Pair<Integer, Integer>> jumps = Board.getJumps(board, source);
            Views.highlightDestinationSquares(steps, jumps);
        }
    }

    /**
     * Updates the key and activePlayer stored by the User, and asks the View to animate the move
     *
     * @param oldBoard the previous authoritative board state
     * @param move the move to animate
     * @param newBoard the new authoritative board state
     * @param activePlayer the authoritative active player
     * @param key the User's key
     */
    public void handleMove(String oldBoard, String move, String newBoard, char activePlayer, char key) {
        user.setKey(key);
        user.setActivePlayer(activePlayer);
        Views.animateMove(oldBoard, newBoard, move);
    }

    /**
     * Updates the board stored by the User
     *
     * @param board the new board for the User to store
     */
    public void updateBoard(String board) {
        user.setBoard(board);
    }

    /**
     * Updates the view with the game over screen according to if the User won or lost
     *
     * @param username the username of the winner
     */
    public void winnerDetermined(String username) {
        Views.displayWinner(username);
    }

    public void processMessage(String message, char style) {
//        Views.displayMessage(message, style);
        Platform.runLater(()-> {
            if (messages.getText().equals("")) {
                messages.setText(message);
            } else {
                messages.setText(messages.getText() + "\r\n" + message);
            }
        });

    }

    public void chat() {
        String message = chat.getText();
        if (!message.equals("")) {
            sendRequest("CHAT\\" + message);
        }

        chat.setText("");
    }
}
