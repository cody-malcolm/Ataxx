package org.amc.ataxx;

import org.javatuples.Pair;

import java.util.ArrayList;

/**
 * GameLogic is a static class that provides methods to interpret a board or evaluate the legality of moves. It is used
 * by both the Server and Client.
 */
public class GameLogic {

    // if unsure what these methods should do, see GameLogicTest
    public static boolean validateMove(String board, Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
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

    // might be redundant, or only needed private
    public static boolean boardFull(String board) {
        return false;
    }


    /**
     * Checks if the indicated player has any legal moves.
     *
     * @param board the board to check
     * @param key the key of the player to check
     * @return false unless the player has no legal moves
     */
    public static boolean noLegalMoves(String board, char key) {
        return false;
    }

    /**
     * Checks if the board is full, and if so returns the key of the player with more squares filled. Otherwise returns
     * '-'
     *
     * @return the key of the player who won, or '-'
     */
    public char checkForWinner(String board) {
        return '-';
    }

}
