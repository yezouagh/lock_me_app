package com.yez_inc.lock_me_app.lockmeapp.locks;

import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class YotLock extends FrameLayout implements View.OnClickListener {
    TextView y0,y1,y2,y3,y4,y5,y6,y7,y8,y9;FrameLayout Layout;
    int count=0,attempts=0, WrongBack_image =R.drawable.wrongbackimage0,RightBack_image=R.drawable.backimage0;
    String pattern="",NewLock ="";boolean isNew=true,confirm=false,isVibrate=false;
    TextView action_title;
    Button reset_button,ok_button,retry;LockManager lockManager;
    Context context;
    Vibrator vibrator;
    private LockListener mListener;
    public YotLock(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public YotLock(Context _context, AttributeSet attrs) {
        super(_context, attrs);context=_context;onCreate();
    }
    public YotLock(Context _context, AttributeSet attrs, int defStyleAttr) {
        super(_context, attrs, defStyleAttr);context=_context;onCreate();
    }
    public YotLock(Context _context, LockManager Manager, boolean _isNew) {
        super(_context);
        isNew=_isNew;context=_context;lockManager=Manager;
        onCreate();
    }
    void onCreate(){
        inflate(context, R.layout.yotslock, this);
        if (lockManager==null){lockManager=new LockManager(context);}
        isVibrate=lockManager.isVibrate();
        if (isVibrate) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        try {
            y0=(TextView)findViewById(R.id.y0);y1=(TextView)findViewById(R.id.y1);y2=(TextView)findViewById(R.id.y2);
            y3=(TextView)findViewById(R.id.y3);y4=(TextView)findViewById(R.id.y4);y5=(TextView)findViewById(R.id.y5);
            y6=(TextView)findViewById(R.id.y6);y7=(TextView)findViewById(R.id.y7);y8=(TextView)findViewById(R.id.y8);
            y9=(TextView)findViewById(R.id.y9);retry=(Button)findViewById(R.id.retry);Layout=(FrameLayout)findViewById(R.id.Layout);
            isVibrate= vibrator!=null&&vibrator.hasVibrator()&&isVibrate;
            if (isNew){
                action_title =(TextView) findViewById(R.id.action_title);
                reset_button =(Button) findViewById(R.id.reset_button);
                ok_button =(Button) findViewById(R.id.ok_button);
                findViewById(R.id.TSpace).setVisibility(View.VISIBLE);
                ok_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Ok_buttonPressed();
                    }
                });
                reset_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onReset_buttonPressed();
                    }
                });
            }
            int size = Math.min( getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
            int  spec =MeasureSpec.getSize(size);
            RelativeLayout.LayoutParams Para =  (RelativeLayout.LayoutParams)Layout.getLayoutParams();
            Para.width=Para.height=spec;
            Layout.setLayoutParams(Para);
            setSize(spec/4);
            setXY(spec);
            animating();
            y0.setOnClickListener(this);y1.setOnClickListener(this);y2.setOnClickListener(this);y3.setOnClickListener(this);
            y4.setOnClickListener(this);y5.setOnClickListener(this);y6.setOnClickListener(this);y7.setOnClickListener(this);
            y8.setOnClickListener(this);y9.setOnClickListener(this);retry.setOnClickListener(this);
        }catch (Exception e){sendCallBack(e.getMessage());}
    }
    @Override public void onClick(View v) {
        if(v.getId()==R.id.retry) {clear();}
        else {
            if(isVibrate) vibrator.vibrate(80);
            count++;
            pattern+=v.getTag();
            disable(v);
            if(isNew || confirm)
            {if(isNew) NewLock =pattern;}
            else isRight();
        }
    }
    void onNewOk_buttonPressed(){
        if(count>3)
        {
            action_title.setText(R.string.confirm);
            reset_button.setVisibility(View.VISIBLE);
            isNew=false; clear();confirm=true;
        }
        else sendCallBack("length()<4,retry");
    }
    void isRight(){
        if(lockManager.isLockRight(pattern)){
            sendCallBack("unlock");
        }else
        if(count==10) {
            int Interval=800;
            attempts++;
            if(attempts>4){
                Interval=30000;
                retry.setEnabled(false);
                sendCallBack("You'r gonna have to wait for 30 seconds before next attempt.");
            }
            setWrong(Interval);
        }
    }
    void Ok_buttonPressed(){
        if(confirm)onConfirmOk_buttonPressed();
        else onNewOk_buttonPressed();
    }
    void onConfirmOk_buttonPressed(){
        if(NewLock.equals(pattern)){
            saveNew(NewLock);
            sendCallBack("unlock");
        }else {
            setWrong(800);
        }
    }
    void onReset_buttonPressed(){
        NewLock ="";isNew=true;confirm=false;
        action_title.setText(R.string.New);
        reset_button.setVisibility(View.GONE);
        clear();
    }
    void clear(){
        count=0;pattern="";
        enable(y0);enable(y1);enable(y2);enable(y3);enable(y4);
        enable(y5);enable(y6);enable(y7);enable(y8);enable(y9);
    }
    void setWrong(int Interval) {
        y0.setBackgroundResource(WrongBack_image);
        y1.setBackgroundResource(WrongBack_image);
        y2.setBackgroundResource(WrongBack_image);
        y3.setBackgroundResource(WrongBack_image);
        y4.setBackgroundResource(WrongBack_image);
        y5.setBackgroundResource(WrongBack_image);
        y6.setBackgroundResource(WrongBack_image);
        y7.setBackgroundResource(WrongBack_image);
        y8.setBackgroundResource(WrongBack_image);
        y9.setBackgroundResource(WrongBack_image);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                try
                {
                    setRight();
                    retry.setEnabled(true);
                }catch(Exception e) {e.printStackTrace();}

            }
        }, Interval);
    }
    void setRight() {
        y0.setBackgroundResource(RightBack_image);
        y1.setBackgroundResource(RightBack_image);
        y2.setBackgroundResource(RightBack_image);
        y3.setBackgroundResource(RightBack_image);
        y4.setBackgroundResource(RightBack_image);
        y5.setBackgroundResource(RightBack_image);
        y6.setBackgroundResource(RightBack_image);
        y7.setBackgroundResource(RightBack_image);
        y8.setBackgroundResource(RightBack_image);
        y9.setBackgroundResource(RightBack_image);
        clear();
    }
    void disable(View yt) {
        yt.setAlpha(1f);
        yt.setEnabled(false);
    }
    void enable(View yt) {
        yt.setAlpha(0.6f);
        yt.setEnabled(true);
    }
    void setXY(int size) {
        y0.setTranslationX(0);                  y0.setTranslationY((float)(size*-0.4));
        y1.setTranslationX((float)(size*0.23)); y1.setTranslationY((float)(size*-0.33));
        y2.setTranslationX((float)(size*0.38)); y2.setTranslationY((float)(size*-0.13));
        y3.setTranslationX((float)(size*0.38)); y3.setTranslationY((float)(size*0.14));
        y4.setTranslationX((float)(size*0.23)); y4.setTranslationY((float)(size*0.34));
        y5.setTranslationX(0);                  y5.setTranslationY((float)(size*0.41));
        y6.setTranslationX((float)(size*-0.24));y6.setTranslationY((float)(size*0.34));
        y7.setTranslationX((float)(size*-0.39));y7.setTranslationY((float)(size*0.14));
        y8.setTranslationX((float)(size*-0.39));y8.setTranslationY((float)(size*-0.13));
        y9.setTranslationX((float)(size*-0.24));y9.setTranslationY((float)(size*-0.33));
    }
    void setSize(int size) {
        ViewGroup.LayoutParams params = y0.getLayoutParams();
        params.width=params.height=size;
        y0.setLayoutParams(params);y1.setLayoutParams(params);y2.setLayoutParams(params);y3.setLayoutParams(params);
        y4.setLayoutParams(params);y5.setLayoutParams(params);y6.setLayoutParams(params);y7.setLayoutParams(params);
        y8.setLayoutParams(params);y9.setLayoutParams(params);
        ViewGroup.LayoutParams param = retry.getLayoutParams();
        param.width=param.height=(size*2)-5;
        retry.setLayoutParams(param);
    }
    void animating() {
        ScaleAnimation anim = new ScaleAnimation(0,1,0,1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        anim.setDuration(1000);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);
        y0.startAnimation(anim);y1.startAnimation(anim);y2.startAnimation(anim);y3.startAnimation(anim);
        y4.startAnimation(anim);y5.startAnimation(anim);y6.startAnimation(anim);y7.startAnimation(anim);
        y8.startAnimation(anim);y9.startAnimation(anim);
    }
    void saveNew(String Lock){
        lockManager.setNewLock(Lock,2);
        lockManager.setIsSet(true);
    }
    void sendCallBack(String action) {
        if (mListener != null) {
            mListener.onLockInteraction(action);
        }
    }
    public void setOnYotInteractionListener(LockListener lockListener) {
        mListener = lockListener;
    }
}
