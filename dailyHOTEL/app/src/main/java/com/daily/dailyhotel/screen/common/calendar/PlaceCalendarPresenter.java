package com.daily.dailyhotel.screen.common.calendar;


import android.support.annotation.NonNull;
import android.view.Window;
import android.view.WindowManager;

import com.daily.base.BaseActivity;
import com.daily.base.BaseViewInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyPreference;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public abstract class PlaceCalendarPresenter<T1 extends BaseActivity, T2 extends BaseViewInterface> extends BaseExceptionPresenter<T1, T2>
{
    private int[] mHolidays;

    public PlaceCalendarPresenter(@NonNull T1 activity)
    {
        super(activity);
    }

    @Override
    public void initialize(T1 activity)
    {
        if (VersionUtils.isOverAPI21() == true)
        {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.black_a67));
        }

        addCompositeDisposable(Observable.empty().subscribeOn(Schedulers.io()).subscribe(new Consumer<Object>()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                // 휴일 정보를 얻어온다.
                String calendarHolidays = DailyPreference.getInstance(getActivity()).getCalendarHolidays();

                if (DailyTextUtils.isTextEmpty(calendarHolidays) == false)
                {
                    String[] holidays = calendarHolidays.split("\\,");
                    mHolidays = new int[holidays.length];

                    for (int i = 0; i < holidays.length; i++)
                    {
                        try
                        {
                            mHolidays[i] = Integer.parseInt(holidays[i]);
                        } catch (NumberFormatException e)
                        {
                            ExLog.e(e.toString());
                        }
                    }
                }
            }
        }));
    }

    protected static class Day
    {
        public static final String DATE_FORMAT = "yyyyMMdd";

        public int dayOffset;
        String dayOfMonth;
        String dateFormat; // yyyyMMdd
        int dayOfWeek;
    }
}
