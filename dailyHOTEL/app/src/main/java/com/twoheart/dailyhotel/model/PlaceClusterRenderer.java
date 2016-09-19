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

public class PlaceClusterRenderer extends DefaultClusterRenderer<PlaceClusterItem>
{
    private Context mContext;
    private PlaceClusterItem mSelectedPlaceClusterItem;
    private OnSelectedClusterItemListener mOnSelectedClusterItemListener;
    private OnClusterRenderedListener mOnClusterRenderedListener;

    public PlaceClusterRenderer(Context context, GoogleMap map, ClusterManager<PlaceClusterItem> clusterManager)
    {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(PlaceClusterItem item, MarkerOptions markerOptions)
    {
        if (mOnClusterRenderedListener != null)
        {
            mOnClusterRenderedListener.onClusterRenderedListener(Renderer.CLUSTER_ITEM);
        }

        Place place = item.getPlace();

        PlaceRenderer placeRenderer = new PlaceRenderer(mContext, place.discountPrice, place.getGradeMarkerResId());

        BitmapDescriptor icon = placeRenderer.getBitmap(false);

        if (icon != null)
        {
            markerOptions.icon(icon);
            markerOptions.anchor(0.0f, 1.0f);
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<PlaceClusterItem> cluster, MarkerOptions markerOptions)
    {
        if (cluster == null || cluster.getSize() == 0)
        {
            markerOptions.visible(false);
            return;
        }

        if (mOnClusterRenderedListener != null)
        {
            mOnClusterRenderedListener.onClusterRenderedListener(Renderer.CLUSTER);
        }

        PlaceRegionRenderer placeRegionRenderer = new PlaceRegionRenderer(mContext, cluster.getSize());

        BitmapDescriptor icon = placeRegionRenderer.getBitmap();

        if (icon != null)
        {
            markerOptions.icon(icon).anchor(0.5f, 0.5f);
        }
    }

    @Override
    protected void onClusterItemRendered(PlaceClusterItem clusterItem, Marker marker)
    {
        if (mSelectedPlaceClusterItem != null)
        {
            LatLng selectedLatLng = mSelectedPlaceClusterItem.getPosition();
            LatLng currentLatLng = clusterItem.getPosition();

            if (selectedLatLng.latitude == currentLatLng.latitude && selectedLatLng.longitude == currentLatLng.longitude)
            {
                mSelectedPlaceClusterItem = null;

                if (mOnSelectedClusterItemListener != null)
                {
                    mOnSelectedClusterItemListener.onSelectedClusterItemListener(marker);
                }
            }
        }
    }

    public void setOnClusterRenderedListener(OnClusterRenderedListener listener)
    {
        mOnClusterRenderedListener = listener;
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<PlaceClusterItem> cluster, float zoom)
    {
        return (Float.compare(zoom, 13.0f) < 0);
    }

    public void setSelectedClusterItem(PlaceClusterItem placeClusterItem)
    {
        mSelectedPlaceClusterItem = placeClusterItem;
    }

    public void setSelectedClusterItemListener(OnSelectedClusterItemListener listener)
    {
        mOnSelectedClusterItemListener = listener;
    }

    public enum Renderer
    {
        CLUSTER,
        CLUSTER_ITEM,
    }

    public interface OnSelectedClusterItemListener
    {
        void onSelectedClusterItemListener(Marker marker);
    }

    public interface OnClusterRenderedListener
    {
        void onClusterRenderedListener(Renderer renderer);
    }
}
