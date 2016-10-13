package com.twoheart.dailyhotel.screen.information.recentplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;
import com.twoheart.dailyhotel.widget.DailyToast;

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
        ((RecentGourmetListNetworkController) mNetworkController).requestRecentGourmetList();
        DailyToast.showToast(mBaseActivity, "recent gourmet", Toast.LENGTH_SHORT);
    }

    private RecentGourmetListNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new RecentGourmetListNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onRecentGourmetList()
        {
            unLockUI();
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
            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);
        }

        @Override
        public void onListItemDeleteClick(int position)
        {
            Gourmet gourmet = (Gourmet) mListLayout.getItem(position);
        }

        @Override
        public void finish()
        {
            finish();
        }
    };
}
