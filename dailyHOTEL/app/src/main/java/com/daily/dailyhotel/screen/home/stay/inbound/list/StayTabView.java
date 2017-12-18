package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Category;
import com.twoheart.dailyhotel.databinding.ActivityStayListDataBinding;

import java.util.ArrayList;
import java.util.List;

public class StayTabView extends BaseDialogView<StayTabView.OnEventListener, ActivityStayListDataBinding> implements StayTabInterface
{
    public interface OnEventListener extends OnBaseEventListener
    {
    }

    public StayTabView(BaseActivity baseActivity, StayTabView.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityStayListDataBinding viewDataBinding)
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
    }

    @Override
    public void setToolbarDateText(String text)
    {

    }

    @Override
    public void setToolbarRegionText(String text)
    {

    }

    @Override
    public void setCategoryTabLayout(FragmentManager fragmentManager, List<? extends Category> categoryList, Category selectedCategory)
    {
        if(getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().categoryTabLayout.setOnTabSelectedListener(null);

            if (categoryList == null)
            {
                getViewDataBinding().viewPager.removeAllViews();
                setCategoryTabLayoutVisibility(View.GONE);
                return;
            }

            int size = categoryList.size();

            if (size <= 2)
            {
                size = 1;
                setCategoryTabLayoutVisibility(View.GONE);

                mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mFloatingActionView, listener);

                mViewPager.removeAllViews();
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

                mViewPager.removeAllViews();
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

    @Override
    public void setOptionFilterSelected(boolean selected)
    {

    }

    private void initToolbar(ActivityStayListDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        if(getViewDataBinding() == null)
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

    private StayTabFragmentPagerAdapter createFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        StayTabFragmentPagerAdapter fragmentPagerAdapter = new StayTabFragmentPagerAdapter(fragmentManager);

        List<StayListFragment> fragmentList = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            StayListFragment stayListFragment = new StayListFragment();
            stayListFragment.setPlaceOnListFragmentListener(listener);
            stayListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(stayListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }
}
