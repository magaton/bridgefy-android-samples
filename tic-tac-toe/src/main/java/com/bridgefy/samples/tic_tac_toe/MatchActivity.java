package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bridgefy.samples.tic_tac_toe.entities.Move;
import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.samples.tic_tac_toe.entities.RefuseMatch;
import com.bridgefy.sdk.client.Bridgefy;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

public class MatchActivity extends TicTacToeActivity {

    private static final String TAG = "MatchActivity";

    // a globaly available variable that identifies the current match
    public static String currentMatchId;

    // a unique string identifier for this match
    public String matchId;

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

        // generate our permanent matchId
        matchId = generateMatchId(BridgefyListener.getUuid(), player.getUuid());

        // register this activity on the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().register(this);

        // create our participants object for the Move message
        participants = new HashMap<>();
        participants.put(X, BridgefyListener.getUuid());
        participants.put(O, player.getUuid());

        Log.d(TAG, "Starting Match with: " + player.getNick());
        Log.d(TAG, "            matchId: " + matchId);
    }

    @Override
    protected void onDestroy() {
        // unregister this activity from the Otto plugin (not a part of the Bridgefy framework)
        BridgefyListener.getOttoBus().unregister(this);

        if (isFinishing())
            matchId = null;

        super.onDestroy();
    }

    @Override
    void sendMove(char[][] board) {
        Move move = new Move(matchId, ++sequence, board);
        move.setParticipants(participants);
        Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(move.toHashMap()));
    }

    @Override
    void setWinner(char w) {
        Move move = new Move(matchId, ++sequence, board);
        move.setParticipants(participants);
        move.setWinner(participants.get(w));
        Bridgefy.sendBroadcastMessage(Bridgefy.createMessage(move.toHashMap()));
    }


    /**
     *      OTTO EVENT BUS LISTENER
     *      These events are managed via the Otto plugin, which is not a part of the Bridgefy framework.
     */
    @Subscribe
    public void onMoveReceived(Move move) {
        // work only with Move objects from our current match
        if (move.getMatchId().equals(matchId)) {
            if (move.getSequence() > sequence) {
                Log.d(TAG, "Move received for matchId: " + move.getMatchId());
                Log.d(TAG, "... " + move.toString());

                if (move.getWinner() == null) {
                    // if the other player started the match, switch the symbols
                    if (move.getSequence() == 1) {
                        turn = O;
                        myTurnChar = O;
                        participants.put(O, BridgefyListener.getUuid());
                        participants.put(X, player.getUuid());
                    }

                    // move the board
                    myTurn = true;
                    turn = myTurnChar;

                    tv_turn.setText("Your turn");
                } else {
                    tv_turn.setText(turn + " Wins!");
                    stopMatch();
                }

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
        return currentMatchId;
    }

    private String generateMatchId(String u1, String u2) {
        // TODO matches are currently fixed, however they should have a random UUID as identifier
//        return UUID.randomUUID().toString();
        if (u1.charAt(0) > u2.charAt(0)) {
            return u1.substring(0, 5) + "-" + u2.substring(0, 5);
        } else {
            return u2.substring(0, 5) + "-" + u1.substring(0, 5);
        }
    }
}
