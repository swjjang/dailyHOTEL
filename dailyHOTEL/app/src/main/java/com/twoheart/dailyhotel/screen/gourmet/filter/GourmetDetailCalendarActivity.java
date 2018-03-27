package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.GourmetDetailParams;
import com.twoheart.dailyhotel.network.model.GourmetProduct;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

@Deprecated
public class GourmetDetailCalendarActivity extends GourmetCalendarActivity
{
    private int mPlaceIndex;

    //    private PlaceDetailCalendarImpl mPlaceDetailCalendarImpl;

    //    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
    //        , GourmetBookingDay gourmetBookingDay, int placeIndex, int dayOfMaxCount, String screen //
    //        , ArrayList<Integer> soldOutList, boolean isSelected, boolean isAnimation)
    //    {
    //        Intent intent = new Intent(context, GourmetDetailCalendarActivity.class);
    //        intent.putExtra(INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
    //        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
    //        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
    //        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
    //        intent.putIntegerArrayListExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST, soldOutList);
    //        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
    //
    //        return intent;
    //    }
    //
    //    public static Intent newInstance(Context context, CommonDateTime commonDateTime //
    //        , String visitDateTime, int gourmetIndex, int dayOfMaxCount, String screen //
    //        , ArrayList<Integer> soldOutList, boolean selected, boolean animation) throws Exception
    //    {
    //        TodayDateTime todayDateTime = commonDateTime.getTodayDateTime();
    //
    //        GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();
    //        gourmetBookingDay.setVisitDay(visitDateTime);
    //
    //        return newInstance(context, todayDateTime, gourmetBookingDay, gourmetIndex, dayOfMaxCount, screen//
    //            , soldOutList, selected, animation);
    //    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //        mPlaceDetailCalendarImpl = new PlaceDetailCalendarImpl(this);

        Intent intent = getIntent();

        mPlaceIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, -1);
    }

    @Override
    protected void onConfirm(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null || mPlaceIndex == -1)
        {
            setSaleTicketResult(gourmetBookingDay, -1);
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        DailyMobileAPI.getInstance(this).requestGourmetDetailInformation(mNetworkTag, mPlaceIndex, gourmetBookingDay.getVisitDay("yyyy-MM-dd"), mGourmetDetailInformationCallback);
    }

    void setSaleTicketResult(GourmetBookingDay gourmetBookingDay, int count)
    {
        if (count < 1)
        {
            showEmptyDialog();
        } else
        {
            if (gourmetBookingDay == null)
            {
                showEmptyDialog();
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            String date = gourmetBookingDay.getVisitDay("yyyy.MM.dd(EEE)");

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.VISIT_DATE, gourmetBookingDay.getVisitDay("yyyyMMdd"));
            params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED//
                , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : //
                    AnalyticsManager.ValueType.NONE_) + "-" + date + "-" + DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분"), params);

            Intent intent = new Intent();
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE, gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));

            setResult(RESULT_OK, intent);
            hideAnimation();
        }
    }

    private void showEmptyDialog()
    {
        unLockUI();

        String title = getResources().getString(R.string.dialog_notice2);
        String message = getResources().getString(R.string.gourmet_detail_calender_dialog_message);
        String confirm = getResources().getString(R.string.dialog_btn_text_confirm);

        showSimpleDialog(title, message, confirm, null);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mGourmetDetailInformationCallback = new retrofit2.Callback<BaseDto<GourmetDetailParams>>()
    {
        @Override
        public void onResponse(Call<BaseDto<GourmetDetailParams>> call, Response<BaseDto<GourmetDetailParams>> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                int saleTicketCount = 0;

                try
                {
                    BaseDto<GourmetDetailParams> baseDto = response.body();

                    if (baseDto.msgCode != 100 || baseDto.data == null)
                    {
                        saleTicketCount = 0;
                    } else
                    {
                        List<GourmetProduct> gourmetProductList = baseDto.data.getProductList();

                        if (gourmetProductList == null)
                        {
                            saleTicketCount = 0;
                        } else
                        {
                            saleTicketCount = gourmetProductList.size();
                        }
                    }
                } catch (Exception e)
                {
                    saleTicketCount = 0;
                } finally
                {
                    unLockUI();
                    GourmetDetailCalendarActivity.this.setSaleTicketResult((GourmetBookingDay) mPlaceBookingDay, saleTicketCount);
                }
            } else
            {
                GourmetDetailCalendarActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<GourmetDetailParams>> call, Throwable t)
        {
            GourmetDetailCalendarActivity.this.onError(t);
        }
    };
}
