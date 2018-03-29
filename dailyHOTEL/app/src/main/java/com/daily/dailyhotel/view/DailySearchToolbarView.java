package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewSearchToolbarDataBinding;

public class DailySearchToolbarView extends ConstraintLayout
{
    private DailyViewSearchToolbarDataBinding mViewDataBinding;

    DailySearchToolbarView.OnToolbarListener mToolbarListener;

    public interface OnToolbarListener
    {
        void onTitleClick();

        void onBackClick();

        void onSelectedRadiusPosition(int position);
    }

    public DailySearchToolbarView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailySearchToolbarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailySearchToolbarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_search_toolbar_data, this, true);

        initListener(mViewDataBinding);
    }

    private void initListener(DailyViewSearchToolbarDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.menuImageView.setOnClickListener(v -> {
            if (mToolbarListener == null)
            {
                return;
            }

            mToolbarListener.onBackClick();
        });

        viewDataBinding.titleBackgroundView.setOnClickListener(v -> {
            if (mToolbarListener == null)
            {
                return;
            }

            mToolbarListener.onTitleClick();
        });

        viewDataBinding.radiusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (mToolbarListener == null)
                {
                    return;
                }

                mToolbarListener.onSelectedRadiusPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    public void setOnToolbarListener(OnToolbarListener listener)
    {
        mToolbarListener = listener;
    }

    public void setTitleText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(text);
    }

    public void setSubTitleText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.subTitleTextView.setText(text);
    }

    public void setTitleImageResource(int resId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleIconImageView.setVectorImageResource(resId);
    }

    public void setRadiusSpinnerVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.radiusSpinner.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setRadiusSpinnerAdapter(SpinnerAdapter adapter)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.radiusSpinner.setAdapter(adapter);
    }

    public SpinnerAdapter getRadiusSpinnerAdapter()
    {
        return mViewDataBinding == null ? null : mViewDataBinding.radiusSpinner.getAdapter();
    }

    public void setRadiusSpinnerSelection(int position)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.radiusSpinner.setSelection(position);
    }

    public void showRadiusSpinnerPopup()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.radiusSpinner.performClick();
    }
}
