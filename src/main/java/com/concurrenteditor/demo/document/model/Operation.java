package com.concurrenteditor.demo.document.model;

/**
 * Model of Operation triggered by User or Server
 */
public class Operation {
    public enum Type {
        INSERT,
        DELETE,
        CHANGEID
    }

    private Type type;
    private int[] position;
    private String clientId;
    private Character character;

    public Operation(String type, int[] pos, String clientId, Character c) {
        this.type = Type.valueOf(type);
        this.position = pos;
        this.clientId = clientId;
        this.character = c;
    }

    public Operation.Type getType() {

        return type;
    }

    public void setType(String type) {

        this.type = Type.valueOf(type);
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] pos) {
        this.position = pos;
    }

    public Character getCharacter() {
        return character;
    }

    public void getCharacter(Character c) {
        this.character = c;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
