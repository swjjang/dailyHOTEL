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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.fragment.GourmetTabBookingFragment;
import com.twoheart.dailyhotel.fragment.GourmetTabMapFragment;
import com.twoheart.dailyhotel.fragment.PlaceTabInfoFragment;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FragmentViewPager;

import org.json.JSONObject;

import java.util.ArrayList;

public class GourmetBookingDetailActivity extends PlaceBookingDetailActivity
{
    @Override
    protected void loadFragments()
    {
        if (mFragmentViewPager == null)
        {
            ArrayList<String> titleList = new ArrayList<String>();
            titleList.add(getString(R.string.frag_booking_tab_title));
            titleList.add(getString(R.string.frag_tab_info_title));
            titleList.add(getString(R.string.frag_tab_map_title));

            mFragmentViewPager = (FragmentViewPager) findViewById(R.id.fragmentViewPager);

            ArrayList<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();

            BaseFragment baseFragment01 = GourmetTabBookingFragment.newInstance(mPlaceBookingDetail, booking, getString(R.string.drawer_menu_pin_title_resrvation));
            mFragmentList.add(baseFragment01);

            BaseFragment baseFragment02 = PlaceTabInfoFragment.newInstance(mPlaceBookingDetail, titleList.get(1));
            mFragmentList.add(baseFragment02);

            BaseFragment baseFragment03 = GourmetTabMapFragment.newInstance(mPlaceBookingDetail, titleList.get(2));
            mFragmentList.add(baseFragment03);

            mFragmentViewPager.setData(mFragmentList);
            mFragmentViewPager.setAdapter(getSupportFragmentManager());

            mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
            mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (mPlaceBookingDetail != null && Util.isTextEmpty(mPlaceBookingDetail.gourmetPhone) == false)
        {
            getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call, menu);
        } else
        {
            getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call2, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_daily_call:
                if (Util.isTelephonyEnabled(GourmetBookingDetailActivity.this) == true)
                {
                    String phone = DailyPreference.getInstance(GourmetBookingDetailActivity.this).getCompanyPhoneNumber();

                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                } else
                {
                    DailyToast.showToast(GourmetBookingDetailActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
                break;

            case R.id.action_kakaotalk:
                try
                {
                    startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94")));
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
                if (Util.isTelephonyEnabled(GourmetBookingDetailActivity.this) == true)
                {
                    String phone = mPlaceBookingDetail.gourmetPhone;

                    if (Util.isTextEmpty(mPlaceBookingDetail.gourmetPhone) == true)
                    {
                        phone = DailyPreference.getInstance(GourmetBookingDetailActivity.this).getCompanyPhoneNumber();
                    }

                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                } else
                {
                    String message = getString(R.string.toast_msg_no_gourmet_call, mPlaceBookingDetail.gourmetPhone);
                    DailyToast.showToast(GourmetBookingDetailActivity.this, message, Toast.LENGTH_LONG);
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void requestPlaceBookingDetail()
    {
        lockUI();

        String params = String.format("?reservation_rec_idx=%d", booking.reservationIndex);
        DailyNetworkAPI.getInstance().requestGourmetBookingDetailInformation(mNetworkTag, params, mReservationBookingDetailJsonResponseListener, this);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mReservationBookingDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    if (mPlaceBookingDetail == null)
                    {
                        mPlaceBookingDetail = new GourmetBookingDetail();
                    }

                    mPlaceBookingDetail.setData(jsonObject);

                    invalidateOptionsMenu();

                    loadFragments();
                } else
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                finish();
                            }
                        }, null, false);
                        return;
                    } else
                    {
                        onInternalError();
                    }
                }
            } catch (Exception e)
            {
                onInternalError();
            } finally
            {
                unLockUI();
            }
        }
    };
}
