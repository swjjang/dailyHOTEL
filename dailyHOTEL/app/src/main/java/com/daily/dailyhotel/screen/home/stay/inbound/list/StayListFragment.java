package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.support.annotation.NonNull;

import com.daily.base.BaseFragment;
import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragment extends BasePagerFragment<StayListFragmentPresenter, StayListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRegionClick();

        void onCalendarClick();

        void onFilterClick();

        void setFloatingActionViewVisible(boolean visible);

        void setFloatingActionViewTypeMapEnabled(boolean enabled);

        void setFloatingActionViewTranslationY(float translationY);
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
