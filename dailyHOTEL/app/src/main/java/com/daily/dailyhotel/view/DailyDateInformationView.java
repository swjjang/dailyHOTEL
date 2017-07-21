package com.daily.dailyhotel.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.widget.DailyTextView;
import com.twoheart.dailyhotel.R;

public class DailyDateInformationView extends ConstraintLayout
{
    private View mDate1Layout, mDate2Layout;
    private DailyTextView mDate1TitleTextView, mDate1TextView;
    private DailyTextView mDate2TitleTextView, mDate2TextView;
    private View mCenterLineView;
    private DailyTextView mCenterNightsTextView;

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
        View view = LayoutInflater.from(context).inflate(R.layout.daily_view_date_information, this, true);

        mDate1Layout = view.findViewById(R.id.date1Layout);
        mDate1TitleTextView = (DailyTextView) mDate1Layout.findViewById(R.id.date1TitleTextView);
        mDate1TextView = (DailyTextView) mDate1Layout.findViewById(R.id.date1TextView);

        mDate2Layout = view.findViewById(R.id.date2Layout);
        mDate2TitleTextView = (DailyTextView) mDate2Layout.findViewById(R.id.date2TitleTextView);
        mDate2TextView = (DailyTextView) mDate2Layout.findViewById(R.id.date2TextView);

        mCenterLineView = view.findViewById(R.id.centerLineView);
        mCenterNightsTextView = (DailyTextView) view.findViewById(R.id.centerNightsTextView);
    }

    public void setDateVisible(boolean date1Visible, boolean date2Visible)
    {
        if (mDate1Layout == null || mDate2Layout == null || mCenterLineView == null)
        {
            return;
        }

        mDate1Layout.setVisibility(date1Visible ? VISIBLE : GONE);
        mDate2Layout.setVisibility(date2Visible ? VISIBLE : GONE);

        if (date1Visible == true && date2Visible == true)
        {
            mCenterLineView.setVisibility(VISIBLE);
        } else
        {
            mCenterLineView.setVisibility(GONE);
        }
    }

    public void setDate1Text(CharSequence title, CharSequence dateString)
    {
        if (mDate1TitleTextView == null || mDate1TextView == null)
        {
            return;
        }

        mDate1TitleTextView.setText(title);
        mDate1TextView.setText(dateString);
    }

    public void setDate2Text(CharSequence title, CharSequence dateString)
    {
        if (mDate2TitleTextView == null || mDate2TextView == null)
        {
            return;
        }

        mDate2TitleTextView.setText(title);
        mDate2TextView.setText(dateString);
    }

    public void setCenterNightsVisible(boolean visible)
    {
        if (mCenterNightsTextView == null)
        {
            return;
        }

        mCenterNightsTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setCenterNightsText(CharSequence nights)
    {
        if (mCenterNightsTextView == null)
        {
            return;
        }

        mCenterNightsTextView.setText(nights);
    }
}
