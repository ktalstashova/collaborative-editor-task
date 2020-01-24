package com.concurrenteditor.controller;

import com.concurrenteditor.model.Document;
import com.concurrenteditor.model.Identifier;
import com.concurrenteditor.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentControllerTest {
    @InjectMocks
    DocumentController documentController;

    @Mock
    Document document;

    @Test
    public void testDocument() {
        Document doc = new Document();
        when(document.getContent()).thenReturn(doc.getContent());
        assertEquals(doc.getContent(), documentController.document());
    }

    @Test
    public void testInsertChar() {
        int[] pos = new int[]{1, 2, 4};
        String clientId = "545432198788353";
        String type = "INSERT";
        Character c = 'B';
        Document doc = new Document();
        Operation operation = new Operation(type, pos, clientId, c);
        Identifier id = new Identifier(pos, clientId);

        when(document.insertChar(id, c)).thenReturn(doc.insertChar(id, c));
        Identifier id2 = documentController.insertChar(operation);
        assertTrue(doc.getContent().containsKey(id2));
        assertEquals(doc.getContent().get(id), c);
    }

    @Test
    public void testDeleteChar() {
        Document doc = new Document();
        int[] pos = new int[]{1, 2, 4};
        String clientId = "545432198788353";
        String type = "DELETE";
        Character c = 'B';
        Operation operation = new Operation(type, pos, clientId, c);
        Identifier id = new Identifier(pos, clientId);

        when(document.insertChar(id, c)).thenReturn(doc.insertChar(id, c));
        documentController.insertChar(operation);
        assertTrue(doc.getContent().containsKey(id));

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Identifier id = (Identifier) invocation.getArguments()[0];
                doc.deleteChar(id);
                return null;
            }
        }).when(document).deleteChar(id);
        documentController.deleteChar(operation);
        assertFalse(doc.getContent().containsKey(id));
    }

}
