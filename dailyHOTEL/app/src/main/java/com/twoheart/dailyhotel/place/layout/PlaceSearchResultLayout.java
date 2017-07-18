package com.twoheart.dailyhotel.place.layout;

import android.animation.Animator;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class PlaceSearchResultLayout extends BaseBlurLayout implements View.OnClickListener
{
    private static final int ANIMATION_DELAY = 200;

    private View mToolbar;
    protected TextView mCalendarTextView;
    private View mEmptyLayout, mSearchLocationLayout;
    private View mResultLayout;

    protected View mBottomOptionLayout;
    private View mViewTypeOptionImageView;
    private View mFilterOptionImageView;

    protected TabLayout mCategoryTabLayout;
    private View mCalendarUnderlineView;
    protected ViewPager mViewPager;
    protected PlaceListFragmentPagerAdapter mFragmentPagerAdapter;

    protected Spinner mDistanceFilterSpinner;
    DistanceFilterAdapter mDistanceFilterAdapter;

    Constants.ANIMATION_STATUS mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
    Constants.ANIMATION_STATE mAnimationState = Constants.ANIMATION_STATE.END;
    private boolean mUpScrolling;
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
    }

    protected abstract int getEmptyIconResourceId();

    protected abstract PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, View bottomOptionLayout, PlaceListFragment.OnPlaceListFragmentListener listener);

    protected abstract void onAnalyticsCategoryFlicking(String category);

    protected abstract void onAnalyticsCategoryClick(String category);

    public PlaceSearchResultLayout(Context context, String callByScreen, OnBaseEventListener listener)
    {
        super(context, listener);

        mCallByScreen = callByScreen;
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbarLayout(view);

        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        mSearchLocationLayout = view.findViewById(R.id.searchLocationLayout);
        mResultLayout = view.findViewById(R.id.resultLayout);

        initEmptyLayout(mEmptyLayout);
        initSearchLocationLayout(mSearchLocationLayout);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        mToolbar = view.findViewById(R.id.toolbar);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED);
            }
        });

        View searchCancelView = mToolbar.findViewById(R.id.searchCancelView);
        searchCancelView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).finish(Constants.CODE_RESULT_ACTIVITY_HOME);
            }
        });

        View calendarLayout = view.findViewById(R.id.calendarLayout);
        calendarLayout.setOnClickListener(this);

        mCalendarTextView = (TextView) view.findViewById(R.id.calendarTextView);

        mDistanceFilterSpinner = (Spinner) view.findViewById(R.id.distanceSpinner);

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
        mCategoryTabLayout = (TabLayout) view.findViewById(R.id.categoryTabLayout);
        mCalendarUnderlineView = view.findViewById(R.id.calendarUnderLine);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
    }

    public void setCalendarText(String date)
    {
        if (DailyTextUtils.isTextEmpty(date) == true)
        {
            return;
        }

        mCalendarTextView.setText(date);
    }

    public void setToolbarTitle(String title)
    {
        TextView titleView = (TextView) mToolbar.findViewById(R.id.titleView);

        if (titleView == null)
        {
            Util.restartApp(mContext);
            return;
        }

        titleView.setText(title);
    }

    private void initEmptyLayout(View view)
    {
        ImageView emptyIconImageView = (ImageView) view.findViewById(R.id.emptyIconImageView);
        View changeDateView = view.findViewById(R.id.changeDateView);
        TextView researchView = (TextView) view.findViewById(R.id.researchView);
        TextView callTextView = (TextView) view.findViewById(R.id.callTextView);

        emptyIconImageView.setImageResource(getEmptyIconResourceId());

        changeDateView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onDateClick();
            }
        });

        int researchResId;
        if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true || AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(mCallByScreen) == true)
        {
            researchResId = R.string.label_searchresult_research;
        } else
        {
            researchResId = R.string.label_searchresult_change_region;
        }

        researchView.setText(researchResId);
        researchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int resultCode;
                if (AnalyticsManager.Screen.HOME.equalsIgnoreCase(mCallByScreen) == true || AnalyticsManager.Screen.SEARCH_MAIN.equalsIgnoreCase(mCallByScreen) == true)
                {
                    resultCode = Constants.CODE_RESULT_ACTIVITY_GO_SEARCH;
                } else
                {
                    resultCode = Constants.CODE_RESULT_ACTIVITY_GO_REGION_LIST;
                }

                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).research(resultCode);
            }
        });

        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        callTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onShowCallDialog();
            }
        });
    }

    private void initSearchLocationLayout(View view)
    {
        if (view == null)
        {
            return;
        }

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(mContext.getResources().getColor(R.color.location_progressbar_cc8c8c8), PorterDuff.Mode.SRC_IN);
    }

    private void initOptionLayout(View view)
    {
        mBottomOptionLayout = view.findViewById(R.id.bottomOptionLayout);
        mBottomOptionLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                mBottomOptionLayout.setTag(mViewPager.getBottom() - mBottomOptionLayout.getTop());
            }
        });

        // 하단 지도 필터
        mViewTypeOptionImageView = view.findViewById(R.id.viewTypeOptionImageView);
        mFilterOptionImageView = view.findViewById(R.id.filterOptionImageView);

        setViewTypeVisibility(false);

        mViewTypeOptionImageView.setOnClickListener(this);
        mFilterOptionImageView.setOnClickListener(this);

        // 기본 설정
        setOptionViewTypeView(Constants.ViewType.LIST);

        setOptionViewTypeEnabled(true);
        setOptionFilterEnabled(true);
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

    public void setOptionViewTypeEnabled(boolean isTypeEnabled)
    {
        // disable opacity 40% - 0 ~ 255
        if (isTypeEnabled == true)
        {
            mViewTypeOptionImageView.setAlpha(1.0f);
        } else
        {
            mViewTypeOptionImageView.setAlpha(0.4f);
        }

        mViewTypeOptionImageView.setEnabled(isTypeEnabled);
    }

    public void setOptionFilterEnabled(boolean isFilterEnabled)
    {
        // disable opacity 40% - 0 ~ 255
        if (isFilterEnabled == true)
        {
            mFilterOptionImageView.setAlpha(1.0f);
        } else
        {
            mFilterOptionImageView.setAlpha(0.4f);
        }

        mFilterOptionImageView.setEnabled(isFilterEnabled);
    }

    public void setOptionFilterSelected(boolean enabled)
    {
        mFilterOptionImageView.setSelected(enabled);
    }

    public void setCategoryTabLayoutVisibility(int visibility)
    {
        ((View) mCategoryTabLayout.getParent()).setVisibility(visibility);

        ViewGroup.LayoutParams layoutParams = mCalendarUnderlineView.getLayoutParams();

        if (layoutParams != null)
        {
            if (visibility == View.VISIBLE)
            {
                mCalendarUnderlineView.getLayoutParams().height = 1;
            } else
            {
                mCalendarUnderlineView.getLayoutParams().height = ScreenUtils.dpToPx(mContext, 1);
            }
        }
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

        setMenuBarLayoutTranslationY(0);

        mCategoryTabLayout.removeAllTabs();

        TabLayout.Tab tab;
        tab = mCategoryTabLayout.newTab();
        tab.setText(Category.ALL.name);
        tab.setTag(Category.ALL);
        mCategoryTabLayout.addTab(tab);

        mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, 1, mBottomOptionLayout, listener);
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

                prevPosition = position;

                PlaceListFragment placeListFragment = getPlaceListFragment().get(position);

                boolean isViewTypeEnabled = placeListFragment.getViewType() != Constants.ViewType.GONE;

                setOptionViewTypeEnabled(isViewTypeEnabled);
                setOptionFilterEnabled(isViewTypeEnabled || placeListFragment.isDefaultFilter() == false);
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

            mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, size, mBottomOptionLayout, listener);

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

    public void setSpinnerVisible(boolean isVisible)
    {
        mDistanceFilterSpinner.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mDistanceFilterSpinner.setEnabled(isVisible);
    }

    private double getSpinnerRadiusValue(int spinnerPosition)
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
                mEmptyLayout.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case EMPTY:
                mEmptyLayout.setVisibility(View.VISIBLE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;

            case SEARCH_LOCATION:
                mEmptyLayout.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.INVISIBLE);
                mSearchLocationLayout.setVisibility(View.VISIBLE);
                break;

            case LIST:
                mEmptyLayout.setVisibility(View.GONE);
                mResultLayout.setVisibility(View.VISIBLE);
                mSearchLocationLayout.setVisibility(View.GONE);
                break;
        }
    }

    public boolean isEmptyLayout()
    {
        return mResultLayout.getVisibility() != View.VISIBLE;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.calendarLayout:
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onDateClick();
                break;

            case R.id.viewTypeOptionImageView:
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onViewTypeClick();
                break;

            case R.id.filterOptionImageView:
                ((PlaceSearchResultLayout.OnEventListener) mOnEventListener).onFilterClick();
                break;
        }
    }

    void setMenuBarLayoutEnabled(boolean enabled)
    {
        mViewTypeOptionImageView.setEnabled(enabled);
        mFilterOptionImageView.setEnabled(enabled);
    }

    void setMenuBarLayoutTranslationY(float dy)
    {
        mBottomOptionLayout.setTranslationY(dy);
    }

    public void setMenuBarLayoutVisible(boolean visible)
    {
        mBottomOptionLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void calculationMenuBarLayoutTranslationY(int dy)
    {
        Object tag = mBottomOptionLayout.getTag();

        if (tag == null || tag instanceof Integer == false)
        {
            return;
        }

        int height = (Integer) tag;
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

        // 움직이는 동안에는 터치가 불가능 하다.
        if (translationY == 0 || translationY == height)
        {
            setMenuBarLayoutEnabled(true);
        } else
        {
            setMenuBarLayoutEnabled(false);
        }

        setMenuBarLayoutTranslationY(translationY);
    }

    public void animationMenuBarLayout()
    {
        Object tag = mBottomOptionLayout.getTag();

        if (tag == null || tag instanceof Integer == false)
        {
            return;
        }

        int height = (Integer) tag;
        float translationY = mBottomOptionLayout.getTranslationY();

        if (translationY == 0 || translationY == height)
        {
            return;
        }

        mBottomOptionLayout.setTag(mBottomOptionLayout.getId(), translationY);

        if (mUpScrolling == true)
        {
            if (translationY >= mBottomOptionLayout.getHeight() / 2)
            {
                hideBottomLayout(true);
            } else
            {
                showBottomLayout(true);
            }
        } else
        {
            if (translationY <= mBottomOptionLayout.getHeight() / 2)
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

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        if (isAnimation == true)
        {
            mValueAnimator = ValueAnimator.ofInt(0, 100);
            mValueAnimator.setDuration(ANIMATION_DELAY);
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
                    setMenuBarLayoutEnabled(false);

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;

                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;
                    }

                    setMenuBarLayoutEnabled(true);
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
            setMenuBarLayoutTranslationY(0);

            setMenuBarLayoutEnabled(true);
        }
    }

    public void hideBottomLayout(boolean isAnimation)
    {
        if (mAnimationState == Constants.ANIMATION_STATE.START && mAnimationStatus == Constants.ANIMATION_STATUS.HIDE)
        {
            return;
        }

        if (mValueAnimator != null && mValueAnimator.isRunning() == true)
        {
            mValueAnimator.cancel();
        }

        if (isAnimation == true)
        {
            mValueAnimator = ValueAnimator.ofInt(0, 100);
            mValueAnimator.setDuration(ANIMATION_DELAY);
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

                    setMenuBarLayoutEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mValueAnimator.removeAllListeners();
                    mValueAnimator.removeAllUpdateListeners();
                    mValueAnimator = null;

                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.HIDE_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;
                    }
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

            setMenuBarLayoutEnabled(false);
        }
    }

    public void setViewTypeVisibility(boolean isShow)
    {
        mViewTypeOptionImageView.setVisibility(isShow ? View.VISIBLE : View.GONE);
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
