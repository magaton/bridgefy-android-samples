package com.bridgefy.samples.tic_tac_toe;

import android.util.Log;

import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.squareup.otto.Bus;

/**
 * @author dekaru on 5/10/17.
 */
class BridgefyListener {

    private static final String TAG = "BridgefyListener";

    private static BridgefyListener instance;
    private Bus ottoBus;
    private String username;


    private BridgefyListener(Bus ottoBus) {
        this.ottoBus = ottoBus;
    }

    static void initialize(String username) {
        instance = new BridgefyListener(new Bus());
        instance.setUsername(username);
    }

    static void release() {
        instance = null;
    }


    /**
     *      BRIDGEFY LISTENER IMPLEMENTATIONS
     */
    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(Device device, Session session) {
            // we present ourselves to the user // TODO status is not always free
            device.sendMessage(Player.create(username, Player.STATUS_FREE));
        }

        @Override
        public void onStarted() {
            Log.i(TAG, "onStarted()");
        }

        @Override
        public void onStartError(String s, int i) {
            Log.e(TAG, s);
        }

        @Override
        public void onStopped() {
            Log.w(TAG, "onStopped()");
        }
    };

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            // recreate the player object from the incoming message
            // post the found object to our activities via the Otto plugin
            // (the Otto plugin is not a part of the Bridgefy framework)
            ottoBus.post(Player.create(message));
        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            // TODO check if it's a move of our current match, otherwise:
            // TODO display the invitation dialog if we're available to play OR:
            // TODO respond with a REFUSE_MATCH object
        }
    };


    /**
     *      GETTERS
     */

    static Bus getOttoBus() {
        return instance.ottoBus;
    }

    static StateListener getStateListener() {
        return instance.stateListener;
    }

    static MessageListener getMessageListener() {
        return instance.messageListener;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
