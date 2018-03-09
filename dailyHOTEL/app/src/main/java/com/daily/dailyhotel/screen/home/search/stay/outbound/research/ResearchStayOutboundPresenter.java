package com.daily.dailyhotel.screen.home.search.stay.outbound.research;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayOutboundViewModel;
import com.daily.dailyhotel.screen.home.search.stay.outbound.suggest.SearchStayOutboundSuggestActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class ResearchStayOutboundPresenter extends BaseExceptionPresenter<ResearchStayOutboundActivity, ResearchStayOutboundInterface.ViewInterface> implements ResearchStayOutboundInterface.OnEventListener
{
    private ResearchStayOutboundInterface.AnalyticsInterface mAnalytics;

    SearchStayOutboundViewModel mSearchModel;

    CommonDateTime mCommonDateTime;

    public ResearchStayOutboundPresenter(@NonNull ResearchStayOutboundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected ResearchStayOutboundInterface.ViewInterface createInstanceViewInterface()
    {
        return new ResearchStayOutboundView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(ResearchStayOutboundActivity activity)
    {
        setContentView(R.layout.activity_research_stay_outbound_data);

        setAnalytics(new ResearchStayOutboundAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (ResearchStayOutboundInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mCommonDateTime = new CommonDateTime(intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME));

        try
        {
            String checkInDateTime = intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            mSearchModel.setBookDateTime(checkInDateTime, checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        StayOutboundSuggestParcel suggestParcel = intent.getParcelableExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel != null)
        {
            mSearchModel.suggest.setValue(suggestParcel.getSuggest());
        }

        int numberOfAdults = intent.getIntExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, 2);
        ArrayList<Integer> childAgeList = intent.getIntegerArrayListExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHILD_LIST);

        setPeople(numberOfAdults, childAgeList);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_search_search_stayoutbound));

        if (mSearchModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(mSearchModel.suggest.getValue().display) == true)
        {
            getViewInterface().setSearchSuggestText(null);
            getViewInterface().setSearchButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchSuggestText(mSearchModel.suggest.getValue().display);
            getViewInterface().setSearchButtonEnabled(true);
        }

        StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

        getViewInterface().setSearchCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getNights()));

        addCompositeDisposable(getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer()
        {
            @Override
            public void accept(Object o) throws Exception
            {
                getViewInterface().showSearch();
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

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
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
            case ResearchStayOutboundActivity.REQUEST_CODE_SUGGEST:
                onSuggestActivityResult(resultCode, data);
                break;

            case ResearchStayOutboundActivity.REQUEST_CODE_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;

            case ResearchStayOutboundActivity.REQUEST_CODE_PEOPLE:
                onPeopleActivityResult(resultCode, data);
                break;
        }
    }

    private void onSuggestActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    try
                    {
                        StayOutboundSuggestParcel suggestParcel = intent.getParcelableExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.suggest.setValue(suggestParcel.getSuggest());
                        mSearchModel.inputString = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                        mSearchModel.clickType = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;
        }
    }

    private void onCalendarActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    try
                    {
                        String checkInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        mSearchModel.setBookDateTime(checkInDateTime, checkOutDateTime);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;
        }
    }

    private void onPeopleActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    try
                    {
                        int numberOfAdults = intent.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> arrayList = intent.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        setPeople(numberOfAdults, arrayList);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSuggestClick()
    {
        try
        {
            startActivityForResult(SearchStayOutboundSuggestActivity.newInstance(getActivity(), null), ResearchStayOutboundActivity.REQUEST_CODE_SUGGEST);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        final int DAYS_OF_MAXCOUNT = 365;
        final int NIGHTS_OF_MAXCOUNT = 28;

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance(mCommonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);
            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);
            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

            startActivityForResult(StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 41), true)//
                , ResearchStayOutboundActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onPeopleClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent;

        if (mSearchModel.people.getValue() == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), mSearchModel.people.getValue().numberOfAdults, mSearchModel.people.getValue().getChildAgeList());
        }

        startActivityForResult(intent, ResearchStayOutboundActivity.REQUEST_CODE_PEOPLE);
    }

    @Override
    public void onDoSearchClick()
    {
        try
        {
            StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

            Intent intent = new Intent();
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_SUGGEST, new StayOutboundSuggestParcel(mSearchModel.suggest.getValue()));
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, mSearchModel.people.getValue().numberOfAdults);
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CHILD_LIST, mSearchModel.people.getValue().getChildAgeList());
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_KEYWORD, mSearchModel.inputString);
            intent.putExtra(ResearchStayOutboundActivity.INTENT_EXTRA_DATA_CLICK_TYPE, mSearchModel.clickType);

            setResult(Activity.RESULT_OK, intent);
            onBackClick();
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        if (recentlyDbPlace == null || lock() == true)
        {
            return;
        }

        StayBookDateTime stayBookDateTime = mSearchModel.getBookDateTime();

        startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), recentlyDbPlace.index, recentlyDbPlace.name//
            , recentlyDbPlace.englishName, recentlyDbPlace.imageUrl, StayOutboundDetailActivity.NONE_PRICE//
            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.people.getValue().numberOfAdults, mSearchModel.people.getValue().getChildAgeList()//
            , false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, null)//
            , ResearchStayOutboundActivity.REQUEST_CODE_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        addCompositeDisposable(Completable.complete().delay(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                finish();
            }
        }));
    }

    @Override
    public void onPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null || lock() == true)
        {
            return;
        }

        mSearchModel.suggest.setValue(stayOutboundSuggest);

        addCompositeDisposable(getViewInterface().getSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                unLockAll();
            }
        }));
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity, new SearchStayOutboundViewModel.SearchStayOutboundViewModelFactory()).get(SearchStayOutboundViewModel.class);

        // StayOutbound
        mSearchModel.suggest.observe(activity, new Observer<StayOutboundSuggest>()
        {
            @Override
            public void onChanged(@Nullable StayOutboundSuggest stayOutboundSuggest)
            {
                getViewInterface().setSearchSuggestText(stayOutboundSuggest.display);

                getViewInterface().setSearchButtonEnabled(DailyTextUtils.isTextEmpty(stayOutboundSuggest.display) == false);
            }
        });

        mSearchModel.bookDateTime.observe(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                getViewInterface().setSearchCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                    , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getNights()));
            }
        });

        mSearchModel.people.observe(activity, new Observer<People>()
        {
            @Override
            public void onChanged(@Nullable People people)
            {
                getViewInterface().setSearchPeopleText(people.toString(getActivity()));
            }
        });

        mSearchModel.people.setValue(new People(People.DEFAULT_ADULTS, null));
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mSearchModel.people.getValue() == null)
        {
            mSearchModel.people.setValue(new People(People.DEFAULT_ADULTS, null));
        }

        mSearchModel.people.getValue().numberOfAdults = numberOfAdults;
        mSearchModel.people.getValue().setChildAgeList(childAgeList);
        mSearchModel.people.setValue(mSearchModel.people.getValue());
    }
}
