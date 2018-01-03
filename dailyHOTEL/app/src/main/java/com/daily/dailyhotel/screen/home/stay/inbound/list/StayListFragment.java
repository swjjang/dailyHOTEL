package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.annotation.NonNull;

import com.daily.base.BaseFragment;
import com.daily.base.OnBaseFragmentEventListener;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragment extends BaseFragment<StayListFragmentPresenter, StayListFragment.OnEventListener>
{
    static final int REQUEST_CODE_DETAIL = 11000;

    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRegionClick();

        void onCalendarClick();

        void onFilterClick();
    }

    @NonNull
    @Override
    protected StayListFragmentPresenter createInstancePresenter()
    {
        return new StayListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
