package test;

import static org.junit.Assert.assertEquals;

import game.Board;
import game.Field;

import org.junit.Before;
import org.junit.Test;

public class  BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board(4);
        board.createFields();
    }

    @Test
    public void testCoordinates() {
        assertEquals(board.getFields().get(30).getX(), 4);
        assertEquals(board.getFields().get(24).getY(), 3);
        assertEquals(board.getFields().get(29).getZ(), 3);
        assertEquals(board.getFields().get(29).getX(), 3);
        assertEquals(board.getFields().get(29).getY(), 4);
    }

    @Test 
    public void testGetUpperRight() {
        assertEquals(board.getUpperRight(board.getFields().get(29)), board.getFields().get(21));
    }

    @Test 
    public void testGetRight() {
        assertEquals(board.getRight(board.getFields().get(29)), board.getFields().get(30));
    }

    @Test 
    public void testGetLowerRight() {
        assertEquals(board.getLowerRight(board.getFields().get(29)), board.getFields().get(38));
    }

    @Test 
    public void testGetLowerLeft() {
        assertEquals(board.getLowerLeft(board.getFields().get(29)), board.getFields().get(37));
    }

    @Test 
    public void testGetLeft() {
        assertEquals(board.getLeft(board.getFields().get(29)), board.getFields().get(28));
    }

    @Test 
    public void testGetUpperLeft() {
        assertEquals(board.getUpperLeft(board.getFields().get(29)), board.getFields().get(20));
    }

    @Test
    public void testSetFieldIndexes() {
        Field firstField = board.getFields().get(29);
        board.setField(30, firstField);
        Field secondField = board.getFields().get(30);
        assertEquals(firstField, secondField);
    }

    @Test
    public void testGetFieldIndexes() {
        Field firstField = board.getFields().get(29);
        Field secondField = board.getField(29);
        assertEquals(firstField, secondField);
    }
      
    @Test
    public void testUpdateField() {
        Field firstField = board.getFields().get(29);
        board.updateField(30, firstField);
        Field secondField = board.getFields().get(30);
        assertEquals(firstField, secondField);
    }



























}
