package com.yez_inc.lock_me_app.lockmeapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Analog_Clock;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Digital_Clock;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Phone_State;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.StateReceiver;
import com.yez_inc.lock_me_app.lockmeapp.locks.GestureLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;
import com.yez_inc.lock_me_app.lockmeapp.locks.YotLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.mainLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.mainLock2;
import com.yez_inc.lock_me_app.lockmeapp.locks.pinLock;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LockScreenView extends FrameLayout {
    Context context=getContext().getApplicationContext();
    FrameLayout view;
    ScreenView screenView;
    int color;
    PanelSwitcher panelSwitcher1;
    TextView msg,Date;
    LockManager lockManager;
    boolean isSet;
    String mAction;
    int D= Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    LayoutParams viewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, PixelFormat.TRANSLUCENT);
    int dial1=R.drawable.clock_dial,dial2=R.drawable.retry,dial22=R.drawable.b,hand_hour1=R.drawable.clock_hour,
            hand_minute1=R.drawable.clock_minute,hand_minute3=R.drawable.clockgoog_minute;
    CountDownTimer countDownTimer = new CountDownTimer(2500,2500) {
        @Override public void onTick(long millisUntilFinished) {}
        @Override public void onFinish() {
            try {msg.setVisibility(View.GONE);}catch(Exception e) {e.printStackTrace();}
        }
    };
    LockListener lockListener = new LockListener() {
        @Override public void onLockInteraction(String action) {
            if (action.equals("unlock"))
                unLockForEver();
            else ToastThis(action);
        }
    };
    StateReceiver stateReceiver = new StateReceiver(context,new LockListener() {
        @Override public void onLockInteraction(String action) {
            if (!action.equals("browser")&&!action.equals("unlock")&&!action.equals("camera")) ToastThis(action);else {mAction=action;unLock();}
        }
    },getHandler());
    private BroadcastReceiver onDateChange = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            Calendar calendar = Calendar.getInstance();
            if (D!=calendar.get(Calendar.DAY_OF_MONTH)){
                D=calendar.get(Calendar.DAY_OF_MONTH);
                date(calendar);
            }
        }};
    private boolean mAttached;
    public LockScreenView(Context context) {
        super(context);init();
    }
    public LockScreenView(Context context, LockManager Manager) {
        super(context);lockManager = Manager;init();
    }

    private void init() {
        inflate(context, R.layout.lock_view, this);
        view = (FrameLayout)findViewById(R.id.lock);
        msg = (TextView)findViewById(R.id.msg);
        if (lockManager==null){lockManager=new LockManager(context);}
        setBackground();
        color = lockManager.getTheme_Color();
        Date = (TextView) findViewById(R.id.date);
        Date.setTypeface(Typeface.create("sans-serif-thin",Typeface.NORMAL));
        Date.setShadowLayer(2,2,2,Color.BLACK);
        date(Calendar.getInstance());setThemeColor();
        context.registerReceiver(onDateChange, new IntentFilter(Intent.ACTION_TIME_TICK));
        isSet = lockManager.isSet();
        panelSwitcher1 = (PanelSwitcher)findViewById(R.id.panelSwitcher1);
        loadWidgets();
        if (!lockManager.isSkip_gestures()||!isSet){showLockScreen();}else {showLock();}
        Fast_unLockListener();
    }

    void date(Calendar calendar) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d  MMM yyyy", Locale.getDefault());
        String t = simpleDateFormat.format(calendar.getTime());
        Date.setText(t);
    }

    void setThemeColor(){
        // sets the custom text
        TextView text = (TextView) findViewById(R.id.texts);
        text.setTypeface(Typeface.create("sans-serif-thin",Typeface.NORMAL));
        String txt = lockManager.getDisplay_Name();
        if (!txt.equals("")) {text.setText(txt);}
        // Sets the theme color
        if (color != Color.WHITE) {
            Date.setTextColor(color);
            text.setTextColor(color);
        }
    }

    void Fast_unLockListener(){
        if (lockManager.isFast_unLock()) {
            Date.setOnClickListener(new OnClickListener() {
                @Override public void onClick(View arg0) {
                    if(Date.getTag()==null){
                        stateReceiver.ToastThis("unlock");
                        Date.setTag("");
                    }
                }
            });
        }
    }

    Drawable getDrawable(int drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(drawable,context.getTheme());
        }else //noinspection deprecation
            return getResources().getDrawable(drawable);
    }

    LinearLayout newRow(View v){
        LinearLayout row = new TableRow(context);
        row.addView(v);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private void loadWidgets(){
        String Widgets = lockManager.getWidgets();
        if (Widgets.contains("0")){panelSwitcher1.addView(newRow(new Digital_Clock(context,color)),viewParams);}
        if (Widgets.contains("1")){panelSwitcher1.addView(newRow(new Phone_State(context,false,stateReceiver)),viewParams);}
        if (Widgets.contains("2")){panelSwitcher1.addView(newRow(new Analog_Clock(context,getDrawable(dial1),getDrawable(dial22), getDrawable(hand_hour1),getDrawable(hand_minute1))),viewParams);}
        if (Widgets.contains("3")){panelSwitcher1.addView(newRow(new Analog_Clock(context,getDrawable(dial2),null, getDrawable(hand_hour1),getDrawable(hand_minute1))),viewParams);}
        if (Widgets.contains("4")){panelSwitcher1.addView(newRow(new Analog_Clock(context,getDrawable(dial1),null, getDrawable(hand_hour1),getDrawable(hand_minute3))),viewParams);}
        panelSwitcher1.onFinishInflate();
    }

    void showLock(){
        try
        {
            int id= lockManager.getLockType();
            switch (id){
                case 1 : showMainLock2();break;
                case 2 : showYotLock();break;
                case 3 : showPinLock(false); break;
                case 4 : showMainLock(); break;
                case 5 : showGestureLock(); break;
                default : showPinLock(true);break;
            }
        }catch(Exception e) {ToastThis(e.getMessage());}
    }

    void showMainLock(){
        mainLock lock =  new mainLock(context,lockManager,false);
        lock.setOnMainLockInteractionListener(lockListener);
        view.addView(lock,viewParams);
    }

    void showGestureLock(){
        GestureLock lock =  new GestureLock(context,lockManager,false);
        lock.setOnGestureLockInteractionListener(lockListener);
        view.addView(lock,viewParams);
    }

    void showMainLock2(){
        mainLock2 lock =  new mainLock2(context,lockManager,false);
        lock.setOnMainLock2InteractionListener(lockListener);
        view.addView(lock,viewParams);
    }

    void showYotLock(){
        YotLock lock =  new YotLock(context,lockManager,false);
        lock.setOnYotInteractionListener(lockListener);
        view.addView(lock,viewParams);
    }

    void showPinLock(boolean isPin1){
        pinLock lock =  new pinLock(context,lockManager,false,isPin1);
        lock.setOnPinInteractionListener(lockListener);
        view.addView(lock,viewParams);
    }

    void showLockScreen(){
        try
        {
            if(screenView==null)
                screenView=new ScreenView(context,lockManager,stateReceiver,color,isSet);
            view.addView(screenView,viewParams);
        }catch(Exception e) {ToastThis(e.getMessage());}
    }

    public void showForgotView() {
        removeViews();
        Forgot forgot = new Forgot(context,lockManager,false);
        view.addView(forgot,viewParams);
        forgot.setOnForgotInteractionListener(lockListener);
    }

    void unLock(){
        try
        {
            removeViews();
            if(isSet){
                showLock();
            }else {
                if(mAction.equals("browser"))openBrowser();
                else if(mAction.equals("camera"))openCamera();
                unLockForEver();
            }
        }catch(Exception e) {ToastThis(e.getMessage());}
    }

    void unLockForEver(){
        try {MyApp.unlock();}catch(Exception e) {ToastThis(e.getMessage());}
    }

    void openCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        context.startActivity(intent);
    }

    void openBrowser(){
        Uri uri = Uri.parse("http://www.google.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    void removeViews(){
        try
        {view.removeAllViews();}catch(Exception e) {ToastThis(e.getMessage());}
    }

    void setBackground(){
        setPadding(0,getStatusBarHeight(),0,0);
        String bg = lockManager.getLockBackground();
        if (!bg.equals("")) {
            if (!lockManager.isBackground_Color()) {
                Drawable b = Drawable.createFromPath(bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    findViewById(R.id.background).setBackground(b);
                else//noinspection deprecation
                    findViewById(R.id.background).setBackgroundDrawable(b);
            } else findViewById(R.id.background).setBackgroundColor(Integer.parseInt(bg));
        }
        // else {
        //     Drawable b= WallpaperManager.getInstance(context).getFastDrawable();
        //     if (b!=null)
        //     {
        //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        //         view.setBackground(b);
        //     else//noinspection deprecation
        //         view.setBackgroundDrawable(b);
        //     }else
        //     view.setBackgroundColor(Color.BLACK);
        // }
    }

    public int getStatusBarHeight() {
        int result;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }else result = getResources().getDimensionPixelSize(R.dimen.twenty_four_dp);
        return result;
    }

    void ToastThis(final String _msg){
        if(!msg.isShown())
            msg.setVisibility(View.VISIBLE);
        msg.setText(_msg);
        countDownTimer.cancel();
        countDownTimer.start();
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            stateReceiver.onDetachedFromWindow();
            getContext().unregisterReceiver(onDateChange);
            mAttached = false;
        }
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
            getContext().registerReceiver(onDateChange, filter, null, getHandler());
        }
    }
}
