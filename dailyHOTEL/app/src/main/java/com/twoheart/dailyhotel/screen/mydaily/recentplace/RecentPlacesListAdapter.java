package com.twoheart.dailyhotel.screen.mydaily.recentplace;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public abstract class RecentPlacesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    protected Context mContext;
    protected LayoutInflater mInflater;
    private ArrayList<PlaceViewItem> mList;
    protected OnRecentPlacesItemListener mListener;

    private Constants.SortType mSortType; // TODO : 추후 제거 필요!
    protected PaintDrawable mPaintDrawable;
    protected boolean mShowDistanceIgnoreSort;
    protected boolean mTrueVREnabled;
    protected boolean mRewardEnabled;

    public interface OnRecentPlacesItemListener
    {
        void onItemClick(View view);

        void onItemLongClick(View view);

        void onDeleteClick(View view);

        void onWishClick(View view);
    }

    public abstract void setPlaceBookingDay(PlaceBookingDay placeBookingDay);

    public RecentPlacesListAdapter(Context context, ArrayList<PlaceViewItem> list, OnRecentPlacesItemListener listener)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("Recent Place list must not be null");
        }

        mList = list;
        mListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public ArrayList<PlaceViewItem> getList()
    {
        return mList;
    }

    public void setData(ArrayList<PlaceViewItem> list)
    {
        mList = list;
    }

    public PlaceViewItem getItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.get(position);
    }

    public PlaceViewItem removeItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        PlaceViewItem removeItem = mList.remove(position);

        if (mList.size() == 1)
        {
            PlaceViewItem checkItem = mList.get(0);
            if (checkItem.mType == PlaceViewItem.TYPE_FOOTER_VIEW)
            {
                mList.remove(0);
            }
        }

        return removeItem;
    }

    @Override
    public int getItemCount()
    {
        return mList != null && mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mList.get(position).mType;
    }

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#E6000000"), Color.parseColor("#99000000"), Color.parseColor("#1A000000"), Color.parseColor("#00000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.24f, 0.66f, 0.8f, 1.0f};

        mPaintDrawable = new PaintDrawable();
        mPaintDrawable.setShape(new RectShape());

        ShapeDrawable.ShaderFactory sf = new ShapeDrawable.ShaderFactory()
        {
            @Override
            public Shader resize(int width, int height)
            {
                return new LinearGradient(0, height, 0, 0, colors, positions, Shader.TileMode.CLAMP);
            }
        };

        mPaintDrawable.setShaderFactory(sf);
    }

    public Constants.SortType getSortType()
    {
        return mSortType;
    }

    public void setSortType(Constants.SortType sortType)
    {
        this.mSortType = sortType;
    }

    public void setShowDistanceIgnoreSort(boolean isShow)
    {
        mShowDistanceIgnoreSort = isShow;
    }

    public void setTrueVREnabled(boolean enabled)
    {
        mTrueVREnabled = enabled;
    }
}
