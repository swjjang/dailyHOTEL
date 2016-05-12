package com.twoheart.dailyhotel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DailyHotel extends android.support.multidex.MultiDexApplication implements Constants
{
    private static volatile DailyHotel mInstance = null;
    private static volatile Activity mCurrentActivity = null;
    public static String VERSION;
    public static String AUTHORIZATION;

    @Override
    public void onCreate()
    {
        super.onCreate();

        if (DEBUG == false)
        {
            final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            {
                @Override
                public void uncaughtException(Thread thread, Throwable ex)
                {
                    if (ex.getClass().equals(OutOfMemoryError.class) == true)
                    {
                        Util.finishOutOfMemory(getApplicationContext());
                    } else
                    {
                        if (uncaughtExceptionHandler != null)
                        {
                            uncaughtExceptionHandler.uncaughtException(thread, ex);
                        }
                    }

                    //                    Util.restartExitApp(getApplicationContext());
                }
            });

            Fabric.with(this, new com.crashlytics.android.Crashlytics());
        }

        mInstance = this;

        Util.setLocale(this, Locale.KOREAN);

        // 버전 정보 얻기
        VERSION = Util.getAppVersion(getApplicationContext());

        AUTHORIZATION = DailyPreference.getInstance(getApplicationContext()).getAuthorization();

        initializeVolley(getApplicationContext());
        initializeAnalytics(getApplicationContext());
        Util.initializeFresco(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        KakaoSDK.init(new KakaoSDKAdapter());
        FontManager.getInstance(getApplicationContext());
    }

    private void initializeAnalytics(Context context)
    {
        AnalyticsManager.getInstance(context);
    }

    private void initializeVolley(Context context)
    {
        VolleyHttpClient.getInstance(context).newRequestQueue(context);
    }

    public static DailyHotel getGlobalApplicationContext()
    {
        return mInstance;
    }

    public static void setCurrentActivity(Activity currentActivity)
    {
        DailyHotel.mCurrentActivity = currentActivity;
    }

    public static Activity getCurrentActivity()
    {
        return mCurrentActivity;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter
    {
        /**
         * Session Config에 대해서는 default값들이 존재한다.
         * 필요한 상황에서만 override해서 사용하면 됨.
         *
         * @return Session의 설정값.
         */
        @Override
        public ISessionConfig getSessionConfig()
        {
            return new ISessionConfig()
            {
                @Override
                public AuthType[] getAuthTypes()
                {
                    return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
                }

                @Override
                public boolean isUsingWebviewTimer()
                {
                    return false;
                }

                @Override
                public ApprovalType getApprovalType()
                {
                    return ApprovalType.INDIVIDUAL;
                }

                @Override
                public boolean isSaveFormData()
                {
                    return false;
                }
            };
        }

        @Override
        public IApplicationConfig getApplicationConfig()
        {
            return new IApplicationConfig()
            {
                @Override
                public Activity getTopActivity()
                {
                    return DailyHotel.getCurrentActivity();
                }

                @Override
                public Context getApplicationContext()
                {
                    return DailyHotel.getGlobalApplicationContext();
                }
            };
        }
    }
}
