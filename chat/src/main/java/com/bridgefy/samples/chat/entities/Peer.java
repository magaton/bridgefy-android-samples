package com.bridgefy.samples.chat.entities;

import com.google.gson.Gson;

/**
 * @author dekaru on 5/9/17.
 */

public class Peer {

    private String  device_name;
    private String  uuid;
    private boolean isNearby;

    public Peer(String uuid, String device_name) {
        this.uuid = uuid;
        this.device_name = device_name;
    }


    public String getDeviceName() {
        return device_name;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isNearby() {
        return isNearby;
    }

    public void setNearby(boolean nearby) {
        isNearby = nearby;
    }

    public static Peer create(String json) {
        return new Gson().fromJson(json, Peer.class);
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
