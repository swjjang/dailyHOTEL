package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.AreaElement;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySubwayArea;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.SearchStayResultAnalyticsParam;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.area.stay.StayAreaListActivity;
import com.daily.dailyhotel.screen.common.area.stay.inbound.StayAreaTabActivity;
import com.daily.dailyhotel.screen.common.calendar.stay.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.filter.StayFilterActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayTabPresenter extends BaseExceptionPresenter<StayTabActivity, StayTabInterface.ViewInterface> implements StayTabInterface.OnEventListener
{
    private StayTabInterface.AnalyticsInterface mAnalytics;

    CommonRemoteImpl mCommonRemoteImpl;
    StayRemoteImpl mStayRemoteImpl;

    public enum ViewType
    {
        NONE,
        LIST,
        MAP,
    }

    StayTabViewModel mStayViewModel;
    DailyDeepLink mDailyDeepLink;
    boolean mHasStayDetailViewDeepLink;
    boolean mEntryShowCalendar;

    public StayTabPresenter(@NonNull StayTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayTabInterface.ViewInterface createInstanceViewInterface()
    {
        return new StayTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayTabActivity activity)
    {
        setContentView(R.layout.activity_stay_tab_data);

        setAnalytics(new StayTabAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayTabInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        onNewIntent(intent);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {
        mStayViewModel.setCategoryType(getActivity(), intent, DailyCategoryType.STAY_ALL);

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
            }
        } else
        {
            if (intent != null)
            {
                mEntryShowCalendar = intent.getBooleanExtra(StayTabActivity.INTENT_EXTRA_DATA_SHOW_CALENDAR, false);
            }
        }
    }

    private void parseDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            throw new NullPointerException("dailyDeepLink == null");
        }

        if (dailyDeepLink.isInternalDeepLink() == true)
        {
            throw new RuntimeException("dailyDeepLink.isInternalDeepLink() == true");
        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelListView() == true//
                || externalDeepLink.isShortcutView() == true)
            {

            } else if (externalDeepLink.isHotelDetailView() == true)
            {
                mHasStayDetailViewDeepLink = true;

                startStayDetailActivity(externalDeepLink);

                setRefresh(false);
                unLockAll();
            } else
            {
                throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
            }
        } else
        {
            throw new RuntimeException("Invalid DeepLink : " + dailyDeepLink.getDeepLink());
        }
    }

    private void startStayDetailActivity(DailyExternalDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return;
        }

        Intent intent = StayDetailActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink());
        startActivityForResult(intent, StayTabActivity.REQUEST_CODE_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
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

    @Override
    public void onPostCreate()
    {
        String title = getTitle(mStayViewModel.categoryType);

        getViewInterface().setToolbarTitle(title);
    }

    private String getTitle(DailyCategoryType categoryType)
    {
        if (categoryType == null || categoryType == DailyCategoryType.STAY_ALL)
        {
            return getString(R.string.label_daily_hotel);
        } else
        {
            return getString(mStayViewModel.categoryType.getNameResId());
        }
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

        if (isShowPreviewGuide() == true)
        {
            getViewInterface().showPreviewGuide();
        }
    }

    private boolean isShowPreviewGuide()
    {
        final int SHOW_COUNT = 2;

        if (Util.supportPreview(getActivity()) == true)
        {
            // View 타입이 리스트일때만
            if (mStayViewModel.viewType.getValue() == StayTabPresenter.ViewType.LIST)
            {
                int count = DailyPreference.getInstance(getActivity()).getCountPreviewGuide() + 1;

                DailyPreference.getInstance(getActivity()).setCountPreviewGuide(count);

                return count == SHOW_COUNT;
            }
        }

        return false;
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
        boolean backPressed = getViewInterface().onFragmentBackPressed();

        if (backPressed == false)
        {
            mAnalytics.onBackClick(getActivity(), mStayViewModel.categoryType);
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

        Util.restartApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case StayTabActivity.REQUEST_CODE_CALENDAR:
                onCalendarActivityResult(resultCode, data);
                break;

            case StayTabActivity.REQUEST_CODE_REGION:
                onRegionActivityResult(resultCode, data);
                break;

            case StayTabActivity.REQUEST_CODE_FILTER:
                onFilterActivityResult(resultCode, data);
                break;

            // 딥링크로 진입한 경우이다.
            case StayTabActivity.REQUEST_CODE_DETAIL:
            case StayTabActivity.REQUEST_CODE_SEARCH:
            case StayTabActivity.REQUEST_CODE_SEARCH_RESULT:
                if (mHasStayDetailViewDeepLink == true)
                {
                    setRefresh(true);
                }
                break;
        }

        mHasStayDetailViewDeepLink = false;
        clearDeepLink();
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

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<CommonDateTime, ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>>()
        {
            @Override
            public ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> apply(CommonDateTime commonDateTime) throws Exception
            {
                setCommonDateTime(commonDateTime);

                try
                {
                    // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                    if (mStayViewModel.bookDateTime.getValue() == null || mStayViewModel.bookDateTime.getValue().validate() == false)
                    {
                        setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                    } else
                    {
                        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                        if (DailyCalendar.compareDateDay(commonDateTime.dailyDateTime, mStayViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)) > 0)
                        {
                            setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                        }
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                if (mHasStayDetailViewDeepLink == true)
                {
                    return Observable.just(new Pair(null, null));
                } else
                {
                    return mStayRemoteImpl.getRegionList(mStayViewModel.categoryType);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>, ObservableSource<Pair<Boolean, List<Category>>>>()
        {
            @Override
            public ObservableSource<Pair<Boolean, List<Category>>> apply(Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> pair) throws Exception
            {
                if (mHasStayDetailViewDeepLink == true)
                {
                    return Observable.just(new Pair(false, null));
                } else
                {
                    List<StayAreaGroup> areaGroupList = pair.first;
                    LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap = pair.second;
                    Pair<PreferenceRegion.AreaType, Pair<StayRegion, List<Category>>> areaTypeRegionCategoryPair;
                    boolean needSetPreferenceRegion;

                    if (mDailyDeepLink != null)
                    {
                        needSetPreferenceRegion = false;
                        areaTypeRegionCategoryPair = processRegionCategoryDeepLink(mDailyDeepLink, areaGroupList, areaGroupMap);

                        if (areaTypeRegionCategoryPair == null || areaTypeRegionCategoryPair.first == null || areaTypeRegionCategoryPair.second == null)
                        {
                            areaTypeRegionCategoryPair = processRegionCategoryByPreferenceRegion(getPreferenceRegion(mStayViewModel.categoryType), areaGroupList, areaGroupMap);
                        }
                    } else
                    {
                        needSetPreferenceRegion = true;
                        areaTypeRegionCategoryPair = processRegionCategoryByPreferenceRegion(getPreferenceRegion(mStayViewModel.categoryType), areaGroupList, areaGroupMap);
                    }

                    if (areaTypeRegionCategoryPair == null || areaTypeRegionCategoryPair.first == null || areaTypeRegionCategoryPair.second == null)
                    {
                        areaTypeRegionCategoryPair = new Pair(PreferenceRegion.AreaType.AREA, getDefaultRegion(areaGroupList));
                    }

                    Pair<StayRegion, List<Category>> regionCategoryPair = areaTypeRegionCategoryPair.second;
                    StayRegion stayRegion = regionCategoryPair.first;
                    List<Category> categoryList = regionCategoryPair.second;

                    mStayViewModel.stayRegion.setValue(stayRegion);

                    if (needSetPreferenceRegion == true)
                    {
                        setPreferenceRegion(areaTypeRegionCategoryPair.first, stayRegion);
                    }

                    if (categoryList == null)
                    {
                        categoryList = new ArrayList<>();
                        categoryList.add(Category.ALL);
                    }

                    return Observable.just(new Pair(true, categoryList));
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Pair<Boolean, List<Category>>, ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> apply(Pair<Boolean, List<Category>> pair) throws Exception
            {
                boolean notifyDataChanged = pair.first;
                List<Category> categoryList = pair.second;

                if (notifyDataChanged == true)
                {
                    notifyDateTextChanged();
                    notifyRegionTextChanged();

                    return getViewInterface().getCategoryTabLayout(categoryList, mStayViewModel.selectedCategory.getValue());
                } else
                {
                    return Observable.just(false);
                }
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();

                if (mEntryShowCalendar == true)
                {
                    mEntryShowCalendar = false;

                    onCalendarClick();
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

    Pair<PreferenceRegion.AreaType, Pair<StayRegion, List<Category>>> processRegionCategoryDeepLink(@NonNull DailyDeepLink deepLink, List<StayAreaGroup> areaGroupList, LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap)
    {
        if (deepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) deepLink;

            if (externalDeepLink.isHotelListView() == true//
                || externalDeepLink.isShortcutView() == true)
            {
                Pair<StayRegion, List<Category>> regionCategoryPair;

                if (externalDeepLink.hasStationIndexParam() == true)
                {
                    regionCategoryPair = parseSubwayAreaDeepLinkStayList(areaGroupMap, mStayViewModel.commonDateTime.getValue(), externalDeepLink);
                    return regionCategoryPair == null ? null : new Pair(PreferenceRegion.AreaType.SUBWAY_AREA, regionCategoryPair);
                } else
                {
                    regionCategoryPair = parseAreaDeepLinkStayList(areaGroupList, mStayViewModel.commonDateTime.getValue(), externalDeepLink);
                    return regionCategoryPair == null ? null : new Pair(PreferenceRegion.AreaType.AREA, regionCategoryPair);
                }
            }
        }

        return null;
    }

    Pair<PreferenceRegion.AreaType, Pair<StayRegion, List<Category>>> processRegionCategoryByPreferenceRegion(PreferenceRegion preferenceRegion, List<StayAreaGroup> areaGroupList, LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap)
    {
        if (preferenceRegion == null)
        {
            return new Pair(PreferenceRegion.AreaType.AREA, getDefaultRegion(areaGroupList));
        } else
        {
            switch (preferenceRegion.areaType)
            {
                case AREA:
                    return new Pair(PreferenceRegion.AreaType.AREA, getRegionCategoryByPreferenceRegion(areaGroupList, preferenceRegion));

                case SUBWAY_AREA:
                    return new Pair(PreferenceRegion.AreaType.SUBWAY_AREA, getRegionCategoryByPreferenceRegion(areaGroupMap, preferenceRegion));

                default:
                    return null;
            }
        }
    }

    void setPreferenceRegion(PreferenceRegion.AreaType areaType, StayRegion stayRegion)
    {
        if (areaType == null || stayRegion == null)
        {
            return;
        }

        if (areaType == PreferenceRegion.AreaType.SUBWAY_AREA)
        {
            AreaElement areaGroupElement = stayRegion.getAreaGroupElement();
            setPreferenceSubwayArea(((StaySubwayAreaGroup) areaGroupElement).getRegion().name, stayRegion.getAreaGroupName(), stayRegion.getAreaName());
        } else
        {
            setPreferenceArea(stayRegion.getAreaGroupName(), stayRegion.getAreaName());
        }
    }

    private void setPreferenceArea(String areaGroupName, String areaName)
    {
        PreferenceRegion preferenceRegion = new PreferenceRegion(PreferenceRegion.AreaType.AREA);
        preferenceRegion.regionName = preferenceRegion.areaGroupName = areaGroupName;
        preferenceRegion.areaName = areaName;
        preferenceRegion.overseas = false;

        DailyPreference.getInstance(getActivity()).setDailyRegion(mStayViewModel.categoryType, preferenceRegion);
    }

    private void setPreferenceSubwayArea(String regionName, String areaGroupName, String areaName)
    {
        PreferenceRegion preferenceRegion = new PreferenceRegion(PreferenceRegion.AreaType.SUBWAY_AREA);
        preferenceRegion.regionName = regionName;
        preferenceRegion.areaGroupName = areaGroupName;
        preferenceRegion.areaName = areaName;
        preferenceRegion.overseas = false;

        DailyPreference.getInstance(getActivity()).setDailyRegion(mStayViewModel.categoryType, preferenceRegion);
    }

    Pair<StayRegion, List<Category>> getDefaultRegion(@NonNull List<StayAreaGroup> areaGroupList)
    {
        StayAreaGroup areaGroup = areaGroupList.get(0);

        if (areaGroup.getAreaCount() == 0)
        {
            return new Pair(new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, new StayArea(StayArea.ALL, areaGroup.name)), areaGroup.getCategoryList());
        } else
        {
            StayArea area = areaGroup.getAreaList().get(0);

            return new Pair(new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area), area.getCategoryList());
        }
    }

    PreferenceRegion getPreferenceRegion(DailyCategoryType categoryType)
    {
        return DailyPreference.getInstance(getActivity()).getDailyRegion(categoryType);
    }

    Pair<StayRegion, List<Category>> getRegionCategoryByPreferenceRegion(@NonNull List<StayAreaGroup> areaGroupList, @NonNull PreferenceRegion preferenceRegion)
    {
        int size = areaGroupList.size();

        StayAreaGroup stayAreaGroup = null;

        for (int i = 0; i < size; i++)
        {
            if (areaGroupList.get(i).name.equalsIgnoreCase(preferenceRegion.areaGroupName) == true)
            {
                stayAreaGroup = areaGroupList.get(i);
                break;
            }
        }

        if (stayAreaGroup != null)
        {
            if (stayAreaGroup.getAreaCount() == 0)
            {
                return new Pair(new StayRegion(PreferenceRegion.AreaType.AREA, stayAreaGroup, new StayArea(StayArea.ALL, stayAreaGroup.name)), stayAreaGroup.getCategoryList());
            } else
            {
                for (Area area : stayAreaGroup.getAreaList())
                {
                    if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                    {
                        return new Pair(new StayRegion(PreferenceRegion.AreaType.AREA, stayAreaGroup, area), ((StayArea) area).getCategoryList());
                    }
                }
            }
        }

        return null;
    }

    Pair<StayRegion, List<Category>> getRegionCategoryByPreferenceRegion(@NonNull LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayAreaMap, @NonNull PreferenceRegion preferenceRegion)
    {
        Iterator<Area> iterator = subwayAreaMap.keySet().iterator();

        while (iterator.hasNext() == true)
        {
            Area region = iterator.next();

            if (region.name.equalsIgnoreCase(preferenceRegion.regionName) == true)
            {
                for (StaySubwayAreaGroup subwayAreaGroup : subwayAreaMap.get(region))
                {
                    if (subwayAreaGroup.name.equalsIgnoreCase(preferenceRegion.areaGroupName) == true)
                    {
                        StaySubwayArea subwayArea = null;

                        for (StaySubwayArea area : subwayAreaGroup.getAreaList())
                        {
                            if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                            {
                                subwayArea = area;
                                break;
                            }
                        }

                        if (subwayArea == null)
                        {
                            subwayArea = subwayAreaGroup.getAreaList().get(0);
                        }

                        return new Pair(new StayRegion(PreferenceRegion.AreaType.SUBWAY_AREA, subwayAreaGroup, subwayArea), subwayArea.getCategoryList());
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void onBackClick()
    {
        finish();
    }

    @Override
    public void onCategoryTabSelected(TabLayout.Tab tab)
    {
        if (tab == null)
        {
            return;
        }

        Category category = (Category) tab.getTag();

        mStayViewModel.selectedCategory.setValue(category);

        getViewInterface().setCategoryTabSelect(tab.getPosition());

        getViewInterface().onSelectedCategory();
    }

    @Override
    public void onCategoryTabReselected(TabLayout.Tab tab)
    {
        getViewInterface().scrollTopCurrentCategory();
    }

    @Override
    public void onRegionClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            StayBookDateTime stayBookDateTime = mStayViewModel.bookDateTime.getValue();
            String checkInDateTime = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

            startActivityForResult(StayAreaTabActivity.newInstance(getActivity()//
                , checkInDateTime, checkOutDateTime, mStayViewModel.categoryType//
                , mStayViewModel.selectedCategory.getValue().code), StayTabActivity.REQUEST_CODE_REGION);

            mAnalytics.onViewTypeClick(getActivity(), mStayViewModel.categoryType, mStayViewModel.viewType.getValue());
        } catch (Exception e)
        {
            Crashlytics.logException(e);

            setRefresh(true);
            onRefresh(true);
        }
    }

    @Override
    public void onCalendarClick()
    {
        if (lock() == true)
        {
            return;
        }

        startCalendar(AnalyticsManager.ValueType.LIST);

        mAnalytics.onCalendarClick(getActivity(), mStayViewModel.categoryType);
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }

        StayBookDateTime stayBookDateTime = mStayViewModel.bookDateTime.getValue();
        String checkInDateTime = stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
        String checkOutDateTime = stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

        ArrayList<String> categoryList = new ArrayList();
        categoryList.add(mStayViewModel.selectedCategory.getValue().code);

        Location location;

        if (mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE)
        {
            location = mStayViewModel.location.getValue();
        } else
        {
            location = null;
        }

        StaySuggest suggest = toSuggest(mStayViewModel.stayRegion.getValue());

        if (suggest == null)
        {
            unLockAll();
            return;
        }

        startActivityForResult(StayFilterActivity.newInstance(getActivity(), StayFilterActivity.ListType.DEFAULT//
            , checkInDateTime, checkOutDateTime//
            , mStayViewModel.categoryType, mStayViewModel.viewType.getValue().name()//
            , mStayViewModel.stayFilter.getValue(), suggest//
            , categoryList, location, 0, null), StayTabActivity.REQUEST_CODE_FILTER);

        mAnalytics.onFilterClick(getActivity(), mStayViewModel.categoryType, mStayViewModel.viewType.getValue());
    }

    private StaySuggest toSuggest(StayRegion region)
    {
        if (region == null)
        {
            return null;
        }

        switch (region.getAreaType())
        {
            case AREA:
            {
                StaySuggest.AreaGroup suggestItem = new StaySuggest.AreaGroup();
                suggestItem.index = region.getAreaGroupIndex();
                suggestItem.name = region.getAreaGroupName();

                if (region.getAreaElement() != null)
                {
                    StaySuggest.Area area = new StaySuggest.Area();
                    area.index = region.getAreaIndex();
                    area.name = region.getAreaName();
                    suggestItem.area = area;
                }

                return new StaySuggest(StaySuggest.MenuType.SUGGEST, suggestItem);
            }

            case SUBWAY_AREA:
            {
                StaySuggest.Station suggestItem = new StaySuggest.Station();
                suggestItem.index = region.getAreaIndex();
                suggestItem.name = region.getAreaName();

                return new StaySuggest(StaySuggest.MenuType.SUGGEST, suggestItem);
            }
        }

        return null;
    }

    @Override
    public void onViewTypeClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().expandedToolbar();

        switch (mStayViewModel.viewType.getValue())
        {
            // 현재 리스트 화면인 경우
            case LIST:
            {
                screenLock(true);

                mStayViewModel.viewType.setValue(ViewType.MAP);
                break;
            }

            // 현재 맵화면인 경우
            case MAP:
            {
                mStayViewModel.viewType.setValue(ViewType.LIST);

                clearCompositeDisposable();

                unLockAll();
                break;
            }
        }

        mAnalytics.onViewTypeClick(getActivity(), mStayViewModel.categoryType, mStayViewModel.viewType.getValue());
    }

    @Override
    public void onSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        try
        {
            StayBookDateTime stayBookDateTime = mStayViewModel.bookDateTime.getValue();

            startActivityForResult(SearchActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
                , stayBookDateTime.getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT))//
                , StayTabActivity.REQUEST_CODE_SEARCH);

            mAnalytics.onSearchClick(getActivity(), mStayViewModel.categoryType, mStayViewModel.viewType.getValue());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCategoryFlicking(String categoryName)
    {

        mAnalytics.onCategoryFlicking(getActivity(), mStayViewModel.categoryType, categoryName);
    }

    @Override
    public void onCategoryClick(String categoryName)
    {
        mAnalytics.onCategoryClick(getActivity(), mStayViewModel.categoryType, categoryName);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayViewModel = ViewModelProviders.of(activity, new StayTabViewModel.StayTabViewModelFactory(getActivity())).get(StayTabViewModel.class);

        mStayViewModel.viewType.observe(activity, new Observer<ViewType>()
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
            }
        });

        mStayViewModel.selectedCategory.observe(activity, new Observer<Category>()
        {
            @Override
            public void onChanged(@Nullable Category category)
            {
                DailyPreference.getInstance(getActivity()).setStayCategory(category.name, category.code);
            }
        });

        mStayViewModel.stayRegion.observe(activity, new Observer<StayRegion>()
        {
            @Override
            public void onChanged(@Nullable StayRegion stayRegion)
            {

            }
        });

        mStayViewModel.stayFilter.observe(activity, new Observer<StayFilter>()
        {
            @Override
            public void onChanged(@Nullable StayFilter stayFilter)
            {
                getViewInterface().setOptionFilterSelected(stayFilter != null && stayFilter.isDefault() == false);
            }
        });
    }

    private void startCalendar(String callByScreen)
    {
        final int DAYS_OF_MAX_COUNT = 60;

        try
        {
            Calendar calendar = DailyCalendar.getInstance(mStayViewModel.commonDateTime.getValue().dailyDateTime, DailyCalendar.ISO_8601_FORMAT);
            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAX_COUNT - 1);
            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            StayBookDateTime stayBookDateTime = mStayViewModel.bookDateTime.getValue();

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , startDateTime, endDateTime, DAYS_OF_MAX_COUNT - 1//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , callByScreen, true//
                , 0, true);

            startActivityForResult(intent, StayTabActivity.REQUEST_CODE_CALENDAR);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mStayViewModel.commonDateTime.setValue(commonDateTime);
    }

    /**
     * @param checkInDateTime  ISO-8601
     * @param checkOutDateTime ISO-8601
     */
    void setStayBookDateTime(String checkInDateTime, String checkOutDateTime)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
        {
            return;
        }

        try
        {
            mStayViewModel.setBookDateTime(checkInDateTime, checkOutDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void setStayBookDateTime(String checkInDateTime, int afterDay)
    {
        if (DailyTextUtils.isTextEmpty(checkInDateTime) == true || afterDay == 0)
        {
            return;
        }

        try
        {
            mStayViewModel.setBookDateTime(checkInDateTime, 0, checkInDateTime, afterDay);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyDateTextChanged()
    {
        if (mStayViewModel == null || mStayViewModel.bookDateTime.getValue() == null)
        {
            return;
        }

        StayBookDateTime stayBookDateTime = mStayViewModel.bookDateTime.getValue();
        String checkInDay = stayBookDateTime.getCheckInDateTime("M.d(EEE)");
        String checkOutDay = stayBookDateTime.getCheckOutDateTime("M.d(EEE)");

        getViewInterface().setToolbarDateText(String.format(Locale.KOREA, "%s - %s", checkInDay, checkOutDay));
    }

    void notifyRegionTextChanged()
    {
        if (mStayViewModel == null || mStayViewModel.stayRegion.getValue() == null)
        {
            return;
        }

        getViewInterface().setToolbarRegionText(mStayViewModel.stayRegion.getValue().getAreaName());
    }

    private void onCalendarActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:

                if (data != null && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME) == true//
                    && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME) == true)
                {
                    String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_IN_DATETIME);
                    String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATETIME);

                    if (DailyTextUtils.isTextEmpty(checkInDateTime, checkOutDateTime) == true)
                    {
                        return;
                    }

                    setStayBookDateTime(checkInDateTime, checkOutDateTime);
                    notifyDateTextChanged();

                    getViewInterface().refreshCurrentCategory();
                }
                break;
        }
    }

    private void onRegionActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
            case com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR:
                if (data != null && data.hasExtra(StayAreaListActivity.INTENT_EXTRA_DATA_REGION) == true)
                {
                    StayRegionParcel stayRegionParcel = data.getParcelableExtra(StayAreaListActivity.INTENT_EXTRA_DATA_REGION);

                    if (stayRegionParcel == null)
                    {
                        return;
                    }

                    StayRegion region = stayRegionParcel.getRegion();

                    if (region == null || region.getAreaGroupElement() == null || region.getAreaElement() == null)
                    {
                        return;
                    }

                    mStayViewModel.stayRegion.setValue(region);

                    // 지역이 수정 되면 필터가 초기화 된다.
                    mStayViewModel.stayFilter.setValue(mStayViewModel.stayFilter.getValue().reset());
                    mStayViewModel.selectedCategory.setValue(Category.ALL);

                    setRefresh(true);

                    boolean changedAreaGroup = data.getBooleanExtra(StayAreaListActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, false);

                    if (changedAreaGroup == true)
                    {
                        mAnalytics.onRegionChanged(getActivity(), mStayViewModel.categoryType, region.getAreaGroupName());
                    }
                }

                if (resultCode == com.daily.base.BaseActivity.RESULT_CODE_START_CALENDAR)
                {
                    lock();
                    startCalendar(AnalyticsManager.Label.CHANGE_LOCATION);
                }
                break;

            case com.daily.base.BaseActivity.RESULT_CODE_START_AROUND_SEARCH:
            {
                StaySuggest.Location suggestItem = new StaySuggest.Location();
                StaySuggest suggest = new StaySuggest(StaySuggest.MenuType.REGION_LOCATION, suggestItem);
                StayBookDateTime bookDateTime = mStayViewModel.getBookDateTime();

                if (mStayViewModel.categoryType == DailyCategoryType.STAY_ALL)
                {
                    SearchStayResultAnalyticsParam analyticsParam = new SearchStayResultAnalyticsParam();
                    analyticsParam.mCallByScreen = AnalyticsManager.Screen.HOME;

                    startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity()//
                        , DailyCategoryType.STAY_ALL//
                        , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , suggest, null, analyticsParam)//
                        , StayTabActivity.REQUEST_CODE_SEARCH_RESULT);
                } else
                {
                    SearchStayResultAnalyticsParam analyticsParam = new SearchStayResultAnalyticsParam();
                    analyticsParam.mCallByScreen = AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC;

                    startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity()//
                        , mStayViewModel.categoryType//
                        , bookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , bookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                        , suggest, null, analyticsParam)//
                        , StayTabActivity.REQUEST_CODE_SEARCH_RESULT);
                }

                break;
            }
        }
    }

    private void onFilterActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:
                if (data != null)
                {
                    try
                    {
                        StayFilterParcel stayFilterParcel = data.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_STAY_FILTER);

                        if (stayFilterParcel == null)
                        {
                            return;
                        }

                        StayFilter stayFilter = stayFilterParcel.getStayFilter();

                        if (stayFilter == null)
                        {
                            return;
                        }

                        if (stayFilter.sortType == StayFilter.SortType.DISTANCE)
                        {
                            Location location = data.getParcelableExtra(StayFilterActivity.INTENT_EXTRA_DATA_LOCATION);

                            if (location != null)
                            {
                                mStayViewModel.location.setValue(location);
                            } else
                            {
                                mStayViewModel.stayFilter.getValue().sortType = StayFilter.SortType.DEFAULT;
                            }

                            mStayViewModel.stayFilter.setValue(stayFilter);

                            getViewInterface().refreshCurrentCategory();
                        } else
                        {

                            mStayViewModel.stayFilter.setValue(stayFilter);

                            getViewInterface().refreshCurrentCategory();
                        }
                    } catch (Exception e)
                    {
                        // 예외 처리 추가 원인 찾기
                        Crashlytics.log(data.toString());
                        Crashlytics.logException(e);

                        // 지역이 수정 되면 필터가 초기화 된다.
                        mStayViewModel.stayFilter.setValue(mStayViewModel.stayFilter.getValue().reset());
                        mStayViewModel.selectedCategory.setValue(Category.ALL);

                        setRefresh(true);
                    }
                }
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Deep Link
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Pair<StayRegion, List<Category>> parseAreaDeepLinkStayList(List<StayAreaGroup> areaGroupList, CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null && externalDeepLink == null)
        {
            return null;
        }

        try
        {
            StayFilter stayFilter = mStayViewModel.stayFilter.getValue() == null ? new StayFilter().reset() : mStayViewModel.stayFilter.getValue();
            stayFilter.sortType = StayFilter.SortType.valueOf(externalDeepLink.getSorting().name());
            mStayViewModel.stayFilter.setValue(stayFilter);

            getViewInterface().setOptionFilterSelected(mStayViewModel.stayFilter.getValue().isDefault() == false);

            StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
            setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));


            int provinceIndex = externalDeepLink.getProvinceIndex();
            int areaIndex = externalDeepLink.getAreaIndex();

            return searchRegionCategory(areaGroupList, provinceIndex, areaIndex);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            externalDeepLink.clear();
        }

        return null;
    }

    private Pair<StayRegion, List<Category>> searchRegionCategory(List<StayAreaGroup> areaGroupList, int areaGroupIndex, int areaIndex)
    {
        if (areaGroupList == null || areaGroupIndex <= 0)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.index == areaGroupIndex)
            {
                if (areaIndex > 0 && areaGroup.getAreaCount() > 0)
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.index == areaIndex)
                        {
                            return new Pair(new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area), area.getCategoryList());
                        }
                    }
                }
            }
        }

        return null;
    }

    private Pair<StayRegion, List<Category>> parseSubwayAreaDeepLinkStayList(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap, CommonDateTime commonDateTime, DailyExternalDeepLink externalDeepLink)
    {
        if (commonDateTime == null && externalDeepLink == null)
        {
            return null;
        }

        try
        {
            StayFilter stayFilter = mStayViewModel.stayFilter.getValue() == null ? new StayFilter().reset() : mStayViewModel.stayFilter.getValue();
            stayFilter.sortType = StayFilter.SortType.valueOf(externalDeepLink.getSorting().name());
            mStayViewModel.stayFilter.setValue(stayFilter);

            getViewInterface().setOptionFilterSelected(mStayViewModel.stayFilter.getValue().isDefault() == false);

            StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
            setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

            int stationIndex = externalDeepLink.getStationIndex();

            return searchRegionCategory(areaGroupMap, stationIndex);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        } finally
        {
            clearDeepLink();
        }

        return null;
    }

    private Pair<StayRegion, List<Category>> searchRegionCategory(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap, int stationIndex)
    {
        if (areaGroupMap == null || stationIndex <= 0)
        {
            return null;
        }

        Iterator<Area> iterator = areaGroupMap.keySet().iterator();

        while (iterator.hasNext() == true)
        {
            Area region = iterator.next();

            for (StaySubwayAreaGroup areaGroupList : areaGroupMap.get(region))
            {
                for (StaySubwayArea area : areaGroupList.getAreaList())
                {
                    if (area.index == stationIndex)
                    {
                        return new Pair(new StayRegion(PreferenceRegion.AreaType.SUBWAY_AREA, areaGroupList, area), area.getCategoryList());
                    }
                }
            }
        }

        return null;
    }
}
