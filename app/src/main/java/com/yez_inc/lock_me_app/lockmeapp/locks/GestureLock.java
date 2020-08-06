package com.yez_inc.lock_me_app.lockmeapp.locks;

import android.content.Context;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
import java.io.File;
import java.util.ArrayList;

public class GestureLock extends FrameLayout implements GestureOverlayView.OnGesturePerformedListener {
    boolean isVibrate=false;
    TextView action_title;
    Button reset_button;LockManager lockManager;
    Context context;
    Vibrator vibrator;int nb=1;
    private GestureLibrary gLibrary;
    GestureOverlayView gOverlay;ArrayList<Gesture> gesturesArrayList;
    private LockListener mListener;
    public GestureLock(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public GestureLock(Context _context, AttributeSet attrs) {
        super(_context, attrs);context=_context;onCreate();
    }
    public GestureLock(Context _context, AttributeSet attrs, int defStyleAttr) {
        super(_context, attrs, defStyleAttr);context=_context;onCreate();
    }
    public GestureLock(Context _context, LockManager Manager, boolean _isNew) {
        super(_context);nb=_isNew?1:0;context=_context;lockManager=Manager;
        onCreate();
    }
    void onCreate() {
       try {
           inflate(getContext(), R.layout.gesturelock, this);
           if (lockManager==null){lockManager=new LockManager(context);}
           isVibrate=lockManager.isVibrate();
           if (isVibrate) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
           isVibrate= vibrator!=null&&vibrator.hasVibrator()&&isVibrate;
           File file = new File(getContext().getFilesDir(),"gesture");
           gLibrary = GestureLibraries.fromFile(file);
           if (!gLibrary.load()) {
               gLibrary.load();
           }
           if (nb==1){
               gesturesArrayList = new ArrayList<>();
               action_title =(TextView) findViewById(R.id.action_title);
               reset_button =(Button) findViewById(R.id.reset_button);
               findViewById(R.id.TSpace).setVisibility(View.VISIBLE);
               reset_button.setOnClickListener(new OnClickListener() {
                   @Override public void onClick(View v) {
                       onReset_buttonPressed();
                   }
               });
        }
           gOverlay = (GestureOverlayView) findViewById(R.id.gestureOverlayView1);
           gOverlay.addOnGesturePerformedListener(this);
       }catch (Exception e){sendCallBack(e.getMessage());}
    }
    void isRight(Gesture gesture){
            ArrayList<Prediction> predictions = gLibrary.recognize(gesture);
        if (predictions.size() > 0) {
            if (predictions.get(0).score > 4.0) {
                String action = predictions.get(0).name;
                if (action.equals("unlock")){sendCallBack("unlock");}
                }
            }
    }
    void onNew(Gesture gesture){

            gesturesArrayList.add(gesture);
            action_title.setText(R.string.confirm);
            reset_button.setVisibility(View.VISIBLE);
            nb=2;
    }
    void onConfirm1(Gesture gesture){
        gesturesArrayList.add(gesture);
        action_title.setText(R.string.one_more);
        nb=3;
    }
    void onReset_buttonPressed(){
        nb=1;gesturesArrayList.clear();
        action_title.setText(R.string.draw_your_new_signature);
        reset_button.setVisibility(View.GONE);
    }
    void saveNew(Gesture gesture){
        action_title.setText(R.string.saving_image);
        reset_button.setEnabled(false);
        gesturesArrayList.add(gesture);
        gLibrary.removeEntry("unlock");
        for (Gesture gesture2:gesturesArrayList) {
            gLibrary.addGesture("unlock",gesture2);
        }gLibrary.save();
        lockManager.setIsSet(true);
        lockManager.setLockType(5);
        sendCallBack("unlock");
    }
    void sendCallBack(String action) {
        if (mListener != null) {
            mListener.onLockInteraction(action);
        }
    }
    public void setOnGestureLockInteractionListener(LockListener lockListener) {
        mListener = lockListener;
    }
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        if(isVibrate) vibrator.vibrate(80);
        switch (nb){
            case 0 : isRight(gesture); break;
            case 1 : onNew(gesture); break;
            case 2 : onConfirm1(gesture); break;
            case 3 : saveNew(gesture); break;
            default:break;
        }
    }
}
