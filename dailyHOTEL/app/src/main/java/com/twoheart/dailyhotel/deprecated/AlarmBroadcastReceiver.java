package com.twoheart.dailyhotel.deprecated;

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

import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.push.PushDialogActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.WakeLock;

/**
 * 판매가 종료된 시간일때 오픈 알람받기를 설정 한 경우 호출되는 브로드캐스트 리시버
 *
 * @author jangjunho
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String title = context.getString(R.string.alarm_title);
        String msg = context.getString(R.string.alarm_msg);
        String ticker = context.getString(R.string.alarm_ticker);

        DailyPreference.getInstance(context).setEnabledOpeningAlarm(false);

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

        Intent callIntent = new Intent(context, LauncherActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, callIntent, 0);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title) //
            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg).setTicker(ticker) //
            .setAutoCancel(true) //
            .setSmallIcon(R.mipmap.ic_launcher).setVibrate(new long[]{100, 500, 100, 500}).setWhen(System.currentTimeMillis()).setContentIntent(pendingIntent);

        notificationManager.notify(0, builder.build());
    }
}
