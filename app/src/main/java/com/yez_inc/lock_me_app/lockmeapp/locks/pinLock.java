package com.yez_inc.lock_me_app.lockmeapp.locks;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class pinLock extends FrameLayout implements View.OnClickListener {
    String pin="", NewLock ="";boolean isNew=true,isVibrate=false,confirm=false,isPin1=true;
    TextView pin1, pin2, pin3, pin4, one, two, three, four, five, six, seven, eight, nine, zero, clear,action_title,delete;
    Button reset_button;LockManager lockManager;
    int WrongBack_image =R.drawable.wrongbackimage0,Right_image=R.drawable.backimage0, index=-1,attempts=0;
    Context context;
    Vibrator vibrator;
    private LockListener mListener;
    public pinLock(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public pinLock(Context _context, AttributeSet attrs) {
        super(_context, attrs);context=_context;
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pinLock);
        isPin1 = ta.getBoolean(R.styleable.pinLock_isPin1, true);
        ta.recycle();
        onCreate();
    }
    public pinLock(Context _context, AttributeSet attrs, int defStyleAttr) {
        super(_context, attrs, defStyleAttr);context=_context;
        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.pinLock);
        isPin1 = ta.getBoolean(R.styleable.pinLock_isPin1, true);
        ta.recycle();
        onCreate();
    }
    public pinLock(Context _context, LockManager Manager, boolean _isNew,boolean _isPin1) {
        super(_context);isPin1=_isPin1;
        isNew=_isNew;context=_context;lockManager=Manager;
        onCreate();
    }
    void onCreate() {
        if(isPin1){ inflate(context, R.layout.pinlock1, this);Right_image=R.drawable.retry;}
        else inflate(context, R.layout.pinlock2, this);
        if (lockManager==null){lockManager=new LockManager(context);}
        isVibrate=lockManager.isVibrate();
        if (isVibrate) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        isVibrate= vibrator!=null&&vibrator.hasVibrator()&&isVibrate;
        pin1 =(TextView) findViewById(R.id.pin1);pin2 =(TextView) findViewById(R.id.pin2);
        pin3 =(TextView) findViewById(R.id.pin3);pin4 =(TextView) findViewById(R.id.pin4);
        one =(TextView) findViewById(R.id.one_button);two =(TextView) findViewById(R.id.two_button);
        three =(TextView) findViewById(R.id.three_button);four =(TextView) findViewById(R.id.four_button);
        five =(TextView) findViewById(R.id.five_button);six =(TextView) findViewById(R.id.six_button);
        seven =(TextView) findViewById(R.id.seven_button);eight =(TextView) findViewById(R.id.eight_button);
        nine =(TextView) findViewById(R.id.nine_button);zero =(TextView) findViewById(R.id.zero_button);
        clear =(TextView) findViewById(R.id.clear);delete =(TextView) findViewById(R.id.delete);
        one.setOnClickListener(this);two.setOnClickListener(this);three.setOnClickListener(this);
        four.setOnClickListener(this);five.setOnClickListener(this);six.setOnClickListener(this);
        seven.setOnClickListener(this);eight.setOnClickListener(this);nine.setOnClickListener(this);
        zero.setOnClickListener(this);clear.setOnClickListener(this);delete.setOnClickListener(this);
        if (isNew){
            action_title =(TextView) findViewById(R.id.action_title);
            reset_button =(Button) findViewById(R.id.reset_button);
            findViewById(R.id.TSpace).setVisibility(View.VISIBLE);
            reset_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReset_buttonPressed();
                }
            });
        }
    }
    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear : clear(); break;
            case R.id.delete :delete(); break;
            default : input(v); break;
        }
    }
    void input(View v){
        if(index<3){
            if(isVibrate) vibrator.vibrate(80);
            index++;pin+=((TextView)v).getText();
            setPin();
        }
        if(index==3){
            if(isNew || confirm) {if(isNew){NewLock =pin;onNewPressed();} else{onConfirmPressed();}} else {
                isRight();}
        }
    }
    void onNewPressed(){
            action_title.setText(R.string.confirm);
            reset_button.setVisibility(View.VISIBLE);
            isNew=false; clear();confirm=true;
    }
    void onConfirmPressed(){
        if(NewLock.equals(pin)){
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
    void isRight(){
        if(lockManager.isLockRight(pin))
        {sendCallBack("unlock");}
        else{
            int Interval=800;
            attempts++;
            if(attempts>4){
                Interval=30000;
                sendCallBack("You'r gonna have to wait for 30 seconds before next attempt.");
            }
            setWrong(Interval);
        }
    }
    void setWrong(int Interval){
        zero.setEnabled(false);
        one.setEnabled(false);
        two.setEnabled(false);
        three.setEnabled(false);
        four.setEnabled(false);
        five.setEnabled(false);
        six.setEnabled(false);
        seven.setEnabled(false);
        eight.setEnabled(false);
        nine.setEnabled(false);
        clear.setEnabled(false);
        clear.setClickable(false);
        delete.setEnabled(false);
        delete.setClickable(false);
        setPinWrong();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try
                {
                    setRight();
                }catch(Exception e) {e.printStackTrace();}
            }
        }, Interval);
    }
    void setRight(){
        clear();
        clear.setEnabled(true);
        clear.setClickable(true);
        delete.setEnabled(true);
        delete.setClickable(true);
        zero.setEnabled(true);
        one.setEnabled(true);
        two.setEnabled(true);
        three.setEnabled(true);
        four.setEnabled(true);
        five.setEnabled(true);
        six.setEnabled(true);
        seven.setEnabled(true);
        eight.setEnabled(true);
        nine.setEnabled(true);
        setPinRight();
    }
    void delete(){
        if(index!=-1){
            pin=pin.substring(0,pin.length()-1);
            switch (index){
                case 0 : pin1.setAlpha(0.4f); break;
                case 1 : pin2.setAlpha(0.4f); break;
                case 2 : pin3.setAlpha(0.4f); break;
                case 3 : pin4.setAlpha(0.4f); break;
                default:break;
            }
            index--;
        }
    }
    void clear(){
        index=-1;pin="";
        pin1.setAlpha(0.4f);
        pin2.setAlpha(0.4f);
        pin3.setAlpha(0.4f);
        pin4.setAlpha(0.4f);
    }
    void setPin(){
        switch (index){
            case 0 : pin1.setAlpha(1f); break;
            case 1 : pin2.setAlpha(1f); break;
            case 2 : pin3.setAlpha(1f); break;
            case 3 : pin4.setAlpha(1f); break;
            default:break;
        }
    }
    void setPinWrong(){
        pin1.setBackgroundResource(WrongBack_image);
        pin2.setBackgroundResource(WrongBack_image);
        pin3.setBackgroundResource(WrongBack_image);
        pin4.setBackgroundResource(WrongBack_image);
    }
    void setPinRight(){
        pin1.setBackgroundResource(Right_image);
        pin2.setBackgroundResource(Right_image);
        pin3.setBackgroundResource(Right_image);
        pin4.setBackgroundResource(Right_image);
    }
    void saveNew(String Lock){
        if(isPin1) lockManager.setNewLock(Lock,0);else lockManager.setNewLock(Lock,3);
        lockManager.setIsSet(true);
    }
    void sendCallBack(String action) {
       if (mListener != null) {
           mListener.onLockInteraction(action);
       }
    }
    public void setOnPinInteractionListener(LockListener lockListener) {
           mListener = lockListener;
    }
}
