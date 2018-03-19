package com.daily.dailyhotel.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class GourmetClusterItem implements ClusterItem
{
    private final Gourmet mGourmet;
    private final LatLng mLatLng;

    public GourmetClusterItem(Gourmet gourmet)
    {
        mGourmet = gourmet;
        mLatLng = new LatLng(gourmet.latitude, gourmet.longitude);
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

    public Gourmet getGourmet()
    {
        return mGourmet;
    }
}
