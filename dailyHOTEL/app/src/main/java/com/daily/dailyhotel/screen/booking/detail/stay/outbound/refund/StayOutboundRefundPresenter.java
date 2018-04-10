package com.daily.dailyhotel.screen.booking.detail.stay.outbound.refund;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.FontManager;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.StayOutboundBookingDetail;
import com.daily.dailyhotel.entity.StayOutboundRefundDetail;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.widget.CustomFontTypefaceSpan;

import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayOutboundRefundPresenter //
    extends BaseExceptionPresenter<StayOutboundRefundActivity, StayOutboundRefundInterface> //
    implements StayOutboundRefundView.OnEventListener
{
    StayOutboundRefundAnalyticsInterface mAnalytics;

    RefundRemoteImpl mRefundRemoteImpl;

    int mBookingIndex;
    private String mTitle;
    StayOutboundRefundDetail mStayOutboundRefundDetail;

    String mCancelKey;
    String mCancelMessage;

    public interface StayOutboundRefundAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onCompletedRefund(Activity activity, String stayName, StayOutboundBookingDetail.RefundType refundType, String cancelMessage);
    }

    public StayOutboundRefundPresenter(@NonNull StayOutboundRefundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayOutboundRefundInterface createInstanceViewInterface()
    {
        return new StayOutboundRefundView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayOutboundRefundActivity activity)
    {
        setContentView(R.layout.activity_stay_outbound_refund_data);

        mAnalytics = new StayOutboundRefundAnalyticsImpl();

        mRefundRemoteImpl = new RefundRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mBookingIndex = intent.getIntExtra(StayOutboundRefundActivity.INTENT_EXTRA_DATA_BOOKING_INDEX, -1);

        if (mBookingIndex < 0)
        {
            return false;
        }

        mTitle = intent.getStringExtra(StayOutboundRefundActivity.INTENT_EXTRA_DATA_TITLE);

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitle);
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
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);

        addCompositeDisposable(mRefundRemoteImpl.getStayOutboundRefundDetail(getActivity(), mBookingIndex).subscribe(new Consumer<StayOutboundRefundDetail>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull StayOutboundRefundDetail stayOutboundRefundDetail) throws Exception
            {
                setStayOutboundRefundDetail(stayOutboundRefundDetail);
                notifyStayOutboundBookingDetailChanged();

                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
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
    public void onRefundClick()
    {
        if (lock() == true)
        {
            return;
        }

        if (DailyTextUtils.isTextEmpty(mCancelKey) == true)
        {
            DailyToast.showToast(getActivity(), getString(R.string.message_stay_outbound_refund_select_a_reason_for_cancellation), DailyToast.LENGTH_SHORT);
            unLockAll();
            return;
        }

        switch (mStayOutboundRefundDetail.refundStatus)
        {
            case FULL:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_stay_outbound_refund_cancel_free)//
                    , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            screenLock(true);

                            addCompositeDisposable(mRefundRemoteImpl.getStayOutboundRefund(getActivity(), mBookingIndex, mStayOutboundRefundDetail.refundStatus.getValue(), mCancelKey, mCancelMessage).subscribe(new Consumer<String>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull String message) throws Exception
                                {
                                    getViewInterface().showSimpleDialog(null, message//
                                        , getString(R.string.dialog_btn_text_yes), null, new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                setResult(Activity.RESULT_OK);
                                                onBackClick();

                                                mAnalytics.onCompletedRefund(getActivity(), mStayOutboundRefundDetail.name, StayOutboundBookingDetail.RefundType.FULL, mCancelMessage);
                                            }
                                        });

                                    unLockAll();
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                                {
                                    unLockAll();

                                    if (throwable instanceof BaseException)
                                    {
                                        onRefundError((BaseException) throwable);
                                    } else
                                    {
                                        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                            {
                                                @Override
                                                public void onDismiss(DialogInterface dialog)
                                                {
                                                }
                                            });
                                    }
                                }
                            }));
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            unLockAll();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            unLockAll();
                        }
                    }, null, true);
                break;

            case PARTIAL:
                getViewInterface().showSimpleDialog(null, getString(R.string.message_stay_outbound_refund_partial_refund)//
                    , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            screenLock(true);

                            addCompositeDisposable(mRefundRemoteImpl.getStayOutboundRefund(getActivity(), mBookingIndex, mStayOutboundRefundDetail.refundStatus.getValue(), mCancelKey, mCancelMessage).subscribe(new Consumer<String>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull String message) throws Exception
                                {
                                    getViewInterface().showSimpleDialog(null, message//
                                        , getString(R.string.dialog_btn_text_yes), null, new DialogInterface.OnDismissListener()
                                        {
                                            @Override
                                            public void onDismiss(DialogInterface dialog)
                                            {
                                                setResult(Activity.RESULT_OK);
                                                onBackClick();

                                                mAnalytics.onCompletedRefund(getActivity(), mStayOutboundRefundDetail.name, StayOutboundBookingDetail.RefundType.PARTIAL, mCancelMessage);
                                            }
                                        });

                                    unLockAll();
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                                {
                                    unLockAll();

                                    if (throwable instanceof BaseException)
                                    {
                                        onRefundError((BaseException) throwable);
                                    } else
                                    {
                                        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                                            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                                            {
                                                @Override
                                                public void onDismiss(DialogInterface dialog)
                                                {
                                                    onBackClick();
                                                }
                                            });
                                    }
                                }
                            }));
                        }
                    }, new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            unLockAll();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            unLockAll();
                        }
                    }, null, true);
                break;
        }
    }

    @Override
    public void onCancelReasonClick()
    {
        getViewInterface().showCancelReasonListDialog(mStayOutboundRefundDetail.getCancelReasonTypeList(), mCancelKey, mCancelMessage);
    }

    @Override
    public void onCancelReasonClick(String key, String reasonText, String message)
    {
        if (DailyTextUtils.isTextEmpty(key, reasonText) == true)
        {
            return;
        }

        mCancelKey = key;
        mCancelMessage = message;

        getViewInterface().setCancelReasonText(reasonText);
    }

    void setStayOutboundRefundDetail(StayOutboundRefundDetail stayOutboundRefundDetail)
    {
        mStayOutboundRefundDetail = stayOutboundRefundDetail;
    }

    void notifyStayOutboundBookingDetailChanged()
    {
        if (mStayOutboundRefundDetail == null)
        {
            return;
        }

        final String DATE_FORMAT = "yyyy.MM.dd(EEE)";

        try
        {
            String checkInTime = getString(R.string.label_stay_outbound_payment_hour, mStayOutboundRefundDetail.checkInTime.split(":")[0]);
            String checkInDate = DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkInDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkInDateSpannableString = new SpannableString(checkInDate + " " + checkInTime);
            checkInDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkInDate.length(), checkInDate.length() + checkInTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String checkOutTime = getString(R.string.label_stay_outbound_payment_hour, mStayOutboundRefundDetail.checkOutTime.split(":")[0]);
            String checkOutDate = DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkOutDate, "yyyy-MM-dd", DATE_FORMAT);

            SpannableString checkOutDateSpannableString = new SpannableString(checkOutDate + " " + checkOutTime);
            checkOutDateSpannableString.setSpan( //
                new CustomFontTypefaceSpan(FontManager.getInstance(getActivity()).getBoldTypeface()),//
                checkOutDate.length(), checkOutDate.length() + checkOutTime.length() + 1,//
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int nights = DailyCalendar.compareDateDay(DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkOutDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT)//
                , DailyCalendar.convertDateFormatString(mStayOutboundRefundDetail.checkInDate, "yyyy-MM-dd", DailyCalendar.ISO_8601_FORMAT));
            getViewInterface().setBookingDate(checkInDateSpannableString, checkOutDateSpannableString, nights);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        getViewInterface().setRefundDetail(mStayOutboundRefundDetail);
    }

    void onRefundError(BaseException baseException)
    {
        unLockAll();

        if (baseException == null)
        {
            getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.act_base_network_connect)//
                , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {

                    }
                });

            return;
        }

        String message = baseException.getMessage();

        switch (baseException.getCode())
        {

        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), message//
            , getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {

                }
            });
    }
}
