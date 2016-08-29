package com.twoheart.dailyhotel.screen.search.stay.result;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceSearchResultLayout;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class StaySearchResultLayout extends PlaceSearchResultLayout
{
    public StaySearchResultLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    protected void setCalendarText(SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        String checkInDay = checkInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        String checkOutDay = checkOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        int nights = checkOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();

        setCalendarText(String.format("%s - %s, %d박", checkInDay, checkOutDay, nights));
    }

    @Override
    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_hotel_ic;
    }

    @Override
    protected synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StaySearchResultListFragment> list = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            StaySearchResultListFragment staySearchResultListFragment = new StaySearchResultListFragment();
            staySearchResultListFragment.setPlaceOnListFragmentListener(listener);
            staySearchResultListFragment.setBottomOptionLayout(bottomOptionLayout);
            list.add(staySearchResultListFragment);
        }

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void onAnalyticsCategoryFlicking(String category)
    {
        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.SCREEN, AnalyticsManager.Screen.SEARCH_RESULT);
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.DAILY_HOTEL_CATEGORY_FLICKING, category, params);
    }

    @Override
    protected void onAnalyticsCategoryClick(String category)
    {
        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.SCREEN, AnalyticsManager.Screen.SEARCH_RESULT);
        AnalyticsManager.getInstance(mContext).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.HOTEL_CATEGORY_CLICKED, category, params);
    }

    /**
     * 매번 add하는 것은 아니고 setCategoryAllTabLayout이후로 한번만 호출되어야 한다 여러번 안됨.
     *
     * @param categoryList
     * @param listener
     */
    public void addCategoryTabLayout(List<Category> categoryList,//
                                     PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (categoryList == null)
        {
            return;
        }

        int size = categoryList.size();

        if (size + mCategoryTabLayout.getTabCount() <= 2)
        {
            size = 1;
            setCategoryTabLayoutVisibility(View.GONE);

            mViewPager.setOffscreenPageLimit(size);
            mViewPager.clearOnPageChangeListeners();
        } else
        {
            setCategoryTabLayoutVisibility(View.VISIBLE);

            Category category;
            TabLayout.Tab tab;
            ArrayList<PlaceListFragment> list = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
            {
                category = categoryList.get(i);

                tab = mCategoryTabLayout.newTab();
                tab.setText(category.name);
                tab.setTag(category);
                mCategoryTabLayout.addTab(tab);

                StaySearchResultListFragment searchResultListFragment = new StaySearchResultListFragment();
                searchResultListFragment.setPlaceOnListFragmentListener(listener);
                searchResultListFragment.setBottomOptionLayout(mBottomOptionLayout);
                list.add(searchResultListFragment);
            }

            mFragmentPagerAdapter.addPlaceListFragment(list);
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(mCategoryTabLayout.getTabCount());

            mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);

            FontManager.apply(mCategoryTabLayout, FontManager.getInstance(mContext).getRegularTypeface());
        }
    }

    public void removeCategoryTab(HashSet<String> existCategorySet)
    {
        int count = mCategoryTabLayout.getTabCount();
        TabLayout.Tab tab;
        Category category;

        for (int i = count - 1; i > 0; i--)
        {
            tab = mCategoryTabLayout.getTabAt(i);
            category = (Category) tab.getTag();

            if (existCategorySet.contains(category.code) == false)
            {
                mCategoryTabLayout.removeTabAt(i);
                mFragmentPagerAdapter.removeItem(i);
            }
        }

        int existTabCount = mCategoryTabLayout.getTabCount();

        // 2개 이하면 전체 탭 한개로 통합한다.
        if (existTabCount <= 2)
        {
            mCategoryTabLayout.removeTabAt(1);
            mFragmentPagerAdapter.removeItem(1);
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(1);
            mViewPager.clearOnPageChangeListeners();
            setCategoryTabLayoutVisibility(View.GONE);
        } else
        {
            mFragmentPagerAdapter.notifyDataSetChanged();

            mViewPager.setOffscreenPageLimit(existTabCount);
        }
    }
}
