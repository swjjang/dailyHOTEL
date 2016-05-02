package com.twoheart.dailyhotel.screen.hotel.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

public class HotelCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 60;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 60;

    private Day mCheckInDay;
    private Day mCheckOutDay;
    private View mConfirmView;

    public static Intent newInstance(Context context, SaleTime dailyTime)
    {
        Intent intent = new Intent(context, HotelCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime dailyTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME);

        initLayout(HotelCalendarActivity.this, dailyTime, ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_hotel_select_checkin));
    }

    @Override
    protected void initLayout(Context context, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(context, dailyTime, enableDayCountOfMax, dayCountOfMax);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setVisibility(View.VISIBLE);
        mConfirmView.setOnClickListener(this);
        mConfirmView.setEnabled(false);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYHOTEL_LIST_CALENDAR, null);

        super.onStart();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.confirmView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mCheckInDay.dayTime);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKOUTDATE, mCheckOutDay.dayTime);

                setResult(RESULT_OK, intent);
                finish();
                break;
            }

            case R.id.cancelView:
            {
                if (mCheckInDay == null)
                {
                    return;
                }

                reset();
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
                    //                    if (mCheckInDay.dayTime == day.dayTime)
                    //                    {
                    //                        mCheckInDay = null;
                    //                        view.setSelected(false);
                    //                        dailyTextView.setTypeface(dailyTextView.getTypeface(), Typeface.NORMAL);
                    //
                    //                        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
                    //                        setRangePreviousDaysEnable(view, true);
                    //                        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);
                    //                        releaseUiComponent();
                    //                        return;
                    //                    }

                    if (mCheckInDay.dayTime.getOffsetDailyDay() >= day.dayTime.getOffsetDailyDay())
                    {
                        releaseUiComponent();
                        return;
                    }

                    mCheckOutDay = day;

                    dailyTextView.setSelected(true);

                    String checkInDate = mCheckInDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String checkOutDate = mCheckOutDay.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd");
                    String title = String.format("%s-%s(%d박)", checkInDate, checkOutDate, (mCheckOutDay.dayTime.getOffsetDailyDay() - mCheckInDay.dayTime.getOffsetDailyDay()));
                    setToolbarText(title);

                    setRangeDaysAlpha(view);
                    setRangeNextDaysEnable(view, false);
                    setCancelViewVisibility(View.VISIBLE);
                    mConfirmView.setEnabled(true);
                    setToastVisibility(View.VISIBLE);
                }

                releaseUiComponent();
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
        mCheckInDay = null;

        for (TextView textview : mDailyTextViews)
        {
            textview.setEnabled(true);
            textview.setSelected(false);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmView.setEnabled(false);

        setCancelViewVisibility(View.GONE);
        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);

        setToastVisibility(View.GONE);
    }
}
