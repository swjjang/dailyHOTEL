package com.daily.dailyhotel.screen.home.search.gourmet;

import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;

public class SearchGourmetFragment extends BasePagerFragment<SearchGourmetFragmentPresenter, SearchGourmetFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRecentlyHistoryClick(GourmetSearchResultHistory recentlyHistory);

        void onPopularTagClick(CampaignTag campaignTag);
    }

    @NonNull
    @Override
    protected SearchGourmetFragmentPresenter createInstancePresenter()
    {
        return new SearchGourmetFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
