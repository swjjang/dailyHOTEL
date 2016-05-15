package com.twoheart.dailyhotel.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class HotelClusterItem implements ClusterItem
{
    private final Hotel mHotel;
    private final LatLng mPosition;

    public HotelClusterItem(Hotel hotel)
    {
        mHotel = hotel;
        mPosition = new LatLng(hotel.latitude, hotel.longitude);
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
