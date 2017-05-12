package com.bridgefy.samples.tic_tac_toe.entities;

import java.util.HashMap;

/**
 * @author dekaru on 5/9/17.
 */

public class Move {

    String mid;
    HashMap<String, String> participants;
    int[][] board = new int[3][3];
    int seq;
    String winner;
}
