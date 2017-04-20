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
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.Suggest;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.SuggestRemoteImpl;
import com.daily.dailyhotel.util.ConvertFormat;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

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
        } catch (Exception e)
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
        if (suggestList == null)
        {
            return;
        }

        getViewInterface().setSuggests(suggestList);
        getViewInterface().setSuggestsVisibility(true);
    }
}
