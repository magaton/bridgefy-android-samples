package com.bridgefy.samples.tic_tac_toe.entities;

import com.bridgefy.sdk.client.Message;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class Player {

    public static final int STATUS_PLAYING = 0;
    public static final int STATUS_FREE    = 1;

    private String nick;
    private String uuid;
    private int    status = STATUS_FREE;


    public Player(String uuid, String nick) {
        this.uuid = uuid;
        this.nick = nick;
    }


    public HashMap<String, Object> toHashMap() {
        return new Event<>(
                Event.EventType.FIRST_MESSAGE,
                this).toHashMap();
    }

    public static Player create(Message message) {
        Player player = new Gson().fromJson(
                                new Gson().toJson(message.getContent().get("content")),
                                Player.class);
        player.setUuid(message.getSenderId());
        return player;
    }

    public static Player create(String json) {
        return new Gson().fromJson(json, Player.class);
    }


    public String getNick() {
        return nick;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
