package com.daily.dailyhotel.view.carousel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.dailyhotel.entity.CarouselListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCarouselAnimationDataBinding;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 25..
 */

public class DailyCarouselAnimationLayout extends ConstraintLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 200;

    private Context mContext;
    private LayoutCarouselAnimationDataBinding mDataBinding;
    private ValueAnimator mValueAnimator;
    private boolean mIsUseAnimation;

    int mMinHeight;
    int mMaxHeight;

    public DailyCarouselAnimationLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout(null);
    }

    public DailyCarouselAnimationLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout(attrs);
    }

    public DailyCarouselAnimationLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout(attrs);
    }

    private void initLayout(AttributeSet attrs)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_carousel_animation_data, this, true);

        mMinHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.home_carousel_min_height);
        mMaxHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.home_carousel_max_height);

        if (attrs != null)
        {
            float wrapContentValue = (float) LayoutParams.WRAP_CONTENT;

            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.dailyCarousel);

            if (typedArray.hasValue(R.styleable.dailyCarousel_topMarginHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_topMarginHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_topMarginBackgroundColor, getResources().getColor(R.color.default_background));
                setTopMarginView((int) height, color);
            } else
            {
                setTopMarginView(LayoutParams.WRAP_CONTENT, getResources().getColor(R.color.transparent));
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_topLineHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_topLineHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_topLineBackgroundColor, getResources().getColor(R.color.default_line_cf0f0f0));
                setTopLineView((int) height, color);
            } else
            {
                setTopLineView(LayoutParams.WRAP_CONTENT, getResources().getColor(R.color.transparent));
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_bottomMarginHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_bottomMarginHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_bottomMarginBackgroundColor, getResources().getColor(R.color.default_background));
                setBottomMarginView((int) height, color);
            } else
            {
                setBottomMarginView(LayoutParams.WRAP_CONTENT, getResources().getColor(R.color.transparent));
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_bottomLineHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_bottomLineHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_bottomLineBackgroundColor, getResources().getColor(R.color.default_line_cf0f0f0));
                setBottomLineView((int) height, color);
            } else
            {
                setBottomLineView(LayoutParams.WRAP_CONTENT, getResources().getColor(R.color.transparent));
            }

            mIsUseAnimation = typedArray.getBoolean(R.styleable.dailyCarousel_use_animation, false);
            boolean isUsePrice = typedArray.getBoolean(R.styleable.dailyCarousel_use_price_layout, true);
            setUsePriceLayout(isUsePrice);
        }
    }

    public void setTopMarginView(int height, int color)
    {
        if (mDataBinding == null)
        {
            return;
        }

        setLayoutParams(mDataBinding.topMarginView, height, color);
    }

    public void setTopLineView(int height, int color)
    {
        if (mDataBinding == null)
        {
            return;
        }

        setLayoutParams(mDataBinding.topLine, height, color);
    }

    public void setBottomMarginView(int height, int color)
    {
        if (mDataBinding == null)
        {
            return;
        }

        setLayoutParams(mDataBinding.bottomMarginView, height, color);
    }

    public void setBottomLineView(int height, int color)
    {
        if (mDataBinding == null)
        {
            return;
        }

        setLayoutParams(mDataBinding.bottomLine, height, color);
    }

    private void setLayoutParams(View view, int height, int color)
    {
        if (view == null)
        {
            return;
        }

        if (height == LayoutParams.WRAP_CONTENT)
        {
            view.setVisibility(View.GONE);
            return;
        }

        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams == null)
        {
            layoutParams = new LayoutParams(LayoutParams.MATCH_CONSTRAINT, height);
        } else
        {
            layoutParams.height = height;
        }

        view.setVisibility(View.VISIBLE);
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(color);
    }

    public void setTitleText(int resId)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setTitleText(resId);
    }

    public boolean hasData()
    {
        if (mDataBinding == null)
        {
            return false;
        }

        return mDataBinding.contentLayout.getData() == null ? false : mDataBinding.contentLayout.getData().size() > 0;
    }

    public ArrayList<CarouselListItem> getData()
    {
        if (mDataBinding == null)
        {
            return null;
        }

        return mDataBinding.contentLayout.getData();
    }

    public void setData(ArrayList<CarouselListItem> list)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setData(list);

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

    public CarouselListItem getItem(int position)
    {
        if (mDataBinding == null)
        {
            return null;
        }

        return mDataBinding.contentLayout.getItem(position);
    }

    public void setUsePriceLayout(boolean isUse)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setUsePriceLayout(isUse);
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

    public void setCarouselListener(DailyCarouselLayout.OnCarouselListener listener)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setCarouselListener(listener);
    }
}
