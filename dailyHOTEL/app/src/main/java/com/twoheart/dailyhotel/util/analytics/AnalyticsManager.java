package com.twoheart.dailyhotel.util.analytics;

import android.app.Activity;
import android.content.Context;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.Map;

public class AnalyticsManager
{
    private static final String TAG = "[AnalyticsManager]";

    private static AnalyticsManager mInstance = null;
    private Context mContext;
    private GoogleAnalyticsManager mGoogleAnalyticsManager;
    private TuneManager mTuneManager;
    private FacebookManager mFacebookManager;

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
        initAnalytics(context);
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
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void setUserIndex(String index)
    {
        try
        {
            mGoogleAnalyticsManager.setUserIndex(index);
            mTuneManager.setUserIndex(index);
            mFacebookManager.setUserIndex(index);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void onResume(Activity activity)
    {
        try
        {
            mGoogleAnalyticsManager.onResume(activity);
            mTuneManager.onResume(activity);
            mFacebookManager.onResume(activity);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void onPause(Activity activity)
    {
        try
        {
            mGoogleAnalyticsManager.onPause(activity);
            mTuneManager.onPause(activity);
            mFacebookManager.onPause(activity);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void recordScreen(String screen, Map<String, String> params)
    {
        try
        {
            mGoogleAnalyticsManager.recordScreen(screen, params);
            mTuneManager.recordScreen(screen, params);
            mFacebookManager.recordScreen(screen, params);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void recordEvent(String category, String action, String label, Map<String, String> params)
    {
        try
        {
            mGoogleAnalyticsManager.recordEvent(category, action, label, params);
            mTuneManager.recordEvent(category, action, label, params);
            mFacebookManager.recordEvent(category, action, label, params);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Special Event
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addCreditCard(String cardType)
    {
        try
        {
            mGoogleAnalyticsManager.addCreditCard(cardType);
            mTuneManager.addCreditCard(cardType);
            mFacebookManager.addCreditCard(cardType);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void singUpSocialUser(String userIndex, String email, String name, String gender, String phoneNumber, String userType)
    {
        try
        {
            mGoogleAnalyticsManager.signUpSocialUser(userIndex, email, name, gender, phoneNumber, userType);
            mTuneManager.signUpSocialUser(userIndex, email, name, gender, phoneNumber, userType);
            mFacebookManager.signUpSocialUser(userIndex, email, name, gender, phoneNumber, userType);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void signUpDailyUser(String userIndex, String email, String name, String phoneNumber, String userType)
    {
        try
        {
            mGoogleAnalyticsManager.signUpDailyUser(userIndex, email, name, phoneNumber, userType);
            mTuneManager.signUpDailyUser(userIndex, email, name, phoneNumber, userType);
            mFacebookManager.signUpDailyUser(userIndex, email, name, phoneNumber, userType);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void purchaseCompleteHotel(String transId, Map<String, String> params)
    {
        try
        {
            mGoogleAnalyticsManager.purchaseCompleteHotel(transId, params);
            mTuneManager.purchaseCompleteHotel(transId, params);
            mFacebookManager.purchaseCompleteHotel(transId, params);

            String price = params.get(AnalyticsManager.KeyType.TOTAL_PRICE);
            AdWordsConversionReporter.reportWithConversionId(mContext, "927521285", "9r3_CPun0GQQhbSjugM", price, true);
        } catch (Exception e)
        {
            ExLog.d(TAG + e.toString());
        }
    }

    public void purchaseCompleteGourmet(String transId, Map<String, String> params)
    {
        try
        {
            mGoogleAnalyticsManager.purchaseCompleteGourmet(transId, params);
            mTuneManager.purchaseCompleteGourmet(transId, params);
            mFacebookManager.purchaseCompleteGourmet(transId, params);

            String price = params.get(AnalyticsManager.KeyType.TOTAL_PRICE);
            AdWordsConversionReporter.reportWithConversionId(mContext, "927521285", "9r3_CPun0GQQhbSjugM", price, true);
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
        public static final String DAILYHOTEL_SEARCH = "DailyHotel_SearchScreenView";
        public static final String DAILYHOTEL_SEARCH_RESULT = "DailyHotel_SearchResultView";
        public static final String DAILYHOTEL_SEARCH_RESULT_EMPTY = "DailyHotel_EmptySearchResultView";
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
        //
        public static final String DAILYGOURMET_LIST = "DailyGourmet_GourmetList";
        public static final String DAILYGOURMET_LIST_MAP = "DailyGourmet_GourmetMapView";
        public static final String DAILYGOURMET_LIST_EMPTY = "DailyGourmet_NotGourmetAvailable";
        public static final String DAILYGOURMET_LIST_REGION_DOMESTIC = "DailyGourmet_GourmetLocationList";
        public static final String DAILYGOURMET_LIST_CALENDAR = "DailyGourmet_GourmetBookingWindow";
        public static final String DAILYGOURMET_BANNER_DETAIL = "DailyGourmet_EventBannerDetailView";
        public static final String DAILYGOURMET_CURATION = "DailyGourmet_SortFilterSelectView";
        public static final String DAILYGOURMET_SEARCH = "DailyGourmet_SearchScreenView";
        public static final String DAILYGOURMET_SEARCH_RESULT = "DailyGourmet_SearchResultView";
        public static final String DAILYGOURMET_SEARCH_RESULT_EMPTY = "DailyGourmet_EmptySearchResultView";
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
        public static final String SIGNUP = "Menu_Registration";
        //
        public static final String TERMSOFUSE = "Menu_TermsofUse";
        public static final String TERMSOFPRIVACY = "Menu_TermsofPrivacy";
        public static final String FORGOTPASSWORD = "Menu_LostPassword";
        public static final String PROFILE = "Menu_Profile";
        public static final String TERMSOFLOCATION = "Menu_TermsofLocation";
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
        //
        public static final String THANKYOU_SCREEN_BUTTON_CLICKED = "ThankyouScreenButtonClicked";
        //
        public static final String HOTEL_SEARCH_BUTTON_CLICKED = "HotelSearchButtonClicked";
        public static final String HOTEL_KEYWORD_SEARCH_CLICKED = "HotelKeywordSearchClicked";
        public static final String HOTEL_SEARCH_BACK_BUTTON_CLICKED = "HotelSearchBackButtonClicked";
        public static final String HOTEL_KEYWORD_RESET_CLICKED = "HotelKeywordResetClicked";
        public static final String HOTEL_AROUND_SEARCH_CLICKED = "HotelAroundSearchClicked";
        public static final String HOTEL_KEYWORD_HISTORY_DELETED = "HotelKeywordsHistoryDeleted";
        public static final String HOTEL_RECENT_KEYWORD_SEARCH_CLICKED = "HotelRecentKeywordSearchClicked";
        public static final String HOTEL_AUTOCOMPLETED_KEYWORD_SEARCH_CLICKED = "HotelAutoCompletedKeywordClicked";
        public static final String HOTEL_AUTOCOMPLETED_KEYWORD_NOTMATCHED = "HotelAutoCompletedKeywordNotMatched";
        public static final String HOTEL_AROUND_NOT_FOUND = "HotelAroundNotFound";
        public static final String HOTEL_SEARCH_NOT_FOUND = "HotelSearchNotFound";
        public static final String HOTEL_SEARCH_AGAIN_CLICKED = "HotelSearchAgainClicked";
        public static final String HOTEL_SEARCH_RESULT_CANCELED = "HotelSearchResultCanceled";
        //
        public static final String GOURMET_SEARCH_BUTTON_CLICKED = "GourmetSearchButtonClicked";
        public static final String GOURMET_KEYWORD_SEARCH_CLICKED = "GourmetKeywordSearchClicked";
        public static final String GOUREMT_SEARCH_BACK_BUTTON_CLICKED = "GourmetSearchBackButtonClicked";
        public static final String GOURMET_KEYWORD_RESET_CLICKED = "GourmetKeywordResetClicked";
        public static final String GOURMET_AROUND_SEARCH_CLICKED = "GourmetAroundSearchClicked";
        public static final String GOURMET_KEYWORD_HISTORY_DELETED = "GourmetKeywordsHistoryDeleted";
        public static final String GOURMET_RECENT_KEYWORD_SEARCH_CLICKED = "GourmetRecentKeywordSearchClicked";
        public static final String GOURMET_AUTOCOMPLETED_KEYWORD_SEARCH_CLICKED = "GourmetAutoCompletedKeywordClicked";
        public static final String GOURMET_AROUND_NOT_FOUND = "GourmetAroundNotFound";
        public static final String GOURMET_SEARCH_NOT_FOUND = "GourmetSearchNotFound";
        public static final String GOURMET_SEARCH_AGAIN_CLICKED = "GourmetSearchAgainClicked";
        public static final String GOURMET_SEARCH_RESULT_CANCELED = "GourmetSearchResultCanceled";
        //
        public static final String LOCATION_AGREEMENT_POPPEDUP = "LocationAgreementPoppedup";
        public static final String CALL_INQUIRY_CLICKED = "CallInquiryClicked";
    }

    public static class Category
    {
        public static final String NAVIGATION = "Navigation";
        public static final String HOTEL_BOOKINGS = "HotelBookings";
        public static final String GOURMET_BOOKINGS = "GourmetBookings";
        public static final String POPUP_BOXES = "PopupBoxes";
        public static final String HOTEL_SEARCH = "HotelSearches";
        public static final String GOURMET_SEARCH_ = "GourmetSearches";
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
        public static final String CREDIT_HISTORY_VIEW = "CreditHistoryView";
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
        public static final String CALL_KEYWORD_HOTEL = "Call-KeywordHotel";
        public static final String CALL_KEYWORD_GOURMET = "Call-KeywordGourmet";
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
    }
}
