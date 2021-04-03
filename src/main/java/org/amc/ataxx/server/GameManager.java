package org.amc.ataxx.server;

import org.amc.Utils;

import java.util.HashMap;

/**
 * The singleton GameManager manages access to the collection of Games being played on the server. All Games are to be
 * created by and looked up via the GameManager.
 */
public class GameManager {
    private static GameManager gameManager = null;

    private HashMap<String, Game> games; // Feel free to change the data type if you want to research the best type of Map

    private GameManager() {
        games = new HashMap<>();
    }

    /**
     * Returns a unique ID for a new Game.
     *
     * @return a unique gameID
     */
    public synchronized String generateID() {
        return "0000";
    }

    /**
     * Returns the GameManager. If none exists, creates one.
     *
     * @return the singleton GameManager
     */
    public synchronized static GameManager getInstance() {
        if (null == gameManager) {
            gameManager = new GameManager();
        }
        return gameManager;
    }

    /**
     * Check the map of Games for a game with only 1 player. If one is found, returns it. Otherwise, returns a new
     * Game with no Players.
     *
     * @return A game for a player to be added to
     */
    /* Note: You could handle this by creating another field (say openGame), which stores the currently waiting
     game if any and null otherwise. Then you don't need to search the map each time, you just store that game in a temp,
     change the field to null, and return the temp. Complexity vs performance. */
    public synchronized Game getAvailableGame() {
        int gameID = Utils.randInt(1000, 9999);
        Game game = new Game(Integer.toString(gameID));
        this.games.put(game.getID(), game);
        // in addition to the above, also need to generate a random ID and ensure it's unique to pass to the constructor for Game
        return game;
    }

    /**
     * Returns the Game associated with the given ID.
     *
     * @param gameID the ID of the Game
     * @return the game associated with the ID
     */
    public Game getGame(String gameID) {
        return games.get(gameID);
    }

    /**
     * Returns the list of Games.
     *
     * @return the list of Games
     */
    public HashMap<String, Game> getGames() {
        return games;
    }
}
