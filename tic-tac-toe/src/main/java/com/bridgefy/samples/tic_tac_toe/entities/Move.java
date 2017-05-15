package com.bridgefy.samples.tic_tac_toe.entities;

import com.bridgefy.sdk.client.Message;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class Move {

    String mid;
    HashMap<String, String> participants; // TODO
    char[][] board = new char[3][3];
    int seq;
    String winner;  // TODO


    public Move(String mid, int seq, char[][] board) {
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


    public void setParticipants(HashMap<String, String> participants) {
        this.participants = participants;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public int getSequence() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getMatchId() {
        return mid;
    }

    public char[][] getBoard() {
        return board;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
