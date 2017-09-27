package com.daily.dailyhotel.screen.common.calendar;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.util.SparseIntArray;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.daily.dailyhotel.storage.preference.DailyPreference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
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

    String mCheckInDateTime;
    String mCheckOutDateTime;

    String mStartDateTime;
    String mEndDateTime;
    int mNightsOfMaxCount;

    private String mCallByScreen;
    boolean mIsSelected;
    private int mMarginTop;
    boolean mIsAnimation;

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
    public void constructorInitialize(StayCalendarActivity activity)
    {
        super.constructorInitialize(activity);

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
            mCheckInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
            mCheckOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

            mStartDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_START_DATETIME);
            mEndDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_END_DATETIME);
            mNightsOfMaxCount = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_NIGHTS_OF_MAXCOUNT, 1);

            mCallByScreen = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CALLBYSCREEN);
            mIsSelected = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_ISSELECTED, true);
            mMarginTop = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_MARGIN_TOP, 0);
            mIsAnimation = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_ISANIMATION, false);

        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        screenLock(false);

        getViewInterface().setMarginTop(mMarginTop);

        addCompositeDisposable(Observable.defer(new Callable<ObservableSource<String>>()
        {
            @Override
            public ObservableSource<String> call() throws Exception
            {
                return Observable.just(DailyPreference.getInstance(getActivity()).getCalendarHolidays());
            }
        }).subscribeOn(Schedulers.io()).map(new Function<String, ArrayList<Pair<String, Day[]>>>()
        {
            @Override
            public ArrayList<Pair<String, Day[]>> apply(String calendarHolidays) throws Exception
            {
                SparseIntArray holidaySparseIntArray = null;

                if (DailyTextUtils.isTextEmpty(calendarHolidays) == false)
                {
                    String[] holidaysSplit = calendarHolidays.split("\\,");
                    holidaySparseIntArray = new SparseIntArray(holidaysSplit.length);

                    for (int i = 0; i < holidaysSplit.length; i++)
                    {
                        try
                        {
                            int holiday = Integer.parseInt(holidaysSplit[i]);
                            holidaySparseIntArray.put(holiday, holiday);
                        } catch (NumberFormatException e)
                        {
                            ExLog.e(e.toString());
                        }
                    }
                }

                return makeCalendar(mStartDateTime, mEndDateTime, holidaySparseIntArray);
            }
        }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<ArrayList<Pair<String, Day[]>>, Observable<Boolean>>()
        {
            @Override
            public Observable<Boolean> apply(@io.reactivex.annotations.NonNull ArrayList<Pair<String, Day[]>> arrayList) throws Exception
            {
                getViewInterface().makeCalendarView(arrayList);

                if (mIsAnimation == true)
                {
                    Observable<Boolean> observable = getViewInterface().showAnimation();

                    if (observable != null)
                    {
                        screenLock(false);

                        return observable;
                    }
                } else
                {
                    getViewInterface().setVisibility(true);
                }

                return new Observable<Boolean>()
                {
                    @Override
                    protected void subscribeActual(Observer<? super Boolean> observer)
                    {
                        observer.onNext(true);
                        observer.onComplete();
                    }
                };
            }
        }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
            {
                unLockAll();

                if (mIsSelected == true)
                {
                    String checkInDateTime = mCheckInDateTime;
                    String checkOutDateTime = mCheckOutDateTime;

                    mCheckInDateTime = mCheckOutDateTime = null;

                    getViewInterface().clickDay(checkInDateTime);

                    if (mNightsOfMaxCount > 1)
                    {
                        getViewInterface().clickDay(checkOutDateTime);
                    }

                    View checkInDayView = getViewInterface().searchDayView(checkInDateTime);
                    getViewInterface().smoothScrollStartDayPosition(checkInDayView);
                }
            }
        }));
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
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
     *
     * @return
     */
    @Override
    public boolean onBackPressed()
    {
        Observable<Boolean> observable = getViewInterface().hideAnimation();

        if (observable != null)
        {
            screenLock(false);

            addCompositeDisposable(observable.subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    getActivity().finish();
                }
            }));
        } else
        {
            getActivity().finish();
        }

        return true;
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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onDayClick(View view)
    {
        Day day = (Day) view.getTag();

        if (day == null)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        try
        {
            // 이미 체크인 체크아웃이 선택되어있으면 초기화
            if (DailyTextUtils.isTextEmpty(mCheckInDateTime, mCheckOutDateTime) == false)
            {
                // 체크인 체크아웃이 되어있는데 마지막 날짜를 체크인할때
                if (mEndDateTime.equalsIgnoreCase(day.dateTime) == true)
                {
                    DailyToast.showToast(getActivity(), getString(R.string.label_message_dont_check_date), DailyToast.LENGTH_SHORT);
                    return;
                } else
                {
                    reset();
                }
            }

            // 기존의 날짜 보다 전날짜를 선택하면 초기화.
            if (DailyTextUtils.isTextEmpty(mCheckInDateTime) == false)
            {
                int compareDay = DailyCalendar.compareDateDay(mCheckInDateTime, day.dateTime);
                if (compareDay > 0)
                {
                    reset();
                } else if (compareDay == 0)
                {
                    return;
                }
            }

            if (DailyTextUtils.isTextEmpty(mCheckInDateTime) == true)
            {
                view.setSelected(true);

                mCheckInDateTime = day.dateTime;
                getViewInterface().setCheckInDay(day.dateTime);
                getViewInterface().setToolbarTitle(getString(R.string.label_calendar_hotel_select_checkout));
                getViewInterface().setLastDayEnabled(true);

                if (mNightsOfMaxCount == 1)
                {
                    Calendar calendar = DailyCalendar.getInstance();
                    calendar.setTime(DailyCalendar.convertDate(mCheckInDateTime, DailyCalendar.ISO_8601_FORMAT));
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                    unLock();
                    getViewInterface().clickDay(DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT));
                }
            } else
            {
                if (mNightsOfMaxCount < DailyCalendar.compareDateDay(day.dateTime, mCheckInDateTime))
                {
                    DailyToast.showToast(getActivity(), getString(R.string.label_calendar_possible_night, mNightsOfMaxCount), DailyToast.LENGTH_SHORT);
                    return;
                }

                // selected가 먼저 되어야지 캘린더에서 체크인과 체크아웃 시간까지 범위를 색칠한다.
                view.setSelected(true);

                mCheckOutDateTime = day.dateTime;
                getViewInterface().setCheckOutDay(day.dateTime);

                String checkInDate = DailyCalendar.convertDateFormatString(mCheckInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
                String checkOutDate = DailyCalendar.convertDateFormatString(mCheckOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
                int nights = DailyCalendar.compareDateDay(mCheckOutDateTime, mCheckInDateTime);

                String title = String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights);

                getViewInterface().setToolbarTitle(title);

                if (day.dateTime.equalsIgnoreCase(mEndDateTime) == false)
                {
                    getViewInterface().setLastDayEnabled(false);
                }

                getViewInterface().setConfirmEnabled(true);
                getViewInterface().setConfirmText(getString(R.string.label_calendar_stay_search_selected_date, nights));
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            unLock();
        }
    }

    @Override
    public void onConfirmClick()
    {
        if (DailyTextUtils.isTextEmpty(mCheckInDateTime, mCheckOutDateTime) == true)
        {
            return;
        }

        if (lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME, mCheckInDateTime);
        intent.putExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME, mCheckOutDateTime);

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }

    private void reset()
    {
        getViewInterface().reset();

        mCheckInDateTime = null;
        mCheckOutDateTime = null;

        getViewInterface().setLastDayEnabled(false);
        getViewInterface().setToolbarTitle(getString(R.string.label_calendar_hotel_select_checkin));
        getViewInterface().setConfirmEnabled(false);
        getViewInterface().setConfirmText(getString(R.string.label_calendar_search_selected_date));
    }
}
