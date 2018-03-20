package com.daily.dailyhotel.screen.home.search.stay.inbound;

import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;

public class SearchStayFragment extends BasePagerFragment<SearchStayFragmentPresenter, SearchStayFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRecentlyHistoryClick(StaySearchResultHistory recentlyHistory);

        void onPopularTagClick(CampaignTag campaignTag);
    }

    @NonNull
    @Override
    protected SearchStayFragmentPresenter createInstancePresenter()
    {
        return new SearchStayFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
