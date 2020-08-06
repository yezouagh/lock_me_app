package com.yez_inc.lock_me_app.lockmeapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yez_inc.lock_me_app.R;

public class NotificationElement  extends FrameLayout {
    TextView img,title,text;
    public NotificationElement(Context context, AttributeSet attrs) {
        super(context, attrs);init(context);
    }
    public NotificationElement(Context context) {
        super(context);init(context);
    }
    public NotificationElement(Context context,int _img, String _text, String _title,int color) {
        super(context);init(context);
        title.setText(_title);
        text.setText(_text);
        img.setBackgroundResource(_img);
    }
    public NotificationElement(Context context, Drawable _img, String _text, String _title,int color) {
        super(context);init(context);
        title.setText(_title);
        if (color != Color.WHITE) {
        title.setTextColor(color);
        text.setTextColor(color);
        ((TextView) findViewById(R.id.delete)).setTextColor(color);
        }
        text.setText(_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            img.setBackground(_img);
        }else //noinspection deprecation
            img.setBackgroundDrawable(_img);
    }
    private void init(Context context){
        inflate(context, R.layout.notification, this);
        img = (TextView) findViewById(R.id.img);
        text = (TextView) findViewById(R.id.text);
        title = (TextView) findViewById(R.id.title);
        final FrameLayout layout = this;
        findViewById(R.id.delete).setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                ((ViewGroup)layout.getParent()).removeView(layout);
            }
        });
    }
}
