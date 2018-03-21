package com.daily.dailyhotel.screen.home.search.stay.inbound.result.search;


import android.support.annotation.NonNull;

import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.OnBaseFragmentEventListener;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.StayCategory;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayResultListFragment extends BasePagerFragment<SearchStayResultListFragmentPresenter, SearchStayResultListFragment.OnEventListener>
{
    public interface OnEventListener extends OnBaseFragmentEventListener
    {
        void setEmptyViewVisible(boolean visible);

        void onResearchClick();

        void onFilterClick();

        void onCalendarClick();

        void onRadiusClick();

        Observable<Boolean> addCategoryList(List<StayCategory> categoryList);
    }

    @NonNull
    @Override
    protected SearchStayResultListFragmentPresenter createInstancePresenter()
    {
        return new SearchStayResultListFragmentPresenter(this);
    }

    @Override
    protected OnEventListener getFragmentEventListener()
    {
        return super.getFragmentEventListener();
    }
}
