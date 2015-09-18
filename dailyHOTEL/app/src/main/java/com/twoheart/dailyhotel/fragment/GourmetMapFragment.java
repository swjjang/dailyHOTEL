package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.GourmetViewPagerAdapter;
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;

public class GourmetMapFragment extends PlaceMapFragment
{
    public GourmetMapFragment()
    {
        super();
    }

    protected PlaceViewPagerAdapter createPlaceViewPagerAdapter(BaseActivity baseActivity)
    {
        return new GourmetViewPagerAdapter(baseActivity);
    }
}
