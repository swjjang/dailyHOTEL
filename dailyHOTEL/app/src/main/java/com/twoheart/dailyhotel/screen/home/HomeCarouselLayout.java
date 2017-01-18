package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyViewPager;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 16..
 */

public class HomeCarouselLayout extends RelativeLayout
{
    private Context mContext;
    private DailyTextView mTitleTextView;
    private DailyTextView mCountTextView;
    private DailyTextView mViewAllTextView;
    private DailyViewPager mViewPager;
    private OnCarouselListener mCarouselListenter;
    private ArrayList<? extends Place> mPlaceList;
    private HomeCarouselPageAdapter mPageAdapter;

    public interface OnCarouselListener {
        void onViewAllClick();
    }

    public HomeCarouselLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    public HomeCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_row_home_carousel_layout, this);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
        mCountTextView = (DailyTextView) view.findViewById(R.id.countTextView);
        mViewAllTextView = (DailyTextView) view.findViewById(R.id.viewAllTextView);

        mViewPager = (DailyViewPager) view.findViewById(R.id.contentViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setClipToPadding(false);
        mViewPager.setPageMargin(Util.dpToPx(mContext, 12d));

        int paddingLeftRight = Util.dpToPx(mContext, 15d);
        int paddingTopBottom = 0;
        mViewPager.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);

        mViewAllTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCarouselListenter != null)
                {
                    mCarouselListenter.onViewAllClick();
                }
            }
        });

        mPageAdapter = new HomeCarouselPageAdapter(mContext, mItemClickListener);

        mViewPager.clearOnPageChangeListeners();
        mViewPager.setAdapter(mPageAdapter);
    }

    public void setCarouselListener(OnCarouselListener listener)
    {
        mCarouselListenter = listener;
    }

    public void setViewPagerData(ArrayList<? extends Place> list)
    {
        mPlaceList = list;
    }

    private HomeCarouselPageAdapter.ItemClickListener mItemClickListener = new HomeCarouselPageAdapter.ItemClickListener()
    {
        @Override
        public void onItemClick(View view, int position)
        {
            // TODO : 해당 업장 상세로 이동하는 부분 필요!
        }
    };
}
