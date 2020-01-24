package com.concurrenteditor.demo.document.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentTest {
    public static final String CLIENT_ID = "14559873256898";

    @Test
    public void testInsertChar(){
        Document doc  = new Document();
        Character c = 'B';
        Identifier id = new Identifier(new int[]{1, 2}, CLIENT_ID);
        doc.insertChar(id, c);
        assertEquals(doc.getContent().get(id), c);

        // Test ids conflict
        id = new Identifier(new int[]{3}, CLIENT_ID);
        Identifier actualId = doc.insertChar(id, c);
        System.out.println(actualId);
        assertEquals(doc.getContent().get(actualId), c);
        assertNotEquals(id, actualId);
    }

    @Test
    public void testDeleteChar(){
        Document doc  = new Document();
        Character c = 'B';
        Identifier id = new Identifier(new int[]{1, 2}, CLIENT_ID);
        doc.insertChar(id, c);
        assertEquals(doc.getContent().get(id), c);
        doc.deleteChar(id);
        assertFalse(doc.getContent().containsKey(id));
    }

    @Test
    public void testGenerateNewIdentifier() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Document doc  = new Document();
        Identifier id = new Identifier(new int[]{3}, CLIENT_ID);
        Method method = Document.class.getDeclaredMethod("generateNewIdentifier", Identifier.class);
        method.setAccessible(true);
        Identifier newId = (Identifier) method.invoke(doc, id);
        assertNotEquals(id, newId);
    }

}
