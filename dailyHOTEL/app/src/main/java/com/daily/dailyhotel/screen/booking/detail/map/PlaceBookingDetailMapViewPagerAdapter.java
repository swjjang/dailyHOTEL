package com.daily.dailyhotel.screen.booking.detail.map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.model.Place;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceBookingDetailMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected List<Place> mPlaceList;
    protected OnPlaceMapViewPagerAdapterListener mOnPlaceMapViewPagerAdapterListener;

    public interface OnPlaceMapViewPagerAdapterListener
    {
        void onPlaceClick(View view, Place place);

        void onCloseClick();
    }

    public PlaceBookingDetailMapViewPagerAdapter(Context context)
    {
        mContext = context;
        mPlaceList = new ArrayList<>();
    }

    @Override
    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    @Override
    public int getCount()
    {
        if (mPlaceList != null)
        {
            return mPlaceList.size();
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

    public void setData(List<Place> list)
    {
        if (mPlaceList == null)
        {
            mPlaceList = new ArrayList<>();
        }

        mPlaceList.clear();

        if (list != null)
        {
            mPlaceList.addAll(list);
        }
    }

    public Place getItem(int position)
    {
        if (mPlaceList == null || mPlaceList.size() == 0 || mPlaceList.size() <= position)
        {
            return null;
        }

        return mPlaceList.get(position);
    }

    public void clear()
    {
        if (mPlaceList == null)
        {
            return;
        }

        mPlaceList.clear();
    }

    public void setOnPlaceMapViewPagerAdapterListener(OnPlaceMapViewPagerAdapterListener listener)
    {
        mOnPlaceMapViewPagerAdapterListener = listener;
    }
}
