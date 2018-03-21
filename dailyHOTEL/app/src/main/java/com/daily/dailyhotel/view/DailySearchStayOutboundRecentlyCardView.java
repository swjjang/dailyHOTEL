package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewSearchStayOutboundRecentlyCardDataBinding;

public class DailySearchStayOutboundRecentlyCardView extends ConstraintLayout
{
    private DailyViewSearchStayOutboundRecentlyCardDataBinding mViewDataBinding;

    public DailySearchStayOutboundRecentlyCardView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailySearchStayOutboundRecentlyCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailySearchStayOutboundRecentlyCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_search_stay_outbound_recently_card_data, this, true);
    }

    public void setIcon(int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.iconImageView.setVectorImageResource(resId);
    }

    public void setNameText(String name)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.nameTextView.setText(name);
    }

    public void setDateText(String date)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.dateTextView.setText(date);
    }

    public void setPeopleText(String text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.peopleTextView.setText(text);
    }

    public void setOnDeleteClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.deleteImageView.setOnClickListener(listener);
    }
}
