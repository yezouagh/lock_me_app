package com.yez_inc.lock_me_app.lockmeapp.Widgets;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;

public class StateReceiver {
    private Context context;
    private static boolean isLight=false,isLightOn = false;
    private boolean _isFirstTime=true;
    private static CameraManager camera2;
    @SuppressWarnings("deprecation")
    private static Camera camera;
    private int audio_State=2;
    @SuppressWarnings("deprecation")
    private static Camera.Parameters parameters;
    private static String camera_id;
    private BluetoothAdapter mBluetoothAdapter;
    private WifiManager wifiManager;
    private AudioManager audioManager;
    private LockListener listener;
    private Phone_StateListener phone_stateListener;
    interface Phone_StateListener {
        void OnPhone_State_Changed(String action,int state);
    }
    public StateReceiver(Context _context, LockListener _listener, Handler handler) {
        context = _context;
        listener = _listener;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(mIntentReceiver, filter, null, handler);
        initCamera();
    }
    public void ToastThis(String msg){
        if (listener!=null)
            listener.onLockInteraction(msg);
    }
    void setPhone_StateListener(Phone_StateListener phoneStateListener){
        phone_stateListener = phoneStateListener;
        if (mBluetoothAdapter == null) {
            sendPhone_State("Bluetooth",-1);
        }
        if(!isLight){sendPhone_State("FLASH",-1);}
    }
    private void sendPhone_State(String action,int state){
        if (phone_stateListener!=null)
            phone_stateListener.OnPhone_State_Changed(action,state);
    }
    public void onDetachedFromWindow() {
        context.unregisterReceiver(mIntentReceiver);
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context _context, Intent intent) {
            String action=intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int STATE = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.STATE_OFF);
                BluetoothState(STATE,false);
            }else if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)){
                int STATE = intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE,AudioManager.RINGER_MODE_NORMAL);
                AudioState(STATE);_isFirstTime=false;
            }
        }
    };

    private void AudioState(int state) {
        if (state==-1)
            state= audioManager.getRingerMode();
        switch (state) {
            case AudioManager.RINGER_MODE_SILENT:
                audio_State=0;
                if (!_isFirstTime)
                ToastThis("SILENT MODE ON");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                audio_State=1;
                if (!_isFirstTime)
                ToastThis("VIBRATE MODE ON");
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                audio_State=2;
                if (!_isFirstTime)
                ToastThis("NORMAL MODE ON");
                break;
        }
        sendPhone_State("Audio",state);
    }
    void toggleAudio(){
        audio_State++;
        if(audio_State==3)audio_State=0;
        switch (audio_State){
            case 0 : setSILENT();break;
            case 1 : setVIBRATE();break;
            case 2 : setNORMAL();break;
        }
    }
    public void toggleBluetooth(){
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        } else {
            mBluetoothAdapter.disable();
        }
    }
    void BluetoothState(int state,boolean isFirstTime) {
        int st=0;
        if (state==-1)
            state= mBluetoothAdapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                st=0;
                if (!isFirstTime)
                ToastThis("Bluetooth OFF");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                st=1;
                if (!isFirstTime)
                ToastThis("TURNING Bluetooth OFF...");
                break;
            case BluetoothAdapter.STATE_ON:
                st=2;
                if (!isFirstTime)
                ToastThis("Bluetooth ON");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                st=3;
                if (!isFirstTime)
                ToastThis("TURNING Bluetooth ON...");
                break;
        }
        sendPhone_State("Bluetooth",st);
    }
    public void toggleWifi(){
        if (!isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
            ToastThis("WIFI ON");
            sendPhone_State("WIFI",1);
        } else {
            wifiManager.setWifiEnabled(false);
            ToastThis("WIFI OFF");
            sendPhone_State("WIFI",0);
        }
    }
    boolean isWifiEnabled(){
         return wifiManager.isWifiEnabled();
    }
    private void initCamera(){
        try {
            isLight = context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(isLight){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    camera2 = (CameraManager)context.getSystemService(Context.CAMERA_SERVICE);
                    camera_id = camera2.getCameraIdList()[0];
                }
                LightOff();
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    private void LightOn(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera2.setTorchMode(camera_id,true);
                ToastThis("FLASH MODE ON");
                isLightOn=true;
                sendPhone_State("FLASH",1);
            }else {
                //noinspection deprecation
                camera = Camera.open();
                parameters = camera.getParameters();
                if (camera!=null){
                    //noinspection deprecation
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();
                    ToastThis("FLASH MODE ON");
                    isLightOn=true;
                    sendPhone_State("FLASH",1);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    private void LightOff(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                camera2.setTorchMode(camera_id,false);
                ToastThis("FLASH MODE OFF");
                isLightOn=false;
                sendPhone_State("FLASH",0);
            }else {
                if (camera!=null){
                    //noinspection deprecation
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    camera.release();
                    ToastThis("FLASH MODE OFF");
                    isLightOn=false;
                    sendPhone_State("FLASH",0);
                }
            }
        } catch (Exception e) {e.printStackTrace();}
    }
    public void toggleLight(){
        if (!isLightOn) {
            LightOn();
        } else {
            LightOff();
        }
    }
    public void setSILENT(){
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }
    public void setVIBRATE(){
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }
    public void setNORMAL(){
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }
}
