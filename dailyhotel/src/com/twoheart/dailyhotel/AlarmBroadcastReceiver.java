package com.twoheart.dailyhotel;

import java.util.List;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.PushDialogActivity;
import com.twoheart.dailyhotel.activity.SplashActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class AlarmBroadcastReceiver extends BroadcastReceiver{
	
	private static PowerManager.WakeLock screenWakeLock;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String title = "dailyHOTEL";
		String msg = "오늘의 호텔이 도착했습니다.\n지금 데일리호텔에서 확인하세요!";
		String ticker = "오늘의 호텔이 도착했습니다!.";

		
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();
		
		if(isScreenOn) {	// 스크린 켜져있음
			
		} else {		// 스크린 꺼져있음
			acquireWakeLock(context);
			KeyguardManager manager = (KeyguardManager)context.getSystemService(Activity.KEYGUARD_SERVICE);  
			KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);  
			lock.disableKeyguard();  
			Intent i = new Intent(context, PushDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
			context.startActivity(i);
		}
		
		
		// 해당 어플을 실행하는 이벤트를 하고싶을 때 아래 주석을 풀어주세요
		PendingIntent pendingIntent = null;

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> proceses = am.getRunningAppProcesses();
		
		boolean isRunning = false;
		
		for(RunningAppProcessInfo process : proceses) {
			if(process.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				if(process.processName.equals("com.twoheart.dailyhotel")) {
					pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).
							setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
			                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
			                | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
					
					isRunning = true;
					
					break;
				} 
				
			} else if(process.processName.equals("com.twoheart.dailyhotel")) {
				pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).
						setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
		                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
		                | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
				
				isRunning = true;
				
				break;
			}
		}
		
		if(!isRunning) {
			pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, SplashActivity.class).
					setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
	                | Intent.FLAG_ACTIVITY_CLEAR_TOP 
	                | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		}
		
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Activity.NOTIFICATION_SERVICE);
		
		Notification notification = new Notification();
		notification.icon = R.drawable.dh_ic_home_72;
		notification.tickerText = ticker;
		notification.when = System.currentTimeMillis();
		notification.vibrate = new long[] { 500, 100, 500, 100 };
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(context, title, msg, pendingIntent);
		notificationManager.notify(0, notification);
	}
	
	private void acquireWakeLock(Context context) {
	    PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
	    screenWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP, context.getClass().getName());

	    if (screenWakeLock != null) {
	        screenWakeLock.acquire();
	    }
	}
}
