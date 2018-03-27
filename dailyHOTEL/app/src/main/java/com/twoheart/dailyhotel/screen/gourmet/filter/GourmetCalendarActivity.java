package com.twoheart.dailyhotel.screen.gourmet.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyDayStrikeTextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class GourmetCalendarActivity extends PlaceCalendarActivity
{
    public static final int DEFAULT_CALENDAR_DAY_OF_MAX_COUNT = 30;

    public static final String INTENT_EXTRA_DATA_VISIT_DATE = "visitDate";

    View mDayView;
    private TextView mConfirmTextView;

    protected boolean mIsChanged;

//    /**
//     * @param context
//     * @param todayDateTime
//     * @param gourmetBookingDay
//     * @param screen
//     * @param isSelected
//     * @param isAnimation
//     * @return
//     */
//    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
//        , GourmetBookingDay gourmetBookingDay, int dayOfMaxCount, String screen //
//        , boolean isSelected, boolean isAnimation)
//    {
//        Intent intent = new Intent(context, GourmetCalendarActivity.class);
//        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
//        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, gourmetBookingDay);
//        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
//        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
//        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
//        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
//
//        return intent;
//    }
//
//    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
//        , String visitDate, int dayOfMaxCount, String screen //
//        , boolean isSelected, boolean isAnimation)
//    {
//        Intent intent = new Intent(context, GourmetCalendarActivity.class);
//        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
//        intent.putExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE, visitDate);
//        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
//        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
//        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
//        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
//
//        return intent;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        mTodayDateTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME);

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE) == true)
        {
            GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

            try
            {
                String visitDate = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_VISIT_DATE);
                gourmetBookingDay.setVisitDay(visitDate);
            } catch (Exception e)
            {
                ExLog.e(e.getMessage());
            }

            mPlaceBookingDay = gourmetBookingDay;
        } else
        {
            mPlaceBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        }

        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        final boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        if (intent.hasExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST) == true)
        {
            mSoldOutDayList = intent.getIntegerArrayListExtra(INTENT_EXTRA_DATA_SOLDOUT_LIST);
        }

        setDayOfMaxCount(intent.getIntExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, 0));

        if (mTodayDateTime == null || mPlaceBookingDay == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout(R.layout.activity_calendar, getDayOfMaxCount());
        initToolbar(getString(R.string.label_calendar_gourmet_select));

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    makeCalendar(mTodayDateTime, getDayOfMaxCount(), true);

                    reset();

                    if (isSelected == true)
                    {
                        setSelectedDay(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay);
                    }

                    showAnimation();

                    smoothScrollStartDayPosition(mDayView);
                }
            }, 20);
        } else
        {
            setTouchEnabled(true);

            makeCalendar(mTodayDateTime, getDayOfMaxCount(), true);

            reset();

            if (isSelected == true)
            {
                setSelectedDay(mTodayDateTime, (GourmetBookingDay) mPlaceBookingDay);
            }

            smoothScrollStartDayPosition(mDayView);
        }
    }

    @Override
    protected void initLayout(int layoutResID, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dayCountOfMax);

        mConfirmTextView = findViewById(R.id.confirmView);
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
                    gourmetBookingDay.setVisitDay(day.dateTime);

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

                try
                {
                    String visitDate = DailyCalendar.convertDateFormatString(day.dateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
                    setToolbarText(visitDate);

                    mConfirmTextView.setEnabled(true);
                    mConfirmTextView.setText(getConfirmTextResId());
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

        setResult(RESULT_OK, gourmetBookingDay);
        hideAnimation();
    }

    @Override
    protected void setResult(int resultCode, PlaceBookingDay placeBookingDay)
    {
        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_VISIT_DATE, ((GourmetBookingDay) placeBookingDay).getVisitDay(DailyCalendar.ISO_8601_FORMAT));

        setResult(resultCode, intent);
    }

    protected int getConfirmTextResId()
    {
        return R.string.label_calendar_gourmet_search_selected_date;
    }

    private void setSelectedDay(View view)
    {
        if (view == null)
        {
            return;
        }

        TextView visitTextView = view.findViewById(R.id.textView);
        visitTextView.setText(R.string.label_visit_day);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
        visitTextView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 5);
        visitTextView.setLayoutParams(visitLayoutParams);

        DailyDayStrikeTextView dayTextView = view.findViewById(R.id.dateTextView);
        dayTextView.setStrikeFlag(false);

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

        final String DATE_FORMAT = "yyyyMMdd";

        try
        {
            int visitDay = Integer.parseInt(gourmetBookingDay.getVisitDay(DATE_FORMAT));

            for (View dayView : mDayViewList)
            {
                if (dayView == null)
                {
                    continue;
                }

                Day day = (Day) dayView.getTag();

                int calendarDay = Integer.parseInt(DailyCalendar.convertDateFormatString(day.dateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT));

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
