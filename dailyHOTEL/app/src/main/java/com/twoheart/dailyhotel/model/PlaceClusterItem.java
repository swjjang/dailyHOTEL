package com.twoheart.dailyhotel.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PlaceClusterItem implements ClusterItem
{
    private final Place mPlace;
    private final LatLng mLatLng;

    public PlaceClusterItem(Place place)
    {
        mPlace = place;
        mLatLng = new LatLng(place.latitude, place.longitude);
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

    public Place getPlace()
    {
        return mPlace;
    }
}
