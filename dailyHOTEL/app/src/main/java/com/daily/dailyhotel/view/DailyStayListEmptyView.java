package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewStayListEmptyDataBinding;

public class DailyStayListEmptyView extends android.support.v4.widget.NestedScrollView
{
    private DailyViewStayListEmptyDataBinding mViewDataBinding;

    public DailyStayListEmptyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyStayListEmptyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyStayListEmptyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_stay_list_empty_data, this, true);
    }

    public void setMessageTextView(String message01, String message02)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.message01TextView.setText(message01);
        mViewDataBinding.message02TextView.setText(message02);
    }

    public void setButton01(boolean visible, String text, View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.button01TextView.setVisibility(visible ? VISIBLE : GONE);
        mViewDataBinding.button01TextView.setText(text);
        mViewDataBinding.button01TextView.setOnClickListener(listener);
    }

    public void setButton02(boolean visible, String text, View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.button02TextView.setVisibility(visible ? VISIBLE : GONE);
        mViewDataBinding.button02TextView.setText(text);
        mViewDataBinding.button02TextView.setOnClickListener(listener);
    }

    public void setBottomMessage(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.bottomMessageLayout.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setOnCallClickListener(View.OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.callTextView.setOnClickListener(listener);
    }
}
