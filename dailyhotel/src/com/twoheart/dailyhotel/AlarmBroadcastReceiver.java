package com.twoheart.dailyhotel;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.twoheart.dailyhotel.activity.PushDialogActivity;
import com.twoheart.dailyhotel.util.WakeLock;

public class AlarmBroadcastReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		WaitTimerFragment.isEnabledNotify = false;
		
		String title = context.getString(R.string.alarm_title);
		String msg = context.getString(R.string.alarm_msg);
		String ticker = context.getString(R.string.alarm_ticker);
		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		
		if (!isScreenOn) { // 스크린 꺼져있음
			WakeLock.acquireWakeLock(context, PowerManager.FULL_WAKE_LOCK
					| PowerManager.ACQUIRE_CAUSES_WAKEUP);	// PushDialogActivity에서 release 해줌.
			KeyguardManager manager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE);  
			KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);  
			lock.disableKeyguard();  
			
			Intent i = new Intent(context, PushDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | 
					Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).
				setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT), 0);
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification();
		notification.icon = R.drawable.img_ic_appicon;
		notification.tickerText = ticker;
		notification.when = System.currentTimeMillis();
		notification.vibrate = new long[] { 100, 500, 100, 500 };
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, title, msg, pendingIntent);
		notificationManager.notify(0, notification);
		
	}
	
}
