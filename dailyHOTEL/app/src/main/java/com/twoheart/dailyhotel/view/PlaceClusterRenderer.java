package com.twoheart.dailyhotel.view;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twoheart.dailyhotel.model.HotelRegionRenderer;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceRenderer;

public class PlaceClusterRenderer
		extends DefaultClusterRenderer<PlaceClusterItem>
{
	public enum Renderer
	{
		CLUSTER, CLUSTER_ITEM,
	}

	private Context mContext;
	private PlaceClusterItem mSelectedPlaceClusterItem;
	private OnSelectedClusterItemListener mOnSelectedClusterItemListener;
	private OnClusterRenderedListener mOnClusterRenderedListener;

	public interface OnSelectedClusterItemListener
	{
		public void onSelectedClusterItemListener(Marker marker);
	}

	public interface OnClusterRenderedListener
	{
		public void onClusterRenderedListener(Renderer renderer);
	}

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

		PlaceRenderer placeRenderer = new PlaceRenderer(mContext, place.discountPrice, place.grade.getMarkerResId());

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
		if (mOnClusterRenderedListener != null)
		{
			mOnClusterRenderedListener.onClusterRenderedListener(Renderer.CLUSTER);
		}

		HotelRegionRenderer hotelRegionRenderer = new HotelRegionRenderer(mContext, cluster.getSize());

		BitmapDescriptor icon = hotelRegionRenderer.getBitmap();

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
		if (Float.compare(zoom, 13.0f) >= 0)
		{
			return false;
		} else
		{
			return true;
		}
	}

	public void setSelectedClusterItem(PlaceClusterItem placeClusterItem)
	{
		mSelectedPlaceClusterItem = placeClusterItem;
	}

	public void setSelectedClusterItemListener(OnSelectedClusterItemListener listener)
	{
		mOnSelectedClusterItemListener = listener;
	}
}
