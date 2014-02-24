package com.twoheart.dailyhotel.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ViewPagerCustom extends ViewPager{
	public ViewPagerCustom(Context context) {
		super(context);
	}
	
	public ViewPagerCustom(Context context, AttributeSet attrs) {
		super(context,attrs);
	}
	
	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
		
		if (v.getClass().getPackage().getName().startsWith("maps.")) {
	        return true;
	    } else if(v != this && v instanceof ViewPager) {
          return true;
       }
		
		return super.canScroll(v, checkV, dx, x, y);
	}
}
