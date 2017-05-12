package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.twoheart.dailyhotel.R;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailPresenter extends BaseExceptionPresenter<StayOutboundDetailActivity, StayOutboundDetailViewInterface> implements StayOutboundDetailView.OnEventListener
{
    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private int mStayIndex;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private Persons mPersons;

    private boolean mIsUsedMultiTransition;
    private boolean mIsDeepLink;


    public interface StayOutboundDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundDetailPresenter(@NonNull StayOutboundDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundDetailViewInterface createInstanceViewInterface()
    {
        return new StayOutboundDetailView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_detail_data);

        setAnalytics(new StayStayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);

        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundDetailAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
            mIsUsedMultiTransition = false;
            mIsDeepLink = false;
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);
            mIsDeepLink = true;

            mStayIndex = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);

            if (mStayIndex == -1)
            {
                return false;
            }

            mStayName = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_STAY_NAME);
            mImageUrl = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_URL);

            String checkInDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKIN);
            String checkOutDateTime = intent.getStringExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHECKOUT);

            try
            {
                mStayBookDateTime = new StayBookDateTime();
                mStayBookDateTime.setCheckInDateTime(checkInDateTime);
                mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                return false;
            }

            mPersons.numberOfAdults = intent.getIntExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
            mPersons.setChildList(intent.getStringArrayListExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_CHILD_LIST));
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {
        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
//            initTransLayout(placeName, imageUrl, grade, isFromMap);
        } else
        {
            getViewInterface().setInitializedImage(mImageUrl);
        }

//        mPlaceDetailLayout.setStatusBarHeight(this);
//        mPlaceDetailLayout.setIsUsedMultiTransitions(mIsUsedMultiTransition);

//        setLockUICancelable(true);
        getViewInterface().setToolbarTitle(mStayName);

//        mOnEventListener.hideActionBar(false);

        if (mIsUsedMultiTransition == true)
        {
            addCompositeDisposable(mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPersons).subscribe(new Consumer<StayOutboundDetail>()
            {
                @Override
                public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
                {
                    if (stayOutboundDetail == null)
                    {
                        return;
                    }

                    onStayOutboundDetail(stayOutboundDetail);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {

                    onHandleError(throwable);
                }
            }));
        } else
        {
            addCompositeDisposable(Observable.zip(getViewInterface().getSharedElementTransition(), mStayOutboundRemoteImpl.getStayOutBoundDetail(mStayIndex, mStayBookDateTime, mPersons), new BiFunction<Boolean, StayOutboundDetail, StayOutboundDetail>()
            {
                @Override
                public StayOutboundDetail apply(Boolean aBoolean, StayOutboundDetail stayOutboundDetail) throws Exception
                {
                    return stayOutboundDetail;
                }
            }).subscribe(new Consumer<StayOutboundDetail>()
            {
                @Override
                public void accept(StayOutboundDetail stayOutboundDetail) throws Exception
                {
                    if (stayOutboundDetail == null)
                    {
                        return;
                    }

                    onStayOutboundDetail(stayOutboundDetail);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {

                    onHandleError(throwable);
                }
            }));
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public void onFinish()
    {
        super.onFinish();

        if (mIsUsedMultiTransition == false)
        {
            getActivity().overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
        }
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(true);


    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onImageClick()
    {

    }

    @Override
    public void onReviewClick()
    {

    }

    @Override
    public void onCalendarClick()
    {

    }

    @Override
    public void onDownloadCouponClick()
    {

    }

    @Override
    public void onMapClick()
    {

    }

    @Override
    public void onClipAddressClick(String address)
    {

    }

    @Override
    public void onNavigatorClick()
    {

    }

    @Override
    public void onWishClick()
    {

    }

    @Override
    public void onConciergeClick()
    {

    }

    @Override
    public void onBookingClick()
    {

    }

    @Override
    public void onHideRoomListClick(boolean animation)
    {

    }

    @Override
    public void onShowRoomListClick()
    {

    }

    private void onStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        if(stayOutboundDetail == null)
        {
            return;
        }

        if (mIsDeepLink == true)
        {
            // 딥링크로 진입한 경우에는 카테고리 코드를 알수가 없다. - 2017.04.28 알 수 없음으로 안보내기로 함 아이폰도 안보내고 있음.
            //            if (DailyTextUtils.isTextEmpty(stayDetailParams.category) == true)
            //            {
            //                stayDetailParams.category = stayDetailParams.getGrade().name();
            //            }

            getViewInterface().setToolbarTitle(stayOutboundDetail.name);
        }

        getViewInterface().setStayDetail(mStayBookDateTime, stayOutboundDetail);

//        if (mCheckPrice == false)
//        {
//            mCheckPrice = true;
//            checkStayRoom(mIsDeepLink, stayDetail, mViewPrice);
//        }
//
//        // 딥링크로 메뉴 오픈 요청
//        if (mIsDeepLink == true && mProductDetailIndex > 0 && stayDetail.getProductList().size() > 0)
//        {
//            if (mPlaceDetailLayout != null)
//            {
//                ((StayDetailLayout) mPlaceDetailLayout).showProductInformationLayout(mProductDetailIndex);
//                mPlaceDetailLayout.hideWishButton();
//            }
//        }

//            hideTrueViewMenu();
//
//            if (mIsShowVR == true)
//            {
//                unLockUI();
//                showSimpleDialog(null, getString(R.string.message_truevr_not_support_hardware), getString(R.string.dialog_btn_text_confirm), null);
//            }
//        }

//        mProductDetailIndex = 0;
        mIsDeepLink = false;
    }
}
