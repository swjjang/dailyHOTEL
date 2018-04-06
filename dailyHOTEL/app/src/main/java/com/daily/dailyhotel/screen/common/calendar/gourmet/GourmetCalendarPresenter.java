package com.daily.dailyhotel.screen.common.calendar.gourmet;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.SparseIntArray;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetDetail;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.repository.remote.CalendarImpl;
import com.daily.dailyhotel.repository.remote.GourmetRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.BaseCalendarPresenter;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;

import java.text.ParseException;
import java.util.List;
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
public class GourmetCalendarPresenter extends BaseCalendarPresenter<GourmetCalendarActivity, GourmetCalendarInterface.ViewInterface> implements GourmetCalendarInterface.OnEventListener
{
    private GourmetCalendarInterface.AnalyticsInterface mAnalytics;

    GourmetRemoteImpl mGourmetRemoteImpl;

    String mVisitDateTime;

    String mStartDateTime;
    String mEndDateTime;

    private String mCallByScreen;
    boolean mIsSelected;
    private int mMarginTop;
    boolean mIsAnimation;

    private CalendarImpl mCalendarImpl;
    private int mGourmetIndex;
    SparseIntArray mSoldOutDays;

    public GourmetCalendarPresenter(@NonNull GourmetCalendarActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetCalendarInterface.ViewInterface createInstanceViewInterface()
    {
        return new GourmetCalendarView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetCalendarActivity activity)
    {
        super.constructorInitialize(activity);

        setContentView(R.layout.activity_calendar_data);

        getViewInterface().setVisible(false);

        mAnalytics = new GourmetCalendarAnalyticsImpl();

        mGourmetRemoteImpl = new GourmetRemoteImpl(activity);
        mCalendarImpl = new CalendarImpl(activity);

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
            mVisitDateTime = intent.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATETIME);

            mAnalytics.setVisitDateTime(mVisitDateTime);

            mStartDateTime = intent.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_START_DATETIME);
            mEndDateTime = intent.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_END_DATETIME);

            mCallByScreen = intent.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_CALLBYSCREEN);
            mIsSelected = intent.getBooleanExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_IS_SELECTED, true);
            mMarginTop = intent.getIntExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_MARGIN_TOP, 0);
            mIsAnimation = intent.getBooleanExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_ISANIMATION, false);

            int[] soldOutDays = intent.getIntArrayExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_SOLD_OUT_DAYS);

            if (soldOutDays != null)
            {
                mSoldOutDays = new SparseIntArray();

                for (int day : soldOutDays)
                {
                    mSoldOutDays.put(day, day);
                }
            }

            mGourmetIndex = intent.getIntExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_INDEX, 0);
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
                    selectCalendarBookDateTime(mVisitDateTime);
                } else
                {
                    reset();
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

    void selectCalendarBookDateTime(String visitDateTime) throws ParseException
    {
        int checkInDay = Integer.parseInt(DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));

        int year = Integer.parseInt(DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy"));
        int month = Integer.parseInt(DailyCalendar.convertDateFormatString(visitDateTime, DailyCalendar.ISO_8601_FORMAT, "MM"));

        onDayClick(visitDateTime, checkInDay);

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
        if (DailyTextUtils.isTextEmpty(mVisitDateTime) == true || lock() == true)
        {
            return;
        }

        if (mGourmetIndex == 0)
        {
            finish(mVisitDateTime);
        } else
        {
            try
            {
                addCompositeDisposable(mGourmetRemoteImpl.getDetail(mGourmetIndex, new GourmetBookDateTime(mVisitDateTime)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetDetail>()
                {
                    @Override
                    public void accept(GourmetDetail gourmetDetail) throws Exception
                    {
                        if (gourmetDetail.hasMenus() == false)
                        {
                            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2)//
                                , getString(R.string.gourmet_detail_calender_dialog_message)//
                                , getString(R.string.dialog_btn_text_confirm), null);
                        } else
                        {
                            finish(mVisitDateTime);
                        }

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        finish(mVisitDateTime);
                    }
                }));
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }

        }

        mAnalytics.onEventConfirmClick(getActivity(), mCallByScreen, mVisitDateTime);
    }

    void finish(String visitDateTime)
    {
        Intent intent = new Intent();
        intent.putExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATETIME, visitDateTime);

        setResult(Activity.RESULT_OK, intent);

        onBackClick();
    }

    void reset()
    {
        mVisitDateTime = null;

        getViewInterface().setToolbarTitle(getString(R.string.label_calendar_gourmet_select));
        getViewInterface().setConfirmEnabled(false);
        getViewInterface().setConfirmText(getString(R.string.label_calendar_gourmet_search_selected_date));

        getViewInterface().setVisitDay(0);

        getViewInterface().notifyCalendarDataSetChanged();
    }

    void onDayClick(String dayDateTime, int yyyyMMdd)
    {
        if (DailyTextUtils.isTextEmpty(dayDateTime) == true || yyyyMMdd == 0)
        {
            return;
        }

        mVisitDateTime = dayDateTime;

        try
        {
            String visitDate = DailyCalendar.convertDateFormatString(dayDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)");
            getViewInterface().setToolbarTitle(visitDate);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        getViewInterface().setVisitDay(yyyyMMdd);
        getViewInterface().setConfirmEnabled(true);
        getViewInterface().setConfirmText(getString(R.string.label_calendar_gourmet_search_selected_date));

        getViewInterface().notifyCalendarDataSetChanged();
    }
}
