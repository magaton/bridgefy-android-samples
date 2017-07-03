package com.bridgefy.samples.remotecontrol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.StateListener;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Unbinder;
import me.roberto.remotecontrol.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int REQUEST_CODE = 256;
    private static final String TEXT_LABEL = "text";
    private static final String COMMAND_LABEL = "command";
    private static final String COLOR_LABEL = "color";
    private static final String IMAGE_LABEL = "image";
    private static final int COMMAND_TEXT = 0x4;
    private static final int COMMAND_FLASH = 0X3;
    private static final int COMMAND_COLOR = 0X2;
    private static final int COMMAND_IMAGE = 0X1;
    private Unbinder unbinder;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)

        {

            Bridgefy.start(messageListener,stateListener);
        }

    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */




    @BindView(R.id.fullscreen_content_controls)View mControlsView;
    @BindView(R.id.content) View mContentView;
    @BindView(R.id.flash_button)Button flashButton;
    @BindView(R.id.text_content)
    TextView textView;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private CameraManager manager;
    private boolean torchEnabled;
    private MessageListener messageListener=new MessageListener() {
        @Override
        public void onBroadcastMessageReceived(Message message) {
            super.onBroadcastMessageReceived(message);

            textView.setVisibility(View.GONE);


            HashMap content = message.getContent();
            switch ((Integer)content.get(COMMAND_LABEL))
            {
                case COMMAND_FLASH:
                    switchFlash();
                    break;

                case COMMAND_COLOR:
                    switchColor((Integer) content.get(COLOR_LABEL));
                    break;
                case COMMAND_IMAGE:
                    switch ((Integer) content.get(IMAGE_LABEL))
                    {
                        case 0:
                            mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_1));
                            break;
                        case 1:
                            mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_2));
                            break;
                        case 2:
                            mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_3));
                            break;
                    }
                    break;

                case COMMAND_TEXT:

                    mContentView.setBackgroundColor(Color.parseColor("#0099cc"));
                    String text = (String) content.get(TEXT_LABEL);
                    textView.setText(text);
                    textView.setVisibility(View.VISIBLE);


            }

        }
    };


    private com.bridgefy.sdk.client.StateListener stateListener=new StateListener() {
        @Override
        public void onStarted() {
            super.onStarted();

        }

        @Override
        public void onStartError(String message, int errorCode) {
            super.onStartError(message, errorCode);
            if (errorCode==StateListener.INSUFFICIENT_PERMISSIONS)
            {
                ActivityCompat.requestPermissions(FullscreenActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;

        unbinder = ButterKnife.bind(this);
        mControlsView = findViewById(R.id.fullscreen_content_controls);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            CameraManager.TorchCallback callback= new CameraManager.TorchCallback() {
                @Override
                public void onTorchModeUnavailable(@NonNull String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
                }

                @Override
                public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                    super.onTorchModeChanged(cameraId, enabled);

                    CameraCharacteristics characteristics = null;
                    try {
                        characteristics = manager.getCameraCharacteristics(cameraId);
                        int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                        if(cOrientation == CameraCharacteristics.LENS_FACING_BACK)
                        {

                            torchEnabled=enabled;
                        }
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }

                }
            };
            manager.registerTorchCallback(callback,null);
        }



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
       // findViewById(R.id.color_button).setOnTouchListener(mDelayHideTouchListener);

        delayedHide(0);


        Bridgefy.initialize(FullscreenActivity.this, "f344bd51-147b-4a86-b7f7-b0886efd6181", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                super.onRegistrationSuccessful(bridgefyClient);
                Bridgefy.start(messageListener,stateListener);
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                super.onRegistrationFailed(errorCode, message);
            }
        });
    }


    @OnLongClick(R.id.content)
    public boolean onClick(View view) {


        AlertDialog.Builder builder=new AlertDialog.Builder(FullscreenActivity.this);
        builder.setCancelable(true);
        builder.setMessage("Do you want to become an admin?");

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                toggle();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return true;
    }




    private void switchColor(int color) {

        mContentView.setBackgroundColor(color);

    }

    private void switchFlash()
    {
        try {
            for(final String cameraId : manager.getCameraIdList()){
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                int cOrientation = characteristics.get(CameraCharacteristics.LENS_FACING);
                if(cOrientation == CameraCharacteristics.LENS_FACING_BACK)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        manager.setTorchMode(cameraId,!torchEnabled);
                    }
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @OnClick({ R.id.flash_button, R.id.color_button, R.id.image_button, R.id.text_button })
    public void click(View view) {
        textView.setVisibility(View.GONE);


        switch (view.getId())
        {
            case R.id.flash_button:



                switchFlash();
                HashMap<String, Object> myData=new HashMap<>();
                myData.put(COMMAND_LABEL,COMMAND_FLASH);
                Message message=Bridgefy.createMessage(myData);
                Bridgefy.sendBroadcastMessage(message);

                break;






            case R.id.color_button:



                final ColorPicker cp = new ColorPicker(FullscreenActivity.this);
                cp.show();

                /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {

                        cp.dismiss();
                        switchColor (color);
                        HashMap<String, Object> myData=new HashMap<>();
                        myData.put(COMMAND_LABEL,COMMAND_COLOR);
                        myData.put(COLOR_LABEL,color);
                        Message message=Bridgefy.createMessage(myData);
                        Bridgefy.sendBroadcastMessage(message);

                    }
                });



                break;


            case R.id.image_button:

                AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this);
                builder.setTitle(R.string.pick_image)
                        .setItems(R.array.colors_array, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                HashMap<String, Object> myData=new HashMap<>();
                                myData.put(COMMAND_LABEL,COMMAND_IMAGE);


                                switch (which)
                                {
                                    case 0:
                                        mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_1));
                                        myData.put(IMAGE_LABEL,0);
                                        break;
                                    case 1:
                                        mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_2));
                                        myData.put(IMAGE_LABEL,1);
                                        break;
                                    case 2:
                                        mContentView.setBackground(getDrawable(R.drawable.lorem_ipsum_3));
                                        myData.put(IMAGE_LABEL,2);
                                        break;
                                }

                                Message message=Bridgefy.createMessage(myData);
                                Bridgefy.sendBroadcastMessage(message);

                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                break;

            case R.id.text_button:

                final EditText input = new EditText(FullscreenActivity.this);
                AlertDialog.Builder textBuilder=new AlertDialog.Builder(FullscreenActivity.this);
                textBuilder.setCancelable(true);
                textBuilder.setMessage("Write your text");

                // Add the buttons
                textBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String text = input.getText().toString();
                        textView.setText(text);

                        HashMap<String, Object> myData=new HashMap<>();
                        myData.put(COMMAND_LABEL,COMMAND_TEXT);
                        myData.put(TEXT_LABEL,text);
                        Message message=Bridgefy.createMessage(myData);
                        Bridgefy.sendBroadcastMessage(message);
                        textView.setVisibility(View.VISIBLE);
                        mContentView.setBackgroundColor(Color.parseColor("#0099cc"));

                    }
                });

                AlertDialog dialog = textBuilder.create();


                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                dialog.setView(input);


                 dialog.show();



    }
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }
}
