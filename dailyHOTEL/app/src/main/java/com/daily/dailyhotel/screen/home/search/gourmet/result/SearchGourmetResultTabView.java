package com.daily.dailyhotel.screen.home.search.gourmet.result;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.screen.home.search.gourmet.result.campaign.SearchGourmetCampaignTagListFragment;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.databinding.ActivitySearchGourmetResultTabDataBinding;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class SearchGourmetResultTabView extends BaseDialogView<SearchGourmetResultTabInterface.OnEventListener, ActivitySearchGourmetResultTabDataBinding> implements SearchGourmetResultTabInterface.ViewInterface
{
    public SearchGourmetResultTabView(BaseActivity baseActivity, SearchGourmetResultTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
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

    private void initToolbar(ActivitySearchGourmetResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {

            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {

            }
        });
    }

    @Override
    public void setViewType(SearchGourmetResultTabPresenter.ViewType viewType)
    {

    }

    @Override
    public void setToolbarDateText(String text)
    {
        getViewDataBinding().toolbarView.setSubTitleText(text);
    }

    @Override
    public Observable setCampaignTagFragment()
    {
        getViewDataBinding().viewPager.setAdapter(null);
        getViewDataBinding().viewPager.removeAllViews();

        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchGourmetCampaignTagListFragment();
        basePagerFragment.setOnFragmentEventListener(new SearchGourmetCampaignTagListFragment.OnEventListener()
        {

            @Override
            public void onRegionClick()
            {

            }

            @Override
            public void onCalendarClick()
            {

            }

            @Override
            public void onFilterClick()
            {

            }

            @Override
            public void onExpireTag()
            {

            }
        });

        fragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(fragmentPagerAdapter);

        return basePagerFragment.getCompleteCreatedObservable();
    }

    @Override
    public Observable<Boolean> setSearchResultFragment()
    {
        return null;
    }
}
