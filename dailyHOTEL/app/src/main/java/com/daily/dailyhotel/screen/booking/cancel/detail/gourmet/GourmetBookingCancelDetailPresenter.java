package com.daily.dailyhotel.screen.booking.cancel.detail.gourmet;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetBookingDetail;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.call.restaurant.RestaurantCallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Random;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetBookingCancelDetailPresenter //
    extends BaseExceptionPresenter<GourmetBookingCancelDetailActivity, GourmetBookingCancelDetailInterface> //
    implements GourmetBookingCancelDetailView.OnEventListener
{
    GourmetBookingCancelAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;

    private int mReservationIndex;
    private String mAggregationId;
    private String mImageUrl;

    private CommonDateTime mCommonDateTime;
    private GourmetBookingDetail mGourmetBookingDetail;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface GourmetBookingCancelAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventShareClick(Activity activity);

        void onEventConciergeClick(Activity activity);

        void onEventViewDetailClick(Activity activity);

        void onEventNavigatorClick(Activity activity);

        void onEventHideBookingCancelClick(Activity activity);

        GourmetDetailAnalyticsParam getDetailAnalyticsParam(GourmetBookingDetail gourmetBookingDetail);
    }

    public GourmetBookingCancelDetailPresenter(@NonNull GourmetBookingCancelDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetBookingCancelDetailInterface createInstanceViewInterface()
    {
        return new GourmetBookingCancelDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetBookingCancelDetailActivity activity)
    {
        setContentView(R.layout.activity_gourmet_booking_cancel_detail_data);

        setAnalytics(new GourmetBookingCancelDetailCancelAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mBookingRemoteImpl = new BookingRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (GourmetBookingCancelAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mReservationIndex = intent.getIntExtra(GourmetBookingCancelDetailActivity.INTENT_EXTRA_DATA_BOOKING_INDEX, -1);
        mAggregationId = intent.getStringExtra(GourmetBookingCancelDetailActivity.INTENT_EXTRA_DATA_AGGREGATION_ID);
        mImageUrl = intent.getStringExtra(GourmetBookingCancelDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return false;
        }

        return true;
    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(getString(R.string.actionbar_title_booking_cancel_list_activity));
        getViewInterface().setBookingDetailToolbar();

        getViewInterface().setDeleteBookingVisible(true);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        mAnalytics.onScreen(getActivity());
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
        if (mGourmetBookingDetail != null && getViewInterface().isExpandedMap() == true)
        {
            onCollapseMapClick();
            return true;
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

        // CODE_RESULT_ACTIVITY_GO_HOME 처리가 필요한지 모르겠음...
        switch (requestCode)
        {
            case GourmetBookingCancelDetailActivity.REQUEST_CODE_DETAIL:
            {
                setResult(resultCode);

                if (resultCode == Activity.RESULT_OK //
                    || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY //
                    || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case GourmetBookingCancelDetailActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                        onMyLocationClick();
                        break;

                    default:
                        break;
                }
                break;
            }

            case GourmetBookingCancelDetailActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
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

        Observable<GourmetBookingDetail> detailObservable = Observable.defer(new Callable<ObservableSource<GourmetBookingDetail>>()
        {
            @Override
            public ObservableSource<GourmetBookingDetail> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                {
                    return mBookingRemoteImpl.getGourmetBookingDetail(mReservationIndex);
                }

                return mBookingRemoteImpl.getGourmetBookingDetail(mAggregationId);
            }
        });

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), detailObservable, new BiFunction<CommonDateTime, GourmetBookingDetail, GourmetBookingDetail>()
        {
            @Override
            public GourmetBookingDetail apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime //
                , @io.reactivex.annotations.NonNull GourmetBookingDetail gourmetBookingDetail) throws Exception
            {
                setCommonDateTime(commonDateTime);
                setGourmetBookingDetail(gourmetBookingDetail);

                return gourmetBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetBookingDetail>()
        {
            @Override
            public void accept(GourmetBookingDetail stayBookingDetail) throws Exception
            {
                notifyGourmetBookingDetailChanged();

                unLockAll();

                //                mAnalytics.onScreen(getActivity(), Booking.BOOKING_STATE_CANCEL, mGourmetBookingDetail.stayIndex, mGourmetBookingDetail.refundStatus);
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
    public void onShareClick()
    {
        if (lock() == true)
        {
            return;
        }

        getViewInterface().showShareDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });

        mAnalytics.onEventShareClick(getActivity());
    }

    @Override
    public void onMapLoading()
    {
        DailyToast.showToast(getActivity(), R.string.message_loading_map, DailyToast.LENGTH_SHORT);
    }

    @Override
    public void onMapClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        Intent intent = ZoomMapActivity.newInstance(getActivity()//
            , ZoomMapActivity.SourceType.GOURMET_BOOKING, mGourmetBookingDetail.gourmetName, mGourmetBookingDetail.gourmetAddress//
            , mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude, true);

        startActivityForResult(intent, GourmetBookingCancelDetailActivity.REQUEST_CODE_ZOOMMAP);
        //
        //        AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
        //            , AnalyticsManager.Action.MAP_CLICK, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onExpandMapClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        getViewInterface().setBookingDetailMapToolbar();

        addCompositeDisposable(getViewInterface().expandMap(mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude)//
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    unLockAll();
                }
            }));
        //
        //        AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
        //            , AnalyticsManager.Action.MAP_CLICK, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onCollapseMapClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        clearCompositeDisposable();

        getViewInterface().setBookingDetailToolbar();

        addCompositeDisposable(getViewInterface().collapseMap()//
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    unLockAll();
                }
            }));
    }

    @Override
    public void onViewDetailClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        try
        {
            GourmetDetailAnalyticsParam analyticsParam = mAnalytics.getDetailAnalyticsParam(mGourmetBookingDetail);

            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , mGourmetBookingDetail.gourmetIndex, mGourmetBookingDetail.gourmetName, null, GourmetDetailActivity.NONE_PRICE//
                , mCommonDateTime.dailyDateTime//
                , null, false, false, false, false//
                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                , analyticsParam);

            startActivityForResult(intent, GourmetBookingCancelDetailActivity.REQUEST_CODE_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

            mAnalytics.onEventViewDetailClick(getActivity());
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onNavigatorClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();
        analyticsParam.category = AnalyticsManager.Category.GOURMET_BOOKINGS;
        analyticsParam.action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED;

        startActivityForResult(NavigatorDialogActivity.newInstance(getActivity(), mGourmetBookingDetail.gourmetName//
            , mGourmetBookingDetail.latitude, mGourmetBookingDetail.longitude, false, analyticsParam), GourmetBookingCancelDetailActivity.REQUEST_CODE_NAVIGATOR);

        mAnalytics.onEventNavigatorClick(getActivity());
    }

    @Override
    public void onClipAddressClick()
    {
        if (mGourmetBookingDetail == null)
        {
            return;
        }

        DailyTextUtils.clipText(getActivity(), mGourmetBookingDetail.gourmetAddress);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT);
    }

    @Override
    public void onMyLocationClick()
    {
        if (lock() == true)
        {
            return;
        }

        Observable<Long> locationAnimationObservable = getViewInterface().getLocationAnimation();
        Observable observable = searchMyLocation(locationAnimationObservable);

        if (observable != null)
        {
            addCompositeDisposable(observable.subscribe(new Consumer<Location>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Location location) throws Exception
                {
                    unLockAll();
                    getViewInterface().setMyLocation(location);
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
    }

    @Override
    public void onConciergeClick()
    {
        final String restaurantPhone;
        if (mGourmetBookingDetail == null)
        {
            restaurantPhone = null;
        } else if (DailyTextUtils.isTextEmpty(mGourmetBookingDetail.phone2) == false)
        {
            restaurantPhone = mGourmetBookingDetail.phone2;
        } else if (DailyTextUtils.isTextEmpty(mGourmetBookingDetail.phone1) == false)
        {
            restaurantPhone = mGourmetBookingDetail.phone1;
        } else if (DailyTextUtils.isTextEmpty(mGourmetBookingDetail.phone3) == false)
        {
            restaurantPhone = mGourmetBookingDetail.phone3;
        } else
        {
            restaurantPhone = null;
        }

        getViewInterface().showConciergeDialog(restaurantPhone, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });

        mAnalytics.onEventConciergeClick(getActivity());
    }

    @Override
    public void onConciergeFaqClick()
    {
        startActivity(FAQActivity.newInstance(getActivity()));
    }

    @Override
    public void onRestaurantCallClick(String restaurantPhone)
    {
        if (DailyTextUtils.isTextEmpty(restaurantPhone) == true)
        {
            return;
        }

        startActivityForResult(RestaurantCallDialogActivity.newInstance(getActivity(), restaurantPhone) //
            , GourmetBookingCancelDetailActivity.REQUEST_CODE_RESTAURANT_CALL);
    }

    @Override
    public void onConciergeHappyTalkClick()
    {
        if (mGourmetBookingDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity() //
                , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_BOOKING_CANCEL//
                , mGourmetBookingDetail.gourmetIndex, mReservationIndex, mGourmetBookingDetail.gourmetName), GourmetBookingCancelDetailActivity.REQUEST_CODE_HAPPYTALK);
        } catch (Exception e)
        {
            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");
                    }
                }, null);
        }
    }

    @Override
    public void onConciergeCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetBookingCancelDetailActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onShareKakaoClick()
    {
        if (mGourmetBookingDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            String message = getString(R.string.message_booking_cancel_gourmet_share_kakao, userName //
                , mGourmetBookingDetail.gourmetName, mGourmetBookingDetail.guestName //
                , mGourmetBookingDetail.ticketName, mGourmetBookingDetail.ticketCount //
                , DailyCalendar.convertDateFormatString(mGourmetBookingDetail.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd") //
                , mGourmetBookingDetail.gourmetAddress);

            KakaoLinkManager.newInstance(getActivity()).shareBookingCancelGourmet(message, mImageUrl);

        } catch (Exception e)
        {
            ExLog.d(e.toString());

            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");
                    }
                }, null);
        }
    }

    @Override
    public void onMoreShareClick()
    {
        if (mGourmetBookingDetail == null)
        {
            return;
        }

        try
        {
            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            final String message = getString(R.string.message_booking_cancel_gourmet_share_sms, userName //
                , mGourmetBookingDetail.gourmetName, mGourmetBookingDetail.guestName //
                , mGourmetBookingDetail.ticketName, mGourmetBookingDetail.ticketCount //
                , DailyCalendar.convertDateFormatString(mGourmetBookingDetail.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd") //
                , mGourmetBookingDetail.gourmetAddress);

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
            startActivity(chooser);

        } catch (Exception e)
        {
            unLockAll();

            ExLog.d(e.toString());
        }

    }

    @Override
    public void onHiddenReservationClick()
    {
        if (mGourmetBookingDetail == null || lock() == true)
        {
            return;
        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking)//
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    screenLock(true);

                    addCompositeDisposable(mBookingRemoteImpl.getGourmetHiddenBooking(mReservationIndex) //
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
                        {
                            @Override
                            public void accept(@NonNull Boolean result) throws Exception
                            {
                                unLockAll();

                                if (result == true)
                                {
                                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2)//
                                        , getString(R.string.message_booking_delete_booking)//
                                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                                                finish();
                                            }
                                        });

                                    mAnalytics.onEventHideBookingCancelClick(getActivity());
                                } else
                                {
                                    getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2)//
                                        , getString(R.string.message_booking_failed_delete_booking)//
                                        , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                            }
                                        });
                                }
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception
                            {
                                unLockAll();

                                getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2)//
                                    , getString(R.string.message_booking_failed_delete_booking)//
                                    , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                    {
                                        @Override
                                        public void onDismiss(DialogInterface dialog)
                                        {
                                        }
                                    });
                            }
                        }));
                }
            }, null, null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    unLock();
                }
            }, true);
    }

    void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    void setGourmetBookingDetail(GourmetBookingDetail gourmetBookingDetail)
    {
        mGourmetBookingDetail = gourmetBookingDetail;
    }

    void notifyGourmetBookingDetailChanged()
    {
        if (mGourmetBookingDetail == null)
        {
            return;
        }

        try
        {
            String ticketDateFormat = DailyCalendar.convertDateFormatString(mGourmetBookingDetail.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH:mm");

            // TODO : 임시 방문인원 - 서버 연결 시 추가 작업 예정
            int randPersons = new Random().nextInt(10);

            getViewInterface().setBookingDateAndPersons(ticketDateFormat, randPersons);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setBookingDetail(mGourmetBookingDetail);
    }

    private Observable<Location> searchMyLocation(Observable locationAnimationObservable)
    {
        if (mDailyLocationExFactory == null)
        {
            mDailyLocationExFactory = new DailyLocationExFactory(getActivity());
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
                mDailyLocationExFactory.checkLocationMeasure(new DailyLocationExFactory.OnCheckLocationListener()
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
                    public void onProviderEnabled()
                    {
                        mDailyLocationExFactory.startLocationMeasure(new DailyLocationExFactory.OnLocationListener()
                        {
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

                    @Override
                    public void onProviderDisabled()
                    {
                        if (locationAnimationDisposable != null)
                        {
                            locationAnimationDisposable.dispose();
                        }

                        observer.onError(new ProviderException());
                    }
                });
            }
        }.doOnComplete(() ->
        {
            if (locationAnimationDisposable != null)
            {
                locationAnimationDisposable.dispose();
            }
        }).doOnDispose(() ->
        {
            if (locationAnimationDisposable != null)
            {
                locationAnimationDisposable.dispose();
            }
        }).doOnError(throwable ->
        {
            unLockAll();

            if (throwable instanceof PermissionException)
            {
                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, GourmetBookingCancelDetailActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GourmetBookingCancelDetailActivity.REQUEST_CODE_SETTING_LOCATION);
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
        });
    }
}
