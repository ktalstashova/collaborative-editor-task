package com.concurrenteditor.demo.document.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Model of Identifier for a char in a document {@link Document}
 */
public class Identifier implements Comparable<Identifier>{

    /** An array of int numbers defining a position of symbol in a text of document.*/
    private final int[] position;

    /** An id of a client that inserted certain symbol.*/
    private final String clientId;

    /**
     * @param position - an array of int numbers defining a position of symbol.
     * @param clientId - an id of a client.
     */
    public Identifier(int[] position, String clientId)
    {
        if (position == null)
        {
            throw new NullPointerException();
        }
        this.clientId = clientId;
        this.position = position;
    }

    /**
     * @param position  - an number defining a position of symbol.
     */
    public Identifier(int position)
    {
        this.clientId = "";
        this.position = new int[]{position};
    }

    /**
     * @param position - an number defining a position of symbol.
     * @param clientId - an id of a client.
     */
    public Identifier(int position, String clientId)
    {
        this.clientId = clientId;
        this.position = new int[]{position};
    }

    /**
     * Returns a position of a document symbol.
     * @return a position of a document symbol.
     */
    public int[] getPosition() {
        return position;
    }

    /**
     * Returns an id of a client.
     * @return a client id
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Generates a new symbol id located in a interval between two ids.
     * @param prevId - an left border of an interval.
     * @param nextId - an right border of an interval.
     * @return a new symbol id {@link Identifier}
     */
    public static Identifier generateNewIdentifier(Identifier prevId, Identifier nextId)
    {
        int i = 0;
        List<Integer> newPos = new ArrayList<>();
        int[] prevPos = prevId.position;
        int[] nextPos = nextId.position;
        int minLength = Math.min(prevPos.length, nextPos.length);
        while (true)
        {
            //When ids have different number of position digits and all numbers of one position equal to the corresponding numbers of another position
            // than take any number from an interval [0, next position element of the second id) - ex. prevPos = {1,2}, nextPos = {1,2,5} --> newPos = {1,2, random number from [0,5)}
            // As we take "prev" and "next" elements from sorted map so the number of digits in the position is always bigger for a "next" element
            if(i == minLength)
            {
                newPos.add((int) Math.floor(Math.random() * nextPos[i]));
                break;
            }
            // if position elements of both ids are equal add them to a new position
            if(prevPos[i] == nextPos[i]){
                newPos.add(prevPos[i]);
                i++;
            }
            else{
                int minEl, maxEl;
                // if the corresponding position digits are different by 1
                // add an element from an interval [0, Integer.MAX_VALUE) to a new position
                // ex. prevPos = {1,2,2}, nextPos = {1,2,3} --> newPos = {1,2,2, random number from [0, Integer.MAX_VALUE)}
                if(nextPos[i] - prevPos[i] == 1)
                {
                    newPos.add(prevPos[i]);
                    minEl = 0;
                    // if prev position contains Integer.MAX_VALUE values add all of them to new position
                    while (i < prevPos.length - 1) {
                        if (prevPos[i + 1] == Integer.MAX_VALUE) {
                            i++;
                            newPos.add(prevPos[i]);
                        } else {
                            // find first position digit less than Integer.MAX_VALUE
                            // set this number as a left border of an interval
                            // ex. prevPos = {1,2,2,Integer.MAX_VALUE,5}, nextPos = {1,2,3} --> newPos = {1,2,2,Integer.MAX_VALUE, random number from (5, Integer.MAX_VALUE)}
                            minEl = prevPos[i + 1] + 1;
                            break;
                        }
                    }
                    maxEl = Integer.MAX_VALUE;
                }
                else
                {
                    minEl = prevPos[i] + 1;
                    maxEl = nextPos[i];
                }
                newPos.add((int)Math.floor(Math.random() * (maxEl - minEl) + minEl)); //add next element in [minEl,maxEl) range
                break;
            }
        }
        return new Identifier(newPos.stream().mapToInt(Integer::intValue).toArray(), prevId.clientId);
    }

    /**
     * Checks an equality of two ids {@link Identifier} by a comparison of two positions.
     * @param other - {@link Identifier}
     * @return whether both ids {@link Identifier} are equal.
     */
    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Identifier))
        {
            return false;
        }
        return Arrays.equals(position, ((Identifier)other).position);
    }

    /**
     * Generates a hash code of an symbol id.
     * @return a hash code of {@link Identifier}
     */
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(position);
    }

    /**
     * Compares to document symbol ids using their positions.
     * @param idToCompare - an id to compare with.
     * @return 0 if ids are equal, 1  - if the first id is bigger that the second one, -1 - otherwise.
     */
    @Override
    public int compareTo(Identifier idToCompare) {

        int[] a = this.position;
        int[] b = idToCompare.position;
        int i = 0;
        int minLength = Math.min(a.length, b.length);
        while(i < minLength)
        {
            if(a[i] == b[i])
            {
                i++;
            }
            else
                return (a[i] > b[i]) ? 1 : -1;
        }
        if(a.length == b.length)
            return 0;
        else
            return (a.length > b.length) ? 1 : -1;
    }

    /**
     * Convert a document symbol id {@link Identifier} to a string.
     * @return JSON representation of {@link Identifier}
     */
    @Override
    public String toString() {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        try {
            json = mapper.writeValueAsString((Object)this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}
