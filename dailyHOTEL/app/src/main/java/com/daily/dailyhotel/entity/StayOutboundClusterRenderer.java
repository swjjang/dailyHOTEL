package com.daily.dailyhotel.entity;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceRegionRenderer;
import com.twoheart.dailyhotel.model.PlaceRenderer;

public class StayOutboundClusterRenderer extends DefaultClusterRenderer<StayOutboundClusterItem>
{
    private Context mContext;
    private StayOutboundClusterItem mClusterItem;
    private OnSelectedClusterItemListener mOnSelectedClusterItemListener;
    private OnClusterRenderedListener mOnClusterRenderedListener;

    public StayOutboundClusterRenderer(Context context, GoogleMap map, ClusterManager<StayOutboundClusterItem> clusterManager)
    {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(StayOutboundClusterItem clusterItem, MarkerOptions markerOptions)
    {
        if (mOnClusterRenderedListener != null)
        {
            mOnClusterRenderedListener.onClusterRenderedListener(Renderer.CLUSTER_ITEM);
        }

        StayOutbound stayOutbound = clusterItem.getStayOutbound();

        PlaceRenderer placeRenderer = new PlaceRenderer(mContext, stayOutbound.nightlyRate, R.drawable.bg_hotel_price_900034);

        BitmapDescriptor icon = placeRenderer.getBitmap(false);

        if (icon != null)
        {
            markerOptions.icon(icon);
            markerOptions.anchor(0.0f, 1.0f);
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<StayOutboundClusterItem> cluster, MarkerOptions markerOptions)
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
    protected void onClusterItemRendered(StayOutboundClusterItem clusterItem, Marker marker)
    {
        if (mClusterItem != null)
        {
            LatLng selectedLatLng = mClusterItem.getPosition();
            LatLng currentLatLng = clusterItem.getPosition();

            if (selectedLatLng.latitude == currentLatLng.latitude && selectedLatLng.longitude == currentLatLng.longitude)
            {
                mClusterItem = null;

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
    protected boolean shouldRenderAsCluster(Cluster<StayOutboundClusterItem> cluster, float zoom)
    {
        //        return (Float.compare(zoom, 13.0f) < 0);
        return false;
    }

    public void setSelectedClusterItem(StayOutboundClusterItem placeClusterItem)
    {
        mClusterItem = placeClusterItem;
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
