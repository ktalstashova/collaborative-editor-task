package com.concurrenteditor.demo.document.controller;

import com.concurrenteditor.demo.document.model.Document;
import com.concurrenteditor.demo.document.model.Operation;
import com.concurrenteditor.demo.document.model.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Documents operations controller.
 */
@Controller
public class OperationController {

    @Autowired
    Document document;

    /**
     * Performs a document operation and broadcast this operation to clients subscribed to @SendTo topic.
     * @param message - an operation described what operation should be performed with a document.
     * @return an operation to broadcast to clients.
     */
    @MessageMapping("/operation")
    @SendTo("/document/operations")
    public Operation operation(Operation message) throws Exception {
        Operation operationToBroadCast = message;
        switch (message.getType()) {
            case INSERT:
                Identifier originalIdentifier = new Identifier(message.getPosition(), message.getClientId());
                Identifier actualIdentifier = document.insertChar(originalIdentifier, message.getCharacter());
                if (actualIdentifier != originalIdentifier) {
                    operationToBroadCast = new Operation(Operation.Type.CHANGEID.name(),
                            actualIdentifier.getPosition(), actualIdentifier.getClientId(), message.getCharacter());
                }
                break;
            case DELETE:
                document.deleteChar(new Identifier(message.getPosition(), message.getClientId()));
                break;
            default:
                break;
        }
        return operationToBroadCast;
    }
}
