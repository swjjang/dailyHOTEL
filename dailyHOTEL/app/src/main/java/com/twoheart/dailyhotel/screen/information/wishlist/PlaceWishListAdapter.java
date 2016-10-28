package com.twoheart.dailyhotel.screen.information.wishlist;

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

import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;

/**
 * Created by android_sam on 2016. 10. 13..
 */

public abstract class PlaceWishListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    protected Context mContext;
    protected LayoutInflater mInflater;
    private ArrayList<? extends Place> mList;
    protected OnWishListItemListener mListener;

    private Constants.SortType mSortType;
    protected PaintDrawable mPaintDrawable;
    protected boolean mShowDistanceIgnoreSort;

    public interface OnWishListItemListener
    {
        void onItemClick(View view);

        void onDeleteClick(View view, int position);
    }

    public PlaceWishListAdapter(Context context, ArrayList<? extends Place> list, OnWishListItemListener listener)
    {
        mContext = context;

        if (list == null)
        {
            throw new IllegalArgumentException("WishList list must not be null");
        }

        mList = list;
        mListener = listener;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    public ArrayList<? extends Place> getList()
    {
        return mList != null ? mList : null;
    }

    public void setData(ArrayList<? extends Place> list)
    {
        mList = list;
    }

    public Place getItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.get(position);
    }

    public Place removeItem(int position)
    {
        if (mList == null || mList.size() == 0)
        {
            return null;
        }

        return mList.remove(position);
    }

    @Override
    public int getItemCount()
    {
        return mList != null ? mList.size() : 0;
    }

    private void makeShaderFactory()
    {
        // 그라디에이션 만들기.
        final int colors[] = {Color.parseColor("#ED000000"), Color.parseColor("#E8000000"), Color.parseColor("#E2000000"), Color.parseColor("#66000000"), Color.parseColor("#00000000")};
        final float positions[] = {0.0f, 0.01f, 0.02f, 0.17f, 0.38f};

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
}
