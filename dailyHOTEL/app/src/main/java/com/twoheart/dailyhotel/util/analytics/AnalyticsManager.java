package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsManager
{
    private static final String TAG = "[AnalyticsManager]";

    // 추후에 작업을 해볼까 생각중
    private static final boolean ENABLED_GOOGLE = true;
    private static final boolean ENABLED_FACEBOOK = true;
    private static final boolean ENABLED_TUNE = true;
    private static final boolean ENABLED_APPBOY = true;

    private static AnalyticsManager mInstance = null;
    private Context mContext;
    private GoogleAnalyticsManager mGoogleAnalyticsManager;
    private TuneManager mTuneManager;
    private FacebookManager mFacebookManager;
    private AppboyManager mAppboyManager;
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

        AdWordsConversionReporter.reportWithConversionId(context, "972698918", "swVfCLnEnWYQpurozwM", "0.00", false);
    }

    private void initAnalytics(Context context)
    {
        mContext = context;

        try
        {
            mTuneManager = new TuneManager(context);

            mGoogleAnalyticsManager = new GoogleAnalyticsManager(context, new GoogleAnalyticsManager.OnClientIdListener()
            {
                @Override
                public void onResponseClientId(String clientId)
                {
                    mTuneManager.setGoogleClientId(clientId);
                }
            });

            mFacebookManager = new FacebookManager(context);
            mAppboyManager = new AppboyManager(context);

            mAnalyticsManagerList.add(mGoogleAnalyticsManager);
            mAnalyticsManagerList.add(mTuneManager);
            mAnalyticsManagerList.add(mFacebookManager);
            mAnalyticsManagerList.add(mAppboyManager);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public GoogleAnalyticsManager getGoogleAnalyticsManager()
    {
        return mGoogleAnalyticsManager;
    }

    public void setUserIndex(String index)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                analyticsManager.setUserIndex(index);
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

    public void onStart(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onStart(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onStop(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onStop(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onResume(Activity activity)
    {

        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onResume(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void onPause(Activity activity)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.onPause(activity);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordScreen(String screen)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordScreen(screen);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void recordScreen(String screen, Map<String, String> params)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.recordScreen(screen, params);
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

    public void signUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.signUpSocialUser(userIndex, email, name, gender, phoneNumber, userType);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType, String recommender)
    {
        for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
        {
            try
            {
                analyticsManager.signUpDailyUser(userIndex, email, name, phoneNumber, userType, recommender);
            } catch (Exception e)
            {
                ExLog.d(TAG + e.toString());
            }
        }
    }

    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                try
                {
                    analyticsManager.purchaseCompleteHotel(transId, params);
                } catch (Exception e)
                {
                    ExLog.d(TAG + e.toString());
                }
            }

            String price = params.get(AnalyticsManager.KeyType.TOTAL_PRICE);
            AdWordsConversionReporter.reportWithConversionId(mContext, "972698918", "2uFUCJrApWYQpurozwM", price, true);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        try
        {
            for (BaseAnalyticsManager analyticsManager : mAnalyticsManagerList)
            {
                try
                {
                    analyticsManager.purchaseCompleteGourmet(transId, params);
                } catch (Exception e)
                {
                    ExLog.d(TAG + e.toString());
                }
            }

            String price = params.get(AnalyticsManager.KeyType.TOTAL_PRICE);
            AdWordsConversionReporter.reportWithConversionId(mContext, "972698918", "KVTICNS-pWYQpurozwM", price, true);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
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
        public static final String DAILYHOTEL_LIST_REGION_GLOBAL = "DailyHotel_HotelGlobalLocationList";
        public static final String DAILYHOTEL_LIST_CALENDAR = "DailyHotel_HotelBookingWindow";
        public static final String DAILYHOTEL_BANNER_DETAIL = "DailyHotel_EventBannerDetailView";
        public static final String DAILYHOTEL_CURATION = "DailyHotel_SortFilterSelectView";
        //
        public static final String DAILYHOTEL_DETAIL = "DailyHotel_HotelDetailView";
        public static final String DAILYHOTEL_DETAIL_ROOMTYPE = " DailyHotel_HotelRoomTypeList";
        public static final String DAILYHOTEL_DETAIL_MAP = "DailyHotel_HotelDetailMapView";
        //
        public static final String DAILYHOTEL_PAYMENT = "DailyHotel_BookingInitialise";
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
        public static final String DAILYGOURMET_BANNER_DETAIL = "DailyGourmet_EventBannerDetailView";
        public static final String DAILYGOURMET_CURATION = "DailyGourmet_SortFilterSelectView";
        //
        public static final String DAILYGOURMET_DETAIL = "DailyGourmet_GourmetDetailView";
        public static final String DAILYGOURMET_DETAIL_TICKETTYPE = " DailyGourmet_GourmetMenuTypeList";
        public static final String DAILYGOURMET_DETAIL_MAP = "DailyGourmet_GourmetDetailMapView";
        //
        public static final String DAILYGOURMET_PAYMENT = "DailyGourmet_BookingInitialise";
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
        public static final String BOOKING_DETAIL = "BookingDetail_MyBookingInfo";
        public static final String BOOKING_DETAIL_INFORMATION = "BookingDetail_PlaceInfo";
        public static final String BOOKING_DETAIL_MAP = "BookingDetail_MapView";
        public static final String BOOKING_DETAIL_RECEIPT = "BookingDetail_Receipt";
        //
        public static final String INFORMATION_SIGNIN = "Menu_AfterLogin";
        public static final String INFORMATION_SIGNOUT = "Menu_BeforeLogin";
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
        public static final String BONUS_LIST = "Menu_CreditHistoryList";
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
        //
        public static final String DAILYHOTEL_DEPOSITWAITING = "DailyHotel_DepositWaiting";
        public static final String DAILYGOURMET_DEPOSITWAITING = "DailyGourmet_DepositWaiting";

        public static final String SEARCH_MAIN = "SearchScreenView";
        public static final String SEARCH_RESULT = "SearchResultView";
        public static final String SEARCH_RESULT_EMPTY = "SearchResultView_Empty";
    }

    public static class Action
    {
        public static final String DAILY_HOTEL_CLICKED = "DailyHotelClicked";
        public static final String HOTEL_LOCATIONS_CLICKED = "HotelLocationsClicked";
        public static final String HOTEL_EVENT_BANNER_CLICKED = "HotelEventBannerClicked";
        public static final String HOTEL_ITEM_CLICKED = "HotelItemClicked";
        public static final String HOTEL_CATEGORY_CLICKED = "DailyHotelCategoryClicked";
        public static final String HOTEL_SORT_FILTER_BUTTON_CLICKED = "HotelSortFilterButtonClicked";
        public static final String HOTEL_SORT_FILTER_BUTTON_UNCLICKED = "HotelSortFilterButtonUnClicked";
        public static final String HOTEL_SORT_FILTER_APPLY_BUTTON_CLICKED = "HotelSortFilterApplyButtonClicked";
        public static final String HOTEL_DETAIL_MAP_CLICKED = "HotelDetailMapClicked";
        public static final String HOTEL_DETAIL_ADDRESS_COPY_CLICKED = "HotelDetailAddressCopyClicked";
        public static final String HOTEL_DETAIL_NAVIGATION_APP_CLICKED = "HotelDetailNavigationAppClicked";
        public static final String HOTEL_BOOKING_DATE_CLICKED = "HotelBookingDateClicked";
        //
        public static final String SOCIAL_SHARE_CLICKED = "SocialShareClicked";
        public static final String KAKAO_INQUIRY_CLICKED = "KakaoInquiryClicked";
        public static final String ROOM_TYPE_CLICKED = "RoomTypeClicked";
        public static final String ROOM_TYPE_ITEM_CLICKED = "RoomTypeItemClicked";
        public static final String ROOM_TYPE_CANCEL_CLICKED = "RoomTypeCancelClicked";
        public static final String BOOKING_CLICKED = "BookingClicked";
        public static final String USING_CREDIT_CLICKED = "UsingCreditClicked";
        public static final String USING_CREDIT_CANCEL_CLICKED = "UsingCreditCancelClicked";
        public static final String PAYMENT_TYPE_ITEM_CLICKED = "PaymentTypeItemClicked";
        public static final String EDIT_BUTTON_CLICKED = "EditButtonClicked";
        public static final String PAYMENT_CLICKED = "PaymentClicked";
        public static final String PAYMENT_AGREEMENT_POPPEDUP = "PaymentAgreementPoppedup";
        public static final String HOTEL_PAYMENT_COMPLETED = "HotelPaymentCompleted";
        public static final String DAILY_GOURMET_CLICKED = "DailyGourmetClicked";
        public static final String GOURMET_LOCATIONS_CLICKED = "GourmetLocationsClicked";
        public static final String GOURMET_EVENT_BANNER_CLICKED = "GourmetEventBannerClicked";
        public static final String GOURMET_ITEM_CLICKED = "GourmetItemClicked";
        public static final String GOURMET_SORT_FILTER_BUTTON_CLICKED = "GourmetSortFilterButtonClicked";
        public static final String GOURMET_SORT_FILTER_BUTTON_UNCLICKED = "GourmetSortFilterButtonUnClicked";
        public static final String GOURMET_SORT_FILTER_APPLY_BUTTON_CLICKED = "GourmetSortFilterApplyButtonClicked";
        public static final String GOURMET_DETAIL_MAP_CLICKED = "GourmetDetailMapClicked";
        public static final String GOURMET_DETAIL_ADDRESS_COPY_CLICKED = "GourmetDetailAddressCopyClicked";
        public static final String GOURMET_DETAIL_NAVIGATION_APP_CLICKED = "GourmetDetailNavigationAppClicked";
        public static final String GOURMET_BOOKING_DATE_CLICKED = "GourmetBookingDateClicked";
        //
        public static final String TICKET_TYPE_CLICKED = "TicketTypeClicked";
        public static final String TICKET_TYPE_ITEM_CLICKED = "TicketTypeItemClicked";
        public static final String TICKET_TYPE_CANCEL_CLICKED = "TicketTypeCancelClicked";
        public static final String GOURMET_PAYMENT_COMPLETED = "GourmetPaymentCompleted";
        public static final String BOOKING_STATUS_CLICKED = "BookingStatusClicked";
        public static final String MENU_CLICKED = "MenuClicked";
        public static final String LOGIN_CLICKED = "LoginClicked";
        public static final String REGISTRATION_CLICKED = "RegistrationClicked";
        public static final String CARD_MANAGEMENT_CLICKED = "CardManagementClicked";
        public static final String REGISTERED_CARD_DELETE_POPPEDUP = "RegisteredCardDeletePoppedup";
        public static final String CREDIT_MANAGEMENT_CLICKED = "CreditManagementClicked";
        public static final String INVITE_FRIEND_CLICKED = "InviteFriendClicked";
        public static final String EVENT_CLICKED = "EventClicked";
        //
        public static final String SATISFACTION_EVALUATION_POPPEDUP = "SatisfactionEvaluationPoppedup";
        public static final String HOTEL_DISSATISFACTION_DETAILED_POPPEDUP = "HotelDissatisfactionDetailedPoppedup";
        public static final String GOURMET_DISSATISFACTION_DETAILED_POPPEDUP = "GourmetDissatisfactionDetailedPoppedup";
        //
        public static final String THANKYOU_SCREEN_BUTTON_CLICKED = "ThankyouScreenButtonClicked";
        //
        public static final String HOTEL_KEYWORD_SEARCH_CLICKED = "HotelKeywordSearchClicked"; // 앱보이 사용
        public static final String HOTEL_SEARCH_BACK_BUTTON_CLICKED = "HotelSearchBackButtonClicked";
        public static final String HOTEL_KEYWORD_RESET_CLICKED = "HotelKeywordResetClicked";
        public static final String HOTEL_AROUND_SEARCH_CLICKED = "HotelAroundSearchClicked";
        public static final String HOTEL_KEYWORD_HISTORY_DELETED = "HotelKeywordsHistoryDeleted";
        public static final String HOTEL_RECENT_KEYWORD_SEARCH_CLICKED = "HotelRecentKeywordSearchClicked";
        public static final String HOTEL_AUTOCOMPLETED_KEYWORD_CLICKED = "HotelAutoCompletedKeywordClicked";
        public static final String HOTEL_AUTOCOMPLETED_KEYWORD_NOTMATCHED = "HotelAutoCompletedKeywordNotMatched";
        public static final String HOTEL_AROUND_SEARCH_NOT_FOUND = "HotelAroundSearchNotFound";
        public static final String HOTEL_KEYWORD_SEARCH_NOT_FOUND = "HotelKeywordSearchNotFound";
        public static final String HOTEL_AUTOCOMPLETE_KEYWORD_NOT_FOUND = "HotelAutoCompletedKeywordNotFound";
        public static final String HOTEL_RECENT_KEYWORD_NOT_FOUND = "HotelRecentKeywordSearchNotFound";
        public static final String HOTEL_SEARCH_AGAIN_CLICKED = "HotelSearchAgainClicked";
        public static final String HOTEL_SEARCH_RESULT_CANCELED = "HotelSearchResultCanceled";
        //
        public static final String GOURMET_KEYWORD_SEARCH_CLICKED = "GourmetKeywordSearchClicked";
        public static final String GOURMET_SEARCH_BACK_BUTTON_CLICKED = "GourmetSearchBackButtonClicked";
        public static final String GOURMET_KEYWORD_RESET_CLICKED = "GourmetKeywordResetClicked";
        public static final String GOURMET_AROUND_SEARCH_CLICKED = "GourmetAroundSearchClicked";
        public static final String GOURMET_KEYWORD_HISTORY_DELETED = "GourmetKeywordsHistoryDeleted";
        public static final String GOURMET_RECENT_KEYWORD_SEARCH_CLICKED = "GourmetRecentKeywordSearchClicked";
        public static final String GOURMET_AUTOCOMPLETED_KEYWORD_CLICKED = "GourmetAutoCompletedKeywordClicked";
        public static final String GOURMET_AUTOCOMPLETED_KEYWORD_NOTMATCHED = "GourmetAutoCompletedKeywordNotMatched";
        public static final String GOURMET_AROUND_SEARCH_NOT_FOUND = "GourmetAroundSearchNotFound";
        public static final String GOURMET_KEYWORD_SEARCH_NOT_FOUND = "GourmetKeywordSearchNotFound";
        public static final String GOURMET_AUTOCOMPLETE_KEYWORD_NOT_FOUND = "GourmetAutoCompletedKeywordNotFound";
        public static final String GOURMET_RECENT_KEYWORD_NOT_FOUND = "GourmetRecentKeywordSearchNotFound";
        public static final String GOURMET_SEARCH_AGAIN_CLICKED = "GourmetSearchAgainClicked";
        public static final String GOURMET_SEARCH_RESULT_CANCELED = "GourmetSearchResultCanceled";
        //
        public static final String LOCATION_AGREEMENT_POPPEDUP = "LocationAgreementPoppedup";
        public static final String CALL_INQUIRY_CLICKED = "CallInquiryClicked";
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
        public static final String KEYWORD = "Keyword";
        public static final String SEARCHSCREEN = "SearchScreen";


    }

    public static class Category
    {
        public static final String NAVIGATION = "Navigation";
        public static final String HOTEL_BOOKINGS = "HotelBookings";
        public static final String GOURMET_BOOKINGS = "GourmetBookings";
        public static final String POPUP_BOXES = "PopupBoxes";
        public static final String HOTEL_SEARCH = "HotelSearches";
        public static final String GOURMET_SEARCH = "GourmetSearches";
        public static final String BOOKING_STATUS = "BookingStatus";
        public static final String COUPON_BOX = "CouponBox";
        public static final String INVITE_FRIEND = "InviteFriend";
        public static final String SEARCH = "Search";
    }

    public static class Label
    {
        public static final String HOTEL = "hotel";
        public static final String GOURMET = "gourmet";
        //
        public static final String HOTEL_SCREEN = "HotelScreen";
        public static final String GOURMET_SCREEN = "GoumetScreen";
        public static final String BOOKINGSTATUS_SCREEN = "BookingStatusScreen";
        public static final String MENU_SCREEN = "MenuScreen";
        //
        public static final String PAYMENT_CARD_EDIT = "PaymentCardEdit";
        public static final String AGREE = "Agree";
        public static final String CANCEL = "Cancel";
        public static final String OK = "Okay";
        public static final String AUTO_LOGIN_ON = "AutoLoginOn";
        public static final String AUTO_LOGIN_OFF = "AutoLoginOff";
        public static final String FACEBOOK_LOGIN = "FacebookLogin";
        public static final String KAKAO_LOGIN = "KakaoLogin";
        public static final String EMAIL_LOGIN = "EmailLogin";
        public static final String REGISTER_ACCOUNT = "RegisterAccount";
        public static final String AGREE_AND_REGISTER = "AgreeAndRegister";
        public static final String ADDING_CARD_ICON_CLICKED = "AddingCardIconClicked";
        public static final String ADDING_CARD_BUTTON_CLICKED = "AddingCardButtonClicked";
        //
        public static final String HOTEL_SATISFACTION = "HotelSatisfaction";
        public static final String HOTEL_DISSATISFACTION = "HotelDissatisfaction";
        public static final String HOTEL_CLOSE_BUTTON_CLICKED = "HotelCloseButtonClicked";
        public static final String GOURMET_SATISFACTION = "GourmetSatisfaction";
        public static final String GOURMET_DISSATISFACTION = "GourmetDissatisfaction";
        public static final String GOURMET_CLOSE_BUTTON_CLICKED = "GourmetCloseButtonClicked";
        //
        public static final String MINUS_BUTTON_CLICKED = "MinusButtonClicked";
        public static final String PLUS_BUTTON_CLICKED = "PlusButtonClicked";
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
        public static final String SORTFILTER_FREEBREAKFAST = "FreeBreakfast";
        public static final String SORTFILTER_KITCHEN = "Kitchen";
        public static final String SORTFILTER_BATHTUB = "Bathtub";
        public static final String SORTFILTER_PARKINGAVAILABEL = "ParkingAvailable";
        public static final String SORTFILTER_POOL = "Pool";
        public static final String SORTFILTER_FITNESS = "Fitness";
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
        public static final String HOTEL_LIST = "HotelList";
        public static final String HOTEL_LOCATION_LIST = "HotelLocationList";
        public static final String HOTEL_MAP = "HotelMap";
        public static final String GOURMET_LIST = "GourmetList";
        public static final String GOURMET_LOCATION_LIST = "GourmetLocationList";
        public static final String GOURMET_MAP = "GourmetMap";
        //
        public static final String KEYWORD_BACK_BUTTON_CLICKED = "KeywordBackButtonClicked";
        public static final String RESULT_BACK_BUTTON_CLICKED = "ResultBackButtonClicked";
        public static final String SEARCH_RESULT_CANCELED = "SearchResultCanceled";
        public static final String SEARCH_KEYWORD_RESET = "SearchKeywordReset";
        public static final String TERMSOF_LOCATION = "TermsofLocation";
        public static final String AGREE_AND_SEARCH = "AgreeAndSearch";
        public static final String DELETE_ALL_KEYWORDS = "DeleteAllKeywords";
        public static final String HOTEL_SEARCH_AGAIN_CLICKED = "HotelSearchAgainClicked";
        public static final String GOURMET_SEARCH_AGAIN_CLICKED = "GourmetSearchAgainClicked";
        public static final String CALL_KEYWORD_HOTEL = "Call-KeywordHotel";
        public static final String CALL_KEYWORD_GOURMET = "Call-KeywordGourmet";
        //
        public static final String CREDIT_MANAGEMENT = "CreditManagement";
        public static final String INVITE_FRIENDS = "InviteFriends";
        public static final String ON = "On";
        public static final String OFF = "Off";
        public static final String REFERRAL_CODE_COPIED = "ReferralCodeCopied";
        public static final String HOTEL_USING_COUPON_CLICKED = "HotelUsingCouponClicked";
        public static final String HOTEL_USING_COUPON_CANCEL = "HotelUsingCouponCancel";
        public static final String COUPON_BOX_CLICKED = "CouponBoxClicked";
        //
        public static final String SWITCHING_HOTEL = "SwitchingHotel";
        public static final String SWITCHING_GOURMET = "SwitchingGourmet";
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
        public static final String GRADE = "grade";
        public static final String DBENEFIT = "dBenefit";
        public static final String CATEGORY = "category";
        public static final String ADDRESS = "address";
        public static final String HOTEL_CATEGORY = "hotelCategory";
        public static final String PROVINCE = "province ";
        public static final String DISTRICT = "district ";
        public static final String AREA = "area ";
        public static final String NUM_OF_BOOKING = "num_of_booking";
        public static final String EVENT_NAME = "event_name";
        public static final String KEYWORD = "keyword";
        public static final String NUM_OF_SEARCH_RESULTS_RETURNED = "num_of_search_results_returned";
        public static final String USER_IDX = "user_idx";
        public static final String COUNTRY = "country";
        public static final String DOMESTIC = "domestic";
        public static final String OVERSEAS = "overseas";
        public static final String APP_VERSION = "app_version";
        public static final String CARD_ISSUING_COMPANY = "card_issuing_company";
        public static final String VIEWD_DATE = "viewed_date";
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
        public static final String SHOW_TAG_PRICE_YN = "show_tag_price_yn";
        public static final String LIST_INDEX = "list_index";
    }

    public static class ValueType
    {
        public static final String EMPTY = "null";
        public static final String LIST = "list";
        public static final String SEARCH = "search";
        public static final String SEARCH_RESULT = "searchResult";
        public static final String CHANGED = "Changed";
        public static final String NONE = "None";
        public static final String MEMBER = "member";
        public static final String GUEST = "guest";
        public static final String DETAIL = "detailview";
        public static final String HOTEL = "hotel";
        public static final String GOURMET = "gourmet";
    }
}
