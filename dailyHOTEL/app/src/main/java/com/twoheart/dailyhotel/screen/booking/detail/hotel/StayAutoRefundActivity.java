package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bank;
import com.twoheart.dailyhotel.model.HotelBookingDetail;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyTextView;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.List;

public class StayAutoRefundActivity extends BaseActivity
{
    private static final String INTENT_EXTRA_DATA_BOOKING_DETAIL = "bookingDetail";

    private static final String PAYMENT_TYPE_VBANK = "VBANK_INICIS";

    protected StayAutoRefundLayout mStayAutoRefundLayout;
    protected StayAutoRefundNetworkController mStayAutoRefundNetworkController;
    private HotelBookingDetail mHotelBookingDetail;
    private Dialog mDialog;

    private int mSelectedCancelReason;
    private String mCancelReasonMessage;
    private Bank mSelectedBank;
    private List<Bank> mBankList;

    public static Intent newInstance(Context context, HotelBookingDetail hotelBookingDetail)
    {
        Intent intent = new Intent(context, StayAutoRefundActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL, hotelBookingDetail);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mStayAutoRefundLayout = new StayAutoRefundLayout(this, mOnEventListener);
        mStayAutoRefundNetworkController = new StayAutoRefundNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mStayAutoRefundLayout.onCreateView(R.layout.activity_stay_autorefund));

        Intent intent = getIntent();

        if (intent == null)
        {
            finish();
            return;
        }

        mHotelBookingDetail = intent.getParcelableExtra(INTENT_EXTRA_DATA_BOOKING_DETAIL);

        initToolbar();

        mSelectedCancelReason = -1;
        mStayAutoRefundLayout.setRefundButtonEnabled(false);

        // 시작시에 은행 계좌인 경우에는 은행 리스트를 먼저 받아야한다.
        mStayAutoRefundLayout.setPlaceBookingDetail(mHotelBookingDetail);

        // 계좌 이체인 경우
        if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mHotelBookingDetail.transactionType) == true && mHotelBookingDetail.bonus == 0)
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
        View toolbar = findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.label_request_free_refund), new View.OnClickListener()
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

    private void showSelectCancelDialog(int position, String message)
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

        cancelRefundView01.setTag(1);
        cancelRefundView02.setTag(2);
        cancelRefundView03.setTag(3);
        cancelRefundView04.setTag(4);
        cancelRefundView05.setTag(5);
        cancelRefundView06.setTag(6);

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
                messageCountTextView.setText(String.format("(%d/300자)", s.length()));
            }
        });

        messageEditText.setText(message);
        messageEditText.setSelection(messageEditText.length());

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
                    {
                        messageEditText.setText(null);
                        messageClickView.setVisibility(View.VISIBLE);
                        messageClickView.setOnClickListener(this);

                        messageEditText.setCursorVisible(false);

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(messageEditText.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

                        setSelected(v);
                        break;
                    }

                    case R.id.cancelRefundView06:
                    {
                        messageClickView.setVisibility(View.GONE);
                        messageClickView.setOnClickListener(null);

                        messageEditText.setCursorVisible(true);

                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(messageEditText, InputMethodManager.SHOW_IMPLICIT);

                        setSelected(v);
                        break;
                    }

                    case R.id.messageClickView:
                        cancelRefundView06.performClick();
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

                    if (Util.isTextEmpty(message) == true)
                    {
                        AnalyticsManager.getInstance(StayAutoRefundActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.FREE_CANCELLATION, cancelReason, null);
                    } else
                    {
                        AnalyticsManager.getInstance(StayAutoRefundActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.FREE_CANCELLATION, message, null);
                    }
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
            mDialog.show();
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

    private void showSelectBankListDialog(Bank bank, List<Bank> bankList)
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
            mDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void setCancelReasonResult(int position, String reason, String message)
    {
        mSelectedCancelReason = position;

        if (Util.isTextEmpty(message) == true)
        {
            mCancelReasonMessage = reason;
        } else
        {
            mCancelReasonMessage = reason + "-" + message;
        }

        mStayAutoRefundLayout.setCancelReasonText(reason);
        mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
    }

    private void setSelectedBankResult(Bank bank)
    {
        mSelectedBank = bank;

        mStayAutoRefundLayout.setBankText(bank.name);
        mStayAutoRefundLayout.setRefundButtonEnabled(isEnabledRefund());
    }

    private boolean isEnabledRefund()
    {
        // 취소 사유 입력
        if (mSelectedCancelReason < 0)
        {
            return false;
        }

        if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mHotelBookingDetail.transactionType) == true && mHotelBookingDetail.bonus == 0)
        {
            String accountNumber = mStayAutoRefundLayout.getAccountNumber();
            String accountName = mStayAutoRefundLayout.getAccountName();

            if (mSelectedBank == null || Util.isTextEmpty(accountNumber, accountName) == true)
            {
                return false;
            }
        }

        return true;
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
                        if (PAYMENT_TYPE_VBANK.equalsIgnoreCase(mHotelBookingDetail.transactionType) == true && mHotelBookingDetail.bonus == 0)
                        {
                            String accountNumber = mStayAutoRefundLayout.getAccountNumber();
                            String accountName = mStayAutoRefundLayout.getAccountName();

                            mStayAutoRefundNetworkController.requestRefund(mHotelBookingDetail.placeIndex, mHotelBookingDetail.checkInDate//
                                , mHotelBookingDetail.transactionType, mHotelBookingDetail.reservationIndex, mCancelReasonMessage//
                                , accountName, accountNumber, mSelectedBank.code);

                        } else
                        {
                            mStayAutoRefundNetworkController.requestRefund(mHotelBookingDetail.placeIndex, mHotelBookingDetail.checkInDate//
                                , mHotelBookingDetail.transactionType, mHotelBookingDetail.reservationIndex, mCancelReasonMessage);
                        }

                        AnalyticsManager.getInstance(StayAutoRefundActivity.this).recordEvent(AnalyticsManager.Category.BOOKING_STATUS//
                            , AnalyticsManager.Action.FREE_CANCELLATION_CLICKED, null, null);
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
            if (readyForRefund == true)
            {
                setResult(CODE_RESULT_ACTIVITY_REFRESH);
                finish();
            } else
            {
                showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        setResult(RESULT_OK);
                        finish();
                    }
                });
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            StayAutoRefundActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
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
    };
}
