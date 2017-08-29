package com.daily.dailyhotel.view.carousel;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.entity.CarouselListItem;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutCarouselAnimationDataBinding;

import java.util.ArrayList;

/**
 * Created by iseung-won on 2017. 8. 25..
 */

public class DailyCarouselAnimationLayout extends ConstraintLayout
{
    private static final int LAYOUT_ANIMATION_DURATION = 15000;

    private Context mContext;
    private LayoutCarouselAnimationDataBinding mDataBinding;
    private ValueAnimator mValueAnimator;
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
            setVisibility(View.GONE);
            return;
        }

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {

                if (VersionUtils.isOverAPI16() == true)
                {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else
                {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                int height = getHeight();
                int top = getTop();
                int bottom = getBottom();

                ValueAnimator animator = ValueAnimator.ofInt(0, height);
                animator.setDuration(LAYOUT_ANIMATION_DURATION);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        Integer value = (Integer) animation.getAnimatedValue();
                        ExLog.d("value : " + value + " / " + (top + value.intValue()));


//                        setTop(top);
//                        setBottom(top + value.intValue());

                        ViewGroup.LayoutParams params = getLayoutParams();
                        params.height = value;
                        setLayoutParams(params);
                        postInvalidate();
                        refreshDrawableState();
//                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.animationLayout.getLayoutParams();
//                        layoutParams.height = value;
//                        mDataBinding.animationLayout.setLayoutParams(layoutParams);
//                        requestLayout();
//                        refreshDrawableState();
//                        getParent().requestLayout();
                    }
                });
                animator.start();
            }
        });
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

    public void setCarouselListener(DailyCarouselLayout.OnCarouselListener listener)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.contentLayout.setCarouselListener(listener);
    }
}