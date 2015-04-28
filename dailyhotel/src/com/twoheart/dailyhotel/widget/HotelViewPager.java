package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HotelViewPager extends ViewPager
{
	public HotelViewPager(Context context)
	{
		super(context);
	}

	public HotelViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y)
	{
		if (v != null)
			if (v.getClass() != null)
				if (v.getClass().getPackage() != null)
					if (v.getClass().getPackage().getName().startsWith("maps."))
					{
						return true;
					} else if (v != this && v instanceof ViewPager)
					{
						return true;
					}

		return super.canScroll(v, checkV, dx, x, y);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
		{
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				this.getParent().requestDisallowInterceptTouchEvent(true);
				return true;
			} else
			{
				return super.onInterceptTouchEvent(event);
			}
		} else
		{
			return super.onInterceptTouchEvent(event);
		}
	}
}
