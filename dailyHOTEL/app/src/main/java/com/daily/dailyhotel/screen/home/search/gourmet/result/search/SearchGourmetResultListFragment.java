package com.daily.dailyhotel.screen.home.search.gourmet.result.search;


import android.support.annotation.NonNull;

import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchGourmetResultListFragment extends BasePagerFragment<SearchGourmetResultListFragmentPresenter, SearchGourmetResultListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void setEmptyViewVisible(boolean visible);

        void onEmptyStayResearchClick();

        void onFilterClick();

        void onCalendarClick();

        void onRadiusClick();
    }

    @NonNull
    @Override
    protected SearchGourmetResultListFragmentPresenter createInstancePresenter()
    {
        return new SearchGourmetResultListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
