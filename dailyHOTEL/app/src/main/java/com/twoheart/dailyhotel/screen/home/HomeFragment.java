package com.twoheart.dailyhotel.screen.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Review;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.gourmet.list.GourmetMainActivity;
import com.twoheart.dailyhotel.screen.hotel.list.StayMainActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.SignupStep1Activity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by android_sam on 2017. 1. 11..
 */

public class HomeFragment extends BaseFragment
{
    private HomeLayout mHomeLayout;
    private BaseActivity mBaseActivity;
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
        }

        if (mHomeLayout != null) {
            mHomeLayout.onResumeReviewAnimation();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        mDontReload = true;

        if (mHomeLayout != null) {
            mHomeLayout.onPauseReviewAnimation();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (mHomeLayout != null) {
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
        public void onRequestReview()
        {
            // TODO : 리뷰 요청하는 부분 작업 필요!
//            Test Code
            mNetworkController.requestReviewInformation();
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
            unLockUI();

            mSaleTime = new SaleTime();
            mSaleTime.setCurrentTime(currentDateTime);
            mSaleTime.setDailyTime(dailyDateTime);
            mSaleTime.setOffsetDailyDay(0);
        }

        @Override
        public void onReviewInformation(Review review)
        {
            mHomeLayout.setReviewMessage(review);
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
