package com.twoheart.dailyhotel;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.repository.local.CartLocalImpl;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.gourmet.payment.GourmetPaymentActivity;
import com.daily.dailyhotel.screen.home.gourmet.thankyou.GourmetThankYouActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.list.StayTabActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.payment.StayPaymentActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.thankyou.StayThankYouActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.payment.StayOutboundPaymentActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.thankyou.StayOutboundThankYouActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;
import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.twoheart.dailyhotel.network.RetrofitHttpClient;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.home.category.list.StayCategoryTabActivity;
import com.twoheart.dailyhotel.screen.home.category.nearby.StayCategoryNearByActivity;
import com.twoheart.dailyhotel.util.AppResearch;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.GoogleAnalyticsManager;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class DailyHotel extends android.support.multidex.MultiDexApplication implements Constants
{
    private static volatile DailyHotel mInstance = null;
    public static String VERSION_CODE;
    public static String VERSION;
    public static String AUTHORIZATION;
    public static String GOOGLE_ANALYTICS_CLIENT_ID;

    private static boolean mIsSuccessTMapAuth = false;

    @Override
    public void onCreate()
    {
        super.onCreate();

        // URL 만들때 사용
        //        com.twoheart.dailyhotel.util.Crypto.getUrlEncoder("");

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

        Fabric.with(this, new Crashlytics());

        FirebaseApp.initializeApp(this);

        mInstance = this;

        mIsSuccessTMapAuth = false;

        Util.setLocale(getApplicationContext(), Locale.KOREAN);

        // 버전 정보 얻기
        VERSION_CODE = VersionUtils.getAppVersionCode(getApplicationContext());
        VERSION = VersionUtils.getAppVersionName(getApplicationContext());

        AUTHORIZATION = DailyUserPreference.getInstance(getApplicationContext()).getAuthorization();

        String preferenceVersion = DailyPreference.getInstance( //
            getApplicationContext()).getFirstAppVersion();

        DailyRemoteConfigPreference.getInstance(getApplicationContext());

        if (DailyTextUtils.isTextEmpty(preferenceVersion) == true)
        {
            DailyPreference.getInstance(getApplicationContext()).setFirstAppVersion(VERSION_CODE);
        }

        initializeNetwork(getApplicationContext());
        initializeAnalytics(getApplicationContext());
        Util.initializeFresco(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());

        try
        {
            KakaoSDK.init(new KakaoSDKAdapter());
        } catch (KakaoSDK.AlreadyInitializedException e)
        {
            ExLog.d(e.toString());
        }

        FontManager.getInstance(getApplicationContext());

        // 장바구니 초기화
        new CartLocalImpl(getApplicationContext()).clearGourmetCart().subscribe();

        registerActivityLifecycleCallbacks(new DailyActivityLifecycleCallbacks());
    }

    private void initializeAnalytics(Context context)
    {
        AnalyticsManager.getInstance(context);

        GoogleAnalyticsManager googleAnalyticsManager = AnalyticsManager.getInstance(getApplicationContext()).getGoogleAnalyticsManager();

        if (googleAnalyticsManager != null)
        {
            GOOGLE_ANALYTICS_CLIENT_ID = googleAnalyticsManager.getClientId();
        }
    }

    private void initializeNetwork(Context context)
    {
        RetrofitHttpClient.getInstance(context);
    }

    public static DailyHotel getGlobalApplicationContext()
    {
        return mInstance;
    }

    public static boolean isLogin()
    {
        return DailyTextUtils.isTextEmpty(AUTHORIZATION) == false;
    }

    public static boolean isSuccessTMapAuth()
    {
        return mIsSuccessTMapAuth;
    }

    public static void setIsSuccessTMapAuth(boolean isSuccessTMapAuth)
    {
        DailyHotel.mIsSuccessTMapAuth = isSuccessTMapAuth;
    }

    private static class KakaoSDKAdapter extends KakaoAdapter
    {
        KakaoSDKAdapter()
        {
        }

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
                public boolean isSecureMode()
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
                public Context getApplicationContext()
                {
                    return getGlobalApplicationContext();
                }
            };
        }
    }

    private class DailyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks
    {
        private int mRunningActivity = 0;
        private AppResearch mAppResearch;

        DailyActivityLifecycleCallbacks()
        {
            mAppResearch = new AppResearch(getApplicationContext());
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle)
        {
            AnalyticsManager.getInstance(activity).onActivityCreated(activity, bundle);
        }

        @Override
        public void onActivityStarted(Activity activity)
        {
            AnalyticsManager.getInstance(activity).onActivityStarted(activity);

            if (++mRunningActivity == 1)
            {
                // 30 분이 지나면 재시작
                final long DELAY_TIME = 30 * 60 * 1000;

                // return to foreground
                long currentTime = DailyCalendar.getInstance().getTimeInMillis();
                long backgroundTime = DailyPreference.getInstance(activity).getBackgroundAppTime();

                if (backgroundTime != 0 && currentTime - backgroundTime > DELAY_TIME)
                {
                    Util.restartApp(activity);

                    new CartLocalImpl(activity).clearGourmetCart().subscribe();
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity)
        {
            AnalyticsManager.getInstance(activity).onActivityResumed(activity);

            if (activity instanceof StayDetailActivity || activity instanceof GourmetDetailActivity || activity instanceof StayOutboundDetailActivity)
            {
                return;
            }

            if (activity instanceof StayTabActivity//
                || activity instanceof StayPaymentActivity//
                || activity instanceof StayThankYouActivity//
                || activity instanceof StayCategoryTabActivity//
                || activity instanceof StayCategoryNearByActivity)
            {
                mAppResearch.onResume("스테이", -1);
            } else if (activity instanceof GourmetMainActivity//
                || activity instanceof GourmetPaymentActivity//
                || activity instanceof GourmetThankYouActivity)
            {
                mAppResearch.onResume("고메", -1);
            } else if (activity instanceof StayOutboundListActivity//
                || activity instanceof StayOutboundPaymentActivity//
                || activity instanceof StayOutboundThankYouActivity)
            {
                mAppResearch.onResume("outbound_스테이", -1);
            } else
            {
                mAppResearch.onResume("etc", -1);
            }
        }

        @Override
        public void onActivityPaused(Activity activity)
        {
            AnalyticsManager.getInstance(activity).onActivityPaused(activity);

            if (activity instanceof StayDetailActivity || activity instanceof GourmetDetailActivity || activity instanceof StayOutboundDetailActivity)
            {
                return;
            }

            if (activity instanceof StayTabActivity//
                || activity instanceof StayPaymentActivity//
                || activity instanceof StayThankYouActivity//
                || activity instanceof StayCategoryTabActivity//
                || activity instanceof StayCategoryNearByActivity)
            {
                mAppResearch.onPause("스테이", -1);
            } else if (activity instanceof GourmetMainActivity//
                || activity instanceof GourmetPaymentActivity//
                || activity instanceof GourmetThankYouActivity)
            {
                mAppResearch.onPause("고메", -1);
            } else if (activity instanceof StayOutboundListActivity//
                || activity instanceof StayOutboundPaymentActivity//
                || activity instanceof StayOutboundThankYouActivity)
            {
                mAppResearch.onPause("outbound_스테이", -1);
            } else
            {
                mAppResearch.onPause("etc", -1);
            }
        }

        @Override
        public void onActivityStopped(Activity activity)
        {
            AnalyticsManager.getInstance(activity).onActivityStopped(activity);

            if (--mRunningActivity == 0)
            {
                // go background
                DailyPreference.getInstance(activity).setBackgroundAppTime(DailyCalendar.getInstance().getTimeInMillis());
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
        {
            AnalyticsManager.getInstance(activity).onActivitySaveInstanceState(activity, bundle);
        }

        @Override
        public void onActivityDestroyed(Activity activity)
        {
            AnalyticsManager.getInstance(activity).onActivityDestroyed(activity);
        }
    }
}
