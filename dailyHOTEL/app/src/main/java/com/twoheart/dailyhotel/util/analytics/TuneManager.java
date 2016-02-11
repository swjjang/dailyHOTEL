package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.mobileapptracker.MATEvent;
import com.mobileapptracker.MATEventItem;
import com.mobileapptracker.MATGender;
import com.mobileapptracker.MobileAppTracker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class TuneManager implements IBaseAnalyticsManager
{
    private static final String ADVERTISE_ID = "190723";
    private static final String CONVERSION_KEY = "93aa9a40026991386dd92922cb14f58f";

    private MobileAppTracker mMobileAppTracker;
    private Context mContext;

    public TuneManager(Context context)
    {
        mContext = context;

        mMobileAppTracker = MobileAppTracker.init(context.getApplicationContext(), ADVERTISE_ID, CONVERSION_KEY);

        // 기존 사용자와 구분하기 위한 값
        if (Util.isTextEmpty(DailyPreference.getInstance(context).getCompanyName()) == false)
        {
            mMobileAppTracker.setExistingUser(true);
        }

        mMobileAppTracker.setAndroidId(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));

        String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        mMobileAppTracker.setDeviceId(deviceId);

        try
        {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mMobileAppTracker.setMacAddress(wifiManager.getConnectionInfo().getMacAddress());
        } catch (NullPointerException e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void recordScreen(String screenName, Map<String, String> params)
    {
        if (params == null)
        {
            return;
        }

        try
        {
            if (AnalyticsManager.Screen.DAILYHOTEL_DETAIL.equalsIgnoreCase(screenName) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_DETAIL, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            } else if (AnalyticsManager.Screen.DAILYGOURMET_DETAIL.equalsIgnoreCase(screenName) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_DETAIL, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            } else if (AnalyticsManager.Screen.DAILYHOTEL_PAYMENT.equalsIgnoreCase(screenName) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_PAYMENT, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            } else if (AnalyticsManager.Screen.DAILYGOURMET_PAYMENT.equalsIgnoreCase(screenName) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_PAYMENT, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void recordEvent(String eventName, String action, String label, Long value)
    {
        if (AnalyticsManager.Event.MENU.equals(eventName) == true)
        {
            // 고메인 경우에만 수집한다.
            String gourmet = mContext.getString(R.string.actionbar_title_gourmet_list_frag);

            if (gourmet.equalsIgnoreCase(label) == true)
            {
                MATEvent matEvent = new MATEvent(TuneEventId.MENU_GOURMET);
                mMobileAppTracker.measureEvent(matEvent);
            }
        }
    }

    @Override
    public void recordEvent(String eventName, String action, String label, Map<String, String> params)
    {
        if(AnalyticsManager.Screen.HOTEL_DETAIL.equalsIgnoreCase(eventName) == true)
        {
            if(AnalyticsManager.Label.SHARE.equalsIgnoreCase(label) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.SOCIAL_SHARE_HOTEL, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            }
        } else if(AnalyticsManager.Screen.GOURMET_DETAIL.equalsIgnoreCase(eventName) == true)
        {
            if(AnalyticsManager.Label.SHARE.equalsIgnoreCase(label) == true)
            {
                MATEvent matEvent = getMATEvent(TuneEventId.SOCIAL_SHARE_GOURMET, params);

                MATEventItem matEventItem = getMATEventItem(params);

                List<MATEventItem> list = new ArrayList<>();
                list.add(matEventItem);
                matEvent.withEventItems(list);

                mMobileAppTracker.measureEvent(matEvent);
            }
        } else if(AnalyticsManager.Screen.CREDIT.equalsIgnoreCase(eventName) == true)
        {
            if(AnalyticsManager.Label.INVITE_KAKAO_FRIEND.equalsIgnoreCase(label) == true)
            {
                MATEvent matEvent = new MATEvent(TuneEventId.INVITE_FRIEND);
                mMobileAppTracker.measureEvent(matEvent);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setUserIndex(String index)
    {
        mMobileAppTracker.setUserId(index);
    }

    @Override
    public void onResume(Activity activity)
    {
        mMobileAppTracker.setReferralSources(activity);
        mMobileAppTracker.measureSession();
    }

    @Override
    public void eventPaymentCardAdded(String cardType)
    {
        MATEvent matEvent = new MATEvent(TuneEventId.CARDLIST_ADDED_CARD);
        matEvent.withAttribute1(cardType);

        mMobileAppTracker.measureEvent(matEvent);
    }

    @Override
    public void recordSocialRegistration(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
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
    }

    @Override
    public void recordRegistration(String userIndex, String email, String name, String phoneNumber, String userType)
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
    }

    @Override
    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        MATEvent matEvent = getMATEvent(TuneEventId.DAILYHOTEL_PURCHASE_COMPLETE, params);

        if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
        {
            matEvent.withAttribute1(AnalyticsManager.KeyType.USED_BOUNS);
        }

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_TYPE) == true)
        {
            matEvent.withAttribute2(AnalyticsManager.KeyType.PAYMENT_TYPE);
        }

        MATEventItem matEventItem = getMATEventItem(params);

        List<MATEventItem> list = new ArrayList<>();
        list.add(matEventItem);
        matEvent.withEventItems(list);

        mMobileAppTracker.measureEvent(matEvent);
    }

    @Override
    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        MATEvent matEvent = getMATEvent(TuneEventId.DAILYGOURMET_PURCHASE_COMPLETE, params);

        if (params.containsKey(AnalyticsManager.KeyType.USED_BOUNS) == true)
        {
            matEvent.withAttribute1(AnalyticsManager.KeyType.USED_BOUNS);
        }

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_TYPE) == true)
        {
            matEvent.withAttribute2(AnalyticsManager.KeyType.PAYMENT_TYPE);
        }

        if (params.containsKey(AnalyticsManager.KeyType.RESERVATION_TIME) == true)
        {
            matEvent.withAttribute3(AnalyticsManager.KeyType.RESERVATION_TIME);
        }

        MATEventItem matEventItem = getMATEventItem(params);

        List<MATEventItem> list = new ArrayList<>();
        list.add(matEventItem);
        matEvent.withEventItems(list);

        mMobileAppTracker.measureEvent(matEvent);
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

    private MATEvent getMATEvent(int eventId, Map<String, String> params)
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

        if (params.containsKey(AnalyticsManager.KeyType.PAYMENT_PRICE) == true)
        {
            matEvent.withRevenue(Double.parseDouble(AnalyticsManager.KeyType.PAYMENT_PRICE));
        }

        return matEvent;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class TuneEventId
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
