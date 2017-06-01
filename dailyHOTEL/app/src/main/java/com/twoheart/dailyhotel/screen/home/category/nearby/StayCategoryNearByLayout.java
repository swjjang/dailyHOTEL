package com.twoheart.dailyhotel.screen.home.category.nearby;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Category;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.base.BaseBlurLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultListFragment;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by android_sam on 2017. 5. 19..
 */

public class StayCategoryNearByLayout extends BaseBlurLayout implements View.OnClickListener
{
    private static final int ANIMATION_DELAY = 200;

    private View mToolbar;
    private TextView mCalendarTextView;
    private View mEmptyLayout;
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

    public StayCategoryNearByLayout(Context context, OnBaseEventListener listener)
    {
        super(context, listener);
    }

    protected int getEmptyIconResourceId()
    {
        return R.drawable.no_hotel_ic;
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

        mEmptyLayout = view.findViewById(R.id.emptyLayout);
        mResultLayout = view.findViewById(R.id.resultLayout);

        initEmptyLayout(mEmptyLayout);
        initCategoryTabLayout(view);
        initOptionLayout(view);
    }

    private void initToolbarLayout(View view)
    {
        mToolbar = view.findViewById(R.id.toolbar);

        View backView = mToolbar.findViewById(R.id.backImageView);
        backView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).finish(Activity.RESULT_CANCELED));

        View searchCancelView = mToolbar.findViewById(R.id.searchCancelView);
        searchCancelView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).finish(Constants.CODE_RESULT_ACTIVITY_HOME));

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

                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onItemSelectedSpinner(getSpinnerRadiusValue(position));
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

    protected void setCalendarText(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        try
        {
            int nights = stayBookingDay.getNights();
            String dateFormat = ScreenUtils.getScreenWidth(mContext) < 720 ? "yyyy.MM.dd" : "yyyy.MM.dd(EEE)";

            setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookingDay.getCheckInDay(dateFormat)//
                , stayBookingDay.getCheckOutDay(dateFormat), nights));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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
        View researchView = view.findViewById(R.id.researchView);
        TextView callTextView = (TextView) view.findViewById(R.id.callTextView);

        emptyIconImageView.setImageResource(getEmptyIconResourceId());

        changeDateView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onDateClick());

        researchView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).research(Activity.RESULT_CANCELED));

        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        callTextView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onShowCallDialog());
    }

    private void initOptionLayout(View view)
    {
        mBottomOptionLayout = view.findViewById(R.id.bottomOptionLayout);
        mBottomOptionLayout.post(() -> mBottomOptionLayout.setTag(mViewPager.getBottom() - mBottomOptionLayout.getTop()));

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

    public void setCategoryTabLayout(FragmentManager fragmentManager, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        mCategoryTabLayout.removeAllTabs();
        setCategoryTabLayoutVisibility(View.GONE);

        mFragmentPagerAdapter = getPlaceListFragmentPagerAdapter(fragmentManager, mBottomOptionLayout, listener);

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

    public void clearCategoryTab()
    {
        mViewPager.setAdapter(null);
        mCategoryTabLayout.setOnTabSelectedListener(null);
        mCategoryTabLayout.removeAllTabs();
        mFragmentPagerAdapter.removeAll();
        mViewPager.removeAllViews();
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

        mDistanceFilterAdapter.setSelection(position);
        mDistanceFilterSpinner.setSelection(position);
    }

    public void showEmptyLayout()
    {
        mEmptyLayout.setVisibility(View.VISIBLE);
        mResultLayout.setVisibility(View.GONE);
    }

    public void showListLayout()
    {
        mEmptyLayout.setVisibility(View.GONE);
        mResultLayout.setVisibility(View.VISIBLE);
    }

    public void processListLayout()
    {
        mEmptyLayout.setVisibility(View.GONE);
        mResultLayout.setVisibility(View.INVISIBLE);
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
                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onDateClick();
                break;

            case R.id.viewTypeOptionImageView:
                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onViewTypeClick();
                break;

            case R.id.filterOptionImageView:
                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onFilterClick();
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
