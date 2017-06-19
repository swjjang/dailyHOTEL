package com.daily.dailyhotel.screen.stay.outbound.filter;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundFilters;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundFilterPresenter extends BaseExceptionPresenter<StayOutboundFilterActivity, StayOutboundFilterViewInterface> implements StayOutboundFilterView.OnEventListener
{
    private StayOutboundFilterAnalyticsInterface mAnalytics;
    private StayOutboundFilters mStayOutboundFilters;
    private StayOutboundFilters.SortType mPrevSortType;
    private boolean[] mEnabledLines;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface StayOutboundFilterAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundFilterPresenter(@NonNull StayOutboundFilterActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundFilterViewInterface createInstanceViewInterface()
    {
        return new StayOutboundFilterView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundFilterActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_filter_data);

        setAnalytics(new StayStayOutboundFilterAnalyticsImpl());

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundFilterAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        try
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.valueOf(intent.getStringExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT));
        } catch (Exception e)
        {
            mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        }

        mStayOutboundFilters.rating = intent.getIntExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, -1);
        mEnabledLines = intent.getBooleanArrayExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_ENABLEDLINES);

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setSort(mStayOutboundFilters.sortType);
        getViewInterface().setRating(mStayOutboundFilters.rating);
        getViewInterface().setEnabledLines(mEnabledLines);
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

        switch (requestCode)
        {
            case StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
                        {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                            {
                                mStayOutboundFilters.sortType = StayOutboundFilters.SortType.DISTANCE;
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                            {
                                if (throwable instanceof PermissionException)
                                {

                                } else
                                {
                                    onReverseSort();
                                }
                            }
                        }));
                        break;

                    default:
                        onReverseSort();
                        break;
                }
                break;
            }

            case StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_SETTING_LOCATION:
                addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                    {
                        mStayOutboundFilters.sortType = StayOutboundFilters.SortType.DISTANCE;
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                    {
                        if (throwable instanceof PermissionException)
                        {

                        } else
                        {
                            onReverseSort();
                        }
                    }
                }));
                break;
        }
    }

    @Override
    protected void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onSortClick(StayOutboundFilters.SortType sortType)
    {
        if (sortType == null)
        {
            return;
        }

        mPrevSortType = mStayOutboundFilters.sortType;
        mStayOutboundFilters.sortType = sortType;

        //        if (sortType == StayOutboundFilters.SortType.DISTANCE)
        //        {
        //            screenLock(true);
        //
        //            addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
        //            {
        //                @Override
        //                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
        //                {
        //                    mStayOutboundFilters.sortType = StayOutboundFilters.SortType.DISTANCE;
        //                }
        //            }, new Consumer<Throwable>()
        //            {
        //                @Override
        //                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
        //                {
        //                    if (throwable instanceof PermissionException)
        //                    {
        //
        //                    } else
        //                    {
        //                        onReverseSort();
        //                    }
        //                }
        //            }));
        //        }
    }

    @Override
    public void onRatingClick(int rating)
    {
        mStayOutboundFilters.rating = rating;
    }

    @Override
    public void onResetClick()
    {
        if (mStayOutboundFilters == null)
        {
            mStayOutboundFilters = new StayOutboundFilters();
        }

        mStayOutboundFilters.sortType = StayOutboundFilters.SortType.RECOMMENDATION;
        mStayOutboundFilters.rating = -1;

        getViewInterface().setSort(mStayOutboundFilters.sortType);
        getViewInterface().setRating(mStayOutboundFilters.rating);
    }

    @Override
    public void onResultClick()
    {
        Intent intent = new Intent();
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_SORT, mStayOutboundFilters.sortType.name());
        intent.putExtra(StayOutboundFilterActivity.INTENT_EXTRA_DATA_RATING, mStayOutboundFilters.rating);

        setResult(Activity.RESULT_OK, intent);
        onBackClick();
    }

    private void onReverseSort()
    {
        mStayOutboundFilters.sortType = mPrevSortType;
        getViewInterface().setSort(mStayOutboundFilters.sortType);
    }

    private Observable<Location> searchMyLocation()
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory();
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
                        observer.onError(new PermissionException());
                    }

                    @Override
                    public void onFailed()
                    {
                        observer.onError(new Exception());
                    }

                    @Override
                    public void onAlreadyRun()
                    {
                        observer.onNext(null);
                        observer.onComplete();
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
                        observer.onError(new ProviderException());
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
                });
            }
        }.doOnError(new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                unLockAll();

                if (throwable instanceof PermissionException)
                {
                    Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                    startActivityForResult(intent, StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    View.OnClickListener positiveListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, StayOutboundFilterActivity.REQUEST_CODE_STAYOUTBOUND_SETTING_LOCATION);
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
                } else
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            }
        });
    }
}
