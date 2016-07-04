package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;

import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;

public class StayMapFragment extends PlaceListMapFragment
{
    public StayMapFragment()
    {
    }

    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new HotelMapViewPagerAdapter(context);
    }
}
