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
    private static final String TAG = "[AdjustManager]";
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
        AdjustEvent event = null;

        if (AnalyticsManager.Screen.MENU_LOGIN_COMPLETE.equalsIgnoreCase(screen) == true)
        {
            // 해당 경우 유저 타입을 알지 못해 recordEvent에서 처리함 - 보내는 시점은 recordEvent와 같음
        } else if (AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE.equalsIgnoreCase(screen) == true)
        {
            event = new AdjustEvent(EventToken.LOGOUT);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen);
            }
        }

        if (event != null)
        {
            Adjust.trackEvent(event);
        }
    }

    @Override
    void recordScreen(String screen, Map<String, String> params)
    {
        AdjustEvent event = null;

        if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            event = getPaymentEvent(EventToken.GOURMET_FIRST_PURCHASE, params);
            event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screen) == true)
        {
            event = getPaymentEvent(EventToken.STAY_FIRST_PURCHASE, params);
            event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        }

        if (event != null)
        {
            Adjust.trackEvent(event);
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (Util.isTextEmpty(category, action) == true)
        {
            return;
        }

        AdjustEvent event = null;

        if (AnalyticsManager.Category.NAVIGATION.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.LOGIN_COMPLETE.equalsIgnoreCase(action) == true)
            {
                if (Util.isTextEmpty(label) == true)
                {
                    return;
                }

                if (AnalyticsManager.UserType.EMAIL.equalsIgnoreCase(label) == true)
                {
                    event = new AdjustEvent(EventToken.LOGIN);
                } else if (AnalyticsManager.UserType.FACEBOOK.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.UserType.KAKAO.equalsIgnoreCase(label) == true)
                {
                    event = new AdjustEvent(EventToken.SOCIAL_LOGIN);
                }

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.INVITE_FRIEND.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.KAKAO_FRIEND_INVITED.equalsIgnoreCase(action) == true)
            {
                event = new AdjustEvent(EventToken.FRIEND_REFERRAL);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.HOTEL_BOOKINGS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SOCIAL_SHARE_CLICKED.equalsIgnoreCase(action) == true)
            {
                String placeIndex = null;
                String placeName = null;

                if (params != null)
                {
                    placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
                    placeName = params.get(AnalyticsManager.KeyType.NAME);
                }

                event = new AdjustEvent(EventToken.SOCIAL_SHARE);
                event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.STAY);
                event.addCallbackParameter(Key.SHARE_METHOD, AnalyticsManager.Label.KAKAO);
                event.addCallbackParameter(Key.PLACE_INDEX, placeIndex);
                event.addCallbackParameter(Key.PLACE_NAME, placeName);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_BOOKINGS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SOCIAL_SHARE_CLICKED.equalsIgnoreCase(action) == true)
            {
                String placeIndex = null;
                String placeName = null;

                if (params != null)
                {
                    placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX);
                    placeName = params.get(AnalyticsManager.KeyType.NAME);
                }

                event = new AdjustEvent(EventToken.SOCIAL_SHARE);
                event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);
                event.addCallbackParameter(Key.SHARE_METHOD, AnalyticsManager.Label.KAKAO);
                event.addCallbackParameter(Key.PLACE_INDEX, placeIndex);
                event.addCallbackParameter(Key.PLACE_NAME, placeName);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.COUPON_BOX.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.REGISTRATION_REJECTED.equalsIgnoreCase(action) == true)
            {
                if (params != null)
                {
                    String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
                    StringBuilder couponBuilder = new StringBuilder("[");

                    if (params.containsKey(AnalyticsManager.KeyType.COUPON_CODE) == true)
                    {
                        couponBuilder.append("id:").append(params.get(AnalyticsManager.KeyType.COUPON_CODE)); // coupon_id
                    }

                    if (params.containsKey(AnalyticsManager.KeyType.STATUS_CODE) == true)
                    {
                        couponBuilder.append(",rejection:").append(params.get(AnalyticsManager.KeyType.STATUS_CODE)); // 거절사유
                    }

                    couponBuilder.append("]");

                    event.addCallbackParameter(Key.SERVICE, placeType);
                    event.addCallbackParameter(Key.COUPON_REJECTED, couponBuilder.toString());
                }
            }
        } else if (AnalyticsManager.Category.POPUP_BOXES.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                if (AnalyticsManager.Label.HOTEL_SATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.GOURMET_SATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.HOTEL_DISSATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.GOURMET_DISSATISFACTION.equalsIgnoreCase(label) == true)
                {
                    String placeName = params.get(AnalyticsManager.KeyType.NAME);
                    String satisfaction = params.get(AnalyticsManager.KeyType.SATISFACTION_SURVEY);
                    String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);

                    event.addCallbackParameter(Key.PLACE_NAME, placeName);
                    event.addCallbackParameter(AnalyticsManager.KeyType.SATISFACTION_SURVEY, satisfaction);
                    event.addCallbackParameter(Key.SERVICE, placeType);
                }
            }
        }

        if (event != null)
        {
            Adjust.trackEvent(event);
        }
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
        } else
        {
            Adjust.addSessionCallbackParameter(Key.USER_INDEX, index);
        }

        if (Util.isTextEmpty(userType) == true)
        {
            Adjust.addSessionCallbackParameter(Key.USER_TYPE, UserType.GUEST);
            Adjust.removeSessionCallbackParameter(Key.MEMBER_TYPE);
        } else
        {
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

        AdjustEvent event = new AdjustEvent(EventToken.SOCIAL_SIGNUP);
        Adjust.trackEvent(event);
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender, String callByScreen)
    {
        setUserInformation(userIndex, userType);

        AdjustEvent event = new AdjustEvent(EventToken.SIGNUP);
        Adjust.trackEvent(event);
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        AdjustEvent event = getPaymentEvent(EventToken.STAY_PURCHASE, params);
        event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.STAY);

        Adjust.trackEvent(event);
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        AdjustEvent event = getPaymentEvent(EventToken.GOURMET_PURCHASE, params);
        event.addCallbackParameter(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

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
        Adjust.addSessionCallbackParameter(AnalyticsManager.KeyType.COUNTRY, country);
        Adjust.addSessionCallbackParameter(AnalyticsManager.KeyType.PROVINCE, provinceName);
    }

    @Override
    void setPushEnabled(boolean onOff, String pushSettingType)
    {
        Adjust.addSessionCallbackParameter(Key.PUSH_NOTIFICATION, onOff == true ? OnOffType.ON : OnOffType.OFF);

        if (Util.isTextEmpty(pushSettingType) == true)
        {
            return;
        }

        if (AnalyticsManager.ValueType.LAUNCH.equalsIgnoreCase(pushSettingType) == true //
            || AnalyticsManager.ValueType.OTHER.equalsIgnoreCase(pushSettingType) == true)
        {
            AdjustEvent event = new AdjustEvent(onOff ? EventToken.PUSH_ON : EventToken.PUSH_OFF);
            event.addCallbackParameter(Key.PUSH_SETTING, pushSettingType);
            Adjust.trackEvent(event);
        }
    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {
        AdjustEvent event = getCouponEvent(EventToken.PURCHASE_WITH_COUPON, param);
        Adjust.trackEvent(event);
    }

    private String getMemberType(String userType)
    {
        if (Util.isTextEmpty(userType) == true)
        {
            return null;
        }

        String memberType;
        if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = AnalyticsManager.UserType.KAKAO;
        } else if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = AnalyticsManager.UserType.FACEBOOK;
        } else if (Constants.DAILY_USER.equalsIgnoreCase(userType) == true)
        {
            memberType = AnalyticsManager.UserType.EMAIL;
        } else
        {
            memberType = null;
        }

        return memberType;
    }

    private AdjustEvent getPaymentEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        AdjustEvent event = new AdjustEvent(eventToken);

        double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)); // 금액
        event.setRevenue(paymentPrice, "KRW");

        String district = params.get(AnalyticsManager.KeyType.DISTRICT); // area ?
        //        String area = params.get(AnalyticsManager.KeyType.AREA); // area ?
        event.addCallbackParameter(AnalyticsManager.KeyType.AREA, district);

        String category = params.get(AnalyticsManager.KeyType.CATEGORY); // category
        event.addCallbackParameter(AnalyticsManager.KeyType.CATEGORY, category);

        String grade = params.get(AnalyticsManager.KeyType.GRADE); // grade
        event.addCallbackParameter(AnalyticsManager.KeyType.GRADE, grade);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addCallbackParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addCallbackParameter(Key.PLACE_NAME, placeName);

        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE); // discounted_price
        event.addCallbackParameter(Key.IS_SHOW_ORIGINAL_PRICE, isShowOriginalPrice);

        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX); // ranking
        event.addCallbackParameter(Key.LIST_INDEX, listIndex);

        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT); // d_benefit
        event.addCallbackParameter(Key.DBENEFIT, dBenefit);

        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX); // product_id
        event.addCallbackParameter(Key.TICKET_INDEX, ticketIndex);

        String checkIn = null;
        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
        }
        event.addCallbackParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
            event.addCallbackParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
        }

        String quantity = params.get(AnalyticsManager.KeyType.QUANTITY); // length_of_stay
        event.addCallbackParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, quantity);

        String registeredSimpleCard = params.get(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD); // card_registration
        event.addCallbackParameter(Key.REGISTERED_SIMPLE_CARD, registeredSimpleCard);

        String nrd = params.get(AnalyticsManager.KeyType.NRD); // nrd
        event.addCallbackParameter(AnalyticsManager.KeyType.NRD, nrd);

        // 결제시에만 들어가는 부분
        if (EventToken.STAY_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.STAY_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.GOURMET_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.GOURMET_PURCHASE.equalsIgnoreCase(eventToken) == true)
        {
            String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE); // payment_method
            event.addCallbackParameter(Key.PAYMENT_TYPE, paymentType);

            if (params.containsKey(AnalyticsManager.KeyType.COUPON_CODE) == true)
            {
                String couponCode = params.get(AnalyticsManager.KeyType.COUPON_CODE); // coupon_id
                event.addCallbackParameter(Key.COUPON_CODE, couponCode);
            }

            if (params.containsKey(AnalyticsManager.KeyType.PRICE_OFF) == true)
            {
                String couponPrice = params.get(AnalyticsManager.KeyType.PRICE_OFF); // coupon_value
                event.addCallbackParameter(Key.COUPON_PRICE, couponPrice);
            }

            if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
            {
                String bonusPrice = params.get(AnalyticsManager.KeyType.USED_BOUNS); // point_value
                event.addCallbackParameter(Key.BONUS_PRICE, bonusPrice);
            }
        }

        return event;
    }

    private AdjustEvent getCouponEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        AdjustEvent event = new AdjustEvent(eventToken);

        String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE); // service
        event.addCallbackParameter(Key.SERVICE, placeType);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addCallbackParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addCallbackParameter(Key.PLACE_NAME, placeName);

        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX); // product_id
        event.addCallbackParameter(Key.TICKET_INDEX, ticketIndex);

        String firstPurchaseYn = params.get(AnalyticsManager.KeyType.FIRST_PURCHASE);
        event.addCallbackParameter(AnalyticsManager.KeyType.FIRST_PURCHASE, firstPurchaseYn);

        String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE); // payment_method
        event.addCallbackParameter(Key.PAYMENT_TYPE, paymentType);

        StringBuilder couponBuilder = new StringBuilder("[");

        if (params.containsKey(AnalyticsManager.KeyType.COUPON_CODE) == true)
        {
            couponBuilder.append("id:").append(params.get(AnalyticsManager.KeyType.COUPON_CODE)); // coupon_id
        }

        if (params.containsKey(AnalyticsManager.KeyType.PRICE_OFF) == true)
        {
            couponBuilder.append(",value:").append(params.get(AnalyticsManager.KeyType.PRICE_OFF)); // coupon_value
        }

        couponBuilder.append("]");

        event.addCallbackParameter(Key.COUPON, couponBuilder.toString());

        return event;
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
        public static final String USER_TYPE = "user_type";
        public static final String MEMBER_TYPE = "member_type";
        public static final String PUSH_NOTIFICATION = "push_notification";
        public static final String PUSH_SETTING = "push_setting";
        public static final String SHARE_METHOD = "share_method"; // 공유수단
        public static final String PLACE_INDEX = "vendor_id"; // 업장 아이디
        public static final String PLACE_NAME = "vendor_name"; // 업장 이름
        public static final String IS_SHOW_ORIGINAL_PRICE = "discounted_price"; // 정가표시여부(y/n)
        public static final String LIST_INDEX = "ranking"; // 리스트화면에서 디테일화면으로 들어온 노출순위
        public static final String DBENEFIT = "d_benefit"; // 디베네핏 여부(y/n)
        public static final String TICKET_INDEX = "product_id"; // sale reco idx
        public static final String REGISTERED_SIMPLE_CARD = "card_registration"; // 간편결제 카드 등록 여부(y/n)
        public static final String PAYMENT_TYPE = "payment_method"; // 결제수단(easycard / card / phonebill / virtualaccount)
        public static final String COUPON = "coupon"; // 사용된 쿠폰
        public static final String COUPON_CODE = "coupon_id"; // 사용된 쿠폰코드
        public static final String COUPON_PRICE = "coupon_value"; // 사용된 쿠폰금액
        public static final String BONUS_PRICE = "point_value"; // 사용한 포인트 금액
        public static final String COUPON_REJECTED = "coupon_rejected"; // 쿠폰정보 및 거절 에러코드
    }

    private static final class UserType
    {
        public static final String GUEST = "guest";
        public static final String MEMBER = "member";
    }

    private static final class OnOffType
    {
        public static final String ON = "on";
        public static final String OFF = "off";
    }
}
