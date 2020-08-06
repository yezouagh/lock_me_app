package com.yez_inc.lock_me_app.lockmeapp;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.view.View;
import android.os.Handler;
import android.content.Intent;
import android.graphics.Color;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;
import android.provider.CallLog;
import android.view.MotionEvent;
import com.yez_inc.lock_me_app.R;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.telephony.SmsMessage;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.app.admin.DevicePolicyManager;
import android.support.v4.content.LocalBroadcastManager;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.StateReceiver;

public class ScreenView extends FrameLayout {
    Context context;
    StateReceiver stateReceiver;
    LockManager lockManager;
    boolean admin, isSet;
    DevicePolicyManager policyManager;
    ComponentName adminReceiver;
    int ClicksCount = 0, color;
    TextView carrier, MSGs, calls,clear;
    LinearLayout notifications;
    public ScreenView(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public ScreenView(Context _context, LockManager Manager,StateReceiver _stateReceiver,int _color,boolean _isSet) {
        super(_context);context=_context;stateReceiver=_stateReceiver;
        lockManager = Manager;
        color = _color;
        isSet = _isSet;
        onCreate();
    }
    void onCreate() {
        inflate(context, R.layout.activity_lock, this);
        init();
        doubleClickSleepListener();
        Count();
    }
    private void init() {
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminReceiver = new ComponentName(context, DeviceAdmin.class);
        admin = policyManager.isAdminActive(adminReceiver);
        carrier = (TextView) findViewById(R.id.carrier);
        MSGs = (TextView) findViewById(R.id.MSGs);
        calls = (TextView) findViewById(R.id.calls);
        clear = (TextView) findViewById(R.id.clear);
        notifications = (LinearLayout) findViewById(R.id.notifications);
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifications.removeAllViews();
                v.setVisibility(GONE);
            }
        });
        setThemeColor();setCarrier();
        getContext().registerReceiver(receiver,  new IntentFilter("android.provider.Telephony.SMS_RECEIVED"), null, getHandler());
        if(lockManager.isShow_notifications()){
            LocalBroadcastManager.getInstance(context).registerReceiver(onNotice, new IntentFilter("Msg"));
        }
    }
    void setThemeColor(){
        if (color != Color.WHITE) {
            carrier.setTextColor(color);
            clear.setTextColor(color);
            MSGs.setTextColor(color);
            calls.setTextColor(color);
            ((TextView) findViewById(R.id.my_swipe_button)).setTextColor(color);
        }
    }
    void doubleClickSleepListener() {
        if (lockManager.isSleep_on_tap()) {
            findViewById(R.id.cl).setOnClickListener(new OnClickListener() {
                @Override public void onClick(View arg0) {
                    ClicksCount++;
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        @Override public void run() {
                            ClicksCount = 0;}
                    };
                    if (ClicksCount == 1) {
                        handler.postDelayed(r, 250);
                    } else if (ClicksCount == 2) {// user should activate the device admin
                        if (admin) {policyManager.lockNow();}
                        ClicksCount = 0;
                    }
                }
            });
        }
    }
    void Count(){
        try {
            callsCount();
            messagesCount();
        } catch (Exception e) {e.printStackTrace();}
    }
    void setCarrier(){
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = telephonyManager.getNetworkOperatorName();
        // Sets carrier name
        if (carrierName == null || carrierName.isEmpty() || carrierName.equals("")) {
            carrier.setText(R.string.no_service);
        } else
            carrier.setText(carrierName.toUpperCase());
    }
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void callsCount() {
        int i = 0;
        final String[] projection = null;
        final String selection = null;
        final String[] selectionArgs = null;
        final String sortOrder = CallLog.Calls.DATE + " DESC";
        try (Cursor cursor = context.getContentResolver().query(Uri.parse("content://call_log/calls"), projection, selection, selectionArgs, sortOrder)) {
            if (cursor != null) {
                while (
                        cursor.moveToNext()) {
                    String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                    String isCallNew = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NEW));
                    if (Integer.parseInt(callType) == CallLog.Calls.MISSED_TYPE && Integer.parseInt(isCallNew) > 0) {
                        i++;
                    }
                }
            }
        } catch (Exception ex) {stateReceiver.ToastThis(ex.getMessage());}
        finally {
            calls.setText(String.valueOf(i));
        }
    }
    public void messagesCount() {
        String[] id = {"count(_id)",};
        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), id, "read = 0", null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int unreadMessagesCount;
                unreadMessagesCount = cursor.getInt(0);
                MSGs.setText(String.valueOf(unreadMessagesCount));
            }
            if (cursor != null) {cursor.close();}
        } catch (NullPointerException e) {e.printStackTrace();}
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
                messagesCount();
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs;
                if (bundle != null){
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        if (pdus != null) {
                            msgs = new SmsMessage[pdus.length];
                            for(int i=0; i<msgs.length; i++){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i],null);
                                }else //noinspection deprecation
                                    msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                                String msg_from = msgs[i].getOriginatingAddress();
                                String msgBody = msgs[i].getMessageBody();
                                notifications.addView(new NotificationElement(context,R.drawable.ic_email_24dp,msgBody,msg_from,color),0);
                                clear.setVisibility(VISIBLE);
                            }
                        }
                    }catch(Exception ignored){}
                }
            }
        }
    };
    // Get the Notifications
    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            try {
                String title = intent.getStringExtra("title");
                String text = intent.getStringExtra("text");
                String PackageName = intent.getStringExtra("PackageName");
                Drawable drawable = context.getPackageManager().getApplicationIcon(PackageName);
                notifications.addView(new NotificationElement(context,drawable,text,title,color),0);
                clear.setVisibility(VISIBLE);
            } catch (Exception e) {stateReceiver.ToastThis(e.getMessage());}
        }
    };
    double actionConfirmDistanceFraction = 0.35;
    private float x1,x2Start,Width;
    private boolean confirmThresholdCrossed = false, swiping = false;
    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getX()<this.getWidth() *4/5;
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN: {
                Width = this.getWidth();
                x1 = event.getX();confirmThresholdCrossed = false;
                return true;}
            case MotionEvent.ACTION_MOVE: {
                float x2 = event.getX();
                if(!swiping){x2Start = x2; swiping = true;}
                if (x1 < x2) {
                    float scale = (x2-x2Start)/((Width*4/5)-x2Start);
                    if(scale<1 && scale>0){
                        setScaleX(1-scale);
                        setScaleY(1-scale);
                    }
                    confirmThresholdCrossed = (x2 - x2Start) > (Width * actionConfirmDistanceFraction);
                    return true;
                }break;
            }
            case MotionEvent.ACTION_UP: {
                swiping = false;
                if (confirmThresholdCrossed) {stateReceiver.ToastThis("unlock");return true;}
                setScaleX(1);setScaleY(1);
                break;
            }
        }
        return super.onTouchEvent(event);
    }
}