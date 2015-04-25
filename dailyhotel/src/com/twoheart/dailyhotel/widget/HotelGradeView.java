package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class HotelGradeView extends FrameLayout
{
	private TextView tvHotelGradeName;

	public HotelGradeView(Context context)
	{
		super(context);
		init();
	}

	public HotelGradeView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public HotelGradeView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		Context context = getContext();
		tvHotelGradeName = new TextView(context);

		tvHotelGradeName.setTextColor(getResources().getColor(android.R.color.white));
		tvHotelGradeName.setTextSize((float) 11.5);
		tvHotelGradeName.setGravity(Gravity.CENTER);
		tvHotelGradeName.setSingleLine(true);

		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		addView(tvHotelGradeName, layoutParams);
		setPadding(Util.dpToPx(context, 5.5), 2, Util.dpToPx(context, 5.5), 1);

	}

	public void setHotelGradeCode(String hotelGradeCode)
	{
		if ("biz".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));
			setHotelGradeName(getContext().getString(R.string.grade_biz));
		} else if ("hostel".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));
			setHotelGradeName(getContext().getString(R.string.grade_hostel));
		} else if ("grade1".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));
			setHotelGradeName(getContext().getString(R.string.grade_1));
		} else if ("grade2".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));
			setHotelGradeName(getContext().getString(R.string.grade_2));
		} else if ("grade3".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));
			setHotelGradeName(getContext().getString(R.string.grade_3));
		} else if ("boutique".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_boutique));
			setHotelGradeName(getContext().getString(R.string.grade_boutique));
		} else if ("residence".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_residence));
			setHotelGradeName(getContext().getString(R.string.grade_residence));
		} else if ("resort".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_resort_pension_condo));
			setHotelGradeName(getContext().getString(R.string.grade_resort));
		} else if ("pension".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_resort_pension_condo));
			setHotelGradeName(getContext().getString(R.string.grade_pension));
		} else if ("condo".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_resort_pension_condo));
			setHotelGradeName(getContext().getString(R.string.grade_condo));
		} else if ("special".equalsIgnoreCase(hotelGradeCode) == true)
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_special));
			setHotelGradeName(getContext().getString(R.string.grade_special));
		} else
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_not_yet));
			setHotelGradeName(getContext().getString(R.string.grade_not_yet));
		}
	}

	private void setHotelGradeName(String hotelGradeName)
	{
		if (tvHotelGradeName == null)
		{
			return;
		}

		tvHotelGradeName.setText(hotelGradeName);
	}

	private void setHotelGradeColor(int parsedColor)
	{
		setBackgroundColor(parsedColor);
	}
}
