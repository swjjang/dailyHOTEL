package com.daily.dailyhotel.screen.home.search.stay.inbound.result;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookDateTime;
import com.daily.dailyhotel.entity.GourmetFilter;
import com.daily.dailyhotel.entity.GourmetSuggestV2;
import com.daily.dailyhotel.parcel.GourmetSuggestParcelV2;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.search.SearchGourmetViewModel;
import com.daily.dailyhotel.screen.home.search.gourmet.research.ResearchGourmetActivity;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.GourmetCuration;
import com.twoheart.dailyhotel.model.GourmetCurationOption;
import com.twoheart.dailyhotel.model.GourmetSearchCuration;
import com.twoheart.dailyhotel.model.PlaceCuration;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.screen.gourmet.filter.GourmetCalendarActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultCurationActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayResultTabPresenter extends BaseExceptionPresenter<SearchStayResultTabActivity, SearchStayResultTabInterface.ViewInterface> implements SearchStayResultTabInterface.OnEventListener
{
    public static final float DEFAULT_RADIUS = 10.0f;

    private SearchStayResultTabInterface.AnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private CampaignTagRemoteImpl mCampaignTagRemoteImpl;

    SearchStayResultViewModel mViewModel;

    DailyDeepLink mDailyDeepLink;

    public enum ViewType
    {
        LIST,
        MAP,
    }

    public SearchStayResultTabPresenter(@NonNull SearchStayResultTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchStayResultTabInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchStayResultTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchStayResultTabActivity activity)
    {
        setContentView(R.layout.activity_search_gourmet_result_tab_data);

        setAnalytics(new SearchStayResultTabAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mViewModel = ViewModelProviders.of(activity, new SearchStayResultViewModel.SearchGourmetViewModelFactory()).get(SearchStayResultViewModel.class);
        mViewModel.searchViewModel = ViewModelProviders.of(activity, new SearchGourmetViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetViewModel.class);

        mViewModel.setViewTypeObserver(activity, new Observer<ViewType>()
        {
            @Override
            public void onChanged(@Nullable ViewType viewType)
            {
                switch (viewType)
                {
                    case LIST:
                        getViewInterface().setViewType(ViewType.MAP);
                        break;

                    case MAP:
                        getViewInterface().setViewType(ViewType.LIST);
                        break;
                }

                mAnalytics.onEventChangedViewType(getActivity(), viewType);
            }
        });

        mViewModel.searchViewModel.bookDateTime.observe(activity, new Observer<GourmetBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable GourmetBookDateTime gourmetBookDateTime)
            {
                final String dateFormat = "MM.dd(EEE)";

                getViewInterface().setToolbarDateText(gourmetBookDateTime.getVisitDateTime(dateFormat));
            }
        });

        mViewModel.searchViewModel.suggest.observe(activity, new Observer<GourmetSuggestV2>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggestV2 suggest)
            {
                getViewInterface().setToolbarTitle(suggest.getText1());
            }
        });

        mViewModel.setFilterObserver(activity, new Observer<GourmetFilter>()
        {
            @Override
            public void onChanged(@Nullable GourmetFilter gourmetFilter)
            {
                getViewInterface().setOptionFilterSelected(gourmetFilter != null && gourmetFilter.isDefault() == false);
            }
        });
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchStayResultTabInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

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

                return false;
            }
        } else
        {
            try
            {
                parseIntent(intent);
            } catch (Exception e)
            {
                return false;
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
            throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isCampaignTagListView() == true)
            {

            } else if (externalDeepLink.isGourmetSearchResultView() == true)
            {

            } else
            {
                throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
            }
        } else
        {
            throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
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

        mViewModel.setBookDateTime(intent, SearchStayResultTabActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);
        GourmetSuggestParcelV2 suggestParcel = intent.getParcelableExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel == null || suggestParcel.getSuggest() == null)
        {
            throw new NullPointerException("suggestParcel == null || suggestParcel.getSuggest() == null");
        }

        GourmetSuggestV2 suggest = suggestParcel.getSuggest();

        mViewModel.setSuggest(suggest);
        mViewModel.setInputKeyword(intent.getStringExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD));

        if (suggest.isLocationSuggestType() == true)
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DISTANCE;
            mViewModel.getFilter().sortType = GourmetFilter.SortType.DISTANCE;
            mViewModel.searchViewModel.radius = DEFAULT_RADIUS;

            getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);
        } else
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DEFAULT;
            mViewModel.searchViewModel.radius = 0.0f;
        }
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        if (mDailyDeepLink != null)
        {
            return;
        }

        GourmetSuggestV2 suggest = mViewModel.getSuggest();

        initView(suggest);
    }

    private void initView(GourmetSuggestV2 suggest)
    {
        if (suggest == null)
        {
            return;
        }

        getViewInterface().setFloatingActionViewVisible(suggest.isCampaignTagSuggestType() == false);

        if (suggest.menuType == GourmetSuggestV2.MenuType.REGION_LOCATION)
        {
            getViewInterface().setToolbarTitleImageResource(R.drawable.search_ic_01_date);
        } else
        {
            getViewInterface().setToolbarTitleImageResource(R.drawable.search_ic_01_search);
        }

        getViewInterface().setToolbarRadiusSpinnerVisible(suggest.isLocationSuggestType());
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
        setResultCode(Activity.RESULT_CANCELED);

        boolean backPressed = getViewInterface().onFragmentBackPressed();

        if (backPressed == true)
        {
            mAnalytics.onEventBackClick(getActivity());
        }

        return backPressed;
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
            case SearchStayResultTabActivity.REQUEST_CODE_RESEARCH:
                onResearchActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_FILTER:
                onFilterActivityResult(resultCode, data);
                break;

            case SearchStayResultTabActivity.REQUEST_CODE_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;
        }
    }

    private void onResearchActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    try
                    {
                        GourmetSuggestParcelV2 gourmetSuggestParcel = intent.getParcelableExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (gourmetSuggestParcel == null || gourmetSuggestParcel.getSuggest() == null)
                        {
                            return;
                        }

                        GourmetSuggestV2 suggest = gourmetSuggestParcel.getSuggest();

                        mViewModel.setSuggest(suggest);
                        mViewModel.setInputKeyword(intent.getStringExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_KEYWORD));

                        if (suggest.isLocationSuggestType() == true)
                        {
                            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DISTANCE;
                            mViewModel.searchViewModel.radius = DEFAULT_RADIUS;
                        } else
                        {
                            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DEFAULT;
                            mViewModel.searchViewModel.radius = 0.0f;
                        }

                        mViewModel.setBookDateTime(intent, ResearchGourmetActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);

                        mViewModel.getFilter().reset();
                        mViewModel.setViewType(ViewType.LIST);
                        setRefresh(true);
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
                    }
                }
                break;
        }
    }

    private void onFilterActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    PlaceCuration placeCuration = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACECURATION);

                    if ((placeCuration instanceof GourmetCuration) == false)
                    {
                        return;
                    }

                    GourmetCuration gourmetCuration = (GourmetCuration) placeCuration;
                    GourmetFilter gourmetFilter = mergerCurationToFilter(gourmetCuration, mViewModel.getFilter());

                    if (gourmetFilter.isDistanceSort() == true)
                    {
                        mViewModel.filterLocation = gourmetCuration.getLocation();
                    } else
                    {
                        mViewModel.filterLocation = null;
                    }

                    mViewModel.setFilter(gourmetFilter);

                    getViewInterface().refreshCurrentFragment();
                }
                break;
        }
    }

    private GourmetFilter mergerCurationToFilter(GourmetCuration gourmetCuration, GourmetFilter gourmetFilter)
    {
        if (gourmetCuration == null || gourmetFilter == null)
        {
            return null;
        }

        GourmetCurationOption gourmetCurationOption = (GourmetCurationOption) gourmetCuration.getCurationOption();

        gourmetFilter.reset();
        gourmetFilter.sortType = GourmetFilter.SortType.valueOf(gourmetCurationOption.getSortType().name());
        gourmetFilter.defaultSortType = GourmetFilter.SortType.valueOf(gourmetCurationOption.getDefaultSortType().name());

        List<String> filerCategoryList = new ArrayList<>(gourmetCurationOption.getFilterMap().keySet());

        Map<String, GourmetFilter.Category> categoryMap = gourmetFilter.getCategoryMap();

        for (String categoryName : filerCategoryList)
        {
            gourmetFilter.addCategory(categoryMap.get(categoryName));
        }

        gourmetFilter.flagAmenitiesFilters = gourmetCurationOption.flagAmenitiesFilters;
        gourmetFilter.flagTimeFilter = gourmetCurationOption.flagTimeFilter;

        return gourmetFilter;
    }

    protected void onCalendarActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent != null)
                {
                    try
                    {
                        GourmetBookingDay gourmetBookingDay = intent.getParcelableExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY);

                        if (gourmetBookingDay == null)
                        {
                            return;
                        }

                        mViewModel.setBookDateTime(gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));
                        mViewModel.getFilter().reset();

                        mViewModel.searchViewModel.radius = DEFAULT_RADIUS;
                        getViewInterface().setOptionFilterSelected(false);
                        getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);

                        getViewInterface().refreshCurrentFragment();
                    } catch (Exception e)
                    {
                        ExLog.e(e.toString());
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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().subscribe(new Consumer<CommonDateTime>()
        {
            @Override
            public void accept(CommonDateTime commonDateTime) throws Exception
            {
                mViewModel.setCommonDateTime(commonDateTime);

                if (mDailyDeepLink != null)
                {
                    DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) mDailyDeepLink;

                    if (externalDeepLink.isCampaignTagListView() == true)
                    {
                        parseCampaignTagListView(externalDeepLink, commonDateTime);
                    } else if (externalDeepLink.isGourmetSearchResultView() == true)
                    {
                        parseSearchGourmetResultListView(externalDeepLink, commonDateTime);
                    }

                    clearDeepLink();

                    initView(mViewModel.getSuggest());
                }

                GourmetSuggestV2 suggest = mViewModel.getSuggest();

                if (suggest.isCampaignTagSuggestType() == true)
                {
                    addCompositeDisposable(getViewInterface().setCampaignTagFragment().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BasePagerFragment>()
                    {
                        @Override
                        public void accept(BasePagerFragment basePagerFragment) throws Exception
                        {
                            basePagerFragment.onSelected();
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override
                        public void accept(Throwable throwable) throws Exception
                        {
                            onHandleErrorAndFinish(throwable);
                        }
                    }));
                } else
                {
                    addCompositeDisposable(getViewInterface().setSearchResultFragment().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BasePagerFragment>()
                    {
                        @Override
                        public void accept(BasePagerFragment basePagerFragment) throws Exception
                        {
                            basePagerFragment.onSelected();
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

    private void parseCampaignTagListView(DailyExternalDeepLink externalDeepLink, CommonDateTime commonDateTime) throws Exception
    {
        if (externalDeepLink == null || commonDateTime == null)
        {
            throw new NullPointerException("externalDeepLink == null || commonDateTime == null");
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
            throw new RuntimeException("Invalid DeepLink : " + externalDeepLink.getDeepLink());
        }

        GourmetSuggestV2.CampaignTag suggestItem = new GourmetSuggestV2.CampaignTag();
        suggestItem.index = index;

        mViewModel.setBookDateTime(gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        GourmetSuggestV2 suggest = new GourmetSuggestV2(GourmetSuggestV2.MenuType.CAMPAIGN_TAG, suggestItem);

        mViewModel.setSuggest(suggest);
    }

    private void parseSearchGourmetResultListView(DailyExternalDeepLink externalDeepLink, CommonDateTime commonDateTime) throws Exception
    {
        if (externalDeepLink == null || commonDateTime == null)
        {
            throw new NullPointerException("externalDeepLink == null || commonDateTime == null");
        }

        GourmetBookDateTime gourmetBookDateTime = externalDeepLink.getGourmetBookDateTime(commonDateTime, externalDeepLink);
        String word = externalDeepLink.getSearchWord();

        if (gourmetBookDateTime == null || DailyTextUtils.isTextEmpty(word) == true)
        {
            throw new RuntimeException("Invalid DeepLink : " + externalDeepLink.getDeepLink());
        }

        GourmetSuggestV2.Direct suggestItem = new GourmetSuggestV2.Direct(word);
        GourmetSuggestV2 suggest = new GourmetSuggestV2(GourmetSuggestV2.MenuType.DIRECT, suggestItem);
        Constants.SortType sortType = externalDeepLink.getSorting();

        if (sortType == Constants.SortType.DISTANCE)
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DISTANCE;
        } else
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DEFAULT;
        }

        mViewModel.getFilter().sortType = GourmetFilter.SortType.valueOf(sortType.name());

        mViewModel.setBookDateTime(gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        mViewModel.setSuggest(suggest);
    }

    @Override
    public void onBackClick()
    {
        mAnalytics.onEventCancelClick(getActivity());
        finish();
    }

    private void setResultCode(int resultCode)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcelV2(mViewModel.getSuggest()));
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME, mViewModel.getBookDateTime().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD, mViewModel.getInputKeyword());

        setResult(resultCode, intent);
    }

    @Override
    public void onResearchClick()
    {
        try
        {
            CommonDateTime commonDateTime = mViewModel.getCommonDateTime();
            GourmetBookDateTime gourmetBookDateTime = mViewModel.getBookDateTime();
            GourmetSuggestV2 suggest = mViewModel.getSuggest();

            startActivityForResult(ResearchGourmetActivity.newInstance(getActivity(), commonDateTime.openDateTime, commonDateTime.closeDateTime//
                , commonDateTime.currentDateTime, commonDateTime.dailyDateTime//
                , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , suggest), SearchStayResultTabActivity.REQUEST_CODE_RESEARCH);

            mAnalytics.onEventResearchClick(getActivity(), suggest);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
    }

    @Override
    public void onFinishAndRefresh()
    {
        setResultCode(BaseActivity.RESULT_CODE_REFRESH);
        finish();
    }

    @Override
    public void onViewTypeClick()
    {
        if (lock() == true)
        {
            return;
        }

        switch (mViewModel.getViewType())
        {
            // 현재 리스트 화면인 경우
            case LIST:
            {
                screenLock(true);

                mViewModel.setViewType(ViewType.MAP);
                break;
            }

            // 현재 맵화면인 경우
            case MAP:
            {
                mViewModel.setViewType(ViewType.LIST);

                clearCompositeDisposable();

                unLockAll();
                break;
            }
        }
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            GourmetSearchCuration gourmetSearchCuration = toGourmetSearchCuration();

            Intent intent = GourmetSearchResultCurationActivity.newInstance(getActivity(),//
                Constants.ViewType.valueOf(mViewModel.getViewType().name())//
                , gourmetSearchCuration, gourmetSearchCuration.getLocation() != null);
            startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_FILTER);

            mAnalytics.onEventFilterClick(getActivity());
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

        CommonDateTime commonDateTime = mViewModel.getCommonDateTime();
        GourmetBookDateTime gourmetBookDateTime = mViewModel.getBookDateTime();

        startActivityForResult(GourmetCalendarActivity.newInstance(getActivity(), commonDateTime.getTodayDateTime() //
            , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT), GourmetCalendarActivity.DEFAULT_CALENDAR_DAY_OF_MAX_COUNT //
            , AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, true, true), SearchStayResultTabActivity.REQUEST_CODE_CALENDAR);

        mAnalytics.onEventCalendarClick(getActivity());
    }

    private GourmetSearchCuration toGourmetSearchCuration() throws Exception
    {
        GourmetSuggestV2 suggest = mViewModel.getSuggest();

        GourmetSearchCuration gourmetSearchCuration = new GourmetSearchCuration();
        gourmetSearchCuration.setSuggest(suggest);
        gourmetSearchCuration.setRadius(mViewModel.searchViewModel.radius);

        gourmetSearchCuration.setGourmetBookingDay(mViewModel.getBookDateTime().getGourmetBookingDay());

        GourmetCurationOption gourmetCurationOption = new GourmetCurationOption();

        GourmetFilter gourmetFilter = mViewModel.getFilter();

        // 추후 형변환 이슈로 수정될 예정
        gourmetCurationOption.setFilterMap((HashMap) gourmetFilter.getCategoryFilterMap());

        List<GourmetFilter.Category> categoryList = new ArrayList(gourmetFilter.getCategoryMap().values());
        HashMap<String, Integer> categoryCodeMap = new HashMap<>();
        HashMap<String, Integer> categorySequenceMap = new HashMap<>();

        for (GourmetFilter.Category gourmetCategory : categoryList)
        {
            categoryCodeMap.put(gourmetCategory.name, gourmetCategory.code);
            categorySequenceMap.put(gourmetCategory.name, gourmetCategory.sequence);
        }

        gourmetCurationOption.setCategoryCoderMap(categoryCodeMap);
        gourmetCurationOption.setCategorySequenceMap(categorySequenceMap);

        gourmetCurationOption.flagAmenitiesFilters = gourmetFilter.flagAmenitiesFilters;
        gourmetCurationOption.flagTimeFilter = gourmetFilter.flagTimeFilter;
        gourmetCurationOption.setDefaultSortType(Constants.SortType.valueOf(gourmetFilter.defaultSortType.name()));
        gourmetCurationOption.setSortType(Constants.SortType.valueOf(gourmetFilter.sortType.name()));

        gourmetSearchCuration.setCurationOption(gourmetCurationOption);

        // 내 주변 검색
        if (suggest.isLocationSuggestType() == true)
        {
            GourmetSuggestV2.Location locationSuggestItem = (GourmetSuggestV2.Location) suggest.getSuggestItem();
            Location location = new Location("provider");
            location.setLatitude(locationSuggestItem.latitude);
            location.setLongitude(locationSuggestItem.longitude);

            gourmetSearchCuration.setLocation(location);
        } else if (gourmetFilter.isDistanceSort() == true)
        {
            gourmetSearchCuration.setLocation(mViewModel.filterLocation);
        }

        return gourmetSearchCuration;
    }

    @Override
    public void onChangedRadius(float radius)
    {
        if (mViewModel.searchViewModel.radius == radius)
        {
            return;
        }

        mViewModel.searchViewModel.radius = radius;

        getViewInterface().refreshCurrentFragment();

        mAnalytics.onEventChangedRadius(getActivity(), mViewModel.getSuggest(), radius);
    }

    @Override
    public void setEmptyViewVisible(boolean visible)
    {
        getViewInterface().setEmptyViewVisible(visible);

        if (visible == true)
        {
            addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(Constants.ServiceType.GOURMET.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<CampaignTag>>()
            {
                @Override
                public void accept(List<CampaignTag> campaignTagList) throws Exception
                {
                    if (campaignTagList == null || campaignTagList.size() == 0)
                    {
                        getViewInterface().setEmptyViewCampaignTagVisible(false);
                        return;
                    }

                    getViewInterface().setEmptyViewCampaignTagVisible(true);
                    getViewInterface().setEmptyViewCampaignTag(getString(R.string.label_search_gourmet_popular_search_tag), campaignTagList);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    ExLog.e(throwable.toString());

                    getViewInterface().setEmptyViewCampaignTagVisible(false);
                }
            }));
        }
    }

    @Override
    public void onStayClick()
    {
        if (lock() == true)
        {
            return;
        }

        mAnalytics.onEventStayClick(getActivity());

        setResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAY);
        finish();
    }

    @Override
    public void onStayOutboundClick()
    {
        if (lock() == true)
        {
            return;
        }

        mAnalytics.onEventStayOutboundClick(getActivity());

        setResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_STAYOUTBOUND);
        finish();
    }

    @Override
    public void onCampaignTagClick(CampaignTag campaignTag)
    {
        if (campaignTag == null || lock() == true)
        {
            return;
        }

        GourmetSuggestV2 suggest = new GourmetSuggestV2(GourmetSuggestV2.MenuType.CAMPAIGN_TAG//
            , GourmetSuggestV2.CampaignTag.getSuggestItem(campaignTag));

        mViewModel.setInputKeyword(null);
        mViewModel.setSuggest(suggest);

        setRefresh(true);
        onRefresh(true);

        mAnalytics.onEventCampaignTagClick(getActivity(), campaignTag.index);
    }
}
