package com.twoheart.dailyhotel.screen.gourmet.list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.place.activity.PlaceCalendarActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyTextView;

import java.util.HashMap;
import java.util.Map;

public class GourmetCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 30;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 14;

    private String mCallByScreen;

    public static Intent newInstance(Context context, SaleTime dailyTime, String screen)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
        intent.putExtra(INTENT_EXTRA_DATA_SCREEN, screen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime dailyTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME);
        mCallByScreen = intent.getStringExtra(INTENT_EXTRA_DATA_SCREEN);

        initLayout(R.layout.activity_calendar, dailyTime, ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_gourmet_select));
    }

    @Override
    protected void initLayout(int layoutResID, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(layoutResID, dailyTime, enableDayCountOfMax, dayCountOfMax);

        View confirmView = findViewById(R.id.confirmView);
        confirmView.setVisibility(View.GONE);
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

        String date = day.dayTime.getDayOfDaysDateFormat("yyyyMMdd");

        Map<String, String> params = new HashMap<>();
        params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(day.dayTime.getDayOfDaysDate().getTime()));
        params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED, date, params);

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, day.dayTime);

        setResult(RESULT_OK, intent);
        finish();
    }
}
