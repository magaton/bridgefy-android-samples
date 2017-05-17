package com.bridgefy.samples.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEnergyProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Config;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An activity representing a list of peers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PeerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PeerListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private ArrayList<Device> devices=new ArrayList<>();
    SimpleItemRecyclerViewAdapter adapter=new SimpleItemRecyclerViewAdapter(devices);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

        View recyclerView = findViewById(R.id.peer_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.peer_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        Bridgefy.initialize(getApplicationContext(), new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                super.onRegistrationSuccessful(bridgefyClient);

                Config config=new Config.Builder().setEncryption(true)
                        .setEnergyProfile(BFEnergyProfile.BALANCED).build();
                Bridgefy.start(messageListener,stateListener,config);



            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                super.onRegistrationFailed(errorCode, message);
            }
        });







    }



    private MessageListener messageListener=new MessageListener() {
        @Override
        public void onMessageReceived(Message message) {
            super.onMessageReceived(message);

        }




        @Override
        public void onBroadcastMessageReceived(Message message) {
            super.onBroadcastMessageReceived(message);
        }
    };


    private String TAG="CHAT";
    StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {
            super.onDeviceConnected(device, session);


            HashMap<String, Object> content=new HashMap<>();
            device.sendMessage(content);

            HashMap<String, Object> map = new HashMap<>();

            map.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);

            device.sendMessage(map);


        }

        @Override
        public void onDeviceLost(Device device) {
            super.onDeviceLost(device);

        }


        @Override
        public void onStarted() {
            super.onStarted();
        }

        @Override
        public void onStartError(String message, int errorCode) {
            super.onStartError(message, errorCode);
            Log.e(TAG, "onStartError: "+message );

            if (errorCode==StateListener.INSUFFICIENT_PERMISSIONS)
            {

                ActivityCompat.requestPermissions(PeerListActivity.this,
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
            }


        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Bridgefy.start(messageListener,stateListener);
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permissions needed to start devices discovery.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Device> devices;

        public SimpleItemRecyclerViewAdapter(List<Device> items) {
            devices = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.peer_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.device = devices.get(position);
            holder.mContentView.setText(devices.get(position).getUserId());
            //holder.mContentView.setText(devices.get(position).);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(PeerDetailFragment.ARG_ITEM_ID, holder.device.getUserId());
                        PeerDetailFragment fragment = new PeerDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.peer_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, PeerDetailActivity.class);
                        //intent.putExtra(PeerDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Device device;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
