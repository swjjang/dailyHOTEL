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
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;

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

            mStayAreaViewModel.bookDateTime.setValue(new StayBookDateTime(checkInDateTime, checkOutDateTime));
        } catch (Exception e)
        {
            ExLog.e(e.toString());

            return false;
        }

        // 카테고리로 넘어오는 경우
        DailyCategoryType categoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_STAY_CATEGORY));
        mStayAreaViewModel.categoryType.setValue(categoryType == null ? DailyCategoryType.STAY_ALL : categoryType);

        // 이름으로 넘어오는 경우
        mCategoryCode = intent.getStringExtra(StayAreaTabActivity.INTENT_EXTRA_DATA_CATEGORY_CODE);

        mStayAreaViewModel.isAgreeTermsOfLocation.setValue(DailyPreference.getInstance(getActivity()).isAgreeTermsOfLocation() == false);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        switch (mStayAreaViewModel.categoryType.getValue())
        {
            case STAY_HOTEL:
            case STAY_BOUTIQUE:
            case STAY_PENSION:
            case STAY_RESORT:
                getViewInterface().setToolbarTitle(getString(R.string.label_select_area_daily_category_format, getString(mStayAreaViewModel.categoryType.getValue().getNameResId())));
                break;

            default:
                getViewInterface().setToolbarTitle(getString(R.string.label_selectarea_stay_area));
                break;
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

        if (mStayAreaViewModel.categoryType.getValue() == DailyCategoryType.STAY_ALL)
        {
            mAnalytics.onScreen(getActivity(), mCategoryCode);
        } else
        {
            mAnalytics.onScreen(getActivity(), getString(mStayAreaViewModel.categoryType.getValue().getCodeResId()));
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
        mAnalytics.onEventClosedClick(getActivity(), getString(mStayAreaViewModel.categoryType.getValue().getCodeResId()));

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

        switch (requestCode)
        {
            case StayAreaTabActivity.REQUEST_CODE_PERMISSION_MANAGER:
                mStayAreaViewModel.isAgreeTermsOfLocation.setValue(false);

                if (resultCode == Activity.RESULT_OK)
                {
                    onAroundSearchClick();
                }
                break;

            case StayAreaTabActivity.REQUEST_CODE_SEARCH:
                break;

            case StayAreaTabActivity.REQUEST_CODE_SETTING_LOCATION:
                mStayAreaViewModel.isAgreeTermsOfLocation.setValue(false);

                onAroundSearchClick();
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

        addCompositeDisposable(getViewInterface().getCompleteCreatedFragment().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>>()
        {
            @Override
            public ObservableSource<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>> apply(Boolean result) throws Exception
            {
                return mStayRemoteImpl.getRegionList(mStayAreaViewModel.categoryType.getValue());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>>>()
        {
            @Override
            public void accept(Pair<List<StayAreaGroup>, LinkedHashMap<Area, List<StaySubwayAreaGroup>>> pair) throws Exception
            {
                mStayAreaViewModel.areaList.setValue(pair.first);
                mStayAreaViewModel.subwayMap.setValue(pair.second);

                getViewInterface().setTabVisible(pair.first != null && pair.first.size() > 0 && pair.second != null && pair.second.size() > 0);

                mStayAreaViewModel.previousArea.setValue(getRegionByPreferenceRegion(mStayAreaViewModel.categoryType.getValue(), pair.first, pair.second));

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

    private StayRegion getRegionByPreferenceRegion(DailyCategoryType categoryType, List<StayAreaGroup> areaGroupList, LinkedHashMap<Area, List<StaySubwayAreaGroup>> areaGroupMap)
    {
        StayRegion stayRegion = null;
        PreferenceRegion preferenceRegion = getPreferenceRegion(categoryType);

        if (preferenceRegion != null)
        {
            switch (preferenceRegion.areaType)
            {
                case AREA:
                    stayRegion = getRegionByPreferenceRegion(areaGroupList, preferenceRegion);
                    break;

                case SUBWAY_AREA:
                    stayRegion = getRegionByPreferenceRegion(areaGroupMap, preferenceRegion);

                    if (stayRegion != null)
                    {
                        getViewInterface().setSubwayAreaTabSelection();
                    }
                    break;
            }
        }

        if (stayRegion == null)
        {
            stayRegion = getDefaultRegion(areaGroupList);
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
    private PreferenceRegion getPreferenceRegion(DailyCategoryType dailyCategoryType)
    {
        return DailyPreference.getInstance(getActivity()).getDailyRegion(dailyCategoryType);
    }

    private StayRegion getRegionByPreferenceRegion(List<StayAreaGroup> areaGroupList, PreferenceRegion preferenceRegion)
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
                return new StayRegion(PreferenceRegion.AreaType.AREA, stayAreaGroup, stayAreaGroup);
            } else
            {
                for (Area area : stayAreaGroup.getAreaList())
                {
                    if (area.name.equalsIgnoreCase(preferenceRegion.areaName) == true)
                    {
                        return new StayRegion(PreferenceRegion.AreaType.AREA, stayAreaGroup, area);
                    }
                }
            }
        }

        return null;
    }

    private StayRegion getRegionByPreferenceRegion(LinkedHashMap<Area, List<StaySubwayAreaGroup>> subwayAreaMap, PreferenceRegion preferenceRegion)
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
                                return new StayRegion(PreferenceRegion.AreaType.SUBWAY_AREA, subwayAreaGroup, area);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private StayRegion getDefaultRegion(@NonNull List<StayAreaGroup> areaGroupList)
    {
        StayAreaGroup areaGroup = areaGroupList.get(0);

        if (areaGroup.getAreaCount() == 0)
        {
            return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, areaGroup);
        } else
        {
            StayArea area = areaGroup.getAreaList().get(0);

            return new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area);
        }
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
    public void onSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = SearchActivity.newInstance(getActivity(), Constants.ServiceType.HOTEL//
            , mStayAreaViewModel.bookDateTime.getValue().getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayAreaViewModel.bookDateTime.getValue().getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        startActivityForResult(intent, StayAreaTabActivity.REQUEST_CODE_SEARCH);

        mAnalytics.onEventSearchClick(getActivity(), mStayAreaViewModel.categoryType.getValue());
    }

    @Override
    public void onAreaTabClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setAreaTabSelection();

        unLockAll();
    }

    @Override
    public void onSubwayTabClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().setSubwayAreaTabSelection();

        unLockAll();
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

                    setResult(BaseActivity.RESULT_CODE_START_AROUND_SEARCH, mStayAreaViewModel.categoryType.getValue(), null, null);
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

        mAnalytics.onEventAroundSearchClick(getActivity(), mStayAreaViewModel.categoryType.getValue());
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
        if (equalsAreaGroupName(mStayAreaViewModel.previousArea.getValue(), areaGroupName) == true)
        {
            setResult(Activity.RESULT_OK, mStayAreaViewModel.categoryType.getValue(), areaGroup, area);
            finish();
        } else
        {
            String message = mStayAreaViewModel.bookDateTime.getValue().getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayAreaViewModel.bookDateTime.getValue().getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);
            final String previousAreaGroupName, previousAreaName;

            if (mStayAreaViewModel.previousArea.getValue() != null)
            {
                previousAreaGroupName = mStayAreaViewModel.previousArea.getValue().getAreaGroupName();
                previousAreaName = mStayAreaViewModel.previousArea.getValue().getAreaName();
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
                    mAnalytics.onEventChangedAreaGroupClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());

                    setResult(Activity.RESULT_OK, mStayAreaViewModel.categoryType.getValue(), areaGroup, area);
                    finish();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedAreaGroupClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());
                    mAnalytics.onEventChangedDateClick(getActivity());

                    // 날짜 선택 화면으로 이동한다.
                    setResult(BaseActivity.RESULT_CODE_START_CALENDAR, mStayAreaViewModel.categoryType.getValue(), areaGroup, area);
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

        mAnalytics.onEventAreaClick(getActivity(), areaGroupName, areaName);
    }

    @Override
    public void onSubwayAreaClick(StaySubwayAreaGroup areaGroup, Area area)
    {
        if (areaGroup == null || area == null)
        {
            finish();
            return;
        }

        final String areaGroupName = areaGroup.name;
        final String areaName = area.name;

        // 지역이 변경된 경우 팝업을 뛰어서 날짜 변경을 할것인지 물어본다.
        if (equalsAreaGroupName(mStayAreaViewModel.previousArea.getValue(), areaGroupName) == true)
        {
            setResult(Activity.RESULT_OK, mStayAreaViewModel.categoryType.getValue(), areaGroup.getRegion(), areaGroup, area);
            finish();
        } else
        {
            String message = mStayAreaViewModel.bookDateTime.getValue().getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayAreaViewModel.bookDateTime.getValue().getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);
            final String previousAreaGroupName, previousAreaName;

            if (mStayAreaViewModel.previousArea.getValue() != null)
            {
                previousAreaGroupName = mStayAreaViewModel.previousArea.getValue().getAreaGroupName();
                previousAreaName = mStayAreaViewModel.previousArea.getValue().getAreaName();
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
                    mAnalytics.onEventChangedAreaGroupClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());

                    setResult(Activity.RESULT_OK, mStayAreaViewModel.categoryType.getValue(), areaGroup.getRegion(), areaGroup, area);
                    finish();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedAreaGroupClick(getActivity(), previousAreaGroupName, previousAreaName, areaGroupName, areaName, mStayAreaViewModel.bookDateTime.getValue());
                    mAnalytics.onEventChangedDateClick(getActivity());

                    // 날짜 선택 화면으로 이동한다.
                    setResult(BaseActivity.RESULT_CODE_START_CALENDAR, mStayAreaViewModel.categoryType.getValue(), areaGroup.getRegion(), areaGroup, area);
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

        mAnalytics.onEventAreaClick(getActivity(), areaGroupName, areaName);
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

        setResult(resultCode, categoryType, areaGroup, area, new StayRegion(PreferenceRegion.AreaType.AREA, areaGroup, area));
    }

    void setResult(int resultCode, DailyCategoryType categoryType, Area regionArea, StaySubwayAreaGroup areaGroup, Area area)
    {
        if (categoryType != null && regionArea != null && areaGroup != null && area != null)
        {
            setPreferenceSubwayArea(categoryType, regionArea.name, areaGroup.name, area.name);
        }

        setResult(resultCode, categoryType, areaGroup, area, new StayRegion(PreferenceRegion.AreaType.SUBWAY_AREA, areaGroup, area));
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
