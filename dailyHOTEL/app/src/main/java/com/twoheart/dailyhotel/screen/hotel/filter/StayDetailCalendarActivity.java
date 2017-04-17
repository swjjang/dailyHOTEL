package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.daily.base.widget.DailyToast;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class StayDetailCalendarActivity extends StayCalendarActivity
{
    private int mHotelIndex;
    private boolean mIsSingleDay;

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay //
        , int hotelIndex, String screen, boolean isSelected//
        , boolean isAnimation, boolean isSingleDay)
    {
        Intent intent = new Intent(context, StayDetailCalendarActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
        intent.putExtra(INTENT_EXTRA_DATA_ISSINGLE_DAY, isSingleDay);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mHotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
        mIsSingleDay = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSINGLE_DAY, false);

        if (mIsSingleDay == true)
        {
            DailyToast.showToast(this, getString(R.string.message_calendar_select_single_day), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.exitView:
            case R.id.closeView:
            case R.id.confirmView:
            {
                super.onClick(view);
                break;
            }

            default:
            {
                super.onClick(view);

                if (mIsSingleDay == true)
                {
                    for (int i = 0; i < mDailyViews.length; i++)
                    {
                        if (view == mDailyViews[i] && i < mDailyViews.length - 1)
                        {
                            super.onClick(mDailyViews[i + 1]);
                            break;
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onConfirm(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            setSaleRoomResult(stayBookingDay, -1, null);
            return;
        }

        if (mHotelIndex == -1)
        {
            setSaleRoomResult(stayBookingDay, -1, null);
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        try
        {
            lockUI();

            // 호텔 정보를 가져온다.
            DailyMobileAPI.getInstance(StayDetailCalendarActivity.this) //
                .requestStayDetailInformation(mNetworkTag, mHotelIndex, stayBookingDay.getCheckInDay("yyyy-MM-dd"), //
                    stayBookingDay.getNights(), mStayDetailInformationCallback);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setSaleRoomResult(StayBookingDay stayBookingDay, int count, String message)
    {
        if (count < 1)
        {
            showEmptyDialog(message);
        } else
        {
            if (stayBookingDay == null)
            {
                showEmptyDialog(message);
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            try
            {
                String checkInDate = stayBookingDay.getCheckInDay("yyyy.MM.dd(EEE)");
                String checkOutDate = stayBookingDay.getCheckOutDay("yyyy.MM.dd(EEE)");
                int nights = stayBookingDay.getNights();

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookingDay.getCheckInDay("yyyyMMdd"));
                params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, stayBookingDay.getCheckOutDay("yyyyMMdd"));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(nights));
                params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

                String phoneDate = DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분");

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED//
                    , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE_) + "-" + checkInDate + "-" + checkOutDate + "-" + phoneDate, params);

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);

                setResult(RESULT_OK, intent);
                hideAnimation();
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }
    }

    private void showEmptyDialog(String message)
    {
        unLockUI();

        String title = getResources().getString(R.string.dialog_notice2);

        if (DailyTextUtils.isTextEmpty(message) == true)
        {
            message = getResources().getString(R.string.stay_detail_calender_dialog_message);
        }

        String confirm = getResources().getString(R.string.dialog_btn_text_confirm);

        showSimpleDialog(title, message, confirm, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mStayDetailInformationCallback = new retrofit2.Callback<BaseDto<StayDetailParams>>()
    {
        @Override
        public void onResponse(Call<BaseDto<StayDetailParams>> call, Response<BaseDto<StayDetailParams>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                int saleRoomCount = 0;
                String message = null;

                try
                {
                    BaseDto<StayDetailParams> baseDto = response.body();

                    if (baseDto.msgCode == 100)
                    {
                        if (baseDto.data == null)
                        {
                            saleRoomCount = 0;
                        } else
                        {
                            List<StayProduct> stayProductList = baseDto.data.getProductList();

                            if (stayProductList == null)
                            {
                                saleRoomCount = 0;
                            } else
                            {
                                saleRoomCount = stayProductList.size();
                            }
                        }
                    } else
                    {
                        message = baseDto.msg;
                    }
                } catch (Exception e)
                {
                    saleRoomCount = 0;
                } finally
                {
                    unLockUI();
                    StayDetailCalendarActivity.this.setSaleRoomResult((StayBookingDay) mPlaceBookingDay, saleRoomCount, message);
                }
            } else
            {
                StayDetailCalendarActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<StayDetailParams>> call, Throwable t)
        {
            StayDetailCalendarActivity.this.onError(t);
        }
    };
}
