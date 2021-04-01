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
    private static Controller controller;

    public static void createCanvas(BorderPane borderPane) {
        Canvas canvas = new Canvas();
        canvas.setHeight(560);
        canvas.setWidth(560);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, Views.handleMouseClick());

        borderPane.setCenter(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawBoard(gc);
    }

    public static void drawBoard(GraphicsContext gc) {
        Color[] colors = {Color.hsb(0, 0, 0.90), Color.hsb(0, 0, 0.80)};
        for (int j = 0; j < 7; j++) {
            gc.setFill(colors[1]);
            gc.setFill(colors[j%2]);
            for (int i = 0; i < 7; i++) {
                gc.fillRect(80*i, j*80, 80, 80);
                gc.setFill(colors[(i+j+1)%2]);
            }
        }
    }

    public static EventHandler<MouseEvent> handleMouseClick() {
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Integer row = Integer.valueOf((int)Math.floor(event.getY()/80));
                Integer col = Integer.valueOf((int)Math.floor(event.getX()/80));

                Pair<Integer, Integer> square = new Pair<>(row, col);
                controller.processMouseClick(square);
            }
        };
    }

    public static void setController(Controller controller) {
        Views.controller = controller;
    }

    public static void renderBoard(String board) {
        System.out.println("draw the board");
        // take the given board and immediately render it, overwriting anything there already
    }

    public static void highlightDestinationSquares(ArrayList<Pair<Integer, Integer>> steps, ArrayList<Pair<Integer, Integer>> jumps) {
        // render a small circle (maybe 15px?) on each step and jump, use somewhat darker colour for jumps
    }

    public static void animateMove(String oldBoard, String newBoard, String move) {
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
    }
}
