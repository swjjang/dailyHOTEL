package com.twoheart.dailyhotel.screen.information.recentplace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.RecentPlaces;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public abstract class RecentPlacesListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected RecentPlacesListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected SaleTime mSaleTime;
    protected RecentPlaces mRecentPlaces;
    protected OnRecentPlaceListFragmentListener mRecentPlaceListFragmentListener;

    protected abstract RecentPlacesListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestRecentPlacesList();

    public interface OnRecentPlaceListFragmentListener
    {
        void onDeleteItemClick(PlaceType placeType, RecentPlaces recentPlaces);
    }

    public void setRecentPlaceListFragmentListener(OnRecentPlaceListFragmentListener listener)
    {
        mRecentPlaceListFragmentListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mListLayout = getListLayout();
        mNetworkController = getNetworkController();

        return mListLayout.onCreateView(R.layout.fragment_recent_places_list, container);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        requestRecentPlacesList();
    }

    public void setSaleTime(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            return;
        }

        mSaleTime = saleTime;
    }

    public void setRecentPlaces(RecentPlaces recentPlaces)
    {
        mRecentPlaces = recentPlaces;
    }
}
