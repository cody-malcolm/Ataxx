package org.amc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.javatuples.Pair;

import java.util.ArrayList;

public class Controller {
    @FXML
    private BorderPane borderPane;

    private User user;

    private ClientListener listener;

    public void connect(ActionEvent actionEvent) {
        this.listener = new ClientListener(this, user.getUsername());
        listener.start();
    }

    public void username(ActionEvent actionEvent) {
        // TODO needs to full from a field, once created
        user.setUsername("AMC");
    }

    public void game(ActionEvent actionEvent) {
        sendRequest("GAME");
    }

    public void resign(ActionEvent actionEvent) {
        sendRequest("RESN");
    }

    public void disconnect(ActionEvent actionEvent) {
        sendRequest("CLSE");
    }

    public void initialize() {
        Views.setController(this);
        Views.createCanvas(borderPane);

        user = new User(this);
        user.setKey('1');
    }

    public void processMouseClick(Pair<Integer, Integer> square) {
        user.clicked(square);
    }

    public void requestMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        StringBuilder request = new StringBuilder();
        request.append("MOVE")
                .append("\\")
                .append(source.getValue0())
                .append(source.getValue1())
                .append(dest.getValue0())
                .append(dest.getValue1());

        sendRequest(request.toString());
    }

    private void sendRequest(String request) {
        if (null != listener) {
            listener.sendRequest(request);
        }
    }

    public void refreshBoard(String board, char activePlayer, char key) {
        Views.renderBoard(board);
        user.setKey(key);
        user.setActivePlayer(activePlayer);
    }

    public void updateBoard(String oldBoard, String move, String newBoard, char activePlayer, char key) {
        refreshBoard(oldBoard, activePlayer, key);
        Views.animateMove(oldBoard, newBoard, move);
    }

    public void highlightSquares(String board) {
        Pair<Integer, Integer> source = user.getSource();
        if (null != source) {
            ArrayList<Pair<Integer, Integer>> steps = Board.getSteps(board, source);
            ArrayList<Pair<Integer, Integer>> jumps = Board.getJumps(board, source);
            Views.highlightDestinationSquares(steps, jumps);
        }
    }
}
