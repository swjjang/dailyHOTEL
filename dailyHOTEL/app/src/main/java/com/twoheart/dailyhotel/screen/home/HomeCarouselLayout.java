package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HomePlace;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 1. 16..
 */

public class HomeCarouselLayout extends LinearLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 200;

    private Context mContext;
    private DailyTextView mTitleTextView;
    private DailyTextView mViewAllTextView;
    OnCarouselListener mCarouselListenter;
    private RecyclerView mRecyclerView;
    private HomeCarouselAdapter mRecyclerAdapter;
    private ValueAnimator mValueAnimator;

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
        LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_row_home_carousel_layout, this);

        view.setOrientation(LinearLayout.VERTICAL);
        view.setBackgroundResource(R.color.default_background);
        setVisibility(View.GONE);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
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
        mRecyclerView.scrollToPosition(0);

        setRecyclerAdapter(list);

        if (list == null || list.size() == 0)
        {
            startLayoutCloseAnimation();
        } else
        {
            if (getVisibility() == View.VISIBLE)
            {
                return;
            }

            setVisibility(View.INVISIBLE);

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

    private void startLayoutShowAnimation()
    {
        if (getVisibility() == View.VISIBLE)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        final int height = getHeight();

        mValueAnimator = ValueAnimator.ofInt(0, height);
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
                setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                setVisibility(View.VISIBLE);
                clearAnimation();

                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(params);

                mValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                setVisibility(View.VISIBLE);
                clearAnimation();

                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(params);

                mValueAnimator = null;
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
        if (getVisibility() == View.GONE)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        final int height = getHeight();

        mValueAnimator = ValueAnimator.ofInt(height, 0);
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
                setVisibility(View.GONE);
                clearAnimation();

                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(params);

                mValueAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {
                setVisibility(View.GONE);
                clearAnimation();

                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setLayoutParams(params);

                mValueAnimator = null;
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
