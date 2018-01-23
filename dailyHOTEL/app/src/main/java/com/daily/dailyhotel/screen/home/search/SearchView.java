package com.daily.dailyhotel.screen.home.search;

import android.support.design.widget.AppBarLayout;
import android.util.TypedValue;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchDataBinding;

public class SearchView extends BaseDialogView<SearchInterface.OnEventListener, ActivitySearchDataBinding> implements SearchInterface.ViewInterface
{
    SearchFragmentPagerAdapter mSearchFragmentPagerAdapter;

    //    SearchStayFragment mSearchStayFragment;
    //    SearchGourmetFragment mSearchGourmetFragment;
    //    SearchStayOutboundFragment SearchStayOutboundFragment;

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

        viewDataBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                getViewDataBinding().dailyTitleTextView.setAlpha(Math.abs((float) verticalOffset) / (float) getViewDataBinding().categoryLayout.getHeight());
            }
        });

        viewDataBinding.staySearchTextView.setOnClickListener(v -> getEventListener().onStaySearchClick(false));
        viewDataBinding.staySuggestTextView.setOnClickListener(v -> getEventListener().onStaySuggestClick());
        viewDataBinding.stayCalendarTextView.setOnClickListener(v -> getEventListener().onStayCalendarClick());
        viewDataBinding.searchStayTextView.setOnClickListener(v -> getEventListener().onStayDoSearchClick());

        viewDataBinding.stayOutboundSearchTextView.setOnClickListener(v -> getEventListener().onStayOutboundSearchClick());
        viewDataBinding.stayOutboundSuggestTextView.setOnClickListener(v -> getEventListener().onStayOutboundSuggestClick());
        viewDataBinding.stayOutboundCalendarTextView.setOnClickListener(v -> getEventListener().onStayOutboundCalendarClick());
        viewDataBinding.peopleBackgroundView.setOnClickListener(v -> getEventListener().onStayOutboundPeopleClick());
        viewDataBinding.searchStayOutboundTextView.setOnClickListener(v -> getEventListener().onStayOutboundDoSearchClick());

        viewDataBinding.gourmetSearchTextView.setOnClickListener(v -> getEventListener().onGourmetSearchClick());
        viewDataBinding.gourmetSuggestTextView.setOnClickListener(v -> getEventListener().onGourmetSuggestClick());
        viewDataBinding.gourmetCalendarTextView.setOnClickListener(v -> getEventListener().onGourmetCalendarClick());
        viewDataBinding.searchGourmetTextView.setOnClickListener(v -> getEventListener().onGourmetDoSearchClick());

        viewDataBinding.staySearchFakeTextView.bringToFront();
        viewDataBinding.getRoot().invalidate();

        viewDataBinding.appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            boolean mMove = false;
            float x, y;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (ScreenUtils.dpToPx(getContext(), 150) - getDimensionPixelSize(R.dimen.toolbar_height) + verticalOffset < 0)
                {
                    int totalHeight = ScreenUtils.dpToPx(getContext(), 100);
                    int value = Math.abs(ScreenUtils.dpToPx(getContext(), 150) - getDimensionPixelSize(R.dimen.toolbar_height) + verticalOffset);
                    float vector = (float)value / totalHeight;

                    getViewDataBinding().toolbarLayout.setAlpha(vector);
                    getViewDataBinding().staySearchTextView.setAlpha(1.0f - vector);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(1.0f - vector);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(1.0f - vector);

                    int paddingValue = (int)(ScreenUtils.dpToPx(getContext(), 15) * (1 - vector));
                    getViewDataBinding().appBarLayout.setPadding(paddingValue, 0, paddingValue, 0);


                    getViewDataBinding().staySearchFakeTextView.setAlpha(1.0f);
                    getViewDataBinding().staySearchFakeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10 + 8 * vector);

                    if(mMove == false)
                    {
                        mMove = true;

                        x = getViewDataBinding().dailyTitleTextView.getX() - getViewDataBinding().staySearchFakeTextView.getX();
                        y = getViewDataBinding().dailyTitleTextView.getY() - getViewDataBinding().staySearchFakeTextView.getY();
                    }

                    if(mMove == true)
                    {
                        getViewDataBinding().staySearchFakeTextView.setTranslationX(x * value);
                        getViewDataBinding().staySearchFakeTextView.setTranslationY(y * value);
                    }

                } else if(getViewDataBinding().toolbarLayout.getAlpha() != 0.0f)
                {
                    getViewDataBinding().toolbarLayout.setAlpha(0.0f);
                    getViewDataBinding().staySearchTextView.setAlpha(1.0f);
                    getViewDataBinding().stayOutboundSearchTextView.setAlpha(1.0f);
                    getViewDataBinding().gourmetSearchTextView.setAlpha(1.0f);
                    getViewDataBinding().appBarLayout.setPadding(ScreenUtils.dpToPx(getContext(), 15), 0, ScreenUtils.dpToPx(getContext(), 15), 0);


                    getViewDataBinding().staySearchFakeTextView.setAlpha(0.0f);
                    getViewDataBinding().staySearchFakeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

                    mMove = false;

                }
            }
        });

        //        mSearchFragmentPagerAdapter = new SearchFragmentPagerAdapter(getFragmentManager());
        //
        //        List<BaseFragment> list = new ArrayList<>();
        //
        //        mSearchStayFragment = new SearchStayFragment();
        //        SearchStayOutboundFragment = new SearchStayOutboundFragment();
        //        mSearchGourmetFragment = new SearchGourmetFragment();
        //
        //        mSearchFragmentPagerAdapter.add(mSearchStayFragment);
        //        mSearchFragmentPagerAdapter.add(SearchStayOutboundFragment);
        //        mSearchFragmentPagerAdapter.add(mSearchGourmetFragment);
        //
        //        getViewDataBinding().viewPager.setAdapter(mSearchFragmentPagerAdapter);
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().dailyTitleTextView.setText(title);
    }

    private void initToolbar(ActivitySearchDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.dailyTitleImageView.setOnClickListener(v -> getEventListener().onBackClick());
    }

    @Override
    public void showSearchStay(boolean force)
    {
        if (force == false && (getViewDataBinding() == null || getViewDataBinding().viewPager.getCurrentItem() == 0))
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

        setToolbarTitle("국내 스테이");
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
        if (getViewDataBinding() == null || getViewDataBinding().viewPager.getCurrentItem() == 1)
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

        setToolbarTitle("해외 스테이");
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
        if (getViewDataBinding() == null || getViewDataBinding().viewPager.getCurrentItem() == 2)
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

        setToolbarTitle("고 메");
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

}
