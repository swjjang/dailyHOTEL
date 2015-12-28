/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.bookingdetail;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.model.BookingHotelDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;

public class HotelBookingDetailTabActivity extends BaseActivity
{
    private static final int TAB_COUNT = 3;

    public BookingHotelDetail mBookingHotelDetail;
    public Booking booking;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private BookingDetailFragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mBookingHotelDetail = new BookingHotelDetail();
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

        initLayout();
    }

    private void initLayout()
    {
        setContentView(R.layout.activity_booking_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar(toolbar, booking.placeName);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_booking_tab_title), true);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_tab_info_title));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.frag_tab_map_title));
        mTabLayout.setOnTabSelectedListener(mOnTabSelectedListener);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (mBookingHotelDetail != null && Util.isTextEmpty(mBookingHotelDetail.hotelPhone) == false)
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
                if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
                {
                    try
                    {
                        String phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getCompanyPhoneNumber();

                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(HotelBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(HotelBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
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
                if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
                {
                    String phone = mBookingHotelDetail.hotelPhone;

                    if (Util.isTextEmpty(mBookingHotelDetail.hotelPhone) == true)
                    {
                        phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getCompanyPhoneNumber();
                    }

                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        String message = getString(R.string.toast_msg_no_hotel_call, mBookingHotelDetail.hotelPhone);
                        DailyToast.showToast(HotelBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
                    }
                } else
                {
                    String message = getString(R.string.toast_msg_no_hotel_call, mBookingHotelDetail.hotelPhone);
                    DailyToast.showToast(HotelBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onAttachFragment(Fragment fragment)
    {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(HotelBookingDetailTabActivity.this).recordScreen(Screen.BOOKING_DETAIL);
        super.onStart();

        lockUI();

        // 호텔 정보를 가져온다.
        String params = String.format("?reservationIdx=%d", booking.reservationIndex);
        DailyNetworkAPI.getInstance().requestHotelBookingDetailInformation(mNetworkTag, params, mReservationBookingDetailJsonResponseListener, this);
    }

    private void loadFragments(ViewPager viewPager, BookingHotelDetail bookingHotelDetail)
    {
        String tag = (String)viewPager.getTag();

        if(tag != null)
        {
            return;
        }

        viewPager.setTag("HotelBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<BaseFragment>();

        BaseFragment baseFragment01 = HotelBookingDetailTabBookingFragment.newInstance(mBookingHotelDetail, booking);
        fragmentList.add(baseFragment01);

        BaseFragment baseFragment02 = HotelBookingDetailTabInfomationFragment.newInstance(mBookingHotelDetail);
        fragmentList.add(baseFragment02);

        BaseFragment baseFragment03 = HotelBookingDetailTabMapFragment.newInstance(mBookingHotelDetail);
        fragmentList.add(baseFragment03);

        mFragmentPagerAdapter = new BookingDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mFragmentPagerAdapter);
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
                            DailyToast.showToast(HotelBookingDetailTabActivity.this, msg, Toast.LENGTH_SHORT);
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

                boolean result = mBookingHotelDetail.setData(jsonObject);

                if (result == true)
                {
                    invalidateOptionsMenu();

                    loadFragments(mViewPager, mBookingHotelDetail);
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
