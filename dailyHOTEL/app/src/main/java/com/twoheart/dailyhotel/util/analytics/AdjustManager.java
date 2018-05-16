package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustAttribution;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEventFailure;
import com.adjust.sdk.AdjustEventSuccess;
import com.adjust.sdk.AdjustSessionFailure;
import com.adjust.sdk.AdjustSessionSuccess;
import com.adjust.sdk.DailyAdjustEvent;
import com.adjust.sdk.LogLevel;
import com.adjust.sdk.OnAttributionChangedListener;
import com.adjust.sdk.OnDeeplinkResponseListener;
import com.adjust.sdk.OnEventTrackingFailedListener;
import com.adjust.sdk.OnEventTrackingSucceededListener;
import com.adjust.sdk.OnSessionTrackingFailedListener;
import com.adjust.sdk.OnSessionTrackingSucceededListener;
import com.appboy.Appboy;
import com.appboy.models.outgoing.AttributionData;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;

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

    Context mContext;

    public AdjustManager(Context context)
    {
        mContext = context;

        AdjustConfig config = new AdjustConfig(context, APPLICATION_TOKEN, ENVIRONMENT);

        if (Constants.DEBUG == true)
        {
            // change the log level
            config.setLogLevel(LogLevel.VERBOSE);
        }

        config.setAppSecret(3, 1885442567, 779097740, 1595381378, 59819778);

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
        config.setEventBufferingEnabled(DEBUG != true);

        Adjust.onCreate(config);

        // put the SDK in offline mode
        //        Adjust.setOfflineMode(true);

        // disable the SDK
        //        Adjust.setEnabled(true);

        Adjust.resetSessionCallbackParameters();
        Adjust.resetSessionPartnerParameters();

        setUserInformation(null, null);
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        DailyAdjustEvent event = null;

        if (AnalyticsManager.Screen.MENU_LOGIN_COMPLETE.equalsIgnoreCase(screenName) == true)
        {
            // 해당 경우 유저 타입을 알지 못해 recordEvent에서 처리함 - 보내는 시점은 recordEvent와 같음
        } else if (AnalyticsManager.Screen.MENU_LOGOUT_COMPLETE.equalsIgnoreCase(screenName) == true)
        {
            event = new DailyAdjustEvent(EventToken.LOGOUT);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName);
            }
        }

        if (event != null)
        {
            Adjust.trackEvent(event);

            if (DEBUG == true)
            {
                ExLog.d(TAG + event.toString());
            }
        }
    }

    @Override
    void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        DailyAdjustEvent event = null;

        if (AnalyticsManager.Screen.DAILY_GOURMET_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            event = getPaymentEvent(EventToken.GOURMET_FIRST_PURCHASE, params, false);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILY_HOTEL_FIRST_PURCHASE_SUCCESS.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getPaymentEvent(EventToken.STAY_FIRST_PURCHASE, params, false);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_LIST.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getListEvent(EventToken.VIEW_LIST, params);
            event.addPartnerParameter(Key.VIEW, AnalyticsManager.ValueType.LIST);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_LIST_MAP.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getListEvent(EventToken.VIEW_LIST, params);
            event.addPartnerParameter(Key.VIEW, AnalyticsManager.ValueType.MAP);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_LIST.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            event = getListEvent(EventToken.VIEW_LIST, params);
            event.addPartnerParameter(Key.VIEW, AnalyticsManager.ValueType.LIST);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_LIST_MAP.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            event = getListEvent(EventToken.VIEW_LIST, params);
            event.addPartnerParameter(Key.VIEW, AnalyticsManager.ValueType.MAP);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getDetailEvent(EventToken.VIEW_DETAIL, params);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            event = getDetailEvent(EventToken.VIEW_DETAIL, params);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getDetailEvent(EventToken.VIEW_DETAIL, params);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

            event = getPaymentEvent(EventToken.VIEW_BOOKING_INITIALISE, params, false);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_BOOKINGINITIALISE.equalsIgnoreCase(screenName) == true)
        {
            params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

            event = getPaymentEvent(EventToken.VIEW_BOOKING_INITIALISE, params, false);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.MENU_WISHLIST.equalsIgnoreCase(screenName) == true)
        {
            event = new DailyAdjustEvent(EventToken.WISH_LIST);

            String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
            event.addPartnerParameter(Key.SERVICE, placeType);

            String checkIn = null;
            if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
            } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
            }
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

            if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
            {
                String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
                event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
            }

            String placeIndexes = params.get(AnalyticsManager.KeyType.LIST_TOP5_PLACE_INDEXES);
            event.addPartnerParameter(Key.WISH_LIST_PLACE_INDEXES, placeIndexes);

            String listCount = params.get(AnalyticsManager.KeyType.PLACE_COUNT);
            event.addPartnerParameter(Key.NUMBER_OF_WISH_LISTS, listCount);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.MENU_RECENT_VIEW.equalsIgnoreCase(screenName) == true)
        {
            event = new DailyAdjustEvent(EventToken.RECENT_VIEW);

            String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
            event.addPartnerParameter(Key.SERVICE, placeType);

            String checkIn = null;
            if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
            } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
            }
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

            if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
            {
                String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
                event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
            }

            String placeIndexes = params.get(AnalyticsManager.KeyType.LIST_TOP5_PLACE_INDEXES);
            event.addPartnerParameter(Key.RECENTVIEW_LIST_PLACE_INDEXES, placeIndexes);

            String listCount = params.get(AnalyticsManager.KeyType.PLACE_COUNT);
            event.addPartnerParameter(Key.NUMBER_OF_RECENTVIEWS, listCount);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        } else if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(screenName) == true)
        {
            event = new DailyAdjustEvent(EventToken.VIEW_HOME);
        } else if (AnalyticsManager.Screen.STAY_LIST_SHORTCUT_HOTEL.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_BOUTIQUE.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_PENSION.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_RESORT.equalsIgnoreCase(screenName) == true //
            || AnalyticsManager.Screen.STAY_LIST_SHORTCUT_NEARBY.equalsIgnoreCase(screenName) == true)
        {
            event = new DailyAdjustEvent(EventToken.SELECT_CATEGORY);

            String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);
            event.addPartnerParameter(Key.SERVICE, placeType);

            String country = params.get(AnalyticsManager.KeyType.COUNTRY);
            event.addPartnerParameter(AnalyticsManager.KeyType.COUNTRY, country);

            String province = params.get(AnalyticsManager.KeyType.PROVINCE);
            event.addPartnerParameter(AnalyticsManager.KeyType.PROVINCE, province);

            String userType = params.get(AnalyticsManager.KeyType.IS_SIGNED);
            event.addPartnerParameter(AnalyticsManager.KeyType.USER_TYPE, userType);

            String memberType = params.get(AnalyticsManager.KeyType.MEMBER_TYPE);
            event.addPartnerParameter(AnalyticsManager.KeyType.MEMBER_TYPE, memberType);

            String pushNotification = params.get(AnalyticsManager.KeyType.PUSH_NOTIFICATION);
            event.addPartnerParameter(AnalyticsManager.KeyType.PUSH_NOTIFICATION, pushNotification);

            String area = params.get(AnalyticsManager.KeyType.DISTRICT);
            event.addPartnerParameter(AnalyticsManager.KeyType.AREA, area);

            String category = params.get(AnalyticsManager.KeyType.CATEGORY);
            event.addPartnerParameter(AnalyticsManager.KeyType.CATEGORY, category);

            String checkInDate = params.get(AnalyticsManager.KeyType.CHECK_IN);
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkInDate);

            String checkOutDate = params.get(AnalyticsManager.KeyType.CHECK_OUT);
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOutDate);

            String nights = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY);
            event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, nights);

            String viewType = params.get(AnalyticsManager.KeyType.VIEW_TYPE);
            event.addPartnerParameter(Key.VIEW, viewType);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screenName + params.toString());
            }
        }

        if (event != null)
        {
            Adjust.trackEvent(event);

            if (DEBUG == true)
            {
                ExLog.d(TAG + event.toString());
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (DailyTextUtils.isTextEmpty(category, action) == true)
        {
            return;
        }

        DailyAdjustEvent event = null;

        if (AnalyticsManager.Category.NAVIGATION_.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.LOGIN_COMPLETE.equalsIgnoreCase(action) == true)
            {
                if (DailyTextUtils.isTextEmpty(label) == true)
                {
                    return;
                }

                if (AnalyticsManager.UserType.EMAIL.equalsIgnoreCase(label) == true)
                {
                    event = new DailyAdjustEvent(EventToken.LOGIN);
                } else if (AnalyticsManager.UserType.FACEBOOK.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.UserType.KAKAO.equalsIgnoreCase(label) == true)
                {
                    event = new DailyAdjustEvent(EventToken.SOCIAL_LOGIN);
                }

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            } else if (AnalyticsManager.Action.WISHLIST_ON.equalsIgnoreCase(action) == true)
            {
                event = getWishOnOffEvent(EventToken.ADD_TO_WISH_LIST, params);

            } else if (AnalyticsManager.Action.WISHLIST_OFF.equalsIgnoreCase(action) == true //
                || AnalyticsManager.Action.WISHLIST_DELETE.equalsIgnoreCase(action) == true)
            {
                event = getWishOnOffEvent(EventToken.DELETE_TO_WISH_LIST, params);
            }
        } else if (AnalyticsManager.Category.INVITE_FRIEND.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.KAKAO_FRIEND_INVITED.equalsIgnoreCase(action) == true)
            {
                event = new DailyAdjustEvent(EventToken.FRIEND_REFERRAL);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.SHARE.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.STAY_ITEM_SHARE.equalsIgnoreCase(action) == true)
            {
                event = new DailyAdjustEvent(EventToken.SOCIAL_SHARE);
                event.addPartnerParameter(params);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            } else if (AnalyticsManager.Action.GOURMET_ITEM_SHARE.equalsIgnoreCase(action) == true)
            {
                event = new DailyAdjustEvent(EventToken.SOCIAL_SHARE);
                event.addPartnerParameter(params);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + (params != null ? params.toString() : "null"));
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_BOOKINGS.equalsIgnoreCase(category) == true)
        {
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

                    event = new DailyAdjustEvent(EventToken.COUPON_REJECTED);
                    event.addPartnerParameter(Key.SERVICE, placeType);
                    event.addPartnerParameter(Key.COUPON_REJECTED, couponBuilder.toString());
                }
            }
        } else if (AnalyticsManager.Category.POPUP_BOXES.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SATISFACTION_EVALUATION_POPPEDUP.equalsIgnoreCase(action) == true)
            {
                if (AnalyticsManager.Label.HOTEL_SATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.HOTEL_DISSATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.GOURMET_SATISFACTION.equalsIgnoreCase(label) == true //
                    || AnalyticsManager.Label.GOURMET_DISSATISFACTION.equalsIgnoreCase(label) == true//
                    || AnalyticsManager.Label.OB_SATISFACTION.equalsIgnoreCase(label) == true//
                    || AnalyticsManager.Label.OB_DISSATISFACTION.equalsIgnoreCase(label) == true)
                {
                    String placeName = params.get(AnalyticsManager.KeyType.NAME);
                    String satisfaction = params.get(AnalyticsManager.KeyType.SATISFACTION_SURVEY);
                    String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE);

                    event = new DailyAdjustEvent(EventToken.SATISFACTION_SURVEY);
                    event.addPartnerParameter(Key.PLACE_NAME, placeName);
                    event.addPartnerParameter(AnalyticsManager.KeyType.SATISFACTION_SURVEY, satisfaction);
                    event.addPartnerParameter(Key.SERVICE, placeType);

                    if (AnalyticsManager.Label.OB_SATISFACTION.equalsIgnoreCase(label) == true//
                        || AnalyticsManager.Label.OB_DISSATISFACTION.equalsIgnoreCase(label) == true)
                    {
                        event.addPartnerParameter(AnalyticsManager.KeyType.COUNTRY, AnalyticsManager.ValueType.OUTBOUND);
                    }
                }
            }
        } else if (AnalyticsManager.Category.SEARCH_.equalsIgnoreCase(category) == true//
            || AnalyticsManager.Category.AUTO_SEARCH_NOT_FOUND.equalsIgnoreCase(category) == true //
            || AnalyticsManager.Category.AUTO_SEARCH.equalsIgnoreCase(category) == true)
        {
            event = getSearchEvent(EventToken.SEARCH_RESULT, params);
        }

        if (event != null)
        {
            Adjust.trackEvent(event);

            if (DEBUG == true)
            {
                ExLog.d(TAG + event.toString());
            }
        }
    }

    @Override
    void recordDeepLink(DailyDeepLink dailyDeepLink)
    {

    }

    @Override
    void setUserInformation(String index, String userType)
    {
        if (DailyTextUtils.isTextEmpty(index) == true)
        {
            Adjust.removeSessionPartnerParameter(Key.USER_INDEX);
            Adjust.removeSessionCallbackParameter(Key.USER_INDEX);
        } else
        {
            Adjust.addSessionPartnerParameter(Key.USER_INDEX, index);
            Adjust.addSessionCallbackParameter(Key.USER_INDEX, index);
        }

        String memberType = getMemberType(userType);
        if (DailyTextUtils.isTextEmpty(memberType) == true)
        {
            Adjust.addSessionPartnerParameter(Key.USER_TYPE, UserType.GUEST);
            Adjust.removeSessionPartnerParameter(Key.MEMBER_TYPE);
            Adjust.addSessionCallbackParameter(Key.USER_TYPE, UserType.GUEST);
            Adjust.removeSessionCallbackParameter(Key.MEMBER_TYPE);
        } else
        {
            Adjust.addSessionPartnerParameter(Key.USER_TYPE, UserType.MEMBER);
            Adjust.addSessionPartnerParameter(Key.MEMBER_TYPE, memberType);
            Adjust.addSessionCallbackParameter(Key.USER_TYPE, UserType.MEMBER);
            Adjust.addSessionCallbackParameter(Key.MEMBER_TYPE, memberType);
        }
    }

    @Override
    void setExceedBonus(boolean isExceedBonus)
    {

    }

    @Override
    void onActivityCreated(Activity activity, Bundle bundle)
    {

    }

    @Override
    void onActivityStarted(Activity activity)
    {

    }

    @Override
    void onActivityStopped(Activity activity)
    {

    }

    @Override
    void onActivityResumed(Activity activity)
    {
        Adjust.onResume();
    }

    @Override
    void onActivityPaused(Activity activity)
    {
        Adjust.onPause();
    }

    @Override
    void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {

    }

    @Override
    void onActivityDestroyed(Activity activity)
    {

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
    void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen)
    {
        setUserInformation(userIndex, userType);

        DailyAdjustEvent event = new DailyAdjustEvent(EventToken.SOCIAL_SIGNUP);
        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void signUpDailyUser(String userIndex, String userType, String recommender, String callByScreen)
    {
        setUserInformation(userIndex, userType);

        DailyAdjustEvent event = new DailyAdjustEvent(EventToken.SIGNUP);
        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

        if (DailyTextUtils.isTextEmpty(aggregationId) == false)
        {
            params.put(AnalyticsManager.KeyType.AGGREGATION_ID, aggregationId);
        }

        DailyAdjustEvent event = getPaymentEvent(EventToken.STAY_PURCHASE, params, true);

        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        params.put(Key.SERVICE, AnalyticsManager.ValueType.STAY);

        if (DailyTextUtils.isTextEmpty(aggregationId) == false)
        {
            params.put(AnalyticsManager.KeyType.AGGREGATION_ID, aggregationId);
        }

        DailyAdjustEvent event = getPaymentEvent(EventToken.STAY_PURCHASE, params, true);

        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        params.put(Key.SERVICE, AnalyticsManager.ValueType.GOURMET);

        if (DailyTextUtils.isTextEmpty(aggregationId) == false)
        {
            params.put(AnalyticsManager.KeyType.AGGREGATION_ID, aggregationId);
        }

        DailyAdjustEvent event = getPaymentEvent(EventToken.GOURMET_PURCHASE, params, true);

        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void startDeepLink(Uri deepLinkUri)
    {
        Adjust.appWillOpenUrl(deepLinkUri);
    }

    @Override
    void startApplication()
    {
        DailyAdjustEvent event = new DailyAdjustEvent(EventToken.LAUNCH);

        String rankTestName = DailyRemoteConfigPreference.getInstance(mContext).getKeyRemoteConfigStayRankTestName();

        if (DailyTextUtils.isTextEmpty(rankTestName) == false)
        {
            Adjust.addSessionPartnerParameter(Key.TEST_TYPE, rankTestName);
            Adjust.addSessionCallbackParameter(Key.TEST_TYPE, rankTestName);
        } else
        {
            Adjust.removeSessionPartnerParameter(Key.TEST_TYPE);
            Adjust.removeSessionCallbackParameter(Key.TEST_TYPE);
        }

        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    @Override
    void onRegionChanged(String country, String provinceName)
    {
        Adjust.addSessionCallbackParameter(AnalyticsManager.KeyType.COUNTRY, country);
        Adjust.addSessionCallbackParameter(AnalyticsManager.KeyType.PROVINCE, provinceName);
        Adjust.addSessionPartnerParameter(AnalyticsManager.KeyType.COUNTRY, country);
        Adjust.addSessionPartnerParameter(AnalyticsManager.KeyType.PROVINCE, provinceName);
    }

    @Override
    void setPushEnabled(boolean onOff, String pushSettingType)
    {
        Adjust.addSessionCallbackParameter(Key.PUSH_NOTIFICATION, onOff == true ? OnOffType.ON : OnOffType.OFF);
        Adjust.addSessionPartnerParameter(Key.PUSH_NOTIFICATION, onOff == true ? OnOffType.ON : OnOffType.OFF);

        if (DailyTextUtils.isTextEmpty(pushSettingType) == true)
        {
            return;
        }

        if (AnalyticsManager.ValueType.LAUNCH.equalsIgnoreCase(pushSettingType) == true //
            || AnalyticsManager.ValueType.OTHER.equalsIgnoreCase(pushSettingType) == true)
        {
            DailyAdjustEvent event = new DailyAdjustEvent(onOff ? EventToken.PUSH_ON : EventToken.PUSH_OFF);
            event.addPartnerParameter(Key.PUSH_SETTING, pushSettingType);
            Adjust.trackEvent(event);

            if (DEBUG == true)
            {
                ExLog.d(TAG + event.toString());
            }
        }
    }

    @Override
    void purchaseWithCoupon(Map<String, String> param)
    {
        DailyAdjustEvent event = getCouponEvent(EventToken.PURCHASE_WITH_COUPON, param);
        Adjust.trackEvent(event);

        if (DEBUG == true)
        {
            ExLog.d(TAG + event.toString());
        }
    }

    private String getMemberType(String userType)
    {
        if (DailyTextUtils.isTextEmpty(userType) == true)
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

    private DailyAdjustEvent getPaymentEvent(String eventToken, Map<String, String> params, boolean isSetRevenue)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        if (isSetRevenue == true)
        {
            double paymentPrice = Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)); // 금액
            event.setRevenue(paymentPrice, "KRW");
        }

        String district = params.get(AnalyticsManager.KeyType.DISTRICT); // area ?
        //        String area = params.get(AnalyticsManager.KeyType.AREA); // area ?
        event.addPartnerParameter(AnalyticsManager.KeyType.AREA, district);

        String category = params.get(AnalyticsManager.KeyType.CATEGORY); // category
        event.addPartnerParameter(AnalyticsManager.KeyType.CATEGORY, category);

        String grade = params.get(AnalyticsManager.KeyType.GRADE); // grade
        event.addPartnerParameter(AnalyticsManager.KeyType.GRADE, grade);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addPartnerParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addPartnerParameter(Key.PLACE_NAME, placeName);

        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE); // discounted_price
        if (DailyTextUtils.isTextEmpty(isShowOriginalPrice) == false)
        {
            isShowOriginalPrice = isShowOriginalPrice.toLowerCase();
        }

        event.addPartnerParameter(Key.IS_SHOW_ORIGINAL_PRICE, isShowOriginalPrice);

        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX); // ranking
        event.addPartnerParameter(Key.LIST_INDEX, listIndex);

        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT); // d_benefit
        dBenefit = getYnType(dBenefit);

        event.addPartnerParameter(Key.DBENEFIT, dBenefit);

        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX); // product_id
        event.addPartnerParameter(Key.TICKET_INDEX, ticketIndex);

        String checkIn = null;
        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
        }
        event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
        }

        String service = params.get(Key.SERVICE);
        event.addPartnerParameter(Key.SERVICE, service);

        if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(service) == false)
        {
            String lengthOfStay = null;
            if (params.containsKey(AnalyticsManager.KeyType.QUANTITY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.QUANTITY); // length_of_stay
            } else if (params.containsKey(AnalyticsManager.KeyType.LENGTH_OF_STAY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY); // length_of_stay
            }

            if (DailyTextUtils.isTextEmpty(lengthOfStay) == false)
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, lengthOfStay);
            }
        }

        String registeredSimpleCard = params.get(AnalyticsManager.KeyType.REGISTERED_SIMPLE_CARD); // card_registration
        event.addPartnerParameter(Key.REGISTERED_SIMPLE_CARD, registeredSimpleCard);

        String nrd = params.get(AnalyticsManager.KeyType.NRD); // nrd
        event.addPartnerParameter(AnalyticsManager.KeyType.NRD, nrd);

        String rating = params.get(AnalyticsManager.KeyType.RATING);
        event.addPartnerParameter(Key.RATING, rating);

        String isDailyChoice = params.get(AnalyticsManager.KeyType.DAILYCHOICE);
        event.addPartnerParameter(AnalyticsManager.KeyType.DAILYCHOICE, isDailyChoice);

        // 결제시에만 들어가는 부분
        if (EventToken.STAY_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.STAY_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.GOURMET_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.GOURMET_PURCHASE.equalsIgnoreCase(eventToken) == true)
        {
            String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE); // payment_method
            event.addPartnerParameter(Key.PAYMENT_TYPE, paymentType);

            if (params.containsKey(AnalyticsManager.KeyType.COUPON_CODE) == true)
            {
                String couponCode = params.get(AnalyticsManager.KeyType.COUPON_CODE); // coupon_id
                event.addPartnerParameter(Key.COUPON_CODE, couponCode);
            }

            if (params.containsKey(AnalyticsManager.KeyType.PRICE_OFF) == true)
            {
                String couponPrice = params.get(AnalyticsManager.KeyType.PRICE_OFF); // coupon_value
                event.addPartnerParameter(Key.COUPON_PRICE, couponPrice);
            }

            if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
            {
                String bonusPrice = params.get(AnalyticsManager.KeyType.USED_BOUNS); // point_value
                event.addPartnerParameter(Key.BONUS_PRICE, bonusPrice);
            }

            if (params.containsKey(AnalyticsManager.KeyType.AGGREGATION_ID) == true)
            {
                event.addPartnerParameter(Key.AGGREGATION_ID, params.get(AnalyticsManager.KeyType.AGGREGATION_ID));
            }
        }

        if (EventToken.STAY_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true //
            || EventToken.GOURMET_FIRST_PURCHASE.equalsIgnoreCase(eventToken) == true)
        {
            event.addPartnerParameter(Key.EVENT_REVENUE, params.get(AnalyticsManager.KeyType.PAYMENT_PRICE));
        }

        return event;
    }

    private DailyAdjustEvent getCouponEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE); // service
        event.addPartnerParameter(Key.SERVICE, placeType);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addPartnerParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addPartnerParameter(Key.PLACE_NAME, placeName);

        String ticketIndex = params.get(AnalyticsManager.KeyType.TICKET_INDEX); // product_id
        event.addPartnerParameter(Key.TICKET_INDEX, ticketIndex);

        String firstPurchaseYn = params.get(AnalyticsManager.KeyType.FIRST_PURCHASE);
        event.addPartnerParameter(AnalyticsManager.KeyType.FIRST_PURCHASE, firstPurchaseYn);

        String paymentType = params.get(AnalyticsManager.KeyType.PAYMENT_TYPE); // payment_method
        event.addPartnerParameter(Key.PAYMENT_TYPE, paymentType);

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

        event.addPartnerParameter(Key.COUPON, couponBuilder.toString());

        return event;
    }

    private DailyAdjustEvent getListEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        String district = params.get(AnalyticsManager.KeyType.DISTRICT); // area ?
        event.addPartnerParameter(AnalyticsManager.KeyType.AREA, district);

        String category = params.get(AnalyticsManager.KeyType.CATEGORY); // category
        event.addPartnerParameter(AnalyticsManager.KeyType.CATEGORY, category);

        String checkIn = null;
        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
        }
        event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
        }

        String service = params.get(Key.SERVICE);
        event.addPartnerParameter(Key.SERVICE, service);

        if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(service) == false)
        {
            String lengthOfStay = null;
            if (params.containsKey(AnalyticsManager.KeyType.QUANTITY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.QUANTITY); // length_of_stay
            } else if (params.containsKey(AnalyticsManager.KeyType.LENGTH_OF_STAY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY); // length_of_stay
            }

            if (DailyTextUtils.isTextEmpty(lengthOfStay) == false)
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, lengthOfStay);
            }
        }

        String filter = params.get(AnalyticsManager.KeyType.FILTER); // filter
        event.addPartnerParameter(AnalyticsManager.KeyType.FILTER, filter);

        return event;
    }

    private DailyAdjustEvent getDetailEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        String district = params.get(AnalyticsManager.KeyType.DISTRICT); // area ?
        event.addPartnerParameter(AnalyticsManager.KeyType.AREA, district);

        String category = params.get(AnalyticsManager.KeyType.CATEGORY); // category
        event.addPartnerParameter(AnalyticsManager.KeyType.CATEGORY, category);

        String grade = params.get(AnalyticsManager.KeyType.GRADE); // grade
        event.addPartnerParameter(AnalyticsManager.KeyType.GRADE, grade);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addPartnerParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addPartnerParameter(Key.PLACE_NAME, placeName);

        String rating = params.get(AnalyticsManager.KeyType.RATING); // vendor_satisfaction
        event.addPartnerParameter(Key.RATING, rating);

        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE); // discounted_price
        if (DailyTextUtils.isTextEmpty(isShowOriginalPrice) == false)
        {
            isShowOriginalPrice = isShowOriginalPrice.toLowerCase();
        }
        event.addPartnerParameter(Key.IS_SHOW_ORIGINAL_PRICE, isShowOriginalPrice);

        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX); // ranking
        event.addPartnerParameter(Key.LIST_INDEX, listIndex);

        String dailyChoice = params.get(AnalyticsManager.KeyType.DAILYCHOICE); // ranking
        event.addPartnerParameter(AnalyticsManager.KeyType.DAILYCHOICE, dailyChoice);

        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT); // d_benefit
        dBenefit = getYnType(dBenefit);
        event.addPartnerParameter(Key.DBENEFIT, dBenefit);

        String checkIn = null;
        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
        }
        event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
        }

        if (params.containsKey(AnalyticsManager.KeyType.COUNTRY) == true)
        {
            event.addPartnerParameter(AnalyticsManager.KeyType.COUNTRY, params.get(AnalyticsManager.KeyType.COUNTRY));
        }

        if (params.containsKey(AnalyticsManager.KeyType.NRD) == true)
        {
            event.addPartnerParameter(AnalyticsManager.KeyType.NRD, params.get(AnalyticsManager.KeyType.NRD));
        }

        String service = params.get(Key.SERVICE);
        event.addPartnerParameter(Key.SERVICE, service);

        if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(service) == false)
        {
            String lengthOfStay = null;
            if (params.containsKey(AnalyticsManager.KeyType.QUANTITY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.QUANTITY); // length_of_stay
            } else if (params.containsKey(AnalyticsManager.KeyType.LENGTH_OF_STAY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY); // length_of_stay
            }

            if (DailyTextUtils.isTextEmpty(lengthOfStay) == false)
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, lengthOfStay);
            }
        }

        return event;
    }

    private DailyAdjustEvent getSearchEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        try
        {
            String checkIn = null;
            if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
            } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
            {
                checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
            }
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

            if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
            {
                String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
                event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
            }

            String placeType = params.get(AnalyticsManager.KeyType.PLACE_TYPE); // service == placeType
            event.addPartnerParameter(Key.SERVICE, placeType);

            if (params.containsKey(AnalyticsManager.KeyType.COUNTRY) == true)
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.COUNTRY, params.get((AnalyticsManager.KeyType.COUNTRY)));
            } else
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.COUNTRY, "domestic");
            }

            String quantity = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY); // length_of_stay
            event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, quantity);

            String searchWord = params.get(AnalyticsManager.KeyType.SEARCH_WORD); // 입력어
            event.addPartnerParameter(AnalyticsManager.KeyType.SEARCH_WORD, searchWord);

            String searchPath = params.get(AnalyticsManager.KeyType.SEARCH_PATH); // 내주변(around), 자동완성(auto), 최근검색어(recent), 검색어(direct)
            event.addPartnerParameter(AnalyticsManager.KeyType.SEARCH_PATH, searchPath);

            String searchCount = params.get(AnalyticsManager.KeyType.SEARCH_COUNT); // 검색되는 업장개수
            event.addPartnerParameter(AnalyticsManager.KeyType.SEARCH_COUNT, searchCount);

            String searchResult = params.get(AnalyticsManager.KeyType.SEARCH_RESULT); // 검색결과
            event.addPartnerParameter(AnalyticsManager.KeyType.SEARCH_RESULT, searchResult);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return null;
        }

        return event;
    }

    private DailyAdjustEvent getWishOnOffEvent(String eventToken, Map<String, String> params)
    {
        if (params == null)
        {
            return null;
        }

        DailyAdjustEvent event = new DailyAdjustEvent(eventToken);

        String district = params.get(AnalyticsManager.KeyType.DISTRICT); // area ?
        event.addPartnerParameter(AnalyticsManager.KeyType.AREA, district);

        String category = params.get(AnalyticsManager.KeyType.CATEGORY); // category
        event.addPartnerParameter(AnalyticsManager.KeyType.CATEGORY, category);

        String grade = params.get(AnalyticsManager.KeyType.GRADE); // grade
        event.addPartnerParameter(AnalyticsManager.KeyType.GRADE, grade);

        String placeIndex = params.get(AnalyticsManager.KeyType.PLACE_INDEX); // vendor_id
        event.addPartnerParameter(Key.PLACE_INDEX, placeIndex);

        String placeName = params.get(AnalyticsManager.KeyType.NAME); // vendor_name
        event.addPartnerParameter(Key.PLACE_NAME, placeName);

        String rating = params.get(AnalyticsManager.KeyType.RATING); // vendor_satisfaction
        event.addPartnerParameter(Key.RATING, rating);

        String isShowOriginalPrice = params.get(AnalyticsManager.KeyType.IS_SHOW_ORIGINAL_PRICE); // discounted_price
        if (DailyTextUtils.isTextEmpty(isShowOriginalPrice) == false)
        {
            isShowOriginalPrice = isShowOriginalPrice.toLowerCase();
        }
        event.addPartnerParameter(Key.IS_SHOW_ORIGINAL_PRICE, isShowOriginalPrice);

        String listIndex = params.get(AnalyticsManager.KeyType.LIST_INDEX); // ranking
        event.addPartnerParameter(Key.LIST_INDEX, listIndex);

        String dailyChoice = params.get(AnalyticsManager.KeyType.DAILYCHOICE); // dailychoice
        event.addPartnerParameter(AnalyticsManager.KeyType.DAILYCHOICE, dailyChoice);

        String dBenefit = params.get(AnalyticsManager.KeyType.DBENEFIT); // d_benefit
        dBenefit = getYnType(dBenefit);
        event.addPartnerParameter(Key.DBENEFIT, dBenefit);

        String checkIn = null;
        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.CHECK_IN); // check_in_date
        } else if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            checkIn = params.get(AnalyticsManager.KeyType.DATE); // check_in_date
        }
        event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_IN_DATE, checkIn);

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String checkOut = params.get(AnalyticsManager.KeyType.CHECK_OUT); // check_out_date
            event.addPartnerParameter(AnalyticsManager.KeyType.CHECK_OUT_DATE, checkOut);
        }

        String service = params.get(Key.SERVICE);
        event.addPartnerParameter(Key.SERVICE, service);

        if (AnalyticsManager.ValueType.GOURMET.equalsIgnoreCase(service) == false)
        {
            String lengthOfStay = null;
            if (params.containsKey(AnalyticsManager.KeyType.QUANTITY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.QUANTITY); // length_of_stay
            } else if (params.containsKey(AnalyticsManager.KeyType.LENGTH_OF_STAY) == true)
            {
                lengthOfStay = params.get(AnalyticsManager.KeyType.LENGTH_OF_STAY); // length_of_stay
            }

            if (DailyTextUtils.isTextEmpty(lengthOfStay) == false)
            {
                event.addPartnerParameter(AnalyticsManager.KeyType.LENGTH_OF_STAY, lengthOfStay);
            }
        }

        return event;
    }

    private String getYnType(String ynString)
    {
        if (DailyTextUtils.isTextEmpty(ynString) == true)
        {
            ynString = "n";
        } else
        {
            ynString = ynString.toLowerCase();
            if (ynString.equalsIgnoreCase("yes") == true //
                || ynString.equalsIgnoreCase("y") == true //
                || ynString.equalsIgnoreCase("true") == true //
                || ynString.equalsIgnoreCase("1") == true)
            {
                ynString = "y";
            } else
            {
                ynString = "n";
            }
        }
        return ynString;
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
        public static final String WISH_LIST = "tnjqjp"; // 위시리스트
        public static final String RECENT_VIEW = "kmmxda"; // 최근 본 업장
        public static final String ADD_TO_WISH_LIST = "7z705c"; // 위시리스트에 추가버튼을 누를 때
        public static final String DELETE_TO_WISH_LIST = "kkeukz"; // 위시리스트에 삭제버튼을 누를 때
        public static final String VIEW_HOME = "mfjyoa"; // 홈 화면 진입 후 위시리스트와 최근 본 업장 결과를 가져온 때
        public static final String SELECT_CATEGORY = "zdhpcy"; // 카테고리 선택 > 지역선택 완료 후 리스트 화면이 노출될 때
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
        public static final String RATING = "vendor_satisfaction"; // 만족도
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
        public static final String VIEW = "view"; // 리스트 인지 맵인지
        public static final String WISH_LIST_PLACE_INDEXES = "wish_lists"; // 위시리스트내 담은 업장(최근 5개)
        public static final String RECENTVIEW_LIST_PLACE_INDEXES = "recentview_lists"; // 위시리스트내 담은 업장 개수
        public static final String NUMBER_OF_WISH_LISTS = "number_of_wish_lists"; // 위시리스트내 담은 업장(최근 5개)
        public static final String NUMBER_OF_RECENTVIEWS = "number_of_recentviews"; // 최근 본 업장 개수
        public static final String TEST_TYPE = "test_type"; // a/b test
        public static final String AGGREGATION_ID = "aggregation_id";
        public static final String EVENT_REVENUE = "event_revenue";
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
