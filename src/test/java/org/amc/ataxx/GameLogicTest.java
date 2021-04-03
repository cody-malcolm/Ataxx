package org.amc.ataxx;

import org.javatuples.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTest {
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
    public void testBoardFull() {
        assertFalse(GameLogic.boardFull(board1), "Initial board");
        assertFalse(GameLogic.boardFull(board3), "Board almost full");
        assertTrue(GameLogic.boardFull(board4), "Board is full");

    }
    @Test
    public void testValidateMove() {
        assertTrue(GameLogic.validateMove(board1, source1, dest1, '1'), "Move is legal.");
        assertFalse(GameLogic.validateMove(board1, source1, dest1, '2'), "Move is illegal - player doesn't control piece.");
        assertFalse(GameLogic.validateMove(board1, source1, dest2, '1'), "Move is illegal - too far.");
        assertFalse(GameLogic.validateMove(board1a, source1, dest1, '1'), "Move is illegal - destination square occupied.");
    }

    @Test
    public void testIsSquareEmpty() {
        assertFalse(GameLogic.isSquareEmpty(board1, source1), "Square is occupied - p1");
        assertTrue(GameLogic.isSquareEmpty(board1, dest1), "Square is unoccupied");
        assertFalse(GameLogic.isSquareEmpty(board1, new Pair<>(0,6)), "Square is occupied - p2");
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

        assertTrue(GameLogic.getAdjacent(source1).equals(list1), "Corner square");
        assertTrue(GameLogic.getAdjacent(dest1).equals(list2), "Edge square");
        assertTrue(GameLogic.getAdjacent(dest3).equals(list3), "Central square");
    }

    @Test
    public void testGetSquare() {
        assertEquals('1', GameLogic.getSquare(board1, source1), "Should return 1");
        assertEquals('2', GameLogic.getSquare(board2b, dest2), "Should return 2");
        assertEquals('-', GameLogic.getSquare(board1, dest1), "Should return -");
    }
}
