package org.amc;

import org.javatuples.Pair;

import java.util.ArrayList;

public class Board {
    // depends on encoding you choose, feel free to change, just a temp for testing controller
    final protected static String INITIAL_BOARD = "1-----2/-------/-------/-------/-------/-------/2-----1";

    private String board;
    private char activePlayer;

    public Board() {
        // needs to set board to initial state (see tests) and pick '1' or '2' at random
    }

    public Board(String board, char c) {
        this.board = board;
        this.activePlayer = c;
    }

    public static boolean validateMove(String board, Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char s) {
        return true;
    }

    public static boolean isSquareEmpty(String board, Pair<Integer, Integer> square) {
        return true;
    }

    public static ArrayList<Pair<Integer, Integer>> getAdjacent(Pair<Integer, Integer> square) {
        return new ArrayList<Pair<Integer, Integer>>();
    }

    public static ArrayList<Pair<Integer, Integer>> getSteps(String board, Pair<Integer, Integer> square) {
        return new ArrayList<Pair<Integer, Integer>>();
    }

    public static ArrayList<Pair<Integer, Integer>> getJumps(String board, Pair<Integer, Integer> square) {
        return new ArrayList<Pair<Integer, Integer>>();
    }

    public static char getSquare(String board, Pair<Integer, Integer> square) {
        return 'a';
    }

    public static boolean boardFull(String board1) {
        return false;
    }

    public String getBoard() {
        return this.board;
    }

    public char getActivePlayer() {
        return this.activePlayer;
    }

    // needs to validate the move before applying it (use Board.validateMove)
    public void applyMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
    }
}
