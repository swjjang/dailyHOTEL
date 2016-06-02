package com.twoheart.dailyhotel.screen.gourmet.search;

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

import java.util.HashMap;
import java.util.Map;

public class GourmetSearchCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 30;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 14;

    private String mCallByScreen;
    private View mConfirmView;

    public static Intent newInstance(Context context, SaleTime saleTime, String screen)
    {
        Intent intent = new Intent(context, GourmetSearchCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime saleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);

        initLayout(R.layout.activity_calendar, saleTime.getClone(0), ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_gourmet_select));

        setSelectedDay(saleTime);
    }

    @Override
    protected void initLayout(int layoutResID, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dailyTime, enableDayCountOfMax, dayCountOfMax);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setVisibility(View.VISIBLE);
        mConfirmView.setOnClickListener(this);
        mConfirmView.setEnabled(false);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_CALENDAR);

        super.onStart();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.confirmView:
            {
                finish();
                break;
            }

            case R.id.cancelView:
            {
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

                if (isLockUiComponent() == true)
                {
                    return;
                }

                dailyTextView.setSelected(true);

                lockUiComponent();

                selectedAndFinish(day.dayTime);
                break;
            }
        }
    }

    private void selectedAndFinish(SaleTime saleTime)
    {
        String date = saleTime.getDayOfDaysDateFormat("yyyyMMdd");

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(saleTime.getDayOfDaysDate().getTime()));
        params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED, date, params);

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, saleTime);

        setResult(RESULT_OK, intent);
        finish();
    }

    private void setSelectedDay(SaleTime saleTime)
    {
        if (saleTime == null)
        {
            return;
        }

        for (TextView dayTextView : mDailyTextViews)
        {
            Day day = (Day) dayTextView.getTag();

            if (saleTime.isDayOfDaysDateEquals(day.dayTime) == true)
            {
                dayTextView.setSelected(true);

                //                setRangeDaysAlpha(dayTextView);
                setDaysEnable(dayTextView, false);
                break;
            }
        }

        setToolbarText(saleTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)"));

        setCancelViewVisibility(View.VISIBLE);
        mConfirmView.setEnabled(true);
        setToastVisibility(View.VISIBLE);
    }

    private void setDaysEnable(View view, boolean enable)
    {
        for (TextView textview : mDailyTextViews)
        {
            if (view != textview)
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
        for (TextView textview : mDailyTextViews)
        {
            textview.setEnabled(true);
            textview.setSelected(false);
        }

        setToolbarText(getString(R.string.label_calendar_hotel_select_checkin));
        mConfirmView.setVisibility(View.GONE);

        setCancelViewVisibility(View.GONE);
        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);

        setToastVisibility(View.GONE);
    }
}
