package com.twoheart.dailyhotel.screen.gourmet.filter;

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

import java.util.HashMap;
import java.util.Map;

public class GourmetCalendarActivity extends PlaceCalendarActivity
{
    private static final int DAYCOUNT_OF_MAX = 30;
    private static final int ENABLE_DAYCOUNT_OF_MAX = 14;

    private Day mDay;
    private String mCallByScreen;
    private View mConfirmView;

    public static Intent newInstance(Context context, SaleTime saleTime, String screen, boolean isSelected, boolean isAnimation)
    {
        Intent intent = new Intent(context, GourmetCalendarActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);
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
        boolean isSelected = intent.getBooleanExtra(INTENT_EXTRA_DATA_ISSELECTED, true);
        boolean isAnimation = intent.getBooleanExtra(INTENT_EXTRA_DATA_ANIMATION, false);

        initLayout(R.layout.activity_calendar, saleTime.getClone(0), ENABLE_DAYCOUNT_OF_MAX, DAYCOUNT_OF_MAX);
        initToolbar(getString(R.string.label_calendar_gourmet_select));

        if (isSelected == true)
        {
            setSelectedDay(saleTime);
        }

        if (isAnimation == true)
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

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setVisibility(View.VISIBLE);
        mConfirmView.setOnClickListener(this);
        mConfirmView.setEnabled(false);

        if (AnalyticsManager.ValueType.LIST.equalsIgnoreCase(mCallByScreen) == true)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(this, 83));
            mExitView.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.DAILYGOURMET_LIST_CALENDAR);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.exitView:
            case R.id.closeView:
                hideAnimation();
                break;

            case R.id.cancelView:
            {
                reset();
                break;
            }

            case R.id.confirmView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                String date = mDay.dayTime.getDayOfDaysDateFormat("yyyyMMdd");

                Map<String, String> params = new HashMap<>();
                params.put(AnalyticsManager.KeyType.VISIT_DATE, Long.toString(mDay.dayTime.getDayOfDaysDate().getTime()));
                params.put(AnalyticsManager.KeyType.SCREEN, mCallByScreen);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.GOURMET_BOOKING_DATE_CLICKED, date, params);

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_CHECKINDATE, mDay.dayTime);

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

                mDay = day;

                dailyTextView.setSelected(true);
                setToolbarText(day.dayTime.getDayOfDaysDateFormat("yyyy.MM.dd(EEE)"));

                setDaysEnable(view, false);
                setCancelViewVisibility(View.VISIBLE);
                mConfirmView.setEnabled(true);
                setToastVisibility(View.VISIBLE);

                releaseUiComponent();
                break;
            }
        }
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
                dayTextView.performClick();
                break;
            }
        }
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

    private void reset()
    {
        int lenght = mDailyTextViews.length;

        for (int i = 0; i < lenght; i++)
        {
            if (i < ENABLE_DAYCOUNT_OF_MAX)
            {
                mDailyTextViews[i].setEnabled(true);
            } else
            {
                mDailyTextViews[i].setEnabled(false);
            }

            mDailyTextViews[i].setSelected(false);
        }

        setToolbarText(getString(R.string.label_calendar_gourmet_select));
        mConfirmView.setEnabled(false);

        setCancelViewVisibility(View.GONE);
        mDailyTextViews[mDailyTextViews.length - 1].setEnabled(false);

        setToastVisibility(View.GONE);
    }
}
