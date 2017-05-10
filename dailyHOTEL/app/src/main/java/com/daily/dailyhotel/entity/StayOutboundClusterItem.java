package com.daily.dailyhotel.entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.twoheart.dailyhotel.model.Place;

public class StayOutboundClusterItem implements ClusterItem
{
    private final StayOutbound mStayOutbound;
    private final LatLng mLatLng;

    public StayOutboundClusterItem(StayOutbound stayOutbound)
    {
        mStayOutbound = stayOutbound;
        mLatLng = new LatLng(mStayOutbound.latitude, mStayOutbound.longitude);
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

    public StayOutbound getStayOutbound()
    {
        return mStayOutbound;
    }
}
