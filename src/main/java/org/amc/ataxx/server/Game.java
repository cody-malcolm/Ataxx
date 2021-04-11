package org.amc.ataxx.server;

import org.javatuples.Pair;

import java.util.ArrayList;

public class Game {

    private String id;
    private Player[] players = new Player[2];
    private ArrayList<Player> spectators;
    private Board board;
    private String winner = "-"; // may be unnecessary

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

    public Game(String id) {
        // set up the board and any other internal stuff you need to do
        // up to you if you track active player at the Game level or the Board level, but requests will be sent to getActivePlayer()
        this.id = id;
        this.board = new Board();
        this.spectators = new ArrayList<>();
    }

    /**
     * Adds a player to "this" game and starts the Game if it has two Players
     *
     * @param player the player to add
     * @return '1' if the player is player 1, '2' otherwise
     */
    public char addPlayer(Player player) {
        sendToAll(player.getUsername() + " has joined the game"); // this should get sent before the player is added
        if (null==players[0]){
            players[0] = player;
            return '1';
        }
        else{
            players[1]=player;
            return '2';
        }
        // add player
        // check if game has two players, and set the active player (here or in Board) to '1' or '2' at random if so
        // return the index the player got assigned this time

    }

    /**
     * Returns the active Player in the Game
     *
     * @return '1' if player 1 is active, '2' otherwise
     */
    public char getActivePlayer() {
        // up to you where you track the active player, here or in the Board class. Either way, Model should be able to
        // confirm move is being requested by the active player before applying it. Also, active Player should be '-'
        // until the Game has two Players.
        return '-';
    }

    /**
     * Getter for ID
     *
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
    public Player getPlayer(int index) { // maybe should be looked up based on player key
        return this.players[index]; // will need a guard against null player
    }

    public Player[] getPlayers() {
        return this.players;
    }

    public String getBoard() {
        // please make sure there are no '\' in how the board is recorded. Up to you where the Board gets converted to String (here or in Board class)
        // just using the example board from the tests for now
        return board.getBoard();
    }

    /**
     * Applies the given move, if it is a legal move, it is the player's turn, and the player controls the piece at the
     * source square.
     *
     * @param move The move to apply. Format is "0123", indicating row-col of source square, then row-col of dest square
     * @param key The player requesting the move. '1' or '2'
     */
    public void applyMove(String move, char key) {
        // you'll want to pass this on to board.applyMove, but it's up to you if you want to verify it at this stage (I'd just do it in board.applymove)
        // please explicitly check for '1' and '2', don't use else, in later iterations spectators might get '3' or something
        // also, activePlayer is '0' before game starts, more reason to not use else
        Pair<Integer, Integer> source = new Pair(Character.getNumericValue(move.charAt(0)), Character.getNumericValue(move.charAt(1)));
        Pair<Integer, Integer> dest = new Pair(Character.getNumericValue(move.charAt(2)), Character.getNumericValue(move.charAt(3)));
        this.board.applyMove(source, dest, key);
        // also need to call GameLogic.checkForWinner() and update "winner" with the corresponding player's username if there is one
        /*if (key== GameLogic.checkForWinner(this.getBoard())){
            winner=key;

        }*/
    }



    /**
     * Returns the username of the winner of the Game, or "-" if the Game is ongoing.
     *
     * @return the username of the winner
     */
    public String getWinner() {
        return this.winner;
    }

    /**
     * Handles a resignation by the player with the associated key.
     *
     * @param key the key of the player resigning
     */
    public void handleResignation(char key) {
        // identify the username of the player resigning, and invoke: sendToAll(username + "has resigned the game")
        // update winner
    }

    /**
     * Adds a spectator to the list of spectators.
     *
     * @param spectator the spectator to add
     * @return the key for the spectator
     */
    public char addSpectator(Player spectator) {
        sendToAll(spectator.getUsername() + " is now spectating the game");
        spectators.add(spectator);
        return '3';
    }

    private void sendToAll(String message) {
        for (Player player : this.players) {
            if (null != player) {
                player.sendSystemMessage(message);
            }
        }

        for (Player spectator : this.spectators) {
            spectator.sendSystemMessage(message);
        }
    }

    /**
     * Removes the given spectator from the ArrayList.
     *
     * @param spectator the spectator to remove
     */
    public void removeSpectator(Player spectator) {
        spectators.remove(spectator);
    }

    /**
     * Returns the list of spectators in the Game.
     *
     * @return the list of spectators
     */
    public ArrayList<Player> getSpectators() {
        return this.spectators;
    }
}
