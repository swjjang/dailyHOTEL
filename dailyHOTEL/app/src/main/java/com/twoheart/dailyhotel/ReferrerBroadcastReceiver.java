package com.twoheart.dailyhotel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReferrerBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        (new com.google.android.gms.analytics.CampaignTrackingReceiver()).onReceive(context, intent);
        (new com.google.ads.conversiontracking.InstallReceiver()).onReceive(context, intent);
        (new com.mobileapptracker.Tracker()).onReceive(context, intent);
    }
}
