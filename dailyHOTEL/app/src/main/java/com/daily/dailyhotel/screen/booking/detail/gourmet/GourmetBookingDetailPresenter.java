package com.daily.dailyhotel.screen.booking.detail.gourmet;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetBookingDetailPresenter extends BaseExceptionPresenter<GourmetBookingDetailActivity, GourmetBookingDetailInterface> implements GourmetBookingDetailView.OnEventListener
{
    private CopyAnalyticsInterface mAnalytics;

    protected int mReservationIndex;
    protected String mAggregationId;
    protected String mImageUrl;
    protected boolean mIsDeepLink;
    protected int mBookingState;

    public interface CopyAnalyticsInterface extends BaseAnalyticsInterface
    {
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
        mAnalytics = (CopyAnalyticsInterface) analytics;
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

            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                if (mPlaceReservationDetailLayout != null)
                {
                    searchMyLocation(mPlaceReservationDetailLayout.getMyLocationView());
                }

                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == RESULT_OK)
                {
                    if (mPlaceReservationDetailLayout != null)
                    {
                        searchMyLocation(mPlaceReservationDetailLayout.getMyLocationView());
                    }
                } else if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_ZOOMMAP:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;

            case CODE_REQUEST_ACTIVITY_SATISFACTION_HOTEL:
            case CODE_REQUEST_ACTIVITY_SATISFACTION_GOURMET:
            {
                if (resultCode == RESULT_OK)
                {
                    mPlaceBookingDetail.reviewStatusType = PlaceBookingDetail.ReviewStatusType.COMPLETE;
                    mPlaceReservationDetailLayout.updateReviewButtonLayout(mPlaceBookingDetail.reviewStatusType);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_FAQ:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(CODE_RESULT_ACTIVITY_GO_HOME);
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
