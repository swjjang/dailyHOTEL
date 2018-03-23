package com.daily.dailyhotel.screen.home.search.stay.inbound.result;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.base.BasePagerFragment;
import com.daily.dailyhotel.entity.CampaignTag;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StaySuggestV2;
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StaySuggestParcelV2;
import com.daily.dailyhotel.repository.local.SearchLocalImpl;
import com.daily.dailyhotel.repository.remote.CampaignTagRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.SearchStayViewModel;
import com.daily.dailyhotel.screen.home.search.stay.inbound.research.ResearchStayActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.filter.StayFilterActivity;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class SearchStayResultTabPresenter extends BaseExceptionPresenter<SearchStayResultTabActivity, SearchStayResultTabInterface.ViewInterface> implements SearchStayResultTabInterface.OnEventListener
{
    public static final float DEFAULT_RADIUS = 10.0f;

    SearchStayResultTabInterface.AnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private CampaignTagRemoteImpl mCampaignTagRemoteImpl;
    private SearchLocalImpl mSearchLocalImpl;

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
        setContentView(R.layout.activity_search_stay_result_tab_data);

        setAnalytics(new SearchStayResultTabAnalyticsImpl());

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

        mViewModel = ViewModelProviders.of(activity, new SearchStayResultViewModel.SearchStayViewModelFactory()).get(SearchStayResultViewModel.class);
        mViewModel.searchViewModel = ViewModelProviders.of(activity, new SearchStayViewModel.SearchStayViewModelFactory()).get(SearchStayViewModel.class);

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

        mViewModel.searchViewModel.setBookDateTimeObserver(activity, new Observer<StayBookDateTime>()
        {
            @Override
            public void onChanged(@Nullable StayBookDateTime bookDateTime)
            {
                getViewInterface().setToolbarDateText(bookDateTime.getToMonthDateFormat());
            }
        });

        mViewModel.searchViewModel.setSuggestObserver(activity, new Observer<StaySuggestV2>()
        {
            @Override
            public void onChanged(@Nullable StaySuggestV2 suggest)
            {
                getViewInterface().setToolbarTitle(suggest.getText1());
            }
        });

        mViewModel.setFilterObserver(activity, new Observer<StayFilter>()
        {
            @Override
            public void onChanged(@Nullable StayFilter filter)
            {
                getViewInterface().setOptionFilterSelected(filter != null && filter.isDefault() == false);
            }
        });

        mViewModel.setCategory(Category.ALL);
        mViewModel.categoryType = DailyCategoryType.STAY_ALL;
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

                mAnalytics.setAnalyticsParam(new SearchStayResultAnalyticsParam());
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

            } else if (externalDeepLink.isStaySearchResultView() == true)
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

        try
        {
            mViewModel.categoryType = DailyCategoryType.valueOf(intent.getStringExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_CATEGORY_TYPE));
        } catch (Exception e)
        {
            mViewModel.categoryType = DailyCategoryType.STAY_ALL;
        }

        String checkInDateTime = intent.getStringExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            throw new NullPointerException("DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true");
        }

        mViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
        StaySuggestParcelV2 suggestParcel = intent.getParcelableExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_SUGGEST);

        if (suggestParcel == null || suggestParcel.getSuggest() == null)
        {
            throw new NullPointerException("suggestParcel == null || suggestParcel.getSuggest() == null");
        }

        StaySuggestV2 suggest = suggestParcel.getSuggest();

        mViewModel.setSuggest(suggest);
        mViewModel.setInputKeyword(intent.getStringExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD));

        if (suggest.isLocationSuggestType() == true)
        {
            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DISTANCE;
            mViewModel.getFilter().sortType = StayFilter.SortType.DISTANCE;
            mViewModel.searchViewModel.setRadius(DEFAULT_RADIUS);

            getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);
        } else
        {
            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DEFAULT;
            mViewModel.searchViewModel.setRadius(0.0f);
        }

        mAnalytics.setAnalyticsParam(intent.getParcelableExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_ANALYTICS));
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

        StaySuggestV2 suggest = mViewModel.getSuggest();

        initView(suggest);
    }

    void initView(StaySuggestV2 suggest)
    {
        if (suggest == null)
        {
            return;
        }

        getViewInterface().setFloatingActionViewVisible(suggest.isCampaignTagSuggestType() == false);

        if (suggest.menuType == StaySuggestV2.MenuType.REGION_LOCATION)
        {
            getViewInterface().setToolbarTitleImageResource(R.drawable.search_ic_01_date);
        } else
        {
            getViewInterface().setToolbarTitleImageResource(R.drawable.search_ic_01_search);
        }

        getViewInterface().setToolbarRadiusSpinnerVisible(suggest.isLocationSuggestType());
        getViewInterface().setCategoryVisible(false);
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
            mAnalytics.onEventBackClick(getActivity(), mViewModel.getSuggest().isLocationSuggestType());
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
                        StaySuggestParcelV2 suggestParcel = intent.getParcelableExtra(ResearchStayActivity.INTENT_EXTRA_DATA_SUGGEST);

                        if (suggestParcel == null || suggestParcel.getSuggest() == null)
                        {
                            return;
                        }

                        String checkInDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
                        String checkOutDateTime = intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

                        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                        {
                            return;
                        }

                        getViewInterface().resetFloatingActionViewTranslation();
                        getViewInterface().removeAllFragment();

                        StaySuggestV2 suggest = suggestParcel.getSuggest();

                        mViewModel.setSuggest(suggest);
                        mViewModel.setInputKeyword(intent.getStringExtra(ResearchStayActivity.INTENT_EXTRA_DATA_KEYWORD));

                        if (suggest.isLocationSuggestType() == true)
                        {
                            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DISTANCE;
                            mViewModel.searchViewModel.setRadius(DEFAULT_RADIUS);

                            getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);
                        } else
                        {
                            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DEFAULT;
                            mViewModel.searchViewModel.setRadius(0.0f);
                        }

                        mViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);

                        initView(mViewModel.getSuggest());

                        mViewModel.resetCategory = true;
                        mViewModel.getFilter().reset();
                        mViewModel.setFilter(mViewModel.getFilter());
                        mViewModel.setViewType(ViewType.LIST);
                        mViewModel.setCategory(Category.ALL);

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
                if (intent == null)
                {
                    return;
                }

                StayFilterParcel stayFilterParcel = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_FILTER);

                if (stayFilterParcel == null)
                {
                    return;
                }

                StayFilter stayFilter = stayFilterParcel.getStayFilter();

                if (stayFilter == null)
                {
                    return;
                }

                if (stayFilter.isDistanceSort() == true)
                {
                    Location location = intent.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION);

                    if (location != null)
                    {
                        mViewModel.filterLocation = location;
                    } else
                    {
                        mViewModel.getFilter().sortType = StayFilter.SortType.DEFAULT;
                    }

                    mViewModel.setFilter(stayFilter);

                    getViewInterface().refreshCurrentFragment();
                } else
                {
                    mViewModel.setFilter(stayFilter);

                    getViewInterface().refreshCurrentFragment();
                }
                break;
        }
    }

    protected void onCalendarActivityResult(int resultCode, Intent intent)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (intent == null)
                {
                    return;
                }

                try
                {
                    String checkInDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME);
                    String checkOutDateTime = intent.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);

                    if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                    {
                        return;
                    }

                    getViewInterface().resetFloatingActionViewTranslation();
                    getViewInterface().removeAllFragment();

                    mViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);

                    if (mViewModel.getSuggest().isLocationSuggestType() == true)
                    {
                        mViewModel.searchViewModel.setRadius(DEFAULT_RADIUS);
                        getViewInterface().setRadiusSpinnerSelection(DEFAULT_RADIUS);
                    }

                    initView(mViewModel.getSuggest());

                    mViewModel.resetCategory = true;
                    mViewModel.getFilter().reset();
                    mViewModel.setFilter(mViewModel.getFilter());
                    mViewModel.setViewType(ViewType.LIST);
                    mViewModel.setCategory(Category.ALL);

                    setRefresh(true);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
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
                    } else if (externalDeepLink.isStaySearchResultView() == true)
                    {
                        parseSearchStayResultListView(externalDeepLink, commonDateTime);
                    }

                    clearDeepLink();

                    initView(mViewModel.getSuggest());
                }

                StaySuggestV2 suggest = mViewModel.getSuggest();

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
                    String callByScreen = mAnalytics.getAnalyticsParam() == null ? null : mAnalytics.getAnalyticsParam().mCallByScreen;

                    addCompositeDisposable(getViewInterface().setSearchResultFragment(callByScreen).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<BasePagerFragment>()
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

        StayBookDateTime bookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);

        int index;
        try
        {
            index = Integer.parseInt(externalDeepLink.getIndex());
        } catch (Exception e)
        {
            index = -1;
        }

        if (bookDateTime == null || index < 0)
        {
            throw new RuntimeException("Invalid DeepLink : " + externalDeepLink.getDeepLink());
        }

        StaySuggestV2.CampaignTag suggestItem = new StaySuggestV2.CampaignTag();
        suggestItem.index = index;

        mViewModel.setBookDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        StaySuggestV2 suggest = new StaySuggestV2(StaySuggestV2.MenuType.CAMPAIGN_TAG, suggestItem);

        mViewModel.setSuggest(suggest);
    }

    void parseSearchStayResultListView(DailyExternalDeepLink externalDeepLink, CommonDateTime commonDateTime) throws Exception
    {
        if (externalDeepLink == null || commonDateTime == null)
        {
            throw new NullPointerException("externalDeepLink == null || commonDateTime == null");
        }

        StayBookDateTime bookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
        String word = externalDeepLink.getSearchWord();

        if (bookDateTime == null || DailyTextUtils.isTextEmpty(word) == true)
        {
            throw new RuntimeException("Invalid DeepLink : " + externalDeepLink.getDeepLink());
        }

        StaySuggestV2.Direct suggestItem = new StaySuggestV2.Direct(word);
        StaySuggestV2 suggest = new StaySuggestV2(StaySuggestV2.MenuType.DIRECT, suggestItem);
        Constants.SortType sortType = externalDeepLink.getSorting();

        if (sortType == Constants.SortType.DISTANCE)
        {
            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DISTANCE;
        } else
        {
            mViewModel.getFilter().defaultSortType = StayFilter.SortType.DEFAULT;
        }

        mViewModel.getFilter().sortType = StayFilter.SortType.valueOf(sortType.name());
        mViewModel.setFilter(mViewModel.getFilter());

        mViewModel.setBookDateTime(bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        mViewModel.setSuggest(suggest);
    }

    @Override
    public void onBackClick()
    {
        mAnalytics.onEventCancelClick(getActivity(), mViewModel.getSuggest().isLocationSuggestType());

        setResultCode(Activity.RESULT_CANCELED);
        finish();
    }

    private void setResultCode(int resultCode)
    {
        Intent intent = new Intent();
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_SUGGEST, new StaySuggestParcelV2(mViewModel.getSuggest()));
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME, mViewModel.getBookDateTime().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME, mViewModel.getBookDateTime().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        intent.putExtra(SearchStayResultTabActivity.INTENT_EXTRA_DATA_INPUT_KEYWORD, mViewModel.getInputKeyword());

        setResult(resultCode, intent);
    }

    @Override
    public void onResearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            CommonDateTime commonDateTime = mViewModel.getCommonDateTime();
            StayBookDateTime bookDateTime = mViewModel.getBookDateTime();
            StaySuggestV2 suggest = mViewModel.getSuggest();

            startActivityForResult(ResearchStayActivity.newInstance(getActivity(), commonDateTime.openDateTime, commonDateTime.closeDateTime//
                , commonDateTime.currentDateTime, commonDateTime.dailyDateTime//
                , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , suggest), SearchStayResultTabActivity.REQUEST_CODE_RESEARCH);

            mAnalytics.onEventResearchClick(getActivity(), suggest);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            unLockAll();
        }
    }

    @Override
    public void onEmptyStayResearchClick()
    {
        StaySuggestV2 suggest = mViewModel.getSuggest();

        if (suggest.menuType == StaySuggestV2.MenuType.REGION_LOCATION)
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
        StaySuggestV2 suggest = mViewModel.getSuggest();

        if (suggest.menuType == StaySuggestV2.MenuType.REGION_LOCATION)
        {
            onCalendarClick();
        } else
        {
            onResearchClick();
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
            StayBookDateTime stayBookDateTime = mViewModel.getBookDateTime();
            String checkInDateTime = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

            ArrayList<String> categoryList = new ArrayList();
            categoryList.add(mViewModel.getCategory().code);

            Location location = mViewModel.getFilter().isDistanceSort() ? mViewModel.filterLocation : null;
            float radius = mViewModel.getSuggest().isLocationSuggestType() ? mViewModel.searchViewModel.getRadius() : 0.0f;

            startActivityForResult(StayFilterActivity.newInstance(getActivity(), checkInDateTime, checkOutDateTime//
                , DailyCategoryType.STAY_ALL, mViewModel.getViewType().name()//
                , mViewModel.getFilter(), mViewModel.getSuggest()//
                , categoryList, location, radius, null), SearchStayResultTabActivity.REQUEST_CODE_FILTER);

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

        final int DAYS_OF_MAX_COUNT = 60;

        try
        {
            Calendar calendar = DailyCalendar.getInstance(mViewModel.getCommonDateTime().dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mViewModel.getBookDateTime();

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , AnalyticsManager.ValueType.SEARCH, true//
                , 0, true);

            startActivityForResult(intent, SearchStayResultTabActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }

        mAnalytics.onEventCalendarClick(getActivity());
    }

    @Override
    public void onChangedRadius(float radius)
    {
        if (mViewModel.searchViewModel.getRadius() == radius)
        {
            return;
        }

        mViewModel.searchViewModel.setRadius(radius);

        getViewInterface().refreshCurrentFragment();

        mAnalytics.onEventChangedRadius(getActivity(), mViewModel.getSuggest(), radius);
    }

    @Override
    public void setEmptyViewVisible(boolean visible)
    {
        getViewInterface().setEmptyViewVisible(visible);

        if (visible == true)
        {
            addCompositeDisposable(mCampaignTagRemoteImpl.getCampaignTagList(Constants.ServiceType.HOTEL.name()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<CampaignTag>>()
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
                    getViewInterface().setEmptyViewCampaignTag(getString(R.string.label_search_stay_popular_search_tag), campaignTagList);
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
            addCompositeDisposable(mSearchLocalImpl.addStayIbSearchResultHistory(mViewModel.getCommonDateTime()//
                , mViewModel.getBookDateTime(), mViewModel.getSuggest()).observeOn(AndroidSchedulers.mainThread()).subscribe());
        }
    }

    @Override
    public void onGourmetClick()
    {
        if (lock() == true)
        {
            return;
        }

        mAnalytics.onEventGourmetClick(getActivity());

        setResult(Constants.CODE_RESULT_ACTIVITY_SEARCH_GOURMET);
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

        StaySuggestV2 suggest = new StaySuggestV2(StaySuggestV2.MenuType.CAMPAIGN_TAG//
            , StaySuggestV2.CampaignTag.getSuggestItem(campaignTag));

        mViewModel.setInputKeyword(null);
        mViewModel.setSuggest(suggest);

        setRefresh(true);
        onRefresh(true);

        mAnalytics.onEventCampaignTagClick(getActivity(), campaignTag.index);
    }

    @Override
    public void onCategoryTabSelected(TabLayout.Tab tab)
    {
        if (tab == null)
        {
            return;
        }

        Category category = (Category) tab.getTag();

        mViewModel.setCategory(category);

        getViewInterface().setCategoryTabSelect(tab.getPosition());

        getViewInterface().onSelectedCategory();
    }

    @Override
    public void onCategoryTabReselected(TabLayout.Tab tab)
    {
        getViewInterface().scrollTopCurrentCategory();
    }
}
