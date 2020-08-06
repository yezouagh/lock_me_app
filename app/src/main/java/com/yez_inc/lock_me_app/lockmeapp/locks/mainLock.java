package com.yez_inc.lock_me_app.lockmeapp.locks;
import android.content.Context;
import android.os.Build;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class mainLock extends FrameLayout implements View.OnClickListener {
    String lock ="", NewLock ="";boolean isNew=true,confirm=false,isVibrate=false;
    TextView main1, main2, main3, main4,action_title;
    Button reset_button, ok_button;Button retry;LockManager lockManager;
    Context context;
    Vibrator vibrator;
    private LockListener mListener;
    public mainLock(Context _context) {
        super(_context);context=_context;onCreate();
    }
    public mainLock(Context _context, AttributeSet attrs) {
        super(_context, attrs);context=_context;onCreate();
    }
    public mainLock(Context _context, AttributeSet attrs, int defStyleAttr) {
        super(_context, attrs, defStyleAttr);context=_context;onCreate();
    }
    public mainLock(Context _context, LockManager Manager, boolean _isNew) {
        super(_context);
        isNew=_isNew;context=_context;lockManager=Manager;
        onCreate();
    }
    void onCreate() {
       try {
           inflate(getContext(), R.layout.mainlock, this);
           if (lockManager==null){lockManager=new LockManager(context);}
           isVibrate=lockManager.isVibrate();
           if (isVibrate) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
           isVibrate= vibrator!=null&&vibrator.hasVibrator()&&isVibrate;
           main1 =(TextView) findViewById(R.id.main1);
           main2 =(TextView) findViewById(R.id.main2);
           main3 =(TextView) findViewById(R.id.main3);
           main4 =(TextView) findViewById(R.id.main4);
           retry =(Button) findViewById(R.id.retry);
           main1.setOnClickListener(this);
           main2.setOnClickListener(this);
           main3.setOnClickListener(this);
           main4.setOnClickListener(this);
           retry.setOnClickListener(this);
           retry();setTg();
           if (!isInEditMode()) vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
           if (isNew){
               action_title =(TextView) findViewById(R.id.action_title);
               reset_button =(Button) findViewById(R.id.reset_button);
               ok_button =(Button) findViewById(R.id.ok_button);
               findViewById(R.id.TSpace).setVisibility(View.VISIBLE);
               ok_button.setOnClickListener(new View.OnClickListener() {
                   @Override public void onClick(View v) {
                       Ok_buttonPressed();
                   }
               });
               reset_button.setOnClickListener(new View.OnClickListener() {
                   @Override public void onClick(View v) {
                       onReset_buttonPressed();
                   }
               });
        }
       }catch (Exception e){sendCallBack(e.getMessage());}
    }
    @Override public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry : retry(); break;
            default :  mainClick((TextView)v); break;
        }
    }
    void mainClick(TextView textView){
        if(isVibrate) vibrator.vibrate(80);
        int index=(int)(textView.getTag(R.id.lockTid));
        if(index<=3)index++;else index=1;
        textView.setTag(R.id.lockTid,index);
        int index1= (int)textView.getTag();
        setTxtColor(textView, index);
        lock +=index1;
        if(isNew || confirm)
        {if(isNew) NewLock =lock;}
        else isRight();
    }
    void setTxtColor(TextView textView, int index){
        int color;
        switch (index){
            case 1 : color=R.color.red; break;
            case 2 : color=R.color.blue; break;
            case 3 : color=R.color.yellow; break;
            default: color=R.color.green;break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            textView.setTextColor(getResources().getColor(color,context.getTheme()));
        else//noinspection deprecation
            textView.setTextColor(getResources().getColor(color));
    }
    void setTg(){
        main1.setTag(1);
        main2.setTag(2);
        main3.setTag(3);
        main4.setTag(4);
    }
    void retry(){
        lock ="";
        main1.setTag(R.id.lockTid,1);
        setTxtColor(main1, 1);
        main2.setTag(R.id.lockTid,2);
        setTxtColor(main2,2);
        main3.setTag(R.id.lockTid,3);
        setTxtColor(main3,3);
        main4.setTag(R.id.lockTid,4);
        setTxtColor(main4,4);
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
        lockManager.setNewLock(Lock,4);
        lockManager.setIsSet(true);
    }
    void sendCallBack(String action) {
        if (mListener != null) {
            mListener.onLockInteraction(action);
        }
    }
    public void setOnMainLockInteractionListener(LockListener lockListener) {
        mListener = lockListener;
    }
}
