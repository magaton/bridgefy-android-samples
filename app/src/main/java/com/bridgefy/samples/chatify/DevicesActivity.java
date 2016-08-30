package com.bridgefy.samples.chatify;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.connectivity.controller.Session;
import com.bridgefy.sdk.connectivity.listeners.DeviceListener;
import com.bridgefy.sdk.connectivity.models.Device;
import com.bridgefy.sdk.connectivity.network.ConnectionType;
import com.bridgefy.sdk.messaging.entity.Message;
import com.bridgefy.sdk.messaging.exception.MessageException;
import com.bridgefy.sdk.messaging.listener.BroadcastMessageListener;
import com.bridgefy.sdk.messaging.listener.MessageListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DevicesActivity extends AppCompatActivity implements DeviceListener, MessageListener {

    private final String TAG = "DevicesActivity";

    @Bind(R.id.devices_recycler_view)
    RecyclerView    devicesRecyclerView;
    DevicesAdapter  devicesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers);
        ButterKnife.bind(this);

        // initialize bridgefy
        Bridgefy.initialize(getApplicationContext());
        Bridgefy.start(null, this);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // initialize the DevicesAdapter and the RecyclerView
        devicesRecyclerView.setAdapter(devicesAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        return super.onCreateView(parent, name, context, attrs);
    }


    /**
     *      BRIDGEFY LISTENERS
     */
    @Override
    public void onDeviceFound(Device device, ConnectionType deviceType) {
        Log.i(TAG, "Device found: " + device.getUuid());

        // enviar mensaje al dispositivo encontrado
        Bridgefy.sendMessage(device, Build.MANUFACTURER + " " + Build.MODEL);
    }

    @Override
    public void onDeviceLost(Device device, ConnectionType deviceType) {
        Log.w(TAG, "Device lost: " + device.getUuid());
        devicesAdapter.removeDevice(device);
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        Log.d(TAG, "Message Received: " + session.getDevice().getUuid() + ", content: " + new String(message.getContent()));
        Device device = session.getDevice();
        device.setDeviceName(new String(message.getContent()));
        devicesAdapter.addDevice(device);
    }

    @Override
    public void onMessageFailed(Message message, MessageException e) {
        Log.e(TAG, "Message failed", e);
    }

    @Override
    public void onMessageSent(Session session, Message message) {
        Log.d(TAG, "Message sent to: " + session.getUuid() + ", content: " + new String(message.getContent()));
    }

    @Override
    public void onMessageDelivered(Session session, Message message) {
        Log.v(TAG, "Message delivered to: " + session.getUuid() + ", content: " + new String(message.getContent()));
    }

    public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
        // the list that holds our incoming devices
        ArrayList<Device> devices;

        @Override
        public int getItemCount() {
            return devices.size();
        }

        void addDevice(Device device) {
            if (!devices.contains(device)) {
                devices.add(device);
                notifyItemInserted(devices.size() - 1);
            }
        }

        void removeDevice(Device device) {
            int position = devices.indexOf(device);
            devices.remove(position);
            notifyItemRemoved(position);
        }

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View deviceView = LayoutInflater.from(viewGroup.getContext()).
                    inflate((R.layout.device_row), viewGroup, false);
            return new DeviceViewHolder(deviceView);
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int position) {
            deviceViewHolder.setDevice(devices.get(position));
        }
    }

    class DeviceViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.txt_device)
        TextView deviceView;

        DeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setDevice(Device device) {
            deviceView.setText(device.getUuid());
        }
    }
}
