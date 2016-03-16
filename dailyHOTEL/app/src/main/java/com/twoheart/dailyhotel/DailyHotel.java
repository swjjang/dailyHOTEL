/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * DailyHotel
 * <p>
 * Android의 Application을 상속받은 서브 클래스로서 어플리케이션의 가장
 * 기본이 되는 클래스이다. 이 클래스에서는 어플리케이션에서 전역적으로 사용되
 * 는 GoogleAnalytics와 폰트, Volley, Universal Image Loder를
 * 초기화하는 작업을 생성될 시(onCreate)에 수행한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.FontManager;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DailyHotel extends Application implements Constants
{
    private static volatile DailyHotel mInstance = null;
    private static volatile Activity mCurrentActivity = null;
    public static String VERSION;

    @Override
    public void onCreate()
    {
        super.onCreate();

        if (DEBUG == false)
        {
            //            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
            //            {
            //                @Override
            //                public void uncaughtException(Thread thread, Throwable ex)
            //                {
            //                    Util.restartExitApp(getApplicationContext());
            //                }
            //            });

            Fabric.with(this, new com.crashlytics.android.Crashlytics(), new Crashlytics());
        }

        mInstance = this;

        Util.setLocale(this, Locale.KOREAN);

        // 버전 정보 얻기
        VERSION = Util.getAppVersion(getApplicationContext());

        initializeVolley();
        initializeAnalytics();
        Util.initializeFresco(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
        KakaoSDK.init(new KakaoSDKAdapter());
        FontManager.getInstance(getApplicationContext());
    }

    private void initializeAnalytics()
    {
        AnalyticsManager.getInstance(getApplicationContext());
    }

    private void initializeVolley()
    {
        VolleyHttpClient.init(this);
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
                    return new AuthType[]{AuthType.KAKAO_TALK};
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
