package com.twoheart.dailyhotel.view;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twoheart.dailyhotel.model.Hotel;

public class HotelClusterItem implements ClusterItem
{
	private final Hotel mHotel;
	private final LatLng mPosition;

	public HotelClusterItem(Hotel hotel)
	{
		mHotel = hotel;
		mPosition = new LatLng(hotel.mLatitude, hotel.mLongitude);
	}

	@Override
	public LatLng getPosition()
	{
		return mPosition;
	}

	public Hotel getHotel()
	{
		return mHotel;
	}
}
