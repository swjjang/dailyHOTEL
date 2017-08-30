package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyTextView;
import com.daily.dailyhotel.entity.Refund;
import com.daily.dailyhotel.repository.remote.RefundRemoteImpl;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.model.StayBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    private RefundRemoteImpl mRefundRemoteImpl;

    protected StayAutoRefundLayout mStayAutoRefundLayout;
    protected StayAutoRefundNetworkController mStayAutoRefundNetworkController;
    StayBookingDetail mStayBookingDetail;
    private String mAggregationId;
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
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
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

    private void setWeightSelectCancelDialog(int orientation, boolean isInMultiWindowMode)
    {
        if (mDialog == null)
        {
            return;
        }

        //        ExLog.d("orientation : " + orientation);

        View topView = mDialog.findViewById(R.id.topWeightView);
        ScrollView scrollView = (ScrollView) mDialog.findViewById(R.id.scrollView);
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

        if (mDialog != null)
        {
            if (mDialog.isShowing())
            {
                mDialog.dismiss();
            }

            mDialog = null;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_refund_dialog_layout, null, false);

        mDialog = new Dialog(this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setCanceledOnTouchOutside(false);

        final ScrollView scrollView = (ScrollView) dialogView.findViewById(R.id.scrollView);

        //
        final View cancelRefundView01 = dialogView.findViewById(R.id.cancelRefundView01);
        final View cancelRefundView02 = dialogView.findViewById(R.id.cancelRefundView02);
        final View cancelRefundView03 = dialogView.findViewById(R.id.cancelRefundView03);
        final View cancelRefundView04 = dialogView.findViewById(R.id.cancelRefundView04);
        final View cancelRefundView05 = dialogView.findViewById(R.id.cancelRefundView05);
        final View cancelRefundView06 = dialogView.findViewById(R.id.cancelRefundView06);
        final View cancelRefundView07 = dialogView.findViewById(R.id.cancelRefundView07);

        cancelRefundView01.setTag(1);
        cancelRefundView02.setTag(2);
        cancelRefundView03.setTag(3);
        cancelRefundView04.setTag(4);
        cancelRefundView05.setTag(5);
        cancelRefundView06.setTag(6);
        cancelRefundView07.setTag(7);

        final DailyEditText messageEditText = (DailyEditText) dialogView.findViewById(R.id.messageEditText);
        final TextView messageCountTextView = (TextView) dialogView.findViewById(R.id.messageCountTextView);
        final View messageClickView = dialogView.findViewById(R.id.messageClickView);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);

        TextView negativeTextView = (TextView) buttonLayout.findViewById(R.id.negativeTextView);
        final TextView positiveTextView = (TextView) buttonLayout.findViewById(R.id.positiveTextView);

        messageEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                messageCountTextView.setText(String.format(Locale.KOREA, "(%d/300자)", s.length()));
            }
        });

        messageEditText.setText(message);
        messageEditText.setSelection(messageEditText.length());

        messageEditText.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction() & MotionEvent.ACTION_MASK)
                {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.getParent().getParent().getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switch (v.getId())
                {
                    case R.id.cancelRefundView01:
                    case R.id.cancelRefundView02:
                    case R.id.cancelRefundView03:
                    case R.id.cancelRefundView04:
                    case R.id.cancelRefundView05:
                    case R.id.cancelRefundView06:
                    {
                        messageEditText.setText(null);
                        messageClickView.setVisibility(View.VISIBLE);
                        messageClickView.setOnClickListener(this);

                        messageEditText.setCursorVisible(false);

                        setSelected(v);

                        messageEditText.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        });


                        break;
                    }

                    case R.id.cancelRefundView07:
                    {
                        messageClickView.setVisibility(View.GONE);
                        messageClickView.setOnClickListener(null);

                        messageEditText.setCursorVisible(true);

                        setSelected(v);

                        scrollView.fullScroll(View.FOCUS_DOWN);

                        messageEditText.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                        break;
                    }

                    case R.id.messageClickView:
                        cancelRefundView07.performClick();
                        break;
                }
            }

            private void setSelected(View view)
            {
                Object tag = ((View) view.getParent()).getTag();

                if (tag != null && tag instanceof DailyTextView == true)
                {
                    ((DailyTextView) tag).setSelected(false);
                    ((DailyTextView) tag).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                ((View) view.getParent()).setTag(view);
                view.setSelected(true);
                ((DailyTextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.card_btn_v_select, 0);

                positiveTextView.setEnabled(true);
            }
        };

        cancelRefundView01.setOnClickListener(onClickListener);
        cancelRefundView02.setOnClickListener(onClickListener);
        cancelRefundView03.setOnClickListener(onClickListener);
        cancelRefundView04.setOnClickListener(onClickListener);
        cancelRefundView05.setOnClickListener(onClickListener);
        cancelRefundView06.setOnClickListener(onClickListener);
        cancelRefundView07.setOnClickListener(onClickListener);

        switch (position)
        {
            case 1:
                cancelRefundView01.performClick();
                break;
            case 2:
                cancelRefundView02.performClick();
                break;
            case 3:
                cancelRefundView03.performClick();
                break;
            case 4:
                cancelRefundView04.performClick();
                break;
            case 5:
                cancelRefundView05.performClick();
                break;
            case 6:
                cancelRefundView06.performClick();
                break;
            case 7:
                cancelRefundView07.performClick();
                break;

            default:
                messageClickView.setVisibility(View.VISIBLE);
                messageClickView.setOnClickListener(onClickListener);

                messageEditText.setCursorVisible(false);
                positiveTextView.setEnabled(false);
                break;
        }

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

                TextView selectedView = (TextView) ((View) cancelRefundView01.getParent()).getTag();

                if (selectedView != null)
                {
                    String cancelReason = selectedView.getText().toString();
                    String message = messageEditText.getText().toString().trim();

                    setCancelReasonResult((Integer) selectedView.getTag(), cancelReason, message);
                }
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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, mDialog);

            mDialog.show();

            mDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        scrollView.post(new Runnable()
        {
            @Override
            public void run()
            {
                scrollView.scrollTo(0, 0);
            }
        });
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
        final RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.recyclerView);
        final BankListAdapter bankListAdapter = new BankListAdapter(this, bankList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bankListAdapter.setSelectedBank(bank);

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);

        TextView negativeTextView = (TextView) buttonLayout.findViewById(R.id.negativeTextView);
        final TextView positiveTextView = (TextView) buttonLayout.findViewById(R.id.positiveTextView);
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

    void setCancelReasonResult(int position, String reason, String message)
    {
        mSelectedCancelReason = position;

        // 기타로 오는 경우
        if (position == 7)
        {
            if (DailyTextUtils.isTextEmpty(message) == true)
            {
                mCancelReasonMessage = reason;
            } else
            {
                mCancelReasonMessage = reason + "-" + message;
            }
        } else
        {
            mCancelReasonMessage = reason;
        }

        mStayAutoRefundLayout.setCancelReasonText(reason);
        mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
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

        if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
        {
            String accountNumber = mStayAutoRefundLayout.getAccountNumber();
            String accountName = mStayAutoRefundLayout.getAccountName();

            if (mSelectedBank == null || DailyTextUtils.isTextEmpty(accountNumber, accountName) == true)
            {
                return false;
            }
        }

        return true;
    }

    private void onRefundResult(int msgCode, String message, boolean readyForRefund)
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
            String cancelMessage = null;

            // 기타인 경우
            if (mSelectedCancelReason == 7)
            {
                if (DailyTextUtils.isTextEmpty(mCancelReasonMessage) == false)
                {
                    if (mCancelReasonMessage.indexOf('-') >= 0)
                    {
                        cancelMessage = mCancelReasonMessage.substring(mCancelReasonMessage.indexOf('-') + 1);
                    }
                }
            } else
            {
                cancelMessage = mCancelReasonMessage;
            }

            StayAutoRefundActivity.this.showSelectCancelDialog(mSelectedCancelReason, cancelMessage);
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

                        if (DailyTextUtils.isTextEmpty(mAggregationId) == true)
                        {
                            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
                            {
                                String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                                String accountName = mStayAutoRefundLayout.getAccountName();

                                mStayAutoRefundNetworkController.requestRefund(mStayBookingDetail.placeIndex, mStayBookingDetail.checkInDate//
                                    , mStayBookingDetail.transactionType, mStayBookingDetail.reservationIndex, mCancelReasonMessage//
                                    , accountName, accountNumber, mSelectedBank.code);

                            } else
                            {
                                mStayAutoRefundNetworkController.requestRefund(mStayBookingDetail.placeIndex, mStayBookingDetail.checkInDate//
                                    , mStayBookingDetail.transactionType, mStayBookingDetail.reservationIndex, mCancelReasonMessage);
                            }
                        } else
                        {
                            String accountNumber;
                            String accountName;

                            if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mStayBookingDetail.transactionType) == true && mStayBookingDetail.bonus == 0)
                            {
                                accountNumber = mStayAutoRefundLayout.getAccountNumber();
                                accountName = mStayAutoRefundLayout.getAccountName();
                            } else
                            {
                                accountNumber = null;
                                accountName = null;
                            }

                            addCompositeDisposable(mRefundRemoteImpl.getRefund(mAggregationId, accountNumber, accountName).subscribe(new Consumer<Refund>()
                            {
                                @Override
                                public void accept(@NonNull Refund refund) throws Exception
                                {
                                    unLockUI();

                                    onRefundResult(refund.msgCode, refund.message, refund.readyForRefund);
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

                        Map<String, String> params = new HashMap<>();
                        params.put(AnalyticsManager.KeyType.NAME, mStayBookingDetail.roomName);
                        params.put(AnalyticsManager.KeyType.VALUE, Integer.toString(mStayBookingDetail.price));
                        params.put(AnalyticsManager.KeyType.COUNTRY, mStayBookingDetail.isOverseas ? AnalyticsManager.ValueType.OVERSEAS : AnalyticsManager.ValueType.DOMESTIC);

                        String cancelMessage = null;

                        if (DailyTextUtils.isTextEmpty(mCancelReasonMessage) == false)
                        {
                            if (mCancelReasonMessage.indexOf('-') >= 0)
                            {
                                cancelMessage = getString(R.string.label_select_cancel_refund07);
                            } else
                            {
                                cancelMessage = mCancelReasonMessage;
                            }
                        }

                        params.put(AnalyticsManager.KeyType.REASON_CANCELLATION, cancelMessage);

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
