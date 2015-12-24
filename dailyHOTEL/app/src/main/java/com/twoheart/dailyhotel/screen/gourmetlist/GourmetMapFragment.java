package com.twoheart.dailyhotel.screen.gourmetlist;

import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.adapter.PlaceViewPagerAdapter;
import com.twoheart.dailyhotel.fragment.PlaceMapFragment;

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
