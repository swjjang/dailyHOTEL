package com.daily.dailyhotel.screen.home.search.stay.inbound.result;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.StayCategory;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.campaign.SearchStayCampaignTagListFragment;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.search.SearchStayResultListFragment;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailySearchResultEmptyView;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivitySearchStayResultTabDataBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class SearchStayResultTabView extends BaseDialogView<SearchStayResultTabInterface.OnEventListener, ActivitySearchStayResultTabDataBinding> implements SearchStayResultTabInterface.ViewInterface
{
    private BaseFragmentPagerAdapter<BasePagerFragment> mFragmentPagerAdapter;
    private RadiusArrayAdapter mRadiusArrayAdapter;

    public SearchStayResultTabView(BaseActivity baseActivity, SearchStayResultTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivitySearchStayResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initEmptyView(viewDataBinding);

        viewDataBinding.floatingActionView.setOnViewOptionClickListener(v -> getEventListener().onViewTypeClick());
        viewDataBinding.floatingActionView.setOnFilterOptionClickListener(v -> getEventListener().onFilterClick());
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

    private void initToolbar(ActivitySearchStayResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        CharSequence[] strings = getContext().getResources().getTextArray(R.array.search_result_distance_array);
        mRadiusArrayAdapter = new RadiusArrayAdapter(getContext(), R.layout.list_row_search_result_spinner, strings);
        mRadiusArrayAdapter.setDropDownViewResource(R.layout.list_row_search_result_sort_dropdown_item);

        viewDataBinding.toolbarView.setRadiusSpinnerAdapter(mRadiusArrayAdapter);
        viewDataBinding.toolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                getEventListener().onToolbarTitleClick();
            }

            @Override
            public void onBackClick()
            {
                getEventListener().onBackClick();
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {
                if (getViewDataBinding() == null)
                {
                    return;
                }

                RadiusArrayAdapter radiusArrayAdapter = (RadiusArrayAdapter) getViewDataBinding().toolbarView.getRadiusSpinnerAdapter();
                radiusArrayAdapter.setSelection(position);

                getEventListener().onChangedRadius(getSpinnerRadiusValue(position));
            }

            private float getSpinnerRadiusValue(int position)
            {
                if (getViewDataBinding() == null)
                {
                    return 0.0f;
                }

                switch (position)
                {
                    case 0:
                        return 0.5f;

                    case 1:
                        return 1.0f;

                    case 2:
                        return 3.0f;

                    case 3:
                        return 5.0f;

                    case 4:
                    default:
                        return SearchStayResultTabPresenter.DEFAULT_RADIUS;
                }
            }
        });
    }

    private void initEmptyView(ActivitySearchStayResultTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.emptyView.setImage(R.drawable.no_gourmet_ic);
        viewDataBinding.emptyView.setMessage(R.string.message_searchresult_stay_empty_subtitle);
        viewDataBinding.emptyView.setBottomLeftButton(R.drawable.vector_search_shortcut_02_ob, R.string.label_searchresult_search_stayoutbound);
        viewDataBinding.emptyView.setBottomRightButton(R.drawable.vector_search_shortcut_03_gourmet, R.string.label_searchresult_search_gourmet);
        viewDataBinding.emptyView.setOnEventListener(new DailySearchResultEmptyView.OnEventListener()
        {
            @Override
            public void onCampaignTagClick(CampaignTag campaignTag)
            {
                getEventListener().onCampaignTagClick(campaignTag);
            }

            @Override
            public void onBottomLeftButtonClick()
            {
                getEventListener().onStayOutboundClick();
            }

            @Override
            public void onBottomRightButtonClick()
            {
                getEventListener().onGourmetClick();
            }
        });
    }

    @Override
    public void setViewType(SearchStayResultTabPresenter.ViewType viewType)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        switch (viewType)
        {
            case LIST:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.LIST);
                break;

            case MAP:
                getViewDataBinding().floatingActionView.setViewOption(DailyFloatingActionView.ViewOption.MAP);
                break;
        }
    }

    @Override
    public void setToolbarTitleImageResource(int resId)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleImageResource(resId);
    }

    @Override
    public void setToolbarDateText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setSubTitleText(text);
    }

    @Override
    public void setToolbarRadiusSpinnerVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setRadiusSpinnerVisible(visible);
    }

    @Override
    public void setRadiusSpinnerSelection(float radius)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        int position = getRadiusPosition(radius);

        getViewDataBinding().toolbarView.setRadiusSpinnerSelection(position);
    }

    private int getRadiusPosition(float radius)
    {
        if (radius > 5.0f)
        {
            return 4; // 10km
        } else if (radius > 3.0f)
        {
            return 3; // 5km
        } else if (radius > 1.0f)
        {
            return 2; // 3km
        } else if (radius > 0.5f)
        {
            return 1; // 1km
        } else
        {
            return 0; // 0.5km
        }
    }

    @Override
    public void setFloatingActionViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setOptionFilterSelected(boolean selected)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().floatingActionView.setFilterOptionSelected(selected);
    }

    @Override
    public Observable<BasePagerFragment> setCampaignTagFragment()
    {
        removeAllFragment();

        mFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchStayCampaignTagListFragment();
        basePagerFragment.setOnFragmentEventListener(new SearchStayCampaignTagListFragment.OnEventListener()
        {
            @Override
            public void setEmptyViewVisible(boolean visible)
            {
                getEventListener().setEmptyViewVisible(visible);
            }

            @Override
            public void onResearchClick()
            {
                getEventListener().onResearchClick();
            }

            @Override
            public void onFinishAndRefresh()
            {
                getEventListener().onFinishAndRefresh();
            }
        });

        mFragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);

        return basePagerFragment.getCompleteCreatedObservable().map(new Function()
        {
            @Override
            public BasePagerFragment apply(Object o) throws Exception
            {
                return basePagerFragment;
            }
        });
    }

    @Override
    public Observable<BasePagerFragment> setSearchResultFragment(String callByScreen)
    {
        removeAllFragment();

        mFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        BasePagerFragment basePagerFragment = new SearchStayResultListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("callByScreen", callByScreen);
        bundle.putString("categoryName", Category.ALL.name);
        bundle.putString("categoryCode", Category.ALL.code);
        basePagerFragment.setArguments(bundle);

        basePagerFragment.setOnFragmentEventListener(new SearchStayResultListFragment.OnEventListener()
        {
            @Override
            public void setEmptyViewVisible(boolean visible)
            {
                getEventListener().setEmptyViewVisible(visible);
            }

            @Override
            public void onEmptyStayResearchClick()
            {
                getEventListener().onEmptyStayResearchClick();
            }

            @Override
            public void onFilterClick()
            {
                getEventListener().onFilterClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onRadiusClick()
            {
                getViewDataBinding().toolbarView.showRadiusSpinnerPopup();
            }

            @Override
            public Observable<Boolean> addCategoryList(List<StayCategory> categoryList)
            {
                if (categoryList == null)
                {
                    getViewDataBinding().viewPager.setOffscreenPageLimit(1);

                    return Observable.just(true);
                }

                int hasStayCategoryCount = getHasStayCategoryCount(categoryList);

                if (hasStayCategoryCount < 2)
                {
                    getViewDataBinding().viewPager.setOffscreenPageLimit(1);

                    return Observable.just(true);
                }

                getViewDataBinding().viewPager.setOffscreenPageLimit(hasStayCategoryCount + 1);

                setCategoryVisible(true);

                return addSearchResultFragment(callByScreen, categoryList);
            }

            private int getHasStayCategoryCount(List<StayCategory> categoryList)
            {
                if (categoryList == null)
                {
                    return 0;
                }

                int hasStayCategoryCount = 0;

                for (StayCategory category : categoryList)
                {
                    if (category.count > 0)
                    {
                        hasStayCategoryCount++;
                    }
                }

                return hasStayCategoryCount;
            }
        });

        List<StayCategory> categoryList = new ArrayList<>();
        StayCategory stayCategory = new StayCategory();
        stayCategory.code = Category.ALL.code;
        stayCategory.name = Category.ALL.name;
        categoryList.add(stayCategory);

        addCategoryTab(categoryList);
        mFragmentPagerAdapter.addFragment(basePagerFragment);
        getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);

        return basePagerFragment.getCompleteCreatedObservable().map(new Function()
        {
            @Override
            public BasePagerFragment apply(Object o) throws Exception
            {
                return basePagerFragment;
            }
        });
    }

    private Observable<Boolean> addSearchResultFragment(String callByScreen, List<StayCategory> categoryList)
    {
        if (mFragmentPagerAdapter == null || categoryList == null)
        {
            return null;
        }

        List<Observable<BasePagerFragment>> fragmentList = new ArrayList<>();

        for (StayCategory category : categoryList)
        {
            fragmentList.add(addSearchResultFragment(callByScreen, category));
        }

        addCategoryTab(categoryList);
        mFragmentPagerAdapter.notifyDataSetChanged();

        return Observable.zip(fragmentList, new Function<Object[], Boolean>()
        {
            @Override
            public Boolean apply(Object[] objects) throws Exception
            {
                initCategoryTabLayout();

                return true;
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    private void initCategoryTabLayout()
    {
        getViewDataBinding().viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(getViewDataBinding().categoryTabLayout));

        getViewDataBinding().categoryTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
        {
            @Override
            public void onTabSelected(TabLayout.Tab tab)
            {
                getEventListener().onCategoryTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab)
            {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab)
            {
                getEventListener().onCategoryTabReselected(tab);
            }
        });

        FontManager.apply(getViewDataBinding().categoryTabLayout, FontManager.getInstance(getContext()).getRegularTypeface());
    }

    private void addCategoryTab(@NonNull List<StayCategory> categoryList)
    {
        if (categoryList == null)
        {
            return;
        }

        Category category;
        TabLayout.Tab tab;

        int size = categoryList.size();

        for (StayCategory stayCategory : categoryList)
        {
            category = new Category(stayCategory.code, stayCategory.name);

            tab = getViewDataBinding().categoryTabLayout.newTab();
            tab.setText(category.name);
            tab.setTag(category);
            getViewDataBinding().categoryTabLayout.addTab(tab);
        }
    }

    private Observable<BasePagerFragment> addSearchResultFragment(String callByScreen, StayCategory category)
    {
        if (mFragmentPagerAdapter == null || category == null)
        {
            return null;
        }

        BasePagerFragment basePagerFragment = new SearchStayResultListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("callByScreen", callByScreen);
        bundle.putString("categoryName", category.name);
        bundle.putString("categoryCode", category.code);
        basePagerFragment.setArguments(bundle);

        basePagerFragment.setOnFragmentEventListener(new SearchStayResultListFragment.OnEventListener()
        {
            @Override
            public void setEmptyViewVisible(boolean visible)
            {
                getEventListener().setEmptyViewVisible(visible);
            }

            @Override
            public void onEmptyStayResearchClick()
            {
                getEventListener().onEmptyStayResearchClick();
            }

            @Override
            public void onFilterClick()
            {
                getEventListener().onFilterClick();
            }

            @Override
            public void onCalendarClick()
            {
                getEventListener().onCalendarClick();
            }

            @Override
            public void onRadiusClick()
            {
                getViewDataBinding().toolbarView.showRadiusSpinnerPopup();
            }

            @Override
            public Observable<Boolean> addCategoryList(List<StayCategory> categoryList)
            {
                return null;
            }
        });

        mFragmentPagerAdapter.addFragment(basePagerFragment);

        return basePagerFragment.getCompleteCreatedObservable().map(new Function()
        {
            @Override
            public BasePagerFragment apply(Object o) throws Exception
            {
                return basePagerFragment;
            }
        });
    }

    @Override
    public void setEmptyViewVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyViewCampaignTagVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setCampaignTagVisible(visible);
    }

    @Override
    public void setEmptyViewCampaignTag(String title, List<CampaignTag> campaignTagList)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().emptyView.setCampaignTag(title, campaignTagList);
    }

    @Override
    public boolean onFragmentBackPressed()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return false;
        }

        return mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).onBackPressed();
    }

    @Override
    public void refreshCurrentFragment()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return;
        }

        mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).onRefresh();
    }

    @Override
    public void removeAllFragment()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryTabLayout.removeAllTabs();

        getViewDataBinding().viewPager.clearOnPageChangeListeners();
        getViewDataBinding().viewPager.setAdapter(null);
        getViewDataBinding().viewPager.removeAllViews();

        if (mFragmentPagerAdapter != null)
        {
            mFragmentPagerAdapter.removeAll();
            mFragmentPagerAdapter = null;
        }
    }

    @Override
    public void setCategoryVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCategoryTabSelect(int position)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().viewPager.setCurrentItem(position);
    }

    @Override
    public void onSelectedCategory()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return;
        }

        int count = mFragmentPagerAdapter.getCount();
        int selectedIndex = getViewDataBinding().viewPager.getCurrentItem();

        for (int i = 0; i < count; i++)
        {
            if (i == selectedIndex)
            {
                mFragmentPagerAdapter.getItem(i).onSelected();
            } else
            {
                mFragmentPagerAdapter.getItem(i).onUnselected();
            }
        }
    }

    @Override
    public void refreshCurrentCategory()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return;
        }

        mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).onRefresh();
    }

    @Override
    public void scrollTopCurrentCategory()
    {
        if (getViewDataBinding() == null || mFragmentPagerAdapter == null)
        {
            return;
        }

        mFragmentPagerAdapter.getItem(getViewDataBinding().viewPager.getCurrentItem()).scrollTop();
    }

    private class RadiusArrayAdapter extends ArrayAdapter<CharSequence>
    {
        private int mSelectedPosition;

        public RadiusArrayAdapter(Context context, int resourceId, CharSequence[] list)
        {
            super(context, resourceId, list);
        }

        public void setSelection(int position)
        {
            mSelectedPosition = position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            View view = super.getDropDownView(position, convertView, parent);

            if (view != null)
            {
                setRadiusTextView(position, (TextView) view);
            }

            return view;
        }

        private void setRadiusTextView(int position, TextView textView)
        {
            if (textView == null)
            {
                return;
            }

            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            textView.setSelected(mSelectedPosition == position);

            if (mSelectedPosition == position)
            {
                textView.setTextColor(getColor(R.color.default_text_cb70038));
            } else
            {
                textView.setTextColor(getColor(R.color.default_text_c323232));
            }
        }
    }
}
