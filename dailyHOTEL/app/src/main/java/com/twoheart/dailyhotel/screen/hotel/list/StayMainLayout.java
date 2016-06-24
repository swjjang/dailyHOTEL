package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.twoheart.dailyhotel.model.EventBanner;
import com.twoheart.dailyhotel.model.HotelCurationOption;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.adapter.PlaceListFragmentPagerAdapter;
import com.twoheart.dailyhotel.place.fragment.PlaceListFragment;
import com.twoheart.dailyhotel.place.layout.PlaceMainLayout;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.widget.DailyFloatingActionButtonBehavior;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayMainLayout extends PlaceMainLayout
{
    private static final int TAB_COUNT = 3;
    private int TOOLBAR_HEIGHT;

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private TabLayout mCategoryTabLayout;
    private View mUnderLine;
    private ViewPager mViewPager;
    private HotelFragmentPagerAdapter mFragmentPagerAdapter;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mFloatingActionView;
    private DailyFloatingActionButtonBehavior mDailyFloatingActionButtonBehavior;

    private SaleTime mTodaySaleTime;
    private boolean mDontReloadAtOnResume, mIsDeepLink;
    private HotelCurationOption mCurationOption;
    private List<EventBanner> mEventBannerList;

    private Constants.ViewType mViewType = Constants.ViewType.LIST;

    public StayMainLayout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    public void onClick(View v)
    {

    }

    @Override
    protected PlaceListFragmentPagerAdapter getPlaceListFragmentPagerAdapter(FragmentManager fragmentManager, int count, PlaceListFragment.OnPlaceListFragmentListener listener)
    {
        return null;
    }

}
