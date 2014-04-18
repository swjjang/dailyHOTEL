package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class HotelGradeView extends FrameLayout {
	
	private Context mContext;
	private TextView tvHotelGradeName;
	
	private String mHotelGradeName;
	private String mHotelGradeCode;
	private int mHotelGradeColor;
	
	public HotelGradeView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	
	public HotelGradeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public HotelGradeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}
	
	private void init() {
		
		setPadding(dpToPx(5.5), 2, dpToPx(5.5), 1);
		tvHotelGradeName = new TextView(mContext);
		
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.MATCH_PARENT);
		
		tvHotelGradeName.setTextColor(getResources().getColor(android.R.color.white));
		tvHotelGradeName.setTextSize((float) 11.5);
		tvHotelGradeName.setGravity(Gravity.CENTER);
		tvHotelGradeName.setSingleLine(true);
		
		addView(tvHotelGradeName);
		
	}
	
	private int dpToPx(double dp) {
		float scale = getResources().getDisplayMetrics().density; 
		return (int) (dp * scale + 0.5f);
		
	}

	public void setHotelGradeCode(String hotelGradeCode) {
		mHotelGradeCode = hotelGradeCode;
		
		if (mHotelGradeCode.equals("biz") | mHotelGradeCode.equals("hostel") | mHotelGradeCode.equals("grade1") | 
				mHotelGradeCode.equals("grade2") | mHotelGradeCode.equals("grade3")) {
			setHotelGradeColor("#055870");
			
			if (mHotelGradeCode.equals("biz"))
				setHotelGradeName("비지니스");
			else if (mHotelGradeCode.equals("hostel"))
				setHotelGradeName("호스텔");
			else if (mHotelGradeCode.equals("grade1"))
				setHotelGradeName("1급");
			else if (mHotelGradeCode.equals("grade2"))
				setHotelGradeName("2급");
			else if (mHotelGradeCode.equals("grade3"))
				setHotelGradeName("3급");

		} else if (mHotelGradeCode.equals("boutique")) {
			setHotelGradeColor("#9f2d58");
			setHotelGradeName("부띠끄");

		} else if (mHotelGradeCode.equals("residence")) {
			setHotelGradeColor("#407f67");
			setHotelGradeName("레지던스");

		} else if (mHotelGradeCode.equals("resort") | mHotelGradeCode.equals("pension") | mHotelGradeCode.equals("condo")) {
			setHotelGradeColor("#cf8d14");
			
			if (mHotelGradeCode.equals("resort"))
				setHotelGradeName("리조트");
			else if (mHotelGradeCode.equals("pension"))
				setHotelGradeName("펜션");
			else if (mHotelGradeCode.equals("condo"))
				setHotelGradeName("콘도");

		} else if (mHotelGradeCode.equals("special")) {
			setHotelGradeColor("#ab380a");
			setHotelGradeName("특급");

		} else {
			setHotelGradeColor("#808080");
			setHotelGradeName("미정");
		}
		
	}
	
	private void setHotelGradeName(String hotelGradeName) {
		mHotelGradeName = hotelGradeName;
		tvHotelGradeName.setText(mHotelGradeName);
		tvHotelGradeName.invalidate();
		invalidate();
	}
	
	private void setHotelGradeColor(String hotelGradeColor) {
		mHotelGradeColor = Color.parseColor(hotelGradeColor);
		setBackgroundColor(mHotelGradeColor);
		invalidate();
	}
	
	

}
