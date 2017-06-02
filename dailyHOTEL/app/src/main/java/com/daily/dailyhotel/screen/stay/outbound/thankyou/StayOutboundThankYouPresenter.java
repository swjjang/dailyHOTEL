package com.daily.dailyhotel.screen.stay.outbound.thankyou;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundThankYouPresenter extends BaseExceptionPresenter<StayOutboundThankYouActivity, StayOutboundThankYouInterface> implements StayOutboundThankYouView.OnEventListener
{
    private StayOutboundThankYouAnalyticsInterface mAnalytics;

    private int mStayIndex;
    private String mStayName;
    private String mImageUrl;
    private int mRoomPrice;
    private StayBookDateTime mStayBookDateTime;
    private String mCheckInTime;
    private String mCheckOutTime;
    private People mPeople;
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
        mCheckInTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECKIN);
        mCheckOutTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECKOUT);

        String checkInDateTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECKIN);
        String checkOutDateTime = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHECKOUT);

        setStayBookDateTime(checkInDateTime, checkOutDateTime);

        int numberOfAdults = intent.getIntExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        mRoomType = intent.getStringExtra(StayOutboundThankYouActivity.INTENT_EXTRA_DATA_ROOM_TYPE);

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

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mPeople == null)
        {
            mPeople = new People(People.DEFAULT_ADULTS, null);
        }

        mPeople.numberOfAdults = numberOfAdults;
        mPeople.setChildAgeList(childAgeList);
    }
}
