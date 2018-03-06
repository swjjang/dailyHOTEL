package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BasePagerFragmentPresenter;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayRegion;
import com.twoheart.dailyhotel.R;

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
public class StayAreaFragmentPresenter extends BasePagerFragmentPresenter<StayAreaFragment, StayAreaFragmentInterface.ViewInterface>//
    implements StayAreaFragmentInterface.OnEventListener
{
    private StayAreaFragmentInterface.AnalyticsInterface mAnalytics;

    StayAreaViewModel mStayAreaViewModel;
    int mAreaGroupPosition = -1;

    public StayAreaFragmentPresenter(@NonNull StayAreaFragment fragment)
    {
        super(fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Bundle bundle = getFragment().getArguments();

        return getViewInterface().getContentView(inflater, R.layout.fragment_stay_area_list_data, container);
    }

    @NonNull
    @Override
    protected StayAreaFragmentInterface.ViewInterface createInstanceViewInterface()
    {
        return new StayAreaFragmentView(this);
    }

    @Override
    public void constructorInitialize(BaseActivity activity)
    {
        setAnalytics(new StayAreaFragmentAnalyticsImpl());

        initViewModel(activity);

        setRefresh(false);

        switch (mStayAreaViewModel.categoryType.getValue())
        {
            case STAY_HOTEL:
            case STAY_BOUTIQUE:
            case STAY_PENSION:
            case STAY_RESORT:
                getViewInterface().setLocationText(getString(R.string.label_view_my_around_daily_category_format, getString(mStayAreaViewModel.categoryType.getValue().getNameResId())));
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

        mStayAreaViewModel.areaList.observe(activity, new Observer<List<StayAreaGroup>>()
        {
            @Override
            public void onChanged(@Nullable List<StayAreaGroup> areaGroupList)
            {
                getViewInterface().setAreaGroup(areaGroupList);
            }
        });

        mStayAreaViewModel.previousArea.observe(activity, new Observer<StayRegion>()
        {
            @Override
            public void onChanged(@Nullable StayRegion stayRegion)
            {
                if (stayRegion.getAreaType() == PreferenceRegion.AreaType.AREA)
                {
                    int groupPosition = getAreaGroupPosition(mStayAreaViewModel.areaList.getValue(), (StayAreaGroup) stayRegion.getAreaGroup());

                    if (groupPosition >= 0)
                    {
                        mAreaGroupPosition = groupPosition;
                        getViewInterface().setSelectedAreaGroup(groupPosition);
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

    int getAreaGroupPosition(List<StayAreaGroup> areaGroupList, StayAreaGroup areaGroup)
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
        mAnalytics = (StayAreaFragmentInterface.AnalyticsInterface) analytics;
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
        if (mStayAreaViewModel.areaList == null || mStayAreaViewModel.areaList.getValue().size() == 0 || groupPosition < 0 || lock() == true)
        {
            return;
        }

        // 하위 지역이 없으면 선택
        if (mStayAreaViewModel.areaList.getValue().get(groupPosition).getAreaCount() == 0)
        {
            StayArea stayArea = new StayArea(StayArea.ALL, mStayAreaViewModel.areaList.getValue().get(groupPosition).name);
            stayArea.setCategoryList(mStayAreaViewModel.areaList.getValue().get(groupPosition).getCategoryList());
            onAreaClick(groupPosition, stayArea);

            unLockAll();
        } else
        {
            if (mAreaGroupPosition == groupPosition)
            {
                addCompositeDisposable(collapseGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        mAreaGroupPosition = -1;

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();

                        ExLog.e(throwable.toString());
                    }
                }));
            } else
            {
                addCompositeDisposable(collapseGroupWithAnimation(mAreaGroupPosition, false).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Boolean>>()
                {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception
                    {
                        return expandGroupWithAnimation(groupPosition, true);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).delaySubscription(200, TimeUnit.MILLISECONDS).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        mAreaGroupPosition = groupPosition;

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();

                        ExLog.e(throwable.toString());
                    }
                }));
            }
        }
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

    @Override
    public void onAreaClick(int groupPosition, StayArea area)
    {
        if (groupPosition < 0 || area == null)
        {
            return;
        }

        StayAreaGroup areaGroup = mStayAreaViewModel.areaList.getValue().get(groupPosition);

        if (areaGroup == null)
        {
            return;
        }

        final String areaGroupName = areaGroup.name;
        final String areaName = area.name;

        getFragment().getFragmentEventListener().onAreaClick(areaGroup, area);
    }
}
