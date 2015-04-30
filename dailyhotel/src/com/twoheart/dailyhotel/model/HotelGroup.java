package com.twoheart.dailyhotel.model;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.util.ui.HotelListViewItem;

public class HotelGroup
{
	public boolean mIsOpend;

	private String mRegion;
	private ArrayList<HotelListViewItem> mArrayList;
	private LatLng mLatLng;

	public HotelGroup(String region, LatLng latlng)
	{
		mRegion = region;
		mLatLng = latlng;

		mArrayList = new ArrayList<HotelListViewItem>();
	}

	public int getCount()
	{
		if (mArrayList == null)
		{
			return 0;
		}

		return mArrayList.size();
	}

	public void add(HotelListViewItem item)
	{
		if (mArrayList == null || item == null)
		{
			return;
		}

		mArrayList.add(item);
	}

	public ArrayList<HotelListViewItem> getHotelList()
	{
		return mArrayList;
	}

	public boolean contains(String detailRegion)
	{
		if (mRegion == null)
		{
			return false;
		}

		return mRegion.contains(detailRegion);
	}

	public LatLng getLatLng()
	{
		return mLatLng;
	}
}
