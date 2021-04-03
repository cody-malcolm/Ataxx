package org.amc.ataxx.server;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardTest {
    // TODO needs to be reviewed to see what should go in GameLogic

    Pair<Integer, Integer> source1 = new Pair<>(0, 0);
    Pair<Integer, Integer> dest1 = new Pair<>(0, 1);
    String board1  = "1-----2/-------/-------/-------/-------/-------/2-----1";

    // board 1 after source1, dest1, from 1
    String board1a = "11----2/-------/-------/-------/-------/-------/2-----1";

    Pair<Integer, Integer> source2 = new Pair<>(2, 1);
    Pair<Integer, Integer> dest2 = new Pair<>(3, 3);
    String board2  = "1-----2/1------/-1-----/-------/2------/2------/2-----1";

    // board 2 after source2, dest2, from 1
    String board2a = "1-----2/1------/-------/---1---/2------/2------/2-----1";

    Pair<Integer, Integer> source3 = new Pair<>(6, 0);
    Pair<Integer, Integer> dest3 = new Pair<>(4, 2);

    // board 2a after source3, dest3, from 2
    String board2b = "1-----2/1------/-------/---2---/2-2----/2------/------1";

    String board3  = "1111222/1111222/111-222/11--222/1111-22/1122222/2222222";
    String board4  = "1111112/1112222/1111222/2222222/2222222/1111122/1111122";

    @Test
    public void testConstructor() {
        Board b = new Board();
        assertTrue(board1.equals(b.getBoard()), "Board should match");

        Board b2 = new Board(board2, '1');
        assertTrue(board2.equals(b.getBoard()), "Board should match");
        assertEquals('1', b.getActivePlayer(), "activePlayer should be 1");
    }

    @Test
    public void testApplyMove() {
        Board b = new Board();
        b.applyMove(source1, dest1, '2');
        assertTrue(board1.equals(b.getBoard()), "Wrong player tried to move");
        b.applyMove(source1, dest3, '1');
        assertTrue(board1.equals(b.getBoard()), "Move is illegal");
        b.applyMove(source1, dest1, '1');
        assertTrue(board1a.equals(b.getBoard()), "Move is legal - step");

        Board b2 = new Board(board2, '1');
        b2.applyMove(source2, dest2, '1');
        assertTrue(board2a.equals(b2.getBoard()), "Move is legal - jump");
        b2.applyMove(source1, dest1, '1');
        assertTrue(board2a.equals(b2.getBoard()), "Wrong player tried to move");
        b2.applyMove(source3, dest3, '2');
        assertTrue(board2b.equals(b2.getBoard()), "Move is legal - jump + convert adjacent");

        // more test cases are needed
    }

    @Test
    public void testGetSteps() {
        // tests are needed for steps
        // similar to Board.getAdjacent, but with filled squares pruned
    }

    @Test
    public void testGetJumps() {
        // tests are needed for jumps
        // like Board.testAdjacent and Board.testGetSteps, should return an ArrayList<Pair<Integer, Integer>>
    }

}
