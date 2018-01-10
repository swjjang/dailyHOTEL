package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.daily.dailyhotel.screen.common.dialog.refund.AutoRefundDialogActivity;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class StayAutoRefundActivity extends BaseActivity
{
    private static final String INTENT_EXTRA_DATA_BOOKING_DETAIL = "bookingDetail";
    private static final String INTENT_EXTRA_DATA_AGGREGATION_ID = "aggregationId";

    private static final String PAYMENT_TYPE_VBANK = "VBANK_INICIS";

    public static final int REQUEST_CODE_SELECT_CANCEL_TYPE = 10000;

    RefundRemoteImpl mRefundRemoteImpl;

    protected StayAutoRefundLayout mStayAutoRefundLayout;
    protected StayAutoRefundNetworkController mStayAutoRefundNetworkController;
    StayBookingDetail mStayBookingDetail;
    String mAggregationId;
    Dialog mDialog;

    int mSelectedCancelReason;
    String mCancelReasonMessage;
    Bank mSelectedBank;
    List<Bank> mBankList;

    public static Intent newInstance(Context context, StayBookingDetail stayBookingDetail, String aggregationId)
    {
        Intent intent = new Intent(context, StayAutoRefundActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL, stayBookingDetail);
        intent.putExtra(INTENT_EXTRA_DATA_AGGREGATION_ID, aggregationId);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mRefundRemoteImpl = new RefundRemoteImpl(this);

        mStayAutoRefundLayout = new StayAutoRefundLayout(this, mOnEventListener);
        mStayAutoRefundNetworkController = new StayAutoRefundNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mStayAutoRefundLayout.onCreateView(R.layout.activity_stay_autorefund));

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mStayBookingDetail = intent.getParcelableExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL);
        mAggregationId = intent.getStringExtra(INTENT_EXTRA_DATA_AGGREGATION_ID);

        if (mStayBookingDetail == null)
        {
            Util.restartApp(this);
            return;
        }

        initToolbar();

        mSelectedCancelReason = -1;
        mStayAutoRefundLayout.setRefundButtonEnabled(false);

        // 시작시에 은행 계좌인 경우에는 은행 리스트를 먼저 받아야한다.
        mStayAutoRefundLayout.setPlaceBookingDetail(mStayBookingDetail);

        if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            // 계좌 이체인 경우
            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
            {
                mStayAutoRefundLayout.setAccountLayoutVisible(true);

                lockUI();

                mStayAutoRefundNetworkController.requestBankList();
            } else
            {
                mStayAutoRefundLayout.setAccountLayoutVisible(false);
            }
        } else
        {
            // 계좌 이체인 경우
            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
            {
                mStayAutoRefundLayout.setAccountLayoutVisible(true);

                lockUI();

                mStayAutoRefundNetworkController.requestBankList();
            } else
            {
                mStayAutoRefundLayout.setAccountLayoutVisible(false);
            }
        }
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.label_request_free_refund);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        if (mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            mDialog = null;
        }

        super.onDestroy();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        int orientation;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else
        {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        boolean isInMultiWindowMode = VersionUtils.isOverAPI24() == true && isInMultiWindowMode();
        setWeightSelectCancelDialog(orientation, isInMultiWindowMode);
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode)
    {
        super.onMultiWindowModeChanged(isInMultiWindowMode);

        int orientation;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation)
        {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
        } else
        {
            orientation = Configuration.ORIENTATION_PORTRAIT;
        }

        //        ExLog.d("isInMultiWindowMode : " + isInMultiWindowMode + " , rotation orientation : " + orientation);
        setWeightSelectCancelDialog(orientation, isInMultiWindowMode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        unLockUI();

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
        }
    }

    private void setWeightSelectCancelDialog(int orientation, boolean isInMultiWindowMode)
    {
        if (mDialog == null)
        {
            return;
        }

        //        ExLog.d("orientation : " + orientation);

        View topView = mDialog.findViewById(R.id.topWeightView);
        ScrollView scrollView = mDialog.findViewById(R.id.scrollView);
        View bottomView = mDialog.findViewById(R.id.bottomWeightView);

        if (isInMultiWindowMode == true || Configuration.ORIENTATION_LANDSCAPE == orientation)
        {
            topView.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
            scrollView.setVerticalScrollBarEnabled(true);
        } else
        {
            topView.setVisibility(View.VISIBLE);
            bottomView.setVisibility(View.VISIBLE);
            scrollView.setVerticalScrollBarEnabled(false);
        }
    }

    void showSelectCancelDialog(int position, String message)
    {
        if (isFinishing())
        {
            return;
        }

        Intent intent = AutoRefundDialogActivity.newInstance(this, position, message);
        startActivityForResult(intent, StayAutoRefundActivity.REQUEST_CODE_SELECT_CANCEL_TYPE);
    }

    void showSelectBankListDialog(Bank bank, List<Bank> bankList)
    {
        if (isFinishing())
        {
            return;
        }

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_refund_banklist_dialog_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        //
        final RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerView);
        final BankListAdapter bankListAdapter = new BankListAdapter(this, bankList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bankListAdapter.setSelectedBank(bank);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);

        TextView negativeTextView = buttonLayout.findViewById(R.id.negativeTextView);
        final TextView positiveTextView = buttonLayout.findViewById(R.id.positiveTextView);
        positiveTextView.setEnabled(false);

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }
            }
        });

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mDialog != null && mDialog.isShowing())
                {
                    mDialog.dismiss();
                }

                setSelectedBankResult(bankListAdapter.getSelectedBank());
            }
        });

        recyclerView.setAdapter(bankListAdapter);
        bankListAdapter.setOnItemClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                positiveTextView.setEnabled(true);
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                mDialog = null;
                unLockUI();
            }
        });

        try
        {
            mDialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    void setCancelReasonResult(String reason)
    {
        mStayAutoRefundLayout.setCancelReasonText(reason);
        mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
    }

    String getCancelReasonText()
    {
        if (mStayAutoRefundLayout == null)
        {
            return null;
        }

        return mStayAutoRefundLayout.getCancelReasonText();
    }

    void setSelectedBankResult(Bank bank)
    {
        mSelectedBank = bank;

        mStayAutoRefundLayout.setBankText(bank.name);
        mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
    }

    boolean isEnabledRefund()
    {
        // 취소 사유 입력
        if (mSelectedCancelReason < 0)
        {
            return false;
        }

        if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
        {
            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
            {
                String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                String accountName = mStayAutoRefundLayout.getAccountName();

                if (mSelectedBank == null || DailyTextUtils.isTextEmpty(accountNumber, accountName) == true)
                {
                    return false;
                }
            }
        } else
        {
            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
            {
                String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                String accountName = mStayAutoRefundLayout.getAccountName();

                if (mSelectedBank == null || DailyTextUtils.isTextEmpty(accountNumber, accountName) == true)
                {
                    return false;
                }
            }
        }

        return true;
    }

    void onRefundResult(int msgCode, String message, boolean readyForRefund)
    {
        if (readyForRefund == true)
        {
            setResult(CODE_RESULT_ACTIVITY_REFRESH);
            finish();
        } else
        {
            switch (msgCode)
            {
                case 1013:
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                    break;

                case 1015:
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            setResult(CODE_RESULT_ACTIVITY_REFRESH);
                            finish();
                        }
                    });
                    break;

                default:
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                    break;
            }
        }
    }

    private StayAutoRefundLayout.OnEventListener mOnEventListener = new StayAutoRefundLayout.OnEventListener()
    {
        @Override
        public void showSelectCancelDialog()
        {
            StayAutoRefundActivity.this.showSelectCancelDialog(mSelectedCancelReason, mCancelReasonMessage);
        }

        @Override
        public void showSelectBankListDialog()
        {
            StayAutoRefundActivity.this.showSelectBankListDialog(mSelectedBank, mBankList);
        }

        @Override
        public void onAccountTextWatcher(int length)
        {
            mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
        }

        @Override
        public void onClickRefund()
        {
            showSimpleDialog(null, getString(R.string.message_do_cancel_free), getString(R.string.dialog_btn_text_yes)//
                , getString(R.string.dialog_btn_text_no), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (lockUiComponentAndIsLockUiComponent() == true)
                        {
                            return;
                        }

                        lockUI();

                        // 서버전송시 메시지에 reasonText와 cancelMessage를 같이 보냄!
                        String cancelMessage;
                        if (mSelectedCancelReason == 7 && DailyTextUtils.isTextEmpty(mCancelReasonMessage) == false)
                        {
                            cancelMessage = getCancelReasonText() + "-" + mCancelReasonMessage;
                        } else
                        {
                            cancelMessage = getCancelReasonText();
                        }

                        if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                        {
                            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
                            {
                                String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                                String accountName = mStayAutoRefundLayout.getAccountName();

                                mStayAutoRefundNetworkController.requestRefund(mStayBookingDetail.placeIndex, mStayBookingDetail.checkInDate//
                                    , mStayBookingDetail.transactionType, mStayBookingDetail.reservationIndex, cancelMessage//
                                    , accountName, accountNumber, mSelectedBank.code);

                            } else
                            {
                                mStayAutoRefundNetworkController.requestRefund(mStayBookingDetail.placeIndex, mStayBookingDetail.checkInDate//
                                    , mStayBookingDetail.transactionType, mStayBookingDetail.reservationIndex, cancelMessage);
                            }
                        } else
                        {
                            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true)
                            {
                                String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                                String accountName = mStayAutoRefundLayout.getAccountName();

                                addCompositeDisposable(mRefundRemoteImpl.getRefund(mAggregationId, mStayBookingDetail.reservationIndex//
                                    , cancelMessage, "HOTEL", accountName, accountNumber, mSelectedBank.code).subscribe(new Consumer<String>()
                                {
                                    @Override
                                    public void accept(@NonNull String message) throws Exception
                                    {
                                        unLockUI();

                                        onRefundResult(100, message, false);
                                    }
                                }, new Consumer<Throwable>()
                                {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception
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
                                    public void accept(@NonNull String message) throws Exception
                                    {
                                        unLockUI();

                                        onRefundResult(100, message, false);
                                    }
                                }, new Consumer<Throwable>()
                                {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception
                                    {
                                        onHandleError(throwable);
                                    }
                                }));
                            }
                        }

                        Map<String, String> params = new HashMap<>();
                        params.put(AnalyticsManager.KeyType.NAME, mStayBookingDetail.roomName);
                        params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mStayBookingDetail.price));
                        params.put(AnalyticsManager.KeyType.COUNTRY, mStayBookingDetail.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);
                        params.put(AnalyticsManager.KeyType.REASON_CANCELLATION, getCancelReasonText());

                        AnalyticsManager.getInstance(StayAutoRefundActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.FREE_CANCELLATION, cancelMessage, params);
                    }
                }, null);
        }

        @Override
        public void finish()
        {
            StayAutoRefundActivity.this.finish();
        }
    };

    private StayAutoRefundNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new StayAutoRefundNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onBankList(List<Bank> bankList)
        {
            unLockUI();

            mBankList = bankList;
        }

        @Override
        public void onRefundResult(int msgCode, String message, boolean readyForRefund)
        {
            unLockUI();

            StayAutoRefundActivity.this.onRefundResult(msgCode, message, readyForRefund);
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            StayAutoRefundActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
        {
            StayAutoRefundActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            StayAutoRefundActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            StayAutoRefundActivity.this.onErrorToastMessage(message);
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            StayAutoRefundActivity.this.onErrorResponse(call, response);
        }
    };
}
