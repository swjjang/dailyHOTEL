package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StayCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

    private View mCheckInDayView;
    private View mCheckOutDayView;
    private TextView mConfirmTextView;

    protected boolean mIsChanged;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String screen, boolean isSelected, boolean isAnimation)
    {
        SaleTime startSaleTime = saleTime.getClone(0);

        return newInstance(context, saleTime, nights, startSaleTime, null, screen, isSelected, isAnimation);
    }

    /**
     * @param context
     * @param saleTime
     * @param nigths
     * @param startSaleTime
     * @param endSaleTime   null인 경우 마지막 날짜로 한다.
     * @param screen
     * @param isSelected
     * @param isAnimation
     * @return
     */
    public static Intent newInstance(Context context, SaleTime saleTime, int nights, SaleTime startSaleTime, SaleTime endSaleTime, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, StayCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
        intent.putExtra(INTENT_EXTRA_DATA_START_SALETIME, startSaleTime);
        intent.putExtra(INTENT_EXTRA_DATA_END_SALETIME, endSaleTime);
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

        final SaleTime saleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 1);
        final boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        mStartSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_START_SALETIME);
        mEndSaleTime = intent.getParcelableExtra(INTENT_EXTRA_DATA_END_SALETIME);

        if (mEndSaleTime == null)
        {
            mEndSaleTime = mStartSaleTime.getClone(ENABLE_DAYCOUNT_OF_MAX - 1);
        } else if (mEndSaleTime.getOffsetDailyDay() > ENABLE_DAYCOUNT_OF_MAX)
        {
            mEndSaleTime.setOffsetDailyDay(ENABLE_DAYCOUNT_OF_MAX - 1);
        }

        if (saleTime == null || mStartSaleTime == null)
        {
            Util.restartApp(this);
            return;
        }

        // startSaleTime, endSaleTime 안에 기간을 요청해야 한다
        // 만일 그렇지 않으면 강제로 start, end time안으로 변경한다.

        int checkInOffsetDailyDay = saleTime.getOffsetDailyDay();
        int checkOutOffsetDailyDay = checkInOffsetDailyDay + nights;

        if (checkInOffsetDailyDay < mStartSaleTime.getOffsetDailyDay()//
            || checkInOffsetDailyDay >= mEndSaleTime.getOffsetDailyDay()//
            || checkOutOffsetDailyDay >= mEndSaleTime.getOffsetDailyDay())
        {
            saleTime.setOffsetDailyDay(mStartSaleTime.getOffsetDailyDay());
            nights = 1;
        }

        final SaleTime checkOutSaleTime = saleTime.getClone(saleTime.getOffsetDailyDay() + nights);

        initLayout(R.layout.activity_calendar, saleTime.getClone(0), DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    makeCalendar(saleTime.getClone(0), DAYCOUNT_OF_MAX);

                    reset();

                    if (isSelected == true)
                    {
                        setSelectedRangeDay(saleTime, checkOutSaleTime);
                        checkLastDay();
                    }

                    showAnimation();
                }
            }, 20);
        } else
        {
            setTouchEnabled(true);

            makeCalendar(saleTime.getClone(0), DAYCOUNT_OF_MAX);

            reset();

            if (isSelected == true)
            {
                setSelectedRangeDay(saleTime, checkOutSaleTime);
                checkLastDay();
            }
        }
    }

    @Override
    protected void initLayout(int layoutResID, SaleTime dailyTime, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dailyTime, dayCountOfMax);

        mConfirmTextView = (TextView) findViewById(R.id.confirmView);
        mConfirmTextView.setVisibility(View.VISIBLE);
        mConfirmTextView.setOnClickListener(this);
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);

        if (AnalyticsManager.ValueType.SEARCH.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(this, 133));
            mExitView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR);

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
        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
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

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
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

                onConfirm(checkInDay.dayTime, checkOutDay.dayTime);
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
                    if (mDailyViews[mEndSaleTime.getOffsetDailyDay()] == view)
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

                    if (currentCheckInDay.dayTime.getOffsetDailyDay() >= day.dayTime.getOffsetDailyDay())
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
                    mDailyViews[mEndSaleTime.getOffsetDailyDay()].setEnabled(true);
                } else
                {
                    mCheckOutDayView = view;
                    setSelectedCheckOut(mCheckOutDayView);

                    Day checkInDay = (Day) mCheckInDayView.getTag();
                    Day checkOutDay = (Day) mCheckOutDayView.getTag();
                    int nights = checkOutDay.dayTime.getOffsetDailyDay() - checkInDay.dayTime.getOffsetDailyDay();

                    view.setSelected(true);

                    String checkInDate = checkInDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
                    String checkOutDate = checkOutDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
                    String title = String.format("%s - %s, %d박", checkInDate, checkOutDate, nights);
                    setToolbarText(title);

                    setRangeDaysAlpha(mCheckOutDayView);
                    checkLastDay();
                    mConfirmTextView.setEnabled(true);
                    mConfirmTextView.setText(getString(R.string.label_calendar_stay_search_selected_date, nights));
                }

                releaseUiComponent();
                break;
            }
        }
    }

    protected void onConfirm(SaleTime checkInSaleTime, SaleTime chekcOutSaleTime)
    {
        if (checkInSaleTime == null || chekcOutSaleTime == null)
        {
            return;
        }

        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        String checkInDate = checkInSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
        String checkOutDate = chekcOutSaleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(checkInSaleTime.getDayOfDaysDate().getTime()));
        params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, Long.toString(chekcOutSaleTime.getDayOfDaysDate().getTime()));
        params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(chekcOutSaleTime.getOffsetDailyDay() - checkInSaleTime.getOffsetDailyDay()));
        params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

        //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분", Locale.KOREA);
        //        String phoneDate = simpleDateFormat.format(new Date());
        String phoneDate = DailyCalendar.format(new Date(), "yyyy.MM.dd(EEE) HH시 mm분");

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED//
            , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE) + "-" + checkInDate + "-" + checkOutDate + "-" + phoneDate, params);

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, checkInSaleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, chekcOutSaleTime);

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

    private void setSelectedRangeDay(SaleTime checkInTime, SaleTime checkOutTime)
    {
        if (checkInTime == null || checkOutTime == null)
        {
            return;
        }

        for (View dayView : mDailyViews)
        {
            if (dayView == null)
            {
                continue;
            }

            Day day = (Day) dayView.getTag();

            if (checkInTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayView.performClick();
            } else if (checkOutTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayView.performClick();
                break;
            }
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
    private void checkLastDay()
    {
        if (mCheckInDayView == null || mCheckOutDayView == null)
        {
            return;
        }

        if (mDailyViews[mEndSaleTime.getOffsetDailyDay()] == mCheckOutDayView)
        {
            return;
        }

        mDailyViews[mEndSaleTime.getOffsetDailyDay()].setEnabled(false);
    }

    private void reset()
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

            Object tag = dayView.getTag();

            dayView.setActivated(false);
            dayView.setSelected(false);

            if (tag != null && tag instanceof Day)
            {
                Day day = (Day) tag;

                int offsetDay = day.dayTime.getOffsetDailyDay();

                if (offsetDay >= mStartSaleTime.getOffsetDailyDay()//
                    && offsetDay < mEndSaleTime.getOffsetDailyDay())
                {
                    dayView.setEnabled(true);
                } else
                {
                    dayView.setEnabled(false);
                }
            } else
            {
                dayView.setEnabled(false);
            }
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);
        mConfirmTextView.setText(R.string.label_calendar_search_selected_date);
    }
}
