package com.daily.dailyhotel.screen.home.search;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
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
        initStayLayout(viewDataBinding);
        initStayOutboundLayout(viewDataBinding);
        initGourmetLayout(viewDataBinding);
        initAppBarLayout(viewDataBinding);
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
        viewDataBinding.staySuggestTextView.setOnClickListener(v -> getEventListener().onStaySuggestClick());
        viewDataBinding.stayCalendarTextView.setOnClickListener(v -> getEventListener().onStayCalendarClick());
        viewDataBinding.searchStayTextView.setOnClickListener(v -> getEventListener().onStayDoSearchClick());
    }

    private void initStayOutboundLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.stayOutboundSearchTextView.setOnClickListener(v -> getEventListener().onStayOutboundClick());
        viewDataBinding.stayOutboundSuggestTextView.setOnClickListener(v -> getEventListener().onStayOutboundSuggestClick());
        viewDataBinding.stayOutboundCalendarTextView.setOnClickListener(v -> getEventListener().onStayOutboundCalendarClick());
        viewDataBinding.peopleBackgroundView.setOnClickListener(v -> getEventListener().onStayOutboundPeopleClick());
        viewDataBinding.searchStayOutboundTextView.setOnClickListener(v -> getEventListener().onStayOutboundDoSearchClick());
    }

    private void initGourmetLayout(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.gourmetSearchTextView.setOnClickListener(v -> getEventListener().onGourmetClick());
        viewDataBinding.gourmetSuggestTextView.setOnClickListener(v -> getEventListener().onGourmetSuggestClick());
        viewDataBinding.gourmetCalendarTextView.setOnClickListener(v -> getEventListener().onGourmetCalendarClick());
        viewDataBinding.searchGourmetTextView.setOnClickListener(v -> getEventListener().onGourmetDoSearchClick());
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

        getViewDataBinding().staySearchTextView.setAlpha(alpha);
        getViewDataBinding().stayLayout.getLayoutParams().width = layoutWidth;
        getViewDataBinding().stayLayout.setPadding(paddingWidth, getViewDataBinding().stayLayout.getPaddingTop()//
            , paddingWidth, getViewDataBinding().stayLayout.getPaddingBottom());
    }

    private void setStayOutboundLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundSearchTextView.setAlpha(alpha);
        getViewDataBinding().stayOutboundLayout.getLayoutParams().width = layoutWidth;
        getViewDataBinding().stayOutboundLayout.setPadding(paddingWidth, getViewDataBinding().stayOutboundLayout.getPaddingTop()//
            , paddingWidth, getViewDataBinding().stayOutboundLayout.getPaddingBottom());
    }

    private void setGourmetLayoutValueForAnimation(float alpha, int layoutWidth, int paddingWidth)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetSearchTextView.setAlpha(alpha);
        getViewDataBinding().gourmetLayout.getLayoutParams().width = layoutWidth;
        getViewDataBinding().gourmetLayout.setPadding(paddingWidth, getViewDataBinding().gourmetLayout.getPaddingTop()//
            , paddingWidth, getViewDataBinding().gourmetLayout.getPaddingBottom());
    }

    void setBoxShadowViewValueForAnimation(int width, int height)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().searchBoxShadowView.getLayoutParams().width = width;
        getViewDataBinding().searchBoxShadowView.getLayoutParams().height = height;
        getViewDataBinding().searchBoxShadowView.requestLayout();
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
    public Completable getStayOutboundSuggestAnimation()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestBackgroundView, View.ALPHA, 1.0f, 0.5f, 1.0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestTextView, View.ALPHA, 1.0f, 0.5f, 1.0f);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestBackgroundView, View.SCALE_X, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestBackgroundView, View.SCALE_Y, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator5 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestTextView, View.SCALE_X, 1.0f, 0.97f, 1.0f);
        ObjectAnimator objectAnimator6 = ObjectAnimator.ofFloat(getViewDataBinding().stayOutboundSuggestTextView, View.SCALE_Y, 1.0f, 0.97f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(200);
        animatorSet.playTogether(objectAnimator1, objectAnimator2, objectAnimator3, objectAnimator4, objectAnimator5, objectAnimator6);

        return new Completable()
        {
            @Override
            protected void subscribeActual(CompletableObserver observer)
            {
                animatorSet.addListener(new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        animatorSet.removeAllListeners();

                        observer.onComplete();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation)
                    {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {

                    }
                });

                animatorSet.start();
            }
        };
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

    private void setStaySelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayLayout.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().staySearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchStayFragment.onSelected();
            getViewDataBinding().staySearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        } else
        {
            mSearchStayFragment.onUnselected();
            getViewDataBinding().staySearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        }
    }

    private void setStayOutboundSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().stayOutboundLayout.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().stayOutboundSearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchStayOutboundFragment.onSelected();
            getViewDataBinding().stayOutboundSearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        } else
        {
            mSearchStayOutboundFragment.onUnselected();
            getViewDataBinding().stayOutboundSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        }
    }

    private void setGourmetSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().gourmetLayout.setVisibility(selected ? View.VISIBLE : View.GONE);
        getViewDataBinding().gourmetSearchTextView.setSelected(selected);

        if (selected == true)
        {
            mSearchGourmetFragment.onSelected();
            getViewDataBinding().gourmetSearchTextView.setTypeface(FontManager.getInstance(getContext()).getBoldTypeface());
        } else
        {
            mSearchGourmetFragment.onUnselected();
            getViewDataBinding().gourmetSearchTextView.setTypeface(FontManager.getInstance(getContext()).getMediumTypeface());
        }
    }
}
