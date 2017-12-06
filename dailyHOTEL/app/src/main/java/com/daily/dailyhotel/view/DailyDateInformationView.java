package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewDateInformationDataBinding;

public class DailyDateInformationView extends ConstraintLayout
{
    private DailyViewDateInformationDataBinding mViewDataBinding;

    public DailyDateInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyDateInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyDateInformationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_date_information_data, this, true);
    }

    public void setDateVisible(boolean date1Visible, boolean date2Visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1Layout.setVisibility(date1Visible ? VISIBLE : GONE);
        mViewDataBinding.date2Layout.setVisibility(date2Visible ? VISIBLE : GONE);

        if (date1Visible == true && date2Visible == true)
        {
            mViewDataBinding.centerLineView.setVisibility(VISIBLE);
        } else
        {
            mViewDataBinding.centerLineView.setVisibility(GONE);
        }
    }

    public void setOnDateClickListener(View.OnClickListener listener1, View.OnClickListener listener2)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1Layout.setOnClickListener(listener1);
        mViewDataBinding.date2Layout.setOnClickListener(listener2);
    }

    public void setDate1Text(CharSequence title, CharSequence dateString)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TitleTextView.setText(title);
        mViewDataBinding.date1TextView.setText(dateString);
    }

    public void setDate1TitleTextColor(int color)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TitleTextView.setTextColor(color);
    }

    public void setDate1TitleTextSize(float fontSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    public void setDate1DescriptionTextColor(int color)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TextView.setTextColor(color);
    }

    public void setDate1DescriptionTextSize(float fontSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    public void setDate1DescriptionTextDrawable(int left, int top, int right, int bottom)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TextView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    public void setData1TextSize(float titleSize, float dateSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date1TitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        mViewDataBinding.date1TextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dateSize);
    }

    public void setDate2Text(CharSequence title, CharSequence dateString)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TitleTextView.setText(title);
        mViewDataBinding.date2TextView.setText(dateString);
    }

    public void setDate2TitleTextColor(int color)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TitleTextView.setTextColor(color);
    }

    public void setDate2TitleTextSize(float fontSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    public void setDate2DescriptionTextColor(int color)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TextView.setTextColor(color);
    }

    public void setDate2DescriptionTextSize(float fontSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize);
    }

    public void setDate2DescriptionTextDrawable(int left, int top, int right, int bottom)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TextView.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    public void setData2TextSize(float titleSize, float dateSize)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.date2TitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);
        mViewDataBinding.date2TextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, dateSize);
    }

    public void setCenterNightsVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.centerNightsTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setCenterNightsText(CharSequence nights)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.centerNightsTextView.setText(nights);
    }
}
