package com.daily.dailyhotel.screen.booking.detail.stayoutbound;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
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
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.screen.booking.detail.hotel.IssuingReceiptActivity;
import com.twoheart.dailyhotel.screen.common.HappyTalkCategoryDialog;
import com.twoheart.dailyhotel.screen.common.ZoomMapActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import java.util.Date;
import java.util.TimeZone;

import io.reactivex.Observable;
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

    }

    @Override
    public void onCallClick()
    {

    }

    @Override
    public void onMapLoaded()
    {

    }

    @Override
    public void onMapLoading()
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
    public void onSearchMapClick()
    {

    }

    @Override
    public void onMyLocationClick()
    {

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

            startActivityForResult(HappyTalkCategoryDialog.newInstance(getActivity(), HappyTalkCategoryDialog.CallScreen.SCREEN_STAY_OUTBOUND_DETAIL//
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
            Date checkInDate = DailyCalendar.convertDate(stayOutboundBookingDetail.checkInDate, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+09:00"));
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundBookingDetail.checkInTime.split(":")[0]);
            String checkInDateString = DailyCalendar.format(checkInDate, DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDateString + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDateString.length(), checkInDateString.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            Date checkOutDate = DailyCalendar.convertDate(stayOutboundBookingDetail.checkOutDate, "yyyy-MM-dd", TimeZone.getTimeZone("GMT+09:00"));
            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, stayOutboundBookingDetail.checkOutTime.split(":")[0]);
            String checkOutDateString = DailyCalendar.format(checkOutDate, DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDateString + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDateString.length(), checkOutDateString.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int nights = DailyCalendar.compareDateDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT), DailyCalendar.format(checkOutDate, DailyCalendar.ISO_8601_FORMAT));
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
}
