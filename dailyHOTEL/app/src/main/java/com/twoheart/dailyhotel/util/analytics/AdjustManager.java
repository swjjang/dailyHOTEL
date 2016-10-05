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
import com.twoheart.dailyhotel.util.Util;

import java.util.Map;

/**
 * Created by android_sam on 2016. 9. 8..
 */
public class AdjustManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String APPLICATION_TOKEN = "jkf7ii0lj9xc";
    private static final String ENVIRONMENT = DEBUG == true ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;

    private Context mContext;

    public AdjustManager(Context context)
    {
        mContext = context;

        AdjustConfig config = new AdjustConfig(context, APPLICATION_TOKEN, ENVIRONMENT);

        // change the log level
        config.setLogLevel(LogLevel.VERBOSE);

        // set default tracker
        //        config.setDefaultTracker("https://app.adjust.com/qbwmpi");

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
            AdjustEvent event = new AdjustEvent(EventToken.GOURMET_FIRST_PURCHASE);
            Adjust.trackEvent(event);
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            AdjustEvent event = new AdjustEvent(EventToken.STAY_FIRST_PURCHASE);
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
    void setUserInformation(String index, String userType)
    {
        if (Util.isTextEmpty(index) == true)
        {
            Adjust.removeSessionCallbackParameter(Key.USER_INDEX);
        } else {
            Adjust.addSessionCallbackParameter(Key.USER_INDEX, index);
        }

        if (Util.isTextEmpty(userType) == true) {
            Adjust.addSessionCallbackParameter(Key.USER_TYPE, UserType.GUEST);
            Adjust.removeSessionCallbackParameter(Key.MEMBER_TYPE);
        } else {
            String memberType = getMemberType(userType);
            Adjust.addSessionCallbackParameter(Key.USER_TYPE, UserType.MEMBER);
            Adjust.addSessionCallbackParameter(Key.MEMBER_TYPE, memberType);
        }
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
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType, String callByScreen)
    {
        setUserInformation(userIndex, userType);
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender, String callByScreen)
    {
        setUserInformation(userIndex, userType);
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        AdjustEvent event = new AdjustEvent(EventToken.STAY_PURCHASE);

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_PRICE) == true)
        {
            event.setRevenue(Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)), "KRW");
        }

        Adjust.trackEvent(event);
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        AdjustEvent event = new AdjustEvent(EventToken.GOURMET_PURCHASE);

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
        Adjust.trackEvent(event);
    }

    @Override
    void onRegionChanged(String country, String provinceName)
    {
        Adjust.addSessionCallbackParameter(Key.COUNTRY, country);
        Adjust.addSessionCallbackParameter(Key.PROVINCE, provinceName);
    }

    @Override
    void setPushEnabled(boolean onOff)
    {
        Adjust.addSessionCallbackParameter(Key.PUSH_NOTIFICATION, onOff == true ? OnOffType.ON : OnOffType.OFF);
    }


//    private void addSessionParam()
//    {
//        //        Adjust.addSessionCallbackParameter("user_id", mUserIndex);
//        Adjust.addSessionCallbackParameter("service", ); // stay, gourmet 구분
//        Adjust.addSessionCallbackParameter("country", AnalyticsManager.KeyType.DOMESTIC); // domestic / overseas 구분
//        Adjust.addSessionCallbackParameter("province", ); // 대지역 구분
//        Adjust.addSessionCallbackParameter("user_type", ); // guest, member 구분
//        Adjust.addSessionCallbackParameter("member_type", ); // 회원 종류 구분(email / facebook / kakao)
//        Adjust.addSessionCallbackParameter("push_notification", ); // 푸쉬 on/off 구분
//    }

    private String getMemberType(String userType)
    {
        if (Util.isTextEmpty(userType) == true)
        {
            return null;
        }

        String memberType;
        if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = MemberType.KAKAO;
        } else if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = MemberType.FACEBOOK;
        } else if (Constants.DAILY_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = MemberType.EMAIL;
        } else
        {
            memberType = null;
        }

        return memberType;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////// Event Token ///////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    protected static final class EventToken
    {
        public static final String LAUNCH = "zglco7"; // 앱이 실행될 경우
        public static final String SIGNUP = "o49cnd"; //회원가입을 하는 경우
        public static final String SOCIAL_SIGNUP = "u9jtyd"; // 소셜 회원가입을 하는 경우
        public static final String LOGIN = "abm00g"; //로그인을 할 경우
        public static final String SOCIAL_LOGIN = "v3dnvq"; // 소셜 로그인을 할 경우
        public static final String LOGOUT = "9wow7l"; // 로그아웃을 할 경우
        public static final String PUSH_ON = "8n8r17"; // 팝업 또는 더보기에서 푸쉬를 켰을 경우
        public static final String PUSH_OFF = "9w31iv"; // 팝업 또는 더보기에서 푸쉬를 껏을 경우
        public static final String FRIEND_REFERRAL = "oqkwnd"; // 친구를 초대했을 경우
        public static final String SOCIAL_SHARE = "ubu8fq"; // 공유버튼을 눌러 세부 공유  "보내기"버튼까지 눌렀을 경우
        public static final String STAY_PURCHASE = "bqwrab"; // 스테이 결제가 완료되었을 때
        public static final String GOURMET_PURCHASE = "bpmxez"; // 고메 결제가 완료되었을 때
        public static final String PURCHASE_WITH_COUPON = "vtzvjn"; // 쿠폰을 사용하여 결제가 완료되었을 때
        public static final String STAY_FIRST_PURCHASE = "9uxbuf"; // 스테이 처음 결제가 완료되었을 때
        public static final String GOURMET_FIRST_PURCHASE = "qvbirj"; // 고메 처음 결제가 완료되었을 때
        public static final String FIRST_PURCHASE_WITH_COUPON = "oqbhce"; // 쿠폰을 사용하여 첫 결제가 완료되었을 때
        public static final String COUPON_REJECTED = "881sbf"; // 쿠폰이 거절되었을 때
        public static final String SATISFACTION_SURVEY = "n5pe52"; // 만족도 평가 완료 시
        public static final String VIEW_LIST = "qtgwuc"; // 리스트 화면이 노출될 때
        public static final String VIEW_DETAIL = "8atmoj"; // 업장 디테일화면이 노출될 때
        public static final String VIEW_BOOKING_INITIALISE = "4s8i0m"; // 결제화면이 노출될 때
        public static final String SEARCH_RESULT = "szintj"; // 검색어를 입력하여 검색결과 화면이 노출될 때
    }

    private static final class Key
    {
        public static final String USER_INDEX = "user_id"; // Adjust에서는 user_id 로 넘긴다고 함 - 혼선가능성으로 키 이름만 변경!
        public static final String SERVICE = "service";
        public static final String COUNTRY = AnalyticsManager.KeyType.COUNTRY;
        public static final String PROVINCE = AnalyticsManager.KeyType.PROVINCE;
        public static final String USER_TYPE = "user_type";
        public static final String MEMBER_TYPE = "member_type";
        public static final String PUSH_NOTIFICATION = "push_notification";
    }

    private static final class UserType
    {
        public static final String GUEST = "guest";
        public static final String MEMBER = "member";
    }

    private static final class MemberType
    {
        public static final String EMAIL = "email";
        public static final String FACEBOOK = "facebook";
        public static final String KAKAO = "kakao";
    }

    private static final class OnOffType {
        public static final String ON = "on";
        public static final String OFF = "off";
    }
}
