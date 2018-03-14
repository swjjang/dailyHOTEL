package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewSearchStayOutboundAreaCardDataBinding;

public class DailySearchStayOutboundAreaCardView extends ConstraintLayout
{
    private DailyViewSearchStayOutboundAreaCardDataBinding mViewDataBinding;

    public DailySearchStayOutboundAreaCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailySearchStayOutboundAreaCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailySearchStayOutboundAreaCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_search_stay_outbound_area_card_data, this, true);
    }

    public void setIcon(int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.iconImageView.setVectorImageResource(resId);
    }

    public void setTitleText(String name)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(name);
    }

    public void setSubTitleText(String date)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.subTitleTextView.setText(date);
    }

    public void setSubTitleVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.subTitleTextView.setVisibility(visible ? VISIBLE : GONE);
    }


    //    public void setOnDeleteClickListener(OnClickListener listener)
    //    {
    //        if (mViewDataBinding == null)
    //        {
    //            return;
    //        }
    //
    //        mViewDataBinding.rightImageView.setOnClickListener(listener);
    //    }
}
