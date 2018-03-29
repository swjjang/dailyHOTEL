package com.daily.dailyhotel.screen.booking.cancel.detail.stay;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.DuplicateRunException;
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.StayBookDateTime;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.parcel.analytics.NavigatorAnalyticsParam;
import com.daily.dailyhotel.parcel.analytics.StayDetailAnalyticsParam;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.call.CallDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.navigator.NavigatorDialogActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.google.android.gms.common.api.ResolvableApiException;
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
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.Date;
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
public class StayBookingCancelDetailPresenter //
    extends BaseExceptionPresenter<StayBookingCancelDetailActivity, StayBookingCancelDetailInterface> //
    implements StayBookingCancelDetailView.OnEventListener
{
    StayBookingCancelAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    BookingRemoteImpl mBookingRemoteImpl;

    int mReservationIndex;
    String mAggregationId;
    private String mImageUrl;

    private CommonDateTime mCommonDateTime;
    private StayBookingDetail mStayBookingDetail;

    DailyLocationExFactory mDailyLocationExFactory;

    public interface StayBookingCancelAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity);

        void onEventShareClick(Activity activity);

        void onEventShareKakaoClick(Activity activity);

        void onEventMoreShareClick(Activity activity);

        void onEventConciergeClick(Activity activity);

        void onEventViewDetailClick(Activity activity);

        void onEventNavigatorClick(Activity activity);

        void onEventHideBookingCancelClick(Activity activity);

        StayDetailAnalyticsParam getDetailAnalyticsParam(StayBookingDetail stayBookingDetail);
    }

    public StayBookingCancelDetailPresenter(@NonNull StayBookingCancelDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayBookingCancelDetailInterface createInstanceViewInterface()
    {
        return new StayBookingCancelDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayBookingCancelDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_booking_cancel_detail_data);

        setAnalytics(new StayBookingCancelDetailCancelAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mBookingRemoteImpl = new BookingRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayBookingCancelAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mReservationIndex = intent.getIntExtra(StayBookingCancelDetailActivity.INTENT_EXTRA_DATA_BOOKING_INDEX, -1);
        mAggregationId = intent.getStringExtra(StayBookingCancelDetailActivity.INTENT_EXTRA_DATA_AGGREGATION_ID);
        mImageUrl = intent.getStringExtra(StayBookingCancelDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);

        if (mReservationIndex <= 0)
        {
            Util.restartApp(getActivity());
            return false;
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
        if (mStayBookingDetail != null && getViewInterface().isExpandedMap() == true)
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
            case StayBookingCancelDetailActivity.REQUEST_CODE_DETAIL:
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

            case StayBookingCancelDetailActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case StayBookingCancelDetailActivity.REQUEST_CODE_SETTING_LOCATION:
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

        Observable<StayBookingDetail> detailObservable = Observable.defer(new Callable<ObservableSource<StayBookingDetail>>()
        {
            @Override
            public ObservableSource<StayBookingDetail> call() throws Exception
            {
                if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                {
                    return mBookingRemoteImpl.getStayBookingDetail(mReservationIndex);
                }

                return mBookingRemoteImpl.getStayBookingDetail(mAggregationId);
            }
        });

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), detailObservable, new BiFunction<CommonDateTime, StayBookingDetail, StayBookingDetail>()
        {
            @Override
            public StayBookingDetail apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime //
                , @io.reactivex.annotations.NonNull StayBookingDetail stayBookingDetail) throws Exception
            {
                setCommonDateTime(commonDateTime);
                setStayBookingDetail(stayBookingDetail);

                return stayBookingDetail;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<StayBookingDetail>()
        {
            @Override
            public void accept(StayBookingDetail stayBookingDetail) throws Exception
            {
                notifyStayBookingDetailChanged();

                unLockAll();

                //                mAnalytics.onScreen(getActivity(), Booking.BOOKING_STATE_CANCEL, mStayBookingDetail.stayIndex, mStayBookingDetail.refundStatus);
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
        if (mStayBookingDetail == null || lock() == true)
        {
            return;
        }

        Intent intent = ZoomMapActivity.newInstance(getActivity()//
            , ZoomMapActivity.SourceType.HOTEL_BOOKING, mStayBookingDetail.stayName, mStayBookingDetail.stayAddress//
            , mStayBookingDetail.latitude, mStayBookingDetail.longitude, mStayBookingDetail.overseas);

        startActivityForResult(intent, StayBookingCancelDetailActivity.REQUEST_CODE_ZOOMMAP);
        //
        //        AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
        //            , AnalyticsManager.Action.MAP_CLICK, AnalyticsManager.ValueType.EMPTY, null);
    }

    @Override
    public void onExpandMapClick()
    {
        if (mStayBookingDetail == null || lock() == true)
        {
            return;
        }

        getViewInterface().setBookingDetailMapToolbar();

        addCompositeDisposable(getViewInterface().expandMap(mStayBookingDetail.latitude, mStayBookingDetail.longitude)//
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
        if (mStayBookingDetail == null || lock() == true)
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
        if (mStayBookingDetail == null || lock() == true)
        {
            return;
        }

        try
        {
            StayBookDateTime stayBookDateTime = new StayBookDateTime();
            stayBookDateTime.setCheckInDateTime(mCommonDateTime.dailyDateTime);
            stayBookDateTime.setCheckOutDateTime(mCommonDateTime.dailyDateTime, 1);

            StayDetailAnalyticsParam analyticsParam = mAnalytics.getDetailAnalyticsParam(mStayBookingDetail);

            Intent intent = StayDetailActivity.newInstance(getActivity() //
                , mStayBookingDetail.stayIndex, mStayBookingDetail.stayName, null, StayDetailActivity.NONE_PRICE//
                , stayBookDateTime.getCheckInDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , stayBookDateTime.getCheckOutDateTime(DailyCalendar.ISO_8601_FORMAT)//
                , false, StayDetailActivity.TRANS_GRADIENT_BOTTOM_TYPE_NONE, analyticsParam);

            startActivityForResult(intent, StayBookingCancelDetailActivity.REQUEST_CODE_DETAIL);

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
        if (mStayBookingDetail == null || lock() == true)
        {
            return;
        }

        NavigatorAnalyticsParam analyticsParam = new NavigatorAnalyticsParam();
        analyticsParam.category = AnalyticsManager.Category.HOTEL_BOOKINGS;
        analyticsParam.action = AnalyticsManager.Action.HOTEL_DETAIL_NAVIGATION_APP_CLICKED;

        startActivityForResult(NavigatorDialogActivity.newInstance(getActivity(), mStayBookingDetail.stayName//
            , mStayBookingDetail.latitude, mStayBookingDetail.longitude, false, analyticsParam), StayBookingCancelDetailActivity.REQUEST_CODE_NAVIGATOR);

        mAnalytics.onEventNavigatorClick(getActivity());
    }

    @Override
    public void onClipAddressClick()
    {
        if (mStayBookingDetail == null)
        {
            return;
        }

        DailyTextUtils.clipText(getActivity(), mStayBookingDetail.stayAddress);

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
        getViewInterface().showConciergeDialog(new DialogInterface.OnDismissListener()
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
    public void onConciergeHappyTalkClick()
    {
        if (mStayBookingDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_BOOKING_CANCEL//
                , mStayBookingDetail.stayIndex, mReservationIndex, mStayBookingDetail.stayName), StayBookingCancelDetailActivity.REQUEST_CODE_HAPPYTALK);
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
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayBookingCancelDetailActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onShareKakaoClick()
    {
        if (mStayBookingDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            String[] checkInDates = mStayBookingDetail.checkInDateTime.split("T");
            String[] checkOutDates = mStayBookingDetail.checkOutDateTime.split("T");

            Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);

            String checkInDay = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");
            String checkOutDay = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd");

            KakaoLinkManager.newInstance(getActivity()).shareBookingCancelStay(userName //
                , mStayBookingDetail.stayName //
                , mStayBookingDetail.stayAddress //
                , mImageUrl //
                , checkInDay //
                , checkOutDay //
                , nights);

            mAnalytics.onEventShareKakaoClick(getActivity());

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
        if (mStayBookingDetail == null)
        {
            return;
        }

        try
        {
            String userName = DailyUserPreference.getInstance(getActivity()).getName();

            final String message = getString(R.string.message_booking_cancel_stay_share_sms, userName //
                , mStayBookingDetail.stayName, mStayBookingDetail.guestName, mStayBookingDetail.roomName //
                , DailyCalendar.convertDateFormatString(mStayBookingDetail.cancelDateTime, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd(EEE)") //
                , mStayBookingDetail.stayAddress);

            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/plain");

            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            intent.putExtra(Intent.EXTRA_TEXT, message);
            Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
            startActivity(chooser);

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
        if (mStayBookingDetail == null || lock() == true)
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

                    addCompositeDisposable(mBookingRemoteImpl.getStayHiddenBooking(mReservationIndex) //
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

    void setStayBookingDetail(StayBookingDetail stayBookingDetail)
    {
        mStayBookingDetail = stayBookingDetail;
    }

    void notifyStayBookingDetailChanged()
    {
        if (mStayBookingDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";
        final String TIME_FORMAT = "HH:mm";

        try
        {
            String checkInTime = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkInDateString = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkInDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDateString + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkInDateString.length(), checkInDateString.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, TIME_FORMAT);
            String checkOutDateString = DailyCalendar.convertDateFormatString(mStayBookingDetail.checkOutDateTime, DailyCalendar.ISO_8601_FORMAT, DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDateString + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getMediumTypeface()),//
                checkOutDateString.length(), checkOutDateString.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 날짜로 계산한다. 서버에 체크인시간, 체크아웃시간이 잘못 기록되어있는 경우 발생해서 예외 처리 추가
            String[] checkInDates = mStayBookingDetail.checkInDateTime.split("T");
            String[] checkOutDates = mStayBookingDetail.checkOutDateTime.split("T");

            Date checkInDate = DailyCalendar.convertDate(checkInDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);
            Date checkOutDate = DailyCalendar.convertDate(checkOutDates[0] + "T00:00:00+09:00", DailyCalendar.ISO_8601_FORMAT);

            int nights = (int) ((DailyCalendar.clearTField(checkOutDate.getTime()) - DailyCalendar.clearTField(checkInDate.getTime())) / DailyCalendar.DAY_MILLISECOND);

            getViewInterface().setBookingDate(checkInDateSpannableString, checkOutDateSpannableString, nights);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setBookingDetail(mStayBookingDetail);
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
                startActivityForResult(intent, StayBookingCancelDetailActivity.REQUEST_CODE_PERMISSION_MANAGER);
            } else if (throwable instanceof ProviderException)
            {
                // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                View.OnClickListener positiveListener = new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, StayBookingCancelDetailActivity.REQUEST_CODE_SETTING_LOCATION);
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
                    ((ResolvableApiException) throwable).startResolutionForResult(getActivity(), StayBookingCancelDetailActivity.REQUEST_CODE_SETTING_LOCATION);
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
}
