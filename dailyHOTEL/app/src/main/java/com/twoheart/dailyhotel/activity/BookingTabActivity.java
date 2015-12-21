/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BookingTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.BookingTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;
import com.twoheart.dailyhotel.view.widget.TabIndicator;
import com.twoheart.dailyhotel.view.widget.TabIndicator.OnTabSelectedListener;

import org.json.JSONObject;

import java.util.ArrayList;

public class BookingTabActivity extends BaseActivity
{
    public BookingHotelDetail mHotelDetail;
    public Booking booking;
    private TabIndicator mTabIndicator;
    private FragmentViewPager mFragmentViewPager;
    private ArrayList<BaseFragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mHotelDetail = new BookingHotelDetail();
        booking = new Booking();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            booking = (Booking) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_BOOKING);
        }

        if (booking == null)
        {
            Util.restartApp(this);
            return;
        }

        setContentView(R.layout.activity_booking_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, booking.placeName);

        ArrayList<String> titleList = new ArrayList<String>();
        titleList.add(getString(R.string.frag_booking_tab_title));
        titleList.add(getString(R.string.frag_tab_info_title));
        titleList.add(getString(R.string.frag_tab_map_title));

        mTabIndicator = (TabIndicator) findViewById(R.id.tabindicator);
        mTabIndicator.setData(titleList, false);
        mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (mHotelDetail != null && Util.isTextEmpty(mHotelDetail.hotelPhone) == false)
        {
            getMenuInflater().inflate(R.menu.actionbar_hotel_booking_call, menu);
        } else
        {
            getMenuInflater().inflate(R.menu.actionbar_hotel_booking_call2, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_daily_call:
                if (Util.isTelephonyEnabled(BookingTabActivity.this) == true)
                {
                    try
                    {
                        String phone = DailyPreference.getInstance(BookingTabActivity.this).getCompanyPhoneNumber();

                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    }catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(BookingTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(BookingTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
                break;

            case R.id.action_kakaotalk:
                try
                {
                    startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94")));
                } catch (ActivityNotFoundException e)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
                    } catch (ActivityNotFoundException e1)
                    {
                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                        marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
                        startActivity(marketLaunch);
                    }
                }
                break;

            case R.id.action_direct_call:
                if (Util.isTelephonyEnabled(BookingTabActivity.this) == true)
                {
                    String phone = mHotelDetail.hotelPhone;

                    if (Util.isTextEmpty(mHotelDetail.hotelPhone) == true)
                    {
                        phone = DailyPreference.getInstance(BookingTabActivity.this).getCompanyPhoneNumber();
                    }

                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    }catch (ActivityNotFoundException e)
                    {
                        String message = getString(R.string.toast_msg_no_hotel_call, mHotelDetail.hotelPhone);
                        DailyToast.showToast(BookingTabActivity.this, message, Toast.LENGTH_LONG);
                    }
                } else
                {
                    String message = getString(R.string.toast_msg_no_hotel_call, mHotelDetail.hotelPhone);
                    DailyToast.showToast(BookingTabActivity.this, message, Toast.LENGTH_LONG);
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void loadFragments()
    {
        if (mFragmentViewPager == null)
        {
            ArrayList<String> titleList = new ArrayList<String>();
            titleList.add(getString(R.string.frag_booking_tab_title));
            titleList.add(getString(R.string.frag_tab_info_title));
            titleList.add(getString(R.string.frag_tab_map_title));

            mFragmentViewPager = (FragmentViewPager) findViewById(R.id.fragmentViewPager);

            mFragmentList = new ArrayList<BaseFragment>();

            BaseFragment baseFragment01 = BookingTabBookingFragment.newInstance(mHotelDetail, booking, getString(R.string.drawer_menu_pin_title_resrvation));
            mFragmentList.add(baseFragment01);

            BaseFragment baseFragment02 = TabInfoFragment.newInstance(mHotelDetail, titleList.get(1));
            mFragmentList.add(baseFragment02);

            BaseFragment baseFragment03 = TabMapFragment.newInstance(mHotelDetail, titleList.get(2));
            mFragmentList.add(baseFragment03);

            mFragmentViewPager.setData(mFragmentList);
            mFragmentViewPager.setAdapter(getSupportFragmentManager());

            mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
            mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(BookingTabActivity.this).recordScreen(Screen.BOOKING_DETAIL);
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        lockUI();

        // 호텔 정보를 가져온다.
        String params = String.format("?reservationIdx=%d", booking.reservationIndex);
        DailyNetworkAPI.getInstance().requestHotelBookingDetailInformation(mNetworkTag, params, mReservationBookingDetailJsonResponseListener, this);

        super.onResume();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(int position)
        {
            if (mFragmentViewPager == null)
            {
                return;
            }

            if (mFragmentViewPager.getCurrentItem() != position)
            {
                mFragmentViewPager.setCurrentItem(position);
            }
        }
    };
    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            mTabIndicator.setCurrentItem(position);

            AnalyticsManager.getInstance(BookingTabActivity.this).recordEvent(Screen.BOOKING_DETAIL, Action.CLICK, mTabIndicator.getMainText(position), (long) position);
        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }
    };

    private DailyHotelJsonResponseListener mReservationBookingDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    // 에러가 나오는 경우 처리는 추후 통합해서 관리해야 한다.
                    switch (msg_code)
                    {
                        case 100:
                        {
                            String msg = response.getString("msg");
                            DailyToast.showToast(BookingTabActivity.this, msg, Toast.LENGTH_SHORT);
                            break;
                        }

                        case 200:
                        {
                            if (isFinishing() == true)
                            {
                                return;
                            }

                            String msg = response.getString("msg");
                            showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
                            break;
                        }
                    }

                    finish();
                    return;
                }

                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = mHotelDetail.setData(jsonObject);

                if (result == true)
                {
                    invalidateOptionsMenu();
                    loadFragments();
                } else
                {
                    throw new NullPointerException("result == false");
                }
            } catch (Exception e)
            {
                onError(e);
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };
}
