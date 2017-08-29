package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyPagerSnapHelper;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 16..
 */

public class HomeCarouselLayout extends LinearLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 15000;

    private Context mContext;
    private DailyTextView mTitleTextView;
    private DailyTextView mViewAllTextView;
    OnCarouselListener mCarouselListener;
    private RecyclerView mRecyclerView;
    private HomeCarouselAdapter mRecyclerAdapter;
    ValueAnimator mValueAnimator;

    int mMinHeight;
    int mMaxHeight;

    public interface OnCarouselListener
    {
        void onViewAllClick();

        void onItemClick(View view);

        void onItemLongClick(View view);
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeCarouselLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_row_home_carousel_layout, this);
        setVisibility(View.VISIBLE);

        mMinHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.home_carousel_min_height);
        mMaxHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.home_carousel_max_height);

        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundResource(R.color.default_background);

        setHeight(mMinHeight);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
        mViewAllTextView = (DailyTextView) view.findViewById(R.id.viewAllTextView);

        mViewAllTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mCarouselListener != null)
                {
                    mCarouselListener.onViewAllClick();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setAutoMeasureEnabled(true);
        layoutManager.setReverseLayout(false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.horizontalRecyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);

        if (ScreenUtils.isTabletDevice((Activity) mContext) == true)
        {
            SnapHelper snapHelper = new DailyPagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
        } else
        {
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(mRecyclerView);
        }

        EdgeEffectColor.setEdgeGlowColor(mRecyclerView, mContext.getResources().getColor(R.color.default_over_scroll_edge));
    }

    public void setTitleText(int titleResId)
    {
        if (mTitleTextView != null)
        {
            mTitleTextView.setText(titleResId);
        }
    }

    public ArrayList<CarouselListItem> getData()
    {
        if (mRecyclerAdapter == null)
        {
            return null;
        }

        return mRecyclerAdapter.getData();
    }

    public void setData(ArrayList<CarouselListItem> list)
    {
        mRecyclerView.scrollToPosition(0);

        setRecyclerAdapter(list);

        if (list == null || list.size() == 0)
        {
            startLayoutCloseAnimation();
        } else
        {
            if (getHeight() >= mMaxHeight)
            {
                return;
            }

            this.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    startLayoutShowAnimation();
                }
            }, 100);
        }
    }

    private void setRecyclerAdapter(ArrayList<CarouselListItem> list)
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
    }

    public boolean hasData()
    {
        if (mRecyclerAdapter == null)
        {
            return false;
        }

        return mRecyclerAdapter.getItemCount() > 0;
    }

    public CarouselListItem getItem(int position)
    {
        if (mRecyclerAdapter == null)
        {
            return null;
        }

        return mRecyclerAdapter.getItem(position);
    }

    void setHeight(int height)
    {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        } else
        {
            params.height = height;
        }

        setLayoutParams(params);
    }

    void startLayoutShowAnimation()
    {
        if (getHeight() >= mMaxHeight)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        final int gap = mMaxHeight - mMinHeight;
        mValueAnimator = ValueAnimator.ofInt(mMinHeight, mMaxHeight);
        mValueAnimator.setDuration(LAYOUT_ANIMATION_DURATION);
        mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = value;
                setLayoutParams(params);

                mRecyclerView.requestLayout();

                float alpha = (float) ((double) value / (double) gap);
                setAlpha(alpha);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setHeight(mMinHeight);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();
                mValueAnimator = null;

                setHeight(mMaxHeight);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    void startLayoutCloseAnimation()
    {
        if (getHeight() == mMinHeight)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        final int height = getHeight();

        mValueAnimator = ValueAnimator.ofInt(height, mMinHeight);
        mValueAnimator.setDuration(LAYOUT_ANIMATION_DURATION);
        mValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = value;
                setLayoutParams(params);
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                mValueAnimator.removeAllUpdateListeners();
                mValueAnimator.removeAllListeners();
                mValueAnimator = null;

                setHeight(mMinHeight);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        mValueAnimator.start();
    }

    public void setCarouselListener(OnCarouselListener listener)
    {
        mCarouselListener = listener;
    }

    private HomeCarouselAdapter.ItemClickListener mRecyclerItemClickListener = new HomeCarouselAdapter.ItemClickListener()
    {
        @Override
        public void onItemClick(View view)
        {
            if (mCarouselListener != null)
            {
                mCarouselListener.onItemClick(view);
            }
        }

        @Override
        public void onItemLongClick(View view)
        {
            if (mCarouselListener != null)
            {
                mCarouselListener.onItemLongClick(view);
            }
        }
    };
}
