/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 */
package com.twoheart.dailyhotel.screen.gourmet.detail;

import android.content.Intent;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetDetail;
import com.twoheart.dailyhotel.model.ImageInformation;
import com.twoheart.dailyhotel.model.PlaceDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.activity.PlaceDetailActivity;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetDetailCalendarActivity;
import com.twoheart.dailyhotel.screen.gourmet.payment.GourmetPaymentActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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

        HashMap<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.NAME, placeDetail.name);
        params.put(AnalyticsManager.KeyType.CHECK_IN, checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"));

        //        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
        //        params.put(AnalyticsManager.KeyType.CURRENT_TIME, dateFormat2.format(new Date()));
        params.put(AnalyticsManager.KeyType.CURRENT_TIME, DailyCalendar.format(new Date(), "yyyy-MM-dd'T'HH:mm:ss"));

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.GOURMET_BOOKINGS//
            , AnalyticsManager.Action.SOCIAL_SHARE_CLICKED, placeDetail.name, params);
    }

    @Override
    protected void requestPlaceDetailInformation(PlaceDetail placeDetail, SaleTime checkInSaleTime)
    {
        DailyNetworkAPI.getInstance(this).requestGourmetDetailInformation(mNetworkTag, placeDetail.index, checkInSaleTime.getDayOfDaysDateFormat("yyMMdd"), mGourmetDetailJsonResponseListener, this);
    }

    @Override
    protected void processBooking(PlaceDetail placeDetail, TicketInformation ticketInformation, SaleTime checkInSaleTime, boolean isBenefit)
    {
        if (placeDetail == null || ticketInformation == null || checkInSaleTime == null)
        {
            return;
        }

        String imageUrl = null;
        ArrayList<ImageInformation> mImageInformationList = placeDetail.getImageInformationList();

        if (mImageInformationList != null && mImageInformationList.size() > 0)
        {
            imageUrl = mImageInformationList.get(0).url;
        }

        Intent intent = GourmetPaymentActivity.newInstance(GourmetDetailActivity.this, ticketInformation//
            , checkInSaleTime, imageUrl, ((GourmetDetail) mPlaceDetail).category, mPlaceDetail.index, isBenefit, mProvince, mArea);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
    }

    @Override
    protected void onCalendarActivityResult(int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            SaleTime checkInSaleTime = data.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

            if (checkInSaleTime == null)
            {
                return;
            }

            mCheckInSaleTime = checkInSaleTime;
            mPlaceDetail = new GourmetDetail(mPlaceDetail.index);

            requestPlaceDetailInformation(mPlaceDetail, mCheckInSaleTime);
        }
    }

    @Override
    protected void startCalendar(SaleTime saleTime, int placeIndex, boolean isAnimation)
    {
        if (isFinishing() == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        Intent intent = GourmetDetailCalendarActivity.newInstance(GourmetDetailActivity.this, saleTime, placeIndex, AnalyticsManager.ValueType.DETAIL, true, isAnimation);
        startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_CALENDAR);

        AnalyticsManager.getInstance(GourmetDetailActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.ValueType.DETAIL, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mGourmetDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");

                JSONObject dataJSONObject = null;

                if (response.has("data") == true && response.isNull("data") == false)
                {
                    dataJSONObject = response.getJSONObject("data");
                }

                if (msgCode != 0 || dataJSONObject == null)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(GourmetDetailActivity.this, msg, Toast.LENGTH_SHORT);
                        setResult(CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                mPlaceDetail.setData(dataJSONObject);

                if (mIsStartByShare == true)
                {
                    mIsStartByShare = false;
                    mDailyToolbarLayout.setToolbarText(mPlaceDetail.name);
                }

                if (mPlaceDetailLayout != null)
                {
                    mPlaceDetailLayout.setDetail(mPlaceDetail, mCheckInSaleTime, mCurrentImage);
                }

                recordAnalyticsGourmetDetail(AnalyticsManager.Screen.DAILYGOURMET_DETAIL, mPlaceDetail);
            } catch (Exception e)
            {
                onError(e);
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };
}
