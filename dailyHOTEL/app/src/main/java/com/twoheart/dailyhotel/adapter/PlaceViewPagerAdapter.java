package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.PlaceMapFragment;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.view.PlaceViewItem;

import java.util.ArrayList;

public abstract class PlaceViewPagerAdapter extends PagerAdapter
{
    protected Context mContext;
    protected PlaceMapFragment.OnUserActionListener mOnUserActionListener;
    private ArrayList<PlaceViewItem> mPlaceViewItemList;

    public PlaceViewPagerAdapter(Context context)
    {
        mContext = context;

        mPlaceViewItemList = new ArrayList<PlaceViewItem>();
    }

    protected abstract void makeLayout(View view, final Place place);

    public void setOnUserActionListener(PlaceMapFragment.OnUserActionListener listener)
    {
        mOnUserActionListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mPlaceViewItemList == null || mPlaceViewItemList.size() < position)
        {
            return null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.viewpager_column_gourmet, null);

        PlaceViewItem item = mPlaceViewItemList.get(position);

        makeLayout(view, item.getPlace());

        container.addView(view, 0);

        return view;
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

    public void setData(ArrayList<PlaceViewItem> list)
    {
        if (mPlaceViewItemList == null)
        {
            mPlaceViewItemList = new ArrayList<PlaceViewItem>();
        }

        mPlaceViewItemList.clear();

        if (list != null)
        {
            mPlaceViewItemList.addAll(list);
        }
    }
}
