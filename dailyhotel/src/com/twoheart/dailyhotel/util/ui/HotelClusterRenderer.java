package com.twoheart.dailyhotel.util.ui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twoheart.dailyhotel.model.HotelRegionRenderer;
import com.twoheart.dailyhotel.model.HotelRenderer;
import com.twoheart.dailyhotel.util.ExLog;

public class HotelClusterRenderer extends
		DefaultClusterRenderer<HotelClusterItem>
{
	private Context mContext;
	private HotelClusterItem mSelectedHotelClusterItem;
	private OnSelectedClusterItemListener mOnSelectedClusterItemListener;
	
	private int mItemSizeOfCluster;

	public interface OnSelectedClusterItemListener
	{
		public void onSelectedClusterItemListener(Marker marker);
	}

	public HotelClusterRenderer(Context context, GoogleMap map, ClusterManager<HotelClusterItem> clusterManager)
	{
		super(context, map, clusterManager);

		mContext = context;
		mItemSizeOfCluster = 1;
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
			markerOptions.icon(icon);
		} else
		{
			super.onBeforeClusterRendered(cluster, markerOptions);
		}
	}

	@Override
	protected void onClusterItemRendered(HotelClusterItem clusterItem, Marker marker)
	{
		if (mSelectedHotelClusterItem != null && mSelectedHotelClusterItem.equals(clusterItem) == true)
		{
			mSelectedHotelClusterItem = null;

			if (mOnSelectedClusterItemListener != null)
			{
				mOnSelectedClusterItemListener.onSelectedClusterItemListener(marker);
			}
		}

		super.onClusterItemRendered(clusterItem, marker);
	}

	@Override
	protected boolean shouldRenderAsCluster(Cluster<HotelClusterItem> cluster)
	{
		return cluster.getSize() > mItemSizeOfCluster;
	}
	
	public void setshouldRenderAsCluster(int itemSizeOfCluster)
	{
		mItemSizeOfCluster = itemSizeOfCluster;
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
