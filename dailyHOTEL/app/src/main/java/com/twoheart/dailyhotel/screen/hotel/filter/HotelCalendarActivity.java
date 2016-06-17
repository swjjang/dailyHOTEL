package com.twoheart.dailyhotel.screen.hotel.filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HotelCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

    private Day mCheckInDay;
    private Day mCheckOutDay;
    private TextView mConfirmTextView;
    private String mCallByScreen;

    private boolean mIsAnimation;
    private boolean mIsChanged;

    public static Intent newInstance(Context context, SaleTime saleTime, int nights, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, HotelCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);
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

        SaleTime saleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);
        int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 1);
        boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        mIsAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        if (saleTime == null)
        {
            Util.restartApp(this);
            return;
        }

        initLayout(R.layout.activity_calendar, saleTime.getClone(0), ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));

        if (isSelected == true)
        {
            setSelectedRangeDay(saleTime, saleTime.getClone(saleTime.getOffsetDailyDay() + nights));
        }

        if (mIsAnimation == true)
        {
            mAnimationLayout.setVisibility(View.INVISIBLE);
            mAnimationLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    showAnimation();
                }
            });
        } else
        {
            setTouchEnabled(true);
        }
    }

    @Override
    protected void initLayout(int layoutResID, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dailyTime, enableDayCountOfMax, dayCountOfMax);

        mConfirmTextView = (TextView) findViewById(R.id.confirmView);
        mConfirmTextView.setVisibility(View.VISIBLE);
        mConfirmTextView.setOnClickListener(this);
        mConfirmTextView.setEnabled(false);

        if (AnalyticsManager.ValueType.LIST.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(this, 83));
            mExitView.setLayoutParams(layoutParams);
        } else
        {
            // 문구 내용을 변경한다.
            TextView toastTextView = (TextView) mToastView.findViewById(R.id.toastTextView);

            mConfirmTextView.setText(R.string.label_calendar_search_selected_date);
            toastTextView.setText(R.string.message_calendar_search_reset);
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
        if (mIsAnimation == true)
        {
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);
        } else
        {
            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_POPPEDUP_CLOSED, AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED, null);
        }

        hideAnimation();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.exitView:
            case R.id.closeView:

                if (mIsAnimation == true)
                {
                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLOSED, mCallByScreen, null);
                } else
                {
                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                        , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_POPPEDUP_CLOSED, AnalyticsManager.Label.HOTEL_CLOSE_BUTTON_CLICKED, null);
                }

                hideAnimation();
                break;

            case R.id.cancelView:
            {
                if (mCheckInDay == null)
                {
                    return;
                }

                reset();
                break;
            }

            case R.id.confirmView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                String checkInDate = mCheckInDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");
                String checkOutDate = mCheckOutDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)");

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.CHECK_IN_DATE, Long.toString(mCheckInDay.dayTime.getDayOfDaysDate().getTime()));
                params.put(AnalyticsManager.KeyType.CHECK_OUT_DATE, Long.toString(mCheckOutDay.dayTime.getDayOfDaysDate().getTime()));
                params.put(AnalyticsManager.KeyType.LENGTH_OF_STAY, Integer.toString(mCheckOutDay.dayTime.getOffsetDailyDay() - mCheckInDay.dayTime.getOffsetDailyDay()));
                params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd(EEE) HH시 mm분", Locale.KOREA);
                String phoneDate = simpleDateFormat.format(new Date());

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.HOTEL_BOOKING_DATE_CLICKED//
                    , (mIsChanged ? AnalyticsManager.ValueType.CHANGED : AnalyticsManager.ValueType.NONE) + "-" + checkInDate + "-" + checkOutDate + "-" + phoneDate, params);

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutDay.dayTime);

                setResult(RESULT_OK, intent);
                hideAnimation();
                break;
            }

            default:
            {
                Day day = (Day) view.getTag();
                DailyTextView dailyTextView = (DailyTextView) view;

                if (day == null)
                {
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                if (mCheckInDay == null)
                {
                    mCheckInDay = day;
                    dailyTextView.setSelected(true);
                    setToolbarText(getString(R.string.label_calendar_hotel_select_checkout));
                    setRangePreviousDaysEnable(view, false);
                    mDailyTextViews[mDailyTextViews.length - 1].setEnabled(true);
                } else
                {
                    if (mCheckInDay.dayTime.getOffsetDailyDay() >= day.dayTime.getOffsetDailyDay())
                    {
                        releaseUiComponent();
                        return;
                    }

                    mCheckOutDay = day;

                    dailyTextView.setSelected(true);

                    String checkInDate = mCheckInDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String checkOutDate = mCheckOutDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String title = String.format("%s - %s(%d박)", checkInDate, checkOutDate, //
                        (mCheckOutDay.dayTime.getOffsetDailyDay() - mCheckInDay.dayTime.getOffsetDailyDay()));
                    setToolbarText(title);

                    setRangeDaysAlpha(view);
                    setRangeNextDaysEnable(view, false);
                    setCancelViewVisibility(View.VISIBLE);
                    mConfirmTextView.setEnabled(true);
                    setToastVisibility(View.VISIBLE);
                }

                releaseUiComponent();
                break;
            }
        }
    }

    private void setSelectedRangeDay(SaleTime checkInTime, SaleTime checkOutTime)
    {
        if (checkInTime == null || checkOutTime == null)
        {
            return;
        }

        for (TextView dayTextView : mDailyTextViews)
        {
            Day day = (Day) dayTextView.getTag();

            if (checkInTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayTextView.performClick();
            } else if (checkOutTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayTextView.performClick();
                break;
            }
        }
    }

    private void setRangePreviousDaysEnable(View view, boolean enable)
    {
        for (TextView textview : mDailyTextViews)
        {
            if (view == textview)
            {
                break;
            } else
            {
                textview.setEnabled(enable);
            }
        }
    }

    private void setRangeNextDaysEnable(View view, boolean enable)
    {
        boolean isStart = false;

        for (TextView textview : mDailyTextViews)
        {
            if (isStart == false)
            {
                if (view == textview)
                {
                    isStart = true;
                }
            } else
            {
                textview.setEnabled(enable);
            }
        }
    }

    private void setRangeDaysAlpha(View view)
    {
        boolean isStartPosition = false;

        for (TextView textview : mDailyTextViews)
        {
            if (isStartPosition == false)
            {
                if (textview.isSelected() == true)
                {
                    isStartPosition = true;
                }
            } else
            {
                if (view == textview)
                {
                    break;
                }

                textview.setSelected(true);
                textview.setEnabled(false);
            }
        }
    }

    private void reset()
    {
        mIsChanged = true;
        mCheckInDay = null;

        for (TextView textview : mDailyTextViews)
        {
            textview.setEnabled(true);
            textview.setSelected(false);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmTextView.setEnabled(false);

        setCancelViewVisibility(View.GONE);
        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);

        setToastVisibility(View.GONE);
    }
}
