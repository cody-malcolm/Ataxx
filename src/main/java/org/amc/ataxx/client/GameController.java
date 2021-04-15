package org.amc.ataxx.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

import java.io.IOException;
import java.util.ArrayList;

public class GameController extends Controller {
    @FXML
    private HBox canvasContainer;
    @FXML
    private VBox messagesContainer;
    @FXML
    private ScrollPane messagesScrollpane;
    @FXML
    private TextField chat;
    @FXML
    private Button replayButton;
    @FXML
    private Button newGameButton;
    @FXML
    private Button resignButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private Label blueNameLabel;
    @FXML
    private Label redNameLabel;
    @FXML
    private Label blueScoreLabel;
    @FXML
    private Label redScoreLabel;
    @FXML
    private Label feedbackLabel;
    @FXML
    private Label buffer;
    @FXML
    private Label gameIDlabel;

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

        // in a future iteration, could return to original splash screen
        System.exit(0);
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
        view.setGameController(this);
        view.createCanvas(canvasContainer);

        this.buffer.setText("\r\n");
    }

    /**
     * Re-renders the board with the given game state, and updates the key and activePlayer stored by the User.
     *
     * @param board the board state to render
     * @param activePlayer the active player
     * @param key the User's key
     */
    public void refreshBoard(String board, char activePlayer, char key, String[] displayNames, String id) {
        view.displayGameId(id, gameIDlabel);
        view.displayCounts(GameLogic.getCounts(board), blueScoreLabel, redScoreLabel);
        view.renderBoard(board);
        user.setKey(key);
        user.setActivePlayer(activePlayer);
        displayActivePlayer();
        view.displayTurn(activePlayer, key, blueNameLabel, redNameLabel, displayNames);
        highlightSquares(board); // TODO 1 99% sure this can be deleted
    }

    private void displayActivePlayer() {
        if (user.usersTurn()) {
            view.feedback("It's your turn!", feedbackLabel);
        } else {
            view.feedback("Opponent's turn...", feedbackLabel);
        }
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
            if (user.getGameActive() && user.usersTurn()) {
                String board = user.getBoard();
                Pair<Integer, Integer> source = user.getSource();
                if (null != source) {
                    ArrayList<Pair<Integer, Integer>> steps = GameLogic.getSteps(board, source);
                    ArrayList<Pair<Integer, Integer>> jumps = GameLogic.getJumps(board, source);
                    view.renderBoard(board);
                    view.applyHighlighting(source, steps, jumps, user.getKey());
                } else {
                    view.renderBoard(board);
                }
            }
        }
    }

    /**
     * Asks the view to render the legal move indicators.
     */
    public void highlightSquares(String board) {
        Pair<Integer, Integer> source = user.getSource();
        if (null != source) {
            ArrayList<Pair<Integer, Integer>> steps = GameLogic.getSteps(board, source);
            ArrayList<Pair<Integer, Integer>> jumps = GameLogic.getJumps(board, source);
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
    public void handleMove(String oldBoard, String move, String newBoard, char activePlayer,
                           char key, String[] displayNames) {
        user.setKey(key);
        user.setActivePlayer(activePlayer);
        displayActivePlayer();
        view.displayCounts(GameLogic.getCounts(newBoard), blueScoreLabel, redScoreLabel);
        view.displayTurn(activePlayer, key, blueNameLabel, redNameLabel, displayNames);
        view.animateMove(oldBoard, newBoard, move, activePlayer);
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
     * @param key the key of the winner
     */
    public void winnerDetermined(char key) {
        String username = user.getName(key);
        view.feedback(username + " has won the game!", feedbackLabel);
    }

    public void processMessage(String message, char style) {
//        Views.displayMessage(message, style);
        if (style == 'd') {
            this.view.addChat(message, this.messagesContainer, this.messagesScrollpane);
        } else if (style == 'i') {
            this.view.addNotification(message, this.messagesContainer, this.messagesScrollpane);
        } else if (style == 'b') {
            this.view.addError(message, this.messagesContainer, this.messagesScrollpane);
        }
//        Platform.runLater(()-> {
//            if (messages.getText().equals("")) {
//                messages.setText(message);
//            } else {
//                messages.setText(messages.getText() + "\r\n" + message);
//            }
//        });

    }

    public void chat() {
        String message = chat.getText();
        if (!message.equals("")) {
            sendRequest("CHAT\\" + message);
        }

        chat.setText("");
    }
}
