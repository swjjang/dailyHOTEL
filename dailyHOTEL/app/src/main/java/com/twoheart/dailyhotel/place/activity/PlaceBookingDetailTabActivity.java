package com.twoheart.dailyhotel.place.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

public abstract class PlaceBookingDetailTabActivity extends BaseActivity
{
    protected static final int TAB_COUNT = 3;

    private ViewPager mViewPager;
    protected Booking mBooking;

    private DailyToolbarLayout mDailyToolbarLayout;

    protected abstract void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail);

    protected abstract void requestPlaceBookingDetail(int reservationIndex);

    protected abstract void onOptionsItemSelected(View view);

    protected abstract void onTabSelected(int position);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mBooking = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
        }

        if (mBooking == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout();

        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL);
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_booking_tab);

        initToolbar(mBooking.placeName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tabLayout.getLayoutParams();
        layoutParams.topMargin = 1 - Util.dpToPx(this, 1);
        tabLayout.setLayoutParams(layoutParams);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_booking_tab_title), true);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_tab_info_title));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.frag_tab_map_title));
        FontManager.apply(tabLayout, FontManager.getInstance(this).getRegularTypeface());
        tabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(title, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        mDailyToolbarLayout.setToolbarMenu(R.drawable.navibar_ic_call, -1);
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
            int position = tab.getPosition();

            if (mViewPager != null)
            {
                mViewPager.setCurrentItem(position);
            }

            switch (position)
            {
                case 0:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL);
                    break;

                case 1:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL_INFORMATION);
                    break;

                case 2:
                    AnalyticsManager.getInstance(PlaceBookingDetailTabActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL_MAP);
                    break;
            }

            PlaceBookingDetailTabActivity.this.onTabSelected(position);
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
