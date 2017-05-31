package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bridgefy.samples.tic_tac_toe.entities.Move;
import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.samples.tic_tac_toe.entities.RefuseMatch;
import com.bridgefy.sdk.client.Bridgefy;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.UUID;

import butterknife.OnClick;

public class MatchActivity extends TicTacToeActivity {

    public static final String TAG = "MatchActivity";

    // a globaly available variable that identifies the current match
    public static String matchId;

    // a Player object representing our rival
    private Player player;

    // a Participants object created
    HashMap<Character, String> participants;

    private int sequence = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get our player object and set a matchId
        player = Player.create(getIntent().getStringExtra(Constants.INTENT_EXTRA_PLAYER));

        // create our participants object for the Move message
        participants = new HashMap<>();
        participants.put(X, BridgefyListener.getUuid());
        participants.put(O, player.getUuid());

        // check if this Match was started with a corresponding matchId
        Move move = Move.create(getIntent().getStringExtra(Constants.INTENT_EXTRA_MOVE));
        if (move != null)
            onMoveReceived(move);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setTitle(player.getNick());

        // register this activity on the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().register(this);
    }

    @Override
    protected void onDestroy() {
        // unregister this activity from the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().unregister(this);

        if (isFinishing()) {
            Log.w(TAG, "Setting matchId to null");
            matchId = null;
        }

        super.onDestroy();
    }

    @Override
    void sendMove(int[][] board) {
        // generate this game's match Id
        if (matchId == null) {
            matchId = generateMatchId();
            Log.d(TAG, "Starting Match with: " + player.getNick());
            Log.d(TAG, "            matchId: " + matchId);
        }

        // create our Move object
        Move move = new Move(matchId, ++sequence, board);
        move.setParticipants(participants);
        Log.d(TAG, "Sending Move for matchId: " + matchId);
        Log.d(TAG, "... " + move.toString());

        // preserve the Move locally and send it as a message
        onMoveReceived(move);
        Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(move.toHashMap()));
    }

    @Override
    void sendWinner() {
        tv_turn.setText("You win!");

        // create the move
        Move move = new Move(matchId, ++sequence, board);
        move.setParticipants(participants);
        move.setWinner(BridgefyListener.getUuid());

        // log
        Log.d(TAG, "Sending Move for matchId: " + matchId);
        Log.d(TAG, "... " + move.toString());

        // preserve the Move locally and send it as a message
        onMoveReceived(move);
        Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(move.toHashMap()));
    }

    @OnClick({R.id.button_new_match})
    void newMatch() {
        // clear the board
        btnNewMatch.setVisibility(View.GONE);
        initializeBoard();
    }


    /**
     *      OTTO EVENT BUS LISTENER
     *      These events are managed via the Otto plugin, which is not a part of the Bridgefy framework.
     */
    @Subscribe
    public void onMoveReceived(Move move) {
        // work only with Move objects from our current match or if a match hasn't been set yet
        if (move.getMatchId().equals(matchId) || matchId == null) {
            if (move.getSequence() > sequence) {
                // get a reference to our matchId
                if (matchId == null) {
                    matchId = move.getMatchId();
                    Log.d(TAG, "Starting Match with: " + player.getNick());
                    Log.d(TAG, "            matchId: " + matchId);
                } else {
                    Log.d(TAG, "Move received for matchId: " + move.getMatchId());
                    Log.d(TAG, "... " + move.toString());
                }

                // enable the controls again if they had stopped before
                if (matchStopped) {
                    initializeBoard();
                    matchStopped = false;
                }

                if (move.getWinner() == null) {
                    // if the other player started the match, switch the symbols
                    if (move.getSequence() % 2 == 1) {
                        turn = O;
                        myTurnChar = O;
                        participants.put(O, BridgefyListener.getUuid());
                        participants.put(X, player.getUuid());
                    }
                    tv_turn.setText("Your turn");
                } else {
                    tv_turn.setText(turn + " Wins!");
                    stopMatch(true);
                }

                // switch the turn
                myTurn = true;
                turn = myTurnChar;

                // set the board to its current status and update the sequence
                resetBoard(move.getBoard());
                this.sequence = move.getSequence();
            } else {
                Log.w(TAG, "Dumping Move object with an expired seq.");
                Log.w(TAG, "... " + move.toString());
            }
        }
    }

    @Subscribe
    public void onMatchRefused(RefuseMatch refuseMatch) {
        Log.d(TAG, "RefuseMatch received for matchId: " + refuseMatch.getMatchId());
        if (refuseMatch.getMatchId().equals(matchId)) {
            Toast.makeText(getBaseContext(),
                                String.format(getString(R.string.match_rejected), player.getNick()),
                                Toast.LENGTH_LONG).show();
            finish();
        }
    }


    public static String getCurrentMatchId() {
        return matchId;
    }

    private String generateMatchId() {
        return UUID.randomUUID().toString().substring(0, 5);
    }
}
