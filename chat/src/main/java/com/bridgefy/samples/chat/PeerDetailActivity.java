package com.bridgefy.samples.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.samples.chat.entities.Peer;
import com.bridgefy.sdk.client.Bridgefy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.bridgefy.samples.chat.PeerListActivity.INTENT_EXTRA_PEER;

public class PeerDetailActivity extends AppCompatActivity {

    private Peer peer;

    MessagesRecyclerViewAdapter messagesAdapter =
            new MessagesRecyclerViewAdapter(new ArrayList<Pair<String,String>>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // recover our Peer object
        String peerString = getIntent().getStringExtra(INTENT_EXTRA_PEER);
        peer = Peer.create(peerString);
        setTitle(peer.getDeviceName());

        // register the receiver to listen for incoming messages
        LocalBroadcastManager.getInstance(getBaseContext())
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String incomingMessage = intent.getStringExtra(PeerListActivity.INTENT_EXTRA_MESSAGE);
                        messagesAdapter.addMessage(
                                peer.getDeviceName(),
                                incomingMessage);

                        Toast.makeText(getBaseContext(), incomingMessage, Toast.LENGTH_LONG).show();
                    }
                }, new IntentFilter(peer.getUuid()));

        // set the action of the fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send message text to device
                HashMap<String, Object> content = new HashMap<>();
                String message = Build.MANUFACTURER + " " + Build.MODEL;
                content.put("text", message);
                Bridgefy.sendMessage(
                        Bridgefy.createMessage(peer.getUuid(), content));
                messagesAdapter.addMessage(
                        "Me",
                        message);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        if (savedInstanceState == null) {
//            // Create the detail fragment and add it to the activity
//            // using a fragment transaction.
//            Bundle arguments = new Bundle();
//            arguments.putString(INTENT_EXTRA_PEER,
//                    getIntent().getStringExtra(INTENT_EXTRA_PEER));
//            PeerDetailFragment fragment = new PeerDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.peer_detail_container, fragment)
//                    .commit();
//        }

        // set the layout manager for the messageListView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.peer_detail_container);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
    }


    public class MessagesRecyclerViewAdapter
            extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageViewHolder> {

        private final List<Pair<String, String>> messages;


        public MessagesRecyclerViewAdapter(List<Pair <String, String>> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        void addMessage(String sender, String message) {
            messages.add(new Pair<>(sender, message));
            notifyItemInserted(messages.size() - 1);
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.peer_row, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MessageViewHolder messageHolder, int position) {
            messageHolder.setMessage(messages.get(position));
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            final TextView txtMessage;
            final TextView txtSender;
            Pair<String, String> message;

            MessageViewHolder(View view) {
                super(view);
                txtMessage = (TextView) view.findViewById(R.id.message);
                txtSender  = (TextView) view.findViewById(R.id.sender);
            }

            void setMessage(Pair<String, String> message) {
                this.message = message;
                this.txtSender.setText(message.first + ": ");
                this.txtMessage.setText(message.second);
            }
        }
    }
}
