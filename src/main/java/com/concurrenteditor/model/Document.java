package com.concurrenteditor.model;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Document model.
 */
@Component
public class Document {
    /** A content of a document in a map format, where every symbol in a document has its own unique identifier.*/
    private static ConcurrentSkipListMap<Identifier, Character> content = null;

    /** Static initialization of a document. This documents is used for testing purpose. */
    static {
        content = new ConcurrentSkipListMap<>();
        content.put(new Identifier(0), Character.MIN_VALUE); // START SYMBOL
        content.put(new Identifier(1), 'I');
        content.put(new Identifier(2), ' ');
        content.put(new Identifier(3), 'l');
        content.put(new Identifier(4), 'o');
        content.put(new Identifier(5), 'v');
        content.put(new Identifier(6), 'e');
        content.put(new Identifier(7), ' ');
        content.put(new Identifier(8), 'B');
        content.put(new Identifier(9), 'o');
        content.put(new Identifier(10), 's');
        content.put(new Identifier(11), 't');
        content.put(new Identifier(12), 'o');
        content.put(new Identifier(13), 'n');
        content.put(new Identifier(14), '!');
        content.put(new Identifier(Integer.MAX_VALUE), Character.MIN_VALUE);
    }

    /**
     * Returns a content of a document.
     * @return a content of a document in form of {@link Map}.
     */
    public Map<Identifier, Character> getContent() {
        return content;
    }

    /**
     * Inserts new char to a document.
     * @param identifier - an id {@link Identifier} of new char.
     * @param c - {@link Character} to insert.
     * @return an id {@link Identifier} of new char.
     */
    public Identifier insertChar(Identifier identifier, Character c) {

        if (content.containsKey(identifier)) {
            // In some rare cases where insertion happens at the same place on different clients at the same time
            // IDs can be generated the same. So they might override each other. Regenerating ID here.
            synchronized (content) {
                identifier = generateNewIdentifier(identifier);
                content.put(identifier, c);
            }
        } else {
            content.put(identifier, c);
        }
        return identifier;
    }

    /**
     * Deletes a char by its id {@link Identifier} from a document.
     * @param identifier - an id {@link Identifier} of a char to delete.
     */
    public void deleteChar(Identifier identifier) {
        content.remove(identifier);
    }

    /**
     * Returns new id bigger than old one.
     * @param id - initial id {@link Identifier} of a char.
     * @return a new id {@link Identifier}.
     */
    private static Identifier generateNewIdentifier(Identifier id) {
        Identifier nextId = content.higherKey(id);
        return Identifier.generateNewIdentifier(id, nextId);
    }
}
