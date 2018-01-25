package com.daily.dailyhotel.screen.home.search;

import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.screen.home.search.gourmet.SearchGourmetFragment;
import com.daily.dailyhotel.screen.home.search.stay.inbound.SearchStayFragment;
import com.daily.dailyhotel.screen.home.search.stay.outbound.SearchStayOutboundFragment;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchDataBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchView extends BaseDialogView<SearchInterface.OnEventListener, ActivitySearchDataBinding> implements SearchInterface.ViewInterface
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
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (ScreenUtils.dpToPx(getContext(), 150) - getDimensionPixelSize(R.dimen.toolbar_height) + verticalOffset < 0)
                {
                    int totalHeight = ScreenUtils.dpToPx(getContext(), 100);
                    int value = Math.abs(ScreenUtils.dpToPx(getContext(), 150) - getDimensionPixelSize(R.dimen.toolbar_height) + verticalOffset);
                    float vector = (float) value / totalHeight;
                    float backVector = 1.0f - vector;

                    if (backVector < 0.0f)
                    {
                        backVector = 0.0f;
                    }

                    getViewDataBinding().staySearchTextView.setAlpha(backVector);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(backVector);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(backVector);

                    int paddingValue = (int) (ScreenUtils.dpToPx(getContext(), 15) * backVector);
                    getViewDataBinding().categoryLayout.setPadding(paddingValue, 0, paddingValue, 0);
                    ((AppBarLayout.LayoutParams) getViewDataBinding().stayLayout.getLayoutParams()).leftMargin = paddingValue;
                    ((AppBarLayout.LayoutParams) getViewDataBinding().stayLayout.getLayoutParams()).rightMargin = paddingValue;
                    getViewDataBinding().stayLayout.requestLayout();


                    //                    getViewDataBinding().toolbarView.setElevation(2 + ScreenUtils.dpToPx(getContext(), 18) * vector);
                    //                    getViewDataBinding().appBarLayout.setElevation(ScreenUtils.dpToPx(getContext(), 10) * vector);

                } else if (getViewDataBinding().toolbarView.getAlpha() != 0.0f)
                {
                    getViewDataBinding().staySearchTextView.setAlpha(1.0f);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(1.0f);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(1.0f);
                    getViewDataBinding().categoryLayout.setPadding(ScreenUtils.dpToPx(getContext(), 15), 0, ScreenUtils.dpToPx(getContext(), 15), 0);

                    ((AppBarLayout.LayoutParams) getViewDataBinding().stayLayout.getLayoutParams()).leftMargin = ScreenUtils.dpToPx(getContext(), 15);
                    ((AppBarLayout.LayoutParams) getViewDataBinding().stayLayout.getLayoutParams()).rightMargin = ScreenUtils.dpToPx(getContext(), 15);
                    getViewDataBinding().stayLayout.requestLayout();

                    //                    getViewDataBinding().toolbarView.setElevation(ScreenUtils.dpToPx(getContext(), 20));
                    //                    getViewDataBinding().appBarLayout.setElevation(ScreenUtils.dpToPx(getContext(), 10));
                }

                // getViewDataBinding().searchTitleTextView 의 상단 마진
                final int DP_20 = ScreenUtils.dpToPx(getContext(), 20);

                if (DP_20 + verticalOffset > 0)
                {
                    float vector = (float) -verticalOffset / DP_20;
                    getViewDataBinding().toolbarView.setAlpha(vector);
                } else
                {
                    getViewDataBinding().toolbarView.setAlpha(1.0f);
                }

                getViewDataBinding().searchTitleTextView.setTranslationY(verticalOffset / 2);
                getViewDataBinding().simpleDraweeView.setTranslationY(verticalOffset / 2);
            }
        });

        mSearchFragmentPagerAdapter = new BaseFragmentPagerAdapter<BasePagerFragment>(getSupportFragmentManager());

        List<BasePagerFragment> list = new ArrayList<>();

        mSearchStayFragment = new SearchStayFragment();
        mSearchStayOutboundFragment = new SearchStayOutboundFragment();
        mSearchGourmetFragment = new SearchGourmetFragment();

        mSearchFragmentPagerAdapter.addFragment(mSearchStayFragment);
        mSearchFragmentPagerAdapter.addFragment(mSearchStayOutboundFragment);
        mSearchFragmentPagerAdapter.addFragment(mSearchGourmetFragment);

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
}
