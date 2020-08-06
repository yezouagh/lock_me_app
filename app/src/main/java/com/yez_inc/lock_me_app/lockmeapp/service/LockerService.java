package com.yez_inc.lock_me_app.lockmeapp.service;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.yez_inc.lock_me_app.lockmeapp.LockScreenView;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
public class LockerService extends Service {
    Binder binder = new ViewServiceBinder();
    private static boolean isLockViewAttached=false,isCallAttached=false;
    private WindowManager windowManager;
    private BroadcastReceiver callReceiver;
    LockManager lockManager;
    private KeyguardManager keyguardManager;
    @SuppressWarnings("deprecation")
    private KeyguardManager.KeyguardLock keyguardLock;
    private WindowManager.LayoutParams playerParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
            WindowManager.LayoutParams.FLAG_FULLSCREEN| WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER|
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN|
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS|
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION|WindowManager.LayoutParams.FLAG_SECURE
            , PixelFormat.TRANSLUCENT);
    private LockScreenView lockScreenView;
    @Override public IBinder onBind(Intent intent) {
        return binder;
    }
    void disableKeyGuard() {
        if (keyguardManager == null) {
            keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(KEYGUARD_SERVICE);
            //noinspection deprecation
            keyguardLock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        }
        keyguardLock.disableKeyguard();
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    public class ViewServiceBinder extends Binder {
        public LockerService getService() {
            return LockerService.this;
        }
    }
    @Override public void onCreate() {
        super.onCreate();
        lockManager =new LockManager(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        callReceiver = new CallsReceiver();
        IntentFilter callFilter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callReceiver, callFilter);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockScreenReceiver, filter);
    }
    @Override public void onDestroy() {
        unregisterReceiver(callReceiver);
        unregisterReceiver(lockScreenReceiver);
        super.onDestroy();
    }
    public void showLockView() {
        if (windowManager != null && !isLockViewAttached && lockManager.isLock_enabled()) {
            lockScreenView = new LockScreenView(this,lockManager);
            windowManager.addView(lockScreenView, playerParams);
            isLockViewAttached = true;
            disableKeyGuard();
        }
    }
    public void removeLockView() {
        if (windowManager != null && lockScreenView != null && isLockViewAttached) {
            windowManager.removeView(lockScreenView);
            lockScreenView = null;
            isLockViewAttached = false;
            disableKeyGuard();
        }
    }
    public BroadcastReceiver lockScreenReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                showLockView();
            }
        }
    };
    public class CallsReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                removeLockView();
                if (isLockViewAttached)
                isCallAttached=true;
            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)|| state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                if (isCallAttached) showLockView();
                isCallAttached=false;
            }
        }
    }
}
