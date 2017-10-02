package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewFloatingActionDataBinding;

public class DailyFloatingActionView extends ConstraintLayout
{
    private DailyViewFloatingActionDataBinding mViewDataBinding;

    public DailyFloatingActionView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyFloatingActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyFloatingActionView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_floating_action_data, this, true);

        setBackgroundResource(R.drawable.fab);
    }

    public void setOnViewOptionClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.viewActionTextView.setOnClickListener(listener);
    }

    public void setOnFilterOptionClickListener(OnClickListener listener)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.filterActionTextView.setOnClickListener(listener);
    }

    public void setViewOptionEnable(boolean enable)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.viewActionTextView.setEnabled(enable);
        mViewDataBinding.viewActionTextView.setAlpha(enable ? 1.0f : 0.8f);
    }

    public void setFilterOptionEnable(boolean enable)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.filterActionTextView.setEnabled(enable);

        float alpha = enable ? 1.0f : 0.8f;

        mViewDataBinding.filterActionTextView.setAlpha(alpha);
        mViewDataBinding.filterOnView.setAlpha(alpha);
    }

    public void setViewOptionListSelected()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.viewActionTextView.setText(R.string.label_list);
        mViewDataBinding.viewActionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ic_fab_02_list, 0, 0, 0);
    }

    public void setViewOptionMapSelected()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.viewActionTextView.setText(R.string.label_map);
        mViewDataBinding.viewActionTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vector_ic_fab_01_map, 0, 0, 0);
    }

    public void setFilterOptionSelected(boolean selected)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.filterActionTextView.setSelected(selected);
        mViewDataBinding.filterOnView.setVisibility(selected ? VISIBLE : INVISIBLE);
    }
}
