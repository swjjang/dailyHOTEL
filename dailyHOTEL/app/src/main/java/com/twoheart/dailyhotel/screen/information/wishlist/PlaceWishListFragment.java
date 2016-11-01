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
 * Created by android_sam on 2016. 11. 1..
 */

public abstract class PlaceWishListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected PlaceWishListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected SaleTime mSaleTime;

    protected OnWishListFragmentListener mWishListFragmentListener;

    protected abstract PlaceWishListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestWishList();

    protected abstract void requestDeleteWishListItem();

    public interface OnWishListFragmentListener
    {
        void onDeleteItemClick(PlaceType placeType, int position);
    }

    public void setWishListFragmentListener(OnWishListFragmentListener listener)
    {
        mWishListFragmentListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mBaseActivity = (BaseActivity) getActivity();
        mListLayout = getListLayout();
        mNetworkController = getNetworkController();

        return mListLayout.onCreateView(R.layout.fragment_wishlist_list, container);
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
}
