package com.concurrenteditor.demo.document.controller;

import com.concurrenteditor.demo.document.model.Document;
import com.concurrenteditor.demo.document.model.Operation;
import com.concurrenteditor.demo.document.model.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Rest controller for working with documents.
 */
@RestController
public class DocumentController {

    @Autowired
    Document document;

    /**
     * Returns a content of a document.
     * @return a content of a document in map form
     */
    @GetMapping("/document")
    public Map<Identifier, Character> document() {
        return document.getContent();
    }

    /**
     * Insert a char to a position defined in a operation {@link Operation}
     * @param operation {@link Operation} describes the operation to perform
     * @return an id of inserted char
     */
    @PostMapping(path="/insert")
    public Identifier insertChar(@RequestBody Operation operation) {
        return document.insertChar(new Identifier(operation.getPosition(), operation.getClientId()), operation.getCharacter());
    }

    /**
     * Delete a char at position defined in {@link Operation}
     * @param operation {@link Operation} describes the operation to perform
     */
    @PostMapping(path="/delete")
    public void deleteChar(@RequestBody Operation operation) {
        document.deleteChar(new Identifier(operation.getPosition(), operation.getClientId()));
    }

}
