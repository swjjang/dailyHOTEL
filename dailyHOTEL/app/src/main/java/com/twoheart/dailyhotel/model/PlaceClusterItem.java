package com.twoheart.dailyhotel.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twoheart.dailyhotel.model.Place;

public class PlaceClusterItem implements ClusterItem
{
    private final Place mPlace;
    private final LatLng mPosition;

    public PlaceClusterItem(Place place)
    {
        mPlace = place;
        mPosition = new LatLng(place.latitude, place.longitude);
    }

    @Override
    public LatLng getPosition()
    {
        return mPosition;
    }

    public Place getPlace()
    {
        return mPlace;
    }
}
