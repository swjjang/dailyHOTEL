package com.daily.dailyhotel.screen.common.region.stay;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;

import com.daily.base.BaseActivity;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Province;
import com.daily.dailyhotel.entity.Region;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
import com.twoheart.dailyhotel.place.activity.PlaceRegionListActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.search.SearchActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyLocationFactory;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayRegionListPresenter extends BaseExceptionPresenter<StayRegionListActivity, StayRegionListInterface> implements StayRegionListView.OnEventListener
{
    private StayRegionListAnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    private StayBookDateTime mStayBookDateTime;

    private String mCategoryCode;
    private List<Region> mRegionList;
    private int mProvincePosition = -1;
    private DailyCategoryType mDailyCategoryType;
    private Pair<String, String> mCategoryRegion;

    DailyLocationFactory mDailyLocationFactory;

    public interface StayRegionListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, String categoryCode);

        void onEventSearchClick(Activity activity, DailyCategoryType dailyCategoryType);

        void onEventChangedProvinceClick(Activity activity, String previousProvinceName, String previousAreaName, String changedProvinceName, String changedAreaName, StayBookDateTime stayBookDateTime);

        void onEventChangedDateClick(Activity activity);

        void onEventChangedRegionClick(Activity activity, String provinceName, String areaName);

        void onEventClosedClick(Activity activity, String stayCategory);

        void onEventAroundSearchClick(Activity activity, DailyCategoryType dailyCategoryType);
    }

    public StayRegionListPresenter(@NonNull StayRegionListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayRegionListInterface createInstanceViewInterface()
    {
        return new StayRegionListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayRegionListActivity activity)
    {
        setContentView(R.layout.activity_stay_region_list_data);

        setAnalytics(new StayRegionListAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayRegionListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        String checkInDateTime = intent.getStringExtra(StayRegionListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayRegionListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        mStayBookDateTime = new StayBookDateTime();
        mStayBookDateTime.getCheckInDateTime(checkInDateTime);
        mStayBookDateTime.getCheckOutDateTime(checkOutDateTime);

        // 카테고리로 넘어오는 경우
        mDailyCategoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayRegionListActivity.INTENT_EXTRA_DATA_STAY_CATEGORY));
        mCategoryRegion = getCategoryRegion(mDailyCategoryType);

        // 이름으로 넘어오는 경우


        mCategoryCode = intent.getStringExtra(StayRegionListActivity.INTENT_EXTRA_DATA_CATEGORY_CODE);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.label_selectarea_stay_area));
        getViewInterface().setLocationTermVisible(DailyPreference.getInstance(getActivity()).isAgreeTermsOfLocation() == false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        if (mDailyCategoryType == DailyCategoryType.STAY_ALL)
        {
            mAnalytics.onScreen(getActivity(), mCategoryCode);
        } else
        {
            mAnalytics.onScreen(getActivity(), getString(mDailyCategoryType.getCodeResId()));
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
        mAnalytics.onEventClosedClick(getActivity(), getString(mDailyCategoryType.getCodeResId()));

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
            case StayRegionListActivity.REQUEST_CODE_PERMISSION_MANAGER:
                getViewInterface().setLocationTermVisible(false);

                if (resultCode == Activity.RESULT_OK)
                {
                    onAroundSearchClick();
                }
                break;

            case StayRegionListActivity.REQUEST_CODE_SEARCH:
                break;

            case StayRegionListActivity.REQUEST_CODE_SETTING_LOCATION:
                getViewInterface().setLocationTermVisible(false);

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

        addCompositeDisposable(mStayRemoteImpl.getRegionList().observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<Region>, ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> apply(List<Region> regionList) throws Exception
            {
                mRegionList = regionList;
                getViewInterface().setRegionList(regionList);

                // 기존에 저장된 지역이 있는 경우
                if (mCategoryRegion != null && DailyTextUtils.isTextEmpty(mCategoryRegion.first) == false)
                {
                    int size = regionList.size();

                    for (int i = 0; i < size; i++)
                    {
                        if (regionList.get(i).getProvince().name.equalsIgnoreCase(mCategoryRegion.first) == true)
                        {
                            mProvincePosition = i;

                            return expandGroupWithAnimation(i, false).subscribeOn(AndroidSchedulers.mainThread());
                        }
                    }
                }

                return Observable.just(true);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
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

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        Intent intent = SearchActivity.newInstance(getActivity(), Constants.PlaceType.HOTEL//
            , mStayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
            , mStayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT));
        startActivityForResult(intent, StayRegionListActivity.REQUEST_CODE_SEARCH);
    }

    @Override
    public void onProvinceClick(int groupPosition)
    {
        if (mRegionList == null || mRegionList.size() == 0 || groupPosition < 0 || lock() == true)
        {
            return;
        }

        // 하위 지역이 없으면 선택
        if (mRegionList.get(groupPosition).getAreaCount() == 0)
        {
            onProvinceClick(mRegionList.get(groupPosition).getProvince());

            unLockAll();
        } else
        {
            // 하위 지역이 있으면 애니메이션
            if (mProvincePosition == groupPosition)
            {
                addCompositeDisposable(collapseGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        mProvincePosition = -1;

                        unLockAll();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();

                        finish();
                    }
                }));
            } else
            {
                addCompositeDisposable(collapseGroupWithAnimation(mProvincePosition, false).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Boolean>>()
                {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception
                    {
                        return expandGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread());
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        mProvincePosition = groupPosition;

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
        }
    }

    @Override
    public void onAreaClick(int groupPosition, Area area)
    {
        if (groupPosition < 0 || area == null)
        {
            finish();
            return;
        }

        Region region = mRegionList.get(groupPosition);

        final String provinceName = region.getProvince().name;

        // 지역이 변경된 경우 팝업을 뛰어서 날짜 변경을 할것인지 물어본다.
        if (mCategoryRegion != null && provinceName.equalsIgnoreCase(mCategoryRegion.first) == true)
        {
            setCategoryRegion(mDailyCategoryType, provinceName, area.name);

            setResult(Activity.RESULT_OK, mDailyCategoryType, provinceName, area.name);
            finish();
        } else
        {
            String message = mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);
            final String previousProvinceName, previousAreaName;

            if (mCategoryRegion != null)
            {
                previousProvinceName = mCategoryRegion.first;
                previousAreaName = mCategoryRegion.second;
            } else
            {
                previousProvinceName = null;
                previousAreaName = null;
            }

            getViewInterface().showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedProvinceClick(getActivity(), previousProvinceName, previousAreaName, provinceName, area.name, mStayBookDateTime);

                    setResult(Activity.RESULT_OK, mDailyCategoryType, provinceName, area.name);
                    finish();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedProvinceClick(getActivity(), previousProvinceName, previousAreaName, provinceName, area.name, mStayBookDateTime);
                    mAnalytics.onEventChangedDateClick(getActivity());

                    // 날짜 선택 화면으로 이동한다.
                    setResult(BaseActivity.RESULT_CODE_START_CALENDAR, mDailyCategoryType, provinceName, area.name);
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

        mAnalytics.onEventChangedRegionClick(getActivity(), provinceName, area.name);
    }

    @Override
    public void onAroundSearchClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (mDailyLocationFactory == null)
        {
            mDailyLocationFactory = new DailyLocationFactory(getActivity());
        }

        if (mDailyLocationFactory.measuringLocation() == true)
        {
            return;
        }

        mDailyLocationFactory.checkLocationMeasure(new DailyLocationFactory.OnCheckLocationListener()
        {
            @Override
            public void onRequirePermission()
            {
                unLockAll();

                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, StayRegionListActivity.REQUEST_CODE_PERMISSION_MANAGER);
            }

            @Override
            public void onFailed()
            {
                unLockAll();
            }

            @Override
            public void onProviderEnabled()
            {
                unLockAll();

                // Location
                Intent intent = new Intent();
                intent.putExtra(PlaceRegionListActivity.NAME_INTENT_EXTRA_DATA_RESULT, PlaceRegionListActivity.Region.DOMESTIC.name());
                setResult(Constants.RESULT_ARROUND_SEARCH_LIST, intent);
                finish();
            }

            @Override
            public void onProviderDisabled()
            {
                unLockAll();

                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                mDailyLocationFactory.stopLocationMeasure();

                getViewInterface().showSimpleDialog(getString(R.string.dialog_title_used_gps)//
                    , getString(R.string.dialog_msg_used_gps)//
                    , getString(R.string.dialog_btn_text_dosetting)//
                    , getString(R.string.dialog_btn_text_cancel)//
                    , new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, StayRegionListActivity.REQUEST_CODE_SETTING_LOCATION);
                        }
                    }, null, false);
            }
        });
    }

    private void onProvinceClick(Province province)
    {
        if (province == null)
        {
            finish();
            return;
        }

        setResult(Activity.RESULT_OK, mDailyCategoryType, province.name, province.name);
        finish();
    }

    private Pair<String, String> getCategoryRegion(DailyCategoryType dailyCategoryType)
    {
        if (dailyCategoryType == null)
        {
            return null;
        }

        JSONObject jsonObject = DailyPreference.getInstance(getActivity()).getDailyRegion(dailyCategoryType);

        if (jsonObject == null)
        {
            return null;
        }

        try
        {
            return new Pair<>(jsonObject.getString(Constants.JSON_KEY_PROVINCE_NAME), jsonObject.getString(Constants.JSON_KEY_AREA_NAME));

        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        return null;
    }

    private void setCategoryRegion(DailyCategoryType dailyCategoryType, String provinceName, String areaName)
    {
        if (dailyCategoryType == null)
        {
            return;
        }

        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_KEY_PROVINCE_NAME, DailyTextUtils.isTextEmpty(provinceName) ? "" : provinceName);
            jsonObject.put(Constants.JSON_KEY_AREA_NAME, DailyTextUtils.isTextEmpty(areaName) ? "" : areaName);
            jsonObject.put(Constants.JSON_KEY_IS_OVER_SEAS, false);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            jsonObject = null;
        }

        DailyPreference.getInstance(getActivity()).setDailyRegion(dailyCategoryType, jsonObject);
    }

    private Observable<Boolean> collapseGroupWithAnimation(int groupPosition, boolean animation)
    {
        Observable<Boolean> observable = getViewInterface().collapseGroupWithAnimation(groupPosition, animation);

        if (observable == null)
        {
            observable = Observable.just(true);
        }

        return observable;
    }

    private Observable<Boolean> expandGroupWithAnimation(int groupPosition, boolean animation)
    {
        Observable<Boolean> observable = getViewInterface().expandGroupWithAnimation(groupPosition, animation);

        if (observable == null)
        {
            observable = Observable.just(true);
        }

        return observable;
    }

    private void setResult(int resultCode, DailyCategoryType categoryType, String provinceName, String areaName)
    {
        setCategoryRegion(categoryType, provinceName, areaName);

        Intent intent = new Intent();
        intent.putExtra(StayRegionListActivity.INTENT_EXTRA_DATA_PROVINCE_NAME, provinceName);
        intent.putExtra(StayRegionListActivity.INTENT_EXTRA_DATA_AREA_NAME, areaName);

        setResult(resultCode, intent);
    }
}
