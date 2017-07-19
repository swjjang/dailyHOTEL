package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StayCalendarActivity extends PlaceCalendarActivity
{
    public static final String INTENT_EXTRA_DATA_CHECKIN_DATETIME = "checkInDateTime";
    public static final String INTENT_EXTRA_DATA_CHECKOUT_DATETIME = "checkOutDateTime";

    protected static final int DAYCOUNT_OF_MAX = 60;

    private View mCheckInDayView;
    private View mCheckOutDayView;
    private TextView mConfirmTextView;

    protected boolean mIsChanged;

    /**
     * @param context
     * @param todayDateTime
     * @param stayBookingDay
     * @param screen
     * @param isSelected
     * @param isAnimation
     * @return
     */
    public static Intent newInstance(Context context, TodayDateTime todayDateTime, StayBookingDay stayBookingDay, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
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

        mTodayDateTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME);
        mPlaceBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        final boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        if (intent.hasExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST) == true)
        {
            mSoldOutDayList = intent.getIntegerArrayListExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST);
        }

        if (mTodayDateTime == null || mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout(R.layout.activity_calendar, getMaxDay());
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    makeCalendar(mTodayDateTime, getMaxDay(), mSoldOutDayList);

                    reset();

//                    setSoldOutDays(mTodayDateTime, mSoldOutDayList);

                    if (isSelected == true)
                    {
                        setSelectedRangeDay(mTodayDateTime, (StayBookingDay) mPlaceBookingDay);
                        checkLastDay();
                    }

                    showAnimation();
                }
            }, 20);
        } else
        {
            setTouchEnabled(true);

            makeCalendar(mTodayDateTime, getMaxDay(), mSoldOutDayList);

            reset();

//            setSoldOutDays(mTodayDateTime, mSoldOutDayList);

            if (isSelected == true)
            {
                setSelectedRangeDay(mTodayDateTime, (StayBookingDay) mPlaceBookingDay);
                checkLastDay();
            }
        }
    }

    @Override
    protected void initLayout(int layoutResID, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dayCountOfMax);

        mConfirmTextView = (TextView) findViewById(R.id.confirmView);
        mConfirmTextView.setVisibility(View.VISIBLE);
        mConfirmTextView.setOnClickListener(this);
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);

        if (AnalyticsManager.ValueType.SEARCH.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(this, 133));
            mExitView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR, null);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onBackPressed()
    {
        // 일단은 애니메이션으로 검색 선택시에 Analytics를 구분하도록 한다.
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
            , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);

        hideAnimation();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.exitView:
            case R.id.closeView:

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);

                hideAnimation();
                break;

            case R.id.confirmView:
            {
                if (mCheckInDayView == null || mCheckOutDayView == null)
                {
                    return;
                }

                Day checkInDay = (Day) mCheckInDayView.getTag();
                Day checkOutDay = (Day) mCheckOutDayView.getTag();

                StayBookingDay stayBookingDay = (StayBookingDay) mPlaceBookingDay;

                try
                {
                    stayBookingDay.setCheckInDay(checkInDay.dateTime);
                    stayBookingDay.setCheckOutDay(checkOutDay.dateTime);

                    onConfirm(stayBookingDay);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }
                break;
            }

            default:
            {
                Day day = (Day) view.getTag();

                if (day == null)
                {
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                // 이미 체크인 체크아웃이 선택되어있으면 초기화
                if (mCheckInDayView != null && mCheckOutDayView != null)
                {
                    // 체크인 체크아웃이 되어있는데 마지막 날짜를 체크인할때
                    if (mDayViewList.get(mDayViewList.size() - 1) == view)
                    {
                        DailyToast.showToast(this, getString(R.string.label_message_dont_check_date), Toast.LENGTH_SHORT);
                        releaseUiComponent();
                        return;
                    } else
                    {
                        reset();
                    }
                }

                // 기존의 날짜 보다 전날짜를 선택하면 초기화.
                if (mCheckInDayView != null)
                {
                    Day currentCheckInDay = (Day) mCheckInDayView.getTag();

                    int compareValue = 0;
                    try
                    {
                        compareValue = DailyCalendar.compareDateDay(currentCheckInDay.dateTime, day.dateTime);
                    } catch (Exception e)
                    {
                        ExLog.w(e.toString());
                    }

                    if (compareValue >= 0)
                    {
                        reset();
                    }
                }

                if (mCheckInDayView == null)
                {
                    mCheckInDayView = view;
                    setSelectedCheckIn(mCheckInDayView);

                    view.setSelected(true);

//                    setSoldOutDays(mTodayDateTime, mSoldOutDayList);

                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    mDayViewList.get(mDayViewList.size() - 1).setEnabled(true);

                    // TODO : 여기서 체크아웃 가능 날짜 조회

                    setAvailableCheckOutDays(mTodayDateTime, mCheckInDayView);
                } else
                {
                    mCheckOutDayView = view;
                    setSelectedCheckOut(mCheckOutDayView);

                    Day checkInDay = (Day) mCheckInDayView.getTag();
                    Day checkOutDay = (Day) mCheckOutDayView.getTag();

                    view.setSelected(true);

//                    setSoldOutDays(mTodayDateTime, mSoldOutDayList);

                    Calendar calendar = DailyCalendar.getInstance();

                    try
                    {
                        int nights = DailyCalendar.compareDateDay(checkOutDay.dateTime, checkInDay.dateTime);

                        DailyCalendar.setCalendarDateString(calendar, checkInDay.dateTime);
                        String checkInDate = DailyCalendar.format(calendar.getTime(), "yyyy.MM.dd(EEE)");

                        DailyCalendar.setCalendarDateString(calendar, checkOutDay.dateTime);
                        String checkOutDate = DailyCalendar.format(calendar.getTime(), "yyyy.MM.dd(EEE)");

                        String title = String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights);
                        setToolbarText(title);

                        setRangeDaysAlpha(mCheckOutDayView);
                        checkLastDay();
                        mConfirmTextView.setEnabled(true);
                        mConfirmTextView.setText(getString(R.string.label_calendar_stay_search_selected_date, nights));
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }

                releaseUiComponent();
                break;
            }
        }
    }

    protected int getMaxDay()
    {
        return DAYCOUNT_OF_MAX;
    }

    protected void onConfirm(StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
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

            Map<String, String> params = new HashMap<>();
            params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, stayBookingDay.getCheckInDay("yyyyMMdd"));
            params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, stayBookingDay.getCheckOutDay("yyyyMMdd"));
            params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(stayBookingDay.getNights()));
            params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

            String phoneDate = DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분");

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED//
                , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE_) + "-" + checkInDate + "-" + checkOutDate + "-" + phoneDate, params);

        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);

        setResult(RESULT_OK, intent);
        hideAnimation();
    }

    private void setSelectedCheckIn(View checkInView)
    {
        if (checkInView == null)
        {
            return;
        }

        TextView visitTextView = (TextView) checkInView.findViewById(R.id.textView);
        visitTextView.setText(R.string.act_booking_chkin);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setVisibility(View.VISIBLE);

        TextView dayTextView = (TextView) checkInView.findViewById(R.id.dateTextView);
        if ((dayTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG)
        {
            dayTextView.setPaintFlags(dayTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        checkInView.setBackgroundResource(R.drawable.select_date_check_in);
    }

    private void setSelectedCheckOut(View checkOutView)
    {
        if (checkOutView == null)
        {
            return;
        }

        TextView visitTextView = (TextView) checkOutView.findViewById(R.id.textView);
        visitTextView.setText(R.string.act_booking_chkout);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setVisibility(View.VISIBLE);

        TextView dayTextView = (TextView) checkOutView.findViewById(R.id.dateTextView);
        if ((dayTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG)
        {
            dayTextView.setPaintFlags(dayTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        checkOutView.setBackgroundResource(R.drawable.select_date_check_out);
    }

    private void resetCheckView(View checkView)
    {
        if (checkView == null)
        {
            return;
        }

//        TextView textView = (TextView) checkView.findViewById(R.id.textView);
//        textView.setText(null);
//        textView.setVisibility(View.INVISIBLE);

        updateDayView(checkView);

        checkView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));
    }

    void setSelectedRangeDay(TodayDateTime todayDateTime, StayBookingDay stayBookingDay)
    {
        if (stayBookingDay == null)
        {
            return;
        }

        Calendar calendar = DailyCalendar.getInstance();

        try
        {
            int checkInDay = Integer.parseInt(stayBookingDay.getCheckInDay("yyyyMMdd"));
            int checkOutDay = Integer.parseInt(stayBookingDay.getCheckOutDay("yyyyMMdd"));

            for (View dayView : mDayViewList)
            {
                if (dayView == null)
                {
                    continue;
                }

                Day day = (Day) dayView.getTag();

                DailyCalendar.setCalendarDateString(calendar, day.dateTime);

                int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));

                if (calendarDay == checkInDay)
                {
                    dayView.performClick();
                } else if (calendarDay == checkOutDay)
                {
                    dayView.performClick();
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void setRangeDaysAlpha(View view)
    {
        boolean isStartPosition = false;

        for (View dayView : mDayViewList)
        {
            if (isStartPosition == false)
            {
                if (dayView.isSelected() == true)
                {
                    isStartPosition = true;
                }
            } else
            {
                if (view == dayView)
                {
                    break;
                }

                dayView.setSelected(true);
                dayView.setActivated(true);
            }
        }
    }

    /**
     * 마지막 날짜는 체크인 날짜로 할수 없다.
     */
    void checkLastDay()
    {
        if (mCheckInDayView == null || mCheckOutDayView == null)
        {
            return;
        }

        if (mDayViewList.get(mDayViewList.size() - 1) == mCheckOutDayView)
        {
            return;
        }

        mDayViewList.get(mDayViewList.size() - 1).setEnabled(false);
    }

//    void setSoldOutDays(TodayDateTime todayDateTime, List<String> unavailableDateList)
//    {
//        if (todayDateTime == null || unavailableDateList == null || unavailableDateList.size() == 0)
//        {
//            return;
//        }
//
//        Calendar calendar = DailyCalendar.getInstance();
//
//        try
//        {
//            for (View dayView : mDailyViews)
//            {
//                if (dayView == null)
//                {
//                    continue;
//                }
//
//                Day day = (Day) dayView.getTag();
//
//                DailyCalendar.setCalendarDateString(calendar, todayDateTime.dailyDateTime, day.dayOffset);
//
//                int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));
//
//                if (dayView.isSelected() == true)
//                {
//                    continue;
//                }
//
//                for (String unavailableDate : unavailableDateList)
//                {
//                    int checkDay = Integer.parseInt(DailyCalendar.convertDateFormatString(unavailableDate, "yyyy-MM-dd", "yyyyMMdd"));
//
//                    if (calendarDay == checkDay)
//                    {
//                        setSoldOutDay(dayView);
//                        break;
//                    }
//
//                }
//            }
//        } catch (Exception e)
//        {
//            ExLog.e(e.toString());
//        }
//    }
//
//    void setSoldOutDay(View view)
//    {
//        if (view == null)
//        {
//            return;
//        }
//
//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        textView.setText(R.string.label_calendar_soldout);
//        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8);
//        textView.setVisibility(View.VISIBLE);
//        view.setEnabled(false);
//
//        TextView dateTextView = (TextView) view.findViewById(R.id.dateTextView);
//        dateTextView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//    }

    void setAvailableCheckOutDays(TodayDateTime todayDateTime, View checkinDayView)
    {
        // do nothing - stayDetailCalendar 용
    }

    void setAvailableCheckOutDays(TodayDateTime todayDateTime, List<String> availableCheckOutDayList)
    {
        if (todayDateTime == null || availableCheckOutDayList == null || availableCheckOutDayList.size() == 0)
        {
            return;
        }

        Calendar calendar = DailyCalendar.getInstance();

        try
        {
            for (View dayView : mDayViewList)
            {
                if (dayView == null)
                {
                    continue;
                }

                if (dayView.isSelected() == true)
                {
                    // 체크인 날짜 이거나 판매 마감 된 날짜
                    continue;
                }

                Day day = (Day) dayView.getTag();

                DailyCalendar.setCalendarDateString(calendar, day.dateTime);

                int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));

                for (String unavailableDate : availableCheckOutDayList)
                {
                    int checkDay = Integer.parseInt(DailyCalendar.convertDateFormatString(unavailableDate, "yyyy-MM-dd", "yyyyMMdd"));

                    if (calendarDay == checkDay)
                    {
//                        setSoldOutDay(dayView);
                        updateDayView(dayView);
                        break;
                    }

                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void reset()
    {
        mIsChanged = true;

        if (mCheckInDayView != null)
        {
            mCheckInDayView.setSelected(false);
            resetCheckView(mCheckInDayView);
            mCheckInDayView = null;
        }

        if (mCheckOutDayView != null)
        {
            mCheckOutDayView.setSelected(false);
            resetCheckView(mCheckOutDayView);
            mCheckOutDayView = null;
        }

        for (View dayView : mDayViewList)
        {
            if (dayView == null)
            {
                continue;
            }

            dayView.setActivated(false);
            dayView.setSelected(false);
//            dayView.setEnabled(true);
            updateDayView(dayView);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);
    }
}
