package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.StayDetailParams;
import com.twoheart.dailyhotel.network.model.StayProduct;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class StayDetailCalendarActivity extends StayCalendarActivity
{
    private int mHotelIndex;

    private CalendarImpl mPlaceDetailCalendarImpl;

    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay //
        , int dayOfMaxCount, int hotelIndex, String screen, ArrayList<Integer> soldOutList, boolean isSelected//
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
        intent.putIntegerArrayListExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST, soldOutList);
        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);

        return intent;
    }

    public static Intent newInstance(Context context, CommonDateTime commonDateTime, String checkInDateTime, String checkOutDateTime //
        , int dayOfMaxCount, int hotelIndex, String screen, ArrayList<Integer> soldOutList, boolean isSelected//
        , boolean isAnimation, boolean isSingleDay) throws Exception
    {
        TodayDateTime todayDateTime = commonDateTime.getTodayDateTime();

        StayBookingDay stayBookingDay = new StayBookingDay();
        stayBookingDay.setCheckInDay(checkInDateTime);
        stayBookingDay.setCheckOutDay(checkOutDateTime);

        return newInstance(context, todayDateTime, stayBookingDay, dayOfMaxCount, hotelIndex//
            , screen, soldOutList, isSelected, isAnimation, isSingleDay);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mPlaceDetailCalendarImpl = new CalendarImpl(this);

        Intent intent = getIntent();
        mHotelIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onConfirm(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null || mHotelIndex == -1)
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
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECK_IN_DATE, stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT));
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECK_OUT_DATE, stayBookingDay.getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));

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

    @Override
    void getAvailableCheckOutDays(final View checkInDayView)
    {
        lockUI();

        String checkInDate = null;

        try
        {
            Day checkInDay = (Day) checkInDayView.getTag();
            checkInDate = DailyCalendar.convertDateFormatString(checkInDay.dateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        if (DailyTextUtils.isTextEmpty(checkInDate) == true)
        {
            unLockUI();
            return;
        }

        addCompositeDisposable(mPlaceDetailCalendarImpl.getStayAvailableCheckOutDates( //
            mHotelIndex, getDayOfMaxCount(), checkInDate).observeOn(Schedulers.io())//
            .map(new Function<List<String>, ArrayList<Integer>>()
            {
                @Override
                public ArrayList<Integer> apply(@NonNull List<String> stringList) throws Exception
                {
                    ArrayList<Integer> availableDayList = new ArrayList<>();

                    for (String dayString : stringList)
                    {
                        int availableDay = Integer.parseInt(DailyCalendar.convertDateFormatString(dayString, "yyyy-MM-dd", "yyyyMMdd"));
                        availableDayList.add(availableDay);
                    }

                    return availableDayList;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Integer>>()
            {
                @Override
                public void accept(@NonNull ArrayList<Integer> availableDayList) throws Exception
                {
                    unLockUI();

                    setAvailableCheckOutDays(checkInDayView, availableDayList);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    onHandleError(throwable);
                }
            }));
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
