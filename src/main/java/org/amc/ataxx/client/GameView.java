package org.amc.ataxx.client;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.javatuples.Pair;

import java.util.ArrayList;

public class GameView {
    /** static variable instance of the class */
    private static GameView instance = null;

    /** The size of one square of the board, in pixels */
    final private static int SIZE = 80;
    /** The true size of a drawn square of the board, in pixels */
    final private static int actualSIZE = SIZE-2;
    /** The size of the canvas, in pixels */
    final private static int canvasSIZE = SIZE*7 + 10;
    /** The controller that manages communication with the rest of the Application */
    private static ClientListener clientListener;
    /** The GraphicsContext associated with the Canvas */
    private static GraphicsContext gc;

    /**
     * Constructor
     */
    private GameView(){}

    /**
     * Creates (if necessary) and returns an instance of the class
     */
    public synchronized static GameView getInstance(){
        if (instance == null){
            instance = new GameView();
        }

        return instance;
    }

    /**
     * Creates the Canvas that board will be rendered on
     *
     * @param borderPane the BorderPane to attach the Canvas to
     */
    public void createCanvas(BorderPane borderPane) {
        Canvas canvas = new Canvas();
        canvas.setHeight(canvasSIZE);
        canvas.setWidth(canvasSIZE);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, instance.handleMouseClick());
        gc = canvas.getGraphicsContext2D();

        borderPane.setCenter(canvas);
    }

    /**
     * Draws the board.
     */
    private void drawBoard() {
        Color[] colors = {Color.hsb(0, 0, 0.85), Color.hsb(0, 0, 0.50)};

        gc.setFill(colors[1]);
        gc.fillRoundRect(0.0, 0.0, canvasSIZE-2, canvasSIZE-2, SIZE/4,SIZE/4);

        gc.setFill(colors[0]);
        for (int j = 0; j < 7; j++) {
            for (int i = 0; i < 7; i++) {
                gc.fillRoundRect(SIZE*i+5, SIZE*j+5, actualSIZE, actualSIZE, SIZE/4,SIZE/4);
            }
        }
    }

    /**
     * Renders the pieces on the board.
     *
     * @param board the String representation of the board indicating the location of the pieces
     */
    private void renderPieces(String board) {
        Color[] colors = {Color.hsb(0,1,0.79), Color.hsb(215, 1, 0.79)};
        String[] rows = board.split("/");

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 7; j++) {

                char piece = rows[i].charAt(j);

                if(piece == '1'){
                    renderPiece(colors[0],i,j);
                } else if (piece == '2'){
                    renderPiece(colors[1],i,j);
                }
            }
        }
    }

    /**
     * Renders a piece on the board.
     *
     * @param color the color of the needed piece
     * @param row the row where the piece goes
     * @param col the col where the piece goes
     */
    private void renderPiece(Color color,int row, int col) {
        gc.setFill(color);
        gc.fillOval(getPosition(col), getPosition(row), actualSIZE-(actualSIZE/10),actualSIZE-(actualSIZE/10));
    }


    /**
     * Returns the x or y position for a piece to be drawn.
     *
     * @param x the column or row of the square the piece will be in
     */
    private double getPosition(int x){
        return 5 + (actualSIZE/10)/2 + (SIZE) * x;
    }

    /**
     * Renders all updates to the board.
     *
     * @param oldBoard the String representation of the old board to render
     * @param newBoard the String representation of the new board to render
     * @param move the String representation of the move made to reach the new board state
     */
    public void updateBoard(String oldBoard, String newBoard, String move) {
        animateMove(oldBoard, newBoard, move);
        renderBoard(newBoard);
    }

    /**
     * Renders the provided board.
     *
     * @param board the String representation of the board to render
     */
    public void renderBoard(String board) {
        drawBoard();
        renderPieces(board);

        // take the given board and immediately render it, overwriting anything there already
    }

