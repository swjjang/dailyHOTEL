package com.daily.dailyhotel.view.carousel;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCarouselDataBinding;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyPagerSnapHelper;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 24..
 */

public class DailyCarouselLayout extends ConstraintLayout
{
    private Context mContext;
    private LayoutCarouselDataBinding mDataBinding;
    private DailyCarouselAdapter mAdapter;
    private boolean mIsUsePriceLayout;

    protected OnCarouselListener mCarouselListener;

    public interface OnCarouselListener
    {
        void onViewAllClick();

        void onItemClick(View view);

        void onItemLongClick(View view);
    }

    public DailyCarouselLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout(context, null);
    }

    public DailyCarouselLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout(context, attrs);
    }

    public DailyCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_carousel_data, this, true);

        setBackgroundResource(R.color.white);

        mDataBinding.viewAllTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCarouselListener == null)
                {
                    return;
                }

                mCarouselListener.onViewAllClick();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
//                layoutManager.setAutoMeasureEnabled(true);

        mDataBinding.horizontalRecyclerView.setLayoutManager(layoutManager);
        EdgeEffectColor.setEdgeGlowColor(mDataBinding.horizontalRecyclerView, context.getResources().getColor(R.color.default_over_scroll_edge));

//                mDataBinding.recyclerView.setNestedScrollingEnabled(false);
//                mDataBinding.recyclerView.setHasFixedSize(true);

        if (ScreenUtils.isTabletDevice((Activity) mContext) == true)
        {
            SnapHelper snapHelper = new DailyPagerSnapHelper();
            snapHelper.attachToRecyclerView(mDataBinding.horizontalRecyclerView);
        } else
        {
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mDataBinding.horizontalRecyclerView);
        }

        if (attrs != null)
        {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.dailyCarousel);
            boolean isUse = typedArray.getBoolean(R.styleable.dailyCarousel_use_price_layout, true);
            setUsePriceLayout(isUse);
        }
    }

    public void setCarouselListener(OnCarouselListener listener)
    {
        mCarouselListener = listener;
    }

    public void setTitleText(int resId)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.titleTextView.setText(resId);
    }

    public boolean hasData()
    {
        if (mAdapter == null)
        {
            return false;
        }

        return mAdapter.getData() == null ? false : mAdapter.getData().size() > 0;
    }

    public ArrayList<CarouselListItem> getData()
    {
        if (mAdapter == null)
        {
            return null;
        }

        return mAdapter.getData();
    }

    public void setData(ArrayList<CarouselListItem> list)
    {
        mDataBinding.horizontalRecyclerView.scrollToPosition(0);

        if (mAdapter == null)
        {
            mAdapter = new DailyCarouselAdapter(mContext, list, mItemClickListener);
            mAdapter.setUsePriceLayout(mIsUsePriceLayout);
            mDataBinding.horizontalRecyclerView.setAdapter(mAdapter);
        } else
        {
            mAdapter.setUsePriceLayout(mIsUsePriceLayout);
            mAdapter.setData(list);
            mAdapter.notifyDataSetChanged();
        }
    }

    public CarouselListItem getItem(int position)
    {
        if (mAdapter == null)
        {
            return null;
        }

        return mAdapter.getItem(position);
    }

    public void setUsePriceLayout(boolean isUse)
    {
        mIsUsePriceLayout = isUse;
    }

    private DailyCarouselAdapter.ItemClickListener mItemClickListener = new DailyCarouselAdapter.ItemClickListener()
    {
        @Override
        public void onItemClick(View view)
        {
            if (mCarouselListener == null)
            {
                return;
            }

            mCarouselListener.onItemClick(view);
        }

        @Override
        public void onItemLongClick(View view)
        {
            if (mCarouselListener == null)
            {
                return;
            }

            mCarouselListener.onItemLongClick(view);
        }
    };
}