package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingInformationDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewLeftTitleRightDescriptionDataBinding;

import java.util.List;

public class DailyBookingInformationView extends ConstraintLayout
{
    private DailyViewBookingInformationDataBinding mViewDataBinding;

    public DailyBookingInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingInformationView(Context context, AttributeSet attrs, int defStyleAttr)
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

    public void setInformation(List<Pair<CharSequence, CharSequence>> informationList)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.informationLayout.removeAllViews();

        if (informationList == null || informationList.size() == 0)
        {
            return;
        }

        for (Pair<CharSequence, CharSequence> pair : informationList)
        {
            DailyViewLeftTitleRightDescriptionDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.daily_view_left_title_right_description_data, mViewDataBinding.informationLayout, true);

            dataBinding.titleTextView.setText(pair.first);
            dataBinding.descriptionTextView.setText(pair.second);
        }
    }
}