//    /**
//     * Renders the provided board.
//     *
//     * @param board the String representation of the board to render
//     */
//    public void renderBoard(String board, char activePlayer, char key, Label playerLabel) {
//        drawBoard();
//        renderPieces(board);
//        displayTurn(activePlayer, key, playerLabel);
//    }

    public void displayTurn(char activePlayer, char key, Label playerLabel) {
        Color[] activeColors = {Color.hsb(0,1,0.79), Color.hsb(215, 1, 0.79)};
        Color[] inactiveColors = {Color.hsb(0,1,0.40), Color.hsb(215, 1, 0.40)};
        if (activePlayer == key){
            // For testing
//            System.out.println("Testing turn render: Inside activePlayer(" + activePlayer + ") == key(" + key + ")");
            playerLabel.setTextFill(activeColors[Integer.valueOf(key)-49]);
        } else {
            // For testing
//            System.out.println("Testing turn render: Inside activePlayer(" + activePlayer + ") != key(" + key + ")");
            playerLabel.setTextFill(inactiveColors[Integer.valueOf(key)-49]);
        }
        playerLabel.setText("Player " + key);
    }

    /**
     * Handles a MouseClick on the Canvas. Provides the controller with row/col index of the selected square.
     *
     * @return the EventHandler to attach to the Canvas
     */
    private EventHandler<MouseEvent> handleMouseClick() {
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
    public void setClientListener(ClientListener clientListener) {
        this.clientListener = clientListener;
    }

    /**
     * Given a list of "steps" and "jumps", highlights the squares identified in each list according to if they are a
     * step or a jump
     *
     * @param steps squares the selected piece can legally move to without jumping
     * @param jumps squares the selected piece can legally move to by jumping
     */
    public void highlightDestinationSquares(ArrayList<Pair<Integer, Integer>> steps, ArrayList<Pair<Integer, Integer>> jumps) {
        // render a small circle (probably SIZE/4) on each step and jump, use somewhat darker colour for jumps
        highlightSquares(steps);
        highlightSquares(jumps);
    }

    /**
     * Highlights the squares in the given list.
     *
     * @param squares the list of pairs of squares to highlight
     */
    private void highlightSquares(ArrayList<Pair<Integer, Integer>> squares) {
        Color color = Color.hsb(0, 0, 0.95);
        int num = squares.size();
        for (int i=0; i < num; i++){
            int row = squares.get(i).getValue0();
            int column = squares.get(i).getValue1();
            renderPiece(color, row, column);
        }

    }

    /**
     * Animates the transition from the "old" board to the "new" board.
     *
     * @param oldBoard the previous state of the board
     * @param newBoard the new state of the board
     * @param move the move being performed to transition from old to new
     */
    public void animateMove(String oldBoard, String newBoard, String move) {
        String sourceSquare = String.valueOf(move.charAt(0)) + String.valueOf(move.charAt(1));
        String destinationSquare = String.valueOf(move.charAt(2)) + String.valueOf(move.charAt(3));

//        ArrayList<Pair<Integer, Integer>> adjacentSquares = getAdjacentSquares(destinationSquare);

        // ensure the currently rendered board is as expected
        renderBoard(oldBoard);

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
        renderBoard(newBoard);
    }

//    /**
//     * Returns a list of Pairs of Row,Column for all squares adjacent to the square provided.
//     *
//     * @param square the destination square of the move
//     */
//    private ArrayList<Pair<Integer, Integer>> getAdjacentSquares(String square) {
//        ArrayList<Pair<Integer, Integer>> adjacentSquares = new ArrayList<>();
//        int row = square.charAt(0);
//        int column = square.charAt(1);
//
//        if(row > 0 && column > 0 && row < 6 && column < 6){
//            for (int i = row-1; i <= row+1; i++){
//                for (int j = column-1; j <= column+1; j++){
//                    adjacentSquares.add(new Pair<>(i,j));
//                }
//            }
//        }
//
//        return adjacentSquares;
//    }

    /**
     * Displays that the game is over and the username of the winner
     *
     * @param winner the Username of the winning player
     */
    public void displayWinner(String winner) { }

    /**
     * Displays the provided message in the chat box. Applies styling based on the given char.
     *
     * @param message the message to display
     * @param style 'i' for italics, 'b' for bold, 'd' for plaintext
     */
    public void displayMessage(String message, char style) {
        System.out.println(message);
    }

    public void addChat(String message, VBox container, ScrollPane pane) {
        insertMessage(message, container, pane, "chatLabel");
    }

    public void addNotification(String message, VBox container, ScrollPane pane) {
        insertMessage(message, container, pane, "notificationLabel");
    }

    public void addError(String message, VBox container, ScrollPane pane) {
        insertMessage(message, container, pane, "errorLabel");
    }

    private void insertMessage(String message, VBox container, ScrollPane pane, String styleClass) {
        Platform.runLater(()-> {
            Label label = new Label(message);
            label.setWrapText(true);
            label.getStyleClass().add(styleClass);
            ObservableList<Node> children = container.getChildren();
            Node buffer = children.remove(children.size()-1);
            children.add(label);
            children.add(buffer);
            pane.setVvalue(1);
        });
    }

    public void displayUsernames(String username, String opponentName) {
        System.out.println("This player is " + username);
        System.out.println("The opponent is " + opponentName);

    }

    public void displayGameId(String gameId) {
        System.out.println("The game ID is " + gameId);
    }
}
