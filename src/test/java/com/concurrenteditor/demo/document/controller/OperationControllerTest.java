package com.concurrenteditor.demo.document.controller;

import com.concurrenteditor.demo.document.model.Document;
import com.concurrenteditor.demo.document.model.Identifier;
import com.concurrenteditor.demo.document.model.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationControllerTest {
    @InjectMocks
    OperationController operationController;

    @Mock
    Document document;

    @Test
    public void testOperation() throws Exception {
        int[] pos = new int[]{1, 2, 4};
        String clientId = "545432198788353";
        Character c = 'B';
        Operation operation = new Operation("INSERT", pos, clientId, 'B');
        Identifier id = new Identifier(pos, clientId);
        // insert operation test
        when(document.insertChar(id, c)).thenAnswer(new Answer<Identifier>() {
            @Override
            public Identifier answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (Identifier) args[0];
            }
        });
        Operation operationToBroadcast = operationController.operation(operation);
        assertEquals(operation, operationToBroadcast);

        // change id operation test
        int[] newPos = new int[]{1, 2, 4,1};
        Identifier id2 = new Identifier(new int[]{1, 2, 4,1}, clientId);
        when(document.insertChar(id, c)).thenReturn(id2);
        operationToBroadcast = operationController.operation(operation);
        assertEquals(Operation.Type.CHANGEID, operationToBroadcast.getType());
        assertTrue(Arrays.equals(newPos, operationToBroadcast.getPosition()));
        assertEquals(clientId, operationToBroadcast.getClientId());

        // delete operation test
        doNothing().when(document).deleteChar(id);
        operation =new Operation("DELETE", pos, clientId,null);
        operationToBroadcast = operationController.operation(operation);
        assertEquals(operation, operationToBroadcast);
    }
}
