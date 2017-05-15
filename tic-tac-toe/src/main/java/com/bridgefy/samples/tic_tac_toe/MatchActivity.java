package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bridgefy.samples.tic_tac_toe.entities.Move;
import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.samples.tic_tac_toe.entities.RefuseMatch;
import com.bridgefy.sdk.client.Bridgefy;
import com.squareup.otto.Subscribe;

public class MatchActivity extends TicTacToeActivity {

    private static final String TAG = "MatchActivity";

    // use this variable to automatically reject incoming match requests when already playing
    // this variable will be rendered useless when the sample app supports multi-match
    public static String currentMatchId;

    // the Player object that represents our rival
    private Player player;

    private int sequence = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our player object and set a matchId
        player = Player.create(getIntent().getStringExtra(Constants.INTENT_EXTRA_PLAYER));

        // TODO make this fixed
        currentMatchId = "temporary.match.id";
        Log.d(TAG, "Starting Match with: " + player.getNick());
        Log.d(TAG, "            matchId: " + currentMatchId);

        // register this activity on the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().register(this);
    }

    @Override
    protected void onDestroy() {
        // unregister this activity from the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().unregister(this);

        if (isFinishing())
            currentMatchId = null;

        super.onDestroy();
    }

    // TODO add menu option to leave match (RefuseMatch object)

    @Override
    void sendMove(char[][] board) {
        myTurn = false;
        Move move = new Move(currentMatchId, ++sequence, board);
        Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(move.toHashMap()));
    }


    /**
     *      OTTO EVENT BUS LISTENER
     *      These events are managed via the Otto plugin, which is not a part of the Bridgefy framework.
     */
    @Subscribe
    public void onMoveReceived(Move move) {
        // work only with Move objects from our current match
        if (move.getMatchId().equals(currentMatchId)) {
            if (move.getSequence() > sequence) {
                Log.d(TAG, "Move received for matchId: " + move.getMatchId());
                Log.d(TAG, "... " + move.toString());

                // if the sequence is an odd number, it means that the other player started the match
                if (move.getSequence()%2 != 0)
                    turn = O;
                else
                    turn = X;

                // move the board
                myTurn = true;
                resetBoard(move.getBoard());

                // update the sequence value
                this.sequence = move.getSequence();
            } else {
                Log.w(TAG, "Dumping Move object with an expired seq.");
                Log.w(TAG, "... " + move.toString());
            }
        }
    }

    @Subscribe
    public void onMatchRefused(RefuseMatch refuseMatch) {
        Log.d(TAG, "RefuseMatch received [matchId: " + refuseMatch.getMatchId() + "]");
        if (refuseMatch.getMatchId().equals(currentMatchId)) {
            Toast.makeText(getBaseContext(),
                                String.format(getString(R.string.match_rejected), player.getNick()),
                                Toast.LENGTH_SHORT).show();
        }
    }


    public static String getMatchId() {
        return currentMatchId;
    }
}
