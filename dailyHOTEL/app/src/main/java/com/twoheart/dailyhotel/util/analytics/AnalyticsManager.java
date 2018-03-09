package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsManager
{
    private static final String TAG = "[AnalyticsManager]";

    // 추후에 작업을 해볼까 생각중
    private static final boolean ENABLED_GOOGLE = true;
    private static final boolean ENABLED_FACEBOOK = true;
    private static final boolean ENABLED_APPBOY = true;

    private static AnalyticsManager mInstance = null;
    private GoogleAnalyticsManager mGoogleAnalyticsManager;
    private FacebookManager mFacebookManager;
    private AppboyManager mAppboyManager;
    private AdjustManager mAdjustManager;
    private FirebaseManager mFirebaseManager;
    private KakaoManager mKakaoManager;
    private List<BaseAnalyticsManager> mAnalyticsManagerList;

    public synchronized static AnalyticsManager getInstance(Context context)
    {
        if (mInstance == null)
        {
            mInstance = new AnalyticsManager(context);
        }
        return mInstance;
    }

    private AnalyticsManager(Context context)
    {
        mAnalyticsManagerList = new ArrayList<>();

        initAnalytics(context);
    }

    private void initAnalytics(Context context)
    {
        try
        {
            mGoogleAnalyticsManager = new GoogleAnalyticsManager(context, new GoogleAnalyticsManager.OnClientIdListener()
            {
                @Override
                public void onResponseClientId(String clientId)
                {
                }
            });
        } catch (Exception e)
        {
            ExLog.d((e.toString()));
        }

        try
        {
            mFacebookManager = new FacebookManager(context);
        } catch (Exception e)
        {
            ExLog.d((e.toString()));
        }

        try
        {
            mAppboyManager = new AppboyManager(context);
        } catch (Exception e)
        {
            ExLog.d((e.toString()));
        }

        try
        {
            mAdjustManager = new AdjustManager(context);
        } catch (Exception e)
        {
            ExLog.d((e.toString()));
        }

        try
        {
            mFirebaseManager = new FirebaseManager(context);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        try
        {
            mKakaoManager = new KakaoManager(context);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (mGoogleAnalyticsManager != null)
        {
            mAnalyticsManagerList.add(mGoogleAnalyticsManager);
        }

        if (mFacebookManager != null)
        {
            mAnalyticsManagerList.add(mFacebookManager);
        }

        if (mAppboyManager != null)
        {
            mAnalyticsManagerList.add(mAppboyManager);
        }

        if (mAdjustManager != null)
        {
            mAnalyticsManagerList.add(mAdjustManager);
        }

        if (mFirebaseManager != null)
        {
            mAnalyticsManagerList.add(mFirebaseManager);
        }

        if (mKakaoManager != null)
        {
            mAnalyticsManagerList.add(mKakaoManager);
        }
    }

    public GoogleAnalyticsManager getGoogleAnalyticsManager()
    {
        return mGoogleAnalyticsManager;
    }

    public void setUserInformation(String index, String userType)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                analyticsManager.setUserInformation(index, userType);
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void setUserBirthday(String birthday)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                analyticsManager.setUserBirthday(birthday);
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void setExceedBonus(boolean isExceedBonus)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                analyticsManager.setExceedBonus(isExceedBonus);
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // ActivityLifecycleCallbacks
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void onActivityCreated(Activity activity, Bundle bundle)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityCreated(activity, bundle);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivityStarted(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityStarted(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivityStopped(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityStopped(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivityResumed(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityResumed(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivityPaused(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityPaused(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle bundle)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivitySaveInstanceState(activity, bundle);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onActivityDestroyed(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onActivityDestroyed(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordScreen(Activity activity, String screenName, String screenClassOverride)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordScreen(activity, screenName, screenClassOverride);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordScreen(Activity activity, String screenName, String screenClassOverride, Map<String, String> params)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordScreen(activity, screenName, screenClassOverride, params);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordEvent(category, action, label, params);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }


    public void recordEvent(String category, String action, String label, long value, Map<String, String> params)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordEvent(category, action, label, value, params);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordDeepLink(DailyDeepLink dailyDeepLink)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordDeepLink(dailyDeepLink);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void currentAppVersion(String version)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.currentAppVersion(version);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void addCreditCard(String cardType)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.addCreditCard(cardType);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void updateCreditCard(String cardTypes)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.updateCreditCard(cardTypes);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void signUpSocialUser(String userIndex, String gender, String userType, String callByScreen)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.signUpSocialUser(userIndex, gender, userType, callByScreen);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void signUpDailyUser(String userIndex, String birthday, String userType, String recommender, String callByScreen)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.signUpDailyUser(userIndex, birthday, userType, recommender, callByScreen);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void purchaseCompleteHotel(String aggregationId, Map<String, String> params)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                try
                {
                    analyticsManager.purchaseCompleteHotel(aggregationId, params);
                } catch (Exception e)
                {
                    ExLog.d(TAG + e.toString());
                }
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void purchaseCompleteStayOutbound(String aggregationId, Map<String, String> params)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                try
                {
                    analyticsManager.purchaseCompleteStayOutbound(aggregationId, params);
                } catch (Exception e)
                {
                    ExLog.d(TAG + e.toString());
                }
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void purchaseCompleteGourmet(String aggregationId, Map<String, String> params)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                try
                {
                    analyticsManager.purchaseCompleteGourmet(aggregationId, params);
                } catch (Exception e)
                {
                    ExLog.d(TAG + e.toString());
                }
            }
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void startDeepLink(Uri deepLinkUri)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.startDeepLink(deepLinkUri);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void startApplication()
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.startApplication();
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onRegionChanged(String country, String provinceName)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onRegionChanged(country, provinceName);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void setPushEnabled(boolean onOff, String pushSettingType)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.setPushEnabled(onOff, pushSettingType);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void purchaseWithCoupon(Map<String, String> param)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.purchaseWithCoupon(param);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Screen
    {
        public static final String DAILYHOTEL_LIST = "DailyHotel_HotelList";
        public static final String DAILYHOTEL_LIST_MAP = "DailyHotel_HotelMapView";
        public static final String DAILYHOTEL_LIST_EMPTY = "DailyHotel_NotHotelAvailable";
        public static final String DAILYHOTEL_LIST_REGION_DOMESTIC = "DailyHotel_HotelDomesticLocationList";
        public static final String DAILYHOTEL_HOTEL_DOMESTIC_SUBWAY_LIST = "DailyHotel_HotelDomesticSubwayList";
        public static final String DAILYHOTEL_LIST_REGION_GLOBAL = "DailyHotel_HotelGlobalLocationList";
        public static final String DAILYHOTEL_LIST_CALENDAR = "DailyHotel_HotelBookingWindow";
        public static final String DAILYHOTEL_CURATION = "DailyHotel_SortFilterSelectView";
        //
        public static final String DAILYHOTEL_DETAIL = "DailyHotel_HotelDetailView";
        public static final String DAILYHOTEL_DETAIL_ROOMTYPE = " DailyHotel_HotelRoomTypeList";
        public static final String DAILYHOTEL_DETAIL_MAP = "DailyHotel_HotelDetailMapView";
        //
        public static final String DAILYHOTEL_BOOKINGINITIALISE = "DailyHotel_BookingInitialise";
        public static final String DAILYHOTEL_PAYMENT_AGREEMENT_POPUP = "DailyHotel_PaymentAgreementPopupScreen";
        public static final String DAILYHOTEL_PAYMENT_PROCESS = "DailyHotel_PaymentGateway";
        public static final String DAILYHOTEL_PAYMENT_COMPLETE = "DailyHotel_PaymentComplete";
        public static final String DAILYHOTEL_PAYMENT_THANKYOU = "DailyHotel_Thankyou";
        //
        public static final String DAILYGOURMET_LIST = "DailyGourmet_GourmetList";
        public static final String DAILYGOURMET_LIST_MAP = "DailyGourmet_GourmetMapView";
        public static final String DAILYGOURMET_LIST_EMPTY = "DailyGourmet_NotGourmetAvailable";
        public static final String DAILYGOURMET_LIST_REGION_DOMESTIC = "DailyGourmet_GourmetLocationList";
        public static final String DAILYGOURMET_LIST_CALENDAR = "DailyGourmet_GourmetBookingWindow";
        public static final String DAILYGOURMET_CURATION = "DailyGourmet_SortFilterSelectView";
        //
        public static final String DAILYGOURMET_DETAIL = "DailyGourmet_GourmetDetailView";
        public static final String DAILYGOURMET_DETAIL_MAP = "DailyGourmet_GourmetDetailMapView";
        //
        public static final String DAILYGOURMET_BOOKINGINITIALISE = "DailyGourmet_BookingInitialise";
        public static final String DAILYGOURMET_PAYMENT_AGREEMENT_POPUP = "DailyGourmet_PaymentAgreementPopupScreen";
        public static final String DAILYGOURMET_PAYMENT_PROCESS = "DailyGourmet_PaymentGateway";
        public static final String DAILYGOURMET_PAYMENT_COMPLETE = "DailyGourmet_PaymentComplete";
        public static final String DAILYGOURMET_PAYMENT_THANKYOU = "DailyGourmet_Thankyou";
        //
        //
        public static final String BOOKING_LIST = "Booking_BookingStatusList";
        public static final String BOOKING_LIST_EMPTY = "Booking_NoBookingHistory";
        public static final String BOOKING_BEFORE_LOGIN_BOOKING_LIST = "Booking_BeforeLoginBookingList";

        //
        public static final String BOOKINGDETAIL_MYBOOKINGINFO = "BookingDetail_MyBookingInfo";
        public static final String BOOKINGDETAIL_MYBOOKINGINFO_POST_VISIT = "BookingDetail_MyBookingInfo_post_visit";
        public static final String BOOKINGDETAIL_MYBOOKINGINFO_TEMPORARY_ACCOUNT = "BookingDetail_MyBookingInfo_temporary_account";
        public static final String BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATION_PROGRESS = "BookingDetail_MyBookingInfo_cancellation_progress";
        public static final String BOOKING_DETAIL_MAP = "BookingDetail_MapView";
        public static final String BOOKING_DETAIL_RECEIPT = "BookingDetail_Receipt";

        //
        public static final String SIGNIN = "Menu_Login";
        public static final String MENU_REGISTRATION = "Menu_Registration";
        public static final String MENU_REGISTRATION_CONFIRM = "Menu_Registration_Confirm";
        public static final String MENU_LOGIN_COMPLETE = "Menu_Login_Complete";
        public static final String MENU_LOGOUT_COMPLETE = "Menu_Logout_Complete";
        //
        public static final String TERMSOFUSE = "Menu_TermsofUse";
        public static final String TERMSOFPRIVACY = "Menu_TermsofPrivacy";
        public static final String FORGOTPASSWORD = "Menu_LostPassword";
        public static final String PROFILE = "Menu_Profile";
        public static final String TERMSOFLOCATION = "Menu_TermsofLocation";
        public static final String TERMSOFJUVENILE = "Menu_TermsofJuvenile";
        //
        public static final String CREDITCARD_LIST = "Menu_PaymentCardRegistered";
        public static final String CREDITCARD_LIST_EMPTY = "Menu_NoCardRegistered";
        public static final String CREDITCARD_ADD = "Menu_AddingPaymentCard";
        //
        public static final String BONUS = "Menu_CreditManagement";
        public static final String BONUS_BEFORE_LOGIN = "Menu_BeforeLoginCreditManagement";
        public static final String EVENT_LIST = "Menu_EventList";
        public static final String EVENT_DETAIL = "Menu_EventDetailView";
        public static final String ABOUT = "Menu_ServiceIntro";
        public static final String NETWORK_ERROR = "Error_NetworkDisconnected";
        //
        public static final String MENU_REGISTRATION_GETINFO = "Menu_Registration_GetInfo";
        public static final String MENU_REGISTRATION_PHONENUMBERVERIFICATION = "Menu_Registration_PhoneNumberVerification";
        public static final String MENU_SETPROFILE_EMAILACCOUNT = "Menu_SetProfileEmailAccount";
        public static final String MENU_SETPROFILE_NAME = "Menu_SetProfileName";
        public static final String MENU_SET_MY_BIRTHDAY = "Menu_SetMyBirthday";
        public static final String MENU_SETPROFILE_PASSWORD = "Menu_SetProfilePassword";
        public static final String MENU_SETPROFILE_PHONENUMBER = "Menu_SetProfilePhoneNumber";
        public static final String MENU_COUPON_BOX = "Menu_CouponBox";
        public static final String MENU_INVITE_FRIENDS_BEFORE_LOGIN = "Menu_InviteFriends_BeforeLogIn";
        public static final String MENU_INVITE_FRIENDS = "Menu_InviteFriends";
        public static final String MENU_COUPON_HISTORY = "Menu_CouponHistory";
        public static final String MENU_COUPON_GENERAL_TERMS_OF_USE = "Menu_CouponGeneralTermsofUse";
        public static final String MENU_COUPON_INDIVIDUAL_TERMS_OF_USE = "Menu_CouponIndividualTermsofUse";
        public static final String DAILY_HOTEL_AVAILABLE_COUPON_LIST = "DailyHotel_AvailableCouponList";
        public static final String DAILY_HOTEL_UNAVAILABLE_COUPON_LIST = "DailyHotel_UnavailableCouponList";
        public static final String DAILY_GOURMET_AVAILABLE_COUPON_LIST = "DailyGourmet_AvailableCouponList";
        public static final String MENU_COUPON_REGISTRATION = "Menu_CouponRegistration";
        //
        public static final String SEARCH_MAIN = "SearchScreenView";
        public static final String SEARCH_RESULT = "SearchResultView";
        public static final String SEARCH_RESULT_EMPTY = "SearchResultView_Empty";
        //
        public static final String BOOKING_ACCOUNTDETAIL = "Booking_AccountDetail";
        public static final String DAILY_HOTEL_FIRST_PURCHASE_SUCCESS = "DailyHotel_FirstPurchaseSuccess";
        public static final String DAILY_GOURMET_FIRST_PURCHASE_SUCCESS = "DailyGourmet_FirstPurchaseSuccess";
        //
        public static final String APP_LAUNCHED = "app_launched";
        //
        public static final String MENU_NOTICELIST = "Menu_NoticeList";
        public static final String MENU_NOTICEDETAILVIEW = "Menu_NoticeDetailView";

        public static final String MENU_RECENT_VIEW = "Menu_RecentView";
        public static final String MENU_RECENT_VIEW_EMPTY = "Menu_RecentView_Empty";

        public static final String MENU_WISHLIST = "Menu_WishList";
        public static final String MENU_WISHLIST_EMPTY = "Menu_WishList_Empty";
        public static final String MENU_WISHLIST_BEFORELOGIN = "Menu_WishList_BeforeLogin";

        //
        public static final String DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE = "DailyHotel_BookingInitialise_Cancelable";
        public static final String DAILYHOTEL_BOOKINGINITIALISE_CANCELLATIONFEE = "DailyHotel_BookingInitialise_CancellationFee";
        public static final String DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS = "DailyHotel_BookingInitialise_NoRefunds";

        public static final String BOOKINGDETAIL_MYBOOKINGINFO_CANCELABLE = "BookingDetail_MyBookingInfo_Cancelable";
        public static final String BOOKINGDETAIL_MYBOOKINGINFO_CANCELLATIONFEE = "BookingDetail_MyBookingInfo_CancellationFee";
        public static final String BOOKINGDETAIL_MYBOOKINGINFO_NOREFUNDS = "BookingDetail_MyBookingInfo_NoRefunds";

        // Review
        public static final String DAILYHOTEL_SATISFACTIONEVALUATION = "DailyHotel_SatisfactionEvaluation";
        public static final String DAILYHOTEL_SATISFACTIONEVALUATION_OUTBOUND = "DailyHotel_SatisfactionEvaluation_outbound";
        public static final String DAILYHOTEL_REVIEWDETAIL = "DailyHotel_ReviewDetail";
        public static final String DAILYHOTEL_REVIEWDETAIL_OUTBOUND = "DailyHotel_ReviewDetail_outbound";
        public static final String DAILYHOTEL_REVIEWWRITE = "DailyHotel_ReviewWrite";
        public static final String DAILYHOTEL_REVIEWWRITE_OUTBOUND = "DailyHotel_ReviewWrite_outbound";
        public static final String DAILYHOTEL_REVIEWEDIT = "DailyHotel_ReviewEdit";
        public static final String DAILYGOURMET_SATISFACTIONEVALUATION = "DailyGourmet_SatisfactionEvaluation";
        public static final String DAILYGOURMET_REVIEWDETAIL = "DailyGourmet_ReviewDetail";
        public static final String DAILYGOURMET_REVIEWWRITE = "DailyGourmet_ReviewWrite";
        public static final String DAILYGOURMET_REVIEWEDIT = "DailyGourmet_ReviewEdit";

        // Detail Image List
        public static final String DAILYHOTEL_HOTELIMAGEVIEW = "DailyHotel_HotelImageView";
        public static final String DAILYGOURMET_GOURMETIMAGEVIEW = "DailyGourmet_GourmetImageView";

        public static final String HOME_EVENT_DETAIL = "home_event_detail";
        public static final String RECOMMEND_LIST = "recommend_list";
        public static final String MYDAILY = "mydaily";
        public static final String TERMS_AND_CONDITION = "terms_and_condition";
        public static final String MENU = "menu";
        public static final String HOME = "home";

        public static final String GOURMET_MENU_DETAIL = "gourmet_menu_detail";

        // TRUEREVIEW
        public static final String TRUE_REVIEW_LIST = "true_review_list";

        // Preview
        public static final String PEEK_POP = "peek_pop";

        // True VR
        public static final String TRUE_VR = "true_vr";

        // HOME - Stay Category
        public static final String STAY_LIST_SHORTCUT_HOTEL = "stay_list_shortcut_hotel";
        public static final String STAY_LIST_SHORTCUT_BOUTIQUE = "stay_list_shortcut_boutique";
        public static final String STAY_LIST_SHORTCUT_PENSION = "stay_list_shortcut_pension";
        public static final String STAY_LIST_SHORTCUT_RESORT = "stay_list_shortcut_resort";
        public static final String STAY_LIST_SHORTCUT_NEARBY = "stay_list_shortcut_nearby";

        // STAY OUTBOUND
        public static final String SEARCHSCREENVIEW_OUTBOUND = "SearchScreenView_outbound";
        public static final String DAILYHOTEL_HOTELLIST_OUTBOUND = "DailyHotel_HotelList_outbound";
        public static final String DAILYHOTEL_HOTELDETAILVIEW_OUTBOUND = "DailyHotel_HotelDetailView_outbound";
        public static final String DAILYHOTEL_HOTELROOMTYPELIST_OUTBOUND = "DailyHotel_HotelRoomTypeList_outbound";
        public static final String DAILYHOTEL_BOOKINGINITIALISE_CANCELABLE_OUTBOUND = "DailyHotel_BookingInitialise_Cancelable_outbound";
        public static final String DAILYHOTEL_BOOKINGINITIALISE_NOREFUNDS_OUTBOUND = "DailyHotel_BookingInitialise_NoRefunds_outbound";
        public static final String DAILYHOTEL_PAYMENTCOMPLETE_OUTBOUND = "DailyHotel_PaymentComplete_outbound";
        public static final String DAILYHOTEL_THANKYOU_OUTBOUND = "DailyHotel_Thankyou_outbound";
        public static final String DAILYHOTEL_PAYMENT_PROCESS_OUTBOUND = "DailyHotel_PaymentGateway_outbound";

        public static final String CANCEL_DETAIL = "cancel_detail";
        public static final String CANCEL_LIST = "cancel_list";

        // REWARD
        public static final String DAILY_REWARD_DETAIL = "daily_reward_detail";

        // 회원 탈퇴
        public static final String MEMBER_LEAVE_STEP_1 = "member_leave_step_1";
        public static final String MEMBER_LEAVE_STEP_2 = "member_leave_step_2";
        public static final String MEMBER_LEAVE_STEP_3 = "member_leave_step_3";
    }

    public static class Action
    {
        public static final String HOTEL_LOCATIONS_CLICKED = "HotelLocationsClicked";
        public static final String HOTEL_CATEGORY_CLICKED = "DailyHotelCategoryClicked";
        public static final String HOTEL_SORT_FILTER_BUTTON_CLICKED = "HotelSortFilterButtonClicked";
        //        public static final String HOTEL_SORT_FILTER_BUTTON_UNCLICKED = "HotelSortFilterButtonUnClicked";
        public static final String HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED = "HotelSortFilterApplyButtonClicked";
        public static final String HOTEL_DETAIL_MAP_CLICKED = "HotelDetailMapClicked";
        public static final String HOTEL_DETAIL_ADDRESS_COPY_CLICKED = "HotelDetailAddressCopyClicked";
        public static final String HOTEL_DETAIL_NAVIGATION_APP_CLICKED = "HotelDetailNavigationAppClicked";
        public static final String HOTEL_BOOKING_DATE_CLICKED = "HotelBookingDateClicked";
        public static final String HOTEL_COUPON_DOWNLOAD = "HotelCouponDownload";
        //
        public static final String SOCIAL_SHARE_CLICKED = "SocialShareClicked";
        public static final String ROOM_TYPE_CLICKED = "RoomTypeClicked";
        public static final String ROOM_TYPE_ITEM_CLICKED = "RoomTypeItemClicked";
        public static final String ROOM_TYPE_CANCEL_CLICKED = "RoomTypeCancelClicked";
        public static final String BOOKING_CLICKED = "BookingClicked";
        public static final String BOOKING_CLICKED_OUTBOUND = "BookingClicked_outbound";
        public static final String USING_CREDIT_CLICKED = "UsingCreditClicked";
        public static final String USING_CREDIT_CANCEL_CLICKED = "UsingCreditCancelClicked";
        public static final String PAYMENT_TYPE_ITEM_CLICKED = "PaymentTypeItemClicked";
        public static final String EDIT_BUTTON_CLICKED = "EditButtonClicked";
        public static final String PAYMENT_CLICKED = "PaymentClicked";
        public static final String PAYMENT_AGREEMENT_POPPEDUP = "PaymentAgreementPoppedup";
        public static final String HOTEL_PAYMENT_COMPLETED = "HotelPaymentCompleted";
        public static final String GOURMET_LOCATIONS_CLICKED = "GourmetLocationsClicked";
        public static final String GOURMET_SORT_FILTER_BUTTON_CLICKED = "GourmetSortFilterButtonClicked";
        public static final String GOURMET_SORT_FILTER_BUTTON_UNCLICKED = "GourmetSortFilterButtonUnClicked";
        public static final String GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED = "GourmetSortFilterApplyButtonClicked";
        public static final String GOURMET_DETAIL_MAP_CLICKED = "GourmetDetailMapClicked";
        public static final String GOURMET_DETAIL_ADDRESS_COPY_CLICKED = "GourmetDetailAddressCopyClicked";
        public static final String GOURMET_DETAIL_NAVIGATION_APP_CLICKED = "GourmetDetailNavigationAppClicked";
        public static final String GOURMET_BOOKING_DATE_CLICKED = "GourmetBookingDateClicked";
        public static final String GOURMET_COUPON_DOWNLOAD = "GourmetCouponDownload";
        //
        public static final String TICKET_TYPE_CLICKED = "TicketTypeClicked";
        public static final String TICKET_TYPE_ITEM_CLICKED = "TicketTypeItemClicked";
        public static final String TICKET_TYPE_CANCEL_CLICKED = "TicketTypeCancelClicked";
        public static final String GOURMET_PAYMENT_COMPLETED = "GourmetPaymentCompleted";
        public static final String LOGIN_CLICKED = "LoginClicked";
        public static final String REGISTRATION_CLICKED = "RegistrationClicked";
        public static final String REGISTRATION_COMPLETE = "RegistrationComplete";
        public static final String REGISTRATION_REJECTED = "RegistrationRejected";
        public static final String CARD_MANAGEMENT_CLICKED = "CardManagementClicked";
        public static final String REGISTERED_CARD_DELETE_POPPEDUP = "RegisteredCardDeletePoppedup";
        public static final String CREDIT_MANAGEMENT_CLICKED = "CreditManagementClicked";
        public static final String INVITE_FRIEND_CLICKED = "InviteFriendClicked";
        public static final String EVENT_CLICKED = "EventClicked";
        //
        public static final String SATISFACTION_EVALUATION_POPPEDUP = "SatisfactionEvaluationPoppedup";
        //
        public static final String THANKYOU_SCREEN_BUTTON_CLICKED = "ThankyouScreenButtonClicked";
        //
        public static final String HOTEL_KEYWORD_SEARCH_CLICKED = "HotelKeywordSearchClicked"; // 앱보이 사용
        public static final String HOTEL_KEYWORD_SEARCH_NOT_FOUND = "HotelKeywordSearchNotFound";
        //
        public static final String GOURMET_KEYWORD_SEARCH_CLICKED = "GourmetKeywordSearchClicked";
        public static final String GOURMET_KEYWORD_SEARCH_NOT_FOUND = "GourmetKeywordSearchNotFound";
        //
        public static final String LOCATION_AGREEMENT_POPPEDUP = "LocationAgreementPoppedup";
        //
        public static final String UPCOMING_BOOKING_MAP_VIEW_CLICKED = "UpcomingBookingMapViewClicked";
        public static final String UPCOMING_BOOKING_ADDRESS_COPY_CLICKED = "UpcomingBookingAddressCopyClicked";
        public static final String UPCOMING_BOOKING_NAVIGATION_APP_CLICKED = "UpcomingBookingNavigationAppClicked";
        public static final String PAST_BOOKING_MAP_VIEW_CLICKED = "PastBookingMapViewClicked";
        public static final String PAST_BOOKING_ADDRESS_COPY_CLICKED = "PastBookingAddressCopyClicked";
        public static final String PAST_BOOKING_NAVIGATION_APP_CLICKED = "PastBookingNavigationAppClicked";
        //
        public static final String HOTEL_BOOKING_CALENDAR_CLOSED = "HotelBookingCalendarClosed";
        public static final String HOTEL_BOOKING_CALENDAR_CLICKED = "HotelBookingCalendarClicked";

        public static final String GOURMET_BOOKING_CALENDAR_CLOSED = "GourmetBookingCalendarClosed";
        public static final String GOURMET_BOOKING_CALENDAR_CLICKED = "GourmetBookingCalendarClicked";
        //
        public static final String HOTEL_BOOKING_DATE_CONFIRMED = "HotelBookingDateConfirmed";
        public static final String HOTEL_BOOKING_DATE_CHANGED = "HotelBookingDateChanged";
        //
        public static final String NOTIFICATION_SETTING_CLICKED = "NotificationSettingClicked";
        public static final String COUPON_BOX_CLICKED = "CouponBoxClicked";
        public static final String COUPON_DOWNLOAD_CLICKED = "CouponDownloadClicked";
        public static final String REFERRAL_CODE_COPIED = "ReferralCodeCopied";
        public static final String KAKAO_FRIEND_INVITED = "KakaoFriendInvited";
        public static final String HOTEL_USING_COUPON_CLICKED = "HotelUsingCouponClicked";
        public static final String HOTEL_COUPON_SELECTED = "HotelCouponSelected";
        public static final String HOTEL_USING_COUPON_CANCEL_CLICKED = "HotelUsingCouponCancelClicked";
        public static final String HOTEL_COUPON_NOT_FOUND = "HotelCouponNotFound";
        public static final String GOURMET_USING_COUPON_CLICKED = "GourmetUsingCouponClicked";
        public static final String GOURMET_COUPON_SELECTED = "GourmetCouponSelected";
        public static final String GOURMET_USING_COUPON_CANCEL_CLICKED = "GourmetUsingCouponCancelClicked";
        public static final String GOURMET_COUPON_NOT_FOUND = "GourmetCouponNotFound";
        public static final String GOURMET_COUPON_DOWNLOADED = "GourmetCouponDownloaded";
        //
        public static final String FIRST_NOTIFICATION_SETTING_CLICKED = "FirstNotificationSettingClicked";
        //
        public static final String CHANGE_LOCATION = "ChangeLocation";
        public static final String CHANGE_VIEW = "ChangeView";
        public static final String DAILY_HOTEL_CATEGORY_FLICKING = "DailyHotelCategoryFlicking";
        public static final String HOTEL_MAP_ICON_CLICKED = "HotelMapIconClicked";
        public static final String HOTEL_MAP_DETAIL_VIEW_CLICKED = "HotelMapDetailViewClicked";
        public static final String GOURMET_MAP_ICON_CLICKED = "GourmetMapIconClicked";
        public static final String GOURMET_MAP_DETAIL_VIEW_CLICKED = "GourmetMapDetailViewClicked";

        public static final String SEARCH_BUTTON_CLICKED = "SearchButtonClicked";
        public static final String KEYWORD_NOT_FOUND = "KeywordNotFound";
        public static final String KEYWORD_ = "Keyword";
        public static final String SEARCH_SCREEN = "SearchScreen";
        public static final String SEARCH_RESULT_VIEW = "SearchResultView";
        //
        public static final String SOLDOUT_CHANGEPRICE = "Soldout_ChangePrice";
        public static final String SOLDOUT_DEEPLINK = "Soldout_Deeplink";
        public static final String SOLDOUT = "Soldout";
        public static final String AROUND_SEARCH_NOT_FOUND = "AroundSearchNotFound";
        public static final String AROUND_SEARCH_NOT_FOUND_LOCATIONLIST = "AroundSearchNotFound_LocationList";
        public static final String AROUND_SEARCH_CLICKED = "AroundSearchClicked";
        public static final String AROUND_SEARCH_CLICKED_LOCATIONLIST = "AroundSearchClicked_LocationList";
        public static final String RECENT_KEYWORD_NOT_FOUND = "RecentKeywordNotFound";
        public static final String RECENT_KEYWORD = "RecentKeyword";
        //
        public static final String LOGIN_COMPLETE = "LoginComplete";
        public static final String SIGN_UP = "SignUp";
        public static final String START_PAYMENT = "StartPayment";
        public static final String END_PAYMENT = "EndPayment";
        public static final String PAYMENT_USED = "PaymentUsed";
        public static final String COUPON_LOGIN = "CouponLogin";
        //
        public static final String ACCOUNT_DETAIL = "AccountDetail";
        public static final String PRODUCT_ID = "ProductID";
        public static final String FIRST_PURCHASE_SUCCESS = "FirstPurchaseSuccess";
        //
        public static final String BOOKING_DETAIL = "BookingDetail";
        public static final String MENU = "Menu";
        public static final String DEPOSIT_WAITING = "DepositWaiting";
        public static final String BOOKING_INITIALISE = "BookingInitialise";
        public static final String BOOKING_STATUS_CHECK = "booking_status_check";
        public static final String BOOKING_DETAIL_CLICK = "booking_detail_click";
        //
        public static final String LOST_PASSWORD_CLICKED = "LostPasswordClicked";

        public static final String RECENT_VIEW_CLICKED = "RecentViewClicked";
        public static final String RECENT_VIEW_DELETE = "RecentViewDelete";
        public static final String RECENT_VIEW_TAB_CHANGE = "RecentViewTabChange";

        public static final String WISHLIST_CLICKED = "WishListClicked";
        public static final String WISHLIST_DELETE = "WishListDelete";
        public static final String WISHLIST_TAB_CHANGE = "WishListTabChange";
        public static final String WISHLIST_ON = "WishListOn";
        public static final String WISHLIST_OFF = "WishListOff";
        public static final String WISHLIST_ON_DETAIL = "WishListOn_detail";
        public static final String WISHLIST_OFF_DETAIL = "WishListOff_detail";
        public static final String WISHLIST_ON_LIST = "WishListOn_list";
        public static final String WISHLIST_OFF_LIST = "WishListOff_list";
        public static final String WISHLIST_ON_PREVIEW = "WishListOn_preview";
        public static final String WISHLIST_OFF_PREVIEW = "WishListOff_preview";
        public static final String WISHLIST_LOGIN_CLICKED = "WishListLoginClicked";

        public static final String REFERRAL_CODE = "ReferralCode";
        //
        public static final String REFUND_INQUIRY_CLICKED = "RefundInquiryClicked";
        public static final String REFUND_INQUIRY = "RefundInquiry";
        public static final String FREE_CANCELLATION_CLICKED = "FreeCancellationClicked";
        public static final String FREE_CANCELLATION = "FreeCancellation";

        public static final String PROFILE_CLICKED = "ProfileClicked";

        // Review
        public static final String REVIEW_DETAIL = "ReviewDetail";
        public static final String REVIEW_DETAIL_OB = "ReviewDetail_ob";
        public static final String REVIEW_POPUP = "ReviewPopup";
        public static final String REVIEW_POPUP_OB = "ReviewPopup_ob";
        public static final String REVIEW_WRITE = "ReviewWrite";
        public static final String REVIEW_WRITE_OB = "ReviewWrite_ob";

        // Detail Image List
        public static final String HOTEL_IMAGE_CLICKED = "HotelImageClicked";
        public static final String GOURMET_IMAGE_CLICKED = "GourmetImageClicked";
        public static final String GOURMET_MENU_IMG_CLICK = "gourmet_menu_img_click";

        public static final String HOTEL_IMAGE_CLOSED = "HotelImageClosed";
        public static final String GOURMET_IMAGE_CLOSED = "GourmetImageClosed";

        //
        public static final String WAYTOVISIT_SELECTED = "WaytovisitSelected";

        //
        public static final String ITEM_SHARE = "item_share";
        public static final String STAY_ITEM_SHARE = "stay_item_share";
        public static final String GOURMET_ITEM_SHARE = "gourmet_item_share";
        public static final String OB_ITEM_SHARE = "ob_item_share";
        public static final String BOOKING_SHARE = "booking_share";
        public static final String STAY_BOOKING_SHARE = "stay_booking_share";
        public static final String GOURMET_BOOKING_SHARE = "gourmet_booking_share";
        public static final String OB_BOOKING_SHARE = "ob_booking_share";
        public static final String STAY_CANCEL_SHARE = "stay_cancel_share";
        public static final String GOURMET_CANCEL_SHARE = "gourmet_cancel_share";
        public static final String OB_CANCEL_SHARE = "ob_cancel_share";
        public static final String STAY_OUTBOUND_CANCELED = "stay_outbound_canceled";

        public static final String WAYTOVISIT_OPEN = "waytovisit_open";
        public static final String WAYTOVISIT_CLOSE = "waytovisit_close";

        public static final String HOME_CLICK = "home_click";
        public static final String BOOKINGSTATUS_CLICK = "bookingstatus_click";
        public static final String MYDAILY_CLICK = "mydaily_click";
        public static final String MENU_CLICK = "menu_click";
        public static final String SEARCH_BUTTON_CLICK = "search_button_click";

        public static final String STAY_LIST_CLICK = "stay_list_click";
        public static final String GOURMET_LIST_CLICK = "gourmet_list_click";

        public static final String STAY_BACK_BUTTON_CLICK = "stay_back_button_click";
        public static final String STAY_ITEM_CLICK = "stay_item_click";
        public static final String STAY_DAILYCHOICE_CLICK_Y = "stay_dailychoice_click_y";
        public static final String STAY_DAILYCHOICE_CLICK_N = "stay_dailychoice_click_n";
        public static final String OB_STAY_DAILYCHOICE_CLICK_Y = "ob_stay_dailychoice_click_y";
        public static final String OB_STAY_DAILYCHOICE_CLICK_N = "ob_stay_dailychoice_click_n";

        public static final String GOURMET_BACK_BUTTON_CLICK = "gourmet_back_button_click";
        public static final String GOURMET_ITEM_CLICK = "gourmet_item_click";
        public static final String GOURMET_DAILYCHOICE_CLICK_Y = "gourmet_dailychoice_click_y";
        public static final String GOURMET_DAILYCHOICE_CLICK_N = "gourmet_dailychoice_click_n";
        public static final String WISHLIST_BACK_BUTTON_CLICK = "wishlist_back_button_click";
        public static final String WISHLIST_ITEM_DELETE = "wishlist_item_delete";
        public static final String RECENTVIEW_BACK_BUTTON_CLICK = "recentview_back_button_click";
        public static final String RECENTVIEW_ITEM_DELETE = "recentview_item_delete";

        public static final String HOME_EVENT_BANNER_CLICK = "home_event_banner_click";
        public static final String HOME_ALL_WISHLIST_CLICK = "home_all_wishlist_click";
        public static final String HOME_ALL_RECENTVIEW_CLICK = "home_all_recentview_click";
        public static final String HOME_RECOMMEND_LIST_CLICK = "home_recommend_list_click";
        public static final String HOME_WISHLIST_CLICK = "home_wishlist_click";
        public static final String HOME_RECENTVIEW_CLICK = "home_recentview_click";
        public static final String HOME_MESSAGE_OPEN = "home_message_open";
        public static final String HOME_BLOCK_SHOW = "home_block_show";
        public static final String MESSAGE_CLICK = "message_click";
        public static final String MESSAGE_CLOSE = "message_close";
        // home category
        public static final String HOME_SHORTCUT_CLICK = "home_shortcut_click";
        public static final String STAY_NEARBY_SEARCH = "stay_nearby_search";
        public static final String LOCATION_LIST_CLOSE = "location_list_close";
        public static final String NEARBY_DISTANCE_05 = "nearby_distance_05";
        public static final String NEARBY_DISTANCE_1 = "nearby_distance_1";
        public static final String NEARBY_DISTANCE_3 = "nearby_distance_3";
        public static final String NEARBY_DISTANCE_5 = "nearby_distance_5";
        public static final String NEARBY_DISTANCE_10 = "nearby_distance_10";

        public static final String GOURMET_MENU_DETAIL_CLICK = "gourmet_menu_detail_click";
        public static final String GOURMET_TICKET_RANK = "gourmet_ticket_rank";
        public static final String GOURMET_MENU_BACK_CLICK = "gourmet_menu_back_click";
        public static final String GOURMET_MENU_DETAIL_CLICK_PHOTO = "gourmet_menu_detail_click_photo";
        public static final String GOURMET_MENU_BOOKING_CLICK_PHOTO = "gourmet_menu_booking_click_photo";
        public static final String GOURMET_MENU_UNFOLD = "gourmet_menu_unfold";
        public static final String GOURMET_MENU_FOLD = "gourmet_menu_fold";
        public static final String GOURMET_MENU_FLICKING = "gourmet_menu_flicking";

        public static final String STAY_SORT = "stay_sort";
        public static final String SORT = "sort";
        public static final String FILTER = "filter";
        public static final String STAY_PERSON = "stay_person";
        public static final String STAY_BEDTYPE = "stay_bedtype";
        public static final String STAY_AMENITIES = "stay_amenities";
        public static final String STAY_ROOM_AMENITIES = "stay_room_amenities";
        public static final String STAY_NO_RESULT = "stay_no_result";
        public static final String GOURMET_SORT = "gourmet_sort";
        public static final String GOURMET_CATEGORY = "gourmet_category";
        public static final String GOURMET_TIME = "gourmet_time";
        public static final String GOURMET_AMENITIES = "gourmet_amenities";
        public static final String GOURMET_NO_RESULT = "gourmet_no_result";

        // AB Test
        public static final String HOME_MENU_BUTTON = "home_menu_button";
        public static final String RANKING = "ranking";

        // Review
        public static final String TRUE_REVIEW_CLICK = "true_review_click";
        public static final String TRUE_REVIEW_BACK_BUTTON_CLICK = "true_review_back_button_click";
        public static final String TRUE_REVIEW_POLICY_CLICK = "true_review_policy_click";

        public static final String DAILY_INFO_CLICK = "daily_info_click";
        public static final String NOTICE_CLICK = "notice_click";
        public static final String FNQ_CLICK = "fnq_click";
        public static final String TNC_CLICK = "tnc_click";
        public static final String INQUIRY_CLICK = "inquiry_click";
        public static final String DAILY_SNS_CLICK = "daily_sns_click";
        public static final String DAILY_LIFESTYLE_PROJECT_CLICK = "daily_lifestyle_project_click";
        public static final String FACEBOOK_CLICK = "facebook_click";
        public static final String INSTAGRAM_CLICK = "instagram_click";
        public static final String BLOG_CLICK = "blog_click";
        public static final String YOUTUBE_CLICK = "youtube_click";

        // Happy Talk
        public static final String CONTACT_DAILY_CONCIERGE = "contact_daily_concierge";
        public static final String HAPPYTALK_CLICK = "happytalk_click";
        public static final String LOGIN_HAPPYTALK = "login_happytalk";
        public static final String CLOSE_HAPPYTALK = "close_happytalk";
        public static final String HAPPYTALK_START = "happytalk_start";
        public static final String CALL_CLICK = "call_click";

        // Preview
        public static final String PEEK_POP_CLOSE = "peek_pop_close";
        public static final String PEEK_POP_RESERVATION = "peek_pop_reservation";
        public static final String PEEK_POP_ADD_WISHLIST = "peek_pop_add_wishlist";
        public static final String PEEK_POP_DELETE_WISHLIST = "peek_pop_delete_wishlist";
        public static final String PEEK_POP_SHARE_KAKAO = "peek_pop_share_kakao";
        public static final String PEEK_POP_NAVER_MAP = "peek_pop_naver_map";

        // true vr
        public static final String STAY_ITEM_CLICK_TRUE_VR = "stay_item_click_true_vr";
        public static final String TRUE_VR_CLICK = "true_vr_click";

        // boutique BM
        public static final String STAY_ITEM_CLICK_BOUTIQUE_AD = "stay_item_click_boutique_ad";

        // Stay OutBound
        public static final String SEARCHSCREEN_OUTBOUND = "SearchScreen_outbound";
        public static final String SEARCHRESULTVIEW_OUTBOUND = "SearchResultView_outbound";
        public static final String STAY_ITEM_CLICK_OUTBOUND = "stay_item_click_outbound";
        public static final String STARTPAYMENT_OUTBOUND = "StartPayment_outbound";
        public static final String ENDPAYMENT_OUTBOUND = "EndPayment_outbound";
        public static final String PAYMENTUSED_OUTBOUND = "PaymentUsed_outbound";
        public static final String KEYWORD_NOT_MATCH_OUTBOUND = "keyword_not_match_outbound";
        public static final String SEARCH_WINDOW_CLICK = "search_window_click";
        public static final String MEMBER_SELECT = "member_select";
        public static final String SEARCH_CANCEL = "search_cancel";
        public static final String SEARCH_HISTORY_DELETE = "search_history_delete";
        public static final String RECOMMEND_RESULT = "recommend_result";
        public static final String NO_RECOMMEND_RESULT = "no_recommend_result";

        //
        public static final String BOOKING_HISTORY_DELETE = "booking_history_delete";
        public static final String BOOKING_HISTORY_DELETE_TRY = "booking_history_delete_try";
        public static final String BOOKING_ITEM_DETAIL_CLICK = "booking_item_detail_click";
        public static final String WRITE_REVIEW = "write_review";
        public static final String MAP_CLICK = "map_click";
        public static final String AUTO_SEARCH = "auto_search";
        public static final String NEARBY = "nearby";
        public static final String RECENT = "recent";
        public static final String KEYWORD = "keyword";

        public static final String GOURMET_RECOMMEND = "gourmet_recommend";
        public static final String LIST_CLICK = "list_click";

        public static final String THIRD_PARTY_PROVIDER_CHECK = "third_party_provider_check";
        public static final String VIEW_HIDDEN_MENU = "view_hidden_menu";

        // campaign tag
        public static final String TAG_SEARCH = "tag_search";
        public static final String TAG_SEARCH_NOT_FOUND = "tag_search_not_found";

        // 예약내역
        public static final String NO_RESULT_MOVE = "no_result_move";
        public static final String RE_RESERVATION = "re_reservation";
        public static final String LEAVE_REVIEW = "leave_review";
        public static final String BUTTONS_AVAILABLE = "buttons_available";

        public static final String OB_KEYWORDS_IN_DOMESTIC = "ob_keywords_in_domestic";
        public static final String GOURMET_KEYWORDS_POPED_UP = "gourmet_keywords_poped_up";
        public static final String GOURMET_KEYWORDS_YES = "gourmet_keywords_yes";
        public static final String GOURMET_KEYWORDS_NO = "gourmet_keywords_no";

        public static final String COUPON_STAY = "coupon_stay";
        public static final String COUPON_GOURMET = "coupon_gourmet";
        public static final String TRUE_REVIEW_STAY = "true_review_stay";
        public static final String TRUE_REVIEW_GOURMET = "true_review_gourmet";
        public static final String WISH_STAY = "wish_stay";
        public static final String WISH_GOURMET = "wish_gourmet";
        public static final String WISH_OUTBOUND = "wish_outbound";
        public static final String DISCOUNT_STAY = "discount_stay";
        public static final String DISCOUNT_GOURMET = "discount_gourmet";

        public static final String CANCEL_HISTORY = "cancel_history";
        public static final String BACK = "back";
        public static final String NO_RESULT = "no_result";
        public static final String STAY_CLICK = "stay_click";
        public static final String GOURMET_CLICK = "gourmet_click";
        public static final String SHARE = "share";
        public static final String INQUIRY = "inquiry";
        public static final String SHOW_DETAIL = "show_detail";
        public static final String FIND_PATH = "find_path";
        public static final String DELETE_HISTORY = "delete_history";

        // Reward
        public static final String MORE_INFO = "more_info";
        public static final String REGISTER_LOGIN = "register_login";
        public static final String MY_REWARD_STATUS = "my_reward_status";
        public static final String THUMBNAIL_CLICK = "thumbnail_click";
        public static final String ROOM_SELECTION = "room_selection";
        public static final String ORDER_PROCEED = "order_proceed";
        public static final String ORDER_COMPLETE = "order_complete";
        public static final String DAILY_REWARD_CLICKED = "daily_reward_clicked";
        public static final String MYDAILY_REWARD_HISTORY = "mydaily_reward_history";
        public static final String MYDAILY_REWARD_INFO = "mydaily_reward_info";
        public static final String REWARD_HISTORY_SEE_RESERVATION = "reward_history_see_reservation";

        public static final String PRIVACY = "privacy";

        // MULTI
        public static final String BOOKINGCLICKED_ = "BookingClicked_";
        public static final String AVAILABLE_TIME = "available_time";
        public static final String SHORTCUT_ORDER = "shortcut_order";
        public static final String AVAILABLE_TIME_EXTRA = "available_time_extra";
        public static final String POPUP_DIFFERENT_SHOP = "popup_different_shop";
        public static final String POPUP_DIFFERENT_TIME = "popup_different_time";
        public static final String POPUP_DIFFERENT_DAY = "popup_different_day";

        // true awards
        public static final String DETAIL_PAGE = "detail_page";
        public static final String QUESTION_MARK = "question_mark";
        public static final String SEE_MORE = "see_more";
    }

    public static class Category
    {
        public static final String NAVIGATION_ = "Navigation";
        public static final String NAVIGATION = "navigation";
        public static final String HOTEL_BOOKINGS = "HotelBookings";
        public static final String GOURMET_BOOKINGS = "GourmetBookings";
        public static final String POPUP_BOXES = "PopupBoxes";
        public static final String HOTEL_SEARCH = "HotelSearches";
        public static final String GOURMET_SEARCH = "GourmetSearches";
        public static final String BOOKING_STATUS = "BookingStatus";
        public static final String COUPON_BOX = "CouponBox";
        public static final String INVITE_FRIEND = "InviteFriend";
        public static final String SEARCH_ = "Search";
        public static final String AUTO_SEARCH = "AutoSearch";
        public static final String AUTO_SEARCH_NOT_FOUND = "AutoSearchNotFound";
        public static final String CALL_BUTTON_CLICKED = "CallButtonClicked";
        public static final String SET_MY_BIRTHDAY = "SetMyBirthday";

        // Review
        public static final String HOTEL_SATISFACTIONEVALUATION = "HotelSatisfactionEvaluation";
        public static final String GOURMET_SATISFACTIONEVALUATION = "GourmetSatisfactionEvaluation";

        // Share
        public static final String SHARE = "share";
        public static final String BOOKING = "booking";
        public static final String SEARCH = "search";

        public static final String HOME_RECOMMEND = "home_recommend";

        public static final String SORT_FLITER = "sort_fliter";

        // AB Test
        public static final String EXPERIMENT = "experiment";

        public static final String CONTACT_DAILY_CONCIERGE = "contact_daily_concierge";

        // Stay OutBound
        public static final String AUTOSEARCH_OUTBOUND = "AutoSearch_outbound";
        public static final String AUTOSEARCHNOTFOUND_OUTBOUND = "AutoSearchNotFound_outbound";

        //
        public static final String AUTO_SEARCH_LIST = "auto_search_list";
        public static final String AUTO_SEARCH_LIST_NO_RESULT = "auto_search_list_no_result";
        public static final String AUTO_SEARCH_RESULT = "auto_search_result";
        public static final String KEYWORD_SEARCH_RESULT = "keyword_search_result";
        public static final String NEARBY_SEARCH_RESULT = "nearby_search_result";
        public static final String RECENT_SEARCH_RESULT = "recent_search_result";
        public static final String SOLDOUT_STAY_ITEM_CLICK = "soldout_stay_item_click";
        public static final String SOLDOUT_GOURMET_ITEM_CLICK = "soldout_gourmet_item_click";

        public static final String BOOKING_DETAIL = "booking_detail";
        public static final String BOOKING_GOURMET_RECOMMEND_LIST_CLICK = "booking_gourmet_recommend_list_click";
        public static final String BOOKING_GOURMET_RECOMMEND_CLICK = "booking_gourmet_recommend_click";

        public static final String STAY_THANK_YOU = "stay_thankyou";
        public static final String THANKYOU_GOURMET_RECOMMEND_LIST_CLICK = "thankyou_gourmet_recommend_list_click";
        public static final String THANKYOU_GOURMET_RECOMMEND_CLICK = "thankyou_gourmet_recommend_click";
        public static final String ORDER_COMPLETE = "order_complete";
        public static final String PRODUCT_RECOMMEND = "product_recommend";
        public static final String PRODUCT_RECOMMEND_PRODUCT_CLICK = "product_recommend_product_click";
        public static final String PRODUCT_RECOMMEND_PRODUCT = "product_recommend_product";

        public static final String OB_SEARCH_AUTO_SEARCH = "ob_search_auto_search";
        public static final String OB_SEARCH_NO_RESULT = "ob_search_no_result";
        public static final String OB_SEARCH_RESULT = "ob_search_result";
        public static final String OB_SEARCH_RECENT = "ob_search_recent";
        public static final String OB_SEARCH_RECOMMEND = "ob_search_recommend";
        public static final String OB_SEARCH_RECOMMEND_NO_RESULT = "ob_search_recommend_no_result";
        public static final String OB_SEARCH_ORIGIN_RECOMMEND = "ob_search_origin_recommend";
        public static final String OB_SEARCH_ORIGIN_AUTO = "ob_search_origin_auto";
        public static final String OB_SEARCH_ORIGIN_RECENT = "ob_search_origin_recent";
        public static final String OB_SEARCH_ORIGIN_ETC = "ob_search_origin_etc";

        public static final String VENDOR_SELECTION_ROOM_SELECTION = "vendor_selection_room_selection";
        public static final String VENDOR_SELECTION_ORDER_COMPLETION = "vendor_selection_order_completion";

        public static final String PRODUCT_LIST = "product_list";

        public static final String RESERVATION_CANCEL = "reservation_cancel";

        public static final String DETAIL_PAGE_TRUE_REVIEW = "detail_page_true_review";
        public static final String DETAIL_PAGE_COUPON = "detail_page_coupon";

        public static final String REWARD = "reward";
        public static final String REGISTRATION = "registration";
        public static final String DEVICE_INFO = "device_info";

        public static final String TRUE_AWARDS = "true_awards";

        public static final String MEMBER_LEAVE = "member_leave";
    }

    public static class Label
    {
        public static final String HOTEL = "hotel";
        public static final String STAY = "stay";
        public static final String GOURMET = "gourmet";
        public static final String BOUTIQUE = "boutique";
        public static final String PENSION = "pension";
        public static final String RESORT = "resort";
        public static final String NEAR_BY = "nearby";
        public static final String OUTBOUND = "outbound";

        public static final String HOTEL_SCREEN = "HotelScreen";
        public static final String GOURMET_SCREEN = "GoumetScreen";
        public static final String BOOKINGSTATUS_SCREEN = "BookingStatusScreen";
        public static final String MENU_SCREEN = "MenuScreen";
        //
        public static final String PAYMENT_CARD_EDIT = "PaymentCardEdit";
        public static final String PAYMENT_CARD_REGISTRATION = "PaymentCardRegistration";
        public static final String AGREE = "Agree";
        public static final String CANCEL_ = "Cancel";
        public static final String CANCEL = "cancel";
        public static final String OK = "Okay";
        public static final String FACEBOOK_LOGIN = "FacebookLogin";
        public static final String KAKAO_LOGIN = "KakaoLogin";
        public static final String EMAIL_LOGIN = "EmailLogin";
        public static final String REGISTER_ACCOUNT = "RegisterAccount";
        public static final String AGREE_AND_REGISTER = "AgreeAndRegister";
        public static final String ADDING_CARD_BUTTON_CLICKED = "AddingCardButtonClicked";
        //
        public static final String HOTEL_SATISFACTION = "HotelSatisfaction";
        public static final String HOTEL_DISSATISFACTION = "HotelDissatisfaction";
        public static final String HOTEL_CLOSE_BUTTON_CLICKED = "HotelCloseButtonClicked";
        public static final String GOURMET_SATISFACTION = "GourmetSatisfaction";
        public static final String GOURMET_DISSATISFACTION = "GourmetDissatisfaction";
        public static final String GOURMET_CLOSE_BUTTON_CLICKED = "GourmetCloseButtonClicked";
        public static final String OB_SATISFACTION = "ob_satisfaction";
        public static final String OB_DISSATISFACTION = "ob_dissatisfaction";
        public static final String OB_CLOSE_BUTTON_CLICKED = "ob_close_button_clicked";

        //
        //        public 7static final String MINUS_BUTTON_CLICKED = "MinusButtonClicked";
        //        public static final String PLUS_BUTTON_CLICKED = "PlusButtonClicked";
        public static final String RESET_BUTTON_CLICKED = "ResetButtonClicked";
        public static final String CLOSE_BUTTON_CLICKED = "CloseButtonClicked";
        //
        public static final String VIEW_BOOKING_STATUS_CLICKED = "ViewBookingStatusClicked";
        //
        public static final String SORTFILTER_DISTRICT = "District";
        public static final String SORTFILTER_DISTANCE = "Distance";
        public static final String SORTFILTER_LOWTOHIGHPRICE = "LowtoHighPrice";
        public static final String SORTFILTER_HIGHTOLOWPRICE = "HightoLowPrice";
        public static final String SORTFILTER_RATING = "Rating";
        public static final String SORTFILTER_DOUBLE = "Double";
        public static final String SORTFILTER_TWIN = "Twin";
        public static final String SORTFILTER_ONDOL = "Ondol";
        //
        public static final String SORTFILTER_NONE = "None";
        public static final String SORTFILTER_WIFI = "Wifi";
        public static final String SORTFILTER_FREE_BREAKFAST = "FreeBreakfast";
        public static final String SORTFILTER_KITCHEN = "Kitchen";
        public static final String SORTFILTER_BATHTUB = "Bathtub";
        public static final String SORTFILTER_PARKINGAVAILABLE = "ParkingAvailable";
        public static final String SORTFILTER_PARKINGDISABLE = "NoParking";
        public static final String SORTFILTER_PET = "Pet";
        public static final String SORTFILTER_BBQ = "BBQ";
        public static final String SORTFILTER_POOL = "Pool";
        public static final String SORTFILTER_FITNESS = "Fitness";
        public static final String SORTFILTER_VALET = "Valet";
        public static final String SORTFILTER_BABYSEAT = "BabySeat";
        public static final String SORTFILTER_PRIVATEROOM = "PrivateRoom";
        public static final String SORTFILTER_GROUP = "Group";
        public static final String SORTFILTER_CORKAGE = "Corkage";
        public static final String SORTFILTER_BUSINESS_CENTER = "business_center";
        public static final String SORTFILTER_SAUNA = "sauna";
        public static final String SORTFILTER_KIDS_PLAY_ROOM = "kids_playroom";
        public static final String SORTFILTER_PC = "pc";
        public static final String SORTFILTER_TV = "tv";
        public static final String SORTFILTER_SPA_WHIRLPOOL = "spa";
        public static final String SORTFILTER_PRIVATE_BBQ = "bbq_room";
        public static final String SORTFILTER_KARAOKE = "karaoke";
        public static final String SORTFILTER_PARTYROOM = "partyroom";
        //
        public static final String SORTFILTER_0611 = "0611";
        public static final String SORTFILTER_1115 = "1115";
        public static final String SORTFILTER_1517 = "1517";
        public static final String SORTFILTER_1721 = "1721";
        public static final String SORTFILTER_2106 = "2199";
        //
        public static final String VIEWTYPE_LIST = "List";
        public static final String VIEWTYPE_MAP = "Map";
        //
        public static final String LOGIN_CLICKED = "LoginClicked";
        public static final String CARD_MANAGEMENT_CLICKED = "CardManagementClicked";
        public static final String CREDIT_MANAGEMENT_CLICKED = "CreditManagementClicked";
        public static final String EVENT_CLICKED = "EventClicked";
        //
        public static final String _HOTEL_LIST = "HotelList"; // Old Ver 로 앞에 _ 삽입
        public static final String _HOTEL_MAP = "HotelMap"; // Old Ver 로 앞에 _ 삽입
        public static final String _GOURMET_LIST_ = "GourmetList"; // Old Ver 로 앞에 _ 삽입
        public static final String _GOURMET_MAP = "GourmetMap"; // Old Ver 로 앞에 _ 삽입
        public static final String HOTEL_LIST = "hotel_list"; // 신규 버전
        public static final String BOUTIQUE_LIST = "boutique_list"; // 신규 버전
        public static final String PENSION_LIST = "pension_list"; // 신규 버전
        public static final String RESORT_LIST = "resort_list"; // 신규 버전
        public static final String HOTEL_LIST_MAP = "hotel_list_map"; // 신규 버전
        public static final String BOUTIQUE_LIST_MAP = "boutique_list_map"; // 신규 버전
        public static final String PENSION_LIST_MAP = "pension_list_map"; // 신규 버전
        public static final String RESORT_LIST_MAP = "resort_list_map"; // 신규 버전
        //
        public static final String TERMSOF_LOCATION = "TermsofLocation";
        public static final String AGREE_AND_SEARCH = "AgreeAndSearch";
        public static final String DELETE_ALL_KEYWORDS = "DeleteAllKeywords";
        //
        public static final String CREDIT_MANAGEMENT = "CreditManagement";
        public static final String INVITE_FRIENDS = "InviteFriends";
        public static final String ON = "On";
        public static final String OFF = "Off";
        public static final String REFERRAL_CODE_COPIED = "ReferralCodeCopied";
        public static final String HOTEL_USING_COUPON_CLICKED = "HotelUsingCouponClicked";
        public static final String HOTEL_USING_COUPON_CANCEL_CLICKED = "HotelUsingCouponCancelClicked";
        public static final String HOTEL_USING_COUPON_CANCEL = "HotelUsingCouponCancel";
        public static final String GOURMET_USING_COUPON_CLICKED = "GourmetUsingCouponClicked";
        public static final String GOURMET_USING_COUPON_CANCEL = "GourmetUsingCouponCancel";
        public static final String GOURMET_USING_COUPON_CANCEL_CLICKED = "GourmetUsingCouponCancelClicked";
        public static final String COUPON_BOX_CLICKED = "CouponBoxClicked";
        //
        public static final String SWITCHING_HOTEL = "SwitchingHotel";
        public static final String SWITCHING_GOURMET = "SwitchingGourmet";
        public static final String CALL = "Call";
        public static final String SEARCH_AGAIN = "SearchAgain";
        public static final String BACK_BUTTON = "BackButton";
        public static final String CLOSED = "Closed";
        public static final String LOGIN_ = "Login";
        public static final String LOGIN = "login";
        public static final String CHANGE_LOCATION = "changelocation";
        public static final String EVENT = "event";
        public static final String SEARCH_RESULT_VIEW = "SearchResultView";
        //
        public static final String FULL_PAYMENT = "FullPayment";
        public static final String PAYMENTWITH_COUPON = "PaymentwithCoupon";
        public static final String PAYMENTWITH_CREDIT = "PaymentwithCredit";
        //
        public static final String CUSTOMER_CENTER_CALL = "CustomerCenterCall";
        public static final String KAKAO = "Kakao";
        public static final String DIRECT_CALL = "DirectCall";
        public static final String CLICK = "Click";
        //
        public static final String MENU_REGISTER_ACCOUNT = "Menu_RegisterAccount";
        public static final String SIGNUP = "signup";
        public static final String SIGNUP_ON = "Signup_On";
        public static final String SIGNUP_OFF = "Signup_Off";

        public static final String PROFILE_EDITED = "ProfileEdited";
        public static final String TRY = "Try";
        public static final String SUCCESS = "Success";

        public static final String REVIEW_WRITE_CLICKED = "ReviewWriteClicked";
        public static final String SUBMIT = "Submit";
        public static final String _YES = "Yes";
        public static final String _NO = "No";
        public static final String YES = "yes";
        public static final String NO = "no";
        public static final String BACK = "Back";
        public static final String CONFIRM = "Confirm";
        public static final String CLOSE_ = "Close";
        public static final String CLOSE = "close";
        public static final String SWIPE = "Swipe";
        public static final String WALK = "Walk";
        public static final String CAR = "Car";
        public static final String PARKING_NOT_AVAILABLE = "ParkingNotAvailable";

        public static final String DIRECTCALL_FRONT = "DirectCall_Front";
        public static final String DIRECTCALL_RESERVATION = "DirectCall_Reservation";

        public static final String HOME = "home";
        public static final String BOOKINGSTATUS = "bookingstatus";
        public static final String MYDAILY = " mydaily";
        public static final String MENU = "menu";
        public static final String STAY_LIST = "stay_list";
        public static final String STAY_LOCATION_LIST = "stay_location_list";
        public static final String STAY_MAP_VIEW = "stay_map_view";
        public static final String GOURMET_LIST = "gourmet_list";
        public static final String GOURMET_LOCATION_LIST = "gourmet_location_list";
        public static final String GOURMET_MAP_VIEW = "gourmet_map_view";
        public static final String HOTEL_LOCATION_LIST = "hotel_location_list";
        public static final String BOUTIQUE_LOCATION_LIST = "boutique_location_list";
        public static final String PENSION_LOCATION_LIST = "pension_location_list";
        public static final String RESORT_LOCATION_LIST = "resort_location_list";

        public static final String WISHLIST = "wishlist";
        public static final String RECENTVIEW = "recentview";
        public static final String WISHLIST_RECENTVIEW = "wishlist_recentview";
        public static final String NONE = "none";

        public static final String MENU_LIST = "menu_list";
        public static final String MENU_DETAIL = "menu_detail";

        public static final String STAY_DETAIL_VIEW = "stay_detail_view";
        public static final String STAY_THANKYOU = "stay_thankyou";

        // AB Test
        public static final String CTA_VARIATION_A = "cta_variation_a";

        public static final String STAY_DETAIL = "stay_detail";
        public static final String STAY_BOOKING_INITIALISE = "stay_booking_initialise";
        public static final String STAY_DEPOSIT_WAITING = "stay_deposit_waiting";
        public static final String STAY_BOOKING_DETAIL = "stay_booking_detail";


        public static final String GOURMET_DETAIL = "gourmet_detail";
        public static final String GOURMET_BOOKING_INITIALISE = "gourmet_booking_initialise";
        public static final String GOURMET_DEPOSIT_WAITING = "gourmet_deposit_waiting";
        public static final String GOURMET_BOOKING_DETAIL = "gourmet_booking_detail";
        public static final String MENU_FNQ = "menu_fnq";
        public static final String MENU_INQUIRY = "menu_inquiry";
        public static final String BACKKEY = "backkey";
        public static final String STAY_BOOKING_DETAIL_REFUND = "stay_booking_detail_refund";

        // Stay OutBound
        public static final String OUTBOUND_CLICK = "outbound_click";
        public static final String EASYCARDPAY = "EasyCardPay";
        public static final String CARDPAY = "CardPay";
        public static final String PHONEBILLPAY = "PhoneBillPay";
        public static final String FULLBONUS = "FullBonus";
        public static final String VIRTUALACCOUNTPAY = "VirtualAccountPay";

        // Booking List
        public static final String POST_VISIT = "post_visit";
        public static final String TEMPORARY_ACCOUNT = "temporary_account";
        public static final String COMPLETE_PAYMENT = "complete_payment";
        public static final String CANCELLEATION_PROGRESS = "cancelleation_progress";

        public static final String Y = "y";
        public static final String N = "n";

        public static final String RESERVATION = "reservation";
        public static final String REVIEW = "review";
        public static final String BOTH = "both";

        public static final String POPED_UP = "poped_up";

        public static final String MULTI = "Multi";
        public static final String SINGLE = "Single";
        public static final String FULL_TIME = "전체시간";
        public static final String SELECT_TIME = "특정시간";

        public static final String CLICKED = "clicked";
        public static final String KEEP_EXISTED = "keep_existed";
        public static final String ADD_NEW = "add_new";

        public static final String OB = "ob";
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
        public static final String VALUE = "value";
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
        public static final String GRADE = "grade";
        public static final String DBENEFIT = "dBenefit";
        public static final String CATEGORY = "category";
        public static final String ADDRESS = "address";
        @Deprecated
        public static final String HOTEL_CATEGORY = "hotelCategory";
        public static final String NRD = "nrd";
        public static final String PROVINCE = "province ";
        public static final String DISTRICT = "district ";
        public static final String AREA = "area ";
        public static final String NUM_OF_BOOKING = "num_of_booking";
        public static final String EVENT_NAME = "event_name";
        public static final String EVENT_IDX = "event_idx";
        public static final String KEYWORD = "keyword";
        public static final String NUM_OF_SEARCH_RESULTS_RETURNED = "num_of_search_results_returned";
        public static final String USER_IDX = "user_idx";
        public static final String COUNTRY = "country";
        public static final String APP_VERSION = "app_version";
        public static final String CARD_ISSUING_COMPANY = "card_issuing_company";
        public static final String VIEWED_DATE = "viewed_date";
        public static final String CHECK_IN_DATE = "check_in_date";
        public static final String CHECK_OUT_DATE = "check_out_date";
        public static final String LENGTH_OF_STAY = "length_of_stay";
        public static final String VISIT_DATE = "visit_date";
        public static final String STAY_CATEGORY = "stay_category";
        public static final String STAY_NAME = "stay_name";
        public static final String UNIT_PRICE = "unit_price";
        public static final String GOURMET_CATEGORY = "gourmet_category";
        public static final String RESTAURANT_NAME = "restaurant_name";
        public static final String PRICE_OF_SELECTED_ROOM = "price_of_selected_room";
        public static final String BOOKING_INITIALISED_DATE = "booking_initialised_date";
        public static final String PRICE_OF_SELECTED_TICKET = "price_of_selected_ticket";
        public static final String REVENUE = "revenue";
        public static final String USED_CREDITS = "used_credits";
        public static final String PURCHASED_DATE = "purchased_date";
        public static final String VISIT_HOUR = "visit_hour";
        public static final String NUM_OF_TICKETS = "num_of_tickets";
        public static final String TYPE_OF_REGISTRATION = "type_of_registration";
        public static final String REGISTRATION_DATE = "registration_date";
        public static final String REFERRAL_CODE = "referral_code";
        public static final String POPUP_STATUS = "popup_status";
        public static final String SELECTED_RESPONSE_ITEM = "selected_response_Item";
        public static final String SCREEN = "screen";
        public static final String SORTING = "sorting";
        public static final String COUPON_REDEEM = "coupon_redeem";
        public static final String COUPON_NAME = "coupon_name";
        public static final String COUPON_AVAILABLE_ITEM = "coupon_available_item";
        public static final String PRICE_OFF = "price_off";
        public static final String EXPIRATION_DATE = "expiration_date";
        public static final String DOWNLOAD_DATE = "download_date";
        public static final String DOWNLOAD_FROM = "download_from";
        public static final String COUPON_CODE = "coupon_code";
        public static final String IS_SIGNED = "is_signed";
        public static final String PLACE_TYPE = "place_type";
        public static final String PLACE_HIT_TYPE = "place_hit_type";
        public static final String PLACE_COUNT = "place_count";
        public static final String RATING = "rating";
        public static final String IS_SHOW_ORIGINAL_PRICE = "isShowOriginalPrice";
        public static final String LIST_INDEX = "list_index";
        public static final String REGISTERED_SIMPLE_CARD = "registeredSimpleCard";
        public static final String FIRST_PURCHASE = "first_purchase"; // 첫 결제 여부
        public static final String STATUS_CODE = "status_code";
        public static final String SATISFACTION_SURVEY = "satisfaction_survey";
        public static final String FILTER = "filter";
        public static final String DAILYCHOICE = "dailychoice";
        public static final String SEARCH_WORD = "search_word";
        public static final String SEARCH_PATH = "search_path";
        public static final String SEARCH_COUNT = "search_count";
        public static final String SEARCH_RESULT = "search_result";
        public static final String FILL_DATE_OF_BIRTH = "fill_date_of_birth";
        public static final String REASON_CANCELLATION = "reason_cancellation";
        public static final String LIST_TOP5_PLACE_INDEXES = "list_top5_place_indexes";
        public static final String KIND_OF_COUPON = "kind_of_coupon";
        public static final String SHARE_METHOD = "share_method";
        public static final String USER_TYPE = "user_type";
        public static final String MEMBER_TYPE = "member_type";
        public static final String PUSH_NOTIFICATION = "push_notification";
        public static final String VENDOR_ID = "vendor_id";
        public static final String VENDOR_NAME = "vendor_name";
        public static final String SERVICE = "service";
        public static final String HOME_SCREEN = "home_screen";
        public static final String RECOMMEND_IDX = "recommend_idx";
        public static final String RECOMMEND_ITEM_IDX = "recommend_item_idx";
        public static final String VIEW_TYPE = "view_type";
        public static final String TAG = "tag";
        public static final String AGGREGATION_ID = "aggregation_id";
        public static final String LABEL = "label";
        public static final String CANCEL_TYPE = "cancel_type";
    }

    public static class ValueType
    {
        public static final String EMPTY = "null";
        public static final String LIST = "list";
        public static final String MAP = "map";
        public static final String SEARCH = "search";
        public static final String SEARCH_RESULT = "searchResult";
        public static final String CHANGED = "Changed";
        public static final String NONE_ = "None";
        public static final String NONE = "none";
        public static final String MEMBER = "member";
        public static final String GUEST = "guest";
        public static final String DETAIL = "detailview";
        @Deprecated
        public static final String HOTEL = "hotel";
        public static final String STAY = "stay";
        public static final String GOURMET = "gourmet";
        public static final String EVENT = "event";
        public static final String CHANGE_LOCATION = "changelocation";
        public static final String ALL_LOCALE_KR = "전체";
        public static final String LAUNCH = "launch";
        public static final String OTHER = "other";
        public static final String SATISFIED = "satisfied";
        public static final String DISSATISFIED = "dissatisfied";
        public static final String AROUND = "around";
        public static final String AUTO = "auto";
        public static final String RECENT = "recent";
        public static final String DIRECT = "direct";
        public static final String ALL = "all";
        public static final String KAKAO = "kakao";
        public static final String MESSAGE = "message";
        public static final String OVERSEAS = "overseas";
        public static final String OUTBOUND = "outbound";
        public static final String DOMESTIC = "domestic";
        public static final String PENSION = "pension";
        public static final String LINK_COPY = "link_copy";
        public static final String ETC = "etc";
        public static final String FREE_CANCEL = "free_cancel";
        public static final String CANCEL_FEE = "cancel_fee";
    }
}
