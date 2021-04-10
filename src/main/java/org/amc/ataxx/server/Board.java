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

    public void changeActivePlayer() {
        this.activePlayer=(getActivePlayer()=='1'? '2': '1');
    }

    // needs to validate the move before applying it (use GameLogic.validateMove)
    public void applyMove(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        if (!GameLogic.validateMove(board, source, dest, key)) {
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
            newBoard=applyMoveStep(source, dest, key);
        }
        else{
            //if the move qualify as a jump, we remove the piece from source square, and add it to the dest square
            newBoard=applyMoveJump(source, dest, key);
        }
        this.setBoard(newBoard);
        changeActivePlayer();

        // note: after applying the move, need to verify if the opponent has any legal moves
        // if the opponent has no legal moves, all the rest of the board gets immediately filled with this player's pieces

        //TODO maybe create a function findEmptySquares()
        char opponentKey = (key == '1' ? '2' : '1');
        /*if (GameLogic.noLegalMoves(this.board, opponentKey)) {
            // fill all empty spaces on board w/ "key"
        }*/
    }

    public String applyMoveStep(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
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
        return newBoard;

    }
    public String applyMoveJump(Pair<Integer, Integer> source, Pair<Integer, Integer> dest, char key) {
        //1 step) we need to add a new piece at a dest square
        //for this, we can use applyMoveStep()
        //2 step) remove a piece from source square
        String step1=applyMoveStep(source, dest, key);
        String[] rows=step1.split("\\/");
        int i=source.getValue0();
        int j=source.getValue1();

        //String are immutable in Java, so we need to recreate a new Board (step2), based on the old one (step1)
        char[] charArray = rows[i].toCharArray();
        charArray[j] = '-';
        rows[i]=new String(charArray); //changing the needed row

        StringBuilder b = new StringBuilder();//b will be our new Board
        for (String row : rows){
            b.append(row+"/");
        }
        b.deleteCharAt(b.length()-1); //deleting last '/'
        String step2=b.toString();
        return step2;

    }
}
