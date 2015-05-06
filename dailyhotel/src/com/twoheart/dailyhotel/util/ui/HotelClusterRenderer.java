package com.twoheart.dailyhotel.util.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twoheart.dailyhotel.model.HotelRegionRenderer;
import com.twoheart.dailyhotel.model.HotelRenderer;

public class HotelClusterRenderer extends
		DefaultClusterRenderer<HotelClusterItem>
{
	private Context mContext;
	private HotelClusterItem mSelectedHotelClusterItem;
	private OnSelectedClusterItemListener mOnSelectedClusterItemListener;
	private GoogleMap mGoogleMap;

	public interface OnSelectedClusterItemListener
	{
		public void onSelectedClusterItemListener(Marker marker);
	}

	public HotelClusterRenderer(Context context, GoogleMap map, ClusterManager<HotelClusterItem> clusterManager)
	{
		super(context, map, clusterManager);

		mContext = context;
		mGoogleMap = map;
	}

	@Override
	protected void onBeforeClusterItemRendered(HotelClusterItem item, MarkerOptions markerOptions)
	{
		HotelRenderer hotelRenderer = new HotelRenderer(mContext, item.getHotel());

		BitmapDescriptor icon = hotelRenderer.getBitmap();

		if (icon != null)
		{
			markerOptions.icon(icon);
		} else
		{
			super.onBeforeClusterItemRendered(item, markerOptions);
		}
	}

	@Override
	protected void onBeforeClusterRendered(Cluster<HotelClusterItem> cluster, MarkerOptions markerOptions)
	{
		HotelRegionRenderer hotelRegionRenderer = new HotelRegionRenderer(mContext, cluster.getSize());

		BitmapDescriptor icon = hotelRegionRenderer.getBitmap();

		if (icon != null)
		{
			markerOptions.icon(icon).anchor(0.5f, 0.5f);
		} else
		{
			super.onBeforeClusterRendered(cluster, markerOptions);
		}
	}

	@Override
	protected void onClusterItemRendered(HotelClusterItem clusterItem, Marker marker)
	{
		if (mSelectedHotelClusterItem != null)
		{
			LatLng selectedLatLng = mSelectedHotelClusterItem.getPosition();
			LatLng currentLatLng = clusterItem.getPosition();

			if (selectedLatLng.latitude == currentLatLng.latitude && selectedLatLng.longitude == currentLatLng.longitude)
			{
				mSelectedHotelClusterItem = null;

				if (mOnSelectedClusterItemListener != null)
				{
					mOnSelectedClusterItemListener.onSelectedClusterItemListener(marker);
				}
			}
		}

		super.onClusterItemRendered(clusterItem, marker);
	}

	@Override
	protected boolean shouldRenderAsCluster(Cluster<HotelClusterItem> cluster, float zoom)
	{
		if (Float.compare(zoom, 13.0f) >= 0)
		{
			return false;
		} else
		{
			return true;
		}
	}

	public void setSelectedClusterItem(HotelClusterItem hotelClusterItem)
	{
		mSelectedHotelClusterItem = hotelClusterItem;
	}

	public void setSelectedClusterItemListener(OnSelectedClusterItemListener listener)
	{
		mOnSelectedClusterItemListener = listener;
	}
}
