package com.daily.dailyhotel.screen.home.search.stay.outbound.research;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.home.search.stay.outbound.SearchStayOutboundFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityResearchStayOutboundDataBinding;

import io.reactivex.Observable;

public class ResearchStayOutboundView extends BaseDialogView<ResearchStayOutboundInterface.OnEventListener, ActivityResearchStayOutboundDataBinding> implements ResearchStayOutboundInterface.ViewInterface
{
    SearchStayOutboundFragment mSearchStayOutboundFragment;


    public ResearchStayOutboundView(BaseActivity baseActivity, ResearchStayOutboundInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityResearchStayOutboundDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.stayOutboundSuggestTextView.setOnClickListener(v -> getEventListener().onSuggestClick());
        viewDataBinding.stayOutboundCalendarTextView.setOnClickListener(v -> getEventListener().onCalendarClick());
        viewDataBinding.peopleBackgroundView.setOnClickListener(v -> getEventListener().onPeopleClick());
        viewDataBinding.searchStayOutboundTextView.setOnClickListener(v -> getEventListener().onDoSearchClick());

        mSearchStayOutboundFragment = (SearchStayOutboundFragment) getSupportFragmentManager().findFragmentById(R.id.searchStayFragment);
        mSearchStayOutboundFragment.setOnFragmentEventListener(new SearchStayOutboundFragment.OnEventListener()
        {
            @Override
            public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
            {
                getEventListener().onRecentlySearchResultClick(recentlyDbPlace);
            }

            @Override
            public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
            {
                getEventListener().onPopularAreaClick(stayOutboundSuggest);
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

    private void initToolbar(ActivityResearchStayOutboundDataBinding viewDataBinding)
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
        if (getViewDataBinding() == null || mSearchStayOutboundFragment == null)
        {
            return;
        }

        mSearchStayOutboundFragment.onSelected();
    }

    @Override
    public void setSearchSuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundSuggestTextView.setText(text);
    }

    @Override
    public void setSearchCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundCalendarTextView.setText(text);
    }

    @Override
    public void setSearchPeopleText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().peopleTextView.setText(text);
    }

    @Override
    public void setSearchButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchStayOutboundTextView.setEnabled(enabled);
    }

    @Override
    public Observable getCompleteCreatedFragment()
    {
        if (getViewDataBinding() == null || mSearchStayOutboundFragment == null)
        {
            return null;
        }

        return mSearchStayOutboundFragment.getCompleteCreatedObservable();
    }
}
