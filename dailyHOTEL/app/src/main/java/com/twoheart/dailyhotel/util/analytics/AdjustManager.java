package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.AdjustSessionFailure;
import com.adjust.sdk.AdjustSessionSuccess;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.OnSessionTrackingFailedListener;
import com.adjust.sdk.OnSessionTrackingSucceededListener;
import com.appboy.Appboy;
import com.appboy.models.outgoing.AttributionData;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.Map;

/**
 * Created by android_sam on 2016. 9. 8..
 */
public class AdjustManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String APPLICATION_TOKEN = "jkf7ii0lj9xc";
    private static final String ENVIRONMENT = AdjustConfig.ENVIRONMENT_PRODUCTION;

    private String mUserIndex;

    private Context mContext;

    public AdjustManager(Context context)
    {
        mContext = context;

        AdjustConfig config = new AdjustConfig(context, APPLICATION_TOKEN, ENVIRONMENT);

        // change the log level
        config.setLogLevel(LogLevel.VERBOSE);

        // set default tracker
        config.setDefaultTracker("https://app.adjust.com/qbwmpi");

        // set process name
        //        config.setProcessName("com.twoheart.dailyhotel");

        // set attribution delegate
        config.setOnAttributionChangedListener(new OnAttributionChangedListener()
        {
            @Override
            public void onAttributionChanged(AdjustAttribution attribution)
            {
                Appboy.getInstance(mContext).getCurrentUser().setAttributionData(new AttributionData( //
                    attribution.network,//
                    attribution.campaign,//
                    attribution.adgroup,//
                    attribution.creative));

                ExLog.d("Adjust attribution: " + attribution.toString());
            }
        });

        // set event success tracking delegate
        config.setOnEventTrackingSucceededListener(new OnEventTrackingSucceededListener()
        {
            @Override
            public void onFinishedEventTrackingSucceeded(AdjustEventSuccess eventSuccessResponseData)
            {
                ExLog.d("Adjust success event tracking: " + eventSuccessResponseData.toString());
            }
        });

        // set event failure tracking delegate
        config.setOnEventTrackingFailedListener(new OnEventTrackingFailedListener()
        {
            @Override
            public void onFinishedEventTrackingFailed(AdjustEventFailure eventFailureResponseData)
            {
                ExLog.d("Adjust failed event tracking: " + eventFailureResponseData.toString());
            }
        });

        // set session success tracking delegate
        config.setOnSessionTrackingSucceededListener(new OnSessionTrackingSucceededListener()
        {
            @Override
            public void onFinishedSessionTrackingSucceeded(AdjustSessionSuccess sessionSuccessResponseData)
            {
                ExLog.d("Adjust success session tracking: " + sessionSuccessResponseData.toString());
            }
        });

        // set session failure tracking delegate
        config.setOnSessionTrackingFailedListener(new OnSessionTrackingFailedListener()
        {
            @Override
            public void onFinishedSessionTrackingFailed(AdjustSessionFailure sessionFailureResponseData)
            {
                ExLog.d("Adjust failed session tracking: " + sessionFailureResponseData.toString());
            }
        });

        // evaluate deeplink to be launched
        config.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener()
        {
            @Override
            public boolean launchReceivedDeeplink(Uri deeplink)
            {
                ExLog.d("Adjust deepLink to open: " + deeplink);

                if (Uri.EMPTY.equals(deeplink) == false)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(deeplink);

                    mContext.startActivity(intent);
                }

                return true;
            }
        });

        // allow to send in the background
        config.setSendInBackground(true);

        // enable event buffering
        config.setEventBufferingEnabled(true);

        Adjust.onCreate(config);

        // put the SDK in offline mode
        //        Adjust.setOfflineMode(true);

        // disable the SDK
        //        Adjust.setEnabled(true);

    }

    @Override
    void recordScreen(String screen)
    {
        //        AdjustEvent event = new AdjustEvent(screen);
        //        event.addCallbackParameter("key", "value");
        //        event.addCallbackParameter("foo", "bar");
        //
        //        Adjust.trackEvent(event);
    }

    @Override
    void recordScreen(String screen, Map<String, String> params)
    {
        if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            AdjustEvent event = new AdjustEvent(EventToken.FIRST_PURCHASE_GOURMET);
            Adjust.trackEvent(event);
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            AdjustEvent event = new AdjustEvent(EventToken.FIRST_PURCHASE_STAY);
            Adjust.trackEvent(event);
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {

    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

    @Override
    void setUserIndex(String index)
    {
        mUserIndex = index;
    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {

    }

    @Override
    void onStart(Activity activity)
    {

    }

    @Override
    void onStop(Activity activity)
    {

    }

    @Override
    void onResume(Activity activity)
    {
        Adjust.onResume();
    }

    @Override
    void onPause(Activity activity)
    {
        Adjust.onPause();
    }

    @Override
    void currentAppVersion(String version)
    {

    }

    @Override
    void addCreditCard(String cardType)
    {

    }

    @Override
    void updateCreditCard(String cardTypes)
    {

    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {

    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender)
    {

    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        AdjustEvent event = new AdjustEvent(EventToken.PURCHASE_STAY);

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_PRICE) == true)
        {
            event.setRevenue(Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), "KRW");
        }

        Adjust.trackEvent(event);
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        AdjustEvent event = new AdjustEvent(EventToken.PURCHASE_GOURMET);

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_PRICE) == true)
        {
            event.setRevenue(Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), "KRW");
        }

        Adjust.trackEvent(event);
    }


    @Override
    void startDeepLink(Uri deepLinkUri)
    {
        Adjust.appWillOpenUrl(deepLinkUri);
    }

    @Override
    void startApplication()
    {
        AdjustEvent event = new AdjustEvent(EventToken.LAUNCH);
        event.addCallbackParameter("user_id", mUserIndex);
        Adjust.trackEvent(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// Event Token ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    protected static final class EventToken
    {
        public static final String LAUNCH = "zglco7";

        public static final String PURCHASE_STAY = "bqwrab";

        public static final String PURCHASE_GOURMET = "bpmxez";

        public static final String FIRST_PURCHASE_STAY = "9uxbuf";

        public static final String FIRST_PURCHASE_GOURMET = "qvbirj";
    }
}
