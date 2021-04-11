package org.amc.ataxx.server;

import org.amc.Utils;

import java.util.HashMap;
import java.util.Set;

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
    private Game waitingGame=null;

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
    public synchronized Game getAvailableGame() {
        // if game available with 1 player, return that, otherwise make new game, add it to internal list of games, and return it

        Set<String> gameIDlist=games.keySet();
        boolean foundUniqueGameID=false;
        int gameID=0;
        while (!foundUniqueGameID) {
            gameID = Utils.randInt(1000, 9999); // temp
            if (!gameIDlist.contains(gameID)) { //gameID is unique
                foundUniqueGameID=true;

            }
        }
        if (null==waitingGame){
            //int gameID = Utils.randInt(1000, 9999); // temp
            Game game = new Game(Integer.toString(gameID)); // temp
            this.games.put(game.getID(), game);
            this.waitingGame=game;
            return game;
        }
        else{
            Game temp=this.waitingGame;
            // TODO fix bug with player leaving (needs to be don in controller first)
            this.waitingGame=null;
            return temp;

        }

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
