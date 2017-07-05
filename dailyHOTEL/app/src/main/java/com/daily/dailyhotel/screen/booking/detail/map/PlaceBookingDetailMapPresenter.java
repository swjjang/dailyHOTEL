package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class PlaceBookingDetailMapPresenter extends BaseExceptionPresenter<PlaceBookingDetailMapActivity, PlaceBookingDetailMapInterface> //
    implements PlaceBookingDetailMapView.OnEventListener
{
    private String mTitle;
    private PlaceBookingDay mPlaceBookingDay;
    private ArrayList<? extends Place> mPlaceList;
    private DailyLocationExFactory mDailyLocationExFactory;

    public PlaceBookingDetailMapPresenter(@NonNull PlaceBookingDetailMapActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected PlaceBookingDetailMapInterface createInstanceViewInterface()
    {
        return new PlaceBookingDetailMapView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(PlaceBookingDetailMapActivity activity)
    {
        setContentView(R.layout.activity_place_booking_detail_map_data);

//        setAnalytics(new StayOutboundListAnalyticsImpl()); // TODO : Analytics

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        // TODO : Analytics
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        try
        {
            mTitle = intent.getStringExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_TITLE);
            mPlaceBookingDay = intent.getParcelableExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
            mPlaceList = intent.getParcelableArrayListExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_PLACE_LIST);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(DailyTextUtils.isTextEmpty(mTitle) == true //
            ? getActivity().getResources().getString(R.string.label_home_view_all) : mTitle);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // TODO : Analytics 체크
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    protected void onRefresh(boolean showProgress)
    {

    }

    @Override
    public void onBackClick()
    {

    }
}
