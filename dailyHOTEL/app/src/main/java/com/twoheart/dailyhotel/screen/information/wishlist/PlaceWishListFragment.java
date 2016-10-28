package com.twoheart.dailyhotel.screen.information.wishlist;

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

public abstract class PlaceWishListFragment extends BaseFragment
{
    protected BaseActivity mBaseActivity;
    protected PlaceWishListLayout mListLayout;
    protected BaseNetworkController mNetworkController;

    protected SaleTime mSaleTime;

    /**
     * 해당 데이터는 리퀘스트 및 저장 용도로만 사용해야 합니다. emptyList 의 판단은 listAdapter의 갯수 또는 서버 전달 데이터 갯수로 판단해야 합니다.
     */
    protected RecentPlaces mWishList;
    protected OnRecentPlaceListFragmentListener mWishListListFragmentListener;

    protected abstract PlaceWishListLayout getListLayout();

    protected abstract BaseNetworkController getNetworkController();

    protected abstract void requestWishListList();

    public interface OnRecentPlaceListFragmentListener
    {
        void onDeleteItemClick(PlaceType placeType, RecentPlaces recentPlaces);
    }

    public void setWishListListFragmentListener(OnRecentPlaceListFragmentListener listener)
    {
        mWishListListFragmentListener = listener;
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

        requestWishListList();
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
