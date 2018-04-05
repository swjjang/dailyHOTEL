package com.daily.dailyhotel.screen.home.stay.outbound.detail.amenities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class AmenityListPresenter extends BaseExceptionPresenter<AmenityListActivity, AmenityListInterface> implements AmenityListView.OnEventListener
{
    private ArrayList<String> mAmenityList;

    public AmenityListPresenter(@NonNull AmenityListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected AmenityListInterface createInstanceViewInterface()
    {
        return new AmenityListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(AmenityListActivity activity)
    {
        setContentView(R.layout.activity_amenity_list_data);

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        ArrayList<String> amenityList = intent.getStringArrayListExtra(AmenityListActivity.INTENT_EXTRA_DATA_AMENITY_LIST);

        if (amenityList == null || amenityList.size() == 0)
        {
            return false;
        }

        setAmenityList(amenityList);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_stay_outbound_amenities));

        getViewInterface().setAmenityList(mAmenityList);
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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void setAmenityList(ArrayList<String> amenityList)
    {
        if (amenityList == null || amenityList.size() == 0)
        {
            return;
        }

        if (mAmenityList == null)
        {
            mAmenityList = new ArrayList<>();
        }

        mAmenityList.clear();
        mAmenityList.addAll(amenityList);
    }
}
