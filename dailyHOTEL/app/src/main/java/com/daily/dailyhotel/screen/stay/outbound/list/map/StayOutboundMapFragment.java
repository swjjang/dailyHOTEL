package com.daily.dailyhotel.screen.stay.outbound.list.map;

import android.support.annotation.NonNull;

import com.daily.base.BaseSupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.twoheart.dailyhotel.model.PlaceClusterItem;

public class StayOutboundMapFragment extends BaseSupportMapFragment<StayOutboundMapFragmentPresenter>//
    implements ClusterManager.OnClusterClickListener<PlaceClusterItem>, ClusterManager.OnClusterItemClickListener<PlaceClusterItem>
{
    @NonNull
    @Override
    protected StayOutboundMapFragmentPresenter createInstancePresenter()
    {
        return new StayOutboundMapFragmentPresenter(this);
    }

    @Override
    public boolean onClusterClick(Cluster<PlaceClusterItem> cluster)
    {
        return false;
    }

    @Override
    public boolean onClusterItemClick(PlaceClusterItem item, Marker marker)
    {
        return false;
    }
}
