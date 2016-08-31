package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StayDetailCalendarActivity extends StayCalendarActivity
{
    private int mHotelIndex;
    private SaleTime mCheckInSaleTime;
    private SaleTime mCheckOutSaleTime;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, int hotelIndex, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayDetailCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mHotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
    }

    @Override
    protected void onConfirm(SaleTime checkInSaleTime, SaleTime chekcOutSaleTime)
    {
        if (checkInSaleTime == null || chekcOutSaleTime == null)
        {
            setSaleRoomResult(-1);
            return;
        }

        if (mHotelIndex == -1)
        {
            setSaleRoomResult(-1);
            return;
        }

        mCheckInSaleTime = checkInSaleTime;
        mCheckOutSaleTime = chekcOutSaleTime;

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        int nights = chekcOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay();

        // 호텔 정보를 가져온다.
        DailyNetworkAPI.getInstance(StayDetailCalendarActivity.this) //
            .requestHotelDetailInformation(mNetworkTag, mHotelIndex, checkInSaleTime.getDayOfDaysDateFormat("yyyy-MM-dd"), //
                nights, mJsonResponseListener);
    }

    private void setSaleRoomResult(int count)
    {
        if (count < 1)
        {
            showEmptyDialog();
        } else
        {
            if (mCheckInSaleTime == null || mCheckOutSaleTime == null)
            {
                showEmptyDialog();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String checkInDate = mCheckInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
            String checkOutDate = mCheckOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
            int nights = mCheckOutSaleTime.getOffsetDailyDay() - mCheckInSaleTime.getOffsetDailyDay();

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInSaleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, Long.toString(mCheckOutSaleTime.getDayOfDaysDate().getTime()));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
            params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

            //            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분", Locale.KOREA);
            //            String phoneDate = simpleDateFormat.format(new Date());
            String phoneDate = DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분");

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED//
                , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE) + "-" + checkInDate + "-" + checkOutDate + "-" + phoneDate, params);

            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInSaleTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutSaleTime);

            setResult(RESULT_OK, intent);
            hideAnimation();
        }
    }

    private void showEmptyDialog()
    {
        unLockUI();

        String title = getResources().getString(R.string.dialog_notice2);
        String message = getResources().getString(R.string.stay_detail_calender_dialog_message);
        String confirm = getResources().getString(R.string.dialog_btn_text_confirm);

        showSimpleDialog(title, message, confirm, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            showEmptyDialog();
        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            int saleRoomCount = 0;

            try
            {
                int msgCode = response.getInt("msgCode");

                JSONObject dataJSONObject = null;

                if (response.has("data") == true && response.isNull("data") == false)
                {
                    dataJSONObject = response.getJSONObject("data");
                }

                if (msgCode != 100 || dataJSONObject == null)
                {
                    saleRoomCount = 0;
                } else
                {
                    JSONArray saleRoomJSONArray = dataJSONObject.getJSONArray("hotelRoomDetail");
                    if (saleRoomJSONArray == null)
                    {
                        saleRoomCount = 0;
                    } else
                    {
                        saleRoomCount = saleRoomJSONArray.length();
                    }
                }
            } catch (Exception e)
            {
                saleRoomCount = 0;
            } finally
            {
                unLockUI();
                StayDetailCalendarActivity.this.setSaleRoomResult(saleRoomCount);
            }
        }
    };
}
