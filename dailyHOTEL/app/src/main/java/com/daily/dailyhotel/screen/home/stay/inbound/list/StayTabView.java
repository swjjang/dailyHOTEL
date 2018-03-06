package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityStayTabDataBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class StayTabView extends BaseDialogView<StayTabInterface.OnEventListener, ActivityStayTabDataBinding> //
    implements StayTabInterface.ViewInterface
{
    BaseFragmentPagerAdapter<StayListFragment> mFragmentPagerAdapter;

    public StayTabView(BaseActivity baseActivity, StayTabInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);

        viewDataBinding.navigationBarView.setOnRegionClickListener(v -> getEventListener().onRegionClick());
        viewDataBinding.navigationBarView.setOnDateClickListener(v -> getEventListener().onCalendarClick());

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

    @Override
    public void setToolbarDateText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().navigationBarView.setDateText(text);
    }

    @Override
    public void setToolbarRegionText(String text)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().navigationBarView.setRegionText(text);
    }

    @Override
    public Observable<Boolean> getCategoryTabLayout(List<Category> categoryList, Category selectedCategory)
    {
        if (getViewDataBinding() == null)
        {
            return Observable.just(false);
        }

        if (mFragmentPagerAdapter != null)
        {
            mFragmentPagerAdapter.removeAll();
        }

        getViewDataBinding().viewPager.setAdapter(null);
        getViewDataBinding().viewPager.removeAllViews();

        getViewDataBinding().categoryTabLayout.setOnTabSelectedListener(null);

        final List<Category> categoryTabList = new ArrayList<>();

        // 카테고리가 2보다 작으면 전체 하나만 보여주기 때문에 카테고리 탭을 안보이도록 한다.
        if (categoryList == null || categoryList.size() <= 2)
        {
            categoryTabList.add(Category.ALL);
        } else
        {
            categoryTabList.addAll(categoryList);
        }

        int size = categoryTabList.size();

        setCategoryTabLayoutVisibility(size == 1 ? View.GONE : View.VISIBLE);
        makeCategoryTab(categoryTabList);
        TabLayout.Tab selectedTab = getCategoryTab(selectedCategory);

        return createFragmentPagerAdapter(getSupportFragmentManager(), categoryList).map(new Function<BaseFragmentPagerAdapter, Boolean>()
        {
            @Override
            public Boolean apply(BaseFragmentPagerAdapter baseFragmentPagerAdapter) throws Exception
            {
                mFragmentPagerAdapter = baseFragmentPagerAdapter;

                getViewDataBinding().viewPager.setOffscreenPageLimit(size);

                Class reflectionClass = ViewPager.class;

                try
                {
                    Field mCurItem = reflectionClass.getDeclaredField("mCurItem");
                    mCurItem.setAccessible(true);
                    mCurItem.setInt(getViewDataBinding().viewPager, selectedTab.getPosition());
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);
                getViewDataBinding().viewPager.clearOnPageChangeListeners();
                getViewDataBinding().viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(getViewDataBinding().categoryTabLayout));
                getViewDataBinding().viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {
                    boolean isScrolling = false;
                    int prevPosition = -1;

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                    {

                    }

                    @Override
                    public void onPageSelected(int position)
                    {
                        if (prevPosition != position)
                        {
                            if (isScrolling == true)
                            {
                                isScrolling = false;

                                getEventListener().onCategoryFlicking(getViewDataBinding().categoryTabLayout.getTabAt(position).getText().toString());
                            } else
                            {
                                getEventListener().onCategoryClick(getViewDataBinding().categoryTabLayout.getTabAt(position).getText().toString());
                            }
                        } else
                        {
                            isScrolling = false;
                        }

                        prevPosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state)
                    {
                        switch (state)
                        {
                            case ViewPager.SCROLL_STATE_DRAGGING:
                                isScrolling = true;
                                break;

                            case ViewPager.SCROLL_STATE_IDLE:
                                break;
                        }
                    }
                });

                if (selectedTab != null)
                {
                    selectedTab.select();

                    getEventListener().onCategoryTabSelected(selectedTab);
                }

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

                return true;
            }
        });
    }

    private void makeCategoryTab(@NonNull List<Category> categoryList)
    {
        Category category;
        TabLayout.Tab tab;

        getViewDataBinding().categoryTabLayout.removeAllTabs();

        int size = categoryList.size();

        for (int i = 0; i < size; i++)
        {
            category = categoryList.get(i);

            tab = getViewDataBinding().categoryTabLayout.newTab();
            tab.setText(category.name);
            tab.setTag(category);
            getViewDataBinding().categoryTabLayout.addTab(tab);
        }
    }

    private TabLayout.Tab getCategoryTab(Category searchCategory)
    {
        if (searchCategory == null)
        {
            return null;
        }

        int count = getViewDataBinding().categoryTabLayout.getTabCount();
        Category category;
        TabLayout.Tab tab;

        for (int i = 0; i < count; i++)
        {
            tab = getViewDataBinding().categoryTabLayout.getTabAt(i);
            category = (Category) tab.getTag();

            if (category.code.equalsIgnoreCase(searchCategory.code) == true)
            {
                return tab;
            }
        }

        return null;
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
    public void setViewType(StayTabPresenter.ViewType viewType)
    {
        if (viewType == null || getViewDataBinding() == null)
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

    @Override
    public void showPreviewGuide()
    {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.view_dialog_preview_layout, null, false);

        View confirmTextView = dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dialogView, null, null, false);
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

    private void initToolbar(ActivityStayTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());

        viewDataBinding.toolbarView.clearMenuItem();

        viewDataBinding.toolbarView.addMenuItem(DailyToolbarView.MenuItem.SEARCH, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onSearchClick();
            }
        });
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryLayout.setVisibility(visibility);

        ViewGroup.LayoutParams layoutParams = getViewDataBinding().navigationBarUnderlineView.getLayoutParams();

        if (layoutParams != null)
        {
            if (visibility == View.VISIBLE)
            {
                layoutParams.height = 1;
            } else
            {
                layoutParams.height = ScreenUtils.dpToPx(getContext(), 1);
            }

            getViewDataBinding().navigationBarUnderlineView.setLayoutParams(layoutParams);
        }
    }

    private Observable<BaseFragmentPagerAdapter> createFragmentPagerAdapter(FragmentManager fragmentManager, List<? extends Category> categoryList)
    {
        if (fragmentManager == null || categoryList == null || categoryList.size() == 0)
        {
            return null;
        }

        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(fragmentManager);

        List<StayListFragment> fragmentList = new ArrayList<>();
        List<Observable<Boolean>> fragmentListObservable = new ArrayList<>();

        for (Category category : categoryList)
        {
            StayListFragment stayListFragment = new StayListFragment();

            Bundle bundle = new Bundle();
            bundle.putString("name", category.name);
            bundle.putString("code", category.code);

            stayListFragment.setArguments(bundle);
            stayListFragment.setOnFragmentEventListener(new StayListFragment.OnEventListener()
            {
                @Override
                public void onRegionClick()
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onRegionClick();
                }

                @Override
                public void onCalendarClick()
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onCalendarClick();
                }

                @Override
                public void onFilterClick()
                {
                    if (getViewDataBinding() == null)
                    {
                        return;
                    }

                    getEventListener().onFilterClick();
                }

                @Override
                public void setCategoryVisible(boolean visible)
                {
                    if (categoryList.size() <= 2)
                    {
                        return;
                    }

                    setCategoryTabLayoutVisibility(visible ? View.VISIBLE : View.GONE);
                }

                @Override
                public void setCategory(List<Category> categoryList)
                {

                }
            });

            fragmentList.add(stayListFragment);
            fragmentListObservable.add(stayListFragment.getCompleteCreatedObservable());
        }

        fragmentPagerAdapter.setFragmentList(fragmentList);

        return Observable.zip(fragmentListObservable, new Function<Object[], BaseFragmentPagerAdapter>()
        {
            @Override
            public BaseFragmentPagerAdapter apply(Object[] objects) throws Exception
            {
                return fragmentPagerAdapter;
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }
}
