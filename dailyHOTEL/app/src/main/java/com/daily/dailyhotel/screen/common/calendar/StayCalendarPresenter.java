package com.daily.dailyhotel.screen.common.calendar;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyPreference;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayCalendarPresenter extends PlaceCalendarPresenter<StayCalendarActivity, StayCalendarViewInterface> implements StayCalendarView.OnEventListener
{
    private StayCalendarPresenterAnalyticsInterface mAnalytics;

    private StayBookDateTime mStayBookDateTime;

    private String mStartDateTime;
    private String mEndDateTime;
    private int mCheckDaysCount;

    private String mCallByScreen;
    private boolean mIsSelected;
    private boolean mIsAnimation;

    public interface StayCalendarPresenterAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayCalendarPresenter(@NonNull StayCalendarActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayCalendarViewInterface createInstanceViewInterface()
    {
        return new StayCalendarView(getActivity(), this);
    }

    @Override
    public void initialize(StayCalendarActivity activity)
    {
        super.initialize(activity);

        setContentView(R.layout.activity_calendar_data);

        getViewInterface().setVisibility(false);

        setAnalytics(new StayCalendarAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayCalendarPresenterAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        try
        {
            String checkInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
            String checkOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

            mStayBookDateTime = new StayBookDateTime();
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);

            mStartDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_START_DATETIME);
            mEndDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_END_DATETIME);
            mCheckDaysCount = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_DAYS_COUNT, -1);

            mCallByScreen = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CALLBYSCREEN);
            mIsSelected = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_ISSELECTED, true);
            mIsAnimation = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_ISANIMATION, false);

        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {
        screenLock(false);

        addCompositeDisposable(Observable.empty().subscribeOn(Schedulers.io()).just(DailyPreference.getInstance(getActivity()).getCalendarHolidays())//
            .map(new Function<String, ArrayList<Pair<String, Day[]>>>()
            {
                @Override
                public ArrayList<Pair<String, Day[]>> apply(String calendarHolidays) throws Exception
                {
                    int[] holidays = null;

                    if (DailyTextUtils.isTextEmpty(calendarHolidays) == false)
                    {
                        String[] holidaysSplit = calendarHolidays.split("\\,");
                        holidays = new int[holidaysSplit.length];

                        for (int i = 0; i < holidaysSplit.length; i++)
                        {
                            try
                            {
                                holidays[i] = Integer.parseInt(holidaysSplit[i]);
                            } catch (NumberFormatException e)
                            {
                                ExLog.e(e.toString());
                            }
                        }
                    }

                    return makeCalendar(mStartDateTime, mEndDateTime, holidays);
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Pair<String, Day[]>>>()
            {
                @Override
                public void accept(ArrayList<Pair<String, Day[]>> arrayList) throws Exception
                {
                    getViewInterface().makeCalendarView(arrayList);

                    if (mIsAnimation == true)
                    {
                        getViewInterface().showAnimation();
                    } else
                    {

                    }

                    if (mIsSelected == true)
                    {

                    }
                }
            }));

//        addCompositeDisposable(Observable.just(DailyPreference.getInstance(getActivity()).getCalendarHolidays())//
//            .subscribeOn(Schedulers.io()).map(new Function<String, ArrayList<Pair<String, Day[]>>>()
//            {
//                @Override
//                public ArrayList<Pair<String, Day[]>> apply(String calendarHolidays) throws Exception
//                {
//                    int[] holidays = null;
//
//                    if (DailyTextUtils.isTextEmpty(calendarHolidays) == false)
//                    {
//                        String[] holidaysSplit = calendarHolidays.split("\\,");
//                        holidays = new int[holidaysSplit.length];
//
//                        for (int i = 0; i < holidaysSplit.length; i++)
//                        {
//                            try
//                            {
//                                holidays[i] = Integer.parseInt(holidaysSplit[i]);
//                            } catch (NumberFormatException e)
//                            {
//                                ExLog.e(e.toString());
//                            }
//                        }
//                    }
//
//                    return makeCalendar(mStartDateTime, mEndDateTime, holidays);
//                }
//            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Pair<String, Day[]>>>()
//            {
//                @Override
//                public void accept(ArrayList<Pair<String, Day[]>> arrayList) throws Exception
//                {
//                    getViewInterface().makeCalendarView(arrayList);
//
//                    if (mIsAnimation == true)
//                    {
//                        getViewInterface().showAnimation();
//                    } else
//                    {
//
//                    }
//
//                    if (mIsSelected == true)
//                    {
//
//                    }
//                }
//            }));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    /**
     * 이 메소드는 activity에서 onBackPressed가 호출되면 호출되는 메소드로
     * 해당 메소드를 호출한다고 해서 종료되지 않음.
     * @return
     */
    @Override
    public boolean onBackPressed()
    {
        getViewInterface().hideAnimation();

        return true;
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }
    }

    @Override
    public void onShowAnimationEnd()
    {
        unLockAll();
    }

    @Override
    public void onHideAnimationEnd()
    {
        getActivity().finish();
    }
}
