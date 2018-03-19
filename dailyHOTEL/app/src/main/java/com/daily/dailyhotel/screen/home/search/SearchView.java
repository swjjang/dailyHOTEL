package com.daily.dailyhotel.screen.home.search;

import android.support.design.widget.AppBarLayout;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.home.search.gourmet.SearchGourmetFragment;
import com.daily.dailyhotel.screen.home.search.stay.inbound.SearchStayFragment;
import com.daily.dailyhotel.screen.home.search.stay.outbound.SearchStayOutboundFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchDataBinding;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class SearchView extends BaseDialogView<SearchInterface.OnEventListener, ActivitySearchDataBinding>//
    implements SearchInterface.ViewInterface
{
    BaseFragmentPagerAdapter mSearchFragmentPagerAdapter;
    SearchStayFragment mSearchStayFragment;
    SearchGourmetFragment mSearchGourmetFragment;
    SearchStayOutboundFragment mSearchStayOutboundFragment;

    public SearchView(BaseActivity baseActivity, SearchInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initAppBarLayout(viewDataBinding);
        initStayLayout(viewDataBinding);
        initStayOutboundLayout(viewDataBinding);
        initGourmetLayout(viewDataBinding);
        initViewPageLayout(viewDataBinding);
    }

    private void initToolbar(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private void initStayLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.staySearchTextView.setOnClickListener(v -> getEventListener().onStayClick());
        viewDataBinding.stayFilterView.setOnFilterListener(new SearchStayFilterView.OnStayFilterListener()
        {
            @Override
            public void onSuggestClick()
            {
                getEventListener().onStaySuggestClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onStayCalendarClick();
            }

            @Override
            public void onSearchClick()
            {
                getEventListener().onStayDoSearchClick();
            }
        });
    }

    private void initStayOutboundLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.stayOutboundSearchTextView.setOnClickListener(v -> getEventListener().onStayOutboundClick());
        viewDataBinding.stayOutboundFilterView.setOnFilterListener(new SearchStayOutboundFilterView.OnStayOutboundFilterListener()
        {
            @Override
            public void onSuggestClick()
            {
                getEventListener().onStayOutboundSuggestClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onStayOutboundCalendarClick();
            }

            @Override
            public void onPeopleClick()
            {
                getEventListener().onStayOutboundPeopleClick();
            }

            @Override
            public void onSearchClick()
            {
                getEventListener().onStayOutboundDoSearchClick();
            }
        });
    }

    private void initGourmetLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.gourmetSearchTextView.setOnClickListener(v -> getEventListener().onGourmetClick());
        viewDataBinding.gourmetFilterView.setOnFilterListener(new SearchGourmetFilterView.OnGourmetFilterListener()
        {
            @Override
            public void onSuggestClick()
            {
                getEventListener().onGourmetSuggestClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onGourmetCalendarClick();
            }

            @Override
            public void onSearchClick()
            {
                getEventListener().onGourmetDoSearchClick();
            }
        });
    }

    private void initAppBarLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            private final int DP_32 = ScreenUtils.dpToPx(getContext(), 32); // 레이아웃이 양쪽으로 화면보더 더 늘어나야하는 길이
            private final int DP_173 = ScreenUtils.dpToPx(getContext(), 173);
            private final int DP_16 = ScreenUtils.dpToPx(getContext(), 16);
            private final int DP_12 = ScreenUtils.dpToPx(getContext(), 12);
            private final int DP_4 = ScreenUtils.dpToPx(getContext(), 4);
            private final int DP_27 = ScreenUtils.dpToPx(getContext(), 27);
            private final int TOOLBAR_HEIGHT = getDimensionPixelSize(R.dimen.toolbar_height);

            private int mVerticalOffset = Integer.MAX_VALUE;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (mVerticalOffset == verticalOffset)
                {
                    return;
                }

                mVerticalOffset = verticalOffset;

                if (DP_173 + verticalOffset < TOOLBAR_HEIGHT)
                {
                    final int value = DP_173 + verticalOffset;
                    final float vector = (float) value / TOOLBAR_HEIGHT;
                    final float backVector = 1.0f - vector;
                    final int layoutWidth = (int) (ScreenUtils.getScreenWidth(getContext()) + DP_32 * backVector);
                    final int paddingWidth = DP_27 + (int) (DP_4 * backVector);

                    setCategoryLayoutValueForAnimation(vector, layoutWidth, paddingWidth);
                    setBoxShadowViewValueForAnimation(layoutWidth, (int) (DP_4 + DP_12 * vector));

                    getViewDataBinding().appBarLayout.requestLayout();
                } else if (DP_173 + verticalOffset > TOOLBAR_HEIGHT && getViewDataBinding().staySearchTextView.getAlpha() != 1.0f)
                {
                    setCategoryLayoutValueForAnimation(1.0f, AppBarLayout.LayoutParams.MATCH_PARENT, DP_27);
                    setBoxShadowViewValueForAnimation(AppBarLayout.LayoutParams.MATCH_PARENT, DP_16);

                    getViewDataBinding().appBarLayout.requestLayout();
                }

                if (TOOLBAR_HEIGHT - verticalOffset > 0)
                {
                    float vector = (float) -verticalOffset / TOOLBAR_HEIGHT;
                    getViewDataBinding().toolbarView.setAlpha(vector);
                } else if (getViewDataBinding().toolbarView.getAlpha() != 0.0f)
                {
                    getViewDataBinding().toolbarView.setAlpha(0f);
                }

                getViewDataBinding().searchTitleTextView.setTranslationY(verticalOffset / 2);
                getViewDataBinding().topImageView.setTranslationY(verticalOffset / 2);
            }
        });
    }

    private void initViewPageLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        if (mSearchFragmentPagerAdapter == null)
        {
            mSearchFragmentPagerAdapter = new BaseFragmentPagerAdapter<BasePagerFragment>(getSupportFragmentManager());
            mSearchFragmentPagerAdapter.addFragment(getStayFragment());
            mSearchFragmentPagerAdapter.addFragment(getStayOutboundFragment());
            mSearchFragmentPagerAdapter.addFragment(getGourmetFragment());
        }

        viewDataBinding.viewPager.setOffscreenPageLimit(3);
        viewDataBinding.viewPager.setPagingEnabled(false);
        viewDataBinding.viewPager.setAdapter(mSearchFragmentPagerAdapter);
    }

    private SearchStayFragment getStayFragment()
    {
        if (mSearchStayFragment == null)
        {
            mSearchStayFragment = new SearchStayFragment();
            mSearchStayFragment.setOnFragmentEventListener(new SearchStayFragment.OnEventListener()
            {
                @Override
                public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
                {
                    getEventListener().onStayRecentlySearchResultClick(recentlyDbPlace);
                }

                @Override
                public void onPopularTagClick(CampaignTag campaignTag)
                {
                    getEventListener().onStayPopularTagClick(campaignTag);
                }
            });
        }

        return mSearchStayFragment;
    }

    private SearchStayOutboundFragment getStayOutboundFragment()
    {
        if (mSearchStayOutboundFragment == null)
        {
            mSearchStayOutboundFragment = new SearchStayOutboundFragment();
            mSearchStayOutboundFragment.setOnFragmentEventListener(new SearchStayOutboundFragment.OnEventListener()
            {
                @Override
                public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
                {
                    getEventListener().onStayOutboundRecentlySearchResultClick(recentlyDbPlace);
                }

                @Override
                public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
                {
                    getEventListener().onStayOutboundPopularAreaClick(stayOutboundSuggest);
                }
            });
        }

        return mSearchStayOutboundFragment;
    }

    private SearchGourmetFragment getGourmetFragment()
    {
        if (mSearchGourmetFragment == null)
        {
            mSearchGourmetFragment = new SearchGourmetFragment();
            mSearchGourmetFragment.setOnFragmentEventListener(new SearchGourmetFragment.OnEventListener()
            {
                @Override
                public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
                {
                    getEventListener().onGourmetRecentlySearchResultClick(recentlyDbPlace);
                }

                @Override
                public void onPopularTagClick(CampaignTag campaignTag)
                {
                    getEventListener().onGourmetPopularTagClick(campaignTag);
                }
            });
        }

        return mSearchGourmetFragment;
    }

    void setCategoryLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryLayout.getLayoutParams().width = layoutWidth;

        setStayLayoutValueForAnimation(alpha, layoutWidth, paddingWidth);
        setStayOutboundLayoutValueForAnimation(alpha, layoutWidth, paddingWidth);
        setGourmetLayoutValueForAnimation(alpha, layoutWidth, paddingWidth);
    }

    private void setStayLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setLayoutValueForAnimation(getViewDataBinding().staySearchTextView, alpha//
            , getViewDataBinding().stayFilterView, layoutWidth, paddingWidth);
    }

    private void setStayOutboundLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setLayoutValueForAnimation(getViewDataBinding().stayOutboundSearchTextView, alpha//
            , getViewDataBinding().stayOutboundFilterView, layoutWidth, paddingWidth);
    }

    private void setGourmetLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setLayoutValueForAnimation(getViewDataBinding().gourmetSearchTextView, alpha//
            , getViewDataBinding().gourmetFilterView, layoutWidth, paddingWidth);
    }

    private void setLayoutValueForAnimation(View alphaView, float alpha, View widthView, int layoutWidth, int paddingWidth)
    {
        if (alphaView == null || widthView == null)
        {
            return;
        }

        alphaView.setAlpha(alpha);
        widthView.getLayoutParams().width = layoutWidth;
        widthView.setPadding(paddingWidth, widthView.getPaddingTop(), paddingWidth, widthView.getPaddingBottom());
    }

    void setBoxShadowViewValueForAnimation(int width, int height)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        ViewGroup.LayoutParams layoutParams = getViewDataBinding().searchBoxShadowView.getLayoutParams();

        if (layoutParams != null)
        {
            layoutParams.width = width;
            layoutParams.height = height;

            getViewDataBinding().searchBoxShadowView.requestLayout();
        }
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

    @Override
    public void showSearchStay()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setToolbarTitle(getString(R.string.label_search_search_stay));

        getViewDataBinding().appBarLayout.setExpanded(true, true);
        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_stay);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_stay_description);

        setStaySelected(true);
        setStayOutboundSelected(false);
        setGourmetSelected(false);

        getViewDataBinding().viewPager.setCurrentItem(0, false);
    }

    @Override
    public void refreshStay()
    {
        if (mSearchStayFragment == null)
        {
            return;
        }

        mSearchStayFragment.onRefresh();
    }

    @Override
    public void setSearchStaySuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setSuggestText(text);
    }

    @Override
    public void setSearchStayCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setCalendarText(text);
    }

    @Override
    public void setSearchStayButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setSearchEnabled(enabled);
    }

    @Override
    public Completable getStaySuggestAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().stayFilterView.getSuggestTextViewAnimation();
    }

    @Override
    public void showSearchStayOutbound()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setToolbarTitle(getString(R.string.label_search_search_stayoutbound));

        getViewDataBinding().appBarLayout.setExpanded(true, true);
        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_ob);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_stayoutbound_description);

        setStaySelected(false);
        setStayOutboundSelected(true);
        setGourmetSelected(false);

        getViewDataBinding().viewPager.setCurrentItem(1, false);
    }

    @Override
    public void refreshStayOutbound()
    {
        if (mSearchStayOutboundFragment == null)
        {
            return;
        }

        mSearchStayOutboundFragment.onRefresh();
    }

    @Override
    public void setSearchStayOutboundSuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundFilterView.setSuggestText(text);
    }

    @Override
    public void setSearchStayOutboundCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundFilterView.setCalendarText(text);
    }

    @Override
    public void setSearchStayOutboundPeopleText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundFilterView.setPeopleText(text);
    }

    @Override
    public void setSearchStayOutboundButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundFilterView.setSearchEnabled(enabled);
    }

    @Override
    public Completable getStayOutboundSuggestAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().stayOutboundFilterView.getSuggestTextViewAnimation();
    }

    @Override
    public void showSearchGourmet()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        setToolbarTitle(getString(R.string.label_search_search_gourmet));

        getViewDataBinding().appBarLayout.setExpanded(true, true);
        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_gourmet);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_gourmet_description);

        setStaySelected(false);
        setStayOutboundSelected(false);
        setGourmetSelected(true);

        getViewDataBinding().viewPager.setCurrentItem(2, false);
    }

    @Override
    public void refreshGourmet()
    {
        if (mSearchGourmetFragment == null)
        {
            return;
        }

        mSearchGourmetFragment.onRefresh();
    }

    @Override
    public void setSearchGourmetSuggestText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setSuggestText(text);
    }

    @Override
    public void setSearchGourmetCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setCalendarText(text);
    }

    @Override
    public void setSearchGourmetButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setSearchEnabled(enabled);
    }

    @Override
    public Completable getGourmetSuggestAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().gourmetFilterView.getSuggestTextViewAnimation();
    }

    @Override
    public Observable<Boolean> getCompleteCreatedFragment()
    {
        return Observable.zip(mSearchStayFragment.getCompleteCreatedObservable(), mSearchStayOutboundFragment.getCompleteCreatedObservable()//
            , mSearchGourmetFragment.getCompleteCreatedObservable(), (o, o2, o3) -> true);
    }

    private void setStaySelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayFilterView.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().staySearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchStayFragment.onSelected();
        } else
        {
            mSearchStayFragment.onUnselected();
        }
    }

    private void setStayOutboundSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundFilterView.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().stayOutboundSearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchStayOutboundFragment.onSelected();
        } else
        {
            mSearchStayOutboundFragment.onUnselected();
        }
    }

    private void setGourmetSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetFilterView.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().gourmetSearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchGourmetFragment.onSelected();
        } else
        {
            mSearchGourmetFragment.onUnselected();
        }
    }
}
