package com.daily.dailyhotel.screen.stay.outbound;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.util.ConvertFormat;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundPresenter extends BaseExceptionPresenter<StayOutboundActivity, StayOutboundViewInterface> implements StayOutboundView.OnEventListener
{
    private static final int REQUEST_CODE_CALENDAR = 10000;

    private StayOutboundAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

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
        setContentView(R.layout.activity_outbound_data);

        setAnalytics(new StayStayOutboundAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {
        onBackPressed();
    }

    @Override
    public void onIntent(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        if (intent.hasExtra(BaseActivity.INTENT_EXTRA_DATA_DEEPLINK) == true)
        {
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            screenLock(true);

            addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime()//
                .subscribe(commonDateTime ->
                {
                    setRefresh(false);
                    onCommonDateTime(commonDateTime);

                    screenUnLock();
                }, throwable -> onHandleError(throwable)));
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
        unLock();

        switch (requestCode)
        {
            case REQUEST_CODE_CALENDAR:
                break;
        }
    }

    @Override
    public void onRequestSuggests(String keyword)
    {
        getViewInterface().setRecentlySuggestsVisibility(false);

        clearCompositeDisposable();

        addCompositeDisposable(Observable.timer(500, TimeUnit.MILLISECONDS).doOnNext(timer -> addCompositeDisposable(//
            mSuggestRemoteImpl.getSuggestsByStayOutBound(keyword).subscribe(suggests -> onSuggests(suggests), throwable -> onSuggests(null)))//
        ).subscribe());
    }

    @Override
    public void onSuggestClick(Suggest suggest)
    {
        if (suggest == null)
        {
            return;
        }


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
            Intent intent = StayCalendarActivity.newInstance(getActivity(), ConvertFormat.convertTodayDateTime(mCommonDateTime)//
                , ConvertFormat.commonStayBookingDay(mStayBookDateTime), //
                AnalyticsManager.ValueType.SEARCH, true, true);

            startActivityForResult(intent, REQUEST_CODE_CALENDAR);
        }catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onReset()
    {
        getViewInterface().setSuggests(null);
    }

    private void setStayBookDefaultDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        // 처음 날짜가 없는 경우에는 x일 이후로 보이게 한다
        final int START_CHECK_IN_AFTER_DAY = 3;

        if (mStayBookDateTime == null)
        {
            mStayBookDateTime = new StayBookDateTime();
        }

        try
        {
            mStayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime, START_CHECK_IN_AFTER_DAY);
            mStayBookDateTime.setCheckOutDateTime(commonDateTime.dailyDateTime, START_CHECK_IN_AFTER_DAY + 1);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            onHandleError(e);
            finish();
        }
    }

    private void onCommonDateTime(CommonDateTime commonDateTime)
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
                // 체크인 날짜 체크

                // 날짜로 비교해야 한다.
                Calendar todayCalendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, true);
                Calendar checkInCalendar = DailyCalendar.getInstance(mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), true);
                Calendar checkOutCalendar = DailyCalendar.getInstance(mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), true);

                // 하루가 지나서 체크인 날짜가 전날짜 인 경우
                if (todayCalendar.getTimeInMillis() > checkInCalendar.getTimeInMillis())
                {
                    mStayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);

                    checkInCalendar = DailyCalendar.getInstance(mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), true);
                }

                // 체크인 날짜가 체크 아웃 날짜와 같거나 큰경우.
                if (checkInCalendar.getTimeInMillis() >= checkOutCalendar.getTimeInMillis())
                {
                    mStayBookDateTime.setCheckOutDateTime(mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), 1);
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                setStayBookDefaultDateTime(commonDateTime);
            }
        }

        try
        {
            getViewInterface().setCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                , mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                , mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)"), mStayBookDateTime.getNights()));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void onSuggests(List<Suggest> suggestList)
    {
        if (suggestList == null)
        {
            return;
        }

        getViewInterface().setSuggests(suggestList);
        getViewInterface().setSuggestsVisibility(true);
    }
}
