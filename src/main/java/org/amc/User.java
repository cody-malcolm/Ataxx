package org.amc;

import org.javatuples.Pair;

public class User {
    /** The previously selected square, if any */
    private Pair<Integer, Integer> source = null;
    /** The "key" this player has been assigned by the server ('1' or '2' for players, '3' for spectators) */
    private char key;
    /** The "key" of the active player */
    private char activePlayer;
    /** The Controller associated with this user */
    private Controller controller;

    private String username = "Anonymous";

    public User(Controller controller) {
        this.controller = controller;
    }

    public void clicked(Pair<Integer, Integer> square) {
        // if it is the player's turn
            // if this.source is null
                // if "square" is occupied by a friendly piece (use Board.getSquare), set this.source to square
            // else (this.source is not null, so user has already selected a source square), handle various scenarios
                // if "square" is the same square as source, or is occupied by an opposing piece,
                    // set this.source to null (may want to use a helper function eg. "clearSelection()"/"clearSource()"
                // else if square is occupied by a friendly piece,
                    // set this.source to square
                // else, square is unoccupied
                    // if Board.validateMove(board, this.source, square, this.key)
                        // invoke controller.requestMove(this.source, square, key)
                    // whether move was valid or not, set this.source to null

        // temp code for testing Controller
        if (null == this.source) {
            this.source = square;
        } else {
            controller.requestMove(this.source, square, this.key);
            source = null;
        }
    }

    public void setKey(char key) {
        this.key = key;
    }

    public void setActivePlayer(char activePlayer) {
        this.activePlayer = activePlayer;
    }

    public boolean setUsername(String username) {
        // TODO validate the user name is not an empty string and doesn't contain a '\' before setting it, return true if valid else false
        this.username = username;
        return true;
    }

    public String getUsername() {
        return this.username;
    }

    public Pair<Integer, Integer> getSource() {
        return this.source;
    }
}
