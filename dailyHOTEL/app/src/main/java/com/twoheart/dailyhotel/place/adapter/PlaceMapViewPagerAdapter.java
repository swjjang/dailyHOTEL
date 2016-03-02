package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.model.PlaceViewItem;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceMapViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected ArrayList<PlaceViewItem> mPlaceViewItemList;

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
}
