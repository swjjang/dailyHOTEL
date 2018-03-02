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
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.RecentlyDbPlace;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.campaigntag.gourmet.GourmetCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.campaigntag.stay.StayCampaignTagListActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.suggest.SearchGourmetSuggestActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.screen.home.search.stay.outbound.suggest.SearchStayOutboundSuggestActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.calendar.StayOutboundCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.activity.PlaceSearchResultActivity;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
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

    DailyDeepLink mDailyDeepLink;

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
        if (DailyIntentUtils.hasDeepLink(intent) == true)
        {
            try
            {
                mDailyDeepLink = DailyIntentUtils.getDeepLink(intent);
                parseDeepLink(mDailyDeepLink);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                clearDeepLink();
                mEnterServiceType = Constants.ServiceType.HOTEL;
            }
        } else
        {
            try
            {
                parseIntent(intent);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
                mEnterServiceType = Constants.ServiceType.HOTEL;
            }
        }

        return true;
    }

    private void parseDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            throw new NullPointerException("dailyDeepLink == null");
        }

        if (dailyDeepLink.isInternalDeepLink() == true)
        {

        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isSearchHomeView() == true)
            {
                parseSearchHomeViewDeepLink(externalDeepLink);
            } else if (externalDeepLink.isCampaignTagListView() == true)
            {
                parseCampaignTagListView(externalDeepLink);
            } else if (externalDeepLink.isHotelSearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.HOTEL;
            } else if (externalDeepLink.isGourmetSearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.GOURMET;
            } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.OB_STAY;
            }
        } else
        {
            throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
        }
    }

    private void parseSearchHomeViewDeepLink(DailyExternalDeepLink dailyExternalDeepLink)
    {
        if (dailyExternalDeepLink == null)
        {
            throw new NullPointerException("dailyExternalDeepLink == null");
        }

        switch (dailyExternalDeepLink.getPlaceType())
        {
            case DailyDeepLink.STAY:
                mEnterServiceType = Constants.ServiceType.HOTEL;
                break;

            case DailyDeepLink.STAY_OUTBOUND:
                mEnterServiceType = Constants.ServiceType.OB_STAY;
                break;

            case DailyDeepLink.GOURMET:
                mEnterServiceType = Constants.ServiceType.GOURMET;
                break;

            default:
                throw new RuntimeException("Invalid DeepLink : " + dailyExternalDeepLink.getDeepLink());
        }
    }

    private void parseCampaignTagListView(DailyExternalDeepLink dailyExternalDeepLink)
    {
        if (dailyExternalDeepLink == null)
        {
            throw new NullPointerException("dailyExternalDeepLink == null");
        }

        switch (dailyExternalDeepLink.getPlaceType())
        {
            case DailyDeepLink.STAY:
                mEnterServiceType = Constants.ServiceType.HOTEL;
                break;

            case DailyDeepLink.GOURMET:
                mEnterServiceType = Constants.ServiceType.GOURMET;
                break;

            default:
                throw new RuntimeException("Invalid DeepLink : " + dailyExternalDeepLink.getDeepLink());
        }
    }

    private void clearDeepLink()
    {
        if (mDailyDeepLink == null)
        {
            return;
        }

        mDailyDeepLink.clear();
        mDailyDeepLink = null;
    }

    private void parseIntent(Intent intent) throws Exception
    {
        if (intent == null)
        {
            throw new NullPointerException("intent == null");
        }

        mEnterServiceType = Constants.ServiceType.valueOf(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_SERVICE_TYPE));

        if (mEnterServiceType == null)
        {
            throw new NullPointerException("intent == null");
        }

        switch (mEnterServiceType)
        {
            case HOTEL:
                if (DailyIntentUtils.hasIntentExtras(intent, SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME//
                    , SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME) == true)
                {
                    String checkInDateTime = intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
                    String checkOutDateTime = intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

                    mSearchModel.setStayBookDateTime(checkInDateTime, checkOutDateTime);
                }
                break;

            case OB_STAY:
                if (DailyIntentUtils.hasIntentExtras(intent, SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME//
                    , SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME) == true)
                {
                    StayBookDateTime stayOutboundBookDateTime = new StayBookDateTime();

                    stayOutboundBookDateTime.setCheckInDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME));
                    stayOutboundBookDateTime.setCheckOutDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME));

                    mSearchModel.stayOutboundViewModel.bookDateTime.setValue(stayOutboundBookDateTime);
                }
                break;

            case GOURMET:
                if (DailyIntentUtils.hasIntentExtras(intent, SearchActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME) == true)
                {
                    GourmetBookDateTime gourmetBookDateTime = new GourmetBookDateTime();
                    gourmetBookDateTime.setVisitDateTime(intent.getStringExtra(SearchActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME));

                    mSearchModel.gourmetViewModel.bookDateTime.setValue(gourmetBookDateTime);
                }
                break;

            default:
                throw new RuntimeException("Invalid intent");
        }
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

        Util.restartApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case SearchActivity.REQUEST_CODE_STAY_SUGGEST:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onOtherCategoryActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY, data);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        onOtherCategoryActivityResult(resultCode, data);
                        break;
                }
                break;

            case SearchActivity.REQUEST_CODE_STAY_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                        String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

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
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onOtherCategoryActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND, data);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        onOtherCategoryActivityResult(resultCode, data);
                        break;
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
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onOtherCategoryActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET, data);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        onOtherCategoryActivityResult(resultCode, data);
                        break;
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
                        GourmetSuggestParcel suggestParcel = data.getParcelableExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel != null)
                        {
                            mSearchModel.gourmetViewModel.suggest.setValue(suggestParcel.getSuggest());
                        }

                        String visitDateTime = data.getStringExtra(PlaceSearchResultActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);

                        mSearchModel.gourmetViewModel.bookDateTime.setValue(new GourmetBookDateTime(visitDateTime));
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

                if (mDailyDeepLink != null && mDailyDeepLink.isExternalDeepLink() == true)
                {
                    DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                    if (externalDeepLink.isCampaignTagListView() == true)
                    {
                        switch (externalDeepLink.getPlaceType())
                        {
                            case DailyDeepLink.STAY:
                                moveDeepLinkStayCampaignTag(mSearchModel.commonDateTime.getValue(), externalDeepLink);
                                break;

                            case DailyDeepLink.GOURMET:
                                moveDeepLinkGourmetCampaignTag(mSearchModel.commonDateTime.getValue(), externalDeepLink);
                                break;
                        }

                        mDailyDeepLink.clear();
                        mDailyDeepLink = null;
                    } else if (externalDeepLink.isHotelSearchResultView() == true)
                    {
                        moveDeepLinkStaySearchResult(mSearchModel.commonDateTime.getValue(), externalDeepLink);
                    } else if (externalDeepLink.isGourmetSearchResultView() == true)
                    {
                        moveDeepLinkGourmetSearchResult(mSearchModel.commonDateTime.getValue(), externalDeepLink);
                    } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
                    {
                        moveDeepLinkStayOutboundSearchResult(mSearchModel.commonDateTime.getValue(), externalDeepLink);
                    }
                }
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

        mAnalytics.onEventStayClick(getActivity());
    }

    @Override
    public void onStayOutboundClick()
    {
        if (mSearchModel == null || mSearchModel.serviceType.getValue() == Constants.ServiceType.OB_STAY || lock() == true)
        {
            return;
        }

        mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);

        mAnalytics.onEventStayOutboundClick(getActivity());
    }

    @Override
    public void onGourmetClick()
    {
        if (mSearchModel == null || mSearchModel.serviceType.getValue() == Constants.ServiceType.GOURMET || lock() == true)
        {
            return;
        }

        mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);

        mAnalytics.onEventGourmetClick(getActivity());
    }

    @Override
    public void onStaySuggestClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            startActivityForResult(SearchStaySuggestActivity.newInstance(getActivity()//
                , null //
                , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)), SearchActivity.REQUEST_CODE_STAY_SUGGEST);

            mAnalytics.onEventStaySuggestClick(getActivity());
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

        final int DAYS_OF_MAX_COUNT = 60;
        final int NIGHTS_OF_MAX_COUNT = 59;

        try
        {
            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(DailyCalendar.convertDate(mSearchModel.commonDateTime.getValue().dailyDateTime, DailyCalendar.ISO_8601_FORMAT));

            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);

            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchModel.stayViewModel.bookDateTime.getValue();

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, NIGHTS_OF_MAX_COUNT//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , ScreenUtils.dpToPx(getActivity(), 44), true);

            startActivityForResult(intent, SearchActivity.REQUEST_CODE_STAY_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }

        mAnalytics.onEventStayCalendarClick(getActivity());
    }

    @Override
    public void onStayDoSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            startActivityForResult(StaySearchResultActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime()//
                , mSearchModel.stayViewModel.bookDateTime.getValue().getStayBookingDay()//
                , mSearchModel.stayViewModel.inputString, mSearchModel.stayViewModel.suggest.getValue(), null, AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);

            mAnalytics.onEventStayDoSearch(getActivity(), mSearchModel.stayViewModel.suggest.getValue());
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

        startStayCampaignTag(campaignTag.index, campaignTag.campaignTag//
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT) //
            , mSearchModel.stayViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
    }

    @Override
    public void onStayOutboundSuggestClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(SearchStayOutboundSuggestActivity.newInstance(getActivity(), ""), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST);

        mAnalytics.onEventStayOutboundSuggestClick(getActivity());
    }

    @Override
    public void onStayOutboundCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

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
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 41), true), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_CALENDAR);
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

        mAnalytics.onEventStayOutboundPeopleClick(getActivity());
    }

    @Override
    public void onStayOutboundDoSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        StayOutboundListAnalyticsParam analyticsParam = new StayOutboundListAnalyticsParam();
        analyticsParam.keyword = mSearchModel.stayOutboundViewModel.inputString;
        analyticsParam.analyticsClickType = mSearchModel.stayOutboundViewModel.clickType;

        startActivityForResult(StayOutboundListActivity.newInstance(getActivity(), mSearchModel.stayOutboundViewModel.suggest.getValue()//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchModel.stayOutboundViewModel.people.getValue().numberOfAdults//
            , mSearchModel.stayOutboundViewModel.people.getValue().getChildAgeList(), analyticsParam)//
            , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT);

        mAnalytics.onEventStayOutboundDoSearch(getActivity(), mSearchModel.stayOutboundViewModel.suggest.getValue());
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

        addCompositeDisposable(getViewInterface().getStayOutboundSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onGourmetSuggestClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            startActivityForResult(SearchGourmetSuggestActivity.newInstance(getActivity()//
                , null //
                , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT))//
                , SearchActivity.REQUEST_CODE_GOURMET_SUGGEST);

            mAnalytics.onEventGourmetSuggestClick(getActivity());
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
        if (lock() == true)
        {
            return;
        }

        try
        {
            startActivityForResult(GourmetSearchResultActivity.newInstance(getActivity(), mSearchModel.commonDateTime.getValue().getTodayDateTime()//
                , mSearchModel.gourmetViewModel.bookDateTime.getValue().getGourmetBookingDay()//
                , mSearchModel.gourmetViewModel.inputString, mSearchModel.gourmetViewModel.suggest.getValue(), null, AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);

            mAnalytics.onEventGourmetDoSearch(getActivity(), mSearchModel.gourmetViewModel.suggest.getValue());
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

        startGourmetCampaignTag(campaignTag.index, campaignTag.campaignTag//
            , mSearchModel.gourmetViewModel.bookDateTime.getValue().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mSearchModel = ViewModelProviders.of(activity, new SearchViewModel.SearchViewModelFactory()).get(SearchViewModel.class);
        mSearchModel.stayViewModel = ViewModelProviders.of(activity, new SearchStayViewModel.SearchStayViewModelFactory()).get(SearchStayViewModel.class);
        mSearchModel.stayOutboundViewModel = ViewModelProviders.of(activity, new SearchStayOutboundViewModel.SearchStayOutboundViewModelFactory()).get(SearchStayOutboundViewModel.class);
        mSearchModel.gourmetViewModel = ViewModelProviders.of(activity, new SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetViewModel.class);

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

    void showSearchStay()
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

    void showSearchStayOutbound()
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

    void showSearchGourmet()
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

    private void onOtherCategoryActivityResult(int category, Intent intent)
    {
        switch (category)
        {
            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
            {
                if (intent != null)
                {
                    try
                    {
                        StaySuggestParcel suggestParcel = intent.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.stayViewModel.suggest.setValue(suggestParcel.getSuggest());
                        mSearchModel.stayViewModel.inputString = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                        getViewInterface().refreshStay();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                if (mSearchModel.serviceType.getValue() != Constants.ServiceType.HOTEL)
                {
                    mSearchModel.serviceType.setValue(Constants.ServiceType.HOTEL);
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
            {
                if (intent != null)
                {
                    StayOutboundSuggestParcel suggestParcel = intent.getParcelableExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                    mSearchModel.stayOutboundViewModel.inputString = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);
                    mSearchModel.stayOutboundViewModel.clickType = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_CLICK_TYPE);

                    if (suggestParcel != null)
                    {
                        mSearchModel.stayOutboundViewModel.suggest.setValue(suggestParcel.getSuggest());
                    }

                    getViewInterface().refreshStayOutbound();
                }

                if (mSearchModel.serviceType.getValue() != Constants.ServiceType.OB_STAY)
                {
                    mSearchModel.serviceType.setValue(Constants.ServiceType.OB_STAY);
                }
                break;
            }

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
            {
                if (intent != null)
                {
                    try
                    {
                        GourmetSuggestParcel gourmetSuggestParcel = intent.getParcelableExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);
                        mSearchModel.gourmetViewModel.suggest.setValue(gourmetSuggestParcel.getSuggest());
                        mSearchModel.gourmetViewModel.inputString = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                        getViewInterface().refreshGourmet();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }

                if (mSearchModel.serviceType.getValue() != Constants.ServiceType.GOURMET)
                {
                    mSearchModel.serviceType.setValue(Constants.ServiceType.GOURMET);
                }
                break;
            }
        }
    }

    void startStayCampaignTag(int index, String campaignTag, String checkInDateTime, String checkOutDateTime)
    {
        if (index <= 0 || DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        startActivityForResult(StayCampaignTagListActivity.newInstance(getActivity() //
            , index, campaignTag, checkInDateTime, checkOutDateTime)//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
    }

    void startGourmetCampaignTag(int index, String campaignTag, String visitDateTime)
    {
        if (index <= 0 || DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        startActivityForResult(GourmetCampaignTagListActivity.newInstance(getActivity() //
            , index, campaignTag, visitDateTime)//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void moveDeepLinkStayCampaignTag(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);

        int index;
        try
        {
            index = Integer.parseInt(externalDeepLink.getIndex());
        } catch (Exception e)
        {
            index = -1;
        }

        if (stayBookDateTime == null || index < 0)
        {
            return;
        }

        startStayCampaignTag(index, null, stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
    }

    void moveDeepLinkGourmetCampaignTag(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        GourmetBookDateTime gourmetBookDateTime = externalDeepLink.getGourmetBookDateTime(commonDateTime, externalDeepLink);

        int index;
        try
        {
            index = Integer.parseInt(externalDeepLink.getIndex());
        } catch (Exception e)
        {
            index = -1;
        }

        if (gourmetBookDateTime == null || index < 0)
        {
            return;
        }

        startGourmetCampaignTag(index, null, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
    }

    void moveDeepLinkStaySearchResult(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
        String word = externalDeepLink.getSearchWord();

        if (stayBookDateTime == null || DailyTextUtils.isTextEmpty(word) == true)
        {
            return;
        }

        Constants.SortType sortType = externalDeepLink.getSorting();
        StaySuggest staySuggest = new StaySuggest(StaySuggest.MENU_TYPE_DIRECT, StaySuggest.CATEGORY_DIRECT, word);

        try
        {
            startActivityForResult(StaySearchResultActivity.newInstance(getActivity()//
                , commonDateTime.getTodayDateTime(), stayBookDateTime.getStayBookingDay()//
                , word, staySuggest, sortType, AnalyticsManager.Screen.SEARCH_MAIN), SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void moveDeepLinkGourmetSearchResult(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        GourmetBookDateTime gourmetBookDateTime = externalDeepLink.getGourmetBookDateTime(commonDateTime, externalDeepLink);
        String word = externalDeepLink.getSearchWord();

        if (gourmetBookDateTime == null || DailyTextUtils.isTextEmpty(word) == true)
        {
            return;
        }

        Constants.SortType sortType = externalDeepLink.getSorting();
        GourmetSuggest gourmetSuggest = new GourmetSuggest(GourmetSuggest.MENU_TYPE_DIRECT, GourmetSuggest.CATEGORY_DIRECT, word);

        try
        {
            startActivityForResult(GourmetSearchResultActivity.newInstance(getActivity()//
                , commonDateTime.getTodayDateTime(), gourmetBookDateTime.getGourmetBookingDay()//
                , word, gourmetSuggest, sortType, AnalyticsManager.Screen.SEARCH_MAIN)//
                , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void moveDeepLinkStayOutboundSearchResult(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(StayOutboundListActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT);
    }
}
