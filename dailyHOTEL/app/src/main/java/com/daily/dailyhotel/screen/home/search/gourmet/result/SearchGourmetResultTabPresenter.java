package com.daily.dailyhotel.screen.home.search.gourmet.result;


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
import com.daily.dailyhotel.entity.GourmetSuggest;
import com.daily.dailyhotel.parcel.GourmetSuggestParcel;
import com.daily.dailyhotel.repository.local.SearchLocalImpl;
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
public class SearchGourmetResultTabPresenter extends BaseExceptionPresenter<SearchGourmetResultTabActivity, SearchGourmetResultTabInterface.ViewInterface> implements SearchGourmetResultTabInterface.OnEventListener
{
    public static final float DEFAULT_RADIUS = 10.0f;

    SearchGourmetResultTabInterface.AnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private CampaignTagRemoteImpl mCampaignTagRemoteImpl;
    private SearchLocalImpl mSearchLocalImpl;

    SearchGourmetResultViewModel mViewModel;

    DailyDeepLink mDailyDeepLink;

    public enum ViewType
    {
        LIST,
        MAP,
    }

    public SearchGourmetResultTabPresenter(@NonNull SearchGourmetResultTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected SearchGourmetResultTabInterface.ViewInterface createInstanceViewInterface()
    {
        return new SearchGourmetResultTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(SearchGourmetResultTabActivity activity)
    {
        setContentView(R.layout.activity_search_gourmet_result_tab_data);

        setAnalytics(new SearchGourmetResultTabAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mCampaignTagRemoteImpl = new CampaignTagRemoteImpl(activity);
        mSearchLocalImpl = new SearchLocalImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mViewModel = ViewModelProviders.of(activity, new SearchGourmetResultViewModel.SearchGourmetViewModelFactory()).get(SearchGourmetResultViewModel.class);
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

        mViewModel.searchViewModel.setBookDateTimeObserver(activity, new Observer<GourmetBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable GourmetBookDateTime gourmetBookDateTime)
            {
                final String dateFormat = "MM.dd(EEE)";

                getViewInterface().setToolbarDateText(gourmetBookDateTime.getVisitDateTime(dateFormat));
            }
        });

        mViewModel.searchViewModel.setSuggestObserver(activity, new Observer<GourmetSuggest>()
        {
            @Override
            public void onChanged(@Nullable GourmetSuggest suggest)
            {
                getViewInterface().setToolbarTitle(suggest.getText1());
            }
        });

        mViewModel.setFilterObserver(activity, new Observer<GourmetFilter>()
        {
            @Override
            public void onChanged(@Nullable GourmetFilter filter)
            {
                getViewInterface().setOptionFilterSelected(filter != null && filter.isDefault() == false);
            }
        });
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (SearchGourmetResultTabInterface.AnalyticsInterface) analytics;
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

    void clearDeepLink()
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

        mViewModel.setBookDateTime(intent, SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME);
        GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel == null || suggestParcel.getSuggest() == null)
        {
            throw new NullPointerException("suggestParcel == null || suggestParcel.getSuggest() == null");
        }

        GourmetSuggest suggest = suggestParcel.getSuggest();

        mViewModel.setSuggest(suggest);
        mViewModel.setInputKeyword(intent.getStringExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD));

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

        GourmetSuggest suggest = mViewModel.getSuggest();

