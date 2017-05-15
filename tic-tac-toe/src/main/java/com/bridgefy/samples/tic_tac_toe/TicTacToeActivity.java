package com.bridgefy.samples.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class TicTacToeActivity extends AppCompatActivity {

    private int size;
    TableLayout mainBoard;
    TextView tv_turn;

    char[][] board;

    // first turn is 'X'
    char turn;
    char X = 'X';
    char O = 'O';
    boolean myTurn = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);

        // TODO receive a seed for the board representing the board status
        initializeBoard();
    }

    @OnClick(R.id.button_new_match)
    protected void newMatch() {
        Intent current = getIntent();
        finish();
        startActivity(current);
    }

    abstract void sendMove(char[][] board);

    protected void initializeBoard() {
        size = 3;
        board = new char[size][size];
        mainBoard = (TableLayout) findViewById(R.id.mainBoard);
        tv_turn = (TextView) findViewById(R.id.turn);

        turn = X;
        resetBoard(null);
        tv_turn.setText("Turn: " + turn);

        for (int i = 0; i < mainBoard.getChildCount(); i++) {
            TableRow row = (TableRow) mainBoard.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView tv = (TextView) row.getChildAt(j);
                tv.setOnClickListener(MoveListener(i, j, tv));
            }
        }
    }

    View.OnClickListener MoveListener(final int r, final int c, final TextView tv) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myTurn) {
                    if (!isCellSet(r, c)) {
                        // update the logic and visual board
                        board[r][c] = turn;
                        if (turn == X) {
                            tv.setText(R.string.X);
                            turn = O;
                        } else if (turn == O) {
                            tv.setText(R.string.O);
                            turn = X;
                        }

                        // send the move
                        sendMove(board);

                        // get the game status
                        if (gameStatus() == 0) {
                            tv_turn.setText("Turn: " + turn);
                        } else if (gameStatus() == -1) {
                            tv_turn.setText("Game: Draw");
                            stopMatch();
                        } else {
                            tv_turn.setText(turn + " Loses!");
                            stopMatch();
                        }
                    } else {
                        tv_turn.setText("Please choose a Cell Which is not already Occupied");
                    }
                } else {
                    tv_turn.setText("Wait for your turn!");
                }
            }
        };
    }

    protected boolean isCellSet(int r, int c) {
        return !(board[r][c] == ' ');
    }

    protected void stopMatch() {
        for (int i = 0; i < mainBoard.getChildCount(); i++) {
            TableRow row = (TableRow) mainBoard.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                TextView tv = (TextView) row.getChildAt(j);
                tv.setOnClickListener(null);
            }
        }
    }

    protected void resetBoard(char[][] board) {
        for (int i = 0; i < size; i++) {
            TableRow row = (TableRow) mainBoard.getChildAt(i);
            for (int j = 0; j < size; j++) {
                char c = board != null ? board[i][j] : ' ';
                this.board[i][j] = c;

                TextView tv = (TextView) row.getChildAt(j);
                switch (c) {
                    case 'X':
                        tv.setText(R.string.X);
                        break;
                    case 'O':
                        tv.setText(R.string.O);
                        break;
                    default:
                        tv.setText(R.string.none);
                        break;
                }
            }
        }
    }

    protected int gameStatus() {
        //0 Continue
        //1 X Wins
        //2 O Wins
        //-1 Draw

        int rowX = 0, colX = 0, rowO = 0, colO = 0;
        for (int i = 0; i < size; i++) {
            if (check_Row_Equality(i, 'X'))
                return 1;
            if (check_Column_Equality(i, 'X'))
                return 1;
            if (check_Row_Equality(i, 'O'))
                return 2;
            if (check_Column_Equality(i, 'O'))
                return 2;
            if (check_Diagonal('X'))
                return 1;
            if (check_Diagonal('O'))
                return 2;
        }

        boolean boardFull = true;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ')
                    boardFull = false;
            }
        }
        if (boardFull)
            return -1;
        else return 0;
    }

    protected boolean check_Diagonal(char player) {
        int count_Equal1 = 0, count_Equal2 = 0;
        for (int i = 0; i < size; i++)
            if (board[i][i] == player)
                count_Equal1++;
        for (int i = 0; i < size; i++)
            if (board[i][size - 1 - i] == player)
                count_Equal2++;

        return (count_Equal1 == size || count_Equal2 == size);
    }

    protected boolean check_Row_Equality(int r, char player) {
        int count_Equal = 0;
        for (int i = 0; i < size; i++) {
            if (board[r][i] == player)
                count_Equal++;
        }

        return (count_Equal == size);
    }

    protected boolean check_Column_Equality(int c, char player) {
        int count_Equal = 0;
        for (int i = 0; i < size; i++) {
            if (board[i][c] == player)
                count_Equal++;
        }

        return (count_Equal == size);
    }
}
