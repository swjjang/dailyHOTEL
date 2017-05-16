package com.daily.dailyhotel.screen.stay.outbound.detail;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundDetail;
import com.daily.dailyhotel.entity.StayOutboundRoom;
import com.daily.dailyhotel.repository.remote.StayOutboundRemoteImpl;
import com.twoheart.dailyhotel.R;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundDetailPresenter extends BaseExceptionPresenter<StayOutboundDetailActivity, StayOutboundDetailViewInterface> implements StayOutboundDetailView.OnEventListener
{
    public static final int STATUS_NONE = 0;
    public static final int STATUS_ROOM_LIST = 1;
    public static final int STATUS_BOOKING = 2;
    public static final int STATUS_SOLD_OUT = 3;

    public static final int PRICE_AVERAGE = 0;
    public static final int PRICE_TOTAL = 1;

    private StayOutboundDetailAnalyticsInterface mAnalytics;

    private StayOutboundRemoteImpl mStayOutboundRemoteImpl;

    private int mStayIndex;
    private String mStayName;
    private String mImageUrl;
    private StayBookDateTime mStayBookDateTime;
    private StayOutboundDetail mStayOutboundDetail;
    private Persons mPersons;

    private int mStatus = STATUS_NONE;
    private int mViewPriceType = PRICE_AVERAGE;

    private boolean mIsUsedMultiTransition;
    private boolean mIsDeepLink;
    private boolean mCheckChangedPrice;

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
    public void constructorInitialize(StayOutboundDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_detail_data);

        setAnalytics(new StayStayOutboundDetailAnalyticsImpl());

        mStayOutboundRemoteImpl = new StayOutboundRemoteImpl(activity);

        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);

        setStatus(STATUS_NONE);

        mViewPriceType = PRICE_AVERAGE;

        Observable<Boolean> observable = getViewInterface().hideRoomList(false);

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe());
        }
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
            mIsDeepLink = true;
        } else
        {
            mIsUsedMultiTransition = intent.getBooleanExtra(StayOutboundDetailActivity.INTENT_EXTRA_DATA_MULTITRANSITION, false);
            mIsDeepLink = false;

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
    public void onPostCreate()
    {
        if (mIsDeepLink == false && mIsUsedMultiTransition == true)
        {
            //            initTransLayout(placeName, imageUrl, grade, isFromMap);
        } else
        {
            getViewInterface().setInitializedImage(mImageUrl);
        }

        //        setLockUICancelable(true);
        getViewInterface().setToolbarTitle(mStayName);

        //        mOnEventListener.hideActionBar(false);

        if (mIsUsedMultiTransition == true)
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
        } else
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
        switch (mStatus)
        {
            case STATUS_BOOKING:
                onHideRoomListClick(true);
                return true;
        }

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
    public void onShareClick()
    {

    }

    @Override
    public void onImageClick(int position)
    {

    }

    @Override
    public void onImageSelected(int position)
    {
        if (mStayOutboundDetail == null)
        {
            return;
        }

        getViewInterface().setDetailImageCaption(mStayOutboundDetail.getImageList().get(position).caption);
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
        Observable<Boolean> observable = getViewInterface().hideRoomList(animation);

        if (observable != null)
        {
            screenLock(false);

            addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    unLockAll();

                    setStatus(STATUS_ROOM_LIST);
                }
            }));
        }
    }

    @Override
    public void onActionButtonClick()
    {
        switch (mStatus)
        {
            case STATUS_BOOKING:
                break;

            case STATUS_ROOM_LIST:
                screenLock(false);

                Observable<Boolean> observable = getViewInterface().showRoomList(true);

                if (observable != null)
                {
                    addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
                    {
                        @Override
                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                        {
                            unLockAll();

                            setStatus(STATUS_BOOKING);
                        }
                    }));
                }
                break;

            default:
                break;
        }
    }

    private void onStayOutboundDetail(StayOutboundDetail stayOutboundDetail)
    {
        if (stayOutboundDetail == null)
        {
            return;
        }

        mStayOutboundDetail = stayOutboundDetail;

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

        if (mCheckChangedPrice == false)
        {
            mCheckChangedPrice = true;
            checkChangedPrice(mIsDeepLink, stayOutboundDetail, null);
        }


        setStatus(STATUS_ROOM_LIST);

        //        mProductDetailIndex = 0;
        mIsDeepLink = false;
    }

    private void setStatus(int status)
    {
        mStatus = status;

        getViewInterface().setBottomButtonLayout(status);
    }

    private void checkChangedPrice(boolean isDeepLink, StayOutboundDetail stayOutboundDetail, String listViewPrice)
    {
        if (stayOutboundDetail == null || DailyTextUtils.isTextEmpty(listViewPrice) == true)
        {
            return;
        }

        // 판매 완료 혹은 가격이 변동되었는지 조사한다
        List<StayOutboundRoom> roomList = stayOutboundDetail.getRoomList();

        if (roomList == null || roomList.size() == 0)
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        //                        setResultCode(CODE_RESULT_ACTIVITY_REFRESH);
                    }
                });
        } else
        {
            if (isDeepLink == false)
            {
                boolean hasPrice = false;

                for (StayOutboundRoom room : roomList)
                {
                    if (listViewPrice == room.nightlyKrw)
                    {
                        hasPrice = true;
                        break;
                    }
                }

                if (hasPrice == false)
                {
                    //                    setResultCode(CODE_RESULT_ACTIVITY_REFRESH);

                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_changed_price)//
                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                Observable<Boolean> observable = getViewInterface().showRoomList(false);

                                if (observable != null)
                                {
                                    screenLock(false);

                                    addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
                                    {
                                        @Override
                                        public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                                        {
                                            unLockAll();
                                        }
                                    }));
                                }
                            }
                        });
                }
            }
        }
    }
}
