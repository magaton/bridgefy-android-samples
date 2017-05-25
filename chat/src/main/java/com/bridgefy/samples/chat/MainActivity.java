package com.bridgefy.samples.chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.samples.chat.entities.Peer;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    static final String INTENT_EXTRA_NAME = "peerName";
    static final String INTENT_EXTRA_UUID = "peerUuid";
    static final String INTENT_EXTRA_TYPE = "deviceType";
    static final String INTENT_EXTRA_MSG  = "message";
    static final String BROADCAST_CHAT    = "Broadcast";

    final static int DEVICE_ANDROID = 0;
    final static int DEVICE_IPHONE  = 1;

    PeersRecyclerViewAdapter peersAdapter =
            new PeersRecyclerViewAdapter(new ArrayList<Peer>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.peer_list);
        recyclerView.setAdapter(peersAdapter);

        Bridgefy.initialize(getApplicationContext(), new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                // Start Bridgefy
                startBridgefy();
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                Toast.makeText(getBaseContext(), getString(R.string.registration_error),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isFinishing())
            Bridgefy.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_broadcast:
                startActivity(new Intent(getBaseContext(), ChatActivity.class)
                        .putExtra(INTENT_EXTRA_NAME, BROADCAST_CHAT)
                        .putExtra(INTENT_EXTRA_UUID, BROADCAST_CHAT));
                return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    /**
     *      BRIDGEFY METHODS
     */
    private void startBridgefy() {
        Bridgefy.start(messageListener, stateListener);
    }

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            super.onMessageReceived(message);

            // direct messages carrying a Device name represent device handshakes
            if (message.getContent().get("device_name") != null) {
                Peer peer = new Peer(message.getSenderId(),
                        (String) message.getContent().get("device_name"));
                peersAdapter.addPeer(peer);

            // any other direct message should be treated as such
            } else {
                String incomingMessage = (String) message.getContent().get("text");
                LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
                        new Intent(message.getSenderId())
                                .putExtra(INTENT_EXTRA_MSG, incomingMessage));
            }
        }

        @Override
        public void onBroadcastMessageReceived(Message message) {
            // we should not expect to have connected previously to the device that originated
            // the incoming broadcast message, so device information is included in this packet
            String incomingMsg = (String) message.getContent().get("text");
            String deviceName  = (String) message.getContent().get("device_name");
            int    deviceType  = (int) message.getContent().get("device_type");

            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(
                    new Intent(BROADCAST_CHAT)
                            .putExtra(INTENT_EXTRA_NAME, deviceName)
                            .putExtra(INTENT_EXTRA_TYPE, deviceType)
                            .putExtra(INTENT_EXTRA_MSG,  incomingMsg));
        }
    };

    StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {
            // send our information to the Device
            HashMap<String, Object> map = new HashMap<>();
            map.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            device.sendMessage(map);
        }

        @Override
        public void onDeviceLost(Device peer) {
//            peersAdapter.removePeer(peer.getUserId());
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e(TAG, "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // Start Bridgefy
            startBridgefy();

        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permissions needed to start peers discovery.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    /**
     *      RECYCLER VIEW CLASSES
     */
    class PeersRecyclerViewAdapter
            extends RecyclerView.Adapter<PeersRecyclerViewAdapter.PeerViewHolder> {

        private final List<Peer> peers;

        PeersRecyclerViewAdapter(List<Peer> peers) {
            this.peers = peers;
        }

        @Override
        public int getItemCount() {
            return peers.size();
        }

        void addPeer(Peer peer) {
            if (!peers.contains(peer)) {
                peers.add(peer);
                notifyItemInserted(peers.size() - 1);
            }
        }

//        void removePeer(String peerId) {
//            for (int i = 0; i <= peers.size(); i++) {
//                if (peers.get(i).getUuid().equals(peerId)) {
//                    peers.remove(i);
//                    notifyItemRemoved(i);
//                }
//            }
//        }
        
        @Override
        public PeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.peer_row, parent, false);
            return new PeerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final PeerViewHolder peerHolder, int position) {
            peerHolder.setPeer(peers.get(position));
        }

        class PeerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            final TextView mContentView;
            Peer peer;

            PeerViewHolder(View view) {
                super(view);
                mContentView = (TextView) view.findViewById(R.id.peerName);
                view.setOnClickListener(this);
            }

            void setPeer(Peer peer) {
                this.peer = peer;
                this.mContentView.setText(peer.getDeviceName());
            }

            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), ChatActivity.class)
                        .putExtra(INTENT_EXTRA_NAME, peer.getDeviceName())
                        .putExtra(INTENT_EXTRA_UUID, peer.getUuid()));
            }
        }
    }
}
