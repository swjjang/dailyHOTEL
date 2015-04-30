package com.twoheart.dailyhotel.util.ui;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.fragment.HotelListMapFragment;
import com.twoheart.dailyhotel.fragment.HotelListMapFragment.OnMakerInfoWindowListener;

public class HotelMapLayout extends FrameLayout implements OnMakerInfoWindowListener
{
	private HotelListMapFragment mHotelListMapFragment;
	private Marker mMarker;
	private View mInfoWindowView;

	public HotelMapLayout(Context context)
	{
		super(context);
		initLayout(context);
	}

	public HotelMapLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initLayout(context);
	}

	public HotelMapLayout(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		initLayout(context);
	}

	public HotelMapLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		initLayout(context);
	}

	private void initLayout(Context context)
	{

	}

	public void setMapFragment(HotelListMapFragment hotelListMapFragment)
	{
		mHotelListMapFragment = hotelListMapFragment;

		if (hotelListMapFragment != null)
		{
			mHotelListMapFragment.setOnMakerInfoWindowListener(this);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev)
	{
		boolean ret = false;
		// Make sure that the infoWindow is shown and we have all the needed references
		if (mHotelListMapFragment != null && mHotelListMapFragment.getMap() != null && mMarker != null && mMarker.isInfoWindowShown() && mInfoWindowView != null)
		{
			// Get a marker position on the screen
			Point point = mHotelListMapFragment.getMap().getProjection().toScreenLocation(mMarker.getPosition());

			// Make a copy of the MotionEvent and adjust it's location
			// so it is relative to the infoWindow left top corner
			MotionEvent copyEv = MotionEvent.obtain(ev);
			copyEv.offsetLocation(-point.x + (mInfoWindowView.getWidth() / 2), -point.y + mInfoWindowView.getHeight());

			// Dispatch the adjusted MotionEvent to the infoWindow
			ret = mInfoWindowView.dispatchTouchEvent(copyEv);
		}
		// If the infoWindow consumed the touch event, then just return true.
		// Otherwise pass this event to the super class and return it's result
		return ret || super.dispatchTouchEvent(ev);
	}

	@Override
	public void setInfoWindow(Marker marker, View infoWindow)
	{
		mMarker = marker;
		mInfoWindowView = infoWindow;
	}
}
