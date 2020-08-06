package com.yez_inc.lock_me_app.lockmeapp.Widgets;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.yez_inc.lock_me_app.R;

public class Phone_State extends FrameLayout implements View.OnClickListener{
    Context context;
    static boolean isData = false,isForAddWidgets=false;
    LinearLayout flash,bluetooth,data,wifi;
    TextView audio;
    ConnectivityManager connectivityManager;
    StateReceiver stateReceiver;
    public Phone_State(Context context) {
        super(context);init();init();
    }
    public Phone_State(Context context, AttributeSet attrs) {
        super(context, attrs);init();
    }
    public Phone_State(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init();
    }
    public Phone_State(Context context,boolean forAddWidgets,StateReceiver state) {
        super(context);isForAddWidgets=forAddWidgets;stateReceiver=state;init();
        StartPhone_StateListener();
    }
    void init(){
        context=getContext();
        inflate(context, R.layout.widget_phone_state, this);
        flash = (LinearLayout) findViewById(R.id.flash);
        bluetooth = (LinearLayout) findViewById(R.id.bluetooth);
        data = (LinearLayout) findViewById(R.id.data);
        wifi = (LinearLayout) findViewById(R.id.wifi);
        audio = (TextView) findViewById(R.id.audio);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!isForAddWidgets){
        flash.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
        data.setOnClickListener(this);
        wifi.setOnClickListener(this);
        audio.setOnClickListener(this);
        }
    }
    void ConnectivityState() {
        if (stateReceiver.isWifiEnabled()) {
            wifi.setAlpha(1f);
        } else {
            wifi.setAlpha(0.4f);
        }
        // This controls isData connection
        try {
            Class<?> cmClass = Class.forName(connectivityManager.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            isData = (Boolean) method.invoke(connectivityManager); // get the setting for "mobile isData"
            if (isData) {data.setAlpha(1f);} else {data.setAlpha(0.4f);}
        } catch (Exception e) {e.printStackTrace();}
    }
    void toggle_data(){
        try {
            final Class<?> conmanClass = Class.forName(connectivityManager.getClass().getName());
            final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager2 = connectivityManagerField.get(connectivityManager);
            final Class<?> connectivityManagerClass =  Class.forName(connectivityManager2.getClass().getName());
            Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(connectivityManager2, !isData);
            isData =!isData;
            if (!isData) {
                data.setAlpha(0.4f);
                stateReceiver.ToastThis("DATA OFF");
            } else {
                data.setAlpha(1f);
                stateReceiver.ToastThis("DATA ON");
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    void  StartPhone_StateListener(){
        stateReceiver.setPhone_StateListener( new StateReceiver.Phone_StateListener() {
            @Override
            public void OnPhone_State_Changed(String action, int state) {
                switch (action)
                {
                    case "Audio":
                        switch (state) {
                            case 0:audio.setBackgroundResource(R.drawable.ic_volume_off);break;
                            case 1:audio.setBackgroundResource(R.drawable.ic_vibration);break;
                            case 2:audio.setBackgroundResource(R.drawable.ic_volume_up);break;
                        }break;
                    case "Bluetooth":
                        switch (state) {
                            case -1:bluetooth.setVisibility(View.GONE);break;
                            case 0:bluetooth.setAlpha(0.4f);break;
                            case 1:bluetooth.setAlpha(0.6f);break;
                            case 2:bluetooth.setAlpha(1f);break;
                            case 3:bluetooth.setAlpha(0.8f);break;
                        }break;
                    case "WIFI":
                        switch (state) {
                            case -1:wifi.setVisibility(GONE);break;
                            case 0:wifi.setAlpha(0.4f);break;
                            case 1:wifi.setAlpha(1f);break;
                        }break;
                    case "FLASH":
                        switch (state) {
                            case 0:flash.setAlpha(0.4f);break;
                            case 1:flash.setAlpha(1f);break;
                        }break;
                }
            }
        });
    }
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        stateReceiver.BluetoothState(-1,true);ConnectivityState();
    }
    @Override public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.flash : stateReceiver.toggleLight();break;
            case R.id.audio : stateReceiver.toggleAudio();break;
            case R.id.wifi : stateReceiver.toggleWifi();break;
            case R.id.bluetooth : stateReceiver.toggleBluetooth();break;
            case R.id.data : toggle_data();break;
        }
    }
}
