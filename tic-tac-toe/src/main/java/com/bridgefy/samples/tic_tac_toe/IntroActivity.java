package com.bridgefy.samples.tic_tac_toe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.RegistrationListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroActivity extends AppCompatActivity {

    private final static String TAG = "IntroActivity";

    // TODO ask for permissions

    @BindView(R.id.text_username)
    public EditText txtUsername;

    @BindView(R.id.progressBar)
    public ProgressBar progressBar;

    @BindView(R.id.button_start)
    public Button buttonStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_start})
    public void onButtonStart(View view) {
        // save our username
        getSharedPreferences(Constants.PREFS_NAME, 0).edit().putString(Constants.PREFS_USERNAME,
                txtUsername.getText().toString()).apply();

        // show a progressbar
        progressBar.setVisibility(View.VISIBLE);
        buttonStart.setVisibility(View.GONE);

        // initialize the sdk
        Bridgefy.initialize(getBaseContext(), registrationListener);
    }


    /**
     *      REGISTRATION LISTENER
     */
    private RegistrationListener registrationListener = new RegistrationListener() {
        @Override
        public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
            Log.i(TAG, "onRegistrationSuccessful");
            startActivity(new Intent(getBaseContext(), MainActivity.class));
        }

        @Override
        public void onRegistrationFailed(int i, String s) {
            Log.w(TAG, "onRegistrationFailed: " + s);
            Toast.makeText(getBaseContext(), getString(R.string.error), Toast.LENGTH_LONG).show();
            // hide the progressbar
            progressBar.setVisibility(View.GONE);
            buttonStart.setVisibility(View.VISIBLE);
        }
    };
}