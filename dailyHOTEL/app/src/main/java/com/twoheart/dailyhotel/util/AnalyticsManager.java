package com.twoheart.dailyhotel.util;

import android.content.Context;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import java.util.Map;

public class AnalyticsManager
{
    private static AnalyticsManager mInstance = null;
    private GoogleAnalytics mGoogleAnalytics;
    private Tracker mTracker;

    private AnalyticsManager(Context context)
    {
        initAnalytics(context);
    }

    public static AnalyticsManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            synchronized (AnalyticsManager.class)
            {
                if (mInstance == null)
                {
                    mInstance = new AnalyticsManager(context);
                }
            }
        }
        return mInstance;
    }

    private void initAnalytics(final Context context)
    {
        mGoogleAnalytics = GoogleAnalytics.getInstance(context);
        mTracker = mGoogleAnalytics.getTracker(Constants.GA_PROPERTY_ID);
    }

    public void recordScreen(String screenName)
    {
        try
        {
            MapBuilder mapBuilder = MapBuilder.createAppView();

            // Set screen name.
            mapBuilder.set(Fields.SCREEN_NAME, screenName);

            // Send a screen view.
            mTracker.send(mapBuilder.build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void recordEvent(String category, String action, String label, Long value)
    {
        try
        {
            MapBuilder mapBuilder = MapBuilder.createEvent(category, action, label, value);

            mTracker.send(mapBuilder.build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        try
        {
            MapBuilder mapBuilder = MapBuilder.createEvent(category, action, label, 0L);
            mapBuilder.setAll(params);

            mTracker.send(mapBuilder.build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    /**
     * 구매 완료 하였으면 구글 애널래틱스 Ecommerce Tracking 을 위하여 필히 호출한다. 실제 우리 앱의 매출을 자동으로
     * 집계하여 알기위함.
     *
     * @param trasId    userId+YYMMDDhhmmss
     * @param pName     호텔명
     * @param pCategory 호텔 카테고리
     * @param pPrice    호텔 판매가(적립금을 사용 하는 경우 적립금을 까고 결제하는 금액)
     */

    public void purchaseComplete(String transId, String userIndex, String roomIndex, String hotelName, String category, String checkInTime, String checkOutTime, String payType, String currentTime, double price)
    {
        try
        {
            mTracker.send(MapBuilder.createTransaction(transId, "DailyHOTEL", price, 0d, 0d, "KRW").set("payType", payType).build());
            mTracker.send(MapBuilder.createItem(transId, hotelName, "1", category, price, 1L, "KRW").set("checkInTime", checkInTime).set("checkOutTime", checkOutTime).set("currentTime", currentTime).build());
            mTracker.send(MapBuilder.createEvent("Purchase", "PurchaseComplete", "PurchaseComplete", 1L).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public static class Screen
    {
        public static final String SPLASH = "Splash Screen";
        public static final String HOTEL_LIST = "HotelList Screen";
        public static final String HOTEL_DETAIL = "HotelDetail Screen";
        public static final String FNB_LIST = "FnBList Screen";
        public static final String FNB_DETAIL = "FnBDetail Screen";
        public static final String BOOKING = "Booking Screen";
        public static final String BOOLKING_LIST = "BookingList Screen";
        public static final String BOOKING_DETAIL = "BookingDetail Screen";
        public static final String CREDIT = "Credit Screen";
        public static final String EVENT = "Event Screen";
        public static final String CREDIT_LIST = "CreditList Screen";
        public static final String SETTING = "Setting Screen";
        public static final String ABOUT = "About Screen";
        public static final String NOTICE = "Notice Screen";
        public static final String PROFILE = "Profile Screen";
        public static final String CREDITCARD = "CreditCard Screen";
        public static final String FAQ = "FAQ Screen";
        public static final String EVENT_WEB = "EventWeb Screen";
        public static final String VERSION = "Version Screen";
        public static final String SIGNUP = "Signup Screen";
        public static final String LOGIN = "Login Screen";
        public static final String PAYMENT = "Payment Screen";
        public static final String GCMSERVICE = "Gcm Service";
        public static final String MENU = "menu";
        public static final String PAYMENT_AGREE_POPUP = "paymentAgreePopup";
        public static final String WAIT_TIMER = "WaitTimer Screen";
    }

    public static class Action
    {
        public static final String CLICK = "click";
        public static final String SWIPE = "swipe";
        public static final String NETWORK = "network";
    }

    public static class Label
    {
        public static final String LOGIN = "login";
        public static final String SIGNUP = "singup";
        public static final String DATE_TAB = "dateTab";
        public static final String HOTEL_INDEX = "hotelIndex";
        public static final String HOTEL_ROOM_INDEX = "hotelRoomIndex";
        public static final String HOTEL_ROOM_NAME = "hotelRoomName";
        public static final String HOTEL_NAME = "hotelName";
        public static final String MENU_OPENED = "menuOpened";
        public static final String AREA = "area";
        public static final String NOTICE = "notice";
        public static final String VERSION = "version";
        public static final String PROFILE = "profile";
        public static final String CREDITCARD = "creditCard";
        public static final String CALL_CS = "callCS";
        public static final String MAIL_CS = "mainCS";
        public static final String FAQ = "faq";
        public static final String ABOUT = "about";
        public static final String PAYMENT = "payment";
        public static final String ON = "on";
        public static final String OFF = "off";
        public static final String USED_CREDIT = "usedCredit";
        public static final String INVITE_KAKAO_FRIEND = "inviteKakaoFriend";
        public static final String VIEW_CREDIT_HISTORY = "viewCreditHistory";
        public static final String BOOKING = "booking";
        public static final String SHARE = "share";
        public static final String CHECK_IN = "chekcInTime";
        public static final String CHECK_OUT = "chekcOutTime";
        public static final String CURRENT_TIME = "currentTime";
        public static final String USER_INDEX = "userIndex";
        public static final String USER_EMAIL = "userEmail";
        public static final String TYPE = "type";
        public static final String LOGOUT = "logout";
        public static final String LOGIN_FACEBOOK = "loginFacebook";
        public static final String FORGOT_PASSWORD = "forgotPassword";
        public static final String ISUSED = "isUsed";
        public static final String RESERVATION_INDEX = "reservationIndex";
        public static final String PROVINCE = "province";
        public static final String NIGHTS = "ngihts";
        public static final String EVENT = "event";
        public static final String FNB_INDEX = "fnbIndex";
        public static final String FNB_NAME = "fnbName";
        public static final String FNB_TICKET_NAME = "fnbTicketName";
        public static final String FNB_TICKET_INDEX = "fnbTicketIndex";
        public static final String PLACE_NAME = "placeName";
        public static final String PLACE_TICKET_NAME = "placeTicketName";
        public static final String PLACE_TICKET_INDEX = "placeTicketIndex";

    }
}
