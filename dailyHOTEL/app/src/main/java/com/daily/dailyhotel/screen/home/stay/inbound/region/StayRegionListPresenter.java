package com.daily.dailyhotel.screen.home.stay.inbound.region;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Area;
import com.daily.dailyhotel.entity.Province;
import com.daily.dailyhotel.entity.Region;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.repository.remote.StayRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
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
public class StayRegionListPresenter extends BaseExceptionPresenter<StayRegionListActivity, StayRegionListInterface> implements StayRegionListView.OnEventListener
{
    private StayRegionListAnalyticsInterface mAnalytics;

    private StayRemoteImpl mStayRemoteImpl;

    private StayBookDateTime mStayBookDateTime;

    private String mCategoryCode;
    private int mProvinceIndex, mAreaIndex;
    private List<Region> mRegionList;
    private int mProvincePosition = -1;

    public interface StayRegionListAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onEventSearchClick(Activity activity);
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

        mProvinceIndex = intent.getIntExtra(StayRegionListActivity.INTENT_EXTRA_DATA_PROVINCE_INDEX, -1);
        mAreaIndex = intent.getIntExtra(StayRegionListActivity.INTENT_EXTRA_DATA_AREA_INDEX, -1);
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

        addCompositeDisposable(mStayRemoteImpl.getRegionList().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Region>>()
        {
            @Override
            public void accept(List<Region> regionList) throws Exception
            {
                mRegionList = regionList;
                getViewInterface().setRegionList(regionList);

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

        //        Intent intent = SearchActivity.newInstance(this, PlaceType.HOTEL, mStayBookingDay);
        //        startActivityForResult(intent, StayRegionListActivity.REQUEST_CODE_SEARCH);
    }

    @Override
    public void onProvinceClick(int groupPosition)
    {
        if (mRegionList == null || mRegionList.size() == 0 || lock() == true)
        {
            return;
        }

        // 하위 지역이 존재하는지
        if (mRegionList.get(groupPosition).getAreaCount() == 0)
        {
            onRegionClick(mRegionList.get(groupPosition).getProvince());

            unLockAll();
        } else
        {
            Observable<Boolean> collapseObservable = getViewInterface().collapseGroupWithAnimation(mProvincePosition, mProvincePosition == groupPosition);

            if (collapseObservable == null)
            {
                collapseObservable = Observable.just(true);
            }

            if (mProvincePosition == groupPosition)
            {
                addCompositeDisposable(collapseObservable.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
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
                    }
                }));
            } else
            {
                addCompositeDisposable(collapseObservable.subscribeOn(AndroidSchedulers.mainThread()).flatMap(new Function<Boolean, ObservableSource<Boolean>>()
                {
                    @Override
                    public ObservableSource<Boolean> apply(Boolean aBoolean) throws Exception
                    {
                        Observable<Boolean> expandObservable = getViewInterface().expandGroupWidthAnimation(groupPosition, true);

                        return expandObservable == null ? Observable.just(true) : expandObservable.subscribeOn(AndroidSchedulers.mainThread());
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

    }

    private void onRegionClick(Province province)
    {
    }

    private void onRegionClick(Area area)
    {
        if (area == null || mProvincePosition < 0)
        {
            setResult(Activity.RESULT_CANCELED);
            onBackClick();
            return;
        }

        Region region = mRegionList.get(mProvincePosition);

        // 지역이 변경된 경우 팝업을 뛰어서 날짜 변경을 할것인지 물어본다.
        if (mProvinceIndex == region.getProvince().index && mAreaIndex == area.index)
        {
            setResult(Activity.RESULT_OK);
            onBackClick();
        } else
        {
            String message = mStayBookDateTime.getCheckInDateTime("yyyy.MM.dd(EEE)") + "-" + mStayBookDateTime.getCheckOutDateTime("yyyy.MM.dd(EEE)") + "\n" + getString(R.string.message_region_search_date);

            //            final String analyticsLabel = getRegionAnalytics(mSelectedProvince, province, mStayBookingDay);
            getViewInterface().showSimpleDialog(getString(R.string.label_visit_date), message, getString(R.string.dialog_btn_text_yes), getString(R.string.label_region_change_date), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //                    if (analyticsLabel != null)
                    //                    {
                    //                        AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    //                            , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CHANGED, analyticsLabel, null);
                    //                    }

                    //                    Intent intent = new Intent();
                    //                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                    //                    setResult(Activity.RESULT_OK, intent);

                    //                    recordEvent(province);
                    //                    onBackClick();
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //                    if (analyticsLabel != null)
                    //                    {
                    //                        AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    //                            , AnalyticsManager.Action.HOTEL_BOOKING_DATE_CONFIRMED, analyticsLabel, null);
                    //                    }
                    //
                    //                    AnalyticsManager.getInstance(StayRegionListActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                    //                        , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.CHANGE_LOCATION, null);
                    //
                    //                    // 날짜 선택 화면으로 이동한다.
                    //                    Intent intent = new Intent();
                    //                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PROVINCE, province);
                    //                    intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEBOOKINGDAY, mStayBookingDay);
                    //                    setResult(RESULT_CHANGED_DATE, intent);
                    //
                    //                    recordEvent(province);
                    //                    onBackClick();
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
    }
}
