package com.twoheart.dailyhotel.screen.home;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyTextView;

/**
 * Created by android_sam on 2017. 4. 11..
 */

public class HomeCategoryLayout extends RelativeLayout
{
    private static final int MAX_COLUMN_COUNT = 5;
    private static final int ANIMATION_DURATION = 200;

    private static final DailyCategoryType[] sColumnList = {DailyCategoryType.STAY_HOTEL, //
        DailyCategoryType.STAY_BOUTIQUE, //
        DailyCategoryType.STAY_PENSION, //
        DailyCategoryType.STAY_RESORT, //
        DailyCategoryType.STAY_SEARCH_MY_LOCATION};

    private Context mContext;
    private RelativeLayout mLayout;
    private android.support.v7.widget.GridLayout mItemGridLayout;
    private boolean mIsEnabled;


    public HomeCategoryLayout(Context context)
    {
        super(context);

        mContext = context;
        initLayout();
    }

    public HomeCategoryLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mContext = context;
        initLayout();
    }

    public HomeCategoryLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        mContext = context;
        initLayout();
    }

    public HomeCategoryLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        initLayout();
    }

    private void initLayout()
    {
        setVisibility(View.GONE);

        mLayout = (RelativeLayout) LayoutInflater.from(mContext) //
            .inflate(R.layout.list_row_home_category_layout, this);

        mLayout.setBackgroundResource(R.color.white);

        RelativeLayout.LayoutParams layoutParams //
            = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = Util.dpToPx(mContext, 10d);
        mLayout.setLayoutParams(layoutParams);

        mItemGridLayout = (android.support.v7.widget.GridLayout) mLayout.findViewById(R.id.categoryGridLayout);
        mItemGridLayout.setColumnCount(MAX_COLUMN_COUNT);

        addGridItemView(mItemGridLayout, sColumnList);
    }

    private void addGridItemView(GridLayout gridLayout, DailyCategoryType[] categoryTypeList)
    {
        if (gridLayout == null)
        {
            return;
        }

        gridLayout.removeAllViews();

        if (categoryTypeList == null || categoryTypeList.length == 0)
        {
            return;
        }

        for (DailyCategoryType type : categoryTypeList)
        {
            gridLayout.addView(getGridLayoutItemView(mContext, type));
        }

        int remainder = categoryTypeList.length % MAX_COLUMN_COUNT;
        if (remainder != 0)
        {
            for (int i = 0; i < remainder; i++)
            {
                gridLayout.addView(getGridLayoutItemView(mContext, DailyCategoryType.NONE));
            }
        }
    }

    private DailyTextView getGridLayoutItemView(Context context, DailyCategoryType categoryType)
    {
        DailyTextView dailyTextView = new DailyTextView(mContext);
        dailyTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        dailyTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
        dailyTextView.setTextColor(mContext.getResources().getColorStateList(R.color.default_text_c323232));
        dailyTextView.setText(categoryType.getNameResId());
        dailyTextView.setCompoundDrawablesWithIntrinsicBounds(0, categoryType.getImageResId(), 0, 0);
        dailyTextView.setDrawableVectorTint(R.color.default_background_c454545);

        android.support.v7.widget.GridLayout.LayoutParams layoutParams = new android.support.v7.widget.GridLayout.LayoutParams();
        layoutParams.width = 0;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.columnSpec = android.support.v7.widget.GridLayout.spec(Integer.MIN_VALUE, 1, 1.0f);

        //        if (isSingleLine == true)
        //        {
        dailyTextView.setPadding(0, Util.dpToPx(context, 10), 0, Util.dpToPx(context, 15));
        //        } else
        //        {
        //            dailyTextView.setPadding(0, Util.dpToPx(context, 10), 0, Util.dpToPx(context, 2));
        //        }

        dailyTextView.setLayoutParams(layoutParams);

        return dailyTextView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //        ExLog.d("widthMeasureSpec : " + widthMeasureSpec + " , heightMeasureSpec : " + heightMeasureSpec);
        ExLog.d("getMeasuredWidth : " + getMeasuredWidth() + " , getMeasuredHeight : " + getMeasuredHeight());
    }

    public void setCategoryEnabled(boolean isEnabled)
    {
        if (isEnabled == true)
        {
            startShowAnimation(mLayout);
        } else
        {
            startCloseAnimation(mLayout);
        }
    }

    private void startShowAnimation(final View view)
    {
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.VISIBLE)
        {
            return;
        }

        view.setVisibility(View.INVISIBLE);

        view.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                final ValueAnimator valueAnimator = ValueAnimator.ofInt(0, view.getHeight());
                valueAnimator.setDuration(ANIMATION_DURATION);
                valueAnimator.setInterpolator(new FastOutSlowInInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        int value = (int) animation.getAnimatedValue();
                        ViewGroup.LayoutParams params = view.getLayoutParams();
                        params.height = value;
                        view.setLayoutParams(params);
                    }
                });

                valueAnimator.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        valueAnimator.removeAllUpdateListeners();
                        valueAnimator.removeAllListeners();

                        view.setVisibility(View.VISIBLE);
                        view.clearAnimation();
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

                valueAnimator.start();
            }
        }, 100);
    }

    private void startCloseAnimation(final View view)
    {
        if (view == null)
        {
            return;
        }

        if (view.getVisibility() == View.GONE)
        {
            return;
        }

        view.setVisibility(View.VISIBLE);

        final ValueAnimator closeValueAnimator = ValueAnimator.ofInt(view.getHeight(), 0);
        closeValueAnimator.setDuration(ANIMATION_DURATION);
        closeValueAnimator.setInterpolator(new FastOutSlowInInterpolator());
        closeValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.height = value;
                view.setLayoutParams(params);
            }
        });

        closeValueAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                closeValueAnimator.removeAllUpdateListeners();
                closeValueAnimator.removeAllListeners();

                view.setVisibility(View.GONE);
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

        closeValueAnimator.start();
    }
}
