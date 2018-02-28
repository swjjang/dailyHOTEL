package com.daily.dailyhotel.screen.common.area.stay.inbound;


import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.PreferenceRegion;
import com.daily.dailyhotel.entity.StayArea;
import com.daily.dailyhotel.entity.StayAreaGroup;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayRegion;
import com.daily.dailyhotel.entity.StaySubwayAreaGroup;
import com.daily.dailyhotel.parcel.StayRegionParcel;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAreaTabPresenter extends BaseExceptionPresenter<StayAreaTabActivity, StayAreaTabInterface.ViewInterface> implements StayAreaTabInterface.OnEventListener
{
    private StayAreaTabInterface.AnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    StayAreaViewModel mStayAreaViewModel;
    String mCategoryCode;
    DailyCategoryType mDailyCategoryType;
    StayRegion mSavedStayRegion; // 기존에 저장된 정보

    DailyLocationExFactory mDailyLocationExFactory;

    public StayAreaTabPresenter(@NonNull StayAreaTabActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayAreaTabInterface.ViewInterface createInstanceViewInterface()
    {
        return new StayAreaTabView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayAreaTabActivity activity)
    {
        setContentView(R.layout.activity_stay_area_list_data);

        setAnalytics(new StayAreaAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        initViewModel(activity);

        setRefresh(true);
    }

    private void initViewModel(BaseActivity activity)
    {
        if (activity == null)
        {
            return;
        }

        mStayAreaViewModel = ViewModelProviders.of(activity, new StayAreaViewModel.StayAreaViewModelFactory()).get(StayAreaViewModel.class);

    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayAreaTabInterface.AnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        try
        {
            String checkInDateTime = intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
            String checkOutDateTime = intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

            StayBookDateTime stayBookDateTime = new StayBookDateTime();
            stayBookDateTime.setCheckInDateTime(checkInDateTime);
            stayBookDateTime.setCheckOutDateTime(checkOutDateTime);

            mStayAreaViewModel.bookDateTime.setValue(stayBookDateTime);
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        // 카테고리로 넘어오는 경우
        mDailyCategoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_STAY_CATEGORY));

        // 이름으로 넘어오는 경우
        mCategoryCode = intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CATEGORY_CODE);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(mDailyCategoryType.getNameResId()));
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
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

        addCompositeDisposable(getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>>()
        {
            @Override
            public ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> apply(Boolean result) throws Exception
            {
                return mStayRemoteImpl.getRegionList(mDailyCategoryType);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>()
        {
            @Override
            public void accept(Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> pair) throws Exception
            {
                mStayAreaViewModel.areaList.setValue(pair.first);
                mStayAreaViewModel.subwayMap.setValue(pair.second);

                getViewInterface().setTabVisible(pair.first != null && pair.first.size() > 0 && pair.second != null && pair.second.size() > 0);

                mStayAreaViewModel.mPreviousArea.setValue(searchRegion(mDailyCategoryType, pair));

                unLockAll();
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

                    if (stayRegion != null)
                    {
                        getViewInterface().setSubwayAreaTabSelection();
                    }
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

    /**
     * first : District 이름
     * second : Town 이름
     *
     * @param dailyCategoryType
     * @return
     */
    PreferenceRegion getPreferenceRegion(DailyCategoryType dailyCategoryType)
    {
        return DailyPreference.getInstance(getActivity()).getDailyRegion(dailyCategoryType);
    }

    StayRegion searchArea(List<StayAreaGroup> areaGroupList, PreferenceRegion preferenceRegion)
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
                return new StayRegion(stayAreaGroup, stayAreaGroup);
            } else
            {
                for (StayArea area : stayAreaGroup.getAreaList())
                {
                    if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                    {
                        return new StayRegion(stayAreaGroup, area);
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

    private Observable<Location> searchMyLocation()
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            return null;
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
                {
                    @Override
                    public void onRequirePermission()
                    {
                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        observer.onError(new Exception());
                    }

                    @Override
                    public void onProviderEnabled()
                    {
                        mDailyLocationExFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
                        {
                            @Override
                            public void onFailed()
                            {
                                observer.onError(new Exception());
                            }

                            @Override
                            public void onAlreadyRun()
                            {
                                observer.onError(new DuplicateRunException());
                            }

                            @Override
                            public void onLocationChanged(Location location)
                            {
                                unLockAll();

                                mDailyLocationExFactory.stopLocationMeasure();

                                if (location == null)
                                {
                                    observer.onError(new NullPointerException());
                                } else
                                {
                                    observer.onNext(location);
                                    observer.onComplete();
                                }
                            }

                            @Override
                            public void onCheckSetting(ResolvableApiException exception)
                            {
                                observer.onError(exception);
                            }
                        });
                    }

                    @Override
                    public void onProviderDisabled()
                    {
                        observer.onError(new ProviderException());
                    }
                });
            }
        }.doOnError(throwable ->
        {
            unLockAll();

            if (throwable instanceof PermissionException)
            {
                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, StayAreaTabActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, StayAreaTabActivity.REQUEST_CODE_SETTING_LOCATION);
                    }
                };

                View.OnClickListener negativeListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                    }
                };

                getViewInterface().showSimpleDialog(//
                    getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                    getString(R.string.dialog_btn_text_dosetting), //
                    getString(R.string.dialog_btn_text_cancel), //
                    positiveListener, negativeListener, cancelListener, null, true);
            } else if (throwable instanceof DuplicateRunException)
            {

            } else if (throwable instanceof ResolvableApiException)
            {
                try
                {
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), StayAreaTabActivity.REQUEST_CODE_SETTING_LOCATION);
                } catch (Exception e)
                {
                    getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            } else
            {
                getViewInterface().showToast(R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onAreaTabClick()
    {

    }

    @Override
    public void onSubwayTabClick()
    {

    }

    @Override
    public void onAroundSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);

        Observable observable = searchMyLocation();

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe(new Consumer<Location>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                {
                    unLockAll();

                    setResult(BaseActivity.RESULT_CODE_START_AROUND_SEARCH, mDailyCategoryType, null, null);
                    finish();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        } else
        {
            unLockAll();
        }

        mAnalytics.onEventAroundSearchClick(getActivity(), mDailyCategoryType);
    }

    @Override
    public void onAreaClick(StayAreaGroup areaGroup, StayArea area)
    {
        if (areaGroup == null || area == null)
        {
            finish();
            return;
        }

        final String areaGroupName = areaGroup.name;
        final String areaName = area.name;

        // 지역이 변경된 경우 팝업을 뛰어서 날짜 변경을 할것인지 물어본다.
        if (equalsAreaGroupName(mStayAreaViewModel.mPreviousArea.getValue(), areaGroupName) == true)
        {
            setResult(Activity.RESULT_OK, mDailyCategoryType, areaGroup, area);
            finish();
        } else
        {
            String message = mStayAreaViewModel.bookDateTime.getValue().getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayAreaViewModel.bookDateTime.getValue().getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);
            final String previousAreaGroupName, previousAreaName;

            if (mStayAreaViewModel.mPreviousArea.getValue() != null)
            {
                previousAreaGroupName = mStayAreaViewModel.mPreviousArea.getValue().getAreaGroupName();
                previousAreaName = mStayAreaViewModel.mPreviousArea.getValue().getAreaName();
            } else
            {
                previousAreaGroupName = null;
                previousAreaName = null;
            }

            getViewInterface().showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedDistrictClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());

                    setResult(Activity.RESULT_OK, mDailyCategoryType, areaGroup, area);
                    finish();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedDistrictClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());
                    mAnalytics.onEventChangedDateClick(getActivity());

                    // 날짜 선택 화면으로 이동한다.
                    setResult(BaseActivity.RESULT_CODE_START_CALENDAR, mDailyCategoryType, areaGroup, area);
                    finish();
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    unLockAll();
                }
            }, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLockAll();
                }
            }, true);
        }

        mAnalytics.onEventTownClick(getActivity(), areaGroupName, areaName);
    }

    private boolean equalsAreaGroupName(StayRegion stayRegion, String areaName)
    {
        if (stayRegion == null || DailyTextUtils.isTextEmpty(areaName) == true)
        {
            return false;
        }

        return areaName.equalsIgnoreCase(stayRegion.getAreaGroupName());
    }

    void setResult(int resultCode, DailyCategoryType categoryType, StayAreaGroup areaGroup, StayArea area)
    {
        if (categoryType != null && areaGroup != null && area != null)
        {
            setPreferenceArea(categoryType, areaGroup.name, area.name);
        }

        setResult(resultCode, categoryType, areaGroup, area, new StayRegion(areaGroup, area));
    }

    void setResult(int resultCode, DailyCategoryType categoryType, Area regionArea, StaySubwayAreaGroup areaGroup, Area area)
    {
        if (categoryType != null && regionArea != null && areaGroup != null && area != null)
        {
            setPreferenceSubwayArea(categoryType, regionArea.name, areaGroup.name, area.name);
        }

        setResult(resultCode, categoryType, areaGroup, area, new StayRegion(areaGroup, area));
    }

    void setResult(int resultCode, DailyCategoryType categoryType, Area areaGroup, Area area, StayRegion stayRegion)
    {
        if (categoryType == null)
        {
            return;
        }

        Intent intent = new Intent();

        if (stayRegion != null)
        {
            intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_REGION, new StayRegionParcel(stayRegion));
        }

        intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_STAY_CATEGORY, categoryType.name());

        if (areaGroup != null)
        {
            if (mSavedStayRegion == null)
            {
                intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, true);
            } else
            {
                if (areaGroup.name.equalsIgnoreCase(mSavedStayRegion.getAreaGroupName()) == true)
                {
                    intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, false);
                } else
                {
                    intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, true);
                }
            }
        } else
        {
            intent.putExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CHANGED_AREA_GROUP, true);
        }

        setResult(resultCode, intent);
    }

    private void setPreferenceArea(DailyCategoryType dailyCategoryType, String areaGroupName, String areaName)
    {
        if (dailyCategoryType == null)
        {
            return;
        }

        PreferenceRegion preferenceRegion = new PreferenceRegion(PreferenceRegion.AreaType.AREA);
        preferenceRegion.regionName = preferenceRegion.areaGroupName = areaGroupName;
        preferenceRegion.areaName = areaName;
        preferenceRegion.overseas = false;

        DailyPreference.getInstance(getActivity()).setDailyRegion(dailyCategoryType, preferenceRegion);
    }

    private void setPreferenceSubwayArea(DailyCategoryType dailyCategoryType, String regionName, String areaGroupName, String areaName)
    {
        if (dailyCategoryType == null)
        {
            return;
        }

        PreferenceRegion preferenceRegion = new PreferenceRegion(PreferenceRegion.AreaType.SUBWAY_AREA);
        preferenceRegion.regionName = regionName;
        preferenceRegion.areaGroupName = areaGroupName;
        preferenceRegion.areaName = areaName;
        preferenceRegion.overseas = false;

        DailyPreference.getInstance(getActivity()).setDailyRegion(dailyCategoryType, preferenceRegion);
    }
}
