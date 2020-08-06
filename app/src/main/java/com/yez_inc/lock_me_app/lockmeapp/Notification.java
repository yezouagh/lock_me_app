package com.yez_inc.lock_me_app.lockmeapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Notification extends NotificationListenerService {
	Context context;
	@Override public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override public void onNotificationPosted(StatusBarNotification sbn) {
		Bundle extras = sbn.getNotification().extras;
		String title = extras.getString("android.title");
		@SuppressWarnings("ConstantConditions") String text = extras.getCharSequence("android.text").toString();
		String pack = sbn.getPackageName();
		Intent msgrcv = new Intent("Msg");
		msgrcv.putExtra("title", title);
		msgrcv.putExtra("text", text);
		msgrcv.putExtra("PackageName", pack);
		LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override public void onNotificationRemoved(StatusBarNotification sbn) {}
}
