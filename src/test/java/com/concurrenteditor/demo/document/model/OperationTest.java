package com.concurrenteditor.demo.document.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OperationTest {

    @Test
    public void testOperation()
    {
        int [] pos = new int[]{1,2,4};
        String clientId = "545432198788353";
        String type = "INSERT";
        Character c = 'B';
        Operation operation  = new Operation(type, pos, clientId, c);
        assertEquals(pos, operation.getPosition());
        assertEquals(type, operation.getType().name());
        assertEquals(c, operation.getCharacter());
        assertEquals(clientId, operation.getClientId());
    }
}
