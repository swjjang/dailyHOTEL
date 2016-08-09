package com.twoheart.dailyhotel.firebase;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

public class DailyRemoteConfig
{
    private static DailyRemoteConfig mInstance = null;

    private Context mContext;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public synchronized static DailyRemoteConfig getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new DailyRemoteConfig(context);
        }

        return mInstance;
    }

    private DailyRemoteConfig(Context context)
    {
        mContext = context;
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
                String androidSplashImageUrl = mFirebaseRemoteConfig.getString("androidSplashImageLink");
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

                // 이미지 로딩 관련(추후 진행)
                //                processIntroImage(androidSplashImageUpdateTime, androidSplashImageUrl);
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

    private void processIntroImage(Context context, String updateTime, String imageUrl)
    {
        if (Util.isTextEmpty(updateTime, imageUrl) == true)
        {
            return;
        }

        // 이미지 로딩 관련
        int densityDpi = context.getResources().getDisplayMetrics().densityDpi;
        String dpi;

        if (densityDpi < 240)
        {
            dpi = "hdpi";
        } else if (densityDpi < 640)
        {
            dpi = "xhdpi";
        } else
        {
            dpi = "xxxhdpi";
        }

        try
        {
            JSONObject updateTimeJSONObject = new JSONObject(updateTime);
            JSONObject imageUrlJSONObject = new JSONObject(imageUrl);

            String url = imageUrlJSONObject.getString(dpi);
            String currentVersion = DailyPreference.getInstance(context).getIntroImageVersion();
            String newVersion = updateTimeJSONObject.getString("time");

            // 기존 버전과 비교해서 다르면 다운로드를 시도한다.
            if (Util.isTextEmpty(currentVersion) == true || currentVersion.equalsIgnoreCase(newVersion) == false)
            {
                new ImageDownloadAsyncTask(context).execute(url, newVersion);
            }
        } catch (JSONException e)
        {
            ExLog.d(e.toString());
        }
    }
}