package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewDetailTrueReviewDataBinding;

public class DailyDetailTrueReviewView extends ConstraintLayout
{
    private DailyViewDetailTrueReviewDataBinding mViewDataBinding;

    public DailyDetailTrueReviewView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyDetailTrueReviewView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyDetailTrueReviewView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_true_review_data, this, true);

        setBackgroundResource(R.color.white);

        final int DP_15 = ScreenUtils.dpToPx(context, 15);
        setPadding(DP_15, ScreenUtils.dpToPx(context, 20), DP_15, 0);
    }

    public void setSatisfactionVText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setText(text);
    }

    public void setSatisfactionVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setTrueReviewCount(int reviewCount)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setText(getContext().getString(R.string.label_detail_view_review_go, reviewCount));
    }

    public void setTrueReviewCountVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        int flag = visible ? VISIBLE : GONE;

        mViewDataBinding.goTrueReviewTopLineView.setVisibility(visible ? VISIBLE : GONE);
        mViewDataBinding.goTrueReviewTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnTrueReviewCountClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.goTrueReviewTextView.setOnClickListener(listener);
    }

    public void setTripAdvisorRating(float tripAdvisorRating)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.tripAdvisorRatingBar.setOnTouchListener((v, event) -> true);
        mViewDataBinding.tripAdvisorRatingBar.setRating(tripAdvisorRating);
        mViewDataBinding.tripAdvisorRatingTextView.setText(getContext().getString(R.string.label_stay_outbound_tripadvisor_rating, Float.toString(tripAdvisorRating)));
    }

    public void setTripAdvisorVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.tripAdvisorImageView.setVisibility(visible ? VISIBLE : GONE);
    }
}
