package com.twoheart.dailyhotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appboy.push.AppboyNotificationUtils;

public class AppboyBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String packageName = context.getPackageName();
        String pushReceivedAction = packageName + AppboyNotificationUtils.APPBOY_NOTIFICATION_RECEIVED_SUFFIX;
        String notificationOpenedAction = packageName + AppboyNotificationUtils.APPBOY_NOTIFICATION_OPENED_SUFFIX;
        String action = intent.getAction();

        if (pushReceivedAction.equals(action))
        {
            if (AppboyNotificationUtils.isUninstallTrackingPush(intent.getExtras()))
            {
            }
        } else if (notificationOpenedAction.equals(action))
        {
            AppboyNotificationUtils.routeUserWithNotificationOpenedIntent(context, intent);
        } else
        {
        }
    }
}