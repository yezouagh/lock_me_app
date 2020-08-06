package com.yez_inc.lock_me_app.lockmeapp;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.yez_inc.lock_me_app.ColorPicker.ColorDialog;
import com.yez_inc.lock_me_app.lockmeapp.locks.LockListener;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;
import com.yez_inc.lock_me_app.R;

public class Setting extends Fragment implements OnClickListener,CompoundButton.OnCheckedChangeListener {
    View view;
	boolean fromTab=true,isOnCreate;
    TabLayout tabLayout;
    PanelSwitcher panelSwitcher;
	Context context ;
	Switch enable, hidden, skip, sleep,vibrate,show_notifications;
    LockManager lockManager;
    LockListener gallery_listener;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting, container, false);
        context = getContext();
        lockManager = new LockManager(context);
        init();
        applyPreferences();
        firstTime();
        return view;
    }
    void init() {
        panelSwitcher = (PanelSwitcher)view.findViewById(R.id.panelSwitcher);
        tabLayout = (TabLayout)view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if(fromTab){panelSwitcher.moveTo(tab.getPosition());}fromTab=true;
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
        panelSwitcher.setonPanelSwitchListener(new PanelSwitcher.onPanelSwitchListener() {
            @Override
            public void onPanelSwitch(int i) {
                try {TabLayout.Tab tab = tabLayout.getTabAt(i);if(tab!=null){fromTab=false; tab.select();}}catch (Exception e){e.printStackTrace();}
            }
        });
        enable = (Switch) view.findViewById(R.id.enable);
        hidden = (Switch) view.findViewById(R.id.hidden);
        skip = (Switch) view.findViewById(R.id.skip);
        sleep = (Switch) view.findViewById(R.id.sleep);
        vibrate = (Switch) view.findViewById(R.id.vibrate);
        show_notifications = (Switch) view.findViewById(R.id.show_notifications);
        view.findViewById(R.id.displayText).setOnClickListener(this);
        view.findViewById(R.id.background).setOnClickListener(this);
        view.findViewById(R.id.screenTextColor).setOnClickListener(this);
        view.findViewById(R.id.disable_system_lock_btn).setOnClickListener(this);
        view.findViewById(R.id.lock_now).setOnClickListener(this);
        view.findViewById(R.id.forgot_btn).setOnClickListener(this);
        view.findViewById(R.id.change_code_btn).setOnClickListener(this);
        enable.setOnCheckedChangeListener(this);
        vibrate.setOnCheckedChangeListener(this);
        show_notifications.setOnCheckedChangeListener(this);
        skip.setOnCheckedChangeListener(this);
        sleep.setOnCheckedChangeListener(this);
        hidden.setOnCheckedChangeListener(this);
    }
    void applyPreferences() {
        isOnCreate=true;
        boolean isEnabled = lockManager.isLock_enabled();
        enable.setChecked(isEnabled);
        hidden.setEnabled(isEnabled);
        vibrate.setEnabled(isEnabled);
        vibrate.setChecked(lockManager.isVibrate());
        show_notifications.setEnabled(isEnabled);
        show_notifications.setChecked(lockManager.isShow_notifications());
        sleep.setEnabled(isEnabled);
        skip.setEnabled(!(!isEnabled||!lockManager.isSet()));
        boolean isEnabled2 = lockManager.isSkip_gestures();
        skip.setChecked(isEnabled2);
        sleep.setChecked(lockManager.isSleep_on_tap());
        sleep.setEnabled(!isEnabled2&&isEnabled);
        hidden.setChecked(lockManager.isFast_unLock());
        isOnCreate=false;
    }
    void firstTime() {
        if (lockManager.isFirst_Time()) {
            try {
                change_code_btnClick();
            }catch (Exception e){e.printStackTrace();}
            lockManager.setIsFirst_Time(false);
        }
    }
    public void setGallery_listener(LockListener galleryListener){
        gallery_listener=galleryListener;
    }
    private void openGallery() {
        gallery_listener.onLockInteraction("openGallery");
    }
    void enableClick(boolean isEnabled){
        lockManager.setIsLock_enabled(isEnabled);
        hidden.setEnabled(isEnabled);
        vibrate.setEnabled(isEnabled);
        show_notifications.setEnabled(isEnabled);
        skip.setEnabled(isEnabled);
        sleep.setEnabled(isEnabled&&!skip.isChecked());
    }
    void Skip_gesturesClick(boolean isEnabled){
        lockManager.setIsSkip_gestures(isEnabled);
        sleep.setEnabled(!isEnabled);
    }
    void notificationClick(boolean is){
        if (is) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.show_notifications)
                    .setMessage("Please Enable from\n" +
                            "Setting > Security > Notifications\n or \n Setting > Status bar\n" +
                            "in order to show notifications in Screen.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            lockManager.setShow_notifications(true);
                            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                        }}).show();
            }else
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.show_notifications)
                        .setMessage("This functionality is not available for your phone,sorry.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }}).show();
                show_notifications.setChecked(false);
            }
        } else {lockManager.setShow_notifications(false);}
    }
    void lock_nowClick(){
        if (enable.isChecked()) {MyApp.lock();} else {Toast.makeText(context, R.string.locker_not_enabled, Toast.LENGTH_SHORT).show();}
    }
    void sleepClick(boolean is_sleep){
        if (is_sleep) {
            DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName adminReceiver = new ComponentName(context, DeviceAdmin.class);
            boolean admin = policyManager.isAdminActive(adminReceiver);
            if (admin) {lockManager.setIsSleep_on_tap(true);}
            else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.double_tap_to_sleep)
                        .setMessage("Device admin is not enabled!\n" +
                                "Enable from\n" +
                                "Setting > Security > Device admin\n" +
                                "in order to use this feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                disable_system_lock_btnClick();
                            }}).show();
                sleep.setChecked(false);
            }
        } else {lockManager.setIsSleep_on_tap(false);}
    }
    void hiddenClick(boolean is_hidden){
        if (is_hidden) {
            lockManager.setIsFast_unLock(true);
            new AlertDialog.Builder(getActivity())
                    .setTitle("Secret Emergency unlock")
                    .setMessage("This Feature was made to make you unlock screen when you are in hurry, also a hidden way to unlock if you are bored of unlock gesture\n\n\nUsage:\n\n-Press on the clock text.\n-Voila\n\n\nNote:\nThis will not work when the pin security is active")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Do nothing
                        }}).show();
        } else {lockManager.setIsFast_unLock(false);}
    }
    void backgroundClick(){
        if (enable.isChecked()) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Background")
                    .setMessage("Select Background")
                    .setPositiveButton("Picture", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            openGallery();}})
                    .setNeutralButton("Color", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String colo = String.valueOf(Color.WHITE);
                            if(lockManager.isBackground_Color())
                                colo = lockManager.getLockBackground();
                            ColorDialog colorDialog = new ColorDialog(getActivity(), Integer.parseInt(colo),true, new ColorDialog.OnColorPickerListener() {
                                @Override
                                public void onOk(ColorDialog dialog, int color) {
                                    lockManager.setIsBackground_Color(true);
                                    lockManager.setLockBackground(color+"");
                                }
                                @Override
                                public void onCancel(ColorDialog dialog) {}
                            });
                            colorDialog.show();
                        }})
                    .setNegativeButton("Default", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            lockManager.setIsBackground_Color(false);
                            lockManager.setLockBackground("");
                        }
                    });
            AlertDialog alertDialog = alert.create();
            alertDialog.show();
        } else {
            Toast.makeText(context, R.string.locker_not_enabled, Toast.LENGTH_SHORT).show();
        }
    }
    void screenTextColorClick(){
        ColorDialog dialog = new ColorDialog(getActivity(), lockManager.getTheme_Color(),true, new ColorDialog.OnColorPickerListener() {
            @Override
            public void onOk(ColorDialog dialog, int color) {
                lockManager.setTheme_Color(color);
            }
            @Override
            public void onCancel(ColorDialog dialog) {}
        });
        dialog.show();
    }
    void displayTextClick(){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.pref_display_name);
        alert.setMessage("Please write Text here");
        final EditText input1 = new EditText(context);
        input1.setText(lockManager.getDisplay_Name());
        alert.setView(input1);
        alert.setPositiveButton(R.string.save,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        lockManager.setDisplay_Name(input1.getText().toString());
                    }
                });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.create().show();
    }
    void Forgot_btnClick(){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.pref_header_security);
        final Forgot forgot = new Forgot(context,lockManager,true);
        forgot.setOnForgotInteractionListener(new LockListener() {
            @Override
            public void onLockInteraction(String action) {
                Toast.makeText(context, action, Toast.LENGTH_LONG).show();
            }
        });
        alert.setView(forgot);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.create().show();
    }
    void change_code_btnClick(){
        startActivity(new Intent(getActivity(), ChangeCode.class));
    }
    void disable_system_lock_btnClick(){
        startActivity(new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD));
    }
    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isOnCreate){
            int id=buttonView.getId();
            switch (id){
                case R.id.enable:enableClick(isChecked);break;
                case R.id.vibrate:lockManager.setVibrate(isChecked);break;
                case R.id.skip:Skip_gesturesClick(isChecked);break;
                case R.id.sleep:sleepClick(isChecked);break;
                case R.id.hidden:hiddenClick(isChecked);break;
                case R.id.show_notifications:notificationClick(isChecked);break;
                default:break;
            }
        }
        }
    @Override public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.lock_now:lock_nowClick();break;
            case R.id.screenTextColor:screenTextColorClick();break;
            case R.id.background:backgroundClick();break;
            case R.id.displayText:displayTextClick();break;
            case R.id.forgot_btn:Forgot_btnClick();break;
            case R.id.change_code_btn:change_code_btnClick();break;
            case R.id.disable_system_lock_btn:disable_system_lock_btnClick();break;
            default:break;
        }
    }

}