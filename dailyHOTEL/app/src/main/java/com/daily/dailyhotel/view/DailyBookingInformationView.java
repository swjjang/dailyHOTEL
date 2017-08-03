package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewLeftTitleRightDescriptionDataBinding;

public class DailyBookingInformationView extends ConstraintLayout
{
    private DailyViewLeftTitleRightDescriptionDataBinding mViewDataBinding;

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
        setMinHeight(ScreenUtils.dpToPx(context, 36));

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_left_title_right_description_data, this, true);
    }

    public void setInformation(CharSequence title, CharSequence description)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(title);
        mViewDataBinding.descriptionTextView.setText(description);
    }
}
