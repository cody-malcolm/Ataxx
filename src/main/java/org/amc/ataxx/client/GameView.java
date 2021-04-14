package org.amc.ataxx.client;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.util.Duration;
import org.javatuples.Pair;

import java.util.ArrayList;

public class GameView {
    /** static variable instance of the class */
    private static GameView instance = null;

    /** The size of one square of the board, in pixels */
    final private static int SIZE = 70;
    /** The true size of a drawn square of the board, in pixels */
    final private static int actualSIZE = SIZE-2;
    /** The size of the canvas, in pixels */
    final private static int canvasSIZE = SIZE*7 + 10;
    /** The controller that manages communication with the rest of the Application */
    private static GameController gameController;
    /** The GraphicsContext associated with the Canvas */
    private static GraphicsContext gc;

    /** Colors */
    private static Color[] pieceColors = {Color.hsb(0,1,0.79), Color.hsb(215, 1, 0.79)};
    private static Color[] squareHighlightColors = {Color.hsb(0,0.0,0.60), Color.hsb(0, 0.0, 0.70)};
    private static Color[] inactiveColors = {Color.hsb(0,1,0.40), Color.hsb(215, 1, 0.40)};


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
        String[] rows = board.split("/");

        for (int i = 0; i < 7; i++){
            for (int j = 0; j < 7; j++) {

                char piece = rows[i].charAt(j);

                if(piece == '1'){
                    renderPiece(pieceColors[0],i,j);
                } else if (piece == '2'){
                    renderPiece(pieceColors[1],i,j);
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
        gc.fillOval(getPosition((double) col), getPosition((double) row), actualSIZE-(actualSIZE/10),actualSIZE-(actualSIZE/10));
    }


    /**
     * Returns the x or y position for a piece to be drawn.
     *
     * @param x the column or row of the square the piece will be in
     */
    private double getPosition(double x){
        return 5 + (actualSIZE/10)/2 + (SIZE) * x;
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

    public void displayTurn(char activePlayer, char key, Label blueLabel, Label redLabel, String[] displayNames) {

        // TODO bet this can be cleaned up now
        if (activePlayer == '1') {
            // For testing
//            System.out.println("Testing turn render: Inside activePlayer(" + activePlayer + ") == key(" + key + ")");
            redLabel.setTextFill(pieceColors[0]);
            blueLabel.setTextFill(inactiveColors[1]);
        } else {
            redLabel.setTextFill(inactiveColors[0]);
            blueLabel.setTextFill(pieceColors[1]);
        }

        Platform.runLater(()-> {
            blueLabel.setText(displayNames[1]);
            redLabel.setText(displayNames[0]);
        });
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
                gameController.processMouseClick(square);
            }
        };
    }

    /**
     * Setter for controller. // TODO rename/reword this
     *
     * @param gameController the Controller to use to communicate with the rest of the Application.
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * Given a list of "steps" and "jumps", highlights the squares identified in each list according to if they are a
     * step or a jump
     *
     * @param steps squares the selected piece can legally move to without jumping
     * @param jumps squares the selected piece can legally move to by jumping
     */
    public void applyHighlighting(Pair<Integer, Integer> source, ArrayList<Pair<Integer, Integer>> steps, ArrayList<Pair<Integer, Integer>> jumps, char key) {
        highlightSquares(jumps, key, squareHighlightColors[1]);
        highlightSquares(steps, key, squareHighlightColors[0]);
        gc.setFill(squareHighlightColors[Integer.valueOf(key)-49]);
        gc.fillRoundRect(SIZE*source.getValue1()+5, SIZE*source.getValue0()+5, actualSIZE, actualSIZE, SIZE/4,SIZE/4);
        renderPiece(pieceColors[Integer.valueOf(key)-49], source.getValue0(), source.getValue1());
    }

    /**
     * Highlights the squares in the given list.
     *
     * @param squares the list of pairs of squares to highlight
     */
    private void highlightSquares(ArrayList<Pair<Integer, Integer>> squares, char key, Color color) {
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
    private static int x;
    public void animateMove(String oldBoard, String newBoard, String move, char activePlayer) {
        String sourceSquare = String.valueOf(move.charAt(0)) + String.valueOf(move.charAt(1));
        String destinationSquare = String.valueOf(move.charAt(2)) + String.valueOf(move.charAt(3));
        int sourceRow = Integer.valueOf(sourceSquare.charAt(0))-49;
        int sourceColumn = Integer.valueOf(sourceSquare.charAt(1))-49;
        int destRow = Integer.valueOf(destinationSquare.charAt(0))-49;
        int destColumn = Integer.valueOf(destinationSquare.charAt(1))-49;
        x = sourceColumn;
        // ensure the currently rendered board is as expected
        renderBoard(oldBoard);

        if (isAdjecent(sourceSquare, destinationSquare)){ // if it's a step

            DoubleProperty x  = new SimpleDoubleProperty(getPosition(destColumn+1));
            DoubleProperty y  = new SimpleDoubleProperty(getPosition(destRow+1));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(x, getPosition(sourceColumn+1)),
                            new KeyValue(y, getPosition(sourceRow+1))
                    ),
                    new KeyFrame(Duration.seconds(0.5),
                            new KeyValue(x, getPosition(destColumn+1)),
                            new KeyValue(y, getPosition(destRow+1))
                    )
            );
            timeline.setCycleCount(1);

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    renderBoard(oldBoard);
                    gc.setFill(pieceColors[changeColor(activePlayer)]);
                    gc.fillOval(x.doubleValue(), y.doubleValue(), actualSIZE-(actualSIZE/10), actualSIZE-(actualSIZE/10));
                }
            };

