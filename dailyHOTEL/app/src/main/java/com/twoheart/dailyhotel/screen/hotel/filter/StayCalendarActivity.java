package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyDayStrikeTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StayCalendarActivity extends PlaceCalendarActivity
{
    public static final String INTENT_EXTRA_DATA_CHECK_IN_DATE = "checkInDate";
    public static final String INTENT_EXTRA_DATA_CHECK_OUT_DATE = "checkOutDate";

    View mCheckInDayView;
    private View mCheckOutDayView;
    private TextView mConfirmTextView;

    protected boolean mIsChanged;

    private boolean mIsSingleDay;

    //    /**
    //     * @param context
    //     * @param todayDateTime
    //     * @param stayBookingDay
    //     * @param screen
    //     * @param isSelected
    //     * @param isAnimation
    //     * @return
    //     */
    //    public static Intent newInstance(Context context, TodayDateTime todayDateTime //
    //        , StayBookingDay stayBookingDay, int dayOfMaxCount, String screen, boolean isSelected, boolean isAnimation)
    //    {
    //        Intent intent = new Intent(context, StayCalendarActivity.class);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, stayBookingDay);
    //        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);
    //        intent.putExtra(INTENT_EXTRA_DATA_ISSELECTED, isSelected);
    //        intent.putExtra(INTENT_EXTRA_DATA_ANIMATION, isAnimation);
    //        intent.putExtra(INTENT_EXTRA_DATA_DAY_OF_MAXCOUNT, dayOfMaxCount);
    //
    //        return intent;
    //    }
    //
    //    public static Intent newInstance(Context context, TodayDateTime todayDateTime, String checkInDate //
    //        , String checkOutDate, int dayOfMaxCount, String screen, boolean isSelected, boolean isAnimation)
    //    {
    //        Intent intent = new Intent(context, StayCalendarActivity.class);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_TODAYDATETIME, todayDateTime);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECK_IN_DATE, checkInDate);
    //        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECK_OUT_DATE, checkOutDate);
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

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_CHECK_IN_DATE) == true)
        {
            StayBookingDay stayBookingDay = new StayBookingDay();

            try
            {
                String checkInDate = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CHECK_IN_DATE);
                stayBookingDay.setCheckInDay(checkInDate);

                if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_CHECK_OUT_DATE) == true)
                {
                    stayBookingDay.setCheckOutDay(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CHECK_OUT_DATE));
                } else
                {
                    stayBookingDay.setCheckOutDay(checkInDate, 1);
                }
            } catch (Exception e)
            {
                ExLog.e(e.getMessage());
            }

            mPlaceBookingDay = stayBookingDay;
        } else
        {
            mPlaceBookingDay = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
        }

        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        final boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        mIsSingleDay = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSINGLE_DAY, false);

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
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    makeCalendar(mTodayDateTime, getDayOfMaxCount(), false);

                    reset();

                    if (isSelected == true)
                    {
                        setSelectedRangeDay((StayBookingDay) mPlaceBookingDay);
                        checkLastDay();
                    }

                    showAnimation();

                    smoothScrollStartDayPosition(mCheckInDayView);
                }
            }, 20);
        } else
        {
            setTouchEnabled(true);

            makeCalendar(mTodayDateTime, getDayOfMaxCount(), false);

            reset();

            if (isSelected == true)
            {
                setSelectedRangeDay((StayBookingDay) mPlaceBookingDay);
                checkLastDay();
            }

            smoothScrollStartDayPosition(mCheckInDayView);
        }

        if (mIsSingleDay == true)
        {
            DailyToast.showToast(this, getString(R.string.message_calendar_select_single_day), Toast.LENGTH_SHORT);
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
                    if (mDayViewList.get(mDayViewList.size() - 1) == view || day.isSoldOut == true)
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

                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    mDayViewList.get(mDayViewList.size() - 1).setEnabled(true);

                    getAvailableCheckOutDays(mCheckInDayView);
                } else
                {
                    mCheckOutDayView = view;
                    setSelectedCheckOut(mCheckOutDayView);

                    Day checkInDay = (Day) mCheckInDayView.getTag();
                    Day checkOutDay = (Day) mCheckOutDayView.getTag();

                    view.setSelected(true);

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

                        setRangeDaysAlphaAndUpdateDayView(mCheckInDayView, mCheckOutDayView);
                        checkLastDay();
                        mConfirmTextView.setEnabled(true);
                        mConfirmTextView.setText(getConfirmText(nights));
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

        setResult(RESULT_OK, stayBookingDay);
        hideAnimation();
    }

    @Override
    protected void setResult(int resultCode, PlaceBookingDay placeBookingDay)
    {
        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, placeBookingDay);
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_IN_DATE, ((StayBookingDay) placeBookingDay).getCheckInDay(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(INTENT_EXTRA_DATA_CHECK_OUT_DATE, ((StayBookingDay) placeBookingDay).getCheckOutDay(DailyCalendar.ISO_8601_FORMAT));

        setResult(resultCode, intent);
    }

    protected String getConfirmText(int nights)
    {
        return getString(R.string.label_calendar_stay_search_selected_date, nights);
    }

    private void setSelectedCheckIn(View checkInView)
    {
        if (checkInView == null)
        {
            return;
        }

        TextView visitTextView = checkInView.findViewById(R.id.textView);
        visitTextView.setText(R.string.act_booking_chkin);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
        visitTextView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 5);
        visitTextView.setLayoutParams(visitLayoutParams);

        DailyDayStrikeTextView dayTextView = checkInView.findViewById(R.id.dateTextView);
        dayTextView.setStrikeFlag(false);

        checkInView.setBackgroundResource(R.drawable.select_date_check_in);
    }

    private void setSelectedCheckOut(View checkOutView)
    {
        if (checkOutView == null)
        {
            return;
        }

        TextView visitTextView = checkOutView.findViewById(R.id.textView);
        visitTextView.setText(R.string.act_booking_chkout);
        visitTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
        visitTextView.setTypeface(FontManager.getInstance(this).getRegularTypeface());
        visitTextView.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams visitLayoutParams = (RelativeLayout.LayoutParams) visitTextView.getLayoutParams();
        visitLayoutParams.topMargin = ScreenUtils.dpToPx(this, 5);
        visitTextView.setLayoutParams(visitLayoutParams);

        DailyDayStrikeTextView dayTextView = checkOutView.findViewById(R.id.dateTextView);
        dayTextView.setStrikeFlag(false);

        checkOutView.setBackgroundResource(R.drawable.select_date_check_out);
    }

    private void resetCheckView(View checkView)
    {
        if (checkView == null)
        {
            return;
        }

        updateDayView(checkView);

        checkView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_calendar_day_background));
    }

    void setSelectedRangeDay(StayBookingDay stayBookingDay)
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
                    // 초기 셋팅으로 서버에서 활용가능한 날짜를 받지 않고 바로 셋팅하기때문에 dayView.performClick() 을 사용하지 않음
                    mCheckInDayView = dayView;
                    setSelectedCheckIn(mCheckInDayView);

                    dayView.setSelected(true);

                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    mDayViewList.get(mDayViewList.size() - 1).setEnabled(true);

                    if (mIsSingleDay == true)
                    {
                        Calendar checkOutCalendar = (Calendar) calendar.clone();
                        checkOutCalendar.add(Calendar.DAY_OF_MONTH, 1);

                        checkOutDay = Integer.parseInt(DailyCalendar.format(checkOutCalendar.getTime(), "yyyyMMdd"));
                    }
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

    private void setRangeDaysAlphaAndUpdateDayView(View checkInDayView, View checkOutDayView)
    {
        if (mDayViewList == null || mDayViewList.size() == 0)
        {
            return;
        }

        if (checkInDayView == null || checkOutDayView == null)
        {
            return;
        }

        boolean isStartPosition = false;
        boolean isEndPosition = false;

        for (View dayView : mDayViewList)
        {
            if (checkInDayView == dayView)
            {
                dayView.setSelected(true);
                isStartPosition = true;
                continue;
            }

            if (checkOutDayView == dayView)
            {
                dayView.setSelected(true);
                isEndPosition = true;
                continue;
            }

            if (isStartPosition == true && isEndPosition == false)
            {
                dayView.setSelected(true);
                dayView.setActivated(true);
            } else
            {
                updateDayView(dayView);
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

    void getAvailableCheckOutDays(View checkInDayView)
    {
        // do nothing - stayDetailCalendar 용
    }

    void setAvailableCheckOutDays(View checkInDayView, ArrayList<Integer> availableDayList)
    {
        if (checkInDayView == null)
        {
            checkInDayView = mCheckInDayView;
        }

        if (availableDayList == null || availableDayList.size() == 0)
        {
            // 이때는 바로 다음 날짜를 강제로 선택 후 판매 완료 다른 날짜 선택 팝업을 띄우기로 함
            if (checkInDayView == null || mDayViewList == null || mDayViewList.size() == 0)
            {
                // 진짜 방법 없음 이럴땐 달력 닫아버림
                if (mExitView == null)
                {
                    finish();
                    return;
                }

                mExitView.performClick();
                return;
            }

            Day checkInDay = (Day) checkInDayView.getTag();
            checkInDay.isSoldOut = true;

            int index = mDayViewList.indexOf(checkInDayView);
            if (index == -1)
            {
                if (mExitView == null)
                {
                    finish();
                    return;
                }

                mExitView.performClick();
                return;
            }

            if (index + 1 < mDayViewList.size())
            {
                mDayViewList.get(index + 1).performClick();
            }

            showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                , getString(R.string.label_changing_date), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // do nothing!
                    }
                });
            return;
        }

        if (mIsSingleDay == true)
        {
            int checkOutPosition = mDayViewList.indexOf(checkInDayView) + 1;
            View checkOutDayView = mDayViewList.get(checkOutPosition);

            checkOutDayView.performClick();
            return;
        }

        Calendar calendar = DailyCalendar.getInstance();

        Day checkInDay = (Day) checkInDayView.getTag();

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
                    // 체크인 날짜는 활성화 되어있음으로 처리 하지 않음
                    continue;
                }

                Day day = (Day) dayView.getTag();
                DailyCalendar.setCalendarDateString(calendar, day.dateTime);

                boolean isAvailable;

                int compareValue = DailyCalendar.compareDateDay(checkInDay.dateTime, day.dateTime);
                if (compareValue >= 0)
                {
                    // 체크인 날짜 이전은 활성화!
                    isAvailable = day.isSoldOut == false;
                } else
                {
                    isAvailable = isAvailableDay(calendar, availableDayList);
                }

                // updateDayView 를 사용하지 말 것. 호출시 다 활성화 됨
                updateAvailableDayView(dayView, isAvailable);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    protected void updateAvailableDayView(View dayView, boolean isAvailable)
    {
        if (dayView == null)
        {
            return;
        }

        Day day = (Day) dayView.getTag();
        if (day == null)
        {
            dayView.setEnabled(false);
            return;
        }

        setDayOfWeekTextColor(dayView);

        if (dayView.isSelected() == true)
        {
            return;
        }

        dayView.setEnabled(isAvailable);
        // 기존에 설정된 SoldOutView 를 선택 가능한 날짜 일 경우 default 설정으로 변경
        setSoldOutTextView(dayView, isAvailable == false && day.isSoldOut);

    }

    private boolean isAvailableDay(Calendar calendar, ArrayList<Integer> availableDayList)
    {
        if (calendar == null)
        {
            return false;
        }

        if (availableDayList == null || availableDayList.size() == 0)
        {
            return false;
        }

        Integer calendarDay = Integer.parseInt(DailyCalendar.format(calendar.getTime(), "yyyyMMdd"));
        return availableDayList.remove(calendarDay);
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
            updateDayView(dayView);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);
    }
}
