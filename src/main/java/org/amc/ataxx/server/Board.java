package org.amc.ataxx.server;

import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

public class Board {
    // depends on encoding you choose, feel free to change, just a temp for testing controller
    final protected static String INITIAL_BOARD = "1-----2/-------/-------/-------/-------/-------/2-----1";

    private String board;
    private char activePlayer;

    public Board() {
        // needs to set board to initial state (see tests) and pick '1' or '2' at random
        board = INITIAL_BOARD;
    }

    // only used in tests
    // TODO remove once testing is complete
    public Board(String board, char c) {
        this.board = board;
        this.activePlayer = c;
    }

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

    // needs to validate the move before applying it (use GameLogic.validateMove)
    public void applyMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        if (!GameLogic.validateMove(board, source, dest, key)) {
            return;
        }
        else{
            String oldBoard=this.getBoard();
            String[] rows=oldBoard.split("\\/");
            int i=dest.getValue0();
            int j=dest.getValue1();

            //String are immutable in Java, so we need to recreate a new Board, based on the old one
            char[] charArray = rows[i].toCharArray();
            charArray[j] = key;
            rows[i]=new String(charArray); //changing the needed row

            StringBuilder b = new StringBuilder();//b will be our new Board
            for (String row : rows){
                b.append(row+"/");
            }
            b.deleteCharAt(b.length()-1); //deleting last '/'
            String newBoard=b.toString();
            this.setBoard(newBoard);
        }

        // note: after applying the move, need to verify if the opponent has any legal moves
        // if the opponent has no legal moves, all the rest of the board gets immediately filled with this player's pieces

        //TODO noLegalMoves for the lines below
        /*char opponentKey = (key == '1' ? '2' : '1');
        if (GameLogic.noLegalMoves(this.board, opponentKey)) {
            // fill all empty spaces on board w/ "key"
        }*/
    }
}
