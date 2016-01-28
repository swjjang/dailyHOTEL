package com.twoheart.dailyhotel.util;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.mobileapptracker.MATEvent;
import com.mobileapptracker.MATGender;
import com.mobileapptracker.MobileAppTracker;

import java.util.Map;

public class AnalyticsManager
{
    private static AnalyticsManager mInstance = null;
    private GoogleAnalytics mGoogleAnalytics;
    private Tracker mTracker;

    // Tune
    private MobileAppTracker mMobileAppTracker;

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
        initGoogleAnalytics(context);
        initTune(context);
    }

    private void initGoogleAnalytics(Context context)
    {
        mGoogleAnalytics = GoogleAnalytics.getInstance(context);
        mGoogleAnalytics.setLocalDispatchPeriod(60);

        mTracker = mGoogleAnalytics.newTracker(Constants.GA_PROPERTY_ID);
        mTracker.enableAdvertisingIdCollection(true);
    }

    private void initTune(Context context)
    {
        mMobileAppTracker = MobileAppTracker.init(context.getApplicationContext(), "190723", "93aa9a40026991386dd92922cb14f58f");

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

    public void setUserIndex(String index)
    {
        mTracker.set("userId", index);
        mMobileAppTracker.setUserId(index);
    }

    public void onResume(Activity activity)
    {
        mMobileAppTracker.setReferralSources(activity);
        mMobileAppTracker.measureSession();
    }

    public void recordSocialRegistration(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        // Tune
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

        MATEvent matEvent = new MATEvent(MATEvent.REGISTRATION)//
            .withAttribute1(userType);

        mMobileAppTracker.measureEvent(matEvent);
    }

    public void recordRegistration(String userIndex, String email, String name, String phoneNumber, String userType)
    {
        // Tune
        mMobileAppTracker.setUserId(userIndex);
        mMobileAppTracker.setUserEmail(email);
        mMobileAppTracker.setUserName(name);
        mMobileAppTracker.setPhoneNumber(phoneNumber);
        mMobileAppTracker.setCurrencyCode("KRW");

        MATEvent matEvent = new MATEvent(MATEvent.REGISTRATION)//
            .withAttribute1(userType);

        mMobileAppTracker.measureEvent(matEvent);
    }

    public void recordScreen(String screenName)
    {
        try
        {
            // Send a screen view.
            mTracker.setScreenName(screenName);
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void recordEvent(String category, String action, String label, Long value)
    {
        try
        {
            mTracker.send(new HitBuilders.EventBuilder()//
                .setCategory(category).setAction(action)//
                .setLabel(label).setValue(value).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        try
        {
            mTracker.send(new HitBuilders.EventBuilder()//
                .setCategory(category).setAction(action)//
                .setLabel(label).setAll(params).build());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    /**
     * 구매 완료 하였으면 구글 애널래틱스 Ecommerce Tracking 을 위하여 필히 호출한다. 실제 우리 앱의 매출을 자동으로
     * 집계하여 알기위함.
     *
     * @param transId
     * @param userIndex
     * @param roomIndex
     * @param hotelName
     * @param category
     * @param checkInTime
     * @param checkOutTime
     * @param payType
     * @param currentTime
     * @param price
     */
    public void purchaseComplete(String transId, String userIndex, String roomIndex, String hotelName, String category, String checkInTime, String checkOutTime, String payType, String currentTime, double price)
    {
        try
        {
            Product product = new Product().setId(roomIndex).setName(hotelName)//
                .setCategory(category).setBrand("DAILYHOTEL").setPrice(price).setQuantity(1);//
            //                .setCustomDimension(1, "User Index : " + userIndex)//
            //                .setCustomDimension(2, "Check-In : " + checkInTime)//
            //                .setCustomDimension(3, "Check-Out : " + checkOutTime)//
            //                .setCustomDimension(4, "Pay Type" + payType)//
            //                .setCustomDimension(5, "Current Time : " + currentTime);

            ProductAction productAction = new ProductAction(ProductAction.ACTION_PURCHASE)//
                .setTransactionId(transId);

            HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder().addProduct(product).setProductAction(productAction);

            mTracker.set("&cu", "KRW");
            mTracker.send(screenViewBuilder.build());

            recordEvent("Purchase", "PurchaseComplete", "PurchaseComplete", 1L);
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
        public static final String MENU = "menu";
        public static final String PAYMENT_AGREE_POPUP = "paymentAgreePopup";
        public static final String WAIT_TIMER = "WaitTimer Screen";
        public static final String CALENDAR = "Calendar Screen";
    }

    public static class Action
    {
        public static final String CLICK = "click";
        public static final String SWIPE = "swipe";
        public static final String NETWORK = "network";
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
}
