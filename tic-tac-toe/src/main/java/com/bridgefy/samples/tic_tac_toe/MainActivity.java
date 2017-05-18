package com.bridgefy.samples.tic_tac_toe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.samples.tic_tac_toe.entities.Move;
import com.bridgefy.samples.tic_tac_toe.entities.Player;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.RegistrationListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private boolean isRegistered = false;
    String username;

    @BindView(R.id.users_toolbar)
    Toolbar toolbar;
    @BindView(R.id.players_recycler_view)
    RecyclerView playersRecyclerView;
    PlayersAdapter playersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // load our username
        username = getSharedPreferences(Constants.PREFS_NAME, 0).getString(Constants.PREFS_USERNAME, null);

        // check that we have permissions, otherwise fire the IntroActivity
        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (username == null)) {
            startActivity(new Intent(getBaseContext(), IntroActivity.class));
            finish();
        } else {
            // initialize the Bridgefy framework
            Bridgefy.initialize(getBaseContext(), registrationListener);

            // initialize the PlayersAdapter and the RecyclerView
            playersAdapter = new PlayersAdapter();
            playersRecyclerView.setAdapter(playersAdapter);
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            playersRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL));
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
        playersAdapter.addPlayer(player);
    }

    @Subscribe
    public void onPlayerLost(Device player) {
        // The Player.uuid field is created with the first 5 digits of the Device.uuid field
        Log.w(TAG, "Player lost: " + player.getUserId());
        playersAdapter.removePlayer(player.getUserId());
    }

    @Subscribe
    public void onMoveReceived(Move move) {
        Log.d(TAG, "Move received for matchId: " + move.getMatchId());
        Log.d(TAG, "... " + move.toString());

        // TODO show the Match if it hasn't been shown
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
            BridgefyListener.initialize(bridgefyClient.getUserUuid(), username);

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


    /**
     *      PLAYER ADAPTER CLASS
     */
    private class PlayersAdapter extends RecyclerView.Adapter<PlayerViewHolder> {
        // the list that holds our incoming players
        ArrayList<Player> players;

        PlayersAdapter() {
            players = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return players.size();
        }

        boolean addPlayer(Player player) {
            if (!players.contains(player)) {
                players.add(player);
                notifyItemInserted(players.size() - 1);
                return true;
            }

            return false;
        }

        void removePlayer(String playerId) {
            for (int i = 0; i <= players.size(); i++) {
                if (players.get(i).getUuid().equals(playerId)) {
                    players.remove(i);
                    notifyItemRemoved(i);
                }
            }
        }

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View playerView = LayoutInflater.from(viewGroup.getContext()).
                    inflate((R.layout.player_row), viewGroup, false);
            return new PlayerViewHolder(playerView);
        }

        @Override
        public void onBindViewHolder(PlayerViewHolder playerViewHolder, int position) {
            playerViewHolder.setPlayer(players.get(position));
        }
    }

    class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.txt_player)
        TextView playerView;

        Player player;

        PlayerViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        void setPlayer(Player player) {
            this.player = player;
            playerView.setText(player.getNick());
        }

        @Override
        public void onClick(View v) {
            startActivity(
                    new Intent(getBaseContext(), MatchActivity.class)
                            .putExtra(Constants.INTENT_EXTRA_PLAYER, player.toString()));
        }
    }
}
