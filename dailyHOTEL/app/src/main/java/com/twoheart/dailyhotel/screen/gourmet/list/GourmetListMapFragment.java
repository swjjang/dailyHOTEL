package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.GourmetClusterRenderer;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceClusterItem;
import com.twoheart.dailyhotel.model.PlaceClusterRenderer;
import com.twoheart.dailyhotel.model.PlaceRenderer;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;

public class GourmetListMapFragment extends PlaceListMapFragment
{
    public GourmetListMapFragment()
    {
    }

    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new GourmetMapViewPagerAdapter(context);
    }

    @Override
    protected PlaceRenderer newInstancePlaceRenderer(Context context, Place place)
    {
        Gourmet gourmet = (Gourmet) place;

        return new PlaceRenderer(context, gourmet.discountPrice, gourmet.grade.getMarkerResId());
    }

    @Override
    protected PlaceClusterRenderer getPlaceClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<PlaceClusterItem> clusterManager)
    {
        return new GourmetClusterRenderer(context, googleMap, clusterManager);
    }
}
