package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GourmetCalendarActivity extends PlaceCalendarActivity
{
    public static final int DAYCOUNT_OF_MAX = 30;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 30;

    private View mDayView;
    private TextView mConfirmTextView;

    protected boolean mIsChanged;

    /**
     * @param context
     * @param todayDateTime
     * @param gourmetBookingDay
     * @param screen
     * @param isSelected
     * @param isAnimation
     * @return
     */
    public static Intent newInstance(Context context, TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
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
            mSoldOutDayList =  intent.getIntegerArrayListExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST);
        }

        if (mTodayDateTime == null || mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout(R.layout.activity_calendar, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_gourmet_select));

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

//                    setSoldOutDays(mTodayDateTime, mSoldOutDayList);

                    if (isSelected == true)
                    {
                        setSelectedDay(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay);
                    }

                    showAnimation();
                }
            }, 20);
        } else
        {
            setTouchEnabled(true);

            makeCalendar(mTodayDateTime, DAYCOUNT_OF_MAX);

            reset();

//            setSoldOutDays(mTodayDateTime, mSoldOutDayList);

            if (isSelected == true)
            {
                setSelectedDay(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay);
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
        mConfirmTextView.setText(R.string.label_calendar_gourmet_search_selected_date);

        if (AnalyticsManager.ValueType.SEARCH.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtils.dpToPx(this, 133));
            mExitView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.DAILYGOURMET_LIST_CALENDAR, null);

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
            , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);

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
                    , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);

                hideAnimation();
                break;

            case R.id.confirmView:
            {
                Day day = (Day) mDayView.getTag();

                GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;

                try
                {
                    gourmetBookingDay.setVisitDay(mTodayDateTime.dailyDateTime, day.dayOffset);

                    onConfirm(gourmetBookingDay);
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

                if (mDayView != null)
                {
                    reset();
                }

                mDayView = view;
                setSelectedDay(mDayView);

                view.setSelected(true);

//                setSoldOutDays(mTodayDateTime, mSoldOutDayList);

                Calendar calendar = DailyCalendar.getInstance();

                try
                {
                    DailyCalendar.setCalendarDateString(calendar, mTodayDateTime.dailyDateTime, day.dayOffset);
                    String visitDate = DailyCalendar.format(calendar.getTime(), "yyyy.MM.dd(EEE)");

                    setToolbarText(visitDate);

                    mConfirmTextView.setEnabled(true);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                releaseUiComponent();
                break;
            }
        }
    }

    protected void onConfirm(GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
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

        setResult(RESULT_OK, intent);
        hideAnimation();
    }

    private void setSelectedDay(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView visitTextView = (TextView) view.findViewById(R.id.textView);
        visitTextView.setText(R.string.label_visit_day);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setVisibility(View.VISIBLE);

        TextView dayTextView = (TextView) view.findViewById(R.id.dateTextView);
        if ((dayTextView.getPaintFlags() & Paint.STRIKE_THRU_TEXT_FLAG) == Paint.STRIKE_THRU_TEXT_FLAG)
        {
            dayTextView.setPaintFlags(dayTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        view.setBackgroundResource(R.drawable.select_date_gourmet);
    }

    private void resetSelectedDay(View view)
    {
        if (view == null)
        {
            return;
        }

//        TextView textView = (TextView) view.findViewById(R.id.textView);
//        textView.setText(null);
//        textView.setVisibility(View.INVISIBLE);
        updateDayView(view);

        view.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));
    }

    void setSelectedDay(TodayDateTime todayDateTime, GourmetBookingDay gourmetBookingDay)
    {
        if (gourmetBookingDay == null)
        {
            return;
        }

        Calendar calendar = DailyCalendar.getInstance();

        try
        {
            int visitDay = Integer.parseInt(gourmetBookingDay.getVisitDay("yyyyMMdd"));

            for (View dayView : mDayViewList)
            {
                if (dayView == null)
                {
                    continue;
                }

                Day day = (Day) dayView.getTag();

                DailyCalendar.setCalendarDateString(calendar, todayDateTime.dailyDateTime, day.dayOffset);

                int calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));

                if (calendarDay == visitDay)
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

    void reset()
    {
        mIsChanged = true;

        if (mDayView != null)
        {
            mDayView.setSelected(false);
            resetSelectedDay(mDayView);
            mDayView = null;
        }

        for (View dayView : mDayViewList)
        {
            if (dayView == null)
            {
                continue;
            }

            dayView.setSelected(false);
            updateDayView(dayView);
        }

        setToolbarText(getString(R.string.label_calendar_gourmet_select));
        mConfirmTextView.setEnabled(false);
    }
}
