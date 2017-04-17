package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.daily.base.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.daily.base.widget.DailyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StayCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

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

        if (mTodayDateTime == null || mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout(R.layout.activity_calendar, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    makeCalendar(mTodayDateTime, DAYCOUNT_OF_MAX);

                    reset();

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

            makeCalendar(mTodayDateTime, DAYCOUNT_OF_MAX);

            reset();

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
                    stayBookingDay.setCheckInDay(mTodayDateTime.dailyDateTime, checkInDay.dayOffset);
                    stayBookingDay.setCheckOutDay(mTodayDateTime.dailyDateTime, checkOutDay.dayOffset);

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
                    if (mDailyViews[mDailyViews.length - 1] == view)
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

                    if (currentCheckInDay.dayOffset >= day.dayOffset)
                    {
                        reset();
                    }
                }

                if (mCheckInDayView == null)
                {
                    mCheckInDayView = view;
                    setSelectedCheckIn(mCheckInDayView);

                    view.setSelected(true);
                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    mDailyViews[mDailyViews.length - 1].setEnabled(true);
                } else
                {
                    mCheckOutDayView = view;
                    setSelectedCheckOut(mCheckOutDayView);

                    Day checkInDay = (Day) mCheckInDayView.getTag();
                    Day checkOutDay = (Day) mCheckOutDayView.getTag();
                    int nights = checkOutDay.dayOffset - checkInDay.dayOffset;

                    view.setSelected(true);

                    Calendar calendar = DailyCalendar.getInstance();

                    try
                    {
                        DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.dailyDateTime, checkInDay.dayOffset);
                        String checkInDate = DailyCalendar.format(calendar.getTime(), "yyyy.MM.dd(EEE)");

                        DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.dailyDateTime, checkOutDay.dayOffset);
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

        TextView textView = (TextView) checkInView.findViewById(R.id.textView);
        textView.setText(R.string.act_booking_chkin);
        textView.setVisibility(View.VISIBLE);

        checkInView.setBackgroundResource(R.drawable.select_date_check_in);
    }

    private void setSelectedCheckOut(View checkOutView)
    {
        if (checkOutView == null)
        {
            return;
        }

        TextView textView = (TextView) checkOutView.findViewById(R.id.textView);
        textView.setText(R.string.act_booking_chkout);
        textView.setVisibility(View.VISIBLE);

        checkOutView.setBackgroundResource(R.drawable.select_date_check_out);
    }

    private void resetCheckView(View checkView)
    {
        if (checkView == null)
        {
            return;
        }

        TextView textView = (TextView) checkView.findViewById(R.id.textView);
        textView.setText(null);
        textView.setVisibility(View.INVISIBLE);

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

            for (View dayView : mDailyViews)
            {
                if (dayView == null)
                {
                    continue;
                }

                Day day = (Day) dayView.getTag();

                DailyCalendar.setCalendarDateString(calendar, todayDateTime.dailyDateTime, day.dayOffset);

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

        for (View dayView : mDailyViews)
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

        if (mDailyViews[mDailyViews.length - 1] == mCheckOutDayView)
        {
            return;
        }

        mDailyViews[mDailyViews.length - 1].setEnabled(false);
    }

    void reset()
    {
        mIsChanged = true;

        if (mCheckInDayView != null)
        {
            resetCheckView(mCheckInDayView);
            mCheckInDayView = null;
        }

        if (mCheckOutDayView != null)
        {
            resetCheckView(mCheckOutDayView);
            mCheckOutDayView = null;
        }

        for (View dayView : mDailyViews)
        {
            if (dayView == null)
            {
                continue;
            }

            dayView.setActivated(false);
            dayView.setSelected(false);
            dayView.setEnabled(true);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);
    }
}
