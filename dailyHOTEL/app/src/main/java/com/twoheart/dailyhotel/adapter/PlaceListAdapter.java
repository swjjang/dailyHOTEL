package com.twoheart.dailyhotel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.twoheart.dailyhotel.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.view.HotelListViewItem;
import com.twoheart.dailyhotel.view.PlaceViewItem;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView.PinnedSectionListAdapter;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PlaceListAdapter extends ArrayAdapter<PlaceViewItem> implements PinnedSectionListAdapter
{
    protected Context context;
    protected int resourceId;
    protected LayoutInflater inflater;
    protected PaintDrawable mPaintDrawable;
    private ArrayList<PlaceViewItem> mPlaceViewItemList;

    // Sort
    protected PlaceListFragment.SortType mSortType = PlaceListFragment.SortType.DEFAULT;

    public PlaceListAdapter(Context context, int resourceId, ArrayList<PlaceViewItem> arrayList)
    {
        super(context, resourceId, arrayList);

        if (mPlaceViewItemList == null)
        {
            mPlaceViewItemList = new ArrayList<PlaceViewItem>();
        }

        mPlaceViewItemList.clear();
        mPlaceViewItemList.addAll(arrayList);

        this.context = context;
        this.resourceId = resourceId;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    @Override
    public abstract View getView(final int position, View convertView, ViewGroup parent);

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

    @Override
    public void clear()
    {
        if (mPlaceViewItemList == null)
        {
            mPlaceViewItemList = new ArrayList<PlaceViewItem>();
        }

        mPlaceViewItemList.clear();

        super.clear();
    }

    @Override
    public PlaceViewItem getItem(int position)
    {
        if (mPlaceViewItemList == null)
        {
            return null;
        }

        return mPlaceViewItemList.get(position);
    }

    @Override
    public int getCount()
    {
        if (mPlaceViewItemList == null)
        {
            return 0;
        }

        return mPlaceViewItemList.size();
    }

    @Override
    public void addAll(Collection<? extends PlaceViewItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        if (mPlaceViewItemList == null)
        {
            mPlaceViewItemList = new ArrayList<PlaceViewItem>();
        }

        mPlaceViewItemList.addAll(collection);
    }

    public ArrayList<PlaceViewItem> getData()
    {
        return mPlaceViewItemList;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType)
    {
        return viewType == HotelListViewItem.TYPE_SECTION;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        return getItem(position).type;
    }
}
