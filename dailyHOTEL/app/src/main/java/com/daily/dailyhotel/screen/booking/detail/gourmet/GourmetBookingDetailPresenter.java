package com.daily.dailyhotel.screen.booking.detail.gourmet;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.GourmetMultiBookingDetail;
import com.daily.dailyhotel.entity.GuestInfo;
import com.daily.dailyhotel.entity.PaymentInfo;
import com.daily.dailyhotel.entity.RestaurantInfo;
import com.daily.dailyhotel.entity.Review;
import com.daily.dailyhotel.entity.ReviewAnswerValue;
import com.daily.dailyhotel.entity.ReviewInfo;
import com.daily.dailyhotel.entity.ReviewItem;
import com.daily.dailyhotel.entity.ReviewQuestionItem;
import com.daily.dailyhotel.entity.TicketInfo;
import com.daily.dailyhotel.parcel.analytics.GourmetDetailAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ReviewRemoteImpl;
import com.daily.dailyhotel.screen.booking.detail.gourmet.receipt.GourmetReceiptActivity;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.call.restaurant.RestaurantCallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlaceBookingDetail;
import com.twoheart.dailyhotel.model.ReviewPickQuestion;
import com.twoheart.dailyhotel.model.ReviewScoreQuestion;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.screen.information.FAQActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.review.ReviewActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class GourmetBookingDetailPresenter extends BaseExceptionPresenter<GourmetBookingDetailActivity, GourmetBookingDetailInterface> implements GourmetBookingDetailView.OnEventListener
{
    GourmetBookingDetailAnalyticsInterface mAnalytics;

    CommonRemoteImpl mCommonRemoteImpl;
    ReviewRemoteImpl mReviewRemoteImpl;
    BookingRemoteImpl mBookingRemoteImpl;

    int mReservationIndex;
    String mAggregationId;
    String mImageUrl;
    boolean mIsDeepLink; // 딱히 쓰지는 않음
    int mBookingState;
    CommonDateTime mCommonDateTime;
    GourmetMultiBookingDetail mGourmetBookingDetail;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface GourmetBookingDetailAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventPaymentState(Activity activity, int gourmetIndex, int bookingState);

        void onEventShareClick(Activity activity);

        void onEventShareKakaoClick(Activity activity);

        void onEventMoreShareClick(Activity activity);

        void onEventConciergeClick(Activity activity);

        void onEventConciergeFaqClick(Activity activity);

        void onEventRestaurantCallClick(Activity activity);

        void onEventHappyTalkClick(Activity activity);

        void onEventHappyTalkClick2(Activity activity);

        void onEventConciergeCallClick(Activity activity);

        void onEventStartConciergeCall(Activity activity);

        void onEventMapClick(Activity activity);

        void onEventViewDetailClick(Activity activity);

        void onEventReviewClick(Activity activity);

        void onEventHideBookingClick(Activity activity, int gourmetIndex);

        void onEventHideBookingSuccess(Activity activity, int gourmetIndex);
    }

    public GourmetBookingDetailPresenter(@NonNull GourmetBookingDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected GourmetBookingDetailInterface createInstanceViewInterface()
    {
        return new GourmetBookingDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(GourmetBookingDetailActivity activity)
    {
        setContentView(R.layout.activity_gourmet_booking_detail_data);

        mAnalytics = new GourmetBookingDetailAnalyticsImpl();

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mReviewRemoteImpl = new ReviewRemoteImpl(activity);
        mBookingRemoteImpl = new BookingRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        Bundle bundle = intent.getExtras();

        if (bundle != null)
        {
            mReservationIndex = bundle.getInt(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_BOOKINGIDX);
            mAggregationId = bundle.getString(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_AGGREGATION_ID);
            mImageUrl = bundle.getString(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_URL);
            mIsDeepLink = bundle.getBoolean(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_DEEPLINK, false);
            mBookingState = bundle.getInt(GourmetBookingDetailActivity.NAME_INTENT_EXTRA_DATA_BOOKING_STATE);
        }

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return true;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return;
        }

        if (DailyHotel.isLogin() == false)
        {
            setRefresh(false);
            startLogin();
            return;
        }

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

        if (Util.supportPreview(getActivity()) == true)
        {
            if (getViewInterface().isBlurVisible() == true)
            {
                getViewInterface().setBlurVisible(getActivity(), false);
            }
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

        if (mDailyLocationExFactory != null)
        {
            mDailyLocationExFactory.stopLocationMeasure();
        }
    }

    @Override
    public boolean onBackPressed()
    {
        if (mGourmetBookingDetail != null && getViewInterface() != null && getViewInterface().isExpandedMap() == true)
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

        Util.restartApp(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();

        switch (requestCode)
        {
            case GourmetBookingDetailActivity.REQUEST_CODE_DETAIL:
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

            case GourmetBookingDetailActivity.REQUEST_CODE_LOGIN:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    setRefresh(true);
                } else
                {
                    finish();
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION:
            {
                onMyLocationClick();
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_PERMISSION_MANAGER:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    onMyLocationClick();
                } else if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_ZOOMMAP:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;

            case GourmetBookingDetailActivity.REQUEST_CODE_REVIEW:
            {
                String reviewStatusType = null;

                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_REVIEW_COMPLETE:
                        reviewStatusType = PlaceBookingDetail.ReviewStatusType.COMPLETE;
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_REVIEW_MODIFIABLE:
                        reviewStatusType = PlaceBookingDetail.ReviewStatusType.MODIFIABLE;
                        break;

                    case Constants.CODE_RESULT_ACTIVITY_REVIEW_ADDABLE:
                        reviewStatusType = PlaceBookingDetail.ReviewStatusType.ADDABLE;
                        break;
                }

                if (DailyTextUtils.isTextEmpty(reviewStatusType) == false)
                {
                    if (mGourmetBookingDetail == null)
                    {
                        return;
                    }

                    if (mGourmetBookingDetail.reviewInfo == null)
                    {
                        mGourmetBookingDetail.reviewInfo = new ReviewInfo();
                    }

                    mGourmetBookingDetail.reviewInfo.reviewStatusType = reviewStatusType;
                    getViewInterface().setReviewButtonLayout(mGourmetBookingDetail.reviewInfo);
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_FAQ:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                    finish();
                }
                break;

            case GourmetBookingDetailActivity.REQUEST_CODE_REFUND:
            {
                switch (resultCode)
                {
                    case Constants.CODE_RESULT_ACTIVITY_REFRESH:
                    {
                        setRefresh(true);

                        setResult(resultCode);
                        break;
                    }

                    case Activity.RESULT_OK:
                    {
                        setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                        finish();
                        break;
                    }
                }
                break;
            }

            case GourmetBookingDetailActivity.REQUEST_CODE_RESTAURANT_CALL:
                if (resultCode == Activity.RESULT_OK)
                {
                    mAnalytics.onEventRestaurantCallClick(getActivity());
                }
                break;

            case GourmetBookingDetailActivity.REQUEST_CODE_CALL:
                if (resultCode == Activity.RESULT_OK)
                {
                    mAnalytics.onEventStartConciergeCall(getActivity());
                }
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

        Observable<GourmetMultiBookingDetail> bookingDetailObservable = Observable.defer(new Callable<ObservableSource<GourmetMultiBookingDetail>>()
        {
            @Override
            public ObservableSource<GourmetMultiBookingDetail> call() throws Exception
            {
                return DailyTextUtils.isTextEmpty(mAggregationId) ? mBookingRemoteImpl.getGourmetMultiBookingDetail(mReservationIndex) : mBookingRemoteImpl.getGourmetMultiBookingDetail(mAggregationId);
            }
        });

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), bookingDetailObservable, new BiFunction<CommonDateTime, GourmetMultiBookingDetail, GourmetMultiBookingDetail>()
        {
            @Override
            public GourmetMultiBookingDetail apply(CommonDateTime commonDateTime, GourmetMultiBookingDetail gourmetMultiBookingDetail) throws Exception
            {
                setCommonDateTime(commonDateTime);
                setGourmetBookingDetail(gourmetMultiBookingDetail);
                return gourmetMultiBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<GourmetMultiBookingDetail>()
        {
            @Override
            public void accept(GourmetMultiBookingDetail gourmetMultiBookingDetail) throws Exception
            {
                notifyGourmetBookingDetail();
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

    void setCommonDateTime(CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    void setGourmetBookingDetail(GourmetMultiBookingDetail gourmetBookingDetail)
    {
        mGourmetBookingDetail = gourmetBookingDetail;
    }

    void notifyGourmetBookingDetail()
    {
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.guestInfo == null //
            || mGourmetBookingDetail.restaurantInfo == null || mCommonDateTime == null)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;
        GuestInfo guestInfo = mGourmetBookingDetail.guestInfo;

        try
        {
            //            String ticketDateFormat = DailyCalendar.convertDateFormatString( //
            //                guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.M.d(EEE) HH:mm");

            getViewInterface().setBookingDateAndPersons(guestInfo.arrivalDateTime, guestInfo.numberOfGuest);

            getViewInterface().setBookingDetail(mGourmetBookingDetail);
            getViewInterface().setHiddenBookingVisible(mBookingState);
            getViewInterface().setRemindDate(mCommonDateTime.currentDateTime, guestInfo.arrivalDateTime);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
            Crashlytics.logException(e);
            finish();
            return;
        }

        mAnalytics.onEventPaymentState(getActivity(), restaurantInfo.index, mBookingState);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    private void startLogin()
    {
        getViewInterface().showSimpleDialog(null, getString(R.string.message_booking_detail_do_login) //
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = LoginActivity.newInstance(getActivity());
                    startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_LOGIN);
                }
            }, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });
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
        }.doOnComplete(() -> {
            if (locationAnimationDisposable != null)
            {
                locationAnimationDisposable.dispose();
            }
        }).doOnDispose(() -> {
            if (locationAnimationDisposable != null)
            {
                locationAnimationDisposable.dispose();
            }
        }).doOnError(throwable -> {
            unLockAll();

            if (locationAnimationDisposable != null)
            {
                locationAnimationDisposable.dispose();
            }

            if (throwable instanceof PermissionException)
            {
                Intent intent = PermissionManagerActivity.newInstance(getActivity(), PermissionManagerActivity.PermissionType.ACCESS_FINE_LOCATION);
                startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION);
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

            } else if (throwable instanceof ResolvableApiException)
            {
                try
                {
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), GourmetBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION);
                } catch (Exception e)
                {
                    DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
                }
            } else
            {
                DailyToast.showToast(getActivity(), R.string.message_failed_mylocation, DailyToast.LENGTH_SHORT);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onIssuingReceiptClick()
    {
        if (getActivity() == null || lock() == true)
        {
            return;
        }

        Intent intent = GourmetReceiptActivity.newInstance(getActivity(), mReservationIndex, mAggregationId);
        startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_ISSUING_RECEIPT);
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
        if (getActivity() == null || mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null || lock() == true)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        Intent intent = ZoomMapActivity.newInstance(getActivity()//
            , ZoomMapActivity.SourceType.GOURMET_BOOKING, restaurantInfo.name, restaurantInfo.address//
            , restaurantInfo.latitude, restaurantInfo.longitude, false);

        startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_ZOOMMAP);

        mAnalytics.onEventMapClick(getActivity());
    }

    @Override
    public void onExpandMapClick()
    {
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null || lock() == true)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        getViewInterface().setBookingDetailMapToolbar();

        addCompositeDisposable(getViewInterface().expandMap(restaurantInfo.latitude, restaurantInfo.longitude)//
            .subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Boolean aBoolean) throws Exception
                {
                    unLockAll();
                }
            }));

        mAnalytics.onEventMapClick(getActivity());
    }

    @Override
    public void onCollapseMapClick()
    {
        if (lock() == true)
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
        if (getActivity() == null || mGourmetBookingDetail == null //
            || mGourmetBookingDetail.restaurantInfo == null || lock() == true)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        try
        {
            Intent intent = GourmetDetailActivity.newInstance(getActivity() //
                , restaurantInfo.index, restaurantInfo.name, null, GourmetDetailActivity.NONE_PRICE//
                , mCommonDateTime.dailyDateTime, null, false, false, false, false//
                , GourmetDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE//
                , new GourmetDetailAnalyticsParam());

            startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_DETAIL);

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
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null || lock() == true)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();
        analyticsParam.category = AnalyticsManager.Category.GOURMET_BOOKINGS;
        analyticsParam.action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED;

        startActivityForResult(NavigatorDialogActivity.newInstance(getActivity(), restaurantInfo.name//
            , restaurantInfo.latitude, restaurantInfo.longitude, false, analyticsParam), GourmetBookingDetailActivity.REQUEST_CODE_NAVIGATOR);
    }

    @Override
    public void onClipAddressClick()
    {
        if (getActivity() == null || mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null)
        {
            return;
        }

        DailyTextUtils.clipText(getActivity(), mGourmetBookingDetail.restaurantInfo.address);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_address, Toast.LENGTH_SHORT);
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
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null)
        {
            restaurantPhone = null;
        } else if (DailyTextUtils.isTextEmpty(mGourmetBookingDetail.restaurantInfo.phoneNumber) == false)
        {
            restaurantPhone = mGourmetBookingDetail.restaurantInfo.phoneNumber;
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

        mAnalytics.onEventConciergeFaqClick(getActivity());
    }

    @Override
    public void onRestaurantCallClick(String restaurantPhone)
    {
        if (DailyTextUtils.isTextEmpty(restaurantPhone) == true)
        {
            return;
        }

        startActivityForResult(RestaurantCallDialogActivity.newInstance(getActivity(), restaurantPhone) //
            , GourmetBookingDetailActivity.REQUEST_CODE_RESTAURANT_CALL);
    }

    @Override
    public void onConciergeHappyTalkClick()
    {
        if (getActivity() == null || mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity()//
                , HappyTalkCategoryDialog.CallScreen.SCREEN_GOURMET_BOOKING//
                , restaurantInfo.index, mReservationIndex, restaurantInfo.name), GourmetBookingDetailActivity.REQUEST_CODE_HAPPYTALK);

            mAnalytics.onEventHappyTalkClick2(getActivity());
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

        mAnalytics.onEventHappyTalkClick(getActivity());
    }

    @Override
    public void onConciergeCallClick()
    {
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), GourmetBookingDetailActivity.REQUEST_CODE_CALL);

        mAnalytics.onEventConciergeCallClick(getActivity());
    }

    @Override
    public void onShareKakaoClick()
    {
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null //
            || mGourmetBookingDetail.paymentInfo == null || mGourmetBookingDetail.ticketInfos == null //
            || mGourmetBookingDetail.guestInfo == null || getActivity() == null)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;
        PaymentInfo paymentInfo = mGourmetBookingDetail.paymentInfo;
        GuestInfo guestInfo = mGourmetBookingDetail.guestInfo;

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            screenLock(true);

            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            String urlFormat = "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s&utm_source=share&utm_medium=gourmet_booking_kakaotalk";
            String longUrl = String.format(Locale.KOREA, urlFormat, restaurantInfo.index //
                , DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(String shortUrl) throws Exception
                {
                    unLockAll();

                    KakaoLinkManager.newInstance(getActivity()).shareBookingGourmet(userName //
                        , restaurantInfo.name //
                        , restaurantInfo.address //
                        , restaurantInfo.index //
                        , mImageUrl //
                        , shortUrl //
                        , DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    unLockAll();

                    KakaoLinkManager.newInstance(getActivity()).shareBookingGourmet(userName //
                        , restaurantInfo.name //
                        , restaurantInfo.address //
                        , restaurantInfo.index //
                        , mImageUrl //
                        , "https://mobile.dailyhotel.co.kr/gourmet/" + restaurantInfo.index //
                        , DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyyMMdd"));
                }
            }));

            mAnalytics.onEventShareKakaoClick(getActivity());
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            unLockAll();

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
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null //
            || mGourmetBookingDetail.paymentInfo == null || mGourmetBookingDetail.ticketInfos == null //
            || mGourmetBookingDetail.guestInfo == null || getActivity() == null)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;
        PaymentInfo paymentInfo = mGourmetBookingDetail.paymentInfo;
        GuestInfo guestInfo = mGourmetBookingDetail.guestInfo;

        try
        {
            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            String firstTicketName = "";
            int totalTicketCount = 0;
            int ticketSize = mGourmetBookingDetail.ticketInfos.size();

            for (TicketInfo ticketInfo : mGourmetBookingDetail.ticketInfos)
            {
                if (DailyTextUtils.isTextEmpty(firstTicketName) == true)
                {
                    firstTicketName = ticketInfo.name;
                }

                totalTicketCount += ticketInfo.count;
            }

            String ticketName;
            if (ticketSize > 1)
            {
                ticketName = getString(R.string.message_multi_ticket_name_n_count, firstTicketName, ticketSize - 1);
            } else
            {
                ticketName = firstTicketName;
            }

            String longUrl = String.format(Locale.KOREA, "https://mobile.dailyhotel.co.kr/gourmet/%d?reserveDate=%s&utm_source=share&utm_medium=gourmet_booking_moretab"//
                , restaurantInfo.index //
                , DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy-MM-dd"));

            final String message = getString(R.string.message_booking_gourmet_share_sms, //
                userName, restaurantInfo.name, guestInfo.name,//
                DailyTextUtils.getPriceFormat(getActivity(), paymentInfo.paymentAmount, false), //
                DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)"),//
                DailyCalendar.convertDateFormatString(guestInfo.arrivalDateTime, DailyCalendar.ISO_8601_FORMAT, "HH:mm"), //
                ticketName, getString(R.string.label_booking_count, totalTicketCount), //
                restaurantInfo.address);

            CommonRemoteImpl commonRemote = new CommonRemoteImpl(getActivity());

            addCompositeDisposable(commonRemote.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + shortUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + "https://mobile.dailyhotel.co.kr/gourmet/" + mGourmetBookingDetail.restaurantInfo.index);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }));

            mAnalytics.onEventMoreShareClick(getActivity());
        } catch (Exception e)
        {
            unLockAll();

            ExLog.d(e.toString());
        }
    }

    @Override
    public void onHiddenReservationClick()
    {
        if (mGourmetBookingDetail == null || mGourmetBookingDetail.restaurantInfo == null || lock() == true)
        {
            return;
        }

        RestaurantInfo restaurantInfo = mGourmetBookingDetail.restaurantInfo;

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking)//
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    screenLock(true);

                    Observable<Boolean> hiddenObservable = Observable.defer(new Callable<ObservableSource<? extends Boolean>>()
                    {
                        @Override
                        public ObservableSource<? extends Boolean> call() throws Exception
                        {
                            if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                            {
                                return mBookingRemoteImpl.getGourmetHiddenBooking(mReservationIndex);
                            }

                            return mBookingRemoteImpl.getGourmetHiddenBooking(mAggregationId);
                        }
                    });

                    addCompositeDisposable(hiddenObservable //
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

                                    mAnalytics.onEventHideBookingSuccess(getActivity(), restaurantInfo.index);
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

        mAnalytics.onEventHideBookingClick(getActivity(), restaurantInfo.index);
    }

    @Override
    public void onReviewClick(String reviewStatus)
    {
        if (lock() == true)
        {
            return;
        }

        if (PlaceBookingDetail.ReviewStatusType.ADDABLE.equalsIgnoreCase(reviewStatus) == true //
            || PlaceBookingDetail.ReviewStatusType.MODIFIABLE.equalsIgnoreCase(reviewStatus) == true)
        {
            addCompositeDisposable(mReviewRemoteImpl.getGourmetReview(mReservationIndex) //
                .subscribeOn(Schedulers.io()).map(new Function<Review, com.twoheart.dailyhotel.model.Review>()
                {
                    @Override
                    public com.twoheart.dailyhotel.model.Review apply(@io.reactivex.annotations.NonNull Review review) throws Exception
                    {
                        return reviewToReviewParcelable(review);
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<com.twoheart.dailyhotel.model.Review>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull com.twoheart.dailyhotel.model.Review review) throws Exception
                    {
                        Intent intent = ReviewActivity.newInstance(getActivity(), review, reviewStatus);
                        startActivityForResult(intent, GourmetBookingDetailActivity.REQUEST_CODE_REVIEW);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                    {
                        onHandleError(throwable);
                    }
                }));

            mAnalytics.onEventReviewClick(getActivity());
        }
    }

    com.twoheart.dailyhotel.model.Review reviewToReviewParcelable(Review review)
    {
        com.twoheart.dailyhotel.model.Review reviewParcelable = new com.twoheart.dailyhotel.model.Review();

        if (review == null)
        {
            return reviewParcelable;
        }

        reviewParcelable.requiredCommentReview = review.requiredCommentReview;
        reviewParcelable.reserveIdx = review.reserveIdx;

        com.twoheart.dailyhotel.model.ReviewItem reviewItemParcelable = new com.twoheart.dailyhotel.model.ReviewItem();

        ReviewItem reviewItem = review.getReviewItem();

        if (reviewItem != null)
        {
            reviewItemParcelable.itemIdx = reviewItem.itemIdx;
            reviewItemParcelable.itemName = reviewItem.itemName;
            reviewItemParcelable.setImageMap(reviewItem.getImageMap());

            switch (reviewItem.serviceType)
            {
                case "HOTEL":
                    reviewItemParcelable.serviceType = Constants.ServiceType.HOTEL;
                    break;

                case "GOURMET":
                    reviewItemParcelable.serviceType = Constants.ServiceType.GOURMET;
                    break;

                case "OUTBOUND":
                    reviewItemParcelable.serviceType = Constants.ServiceType.OB_STAY;
                    break;

                default:
                    ExLog.d("unKnown service type");
                    break;
            }

            reviewItemParcelable.useEndDate = reviewItem.useEndDate;
            reviewItemParcelable.useStartDate = reviewItem.useStartDate;
        }

        reviewParcelable.setReviewItem(reviewItemParcelable);

        //
        ArrayList<ReviewPickQuestion> reviewPickQuestionListParcelable = new ArrayList<>();

        List<ReviewQuestionItem> reviewPickQuestionList = review.getReviewPickQuestionList();

        if (reviewPickQuestionList != null && reviewPickQuestionList.size() > 0)
        {
            for (ReviewQuestionItem reviewQuestionItem : reviewPickQuestionList)
            {
                ReviewPickQuestion reviewPickQuestion = new ReviewPickQuestion();
                reviewPickQuestion.title = reviewQuestionItem.title;
                reviewPickQuestion.description = reviewQuestionItem.description;
                reviewPickQuestion.answerCode = reviewQuestionItem.answerCode;

                //
                ArrayList<com.twoheart.dailyhotel.model.ReviewAnswerValue> reviewAnswerValueListParcelable = new ArrayList<>();

                List<ReviewAnswerValue> reviewAnswerValueList = reviewQuestionItem.getAnswerValueList();

                if (reviewAnswerValueList != null && reviewAnswerValueList.size() > 0)
                {
                    for (ReviewAnswerValue reviewAnswerValue : reviewAnswerValueList)
                    {
                        com.twoheart.dailyhotel.model.ReviewAnswerValue reviewAnswerValueParcelable = new com.twoheart.dailyhotel.model.ReviewAnswerValue();

                        reviewAnswerValueParcelable.code = reviewAnswerValue.code;
                        reviewAnswerValueParcelable.description = reviewAnswerValue.description;

                        reviewAnswerValueListParcelable.add(reviewAnswerValueParcelable);
                    }

                    // 짝수개로 맞춘다.
                    if (reviewAnswerValueListParcelable.size() % 2 == 1)
                    {
                        reviewAnswerValueListParcelable.add(new com.twoheart.dailyhotel.model.ReviewAnswerValue());
                    }
                }

                reviewPickQuestion.setAnswerValueList(reviewAnswerValueListParcelable);
                reviewPickQuestionListParcelable.add(reviewPickQuestion);
            }
        }

        reviewParcelable.setReviewPickQuestionList(reviewPickQuestionListParcelable);

        //
        ArrayList<ReviewScoreQuestion> reviewScoreQuestionListParcelable = new ArrayList<>();

        List<ReviewQuestionItem> reviewScoreQuestionList = review.getReviewScoreQuestionList();

        if (reviewScoreQuestionList != null && reviewScoreQuestionList.size() > 0)
        {
            for (ReviewQuestionItem reviewQuestionItem : reviewScoreQuestionList)
            {
                ReviewScoreQuestion reviewScoreQuestion = new ReviewScoreQuestion();
                reviewScoreQuestion.title = reviewQuestionItem.title;
                reviewScoreQuestion.description = reviewQuestionItem.description;
                reviewScoreQuestion.answerCode = reviewQuestionItem.answerCode;

                reviewScoreQuestionListParcelable.add(reviewScoreQuestion);
            }
        }

        reviewParcelable.setReviewScoreQuestionList(reviewScoreQuestionListParcelable);

        return reviewParcelable;
    }
}
