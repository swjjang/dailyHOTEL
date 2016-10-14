package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StaySearchCuration;
import com.twoheart.dailyhotel.model.StaySearchParams;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentStayListFragment extends RecentPlacesListFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentStayListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new RecentStayListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void requestRecentPlacesList()
    {
        lockUI();

        int nights = 1;
        int count = mRecentPlaces != null ? mRecentPlaces.size() : 0;
        if (count == 0)
        {
            unLockUI();
            return;
        }

        // Test Code!
        StaySearchCuration staySearchCuration = new StaySearchCuration();
        staySearchCuration.setKeyword(new Keyword(0, "서울"));
        staySearchCuration.setCheckInSaleTime(mSaleTime);
        staySearchCuration.setCheckOutSaleTime(mSaleTime.getClone(mSaleTime.getOffsetDailyDay() + nights));

        StaySearchParams staySearchParams = new StaySearchParams(staySearchCuration);
        staySearchParams.setPageInformation(1, count, true);

        // Test Code!

        ((RecentStayListNetworkController) mNetworkController).requestRecentStayList(staySearchParams);
//        DailyToast.showToast(mBaseActivity, "recent Stay", Toast.LENGTH_SHORT);
    }

    private RecentStayListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentStayListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentStayList(ArrayList<Stay> list)
        {
            unLockUI();

            if (isFinishing() == true)
            {
                return;
            }

            mListLayout.setData(list);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            unLockUI();
            mBaseActivity.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            unLockUI();
            mBaseActivity.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            unLockUI();
            mBaseActivity.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            unLockUI();
            mBaseActivity.onErrorToastMessage(message);
        }
    };

    RecentPlacesListLayout.OnEventListener mEventListener = new RecentPlacesListLayout.OnEventListener()
    {
        @Override
        public void onListItemClick(int position)
        {
            if (position < 0 || mRecentPlaces.size() - 1 < position)
            {
                return;
            }

            Stay stay = (Stay) mListLayout.getItem(position);

            Intent intent = StayDetailActivity.newInstance(mBaseActivity, //
                mSaleTime, stay, 0);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mRecentPlaces.size() - 1 < position)
            {
                return;
            }

            Stay stay = (Stay) mListLayout.getItem(position);
            mRecentPlaces.remove(stay.index);

            boolean isRemove = mListLayout.removeItem(stay);
            ExLog.d("isRemove : " + isRemove);

            if (isRemove == true)
            {
                DailyPreference.getInstance(mBaseActivity).setStayRecentPlaces(mRecentPlaces.toString());
            }

            mListLayout.setData(mListLayout.getList());
            mListLayout.notifyDataSetChanged();

            mRecentPlaceListFragmentListener.onDeleteItemClick(PlaceType.HOTEL, mRecentPlaces);
        }

        @Override
        public void finish()
        {
            unLockUI();
            finish();
        }
    };
}
