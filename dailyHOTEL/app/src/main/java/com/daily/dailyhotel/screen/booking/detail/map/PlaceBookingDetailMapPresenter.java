package com.daily.dailyhotel.screen.booking.detail.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.List;

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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }

        // TODO : Analytics
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
            // TODO : 추후 Request code Constants 에서 Activity 의 리퀘스트코드로 변경, StayOutboundListActivity 참조
            case Constants.CODE_RESULT_ACTIVITY_SETTING_LOCATION:
            {
                onMyLocationClick();
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    onMyLocationClick();
                }
                break;
            }
        }
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

        // TODO : 리스트 노출

        ArrayList<PlaceViewItem> placeViewItemList = makePlaceViewItemList(mPlaceList);

        getViewInterface().setPlaceList(getActivity().getSupportFragmentManager(), placeViewItemList, mPlaceBookingDay);
    }


    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onStayClick(View view, PlaceViewItem placeViewItem)
    {

    }

    @Override
    public void onMapReady()
    {

    }

    @Override
    public void onMarkerClick(Place place)
    {

    }

    @Override
    public void onMarkersCompleted()
    {

    }

    @Override
    public void onMapClick()
    {

    }

    @Override
    public void onMyLocationClick()
    {

    }

    private ArrayList<PlaceViewItem> makePlaceViewItemList(List<? extends Place> placeList)
    {
        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();
        if (placeList == null || placeList.size() == 0)
        {
            return placeViewItemList;
        }

        int entryPosition = 1;

        for (Place place : placeList             )
        {
            place.entryPosition = entryPosition;
            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
            entryPosition++;
        }

        return placeViewItemList;
    }
}
