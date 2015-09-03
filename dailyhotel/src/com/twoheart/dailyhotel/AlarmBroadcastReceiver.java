package com.twoheart.dailyhotel;

import com.twoheart.dailyhotel.activity.PushDialogActivity;
import com.twoheart.dailyhotel.util.DailyHotelPreference;
import com.twoheart.dailyhotel.util.WakeLock;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

/**
 * 판매가 종료된 시간일때 오픈 알람받기를 설정 한 경우 호출되는 브로드캐스트 리시버
 * 
 * @author jangjunho
 *
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String title = context.getString(R.string.alarm_title);

		boolean enabledHotelAlarm = DailyHotelPreference.getInstance(context).getEnabledHotelAlarm();
		boolean enabledFnBAlarm = DailyHotelPreference.getInstance(context).getEnabledFnBAlarm();

		String param = null;

		if (enabledHotelAlarm == true && enabledFnBAlarm == true)
		{
			param = String.format("%s, %s", context.getString(R.string.label_hotel), context.getString(R.string.label_fnb));
		} else if (enabledHotelAlarm == true)
		{
			param = context.getString(R.string.label_hotel);
		} else if (enabledFnBAlarm == true)
		{
			param = context.getString(R.string.label_hotel);
		} else
		{
			return;
		}

		String msg = context.getString(R.string.alarm_msg, param);
		String ticker = context.getString(R.string.alarm_ticker, param);

		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

		boolean isScreenOn;

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
		{
			isScreenOn = pm.isScreenOn();
		} else
		{
			isScreenOn = pm.isInteractive();
		}

		if (isScreenOn == false)
		{
			// 스크린 꺼져있음
			// PushDialogActivity에서 release해줌.
			WakeLock.acquireWakeLock(context, PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP);
			KeyguardManager manager = (KeyguardManager) context.getSystemService(Activity.KEYGUARD_SERVICE);
			KeyguardLock lock = manager.newKeyguardLock(Context.KEYGUARD_SERVICE);
			lock.disableKeyguard();

			Intent i = new Intent(context, PushDialogActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT), 0);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setContentTitle(title) //
		.setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg).setTicker(ticker) //
		.setAutoCancel(true) //
		.setSmallIcon(R.drawable.img_ic_appicon_feature).setVibrate(new long[] { 100, 500, 100, 500 }).setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent);

		notificationManager.notify(0, builder.build());
	}
}