            timer.start();
            timeline.play();
            timeline.setOnFinished(event -> {
                timer.stop();
                renderBoard(newBoard);
            });

        } else { // else it's a jump
            DoubleProperty x  = new SimpleDoubleProperty(getPosition(destColumn+1));
            DoubleProperty y  = new SimpleDoubleProperty(getPosition(destRow+1));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(0),
                            new KeyValue(x, getPosition(sourceColumn+1)),
                            new KeyValue(y, getPosition(sourceRow+1))
                    ),
                    new KeyFrame(Duration.seconds(0.5),
                            new KeyValue(x, getPosition(destColumn+1)),
                            new KeyValue(y, getPosition(destRow+1))
                    )
            );
            timeline.setCycleCount(1);

            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    renderBoard(oldBoard);
                    gc.setFill(pieceColors[changeColor(activePlayer)]);
                    gc.fillOval(x.doubleValue(), y.doubleValue(), actualSIZE-(actualSIZE/10), actualSIZE-(actualSIZE/10));
                    renderPiece(pieceColors[changeColor(activePlayer)], (int)sourceRow, (int)sourceColumn);
                }
            };

            timer.start();
            timeline.play();
            timeline.setOnFinished(event -> {
                timer.stop();
                renderBoard(newBoard);
            });

        }




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
        gc.clearRect(0,0, canvasSIZE,canvasSIZE);
        renderBoard(newBoard);

    }

    private int changeColor(char activePlayer){
        int k = Integer.valueOf(activePlayer)-49;
        if (k == 0){
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isAdjecent(String source, String destination){
        int sourceRow = Integer.valueOf(source.charAt(0))-49;
        int sourceColumn = Integer.valueOf(source.charAt(1))-49;
        int destRow = Integer.valueOf(destination.charAt(0))-49;
        int destColumn = Integer.valueOf(destination.charAt(1))-49;

        for (int i = sourceRow-1; i <= sourceRow+1; i++) {
            if (destRow == i){
                return true;
            }
        }

        for (int i = sourceColumn-1; i <= sourceColumn+1; i++) {
            if (destColumn == i){
                return true;
            }
        }

        return false;
    }


    /**
     * Displays that the game is over and the username of the winner
     *
     * @param winner the Username of the winning player
     */
    public void displayWinner(String winner) {

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

    public void displayGameId(String gameId, Label gameIDlabel) {
        gameIDlabel.setText("Game ID:" + gameId);
    }

    public void displayCounts(Pair<Integer, Integer> counts, Label blueLabel, Label redLabel) {
        blueLabel.setTextFill(pieceColors[1]);
        redLabel.setTextFill(pieceColors[0]);
        Platform.runLater(()-> {
            blueLabel.setText(counts.getValue1().toString());
            redLabel.setText(counts.getValue0().toString());
        });
    }
}
