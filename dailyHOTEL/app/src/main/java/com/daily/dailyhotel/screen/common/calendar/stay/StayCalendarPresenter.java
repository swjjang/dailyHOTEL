package com.daily.dailyhotel.screen.common.calendar.stay;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.daily.dailyhotel.screen.common.calendar.BaseCalendarPresenter;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayCalendarPresenter extends BaseCalendarPresenter<StayCalendarActivity, StayCalendarInterface.ViewInterface> implements StayCalendarInterface.OnEventListener
{
    private StayCalendarInterface.AnalyticsInterface mAnalytics;

    String mCheckInDateTime;
    String mCheckOutDateTime;

    String mStartDateTime;
    String mEndDateTime;
    int mNightsOfMaxCount;

    private String mCallByScreen;
    boolean mIsSelected;
    private int mMarginTop;
    boolean mIsAnimation;

    private CalendarImpl mCalendarImpl;
    private int mStayIndex;
    SparseIntArray mSoldOutDays;

    public StayCalendarPresenter(@NonNull StayCalendarActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayCalendarInterface.ViewInterface createInstanceViewInterface()
    {
        return new StayCalendarView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayCalendarActivity activity)
    {
        super.constructorInitialize(activity);

        setContentView(R.layout.activity_calendar_data);

        getViewInterface().setVisible(false);

        mAnalytics = new StayCalendarAnalyticsImpl();

        mCalendarImpl = new CalendarImpl();

        setRefresh(true);
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
            mCheckInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME);
            mCheckOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);

            mAnalytics.setCheckInOutDateTime(mCheckInDateTime, mCheckOutDateTime);

            mStartDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_START_DATETIME);
            mEndDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_END_DATETIME);
            mNightsOfMaxCount = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_NIGHTS_OF_MAXCOUNT, 1);

            mCallByScreen = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CALLBYSCREEN);
            mIsSelected = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_IS_SELECTED, true);
            mMarginTop = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_MARGIN_TOP, 0);
            mIsAnimation = intent.getBooleanExtra(StayCalendarActivity.INTENT_EXTRA_DATA_ISANIMATION, false);

            int[] soldOutDays = intent.getIntArrayExtra(StayCalendarActivity.INTENT_EXTRA_DATA_SOLD_OUT_DAYS);

            if (soldOutDays != null)
            {
                mSoldOutDays = new SparseIntArray();

                for (int day : soldOutDays)
                {
                    mSoldOutDays.put(day, day);
                }
            }

            mStayIndex = intent.getIntExtra(StayCalendarActivity.INTENT_EXTRA_DATA_INDEX, 0);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

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
        }).subscribeOn(Schedulers.io()).map(new Function<String, List<ObjectItem>>()
        {
            @Override
            public List<ObjectItem> apply(String calendarHolidays) throws Exception
            {
                List<ObjectItem> calendarList = makeCalendar(mStartDateTime, mEndDateTime, getHolidayArray(calendarHolidays), mSoldOutDays);

                calendarList.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));

                return calendarList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<ObjectItem>, Observable<Boolean>>()
        {
            @Override
            public Observable<Boolean> apply(@io.reactivex.annotations.NonNull List<ObjectItem> arrayList) throws Exception
            {
                getViewInterface().setCalendarList(arrayList);

                if (mIsAnimation == true)
                {
                    Observable<Boolean> observable = getViewInterface().showAnimation();

                    if (observable != null)
                    {
                        screenLock(false);

                        return observable.map(new Function<Boolean, Boolean>()
                        {
                            @Override
                            public Boolean apply(Boolean aBoolean) throws Exception
                            {
                                return mIsSelected;
                            }
                        });
                    }
                } else
                {
                    getViewInterface().setVisible(true);
                }

                return Observable.just(mIsSelected).subscribeOn(AndroidSchedulers.mainThread());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Boolean isSelectedDays) throws Exception
            {
                unLockAll();

                if (isSelectedDays == true)
                {
                    String checkInDateTime = mCheckInDateTime;
                    String checkOutDateTime = mCheckOutDateTime;

                    mCheckInDateTime = mCheckOutDateTime = null;

                    selectCalendarBookDateTime(checkInDateTime, checkOutDateTime);
                } else
                {
                    reset();
                }

                if (mNightsOfMaxCount == 1)
                {
                    getViewInterface().showToast(R.string.message_calendar_select_single_day, DailyToast.LENGTH_SHORT);
                }
            }
        }));
    }

    SparseIntArray getHolidayArray(String calendarHolidays)
    {
        if (DailyTextUtils.isTextEmpty(calendarHolidays) == true)
        {
            return null;
        }

        String[] holidaysSplit = calendarHolidays.split("\\,");
        int length = holidaysSplit.length;
        SparseIntArray holidaySparseIntArray = new SparseIntArray(length);

        for (String holidaySplit : holidaysSplit)
        {
            try
            {
                int holiday = Integer.parseInt(holidaySplit);
                holidaySparseIntArray.put(holiday, holiday);
            } catch (NumberFormatException e)
            {
                ExLog.e(e.toString());
            }
        }

        return holidaySparseIntArray;
    }

    void selectCalendarBookDateTime(String checkInDateTime, String checkOutDateTime) throws ParseException
    {
        int checkInDay = Integer.parseInt(DailyCalendar.convertDateFormatString(checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
        int checkOutDay = Integer.parseInt(DailyCalendar.convertDateFormatString(checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));

        int year = Integer.parseInt(DailyCalendar.convertDateFormatString(checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy"));
        int month = Integer.parseInt(DailyCalendar.convertDateFormatString(checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "MM"));

        onDayClick(checkInDateTime, checkInDay);

        if (mNightsOfMaxCount > 1)
        {
            onDayClick(checkOutDateTime, checkOutDay);
        }

        getViewInterface().scrollMonthPosition(year, month);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        mAnalytics.onScreen(getActivity());

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
        mAnalytics.onEventCloseClick(getActivity(), mCallByScreen);

        getActivity().onBackPressed();
    }

    @Override
    public void onDayClick(Day day)
    {
        if (day == null || lock() == true)
        {
            return;
        }

        onDayClick(day.getDateTime(), day.toyyyyMMdd());
        unLockAll();
    }

    @Override
    public void onConfirmClick()
    {
        if (DailyTextUtils.isTextEmpty(mCheckInDateTime, mCheckOutDateTime) == true || lock() == true)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME, mCheckInDateTime);
        intent.putExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME, mCheckOutDateTime);

        setResult(Activity.RESULT_OK, intent);

        mAnalytics.onEventConfirmClick(getActivity(), mCallByScreen, mCheckInDateTime, mCheckOutDateTime);

        onBackClick();
    }

    void reset()
    {
        mCheckInDateTime = null;
        mCheckOutDateTime = null;

        getViewInterface().setLastDayEnabled(false);
        getViewInterface().setToolbarTitle(getString(R.string.label_calendar_hotel_select_checkin));
        getViewInterface().setConfirmEnabled(false);
        getViewInterface().setConfirmText(getString(R.string.label_calendar_search_selected_date));

        getViewInterface().setCheckInDay(0);
        getViewInterface().setCheckOutDay(0);
        getViewInterface().setAvailableCheckOutDays(null);

        getViewInterface().notifyCalendarDataSetChanged();
    }

    void onDayClick(String dayDateTime, int yyyyMMdd)
    {
        if (DailyTextUtils.isTextEmpty(dayDateTime) == true || yyyyMMdd == 0)
        {
            return;
        }

        try
        {
            if (selectedCheckInCheckOutDateTime() == true)
            {
                if (isValidCheckInDateTime(dayDateTime, yyyyMMdd) == true)
                {
                    reset();
                } else
                {
                    DailyToast.showToast(getActivity(), getString(R.string.label_message_dont_check_date), DailyToast.LENGTH_SHORT);
                    return;
                }
            }

            // 기존의 날짜 보다 전날짜를 선택하면 초기화.
            if (selectedCheckInDateTime() == true)
            {
                int compareDay = DailyCalendar.compareDateDay(mCheckInDateTime, dayDateTime);
                if (compareDay > 0)
                {
                    reset();
                } else if (compareDay == 0)
                {
                    return;
                }
            }

            if (selectedCheckInDateTime() == false)
            {
                processCheckInDateTime(dayDateTime, yyyyMMdd);

                if (mStayIndex > 0 && mNightsOfMaxCount > 1)
                {
                    setAvailableCheckOutDays(mCheckInDateTime, DailyCalendar.compareDateDay(mEndDateTime, mStartDateTime) + 1);
                }
            } else
            {
                clearCompositeDisposable();

                processCheckOutDateTime(mCheckInDateTime, dayDateTime, yyyyMMdd);

                getViewInterface().setAvailableCheckOutDays(null);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private boolean selectedCheckInCheckOutDateTime()
    {
        return DailyTextUtils.isTextEmpty(mCheckInDateTime, mCheckOutDateTime) == false;
    }

    private boolean selectedCheckInDateTime()
    {
        return DailyTextUtils.isTextEmpty(mCheckInDateTime) == false;
    }

    private boolean isValidCheckInDateTime(String checkInDateTime, int yyyyMMdd) throws ParseException
    {
        return (DailyCalendar.compareDateDay(mEndDateTime, checkInDateTime) > 0//
            && (mSoldOutDays == null || mSoldOutDays.get(yyyyMMdd) == 0));
    }

    private void processCheckInDateTime(String checkInDateTime, int yyyyMMdd) throws ParseException
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime) == true)
        {
            return;
        }

        mCheckInDateTime = checkInDateTime;
        getViewInterface().setCheckInDay(yyyyMMdd);
        getViewInterface().setToolbarTitle(getString(R.string.label_calendar_hotel_select_checkout));
        getViewInterface().setLastDayEnabled(true);

        if (mNightsOfMaxCount == 1)
        {
            Calendar calendar = DailyCalendar.getInstance(checkInDateTime, DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, 1);

            unLock();

            onDayClick(DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT)//
                , calendar.get(Calendar.YEAR) * 10000 + (calendar.get(Calendar.MONTH) + 1) * 100 + calendar.get(Calendar.DAY_OF_MONTH));
        } else
        {
            getViewInterface().notifyCalendarDataSetChanged();
        }
    }

    private void processCheckOutDateTime(String checkInDateTime, String checkOutDateTime, int yyyyMMdd) throws ParseException
    {
        if (mNightsOfMaxCount < DailyCalendar.compareDateDay(checkOutDateTime, checkInDateTime))
        {
            DailyToast.showToast(getActivity(), getString(R.string.label_calendar_possible_night, mNightsOfMaxCount), DailyToast.LENGTH_SHORT);
            return;
        }

        // selected가 먼저 되어야지 캘린더에서 체크인과 체크아웃 시간까지 범위를 색칠한다.
        mCheckOutDateTime = checkOutDateTime;
        getViewInterface().setCheckOutDay(yyyyMMdd);

        String checkInDate = DailyCalendar.convertDateFormatString(checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
        String checkOutDate = DailyCalendar.convertDateFormatString(checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
        int nights = DailyCalendar.compareDateDay(checkOutDateTime, checkInDateTime);

        getViewInterface().setToolbarTitle(String.format(Locale.KOREA, "%s - %s, %d박", checkInDate, checkOutDate, nights));

        if (DailyCalendar.compareDateDay(checkOutDateTime, mEndDateTime) != 0)
        {
            getViewInterface().setLastDayEnabled(false);
        }

        getViewInterface().setConfirmEnabled(true);
        getViewInterface().setConfirmText(getString(R.string.label_calendar_stay_search_selected_date, nights));

        getViewInterface().notifyCalendarDataSetChanged();
    }

    private void setAvailableCheckOutDays(String checkInDateTime, int dayOfMaxCount) throws ParseException
    {
        if (checkInDateTime == null || dayOfMaxCount == 0)
        {
            return;
        }

        String checkInDay = DailyCalendar.convertDateFormatString(mCheckInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd");

        addCompositeDisposable(mCalendarImpl.getStayAvailableCheckOutDates(mStayIndex, dayOfMaxCount, checkInDay)//
            .map(new Function<List<String>, SparseIntArray>()
            {
                @Override
                public SparseIntArray apply(@NonNull List<String> availableDayList) throws Exception
                {
                    SparseIntArray availableDays = new SparseIntArray();

                    for (String stringDay : availableDayList)
                    {
                        int yyyyMMdd = Integer.parseInt(stringDay.replaceAll("-", ""));
                        availableDays.put(yyyyMMdd, yyyyMMdd);
                    }

                    return availableDays;
                }
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<SparseIntArray>()
            {
                @Override
                public void accept(@NonNull SparseIntArray availableDays) throws Exception
                {
                    if (availableDays.size() == 0)
                    {
                        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_stay_detail_sold_out)//
                            , getString(R.string.label_changing_date), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    reset();
                                }
                            });
                    } else
                    {
                        getViewInterface().setAvailableCheckOutDays(availableDays);

                        getViewInterface().notifyCalendarDataSetChanged();
                    }

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {

                }
            }));
    }
}
