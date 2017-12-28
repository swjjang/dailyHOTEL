package com.daily.dailyhotel.screen.home.stay.inbound.list;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseFragmentExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Category;
import com.daily.dailyhotel.entity.ObjectItem;
import com.daily.dailyhotel.entity.Stay;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayFilter;
import com.daily.dailyhotel.entity.Stays;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayListFragmentPresenter extends BaseFragmentExceptionPresenter<StayListFragment, StayListFragmentInterface> implements StayListFragmentView.OnEventListener
{
    static final int MAXIMUM_NUMBER_PER_PAGE = Constants.PAGENATION_LIST_SIZE;
    static final int PAGE_NONE = -1;
    static final int PAGE_FINISH = Integer.MAX_VALUE;

    private StayListFragmentAnalyticsInterface mAnalytics;

    StayRemoteImpl mStayRemoteImpl;

    StayTabPresenter.StayViewModel mStayViewModel;

    Category mCategory;
    int mPage = PAGE_NONE;
    boolean mMoreRefreshing;

    public interface StayListFragmentAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public interface OnStayListFragmentListener
    {
        // 왜 onActivityCreated 했을까?
        // http://blog.saltfactory.net/android/implement-layout-using-with-fragment.html
        void onActivityCreated(StayListFragmentPresenter stayListFragment);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onShowMenuBar();

        void onBottomOptionVisible(boolean visible);

        void onUpdateFilterEnabled(boolean isShowFilterEnabled);

        void onUpdateViewTypeEnabled(boolean isShowViewTypeEnabled);

        void onFilterClick();

        void onShowActivityEmptyView(boolean isShow);

        void onSearchCountUpdate(int searchCount, int searchMaxCount);

        void onStayClick(View view, ObjectItem objectItem, int listCount);

        void onStayLongClick(View view, ObjectItem objectItem, int listCount);

        void onRegionClick();

        void onCalendarClick();

        void onRecordAnalytics(Constants.ViewType viewType);
    }

    public StayListFragmentPresenter(@NonNull StayListFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        if (bundle != null)
        {
            String name = bundle.getString("name");
            String code = bundle.getString("code");

            mCategory = new Category(code, name);
        }

        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_list_data, container);
    }

    @NonNull
    @Override
    protected StayListFragmentInterface createInstanceViewInterface()
    {
        return new StayListFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StayListFragmentAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(false);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayListFragmentAnalyticsInterface) analytics;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

    }

    @Override
    public void notifyRefresh(boolean force)
    {
        if (force == true || (isCurrentFragment() == true && mPage == PAGE_NONE))
        {
            mPage = 1;

            setRefresh(true);

            // Activity 가 아직 생성되지 않은 경우가 있다.
            if (getActivity() != null)
            {
                onRefresh(true);
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onBackClick()
    {

    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false || mStayViewModel == null || mPage == PAGE_FINISH)
        {
            setRefresh(false);
            return;
        }

        if (mStayViewModel.selectedCategory.getValue() == null//
            || mStayViewModel.stayFilter.getValue() == null//
            || mStayViewModel.viewType.getValue() == null//
            || mStayViewModel.stayRegion.getValue() == null//
            || mStayViewModel.stayBookDateTime.getValue() == null//
            || mStayViewModel.commonDateTime.getValue() == null)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);

        // 더보기 시에는 화면 락을 걸지 않음.
        if (mPage <= 1)
        {
            screenLock(showProgress);
        }

        addCompositeDisposable(mStayRemoteImpl.getList(getQueryMap(mPage), DailyRemoteConfigPreference.getInstance(getActivity()).getKeyRemoteConfigStayRankTestType()).map(new Function<Stays, Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public Pair<Boolean, List<ObjectItem>> apply(Stays stays) throws Exception
            {
                DailyRemoteConfigPreference.getInstance(getActivity()).setKeyRemoteConfigRewardStickerEnabled(stays.activeReward);

                return new Pair<>(stays.activeReward, makeObjectItemList(stays.getStayList(), mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DEFAULT));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<Boolean, List<ObjectItem>>>()
        {
            @Override
            public void accept(Pair<Boolean, List<ObjectItem>> pair) throws Exception
            {
                int listSize = pair.second.size();

                if (listSize < MAXIMUM_NUMBER_PER_PAGE)
                {
                    mPage = PAGE_FINISH;

                    pair.second.add(new ObjectItem(ObjectItem.TYPE_FOOTER_VIEW, null));
                } else
                {
                    pair.second.add(new ObjectItem(ObjectItem.TYPE_LOADING_VIEW, null));
                }

                if (mPage > 1)
                {
                    getViewInterface().addList(pair.second, mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE//
                        , mStayViewModel.stayBookDateTime.getValue().getNights() > 1, pair.first,//
                        DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);
                } else
                {
                    getViewInterface().setList(pair.second, mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE//
                        , mStayViewModel.stayBookDateTime.getValue().getNights() > 1, pair.first//
                        , DailyPreference.getInstance(getActivity()).getTrueVRSupport() > 0);
                }

                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                mMoreRefreshing = false;
                getViewInterface().setSwipeRefreshing(false);

                onHandleError(throwable);
            }
        }));
    }

    @Override
    public void onSwipeRefreshing()
    {
        if (lock() == true)
        {
            return;
        }

        clearCompositeDisposable();

        mPage = 1;

        setRefresh(true);
        onRefresh(false);
    }

    @Override
    public void onMoreRefreshing()
    {
        if (mPage == PAGE_FINISH || mMoreRefreshing == true)
        {
            return;
        }

        mMoreRefreshing = true;

        mPage++;

        setRefresh(true);
        onRefresh(false);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayViewModel = ViewModelProviders.of(activity).get(StayTabPresenter.StayViewModel.class);


    }

    boolean isCurrentFragment()
    {
        return (mStayViewModel.selectedCategory.getValue() != null && mCategory != null//
            && mStayViewModel.selectedCategory.getValue().code.equalsIgnoreCase(mCategory.code) == true);
    }

    /**
     * @param stayList
     * @param hasSection
     * @return
     */
    List<ObjectItem> makeObjectItemList(List<Stay> stayList, boolean hasSection)
    {
        List<ObjectItem> objectItemList = new ArrayList<>();

        if (stayList == null || stayList.size() == 0)
        {
            return objectItemList;
        }

        // 초이스가 없는 경우 섹션이 필요없다.
        if (hasSection == true && stayList.get(0).dailyChoice == true)
        {
            boolean addAllSection = false;
            boolean addDailyChoiceSection = false;
            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                if (stay.dailyChoice == true)
                {
                    if (addDailyChoiceSection == false)
                    {
                        addDailyChoiceSection = true;
                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, getString(R.string.label_dailychoice)));
                    }
                } else
                {
                    if (addAllSection == false)
                    {
                        addAllSection = true;
                        objectItemList.add(new ObjectItem(ObjectItem.TYPE_SECTION, getString(R.string.label_all)));
                    }
                }

                stay.entryPosition = ++entryPosition;
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
            }
        } else
        {
            int entryPosition = 0;

            for (Stay stay : stayList)
            {
                stay.entryPosition = ++entryPosition;
                objectItemList.add(new ObjectItem(ObjectItem.TYPE_ENTRY, stay));
            }
        }

        return objectItemList;
    }

    Map<String, Object> getQueryMap(int page)
    {
        Map<String, Object> queryMap = new HashMap<>();

        if (mStayViewModel.stayBookDateTime.getValue() != null)
        {
            // dateCheckIn
            queryMap.put("dateCheckIn", mStayViewModel.stayBookDateTime.getValue().getCheckInDateTime("yyyy-MM-dd"));

            // stays
            queryMap.put("stays", mStayViewModel.stayBookDateTime.getValue().getNights());
        }

        if (mStayViewModel.stayRegion.getValue() != null)
        {
            // provinceIdx
            Area areaGroup = mStayViewModel.stayRegion.getValue().getAreaGroup();
            if (areaGroup != null)
            {
                queryMap.put("provinceIdx", mStayViewModel.stayRegion.getValue().getAreaGroup().index);
            }

            Area area = mStayViewModel.stayRegion.getValue().getArea();
            if (area != null && area.index != StayArea.ALL)
            {
                // areaIdx
                queryMap.put("areaIdx", mStayViewModel.stayRegion.getValue().getArea().index);
            }
        }

        if (mCategory != null && Category.ALL.code.equalsIgnoreCase(mCategory.code) == false)
        {
            queryMap.put("category", mCategory.code);
        }

        if (mStayViewModel.stayFilter.getValue() != null)
        {
            // persons
            queryMap.put("persons", mStayViewModel.stayFilter.getValue().person);

            // bedType [Double, Twin, Ondol, Etc]
            List<String> flagBedTypeFilters = mStayViewModel.stayFilter.getValue().getBedTypeList();

            if (flagBedTypeFilters != null && flagBedTypeFilters.size() > 0)
            {
                queryMap.put("bedType", flagBedTypeFilters);
            }

            // luxury [Breakfast, Cooking, Bath, Parking, Pool, Finess, WiFi, NoParking, Pet, ShareBbq, KidsPlayRoom
            // , Sauna, BusinessCenter, Tv, Pc, SpaWallPool, Karaoke, PartyRoom, PrivateBbq
            List<String> luxuryFilterList = new ArrayList<>();
            List<String> amenitiesFilterList = mStayViewModel.stayFilter.getValue().getAmenitiesFilter();
            List<String> roomAmenitiesFilterList = mStayViewModel.stayFilter.getValue().getRoomAmenitiesFilterList();

            if (amenitiesFilterList != null && amenitiesFilterList.size() > 0)
            {
                luxuryFilterList.addAll(amenitiesFilterList);
            }

            if (roomAmenitiesFilterList != null && roomAmenitiesFilterList.size() > 0)
            {
                luxuryFilterList.addAll(roomAmenitiesFilterList);
            }

            if (luxuryFilterList.size() > 0)
            {
                queryMap.put("luxury", luxuryFilterList);
            }

            // sortProperty
            // sortDirection
            switch (mStayViewModel.stayFilter.getValue().sortType)
            {
                case DEFAULT:
                    break;

                case DISTANCE:
                    queryMap.put("sortProperty", "Distance");
                    queryMap.put("sortDirection", "Asc");
                    break;

                case LOW_PRICE:
                    queryMap.put("sortProperty", "Price");
                    queryMap.put("sortDirection", "Asc");
                    break;

                case HIGH_PRICE:
                    queryMap.put("sortProperty", "Price");
                    queryMap.put("sortDirection", "Desc");
                    break;

                case SATISFACTION:
                    queryMap.put("sortProperty", "Rating");
                    queryMap.put("sortDirection", "Desc");
                    break;
            }

            // longitude
            // latitude
            // radius
            if (mStayViewModel.stayFilter.getValue().sortType == StayFilter.SortType.DISTANCE && mStayViewModel.location.getValue() != null)
            {
                queryMap.put("latitude", mStayViewModel.location.getValue().getLatitude());
                queryMap.put("longitude", mStayViewModel.location.getValue().getLongitude());
            }
        }

        // page
        // limit
        // 페이지 번호. 0 보다 큰 정수. 값을 입력하지 않으면 페이지네이션을 적용하지 않고 전체 데이터를 반환함
        if (page > 0)
        {
            queryMap.put("page", page);
            queryMap.put("limit", MAXIMUM_NUMBER_PER_PAGE);
        }

        // details
        queryMap.put("details", true);

        return queryMap;
    }
}
