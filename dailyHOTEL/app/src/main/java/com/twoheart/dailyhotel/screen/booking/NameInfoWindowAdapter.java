package com.twoheart.dailyhotel.screen.booking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.view.widget.DailyTextView;

public class NameInfoWindowAdapter implements InfoWindowAdapter
{
    private LayoutInflater mLayoutInflaterinflater = null;

    public NameInfoWindowAdapter(Context context)
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

        DailyTextView textView = (DailyTextView) view.findViewById(R.id.titleTextView);

        textView.setText(marker.getTitle());

        return view;
    }
}
