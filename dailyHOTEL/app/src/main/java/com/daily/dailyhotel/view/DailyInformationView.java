package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewLeftTitleRightDescriptionDataBinding;

import java.util.List;

public class DailyInformationView extends ConstraintLayout
{
    private DailyTextView mTitleTextView;
    private LinearLayout mInformationLayout;

    public DailyInformationView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyInformationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyInformationView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.daily_view_booking_information, this, true);

        mTitleTextView = (DailyTextView) view.findViewById(R.id.titleTextView);
        mInformationLayout = (LinearLayout) view.findViewById(R.id.informationLayout);
    }

    public void setTitle(CharSequence title)
    {
        if (mTitleTextView == null)
        {
            return;
        }

        mTitleTextView.setText(title);
    }

    public void setTitle(@StringRes int resid)
    {
        if (mTitleTextView == null)
        {
            return;
        }

        mTitleTextView.setText(resid);
    }

    public void setInformation(List<Pair<CharSequence, CharSequence>> informationList)
    {
        if (mInformationLayout == null)
        {
            return;
        }

        mInformationLayout.removeAllViews();

        if (informationList == null || informationList.size() == 0)
        {
            return;
        }

        for (Pair<CharSequence, CharSequence> pair : informationList)
        {
            DailyViewLeftTitleRightDescriptionDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.daily_view_left_title_right_description_data, mInformationLayout, true);

            dataBinding.titleTextView.setText(pair.first);
            dataBinding.descriptionTextView.setText(pair.second);
        }
    }
}
