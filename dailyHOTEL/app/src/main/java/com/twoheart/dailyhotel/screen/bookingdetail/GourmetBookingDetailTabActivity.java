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
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;

public class GourmetBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    private GourmetBookingDetail mGourmetBookingDetail;
    private BookingDetailFragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        viewPager.setTag("GourmetBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<BaseFragment>();

        BaseFragment baseFragment01 = GourmetBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mBooking.reservationIndex, mBooking.isUsed);
        fragmentList.add(baseFragment01);

        BaseFragment baseFragment02 = GourmetBookingDetailTabInfomationFragment.newInstance(placeBookingDetail);
        fragmentList.add(baseFragment02);

        BaseFragment baseFragment03 = GourmetBookingDetailTabMapFragment.newInstance(placeBookingDetail);
        fragmentList.add(baseFragment03);

        mFragmentPagerAdapter = new BookingDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(mFragmentPagerAdapter);
    }

    @Override
    protected void onDestroy()
    {
        for (Fragment fragment : mFragmentPagerAdapter.getFragmentList())
        {
            getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (mGourmetBookingDetail == null)
        {
            menu.clear();
        } else
        {
            if (Util.isTextEmpty(mGourmetBookingDetail.gourmetPhone) == false)
            {
                getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call, menu);
            } else
            {
                getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call2, menu);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_daily_call:
                if (Util.isTelephonyEnabled(GourmetBookingDetailTabActivity.this) == true)
                {
                    try
                    {
                        String phone = DailyPreference.getInstance(GourmetBookingDetailTabActivity.this).getCompanyPhoneNumber();

                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(GourmetBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(GourmetBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
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
                if (Util.isTelephonyEnabled(GourmetBookingDetailTabActivity.this) == true)
                {
                    String phone = mGourmetBookingDetail.gourmetPhone;

                    if (Util.isTextEmpty(mGourmetBookingDetail.gourmetPhone) == true)
                    {
                        phone = DailyPreference.getInstance(GourmetBookingDetailTabActivity.this).getCompanyPhoneNumber();
                    }

                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        String message = getString(R.string.toast_msg_no_gourmet_call, mGourmetBookingDetail.gourmetPhone);
                        DailyToast.showToast(GourmetBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
                    }
                } else
                {
                    String message = getString(R.string.toast_msg_no_gourmet_call, mGourmetBookingDetail.gourmetPhone);
                    DailyToast.showToast(GourmetBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
                }
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void requestPlaceBookingDetail(int reservationIndex)
    {
        lockUI();

        String params = String.format("?reservation_rec_idx=%d", reservationIndex);
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
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    if (mGourmetBookingDetail == null)
                    {
                        mGourmetBookingDetail = new GourmetBookingDetail();
                    }

                    mGourmetBookingDetail.setData(jsonObject);

                    invalidateOptionsMenu();

                    loadFragments(getViewPager(), mGourmetBookingDetail);
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
