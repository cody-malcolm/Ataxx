package org.amc;

import org.amc.ataxx.server.Board;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

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


    @Test
    public void testBoardFull() {
        assertFalse(Board.boardFull(board1), "Initial board");
        assertFalse(Board.boardFull(board3), "Board almost full");
        assertTrue(Board.boardFull(board4), "Board is full");

    }
    @Test
    public void testValidateMove() {
        assertTrue(Board.validateMove(board1, source1, dest1, '1'), "Move is legal.");
        assertFalse(Board.validateMove(board1, source1, dest1, '2'), "Move is illegal - player doesn't control piece.");
        assertFalse(Board.validateMove(board1, source1, dest2, '1'), "Move is illegal - too far.");
        assertFalse(Board.validateMove(board1a, source1, dest1, '1'), "Move is illegal - destination square occupied.");
    }

    @Test
    public void testIsSquareEmpty() {
        assertFalse(Board.isSquareEmpty(board1, source1), "Square is occupied - p1");
        assertTrue(Board.isSquareEmpty(board1, dest1), "Square is unoccupied");
        assertFalse(Board.isSquareEmpty(board1, new Pair<>(0,6)), "Square is occupied - p2");
    }

    @Test
    public void testGetAdjacent() {
        ArrayList<Pair<Integer, Integer>> list1 = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> list2 = new ArrayList<>();
        ArrayList<Pair<Integer, Integer>> list3 = new ArrayList<>();

        list1.add(new Pair<>(0,1));
        list1.add(new Pair<>(1,1));
        list1.add(new Pair<>(1,0));

        list2.add(new Pair<>(0,0));
        list2.add(new Pair<>(1,0));
        list2.add(new Pair<>(1,1));
        list2.add(new Pair<>(1,2));
        list2.add(new Pair<>(0,2));

        list3.add(new Pair<>(3,1));
        list3.add(new Pair<>(3,2));
        list3.add(new Pair<>(3,3));
        list3.add(new Pair<>(4,1));
        list3.add(new Pair<>(4,3));
        list3.add(new Pair<>(5,1));
        list3.add(new Pair<>(5,2));
        list3.add(new Pair<>(5,3));

        assertTrue(Board.getAdjacent(source1).equals(list1), "Corner square");
        assertTrue(Board.getAdjacent(dest1).equals(list2), "Edge square");
        assertTrue(Board.getAdjacent(dest3).equals(list3), "Central square");
    }

    @Test
    public void testGetSquare() {
        assertEquals('1', Board.getSquare(board1, source1), "Should return 1");
        assertEquals('2', Board.getSquare(board2b, dest2), "Should return 2");
        assertEquals('-', Board.getSquare(board1, dest1), "Should return -");
    }

}
