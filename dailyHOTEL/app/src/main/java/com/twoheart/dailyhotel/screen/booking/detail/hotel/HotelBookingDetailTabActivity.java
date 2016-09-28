/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlaceBookingDetailTabActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.BookingDetailFragmentPagerAdapter;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;

public class HotelBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    public HotelBookingDetail mHotelBookingDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mHotelBookingDetail = new HotelBookingDetail();
    }

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        viewPager.setTag("HotelBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<>();

        BaseFragment baseFragment01 = HotelBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mBooking.reservationIndex);
        fragmentList.add(baseFragment01);

        BaseFragment baseFragment02 = HotelBookingDetailTabInfomationFragment.newInstance(placeBookingDetail);
        fragmentList.add(baseFragment02);

        BookingDetailFragmentPagerAdapter fragmentPagerAdapter = new BookingDetailFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentPagerAdapter);
    }

    @Override
    protected void showCallDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_call_dialog_layout, null, false);

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        final View callDailyView = dialogView.findViewById(R.id.callDailyView);
        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        TextView callPlaceView = (TextView) dialogView.findViewById(R.id.callPlaceView);

        callPlaceView.setText(R.string.label_hotel_direct_phone);

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callDaily();
            }
        });
        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                kakaoDaily();
            }
        });
        callPlaceView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                callHotel();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockUI();
            }
        });

        try
        {
            dialog.setContentView(dialogView);
            dialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    protected void onTabSelected(int position)
    {
        switch (position)
        {
            case 0:
                break;

            case 1:
                break;

            case 2:
            {
                try
                {
                    // Check In
                    //                    String checkInDay = Util.simpleDateFormatISO8601toFormat(mHotelBookingDetail.checkInDate, "yyMMdd");
                    String checkInDay = DailyCalendar.convertDateFormatString(mHotelBookingDetail.checkInDate, DailyCalendar.ISO_8601_FORMAT, "yyMMdd");

                    // Check Out
                    //                    String checkOutDay = Util.simpleDateFormatISO8601toFormat(mHotelBookingDetail.checkOutDate, "yyMMdd");
                    String checkOutDay = DailyCalendar.convertDateFormatString(mHotelBookingDetail.checkOutDate, DailyCalendar.ISO_8601_FORMAT, "yyMMdd");

                    String label = String.format("Hotel-%s-%s-%s", mHotelBookingDetail.placeName, checkInDay, checkOutDay);

                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                        , mBooking.isUsed ? AnalyticsManager.Action.PAST_BOOKING_MAP_VIEW_CLICKED : AnalyticsManager.Action.UPCOMING_BOOKING_MAP_VIEW_CLICKED//
                        , label, null);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_daily_call:
                callDaily();
                break;

            case R.id.action_kakaotalk:
                kakaoDaily();
                break;

            case R.id.action_direct_call:
                callHotel();
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

        DailyNetworkAPI.getInstance(this).requestHotelBookingDetailInformation(mNetworkTag, reservationIndex, mReservationBookingDetailJsonResponseListener);
    }

    @Override
    protected void setCurrentDateTime(long currentDateTime, long dailyDateTime)
    {
        mHotelBookingDetail.currentDateTime = currentDateTime;
        mHotelBookingDetail.dailyDateTime = dailyDateTime;
    }

    private void callDaily()
    {
        if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
        {
            try
            {
                String phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getCompanyPhoneNumber();

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            } catch (ActivityNotFoundException e)
            {
                DailyToast.showToast(HotelBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
            }
        } else
        {
            DailyToast.showToast(HotelBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
        }
    }

    private void kakaoDaily()
    {
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
    }

    private void callHotel()
    {
        if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
        {
            String phone = mHotelBookingDetail.hotelPhone;

            if (Util.isTextEmpty(mHotelBookingDetail.hotelPhone) == true)
            {
                phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getCompanyPhoneNumber();
            }

            try
            {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
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
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    mHotelBookingDetail.setData(jsonObject);

                    loadFragments(getViewPager(), mHotelBookingDetail);
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            HotelBookingDetailTabActivity.this.onErrorResponse(volleyError);
        }
    };
}
