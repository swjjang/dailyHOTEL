package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterManager;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceClusterItem;
import com.twoheart.dailyhotel.model.PlaceClusterRenderer;
import com.twoheart.dailyhotel.model.PlaceRenderer;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.StayClusterRenderer;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;

public class StayMapFragment extends PlaceListMapFragment
{
    public StayMapFragment()
    {
    }

    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new HotelMapViewPagerAdapter(context);
    }

    @Override
    protected PlaceRenderer newInstancePlaceRenderer(Context context, Place place)
    {
        Stay stay = (Stay)place;

        return new PlaceRenderer(context, stay.averageDiscountPrice, stay.getGrade().getMarkerResId());
    }

    @Override
    protected PlaceClusterRenderer getPlaceClusterRenderer(Context context, GoogleMap googleMap, ClusterManager<PlaceClusterItem> clusterManager)
    {
        return new StayClusterRenderer(context, googleMap, clusterManager);
    }
}
