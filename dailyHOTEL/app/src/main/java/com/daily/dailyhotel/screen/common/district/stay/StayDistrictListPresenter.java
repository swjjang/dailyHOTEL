package com.daily.dailyhotel.screen.common.district.stay;


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
import com.daily.dailyhotel.entity.District;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayDistrict;
import com.daily.dailyhotel.entity.StayTown;
import com.daily.dailyhotel.parcel.StayTownParcel;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.DailyCategoryType;
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
public class StayDistrictListPresenter extends BaseExceptionPresenter<StayDistrictListActivity, StayDistrictListInterface> implements StayDistrictListView.OnEventListener
{
    private StayDistrictListAnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    private StayBookDateTime mStayBookDateTime;

    private String mCategoryCode;
    private List<StayDistrict> mDistrictList;
    private int mDistrictPosition = -1;
    private DailyCategoryType mDailyCategoryType;
    private StayTown mSavedTown;

    DailyLocationFactory mDailyLocationFactory;

    public interface StayDistrictListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, String categoryCode);

        void onEventSearchClick(Activity activity, DailyCategoryType dailyCategoryType);

        void onEventChangedDistrictClick(Activity activity, String previousDistrictName, String previousTownName//
            , String changedDistrictName, String changedTownName, StayBookDateTime stayBookDateTime);

        void onEventChangedDateClick(Activity activity);

        void onEventTownClick(Activity activity, String districtName, String townName);

        void onEventClosedClick(Activity activity, String stayCategory);

        void onEventAroundSearchClick(Activity activity, DailyCategoryType dailyCategoryType);
    }

    public StayDistrictListPresenter(@NonNull StayDistrictListActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayDistrictListInterface createInstanceViewInterface()
    {
        return new StayDistrictListView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayDistrictListActivity activity)
    {
        setContentView(R.layout.activity_stay_region_list_data);

        setAnalytics(new StayDistrictListAnalyticsImpl());

        mStayRemoteImpl = new StayRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayDistrictListAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        String checkInDateTime = intent.getStringExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CHECK_IN_DATE_TIME);
        String checkOutDateTime = intent.getStringExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CHECK_OUT_DATE_TIME);

        mStayBookDateTime = new StayBookDateTime();
        mStayBookDateTime.getCheckInDateTime(checkInDateTime);
        mStayBookDateTime.getCheckOutDateTime(checkOutDateTime);

        // 카테고리로 넘어오는 경우
        mDailyCategoryType = DailyCategoryType.valueOf(intent.getStringExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_STAY_CATEGORY));


        // 이름으로 넘어오는 경우


        mCategoryCode = intent.getStringExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CATEGORY_CODE);

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
            case StayDistrictListActivity.REQUEST_CODE_PERMISSION_MANAGER:
                getViewInterface().setLocationTermVisible(false);

                if (resultCode == Activity.RESULT_OK)
                {
                    onAroundSearchClick();
                }
                break;

            case StayDistrictListActivity.REQUEST_CODE_SEARCH:
                break;

            case StayDistrictListActivity.REQUEST_CODE_SETTING_LOCATION:
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

        addCompositeDisposable(mStayRemoteImpl.getDistrictList(mDailyCategoryType).observeOn(AndroidSchedulers.mainThread()).flatMap(new Function<List<StayDistrict>, ObservableSource<Boolean>>()
        {
            @Override
            public ObservableSource<Boolean> apply(List<StayDistrict> districtList) throws Exception
            {
                mDistrictList = districtList;

                getViewInterface().setDistrictList(districtList);

                mSavedTown = null;
                Pair<String, String> namePair = getDistrictNTownNameByCategory(mDailyCategoryType);

                if (namePair != null)
                {
                    mDistrictPosition = getDistrictPosition(districtList, namePair.first);
                } else
                {
                    mDistrictPosition = -1;
                }

                // 기존에 저장된 지역이 있는 경우
                if (mDistrictPosition >= 0)
                {
                    StayDistrict stayDistrict = districtList.get(mDistrictPosition);

                    List<StayTown> stayTownList = stayDistrict.getTownList();

                    mSavedTown = getTown(stayTownList, namePair.second);

                    if (mSavedTown == null)
                    {
                        mSavedTown = new StayTown(stayDistrict);
                    }

                    return expandGroupWithAnimation(mDistrictPosition, false).subscribeOn(AndroidSchedulers.mainThread());
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
        startActivityForResult(intent, StayDistrictListActivity.REQUEST_CODE_SEARCH);
    }

    @Override
    public void onDistrictClick(int groupPosition)
    {
        if (mDistrictList == null || mDistrictList.size() == 0 || groupPosition < 0 || lock() == true)
        {
            return;
        }

        // 하위 지역이 없으면 선택
        if (mDistrictList.get(groupPosition).getTownCount() == 0)
        {
            onDistrictClick(mDistrictList.get(groupPosition));

            unLockAll();
        } else
        {
            // 하위 지역이 있으면 애니메이션
            if (mDistrictPosition == groupPosition)
            {
                addCompositeDisposable(collapseGroupWithAnimation(groupPosition, true).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception
                    {
                        mDistrictPosition = -1;

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
                addCompositeDisposable(collapseGroupWithAnimation(mDistrictPosition, false).subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Boolean>>()
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
                        mDistrictPosition = groupPosition;

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
    public void onTownClick(int groupPosition, StayTown stayTown)
    {
        if (groupPosition < 0 || stayTown == null)
        {
            finish();
            return;
        }

        StayDistrict stayDistrict = mDistrictList.get(groupPosition);

        final String districtName = stayDistrict.name;
        final String townName = stayTown.name;

        // 지역이 변경된 경우 팝업을 뛰어서 날짜 변경을 할것인지 물어본다.
        if (mDailyCategoryType != DailyCategoryType.STAY_ALL || equalsDistrictName(mSavedTown, districtName) == true)
        {
            setResult(Activity.RESULT_OK, mDailyCategoryType, stayTown);
            finish();
        } else
        {
            String message = mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);
            final String previousDistrictName, previousTownName;

            if (mSavedTown != null)
            {
                District district = mSavedTown.getDistrict();
                previousDistrictName = district == null ? null : district.name;
                previousTownName = mSavedTown.name;
            } else
            {
                previousDistrictName = null;
                previousTownName = null;
            }

            getViewInterface().showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedDistrictClick(getActivity(), previousDistrictName, previousTownName, districtName, townName, mStayBookDateTime);

                    setResult(Activity.RESULT_OK, mDailyCategoryType, stayTown);
                    finish();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mAnalytics.onEventChangedDistrictClick(getActivity(), previousDistrictName, previousTownName, districtName, townName, mStayBookDateTime);
                    mAnalytics.onEventChangedDateClick(getActivity());

                    // 날짜 선택 화면으로 이동한다.
                    setResult(BaseActivity.RESULT_CODE_START_CALENDAR, mDailyCategoryType, stayTown);
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

        mAnalytics.onEventTownClick(getActivity(), districtName, townName);
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
                startActivityForResult(intent, StayDistrictListActivity.REQUEST_CODE_PERMISSION_MANAGER);
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

                setResult(BaseActivity.RESULT_CODE_START_AROUND_SEARCH, mDailyCategoryType, null);
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
                            startActivityForResult(intent, StayDistrictListActivity.REQUEST_CODE_SETTING_LOCATION);
                        }
                    }, null, false);
            }
        });
    }

    private void onDistrictClick(StayDistrict stayDistrict)
    {
        if (stayDistrict == null)
        {
            finish();
            return;
        }

        setResult(Activity.RESULT_OK, mDailyCategoryType, new StayTown(stayDistrict));
        finish();
    }

    /**
     * first : District 이름
     * second : Town 이름
     *
     * @param dailyCategoryType
     * @return
     */
    private Pair<String, String> getDistrictNTownNameByCategory(DailyCategoryType dailyCategoryType)
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

    private void setCategoryRegion(DailyCategoryType dailyCategoryType, String districtName, String townName)
    {
        if (dailyCategoryType == null)
        {
            return;
        }

        JSONObject jsonObject;
        try
        {
            jsonObject = new JSONObject();
            jsonObject.put(Constants.JSON_KEY_PROVINCE_NAME, DailyTextUtils.isTextEmpty(districtName) ? "" : districtName);
            jsonObject.put(Constants.JSON_KEY_AREA_NAME, DailyTextUtils.isTextEmpty(townName) ? "" : townName);
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

    private void setResult(int resultCode, DailyCategoryType categoryType, StayTown stayTown)
    {
        if (categoryType == null)
        {
            return;
        }

        Intent intent = new Intent();

        if (stayTown != null && stayTown.getDistrict() != null)
        {
            setCategoryRegion(categoryType, stayTown.getDistrict().name, stayTown.name);

            intent.putExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_STAY_TOWN, new StayTownParcel(stayTown));
        }

        intent.putExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_STAY_CATEGORY, categoryType.name());

        if (mSavedTown != null && mSavedTown.getDistrict() != null && mSavedTown.getDistrict().name.equalsIgnoreCase(stayTown.getDistrict().name) == true)
        {
            intent.putExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CHANGED_DISTRICT, false);
        } else
        {
            intent.putExtra(StayDistrictListActivity.INTENT_EXTRA_DATA_CHANGED_DISTRICT, true);
        }

        setResult(resultCode, intent);
    }

    private int getDistrictPosition(List<StayDistrict> districtList, String districtName)
    {
        if (districtList == null || districtList.size() == 0 || DailyTextUtils.isTextEmpty(districtName) == true)
        {
            return -1;
        }

        int size = districtList.size();

        for (int i = 0; i < size; i++)
        {
            if (districtList.get(i).name.equalsIgnoreCase(districtName) == true)
            {
                return i;
            }
        }

        return -1;
    }

    private StayTown getTown(List<StayTown> townList, String townName)
    {
        if (townList == null || townList.size() == 0 || DailyTextUtils.isTextEmpty(townName) == true)
        {
            return null;
        }

        for (StayTown stayTown : townList)
        {
            if (stayTown.name.equalsIgnoreCase(townName) == true)
            {
                return stayTown;
            }
        }

        return null;
    }

    private boolean equalsDistrictName(StayTown stayTown, String districtName)
    {
        if (stayTown == null || DailyTextUtils.isTextEmpty(districtName) == true)
        {
            return false;
        }

        District district = stayTown.getDistrict();

        if (district == null)
        {
            return false;
        }

        return districtName.equalsIgnoreCase(district.name);
    }
}
