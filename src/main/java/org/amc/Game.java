package org.amc;

import java.util.HashMap;

public class Game {

    private static HashMap<String, Game> games = new HashMap<>(); // Feel free to change the data type if desired
    private String id = null;
    private Player[] players = new Player[2];
    // will want some sort of a list of spectators unless we really run out of time - with a method to add arbitrary amount, and a getter for the whole list

    // '1' -> player 1 piece
    // '2' -> player 2 piece
    // '-' -> empty space

//    public static void main(String[] args) {
        // some demo for parsing
//        String[] temp = board.split("\\,");
//
//        for (String s : temp) {
//            System.out.println(s);
//        }
//
//        String[] b = temp[0].split("\\/");
//
//        for (String row : b) {
//            System.out.println(row);
//        }
//        System.out.println(b[3].charAt(3));
//
//        // is a space available
//        if ('-' == b[rowIndex].charAt(colIndex)) {
//
//        }
//    }

    public Game() {
        String id = ""; // generate a random id

        // verify id is unique - eg. use Game.games.get() and ensure it returns whatever the "not found" response is

        // set up the board and any other internal stuff you need to do
        // up to you if you track active player at the Game level or the Board level, but requests will be sent to getActivePlayer()

        this.id = id;
        Game.games.put(id, this);
    }

    /**
     * Check the map of Games for a game with only 1 player. If one is found, returns it. Otherwise, returns a new
     * Game with no Players.
     *
     * @return A game for a player to be added to
     */
    /* Note: You could handle this by creating another static field (say openGame), which stores the currently waiting
     game if any and null otherwise. Then you don't need to search the map each time, you just store that game in a temp,
     change the static field to null, and return the temp. Complexity vs performance. */
    public static synchronized Game getAvailableGame() {

        return new Game();
    }

    /**
     * Returns the Game associated with the given ID
     * @param gameID the ID of the Game
     * @return the game associated with the ID
     */
    public static Game getGame(String gameID) {
        return Game.games.get(gameID);
    }

    /**
     * Adds a player to "this" game and starts the Game if it has two Players
     *
     * @param player the player to add
     * @return '1' if the player is player 1, '2' otherwise
     */
    public char addPlayer(Player player) {
        // add player
        // check if game has two players, and set the active player (here or in Board) to '1' or '2' at random if so
        // return the index the player got assigned this time
        return '0';
    }

    /**
     * Returns the active Player in the Game
     *
     * @return '1' if player 1 is active, '2' otherwise
     */
    public char getActivePlayer() {
        // up to you where you track the active player, here or in the Board class. Either way, Model should be able to
        // confirm move is being requested by the active player before applying it. Also, active Player should be '0'
        // until the Game has two Players.
        return '0';
    }

    /**
     * Getter for ID
     * @return the ID of the Game
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the Player at the specified index in this.players
     *
     * @param index The index of the Player
     * @return the Player at the specified index
     */
    public Player getPlayer(int index) {
        return this.players[index];
    }

    public String getBoard() {
        // please make sure there are no '\' in how the board is recorded. Up to you where the Board gets converted to String (here or in Board class)
        return "";
    }

    /**
     * Handles the case where the player indicated by the key resigns
     *
     * @param key '1' if player 1 resigns, '2' if player 2 resigns
     */
    public void handleResign(char key) {
        // recommend to do this in a way where the board gets filled, eg. fill all empty squares with the opponent color

    }

    /**
     * Applies the given move, if it is a legal move, it is the player's turn, and the player controls the piece at the
     * source square.
     *
     * @param move The move to apply. Format is "0123", indicating row-col of source square, then row-col of dest square
     * @param key The player requesting the move. '1' or '2'
     */
    public void applyMove(String move, char key) {
        // you'll want to pass this on to board.applyMove, but it's up to you if you want to verify it at this stage
        // please explicitly check for '1' and '2', don't use else, in later iterations spectators might get '3' or something
        // also, activePlayer is '0' before game starts, more reason to not use else
    }

    public static HashMap<String, Game> getGames() {
        return Game.games;
    }
}
