/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * HotelBookingDetailTabActivity (예약한 호텔의 예약, 정보, 지도탭을 보여주는 화면)
 * <p>
 * 예약한 호텔리스트에서 호텔 클릭 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

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
import com.twoheart.dailyhotel.model.GourmetBookingDetail;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlaceBookingDetailTabActivity;
import com.twoheart.dailyhotel.place.base.BaseFragment;
import com.twoheart.dailyhotel.screen.booking.detail.BookingDetailFragmentPagerAdapter;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;

public class GourmetBookingDetailTabActivity extends PlaceBookingDetailTabActivity
{
    private GourmetBookingDetail mGourmetBookingDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mGourmetBookingDetail = new GourmetBookingDetail();
    }

    @Override
    protected void loadFragments(ViewPager viewPager, PlaceBookingDetail placeBookingDetail)
    {
        String tag = (String) viewPager.getTag();

        if (tag != null)
        {
            return;
        }

        viewPager.setTag("GourmetBookingDetailTabActivity");

        ArrayList<BaseFragment> fragmentList = new ArrayList<>();

        BaseFragment baseFragment01 = GourmetBookingDetailTabBookingFragment.newInstance(placeBookingDetail, mReservationIndex);
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

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);

        // 버튼
        View callDailyView = dialogView.findViewById(R.id.callDailyView);
        View kakaoDailyView = dialogView.findViewById(R.id.kakaoDailyView);
        TextView callPlaceView = (TextView) dialogView.findViewById(R.id.callPlaceView);

        callPlaceView.setText(R.string.label_restaurant_direct_phone);

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
                callGourmet();
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
                callGourmet();
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

        DailyNetworkAPI.getInstance(this).requestGourmetBookingDetailInformation(mNetworkTag, reservationIndex, mReservationBookingDetailJsonResponseListener);
    }

    @Override
    protected void setCurrentDateTime(long currentDateTime, long dailyDateTime)
    {
        mGourmetBookingDetail.currentDateTime = currentDateTime;
        mGourmetBookingDetail.dailyDateTime = dailyDateTime;
    }

    private void callDaily()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.CUSTOMER_CENTER_CALL, null);

        if (Util.isTelephonyEnabled(GourmetBookingDetailTabActivity.this) == true)
        {
            try
            {
                String phone = DailyPreference.getInstance(GourmetBookingDetailTabActivity.this).getCompanyPhoneNumber();

                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            } catch (ActivityNotFoundException e)
            {
                DailyToast.showToast(GourmetBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
            }
        } else
        {
            DailyToast.showToast(GourmetBookingDetailTabActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
        }
    }

    private void kakaoDaily()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.KAKAO, null);

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
    }

    private void callGourmet()
    {
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.CALL_BUTTON_CLICKED, AnalyticsManager.Action.BOOKING_DETAIL, AnalyticsManager.Label.DIRECT_CALL, null);

        if (Util.isTelephonyEnabled(GourmetBookingDetailTabActivity.this) == true)
        {
            String phone = mGourmetBookingDetail.gourmetPhone;

            if (Util.isTextEmpty(mGourmetBookingDetail.gourmetPhone) == true)
            {
                phone = DailyPreference.getInstance(GourmetBookingDetailTabActivity.this).getCompanyPhoneNumber();
            }

            try
            {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
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

                switch (msgCode)
                {
                    case 100:
                        JSONObject jsonObject = response.getJSONObject("data");

                        mGourmetBookingDetail.setData(jsonObject);

                        loadFragments(getViewPager(), mGourmetBookingDetail);
                        break;

                    // 예약 내역 진입시에 다른 사용자가 딥링크로 진입시 예외 처리 추가
                    case 501:
                        onErrorPopupMessage(msgCode, response.getString("msg"), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Util.restartApp(GourmetBookingDetailTabActivity.this);
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
            GourmetBookingDetailTabActivity.this.onErrorResponse(volleyError);
        }
    };
}
