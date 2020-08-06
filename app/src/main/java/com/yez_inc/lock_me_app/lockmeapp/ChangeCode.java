package com.yez_inc.lock_me_app.lockmeapp;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.locks.GestureLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;
import com.yez_inc.lock_me_app.lockmeapp.locks.YotLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.mainLock;
import com.yez_inc.lock_me_app.lockmeapp.locks.mainLock2;
import com.yez_inc.lock_me_app.lockmeapp.locks.pinLock;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class ChangeCode extends Activity {
    LockManager lockManager; int lastTab = 0;
    @Override protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN| WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER
                ,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.change_pass_code);
        lockManager = new LockManager(this);
        setBackground();
        final PanelSwitcher lock_type =  (PanelSwitcher)findViewById(R.id.lock_type);
        final View tabs[]={findViewById(R.id.tab1_btn),findViewById(R.id.tab2_btn), findViewById(R.id.tab3_btn)
                ,findViewById(R.id.tab4_btn),findViewById(R.id.tab5_btn),findViewById(R.id.tab6_btn)};
        lock_type.setonPanelSwitchListener(new PanelSwitcher.onPanelSwitchListener() {
            @Override public void onPanelSwitch(int i) {
                tabs[lastTab].setAlpha(0.6f);
                tabs[i].setAlpha(1f);
                lastTab = i;
            }});
        View.OnClickListener ClickListener =new View.OnClickListener() {
            @Override public void onClick(View v) {
                int i=0;
                switch (v.getId()){case R.id.tab1_btn: i= 0;break;case R.id.tab2_btn: i= 1;break;
                    case R.id.tab3_btn: i= 2;break;case R.id.tab4_btn: i= 3;break;case R.id.tab5_btn: i= 4;break;
                    case R.id.tab6_btn: i= 5;default:break;}
                tabs[lastTab].setAlpha(0.6f);
                lock_type.moveTo(i);tabs[i].setAlpha(1f);lastTab = i;
            }
        };
        for (int i = 0; i < 6; i++) {
            tabs[i].setOnClickListener(ClickListener);
        }
        int i=lockManager.getLockType();
        if(i!=0){
        tabs[lastTab].setAlpha(0.6f);
        tabs[i].setAlpha(1f);
        lock_type.startFrom(i);
        lastTab = i;
        }
        LockListener lockListener = new LockListener() {
            @Override public void onLockInteraction(String action) {
                if (action.equals("unlock")){
                Toast.makeText(getApplicationContext(), R.string.PassCode_saved, Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                }else
                    Toast.makeText(getApplicationContext(),action , Toast.LENGTH_SHORT).show();

            }
        };
        ((pinLock)findViewById(R.id.tab1)).setOnPinInteractionListener(lockListener);
        ((mainLock2)findViewById(R.id.tab2)).setOnMainLock2InteractionListener(lockListener);
        ((YotLock)findViewById(R.id.tab3)).setOnYotInteractionListener(lockListener);
        ((pinLock)findViewById(R.id.tab4)).setOnPinInteractionListener(lockListener);
        ((mainLock)findViewById(R.id.tab5)).setOnMainLockInteractionListener(lockListener);
        ((GestureLock)findViewById(R.id.tab6)).setOnGestureLockInteractionListener(lockListener);
    }
    void setBackground(){
     LinearLayout layout = (LinearLayout)findViewById(R.id.lock_bLayout);
        String bg = lockManager.getLockBackground();
        if (!lockManager.isBackground_Color()) {
            if (!bg.equals("")) {
                Drawable b = Drawable.createFromPath(bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    layout.setBackground(b);
                else//noinspection deprecation
                    layout.setBackgroundDrawable(b);
            }
        } else if (!bg.equals("")) {
            layout.setBackgroundColor(Integer.parseInt(bg));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
