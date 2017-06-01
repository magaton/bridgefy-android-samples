package com.bridgefy.samples.tic_tac_toe.entities;

/**
 * @author dekaru on 5/9/17.
 */

public class Participants {

    private String X;
    private String O;

    public Participants(String X, String O) {
        this.X = X;
        this.O = O;
    }


    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getO() {
        return O;
    }

    public void setO(String o) {
        O = o;
    }
}
