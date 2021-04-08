package org.amc.ataxx.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class GameController extends Controller {
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextArea messages;
    @FXML
    private TextField chat;
    @FXML
    private Button resignButton;
    @FXML
    private Button disconnectButton;

    private GameView view;

    /** The User associated with the controller */
    private User user;
    /** The ClientListener that listens for updates from server */
    private ClientListener listener;

    public GameController(User user, ClientListener listener) { // TODO can probably refactor this to reduce redundancy
        super(listener.getStage(), listener);
        this.user = user;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));

            loader.setController(this);

            setScene(new Scene(loader.load()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles a click of the "Resign" button
     */
    public void resignClick() {
        sendRequest("RESN");
    }

    /**
     * Handles a click of the "Disconnect" button
     */
    public void disconnectClick() {
        sendRequest("CLSE");
    }

    /**
     * Handles the initialization of the Client-side Application
     */
    @FXML
    private void initialize() {
        resignButton.setOnAction(event -> resignClick());
        disconnectButton.setOnAction(event -> disconnectClick());
        // initialize the Canvas
        view = GameView.getInstance();
        view.createCanvas(borderPane);
    }

    /**
     * Re-renders the board with the given game state, and updates the key and activePlayer stored by the User.
     *
     * @param board the board state to render
     * @param activePlayer the active player
     * @param key the User's key
     */
    public void refreshBoard(String board, char activePlayer, char key) {
        view.renderBoard(board);
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
            ArrayList<Pair<Integer, Integer>> steps = GameLogic.getSteps(board, source);
            ArrayList<Pair<Integer, Integer>> jumps = GameLogic.getJumps(board, source);
            view.highlightDestinationSquares(steps, jumps);
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
        view.animateMove(oldBoard, newBoard, move);
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
        view.displayWinner(username);
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
