package com.twoheart.dailyhotel.fragment;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.FnBViewPagerAdapter;
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;

public class FnBMapFragment extends PlaceMapFragment
{
    public FnBMapFragment()
    {
        super();
    }

    protected PlaceViewPagerAdapter createPlaceViewPagerAdapter(BaseActivity baseActivity)
    {
        return new FnBViewPagerAdapter(baseActivity);
    }
}
