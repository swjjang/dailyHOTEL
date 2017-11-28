package com.daily.dailyhotel.screen.booking.detail.gourmet;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;

import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetBookingDetailPresenter extends BaseExceptionPresenter<GourmetBookingDetailActivity, GourmetBookingDetailInterface> implements GourmetBookingDetailView.OnEventListener
{
    private GourmetBookingDetailAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    private int mReservationIndex;
    private String mAggregationId;
    private String mImageUrl;
    private boolean mIsDeepLink;
    private int mBookingState;
    private CommonDateTime mCommonDateTime;
    private User mUser;
    private GourmetBookingDetail mGourmetBookingDetail;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface GourmetBookingDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventPaymentState(Activity activity, int gourmetIndex, int bookingState);
    }

    public GourmetBookingDetailPresenter(@NonNull GourmetBookingDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetBookingDetailInterface createInstanceViewInterface()
    {
        return new GourmetBookingDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetBookingDetailActivity activity)
    {
        setContentView(R.layout.activity_gourmet_booking_detail_data);

        setAnalytics(new GourmetBookingDetailAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetBookingDetailAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        Bundle bundle = intent.getExtras();

        if (bundle != null)
        {
            mReservationIndex = bundle.getInt(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_BOOKINGIDX);
            mAggregationId = bundle.getString(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_AGGREGATION_ID);
            mImageUrl = bundle.getString(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_URL);
            mIsDeepLink = bundle.getBoolean(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_DEEPLINK, false);
            mBookingState = bundle.getInt(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_BOOKING_STATE);
        }

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return true;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            startLogin();
            return;
        }

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // TODO : 매번 리로딩 해야 하는 지 확인 필요.
        //        setRefresh(true);

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        if (Util.supportPreview(getActivity()) == true)
        {
            if (getViewInterface().isBlurVisible() == true)
            {
                getViewInterface().setBlurVisible(getActivity(), false);
            }
        }
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (mGourmetBookingDetail != null && getViewInterface().isExpandedMap() == true)
        {
            onCollapseMapClick();
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

        Util.restartApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case GourmetBookingDetailActivity.REQUEST_CODE_DETAIL:
            {
                setResult(resultCode);

                if (resultCode == Activity.RESULT_OK //
                    || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY //
                    || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_LOGIN:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);
                } else
                {
                    finish();
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION:
            {
                onMyLocationClick();
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    onMyLocationClick();
                } else if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_ZOOMMAP:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;

            case GourmetBookingDetailActivity.REQUEST_CODE_REVIEW:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    mGourmetBookingDetail.reviewStatusType = PlaceBookingDetail.ReviewStatusType.COMPLETE;
                    getViewInterface().setReviewButtonLayout(mGourmetBookingDetail.reviewStatusType);
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_FAQ:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                    finish();
                }
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        Observable<GourmetBookingDetail> detailObservable = Observable.defer(new Callable<ObservableSource<GourmetBookingDetail>>()
        {
            @Override
            public ObservableSource<GourmetBookingDetail> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                {
                    return mBookingRemoteImpl.getGourmetBookingDetail(mReservationIndex);
                }

                return mBookingRemoteImpl.getGourmetBookingDetail(mAggregationId);
            }
        });

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime() //
            , mProfileRemoteImpl.getProfile(), detailObservable //
            , new Function3<CommonDateTime, User, GourmetBookingDetail, GourmetBookingDetail>()
            {
                @Override
                public GourmetBookingDetail apply(CommonDateTime commonDateTime, User user, GourmetBookingDetail gourmetBookingDetail) throws Exception
                {
                    setCommonDateTime(commonDateTime);
                    setUser(user);
                    setGourmetBookingDetail(gourmetBookingDetail);
                    return null;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetBookingDetail>()
        {
            @Override
            public void accept(GourmetBookingDetail gourmetBookingDetail) throws Exception
            {
                notifyGourmetBookingDetail();
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));
    }

    private void setCommonDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void setUser(User user)
    {
        mUser = user;
    }

    private void setGourmetBookingDetail(GourmetBookingDetail gourmetBookingDetail)
    {
        mGourmetBookingDetail = gourmetBookingDetail;
    }

    private void notifyGourmetBookingDetail()
    {
        if (mGourmetBookingDetail == null || mCommonDateTime == null)
        {
            return;
        }

        try
        {
            String ticketDateFormat = DailyCalendar.convertDateFormatString(mGourmetBookingDetail.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH:mm");

            // TODO : 임시 방문인원 - 서버 연결 시 추가 작업 예정
            int randPersons = new Random(10).nextInt();
            getViewInterface().setBookingDateAndPersons(ticketDateFormat, randPersons);

            getViewInterface().setBookingDetail(mGourmetBookingDetail);
            getViewInterface().setHiddenBookingVisible(mBookingState);
            getViewInterface().setRemindDate(mCommonDateTime.currentDateTime, mGourmetBookingDetail.arrivalDateTime);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
            Crashlytics.logException(e);
            finish();
            return;
        }

       mAnalytics.onEventPaymentState(getActivity(), mGourmetBookingDetail.gourmetIndex, mBookingState);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void startLogin()
    {
        getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_detail_do_login) //
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = LoginActivity.newInstance(getActivity());
                    startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_LOGIN);
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onIssuingReceiptClick()
    {

    }

    @Override
    public void onShareClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showShareDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });

        mAnalytics.onEventShareClick(getActivity());
    }

    @Override
    public void onMapLoading()
    {

    }

    @Override
    public void onMapClick()
    {

    }

    @Override
    public void onExpandMapClick()
    {

    }

    @Override
    public void onCollapseMapClick()
    {

    }

    @Override
    public void onViewDetailClick()
    {

    }

    @Override
    public void onNavigatorClick()
    {

    }

    @Override
    public void onClipAddressClick()
    {

    }

    @Override
    public void onMyLocationClick()
    {

    }

    @Override
    public void onConciergeClick()
    {

    }

    @Override
    public void onConciergeFaqClick()
    {

    }

    @Override
    public void onRestaurantCallClick(String restaurantPhone)
    {

    }

    @Override
    public void onConciergeHappyTalkClick()
    {

    }

    @Override
    public void onConciergeCallClick()
    {

    }

    @Override
    public void onShareKakaoClick()
    {

    }

    @Override
    public void onMoreShareClick()
    {

    }

    @Override
    public void onHiddenReservationClick()
    {

    }

    @Override
    public void onReviewClick(String reviewStatus)
    {

    }
}
