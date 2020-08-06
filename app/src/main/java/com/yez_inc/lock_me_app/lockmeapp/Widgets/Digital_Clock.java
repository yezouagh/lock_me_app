package com.yez_inc.lock_me_app.lockmeapp.Widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Digital_Clock extends LinearLayout {
    TextView time,PM;
    Calendar mCalendar;
    private boolean mAttached;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
    SimpleDateFormat PMFormat = new SimpleDateFormat("aa", Locale.getDefault());
    public Digital_Clock(Context context) {
        super(context);init(Color.WHITE);
    }
    public Digital_Clock(Context context, int color) {
        super(context);init(color);
    }
    public Digital_Clock(Context context, AttributeSet attrs) {
        super(context, attrs);init(Color.WHITE);
    }
    public Digital_Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init(Color.WHITE);
    }
    void init(int color){
        inflate(getContext(), R.layout.digital_clock, this);
        time = (TextView) findViewById(R.id.time);
        PM = (TextView) findViewById(R.id.pm);
        time.setTextColor(color);
        time.setShadowLayer(2,2,2,Color.BLACK);
        time.setTypeface(Typeface.create("sans-serif-thin",Typeface.NORMAL));
        PM.setShadowLayer(2,2,2,Color.BLACK);
        PM.setTextColor(color);
    }
    @Override public CharSequence getAccessibilityClassName() {
        //noinspection deprecation
        return Digital_Clock.class.getName();
    }
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
            getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
        }
        onTimeChanged();
    }
    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }
    private void onTimeChanged() {
        mCalendar=Calendar.getInstance();
        time.setText(timeFormat.format(mCalendar.getTime()));
        PM.setText(PMFormat.format(mCalendar.getTime()));
        invalidate();
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onTimeChanged();
        }
    };
}
