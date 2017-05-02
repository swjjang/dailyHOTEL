package com.daily.dailyhotel.screen.common.calendar;


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
import com.daily.dailyhotel.screen.stay.outbound.StayOutboundActivity;
import com.daily.dailyhotel.screen.stay.outbound.StayOutboundView;
import com.daily.dailyhotel.screen.stay.outbound.StayOutboundViewInterface;
import com.daily.dailyhotel.screen.stay.outbound.StayStayOutboundAnalyticsImpl;
import com.daily.dailyhotel.screen.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.util.ConvertFormat;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayCalendarPresenter extends PlaceCalendarPresenter<StayCalendarActivity, StayCalendarViewInterface> implements StayCalendarView.OnEventListener
{
    private static final int REQUEST_CODE_CALENDAR = 10000;

    private StayCalendarPresenterAnalyticsInterface mAnalytics;
    private SuggestRemoteImpl mSuggestRemoteImpl;
    private CommonRemoteImpl mCommonRemoteImpl;

    private CommonDateTime mCommonDateTime;
    private StayBookDateTime mStayBookDateTime;

    private Suggest mSuggest;
    private Persons mPersons;

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
        setContentView(R.layout.activity_calendar_data);

        setAnalytics(new StayCalendarAnalyticsImpl());

        mSuggestRemoteImpl = new SuggestRemoteImpl(activity);
        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        // 기본 성인 2명, 아동 0명
        mPersons = new Persons(Persons.DEFAULT_PERSONS, null);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayCalendarPresenterAnalyticsInterface) analytics;
    }

    @Override
    public void finish()
    {
        onBackPressed();
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

                screenUnLock();
            }, throwable ->
            {
                onHandleError(throwable);

                // 처음 시작부터 정보를 못가져오면 종료시킨다.
                finish();
            }));
    }
}
