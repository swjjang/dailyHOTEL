package com.twoheart.dailyhotel.firebase.fcm;

import com.appboy.Appboy;
import com.daily.base.util.ExLog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class AppboyFirebaseInstanceIdService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        try
        {
            String token = FirebaseInstanceId.getInstance().getToken();
            Appboy.getInstance(getApplicationContext()).registerAppboyPushMessages(token);
        } catch (Exception e)
        {
            ExLog.e("Exception while automatically registering Firebase token with Appboy." + e.toString());
        }
    }
}