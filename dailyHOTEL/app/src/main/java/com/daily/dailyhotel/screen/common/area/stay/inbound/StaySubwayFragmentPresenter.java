package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.twoheart.dailyhotel.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StaySubwayFragmentPresenter extends BasePagerFragmentPresenter<StaySubwayFragment, StaySubwayFragmentInterface.ViewInterface>//
    implements StaySubwayFragmentInterface.OnEventListener
{
    private StaySubwayFragmentInterface.AnalyticsInterface mAnalytics;

    StayAreaViewModel mStayAreaViewModel;
    int mAreaGroupPosition = -1;
    Area mCurrentRegion;
    Pair<Integer, StaySubwayAreaGroup> mLastSelectedSubwayAreaGroup;

    public StaySubwayFragmentPresenter(@NonNull StaySubwayFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_subway_list_data, container);
    }

    @NonNull
    @Override
    protected StaySubwayFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new StaySubwayFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StaySubwayFragmentAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);

        switch (mStayAreaViewModel.categoryType)
        {
            case STAY_HOTEL:
            case STAY_BOUTIQUE:
            case STAY_PENSION:
            case STAY_RESORT:
                getViewInterface().setLocationText(getString(R.string.label_view_my_around_daily_category_format, getString(mStayAreaViewModel.categoryType.getNameResId())));
                break;

            default:
                getViewInterface().setLocationText(getString(R.string.label_region_around_stay));
                break;
        }
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayAreaViewModel = ViewModelProviders.of(activity).get(StayAreaViewModel.class);

        mStayAreaViewModel.subwayMap.observe(activity, new Observer<LinkedHashMap<Area, List<StaySubwayAreaGroup>>>()
        {
            @Override
            public void onChanged(@Nullable LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap)
            {
                List<Area> tabList = new ArrayList<>();
                Iterator<Area> iterator = areaGroupMap.keySet().iterator();

                while (iterator.hasNext() == true)
                {
                    tabList.add(iterator.next());
                }

                getViewInterface().setTab(tabList);
                getViewInterface().setTabSelected(0);

                mCurrentRegion = tabList.get(0);

                getViewInterface().setAreaGroup(areaGroupMap.get(tabList.get(0)));
            }
        });

        mStayAreaViewModel.previousArea.observe(activity, new Observer<StayRegion>()
        {
            @Override
            public void onChanged(@Nullable StayRegion stayRegion)
            {
                if (stayRegion.getAreaType() == PreferenceRegion.AreaType.SUBWAY_AREA)
                {
                    StaySubwayAreaGroup subwayAreaGroup = (StaySubwayAreaGroup) stayRegion.getAreaGroup();
                    Pair<Area, Integer> region = getRegion(mStayAreaViewModel.subwayMap.getValue(), subwayAreaGroup);

                    if (region != null)
                    {
                        List<StaySubwayAreaGroup> subwayAreaGroupList = mStayAreaViewModel.subwayMap.getValue().get(region.first);
                        int groupPosition = getAreaGroupPosition(subwayAreaGroupList, subwayAreaGroup);

                        if (groupPosition >= 0)
                        {
                            mCurrentRegion = region.first;
                            mAreaGroupPosition = groupPosition;

                            getViewInterface().setTabSelected(region.second);
                            getViewInterface().setAreaGroup(subwayAreaGroupList);
                            getViewInterface().setAreaGroupSelected(groupPosition);

                            mLastSelectedSubwayAreaGroup = new Pair(groupPosition, subwayAreaGroup);
                        }
                    }
                }

                mStayAreaViewModel.previousArea.removeObserver(this);
            }
        });

        mStayAreaViewModel.isAgreeTermsOfLocation.observe(activity, new Observer<Boolean>()
        {
            @Override
            public void onChanged(@Nullable Boolean isAgree)
            {
                getViewInterface().setLocationTermVisible(isAgree);
            }
        });
    }

    Pair<Area, Integer> getRegion(LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap, StaySubwayAreaGroup areaGroup)
    {
        if (areaGroupMap == null || areaGroup == null)
        {
            return null;
        }

        Area areaGroupRegion = areaGroup.getRegion();

        if (areaGroupRegion == null)
        {
            return null;
        }

        Iterator<Area> iterator = areaGroupMap.keySet().iterator();
        int tabPosition = 0;

        while (iterator.hasNext() == true)
        {
            Area region = iterator.next();

            if (region.name.equalsIgnoreCase(areaGroupRegion.name) == true)
            {
                return new Pair(region, tabPosition);
            }

            tabPosition++;
        }

        return null;
    }

    int getAreaGroupPosition(List<StaySubwayAreaGroup> areaGroupList, StaySubwayAreaGroup areaGroup)
    {
        if (areaGroupList == null || areaGroup == null)
        {
            return -1;
        }

        int size = areaGroupList.size();

        for (int i = 0; i < size; i++)
        {
            if (areaGroupList.get(i).name.equalsIgnoreCase(areaGroup.name) == true)
            {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StaySubwayFragmentInterface.AnalyticsInterface) analytics;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
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
    public void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public void onBackClick()
    {
        // 사용하지 않음.
    }

    @Override
    public void onSelected()
    {
    }

    @Override
    public void onUnselected()
    {
    }

    @Override
    public void onRefresh()
    {
    }

    @Override
    public void scrollTop()
    {
    }

    @Override
    public boolean onBackPressed()
    {
        return false;
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            setRefresh(false);
            return;
        }

        setRefresh(false);
        screenLock(showProgress);


    }

    @Override
    public void onAroundSearchClick()
    {
        getFragment().getFragmentEventListener().onAroundSearchClick();
    }

    @Override
    public void onAreaGroupClick(int groupPosition)
    {
        if (mStayAreaViewModel.subwayMap == null || groupPosition < 0 || lock() == true)
        {
            return;
        }

        if (mAreaGroupPosition == groupPosition)
        {
            addCompositeDisposable(collapseGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    mAreaGroupPosition = -1;
                    mLastSelectedSubwayAreaGroup = null;

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        } else
        {
            addCompositeDisposable(collapseGroupWithAnimation(mAreaGroupPosition, false).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Boolean>>()
            {
                @Override
                public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception
                {
                    return expandGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread());
                }
            }).observeOn(AndroidSchedulers.mainThread()).delaySubscription(200, TimeUnit.MILLISECONDS).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(Boolean aBoolean) throws Exception
                {
                    mAreaGroupPosition = groupPosition;
                    mLastSelectedSubwayAreaGroup = new Pair(groupPosition, getAreaGroup(groupPosition));

                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        }

        mAnalytics.onEventAreaGroupClick(getActivity(), mCurrentRegion.name, mStayAreaViewModel.subwayMap.getValue().get(mCurrentRegion).get(groupPosition).name);
    }

    @Override
    public void onAreaClick(int groupPosition, Area area)
    {
        StaySubwayAreaGroup areaGroup = getAreaGroup(groupPosition);

        if (areaGroup == null || area == null)
        {
            return;
        }

        getFragment().getFragmentEventListener().onSubwayAreaClick(areaGroup, area);

        mAnalytics.onEventAreaClick(getActivity(), mCurrentRegion.name, areaGroup.name, area.name);
    }

    private StaySubwayAreaGroup getAreaGroup(int position)
    {
        return mStayAreaViewModel.subwayMap.getValue().get(mCurrentRegion).get(position);
    }

    @Override
    public void onTabChanged(int position, Object tag)
    {
        if (lock() == true)
        {
            return;
        }

        mCurrentRegion = (Area) tag;

        Observable<Boolean> observable;

        if (mAreaGroupPosition >= 0)
        {
            observable = collapseGroupWithAnimation(mAreaGroupPosition, false).subscribeOn(AndroidSchedulers.mainThread());
        } else
        {
            observable = Observable.just(true);
        }

        addCompositeDisposable(observable.subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            private boolean equalsRegion(Area region, StaySubwayAreaGroup lastAreaGroup)
            {
                if (region == null || lastAreaGroup == null)
                {
                    return false;
                }

                Area lastRegion = lastAreaGroup.getRegion();

                if (lastRegion == null)
                {
                    return false;
                }

                if (region.index == lastRegion.index)
                {
                    return true;
                }

                return false;
            }

            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                mAreaGroupPosition = -1;

                getViewInterface().setTabSelected(position);
                getViewInterface().setAreaGroup(mStayAreaViewModel.subwayMap.getValue().get(tag));

                if (mLastSelectedSubwayAreaGroup != null && equalsRegion(mCurrentRegion, mLastSelectedSubwayAreaGroup.second) == true)
                {
                    if (mLastSelectedSubwayAreaGroup.first != null)
                    {
                        getViewInterface().setAreaGroupSelected(mLastSelectedSubwayAreaGroup.first);
                    }
                }

                unLockAll();

                mAnalytics.onEventRegionClick(getActivity(), mCurrentRegion.name);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation)
    {
        Observable<Boolean> observable = getViewInterface().collapseGroupWithAnimation(groupPosition, animation);

        if (observable == null)
        {
            observable = Observable.just(true);
        }

        return observable;
    }

    Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        Observable<Boolean> observable = getViewInterface().expandGroupWithAnimation(groupPosition, animation);

        if (observable == null)
        {
            observable = Observable.just(true);
        }

        return observable;
    }
}
