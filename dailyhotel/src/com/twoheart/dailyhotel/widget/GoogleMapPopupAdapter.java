package com.twoheart.dailyhotel.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.R;

public class GoogleMapPopupAdapter implements InfoWindowAdapter
{
	private LayoutInflater mLayoutInflaterinflater = null;

	public GoogleMapPopupAdapter(Context context)
	{
		mLayoutInflaterinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getInfoWindow(Marker marker)
	{
		return (null);
	}

	@Override
	public View getInfoContents(Marker marker)
	{
		View view = mLayoutInflaterinflater.inflate(R.layout.fragment_tabmap_popup, null);

		TextView textView = (TextView) view.findViewById(R.id.titleTextView);

		textView.setText(marker.getTitle());

		return view;
	}
}
