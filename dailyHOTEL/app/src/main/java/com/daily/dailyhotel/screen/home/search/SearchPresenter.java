package com.daily.dailyhotel.screen.home.search;


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
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.calendar.StayOutboundCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.search.StayOutboundSearchSuggestActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchPresenter extends BaseExceptionPresenter<SearchActivity, SearchInterface.ViewInterface> implements SearchInterface.OnEventListener
{
    private SearchInterface.AnalyticsInterface mAnalytics;

    SearchViewModel mSearchModel;

    public SearchPresenter(@NonNull SearchActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchActivity activity)
    {
        setContentView(R.layout.activity_search_data);

        setAnalytics(new SearchAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mSearchModel.commonDateTime.setValue(new CommonDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_OPEN_DATE_TIME)//
            , intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CLOSE_DATE_TIME)//
            , intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CURRENT_DATE_TIME)//
            , intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_DAILY_DATE_TIME)));

        try
        {
            StayBookDateTime stayBookDateTime = new StayBookDateTime();
            stayBookDateTime.setCheckInDateTime(mSearchModel.commonDateTime.getValue().dailyDateTime);
            stayBookDateTime.setCheckOutDateTime(mSearchModel.commonDateTime.getValue().dailyDateTime, 1);
            mSearchModel.stayViewModel.bookDateTime.setValue(stayBookDateTime);

            StayBookDateTime stayOutboundBookDateTime = new StayBookDateTime();
            stayOutboundBookDateTime.setCheckInDateTime(mSearchModel.commonDateTime.getValue().currentDateTime);
            stayOutboundBookDateTime.setCheckOutDateTime(mSearchModel.commonDateTime.getValue().currentDateTime, 1);
            mSearchModel.stayOutboundViewModel.bookDateTime.setValue(stayOutboundBookDateTime);

            GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
            gourmetBookDateTime.setVisitDateTime(mSearchModel.commonDateTime.getValue().dailyDateTime);
            mSearchModel.gourmetViewModel.bookDateTime.setValue(gourmetBookDateTime);

            Constants.ServiceType serviceType = Constants.ServiceType.valueOf(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_SERVICE_TYPE));

            addCompositeDisposable(getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    if (serviceType != null)
                    {
                        mSearchModel.serviceType.setValue(serviceType);
                    } else
                    {
                        mSearchModel.serviceType.setValue(Constants.ServiceType.HOTEL);
                    }
                }
            }));
        } catch (Exception e)
        {
            ExLog.e(e.toString());
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
            case SearchActivity.REQUEST_CODE_STAY_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        StaySuggestParcel staySuggestParcel = data.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.stayViewModel.suggest.setValue(staySuggestParcel.getSuggest());
                        mSearchModel.stayViewModel.inputString = data.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE);

                        mSearchModel.stayViewModel.bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT:
                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);
                        break;

                    default:
                        if (data != null && data.hasExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
                        {
                            StaySuggestParcel staySuggestParcel = data.getParcelableExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_SUGGEST);

                            if (staySuggestParcel != null)
                            {
                                mSearchModel.stayViewModel.suggest.setValue(staySuggestParcel.getSuggest());
                            }
                        }
                        break;
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST) == true)
                    {
                        StayOutboundSuggestParcel suggestParcel = data.getParcelableExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        String keyword = data.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                        String clickType = data.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE);

                        if (suggestParcel != null)
                        {
                            mSearchModel.stayOutboundViewModel.suggest.setValue(suggestParcel.getSuggest());
                        }
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayOutboundCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

                        mSearchModel.stayOutboundViewModel.bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {

                }
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String visitDate = data.getStringExtra(GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATE);

                        mSearchModel.gourmetViewModel.bookDateTime.setValue(new GourmetBookDateTime(visitDate));
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_PEOPLE:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    if (data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS) == true && data.hasExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST) == true)
                    {
                        int numberOfAdults = data.getIntExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> arrayList = data.getIntegerArrayListExtra(SelectPeopleActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        setPeople(numberOfAdults, arrayList);
                    }
                }
                break;
            }
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
    public void onStayClick()
    {
        if (mSearchModel == null || mSearchModel.serviceType.getValue() == Constants.ServiceType.HOTEL || lock() == true)
        {
            return;
        }

        mSearchModel.serviceType.setValue(Constants.ServiceType.HOTEL);
    }

    @Override
    public void onStayOutboundClick()
    {
        if (mSearchModel == null || mSearchModel.serviceType.getValue() == Constants.ServiceType.OB_STAY || lock() == true)
        {
            return;
        }

        mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);
    }

    @Override
    public void onGourmetClick()
    {
        if (mSearchModel == null || mSearchModel.serviceType.getValue() == Constants.ServiceType.GOURMET || lock() == true)
        {
            return;
        }

        mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);
    }

    @Override
    public void onStaySuggestClick()
    {
        try
        {
            startActivityForResult(SearchStaySuggestActivity.newInstance(getActivity()//
                , null //
                , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)), SearchActivity.REQUEST_CODE_STAY_SUGGEST);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onStayCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        TodayDateTime todayDateTime = new TodayDateTime();
        todayDateTime.openDateTime = mSearchModel.commonDateTime.getValue().openDateTime;
        todayDateTime.closeDateTime = mSearchModel.commonDateTime.getValue().closeDateTime;
        todayDateTime.currentDateTime = mSearchModel.commonDateTime.getValue().currentDateTime;
        todayDateTime.dailyDateTime = mSearchModel.commonDateTime.getValue().dailyDateTime;

        startActivityForResult(StayCalendarActivity.newInstance(getActivity(), todayDateTime//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH, true, true), SearchActivity.REQUEST_CODE_STAY_CALENDAR);
    }

    @Override
    public void onStayDoSearchClick()
    {
        try
        {
            TodayDateTime todayDateTime = new TodayDateTime();
            todayDateTime.openDateTime = mSearchModel.commonDateTime.getValue().openDateTime;
            todayDateTime.closeDateTime = mSearchModel.commonDateTime.getValue().closeDateTime;
            todayDateTime.currentDateTime = mSearchModel.commonDateTime.getValue().currentDateTime;
            todayDateTime.dailyDateTime = mSearchModel.commonDateTime.getValue().dailyDateTime;

            StayBookingDay stayBookingDay = new StayBookingDay();
            stayBookingDay.setCheckInDay(mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
            stayBookingDay.setCheckOutDay(mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

            startActivityForResult(StaySearchResultActivity.newInstance(getActivity(), todayDateTime//
                , stayBookingDay, mSearchModel.stayViewModel.inputString, mSearchModel.stayViewModel.suggest.getValue(), AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onStayOutboundSuggestClick()
    {
        startActivityForResult(StayOutboundSearchSuggestActivity.newInstance(getActivity(), ""), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST);
    }

    @Override
    public void onStayOutboundCalendarClick()
    {
        final int DAYS_OF_MAXCOUNT = 365;
        final int NIGHTS_OF_MAXCOUNT = 28;

        try
        {
            Calendar startCalendar = DailyCalendar.getInstance();
            startCalendar.setTime(DailyCalendar.convertDate(mSearchModel.commonDateTime.getValue().currentDateTime, DailyCalendar.ISO_8601_FORMAT));
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);

            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT);

            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            startActivityForResult(StayOutboundCalendarActivity.newInstance(getActivity()//
                , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 77), true), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLock();
        }
    }

    @Override
    public void onStayOutboundPeopleClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent;

        if (mSearchModel.stayOutboundViewModel.people.getValue() == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults, mSearchModel.stayOutboundViewModel.people.getValue().getChildAgeList());
        }

        startActivityForResult(intent, SearchActivity.REQUEST_CODE_STAY_OUTBOUND_PEOPLE);
    }

    @Override
    public void onStayOutboundDoSearchClick()
    {
        startActivity(StayOutboundListActivity.newInstance(getActivity(), mSearchModel.stayOutboundViewModel.suggest.getValue()//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults, mSearchModel.stayOutboundViewModel.people.getValue().getChildAgeList(), null));
    }

    @Override
    public void onGourmetSuggestClick()
    {

    }

    @Override
    public void onGourmetCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        TodayDateTime todayDateTime = new TodayDateTime();
        todayDateTime.openDateTime = mSearchModel.commonDateTime.getValue().openDateTime;
        todayDateTime.closeDateTime = mSearchModel.commonDateTime.getValue().closeDateTime;
        todayDateTime.currentDateTime = mSearchModel.commonDateTime.getValue().currentDateTime;
        todayDateTime.dailyDateTime = mSearchModel.commonDateTime.getValue().dailyDateTime;

        startActivityForResult(GourmetCalendarActivity.newInstance(getActivity(), todayDateTime //
            , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, true), SearchActivity.REQUEST_CODE_GOURMET_CALENDAR);
    }

    @Override
    public void onGourmetDoSearchClick()
    {

    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity, new SearchViewModel.SearchViewModelFactory()).get(SearchViewModel.class);
        mSearchModel.stayViewModel = ViewModelProviders.of(activity, new SearchViewModel.SearchStayViewModel.SearchStayViewModelFactory()).get(SearchViewModel.SearchStayViewModel.class);
        mSearchModel.stayOutboundViewModel = ViewModelProviders.of(activity, new SearchViewModel.SearchStayOutboundViewModel.SearchStayOutboundViewModelFactory()).get(SearchViewModel.SearchStayOutboundViewModel.class);
        mSearchModel.gourmetViewModel = ViewModelProviders.of(activity, new SearchViewModel.SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchViewModel.SearchGourmetViewModel.class);

        mSearchModel.serviceType.observe(activity, new Observer<Constants.ServiceType>()
        {
            @Override
            public void onChanged(@Nullable Constants.ServiceType serviceType)
            {
                switch (serviceType)
                {
                    case HOTEL:
                        showSearchStay();
                        break;

                    case GOURMET:
                        showSearchGourmet();
                        break;

                    case OB_STAY:
                        showSearchStayOutbound();
                        break;
                }

                unLockAll();
            }
        });

        // Stay
        mSearchModel.stayViewModel.suggest.observe(activity, new Observer<StaySuggest>()
        {
            @Override
            public void onChanged(@Nullable StaySuggest staySuggest)
            {
                getViewInterface().setSearchStaySuggestText(staySuggest.displayName);

                getViewInterface().setSearchStayButtonEnabled(DailyTextUtils.isTextEmpty(staySuggest.displayName) == false);
            }
        });

        mSearchModel.stayViewModel.bookDateTime.observe(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                    , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getNights()));
            }
        });

        // StayOutbound
        mSearchModel.stayOutboundViewModel.suggest.observe(activity, new Observer<StayOutboundSuggest>()
        {
            @Override
            public void onChanged(@Nullable StayOutboundSuggest stayOutboundSuggest)
            {
                getViewInterface().setSearchStayOutboundSuggestText(stayOutboundSuggest.display);

                getViewInterface().setSearchStayOutboundButtonEnabled(DailyTextUtils.isTextEmpty(stayOutboundSuggest.display) == false);
            }
        });

        mSearchModel.stayOutboundViewModel.bookDateTime.observe(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                getViewInterface().setSearchStayOutboundCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                    , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getNights()));
            }
        });

        mSearchModel.stayOutboundViewModel.people.observe(activity, new Observer<People>()
        {
            @Override
            public void onChanged(@Nullable People people)
            {
                getViewInterface().setSearchStayOutboundPeopleText(people.toString(getActivity()));
            }
        });

        mSearchModel.stayOutboundViewModel.people.setValue(new People(People.DEFAULT_ADULTS, null));

        // Gourmet
        mSearchModel.gourmetViewModel.suggest.observe(activity, new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String suggest)
            {
                getViewInterface().setSearchGourmetSuggestText(suggest);

                getViewInterface().setSearchGourmetButtonEnabled(DailyTextUtils.isTextEmpty(suggest) == false);
            }
        });

        mSearchModel.gourmetViewModel.bookDateTime.observe(activity, new Observer<GourmetBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable GourmetBookDateTime gourmetBookDateTime)
            {
                getViewInterface().setSearchGourmetCalendarText(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));
            }
        });
    }

    private void setPeople(int numberOfAdults, ArrayList<Integer> childAgeList)
    {
        if (mSearchModel.stayOutboundViewModel.people.getValue() == null)
        {
            mSearchModel.stayOutboundViewModel.people.setValue(new People(People.DEFAULT_ADULTS, null));
        }

        mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults = numberOfAdults;
        mSearchModel.stayOutboundViewModel.people.getValue().setChildAgeList(childAgeList);
        mSearchModel.stayOutboundViewModel.people.setValue(mSearchModel.stayOutboundViewModel.people.getValue());
    }

    private void showSearchStay()
    {
        if (mSearchModel.stayViewModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(mSearchModel.stayViewModel.suggest.getValue().displayName) == true)
        {
            getViewInterface().setSearchStaySuggestText(null);
            getViewInterface().setSearchStayButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchStaySuggestText(mSearchModel.stayViewModel.suggest.getValue().displayName);
            getViewInterface().setSearchStayButtonEnabled(true);
        }

        getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getNights()));

        getViewInterface().showSearchStay();
    }

    private void showSearchStayOutbound()
    {
        if (mSearchModel.stayOutboundViewModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(mSearchModel.stayOutboundViewModel.suggest.getValue().display) == true)
        {
            getViewInterface().setSearchStayOutboundSuggestText(null);
            getViewInterface().setSearchStayOutboundButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchStayOutboundSuggestText(mSearchModel.stayOutboundViewModel.suggest.getValue().display);
            getViewInterface().setSearchStayOutboundButtonEnabled(true);
        }

        getViewInterface().setSearchStayOutboundCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getNights()));

        getViewInterface().showSearchStayOutbound();
    }

    private void showSearchGourmet()
    {
        getViewInterface().setSearchGourmetSuggestText(mSearchModel.gourmetViewModel.suggest.getValue());

        getViewInterface().setSearchGourmetCalendarText(mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime("yyyy.MM.dd(EEE)"));

        getViewInterface().showSearchGourmet();

        getViewInterface().setSearchGourmetButtonEnabled(DailyTextUtils.isTextEmpty(mSearchModel.gourmetViewModel.suggest.getValue()) == false);
    }
}
