package org.amc.ataxx.client;

import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

/**
 * A User implements the "Model" part of the Client-side Application. Although the Server holds the authoritative Game
 * state, this stores many details in order to provide Client-side verification ensure Users with non-modified Clients
 * enjoy a smooth experience.
 */
public class User {
    /** The previously selected square, if any */
    private Pair<Integer, Integer> source = null;
    /** The "key" this player has been assigned by the server ('1' or '2' for players, '3' for spectators) */
    private char key;
    /** The "key" of the active player */
    private char activePlayer;
    /** The username for the User */
    final private String username;
    /** The current state of the board */
    private String board;

    public User(String username, char key) {
        this.username = username;
        this.key = key;
    }
    /**
     * Handles a mouse click on the Game board and returns the Move associated with the click, if any.
     *
     * @param square The square that was clicked on
     * @return a String representing the move in "0123" format (source row/source col/dest row/dest col) or null if no move
     */
    public String clicked(Pair<Integer, Integer> square) {
        //if it's player's turn
        if (this.key==this.activePlayer){

            // if this.source is null
            if (null == this.source) {
                //if the player controls this piece
                if (GameLogic.getSquare(this.board, square)==this.key){
                    this.source = square;
                }

            } else {//this.source is not null, so user has already selected a source square
                char oppKey=key=='1'?'2':'1';
                // if "square" is the same square as source, or is occupied by an opposing piece,
                // set this.source to null
                if (square.equals(this.source) ||GameLogic.getSquare(this.board, square) ==oppKey){
                    this.source=null;
                }
                //if square is occupied by a friendly piece,
                // set this.source to square
                else if (GameLogic.getSquare(this.board, square) ==key){
                    this.source=square;
                }
                //square is unoccupied
                else {
                    //if move is legal, return move
                    //return move and reset this.source to null
                    if (GameLogic.validateMove(this.board, this.source, square, this.key)){
                        StringBuilder move = new StringBuilder();
                        move.append(this.source.getValue0())
                                .append(this.source.getValue1())
                                .append(square.getValue0())
                                .append(square.getValue1());
                        this.source = null;
                        return move.toString();
                    }
                    this.source = null;
                }
            }
        }
        return null;

    }

    /**
     * Setter for key.
     *
     * @param key the "key" provided by the server
     */
    public void setKey(char key) {
        this.key = key;
    }

    /**
     * Setter for activePlayer.
     *
     * @param activePlayer the "key" of the player whose turn it is, according to the server
     */
    public void setActivePlayer(char activePlayer) {
        this.activePlayer = activePlayer;
    }

    /**
     * Getter for username.
     *
     * @return the Username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Getter for source.
     *
     * @return the square the user has current selected
     */
    public Pair<Integer, Integer> getSource() {
        return this.source;
    }

    /**
     * Setter for board.
     *
     * @param board the String representation of the board
     */
    public void setBoard(String board) {
        this.board = board;
    }

    /**
     * Getter for board.
     *
     * @return the String representation of the board
     */
    public String getBoard() {
        return this.board;
    }

}
