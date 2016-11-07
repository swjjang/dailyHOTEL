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
import android.view.View;
import android.view.Window;
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
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

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

        BaseFragment baseFragment01 = HotelBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mReservationIndex);
        fragmentList.add(baseFragment01);

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

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View contactUs01Layout = dialogView.findViewById(R.id.contactUs01Layout);
        View contactUs02Layout = dialogView.findViewById(R.id.contactUs02Layout);

        DailyTextView contactUs01TextView = (DailyTextView) contactUs01Layout.findViewById(R.id.contactUs01TextView);
        contactUs01TextView.setText(R.string.frag_faqs);
        contactUs01TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_05_faq, 0, 0, 0);

        contactUs01Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startFAQ();
            }
        });

        DailyTextView contactUs02TextView = (DailyTextView) contactUs02Layout.findViewById(R.id.contactUs02TextView);
        contactUs02TextView.setText(R.string.label_hotel_direct_phone);
        contactUs02TextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.popup_ic_ops_01_store_call, 0, 0, 0);

        contactUs02Layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startHotelCall();
            }
        });

        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        View callDailyView = dialogView.findViewById(R.id.callDailyView);

        kakaoDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startKakao();
            }
        });

        callDailyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }

                startDailyCall();
            }
        });

        View closeView = dialogView.findViewById(R.id.closeView);
        closeView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(dialog.isShowing() == true)
                {
                    dialog.dismiss();
                }
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

    private void startFAQ()
    {
        startActivityForResult(new Intent(this, FAQActivity.class), CODE_REQUEST_ACTIVITY_FAQ);
    }

    private void startHotelCall()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECT_CALL, null);

        if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
        {
            String phone = mHotelBookingDetail.hotelPhone;

            if (Util.isTextEmpty(mHotelBookingDetail.hotelPhone) == true)
            {
                phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getRemoteConfigCompanyPhoneNumber();
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

    private void startKakao()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

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

    private void startDailyCall()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.CUSTOMER_CENTER_CALL, null);

        if (Util.isTelephonyEnabled(HotelBookingDetailTabActivity.this) == true)
        {
            try
            {
                String phone = DailyPreference.getInstance(HotelBookingDetailTabActivity.this).getRemoteConfigCompanyPhoneNumber();

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mReservationBookingDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, Map<String, String> params, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                switch (msgCode)
                {
                    case 100:
                        JSONObject jsonObject = response.getJSONObject("data");

                        mHotelBookingDetail.setData(jsonObject);

                        loadFragments(getViewPager(), mHotelBookingDetail);
                        break;

                    // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                    case 501:
                        onErrorPopupMessage(msgCode, response.getString("msg"), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Util.restartApp(HotelBookingDetailTabActivity.this);
                            }
                        });
                        break;

                    default:
                        onErrorPopupMessage(msgCode, response.getString("msg"));
                        break;
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
