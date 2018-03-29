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
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;
import com.daily.dailyhotel.parcel.StayOutboundSuggestParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcel;
import com.daily.dailyhotel.parcel.analytics.StayOutboundListAnalyticsParam;
import com.daily.dailyhotel.repository.local.model.GourmetSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.StayObSearchResultHistory;
import com.daily.dailyhotel.repository.local.model.StaySearchResultHistory;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.gourmet.GourmetCalendarActivity;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.suggest.SearchGourmetSuggestActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.suggest.SearchStaySuggestActivity;
import com.daily.dailyhotel.screen.home.search.stay.outbound.suggest.SearchStayOutboundSuggestActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.people.SelectPeopleActivity;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
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

    SearchViewModel mSearchViewModel;
    CommonDateTimeViewModel mCommonDateTimeViewModel;

    Constants.ServiceType mEnterServiceType = Constants.ServiceType.HOTEL;

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
            } else if (externalDeepLink.isStaySearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.HOTEL;
            } else if (externalDeepLink.isGourmetSearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.GOURMET;
            } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
            {
                mEnterServiceType = Constants.ServiceType.OB_STAY;
            } else if (externalDeepLink.isPlaceDetailView() == true)
            {
                String placeType = externalDeepLink.getPlaceType();
                if (placeType != null)
                {
                    switch (placeType)
                    {
                        case DailyDeepLink.STAY_OUTBOUND:
                            mEnterServiceType = Constants.ServiceType.OB_STAY;
                            break;

                        default:
                            mDailyDeepLink.clear();
                            mDailyDeepLink = null;
                            break;
                    }
                }
            } else
            {
                mDailyDeepLink.clear();
                mDailyDeepLink = null;
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
                mSearchViewModel.setStayBookDateTime(intent, SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);
                break;

            case OB_STAY:
                mSearchViewModel.setStayOutboundBookDateTime(intent, SearchActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, SearchActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);
                break;

            case GOURMET:
                mSearchViewModel.setGourmetBookDateTime(intent, SearchActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);
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
                onStaySuggestActivityResult(resultCode, data);
                break;

            case SearchActivity.REQUEST_CODE_STAY_CALENDAR:
                onStayCalendarActivityResult(resultCode, data);
                break;

            case SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT:
                onStaySearchResultActivityResult(resultCode, data);
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST:
                onStayOutboundSuggestActivityResult(resultCode, data);
                break;

            case SearchActivity.REQUEST_CODE_STAY_OUTBOUND_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        mSearchViewModel.setStayOutboundBookDateTime(data, StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME, StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);
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
                            mSearchViewModel.stayOutboundViewModel.setSuggest(suggestParcel.getSuggest());
                        }

                        mSearchViewModel.setStayOutboundBookDateTime(data, StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, StayOutboundListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

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
                        mSearchViewModel.setServiceType(Constants.ServiceType.HOTEL);
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                        mSearchViewModel.setServiceType(Constants.ServiceType.GOURMET);
                        break;

                    default:
                        break;
                }
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_SUGGEST:
                onGourmetSuggestActivityResult(resultCode, data);
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_CALENDAR:
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    try
                    {
                        mSearchViewModel.setGourmetBookDateTime(data, GourmetCalendarActivity.INTENT_EXTRA_DATA_VISIT_DATETIME);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
                break;

            case SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT:
                onGourmetSearchResult(resultCode, data);
                break;
        }
    }

    private void onStaySuggestActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                selectActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY, intent);
                break;

            default:
                selectActivityResult(resultCode, intent);
                break;
        }
    }

    private void onStayOutboundSuggestActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                selectActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND, intent);
                break;

            default:
                selectActivityResult(resultCode, intent);
                break;
        }
    }

    private void onGourmetSuggestActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                selectActivityResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET, intent);
                break;

            default:
                selectActivityResult(resultCode, intent);
                break;
        }
    }

    private void onGourmetSearchResult(int resultCode, Intent intent)
    {
        if (intent != null)
        {
            try
            {
                GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_SUGGEST);

                if (suggestParcel != null)
                {
                    mSearchViewModel.gourmetViewModel.setSuggest(suggestParcel.getSuggest());
                }

                mSearchViewModel.setGourmetBookDateTime(intent, SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);
                mSearchViewModel.gourmetViewModel.inputKeyword = intent.getStringExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD);

            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        getViewInterface().refreshGourmet();

        switch (resultCode)
        {
            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                mSearchViewModel.setServiceType(Constants.ServiceType.HOTEL);
                break;

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                mSearchViewModel.setServiceType(Constants.ServiceType.OB_STAY);
                break;

            default:
                break;
        }
    }

    private void selectActivityResult(int category, Intent intent)
    {
        switch (category)
        {
            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY:
                selectActivityResultStay(intent);
                break;

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                selectActivityResultStayOutbound(intent);
                break;

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                selectActivityResultGourmet(intent);
                break;
        }
    }

    private void updateBookDateTime(Constants.ServiceType expectedServiceType, Constants.ServiceType actualServiceType)
    {
        if (expectedServiceType == null || actualServiceType == null || expectedServiceType == actualServiceType)
        {
            return;
        }

        String startDate = null;
        String endDate = null;

        switch (actualServiceType)
        {
            case HOTEL:
            {
                StayBookDateTime stayBookDateTime = mSearchViewModel.stayViewModel.getBookDateTime();

                if (stayBookDateTime == null)
                {
                    return;
                }

                startDate = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
                endDate = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);
                break;
            }

            case GOURMET:
            {
                GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.gourmetViewModel.getBookDateTime();

                if (gourmetBookDateTime == null)
                {
                    return;
                }

                startDate = gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT);
                endDate = null;
                break;
            }

            case OB_STAY:
            {
                StayBookDateTime stayBookDateTime = mSearchViewModel.stayOutboundViewModel.getBookDateTime();

                if (stayBookDateTime == null)
                {
                    return;
                }

                startDate = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
                endDate = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);
                break;
            }
        }

        if (DailyTextUtils.isTextEmpty(startDate))
        {
            return;
        }

        try
        {
            switch (expectedServiceType)
            {
                case HOTEL:
                {
                    if (DailyTextUtils.isTextEmpty(endDate))
                    {
                        mSearchViewModel.stayViewModel.setBookDateTime(startDate, 0, startDate, 1);
                    } else
                    {
                        mSearchViewModel.stayViewModel.setBookDateTime(startDate, endDate);
                    }

                    break;
                }

                case GOURMET:
                {
                    mSearchViewModel.gourmetViewModel.setBookDateTime(startDate);
                    break;
                }

                case OB_STAY:
                {
                    if (DailyTextUtils.isTextEmpty(endDate))
                    {
                        mSearchViewModel.stayOutboundViewModel.setBookDateTime(startDate, 0, startDate, 1);
                    } else
                    {
                        mSearchViewModel.stayOutboundViewModel.setBookDateTime(startDate, endDate);
                    }
                    break;
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void selectActivityResultStay(Intent intent)
    {
        if (intent != null)
        {
            try
            {
                StaySuggestParcel suggestParcel = intent.getParcelableExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_SUGGEST);

                if (suggestParcel != null)
                {
                    mSearchViewModel.setStaySuggest(suggestParcel.getSuggest());
                }

                mSearchViewModel.stayViewModel.inputKeyword = intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                try
                {
                    Constants.ServiceType serviceType = Constants.ServiceType.valueOf( //
                        intent.getStringExtra(SearchStaySuggestActivity.INTENT_EXTRA_DATA_ORIGIN_SERVICE_TYPE));

                    updateBookDateTime(Constants.ServiceType.HOTEL, serviceType);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                getViewInterface().refreshStay();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.HOTEL);
    }

    private void selectActivityResultStayOutbound(Intent intent)
    {
        if (intent != null)
        {
            StayOutboundSuggestParcel suggestParcel = intent.getParcelableExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);

            if (suggestParcel != null)
            {
                mSearchViewModel.stayOutboundViewModel.setSuggest(suggestParcel.getSuggest());
            }

            mSearchViewModel.stayOutboundViewModel.inputKeyword = intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

            try
            {
                Constants.ServiceType serviceType = Constants.ServiceType.valueOf( //
                    intent.getStringExtra(SearchStayOutboundSuggestActivity.INTENT_EXTRA_DATA_ORIGIN_SERVICE_TYPE));

                updateBookDateTime(Constants.ServiceType.OB_STAY, serviceType);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            getViewInterface().refreshStayOutbound();
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.OB_STAY);
    }

    private void selectActivityResultGourmet(Intent intent)
    {
        if (intent != null)
        {
            try
            {
                GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_SUGGEST);

                if (suggestParcel != null)
                {
                    mSearchViewModel.gourmetViewModel.setSuggest(suggestParcel.getSuggest());
                }

                mSearchViewModel.gourmetViewModel.inputKeyword = intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_KEYWORD);

                try
                {
                    Constants.ServiceType serviceType = Constants.ServiceType.valueOf( //
                        intent.getStringExtra(SearchGourmetSuggestActivity.INTENT_EXTRA_DATA_ORIGIN_SERVICE_TYPE));

                    updateBookDateTime(Constants.ServiceType.GOURMET, serviceType);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

                getViewInterface().refreshGourmet();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.GOURMET);
    }

    private void onStayCalendarActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                try
                {
                    mSearchViewModel.setStayBookDateTime(intent, StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME, StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
                break;
        }
    }

    private void onStaySearchResultActivityResult(int resultCode, Intent intent)
    {
        if (intent != null)
        {
            try
            {
                StaySuggestParcel suggestParcel = intent.getParcelableExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_SUGGEST);

                if (suggestParcel != null)
                {
                    mSearchViewModel.setStaySuggest(suggestParcel.getSuggest());
                }

                mSearchViewModel.setStayBookDateTime(intent, SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);
            } catch (Exception e)
            {
                ExLog.e(e.toString());
            }
        }

        getViewInterface().refreshStay();

        switch (resultCode)
        {
            case Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND:
                mSearchViewModel.setServiceType(Constants.ServiceType.OB_STAY);
                break;

            case Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET:
                mSearchViewModel.setServiceType(Constants.ServiceType.GOURMET);
                break;

            default:
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
                mCommonDateTimeViewModel.commonDateTime = commonDateTime;

                if (mSearchViewModel.stayViewModel.getBookDateTime() == null)
                {
                    mSearchViewModel.setStayBookDateTime(commonDateTime.dailyDateTime, 0, commonDateTime.dailyDateTime, 1);
                }

                if (mSearchViewModel.stayOutboundViewModel.getBookDateTime() == null)
                {
                    mSearchViewModel.setStayOutboundBookDateTime(commonDateTime.dailyDateTime, 0, commonDateTime.dailyDateTime, 1);
                }

                if (mSearchViewModel.gourmetViewModel.getBookDateTime() == null)
                {
                    mSearchViewModel.setGourmetBookDateTime(commonDateTime.dailyDateTime);
                }

                return getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread());
            }
        }).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                if (mSearchViewModel.serviceType.getValue() == null && mEnterServiceType != null)
                {
                    mSearchViewModel.setServiceType(mEnterServiceType);
                }

                unLockAll();

                CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;

                if (mDailyDeepLink != null && mDailyDeepLink.isExternalDeepLink() == true)
                {
                    DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                    if (externalDeepLink.isCampaignTagListView() == true)
                    {
                        switch (externalDeepLink.getPlaceType())
                        {
                            case DailyDeepLink.STAY:
                                moveDeepLinkStayCampaignTag(externalDeepLink);
                                break;

                            case DailyDeepLink.GOURMET:
                                moveDeepLinkGourmetCampaignTag(externalDeepLink);
                                break;
                        }

                        mDailyDeepLink.clear();
                        mDailyDeepLink = null;
                    } else if (externalDeepLink.isStaySearchResultView() == true)
                    {
                        moveDeepLinkStaySearchResult(commonDateTime, externalDeepLink);
                    } else if (externalDeepLink.isGourmetSearchResultView() == true)
                    {
                        moveDeepLinkGourmetSearchResult(externalDeepLink);
                    } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
                    {
                        moveDeepLinkStayOutboundSearchResult(commonDateTime, externalDeepLink);
                    } else if (externalDeepLink.isPlaceDetailView() == true)
                    {
                        String placeType = externalDeepLink.getPlaceType();
                        if (placeType != null)
                        {
                            switch (placeType)
                            {
                                case DailyDeepLink.STAY_OUTBOUND:

                                    startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
                                        , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_DETAIL);

                                    mDailyDeepLink.clear();
                                    mDailyDeepLink = null;
                                    break;

                                default:
                                    mDailyDeepLink.clear();
                                    mDailyDeepLink = null;
                                    break;
                            }
                        }
                    } else
                    {
                        mDailyDeepLink.clear();
                        mDailyDeepLink = null;
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
        if (mSearchViewModel == null || mSearchViewModel.serviceType.getValue() == Constants.ServiceType.HOTEL || lock() == true)
        {
            return;
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.HOTEL);

        mAnalytics.onEventStayClick(getActivity());
    }

    @Override
    public void onStayOutboundClick()
    {
        if (mSearchViewModel == null || mSearchViewModel.serviceType.getValue() == Constants.ServiceType.OB_STAY || lock() == true)
        {
            return;
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.OB_STAY);

        mAnalytics.onEventStayOutboundClick(getActivity());
    }

    @Override
    public void onGourmetClick()
    {
        if (mSearchViewModel == null || mSearchViewModel.serviceType.getValue() == Constants.ServiceType.GOURMET || lock() == true)
        {
            return;
        }

        mSearchViewModel.setServiceType(Constants.ServiceType.GOURMET);

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
            StayBookDateTime stayBookDateTime = mSearchViewModel.stayViewModel.getBookDateTime();

            startActivityForResult(SearchStaySuggestActivity.newInstance(getActivity()//
                , null //
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT), false), SearchActivity.REQUEST_CODE_STAY_SUGGEST);

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

        try
        {
            CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;
            Calendar calendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchViewModel.stayViewModel.getBookDateTime();

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
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

        StayBookDateTime bookDateTime = mSearchViewModel.stayViewModel.getBookDateTime();
        StaySuggest suggest = mSearchViewModel.stayViewModel.getSuggest();

        startSearchStayResultTab(suggest, bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchViewModel.stayViewModel.inputKeyword);
    }

    @Override
    public void onStayRecentlyHistoryClick(StaySearchResultHistory recentlyHistory)
    {
        if (recentlyHistory == null || lock() == true)
        {
            return;
        }

        try
        {
            StayBookDateTime bookDateTime = recentlyHistory.stayBookDateTime;
            StaySuggest suggest = recentlyHistory.staySuggest;

            startSearchStayResultTab(suggest, bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , null);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onStayPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        StaySuggest.CampaignTag suggestItem = StaySuggest.CampaignTag.getSuggestItem(campaignTag);
        StaySuggest suggest = new StaySuggest(StaySuggest.MenuType.CAMPAIGN_TAG, suggestItem);

        mSearchViewModel.stayViewModel.setSuggest(suggest);

        addCompositeDisposable(getViewInterface().getStaySuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
        {
            @Override
            public void run() throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onStayOutboundSuggestClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(SearchStayOutboundSuggestActivity.newInstance(getActivity(), "", false), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SUGGEST);

        mAnalytics.onEventStayOutboundSuggestClick(getActivity());
    }

    @Override
    public void onStayOutboundCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        final int DAYS_OF_MAX_COUNT = 365;
        final int NIGHTS_OF_MAX_COUNT = 28;

        try
        {
            CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;
            Calendar startCalendar = DailyCalendar.getInstance(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, -1);
            String startDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            startCalendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT);
            String endDateTime = DailyCalendar.format(startCalendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mSearchViewModel.stayOutboundViewModel.getBookDateTime();

            startActivityForResult(StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, NIGHTS_OF_MAX_COUNT//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true, ScreenUtils.dpToPx(getActivity(), 41), true), SearchActivity.REQUEST_CODE_STAY_OUTBOUND_CALENDAR);
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

        if (mSearchViewModel.stayOutboundViewModel.getPeople() == null)
        {
            intent = SelectPeopleActivity.newInstance(getActivity(), People.DEFAULT_ADULTS, null);
        } else
        {
            People people = mSearchViewModel.stayOutboundViewModel.getPeople();

            intent = SelectPeopleActivity.newInstance(getActivity(), people.numberOfAdults, people.getChildAgeList());
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
        analyticsParam.keyword = mSearchViewModel.stayOutboundViewModel.inputKeyword;

        StayOutboundSuggest suggest = mSearchViewModel.stayOutboundViewModel.getSuggest();
        StayBookDateTime stayBookDateTime = mSearchViewModel.stayOutboundViewModel.getBookDateTime();
        People people = mSearchViewModel.stayOutboundViewModel.getPeople();

        startActivityForResult(StayOutboundListActivity.newInstance(getActivity(), suggest//
            , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , people.numberOfAdults, people.getChildAgeList(), analyticsParam)//
            , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT);
    }

    @Override
    public void onStayOutboundRecentlyHistoryClick(StayObSearchResultHistory recentlyHistory)
    {
        if (recentlyHistory == null || lock() == true)
        {
            return;
        }

        try
        {
            StayOutboundSuggest suggest = recentlyHistory.stayOutboundSuggest;
            StayBookDateTime stayBookDateTime = recentlyHistory.stayBookDateTime;
            People people = new People(recentlyHistory.adultCount, recentlyHistory.getChildAgeList());

            startActivityForResult(StayOutboundListActivity.newInstance(getActivity(), suggest//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , people.numberOfAdults, people.getChildAgeList(), null)//
                , SearchActivity.REQUEST_CODE_STAY_OUTBOUND_SEARCH_RESULT);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onStayOutboundPopularAreaClick(StayOutboundSuggest suggest)
    {
        if (suggest == null || lock() == true)
        {
            return;
        }

        mSearchViewModel.stayOutboundViewModel.setSuggest(suggest);

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
            GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.gourmetViewModel.getBookDateTime();

            startActivityForResult(SearchGourmetSuggestActivity.newInstance(getActivity()//
                , null //
                , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT))//
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


        final int DAYS_OF_MAX_COUNT = 30;

        try
        {
            CommonDateTime commonDateTime = mCommonDateTimeViewModel.commonDateTime;
            GourmetBookDateTime bookDateTime = mSearchViewModel.gourmetViewModel.getBookDateTime();

            Calendar calendar = DailyCalendar.getInstance(commonDateTime.dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = GourmetCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, bookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , ScreenUtils.dpToPx(getActivity(), 41), true);

            startActivityForResult(intent, SearchActivity.REQUEST_CODE_GOURMET_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
    }

    @Override
    public void onGourmetDoSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.gourmetViewModel.getBookDateTime();
        GourmetSuggest suggest = mSearchViewModel.gourmetViewModel.getSuggest();

        startSearchGourmetResultTab(suggest, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mSearchViewModel.gourmetViewModel.inputKeyword);
    }

    @Override
    public void onGourmetRecentlyHistoryClick(GourmetSearchResultHistory recentlyHistory)
    {
        if (recentlyHistory == null || lock() == true)
        {
            return;
        }

        try
        {
            GourmetBookDateTime gourmetBookDateTime = recentlyHistory.gourmetBookDateTime;
            GourmetSuggest suggest = recentlyHistory.gourmetSuggest;

            startSearchGourmetResultTab(suggest, gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , null);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onGourmetPopularTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        GourmetSuggest.CampaignTag suggestItem = GourmetSuggest.CampaignTag.getSuggestItem(campaignTag);
        GourmetSuggest suggest = new GourmetSuggest(GourmetSuggest.MenuType.CAMPAIGN_TAG, suggestItem);

        mSearchViewModel.gourmetViewModel.setSuggest(suggest);

        addCompositeDisposable(getViewInterface().getGourmetSuggestAnimation().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Action()
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

        mCommonDateTimeViewModel = ViewModelProviders.of(activity, new CommonDateTimeViewModel.CommonDateTimeViewModelFactory()).get(CommonDateTimeViewModel.class);

        mSearchViewModel = ViewModelProviders.of(activity, new SearchViewModel.SearchViewModelFactory()).get(SearchViewModel.class);
        mSearchViewModel.stayViewModel = ViewModelProviders.of(activity, new SearchStayViewModel.SearchStayViewModelFactory()).get(SearchStayViewModel.class);
        mSearchViewModel.stayOutboundViewModel = ViewModelProviders.of(activity, new SearchStayOutboundViewModel.SearchStayOutboundViewModelFactory()).get(SearchStayOutboundViewModel.class);
        mSearchViewModel.gourmetViewModel = ViewModelProviders.of(activity, new SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetViewModel.class);

        mSearchViewModel.serviceType.observe(activity, new Observer<Constants.ServiceType>()
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
        mSearchViewModel.stayViewModel.setSuggestObserver(activity, new Observer<StaySuggest>()
        {
            @Override
            public void onChanged(@Nullable StaySuggest suggest)
            {
                String displayName = suggest.getText1();

                getViewInterface().setSearchStaySuggestText(displayName);

                getViewInterface().setSearchStayButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchViewModel.stayViewModel.setBookDateTimeObserver(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime stayBookDateTime)
            {
                if (stayBookDateTime == null)
                {
                    Util.restartApp(getActivity());
                    return;
                }

                getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
                    , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
                    , stayBookDateTime.getNights()));
            }
        });

        // StayOutbound
        mSearchViewModel.stayOutboundViewModel.setSuggestObserver(activity, new Observer<StayOutboundSuggest>()
        {
            @Override
            public void onChanged(@Nullable StayOutboundSuggest stayOutboundSuggest)
            {
                String displayName = stayOutboundSuggest.displayText;

                getViewInterface().setSearchStayOutboundSuggestText(displayName);

                getViewInterface().setSearchStayOutboundButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchViewModel.stayOutboundViewModel.setBookDateTimeObserver(activity, new Observer<StayBookDateTime>()
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

        mSearchViewModel.stayOutboundViewModel.setPeopleObserver(activity, new Observer<People>()
        {
            @Override
            public void onChanged(@Nullable People people)
            {
                getViewInterface().setSearchStayOutboundPeopleText(people.toString(getActivity()));
            }
        });

        mSearchViewModel.stayOutboundViewModel.setPeople(People.DEFAULT_ADULTS, null);

        // Gourmet
        mSearchViewModel.gourmetViewModel.setSuggestObserver(activity, new Observer<GourmetSuggest>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggest suggest)
            {
                String displayName = suggest.getText1();

                getViewInterface().setSearchGourmetSuggestText(displayName);

                getViewInterface().setSearchGourmetButtonEnabled(DailyTextUtils.isTextEmpty(displayName) == false);
            }
        });

        mSearchViewModel.gourmetViewModel.setBookDateTimeObserver(activity, new Observer<GourmetBookDateTime>()
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
        mSearchViewModel.stayOutboundViewModel.setPeople(numberOfAdults, childAgeList);
    }

    void showSearchStay()
    {
        StaySuggest suggest = mSearchViewModel.stayViewModel.getSuggest();

        if (suggest == null || DailyTextUtils.isTextEmpty(suggest.getText1()) == true)
        {
            getViewInterface().setSearchStaySuggestText(null);
            getViewInterface().setSearchStayButtonEnabled(false);
        } else
        {
            String displayName = suggest.getText1();

            getViewInterface().setSearchStaySuggestText(displayName);
            getViewInterface().setSearchStayButtonEnabled(true);
        }

        StayBookDateTime stayBookDateTime = mSearchViewModel.stayViewModel.getBookDateTime();

        getViewInterface().setSearchStayCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getNights()));

        getViewInterface().showSearchStay();
    }

    void showSearchStayOutbound()
    {
        StayOutboundSuggest suggest = mSearchViewModel.stayOutboundViewModel.getSuggest();

        if (suggest == null || DailyTextUtils.isTextEmpty(suggest.display) == true)
        {
            getViewInterface().setSearchStayOutboundSuggestText(null);
            getViewInterface().setSearchStayOutboundButtonEnabled(false);
        } else
        {
            getViewInterface().setSearchStayOutboundSuggestText(suggest.display);
            getViewInterface().setSearchStayOutboundButtonEnabled(true);
        }

        StayBookDateTime stayBookDateTime = mSearchViewModel.stayOutboundViewModel.getBookDateTime();

        getViewInterface().setSearchStayOutboundCalendarText(String.format(Locale.KOREA, "%s - %s, %d박"//
            , stayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)")//
            , stayBookDateTime.getNights()));

        getViewInterface().showSearchStayOutbound();
    }

    void showSearchGourmet()
    {
        GourmetSuggest suggest = mSearchViewModel.gourmetViewModel.getSuggest();

        if (suggest == null || DailyTextUtils.isTextEmpty(suggest.getText1()) == true)
        {
            getViewInterface().setSearchGourmetSuggestText(null);
            getViewInterface().setSearchGourmetButtonEnabled(false);
        } else
        {
            String displayName = suggest.getText1();

            getViewInterface().setSearchGourmetSuggestText(displayName);
            getViewInterface().setSearchGourmetButtonEnabled(true);
        }

        GourmetBookDateTime gourmetBookDateTime = mSearchViewModel.gourmetViewModel.getBookDateTime();

        getViewInterface().setSearchGourmetCalendarText(gourmetBookDateTime.getVisitDateTime("yyyy.MM.dd(EEE)"));

        getViewInterface().showSearchGourmet();
    }

    void startSearchStayResultTab(StaySuggest suggest, String checkInDateTime, String checkOutDateTime, String inputKeyWord)
    {
        if (suggest == null || DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        SearchStayResultAnalyticsParam analyticsParam = new SearchStayResultAnalyticsParam();
        analyticsParam.mCallByScreen = AnalyticsManager.Screen.SEARCH_MAIN;

        startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity(), DailyCategoryType.STAY_ALL//
            , checkInDateTime, checkOutDateTime, suggest, inputKeyWord, analyticsParam)//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
    }

    void startSearchGourmetResultTab(GourmetSuggest suggest, String visitDateTime, String inputKeyword)
    {
        if (suggest == null || DailyTextUtils.isTextEmpty(visitDateTime) == true)
        {
            return;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(getActivity(), visitDateTime, suggest, inputKeyword)//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void moveDeepLinkStayCampaignTag(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void moveDeepLinkGourmetCampaignTag(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void moveDeepLinkStaySearchResult(CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null || externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
    }

    void moveDeepLinkGourmetSearchResult(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
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
