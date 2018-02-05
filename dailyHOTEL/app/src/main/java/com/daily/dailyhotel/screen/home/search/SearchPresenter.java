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
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayOutboundSuggest;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.campaigntag.gourmet.GourmetCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.suggest.SearchGourmetSuggestActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.calendar.StayOutboundCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.search.StayOutboundSearchSuggestActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.hotel.filter.StayCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchPresenter extends BaseExceptionPresenter<SearchActivity, SearchInterface.ViewInterface> implements SearchInterface.OnEventListener
{
    private SearchInterface.AnalyticsInterface mAnalytics;

    CommonRemoteImpl mCommonRemoteImpl;

    SearchViewModel mSearchModel;

    Constants.ServiceType mEnterServiceType; // 시작시에 받고 삭제한다.

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

        mCommonRemoteImpl = new CommonRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
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

        try
        {
            mEnterServiceType = Constants.ServiceType.valueOf(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_SERVICE_TYPE));

            switch (mEnterServiceType)
            {
                case HOTEL:
                    if (intent.hasExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME) == true//
                        && intent.hasExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME) == true)
                    {
                        StayBookDateTime stayBookDateTime = new StayBookDateTime();
                        stayBookDateTime.setCheckInDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME));
                        stayBookDateTime.setCheckOutDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME));

                        mSearchModel.stayViewModel.bookDateTime.setValue(stayBookDateTime);
                    }
                    break;

                case GOURMET:
                    if (intent.hasExtra(SearchActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME) == true)
                    {
                        GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
                        gourmetBookDateTime.setVisitDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME));

                        mSearchModel.gourmetViewModel.bookDateTime.setValue(gourmetBookDateTime);
                    }
                    break;

                case OB_STAY:
                    if (intent.hasExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME) == true//
                        && intent.hasExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME) == true)
                    {
                        StayBookDateTime stayOutboundBookDateTime = new StayBookDateTime();

                        stayOutboundBookDateTime.setCheckInDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME));
                        stayOutboundBookDateTime.setCheckOutDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME));

                        mSearchModel.stayOutboundViewModel.bookDateTime.setValue(stayOutboundBookDateTime);
                    }
                    break;

                default:
                    mEnterServiceType = Constants.ServiceType.HOTEL;
                    break;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            mEnterServiceType = Constants.ServiceType.HOTEL;
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
                        StaySuggestParcel suggestParcel = data.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.stayViewModel.suggest.setValue(suggestParcel.getSuggest());
                        mSearchModel.stayViewModel.inputString = data.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                        getViewInterface().refreshStay();
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
                if (data != null)
                {
                    try
                    {
                        StaySuggestParcel suggestParcel = data.getParcelableExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel != null)
                        {
                            mSearchModel.stayViewModel.suggest.setValue(suggestParcel.getSuggest());
                        }

                        String checkInDateTime = data.getStringExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
                        String checkOutDateTime = data.getStringExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

                        mSearchModel.stayViewModel.bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }

                getViewInterface().refreshStay();

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);
                        break;

                    default:
                        break;
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    StayOutboundSuggestParcel suggestParcel = data.getParcelableExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                    String keyword = data.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                    String clickType = data.getStringExtra(StayOutboundSearchSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE);

                    if (suggestParcel != null)
                    {
                        mSearchModel.stayOutboundViewModel.suggest.setValue(suggestParcel.getSuggest());
                    }

                    getViewInterface().refreshStayOutbound();
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
                        ExLog.e(e.toString());
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

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT:
                if (data != null)
                {
                    try
                    {
                        StayOutboundSuggestParcel suggestParcel = data.getParcelableExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel != null)
                        {
                            mSearchModel.stayOutboundViewModel.suggest.setValue(suggestParcel.getSuggest());
                        }

                        String checkInDateTime = data.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
                        String checkOutDateTime = data.getStringExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

                        mSearchModel.stayOutboundViewModel.bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));

                        int numberOfAdults = data.getIntExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_NUMBER_OF_ADULTS, People.DEFAULT_ADULTS);
                        ArrayList<Integer> arrayList = data.getIntegerArrayListExtra(StayOutboundListActivity.INTENT_EXTRA_DATA_CHILD_LIST);

                        setPeople(numberOfAdults, arrayList);
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }

                getViewInterface().refreshStayOutbound();

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.HOTEL);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);
                        break;

                    default:
                        break;
                }
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_SUGGEST:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        GourmetSuggestParcel gourmetSuggestParcel = data.getParcelableExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.gourmetViewModel.suggest.setValue(gourmetSuggestParcel.getSuggest());
                        mSearchModel.gourmetViewModel.inputString = data.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                        getViewInterface().refreshGourmet();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
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

            case SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT:
                if (data != null)
                {
                    try
                    {
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }

                getViewInterface().refreshGourmet();

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.HOTEL);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                        mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);
                        break;

                    default:
                        break;
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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<CommonDateTime, ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> apply(CommonDateTime commonDateTime) throws Exception
            {
                mSearchModel.commonDateTime.setValue(commonDateTime);

                if (mSearchModel.stayViewModel.bookDateTime.getValue() == null)
                {
                    StayBookDateTime stayBookDateTime = new StayBookDateTime();
                    stayBookDateTime.setCheckInDateTime(commonDateTime.dailyDateTime);
                    stayBookDateTime.setCheckOutDateTime(commonDateTime.dailyDateTime, 1);
                    mSearchModel.stayViewModel.bookDateTime.setValue(stayBookDateTime);
                }

                if (mSearchModel.stayOutboundViewModel.bookDateTime.getValue() == null)
                {
                    StayBookDateTime stayOutboundBookDateTime = new StayBookDateTime();
                    stayOutboundBookDateTime.setCheckInDateTime(mSearchModel.commonDateTime.getValue().currentDateTime);
                    stayOutboundBookDateTime.setCheckOutDateTime(mSearchModel.commonDateTime.getValue().currentDateTime, 1);
                    mSearchModel.stayOutboundViewModel.bookDateTime.setValue(stayOutboundBookDateTime);
                }

                if (mSearchModel.gourmetViewModel.bookDateTime.getValue() == null)
                {
                    GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
                    gourmetBookDateTime.setVisitDateTime(mSearchModel.commonDateTime.getValue().dailyDateTime);
                    mSearchModel.gourmetViewModel.bookDateTime.setValue(gourmetBookDateTime);
                }

                return getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread());
            }
        }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                if (mSearchModel.serviceType.getValue() == null && mEnterServiceType != null)
                {
                    mSearchModel.serviceType.setValue(mEnterServiceType);
                }

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleErrorAndFinish(throwable);
            }
        }));
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

        startActivityForResult(StayCalendarActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime()//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , StayCalendarActivity.DEFAULT_DOMESTIC_CALENDAR_DAY_OF_MAX_COUNT, AnalyticsManager.ValueType.SEARCH, true, true), SearchActivity.REQUEST_CODE_STAY_CALENDAR);
    }

    @Override
    public void onStayDoSearchClick()
    {
        try
        {
            startActivityForResult(StaySearchResultActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime()//
                , mSearchModel.stayViewModel.bookDateTime.getValue().getStayBookingDay()//
                , mSearchModel.stayViewModel.inputString, mSearchModel.stayViewModel.suggest.getValue(), AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onStayRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        if (recentlyDbPlace == null || lock() == true)
        {
            return;
        }

        StayDetailAnalyticsParam analyticsParam = new StayDetailAnalyticsParam();

        startActivityForResult(StayDetailActivity.newInstance(getActivity() //
            , recentlyDbPlace.index, recentlyDbPlace.name, recentlyDbPlace.imageUrl//
            , StayDetailActivity.NONE_PRICE//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
            , SearchActivity.REQUEST_CODE_STAY_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
    }

    @Override
    public void onStayPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        startActivityForResult(StayCampaignTagListActivity.newInstance(getActivity() //
            , campaignTag.index, campaignTag.campaignTag//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT))//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
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
        startActivityForResult(StayOutboundListActivity.newInstance(getActivity(), mSearchModel.stayOutboundViewModel.suggest.getValue()//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults//
            , mSearchModel.stayOutboundViewModel.people.getValue().getChildAgeList(), null)//
            , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT);
    }

    @Override
    public void onStayOutboundRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        if (recentlyDbPlace == null || lock() == true)
        {
            return;
        }

        startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), recentlyDbPlace.index, recentlyDbPlace.name//
            , recentlyDbPlace.englishName, recentlyDbPlace.imageUrl, StayOutboundDetailActivity.NONE_PRICE//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults, mSearchModel.stayOutboundViewModel.people.getValue().getChildAgeList()//
            , false, StayOutboundDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, null)//
            , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
    }

    @Override
    public void onStayOutboundPopularAreaClick(StayOutboundSuggest stayOutboundSuggest)
    {
        if (stayOutboundSuggest == null || lock() == true)
        {
            return;
        }

        mSearchModel.stayOutboundViewModel.suggest.setValue(stayOutboundSuggest);

        unLockAll();
    }

    @Override
    public void onGourmetSuggestClick()
    {
        try
        {
            startActivityForResult(SearchGourmetSuggestActivity.newInstance(getActivity()//
                , null //
                , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT))//
                , SearchActivity.REQUEST_CODE_GOURMET_SUGGEST);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onGourmetCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(GourmetCalendarActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime() //
            , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.ValueType.SEARCH, true, true), SearchActivity.REQUEST_CODE_GOURMET_CALENDAR);
    }

    @Override
    public void onGourmetDoSearchClick()
    {
        try
        {
            startActivityForResult(GourmetSearchResultActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime()//
                , mSearchModel.gourmetViewModel.bookDateTime.getValue().getGourmetBookingDay()//
                , mSearchModel.gourmetViewModel.inputString, mSearchModel.gourmetViewModel.suggest.getValue(), AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onGourmetRecentlySearchResultClick(RecentlyDbPlace recentlyDbPlace)
    {
        if (recentlyDbPlace == null || lock() == true)
        {
            return;
        }

        GourmetDetailAnalyticsParam analyticsParam = new GourmetDetailAnalyticsParam();

        startActivityForResult(GourmetDetailActivity.newInstance(getActivity() //
            , recentlyDbPlace.index, recentlyDbPlace.name, recentlyDbPlace.imageUrl//
            , GourmetDetailActivity.NONE_PRICE//
            , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , null, false, false, false, false//
            , StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam)//
            , SearchActivity.REQUEST_CODE_GOURMET_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
    }

    @Override
    public void onGourmetPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        startActivityForResult(GourmetCampaignTagListActivity.newInstance(getActivity() //
            , campaignTag.index, campaignTag.campaignTag//
            , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)) //
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
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
        mSearchModel.gourmetViewModel.suggest.observe(activity, new Observer<GourmetSuggest>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggest suggest)
            {
                getViewInterface().setSearchGourmetSuggestText(suggest.displayName);

                getViewInterface().setSearchGourmetButtonEnabled(DailyTextUtils.isTextEmpty(suggest.displayName) == false);
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
        if (mSearchModel.gourmetViewModel.suggest.getValue() == null || DailyTextUtils.isTextEmpty(mSearchModel.gourmetViewModel.suggest.getValue().displayName) == true)
        {
            getViewInterface().setSearchGourmetSuggestText(null);
            getViewInterface().setSearchGourmetButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchGourmetSuggestText(mSearchModel.gourmetViewModel.suggest.getValue().displayName);
            getViewInterface().setSearchGourmetButtonEnabled(true);
        }

        getViewInterface().setSearchGourmetCalendarText(mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime("yyyy.MM.dd(EEE)"));

        getViewInterface().showSearchGourmet();
    }
}
