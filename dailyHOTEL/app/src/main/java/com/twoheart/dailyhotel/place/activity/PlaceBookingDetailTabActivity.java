package com.twoheart.dailyhotel.place.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import org.json.JSONObject;

public abstract class PlaceBookingDetailTabActivity extends BaseActivity
{
    protected static final int TAB_COUNT = 1;

    private ViewPager mViewPager;
    private boolean mDontReload;
    protected Booking mBooking;

    private DailyToolbarLayout mDailyToolbarLayout;

    protected abstract void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail);

    protected abstract void requestPlaceBookingDetail(int reservationIndex);

    protected abstract void setCurrentDateTime(long currentDateTime, long dailyDateTime);

    protected abstract void showCallDialog();

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

        initToolbar(getString(R.string.actionbar_title_booking_list_frag));

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.clearOnPageChangeListeners();
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
                showCallDialog();
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

        if (mBooking == null)
        {
            Util.restartApp(this);
            return;
        }

        if (mDontReload == false)
        {
            lockUI();

            requestCommonDatetime();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mDontReload == false)
        {
            mDontReload = true;
        } else
        {
            unLockUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }
        }
    }

    protected void requestCommonDatetime()
    {
        DailyNetworkAPI.getInstance(this).requestCommonDatetime(mNetworkTag, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, JSONObject response)
            {
                try
                {
                    long currentDateTime = response.getLong("currentDateTime");
                    long dailyDateTime = response.getLong("dailyDateTime");

                    setCurrentDateTime(currentDateTime, dailyDateTime);

                    // 호텔 정보를 가져온다.
                    requestPlaceBookingDetail(mBooking.reservationIndex);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                PlaceBookingDetailTabActivity.this.onErrorResponse(volleyError);
            }
        });
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
