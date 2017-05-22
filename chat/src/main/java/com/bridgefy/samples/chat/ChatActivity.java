package com.bridgefy.samples.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bridgefy.samples.chat.entities.Peer;
import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bridgefy.samples.chat.MainActivity.INTENT_EXTRA_PEER;

public class ChatActivity extends AppCompatActivity {

    final int INCOMING_MESSAGE = 0;
    final int OUTGOING_MESSAGE = 1;

    private Peer peer;

    @BindView(R.id.txtMessage)
    EditText txtMessage;

    MessagesRecyclerViewAdapter messagesAdapter =
            new MessagesRecyclerViewAdapter(new ArrayList<Pair<Integer,String>>());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        // recover our Peer object
        String peerString = getIntent().getStringExtra(INTENT_EXTRA_PEER);
        peer = Peer.create(peerString);

        // register the receiver to listen for incoming messages
        LocalBroadcastManager.getInstance(getBaseContext())
                .registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String incomingMessage = intent.getStringExtra(MainActivity.INTENT_EXTRA_MESSAGE);
                        messagesAdapter.addMessage(INCOMING_MESSAGE, incomingMessage);
                    }
                }, new IntentFilter(peer.getUuid()));

        // Configure the action bar
        setTitle(peer.getDeviceName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // configure the recyclerview
        RecyclerView messagesRecyclerView = (RecyclerView) findViewById(R.id.message_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        messagesRecyclerView.setLayoutManager(mLinearLayoutManager);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @OnClick({R.id.btnSend})
    public void onMessageSend(View v) {
        // get the message and push it to the views
        String message = txtMessage.getText().toString();
        if (message.trim().length() > 0) {
            txtMessage.setText("");
            messagesAdapter.addMessage(OUTGOING_MESSAGE, message);

            // send message text to device
            HashMap<String, Object> content = new HashMap<>();
            content.put("text", message);
            Bridgefy.sendMessage(
                    Bridgefy.createMessage(peer.getUuid(), content),
                    BFEngineProfile.BFConfigProfileLongReach);
        }
    }

    public class MessagesRecyclerViewAdapter
            extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.MessageViewHolder> {

        private final List<Pair<Integer, String>> messages;


        public MessagesRecyclerViewAdapter(List<Pair <Integer, String>> messages) {
            this.messages = messages;
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        void addMessage(int mine, String message) {
            messages.add(0, new Pair<>(mine, message));
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return messages.get(position).first;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View messageView = null;

            switch (viewType) {
                case INCOMING_MESSAGE:
                    messageView = LayoutInflater.from(viewGroup.getContext()).
                            inflate((R.layout.message_row_incoming), viewGroup, false);
                    break;
                case OUTGOING_MESSAGE:
                    messageView = LayoutInflater.from(viewGroup.getContext()).
                            inflate((R.layout.message_row_outgoing), viewGroup, false);
                    break;
            }

            return new MessageViewHolder(messageView);
        }

        @Override
        public void onBindViewHolder(final MessageViewHolder messageHolder, int position) {
            messageHolder.setMessage(messages.get(position));
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            final TextView txtMessage;
            Pair<Integer, String> message;

            MessageViewHolder(View view) {
                super(view);
                txtMessage = (TextView) view.findViewById(R.id.txtMessage);
            }

            void setMessage(Pair<Integer, String> message) {
                this.message = message;
                this.txtMessage.setText(message.second);
            }
        }
    }
}
