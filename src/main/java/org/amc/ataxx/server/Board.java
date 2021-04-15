package org.amc.ataxx.server;

import org.amc.Utils;
import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

import java.util.ArrayList;

public class Board {
    //board is represented by 7 rows separated by '/' from top to bottom, 0-based indexing
    //so it consists of rows 0-6 and columns 0-6
    //'1' means player 1's pieces, '2' means player 2's pieces, '-' means empty square
    final protected static String INITIAL_BOARD = "2-----1/-------/-------/-------/-------/-------/1-----2";

    private String board;
    private char activePlayer;

    public Board() {
        // needs to set board to initial state (see tests) and pick '1' or '2' at random
        board = INITIAL_BOARD;
        this.activePlayer = Character.forDigit(Utils.randInt(1, 2), 10);
    }

    /*
    // only used in tests
    public Board(String board, char c) {
        this.board = board;
        this.activePlayer = c;
    }*/

    /**
     * Getter for board.
     *
     * @return the String representation of the Board
     */
    public String getBoard() {
        return this.board;
    }

    /**
     * Setter for board.
     */
    private void setBoard(String newBoard){
        this.board=newBoard;
    }

    /**
     * Getter for activePlayer.
     *
     * @return a character representing the key of the activePlayer ('1' or '2' when game is active)
     */
    public char getActivePlayer() {
        return this.activePlayer;
    }

    public void changeActivePlayer() {
        this.activePlayer=(getActivePlayer()=='1'? '2': '1');
    }

    /**
     * This method applies the current move by changing this.board to the new state
     * @param source origin square
     * @param dest destination square
     * @param key character ('1' or '2') representing an active player making the move
     */
    public void applyMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        //first, need to check if the move is valid
        if (!GameLogic.validateMove(board, source, dest, key)) {
            return;
        }
        if (key != activePlayer) {//if it's not player's turn, return
            return;
        }
        int sourceRow = source.getValue0();
        int sourceCol = source.getValue1();
        int destRow = dest.getValue0();
        int destCol = dest.getValue1();
        String newBoard;
        //sqrt((destRow-sourceRow)**2+(destCol-sourceCol)**2)=sqrt(?)
        //sqrt(1**2+1**2)=sqrt(2) valid(step)
        //sqrt(0**2+1**2)=sqrt(1) valid(step)
        //if the move qualify as a step, we add a new piece at a dest square, keeping the piece at source square
        if (Math.pow(destRow - sourceRow, 2) + Math.pow(destCol - sourceCol, 2) <= 2.0){
            newBoard=fillSquare(this.getBoard(), dest, key);

        }
        else{
            //if the move qualify as a jump, we remove the piece from source square, and add it to the dest square
            newBoard=applyMoveJump(source, dest, key);
        }
        this.setBoard(newBoard);
        changeActivePlayer();

        // note: after applying the move, need to verify if the opponent has any legal moves
        // if the opponent has no legal moves, all the rest of the board gets immediately filled with this player's pieces

        char opponentKey = (key == '1' ? '2' : '1');
        if (GameLogic.noLegalMoves(this.board, opponentKey)) {
            // fill all empty spaces on board w/ "key"
            ArrayList<Pair<Integer, Integer>> emptySquaresList=GameLogic.getEmptySquares(board);
            for (Pair<Integer, Integer> emptySq : emptySquaresList){
                String filledCurrentEmptySquare=fillSquare(this.getBoard(), emptySq, key);
                this.setBoard(filledCurrentEmptySquare);
            }
        }
    }

    /**
     * This method fills the square with key player's piece
     * @param board current board
     * @param square -a square to fill
     * @param key character ('1' or '2') representing an active player making the move
     * @return a String representing a new state of the board
     */
    public String fillSquare(String board, Pair<Integer, Integer> square, char key){
        ArrayList<Pair<Integer, Integer>> adjacent = GameLogic.getAdjacent(square);

        char oppoKey = '3'; // so that '-' doesn't convert anything
        if (key == '1') {
            oppoKey = '2';
        } else if (key == '2') {
            oppoKey = '1';
        }

        StringBuilder newBoard = new StringBuilder();
        for (int r = 0; r < 7; r++) {
            if (r != 0) {
                newBoard.append('/');
            }
            for (int c = 0; c < 7; c++) {
                Pair<Integer, Integer> temp = new Pair(r, c);
                if (square.equals(temp)) {
                    newBoard.append(key);
                } else if (adjacent.contains(temp)) {
                    //if adjacent piece is an opponent's piece, we replace it with our piece
                    if (oppoKey == GameLogic.getSquare(board, temp)) {
                        newBoard.append(key);
                    } else {
                        //if adjacent piece is a friendly/empty , it stays the same
                        newBoard.append(GameLogic.getSquare(board, temp));
                    }
                } else {
                    //if not adjacent or the square, it stays the same
                    newBoard.append(GameLogic.getSquare(board, temp));
                }
            }
        }

        return newBoard.toString();
    }

    /**
     * This method fills the destination square with key player's piece and removes the pieces from the source square
     * @param source - origin square
     * @param dest - destination square
     * @param key character ('1' or '2') representing an active player doing the jump
     * @return a String representing a new state of the board
     */
    public String applyMoveJump(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        //1 step) we need to add a new piece at a dest square
        //for this, we can use applyMoveStep()
        //2 step) remove a piece from source square
        String step1=fillSquare(this.getBoard(), dest, key);
        String step2=fillSquare(step1, source, '-' );

        return step2;

    }

    /**
     * This method fills all the empty squares with a key player's pieces
     *
     * @param key character ('1' or '2') representing an active player
     */
    public void fillAllSquares(char key) {
        ArrayList<Pair<Integer, Integer>> emptySquares = GameLogic.getEmptySquares(this.board);
        StringBuilder newBoard = new StringBuilder();
        for (int r = 0; r < 7; r++) {
            if (r != 0) {
                newBoard.append('/');
            }
            for (int c = 0; c < 7; c++) {
                Pair<Integer, Integer> temp = new Pair(r, c);
                if (emptySquares.contains(temp)) {
                    newBoard.append(key);
                } else {
                    newBoard.append(GameLogic.getSquare(this.board, temp));
                }
            }
        }
        this.board = newBoard.toString();
    }
}
