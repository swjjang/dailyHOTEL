package com.daily.dailyhotel.screen.home.search.gourmet.research;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.home.search.SearchGourmetFilterView;
import com.daily.dailyhotel.screen.home.search.gourmet.SearchGourmetFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityResearchGourmetDataBinding;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class ResearchGourmetView extends BaseDialogView<ResearchGourmetInterface.OnEventListener, ActivityResearchGourmetDataBinding> implements ResearchGourmetInterface.ViewInterface
{
    SearchGourmetFragment mSearchGourmetFragment;

    public ResearchGourmetView(BaseActivity baseActivity, ResearchGourmetInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityResearchGourmetDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.gourmetFilterView.setOnFilterListener(new SearchGourmetFilterView.OnGourmetFilterListener()
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

        mSearchGourmetFragment = (SearchGourmetFragment) getSupportFragmentManager().findFragmentById(R.id.searchGourmetFragment);
        mSearchGourmetFragment.setOnFragmentEventListener(new SearchGourmetFragment.OnEventListener()
        {
            @Override
            public void onRecentlySearchResultClick(GourmetSearchResultHistory recentlyHistory)
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

    private void initToolbar(ActivityResearchGourmetDataBinding viewDataBinding)
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
        if (getViewDataBinding() == null || mSearchGourmetFragment == null)
        {
            return;
        }

        mSearchGourmetFragment.onSelected();
    }

    @Override
    public void setSearchSuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setSuggestText(text);
    }

    @Override
    public void setSearchCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setCalendarText(text);
    }

    @Override
    public void setSearchButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setSearchEnabled(enabled);
    }

    @Override
    public Completable getSuggestAnimation()
    {
        return getViewDataBinding().gourmetFilterView.getSuggestTextViewAnimation();
    }

    @Override
    public Observable getCompleteCreatedFragment()
    {
        if (getViewDataBinding() == null || mSearchGourmetFragment == null)
        {
            return null;
        }

        return mSearchGourmetFragment.getCompleteCreatedObservable();
    }
}
