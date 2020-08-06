package com.yez_inc.lock_me_app.lockmeapp.locks;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.Circle;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class mainLock2 extends FrameLayout implements View.OnClickListener {
    String lock ="", NewLock ="";boolean isNew=true,confirm=false,isVibrate=false;
    TextView action_title;RelativeLayout main1, main2, main3, main4;
    Button reset_button, ok_button;FrameLayout retry;LockManager lockManager;
    Circle progress;
    Context context;
    Vibrator vibrator;float nb;
    private LockListener mListener;
    public mainLock2(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public mainLock2(Context _context, AttributeSet attrs) {
        super(_context, attrs);context=_context;onCreate();
    }
    public mainLock2(Context _context, AttributeSet attrs, int defStyleAttr) {
        super(_context, attrs, defStyleAttr);context=_context;onCreate();
    }
    public mainLock2(Context _context, LockManager Manager, boolean _isNew) {
        super(_context);
        isNew=_isNew;context=_context;lockManager=Manager;
        onCreate();
    }
    void onCreate() {
       try {
           inflate(getContext(), R.layout.mainlock2, this);
           if (lockManager==null){lockManager=new LockManager(context);}
           isVibrate=lockManager.isVibrate();
           if (isVibrate) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
           isVibrate= vibrator!=null&&vibrator.hasVibrator()&&isVibrate;
           main1 =(RelativeLayout) findViewById(R.id.main1);
           main2 =(RelativeLayout) findViewById(R.id.main2);
           main3 =(RelativeLayout) findViewById(R.id.main3);
           main4 =(RelativeLayout) findViewById(R.id.main4);
           retry =(FrameLayout) findViewById(R.id.retry);
           progress =(Circle) findViewById(R.id.progress);
           main1.setOnClickListener(this);
           main2.setOnClickListener(this);
           main3.setOnClickListener(this);
           main4.setOnClickListener(this);
           retry.setOnClickListener(this);
           retry();setTg();
           if (isNew){
               nb=10;
               action_title =(TextView) findViewById(R.id.action_title);
               reset_button =(Button) findViewById(R.id.reset_button);
               ok_button =(Button) findViewById(R.id.ok_button);
               findViewById(R.id.TSpace).setVisibility(View.VISIBLE);
               ok_button.setOnClickListener(new OnClickListener() {
                   @Override public void onClick(View v) {
                       Ok_buttonPressed();
                   }
               });
               reset_button.setOnClickListener(new OnClickListener() {
                   @Override public void onClick(View v) {
                       onReset_buttonPressed();
                   }
               });
        }else nb=360/lockManager.getLock().length();
       }catch (Exception e){sendCallBack(e.getMessage());}
    }
    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry : retry(); break;
            default :  mainClick(v); break;
        }
    }
    void mainClick(View view){
        if(isVibrate) vibrator.vibrate(80);
        String index= view.getTag().toString();
        lock +=index;
        setProgress(nb);
        if(isNew || confirm)
        {if(isNew) NewLock =lock;}
        else isRight();
    }
    void setProgress(float x){
        progress.setDrawUpTo(x);
    }
    void setTg(){
        main1.setTag(1);
        main2.setTag(2);
        main3.setTag(3);
        main4.setTag(4);
    }
    void retry(){
        lock ="";
        setProgress(0);
    }
    void isRight(){
        if(lockManager.isLockRight(lock))
        {sendCallBack("unlock");}
    }
    void Ok_buttonPressed(){
        if(confirm)onConfirmOk_buttonPressed();
        else onNewOk_buttonPressed();
    }
    void onNewOk_buttonPressed(){
        if(lock.length()>3)
        {
        action_title.setText(R.string.confirm);
        reset_button.setVisibility(View.VISIBLE);
        isNew=false; retry();confirm=true;
        }
        else sendCallBack("length()<4,retry");
    }
    void onConfirmOk_buttonPressed(){
        if(NewLock.equals(lock)){
            saveNew(NewLock);
            sendCallBack("unlock");
        }else {
            sendCallBack("wrong,retry");
            retry();
        }
    }
    void onReset_buttonPressed(){
        NewLock ="";isNew=true;confirm=false;
        action_title.setText(R.string.New);
        reset_button.setVisibility(View.GONE);
        retry();
    }
    void saveNew(String Lock){
        lockManager.setNewLock(Lock,1);
        lockManager.setIsSet(true);
    }
    void sendCallBack(String action) {
        if (mListener != null) {
            mListener.onLockInteraction(action);
        }
    }
    public void setOnMainLock2InteractionListener(LockListener lockListener) {
        mListener = lockListener;
    }
}
