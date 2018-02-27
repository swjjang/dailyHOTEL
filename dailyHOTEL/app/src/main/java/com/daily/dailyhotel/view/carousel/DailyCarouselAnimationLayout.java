package com.daily.dailyhotel.view.carousel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCarouselAnimationDataBinding;

import java.util.ArrayList;

/**
 * Created by android_sam on 2017. 8. 25..
 */

public class DailyCarouselAnimationLayout extends ConstraintLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 200;

    private Context mContext;
    LayoutCarouselAnimationDataBinding mDataBinding;
    ValueAnimator mValueAnimator;
    private boolean mIsUseAnimation;

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

        setVisibility(View.GONE);

        if (attrs != null)
        {
            float wrapContentValue = (float) LayoutParams.WRAP_CONTENT;

            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.dailyCarousel);

            if (typedArray.hasValue(R.styleable.dailyCarousel_topMarginHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_topMarginHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_topMarginBackgroundColor, getResources().getColor(R.color.default_background));
                setTopMarginView((int) height, color);
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_topLineHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_topLineHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_topLineBackgroundColor, getResources().getColor(R.color.default_line_cf0f0f0));
                setTopLineView((int) height, color);
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_bottomMarginHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_bottomMarginHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_bottomMarginBackgroundColor, getResources().getColor(R.color.default_background));
                setBottomMarginView((int) height, color);
            }

            if (typedArray.hasValue(R.styleable.dailyCarousel_bottomLineHeight) == true)
            {
                float height = typedArray.getDimension(R.styleable.dailyCarousel_bottomLineHeight, wrapContentValue);
                int color = typedArray.getColor(R.styleable.dailyCarousel_bottomLineBackgroundColor, getResources().getColor(R.color.default_line_cf0f0f0));
                setBottomLineView((int) height, color);
            }

            mIsUseAnimation = typedArray.getBoolean(R.styleable.dailyCarousel_useAnimation, false);
            boolean isUsePrice = typedArray.getBoolean(R.styleable.dailyCarousel_usePriceLayout, true);
            setUsePriceLayout(isUsePrice);

            boolean isUseViewAllButton = typedArray.getBoolean(R.styleable.dailyCarousel_useViewAllButton, true);
            setUseViewAllButtonLayout(isUseViewAllButton);

            typedArray.recycle();
        } else
        {
            setUsePriceLayout(true);
            setUseViewAllButtonLayout(true);
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

    public void setUseAnimation(boolean isUse)
    {
        mIsUseAnimation = isUse;
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

        return mDataBinding.contentLayout.getData() != null && mDataBinding.contentLayout.getData().size() > 0;
    }

    public ArrayList<CarouselListItem> getData()
    {
        if (mDataBinding == null)
        {
            return null;
        }

        return mDataBinding.contentLayout.getData();
    }

    public void setData(ArrayList<CarouselListItem> list, boolean nightsEnabled)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setData(list, nightsEnabled);

        boolean hasData = list != null && list.size() > 0;

        if (hasData == false)
        {

            if (getVisibility() == View.GONE)
            {
                return;
            }

            if (mIsUseAnimation == false)
            {
                setVisibility(View.GONE);
            } else
            {
                // start close Animation
                mDataBinding.contentLayout.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startAnimation(false);
                    }
                });
            }
        } else
        {
            if (getVisibility() == View.VISIBLE)
            {
                return;
            }

            if (mIsUseAnimation == false)
            {
                setVisibility(View.VISIBLE);
                return;
            }

            // start show Animation
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout()
                {
                    mDataBinding.contentLayout.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            startAnimation(true);
                        }
                    });

                    if (VersionUtils.isOverAPI16() == true)
                    {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else
                    {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });

            setVisibility(View.VISIBLE);
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

    public void setUseViewAllButtonLayout(boolean isUse)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setUseViewAllButtonLayout(isUse);
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

    void startAnimation(boolean isShow)
    {
        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        int height = getHeight();
        int start = isShow == false ? height : 0;
        int end = isShow == false ? 0 : height;

        mValueAnimator = ValueAnimator.ofInt(start, end);
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

                if (isShow == true)
                {
                    float alpha = (float) ((double) value / (double) height);
                    setAlpha(alpha);
                }
            }
        });

        mValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
                setHeight(start);
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mValueAnimator != null)
                {
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator.removeAllListeners();
                    mValueAnimator = null;
                }

                setHeight(end);

                if (isShow == true)
                {
                    setVisibility(View.VISIBLE);
                } else
                {
                    setVisibility(View.GONE);
                }
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