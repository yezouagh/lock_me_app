package com.yez_inc.lock_me_app.lockmeapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.yez_inc.lock_me_app.R;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Analog_Clock;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Digital_Clock;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.Phone_State;
import com.yez_inc.lock_me_app.lockmeapp.Widgets.StateReceiver;
import com.yez_inc.lock_me_app.lockmeapp.utils.LockManager;

public class AddWidgets extends Fragment {
    TableLayout widgets;
    TableRow Rows[]=new TableRow[5];
    String Widgets;Context context;
    LockManager lockManager;
    int dial1=R.drawable.clock_dial,dial2=R.drawable.retry,dial22=R.drawable.b,hand_hour1=R.drawable.clock_hour,
            hand_minute1=R.drawable.clock_minute,hand_minute3=R.drawable.clockgoog_minute;
    TableRow.LayoutParams param;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_widgets, container, false);
        context = getContext().getApplicationContext();
        lockManager = new LockManager(context);
        param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,  (int) getResources().getDimension(R.dimen.one_hundred_dp));
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        Widgets=lockManager.getWidgets();
        widgets = (TableLayout)view.findViewById(R.id.widgets);
        Rows[0]=newRow(new Digital_Clock(context));
        Phone_State phone_state = new Phone_State(context,true,new StateReceiver(context, null, null));
        Rows[1]=newRow(phone_state);
        Rows[2]=newRow(new Analog_Clock(context,getDrawable(dial1),getDrawable(dial22), getDrawable(hand_hour1),getDrawable(hand_minute1)));
        Rows[3]=newRow(new Analog_Clock(context,getDrawable(dial2),null, getDrawable(hand_hour1),getDrawable(hand_minute1)));
        Rows[4]=newRow(new Analog_Clock(context,getDrawable(dial1),null, getDrawable(hand_hour1),getDrawable(hand_minute3)));
        for (int i=0;i<Rows.length;i++)
        {
            widgets.addView(Rows[i],params);
            final String finalI = String.valueOf(i);
            if (Widgets.contains(finalI))
                Rows[i].setSelected(true);
            Rows[i].setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    boolean selected=v.isSelected();
                    v.setSelected(!selected);
                    if (!selected){
                        Widgets+=finalI;
                    }else {
                        Widgets = Widgets.replace(finalI,"").trim();
                    }
                    lockManager.setWidgets(Widgets);
                }
            });
        }
        return view;
    }
    TableRow newRow(View v){
        TableRow row = new TableRow(context);
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        row.addView(v,param);
        row.setBackgroundResource(R.drawable.widget_selector);
        return row;
    }
    Drawable getDrawable(int drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(drawable,context.getTheme());
        }else //noinspection deprecation
            return getResources().getDrawable(drawable);
    }
}