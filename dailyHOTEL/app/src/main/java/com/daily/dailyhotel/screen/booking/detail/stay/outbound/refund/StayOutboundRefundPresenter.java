package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundRefundPresenter extends BaseExceptionPresenter<StayOutboundRefundActivity, StayOutboundRefundInterface> implements StayOutboundRefundView.OnEventListener
{
    private StayOutboundRefundAnalyticsInterface mAnalytics;

    private RefundRemoteImpl mRefundRemoteImpl;

    private int mBookingIndex;
    private StayOutboundRefundDetail mStayOutboundRefundDetail;

    public interface StayOutboundRefundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundRefundPresenter(@NonNull StayOutboundRefundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundRefundInterface createInstanceViewInterface()
    {
        return new StayOutboundRefundView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundRefundActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_refund_data);

        setAnalytics(new StayOutboundRefundAnalyticsImpl());

        mRefundRemoteImpl = new RefundRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundRefundAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mBookingIndex = intent.getIntExtra(StayOutboundRefundActivity.INTENT_EXTRA_DATA_BOOKING_INDEX, -1);

        if (mBookingIndex < 0)
        {
            return false;
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

        if (isRefresh() == true)
        {
            onRefresh(true);
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
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(mRefundRemoteImpl.getStayOutboundRefundDetail(mBookingIndex).subscribe(new Consumer<StayOutboundRefundDetail>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundRefundDetail stayOutboundRefundDetail) throws Exception
            {
                setStayOutboundRefundDetail(stayOutboundRefundDetail);
                notifyStayOutboundBookingDetailChanged();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onRefundClick()
    {

    }

    private void setStayOutboundRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        mStayOutboundRefundDetail = stayOutboundRefundDetail;
    }

    private void notifyStayOutboundBookingDetailChanged()
    {
        if (mStayOutboundRefundDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, mStayOutboundRefundDetail.checkInTime.split(":")[0]);
            String checkInDate = DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkInDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, mStayOutboundRefundDetail.checkOutTime.split(":")[0]);
            String checkOutDate = DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkOutDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int nights = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkOutDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                , DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkInDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));
            getViewInterface().setBookingDate(checkInDateSpannableString, checkOutDateSpannableString, nights);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setRefundDetail(mStayOutboundRefundDetail);
    }
}
