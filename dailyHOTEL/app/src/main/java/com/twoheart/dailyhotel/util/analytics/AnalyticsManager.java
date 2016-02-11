package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import java.util.Map;

public class AnalyticsManager
{
    private static AnalyticsManager mInstance = null;
    private GoogleAnalyticsManager mGoogleAnalyticsManager;
    private TuneManager mTuneManager;


    private AnalyticsManager(Context context)
    {
        initAnalytics(context);
    }

    public synchronized static AnalyticsManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new AnalyticsManager(context);
        }
        return mInstance;
    }

    private void initAnalytics(Context context)
    {
        mGoogleAnalyticsManager = new GoogleAnalyticsManager(context);
        mTuneManager = new TuneManager(context);
    }

    public void setUserIndex(String index)
    {
        mGoogleAnalyticsManager.setUserIndex(index);
        mTuneManager.setUserIndex(index);
    }

    public void onResume(Activity activity)
    {
        mGoogleAnalyticsManager.onResume(activity);
        mTuneManager.onResume(activity);
    }

    public void recordScreen(String screenName)
    {
        mGoogleAnalyticsManager.recordScreen(screenName, null);
        mTuneManager.recordScreen(screenName, null);
    }

    public void recordScreen(String screenName, Map<String, String> params)
    {
        mGoogleAnalyticsManager.recordScreen(screenName, params);
        mTuneManager.recordScreen(screenName, params);
    }

    public void recordEvent(String category, String action, String label, Long value)
    {
        mGoogleAnalyticsManager.recordEvent(category, action, label, value);
        mTuneManager.recordEvent(category, action, label, value);
    }

    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        mGoogleAnalyticsManager.recordEvent(category, action, label, params);
        mTuneManager.recordEvent(category, action, label, params);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void eventPaymentCardAdded(String cardType)
    {
        mGoogleAnalyticsManager.eventPaymentCardAdded(cardType);
        mTuneManager.eventPaymentCardAdded(cardType);
    }

    public void recordSocialRegistration(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        mGoogleAnalyticsManager.recordSocialRegistration(userIndex, email, name, gender, phoneNumber, userType);
        mTuneManager.recordSocialRegistration(userIndex, email, name, gender, phoneNumber, userType);
    }

    public void recordRegistration(String userIndex, String email, String name, String phoneNumber, String userType)
    {
        mGoogleAnalyticsManager.recordRegistration(userIndex, email, name, phoneNumber, userType);
        mTuneManager.recordRegistration(userIndex, email, name, phoneNumber, userType);
    }

    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        mGoogleAnalyticsManager.purchaseCompleteHotel(transId, params);
        mTuneManager.purchaseCompleteHotel(transId, params);
    }

    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        mGoogleAnalyticsManager.purchaseCompleteGourmet(transId, params);
        mTuneManager.purchaseCompleteGourmet(transId, params);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Screen
    {
        public static final String SPLASH = "Splash Screen";
        public static final String HOTEL_LIST = "HotelList Screen";
        public static final String HOTEL_DETAIL = "HotelDetail Screen";
        public static final String GOURMET_LIST = "GourmetList Screen";
        public static final String GOURMET_DETAIL = "GourmetDetail Screen";
        public static final String GOURMET_PAYMENT = "GourmetPayment Screen";
        public static final String BOOKING = "Booking Screen";
        public static final String BOOLKING_LIST = "BookingList Screen";
        public static final String BOOKING_DETAIL = "BookingDetail Screen";
        public static final String CREDIT = "Credit Screen";
        public static final String EVENT = "Event Screen";
        public static final String CREDIT_LIST = "CreditList Screen";
        public static final String INFORMATION = "Information Screen";
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
        public static final String PAYMENT_AGREE_POPUP = "paymentAgreePopup";
        public static final String WAIT_TIMER = "WaitTimer Screen";
        public static final String CALENDAR = "Calendar Screen";

        //
        public static final String DAILYHOTEL_DETAIL = "DailyHotel_HotelDetailView";
        public static final String DAILYGOURMET_DETAIL = "DailyGourmet_GourmetDetailView";

        public static final String DAILYHOTEL_PAYMENT = "DailyHotel_BookingInitialise";
        public static final String DAILYGOURMET_PAYMENT = "DailyGourmet_BookingInitialise";
    }

    public static class Action
    {
        public static final String CLICK = "click";
        public static final String SWIPE = "swipe";
        public static final String NETWORK = "network";
        public static final String EVENT = "event";
    }

    public static class Event
    {
        public static final String CARDLIST_ADDED_CARD = "PaymentCardAdded";
        public static final String MENU = "menu";

        public static final String HOTEL_PAYMENT_COMPLETED = "HotelPurchaseComplete";
        public static final String HOTEL_DETAIL_SHARE = "HotelSocialShare";

        public static final String GOURMET_PAYMENT_COMPLETED = "GourmetPurchaseComplete";
        public static final String GOURMET_DETAIL_SHARE = "GourmetSocialShare";

        public static final String BONUS_INVITE_FRIEND = "InviteFriend";
    }


    public static class Label
    {
        public static final String HOTEL = "hotel";
        public static final String GOURMET = "gourmet";
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
        public static final String BOUNS = "bonus";
    }

    public static class UserType
    {
        public static final String KAKAO = "kakao";
        public static final String FACEBOOK = "facebook";
        public static final String EMAIL = "email";
    }

    public static class KeyType
    {
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String QUANTITY = "quantity";
        public static final String TOTAL_PRICE = "totalPrice";
        public static final String PAYMENT_PRICE = "paymentPrice";
        public static final String PLACE_INDEX = "placeIndex";
        public static final String CHECK_IN = "checkIn";
        public static final String CHECK_OUT = "checkOut";
        public static final String DATE = "date";
        public static final String TICKET_NAME = "ticketName";
        public static final String TICKET_INDEX = "ticketIndex";
        public static final String USED_BOUNS = "usedBonus";
        public static final String PAYMENT_TYPE = "paymentType";
        public static final String RESERVATION_TIME = "reservationTime";
        public static final String CURRENT_TIME = "currentTime";
        public static final String USER_INDEX = "userIndex";
    }
}
