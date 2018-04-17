package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected ArrayList<PlaceViewItem> mPlaceViewItemList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    protected boolean mRewardEnabled;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onInformationClick(View view, Place place);

        void onWishClick(int position, Place place);

        void onCloseClick();
    }

    public abstract void setPlaceBookingDay(PlaceBookingDay placeBookingDay);

    public PlaceMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mPlaceViewItemList = new ArrayList<>();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mPlaceViewItemList != null)
        {
            return mPlaceViewItemList.size();
        } else
        {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View) object);
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public void setData(List<PlaceViewItem> list)
    {
        if (mPlaceViewItemList == null)
        {
            mPlaceViewItemList = new ArrayList<>();
        }

        mPlaceViewItemList.clear();

        if (list != null)
        {
            mPlaceViewItemList.addAll(list);
        }
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}
