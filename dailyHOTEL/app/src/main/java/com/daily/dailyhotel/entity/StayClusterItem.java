package com.daily.dailyhotel.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class StayClusterItem implements ClusterItem
{
    private final Stay mStay;
    private final LatLng mLatLng;

    public StayClusterItem(Stay stay)
    {
        mStay = stay;
        mLatLng = new LatLng(stay.latitude, stay.longitude);
    }

    @Override
    public LatLng getPosition()
    {
        return mLatLng;
    }

    @Override
    public String getTitle()
    {
        return null;
    }

    @Override
    public String getSnippet()
    {
        return null;
    }

    public Stay getStay()
    {
        return mStay;
    }
}
