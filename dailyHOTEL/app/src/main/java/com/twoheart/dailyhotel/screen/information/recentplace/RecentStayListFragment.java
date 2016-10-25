package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.RecentStayParams;
import com.twoheart.dailyhotel.model.Stay;
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

        int count = mRecentPlaces != null ? mRecentPlaces.size() : 0;
        if (count == 0)
        {
            unLockUI();

            if (mListLayout != null && isFinishing() == false)
            {
                mListLayout.setData(null);
            }
            return;
        }

        RecentStayParams recentStayParams = new RecentStayParams();
        recentStayParams.setCheckInTime(mSaleTime);
        recentStayParams.setTargetIndices(mRecentPlaces.toString());

        ((RecentStayListNetworkController) mNetworkController).requestRecentStayList(recentStayParams);
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
                ExLog.d("position Error Stay");
                return;
            }

            mRecentPlaces.remove(position);

            Place place = mListLayout.removeItem(position);
            ExLog.d("isRemove : " + (place != null));

            if (place != null)
            {
                DailyPreference.getInstance(mBaseActivity).setStayRecentPlaces(mRecentPlaces.toString());
            }

            mListLayout.setData(mListLayout.getList());
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
