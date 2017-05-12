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
    private int    status;


    public Player(String nick, int status) {
        this.nick = nick;
        this.status = status;
    }

    public Player(String nick, String uuid) {
        this.nick = nick;
        this.uuid = uuid;
    }

    public static HashMap<String, Object> create(String nick, int status) {
        return new Event<>(
                Event.EventType.FIRST_MESSAGE,
                new Player(nick, status)).toHashMap();
    }

    public static Player create(Message message) {
        Player player = new Gson().fromJson(
                                new Gson().toJson(message.getContent().get("content")),
                                Player.class);
        player.setUuid(message.getSenderId());
        return player;
    }


    public String getNick() {
        return nick;
    }

    public int getStatus() {
        return status;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid.substring(0, 5);
    }
}
