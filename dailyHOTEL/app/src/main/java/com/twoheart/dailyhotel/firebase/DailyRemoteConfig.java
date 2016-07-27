package com.twoheart.dailyhotel.firebase;

import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigFetchThrottledException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;

import org.json.JSONObject;

public class DailyRemoteConfig
{
    private static DailyRemoteConfig mInstance = null;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public synchronized static DailyRemoteConfig getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new DailyRemoteConfig();
        }
        return mInstance;
    }

    private DailyRemoteConfig()
    {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(Constants.DEBUG).build());
    }

    public void requestRemoteConfig()
    {
        mFirebaseRemoteConfig.fetch(0L).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful() == true)
                {
                    mFirebaseRemoteConfig.activateFetched();
                } else
                {
                    return;
                }

                String androidUpdateVersion = mFirebaseRemoteConfig.getString("androidUpdateVersion");
                String androidPaymentType = mFirebaseRemoteConfig.getString("androidPaymentType");
                String companyInfo = mFirebaseRemoteConfig.getString("companyInfo");
                String androidSplashImageLink = mFirebaseRemoteConfig.getString("androidSplashImageLink");
                String androidSplashImageUpdateTime = mFirebaseRemoteConfig.getString("androidSplashImageUpdateTime");
                String androidServiceShutdown = mFirebaseRemoteConfig.getString("androidServiceShutdown");
                String androidServiceShutdownMessage = mFirebaseRemoteConfig.getString("androidServiceShutdownMessage");

//                try
//                {
//                    ExLog.d("androidUpdateVersion : " + new JSONObject(androidUpdateVersion).toString());
//                    ExLog.d("androidPaymentType : " + new JSONObject(androidPaymentType).toString());
//                    ExLog.d("companyInfo : " + new JSONObject(companyInfo).toString());
//                    ExLog.d("androidSplashImageLink : " + new JSONObject(androidSplashImageLink).toString());
//                    ExLog.d("androidSplashImageUpdateTime : " + new JSONObject(androidSplashImageUpdateTime).toString());
//                    ExLog.d("androidServiceShutdown : " + new JSONObject(androidServiceShutdown).toString());
//                    ExLog.d("androidServiceShutdownMessage : " + new JSONObject(androidServiceShutdownMessage).toString());
//                } catch (Exception e)
//                {
//                    ExLog.d(e.toString());
//                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                if (Constants.DEBUG == false)
                {
                    Crashlytics.getInstance().logException(e);
                } else
                {
                    ExLog.e(e.toString());
                }
            }
        });
    }
}