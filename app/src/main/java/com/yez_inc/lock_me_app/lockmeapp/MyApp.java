package com.yez_inc.lock_me_app.lockmeapp;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.yez_inc.lock_me_app.lockmeapp.service.LockerService;
public class MyApp extends Application {
    static LockerService.ViewServiceBinder binder;
    @Override public void onCreate() {
        super.onCreate();
        bindLockerService();
    }
    private void bindLockerService() {
        Intent intent = new Intent(this, LockerService.class);
        bindService(intent, new LockServiceConnection(), Context.BIND_AUTO_CREATE);
    }
    private class LockServiceConnection implements ServiceConnection {
        @Override public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (LockerService.ViewServiceBinder) service;
        }
        @Override public void onServiceDisconnected(ComponentName name) {
        }
    }
    public static void lock() {
        binder.getService().showLockView();
    }
    public static void unlock() {
        binder.getService().removeLockView();
    }
}
