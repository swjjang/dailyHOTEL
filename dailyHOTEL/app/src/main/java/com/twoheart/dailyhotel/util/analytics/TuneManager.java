package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.mobileapptracker.MATDeeplinkListener;
import com.mobileapptracker.MATEvent;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MATGender;
import com.mobileapptracker.MobileAppTracker;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class TuneManager extends BaseAnalyticsManager
{
    private static final boolean DEBUG = Constants.DEBUG;
    private static final String TAG = "[TuneManager]";
    private static final String ADVERTISE_ID = "190723";
    private static final String CONVERSION_KEY = "93aa9a40026991386dd92922cb14f58f";

    private MobileAppTracker mMobileAppTracker;
    private Context mContext;
    private String mUserIndex;

    public TuneManager(Context context)
    {
        mContext = context;
        mMobileAppTracker = MobileAppTracker.init(context.getApplicationContext(), ADVERTISE_ID, CONVERSION_KEY);
        mMobileAppTracker.setCurrencyCode("KRW");

        // 기존 사용자와 구분하기 위한 값
        if (Util.isTextEmpty(DailyPreference.getInstance(context).getCompanyName()) == false)
        {
            mMobileAppTracker.setExistingUser(true);
        }

        setDeferredDeepLink();
    }

    void setGoogleClientId(String clientId)
    {
        mMobileAppTracker.setGoogleUserId(clientId);
    }

    private void setDeferredDeepLink()
    {
        mMobileAppTracker.checkForDeferredDeeplink(new MATDeeplinkListener()
        {
            @Override
            public void didReceiveDeeplink(String deeplink)
            {
                if (Util.isTextEmpty(deeplink) == false)
                {
                    Intent intent = new Intent(mContext, LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(deeplink));

                    mContext.startActivity(intent);
                }
            }

            @Override
            public void didFailDeeplink(String error)
            {
            }
        });
    }

    @Override
    void recordScreen(String screen)
    {

    }

    @Override
    void recordScreen(String screen, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screen) == true)
        {
            MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_DETAIL, params, false);

            MATEventItem matEventItem = getMATEventItem(params);

            matEventItem.withAttribute2(params.get(AnalyticsManager.KeyType.GRADE));
            matEventItem.withAttribute3(params.get(AnalyticsManager.KeyType.PROVINCE));

            List<MATEventItem> list = new ArrayList<>();
            list.add(matEventItem);
            matEvent.withEventItems(list);

            setUserIndex(mUserIndex);

            mMobileAppTracker.measureEvent(matEvent);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screen) == true)
        {
            MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_DETAIL, params, false);

            MATEventItem matEventItem = getMATEventItem(params);
            matEventItem.withAttribute2(params.get(AnalyticsManager.KeyType.CATEGORY));
            matEventItem.withAttribute3(params.get(AnalyticsManager.KeyType.PROVINCE));

            List<MATEventItem> list = new ArrayList<>();
            list.add(matEventItem);
            matEvent.withEventItems(list);

            setUserIndex(mUserIndex);

            mMobileAppTracker.measureEvent(matEvent);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENT.equalsIgnoreCase(screen) == true)
        {
            MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_PAYMENT, params, true);

            MATEventItem matEventItem = getMATEventItem(params);
            matEventItem.withAttribute4(params.get(AnalyticsManager.KeyType.GRADE));
            matEventItem.withAttribute5(params.get(AnalyticsManager.KeyType.PROVINCE));

            List<MATEventItem> list = new ArrayList<>();
            list.add(matEventItem);
            matEvent.withEventItems(list);

            setUserIndex(mUserIndex);

            mMobileAppTracker.measureEvent(matEvent);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        } else if (AnalyticsManager.Screen.DAILYGOURMET_PAYMENT.equalsIgnoreCase(screen) == true)
        {
            MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_PAYMENT, params, true);

            MATEventItem matEventItem = getMATEventItem(params);
            matEventItem.withAttribute2(params.get(AnalyticsManager.KeyType.CATEGORY));
            matEventItem.withAttribute3(params.get(AnalyticsManager.KeyType.PROVINCE));

            List<MATEventItem> list = new ArrayList<>();
            list.add(matEventItem);
            matEvent.withEventItems(list);

            setUserIndex(mUserIndex);

            mMobileAppTracker.measureEvent(matEvent);

            if (DEBUG == true)
            {
                ExLog.d(TAG + "Screen : " + screen + params.toString());
            }
        }
    }

    @Override
    void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        if (Util.isTextEmpty(category, action, label) == true)
        {
            return;
        }

        if (AnalyticsManager.Category.HOTEL_BOOKINGS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SOCIAL_SHARE_CLICKED.equalsIgnoreCase(action) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.SOCIAL_SHARE_HOTEL, params, false);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                setUserIndex(mUserIndex);

                mMobileAppTracker.measureEvent(matEvent);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + params.toString());
                }
            }
        } else if (AnalyticsManager.Category.GOURMET_BOOKINGS.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.SOCIAL_SHARE_CLICKED.equalsIgnoreCase(action) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.SOCIAL_SHARE_GOURMET, params, false);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                setUserIndex(mUserIndex);

                mMobileAppTracker.measureEvent(matEvent);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label + " | " + params.toString());
                }
            }
        } else if (AnalyticsManager.Category.NAVIGATION.equalsIgnoreCase(category) == true)
        {
            if (AnalyticsManager.Action.DAILY_GOURMET_CLICKED.equalsIgnoreCase(action) == true)
            {
                MATEvent matEvent = new MATEvent(TuneEventId.MENU_GOURMET);

                setUserIndex(mUserIndex);

                mMobileAppTracker.measureEvent(matEvent);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label);
                }
            } else if (AnalyticsManager.Action.INVITE_FRIEND_CLICKED.equalsIgnoreCase(action) == true)
            {
                MATEvent matEvent = new MATEvent(TuneEventId.INVITE_FRIEND);

                setUserIndex(mUserIndex);

                mMobileAppTracker.measureEvent(matEvent);

                if (DEBUG == true)
                {
                    ExLog.d(TAG + "Event : " + category + " | " + action + " | " + label);
                }
            }
        }
    }

    @Override
    void recordEvent(Map<String, String> params)
    {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    void setUserIndex(String index)
    {
        mUserIndex = index;

        if (Util.isTextEmpty(index) == true)
        {
            mMobileAppTracker.setUserId(AnalyticsManager.ValueType.EMPTY);
        } else
        {
            mMobileAppTracker.setUserId(index);
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
        mMobileAppTracker.setReferralSources(activity);
        mMobileAppTracker.measureSession();
    }

    @Override
    void onPause(Activity activity)
    {
    }

    @Override
    void currentAppVersion(String version)
    {

    }

    @Override
    void addCreditCard(String cardType)
    {
        MATEvent matEvent = new MATEvent(TuneEventId.CARDLIST_ADDED_CARD);
        matEvent.withAttribute1(cardType);

        setUserIndex(mUserIndex);

        mMobileAppTracker.measureEvent(matEvent);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "addCreditCard : " + cardType);
        }
    }

    @Override
    void updateCreditCard(String cardTypes)
    {

    }

    @Override
    void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        mMobileAppTracker.setUserId(userIndex);

        if (Util.isTextEmpty(email) == false)
        {
            mMobileAppTracker.setUserEmail(email);
        }

        if (Util.isTextEmpty(name) == false)
        {
            mMobileAppTracker.setUserName(name);
        }

        if (Util.isTextEmpty(gender) == false)
        {
            if ("male".equalsIgnoreCase(gender) == true)
            {
                mMobileAppTracker.setGender(MATGender.MALE);
            } else if ("female".equalsIgnoreCase(gender) == true)
            {
                mMobileAppTracker.setGender(MATGender.FEMALE);
            }
        }

        if (Util.isTextEmpty(phoneNumber) == false)
        {
            mMobileAppTracker.setPhoneNumber(phoneNumber);
        }

        mMobileAppTracker.setCurrencyCode("KRW");

        MATEvent matEvent = new MATEvent(TuneEventId.SIGNUP_REGISTRATION);
        matEvent.withAttribute1(userType);

        mMobileAppTracker.measureEvent(matEvent);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "signUpSocialUser : " + userIndex + ", " + email + ", " + name + ", " + gender + ", " + phoneNumber + ", " + userType);
        }
    }

    @Override
    void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender)
    {
        // Tune
        mMobileAppTracker.setUserId(userIndex);
        mMobileAppTracker.setUserEmail(email);
        mMobileAppTracker.setUserName(name);
        mMobileAppTracker.setPhoneNumber(phoneNumber);
        mMobileAppTracker.setCurrencyCode("KRW");

        MATEvent matEvent = new MATEvent(TuneEventId.SIGNUP_REGISTRATION);
        matEvent.withAttribute1(userType);

        mMobileAppTracker.measureEvent(matEvent);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "signUpDailyUser : " + userIndex + ", " + email + ", " + name + ", " + phoneNumber + ", " + userType);
        }
    }

    @Override
    void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_PURCHASE_COMPLETE, params, true);

        if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
        {
            matEvent.withAttribute1(params.get(AnalyticsManager.KeyType.USED_BOUNS));
        }

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_TYPE) == true)
        {
            matEvent.withAttribute2(params.get(AnalyticsManager.KeyType.PAYMENT_TYPE));
        }

        MATEventItem matEventItem = getMATEventItem(params);
        matEventItem.withAttribute4(params.get(AnalyticsManager.KeyType.GRADE));
        matEventItem.withAttribute5(params.get(AnalyticsManager.KeyType.PROVINCE));

        List<MATEventItem> list = new ArrayList<>();
        list.add(matEventItem);
        matEvent.withEventItems(list);

        if (params.containsKey(AnalyticsManager.KeyType.USER_INDEX) == true)
        {
            setUserIndex(params.get(AnalyticsManager.KeyType.USER_INDEX));
        } else
        {
            setUserIndex(mUserIndex);
        }

        mMobileAppTracker.measureEvent(matEvent);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "purchaseCompleteHotel : " + params.toString());
        }
    }

    @Override
    void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_PURCHASE_COMPLETE, params, true);

        if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
        {
            matEvent.withAttribute1(params.get(AnalyticsManager.KeyType.USED_BOUNS));
        }

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_TYPE) == true)
        {
            matEvent.withAttribute2(params.get(AnalyticsManager.KeyType.PAYMENT_TYPE));
        }

        if (params.containsKey(AnalyticsManager.KeyType.RESERVATION_TIME) == true)
        {
            matEvent.withAttribute3(params.get(AnalyticsManager.KeyType.RESERVATION_TIME));
        }

        MATEventItem matEventItem = getMATEventItem(params);
        matEventItem.withAttribute4(params.get(AnalyticsManager.KeyType.CATEGORY));
        matEventItem.withAttribute5(params.get(AnalyticsManager.KeyType.PROVINCE));

        List<MATEventItem> list = new ArrayList<>();
        list.add(matEventItem);
        matEvent.withEventItems(list);

        if (params.containsKey(AnalyticsManager.KeyType.USER_INDEX) == true)
        {
            setUserIndex(params.get(AnalyticsManager.KeyType.USER_INDEX));
        } else
        {
            setUserIndex(mUserIndex);
        }

        mMobileAppTracker.measureEvent(matEvent);

        if (DEBUG == true)
        {
            ExLog.d(TAG + "purchaseCompleteGourmet : " + params.toString());
        }
    }

    private MATEventItem getMATEventItem(Map<String, String> params)
    {
        MATEventItem matEventItem = new MATEventItem(params.get(AnalyticsManager.KeyType.NAME));

        if (params.containsKey(AnalyticsManager.KeyType.PRICE) == true)
        {
            matEventItem.withUnitPrice(Double.parseDouble(params.get(AnalyticsManager.KeyType.PRICE)));
        }

        if (params.containsKey(AnalyticsManager.KeyType.QUANTITY) == true)
        {
            matEventItem.withQuantity(Integer.parseInt(params.get(AnalyticsManager.KeyType.QUANTITY)));
        }

        if (params.containsKey(AnalyticsManager.KeyType.TOTAL_PRICE) == true)
        {
            matEventItem.withRevenue(Double.parseDouble(params.get(AnalyticsManager.KeyType.TOTAL_PRICE)));
        }

        if (params.containsKey(AnalyticsManager.KeyType.PLACE_INDEX) == true)
        {
            matEventItem.withAttribute1(params.get(AnalyticsManager.KeyType.PLACE_INDEX));
        }

        if (params.containsKey(AnalyticsManager.KeyType.TICKET_NAME) == true)
        {
            matEventItem.withAttribute2(params.get(AnalyticsManager.KeyType.TICKET_NAME));
        }

        if (params.containsKey(AnalyticsManager.KeyType.TICKET_INDEX) == true)
        {
            matEventItem.withAttribute3(params.get(AnalyticsManager.KeyType.TICKET_INDEX));
        }

        return matEventItem;
    }

    private MATEvent getMATEvent(int eventId, Map<String, String> params, boolean usedRevenue)
    {
        MATEvent matEvent = new MATEvent(eventId);
        matEvent.withCurrencyCode("KRW");

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_IN) == true)
        {
            String[] checkInDate = params.get(AnalyticsManager.KeyType.CHECK_IN).split("\\-");
            matEvent.withDate1(new GregorianCalendar(Integer.parseInt(checkInDate[0]), Integer.parseInt(checkInDate[1]), Integer.parseInt(checkInDate[2])).getTime());
        }

        if (params.containsKey(AnalyticsManager.KeyType.CHECK_OUT) == true)
        {
            String[] checkOutDate = params.get(AnalyticsManager.KeyType.CHECK_OUT).split("\\-");
            matEvent.withDate2(new GregorianCalendar(Integer.parseInt(checkOutDate[0]), Integer.parseInt(checkOutDate[1]), Integer.parseInt(checkOutDate[2])).getTime());
        }

        if (params.containsKey(AnalyticsManager.KeyType.DATE) == true)
        {
            String[] checkInDate = params.get(AnalyticsManager.KeyType.DATE).split("\\-");
            matEvent.withDate1(new GregorianCalendar(Integer.parseInt(checkInDate[0]), Integer.parseInt(checkInDate[1]), Integer.parseInt(checkInDate[2])).getTime());
        }

        if (usedRevenue == true && params.containsKey(AnalyticsManager.KeyType.PAYMENT_PRICE) == true)
        {
            matEvent.withRevenue(Double.parseDouble(params.get(AnalyticsManager.KeyType.PAYMENT_PRICE)));
        }

        return matEvent;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    protected static final class TuneEventId
    {
        public static final int SIGNUP_REGISTRATION = 1132821069;

        public static final int CARDLIST_ADDED_CARD = 1142355899;

        public static final int MENU_GOURMET = 1142363135;

        public static final int DAILYHOTEL_DETAIL = 1142364007;

        public static final int DAILYGOURMET_DETAIL = 1142364473;

        public static final int DAILYHOTEL_PAYMENT = 1142364947;

        public static final int DAILYGOURMET_PAYMENT = 1142365473;

        public static final int DAILYHOTEL_PURCHASE_COMPLETE = 1142371481;

        public static final int DAILYGOURMET_PURCHASE_COMPLETE = 1142372395;

        public static final int INVITE_FRIEND = 1142373179;

        public static final int SOCIAL_SHARE_HOTEL = 1142374137;

        public static final int SOCIAL_SHARE_GOURMET = 1142374707;
    }
}
