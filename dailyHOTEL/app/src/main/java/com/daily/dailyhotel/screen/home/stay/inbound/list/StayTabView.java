package com.daily.dailyhotel.screen.home.stay.inbound.list;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.BaseFragmentPagerAdapter;
import com.daily.base.OnBaseEventListener;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.entity.Category;
import com.twoheart.dailyhotel.databinding.ActivityStayTabDataBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StayTabView extends BaseDialogView<StayTabView.OnEventListener, ActivityStayTabDataBinding> implements StayTabInterface
{
    private BaseFragmentPagerAdapter<StayListFragment> mFragmentPagerAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);
    }

    public StayTabView(BaseActivity baseActivity, StayTabView.OnEventListener listener)
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
        if (getViewDataBinding() == null)
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

            mFragmentPagerAdapter = createFragmentPagerAdapter(fragmentManager, size);

            getViewDataBinding().viewPager.removeAllViews();
            getViewDataBinding().viewPager.setOffscreenPageLimit(size);
            getViewDataBinding().viewPager.setAdapter(mFragmentPagerAdapter);
            getViewDataBinding().viewPager.clearOnPageChangeListeners();

            getViewDataBinding().categoryTabLayout.setOnTabSelectedListener(null);
        } else
        {
            setCategoryTabLayoutVisibility(View.VISIBLE);

            Category category;
            TabLayout.Tab tab;
            TabLayout.Tab selectedTab = null;

            getViewDataBinding().categoryTabLayout.removeAllTabs();

            int position = 0;

            for (int i = 0; i < size; i++)
            {
                category = categoryList.get(i);

                tab = getViewDataBinding().categoryTabLayout.newTab();
                tab.setText(category.name);
                tab.setTag(category);
                getViewDataBinding().categoryTabLayout.addTab(tab);

                if (selectedCategory != null && category.code.equalsIgnoreCase(selectedCategory.code) == true)
                {
                    position = i;
                    selectedTab = tab;
                }
            }

            mFragmentPagerAdapter = createFragmentPagerAdapter(fragmentManager, size);

            getViewDataBinding().viewPager.removeAllViews();
            getViewDataBinding().viewPager.setOffscreenPageLimit(size);

            Class reflectionClass = ViewPager.class;

            try
            {
                Field mCurItem = reflectionClass.getDeclaredField("mCurItem");
                mCurItem.setAccessible(true);
                mCurItem.setInt(getViewDataBinding().viewPager, position);
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

                            //                            onAnalyticsCategoryFlicking(mCategoryTabLayout.getTabAt(position).getText().toString());
                        } else
                        {
                            //                            onAnalyticsCategoryClick(mCategoryTabLayout.getTabAt(position).getText().toString());
                        }
                    } else
                    {
                        isScrolling = false;
                    }

//                    getEventListener().onPageSelected(mFragmentPagerAdapter.getItem(position), mFragmentPagerAdapter.getItem(prevPosition));

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
        }
    }

    @Override
    public void setOptionFilterSelected(boolean selected)
    {

    }

    @Override
    public void setCategoryTab(int position)
    {

    }

    private void initToolbar(ActivityStayTabDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
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

    private BaseFragmentPagerAdapter createFragmentPagerAdapter(FragmentManager fragmentManager, int count)
    {
        BaseFragmentPagerAdapter fragmentPagerAdapter = new BaseFragmentPagerAdapter(fragmentManager);

        List<StayListFragment> fragmentList = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            StayListFragment stayListFragment = new StayListFragment();
            fragmentList.add(stayListFragment);
        }

        fragmentPagerAdapter.setFragmentList(fragmentList);

        return fragmentPagerAdapter;
    }
}
