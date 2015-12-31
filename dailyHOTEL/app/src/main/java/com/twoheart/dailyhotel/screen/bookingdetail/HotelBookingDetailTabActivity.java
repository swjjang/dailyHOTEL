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
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.BaseFragment;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONObject;

import java.util.ArrayList;

public class HotelBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    public HotelBookingDetail mHotelBookingDetail;
    private BookingDetailFragmentPagerAdapter mFragmentPagerAdapter;

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        viewPager.setTag("HotelBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<BaseFragment>();

        BaseFragment baseFragment01 = HotelBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mBooking.reservationIndex, mBooking.isUsed);
        fragmentList.add(baseFragment01);

        BaseFragment baseFragment02 = HotelBookingDetailTabInfomationFragment.newInstance(placeBookingDetail);
        fragmentList.add(baseFragment02);

        BaseFragment baseFragment03 = HotelBookingDetailTabMapFragment.newInstance(placeBookingDetail);
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
    protected void onOptionsItemSelected(View view)
    {
        PopupMenu popupMenu = new PopupMenu(this, view);

        if (Util.isTextEmpty(mHotelBookingDetail.hotelPhone) == false)
        {
            popupMenu.getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call, popupMenu.getMenu());
        } else
        {
            popupMenu.getMenuInflater().inflate(R.menu.actionbar_gourmet_booking_call2, popupMenu.getMenu());
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                onOptionsItemSelected(item);
                return false;
            }
        });

        popupMenu.show();

        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                FontManager.apply(((ViewGroup) getWindow().getDecorView())//
                    , FontManager.getInstance(HotelBookingDetailTabActivity.this).getRegularTypeface());
            }
        });
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
                    String phone = mHotelBookingDetail.hotelPhone;

                    if (Util.isTextEmpty(mHotelBookingDetail.hotelPhone) == true)
                    {
                        phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getCompanyPhoneNumber();
                    }

                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(phone).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        String message = getString(R.string.toast_msg_no_hotel_call, mHotelBookingDetail.hotelPhone);
                        DailyToast.showToast(HotelBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
                    }
                } else
                {
                    String message = getString(R.string.toast_msg_no_hotel_call, mHotelBookingDetail.hotelPhone);
                    DailyToast.showToast(HotelBookingDetailTabActivity.this, message, Toast.LENGTH_LONG);
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

        // 호텔 정보를 가져온다.
        String params = String.format("?reservationIdx=%d", reservationIndex);
        DailyNetworkAPI.getInstance().requestHotelBookingDetailInformation(mNetworkTag, params, mReservationBookingDetailJsonResponseListener, this);
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

                    if (mHotelBookingDetail == null)
                    {
                        mHotelBookingDetail = new HotelBookingDetail();
                    }

                    mHotelBookingDetail.setData(jsonObject);

                    invalidateOptionsMenu();

                    loadFragments(getViewPager(), mHotelBookingDetail);
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
