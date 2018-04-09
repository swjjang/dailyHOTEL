package com.daily.dailyhotel.screen.booking.detail.stay.refund;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.util.DailyTextUtils;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.Bank;
import com.daily.dailyhotel.entity.OldRefund;
import com.daily.dailyhotel.entity.StayBookingDetail;
import com.daily.dailyhotel.parcel.BankParcel;
import com.daily.dailyhotel.parcel.StayBookingDetailParcel;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.refund.AutoRefundDialogActivity;
import com.daily.dailyhotel.screen.common.dialog.refund.BankListDialogActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class StayAutoRefundPresenter extends BaseExceptionPresenter<StayAutoRefundActivity, StayAutoRefundInterface> implements StayAutoRefundView.OnEventListener
{
    StayAutoRefundAnalyticsInterface mAnalytics;

    RefundRemoteImpl mRefundRemoteImpl;

    StayBookingDetail mStayBookingDetail;
    String mAggregationId;
    int mSelectedCancelReason;
    String mCancelReasonMessage;
    Bank mSelectedBank;

    public interface StayAutoRefundAnalyticsInterface extends BaseAnalyticsInterface
    {
        void onRefundPositiveButtonClick(Activity activity, String roomName, int price, boolean isOverseas, String cancelMessage);
    }

    public StayAutoRefundPresenter(@NonNull StayAutoRefundActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected StayAutoRefundInterface createInstanceViewInterface()
    {
        return new StayAutoRefundView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(StayAutoRefundActivity activity)
    {
        setContentView(R.layout.activity_stay_auto_refund_data);

        mAnalytics = new StayAutoRefundAnalyticsImpl();

        mRefundRemoteImpl = new RefundRemoteImpl(activity);

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return false;
        }

        StayBookingDetailParcel stayBookingDetailParcel = intent.getParcelableExtra(StayAutoRefundActivity.INTENT_EXTRA_DATA_BOOKING_DETAIL);
        mStayBookingDetail = stayBookingDetailParcel == null ? null : stayBookingDetailParcel.getStayBookingDetail();

        mAggregationId = intent.getStringExtra(StayAutoRefundActivity.INTENT_EXTRA_DATA_AGGREGATION_ID);

        if (mStayBookingDetail == null)
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
        if (getViewInterface() == null)
        {
            return;
        }

        mSelectedCancelReason = -1;

        getViewInterface().setRefundButtonEnabled(false);
        getViewInterface().setPlaceBookingDetail(mStayBookingDetail);

        // 계좌 이체인 경우
        if (StayAutoRefundActivity.PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
        {
            getViewInterface().setAccountLayoutVisible(true);
        } else
        {
            getViewInterface().setAccountLayoutVisible(false);
        }
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

        switch (requestCode)
        {
            case StayAutoRefundActivity.REQUEST_CODE_SELECT_CANCEL_TYPE:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    mSelectedCancelReason = data.getIntExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_TYPE, -1);
                    mCancelReasonMessage = data.getStringExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_MESSAGE);

                    String cancelReasonText = data.getStringExtra(AutoRefundDialogActivity.INTENT_EXTRA_DATA_CANCEL_TYPE_NAME);

                    setCancelReasonResult(cancelReasonText);
                }
            }

            case StayAutoRefundActivity.REQUEST_CODE_SELECT_BANK_LIST:
            {
                if (resultCode == Activity.RESULT_OK && data != null)
                {
                    BankParcel bankParcel = data.getParcelableExtra(BankListDialogActivity.INTENT_EXTRA_DATA_SELECTED_BANK);

                    if (bankParcel != null)
                    {
                        setSelectedBankResult(bankParcel.getBank());
                    } else
                    {
                        setSelectedBankResult(null);
                    }
                }
            }
        }
    }

    private void setCancelReasonResult(String reason)
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setCancelReasonText(reason);
        getViewInterface().setRefundButtonEnabled(isEnabledRefund());
    }

    private void setSelectedBankResult(Bank bank)
    {
        if (getViewInterface() == null)
        {
            return;
        }

        mSelectedBank = bank;

        getViewInterface().setBankText(bank == null ? null : bank.name);
        getViewInterface().setRefundButtonEnabled(isEnabledRefund());
    }

    private boolean isEnabledRefund()
    {
        // 취소 사유 입력
        if (getViewInterface() == null || mSelectedCancelReason < 0)
        {
            return false;
        }

        if (StayAutoRefundActivity.PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
        {
            String accountNumber = getViewInterface().getAccountNumber();
            String accountName = getViewInterface().getAccountName();

            return mSelectedBank != null && DailyTextUtils.isTextEmpty(accountNumber, accountName) != true;
        }

        return true;
    }

    void onRefundResult(int msgCode, String message, boolean readyForRefund)
    {
        if (readyForRefund == true)
        {
            setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
            finish();
        } else
        {
            switch (msgCode)
            {
                case 1013:
                    getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                    break;

                case 1015:
                    getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            setResult(Constants.CODE_RESULT_ACTIVITY_REFRESH);
                            finish();
                        }
                    });
                    break;

                default:
                    getViewInterface().showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                    break;
            }
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
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void showSelectCancelDialog()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        Intent intent = AutoRefundDialogActivity.newInstance(getActivity(), mSelectedCancelReason, mCancelReasonMessage);
        startActivityForResult(intent, StayAutoRefundActivity.REQUEST_CODE_SELECT_CANCEL_TYPE);
    }

    @Override
    public void showSelectBankListDialog()
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        BankParcel bankParcel = null;
        if (mSelectedBank != null)
        {
            bankParcel = new BankParcel(mSelectedBank);
        }

        Intent intent = BankListDialogActivity.newInstance(getActivity(), bankParcel);
        startActivityForResult(intent, StayAutoRefundActivity.REQUEST_CODE_SELECT_BANK_LIST);
    }

    @Override
    public void onAccountTextWatcher(int length)
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().setRefundButtonEnabled(isEnabledRefund());
    }

    @Override
    public void onRefundClick()
    {
        if (getViewInterface() == null)
        {
            return;
        }

        getViewInterface().showSimpleDialog(null, getString(R.string.message_do_cancel_free), getString(R.string.dialog_btn_text_yes)//
            , getString(R.string.dialog_btn_text_no), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (getActivity() == null || getViewInterface() == null || lock() == true)
                    {
                        return;
                    }

                    screenLock(true);

                    // 서버전송시 메시지에 reasonText와 cancelMessage를 같이 보냄!
                    String cancelMessage;
                    if (mSelectedCancelReason == 7 && DailyTextUtils.isTextEmpty(mCancelReasonMessage) == false)
                    {
                        cancelMessage = getViewInterface().getCancelReasonText() + "-" + mCancelReasonMessage;
                    } else
                    {
                        cancelMessage = getViewInterface().getCancelReasonText();
                    }

                    if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                    {
                        String accountNumber = null;
                        String accountName = null;
                        String selectBankCode = null;

                        if (StayAutoRefundActivity.PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
                        {
                            accountNumber = getViewInterface().getAccountNumber();
                            accountName = getViewInterface().getAccountName();
                            selectBankCode = mSelectedBank.code;
                        }

                        addCompositeDisposable(mRefundRemoteImpl.getRefund( //
                            mStayBookingDetail.stayIndex, mStayBookingDetail.checkInDateTime //
                            , mStayBookingDetail.transactionType, mStayBookingDetail.reservationIndex //
                            , cancelMessage, accountName, accountNumber, selectBankCode) //
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<OldRefund>()
                            {
                                @Override
                                public void accept(OldRefund oldRefund) throws Exception
                                {
                                    unLockAll();
                                    onRefundResult(oldRefund.msgCode, oldRefund.messageFromPg, oldRefund.readyForRefund);
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(Throwable throwable) throws Exception
                                {
                                    onHandleError(throwable);
                                }
                            }));
                    } else
                    {
                        if (StayAutoRefundActivity.PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
                        {
                            String accountNumber = getViewInterface().getAccountNumber();
                            String accountName = getViewInterface().getAccountName();

                            addCompositeDisposable(mRefundRemoteImpl.getRefund(mAggregationId, mStayBookingDetail.reservationIndex//
                                , cancelMessage, "HOTEL", accountName, accountNumber, mSelectedBank.code).subscribe(new Consumer<String>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull String message) throws Exception
                                {
                                    unLockAll();

                                    onRefundResult(100, message, false);
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                                {
                                    onHandleError(throwable);
                                }
                            }));
                        } else
                        {
                            addCompositeDisposable(mRefundRemoteImpl.getRefund(mAggregationId, mStayBookingDetail.reservationIndex//
                                , cancelMessage, "HOTEL").subscribe(new Consumer<String>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull String message) throws Exception
                                {
                                    unLockAll();

                                    onRefundResult(100, message, false);
                                }
                            }, new Consumer<Throwable>()
                            {
                                @Override
                                public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception
                                {
                                    onHandleError(throwable);
                                }
                            }));
                        }
                    }

                    mAnalytics.onRefundPositiveButtonClick(getActivity(), mStayBookingDetail.roomName //
                        , mStayBookingDetail.discountTotal, mStayBookingDetail.overseas, getViewInterface().getCancelReasonText());
                }
            }, null);
    }
}
