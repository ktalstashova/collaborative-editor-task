package com.concurrenteditor.demo.document.model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class IdentifierTest {
    public static final String CLIENT_ID = "46542315646";

    @Test()
    public void testConstructors() {
        int [] pos = new int[]{1,2,3};
        Identifier id = new Identifier(pos, CLIENT_ID);
        assertEquals(pos, id.getPosition());
        assertEquals(CLIENT_ID, id.getClientId());

        id = new Identifier(3, CLIENT_ID);
        assertEquals(3, id.getPosition()[0]);
        assertEquals(CLIENT_ID, id.getClientId());

        id = new Identifier(3);
        assertEquals(3, id.getPosition()[0]);
        assertEquals("", id.getClientId());

        assertThrows(NullPointerException.class, () -> {
            new Identifier(null, CLIENT_ID);
        });
    }
        @Test
    public void testEquals(){
        Identifier id1 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        Identifier id2 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        Identifier id3 = new Identifier(new int[]{2,3,1}, CLIENT_ID);
        Identifier id4 = new Identifier(new int[]{2,3}, CLIENT_ID);
        assertTrue(id1.equals(id1));
        assertTrue(id1.equals(id2));
        assertFalse(id1.equals(id3));
        assertFalse(id3.equals(id4));
        assertFalse(id1.equals(id4));
    }

    @Test
    public void testCompareTo(){
        Identifier id1 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        Identifier id2 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        Identifier id3 = new Identifier(new int[]{2,3,1}, CLIENT_ID);
        Identifier id4 = new Identifier(new int[]{2,3}, CLIENT_ID);
        Identifier id5 = new Identifier(new int[]{2,3,1,5}, CLIENT_ID);
        assertTrue(id1.compareTo(id1) == 0);
        assertTrue(id1.compareTo(id2) == 0);
        assertTrue(id1.compareTo(id3) < 0);
        assertTrue(id3.compareTo(id4) > 0);
        assertTrue(id3.compareTo(id5) < 0);
    }

    @Test
    public void testHashCode(){
        int [] pos = new int[]{1,2,3};
        Identifier id = new Identifier(pos, CLIENT_ID);
        assertEquals(Arrays.hashCode(pos), id.hashCode());
    }

    @Test
    public void testGenerateNewIdentifier(){
        Identifier id1 = new Identifier(new int[]{1}, CLIENT_ID);
        Identifier id2 = new Identifier(new int[]{3}, CLIENT_ID);
        Identifier newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(new Identifier(2, CLIENT_ID), newId);

        id1 = new Identifier(new int[]{1,2}, CLIENT_ID);
        id2 = new Identifier(new int[]{3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(new Identifier(2, CLIENT_ID), newId);

        id1 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,4,3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(new Identifier(new int[]{1,3}, CLIENT_ID), newId);

        id1 = new Identifier(new int[]{1,2}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(1, newId.getPosition()[0]);
        assertEquals(2, newId.getPosition()[1]);
        assertTrue(newId.getPosition()[2] < Integer.MAX_VALUE && newId.getPosition()[2] >=0);

        id1 = new Identifier(new int[]{1,2,3}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(1, newId.getPosition()[0]);
        assertEquals(2, newId.getPosition()[1]);
        assertTrue(newId.getPosition()[2] < Integer.MAX_VALUE && newId.getPosition()[2] > 3);

        id1 = new Identifier(new int[]{1,2,Integer.MAX_VALUE, Integer.MAX_VALUE}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(1, newId.getPosition()[0]);
        assertEquals(2, newId.getPosition()[1]);
        assertEquals(Integer.MAX_VALUE, newId.getPosition()[2]);
        assertEquals(Integer.MAX_VALUE, newId.getPosition()[3]);
        assertTrue(newId.getPosition()[4] < Integer.MAX_VALUE && newId.getPosition()[4] >= 0);

        id1 = new Identifier(new int[]{1,2,Integer.MAX_VALUE, Integer.MAX_VALUE - 2}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,3}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(1, newId.getPosition()[0]);
        assertEquals(2, newId.getPosition()[1]);
        assertEquals(Integer.MAX_VALUE, newId.getPosition()[2]);
        assertEquals(Integer.MAX_VALUE - 1, newId.getPosition()[3]);

        id1 = new Identifier(new int[]{1,3}, CLIENT_ID);
        id2 = new Identifier(new int[]{1,10}, CLIENT_ID);
        newId = Identifier.generateNewIdentifier(id1,id2);
        assertEquals(1, newId.getPosition()[0]);
        assertTrue(newId.getPosition()[1] < 10 && newId.getPosition()[1] > 3);
   }

}
