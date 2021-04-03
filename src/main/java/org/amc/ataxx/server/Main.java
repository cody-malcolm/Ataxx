package org.amc.ataxx.server;

import java.util.Collection;
import java.util.Scanner;

/**
 * Handles the processing of Server admin instructions.
 *
 * Acknowledgment: Much of the code in this Class was reused from Assignment #2
 */
public class Main {
    /** A flag to track whether the server is listening for requests or not */
    private static boolean listening = false;
    /** A Thread to handle the listening for client requests (so the server listen for admin input) */
    private static ServerListener serverListener;

    /**
     * Main method. Prints instructions for admin and processes admin input.
     *
     * @param args None
     */
    public static void main(String[] args) {
        printCommands();
        processCommands();

        System.out.println("Closing the application.");
        System.exit(0);
    }

    /**
     * A helper method to read in admin instructions. Calls the appropriate handler function for each valid instruction.
     */
    private static void processCommands() {
        // initialize the Scanner
        Scanner scanner = new Scanner(System.in);

        // TODO add and implement command to force close all connections

        // initialize the exit flag
        boolean exit = false;

        while (!exit) {
            // wait for a command (note: requires gradle exec task to have 'standardInput = System.in')
            String command = scanner.nextLine();

            if (command.regionMatches(true, 0, "start", 0, 5)) {
                startListening();
            } else if (command.regionMatches(true, 0, "stop", 0, 4)) {
                // true - print message if instruction is redundant
                stopListening(true);
            } else if (command.regionMatches(true, 0, "status", 0, 6)) {
                printStatus();
            } else if (command.regionMatches(true, 0, "games", 0, 5)) {
                printGames();
            } else if (command.regionMatches(true, 0, "help", 0, 4)) {
                printCommands();
            } else if (command.regionMatches(true, 0, "quit", 0, 4)) {
                // false - do not print message if stop instruction is redundant
                stopListening(false);
                // update exit flag
                exit = true;
            } else {
                // display an appropriate message and reprint the commands
                System.out.println("That command was not recognized.");
                printCommands();
            }
        }

        // close the Scanner
        scanner.close();
    }


    /**
     * Prints a listing of the active games on the server, or an appropriate message if no games are being played.
     */
    private static void printGames() {
        Collection<Game> games = GameManager.getInstance().getGames().values();
        if (games.size() != 0) {
            System.out.println("The active games are:");
            for (Game game : games) {
                Player player1 = game.getPlayer(0);
                Player player2 = game.getPlayer(1);
                String username1 = "opponent";
                String username2 = "opponent";

                if (null != player1) {
                    username1 = player1.getUsername();
                }
                if (null != player2) {
                    username2 = player2.getUsername();
                }
                System.out.println(game.getID() + ": " + username1 + " vs. " + username2);
            }
        } else {
            System.out.println("There are no active games.");
        }
    }

    /**
     * Prints a message notifying the admin if the server is listening for requests or not.
     */
    private static void printStatus() {
        System.out.println("The server is" + (listening ? "" : " not") + " listening for requests.");
    }

    /**
     * Handles a stop listening instruction.
     *
     * @param notify Whether or not to print a message when redundant stop instruction is received.
     */
    private static void stopListening(boolean notify) {
        // if server is listening,
        if (listening) {
            // close the socket
            serverListener.closeSocket();

            // update the listening flag
            listening = false;

            // print an appropriate message
            System.out.println("The server has stopped listening for requests.");
        } else {
            // code style note: don't like else-if here for semantic reasons
            // server was already stopped - print status message if notify flag is true
            if (notify) {
                printStatus();
            }
        }
    }

    /**
     * Handles a start listening instruction.
     */
    private static void startListening() {
        // if the server is not listening,
        if (!listening) {
            // create a new Thread to listen for client requests
            serverListener = new ServerListener();

            // start the thread
            serverListener.start();

            // update the listening flag
            listening = true;
        } else {
            // print an appropriate message
            System.out.println("The server is already listening for requests");
        }
    }

    /**
     * Prints a list of valid instructions to the console.
     */
    private static void printCommands() {
        System.out.println("          ------------\r\n          | Commands |\r\n          ------------");
        System.out.println(
                        "start   - starts listening for requests\r\n" +
                        "stop    - stops listening for requests\r\n" +
                        "status  - prints the status of the server\r\n" +
                        "games   - prints the games currently being played on the server\r\n" +
                        "help    - prints a list of commands\r\n" +
                        "quit    - shuts down the application");
    }
}
