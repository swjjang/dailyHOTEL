package com.daily.dailyhotel.screen.home.search.stay.inbound.research;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;
import com.daily.dailyhotel.screen.home.search.SearchStayFilterView;
import com.daily.dailyhotel.screen.home.search.stay.inbound.SearchStayFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityResearchStayDataBinding;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ResearchStayView extends BaseDialogView<ResearchStayInterface.OnEventListener, ActivityResearchStayDataBinding> implements ResearchStayInterface.ViewInterface
{
    SearchStayFragment mSearchStayFragment;

    public ResearchStayView(BaseActivity baseActivity, ResearchStayInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityResearchStayDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.stayFilterView.setOnFilterListener(new SearchStayFilterView.OnStayFilterListener()
        {
            @Override
            public void onSuggestClick()
            {
                getEventListener().onSuggestClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onSearchClick()
            {
                getEventListener().onDoSearchClick();
            }
        });

        mSearchStayFragment = (SearchStayFragment) getSupportFragmentManager().findFragmentById(R.id.searchStayFragment);
        mSearchStayFragment.setOnFragmentEventListener(new SearchStayFragment.OnEventListener()
        {
            @Override
            public void onRecentlyHistoryClick(StaySearchResultHistory recentlyHistory)
            {
                getEventListener().onRecentlyHistoryClick(recentlyHistory);
            }

            @Override
            public void onPopularTagClick(CampaignTag campaignTag)
            {
                getEventListener().onPopularTagClick(campaignTag);
            }
        });
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityResearchStayDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setBackImageResource(R.drawable.navibar_ic_x);
        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void showSearch()
    {
        if (getViewDataBinding() == null || mSearchStayFragment == null)
        {
            return;
        }

        mSearchStayFragment.onSelected();
    }

    @Override
    public void setSearchSuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setSuggestText(text);
    }

    @Override
    public void setSearchCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setCalendarText(text);
    }

    @Override
    public void setSearchButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setSearchEnabled(enabled);
    }

    @Override
    public Completable getSuggestAnimation()
    {
        return getViewDataBinding().stayFilterView.getSuggestTextViewAnimation();
    }

    @Override
    public Observable getCompleteCreatedFragment()
    {
        if (getViewDataBinding() == null || mSearchStayFragment == null)
        {
            return null;
        }

        return mSearchStayFragment.getCompleteCreatedObservable();
    }
}
