package com.twoheart.dailyhotel.screen.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.model.Event;
import com.twoheart.dailyhotel.network.model.Recommendation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.event.EventWebActivity;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.screen.search.collection.CollectionGourmetActivity;
import com.twoheart.dailyhotel.screen.search.collection.CollectionStayActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeFragment extends BaseFragment
{
    private HomeLayout mHomeLayout;
    BaseActivity mBaseActivity;
    private PlaceType mPlaceType = PlaceType.HOTEL;
    private HomeNetworkController mNetworkController;
    private SaleTime mSaleTime;
    private int mNights = 1;
    private boolean mIsAttach;
    private boolean mDontReload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();

        mHomeLayout = new HomeLayout(mBaseActivity, mOnEventListener);
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
    public void onStart()
    {
        super.onStart();


    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (mDontReload == true)
        {
            mDontReload = true;
        } else
        {
            lockUI();

            // TODO : event, message, wishList, recentList, recommendList 요청 부분 필요
            mNetworkController.requestCommonDateTime();
            requestMessageData();
            mNetworkController.requestEventList();
            mNetworkController.requestRecommendationList();
            mNetworkController.requestWishList();
        }

        // 애니메이션 처리!
        if (mHomeLayout != null)
        {
            mHomeLayout.onResumeReviewAnimation();
            mHomeLayout.onResumeCarouselAnimation();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mDontReload = true;

        if (mHomeLayout != null)
        {
            mHomeLayout.onPauseReviewAnimation();
            mHomeLayout.onPauseCarouselAnimation();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mHomeLayout != null)
        {
            mHomeLayout.onDestroyReviewAnimation();
        }
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

            case CODE_REQUEST_ACTIVITY_EVENTWEB:
            {
                mDontReload = true;
                break;
            }
        }
    }

    private void requestMessageData()
    {
        if (mHomeLayout == null)
        {
            return;
        }

        HomeLayout.MessageType messageType = HomeLayout.MessageType.NONE;

        if (DailyHotel.isLogin() == true)
        {
            boolean isLoginAreaEnable = DailyPreference.getInstance(mBaseActivity).isRemoteConfigHomeMessageAreaLoginEnabled();
            if (isLoginAreaEnable == true)
            {
                messageType = HomeLayout.MessageType.REVIEW;
            }
        } else
        {
            boolean isLogoutAreaEnable = DailyPreference.getInstance(mBaseActivity).isRemoteConfigHomeMessageAreaLogoutEnabled();
            if (isLogoutAreaEnable == true)
            {
                messageType = HomeLayout.MessageType.TEXT;
            }
        }

        if (HomeLayout.MessageType.REVIEW == messageType)
        {
            // TODO : request review data;
            mNetworkController.requestReviewInformation();
        } else if (HomeLayout.MessageType.TEXT == messageType)
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
        }
    }

    private void startSignUp(String recommenderCode)
    {
        if (isLockUiComponent() == true || mIsAttach == false)
        {
            return;
        }

        lockUiComponent();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        Intent intent;

        if (Util.isTextEmpty(recommenderCode) == true)
        {
            intent = SignupStep1Activity.newInstance(baseActivity, null);
        } else
        {
            intent = SignupStep1Activity.newInstance(baseActivity, recommenderCode, null);
        }

        startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
    }

    private void startEventListActivity(String url, String eventName)
    {
        if (Util.isTextEmpty(url) == true)
        {
            return;
        }

        Intent intent = EventWebActivity.newInstance(mBaseActivity, EventWebActivity.SourceType.EVENT, url, eventName);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_EVENTWEB);
    }

    private HomeLayout.OnEventListener mOnEventListener = new HomeLayout.OnEventListener()
    {
        @Override
        public void onMessageTextAreaClick()
        {
            // 회원가입으로 이동!
            startSignUp(null);
        }

        @Override
        public void onMessageCloseClick()
        {
            // TODO : 다시 보지 않기 기능 추가 필요???
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

            mBaseActivity.startActivity(SearchActivity.newInstance(mBaseActivity, mPlaceType, mSaleTime, mNights));
        }

        @Override
        public void onStayButtonClick()
        {
            if (mBaseActivity == null)
            {
                return;
            }

            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            mBaseActivity.startActivityForResult(StayMainActivity.newInstance(mBaseActivity), Constants.CODE_REQUEST_ACTIVITY_STAY);
        }

        @Override
        public void onGourmetButtonClick()
        {
            if (mBaseActivity == null)
            {
                return;
            }

            if (isLockUiComponent() == true || mIsAttach == false)
            {
                return;
            }

            mBaseActivity.startActivityForResult(GourmetMainActivity.newInstance(getContext()), Constants.CODE_REQUEST_ACTIVITY_GOURMET);
        }

        @Override
        public void onRefreshAll(boolean isShowProgress)
        {
            ExLog.d("isShowProgress : " + isShowProgress);

            mHomeLayout.setRefreshing(false);
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

            if (Util.isTextEmpty(event.linkUrl, event.title) == true)
            {
                return;
            }

            HomeFragment.this.startEventListActivity(event.linkUrl, event.title);
        }

        @Override
        public void onRecommendationClick(View view, Recommendation recommendation)
        {
//            Intent intent = CollectionStayActivity.newInstance(mBaseActivity, recommendation.idx//
//                , Util.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
//                , recommendation.title, recommendation.subtitle);

            Intent intent = CollectionGourmetActivity.newInstance(mBaseActivity, recommendation.idx//
                , Util.getResolutionImageUrl(mBaseActivity, recommendation.defaultImageUrl, recommendation.lowResolutionImageUrl)//
                , recommendation.title, recommendation.subtitle);


            if (Util.isUsedMultiTransition() == true)
            {
                View simpleDraweeView = view.findViewById(R.id.contentImageView);
                View contentTextLayout = view.findViewById(R.id.contentTextLayout);
                View titleTextView = contentTextLayout.findViewById(R.id.contentTextView);
                View subTitleTextView = contentTextLayout.findViewById(R.id.contentDescriptionView);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mBaseActivity,//
                    android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image)),//
                    android.support.v4.util.Pair.create(contentTextLayout, getString(R.string.transition_layout)),//
                    android.support.v4.util.Pair.create(titleTextView, getString(R.string.transition_title)),//
                    android.support.v4.util.Pair.create(subTitleTextView, getString(R.string.transition_subtitle)));

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION, options.toBundle());
            } else
            {
                mBaseActivity.startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COLLECTION);
            }
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
        public void onCommonDateTime(long currentDateTime, long dailyDateTime)
        {
            if (isFinishing() == true)
            {
                return;
            }

            unLockUI();

            mSaleTime = new SaleTime();
            mSaleTime.setCurrentTime(currentDateTime);
            mSaleTime.setDailyTime(dailyDateTime);
            mSaleTime.setOffsetDailyDay(0);
        }

        @Override
        public void onReviewInformation(Review review)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setReviewData(review);
            }
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
        public void onWishList(ArrayList<? extends Place> list)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setWishListData(list);
            }
        }

        @Override
        public void onRecommendationList(ArrayList<Recommendation> list)
        {
            if (mHomeLayout != null)
            {
                mHomeLayout.setRecommendationData(list);
            }
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
