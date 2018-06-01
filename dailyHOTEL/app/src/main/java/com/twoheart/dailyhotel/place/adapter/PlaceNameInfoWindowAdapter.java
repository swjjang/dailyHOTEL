package com.twoheart.dailyhotel.place.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.daily.base.widget.DailyTextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.R;

public class PlaceNameInfoWindowAdapter implements InfoWindowAdapter
{
    private LayoutInflater mLayoutInflater;

    public PlaceNameInfoWindowAdapter(Context context)
    {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return (null);
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View view = mLayoutInflater.inflate(R.layout.fragment_tabmap_popup, null);

        DailyTextView textView = view.findViewById(R.id.titleTextView);
        textView.setText(marker.getTitle());

        return view;
    }
}
