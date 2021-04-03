package org.amc;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.javatuples.Pair;

import java.util.ArrayList;

public class Views {
    /** The size of one square of the board, in pixels */
    final private static int SIZE = 80;
    /** The controller that manages communication with the rest of the Application */
    private static ClientListener clientListener;
    /** The GraphicsContext associated with the Canvas */
    private static GraphicsContext gc;

    // TODO multiple Color themes - extra

    /**
     * Creates the Canvas that board will be rendered on
     *
     * @param borderPane the BorderPane to attach the Canvas to
     */
    public static void createCanvas(BorderPane borderPane) {
        Canvas canvas = new Canvas();
        canvas.setHeight(SIZE*7);
        canvas.setWidth(SIZE*7);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, Views.handleMouseClick());
        gc = canvas.getGraphicsContext2D();

        borderPane.setCenter(canvas);
    }

    /**
     * Draws the board.
     */
    public static void drawBoard() {
        Color[] colors = {Color.hsb(0, 0, 0.90), Color.hsb(0, 0, 0.80)};
        for (int j = 0; j < 7; j++) {
            gc.setFill(colors[1]);
            gc.setFill(colors[j%2]);
            for (int i = 0; i < 7; i++) {
                gc.fillRect(SIZE*i, SIZE*j, SIZE, SIZE);
                gc.setFill(colors[(i+j+1)%2]);
            }
        }
    }

    /**
     * Renders the pieces on the board.
     *
     * @param board the String representation of the board indicating the location of the pieces
     */
    private static void renderPieces(String board) {

    }

    /**
     * Renders the provided board.
     *
     * @param board the String representation of the board to render
     */
    public static void renderBoard(String board) {
        Views.drawBoard();
        Views.renderPieces(board);

        // take the given board and immediately render it, overwriting anything there already
    }

    /**
     * Handles a MouseClick on the Canvas. Provides the controller with row/col index of the selected square.
     *
     * @return the EventHandler to attach to the Canvas
     */
    public static EventHandler<MouseEvent> handleMouseClick() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Integer row = Integer.valueOf((int)Math.floor(event.getY()/SIZE));
                Integer col = Integer.valueOf((int)Math.floor(event.getX()/SIZE));

                Pair<Integer, Integer> square = new Pair<>(row, col);
                clientListener.processMouseClick(square);
            }
        };
    }

    /**
     * Setter for controller. // TODO rename/reword this
     *
     * @param clientListener the Controller to use to communicate with the rest of the Application.
     */
    public static void setClientListener(ClientListener clientListener) {
        Views.clientListener = clientListener;
    }

    /**
     * Given a list of "steps" and "jumps", highlights the squares identified in each list according to if they are a
     * step or a jump
     *
     * @param steps squares the selected piece can legally move to without jumping
     * @param jumps squares the selected piece can legally move to by jumping
     */
    public static void highlightDestinationSquares(ArrayList<Pair<Integer, Integer>> steps, ArrayList<Pair<Integer, Integer>> jumps) {
        // render a small circle (probably SIZE/4) on each step and jump, use somewhat darker colour for jumps
    }

    /**
     * Animates the transition from the "old" board to the "new" board.
     *
     * @param oldBoard the previous state of the board
     * @param newBoard the new state of the board
     * @param move the move being performed to transition from old to new
     */
    public static void animateMove(String oldBoard, String newBoard, String move) {
        // ensure the currently rendered board is as expected
        Views.renderBoard(oldBoard);

        // Two consecutive animations:
            // first, animate the piece stepping or jumping

                // my approach would be:
                // compare the square at index move.charAt(0), move.charAt(1) in oldBoard and newBoard, if same do nothing, if different, shrink the piece to nothing
                // at same time, expand a circle from nothing at square indicated by move.charAt(2), move.charAt(3)

            // second, animate the converted pieces changing color

                // my approach would be:
                // compare each square in old board and each in new and convert them if the color is different
                // if you use Color.hsb, you can transition the color from one to the other over an interval of time
                        // eg. could have '1' to '2' transition like (but obviously programmatically)
        //                                     Color.hsb(0, 0.5, 0.5)
        //                                     Color.hsb(20, 0.5, 0.5)
        //                                     Color.hsb(40, 0.5, 0.5)
        //                                     Color.hsb(60, 0.5, 0.5)
        //                                     Color.hsb(80, 0.5, 0.5)
        //                                     Color.hsb(100, 0.5, 0.5)
        //                                     Color.hsb(120, 0.5, 0.5)
        //                                     Color.hsb(140, 0.5, 0.5)
        //                                     Color.hsb(160, 0.5, 0.5)
        //                                     Color.hsb(180, 0.5, 0.5)

        // Note: The animation should be quite fast, probably between 200-400ms each but we can adjust it easily if needed.
        // TODO later addition: execute the animation in a separate thread to not block other parts of UI such as chat window
    }

    /**
     * Displays that the game is over and the username of the winner
     *
     * @param winner the Username of the winning player
     */
    public static void displayWinner(String winner) {
    }

    /**
     * Displays the provided message in the chat box. Applies styling based on the given char.
     *
     * @param message the message to display
     * @param style 'i' for italics, 'b' for bold, 'd' for plaintext
     */
    public static void displayMessage(String message, char style) {
        System.out.println(message);
    }
}
