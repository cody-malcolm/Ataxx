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
        int sourceRow = source.getValue0();
        int sourceCol = source.getValue1();
        int destRow = dest.getValue0();
        ;
        int destCol = dest.getValue1();
        boolean legalMove = false;

        if (getSquare(board, source) != key) { //player doesn't control piece
            return false;
        }
        if (!isSquareEmpty(board, dest)) { //destination square occupied
            return false;
        }

        //sqrt((destRow-sourceRow)**2+(destCol-sourceCol)**2)=sqrt(?)
        //sqrt(1**2+1**2)=sqrt(2) valid
        //sqrt(0**2+1**2)=sqrt(1) valid
        //sqrt(2**2+1**2)=sqrt(5) invalid
        //sqrt(0**2+2**2)=sqrt(4) invalid(maybe valid is some scenarios, but that's later)
        if (Math.pow(destRow - sourceRow, 2) + Math.pow(destCol - sourceCol, 2) <= 2.0) {
            legalMove = true;
        } else
            legalMove = false;

        return legalMove;
    }

    public static boolean isSquareEmpty(String board, Pair<Integer, Integer> square) {
        if (getSquare(board, square) == '-')
            return true;
        return false;

    }

    public static ArrayList<Pair<Integer, Integer>> getAdjacent(Pair<Integer, Integer> square) {
        ArrayList<Pair<Integer, Integer>> listAdj = new ArrayList<>();
        int row = square.getValue0();
        int col = square.getValue1();
        if ((row > 0 && row < 7) && (col > 0 && col < 7)) { //centred square
            listAdj.add(new Pair<>(row - 1, col - 1));
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row - 1, col + 1));
            listAdj.add(new Pair<>(row, col - 1));
            listAdj.add(new Pair<>(row, col + 1));
            listAdj.add(new Pair<>(row + 1, col - 1));
            listAdj.add(new Pair<>(row + 1, col));
            listAdj.add(new Pair<>(row + 1, col + 1));
        } else if (row == 0 && (col > 0 && col < 7)) { //first inner row
            listAdj.add(new Pair<>(row, col - 1));
            listAdj.add(new Pair<>(row, col + 1));
            listAdj.add(new Pair<>(row + 1, col - 1));
            listAdj.add(new Pair<>(row + 1, col));
            listAdj.add(new Pair<>(row + 1, col + 1));
        } else if (row == 7 && (col > 0 && col < 7)) { //last inner row
            listAdj.add(new Pair<>(row - 1, col - 1));
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row - 1, col + 1));
            listAdj.add(new Pair<>(row, col - 1));
            listAdj.add(new Pair<>(row, col + 1));

        } else if (col == 0 && (row > 0 && row < 7)) { //first inner column
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row - 1, col + 1));
            listAdj.add(new Pair<>(row, col + 1));
            listAdj.add(new Pair<>(row + 1, col));
            listAdj.add(new Pair<>(row + 1, col + 1));

        } else if (col == 7 && (row > 0 && row < 7)) { //last inner column
            listAdj.add(new Pair<>(row - 1, col - 1));
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row, col - 1));
            listAdj.add(new Pair<>(row + 1, col - 1));
            listAdj.add(new Pair<>(row + 1, col));

        } else if (col == 0 && row == 0) { //left top edge
            listAdj.add(new Pair<>(row, col + 1));
            listAdj.add(new Pair<>(row + 1, col));
            listAdj.add(new Pair<>(row + 1, col + 1));
        } else if (row == 0 && col == 7) { //right top edge
            listAdj.add(new Pair<>(row, col - 1));
            listAdj.add(new Pair<>(row + 1, col - 1));
            listAdj.add(new Pair<>(row + 1, col));
        } else if (row == 7 && col == 0) { //left bottom edge
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row - 1, col + 1));
            listAdj.add(new Pair<>(row, col + 1));
        } else if (row == 7 && col == 7) { //right bottom edge
            listAdj.add(new Pair<>(row - 1, col - 1));
            listAdj.add(new Pair<>(row - 1, col));
            listAdj.add(new Pair<>(row, col - 1));
        } else
            System.err.println("Square indices out of bound (0-7)");
        return listAdj;
    }

    public static ArrayList<Pair<Integer, Integer>> getSteps(String board, Pair<Integer, Integer> square) {
        return new ArrayList<Pair<Integer, Integer>>();
    }

    public static ArrayList<Pair<Integer, Integer>> getJumps(String board, Pair<Integer, Integer> square) {
        return new ArrayList<Pair<Integer, Integer>>();
    }

    public static char getSquare(String board, Pair<Integer, Integer> square) {
        int row = square.getValue0();
        int col = square.getValue1();
        String[] rows = board.split("\\/");
        char sq = rows[row].charAt(col);
        return sq;
    }

    // might be redundant, or only needed private
    public static boolean boardFull(String board) {
        //TODO still need this function for checking the winner
        return false;
    }


    /**
     * Checks if the indicated player has any legal moves.
     *
     * @param board the board to check
     * @param key   the key of the player to check
     * @return false unless the player has no legal moves
     */
    public static boolean noLegalMoves(String board, char key) {
        /*String[] rows = board.split("\\/");
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                Pair<Integer, Integer> square = new Pair<>(i, j);
                if (getSquare(board, square) == '-') { //there's legal move
                    return false;
                }
            }
        }
        return true; //no legal moves*/
        return false;
    }

    /**
     * Checks if the board is full, and if so returns the key of the player with more squares filled. Otherwise returns
     * '-'
     *
     * @return the key of the player who won, or '-'
     */
    public char checkForWinner(String board) {
        char winner = '-';
        if (boardFull(board)){
            int numSquares1 = 0;
            int numSquares2 = 0;
            String[] rows = board.split("\\/");
            for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 7; j++) {
                    Pair<Integer, Integer> square = new Pair<>(i, j);
                    if (getSquare(board, square) == '1') {
                        numSquares1++;
                    } else if (getSquare(board, square) == '2') {
                        numSquares2++;
                    }
                }
            }

            if (numSquares1!=numSquares2){
                winner=(numSquares1>numSquares2)?'1':'2';
            }
             else
                System.err.println("Draw");
        }
        return winner;
    }
}


