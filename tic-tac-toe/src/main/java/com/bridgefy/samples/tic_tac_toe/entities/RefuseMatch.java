package com.bridgefy.samples.tic_tac_toe.entities;

import com.bridgefy.sdk.client.Message;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class RefuseMatch {

    private String mid;
    private boolean already_playing;


    public RefuseMatch(String mid, boolean already_playing) {
        this.mid = mid;
        this.already_playing = already_playing;
    }


    public HashMap<String, Object> toHashMap() {
        return new Event<>(
                Event.EventType.REFUSE_MATCH,
                this).toHashMap();
    }

    public static RefuseMatch create(Message message) {
        return new Gson().fromJson(
                new Gson().toJson(message.getContent().get("content")),
                RefuseMatch.class);
    }


    public String getMatchId() {
        return mid;
    }
}
