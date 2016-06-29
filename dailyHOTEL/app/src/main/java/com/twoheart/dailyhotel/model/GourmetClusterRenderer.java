package com.twoheart.dailyhotel.model;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class GourmetClusterRenderer extends PlaceClusterRenderer
{
    public GourmetClusterRenderer(Context context, GoogleMap map, ClusterManager<PlaceClusterItem> clusterManager)
    {
        super(context, map, clusterManager);
    }

    @Override
    protected PlaceRenderer newInstancePlaceRenderer(Context context, Place place)
    {
        Gourmet gourmet = (Gourmet)place;

        return new PlaceRenderer(context, gourmet.discountPrice, gourmet.grade.getMarkerResId());
    }
}
