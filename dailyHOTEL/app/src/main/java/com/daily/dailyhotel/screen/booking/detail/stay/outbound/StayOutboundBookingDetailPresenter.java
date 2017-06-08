package com.daily.dailyhotel.screen.booking.detail.stay.outbound;


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
import com.daily.base.exception.PermissionException;
import com.daily.base.exception.ProviderException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.People;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.repository.remote.BookingRemoteImpl;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.common.call.CallDialogActivity;
import com.daily.dailyhotel.screen.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.util.DailyLocationExFactory;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.booking.detail.hotel.IssuingReceiptActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundBookingDetailPresenter extends BaseExceptionPresenter<StayOutboundBookingDetailActivity, StayOutboundBookingDetailInterface> implements StayOutboundBookingDetailView.OnEventListener
{
    private StayOutboundBookingAnalyticsInterface mAnalytics;

    private CommonRemoteImpl mCommonRemoteImpl;
    private BookingRemoteImpl mBookingRemoteImpl;

    private int mReservationIndex;
    private String mImageUrl;

    private CommonDateTime mCommonDateTime;
    private StayOutboundBookingDetail mStayOutboundBookingDetail;

    private DailyLocationExFactory mDailyLocationExFactory;

    public interface StayOutboundBookingAnalyticsInterface extends BaseAnalyticsInterface
    {
    }

    public StayOutboundBookingDetailPresenter(@NonNull StayOutboundBookingDetailActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundBookingDetailInterface createInstanceViewInterface()
    {
        return new StayOutboundBookingDetailView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundBookingDetailActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_booking_detail_data);

        setAnalytics(new StayOutboundBookingDetailAnalyticsImpl());

        mCommonRemoteImpl = new CommonRemoteImpl(activity);
        mBookingRemoteImpl = new BookingRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public void setAnalytics(BaseAnalyticsInterface analytics)
    {
        mAnalytics = (StayOutboundBookingAnalyticsInterface) analytics;
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mReservationIndex = intent.getIntExtra(StayOutboundBookingDetailActivity.INTENT_EXTRA_DATA_RESERVATION_INDEX, -1);
        mImageUrl = intent.getStringExtra(StayOutboundBookingDetailActivity.INTENT_EXTRA_DATA_IMAGE_URL);

        return true;
    }

    @Override
    public void onPostCreate()
    {

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
            case StayOutboundBookingDetailActivity.REQUEST_CODE_PERMISSION_MANAGER:
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

            case StayOutboundBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION:
                onMyLocationClick();
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

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(Observable.zip(mCommonRemoteImpl.getCommonDateTime(), mBookingRemoteImpl.getStayOutboundBookingDetail(mReservationIndex)//
            , new BiFunction<CommonDateTime, StayOutboundBookingDetail, StayOutboundBookingDetail>()
            {

                @Override
                public StayOutboundBookingDetail apply(@io.reactivex.annotations.NonNull CommonDateTime commonDateTime, @io.reactivex.annotations.NonNull StayOutboundBookingDetail stayOutboundBookingDetail) throws Exception
                {
                    setCommonDateTime(commonDateTime);
                    setStayOutboundBookingDetail(stayOutboundBookingDetail);
                    return stayOutboundBookingDetail;
                }
            }).subscribe(new Consumer<StayOutboundBookingDetail>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundBookingDetail stayOutboundBookingDetail) throws Exception
            {
                onStayOutboundBookingDetail(stayOutboundBookingDetail);
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
            {
                // 에러가 나는 경우 리스트로 복귀
                onHandleError(throwable);
                onBackClick();
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
    }

    @Override
    public void onMapLoaded()
    {

    }

    @Override
    public void onMapLoading()
    {
        DailyToast.showToast(getActivity(), R.string.message_loading_map, DailyToast.LENGTH_SHORT);
    }

    @Override
    public void onMapClick(boolean isGoogleMap)
    {
        if (lock() == true || mStayOutboundBookingDetail == null)
        {
            return;
        }

        if (isGoogleMap == false)
        {
            Intent intent = ZoomMapActivity.newInstance(getActivity()//
                , ZoomMapActivity.SourceType.HOTEL_BOOKING, mStayOutboundBookingDetail.name, mStayOutboundBookingDetail.address//
                , mStayOutboundBookingDetail.latitude, mStayOutboundBookingDetail.longitude, true);

            startActivityForResult(intent, StayOutboundBookingDetailActivity.REQUEST_CODE_ZOOMMAP);
        } else
        {
            getViewInterface().expandMap(mStayOutboundBookingDetail.latitude, mStayOutboundBookingDetail.longitude);
        }
    }

    @Override
    public void onViewDetailClick()
    {
        if (lock() == true || mStayOutboundBookingDetail == null)
        {
            return;
        }

        try
        {
            String checkInDateTime = DailyCalendar.convertDateFormatString(mStayOutboundBookingDetail.checkInDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT);
            String checkOutDateTime = DailyCalendar.convertDateFormatString(mStayOutboundBookingDetail.checkOutDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT);

            People people = mStayOutboundBookingDetail.getPeople();

            startActivityForResult(StayOutboundDetailActivity.newInstance(getActivity(), mStayOutboundBookingDetail.stayIndex//
                , mStayOutboundBookingDetail.name, null, -1, checkInDateTime, checkOutDateTime//
                , people.numberOfAdults, people.getChildAgeList(), false, true), StayOutboundBookingDetailActivity.REQUEST_CODE_DETAIL);

            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    @Override
    public void onNavigatorClick()
    {
        if (getActivity().isFinishing() == true || lock() == true)
        {
            return;
        }

        getViewInterface().showNavigatorDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });
    }

    @Override
    public void onRefundClick()
    {
        if (lock() == true)
        {
            return;
        }

        switch (getRefundPolicyStatus(mStayOutboundBookingDetail))
        {
            case StayOutboundBookingDetail.STATUS_NO_CHARGE_REFUND:
            {
                //                Intent intent = StayAutoRefundActivity.newInstance(StayReservationDetailActivity.this, stayBookingDetail);
                //                startActivityForResult(intent, CODE_RESULT_ACTIVITY_STAY_AUTOREFUND);
                break;
            }

            default:
                getViewInterface().showConciergeDialog(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        unLockAll();
                    }
                });
                break;
        }
    }

    @Override
    public void onClipAddressClick()
    {
        if (mStayOutboundBookingDetail == null)
        {
            return;
        }

        DailyTextUtils.clipText(getActivity(), mStayOutboundBookingDetail.address);

        DailyToast.showToast(getActivity(), R.string.message_detail_copy_address, DailyToast.LENGTH_SHORT);
    }

    @Override
    public void onMyLocationClick()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(searchMyLocation().subscribe(new Consumer<Location>()
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
    }

    @Override
    public void onIssuingReceiptClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivityForResult(IssuingReceiptActivity.newInstance(getActivity(), mReservationIndex), StayOutboundBookingDetailActivity.REQUEST_CODE_ISSUING_RECEIPT);
    }

    @Override
    public void onShareMapClick()
    {
        Util.shareGoogleMap(getActivity(), mStayOutboundBookingDetail.name, Double.toString(mStayOutboundBookingDetail.latitude), Double.toString(mStayOutboundBookingDetail.longitude));
    }

    @Override
    public void onConciergeHappyTalkClick()
    {
        if (mStayOutboundBookingDetail == null)
        {
            return;
        }

        try
        {
            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_OUTBOUND_BOOKING//
                , mStayOutboundBookingDetail.stayIndex, 0, mStayOutboundBookingDetail.name), StayOutboundBookingDetailActivity.REQUEST_CODE_HAPPYTALK);
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
        startActivityForResult(CallDialogActivity.newInstance(getActivity()), StayOutboundBookingDetailActivity.REQUEST_CODE_CALL);
    }

    @Override
    public void onShareSmsClick()
    {
        if (mStayOutboundBookingDetail == null || lock() == true)
        {
            return;
        }

        try
        {
            String name = DailyUserPreference.getInstance(getActivity()).getName();

            if (DailyTextUtils.isTextEmpty(name) == true)
            {
                name = getString(R.string.label_friend) + "가";
            } else
            {
                name += "님이";
            }

            final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

            String checkInDate = DailyCalendar.convertDateFormatString(mStayOutboundBookingDetail.checkInDate, "yyyy-MM-dd", DATE_FORMAT);
            String checkOutDate = DailyCalendar.convertDateFormatString(mStayOutboundBookingDetail.checkOutDate, "yyyy-MM-dd", DATE_FORMAT);
            int nights = DailyCalendar.compareDateDay(checkOutDate, checkInDate);

            String message = getString(R.string.message_detail_stay_share_sms//
                , name, mStayOutboundBookingDetail.name//
                , checkInDate, checkOutDate//
                , nights, nights + 1 //
                , mStayOutboundBookingDetail.address);

            Util.sendSms(getActivity(), message);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void setCommonDateTime(@NonNull CommonDateTime commonDateTime)
    {
        if (commonDateTime == null)
        {
            return;
        }

        mCommonDateTime = commonDateTime;
    }

    private void setStayOutboundBookingDetail(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        mStayOutboundBookingDetail = stayOutboundBookingDetail;
    }

    private void onStayOutboundBookingDetail(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (stayOutboundBookingDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundBookingDetail.checkInTime.split(":")[0]);
            String checkInDate = DailyCalendar.convertDateFormatString(stayOutboundBookingDetail.checkInDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundBookingDetail.checkOutTime.split(":")[0]);
            String checkOutDate = DailyCalendar.convertDateFormatString(stayOutboundBookingDetail.checkOutDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int nights = DailyCalendar.compareDateDay(checkOutDate, checkInDate);
            getViewInterface().setBookingDate(checkInDateSpannableString, checkOutDateSpannableString, nights);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setBookingDetail(stayOutboundBookingDetail);
    }

    private String getRefundPolicyStatus(StayOutboundBookingDetail stayOutboundBookingDetail)
    {
        if (stayOutboundBookingDetail == null)
        {
            return StayOutboundBookingDetail.STATUS_NONE;
        }

        // 환불 대기 상태
        if (stayOutboundBookingDetail.readyForRefund == true)
        {
            return StayOutboundBookingDetail.STATUS_WAIT_REFUND;
        } else
        {
            if (DailyTextUtils.isTextEmpty(stayOutboundBookingDetail.refundPolicy) == false)
            {
                return stayOutboundBookingDetail.refundPolicy;
            } else
            {
                return stayOutboundBookingDetail.STATUS_SURCHARGE_REFUND;
            }
        }
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
                    startActivityForResult(intent, StayOutboundBookingDetailActivity.REQUEST_CODE_PERMISSION_MANAGER);
                } else if (throwable instanceof ProviderException)
                {
                    // 현재 GPS 설정이 꺼져있습니다 설정에서 바꾸어 주세요.
                    mDailyLocationExFactory.stopLocationMeasure();

                    View.OnClickListener positiveListener = new View.OnClickListener()//
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, StayOutboundBookingDetailActivity.REQUEST_CODE_SETTING_LOCATION);
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
