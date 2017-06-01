package com.bridgefy.samples.tic_tac_toe.entities;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class Event<T> {

    int event;
    T   content;

    public enum EventType {
        FIRST_MESSAGE,
        AVAILABLE,
        MOVE_EVENT,
        REFUSE_MATCH
    }


    public Event(EventType event, T content) {
        this.event = event.ordinal();
        this.content = content;
    }

    public HashMap<String, Object> toHashMap() {
        Gson gson = new Gson();
        String s = gson.toJson(this);
        Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
        return gson.fromJson(s, type);
    }
}
