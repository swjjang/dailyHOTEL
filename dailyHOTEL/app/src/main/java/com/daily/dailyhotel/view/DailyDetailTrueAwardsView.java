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

public class DailyDetailTrueAwardsView extends ConstraintLayout
{
    private DailyViewDetailTrueAwardDataBinding mDataBinding;
    OnDailyDetailTrueAwardsListener mListener;

    public interface OnDailyDetailTrueAwardsListener
    {
        void onQuestionClick();
    }

    public DailyDetailTrueAwardsView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyDetailTrueAwardsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyDetailTrueAwardsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    public void setListener(OnDailyDetailTrueAwardsListener listener)
    {
        mListener = listener;
    }

    private void initLayout(Context context)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_detail_true_award_data, this, true);

        setBackgroundResource(R.color.white);

        int horizontalPadding = ScreenUtils.dpToPx(context, 15d);
        setPadding(horizontalPadding, 0, horizontalPadding, 0);

        mDataBinding.awardsQuestionView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mListener == null)
                {
                    return;
                }

                mListener.onQuestionClick();
            }
        });
    }

    public void setAwardsNameText(String text)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardsNameView.setText(text);
    }

    public void setAwardsDetailText(String text)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardsDetailView.setText(text);
    }

    public void setAwardsDetailLayoutVisible(boolean isVisible)
    {
        if (mDataBinding == null)
        {
            return;
        }

        mDataBinding.awardsDetailView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
