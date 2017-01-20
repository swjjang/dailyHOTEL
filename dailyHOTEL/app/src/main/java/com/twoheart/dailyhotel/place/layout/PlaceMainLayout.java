package com.twoheart.dailyhotel.place.layout;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ScaleXSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.FontManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceMainLayout extends BaseLayout implements View.OnClickListener
{
    private TextView mRegionTextView;
    private TextView mDateTextView;

    protected AppBarLayout mAppBarLayout;
    protected View mToolbarLayout;
    protected View mBottomOptionLayout;
    private View mViewTypeOptionImageView;
    private View mFilterOptionImageView;

    TabLayout mCategoryTabLayout;
    private View mToolbarUnderlineView;
    private ViewPager mViewPager;
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
    }

    protected abstract PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener);

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
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        mAppBarLayout.setTag(0);

        mToolbarLayout = mAppBarLayout.findViewById(R.id.toolbarLayout);
        View backImageView = mToolbarLayout.findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });

        TextView titleTextView = (TextView) mToolbarLayout.findViewById(R.id.titleTextView);
        titleTextView.setText(getAppBarTitle());

        // 검색
        // 지역 이름
        // 날짜
        View searchTextView = view.findViewById(R.id.searchTextView);

        View regionTextLayout = view.findViewById(R.id.regionTextLayout);
        mRegionTextView = (TextView) view.findViewById(R.id.regionTextView);

        View dateTextLayout = view.findViewById(R.id.dateTextLayout);
        mDateTextView = (TextView) view.findViewById(R.id.dateTextView);

        searchTextView.setOnClickListener(this);
        regionTextLayout.setOnClickListener(this);
        dateTextLayout.setOnClickListener(this);
    }

    private void initOptionLayout(View view)
    {
        mBottomOptionLayout = view.findViewById(R.id.bottomOptionLayout);

        // 하단 지도 필터
        mViewTypeOptionImageView = view.findViewById(R.id.viewTypeOptionImageView);
        mFilterOptionImageView = view.findViewById(R.id.filterOptionImageView);

        mViewTypeOptionImageView.setOnClickListener(this);
        mFilterOptionImageView.setOnClickListener(this);

        // 기본 설정
        setOptionViewTypeView(Constants.ViewType.LIST);
    }

    private void initCategoryTabLayout(View view)
    {
        mCategoryTabLayout = (TabLayout) view.findViewById(R.id.categoryTabLayout);
        mToolbarUnderlineView = view.findViewById(R.id.toolbarUnderline);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    public void setToolbarRegionText(String region)
    {
        mRegionTextView.setText(region);
    }

    public void setToolbarDateText(String text)
    {
        int viewWidth = mDateTextView.getWidth() - (mDateTextView.getCompoundDrawablePadding() * 2) - mDateTextView.getCompoundDrawables()[0].getIntrinsicWidth() - mDateTextView.getCompoundDrawables()[2].getIntrinsicWidth();

        final Typeface typeface = FontManager.getInstance(mContext).getRegularTypeface();
        final float width = Util.getTextWidth(mContext, text, 12d, typeface);

        if (viewWidth > width)
        {
            mDateTextView.setText(text);
        } else
        {
            float scaleX = 1f;
            float scaleWidth;

            for (int i = 99; i >= 60; i--)
            {
                scaleX = (float) i / 100;
                scaleWidth = Util.getScaleTextWidth(mContext, text, 12d, scaleX, typeface);

                if (viewWidth > scaleWidth)
                {
                    break;
                }
            }

            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new ScaleXSpan(scaleX), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mDateTextView.setText(spannableString);
        }
    }

    public void setOptionViewTypeView(Constants.ViewType viewType)
    {
        switch (viewType)
        {
            case LIST:
                mViewTypeOptionImageView.setBackgroundResource(R.drawable.fab_01_map);
                break;

            case MAP:
                mViewTypeOptionImageView.setBackgroundResource(R.drawable.fab_02_list);
                break;

            case GONE:
                break;
        }
    }

    public void setOptionFilterEnabled(boolean enabled)
    {
        mFilterOptionImageView.setSelected(enabled);
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        ((View) mCategoryTabLayout.getParent()).setVisibility(visibility);

        ViewGroup.LayoutParams layoutParams = mToolbarUnderlineView.getLayoutParams();

        if (layoutParams != null)
        {
            if (visibility == View.VISIBLE)
            {
                layoutParams.height = 1;
            } else
            {
                layoutParams.height = Util.dpToPx(mContext, 1);
            }

            mToolbarUnderlineView.setLayoutParams(layoutParams);
        }
    }

    public void setCurrentItem(int item)
    {
        if (mViewPager != null)
        {
            mViewPager.setCurrentItem(item);
        }
    }

    public void setCategoryTabLayout(FragmentManager fragmentManager, List<Category> categoryList//
        , Category selectedCategory, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        mCategoryTabLayout.setOnTabSelectedListener(null);

        if (categoryList == null)
        {
            mViewPager.removeAllViews();
            setCategoryTabLayoutVisibility(View.GONE);
            return;
        }

        int size = categoryList.size();

        if (size <= 2)
        {
            size = 1;
            setCategoryTabLayoutVisibility(View.GONE);

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mBottomOptionLayout, listener);

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

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mBottomOptionLayout, listener);

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
                    switch (state)
                    {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            isScrolling = true;
                            hideBottomLayout();
                            break;

                        case ViewPager.SCROLL_STATE_IDLE:
                            showBottomLayout();
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.searchTextView:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onSearchClick();
                break;

            case R.id.regionTextLayout:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onRegionClick();
                break;

            case R.id.dateTextLayout:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onDateClick();
                break;

            case R.id.viewTypeOptionImageView:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onViewTypeClick();
                break;

            case R.id.filterOptionImageView:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onFilterClick();
                break;
        }
    }

    private void setMenuBarLayoutEnabled(boolean enabled)
    {
        mViewTypeOptionImageView.setEnabled(enabled);
        mFilterOptionImageView.setEnabled(enabled);
    }

    public synchronized void showAppBarLayout()
    {
        if (mAppBarLayout == null)
        {
            return;
        }

        mAppBarLayout.setExpanded(true, true);
    }

    public synchronized void showBottomLayout()
    {
        if (mBottomOptionLayout == null)
        {
            return;
        }

        mBottomOptionLayout.setVisibility(View.VISIBLE);
        mBottomOptionLayout.setTranslationY(0);

        setMenuBarLayoutEnabled(true);
    }

    public void hideBottomLayout()
    {
        if (mBottomOptionLayout == null)
        {
            return;
        }

        mBottomOptionLayout.setVisibility(View.GONE);

        setMenuBarLayoutEnabled(false);
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
