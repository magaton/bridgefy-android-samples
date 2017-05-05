package com.bridgefy.samples.tic_tac_toe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";


    @BindView(R.id.users_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set this activity as StateListener
        Bridgefy.start(null, stateListener);
    }

    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(Device device, Session session) {
            // TODO show nearby user on recyclerview
        }

        @Override
        public void onDeviceLost(Device device) {
            // TODO hide nearby user on recyclerview
        }

        @Override
        public void onStarted() {
            Log.i(TAG, "onStarted()");
        }

        @Override
        public void onStartError(String s, int i) {
            Log.e(TAG, s);
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "onStopped()");
        }
    };
}
