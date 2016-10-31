package com.twoheart.dailyhotel.screen.information.wishlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.place.base.BaseNetworkController;

/**
 * Created by android_sam on 2016. 10. 10..
 */

public abstract class PlaceWishListFragment extends BaseFragment
{
    private PlaceType mPlaceType;

    protected BaseActivity mBaseActivity;
    protected PlaceWishListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected SaleTime mSaleTime;

    protected OnWishListFragmentListener mWishListFragmentListener;

    protected abstract PlaceWishListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestWishList();

    public interface OnWishListFragmentListener
    {
        void onDeleteItemClick(PlaceType placeType, int position);
    }

    public void setWishListListFragmentListener(OnWishListFragmentListener listener)
    {
        mWishListFragmentListener = listener;
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

        requestWishList();
    }

    public void setSaleTime(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            return;
        }

        mSaleTime = saleTime;
    }

    public void setPlaceType(PlaceType placeType)
    {
        mPlaceType = placeType;
    }

    public PlaceType getPlaceType()
    {
        return mPlaceType;
    }
}
