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
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.entity.StaySuggest;
import com.daily.dailyhotel.parcel.StayFilterParcel;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.screen.common.area.stay.StayAreaListActivity;
import com.daily.dailyhotel.screen.common.area.stay.inbound.StayAreaTabActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.calendar.StayCalendarActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.filter.StayFilterActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.util.DailyIntentUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONObject;

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
    private static final int DAYS_OF_MAXCOUNT = 60;
    private static final int NIGHTS_OF_MAXCOUNT = 60;

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
    boolean mHasDeepLink;

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
        if (DailyIntentUtils.hasDeepLink(intent) == false)
        {
            return;
        }

        try
        {
            mDailyDeepLink = DailyIntentUtils.getDeepLink(intent);
            parseDeepLink(mDailyDeepLink);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            clearDeepLink();
        }
    }

    private void parseDeepLink(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || dailyDeepLink.isValidateLink() == false)
        {
            throw new NullPointerException("dailyDeepLink == null || dailyDeepLink.isValidateLink() == false");
        }

        if (dailyDeepLink.isInternalDeepLink() == true)
        {

        } else if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelDetailView() == true)
            {
                parseDetailViewDeepLink(externalDeepLink);

                setRefresh(false);
                unLockAll();
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

        mHasDeepLink = false;

        mDailyDeepLink.clear();
        mDailyDeepLink = null;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_daily_hotel));
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
            mAnalytics.onBackClick(getActivity());
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
                if (mHasDeepLink == true)
                {
                    setRefresh(true);
                }
                break;
        }

        mHasDeepLink = false;
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
                    if (mStayViewModel.stayBookDateTime.getValue() == null || mStayViewModel.stayBookDateTime.getValue().validate() == false)
                    {
                        setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                    } else
                    {
                        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                        if (DailyCalendar.compareDateDay(commonDateTime.dailyDateTime, mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)) > 0)
                        {
                            setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                        }
                    }

                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                        && processDeepLinkByDateTime(commonDateTime, mDailyDeepLink) == true)
                    {
                        return Observable.just(new Pair(null, null));
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return mStayRemoteImpl.getRegionList(DailyCategoryType.STAY_ALL);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>, ObservableSource<Pair<Boolean, List<Category>>>>()
        {
            @Override
            public ObservableSource<Pair<Boolean, List<Category>>> apply(Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> pair) throws Exception
            {
                if (mHasDeepLink == true || pair.first == null || pair.second == null)
                {
                    return Observable.just(new Pair(false, null));
                } else
                {
                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
                    {
                        List<Category> categoryList = processDeepLinkByRegionList(pair.first, mStayViewModel.commonDateTime.getValue(), mDailyDeepLink);

                        if (categoryList == null)
                        {
                            mStayViewModel.stayRegion.setValue(searchRegion(DailyCategoryType.STAY_ALL, pair));

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, categoryList));
                        }
                    } else
                    {
                        if (mStayViewModel.stayRegion.getValue() == null)
                        {
                            mStayViewModel.stayRegion.setValue(searchRegion(DailyCategoryType.STAY_ALL, pair));

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, searchRegionCategory(mStayViewModel.stayRegion.getValue(), areaGroupList)));
                        }
                    }
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
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean result) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private void onRefreshArea()
    {
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<CommonDateTime, ObservableSource<List<StayAreaGroup>>>()
        {
            @Override
            public ObservableSource<List<StayAreaGroup>> apply(CommonDateTime commonDateTime) throws Exception
            {
                setCommonDateTime(commonDateTime);

                try
                {
                    // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                    if (mStayViewModel.stayBookDateTime.getValue() == null || mStayViewModel.stayBookDateTime.getValue().validate() == false)
                    {
                        setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                    } else
                    {
                        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                        if (DailyCalendar.compareDateDay(commonDateTime.dailyDateTime, mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)) > 0)
                        {
                            setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                        }
                    }

                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                        && processDeepLinkByDateTime(commonDateTime, mDailyDeepLink) == true)
                    {
                        return Observable.just(new ArrayList<>());
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return mStayRemoteImpl.getAreaList(DailyCategoryType.STAY_ALL);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<StayAreaGroup>, ObservableSource<Pair<Boolean, List<Category>>>>()
        {
            @Override
            public ObservableSource<Pair<Boolean, List<Category>>> apply(List<StayAreaGroup> areaGroupList) throws Exception
            {
                if (mHasDeepLink == true)
                {
                    return Observable.just(new Pair(false, null));
                } else
                {
                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
                    {
                        List<Category> categoryList = processDeepLinkByRegionList(areaGroupList, mStayViewModel.commonDateTime.getValue(), mDailyDeepLink);

                        if (categoryList == null)
                        {
                            Pair<StayRegion, List<Category>> pair = searchRegion(areaGroupList, getPreferenceRegion(DailyCategoryType.STAY_ALL));

                            mStayViewModel.stayRegion.setValue(pair.first);

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, categoryList));
                        }
                    } else
                    {
                        if (mStayViewModel.stayRegion.getValue() == null)
                        {
                            Pair<StayRegion, List<Category>> pair = searchRegion(areaGroupList, getPreferenceRegion(DailyCategoryType.STAY_ALL));

                            mStayViewModel.stayRegion.setValue(pair.first);

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, searchRegionCategory(areaGroupList, mStayViewModel.stayRegion.getValue())));
                        }
                    }
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
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean result) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private void onRefreshSubwayArea()
    {
        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<CommonDateTime, ObservableSource<LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>()
        {
            @Override
            public ObservableSource<LinkedHashMap<Area, List<StaySubwayAreaGroup>>> apply(CommonDateTime commonDateTime) throws Exception
            {
                setCommonDateTime(commonDateTime);

                try
                {
                    // 체크인 시간이 설정되어 있지 않는 경우 기본값을 넣어준다.
                    if (mStayViewModel.stayBookDateTime.getValue() == null || mStayViewModel.stayBookDateTime.getValue().validate() == false)
                    {
                        setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                    } else
                    {
                        // 예외 처리로 보고 있는 체크인/체크아웃 날짜가 지나 간경우 다음 날로 변경해준다.
                        if (DailyCalendar.compareDateDay(commonDateTime.dailyDateTime, mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)) > 0)
                        {
                            setStayBookDateTime(commonDateTime.dailyDateTime, 1);
                        }
                    }

                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true //
                        && processDeepLinkByDateTime(commonDateTime, mDailyDeepLink) == true)
                    {
                        return Observable.just(new LinkedHashMap<>());
                    }
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
                }

                return mStayRemoteImpl.getSubwayAreaList(DailyCategoryType.STAY_ALL);
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<LinkedHashMap<Area, List<StaySubwayAreaGroup>>, ObservableSource<Pair<Boolean, List<Category>>>>()
        {
            @Override
            public ObservableSource<Pair<Boolean, List<Category>>> apply(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap) throws Exception
            {
                if (mHasDeepLink == true)
                {
                    return Observable.just(new Pair(false, null));
                } else
                {
                    if (mDailyDeepLink != null && mDailyDeepLink.isValidateLink() == true)
                    {
                        List<Category> categoryList = processDeepLinkByRegionList(areaGroupMap, mStayViewModel.commonDateTime.getValue(), mDailyDeepLink);

                        if (categoryList == null)
                        {
                            mStayViewModel.stayRegion.setValue(searchRegion(DailyCategoryType.STAY_ALL, pair));

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, categoryList));
                        }
                    } else
                    {
                        if (mStayViewModel.stayRegion.getValue() == null)
                        {
                            mStayViewModel.stayRegion.setValue(searchRegion(DailyCategoryType.STAY_ALL, pair));

                            return Observable.just(new Pair(true, pair.second));
                        } else
                        {
                            return Observable.just(new Pair(true, searchRegionCategory(mStayViewModel.stayRegion.getValue(), areaGroupList)));
                        }
                    }
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
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean result) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }

    private StayRegion searchRegion(DailyCategoryType categoryType, Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> pair)
    {
        StayRegion stayRegion = null;
        PreferenceRegion preferenceRegion = getPreferenceRegion(categoryType);

        if (preferenceRegion != null)
        {
            switch (preferenceRegion.areaType)
            {
                case AREA:
                    stayRegion = searchArea(pair.first, preferenceRegion);
                    break;

                case SUBWAY_AREA:
                    stayRegion = searchSubwayArea(pair.second, preferenceRegion);
                    break;
            }
        }

        if (stayRegion == null)
        {
            StayAreaGroup stayAreaGroup = pair.first.get(0);
            stayRegion = new StayRegion(stayAreaGroup, stayAreaGroup);
        }

        return stayRegion;
    }

    PreferenceRegion getPreferenceRegion(DailyCategoryType dailyCategoryType)
    {
        return DailyPreference.getInstance(getActivity()).getDailyRegion(dailyCategoryType);
    }

    Pair<StayRegion, List<Category>> searchRegion(List<StayAreaGroup> areaGroupList, PreferenceRegion preferenceRegion)
    {
        if (areaGroupList == null || areaGroupList.size() == 0 || preferenceRegion == null)
        {
            return null;
        }

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
                return new Pair(new StayRegion(stayAreaGroup, stayAreaGroup), stayAreaGroup.getCategoryList());
            } else
            {
                for (StayArea area : stayAreaGroup.getAreaList())
                {
                    if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                    {
                        return new Pair(new StayRegion(stayAreaGroup, area), area.getCategoryList());
                    }
                }
            }
        }

        return null;
    }

    List<Category> searchRegionCategory(List<StayAreaGroup> areaGroupList, StayRegion stayRegion)
    {
        if (areaGroupList == null || areaGroupList.size() == 0 || stayRegion == null)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.name.equalsIgnoreCase(stayRegion.getAreaGroupName()) == true)
            {
                if (areaGroup.getAreaCount() == 0)
                {
                    return areaGroup.getCategoryList();
                } else
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.name.equalsIgnoreCase(stayRegion.getAreaName()) == true)
                        {
                            return area.getCategoryList();
                        }
                    }
                }
            }
        }

        return null;
    }

    StayRegion searchSubwayArea(LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayAreaMap, PreferenceRegion preferenceRegion)
    {
        if (subwayAreaMap == null || subwayAreaMap.size() == 0 || preferenceRegion == null)
        {
            return null;
        }

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
                        for (Area area : subwayAreaGroup.getAreaList())
                        {
                            if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                            {
                                return new StayRegion(subwayAreaGroup, area);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    StayRegion searchSubwayArea(LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayAreaMap, PreferenceRegion preferenceRegion)
    {
        if (subwayAreaMap == null || subwayAreaMap.size() == 0 || preferenceRegion == null)
        {
            return null;
        }

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
                        for (Area area : subwayAreaGroup.getAreaList())
                        {
                            if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                            {
                                return new StayRegion(subwayAreaGroup, area);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
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

        getViewInterface().setCategoryTab(tab.getPosition());

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
            String checkInDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

            startActivityForResult(StayAreaTabActivity.newInstance(getActivity()//
                , checkInDateTime, checkOutDateTime, DailyCategoryType.STAY_ALL//
                , mStayViewModel.selectedCategory.getValue().code), StayTabActivity.REQUEST_CODE_REGION);

            mAnalytics.onViewTypeClick(getActivity(), mStayViewModel.viewType.getValue());
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

        mAnalytics.onCalendarClick(getActivity());
    }

    @Override
    public void onFilterClick()
    {
        if (lock() == true)
        {
            return;
        }

        String checkInDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT);
        String checkOutDateTime = mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT);

        ArrayList<String> categoryList = new ArrayList();
        categoryList.add(mStayViewModel.selectedCategory.getValue().code);

        Location location;
        double radius;

        if (mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE)
        {
            location = mStayViewModel.location.getValue();
            radius = 10;
        } else
        {
            location = null;
            radius = 0;
        }

        startActivityForResult(StayFilterActivity.newInstance(getActivity(), checkInDateTime, checkOutDateTime//
            , mStayViewModel.viewType.getValue(), mStayViewModel.stayFilter.getValue(), mStayViewModel.stayRegion.getValue()//
            , categoryList, location, radius, null), StayTabActivity.REQUEST_CODE_FILTER);

        mAnalytics.onFilterClick(getActivity(), mStayViewModel.viewType.getValue());
    }

    @Override
    public void onViewTypeClick()
    {
        if (lock() == true)
        {
            return;
        }

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

        mAnalytics.onViewTypeClick(getActivity(), mStayViewModel.viewType.getValue());
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
            startActivityForResult(SearchActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
                , mStayViewModel.stayBookDateTime.getValue().getStayBookingDay().getCheckInDay(DailyCalendar.ISO_8601_FORMAT)//
                , mStayViewModel.stayBookDateTime.getValue().getStayBookingDay().getCheckOutDay(DailyCalendar.ISO_8601_FORMAT))//
                , StayTabActivity.REQUEST_CODE_SEARCH);

            mAnalytics.onSearchClick(getActivity(), mStayViewModel.viewType.getValue());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onCategoryFlicking(String categoryName)
    {

        mAnalytics.onCategoryFlicking(getActivity(), categoryName);
    }

    @Override
    public void onCategoryClick(String categoryName)
    {
        mAnalytics.onCategoryClick(getActivity(), categoryName);
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
                getViewInterface().setOptionFilterSelected(stayFilter != null && stayFilter.isDefaultFilter() == false);
            }
        });
    }

    private void startCalendar(String callByScreen)
    {
        try
        {
            Calendar calendar = DailyCalendar.getInstance();
            calendar.setTime(DailyCalendar.convertDate(mStayViewModel.commonDateTime.getValue().dailyDateTime, DailyCalendar.ISO_8601_FORMAT));

            String startDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            calendar.add(Calendar.DAY_OF_MONTH, DAYS_OF_MAXCOUNT - 1);

            String endDateTime = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);

            Intent intent = StayCalendarActivity.newInstance(getActivity()//
                , mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , startDateTime, endDateTime, NIGHTS_OF_MAXCOUNT, callByScreen, true//
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

        if (mStayViewModel.stayBookDateTime.getValue() == null)
        {
            mStayViewModel.stayBookDateTime.setValue(new StayBookDateTime());
        }

        try
        {
            mStayViewModel.stayBookDateTime.getValue().setCheckInDateTime(checkInDateTime);
            mStayViewModel.stayBookDateTime.getValue().setCheckOutDateTime(checkOutDateTime);

            mStayViewModel.stayBookDateTime.setValue(mStayViewModel.stayBookDateTime.getValue());
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

        if (mStayViewModel.stayBookDateTime.getValue() == null)
        {
            mStayViewModel.stayBookDateTime.setValue(new StayBookDateTime());
        }

        try
        {
            mStayViewModel.stayBookDateTime.getValue().setCheckInDateTime(checkInDateTime);
            mStayViewModel.stayBookDateTime.getValue().setCheckOutDateTime(checkInDateTime, afterDay);

            mStayViewModel.stayBookDateTime.setValue(mStayViewModel.stayBookDateTime.getValue());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    void notifyDateTextChanged()
    {
        if (mStayViewModel == null || mStayViewModel.stayBookDateTime.getValue() == null)
        {
            return;
        }

        String checkInDay = mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime("M.d(EEE)");
        String checkOutDay = mStayViewModel.stayBookDateTime.getValue().getCheckOutDateTime("M.d(EEE)");

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

    Pair<StayRegion, List<Category>> searchRegion(List<StayAreaGroup> areaGroupList, Pair<String, String> namePair)
    {
        if (areaGroupList == null || namePair == null)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.name.equalsIgnoreCase(namePair.first) == true)
            {
                if (areaGroup.getAreaCount() > 0)
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.name.equalsIgnoreCase(namePair.second) == true)
                        {
                            return new Pair(new StayRegion(areaGroup, area), area.getCategoryList());
                        }
                    }
                } else
                {
                    return new Pair(new StayRegion(areaGroup, areaGroup), areaGroup.getCategoryList());
                }
            }
        }

        return null;
    }

    Pair<StayRegion, List<Category>> searchRegion(List<StayAreaGroup> areaGroupList, int areaGroupIndex, int areaIndex)
    {
        if (areaGroupList == null || areaGroupIndex < 0)
        {
            return null;
        }

        for (StayAreaGroup areaGroup : areaGroupList)
        {
            if (areaGroup.index == areaGroupIndex)
            {
                if (areaIndex >= 0 && areaGroup.getAreaCount() > 0)
                {
                    for (StayArea area : areaGroup.getAreaList())
                    {
                        if (area.index == areaIndex)
                        {
                            return new Pair(new StayRegion(areaGroup, area), area.getCategoryList());
                        }
                    }
                } else
                {
                    return new Pair(new StayRegion(areaGroup, areaGroup), areaGroup.getCategoryList());
                }
            }
        }

        return null;
    }

    private void onCalendarActivityResult(int resultCode, Intent data)
    {
        switch (resultCode)
        {
            case Activity.RESULT_OK:

                if (data != null && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME) == true//
                    && data.hasExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME) == true)
                {
                    String checkInDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKIN_DATETIME);
                    String checkOutDateTime = data.getStringExtra(StayCalendarActivity.INTENT_EXTRA_DATA_CHECKOUT_DATETIME);

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

                    if (region == null || region.getAreaGroup() == null || region.getArea() == null)
                    {
                        return;
                    }

                    mStayViewModel.stayRegion.setValue(region);

                    // 지역이 수정 되면 필터가 초기화 된다.
                    mStayViewModel.stayFilter.setValue(mStayViewModel.stayFilter.getValue().resetFilter());
                    mStayViewModel.selectedCategory.setValue(Category.ALL);

                    setRefresh(true);

                    boolean changedAreaGroup = data.getBooleanExtra(StayAreaListActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, false);

                    if (changedAreaGroup == true)
                    {
                        mAnalytics.onRegionChanged(getActivity(), region.getAreaGroupName());
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
                try
                {
                    StaySuggest staySuggest = new StaySuggest(StaySuggest.MENU_TYPE_LOCATION, StaySuggest.CATEGORY_LOCATION, null);

                    startActivityForResult(StaySearchResultActivity.newInstance(getActivity()//
                        , mStayViewModel.commonDateTime.getValue().getTodayDateTime()//
                        , mStayViewModel.stayBookDateTime.getValue().getStayBookingDay()//
                        , null, staySuggest, null, AnalyticsManager.Screen.DAILYHOTEL_LIST_REGION_DOMESTIC)//
                        , StayTabActivity.REQUEST_CODE_SEARCH_RESULT);
                } catch (Exception e)
                {
                    ExLog.e(e.toString());
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
                        mStayViewModel.stayFilter.setValue(mStayViewModel.stayFilter.getValue().resetFilter());
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

    boolean processDeepLinkByDateTime(CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelListView() == false)
            {
                externalDeepLink.clear();
            }
        }

        return false;
    }

    List<Category> processDeepLinkByRegionList(List<StayAreaGroup> stayDistrictList, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return null;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelListView() == true)
            {
                return moveDeepLinkStayList(stayDistrictList, commonDateTime, externalDeepLink);
            } else
            {
                externalDeepLink.clear();
            }
        } else
        {

        }

        return null;
    }

    boolean processDeepLinkByRegionList(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        if (dailyDeepLink.isExternalDeepLink() == true)
        {
            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

            if (externalDeepLink.isHotelListView() == true)
            {
                return moveDeepLinkStayList(areaGroupMap, commonDateTime, externalDeepLink);
            } else
            {
                externalDeepLink.clear();
            }
        } else
        {

        }

        return null;
    }

    List<Category> moveDeepLinkStayList(List<StayAreaGroup> stayDistrictList, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (commonDateTime == null && dailyDeepLink == null)
        {
            return null;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                String categoryCode = externalDeepLink.getCategoryCode();
                StayFilter stayFilter = mStayViewModel.stayFilter.getValue() == null ? new StayFilter().resetFilter() : mStayViewModel.stayFilter.getValue();
                stayFilter.sortType = StayFilter.SortType.valueOf(externalDeepLink.getSorting().name());
                mStayViewModel.stayFilter.setValue(stayFilter);

                getViewInterface().setOptionFilterSelected(mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

                int provinceIndex;
                int areaIndex;

                try
                {
                    provinceIndex = Integer.parseInt(externalDeepLink.getProvinceIndex());
                } catch (Exception e)
                {
                    provinceIndex = -1;
                }

                try
                {
                    areaIndex = Integer.parseInt(externalDeepLink.getAreaIndex());
                } catch (Exception e)
                {
                    areaIndex = -1;
                }

                boolean isOverseas = externalDeepLink.getIsOverseas();

                // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
                Pair<StayRegion, List<Category>> pair = searchRegion(stayDistrictList, provinceIndex, areaIndex);

                if (pair == null)
                {
                    return null;
                }

                StayRegion stayRegion = pair.first;
                List<Category> categoryList = pair.second;

                if (stayRegion == null)
                {
                    return null;
                }

                mStayViewModel.stayRegion.setValue(stayRegion);
                notifyRegionTextChanged();

                // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
                if (DailyTextUtils.isTextEmpty(categoryCode) == false && stayRegion.getArea() != null && categoryList != null && categoryList.size() > 0)
                {
                    for (Category category : categoryList)
                    {
                        if (category.code.equalsIgnoreCase(categoryCode) == true)
                        {
                            mStayViewModel.selectedCategory.setValue(category);
                            break;
                        }
                    }
                }

                StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
                setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

                return categoryList;
            } else
            {

            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return null;
        } finally
        {
            dailyDeepLink.clear();
        }

        return null;
    }

    List<Category> moveDeepLinkStayList(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap, CommonDateTime commonDateTime, DailyDeepLink dailyDeepLink)
    {
        if (commonDateTime == null && dailyDeepLink == null)
        {
            return null;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                String categoryCode = externalDeepLink.getCategoryCode();
                StayFilter stayFilter = mStayViewModel.stayFilter.getValue() == null ? new StayFilter().resetFilter() : mStayViewModel.stayFilter.getValue();
                stayFilter.sortType = StayFilter.SortType.valueOf(externalDeepLink.getSorting().name());
                mStayViewModel.stayFilter.setValue(stayFilter);

                getViewInterface().setOptionFilterSelected(mStayViewModel.stayFilter.getValue().isDefaultFilter() == false);

                int stationIndex;

                try
                {
                    provinceIndex = Integer.parseInt(externalDeepLink.getProvinceIndex());
                } catch (Exception e)
                {
                    provinceIndex = -1;
                }

                try
                {
                    areaIndex = Integer.parseInt(externalDeepLink.getAreaIndex());
                } catch (Exception e)
                {
                    areaIndex = -1;
                }

                boolean isOverseas = externalDeepLink.getIsOverseas();

                // 지역이 있는 경우 지역을 디폴트로 잡아주어야 한다
                Pair<StayRegion, List<Category>> pair = searchRegion(areaGroupMap, provinceIndex, areaIndex);

                if (pair == null)
                {
                    return null;
                }

                StayRegion stayRegion = pair.first;

                if (stayRegion == null)
                {
                    return null;
                }

                mStayViewModel.stayRegion.setValue(stayRegion);
                notifyRegionTextChanged();

                // 카테고리가 있는 경우 카테고리를 디폴트로 잡아주어야 한다
                if (DailyTextUtils.isTextEmpty(categoryCode) == false && stayRegion.getArea() != null && categoryList != null && categoryList.size() > 0)
                {
                    for (Category category : categoryList)
                    {
                        if (category.code.equalsIgnoreCase(categoryCode) == true)
                        {
                            mStayViewModel.selectedCategory.setValue(category);
                            break;
                        }
                    }
                }

                StayBookDateTime stayBookDateTime = externalDeepLink.getStayBookDateTime(commonDateTime, externalDeepLink);
                setStayBookDateTime(stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT), stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));

                return categoryList;
            } else
            {

            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return null;
        } finally
        {
            dailyDeepLink.clear();
        }

        return null;
    }

    void parseDetailViewDeepLink(DailyExternalDeepLink dailyDeepLink)
    {
        Intent intent = StayDetailActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink());
        startActivityForResult(intent, StayTabActivity.REQUEST_CODE_DETAIL);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        mHasDeepLink = true;
    }
}
