package com.twoheart.dailyhotel.gcm;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

import com.appboy.AppboyGcmReceiver;
import com.appboy.Constants;
import com.appboy.push.AppboyNotificationUtils;
import com.appboy.support.StringUtils;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.util.ExLog;

public class AppboyBroadcastReceiver extends BroadcastReceiver
{
    private static final String TAG = String.format("%s.%s", Constants.APPBOY_LOG_TAG_PREFIX, AppboyBroadcastReceiver.class.getName());
    public static final String SOURCE_KEY = "source";
    public static final String DESTINATION_VIEW = "destination";
    public static final String HOME = "home";
    public static final String FEED = "feed";
    public static final String FEEDBACK = "feedback";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String packageName = context.getPackageName();
        String pushReceivedAction = packageName + AppboyNotificationUtils.APPBOY_NOTIFICATION_RECEIVED_SUFFIX;
        String notificationOpenedAction = packageName + AppboyNotificationUtils.APPBOY_NOTIFICATION_OPENED_SUFFIX;
        String action = intent.getAction();
        ExLog.d(TAG + String.format("Received intent with action %s", action));

        if (pushReceivedAction.equals(action))
        {
            ExLog.d(TAG + "Received push notification.");
            if (AppboyNotificationUtils.isUninstallTrackingPush(intent.getExtras()))
            {
                ExLog.d(TAG + "Got uninstall tracking push");
            }
        } else if (notificationOpenedAction.equals(action))
        {
            if (intent.getBooleanExtra(Constants.APPBOY_ACTION_IS_CUSTOM_ACTION_KEY, false))
            {
                Toast.makeText(context, "You clicked a Droidboy custom action!", Toast.LENGTH_LONG).show();
            } else
            {
                Bundle extras = getPushExtrasBundle(intent);

                // If a deep link exists, start an ACTION_VIEW intent pointing at the deep link.
                // The intent returned from getStartActivityIntent() is placed on the back stack.
                // Otherwise, start the intent defined in getStartActivityIntent().
                String deepLink = intent.getStringExtra(Constants.APPBOY_PUSH_DEEP_LINK_KEY);
                if (!StringUtils.isNullOrBlank(deepLink))
                {
                    if (extras == null)
                    {
                        Intent uriIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntent(getStartActivityIntent(context, null));
                        stackBuilder.addNextIntent(uriIntent);
                        try
                        {
                            stackBuilder.startActivities();
                        } catch (ActivityNotFoundException e)
                        {
                            ExLog.w(TAG + String.format("Could not find appropriate activity to open for deep link %s.", deepLink));
                        }
                    } else
                    {
                        Intent uriIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink)).putExtras(extras);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addNextIntent(getStartActivityIntent(context, extras));
                        stackBuilder.addNextIntent(uriIntent);
                        try
                        {
                            stackBuilder.startActivities(extras);
                        } catch (Exception e)
                        {
                            Intent launcherIntent = new Intent(context, LauncherActivity.class);
                            intent.setData(Uri.parse(deepLink));
                            context.startActivity(launcherIntent);
                            ExLog.w(TAG + String.format("Could not find appropriate activity to open for deep link %s.", deepLink));
                        }
                    }
                } else
                {
                    context.startActivity(getStartActivityIntent(context, extras));
                }
            }
        } else
        {
            ExLog.d(TAG + String.format("Ignoring intent with unsupported action %s", action));
        }
    }

    private Intent getStartActivityIntent(Context context, Bundle extras)
    {
        Intent startActivityIntent = new Intent(context, LauncherActivity.class);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (extras != null)
        {
            startActivityIntent.putExtras(extras);
        }
        return startActivityIntent;
    }

    private Bundle getPushExtrasBundle(Intent intent)
    {
        Bundle extras = intent.getBundleExtra(Constants.APPBOY_PUSH_EXTRAS_KEY);
        if (extras == null)
        {
            extras = new Bundle();
        }
        extras.putString(AppboyGcmReceiver.CAMPAIGN_ID_KEY, intent.getStringExtra(AppboyGcmReceiver.CAMPAIGN_ID_KEY));
        extras.putString(SOURCE_KEY, Constants.APPBOY);
        return extras;
    }
}