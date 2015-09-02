package com.twoheart.dailyhotel.view;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.twoheart.dailyhotel.model.HotelRegionRenderer;
import com.twoheart.dailyhotel.model.TicketDto;
import com.twoheart.dailyhotel.model.TicketRenderer;

import android.content.Context;

public class TicketClusterRenderer
		extends DefaultClusterRenderer<TicketClusterItem>
{
	public enum Renderer
	{
		CLUSTER, CLUSTER_ITEM,
	}

	private Context mContext;
	private TicketClusterItem mSelectedTicketClusterItem;
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

	public TicketClusterRenderer(Context context, GoogleMap map, ClusterManager<TicketClusterItem> clusterManager)
	{
		super(context, map, clusterManager);

		mContext = context;
	}

	@Override
	protected void onBeforeClusterItemRendered(TicketClusterItem item, MarkerOptions markerOptions)
	{
		if (mOnClusterRenderedListener != null)
		{
			mOnClusterRenderedListener.onClusterRenderedListener(Renderer.CLUSTER_ITEM);
		}

		TicketDto ticketDto = item.getTicketDto();

		TicketRenderer ticketRenderer = new TicketRenderer(mContext, ticketDto.discountPrice, ticketDto.grade.getMarkerResId());

		BitmapDescriptor icon = ticketRenderer.getBitmap(false);

		if (icon != null)
		{
			markerOptions.icon(icon);
			markerOptions.anchor(0.0f, 1.0f);
		}
	}

	@Override
	protected void onBeforeClusterRendered(Cluster<TicketClusterItem> cluster, MarkerOptions markerOptions)
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
	protected void onClusterItemRendered(TicketClusterItem clusterItem, Marker marker)
	{
		if (mSelectedTicketClusterItem != null)
		{
			LatLng selectedLatLng = mSelectedTicketClusterItem.getPosition();
			LatLng currentLatLng = clusterItem.getPosition();

			if (selectedLatLng.latitude == currentLatLng.latitude && selectedLatLng.longitude == currentLatLng.longitude)
			{
				mSelectedTicketClusterItem = null;

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
	protected boolean shouldRenderAsCluster(Cluster<TicketClusterItem> cluster, float zoom)
	{
		if (Float.compare(zoom, 13.0f) >= 0)
		{
			return false;
		} else
		{
			return true;
		}
	}

	public void setSelectedClusterItem(TicketClusterItem ticketClusterItem)
	{
		mSelectedTicketClusterItem = ticketClusterItem;
	}

	public void setSelectedClusterItemListener(OnSelectedClusterItemListener listener)
	{
		mOnSelectedClusterItemListener = listener;
	}
}
