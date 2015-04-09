package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DayView extends LinearLayout
{
	private TextView mDayTextView;
	private TextView mWeekTextView;

	public DayView(Context context)
	{
		super(context);

		initLayout();
	}

	public DayView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initLayout();
	}

	public DayView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);

		initLayout();
	}

	public DayView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);

		initLayout();
	}

	private void initLayout()
	{
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER_HORIZONTAL);

		mDayTextView = new TextView(getContext());
		mWeekTextView = new TextView(getContext());

		addView(mDayTextView);
		addView(mWeekTextView);
	}

	public void setSelected(boolean selected)
	{
		mDayTextView.setSelected(selected);
	}
}
