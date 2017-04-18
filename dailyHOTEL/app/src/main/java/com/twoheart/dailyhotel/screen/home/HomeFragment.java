package com.twoheart.dailyhotel.screen.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseMenuNavigationFragment;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.home.collection.CollectionGourmetActivity;
import com.twoheart.dailyhotel.screen.home.collection.CollectionStayActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainActivity;
import com.twoheart.dailyhotel.screen.information.terms.LocationTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.ProtectYouthTermsActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.mydaily.recentplace.RecentPlacesTabActivity;
import com.twoheart.dailyhotel.screen.mydaily.wishlist.WishListTabActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeFragment extends BaseMenuNavigationFragment
{
    private static final int MAX_REQUEST_SIZE = 10;

    private static final int IS_RUNNED_NONE = 0;
    private static final int IS_RUNNED_WISHLIST = 1 << 1;
    private static final int IS_RUNNED_RECENTLIST = 1 << 2;

    HomeLayout mHomeLayout;
    BaseActivity mBaseActivity;
    PlaceType mPlaceType = PlaceType.HOTEL;
    private HomeNetworkController mNetworkController;
    TodayDateTime mTodayDateTime;
    int mNights = 1;
    boolean mIsAttach;
    boolean mDontReload;
    private boolean mIsLogin;

    int mNetworkRunState = IS_RUNNED_NONE; // 0x0000 : 초기 상태, Ox0010 : 위시 완료 , Ox0100 : 최근 본 업장완료!

    private DailyDeepLink mDailyDeepLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mIsLogin = DailyHotel.isLogin();
        mHomeLayout = new HomeLayout(mBaseActivity, mOnEventListener);
        mHomeLayout.setOnScrollChangedListener(mOnScreenScrollChangeListener);

        mNetworkController = new HomeNetworkController(mBaseActivity, mNetworkTag, mNetworkControllerListener);
        return mHomeLayout.onCreateView(R.layout.fragment_home_main, container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mIsAttach = true;
    }

    @Override
    public void onNewBundle(Bundle bundle)
    {
        if (bundle == null)
        {
            return;
        }

        if (bundle.containsKey(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            try
            {
                mDailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(bundle.getString(Constants.NAME_INTENT_EXTRA_DATA_DEEPLINK)));
            } catch (Exception e)
            {
                mDailyDeepLink = null;
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        moveDeepLink(mDailyDeepLink);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        refreshList(true);

        sendHomeScreenAnalytics();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mDontReload = true;
    }

    @Override
    public void onDestroy()
    {
        if (mHomeLayout != null)
        {
            mHomeLayout.removeOnLayoutChangeListener();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
                if (resultCode == Activity.RESULT_OK)
                {
                }
                break;

            case CODE_REQEUST_ACTIVITY_SIGNUP:
            {
                break;
            }

            case CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL:
            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
            {
                mDontReload = true;
                mHomeLayout.setScrollTop();

                forceRefreshing();
                break;
            }

            // 해당 go home 목록이 MainActivity 목록과 동일해야함.
            case Constants.CODE_REQUEST_ACTIVITY_STAY:
            case Constants.CODE_REQUEST_ACTIVITY_GOURMET:
            case Constants.CODE_REQUEST_ACTIVITY_EVENTWEB:
            case Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_SEARCH:
            case Constants.CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case Constants.CODE_REQUEST_ACTIVITY_BOOKING_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_COLLECTION:
            case Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE:
            case Constants.CODE_REQUEST_ACTIVITY_GUIDE:
            case Constants.CODE_REQUEST_ACTIVITY_ABOUT:
            case Constants.CODE_REQUEST_ACTIVITY_SNS:
            case Constants.CODE_REQUEST_ACTIVITY_LIFESTYLE:
            case Constants.CODE_REQUEST_ACTIVITY_EVENT_LIST:
            case Constants.CODE_REQUEST_ACTIVITY_NOTICE_LIST:
            case Constants.CODE_REQUEST_ACTIVITY_FAQ:
            case Constants.CODE_REQUEST_ACTIVITY_CONTACTUS:
            case Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY:
            case Constants.CODE_REQUEST_ACTIVITY_VIRTUAL_BOOKING_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_COUPONLIST:
            case Constants.CODE_REQUEST_ACTIVITY_BONUS:
            case Constants.CODE_REQUEST_ACTIVITY_STAMP:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    mDontReload = true;
                    mHomeLayout.setScrollTop();

                    forceRefreshing();
                }
                break;
        }
    }

    private void moveDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || dailyDeepLink.isValidateLink() == false)
        {
            return;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHomeEventDetailView() == true)
            {
                startEventWebActivity(externalDeepLink.getUrl(), externalDeepLink.getTitle());
            } else if (externalDeepLink.isHomeRecommendationPlaceListView() == true)
            {
                String serviceType = externalDeepLink.getPlaceType();
                String stringIndex = externalDeepLink.getIndex();

                int idx;

                try
                {
                    idx = Integer.parseInt(stringIndex);
                    startDeepLinkRecommendationActivity(serviceType, idx);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
            } else if (externalDeepLink.isHotelView() == true)
            {
                onStayClick(true, externalDeepLink);
            } else if (externalDeepLink.isGourmetView() == true)
            {
                onGourmetClick(true, externalDeepLink);
            } else if (externalDeepLink.isRecentlyWatchHotelView() == true)
            {
                startRecentList(PlaceType.HOTEL);
            } else if (externalDeepLink.isRecentlyWatchGourmetView() == true)
            {
                startRecentList(PlaceType.FNB);
            } else if (externalDeepLink.isWishListHotelView() == true)
            {
                startWishList(PlaceType.HOTEL);
            } else if (externalDeepLink.isWishListGourmetView() == true)
            {
                startWishList(PlaceType.FNB);
            } else if (externalDeepLink.isStampView() == true)
            {
                if (DailyPreference.getInstance(mBaseActivity).isRemoteConfigStampStayEndEventPopupEnabled() == true)
                {
                    mBaseActivity.showSimpleDialog(null, getString(R.string.message_stamp_finish_stamp), getString(R.string.dialog_btn_text_confirm), null);
                }
            }
        } else
        {

        }

        dailyDeepLink.clear();
    }

    private void requestMessageData()
    {
        if (mHomeLayout == null)
        {
            return;
        }

        HomeLayout.MessageType messageType = HomeLayout.MessageType.NONE;

        if (mIsLogin == false)
        {
            boolean isLogoutAreaEnable = DailyPreference.getInstance(mBaseActivity).isRemoteConfigHomeMessageAreaLogoutEnabled();
            boolean isTextMessageAreaEnable = DailyPreference.getInstance(mBaseActivity).isHomeTextMessageAreaEnabled();

            if (isLogoutAreaEnable == true && isTextMessageAreaEnable == true)
            {
                messageType = HomeLayout.MessageType.TEXT;
            }
        }

        if (HomeLayout.MessageType.TEXT == messageType)
        {
            requestTextMessage();
        } else
        {
            mHomeLayout.hideMessageLayout();
        }
    }

    private void requestTextMessage()
    {
        String title = DailyPreference.getInstance(mBaseActivity).getRemoteConfigHomeMessageAreaLogoutTitle();
        String description = DailyPreference.getInstance(mBaseActivity).getRemoteConfigHomeMessageAreaLogoutCallToAction();

        if (mHomeLayout != null)
        {
            mHomeLayout.setTextMessageData(title, description);

            if (DailyTextUtils.isTextEmpty(title) == false || DailyTextUtils.isTextEmpty(description) == false)
            {
                AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                    AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_MESSAGE_OPEN,//
                    AnalyticsManager.Label.SIGNUP, null);
            }
        }
    }

    void startSignUp(String recommenderCode)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        Intent intent;

        if (DailyTextUtils.isTextEmpty(recommenderCode) == true)
        {
            intent = SignupStep1Activity.newInstance(baseActivity, null);
        } else
        {
            intent = SignupStep1Activity.newInstance(baseActivity, recommenderCode, null);
        }

        startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
    }

    void startEventWebActivity(String url, String eventName)
    {
        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            return;
        }

        Intent intent = EventWebActivity.newInstance(mBaseActivity, EventWebActivity.SourceType.HOME_EVENT, url, eventName);
        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
    }

    private void startDeepLinkRecommendationActivity(String serviceType, int index)
    {
        Intent intent;

        switch (serviceType)
        {
            case "gourmet":
                intent = CollectionGourmetActivity.newInstance(mBaseActivity, index//
                    , null//
                    , null, null, false);
                break;

            case "stay":
            default:
                intent = CollectionStayActivity.newInstance(mBaseActivity, index//
                    , null//
                    , null, null, false);
                break;

        }

        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);

        mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
    }

    void startWishList(PlaceType placeType)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();
        Intent intent = WishListTabActivity.newInstance(baseActivity, WishListTabActivity.SourceType.HOME, placeType);

        baseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE);
    }

    public void startRecentList(PlaceType placeType)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();
        Intent intent = RecentPlacesTabActivity.newInstance(baseActivity, RecentPlacesTabActivity.SourceType.HOME, placeType);

        baseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_RECENTPLACE);
    }

    public void startPlaceDetail(View view, HomePlace place, TodayDateTime todayDateTime)
    {
        if (place == null || todayDateTime == null)
        {
            return;
        }

        try
        {
            switch (place.placeType)
            {
                case HOTEL:
                {
                    StayBookingDay stayBookingDay = new StayBookingDay();
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                    stayBookingDay.setCheckOutDay(todayDateTime.dailyDateTime, 1);

                    if (Util.isUsedMultiTransition() == true)
                    {
                        Intent intent = StayDetailActivity.newInstance(mBaseActivity, stayBookingDay, place, true);

                        if (intent == null)
                        {
                            Util.restartApp(mBaseActivity);
                            return;
                        }

                        View simpleDraweeView = view.findViewById(R.id.contentImageView);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                            android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)));

                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL, options.toBundle());
                    } else
                    {
                        Intent intent = StayDetailActivity.newInstance(mBaseActivity, stayBookingDay, place, false);

                        if (intent == null)
                        {
                            Util.restartApp(mBaseActivity);
                            return;
                        }

                        mBaseActivity.startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                        mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }
                    break;
                }

                case FNB:
                {
                    GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
                    gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime);

                    if (Util.isUsedMultiTransition() == true)
                    {
                        Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, gourmetBookingDay, place, true);

                        if (intent == null)
                        {
                            Util.restartApp(mBaseActivity);
                            return;
                        }

                        View simpleDraweeView = view.findViewById(R.id.contentImageView);

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                            android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)));

                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
                    } else
                    {
                        Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, gourmetBookingDay, place, false);

                        if (intent == null)
                        {
                            Util.restartApp(mBaseActivity);
                            return;
                        }

                        mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                        mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
                    }
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void sendHomeScreenAnalytics()
    {
        if (mBaseActivity == null)
        {
            return;
        }

        String memberType = mIsLogin == true //
            ? AnalyticsManager.ValueType.MEMBER : AnalyticsManager.ValueType.GUEST;

        HashMap<String, String> params = new HashMap<>();

        params.put(AnalyticsManager.KeyType.MEMBER_TYPE, memberType);

        AnalyticsManager.getInstance(mBaseActivity).recordScreen(//
            mBaseActivity, AnalyticsManager.Screen.HOME, null, params);
    }

    public void sendHomeBlockEventAnalytics()
    {
        if (mHomeLayout == null || mBaseActivity == null)
        {
            return;
        }

        boolean isRunWishList = (mNetworkRunState & IS_RUNNED_WISHLIST) > 0;
        boolean isRunRecentList = (mNetworkRunState & IS_RUNNED_RECENTLIST) > 0;

        if (mNetworkRunState != IS_RUNNED_NONE && isRunWishList == true && isRunRecentList == true)
        {
            String label = AnalyticsManager.Label.NONE;
            if (mHomeLayout.hasWishListData() == true && mHomeLayout.hasRecentListData() == true)
            {
                label = AnalyticsManager.Label.WISHLIST_RECENTVIEW;
            } else if (mHomeLayout.hasWishListData() == true)
            {
                label = AnalyticsManager.Label.WISHLIST;
            } else if (mHomeLayout.hasRecentListData() == true)
            {
                label = AnalyticsManager.Label.RECENTVIEW;
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_BLOCK_SHOW,//
                label, null);
        }
    }

    private void requestWishList()
    {
        if (mIsLogin == false)
        {
            mNetworkControllerListener.onWishList(null, false);
            return;
        }

        mNetworkController.requestWishList();
    }

    private void requestRecentList()
    {
        RecentPlaces allRecentPlaces = new RecentPlaces(mBaseActivity);
        mNetworkController.requestRecentList(allRecentPlaces.getParamList(MAX_REQUEST_SIZE));
    }

    public void forceRefreshing()
    {
        if (mHomeLayout == null && mHomeLayout.isRefreshing() == false && lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI(false);

        mHomeLayout.forceRefreshing(false);
    }

    public void refreshList(boolean isShowLockUi)
    {
        boolean newLoginState = DailyHotel.isLogin();
        if (newLoginState != mIsLogin)
        {
            mIsLogin = newLoginState;
            requestAllData(isShowLockUi);
            return;
        }

        if (mDontReload == true)
        {
            mDontReload = false;
        } else
        {
            requestAllData(isShowLockUi);
        }
    }

    private void requestAllData(boolean isShowLockUi)
    {
        if (mNetworkController != null)
        {
            lockUI(isShowLockUi);

            mNetworkRunState = IS_RUNNED_NONE;

            mNetworkController.requestCommonDateTime();
            requestMessageData();
            mNetworkController.requestEventList();
            mNetworkController.requestRecommendationList();
            requestWishList();
            requestRecentList();
        }
    }

    private void onStayClick(boolean isDeepLink, DailyDeepLink dailyDeepLink)
    {
        if (mBaseActivity == null)
        {
            return;
        }

        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        mBaseActivity.startActivityForResult(StayMainActivity.newInstance(mBaseActivity//
            , dailyDeepLink != null ? dailyDeepLink.getDeepLink() : null), Constants.CODE_REQUEST_ACTIVITY_STAY);

        if (isDeepLink == false)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.STAY_LIST_CLICK,//
                AnalyticsManager.Label.HOME, null);
        }
    }

    private void onGourmetClick(boolean isDeepLink, DailyDeepLink dailyDeepLink)
    {
        if (mBaseActivity == null)
        {
            return;
        }

        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        mBaseActivity.startActivityForResult(GourmetMainActivity.newInstance(mBaseActivity//
            , dailyDeepLink != null ? dailyDeepLink.getDeepLink() : null), Constants.CODE_REQUEST_ACTIVITY_GOURMET);

        if (isDeepLink == false)
        {
            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_LIST_CLICK,//
                AnalyticsManager.Label.HOME, null);
        }
    }

    @Override
    public void setOnScrollChangedListener(BaseMenuNavigationFragment.OnScreenScrollChangeListener listener)
    {
        mOnScreenScrollChangeListener = listener;

        if (mHomeLayout != null)
        {
            mHomeLayout.setOnScrollChangedListener(listener);
        }
    }

    @Override
    public void scrollTop()
    {
        if (mHomeLayout != null)
        {
            mHomeLayout.setScrollTop();
        }
    }

    private HomeLayout.OnEventListener mOnEventListener = new HomeLayout.OnEventListener()
    {
        @Override
        public void onMessageTextAreaClick()
        {
            // 회원가입으로 이동!
            startSignUp(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.MESSAGE_CLICK,//
                AnalyticsManager.Label.SIGNUP, null);
        }

        @Override
        public void onMessageTextAreaCloseClick()
        {
            DailyPreference.getInstance(mBaseActivity).setHomeTextMessageAreaEnabled(false);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.MESSAGE_CLOSE,//
                AnalyticsManager.Label.SIGNUP, null);
        }

        @Override
        public void onSearchImageClick()
        {
            if (mBaseActivity == null)
            {
                return;
            }

            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            try
            {
                StayBookingDay stayBookingDay = new StayBookingDay();
                stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime);
                stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, 1);

                mBaseActivity.startActivityForResult(SearchActivity.newInstance(mBaseActivity, mPlaceType, stayBookingDay), Constants.CODE_REQUEST_ACTIVITY_SEARCH);

                AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                    AnalyticsManager.Category.SEARCH, AnalyticsManager.Action.SEARCH_BUTTON_CLICK,//
                    AnalyticsManager.Label.HOME, null);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        @Override
        public void onStayButtonClick()
        {
            onStayClick(false, null);
        }

        @Override
        public void onGourmetButtonClick()
        {
            onGourmetClick(false, null);
        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            mDontReload = false;
            refreshList(isShowProgress);
        }

        @Override
        public void onTopButtonClick()
        {
            mHomeLayout.setScrollTop();
        }

        @Override
        public void onEventItemClick(Event event)
        {
            if (event == null)
            {
                return;
            }

            if (DailyTextUtils.isTextEmpty(event.linkUrl, event.title) == true)
            {
                return;
            }

            HomeFragment.this.startEventWebActivity(event.linkUrl, event.title);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_EVENT_BANNER_CLICK,//
                Integer.toString(event.index), null);
        }

        @Override
        public void onRecommendationClick(View view, Recommendation recommendation)
        {
            Intent intent;

            if (Util.isUsedMultiTransition() == true)
            {
                switch (recommendation.serviceType)
                {
                    case "GOURMET":
                        intent = CollectionGourmetActivity.newInstance(mBaseActivity, recommendation.idx//
                            , ScreenUtils.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                            , recommendation.title, recommendation.subtitle, true);
                        break;

                    case "HOTEL":
                    default:
                        intent = CollectionStayActivity.newInstance(mBaseActivity, recommendation.idx//
                            , ScreenUtils.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                            , recommendation.title, recommendation.subtitle, true);
                        break;
                }

                View simpleDraweeView = view.findViewById(R.id.contentImageView);
                View contentTextLayout = view.findViewById(R.id.contentTextLayout);
                View titleTextView = contentTextLayout.findViewById(R.id.contentTextView);
                View subTitleTextView = contentTextLayout.findViewById(R.id.contentDescriptionView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(contentTextLayout, getString(R.string.transition_layout)),//
                    android.support.v4.util.Pair.create(titleTextView, getString(R.string.transition_title)),//
                    android.support.v4.util.Pair.create(subTitleTextView, getString(R.string.transition_subtitle)));

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION, options.toBundle());
            } else
            {
                switch (recommendation.serviceType)
                {
                    case "GOURMET":
                        intent = CollectionGourmetActivity.newInstance(mBaseActivity, recommendation.idx//
                            , ScreenUtils.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                            , recommendation.title, recommendation.subtitle, false);
                        break;

                    case "HOTEL":
                    default:
                        intent = CollectionStayActivity.newInstance(mBaseActivity, recommendation.idx//
                            , ScreenUtils.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                            , recommendation.title, recommendation.subtitle, false);
                        break;
                }

                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);

                mBaseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_RECOMMEND_LIST_CLICK,//
                Integer.toString(recommendation.idx), null);
        }

        @Override
        public void onWishListViewAllClick()
        {
            startWishList(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_ALL_WISHLIST_CLICK, //
                null, null);
        }

        @Override
        public void onRecentListViewAllClick()
        {
            startRecentList(null);

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_ALL_RECENTVIEW_CLICK, //
                null, null);
        }

        @Override
        public void onWishListItemClick(View view, int position)
        {
            HomePlace wishItem = null;

            if (view != null)
            {
                wishItem = (HomePlace) view.getTag();
            }

            if (wishItem == null && mHomeLayout != null)
            {
                wishItem = mHomeLayout.getWishItem(position);
            }

            if (wishItem != null)
            {
                startPlaceDetail(view, wishItem, mTodayDateTime);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_WISHLIST_CLICK,//
                Integer.toString(wishItem.index), null);
        }

        @Override
        public void onRecentListItemClick(View view, int position)
        {
            HomePlace recentItem = null;

            if (view != null)
            {
                recentItem = (HomePlace) view.getTag();
            }

            if (recentItem == null && mHomeLayout != null)
            {
                recentItem = mHomeLayout.getRecentItem(position);
            }

            if (recentItem != null)
            {
                startPlaceDetail(view, recentItem, mTodayDateTime);
            }

            AnalyticsManager.getInstance(mBaseActivity).recordEvent(//
                AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOME_RECENTVIEW_CLICK,//
                Integer.toString(recentItem.index), null);
        }

        @Override
        public void onTermsClick()
        {
            Intent intent = new Intent(mBaseActivity, TermActivity.class);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY);
        }

        @Override
        public void onPrivacyTermsClick()
        {
            Intent intent = new Intent(mBaseActivity, PrivacyActivity.class);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY);
        }

        @Override
        public void onLocationTermsClick()
        {
            Intent intent = new Intent(mBaseActivity, LocationTermsActivity.class);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY);
        }

        @Override
        public void onProtectedYouthClick()
        {
            Intent intent = new Intent(mBaseActivity, ProtectYouthTermsActivity.class);
            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_TERMS_AND_POLICY);
        }

        @Override
        public void finish()
        {
            unLockUI();
            mBaseActivity.finish();
        }
    };

    HomeNetworkController.OnNetworkControllerListener mNetworkControllerListener = new HomeNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onCommonDateTime(TodayDateTime todayDateTime)
        {
            if (isFinishing() == true)
            {
                return;
            }

            unLockUI();

            if (mHomeLayout != null)
            {
                if (mHomeLayout.isRefreshing() == true)
                {
                    mHomeLayout.setRefreshing(false);
                }
            }

            mTodayDateTime = todayDateTime;
        }

        @Override
        public void onEventList(ArrayList<Event> list)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setEventList(list);
            }
        }

        @Override
        public void onWishList(ArrayList<HomePlace> list, boolean isError)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setWishListData(list, isError);
            }

            mNetworkRunState = mNetworkRunState | IS_RUNNED_WISHLIST;

            sendHomeBlockEventAnalytics();
        }

        @Override
        public void onRecentList(ArrayList<HomePlace> list, boolean isError)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setRecentListData(list, isError);
            }

            mNetworkRunState = mNetworkRunState | IS_RUNNED_RECENTLIST;

            sendHomeBlockEventAnalytics();
        }

        @Override
        public void onRecommendationList(ArrayList<Recommendation> list, boolean isError)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setRecommendationData(list, isError);
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            HomeFragment.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            HomeFragment.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            HomeFragment.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            HomeFragment.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            HomeFragment.this.onErrorResponse(call, response);
        }
    };
}
