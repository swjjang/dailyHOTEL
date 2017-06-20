package com.daily.dailyhotel.screen.home.stay.outbound.thankyou;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundThankYouPresenter extends BaseExceptionPresenter<StayOutboundThankYouActivity, StayOutboundThankYouInterface> implements StayOutboundThankYouView.OnEventListener
{
    private StayOutboundThankYouAnalyticsInterface mAnalytics;

    private int mStayIndex, mReservationId;
    private String mStayName;
    private String mImageUrl;
    private int mRoomPrice;
    private StayBookDateTime mStayBookDateTime;
    private String mCheckInTime;
    private String mCheckOutTime;
    private String mRoomType;

    public interface StayOutboundThankYouAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundThankYouPresenter(@NonNull StayOutboundThankYouActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundThankYouInterface createInstanceViewInterface()
    {
        return new StayOutboundThankYouView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundThankYouActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_payment_thank_you_data);

        setAnalytics(new StayOutboundThankYouAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundThankYouAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mStayIndex = intent.getIntExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_STAY_INDEX, -1);
        mStayName = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_STAY_NAME);
        mImageUrl = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_IMAGE_URL);
        mRoomPrice = intent.getIntExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_ROOM_PRICE, -1);
        mCheckInTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECK_IN_TIME);
        mCheckOutTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECK_OUT_TIME);

        String checkInDateTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECK_IN);
        String checkOutDateTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECK_OUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        mRoomType = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_ROOM_TYPE);
        mReservationId = intent.getIntExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_RESERVATION_ID, -1);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setImageUrl(mImageUrl);

        String name = DailyUserPreference.getInstance(getActivity()).getName();
        getViewInterface().setUserName(name);

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, mCheckInTime.split(":")[0]);
            String checkInDate = mStayBookDateTime.getCheckInDateTime(DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, mCheckOutTime.split(":")[0]);
            String checkOutDate = mStayBookDateTime.getCheckOutDateTime(DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            getViewInterface().setBooking(checkInDateSpannableString, checkOutDateSpannableString, mStayBookDateTime.getNights(), mStayName, mRoomType);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().startAnimation();
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
        startActivity(DailyInternalDeepLink.getStayOutboundBookingDetailScreenLink(getActivity(), mReservationId));

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

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }
}
