package com.bridgefy.samples.tic_tac_toe.entities;

import com.bridgefy.samples.tic_tac_toe.BridgefyListener;
import com.bridgefy.samples.tic_tac_toe.TicTacToeActivity;
import com.bridgefy.sdk.client.Message;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class Move {

    String mid;
    HashMap<Character, String> participants;
    int[][] board = new int[3][3];
    int seq;
    int winner;


    public Move(String mid, int seq, int[][] board) {
        this.mid = mid;
        this.seq = seq;
        this.board = board;
    }

    public HashMap<String, Object> toHashMap() {
        return new Event<>(
                Event.EventType.MOVE_EVENT,
                this).toHashMap();
    }

    public static Move create(Message message) {
        return new Gson().fromJson(
                new Gson().toJson(message.getContent().get("content")),
                Move.class);
    }

    public static Move create(String json) {
        return new Gson().fromJson(json, Move.class);
    }

    /**
     * @return The UUID String of the user playing against the current user. Null if myMatch() is false.
     */
    public String getOtherUuid() {
        if (myMatch())
            return BridgefyListener.getUuid().equals(participants.get(TicTacToeActivity.O)) ?
                    participants.get(TicTacToeActivity.X) : participants.get(TicTacToeActivity.O);
        else
            return null;
    }

    /**
     * @return True if this Move belongs to a Match that belongs to the current user
     */
    public boolean myMatch() {
        return (BridgefyListener.getUuid().equals(participants.get(TicTacToeActivity.O)) ||
                BridgefyListener.getUuid().equals(participants.get(TicTacToeActivity.X)));
    }

    public void setParticipants(HashMap<Character, String> participants) {
        this.participants = participants;
    }

    public HashMap<Character, String> getParticipants() {
        return participants;
    }

    public int getSequence() {
        return seq;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getWinner() {
        return winner;
    }

    public String getMatchId() {
        return mid;
    }

    public int[][] getBoard() {
        return board;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
