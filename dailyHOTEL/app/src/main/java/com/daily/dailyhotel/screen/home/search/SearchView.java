package com.daily.dailyhotel.screen.home.search;

import android.support.design.widget.AppBarLayout;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.FontManager;
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

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Function3;

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

        viewDataBinding.staySearchTextView.setOnClickListener(v -> getEventListener().onStayClick());
        viewDataBinding.staySuggestTextView.setOnClickListener(v -> getEventListener().onStaySuggestClick());
        viewDataBinding.stayCalendarTextView.setOnClickListener(v -> getEventListener().onStayCalendarClick());
        viewDataBinding.searchStayTextView.setOnClickListener(v -> getEventListener().onStayDoSearchClick());

        viewDataBinding.stayOutboundSearchTextView.setOnClickListener(v -> getEventListener().onStayOutboundClick());
        viewDataBinding.stayOutboundSuggestTextView.setOnClickListener(v -> getEventListener().onStayOutboundSuggestClick());
        viewDataBinding.stayOutboundCalendarTextView.setOnClickListener(v -> getEventListener().onStayOutboundCalendarClick());
        viewDataBinding.peopleBackgroundView.setOnClickListener(v -> getEventListener().onStayOutboundPeopleClick());
        viewDataBinding.searchStayOutboundTextView.setOnClickListener(v -> getEventListener().onStayOutboundDoSearchClick());

        viewDataBinding.gourmetSearchTextView.setOnClickListener(v -> getEventListener().onGourmetClick());
        viewDataBinding.gourmetSuggestTextView.setOnClickListener(v -> getEventListener().onGourmetSuggestClick());
        viewDataBinding.gourmetCalendarTextView.setOnClickListener(v -> getEventListener().onGourmetCalendarClick());
        viewDataBinding.searchGourmetTextView.setOnClickListener(v -> getEventListener().onGourmetDoSearchClick());

        viewDataBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            private final int DP_32 = ScreenUtils.dpToPx(getContext(), 32);
            private final int DP_173 = ScreenUtils.dpToPx(getContext(), 173);
            private final int DP_18 = ScreenUtils.dpToPx(getContext(), 18);
            private final int DP_14 = ScreenUtils.dpToPx(getContext(), 14);
            private final int DP_15 = ScreenUtils.dpToPx(getContext(), 15);
            private final int DP_4 = ScreenUtils.dpToPx(getContext(), 4);
            private final int DP_27 = ScreenUtils.dpToPx(getContext(), 27);
            private final int TOOLBAR_HEIGHT = getDimensionPixelSize(R.dimen.toolbar_height);
            private final int ANIMATION_HEIGHT = TOOLBAR_HEIGHT / 2;

            private int mVerticalOffset = Integer.MAX_VALUE;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (mVerticalOffset == verticalOffset)
                {
                    return;
                } else
                {
                    mVerticalOffset = verticalOffset;
                }

                if (DP_173 + verticalOffset < ANIMATION_HEIGHT)
                {
                    int totalHeight = ANIMATION_HEIGHT;
                    int value = DP_173 + verticalOffset;
                    float vector = (float) value / totalHeight;
                    float backVector = 1.0f - vector;

                    getViewDataBinding().staySearchTextView.setAlpha(vector);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(vector);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(vector);

                    final int layoutWidth = (int) (ScreenUtils.getScreenWidth(getContext()) + DP_32 * backVector);
                    final int paddingWidth = DP_27 + (int) (DP_4 * backVector);

                    getViewDataBinding().categoryLayout.getLayoutParams().width = layoutWidth;

                    getViewDataBinding().stayLayout.getLayoutParams().width = layoutWidth;
                    getViewDataBinding().stayLayout.setPadding(paddingWidth, DP_14, paddingWidth, DP_15);

                    getViewDataBinding().stayOutboundLayout.getLayoutParams().width = layoutWidth;
                    getViewDataBinding().stayOutboundLayout.setPadding(paddingWidth, DP_14, paddingWidth, DP_15);

                    getViewDataBinding().gourmetLayout.getLayoutParams().width = layoutWidth;
                    getViewDataBinding().gourmetLayout.setPadding(paddingWidth, DP_14, paddingWidth, DP_15);

                    getViewDataBinding().searchBoxShadowView.getLayoutParams().width = layoutWidth;
                    getViewDataBinding().searchBoxShadowView.getLayoutParams().height = (int) (DP_4 + DP_14 * vector);

                    getViewDataBinding().appBarLayout.requestLayout();
                } else if (DP_173 + verticalOffset > ANIMATION_HEIGHT && getViewDataBinding().staySearchTextView.getAlpha() != 1.0f)
                {
                    getViewDataBinding().staySearchTextView.setAlpha(1.0f);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(1.0f);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(1.0f);

                    getViewDataBinding().categoryLayout.getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;

                    getViewDataBinding().stayLayout.getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;
                    getViewDataBinding().stayLayout.setPadding(DP_27, DP_14, DP_27, DP_15);

                    getViewDataBinding().stayOutboundLayout.getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;
                    getViewDataBinding().stayOutboundLayout.setPadding(DP_27, DP_14, DP_27, DP_15);

                    getViewDataBinding().gourmetLayout.getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;
                    getViewDataBinding().gourmetLayout.setPadding(DP_27, DP_14, DP_27, DP_15);

                    getViewDataBinding().searchBoxShadowView.getLayoutParams().width = AppBarLayout.LayoutParams.MATCH_PARENT;
                    getViewDataBinding().searchBoxShadowView.getLayoutParams().height = DP_18;

                    getViewDataBinding().appBarLayout.requestLayout();
                }

                if (ANIMATION_HEIGHT - verticalOffset > 0)
                {
                    float vector = (float) -verticalOffset / ANIMATION_HEIGHT;
                    getViewDataBinding().toolbarView.setAlpha(vector);
                } else if (getViewDataBinding().toolbarView.getAlpha() != 0.0f)
                {
                    getViewDataBinding().toolbarView.setAlpha(0f);
                }

                getViewDataBinding().searchTitleTextView.setTranslationY(verticalOffset / 2);
                getViewDataBinding().topImageView.setTranslationY(verticalOffset / 2);
            }
        });

        mSearchFragmentPagerAdapter = new BaseFragmentPagerAdapter<BasePagerFragment>(getSupportFragmentManager());

        List<BasePagerFragment> list = new ArrayList<>();

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

        mSearchFragmentPagerAdapter.addFragment(mSearchStayFragment);
        mSearchFragmentPagerAdapter.addFragment(mSearchStayOutboundFragment);
        mSearchFragmentPagerAdapter.addFragment(mSearchGourmetFragment);

        getViewDataBinding().viewPager.setOffscreenPageLimit(3);
        getViewDataBinding().viewPager.setPagingEnabled(false);
        getViewDataBinding().viewPager.setAdapter(mSearchFragmentPagerAdapter);
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

    private void initToolbar(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void showSearchStay()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_stay);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_stay_description);

        getViewDataBinding().stayLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().stayOutboundLayout.setVisibility(View.GONE);
        getViewDataBinding().gourmetLayout.setVisibility(View.GONE);

        getViewDataBinding().staySearchTextView.setSelected(true);
        getViewDataBinding().stayOutboundSearchTextView.setSelected(false);
        getViewDataBinding().gourmetSearchTextView.setSelected(false);

        getViewDataBinding().viewPager.setCurrentItem(0, false);

        setToolbarTitle(getString(R.string.label_search_search_stay));

        mSearchStayFragment.onSelected();
        mSearchStayOutboundFragment.onUnselected();
        mSearchGourmetFragment.onUnselected();

        getViewDataBinding().staySearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        getViewDataBinding().stayOutboundSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        getViewDataBinding().gourmetSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
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

        getViewDataBinding().staySuggestTextView.setText(text);
    }

    @Override
    public void setSearchStayCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayCalendarTextView.setText(text);
    }

    @Override
    public void setSearchStayButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchStayTextView.setEnabled(enabled);
    }

    @Override
    public void showSearchStayOutbound()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_ob);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_stayoutbound_description);

        getViewDataBinding().stayLayout.setVisibility(View.GONE);
        getViewDataBinding().stayOutboundLayout.setVisibility(View.VISIBLE);
        getViewDataBinding().gourmetLayout.setVisibility(View.GONE);

        getViewDataBinding().staySearchTextView.setSelected(false);
        getViewDataBinding().stayOutboundSearchTextView.setSelected(true);
        getViewDataBinding().gourmetSearchTextView.setSelected(false);

        getViewDataBinding().viewPager.setCurrentItem(1, false);

        setToolbarTitle(getString(R.string.label_search_search_stayoutbound));

        mSearchStayFragment.onUnselected();
        mSearchStayOutboundFragment.onSelected();
        mSearchGourmetFragment.onUnselected();

        getViewDataBinding().staySearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        getViewDataBinding().stayOutboundSearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        getViewDataBinding().gourmetSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
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

        getViewDataBinding().stayOutboundSuggestTextView.setText(text);
    }

    @Override
    public void setSearchStayOutboundCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundCalendarTextView.setText(text);
    }

    @Override
    public void setSearchStayOutboundPeopleText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().peopleTextView.setText(text);
    }

    @Override
    public void setSearchStayOutboundButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchStayOutboundTextView.setEnabled(enabled);
    }

    @Override
    public void showSearchGourmet()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().topImageView.setImageResource(R.drawable.search_bg_gourmet);
        getViewDataBinding().searchTitleTextView.setText(R.string.message_search_gourmet_description);

        getViewDataBinding().stayLayout.setVisibility(View.GONE);
        getViewDataBinding().stayOutboundLayout.setVisibility(View.GONE);
        getViewDataBinding().gourmetLayout.setVisibility(View.VISIBLE);

        getViewDataBinding().staySearchTextView.setSelected(false);
        getViewDataBinding().stayOutboundSearchTextView.setSelected(false);
        getViewDataBinding().gourmetSearchTextView.setSelected(true);

        getViewDataBinding().viewPager.setCurrentItem(2, false);

        setToolbarTitle(getString(R.string.label_search_search_gourmet));

        mSearchStayFragment.onUnselected();
        mSearchStayOutboundFragment.onUnselected();
        mSearchGourmetFragment.onSelected();

        getViewDataBinding().staySearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        getViewDataBinding().stayOutboundSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        getViewDataBinding().gourmetSearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
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

        getViewDataBinding().gourmetSuggestTextView.setText(text);
    }

    @Override
    public void setSearchGourmetCalendarText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetCalendarTextView.setText(text);
    }

    @Override
    public void setSearchGourmetButtonEnabled(boolean enabled)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchGourmetTextView.setEnabled(enabled);
    }

    @Override
    public Observable getCompleteCreatedFragment()
    {
        return Observable.zip(mSearchStayFragment.getCompleteCreatedObservable(), mSearchStayOutboundFragment.getCompleteCreatedObservable()//
            , mSearchGourmetFragment.getCompleteCreatedObservable(), new Function3<Object, Object, Object, Boolean>()
            {
                @Override
                public Boolean apply(Object o, Object o2, Object o3) throws Exception
                {
                    return true;
                }
            });
    }
}
