package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;

import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.PlaceViewItem;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceMapViewPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListMapFragment;

import java.util.List;

public class GourmetListMapFragment extends PlaceListMapFragment
{
    @Override
    protected PlaceMapViewPagerAdapter getPlaceListMapViewPagerAdapter(Context context)
    {
        return new GourmetMapViewPagerAdapter_v2(context);
    }

    public GourmetListMapFragment()
    {
    }
}
