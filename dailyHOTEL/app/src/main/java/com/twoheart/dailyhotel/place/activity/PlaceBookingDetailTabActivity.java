/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.place.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.FontManager;

public abstract class PlaceBookingDetailTabActivity extends BaseActivity
{
    protected static final int TAB_COUNT = 3;

    private ViewPager mViewPager;
    protected Booking mBooking;

    private DailyToolbarLayout mDailyToolbarLayout;

    protected abstract void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail);

    protected abstract void requestPlaceBookingDetail(int reservationIndex);

    protected abstract void onOptionsItemSelected(View view);

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

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL, null);
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_booking_tab);

        initToolbar(mBooking.placeName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_booking_tab_title), true);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_tab_info_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_tab_map_title));
        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
        tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initToolbar(String title)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title);
        mDailyToolbarLayout.setToolbarRegionMenu(R.drawable.navibar_ic_call, -1);
        mDailyToolbarLayout.setToolbarMenuClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onOptionsItemSelected(v);
            }
        });
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

            switch (tab.getPosition())
            {
                case 0:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL, null);
                    break;

                case 1:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL_INFORMATION, null);
                    break;

                case 2:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL_MAP, null);
                    break;
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
