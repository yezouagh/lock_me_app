package com.yez_inc.lock_me_app.lockmeapp;

import android.view.animation.TranslateAnimation;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;

class PanelSwitcher extends FrameLayout {
    private static final int ANIM_DURATION = 250;
    private static final int MAJOR_MOVE = 40;
    private int count = 0;
    private GestureDetector mGestureDetector;
    private int mCurrentView;
    private View mChildren[] = new View[0];
    private TranslateAnimation inLeft;
    private TranslateAnimation outLeft;
    private TranslateAnimation inRight;
    private TranslateAnimation outRight;
    private onPanelSwitchListener PanelSwitchListener;

    public void setonPanelSwitchListener(onPanelSwitchListener onSwitchListener) {
        PanelSwitchListener = onSwitchListener;
    }

    public PanelSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCurrentView = 0;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (count > 1) {
                    int dx = (int) (e2.getX() - e1.getX());
                    int dy = (int) (e2.getY() - e1.getY());
                    if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > MAJOR_MOVE) {
                        if (velocityX < 0) moveLeft();
                        else moveRight();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onSizeChanged(int w, int h, int oldW, int oldH) {
        inLeft = new TranslateAnimation(w, 0, 0, 0);
        outLeft = new TranslateAnimation(0, -w, 0, 0);
        inRight = new TranslateAnimation(-w, 0, 0, 0);
        outRight = new TranslateAnimation(0, w, 0, 0);
        inLeft.setDuration(ANIM_DURATION);
        outLeft.setDuration(ANIM_DURATION);
        inRight.setDuration(ANIM_DURATION);
        outRight.setDuration(ANIM_DURATION);
    }

    protected void onFinishInflate() {
        count = getChildCount();
        if (count > 0) {
            mChildren = new View[count];
            for (int i = 0; i < count; ++i) {
                View v = getChildAt(i);
                mChildren[i] = v;
                v.setVisibility(View.GONE);
            }
            mChildren[mCurrentView].setVisibility(View.VISIBLE);
        }
    }
    @Override public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }
    @Override public boolean onInterceptTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
    private void moveLeft() {
        int i=mCurrentView;
        mCurrentView++;
        if (mCurrentView == count)
            mCurrentView=0;
        if (PanelSwitchListener != null)
            PanelSwitchListener.onPanelSwitch(mCurrentView);
        mChildren[mCurrentView].setVisibility(View.VISIBLE);
        mChildren[mCurrentView].startAnimation(inLeft);
        mChildren[i].startAnimation(outLeft);
        mChildren[i].setVisibility(View.GONE);
    }
    private void moveRight() {
        int i=mCurrentView;
        mCurrentView--;
        if (mCurrentView == -1)
            mCurrentView=count-1;
        if (PanelSwitchListener != null)
            PanelSwitchListener.onPanelSwitch(mCurrentView);
        mChildren[mCurrentView].setVisibility(View.VISIBLE);
        mChildren[mCurrentView].startAnimation(inRight);
        mChildren[i].startAnimation(outRight);
        mChildren[i].setVisibility(View.GONE);
    }
    public void moveTo(int i) {
        if (i >= 0 && i < count) {
            TranslateAnimation out = outLeft;
            TranslateAnimation in = inLeft;
            if(i<mCurrentView){in=inRight;out=outRight;}
            mChildren[i].setVisibility(View.VISIBLE);
            mChildren[i].startAnimation(in);
            mChildren[mCurrentView].startAnimation(out);
            mChildren[mCurrentView].setVisibility(View.GONE);
            mCurrentView=i;
        }
    }
    public void startFrom(int i) {
        if (i >= 0 && i < count) {
            mChildren[i].setVisibility(View.VISIBLE);
            mChildren[mCurrentView].setVisibility(View.GONE);
            mCurrentView=i;
        }
    }
    public interface onPanelSwitchListener {
        void onPanelSwitch(int i);
    }
}