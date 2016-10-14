package com.twoheart.dailyhotel.screen.information.recentplace;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.GourmetSearchParams;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 12..
 */

public class RecentGourmetListFragment extends RecentPlacesListFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecentPlacesListLayout getListLayout()
    {
        return new RecentGourmetListLayout(mBaseActivity, mEventListener);
    }

    @Override
    protected BaseNetworkController getNetworkController()
    {
        return new RecentGourmetListNetworkController(mBaseActivity, mNetworkTag, mOnNetworkControllerListener);
    }

    @Override
    protected void requestRecentPlacesList()
    {
        lockUI();

        int count = mRecentPlaces != null ? mRecentPlaces.size() : 0;
        if (count == 0)
        {
            unLockUI();
            return;
        }

        // Test Code

        GourmetSearchCuration gourmetSearchCuration = new GourmetSearchCuration();
        gourmetSearchCuration.setKeyword(new Keyword(0, "서울"));
        gourmetSearchCuration.setSaleTime(mSaleTime);

        GourmetSearchParams params = (GourmetSearchParams) gourmetSearchCuration.toPlaceParams(1, count, true);

        // Test Code

        ((RecentGourmetListNetworkController) mNetworkController).requestRecentGourmetList(params);
    }

    private RecentGourmetListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentGourmetListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentGourmetList(ArrayList<Gourmet> list)
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

            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);

            Intent intent = GourmetDetailActivity.newInstance(mBaseActivity, //
                mSaleTime, gourmet, 0);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            if (position < 0 || mRecentPlaces.size() - 1 < position)
            {
                return;
            }

            mRecentPlaces.remove(position);

            Place place = mListLayout.removeItem(position);
            ExLog.d("isRemove : " + (place != null));

            if (place != null)
            {
                DailyPreference.getInstance(mBaseActivity).setGourmetRecentPlaces(mRecentPlaces.toString());
            }

            mListLayout.setData(mListLayout.getList());
            mRecentPlaceListFragmentListener.onDeleteItemClick(PlaceType.FNB, mRecentPlaces);
        }

        @Override
        public void finish()
        {
            finish();
        }
    };
}
