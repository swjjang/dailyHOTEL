package com.twoheart.dailyhotel.place.layout;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.view.DailyFloatingActionView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceSearchResultLayout extends BaseBlurLayout
{
    private View mToolbar;
    protected TextView mCalendarTextView;
    private ScrollView mEmptyScrollView;
    private View mSearchLocationLayout;
    private View mResultLayout;

    protected DailyFloatingActionView mFloatingActionView;

    protected TabLayout mCategoryTabLayout;
    protected ViewPager mViewPager;
    protected PlaceListFragmentPagerAdapter mFragmentPagerAdapter;

    protected Spinner mDistanceFilterSpinner;
    DistanceFilterAdapter mDistanceFilterAdapter;

    Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
    Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    ValueAnimator mValueAnimator;

    protected String mCallByScreen;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onCategoryTabSelected(TabLayout.Tab tab);

        void onCategoryTabUnselected(TabLayout.Tab tab);

        void onCategoryTabReselected(TabLayout.Tab tab);

        void onDateClick();

        void onViewTypeClick();// 리스트, 맵 타입

        void onFilterClick();

        void finish(int resultCode);

        void research(int resultCode);

        void onShowCallDialog();

        void onItemSelectedSpinner(double radius);

        void onPageScroll();

        void onPageSelected(int changedPosition, int prevPosition);

        void onResearchClick();
    }

    protected abstract void initEmptyLayout(ScrollView scrollView);

    protected abstract int getEmptyIconResourceId();

    protected abstract PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener);

    protected abstract void onAnalyticsCategoryFlicking(String category);

    protected abstract void onAnalyticsCategoryClick(String category);

    public abstract void setCampaignTagVisible(boolean visible);

    public abstract void setCampaignTagList(List<CampaignTag> campaignTagList);

    public abstract boolean hasCampaignTag();

    public PlaceSearchResultLayout(Context context, String callByScreen, OnBaseEventListener listener)
    {
        super(context, listener);

        mCallByScreen = callByScreen;
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        mEmptyScrollView = view.findViewById(R.id.emptyScrollView);
        mSearchLocationLayout = view.findViewById(R.id.searchLocationLayout);
        mResultLayout = view.findViewById(R.id.resultLayout);

        initEmptyLayout(mEmptyScrollView);
        initSearchLocationLayout(mSearchLocationLayout);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        mToolbar = view.findViewById(R.id.toolbarLayout);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED));

        View titleBackgroundView = view.findViewById(R.id.titleBackgroundView);
        titleBackgroundView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onResearchClick());

        mCalendarTextView = view.findViewById(R.id.calendarTextView);
        mDistanceFilterSpinner = view.findViewById(R.id.distanceSpinner);

        CharSequence[] strings = mContext.getResources().getTextArray(R.array.search_result_distance_array);
        mDistanceFilterAdapter = new DistanceFilterAdapter(mContext, R.layout.list_row_search_result_spinner, strings);

        mDistanceFilterAdapter.setDropDownViewResource(R.layout.list_row_search_result_sort_dropdown_item);
        mDistanceFilterSpinner.setAdapter(mDistanceFilterAdapter);
        mDistanceFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                mDistanceFilterAdapter.setSelection(position);

                ((OnEventListener) mOnEventListener).onItemSelectedSpinner(getSpinnerRadiusValue(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    private void initCategoryTabLayout(View view)
    {
        mCategoryTabLayout = view.findViewById(R.id.categoryTabLayout);
        mViewPager = view.findViewById(R.id.viewPager);
    }

    public void setCalendarText(String date)
    {
        if (DailyTextUtils.isTextEmpty(date) == true || mCalendarTextView == null)
        {
            return;
        }

        mCalendarTextView.setText(date);
    }

    public void setToolbarTitle(String title)
    {
        TextView titleView = mToolbar.findViewById(R.id.titleView);

        if (titleView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        titleView.setText(title);
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

    public void setCategoryAllTabLayout(FragmentManager fragmentManager, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (fragmentManager == null)
        {
            return;
        }

        setCategoryTabLayoutVisibility(View.INVISIBLE);

        if (mFloatingActionView != null)
        {
            mFloatingActionView.setTranslationY(0);
        }

        mCategoryTabLayout.removeAllTabs();

        TabLayout.Tab tab;
        tab = mCategoryTabLayout.newTab();
        tab.setText(Category.ALL.name);
        tab.setTag(Category.ALL);
        mCategoryTabLayout.addTab(tab);

        mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, 1, mFloatingActionView, listener);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.removeAllViews();
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

        mCategoryTabLayout.setOnTabSelectedListener(mOnCategoryTabSelectedListener);

        FontManager.apply(mCategoryTabLayout, FontManager.getInstance(mContext).getRegularTypeface());
    }

    public void setCategoryTabLayout(FragmentManager fragmentManager, List<Category> categoryList//
        , Category selectedCategory, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (categoryList == null)
        {
            mCategoryTabLayout.removeAllTabs();
            mCategoryTabLayout.setOnTabSelectedListener(null);
            mViewPager.removeAllViews();
            setCategoryTabLayoutVisibility(View.GONE);
            return;
        }

        int size = categoryList.size();

        if (size <= 2)
        {
            size = 1;
            mCategoryTabLayout.removeAllTabs();
            setCategoryTabLayoutVisibility(View.GONE);

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mFloatingActionView, listener);

            mViewPager.removeAllViews();
            mViewPager.setOffscreenPageLimit(size);
            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.clearOnPageChangeListeners();
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

                    prevPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING)
                    {
                        isScrolling = true;
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
        if (mDistanceFilterSpinner == null)
        {
            return;
        }

        int flag = visible ? View.VISIBLE : View.GONE;

        mDistanceFilterSpinner.setVisibility(flag);
        mDistanceFilterSpinner.setEnabled(visible);

    }

    public void showSpinner()
    {
        if (mDistanceFilterSpinner == null)
        {
            return;
        }

        mDistanceFilterSpinner.performClick();
    }

    double getSpinnerRadiusValue(int spinnerPosition)
    {
        if (mDistanceFilterSpinner == null)
        {
            return 0d;
        }

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
        if (mDistanceFilterSpinner == null)
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

        mDistanceFilterSpinner.setSelection(position);
    }

    public void setScreenVisible(PlaceSearchResultActivity.ScreenType screenType)
    {
        if (screenType == null)
        {
            return;
        }

        switch (screenType)
        {
            case NONE:
                mEmptyScrollView.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case EMPTY:
                mEmptyScrollView.setVisibility(View.VISIBLE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case SEARCH_LOCATION:
                mEmptyScrollView.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.VISIBLE);
                break;

            case LIST:
                mEmptyScrollView.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.VISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;
        }
    }

    public boolean isEmptyLayout()
    {
        return mResultLayout.getVisibility() != View.VISIBLE;
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
            ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onCategoryTabSelected(tab);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onCategoryTabUnselected(tab);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onCategoryTabReselected(tab);
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
