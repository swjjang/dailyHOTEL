package com.twoheart.dailyhotel.screen.home.category.nearby;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.daily.dailyhotel.view.DailySearchToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultListFragment;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 5. 19..
 */

@Deprecated
public class StayCategoryNearByLayout extends BaseBlurLayout
{
    DailySearchToolbarView mToolbarView;
    private View mSearchLocationLayout;
    private View mResultLayout;

    DailyFloatingActionView mFloatingActionView;

    protected TabLayout mCategoryTabLayout;
    protected ViewPager mViewPager;
    protected PlaceListFragmentPagerAdapter mFragmentPagerAdapter;

    DistanceFilterAdapter mDistanceFilterAdapter;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabUnselected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);

        void onDateClick();

        void onViewTypeClick();// 리스트, 맵 타입

        void onFilterClick();

        void finish(int resultCode);

        void research();

        void onShowCallDialog();

        void onItemSelectedSpinner(double radius);
    }

    public StayCategoryNearByLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    private synchronized PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, View bottomOptionLayout //
        , PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        PlaceListFragmentPagerAdapter placeListFragmentPagerAdapter = new PlaceListFragmentPagerAdapter(fragmentManager);

        ArrayList<StayCategoryNearByListFragment> list = new ArrayList<>();

        StayCategoryNearByListFragment stayCategoryNearByListFragment = new StayCategoryNearByListFragment();
        stayCategoryNearByListFragment.setPlaceOnListFragmentListener(listener);
        stayCategoryNearByListFragment.setBottomOptionLayout(bottomOptionLayout);
        list.add(stayCategoryNearByListFragment);

        placeListFragmentPagerAdapter.setPlaceFragmentList(list);

        return placeListFragmentPagerAdapter;
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        ScrollView emptyScrollView = view.findViewById(R.id.emptyScrollView);
        emptyScrollView.setVisibility(View.GONE);

        mSearchLocationLayout = view.findViewById(R.id.searchLocationLayout);
        mResultLayout = view.findViewById(R.id.resultLayout);

        initSearchLocationLayout(mSearchLocationLayout);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        mToolbarView = view.findViewById(R.id.toolbarView);

        mToolbarView.setOnToolbarListener(new DailySearchToolbarView.OnToolbarListener()
        {
            @Override
            public void onTitleClick()
            {
                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onDateClick();
            }

            @Override
            public void onBackClick()
            {
                ((OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED);
            }

            @Override
            public void onSelectedRadiusPosition(int position)
            {
                mDistanceFilterAdapter.setSelection(position);

                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onItemSelectedSpinner(getSpinnerRadiusValue(position));
            }
        });

        mToolbarView.setTitleImageResource(R.drawable.search_ic_01_date);

        CharSequence[] strings = mContext.getResources().getTextArray(R.array.search_result_distance_array);
        mDistanceFilterAdapter = new DistanceFilterAdapter(mContext, R.layout.list_row_search_result_spinner, strings);
        mDistanceFilterAdapter.setDropDownViewResource(R.layout.list_row_search_result_sort_dropdown_item);

        mToolbarView.setRadiusSpinnerAdapter(mDistanceFilterAdapter);
    }

    private void initCategoryTabLayout(View view)
    {
        mCategoryTabLayout = view.findViewById(R.id.categoryTabLayout);
        mViewPager = view.findViewById(R.id.viewPager);
    }

    void setCalendarText(StayBookingDay stayBookingDay)
    {
        if (mToolbarView == null || stayBookingDay == null)
        {
            return;
        }

        try
        {
            int nights = stayBookingDay.getNights();
            String dateFormat = ScreenUtils.getScreenWidth(mContext) < 720 ? "MM.dd" : "MM.dd(EEE)";
            String date = String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookingDay.getCheckInDay(dateFormat)//
                , stayBookingDay.getCheckOutDay(dateFormat), nights);

            mToolbarView.setSubTitleText(date);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    public void setToolbarTitle(String title)
    {
        if (mToolbarView == null)
        {
            return;
        }

        mToolbarView.setTitleText(title);
    }

    private void initSearchLocationLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.location_progressbar_cc8c8c8), PorterDuff.Mode.SRC_IN);
    }

    private void initOptionLayout(View view)
    {
        mFloatingActionView = view.findViewById(R.id.floatingActionView);
        mFloatingActionView.setOnViewOptionClickListener(v -> ((OnEventListener) mOnEventListener).onViewTypeClick());
        mFloatingActionView.setOnFilterOptionClickListener(v -> ((OnEventListener) mOnEventListener).onFilterClick());
        mFloatingActionView.post(() -> mFloatingActionView.setTag(mViewPager.getBottom() - mFloatingActionView.getTop()));

        // 기본 설정
        setOptionViewTypeView(Constants.ViewType.LIST);

        setOptionViewTypeEnabled(true);
        setOptionFilterEnabled(true);
    }

    public void setOptionViewTypeView(Constants.ViewType viewType)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

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

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        ((View) mCategoryTabLayout.getParent()).setVisibility(visibility);
    }

    public int getCategoryTabCount()
    {
        if (mCategoryTabLayout == null)
        {
            return 0;
        }

        return mCategoryTabLayout.getTabCount();
    }

    public void setCurrentItem(int item)
    {
        if (mViewPager != null)
        {
            mViewPager.setCurrentItem(item);
        }
    }

    public void setCategoryTabLayout(FragmentManager fragmentManager, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        mCategoryTabLayout.removeAllTabs();
        setCategoryTabLayoutVisibility(View.GONE);

        mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, mFloatingActionView, listener);

        mViewPager.removeAllViews();
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.clearOnPageChangeListeners();
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
                searchResultListFragment.setBottomOptionLayout(mFloatingActionView);
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

    public void clearCategoryTab()
    {
        if (mCategoryTabLayout != null)
        {
            mCategoryTabLayout.setOnTabSelectedListener(null);
            mCategoryTabLayout.removeAllTabs();
        }

        if (mFragmentPagerAdapter != null)
        {
            mFragmentPagerAdapter.removeAll();
        }

        if (mViewPager != null)
        {
            mViewPager.setAdapter(null);
            mViewPager.removeAllViews();
        }
    }

    public PlaceListFragment getCurrentPlaceListFragment()
    {
        return (PlaceListFragment) mFragmentPagerAdapter.getItem(mViewPager.getCurrentItem());
    }

    public ArrayList<PlaceListFragment> getPlaceListFragment()
    {
        return mFragmentPagerAdapter.getFragmentList();
    }

    public void setSpinnerVisible(boolean visible)
    {
        if (mToolbarView == null)
        {
            return;
        }

        mToolbarView.setRadiusSpinnerVisible(visible);
    }

    public void showSpinner()
    {
        if (mToolbarView == null)
        {
            return;
        }

        mToolbarView.showRadiusSpinnerPopup();
    }

    double getSpinnerRadiusValue(int spinnerPosition)
    {
        double radius;

        switch (spinnerPosition)
        {
            case 4:
                radius = 10d;
                break;

            case 3:
                radius = 5d;
                break;

            case 2:
                radius = 3d;
                break;

            case 1:
                radius = 1d;
                break;

            case 0:
                radius = 0.5d;
                break;

            default:
                radius = 3d;
                break;
        }

        return radius;
    }

    public void setSelectionSpinner(double radius)
    {
        if (mToolbarView == null)
        {
            return;
        }

        int position;

        if (radius > 5)
        {
            position = 4; // 10km
        } else if (radius > 3)
        {
            position = 3; // 5km
        } else if (radius > 1)
        {
            position = 2; // 3km
        } else if (radius > 0.5)
        {
            position = 1; // 1km
        } else
        {
            position = 0; // 0.5km
        }

        mToolbarView.setRadiusSpinnerSelection(position);
    }

    public void setScreenVisible(StayCategoryNearByActivity.ScreenType screenType)
    {
        if (screenType == null)
        {
            return;
        }

        switch (screenType)
        {
            case NONE:
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case EMPTY:
                mResultLayout.setVisibility(View.VISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case SEARCH_LOCATION:
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.VISIBLE);
                break;

            case LIST:
                mResultLayout.setVisibility(View.VISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;
        }
    }

    public boolean isEmptyLayout()
    {
        return mResultLayout.getVisibility() != View.VISIBLE;
    }

    void setMenuBarLayoutTranslationY(float dy)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setTranslationY(dy);
    }

    public void setMenuBarLayoutVisible(boolean visible)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setVisibility(visible ? View.VISIBLE : View.GONE);
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

    protected TabLayout.OnTabSelectedListener mOnCategoryTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onCategoryTabSelected(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onCategoryTabUnselected(tab);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onCategoryTabReselected(tab);
        }
    };

    private class DistanceFilterAdapter extends ArrayAdapter<CharSequence>
    {
        private int mSelectedPosition;

        public DistanceFilterAdapter(Context context, int resourceId, CharSequence[] list)
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
                TextView textView = (TextView) view;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                textView.setSelected(mSelectedPosition == position);

                if (mSelectedPosition == position)
                {
                    textView.setTextColor(mContext.getResources().getColor(R.color.default_text_cb70038));
                } else
                {
                    textView.setTextColor(mContext.getResources().getColor(R.color.default_text_c323232));
                }
            }

            return view;
        }
    }
}
