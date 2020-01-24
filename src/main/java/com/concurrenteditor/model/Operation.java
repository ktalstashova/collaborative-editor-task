package com.concurrenteditor.model;

/**
 * Model of Operation triggered by User or Server
 */
public class Operation {
    /** Operations types.*/
    public enum Type {
        INSERT,
        DELETE,
        CHANGEID
    }

    /**A type of an operation.*/
    private Type type;
    /** A position of doc symbol.*/
    private int[] position;
    /** A client Id who performs an operation.*/
    private String clientId;
    /** A char to do operation with.*/
    private Character character;

    /**
     * Operation constructor.
     * @param type an operation type.
     * @param pos a doc symbol position.
     * @param clientId a client ID.
     * @param c a char to do operation with.
     */
    public Operation(String type, int[] pos, String clientId, Character c) {
        this.type = Type.valueOf(type);
        this.position = pos;
        this.clientId = clientId;
        this.character = c;
    }

    /**
     * Returns an operation type.
     * @return an operation type.
     */
    public Operation.Type getType() {

        return type;
    }

    /**
     * Sets an operation type.
     * @param type an operation type.
     */
    public void setType(String type) {

        this.type = Type.valueOf(type);
    }

    /**
     * Returns a doc symbol position.
     * @return a doc symbol position.
     */
    public int[] getPosition() {
        return position;
    }

    /**
     * Sets a doc symbol position.
     * @param pos a doc symbol position.
     */
    public void setPosition(int[] pos) {
        this.position = pos;
    }

    /**
     * Sets a char to do operation with.
     * @return a char to do operation with.
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * Returns a char to do operation with.
     * @param c a char to do operation with.
     */
    public void getCharacter(Character c) {
        this.character = c;
    }

    /**
     * Returns a client ID.
     * @return a client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets a client ID.
     * @param clientId a client ID.
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
