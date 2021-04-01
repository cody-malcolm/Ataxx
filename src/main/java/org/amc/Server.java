package org.amc;

import java.util.Scanner;

/**
 * Handles the processing of Server admin instructions
 */
public class Server {
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
            } else if (command.regionMatches(true, 0, "games", 0, 6)) {
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
     * Prints an appropriate message notifying the admin of the directory (if listening) and port being used.
     */
    private static void printGames() {
        // TODO
        System.out.println("Games will be printed here");
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
                "start [name] - starts listening for requests (name = shared folder to use, default='shared')\r\n" +
                        "stop         - stops listening for requests\r\n" +
                        "status       - prints the status of the server\r\n" +
                        "games        - prints the games currently being played on the server\r\n" +
                        "help         - prints a list of commands\r\n" +
                        "quit         - shuts down the application");
    }
}
