package com.twoheart.dailyhotel.screen.home.category.nearby;

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
import com.daily.dailyhotel.view.DailyFloatingActionView;
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
    TextView mCalendarTextView;
    private View mEmptyLayout, mSearchLocationLayout;
    private View mResultLayout;

    private DailyFloatingActionView mFloatingActionView;

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

        void research();

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
            String date = String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookingDay.getCheckInDay(dateFormat)//
                , stayBookingDay.getCheckOutDay(dateFormat), nights);

            if (DailyTextUtils.isTextEmpty(date) == true)
            {
                return;
            }

            mCalendarTextView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    int width = mCalendarTextView.getWidth() - mCalendarTextView.getPaddingLeft() - mCalendarTextView.getPaddingRight();
                    int textSize = date.length();

                    Paint paint = mCalendarTextView.getPaint();
                    int endPosition = paint.breakText(date, true, width, null);

                    if (textSize > endPosition)
                    {
                        String newDateFormat = "yyyy.MM.dd";
                        String newDate = String.format(Locale.KOREA, "%s - %s, %d박"//
                            , stayBookingDay.getCheckInDay(newDateFormat)//
                            , stayBookingDay.getCheckOutDay(newDateFormat), nights);

                        mCalendarTextView.setText(newDate);
                    } else
                    {
                        mCalendarTextView.setText(date);
                    }
                }
            });
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
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

        changeDateView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onDateClick());

        researchView.setText(R.string.label_searchresult_change_region);
        researchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).research();
            }
        });

        callTextView.setPaintFlags(callTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        callTextView.setOnClickListener(v -> ((OnEventListener) mOnEventListener).onShowCallDialog());
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
        mFloatingActionView = (DailyFloatingActionView) view.findViewById(R.id.floatingActionView);
        mFloatingActionView.setOnViewOptionClickListener(v -> ((OnEventListener) mOnEventListener).onViewTypeClick());
        mFloatingActionView.setOnFilterOptionClickListener(v -> ((OnEventListener) mOnEventListener).onFilterClick());
        mFloatingActionView.post(() -> mFloatingActionView.setTag(mViewPager.getBottom() - mFloatingActionView.getTop()));

        setViewTypeVisibility(false);

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
                mFloatingActionView.setViewOptionMapSelected();
                break;

            case MAP:
                mFloatingActionView.setViewOptionListSelected();
                break;

            case GONE:
                break;
        }
    }

    public void setOptionViewTypeEnabled(boolean enabled)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setViewOptionEnable(enabled);
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

    public void setSpinnerVisible(boolean isVisible)
    {
        mDistanceFilterSpinner.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mDistanceFilterSpinner.setEnabled(isVisible);
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

    public void setScreenVisible(StayCategoryNearByActivity.ScreenType screenType)
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
                ((StayCategoryNearByLayout.OnEventListener) mOnEventListener).onDateClick();
                break;
        }
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

    public void calculationMenuBarLayoutTranslationY(int dy)
    {
        Object tag = mFloatingActionView.getTag();

        if (tag == null || tag instanceof Integer == false)
        {
            return;
        }

        int height = (Integer) tag;
        float translationY = dy + mFloatingActionView.getTranslationY();

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
            setOptionViewTypeEnabled(true);
            setOptionFilterEnabled(true);
        } else
        {
            setOptionViewTypeEnabled(false);
            setOptionFilterEnabled(false);
        }

        setMenuBarLayoutTranslationY(translationY);
    }

    public void animationMenuBarLayout()
    {
        Object tag = mFloatingActionView.getTag();

        if (tag == null || tag instanceof Integer == false)
        {
            return;
        }

        int height = (Integer) tag;
        float translationY = mFloatingActionView.getTranslationY();

        if (translationY == 0 || translationY == height)
        {
            return;
        }

        mFloatingActionView.setTag(mFloatingActionView.getId(), translationY);

        if (mUpScrolling == true)
        {
            if (translationY >= mFloatingActionView.getHeight() / 2)
            {
                hideBottomLayout(true);
            } else
            {
                showBottomLayout(true);
            }
        } else
        {
            if (translationY <= mFloatingActionView.getHeight() / 2)
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
                    float prevTranslationY = (Float) mFloatingActionView.getTag(mFloatingActionView.getId());
                    float translationY = prevTranslationY * value / 100;

                    setMenuBarLayoutTranslationY(prevTranslationY - translationY);
                }
            });

            mValueAnimator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    setOptionViewTypeEnabled(false);
                    setOptionFilterEnabled(false);

                    mAnimationState = Constants.ANIMATION_STATE.START;
                    mAnimationStatus = Constants.ANIMATION_STATUS.SHOW;
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mValueAnimator != null)
                    {
                        mValueAnimator.removeAllListeners();
                        mValueAnimator.removeAllUpdateListeners();
                        mValueAnimator = null;
                    }

                    if (mAnimationState != Constants.ANIMATION_STATE.CANCEL)
                    {
                        mAnimationStatus = Constants.ANIMATION_STATUS.SHOW_END;
                        mAnimationState = Constants.ANIMATION_STATE.END;
                    }

                    setOptionViewTypeEnabled(true);
                    setOptionFilterEnabled(true);
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

            setOptionViewTypeEnabled(true);
            setOptionFilterEnabled(true);
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
                    float prevTranslationY = (Float) mFloatingActionView.getTag(mFloatingActionView.getId());
                    float height = (Integer) mFloatingActionView.getTag() - prevTranslationY;
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

                    setOptionViewTypeEnabled(false);
                    setOptionFilterEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation)
                {
                    if (mValueAnimator != null)
                    {
                        mValueAnimator.removeAllListeners();
                        mValueAnimator.removeAllUpdateListeners();
                        mValueAnimator = null;
                    }

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
            setMenuBarLayoutTranslationY((Integer) mFloatingActionView.getTag());

            setOptionViewTypeEnabled(false);
            setOptionFilterEnabled(false);
        }
    }

    public void setViewTypeVisibility(boolean visible)
    {
        if (mFloatingActionView == null)
        {
            return;
        }

        mFloatingActionView.setViewOptionVisible(visible);
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
