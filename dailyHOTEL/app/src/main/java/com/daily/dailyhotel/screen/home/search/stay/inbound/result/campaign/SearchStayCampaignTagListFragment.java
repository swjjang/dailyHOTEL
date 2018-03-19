package com.daily.dailyhotel.screen.home.search.stay.inbound.result.campaign;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayCampaignTagListFragment extends BasePagerFragment<SearchStayCampaignTagListFragmentPresenter, SearchStayCampaignTagListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRegionClick();

        void onCalendarClick();

        void onFilterClick();
    }

    @NonNull
    @Override
    protected SearchStayCampaignTagListFragmentPresenter createInstancePresenter()
    {
        return new SearchStayCampaignTagListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
