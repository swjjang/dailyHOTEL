package com.daily.dailyhotel.screen.home.search.gourmet.result.campaign;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetCampaignListFragment extends BasePagerFragment<SearchGourmetCampaignListFragmentPresenter, SearchGourmetCampaignListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRegionClick();

        void onCalendarClick();

        void onFilterClick();
    }

    @NonNull
    @Override
    protected SearchGourmetCampaignListFragmentPresenter createInstancePresenter()
    {
        return new SearchGourmetCampaignListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
