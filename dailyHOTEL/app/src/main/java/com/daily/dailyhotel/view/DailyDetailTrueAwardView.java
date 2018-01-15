package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewDetailTrueAwardDataBinding;

/**
 * Created by android_sam on 2018. 1. 15..
 */

public class DailyDetailTrueAwardView extends ConstraintLayout
{
    DailyViewDetailTrueAwardDataBinding mDataBinding;

    public DailyDetailTrueAwardView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyDetailTrueAwardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyDetailTrueAwardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_true_award_data, this, true);

        setBackgroundResource(R.color.white);

        int horizontalPadding = ScreenUtils.dpToPx(context, 15d);
        int verticalPadding = ScreenUtils.dpToPx(context, 15d);

        setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    }

    public void setAwardsNameText(String text)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardNameView.setText(text);
    }

    public void setAwardsCategoryText(String text)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardCategoryView.setText(text);
    }

    public void setAwardsCategoryVisible(boolean isVisible)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardCategoryView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
