package org.amc.ataxx.server;

import org.amc.ataxx.GameLogic;
import org.javatuples.Pair;

import java.util.ArrayList;

public class Game {

    private boolean active = false;
    private String id;
    private Player[] players = new Player[2];
    private ArrayList<Player> spectators;
    private Board board;
    private char winner = '-'; // may be unnecessary
    private boolean finished = false;
    private boolean replayRequest;

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
        if (null==players[0]){
            players[0] = player;
            return '1';
        }
        else{
            players[1]=player;
            sendGameInfo();
            return '2';
        }

    }

    /**
     * Returns the active Player in the Game
     *
     * @return '1' if player 1 is active, '2' otherwise
     */
    public char getActivePlayer() {
        //Model should be able to
        // confirm move is being requested by the active player before applying it. Also, active
        // Player should be '-' until the Game has two Players.
        int numPlayers=this.players.length;
        if (numPlayers!=2){
            return '-';
        }
        return this.board.getActivePlayer();

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

    /**
     * Getter for players
     *
     * @return array of PLayers
     */
    public Player[] getPlayers() {
        return this.players;
    }

    /**
     * Getter for board
     *
     * @return the board of the Game
     */
    public String getBoard() { return board.getBoard(); }

    /**
     * Applies the given move, if it is a legal move, it is the player's turn, and the player controls the piece at the
     * source square.
     *
     * @param move The move to apply. Format is "0123", indicating row-col of source square, then row-col of dest square
     * @param key The player requesting the move. '1' or '2'
     */
    public void applyMove(String move, char key) {
        Pair<Integer, Integer> source = new Pair(Character.getNumericValue(move.charAt(0)), Character.getNumericValue(move.charAt(1)));
        Pair<Integer, Integer> dest = new Pair(Character.getNumericValue(move.charAt(2)), Character.getNumericValue(move.charAt(3)));
        this.board.applyMove(source, dest, key);
        this.winner = GameLogic.checkForWinner(this.board.getBoard());
        if (this.winner != '-') {
            handleGameOver();
        }
    }

    /**
     * Sets the current game finished and allows for its players & spectators to start/observe a new game
     */
    private void handleGameOver() {
        this.finished = false;
        for (Player player : this.players) {
            if (null != player) {
                player.finishedGame();
            }
        }

        for (Player spectator : this.spectators) {
            spectator.finishedGame();
        }
        GameManager.getInstance().removeGame(this.id);
    }


    /**
     * Returns the key of the winner of the Game, or '-' if the Game is ongoing.
     *
     * @return the key of the winner
     */
    public char getWinner() {
        return this.winner;
    }

    /**
     * Handles a resignation by the player with the associated key.
     * @param key the key of the player resigning
     */
    public void handleResignation(char key) {
        // identify the username of the player resigning, and invoke: sendToAll(username + "has resigned the game")
        // update winner
        if (key == '1' || key == '2') {
            int index = Character.getNumericValue(key)-1;
            Player resigningPlayer=getPlayer(index);
            sendToAll(resigningPlayer.getUsername() + " has resigned the game");
            char winnerKey = key == '1' ? '2' : '1';
            board.fillAllSquares(winnerKey);
            this.winner = winnerKey;
            handleGameOver();
        }
    }

    /**
     * Adds a spectator to the list of spectators.
     * @param spectator the spectator to add
     * @return the key for the spectator
     */
    public char addSpectator(Player spectator) {
        if (this.active) {
            sendToAll(spectator.getUsername() + " is now spectating the game");
            spectators.add(spectator);
            sendGameInfo();
        } else {
            spectators.add(spectator);
        }
        return '3';
    }

    /**
     * Sends the message to the clients, associated with "this"
     * @param message
     */
    public void sendToAll(String message) {
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
     * Sends player's names and gameID to the clients associated with "this"
     *
     */
    private void sendGameInfo() {
        if (null != this.players[0] && null != this.players[1]) {
            String playerOneUsername = this.players[0].getUsername();
            String playerTwoUsername = this.players[1].getUsername();
            this.active = true;
            for (Player player : this.players) {
                player.sendGameInformation(playerOneUsername, playerTwoUsername, this.id);
            }

            for (Player spectator : this.spectators) {
                spectator.sendGameInformation(playerOneUsername, playerTwoUsername, this.id);
            }
        }
    }


    /**
     * Removes the given spectator from the ArrayList.
     * @param spectator the spectator to remove
     */
    public void removeSpectator(Player spectator) {
        spectators.remove(spectator);
        sendToAll(spectator.getUsername() + " is no longer spectating the game");
    }

    /**
     * Returns the list of spectators in the Game.
     * @return the list of spectators
     */
    public ArrayList<Player> getSpectators() {
        return this.spectators;
    }

    /**
    * Returns true if this game is still active, false otherwise
     *
     * @return true if the game is active, false otherwise
    */
    public boolean getActive() { return this.active; }

    /**
     * Returns true if the game is finished, false otherwise
     *
     * @return true if the game is finished, false otherwise
     */
    public boolean getFinished() {
        return this.finished;
    }

    public boolean getReplayRequest() {
        return this.replayRequest;
    }

    public void setReplayRequest(boolean request) {
        this.replayRequest = request;
    }

    public void restart() {
        this.finished = false;
        this.replayRequest = false;
        this.winner = '-';
        this.board = new Board();
        sendGameInfo();
    }
}
