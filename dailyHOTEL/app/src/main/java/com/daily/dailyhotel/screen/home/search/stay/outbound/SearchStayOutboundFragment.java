package com.daily.dailyhotel.screen.home.search.stay.outbound;

import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.home.search.SearchPresenter;
import com.twoheart.dailyhotel.databinding.FragmentSearchStayOutboundDataBinding;

public class SearchStayOutboundFragment extends BasePagerFragment<SearchStayOutboundFragmentPresenter, SearchStayOutboundFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace);

        void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest);

    }

    @NonNull
    @Override
    protected SearchStayOutboundFragmentPresenter createInstancePresenter()
    {
        return new SearchStayOutboundFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
