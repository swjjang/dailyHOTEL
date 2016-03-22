package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionRecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PlaceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecyclerView.PinnedSectionListAdapter
{
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<PlaceViewItem> mPlaceViewItemList;
    protected PaintDrawable mPaintDrawable;

    public PlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        mContext = context;

        mPlaceViewItemList = new ArrayList<>();
        mPlaceViewItemList.addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
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

    public void clear()
    {
        mPlaceViewItemList.clear();
    }

    public void addAll(Collection<? extends PlaceViewItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mPlaceViewItemList.clear();
        mPlaceViewItemList.addAll(collection);
    }

    public List<PlaceViewItem> getAll()
    {
        return mPlaceViewItemList;
    }

    public PlaceViewItem getItem(int position)
    {
        if (mPlaceViewItemList.size() <= position)
        {
            return null;
        }

        return mPlaceViewItemList.get(position);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == PlaceViewItem.TYPE_SECTION;
    }

    @Override
    public int getItemViewType(int position)
    {
        return mPlaceViewItemList.get(position).getType();
    }

    @Override
    public int getItemCount()
    {
        if (mPlaceViewItemList == null)
        {
            return 0;
        }

        return mPlaceViewItemList.size();
    }
}
