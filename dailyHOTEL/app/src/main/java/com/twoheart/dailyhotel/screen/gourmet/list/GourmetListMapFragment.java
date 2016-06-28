package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;

import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;

public class GourmetListMapFragment extends PlaceListMapFragment
{
    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new GourmetMapViewPagerAdapter(context);
    }

    public GourmetListMapFragment()
    {
    }
}
