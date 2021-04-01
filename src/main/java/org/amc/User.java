package org.amc;

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
    /** The Controller associated with this user */
    private Controller controller;
    /** The username for the User */
    private String username = "Anonymous";
    /** The current state of the board */
    private String board;

    /**
     * Constructor for User. Stores the associated Controller.
     *
     * @param controller The controller to manage communcation with the rest of the Application.
     */
    public User(Controller controller) {
        this.controller = controller;
    }

    /**
     * Handles a mouse click on the Game board
     *
     * @param square The square that was clicked on
     */
    public void clicked(Pair<Integer, Integer> square) {
        // if it is the player's turn
            // if this.source is null
                // if "square" is occupied by a friendly piece (use Board.getSquare(board, square)), set this.source to square
            // else (this.source is not null, so user has already selected a source square), handle various scenarios
                // if "square" is the same square as source, or is occupied by an opposing piece,
                    // set this.source to null (may want to use a helper function eg. "clearSelection()"/"clearSource()"
                // else if square is occupied by a friendly piece,
                    // set this.source to square
                // else, square is unoccupied
                    // if Board.validateMove(board, this.source, square, this.key)
                        // invoke controller.requestMove(this.source, square, key)
                    // whether move was valid or not, set this.source to null

        // after everything, if this.source is not null, invoke controller.refreshBoard

        // temp code for testing Controller
        if (null == this.source) {
            this.source = square;
        } else {
            controller.requestMove(this.source, square);
            source = null;
        }
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
     * Setter for username.
     *
     * @param username the username the User has provided
     * @return true if the username was valid, false if it is deemed invalid
     */
    public boolean setUsername(String username) {
        // TODO validate the user name is not an empty string and doesn't contain a '\' before setting it, return true if valid else false
        this.username = username;
        return true;
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
