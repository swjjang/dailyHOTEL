package com.daily.dailyhotel.screen.mydaily.coupon.select.stay.outbound;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SelectStayOutboundCouponDialogPresenter extends BaseExceptionPresenter<SelectStayOutboundCouponDialogActivity, SelectStayOutboundCouponDialogInterface.ViewInterface> implements SelectStayOutboundCouponDialogInterface.OnEventListener
{
    private SelectStayOutboundCouponDialogInterface.AnalyticsInterface mAnalytics;

    CouponRemoteImpl mCouponRemoteImpl;

    int mStayIndex;
    int mStayName;
    StayBookDateTime mStayBookDateTime;
    String m

    public SelectStayOutboundCouponDialogPresenter(@NonNull SelectStayOutboundCouponDialogActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SelectStayOutboundCouponDialogInterface.ViewInterface createInstanceViewInterface()
    {
        return new SelectStayOutboundCouponDialogView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SelectStayOutboundCouponDialogActivity activity)
    {
        setContentView(R.layout.activity_select_coupon_dialog_data);

        setAnalytics(new SelectStayOutboundCouponDialogAnalyticsImpl());

        mCouponRemoteImpl = new CouponRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SelectStayOutboundCouponDialogInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, checkInDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, checkOutDateTime);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_INDEX, stayIndex);
        intent.putExtra(INTENT_EXTRA_DATA_STAY_NAME, stayName);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_CODE, rateCode);
        intent.putExtra(INTENT_EXTRA_DATA_RATE_KEY, rateKey);
        intent.putExtra(INTENT_EXTRA_DATA_ROOM_TYPE_CODE, roomTypeCode);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
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

        addCompositeDisposable(mCouponRemoteImpl.getStayOutboundCouponListByPayment(m));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

}
