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

public class GourmetCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 30;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 14;

    public static Intent newInstance(Context context, SaleTime dailyTime)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        SaleTime dailyTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME);

        initLayout(GourmetCalendarActivity.this, dailyTime, ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_gourmet_select));
    }

    @Override
    protected void initLayout(Context context, SaleTime dailyTime, int enableDayCountOfMax, int dayCountOfMax)
    {
        super.initLayout(context, dailyTime, enableDayCountOfMax, dayCountOfMax);

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

        Intent intent = new Intent();
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, day.dayTime);

        setResult(RESULT_OK, intent);
        finish();
    }
}
