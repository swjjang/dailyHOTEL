package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.widget.DailyImageView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewRewardCardDataBinding;

public class DailyRewardCardView extends ConstraintLayout
{
    private DailyViewRewardCardDataBinding mViewDataBinding;

    public DailyRewardCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyRewardCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyRewardCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_reward_card_data, this, true);

        setBackgroundResource(R.drawable.r_minibox);
    }


    public void setGuideVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideImageView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnGuideClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideImageView.setOnClickListener(listener);
    }

    public void setOptionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setText(text);
    }

    public void setOptionVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnOptionClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.optionTextView.setOnClickListener(listener);
    }

    public void setNights(int nights)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        final DailyImageView[] dotImageViews = {mViewDataBinding.nights1ImageView//
            , mViewDataBinding.nights1ImageView//
            , mViewDataBinding.nights2ImageView//
            , mViewDataBinding.nights3ImageView//
            , mViewDataBinding.nights4ImageView//
            , mViewDataBinding.nights5ImageView//
            , mViewDataBinding.nights6ImageView//
            , mViewDataBinding.nights7ImageView//
            , mViewDataBinding.nights8ImageView//
            , mViewDataBinding.nights9ImageView};

        final View[] lineViews = {mViewDataBinding.nights2LineView//
            , mViewDataBinding.nights3LineView//
            , mViewDataBinding.nights4LineView//
            , mViewDataBinding.nights5LineView//
            , mViewDataBinding.nights6LineView//
            , mViewDataBinding.nights7LineView//
            , mViewDataBinding.nights8LineView//
            , mViewDataBinding.nights9LineView};

        final int dotViewsLength = dotImageViews.length;

        for (int i = 0; i < dotViewsLength; i++)
        {
            if (i < nights)
            {
                dotImageViews[i].setVectorImageResource(R.drawable.vector_ic_reward_circle_active);
            } else
            {
                dotImageViews[i].setVectorImageResource(R.drawable.vector_ic_reward_circle_normal);
            }
        }

        final int lineViewsLength = lineViews.length;

        for (int i = 0; i < lineViewsLength; i++)
        {
            if (i + 1 < nights)
            {
                lineViews[i].setBackgroundResource(R.color.default_line_cfaae37);
            } else
            {
                lineViews[i].setBackgroundResource(R.color.default_line_ce7e7e7);
            }
        }

    }

    public void setRewardTitleText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.rewardTitleTextView.setText(text);
    }

    public void setWarningText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.warningTextView.setText(text);
    }

    public void setWarningVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.warningTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setDescriptionText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.descriptionTextView.setText(text);
    }
}
