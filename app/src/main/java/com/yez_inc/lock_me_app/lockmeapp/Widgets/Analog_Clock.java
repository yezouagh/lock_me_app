package com.yez_inc.lock_me_app.lockmeapp.Widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import java.util.Calendar;

public class Analog_Clock extends View {
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mDial;
    private Drawable mDial2;
    private int mDialWidth;
    private int mDialHeight;
    private boolean mAttached;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;
    int availableWidth;
    int availableHeight ;
    public Analog_Clock(Context context) {
        super(context);
    }
    public Analog_Clock(Context context, Drawable Dial,Drawable Dial2, Drawable HourHand, Drawable MinuteHand) {
        super(context);
        mDial = Dial;
        mDial2 = Dial2;
        mHourHand = HourHand;
        mMinuteHand = MinuteHand;
        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }
    public Analog_Clock(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public Analog_Clock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  View.MeasureSpec.getSize(heightMeasureSpec);
        float hScale = 1.0f;
        float vScale = 1.0f;
        if (widthMode != View.MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }
        if (heightMode != View.MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }
        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        availableWidth=w;
        availableHeight=h;
        mChanged = true;
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean changed = mChanged;if (changed) {mChanged = false;}
        int x = availableWidth / 2;
        int y = availableHeight / 2;
        final Drawable dial = mDial;
        final Drawable dial2 = mDial2;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w, (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }
        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
            if (dial2!=null){dial2.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));dial.setAlpha(90);}
        }
        dial.draw(canvas);
        if (dial2!=null){dial2.draw(canvas);}
        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);
        final Drawable hourHand = mHourHand;
        if (changed) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();
            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
        final Drawable minuteHand = mMinuteHand;
        if (changed) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        minuteHand.draw(canvas);
        canvas.restore();
        if (scaled) {
            canvas.restore();
        }
    }
    private void onTimeChanged() {
        Calendar mCalendar = Calendar.getInstance();
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;
        updateContentDescription(mCalendar);
        invalidate();
    }
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            onTimeChanged();
        }
    };
    private void updateContentDescription(Calendar time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME;
        String contentDescription = DateUtils.formatDateTime(getContext(), time.getTimeInMillis(), flags);
        setContentDescription(contentDescription);
    }
}
