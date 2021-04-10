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
        //sqrt(1**2+1**2)=sqrt(2) valid(step)
        //sqrt(0**2+1**2)=sqrt(1) valid(step)
        //sqrt(2**2+1**2)=sqrt(5) valid(jump)
        //sqrt(0**2+2**2)=sqrt(4) valid(jump)
        //sqrt(0**2+3**2)=sqrt(4) invalid(too far)
        if (Math.pow(destRow - sourceRow, 2) + Math.pow(destCol - sourceCol, 2) <= 8.0) {
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
    /**
     *Returns all square positions that are adjacent to the current square.
     * Examples: (0,0)->(0,1) or (0,0)->(1,1) or (0,0)->(1,0) and so on
     * @param square a pair of numbers indicating row&col index on a board (0-6,0-6)
     * @return list of pairs(squares) that can be reached within at most 1 step in each direction
     */
    public static ArrayList<Pair<Integer, Integer>> getAdjacent(Pair<Integer, Integer> square) {
        ArrayList<Pair<Integer, Integer>> listAdj = new ArrayList<>();
        int row = square.getValue0();
        int col = square.getValue1();
        for (int r = row-1; r <= row+1; r++) {
            for (int c = col-1; c <= col+1; c++) {
                if (r >=0 && r <= 6 && c >= 0 && c <=6) {
                    listAdj.add(new Pair<>(r, c));
                }
            }
        }

        return listAdj;
    }
    /**
     *Returns occupied square positions that are adjacent to the current square.S
     * Examples: (0,0)->(0,1) or (0,0)->(1,1) or (0,0)->(1,0) and so on
     * @param board the board to check
     * @param square a pair of numbers indicating row&col index on a board (0-6,0-6)
     * @return list of pairs(occupied squares) that can be reached within at most 1 step in each direction
     */
    public static ArrayList<Pair<Integer, Integer>> getSteps(String board, Pair<Integer, Integer> square) {
        //getAdjacent returns ALL adjacent squares, but we only need occupied
        ArrayList<Pair<Integer, Integer>> adjacent=getAdjacent(square);
        ArrayList<Pair<Integer, Integer>> occupiedAdjacent=new ArrayList<>();
        //we go through all adjacent squares and take those which are occupied
        for(Pair<Integer, Integer> adjPair:adjacent){
            if (!isSquareEmpty(board, adjPair)){
                occupiedAdjacent.add(adjPair);
            }
        }
        return occupiedAdjacent;
    }
    /**
     *Returns occupied square positions that are within a jump from the current square.
     * Examples: (0,0)->(0,2) or (0,0)->(1,2) or (0,0)->(2,2)
     * @param board the board to check
     * @param square a pair of numbers indicating row&col index on a board (0-6,0-6)
     * @return list of pairs(occupied squares) that can be reached within a jump (at most 2 steps in each direction)
     */
    public static ArrayList<Pair<Integer, Integer>> getJumps(String board, Pair<Integer, Integer> square) {
        ArrayList<Pair<Integer, Integer>> occupiedJumpAdjacent=new ArrayList<>();
        int row=square.getValue0();
        int col=square.getValue1();
        for (int r = row-2; r <= row+2; r++) {
            for (int c = col-2; c <= col+2; c++) {
                if (r >=0 && r <= 6 && c >= 0 && c <=6) {
                    Pair<Integer, Integer> jumpSquare=new Pair<>(r,c);
                    if (!isSquareEmpty(board, jumpSquare)){
                        occupiedJumpAdjacent.add(jumpSquare);
                    }

                }
            }
        }
        return occupiedJumpAdjacent;
    }
    /**
     * Checks which player('1' or '2') occupies a given square (if any); if the square is empty, returns '-'
     * @param board the board to check
     * @param square a pair of numbers indicating row&col index on a board (0-6,0-6)
     * @return char representation of a square
     */
    public static char getSquare(String board, Pair<Integer, Integer> square) {
        int row = square.getValue0();
        int col = square.getValue1();
        String[] rows = board.split("\\/");
        char sq = rows[row].charAt(col);
        return sq;
    }

    /**
     * Checks if all board squares are occupied
     * @param board the board to check
     * @return false unless the board is full
     */
    public static boolean boardFull(String board) {
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
                Pair<Integer, Integer> square = new Pair<>(i, j);
                if (isSquareEmpty(board, square)) { //there's empty square, board is not full
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks if the indicated player has any legal moves.
     *
     * @param board the board to check
     * @param key   the key of the player to check
     * @return false unless the player has no legal moves
     */
    public static boolean noLegalMoves(String board, char key) {
        String[] rows = board.split("\\/");
        for (int i = 0; i <= 6; i++) {
            for (int j = 0; j <= 6; j++) {
                Pair<Integer, Integer> square = new Pair<>(i, j);
                if (isSquareEmpty(board, square)) { //there's empty square
                    //need to check now if this empty square is in reach of 'key' player

                    //first, checking adjacent occupied squares (within 1 square)
                    ArrayList<Pair<Integer, Integer>> adjList=getSteps(board, square);
                    for (Pair<Integer, Integer> adjSquare:adjList){
                        if (getSquare(board, adjSquare)==key){
                            return false;
                        }
                    }
                    //second,checking if a key player can jump to the empty square
                    ArrayList<Pair<Integer, Integer>> jumpList=getJumps(board, square);
                    for (Pair<Integer, Integer> jumpSquare:adjList){
                        if (getSquare(board, jumpSquare)==key){
                            return false;
                        }
                    }


                }
            }
        }
        return true; //no legal moves

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
                System.out.println("Draw");
        }
        return winner;
    }
}


