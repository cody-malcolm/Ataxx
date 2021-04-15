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
    /** The username of the current opponent */
    private String opponentUsername;
    /** The ID of the Game being played */
    private String gameId;
    private String[] displayNames = new String[2];
    private boolean gameActive = false;

    /**
     * Constructor for a User
     * @param username client's username
     * @param key '1' or '2' for players, '3' for spectators
     */
    public User(String username, char key) {
        this.username = username;
        this.key = key;
    }

    /**
     * Getter for User's key
     * @return key of type char, provided by the server
     */
    public char getKey() {
        return this.key;
    }
    /**
     * Returns true if it's User's turn
     * @return boolean representing if it's player's turn
     */
    public boolean usersTurn() {
        return this.key == this.activePlayer;
    }
    
    /**
     * Handles a mouse click on the Game board and returns the Move associated with the click, if any.
     *
     * @param square The square that was clicked on
     * @return a String representing the move in "0123" format (source row/source col/dest row/dest col) or null if no move
     */
    public String clicked(Pair<Integer, Integer> square) {
        //if it's player's turn
        if (this.key==this.activePlayer && this.gameActive){

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

    /**
     * Method setting player's usernames to display
     *
     * @param playerOneUsername first player's username
     * @param playerTwoUsername second player's username
     */
    public void setDisplayNames(String playerOneUsername, String playerTwoUsername) {
        this.displayNames[0] = playerOneUsername;
        this.displayNames[1] = playerTwoUsername;
    }

    /**
     * Getter for displayNames (players' usernames)
     * @return array of Strings
     */
    public String[] getDisplayNames() {
        return this.displayNames;
    }

    /**
     * Setter for GameID.
     *
     * @param gameId of type String, randomly assigned
     */
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    /*public String getOpponentUsername() {
        return this.opponentUsername;
    }*/

    /**
     * Getter for GameID.
     *
     * @return the game's gameID
     */
    public String getGameId() {
        return this.gameId;
    }

    /**
     * Gets a username of one of the players.
     * @param key the "key" of the player, '1' or '2'
     * @return active player's username, if key is this.key; opponent's username otherwise
     */
    public String getName(char key) {
        if (key == this.key) {
            return this.username;
        } else {
            return this.opponentUsername;
        }
    }

    /**
     * Setter for this.gameActive
     * @param gameActive boolean representing if the game is active
     */
    public void setGameActive(boolean gameActive) {
        this.gameActive = gameActive;
    }

    /**
     * Getter for this.gameActive
     * @return true if the game is active, false otherwise
     */
    public boolean getGameActive() {
        return this.gameActive;
    }
}
