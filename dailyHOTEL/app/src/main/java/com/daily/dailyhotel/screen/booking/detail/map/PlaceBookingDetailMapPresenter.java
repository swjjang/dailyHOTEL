package com.daily.dailyhotel.screen.booking.detail.map;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.repository.local.model.AnalyticsParam;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.facebook.drawee.view.SimpleDraweeView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Gourmet;
import com.twoheart.dailyhotel.model.Place;
import com.twoheart.dailyhotel.model.Stay;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.PlaceBookingDay;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by android_sam on 2017. 7. 5..
 */

public class PlaceBookingDetailMapPresenter extends BaseExceptionPresenter<PlaceBookingDetailMapActivity, PlaceBookingDetailMapInterface> //
    implements PlaceBookingDetailMapView.OnEventListener
{
    private String mTitle;
    private PlaceBookingDay mPlaceBookingDay;
    private ArrayList<Place> mPlaceList;
    private DailyLocationExFactory mDailyLocationExFactory;

    public PlaceBookingDetailMapPresenter(@NonNull PlaceBookingDetailMapActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected PlaceBookingDetailMapInterface createInstanceViewInterface()
    {
        return new PlaceBookingDetailMapView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(PlaceBookingDetailMapActivity activity)
    {
        setContentView(R.layout.activity_place_booking_detail_map_data);

        //        setAnalytics(new StayOutboundListAnalyticsImpl()); // TODO : Analytics
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        // TODO : Analytics
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        try
        {
            mTitle = intent.getStringExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_TITLE);
            mPlaceBookingDay = intent.getParcelableExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_PLACEBOOKINGDAY);
            mPlaceList = intent.getParcelableArrayListExtra(PlaceBookingDetailMapActivity.INTENT_EXTRA_DATA_PLACE_LIST);

            if (mPlaceList == null || mPlaceList.size() == 0)
            {
                DailyToast.showToast(getActivity(), "Test - 임시 추천 업장이 없습니다", Toast.LENGTH_SHORT);
                return false;
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(DailyTextUtils.isTextEmpty(mTitle) == true //
            ? getActivity().getResources().getString(R.string.label_home_view_all) : mTitle);

        getViewInterface().initMapLayout(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // TODO : Analytics 체크
    }

    @Override
    public void onResume()
    {
        super.onResume();
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }

        // TODO : Analytics
    }

    @Override
    public boolean onBackPressed()
    {
        if (getViewInterface().isMapViewPagerVisibility() == true)
        {
            getViewInterface().setMapViewPagerVisibility(false);
        }

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
            // TODO : 추후 Request code Constants 에서 Activity 의 리퀘스트코드로 변경, StayOutboundListActivity 참조
            case PlaceBookingDetailMapActivity.REQUEST_CODE_SETTING_LOCATION:
            {
                onMyLocationClick();
                break;
            }

            case PlaceBookingDetailMapActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    onMyLocationClick();
                }
                break;
            }
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        // do nothing - onMapReady 에서 처리
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPlaceClick(View view, Place place)
    {
        if (place == null || lock() == true)
        {
            return;
        }

        if (place instanceof Stay)
        {
            // 아직 지원하지 않음
            return;
        }

        Gourmet gourmet = (Gourmet) place;

        GourmetBookingDay gourmetBookingDay = (GourmetBookingDay) mPlaceBookingDay;


        if (Util.isUsedMultiTransition() == true)
        {
            getActivity().setExitSharedElementCallback(new SharedElementCallback()
            {
                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots)
                {
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);

                    for (View view : sharedElements)
                    {
                        if (view instanceof SimpleDraweeView)
                        {
                            view.setVisibility(View.VISIBLE);
                            break;
                        }
                    }
                }
            });

            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), gourmet);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(-1);

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , gourmetBookingDay, gourmet.index, gourmet.name //
                , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut, analyticsParam, true);

            if (intent == null)
            {
                Util.restartApp(getActivity());
                return;
            }

            View simpleDraweeView = view.findViewById(R.id.simpleDraweeView);
            View nameTextView = view.findViewById(R.id.nameTextView);
            View gradientTopView = view.findViewById(R.id.gradientTopView);
            View gradientBottomView = view.findViewById(R.id.gradientView);

            android.support.v4.util.Pair[] pairs = new Pair[4];
            pairs[0] = android.support.v4.util.Pair.create(simpleDraweeView, getString(R.string.transition_place_image));
            pairs[1] = android.support.v4.util.Pair.create(nameTextView, getString(R.string.transition_place_name));
            pairs[2] = android.support.v4.util.Pair.create(gradientTopView, getString(R.string.transition_gradient_top_view));
            pairs[3] = android.support.v4.util.Pair.create(gradientBottomView, getString(R.string.transition_gradient_bottom_view));

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pairs);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL, options.toBundle());
        } else
        {
            AnalyticsParam analyticsParam = new AnalyticsParam();
            analyticsParam.setParam(getActivity(), gourmet);
            analyticsParam.setProvince(null);
            analyticsParam.setTotalListCount(-1);

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , gourmetBookingDay, gourmet.index, gourmet.name //
                , gourmet.imageUrl, gourmet.category, gourmet.isSoldOut, analyticsParam, false);

            startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        }
    }

    @Override
    public void onMapReady()
    {
        if (getActivity().isFinishing() == true)
        {
//            unLockAll();
            return;
        }

        getViewInterface().setPlaceList(mPlaceList, mPlaceBookingDay);

        unLockAll();
    }

    @Override
    public void onMarkerClick(Place place)
    {
        if (place == null || mPlaceList == null)
        {
            return;
        }

        addCompositeDisposable(Observable.just(place).subscribeOn(Schedulers.io()).map(new Function<Place, ArrayList<Place>>()
        {
            @Override
            public ArrayList<Place> apply(@io.reactivex.annotations.NonNull Place place) throws Exception
            {
                Comparator<Place> comparator = new Comparator<Place>()
                {
                    public int compare(Place place1, Place place2)
                    {
                        float[] results1 = new float[3];
                        Location.distanceBetween(place.latitude, place.longitude, place1.latitude, place1.longitude, results1);

                        float[] results2 = new float[3];
                        Location.distanceBetween(place.latitude, place.longitude, place2.latitude, place2.longitude, results2);

                        return Float.compare(results1[0], results2[0]);
                    }
                };

                Collections.sort(mPlaceList, comparator);

                return mPlaceList;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Place>>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull ArrayList<Place> placeList) throws Exception
            {
                getViewInterface().setPlaceMapViewPagerList(getActivity(), placeList);
                getViewInterface().setMapViewPagerVisibility(true);
            }
        }));
    }

    @Override
    public void onMarkersCompleted()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        unLockAll();
    }

    @Override
    public void onMapClick()
    {
        getViewInterface().setMapViewPagerVisibility(false);
    }

    @Override
    public void onMyLocationClick()
    {
        if (lock() == true)
        {
            return;
        }

        screenLock(true);
        Observable<Long> locationAnimationObservable = getViewInterface().getLocationAnimation();

        Observable observable = searchMyLocation(locationAnimationObservable);

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe(new Consumer<Location>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                {
                    getViewInterface().setMyLocation(location);
                    unLockAll();
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();
                }
            }));
        }
    }

    private Observable<Location> searchMyLocation(Observable locationAnimationObservable)
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory();
        }

        if (mDailyLocationExFactory.measuringLocation() == true)
        {
            return null;
        }

        Disposable locationAnimationDisposable;

        if (locationAnimationObservable != null)
        {
            locationAnimationDisposable = locationAnimationObservable.subscribe();
        } else
        {
            locationAnimationDisposable = null;
        }

        return new Observable<Location>()
        {
            @Override
            protected void subscribeActual(Observer<? super Location> observer)
            {
                mDailyLocationExFactory.startLocationMeasure(getActivity(), new DailyLocationExFactory.LocationListenerEx()
                {
                    @Override
                    public void onRequirePermission()
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

                        observer.onError(new Exception());
                    }

                    @Override
                    public void onAlreadyRun()
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

                        observer.onError(new DuplicateRunException());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderEnabled(String provider)
                    {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProviderDisabled(String provider)
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

                        observer.onError(new ProviderException());
                    }

                    @Override
                    public void onLocationChanged(Location location)
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

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
                });
            }
        }.doOnComplete(new Action()
        {
            @Override
            public void run() throws Exception
            {
                if (locationAnimationDisposable != null)
                {
                    locationAnimationDisposable.dispose();
                }
            }
        }).doOnDispose(new Action()
        {
            @Override
            public void run() throws Exception
            {
                if (locationAnimationDisposable != null)
                {
                    locationAnimationDisposable.dispose();
                }
            }
        }).doOnError(new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                if (throwable instanceof PermissionException)
                {
                    Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, PlaceBookingDetailMapActivity.REQUEST_CODE_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    View.OnClickListener positiveListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, PlaceBookingDetailMapActivity.REQUEST_CODE_SETTING_LOCATION);
                        }
                    };

                    View.OnClickListener negativeListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                        }
                    };

                    DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                        }
                    };

                    getViewInterface().showSimpleDialog(//
                        getString(R.string.dialog_title_used_gps), getString(R.string.dialog_msg_used_gps), //
                        getString(R.string.dialog_btn_text_dosetting), //
                        getString(R.string.dialog_btn_text_cancel), //
                        positiveListener, negativeListener, cancelListener, null, true);
                } else if (throwable instanceof DuplicateRunException)
                {

                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }
        });
    }

//    private ArrayList<PlaceViewItem> makePlaceViewItemList(List<? extends Place> placeList)
//    {
//        ArrayList<PlaceViewItem> placeViewItemList = new ArrayList<>();
//        if (placeList == null || placeList.size() == 0)
//        {
//            return placeViewItemList;
//        }
//
//        int entryPosition = 1;
//
//        for (Place place : placeList)
//        {
//            place.entryPosition = entryPosition;
//            placeViewItemList.add(new PlaceViewItem(PlaceViewItem.TYPE_ENTRY, place));
//            entryPosition++;
//        }
//
//        return placeViewItemList;
//    }
}
