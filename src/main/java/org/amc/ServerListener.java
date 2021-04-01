package org.amc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Listens for and manages Client connections. When a request comes in, it creates
 * a Thread to manage the request. Also handles closure of the associated socket.
 */
public class ServerListener extends Thread{
    /** The port the server will listen to */
    final private static int PORT = 25436;
    /** The Socket used to facilitate the connection */
    private ServerSocket serverSocket = null;
    /** A flag to indicate if a stop has been requested */
    private boolean stopped;

    /**
     * Constructor for ListenerThread.
     */
    public ServerListener() {
        super("Listener");
    }

    /**
     * This is executed when the FileSharerServer starts the Thread. After initializing some variables and printing
     * status messages, it will listen to and process incoming client requests until the FileSharerServer invokes the
     * closeSocket() method which closes the Socket - this interrupts the Socket's accept() method, the SocketException
     * is caught, and the loop ends (via closeSocket() updating the stopped flag).
     */
    @Override
    public void run() {
        try {
            // initialize the flag and Socket
            stopped = false;
            serverSocket = new ServerSocket(PORT);

            // print a message indicating that the server is listening for requests
            System.out.println("The server is listening to port: " + PORT);

            while (!stopped) {
                try {
                    // wait for a connection
                    Socket clientSocket = serverSocket.accept();

                    // create a Thread for the new connection
                    Player player = new Player(clientSocket);

                    // start the thread
                    player.start();
                } catch(SocketException e) {
                    // Happens when thread is interrupted. Do nothing.
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the Socket (and updates the stopped flag accordingly)
     */
    public void closeSocket() {
        // guard against incorrect usage (ex. being invoked before the Thread has been started and Socket initialized)
        if (null != serverSocket) {
            // update stopped flag
            stopped = true;

            // close the Socket
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
