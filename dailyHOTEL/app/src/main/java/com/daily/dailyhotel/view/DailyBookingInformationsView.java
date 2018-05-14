package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingInformationDataBinding;

public class DailyBookingInformationsView extends ConstraintLayout
{
    private DailyViewBookingInformationDataBinding mViewDataBinding;

    public DailyBookingInformationsView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingInformationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingInformationsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_information_data, this, true);
    }

    public void setTitle(CharSequence title)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(title);
    }

    public void setTitle(@StringRes int resid)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(resid);
    }

    public void removeAllInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        if (mViewDataBinding.informationLayout.getChildCount() > 0)
        {
            mViewDataBinding.informationLayout.removeAllViews();
        }
    }

    public void addInformation(String title, String description)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        DailyBookingInformationView informationView = new DailyBookingInformationView(getContext());
        informationView.setInformation(title, description);

        mViewDataBinding.informationLayout.addView(informationView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
