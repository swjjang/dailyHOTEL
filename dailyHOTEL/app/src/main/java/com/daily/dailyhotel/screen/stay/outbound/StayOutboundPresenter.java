package com.daily.dailyhotel.screen.stay.outbound;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.Persons;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.stay.outbound.list.StayOutboundListActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPresenter extends BaseExceptionPresenter<StayOutboundActivity, StayOutboundViewInterface> implements StayOutboundView.OnEventListener
{
    private static final int REQUEST_CODE_CALENDAR = 10000;
    private static final int DAYS_OF_MAXCOUNT = 90;
    private static final int NIGHTS_OF_MAXCOUNT = 28;

    private StayOutboundAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private Persons mPersons;

    public interface StayOutboundAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundPresenter(@NonNull StayOutboundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundViewInterface createInstanceViewInterface()
    {
        return new StayOutboundView(getActivity(), this);
    }

    @Override
    public void initialize(StayOutboundActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_data);

        setAnalytics(new StayStayOutboundAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        // 기본 성인 2명, 아동 0명
        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }

        return true;
    }

    @Override
    public void onIntentAfter()
    {

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

    @Override
    public boolean onBackPressed()
    {
        return super.onBackPressed();
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
        unLockAll();

        switch (requestCode)
        {
            case REQUEST_CODE_CALENDAR:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                        && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                        {
                            return;
                        }

                        onCalendarDateTime(checkInDateTime, checkOutDateTime);
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onRefresh()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        setRefresh(false);
        screenLock(true);

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime()//
            .subscribe(commonDateTime ->
            {
                onCommonDateTime(commonDateTime);

                screenUnLock();
            }, throwable ->
            {
                onHandleError(throwable);

                // 처음 시작부터 정보를 못가져오면 종료시킨다.
                onBackClick();
            }));
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onRequestSuggests(String keyword)
    {
        clearCompositeDisposable();

        if (mSuggest == null)
        {
            mSuggest = new Suggest();
        }

        mSuggest.id = null;
        mSuggest.city = keyword;

        if (DailyTextUtils.isTextEmpty(keyword) == true)
        {
            getViewInterface().setRecentlySuggestsVisibility(true);
            getViewInterface().setSuggestsVisibility(false);
            getViewInterface().setToolbarMenuEnable(false);

            onSuggests(null);
        } else
        {
            getViewInterface().setRecentlySuggestsVisibility(false);
            getViewInterface().setSuggestsVisibility(true);
            getViewInterface().setToolbarMenuEnable(true);

            addCompositeDisposable(mSuggestRemoteImpl.getSuggestsByStayOutBound(keyword)//
                .delaySubscription(500, TimeUnit.MILLISECONDS).subscribe(suggests -> onSuggests(suggests), throwable -> onSuggests(null)));
        }
    }

    @Override
    public void onSuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        mSuggest = suggest.getClone();

        // 검색어에 해당 내용을 넣어준다.
        getViewInterface().setSuggest(suggest);
    }

    @Override
    public void onSearchKeyword()
    {
        if (mSuggest == null)
        {
            return;
        }

        Intent intent;

        if (DailyTextUtils.isTextEmpty(mSuggest.id) == true)
        {
            // 키워드 검색인 경우
            intent = StayOutboundListActivity.newInstance(getActivity(), mSuggest.city//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPersons.numberOfAdults, mPersons.getChildList());
        } else
        {
            // Suggest검색인 경우
            intent = StayOutboundListActivity.newInstance(getActivity(), mSuggest//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mPersons.numberOfAdults, mPersons.getChildList());

        }

        startActivity(intent);
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true || mStayBookDateTime == null)
        {
            return;
        }

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance();
            startCalendar.setTime(DailyCalendar.convertDate(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT));
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);

            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);

            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, true);

            startActivityForResult(intent, REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    private void setStayBookDefaultDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        // 처음 날짜가 없는 경우에는 x일 이후로 보이게 한다
        final int START_CHECK_IN_AFTER_DAY = 7;

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(commonDateTime.currentDateTime, START_CHECK_IN_AFTER_DAY);
            mStayBookDateTime.setCheckOutDateTime(commonDateTime.currentDateTime, START_CHECK_IN_AFTER_DAY + 1);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            onHandleError(e);
            onBackClick();
        }
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    private void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(checkInDateTime);
            mStayBookDateTime.setCheckOutDateTime(checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onStayBookDateTime(@NonNull StayBookDateTime stayBookDateTime)
    {
        if (stayBookDateTime == null)
        {
            return;
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), stayBookDateTime.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onCalendarDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        setStayBookDateTime(checkInDateTime, checkOutDateTime);
        onStayBookDateTime(mStayBookDateTime);
    }

    private void onCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;

        if (mStayBookDateTime == null)
        {
            setStayBookDefaultDateTime(commonDateTime);
        } else
        {
            try
            {
                // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                mStayBookDateTime.verifyCommonDateTime(commonDateTime);
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                setStayBookDefaultDateTime(commonDateTime);
            }
        }

        onStayBookDateTime(mStayBookDateTime);
    }

    private void onSuggests(List<Suggest> suggestList)
    {
        getViewInterface().setSuggests(suggestList);
    }
}
