package com.bridgefy.samples.chatify;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.DeviceListener;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.framework.controller.Session;
import com.bridgefy.sdk.framework.entities.ForwardPacket;
import com.bridgefy.sdk.framework.exceptions.MessageException;
import com.bridgefy.sdk.framework.network.ConnectionType;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DevicesActivity extends AppCompatActivity implements
        DeviceListener, MessageListener, Bridgefy.RegistrationListener {

    private final String TAG = "DevicesActivity";

    @BindView(R.id.devices_recycler_view)
    RecyclerView    devicesRecyclerView;
    DevicesAdapter  devicesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        ButterKnife.bind(this);

        // initialize the DevicesAdapter and the RecyclerView
        devicesAdapter = new DevicesAdapter();
        devicesRecyclerView.setAdapter(devicesAdapter);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // check that we have Location permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeBridgefy();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 0);
        }
    }


    /**
     *      BRIDGEFY REGISTRATION LISTENERS
     */
    @Override
    public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
        Log.i(TAG, "onRegistrationSuccessful: current userId is: " + bridgefyClient.getUserUuid());
        Log.i(TAG, "Device Rating " + bridgefyClient.getDeviceProfile().getRating());
        Log.i(TAG, "Device Evaluation " + bridgefyClient.getDeviceProfile().getDeviceEvaluation());

        // Start the Bridgefy SDK
        Bridgefy.start(this, this);
    }

    @Override
    public void onRegistrationFailed(int errorCode, String message) {
        Log.e(TAG, "onRegistrationFailed: failed with ERROR_CODE: " + errorCode + ", MESSAGE: " + message);
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DevicesActivity.this, "Bridgefy registration did not succeed.", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     *      BRIDGEFY WORKFLOW LISTENERS
     */
    @Override
    public void onDeviceConnected(Device device, ConnectionType connectionType) {
        Log.i(TAG, "Device found: " + device.getUserId());
        broadcastPresence();
    }

    @Override
    public void onDeviceLost(Device device, ConnectionType deviceType) {
        Log.w(TAG, "Device lost: " + device.getUserId());
    }

    @Override
    public void onMessageReceived(Session session, Message message) {
        String s = message.getContent().get("manufacturer ") + " " + message.getContent().get("model");
        Log.d(TAG, "Message Received: " + session.getDevice().getUserId() + ", content: " + s);
        devicesAdapter.addDevice(s);
    }

    @Override
    public void onMessageFailed(Message message, MessageException e) {
        Log.e(TAG, "Message failed", e);
    }

    @Override
    public void onBroadcastMessageReceived(Message message) {
        String s = message.getContent().get("manufacturer ") + " " + message.getContent().get("model");
        Log.d(TAG, "Message Received: content: " + s);
        if (devicesAdapter.addDevice(s))
            broadcastPresence();
            
    }

    @Override
    public void onMessageSent(Session session, Message message) {
        Log.d(TAG, "Message sent to: " + session.getUuid());
    }

    @Override
    public void onMessageReceivedException(ForwardPacket forwardPacket, MessageException e) {
        Log.e(TAG, e.getMessage());
    }


    /**
     *      OTHER STUFF
     */
    private void broadcastPresence() {
        // construir objeto de mensaje
        HashMap<String, Object> data = new HashMap<>();
        data.put("manufacturer ",Build.MANUFACTURER);
        data.put("model", Build.MODEL);

        // since this is a broadcast message, it's not necessary to specify a receiver
        Message message = Bridgefy.createMessage(null, data);
        Bridgefy.sendBroadcastMessage(message);
    }
    
    private void initializeBridgefy() {
        Bridgefy.initialize(getApplicationContext(), this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeBridgefy();
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, "Location permissions needed to start devices discovery.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public class DevicesAdapter extends RecyclerView.Adapter<DeviceViewHolder> {
        // the list that holds our incoming devices
        ArrayList<String> devices;

        DevicesAdapter() {
            devices = new ArrayList<>();
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        boolean addDevice(String device) {
            if (!devices.contains(device)) {
                devices.add(device);
                notifyItemInserted(devices.size() - 1);
                return true;
            }
            
            return false;
        }

        void removeDevice(Device device) {
            int position = devices.indexOf(device);
            if (position > -1) {
                devices.remove(position);
                notifyItemRemoved(position);
            }
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
        @BindView(R.id.txt_device)
        TextView deviceView;

        DeviceViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void setDevice(String device) {
            deviceView.setText(device);
        }
    }
}
