package com.twoheart.dailyhotel.screen.mydaily.wishlist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.Constants;

import java.util.List;

/**
 * Created by android_sam on 2016. 11. 1..
 */

public abstract class PlaceWishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<PlaceViewItem> mList;
    protected OnPlaceWishListItemListener mListener;

    private Constants.SortType mSortType; // TODO : 추후 제거 필요!
    protected boolean mShowDistanceIgnoreSort;
    protected boolean mTrueVREnabled;
    protected boolean mRewardEnabled;

    public interface OnPlaceWishListItemListener
    {
        void onItemClick(View view);

        void onItemLongClick(View view);

        void onItemRemoveClick(View view);
    }

    public PlaceWishListAdapter(Context context, List<PlaceViewItem> list, OnPlaceWishListItemListener listener)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("Wishlist must not be null");
        }

        mList = list;
        mListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setRewardEnabled(boolean enabled)
    {
        mRewardEnabled = enabled;
    }

    public List<PlaceViewItem> getList()
    {
        return mList;
    }

    public void setData(List<PlaceViewItem> list)
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

    public PlaceViewItem remove(int position)
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
