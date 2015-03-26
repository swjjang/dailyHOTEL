package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Util;

public class HotelGradeView extends FrameLayout
{

	private TextView tvHotelGradeName;

	private String mHotelGradeName;
	private String mHotelGradeCode;
	private int mHotelGradeColor;

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

		addView(tvHotelGradeName);
		setPadding(Util.dpToPx(context, 5.5), 2, Util.dpToPx(context, 5.5), 1);

	}

	public void setHotelGradeCode(String hotelGradeCode)
	{
		mHotelGradeCode = hotelGradeCode;

		if (mHotelGradeCode.equals("biz") | mHotelGradeCode.equals("hostel") | mHotelGradeCode.equals("grade1") | mHotelGradeCode.equals("grade2") | mHotelGradeCode.equals("grade3"))
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_hotel));

			if (mHotelGradeCode.equals("biz"))
				setHotelGradeName(getContext().getString(R.string.grade_biz));
			else if (mHotelGradeCode.equals("hostel"))
				setHotelGradeName(getContext().getString(R.string.grade_hostel));
			else if (mHotelGradeCode.equals("grade1"))
				setHotelGradeName(getContext().getString(R.string.grade_1));
			else if (mHotelGradeCode.equals("grade2"))
				setHotelGradeName(getContext().getString(R.string.grade_2));
			else if (mHotelGradeCode.equals("grade3"))
				setHotelGradeName(getContext().getString(R.string.grade_3));

		} else if (mHotelGradeCode.equals("boutique"))
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_boutique));
			setHotelGradeName(getContext().getString(R.string.grade_boutique));

		} else if (mHotelGradeCode.equals("residence"))
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_residence));
			setHotelGradeName(getContext().getString(R.string.grade_residence));

		} else if (mHotelGradeCode.equals("resort") | mHotelGradeCode.equals("pension") | mHotelGradeCode.equals("condo"))
		{
			setHotelGradeColor(getContext().getResources().getColor(R.color.grade_resort_pension_condo));

			if (mHotelGradeCode.equals("resort"))
				setHotelGradeName(getContext().getString(R.string.grade_resort));
			else if (mHotelGradeCode.equals("pension"))
				setHotelGradeName(getContext().getString(R.string.grade_pension));
			else if (mHotelGradeCode.equals("condo"))
				setHotelGradeName(getContext().getString(R.string.grade_condo));

		} else if (mHotelGradeCode.equals("special"))
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
		mHotelGradeName = hotelGradeName;
		tvHotelGradeName.setText(mHotelGradeName);
		tvHotelGradeName.requestLayout();

	}

	private void setHotelGradeColor(String hotelGradeColor)
	{
		mHotelGradeColor = Color.parseColor(hotelGradeColor);
		setBackgroundColor(mHotelGradeColor);
	}

	private void setHotelGradeColor(int parsedColor)
	{
		mHotelGradeColor = parsedColor;
		setBackgroundColor(mHotelGradeColor);
	}

}
