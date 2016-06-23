package com.twoheart.dailyhotel.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class HotelClusterItem implements ClusterItem
{
    private final Stay mStay;
    private final LatLng mPosition;

    public HotelClusterItem(Stay stay)
    {
        mStay = stay;
        mPosition = new LatLng(stay.latitude, stay.longitude);
    }

    @Override
    public LatLng getPosition()
    {
        return mPosition;
    }

    public Stay getHotel()
    {
        return mStay;
    }
}
