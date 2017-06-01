package com.bridgefy.samples.tic_tac_toe;

import android.util.Log;

import com.bridgefy.samples.tic_tac_toe.entities.Event;
import com.bridgefy.samples.tic_tac_toe.entities.Move;
import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.samples.tic_tac_toe.entities.RefuseMatch;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

/**
 * @author dekaru on 5/10/17.
 */
public class BridgefyListener {

    private static final String TAG = "BridgefyListener";

    private static BridgefyListener instance;

    // This sample app uses the Otto Event Bus to communicate between app components easily.
    // The Otto plugin is not a part of the Bridgefy framework
    private Bus ottoBus;

    private String username;
    private String uuid;


    private BridgefyListener(Bus ottoBus) {
        this.ottoBus = ottoBus;
    }

    static void initialize(String uuid, String username) {
        instance = new BridgefyListener(new Bus());
        instance.setUuid(uuid);
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
            // send a handshake to nearby devices
            device.sendMessage(new Player(uuid, username).toHashMap());
        }

        @Override
        public void onDeviceLost(Device device) {
            // let our components know that a device is no longer in range
            ottoBus.post(device);
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
            // identify the type of incoming event
            Event.EventType eventType = extractType(message);
            switch (eventType) {
                case FIRST_MESSAGE:
                    // recreate the Player object from the incoming message
                    // post the found object to our activities via the Otto plugin
                    ottoBus.post(Player.create(message));
                    break;

                case REFUSE_MATCH:
                    // recreate the RefuseMatch object from the incoming message
                    // post the found object to our activities via the Otto plugin
                    ottoBus.post(RefuseMatch.create(message));

                    // let iPhone devices know we're available
                    Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(
                            new Event<>(
                                    Event.EventType.AVAILABLE,
                                    this).toHashMap()));
                    break;
            }
        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            // build a TicTacToe Move object from our incoming Bridgefy Message
            Event.EventType eventType = extractType(message);
            switch (eventType) {
                case MOVE_EVENT:
                    Move move = Move.create(message);
                    // log
                    Log.d(TAG, "Move received for matchId: " + move.getMatchId());
                    Log.d(TAG, "... " + move.toString());

                    // post this event via the Otto plugin so our components can update their views
                    ottoBus.post(move);
                    break;

                case AVAILABLE:
                    Log.d(TAG, "AVAILABLE event not implemented.");
                    break;

                default:
                    Log.d(TAG, "Unrecognized Event received: " +
                            new Gson().toJson(message.getContent().toString()));
                    break;
            }

            // TODO make moves persistent

            // if it's not a Move object from our current match, create a notification
//            if (!move.getMatchId().equals(MatchActivity.getCurrentMatchId())) {
//                // TODO create a notification for the incoming move
//            }
        }

        private Event.EventType extractType(Message message) {
            int eventOrdinal;
            Object eventObj = message.getContent().get("event");
            if (eventObj instanceof Double) {
                eventOrdinal = ((Double) eventObj).intValue();
            } else {
                eventOrdinal = (Integer) eventObj;
            }
            return Event.EventType.values()[eventOrdinal];
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

    public static String getUuid() {
        return instance.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
