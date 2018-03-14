package com.daily.dailyhotel.screen.home.search.gourmet.result.campaign;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetCampaignTagListFragment extends BasePagerFragment<SearchGourmetCampaignTagListFragmentPresenter, SearchGourmetCampaignTagListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onResearchClick();

        void onFinishAndRefresh();
    }

    @NonNull
    @Override
    protected SearchGourmetCampaignTagListFragmentPresenter createInstancePresenter()
    {
        return new SearchGourmetCampaignTagListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
