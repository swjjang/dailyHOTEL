package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.annotation.NonNull;

import com.daily.base.BaseFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragment extends BaseFragment<StayListFragmentPresenter>
{
    @NonNull
    @Override
    protected StayListFragmentPresenter createInstancePresenter()
    {
        return new StayListFragmentPresenter(this);
    }
}
