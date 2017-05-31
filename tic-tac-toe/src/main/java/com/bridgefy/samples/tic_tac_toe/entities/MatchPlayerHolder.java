package com.bridgefy.samples.tic_tac_toe.entities;

/**
 * @author dekaru on 5/9/17.
 */

public class MatchPlayerHolder {

    private Player player;
    private Move   move;


    public MatchPlayerHolder(Player player) {
        this.player = player;
    }

    public MatchPlayerHolder(Move move) {
        this.move = move;
    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Move getMove() {
        return move;
    }

    public String getMoveString() {
        return move != null ? move.toString() : null;
    }

    public String getMatchId() {
        return move != null ? move.getMatchId() : null;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
