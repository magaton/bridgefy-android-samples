package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;

public class MatchActivity extends TicTacToeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // register this activity as a Bus listener
        BridgefyListener.getOttoBus().register(this);
    }

    @Override
    protected void onDestroy() {
        // unregister this activity as a Bus listener
        BridgefyListener.getOttoBus().unregister(this);

        super.onDestroy();
    }
}
