package com.twoheart.dailyhotel.screen.home;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;

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
    OnCarouselListener mCarouselListenter;
    private RecyclerView mRecyclerView;
    private HomeCarouselAdapter mRecyclerAdapter;

    public interface OnCarouselListener
    {
        void onViewAllClick();

        void onItemClick(View view, int position);
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
        setVisibility(View.VISIBLE);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
        mCountTextView = (DailyTextView) view.findViewById(R.id.countTextView);
        mViewAllTextView = (DailyTextView) view.findViewById(R.id.viewAllTextView);

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setAutoMeasureEnabled(true);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.horizontalRecyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setTitleText(int titleResId)
    {
        if (mTitleTextView != null)
        {
            mTitleTextView.setText(titleResId);
        }
    }

    public void setData(ArrayList<HomePlace> list)
    {
        setRecyclerAdapter(list);

        if (list == null || list.size() == 0)
        {
            setVisibility(View.GONE);
        } else
        {
            setVisibility(View.VISIBLE);
        }
    }

    private void setRecyclerAdapter(ArrayList<HomePlace> list)
    {
        if (mRecyclerAdapter == null)
        {
            mRecyclerAdapter = new HomeCarouselAdapter(mContext, list, mRecyclerItemClickListener);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        } else
        {
            mRecyclerAdapter.setData(list);
            mRecyclerAdapter.notifyDataSetChanged();
        }
    }

    public void clearAll()
    {
        setRecyclerAdapter(null);
        setVisibility(View.VISIBLE);
    }

    public boolean hasData()
    {
        if (mRecyclerAdapter == null)
        {
            return false;
        }

        return mRecyclerAdapter.getItemCount() > 0 ? true : false;
    }

    public HomePlace getItem(int position)
    {
        if (mRecyclerAdapter == null)
        {
            return null;
        }

        return mRecyclerAdapter.getItem(position);
    }

    public void setCarouselListener(OnCarouselListener listener)
    {
        mCarouselListenter = listener;
    }

    private HomeCarouselAdapter.ItemClickListener mRecyclerItemClickListener = new HomeCarouselAdapter.ItemClickListener()
    {
        @Override
        public void onItemClick(View view, int position)
        {
            if (mCarouselListenter != null)
            {
                mCarouselListenter.onItemClick(view, position);
            }
        }
    };
}
