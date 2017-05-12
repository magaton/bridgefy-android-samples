package com.bridgefy.samples.tic_tac_toe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.RegistrationListener;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private boolean isRegistered = false;

    @BindView(R.id.users_toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // check that we have permissions, otherwise fire the IntroActivity
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(getBaseContext(), IntroActivity.class));
            finish();
        } else {
            // initialize the Bridgefy framework
            Bridgefy.initialize(getBaseContext(), registrationListener);
        }
    }

    @Override
    protected void onDestroy() {
        // check that the activity is actually finishing before freeing resources
        if (isRegistered && isFinishing()) {
            // unregister the Otto bus and free up resources
            BridgefyListener.getOttoBus().unregister(this);
            BridgefyListener.release();

            // stop bridgefy operations
            Bridgefy.stop();
        }

        super.onDestroy();
    }



    /**
     *      OTTO EVENT BUS LISTENER
     *      These events are managed via the Otto plugin, which is not a part of the Bridgefy framework.
     */
    @Subscribe
    public void onPlayerFound(Player player) {
        Log.d(TAG, "Player found: " + player.getNick());
    }


    /**
     *      BRIDGEFY REGISTRATION LISTENER
     */
    private RegistrationListener registrationListener = new RegistrationListener() {
        @Override
        public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
            Log.i(TAG, "onRegistrationSuccessful:");
            Log.i(TAG, "... Device Rating " + bridgefyClient.getDeviceProfile().getRating());
            Log.i(TAG, "... Device Evaluation " + bridgefyClient.getDeviceProfile().getDeviceEvaluation());

            // initialize our EventListener
            BridgefyListener.initialize(getIntent().getStringExtra(Constants.PREFS_USERNAME));

            // register this activity as a Bus listener
            BridgefyListener.getOttoBus().register(MainActivity.this);

            // set this activity as StateListener
            Bridgefy.start(
                    BridgefyListener.getMessageListener(),
                    BridgefyListener.getStateListener());

            isRegistered = true;
        }

        @Override
        public void onRegistrationFailed(int i, String s) {
            Log.w(TAG, "onRegistrationFailed: " + s);
            Toast.makeText(getBaseContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
            startActivity(new Intent(getBaseContext(), IntroActivity.class));
            finish();
        }
    };
}
