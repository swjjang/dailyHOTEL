/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.bookingdetail;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.FontManager;

public abstract class PlaceBookingDetailTabActivity extends BaseActivity
{
    protected static final int TAB_COUNT = 3;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    protected Booking mBooking;

    protected abstract void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail);

    protected abstract void requestPlaceBookingDetail(int reservationIndex);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mBooking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
        }

        if (mBooking == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_booking_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, mBooking.placeName);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_booking_tab_title), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_tab_info_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_tab_map_title));
        FontManager.apply(mTabLayout, FontManager.getInstance(this).getRegularTypeface());
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    public ViewPager getViewPager()
    {
        return mViewPager;
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        lockUI();

        // 호텔 정보를 가져온다.
        requestPlaceBookingDetail(mBooking.reservationIndex);

        AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(Screen.BOOKING_DETAIL);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(tab.getPosition());
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
        }
    };
}
