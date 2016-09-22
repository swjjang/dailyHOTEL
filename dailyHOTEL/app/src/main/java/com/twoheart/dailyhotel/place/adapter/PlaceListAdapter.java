package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.widget.DailyLoopViewPager;
import com.twoheart.dailyhotel.widget.DailyViewPagerCircleIndicator;
import com.twoheart.dailyhotel.widget.PinnedSectionRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PlaceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PinnedSectionRecyclerView.PinnedSectionListAdapter
{
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<PlaceViewItem> mPlaceViewItemList;
    protected PaintDrawable mPaintDrawable;
    protected boolean mShowDistanceIgnoreSort;
    protected View.OnClickListener mOnEventBannerClickListener;

    private Handler mEventBannerHandler;
    private int mLastEventBannerPosition;
    private Constants.SortType mSortType;

    public PlaceListAdapter(Context context, ArrayList<PlaceViewItem> arrayList)
    {
        mContext = context;

        mPlaceViewItemList = new ArrayList<>();
        mEventBannerHandler = new EventBannerHandler(this);

        addAll(arrayList);

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        makeShaderFactory();
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder)
    {
        super.onViewRecycled(holder);

        if (holder instanceof EventBannerViewHolder)
        {
            mEventBannerHandler.removeMessages(0);
        }
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

    public void add(PlaceViewItem placeViewItem)
    {
        mPlaceViewItemList.add(placeViewItem);
    }

    public void add(int position, PlaceViewItem placeViewItem)
    {
        if (position >= 0 && position < mPlaceViewItemList.size())
        {
            mPlaceViewItemList.add(position, placeViewItem);
        }
    }

    public void addAll(Collection<? extends PlaceViewItem> collection)
    {
        if (collection == null)
        {
            return;
        }

        mPlaceViewItemList.addAll(collection);
    }

    public void setAll(Collection<? extends PlaceViewItem> collection)
    {
        clear();
        addAll(collection);
    }

    public List<PlaceViewItem> getAll()
    {
        return mPlaceViewItemList;
    }

    public PlaceViewItem getItem(int position)
    {
        if (position < 0 || mPlaceViewItemList.size() <= position)
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
        return mPlaceViewItemList.get(position).mType;
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

    protected void onBindViewHolder(SectionViewHolder holder, PlaceViewItem placeViewItem)
    {
        holder.regionDetailName.setText(placeViewItem.<String>getItem());
    }

    protected void onBindViewHolder(final EventBannerViewHolder holder, PlaceViewItem placeViewItem)
    {
        ArrayList<EventBanner> eventBannerList = placeViewItem.getItem();

        PlaceBannerViewPagerAdapter adapter = new PlaceBannerViewPagerAdapter(mContext, eventBannerList, mOnEventBannerClickListener);
        holder.dailyLoopViewPager.setOnPageChangeListener(null);
        holder.dailyLoopViewPager.setAdapter(adapter);
        holder.viewpagerCircleIndicator.setTotalCount(eventBannerList.size());
        holder.dailyLoopViewPager.setCurrentItem(mLastEventBannerPosition);
        holder.dailyLoopViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }

            @Override
            public void onPageSelected(int position)
            {
                mLastEventBannerPosition = position;

                holder.viewpagerCircleIndicator.setPosition(position);

                nextEventBannerPosition(holder, position);
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                if (state == DailyLoopViewPager.SCROLL_STATE_DRAGGING)
                {
                    mEventBannerHandler.removeMessages(0);
                }
            }
        });

        nextEventBannerPosition(holder, mLastEventBannerPosition);
    }

    private void nextEventBannerPosition(EventBannerViewHolder eventViewHolder, int position)
    {
        mEventBannerHandler.removeMessages(0);

        Message message = new Message();
        message.what = 0;
        message.arg1 = position;
        message.obj = eventViewHolder;
        mEventBannerHandler.sendMessageDelayed(message, 5000);
    }

    protected class SectionViewHolder extends RecyclerView.ViewHolder
    {
        public TextView regionDetailName;

        public SectionViewHolder(View itemView)
        {
            super(itemView);

            regionDetailName = (TextView) itemView.findViewById(R.id.sectionTextView);
        }
    }

    protected class EventBannerViewHolder extends RecyclerView.ViewHolder
    {
        public DailyLoopViewPager dailyLoopViewPager;
        public DailyViewPagerCircleIndicator viewpagerCircleIndicator;

        public EventBannerViewHolder(View itemView)
        {
            super(itemView);

            dailyLoopViewPager = (DailyLoopViewPager) itemView.findViewById(R.id.loopViewPager);
            viewpagerCircleIndicator = (DailyViewPagerCircleIndicator) itemView.findViewById(R.id.viewpagerCircleIndicator);

            dailyLoopViewPager.setSlideTime(4);
        }
    }

    protected class FooterViewHolder extends RecyclerView.ViewHolder
    {
        public FooterViewHolder(View itemView)
        {
            super(itemView);
        }
    }

    protected static class EventBannerHandler extends Handler
    {
        private final WeakReference<PlaceListAdapter> mWeakReference;

        public EventBannerHandler(PlaceListAdapter placeListAdapter)
        {
            mWeakReference = new WeakReference<>(placeListAdapter);
        }

        @Override
        public void handleMessage(Message msg)
        {
            if (mWeakReference.get() == null)
            {
                return;
            }

            EventBannerViewHolder eventBannerViewHolder = (EventBannerViewHolder) msg.obj;

            if (eventBannerViewHolder != null)
            {
                eventBannerViewHolder.dailyLoopViewPager.setCurrentItem(msg.arg1 + 1);
            }
        }
    }
}
