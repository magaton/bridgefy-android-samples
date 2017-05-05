package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;

public class MatchActivity extends TicTacToeActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the current activity as the MessageListener
        Bridgefy.setMessageListener(messageListener);
    }


    MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            // TODO make a move
        }

        @Override
        public void onMessageSent(Message message) {
            // TODO make a move
        }
    };
}
