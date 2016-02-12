/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.gourmetdetail;

import android.content.Intent;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.GourmetPaymentActivity;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GourmetDetailActivity extends PlaceDetailActivity
{
    @Override
    protected GourmetDetailLayout getLayout(BaseActivity activity, String imageUrl)
    {
        return new GourmetDetailLayout(activity, imageUrl);
    }

    @Override
    protected PlaceDetail createPlaceDetail(Intent intent)
    {
        if (intent == null)
        {
            return null;
        }

        int index = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);

        return new GourmetDetail(index);
    }

    @Override
    protected void shareKakao(PlaceDetail placeDetail, String imageUrl, SaleTime checkInSaleTime, SaleTime checkOutSaleTime)
    {
        String name = DailyPreference.getInstance(GourmetDetailActivity.this).getUserName();

        if (Util.isTextEmpty(name) == true)
        {
            name = getString(R.string.label_friend) + "가";
        } else
        {
            name += "님이";
        }

        KakaoLinkManager.newInstance(this).shareGourmet(name, placeDetail.name, placeDetail.address//
            , placeDetail.index //
            , imageUrl //
            , checkInSaleTime);

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMETBOOKINGS//
            , AnalyticsManager.Action.SOCIAL_SHARE_CLICKED, placeDetail.name, 0L);
    }

    @Override
    protected void requestPlaceDetailInformation(PlaceDetail placeDetail, SaleTime checkInSaleTime)
    {
        String params = String.format("?restaurant_idx=%d&sday=%s", placeDetail.index, checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"));

        if (DEBUG == true)
        {
            showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
        }

        DailyNetworkAPI.getInstance().requestGourmetDetailInformation(mNetworkTag, params, mGourmetDetailJsonResponseListener, this);
    }

    @Override
    protected void processBooking(TicketInformation ticketInformation, SaleTime checkInSaleTime, int gourmetIndex)
    {
        if (ticketInformation == null)
        {
            return;
        }

        Intent intent = GourmetPaymentActivity.newInstance(GourmetDetailActivity.this, ticketInformation, checkInSaleTime, gourmetIndex);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION, ticketInformation);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, checkInSaleTime);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mGourmetDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        private void recordAnalytics(PlaceDetail placeDetail)
        {
            if (placeDetail == null)
            {
                return;
            }

            try
            {
                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.NAME, placeDetail.name);

                if (placeDetail.getTicketInformation() == null || placeDetail.getTicketInformation().size() == 0)
                {
                    params.put(AnalyticsManager.KeyType.PRICE, "0");
                } else
                {
                    params.put(AnalyticsManager.KeyType.PRICE, Integer.toString(placeDetail.getTicketInformation().get(0).discountPrice));
                }

                params.put(AnalyticsManager.KeyType.PLACE_INDEX, Integer.toString(placeDetail.index));
                params.put(AnalyticsManager.KeyType.DATE, mCheckInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

                AnalyticsManager.getInstance(GourmetDetailActivity.this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_DETAIL, params);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(GourmetDetailActivity.this, msg, Toast.LENGTH_SHORT);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject dataJSONObject = response.getJSONObject("data");

                mPlaceDetail.setData(dataJSONObject);

                if (mIsStartByShare == true)
                {
                    mIsStartByShare = false;
                    mDailyToolbarLayout.setToolbarText(mPlaceDetail.name);
                }

                if (mPlaceDetailLayout != null)
                {
                    mPlaceDetailLayout.setDetail(mPlaceDetail, mCurrentImage);
                }

                recordAnalytics(mPlaceDetail);
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