        initView(suggest);
    }

    void initView(GourmetSuggest suggest)
    {
        if (suggest == null)
        {
            return;
        }

        getViewInterface().setFloatingActionViewVisible(suggest.isCampaignTagSuggestType() == false);

        if (suggest.menuType == GourmetSuggest.MenuType.REGION_LOCATION)
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
            case SearchGourmetResultTabActivity.REQUEST_CODE_RESEARCH:
                onResearchActivityResult(resultCode, data);
                break;

            case SearchGourmetResultTabActivity.REQUEST_CODE_FILTER:
                onFilterActivityResult(resultCode, data);
                break;

            case SearchGourmetResultTabActivity.REQUEST_CODE_CALENDAR:
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
                        GourmetSuggestParcel suggestParcel = intent.getParcelableExtra(ResearchGourmetActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel == null || suggestParcel.getSuggest() == null)
                        {
                            return;
                        }

                        getViewInterface().resetFloatingActionViewTranslation();
                        getViewInterface().removeAllFragment();

                        GourmetSuggest suggest = suggestParcel.getSuggest();

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

                        initView(mViewModel.getSuggest());

                        mViewModel.getFilter().reset();
                        mViewModel.setFilter(mViewModel.getFilter());
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
                    GourmetFilter gourmetFilter = mergeCurationToFilter(gourmetCuration, mViewModel.getFilter());

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

    private GourmetFilter mergeCurationToFilter(GourmetCuration gourmetCuration, GourmetFilter gourmetFilter)
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

                        getViewInterface().resetFloatingActionViewTranslation();

                        mViewModel.setBookDateTime(gourmetBookingDay.getVisitDay(DailyCalendar.ISO_8601_FORMAT));

                        if (mViewModel.getSuggest().isLocationSuggestType() == true)
                        {
                            mViewModel.searchViewModel.radius = DEFAULT_RADIUS;
                            getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);
                        }

                        initView(mViewModel.getSuggest());

                        mViewModel.getFilter().reset();
                        mViewModel.setFilter(mViewModel.getFilter());
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

                GourmetSuggest suggest = mViewModel.getSuggest();

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

    void parseCampaignTagListView(DailyExternalDeepLink externalDeepLink, CommonDateTime commonDateTime) throws Exception
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

        GourmetSuggest.CampaignTag suggestItem = new GourmetSuggest.CampaignTag();
        suggestItem.index = index;

        mViewModel.setBookDateTime(gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        GourmetSuggest suggest = new GourmetSuggest(GourmetSuggest.MenuType.CAMPAIGN_TAG, suggestItem);

        mViewModel.setSuggest(suggest);
    }

    void parseSearchGourmetResultListView(DailyExternalDeepLink externalDeepLink, CommonDateTime commonDateTime) throws Exception
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

        GourmetSuggest.Direct suggestItem = new GourmetSuggest.Direct(word);
        GourmetSuggest suggest = new GourmetSuggest(GourmetSuggest.MenuType.DIRECT, suggestItem);
        Constants.SortType sortType = externalDeepLink.getSorting();

        if (sortType == Constants.SortType.DISTANCE)
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DISTANCE;
        } else
        {
            mViewModel.getFilter().defaultSortType = GourmetFilter.SortType.DEFAULT;
        }

        mViewModel.getFilter().sortType = GourmetFilter.SortType.valueOf(sortType.name());
        mViewModel.setFilter(mViewModel.getFilter());

        mViewModel.setBookDateTime(gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        mViewModel.setSuggest(suggest);
    }

    @Override
    public void onBackClick()
    {
        mAnalytics.onEventCancelClick(getActivity());

        setResultCode(Activity.RESULT_CANCELED);
        finish();
    }

    private void setResultCode(int resultCode)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_SUGGEST, new GourmetSuggestParcel(mViewModel.getSuggest()));
        intent.putExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_VISIT_DATE_TIME, mViewModel.getBookDateTime().getVisitDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(SearchGourmetResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD, mViewModel.getInputKeyword());

        setResult(resultCode, intent);
    }

    @Override
    public void onEmptyStayResearchClick()
    {
        GourmetSuggest suggest = mViewModel.getSuggest();

        if (suggest.menuType == GourmetSuggest.MenuType.REGION_LOCATION)
        {
            onCalendarClick();
        } else
        {
            onResearchClick();
        }
    }

    @Override
    public void onToolbarTitleClick()
    {
        GourmetSuggest suggest = mViewModel.getSuggest();

        if (suggest.menuType == GourmetSuggest.MenuType.REGION_LOCATION)
        {
            onCalendarClick();
        } else
        {
            onResearchClick();
        }
    }

    @Override
    public void onResearchClick()
    {
        try
        {
            CommonDateTime commonDateTime = mViewModel.getCommonDateTime();
            GourmetBookDateTime gourmetBookDateTime = mViewModel.getBookDateTime();
            GourmetSuggest suggest = mViewModel.getSuggest();

            startActivityForResult(ResearchGourmetActivity.newInstance(getActivity(), commonDateTime.openDateTime, commonDateTime.closeDateTime//
                , commonDateTime.currentDateTime, commonDateTime.dailyDateTime//
                , gourmetBookDateTime.getVisitDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , suggest), SearchGourmetResultTabActivity.REQUEST_CODE_RESEARCH);

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

            Intent intent = GourmetSearchResultCurationActivity.newInstance(getActivity()//
                , GourmetSearchResultCurationActivity.ListType.SEARCH//
                , com.twoheart.dailyhotel.util.Constants.ViewType.valueOf(mViewModel.getViewType().name())//
                , gourmetSearchCuration, gourmetSearchCuration.getLocation() != null);
            startActivityForResult(intent, SearchGourmetResultTabActivity.REQUEST_CODE_FILTER);

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
            , AnalyticsManager.Screen.DAILYGOURMET_LIST_REGION_DOMESTIC, true, true), SearchGourmetResultTabActivity.REQUEST_CODE_CALENDAR);

        mAnalytics.onEventCalendarClick(getActivity());
    }

    private GourmetSearchCuration toGourmetSearchCuration() throws Exception
    {
        GourmetSuggest suggest = mViewModel.getSuggest();

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
            GourmetSuggest.Location locationSuggestItem = (GourmetSuggest.Location) suggest.getSuggestItem();
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
        } else
        {
            if (mViewModel.getSuggest().menuType != GourmetSuggest.MenuType.REGION_LOCATION)
            {
                addCompositeDisposable(mSearchLocalImpl.addGourmetSearchResultHistory(mViewModel.getCommonDateTime()//
                    , mViewModel.getBookDateTime(), mViewModel.getSuggest()).observeOn(AndroidSchedulers.mainThread()).subscribe());
            }
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

        GourmetSuggest suggest = new GourmetSuggest(GourmetSuggest.MenuType.CAMPAIGN_TAG//
            , GourmetSuggest.CampaignTag.getSuggestItem(campaignTag));

        mViewModel.setInputKeyword(null);
        mViewModel.setSuggest(suggest);

        setRefresh(true);
        onRefresh(true);

        mAnalytics.onEventCampaignTagClick(getActivity(), campaignTag.index);
    }
}
