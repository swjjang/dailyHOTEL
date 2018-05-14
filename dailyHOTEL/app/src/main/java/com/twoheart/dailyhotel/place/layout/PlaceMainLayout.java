package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailyStayListNavigationBarView;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Constants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceMainLayout extends BaseBlurLayout
{
    private DailyStayListNavigationBarView mNavigationBarView;

    protected AppBarLayout mAppBarLayout;
    protected DailyToolbarView mToolbarView;
    protected DailyFloatingActionView mFloatingActionView;

    TabLayout mCategoryTabLayout;
    private View mAppBarUnderlineView;
    ViewPager mViewPager;
    private PlaceListFragmentPagerAdapter mFragmentPagerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabUnselected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);

        void onSearchClick();

        void onDateClick();

        void onRegionClick();

        void onViewTypeClick();// 리스트, 맵 타입

        void onFilterClick();

        void onPageScroll();

        void onPageSelected(int changedPosition, int prevPosition);

        void onCartMenusBookingClick();
    }

    protected abstract PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager//
        , int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener);

    protected abstract void onAnalyticsCategoryFlicking(String category);

    protected abstract void onAnalyticsCategoryClick(String category);

    protected abstract String getAppBarTitle();

    public PlaceMainLayout(Context context, OnEventListener listener)
    {
        super(context, listener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }

    private void initToolbar(View view)
    {
        mAppBarLayout = view.findViewById(R.id.appBarLayout);
        mAppBarLayout.setTag(0);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            final int dp52Height = mContext.getResources().getDimensionPixelSize(R.dimen.toolbar_height);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (mViewPager == null)
                {
                    return;
                }

                if (verticalOffset == 0)
                {
                    if (mViewPager.getPaddingBottom() == dp52Height)
                    {
                        return;
                    }

                    mViewPager.setPadding(mViewPager.getPaddingLeft(), mViewPager.getPaddingTop(), mViewPager.getPaddingRight(), dp52Height);
                } else
                {
                    if (mViewPager.getPaddingBottom() == 0)
                    {
                        return;
                    }

                    mViewPager.setPadding(mViewPager.getPaddingLeft(), mViewPager.getPaddingTop(), mViewPager.getPaddingRight(), 0);
                }
            }
        });

        mToolbarView = mAppBarLayout.findViewById(R.id.toolbarView);
        mToolbarView.setTitleText(getAppBarTitle());
        mToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        mToolbarView.clearMenuItem();

        mToolbarView.addMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, 0, null);

        mToolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, false);

        mToolbarView.addMenuItem(DailyToolbarView.MenuItem.SEARCH, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onSearchClick();
            }
        });

        // 지역 이름
        // 날짜
        mNavigationBarView = view.findViewById(R.id.navigationBarView);
        mNavigationBarView.setOnDateClickListener(v -> ((PlaceMainLayout.OnEventListener) mOnEventListener).onDateClick());
        mNavigationBarView.setOnRegionClickListener(v -> ((PlaceMainLayout.OnEventListener) mOnEventListener).onRegionClick());
    }

    private void initOptionLayout(View view)
    {
        // 하단 지도 필터
        mFloatingActionView = view.findViewById(R.id.floatingActionView);
        mFloatingActionView.setOnViewOptionClickListener(v -> ((OnEventListener) mOnEventListener).onViewTypeClick());
        mFloatingActionView.setOnFilterOptionClickListener(v -> ((OnEventListener) mOnEventListener).onFilterClick());

        // 기본 설정
        setOptionViewTypeView(Constants.ViewType.LIST);

        showBottomLayout();
        setOptionViewTypeEnabled(true);
        setOptionFilterEnabled(true);
    }

    private void initCategoryTabLayout(View view)
    {
        mCategoryTabLayout = view.findViewById(R.id.categoryTabLayout);
        mAppBarUnderlineView = view.findViewById(R.id.appBarUnderline);
        mViewPager = view.findViewById(R.id.viewPager);
    }

    public void setToolbarRegionText(String region)
    {
        if (mNavigationBarView == null)
        {
            return;
        }

        mNavigationBarView.setRegionText(region);
    }

    public void setToolbarDateText(String text)
    {
        if (mNavigationBarView == null)
        {
            return;
        }

        mNavigationBarView.setDateText(text);
    }

    public void setToolbarCartMenusVisible(boolean visible)
    {
        if (mToolbarView == null)
        {
            return;
        }

        mToolbarView.setMenuItemVisible(DailyToolbarView.MenuItem.ORDER_MENUS, visible);
    }

    public void setToolbarCartMenusCount(int count)
    {
        if (mToolbarView == null)
        {
            return;
        }

        mToolbarView.updateMenuItem(DailyToolbarView.MenuItem.ORDER_MENUS, null, count, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).onCartMenusBookingClick();
            }
        });
    }

    public void setOptionViewTypeView(Constants.ViewType viewType)
    {
        switch (viewType)
        {
            case LIST:
                mFloatingActionView.setViewOption(DailyFloatingActionView.ViewOption.MAP);
                break;

            case MAP:
                mFloatingActionView.setViewOption(DailyFloatingActionView.ViewOption.LIST);
                break;
        }
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        ((View) mCategoryTabLayout.getParent()).setVisibility(visibility);

        ViewGroup.LayoutParams layoutParams = mAppBarUnderlineView.getLayoutParams();

        if (layoutParams != null)
        {
            if (visibility == View.VISIBLE)
            {
                layoutParams.height = 1;
            } else
            {
                layoutParams.height = ScreenUtils.dpToPx(mContext, 1);
            }

            mAppBarUnderlineView.setLayoutParams(layoutParams);
        }
    }

    public void setCurrentItem(int item)
    {
        if (mViewPager != null)
        {
            mViewPager.setCurrentItem(item);
        }
    }

    public void setCategoryTabLayout(FragmentManager fragmentManager, List<? extends Category> categoryList//
        , Category selectedCategory, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        mCategoryTabLayout.setOnTabSelectedListener(null);

        if (categoryList == null)
        {
            if (mViewPager.getChildCount() > 0)
            {
                mViewPager.removeAllViews();
            }

            setCategoryTabLayoutVisibility(View.GONE);
            return;
        }

        int size = categoryList.size();

        if (size <= 2)
        {
            size = 1;
            setCategoryTabLayoutVisibility(View.GONE);

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mFloatingActionView, listener);

            if (mViewPager.getChildCount() > 0)
            {
                mViewPager.removeAllViews();
            }

            mViewPager.setOffscreenPageLimit(size);
            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.clearOnPageChangeListeners();

            mCategoryTabLayout.setOnTabSelectedListener(null);
        } else
        {
            setCategoryTabLayoutVisibility(View.VISIBLE);

            Category category;
            TabLayout.Tab tab;
            TabLayout.Tab selectedTab = null;

            mCategoryTabLayout.removeAllTabs();

            int position = 0;

            for (int i = 0; i < size; i++)
            {
                category = categoryList.get(i);

                tab = mCategoryTabLayout.newTab();
                tab.setText(category.name);
                tab.setTag(category);
                mCategoryTabLayout.addTab(tab);

                if (selectedCategory != null && category.code.equalsIgnoreCase(selectedCategory.code) == true)
                {
                    position = i;
                    selectedTab = tab;
                }
            }

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mFloatingActionView, listener);

            if (mViewPager.getChildCount() > 0)
            {
                mViewPager.removeAllViews();
            }

            mViewPager.setOffscreenPageLimit(size);

            Class reflectionClass = ViewPager.class;

            try
            {
                Field mCurItem = reflectionClass.getDeclaredField("mCurItem");
                mCurItem.setAccessible(true);
                mCurItem.setInt(mViewPager, position);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.clearOnPageChangeListeners();
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mCategoryTabLayout));
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
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

                            onAnalyticsCategoryFlicking(mCategoryTabLayout.getTabAt(position).getText().toString());
                        } else
                        {
                            onAnalyticsCategoryClick(mCategoryTabLayout.getTabAt(position).getText().toString());
                        }
                    } else
                    {
                        isScrolling = false;
                    }

                    ((OnEventListener) mOnEventListener).onPageSelected(position, prevPosition);

                    prevPosition = position;

                    PlaceListFragment placeListFragment = getPlaceListFragment().get(position);

                    if (placeListFragment.getPlaceCount() == 0)
                    {
                        if (placeListFragment.isDefaultFilter() == true)
                        {
                            setBottomOptionVisible(false);
                        } else
                        {
                            setBottomOptionVisible(true);
                            setOptionViewTypeEnabled(false);
                            setOptionFilterEnabled(true);
                        }
                    } else
                    {
                        setBottomOptionVisible(true);
                        setOptionViewTypeEnabled(true);
                        setOptionFilterEnabled(true);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                    switch (state)
                    {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            isScrolling = true;
                            //                            hideBottomLayout();

                            ((OnEventListener) mOnEventListener).onPageScroll();
                            break;

                        case ViewPager.SCROLL_STATE_IDLE:
                            PlaceListFragment placeListFragment = getCurrentPlaceListFragment();

                            if (placeListFragment.getPlaceCount() == 0)
                            {
                                if (placeListFragment.isDefaultFilter() == true)
                                {
                                    setBottomOptionVisible(false);
                                } else
                                {
                                    setBottomOptionVisible(true);
                                    setOptionViewTypeEnabled(false);
                                    setOptionFilterEnabled(true);
                                }
                            } else
                            {
                                setBottomOptionVisible(true);
                                setOptionViewTypeEnabled(true);
                                setOptionFilterEnabled(true);
                            }
                            break;
                    }
                }
            });

            if (selectedTab != null)
            {
                selectedTab.select();
            }

            mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);

            FontManager.apply(mCategoryTabLayout, FontManager.getInstance(mContext).getRegularTypeface());
        }
    }

    public PlaceListFragment getCurrentPlaceListFragment()
    {
        return (PlaceListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    public ArrayList<PlaceListFragment> getPlaceListFragment()
    {
        if (mFragmentPagerAdapter == null)
        {
            return null;
        }

        return mFragmentPagerAdapter.getFragmentList();
    }

    public void setOptionViewTypeEnabled(boolean enabled)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setViewOptionEnabled(enabled);
    }

    public void setOptionFilterEnabled(boolean enabled)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setFilterOptionEnable(enabled);
    }

    public void setOptionFilterSelected(boolean selected)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setFilterOptionSelected(selected);
    }

    public synchronized void showAppBarLayout(boolean animate)
    {
        if (mAppBarLayout == null)
        {
            return;
        }

        mAppBarLayout.setExpanded(true, animate);
    }

    public synchronized void showBottomLayout()
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        setBottomOptionVisible(true);
        mFloatingActionView.setTranslationY(0);
    }

    public void hideBottomLayout()
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        setBottomOptionVisible(false);
    }

    public void setBottomOptionVisible(boolean visible)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    private TabLayout.OnTabSelectedListener mOnCategoryTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            ((PlaceMainLayout.OnEventListener) mOnEventListener).onCategoryTabSelected(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            ((PlaceMainLayout.OnEventListener) mOnEventListener).onCategoryTabUnselected(tab);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            ((PlaceMainLayout.OnEventListener) mOnEventListener).onCategoryTabReselected(tab);
        }
    };
}
