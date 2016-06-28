package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.screen.main.MenuBarLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.FontManager;

import java.util.ArrayList;
import java.util.List;

public abstract class PlaceMainLayout extends BaseLayout implements View.OnClickListener
{
    private static final int ANIMATION_DEALY = 200;

    private TextView mSearchTextView;
    private TextView mRegionTextView;
    private TextView mDateTextView;

    private View mBottomOptionLayout;
    private View mViewTypeOptionImageView;
    private View mFilterOptionImageView;

    private TabLayout mCategoryTabLayout;
    private View mUnderLine;
    private ViewPager mViewPager;
    private PlaceListFragmentPagerAdapter mFragmentPagerAdapter;

    private MenuBarLayout mMenuBarLayout;

    private Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
    private Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private boolean mUpScrolling;
    private ValueAnimator mValueAnimator;

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

    protected abstract PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener);

    public PlaceMainLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initCategoryTabLayout(view);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }


    private void initToolbar(View view)
    {
        // 검색
        // 지역 이름
        // 날짜
        mSearchTextView = (TextView) view.findViewById(R.id.searchTextView);
        mRegionTextView = (TextView) view.findViewById(R.id.regionTextView);
        mDateTextView = (TextView) view.findViewById(R.id.dateTextView);

        mSearchTextView.setOnClickListener(this);
        mRegionTextView.setOnClickListener(this);
        mDateTextView.setOnClickListener(this);
    }

    private void initOptionLayout(View view)
    {
        mBottomOptionLayout = view.findViewById(R.id.bottomOptionLayout);
        mBottomOptionLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                Rect rect = new Rect();
                mBottomOptionLayout.getGlobalVisibleRect(rect);
                mBottomOptionLayout.setTag(Util.getLCDHeight(mContext) - rect.top);
            }
        });

        // 하단 지도 필터
        mViewTypeOptionImageView = view.findViewById(R.id.viewTypeOptionImageView);
        mFilterOptionImageView = view.findViewById(R.id.filterOptionImageView);

        mViewTypeOptionImageView.setOnClickListener(this);
        mFilterOptionImageView.setOnClickListener(this);
    }

    private void initCategoryTabLayout(View view)
    {
        mCategoryTabLayout = (TabLayout) view.findViewById(R.id.categoryTabLayout);
        mUnderLine = view.findViewById(R.id.underLine);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    public void setMenuBarLayout(MenuBarLayout menuBarLayout)
    {
        mMenuBarLayout = menuBarLayout;
    }

    public void setToolbarRegionText(String region)
    {
        mRegionTextView.setText(region);
    }

    public void setToolbarDateText(String date)
    {
        mDateTextView.setText(date);
    }

    public void setOptionViewTypeView(Constants.ViewType viewType)
    {
        switch (viewType)
        {
            case LIST:
                break;

            case MAP:
                break;

            case GONE:
                break;
        }
    }

    public void setOptionFilterView()
    {
        //        mFilterOptionImageView
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        mCategoryTabLayout.setVisibility(visibility);
        mUnderLine.setVisibility(visibility);
    }

    public void setSelectCategory(Category category)
    {
        int count = mCategoryTabLayout.getTabCount();

        //        for(int i = 0; i< count)
    }

    public void setCategoryTabLayout(FragmentManager fragmentManager, List<Category> categoryList//
        , Category selectedCategory, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        if (categoryList == null)
        {
            mCategoryTabLayout.setOnTabSelectedListener(null);
            mViewPager.removeAllViews();
            setCategoryTabLayoutVisibility(View.GONE);
            return;
        }

        int size = categoryList.size();

        if (size <= 2)
        {
            size = 1;
            setCategoryTabLayoutVisibility(View.GONE);

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, listener);

            mViewPager.removeAllViews();
            mViewPager.setOffscreenPageLimit(size);
            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.addOnPageChangeListener(null);
        } else
        {
            setCategoryTabLayoutVisibility(View.VISIBLE);

            TabLayout.Tab selectedTab = null;

            // 화면에 4.5개 나오개 한다.
            final float TAB_COUNT = 4.5f;
            final int TAB_WIDTH = (int) (Util.getLCDWidth(mContext) / TAB_COUNT);

            Category category;
            TabLayout.Tab tab;
            View tabView;
            ViewGroup.LayoutParams layoutParams;

            mCategoryTabLayout.removeAllTabs();

            for (int i = 0; i < size; i++)
            {
                category = categoryList.get(i);

                tab = mCategoryTabLayout.newTab();
                tab.setText(category.name);
                tab.setTag(category);
                mCategoryTabLayout.addTab(tab);

                tabView = ((ViewGroup) mCategoryTabLayout.getChildAt(0)).getChildAt(i);
                layoutParams = tabView.getLayoutParams();
                layoutParams.width = TAB_WIDTH;
                tabView.setLayoutParams(layoutParams);

                if (category.code.equalsIgnoreCase(selectedCategory.code) == true)
                {
                    selectedTab = tab;
                }
            }

            if (selectedTab != null)
            {
                selectedTab.select();
            }

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, listener);

            mViewPager.removeAllViews();
            mViewPager.setOffscreenPageLimit(size);
            mViewPager.setAdapter(mFragmentPagerAdapter);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mCategoryTabLayout));

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

            case R.id.regionTextView:
                ((PlaceMainLayout.OnEventListener) mOnEventListener).onRegionClick();
                break;

            case R.id.dateTextView:
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

    private void setBottomTouchEnabled(boolean enabled)
    {
        mBottomOptionLayout.setEnabled(enabled);
        mMenuBarLayout.setEnabled(enabled);
    }

    private void setMenuBarLayoutTranslationY(float dy)
    {
        mBottomOptionLayout.setTranslationY(dy);
        mMenuBarLayout.setTranslationY(dy);
    }

    public void calculationMenuBarLayoutTranslationY(int dy)
    {
        int height = (Integer) mBottomOptionLayout.getTag();

        float translationY = dy + mBottomOptionLayout.getTranslationY();

        if (translationY >= height)
        {
            translationY = height;
        } else if (translationY <= 0)
        {
            translationY = 0;
        }

        if (dy > 0)
        {
            mUpScrolling = true;
        } else if (dy < 0)
        {
            mUpScrolling = false;
        }

        setMenuBarLayoutTranslationY(translationY);
    }

    public void animationMenuBarLayout()
    {
        int height = (Integer) mBottomOptionLayout.getTag();
        float translationY = mBottomOptionLayout.getTranslationY();

        if (translationY == 0 || translationY == height)
        {
            return;
        }

        mBottomOptionLayout.setTag(mBottomOptionLayout.getId(), translationY);

        if (mUpScrolling == true)
        {
            if (translationY >= mMenuBarLayout.getHeight() / 2)
            {
                hideBottomLayout(true);
            } else
            {
                showBottomLayout(true);
            }
        } else
        {
            if (translationY <= mMenuBarLayout.getHeight() / 2)
            {
                showBottomLayout(true);
            } else
            {
                hideBottomLayout(true);
            }
        }
    }

    public synchronized void showBottomLayout(boolean isAnimation)
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.SHOW)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            if (mValueAnimator.isRunning() == true)
            {
                mValueAnimator.cancel();
                mValueAnimator.removeAllListeners();
            }

            mValueAnimator = null;
        }

        if (isAnimation == true)
        {
            mValueAnimator = ValueAnimator.ofInt(0, 100);
            mValueAnimator.setDuration(ANIMATION_DEALY);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int value = (Integer) animation.getAnimatedValue();
                    float prevTranslationY = (Float) mBottomOptionLayout.getTag(mBottomOptionLayout.getId());
                    float translationY = prevTranslationY * value / 100;

                    setMenuBarLayoutTranslationY(prevTranslationY - translationY);
                }
            });

            mValueAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    //                if (mBottomOptionLayout.getVisibility() != View.VISIBLE)
                    //                {
                    //                    mBottomOptionLayout.setVisibility(View.VISIBLE);
                    //                }

                    setBottomTouchEnabled(false);

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;
                    }

                    setBottomTouchEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;

                    setBottomTouchEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mValueAnimator.start();
        } else
        {
            setMenuBarLayoutTranslationY(0);
        }
    }

    public void hideBottomLayout(boolean isAnimation)
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mValueAnimator != null)
        {
            if (mValueAnimator.isRunning() == true)
            {
                mValueAnimator.cancel();
                mValueAnimator.removeAllListeners();
            }

            mValueAnimator = null;
        }

        if (isAnimation == true)
        {
            mValueAnimator = ValueAnimator.ofInt(0, 100);
            mValueAnimator.setDuration(ANIMATION_DEALY);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator animation)
                {
                    int value = (Integer) animation.getAnimatedValue();
                    float prevTranslationY = (Float) mBottomOptionLayout.getTag(mBottomOptionLayout.getId());
                    float height = (Integer) mBottomOptionLayout.getTag() - prevTranslationY;
                    float translationY = height * value / 100;

                    setMenuBarLayoutTranslationY(prevTranslationY + translationY);
                }
            });

            mValueAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.HIDE;

                    setBottomTouchEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;
                    }

                    //                    mBottomOptionLayout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation)
                {
                    mAnimationState = Constants.ANIMATION_STATE.CANCEL;
                }

                @Override
                public void onAnimationRepeat(Animator animation)
                {

                }
            });

            mValueAnimator.start();
        } else
        {
            setMenuBarLayoutTranslationY((Integer) mBottomOptionLayout.getTag());
        }
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
