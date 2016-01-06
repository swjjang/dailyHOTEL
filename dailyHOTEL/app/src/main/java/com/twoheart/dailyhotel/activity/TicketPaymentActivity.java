/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BookingActivity (예약 화면)
 * <p>
 * 결제 화면으로 넘어가기 전 예약 정보를 보여주고 결제방식을 선택할 수 있는 화면
 */
package com.twoheart.dailyhotel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.model.TicketPayment.PaymentType;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

@SuppressLint({"NewApi", "ResourceAsColor"})
public abstract class TicketPaymentActivity extends BaseActivity
{
    protected static final int DEFAULT_AVAILABLE_RESERVES = 20000;

    protected static final int DIALOG_CONFIRM_PAYMENT_CARD = 0;
    protected static final int DIALOG_CONFIRM_PAYMENT_HP = 1;
    protected static final int DIALOG_CONFIRM_PAYMENT_ACCOUNT = 2;

    protected static final int STATE_NONE = 0;
    protected static final int STATE_ACTIVITY_RESULT = 1;
    protected static final int STATE_PAYMENT = 2;

    protected TicketPayment mTicketPayment;
    protected CreditCard mSelectedCreditCard;
    protected boolean mIsChangedPrice; // 가격이 변경된 경우.
    protected int mState;
    protected Dialog mFinalCheckDialog;
    protected SaleTime mCheckInSaleTime;
    protected boolean mIsEditMode;
    protected boolean mDoReload;

    private int mReqCode;
    private int mResCode;
    private Intent mResIntent;
    private ProgressDialog mProgressDialog;
    private String mCSoperatingTimeMessage;

    protected abstract void requestPayEasyPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime);

    protected abstract void requestTicketPaymentInfomation(int index);

    protected abstract void requestValidateTicketPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime);

    protected abstract void updatePaymentInformation(TicketPayment ticketPayment, CreditCard creditCard);

    protected abstract void checkPaymentType(TicketPayment.PaymentType type);

    protected abstract void updateLayout(TicketPayment ticketPayment, CreditCard creditCard);

    protected abstract void checkLastChangedValue();

    protected abstract void showFinalCheckDialog();

    protected abstract Dialog getPaymentConfirmDialog(int type);

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mDoReload == true)
        {
            lockUI();
            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, TicketPaymentActivity.this);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error)
    {
        super.onErrorResponse(error);

        hidePorgressDialog();
    }

    @Override
    public void onError()
    {
        super.onError();

        showSimpleDialog(null, getString(R.string.act_toast_payment_network_error), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        }, null, false);
    }

    protected void processPayment()
    {
        unLockUI();
        mDoReload = false;

        if (mTicketPayment.paymentType == TicketPayment.PaymentType.EASY_CARD)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
            {
                mFinalCheckDialog.dismiss();
            }

            showProgressDialog();

            requestPayEasyPayment(mTicketPayment, mCheckInSaleTime);
        } else
        {
            Intent intent = new Intent(this, com.twoheart.dailyhotel.activity.PaymentWebActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT, mTicketPayment);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mCheckInSaleTime);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        unLockUI();

        mReqCode = requestCode;
        mResCode = resultCode;
        mResIntent = intent;

        mState = STATE_ACTIVITY_RESULT;

        lockUI();

        // 1. 세션이 연결되어있는지 검사.
        DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, this);
    }

    protected void activityResulted(int requestCode, int resultCode, Intent intent)
    {
        //결제가 끝난 뒤 호출됨.
        if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT)
        {
            String title = getString(R.string.dialog_title_payment);
            String msg = "";
            String posTitle = getString(R.string.dialog_btn_text_confirm);
            View.OnClickListener posListener = null;

            if (resultCode != CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE && resultCode != CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS)
            {
                mState = STATE_NONE;
            }

            switch (resultCode)
            {
                // 결제가 성공한 경우 GA와 믹스패널에 등록
                case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
                case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
                    // 가상계좌완료후에는 예약화면의 가상계좌 화면까지 이동한다.
                    if (mTicketPayment.paymentType == PaymentType.VBANK)
                    {
                        activityResulted(requestCode, CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY, intent);
                        return;
                    } else
                    {
                        writeLogPaid(mTicketPayment);

                        posListener = new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                            {
                                mState = STATE_NONE;
                                mDoReload = true;

                                setResult(RESULT_OK);
                                finish();
                            }
                        };

                        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                        {
                            msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                        } else
                        {
                            msg = getString(R.string.act_toast_payment_success);
                        }
                    }
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
                    msg = getString(R.string.act_toast_payment_soldout);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
                    title = getString(R.string.dialog_notice2);
                    msg = getString(R.string.act_toast_payment_not_available);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                    msg = getString(R.string.act_toast_payment_network_error);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                    VolleyHttpClient.createCookie(); // 쿠키를 다시 생성 시도
                    return;

                case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
                    msg = getString(R.string.act_toast_payment_invalid_date);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                    if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                    {
                        msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                    } else
                    {
                        msg = getString(R.string.act_toast_payment_fail);
                    }
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
                    msg = getString(R.string.act_toast_payment_canceled);

                    posListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            mDoReload = true;
                        }
                    };
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
                    /**
                     * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함. 이를 위한 정보를 셋팅.
                     * 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함. 들어간 후에는 다시 프리퍼런스를 초기화해줌.
                     * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약
                     * 리스트 갱신 후 최상단 아이템 인텐트
                     */
                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT) == true)
                    {
                        TicketPayment ticketPayment = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT);

                        DailyPreference.getInstance(TicketPaymentActivity.this).setVirtuaAccountInformation(//
                            ticketPayment.getCustomer().getUserIdx()//
                            , ticketPayment.getTicketInformation().placeName//
                            , Integer.toString(ticketPayment.getTicketInformation().index)//
                            , ticketPayment.checkInTime//
                            , ticketPayment.checkOutTime);
                    }

                    DailyPreference.getInstance(TicketPaymentActivity.this).setVirtualAccountReadyFlag(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);

                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                    {
                        msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                    } else
                    {
                        msg = getString(R.string.dialog_msg_issuing_account);
                    }

                    posListener = new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mDoReload = true;

                            setResult(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
                            finish();
                        }
                    };
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR:
                    msg = getString(R.string.act_toast_payment_account_time_error);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE:
                    msg = getString(R.string.act_toast_payment_account_duplicate);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                    msg = getString(R.string.act_toast_payment_account_timeover);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR:
                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
                    {
                        msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                    } else
                    {
                        msg = getString(R.string.act_toast_payment_fail);
                    }
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_CANCEL:
                {
                    if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
                    {
                        msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
                    } else
                    {
                        msg = getString(R.string.act_toast_payment_fail);
                    }

                    posListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            mDoReload = true;
                        }
                    };
                    break;
                }

                default:
                    mDoReload = true;
                    return;
            }

            if (posListener == null)
            {
                posListener = new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        mDoReload = true;
                        finish();
                    }
                };
            }

            showSimpleDialog(title, msg, posTitle, null, posListener, null, false);
        } else if (requestCode == CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER)
        {
            mState = STATE_NONE;

            // 신용카드 간편 결제 선택후
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    if (intent != null)
                    {
                        CreditCard creditCard = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD);

                        if (creditCard != null)
                        {
                            mSelectedCreditCard = creditCard;

                            // 간편 결제로 체크 하기
                            checkPaymentType(TicketPayment.PaymentType.EASY_CARD);
                        }
                    }
                    break;

            }
        } else if (requestCode == CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD)
        {
            mState = STATE_NONE;

            // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
            String msg = null;

            switch (resultCode)
            {
                case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                    lockUI();

                    // credit card 요청
                    DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserRegisterBillingCardInfoJsonResponseListener, TicketPaymentActivity.this);
                    return;

                case CODE_RESULT_PAYMENT_BILLING_DUPLICATE:
                    msg = getString(R.string.message_billing_duplicate);
                    break;

                case CODE_RESULT_PAYMENT_BILLING_FAIL:
                    msg = getString(R.string.message_billing_fail);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                    msg = getString(R.string.act_toast_payment_fail);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                    msg = getString(R.string.act_toast_payment_network_error);
                    break;
            }

            if (msg != null)
            {
                String title = getString(R.string.dialog_notice2);
                String positive = getString(R.string.dialog_btn_text_confirm);

                showSimpleDialog(title, msg, positive, null);
            }
        } else
        {
            mState = STATE_NONE;
        }
    }

    @Override
    protected void onStart()
    {
        try
        {
            super.onStart();

            AnalyticsManager.getInstance(this).recordScreen(Screen.GOURMET_PAYMENT);
        } catch (NullPointerException e)
        {
            Util.restartApp(this);
        }
    }

    @Override
    protected void onDestroy()
    {
        if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
        {
            mFinalCheckDialog.dismiss();
        }

        mFinalCheckDialog = null;

        hidePorgressDialog();

        super.onDestroy();
    }

    /**
     * 결제 진행
     */
    protected void processValidatePayment()
    {
        unLockUI();

        if (mTicketPayment.paymentType == TicketPayment.PaymentType.EASY_CARD)
        {
            // 간편 결제를 시도하였으나 결제할 카드가 없는 경우.
            if (mSelectedCreditCard == null)
            {
                Intent intent = new Intent(this, RegisterCreditCardActivity.class);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            } else
            {
                showFinalCheckDialog();
            }
        } else
        {
            // 일반 결제 시도
            showAgreeTermDialog(mTicketPayment.paymentType);
        }

        String region = DailyPreference.getInstance(TicketPaymentActivity.this).getGASelectedPlaceRegion();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
        params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
        params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);
        params.put(Label.AREA, region);

        AnalyticsManager.getInstance(this).recordEvent(Screen.BOOKING, Action.CLICK, Label.PAYMENT, params);
    }

    protected void showProgressDialog()
    {
        hidePorgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.dialog_msg_processing_payment));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hidePorgressDialog()
    {
        if (mProgressDialog != null)
        {
            if (mProgressDialog.isShowing() == true)
            {
                mProgressDialog.dismiss();
            }

            mProgressDialog = null;
        }
    }

    protected void showAgreeTermDialog(TicketPayment.PaymentType type)
    {
        if (type == null)
        {
            return;
        }

        if (mFinalCheckDialog != null)
        {
            mFinalCheckDialog.cancel();
        }

        mFinalCheckDialog = null;

        switch (type)
        {
            case CARD:
                // 신용카드를 선택했을 경우
                mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_CARD);
                break;

            case PHONE_PAY:
                // 핸드폰을 선택했을 경우
                mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_HP);
                break;

            case VBANK:
                // 가상계좌 입금을 선택했을 경우
                mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_ACCOUNT);
                break;

            default:
                return;
        }

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.BOOKING, Action.CLICK, type.name(), 0L);

        if (null != mFinalCheckDialog)
        {
            if (isFinishing() == true)
            {
                return;
            }

            mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    releaseUiComponent();
                }
            });

            try
            {
                mFinalCheckDialog.show();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }
    }

    protected void showCallDialog()
    {
        if (isFinishing() == true)
        {
            return;
        }

        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(TicketPaymentActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString())));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(TicketPaymentActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(TicketPaymentActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
                params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
                params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

                AnalyticsManager.getInstance(TicketPaymentActivity.this).recordEvent(Screen.BOOKING, Action.CLICK, Label.CALL_CS, params);
            }
        };

        if (Util.isTextEmpty(mCSoperatingTimeMessage) == true)
        {
            mCSoperatingTimeMessage = getString(R.string.dialog_msg_call);
        }

        showSimpleDialog(getString(R.string.dialog_notice2), mCSoperatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    private void showStopOnSaleDialog()
    {
        if (isFinishing() == true)
        {
            return;
        }

        View.OnClickListener positiveListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_stop_onsale), getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, null, null, false);
    }

    protected void showChangedPayDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_changed_price);
    }

    protected void showChangedTimeDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_changed_time);
    }

    protected void showChangedValueDialog(int messageResId)
    {
        View.OnClickListener positiveListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDoReload = true;

                setResult(RESULT_CANCELED);
                finish();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(messageResId), getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, null, null, false);
    }

    private void showChangedBonusDialog()
    {
        // 적립금이 변동된 경우.
        if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
        {
            mFinalCheckDialog.cancel();
            mFinalCheckDialog = null;
        }

        String title = getString(R.string.dialog_notice2);
        String msg = getString(R.string.dialog_msg_changed_bonus);
        String positive = getString(R.string.dialog_btn_text_confirm);

        showSimpleDialog(title, msg, positive, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                lockUI();

                mState = STATE_NONE;
                mDoReload = true;

                requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
            }
        }, null, false);
    }

    protected void writeLogPaid(TicketPayment ticketPayment)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
            Date date = new Date();
            String strDate = dateFormat.format(date);
            String userIndex = ticketPayment.getCustomer().getUserIdx();
            String transId = strDate + userIndex;

            double price = ticketPayment.getPaymentToPay();

            TicketInformation ticketInformation = ticketPayment.getTicketInformation();

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
            strDate = dateFormat2.format(date);

            AnalyticsManager.getInstance(getApplicationContext()).purchaseComplete(transId, userIndex, Integer.toString(ticketInformation.index), //
                ticketInformation.placeName, Label.PAYMENT, ticketPayment.checkInTime, ticketPayment.checkOutTime, ticketPayment.paymentType.name(), strDate, price);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void requestLogin()
    {
        // 세션이 종료되어있으면 다시 로그인한다.
        if (DailyPreference.getInstance(TicketPaymentActivity.this).isAutoLogin() == true)
        {
            HashMap<String, String> params = Util.getLoginParams(TicketPaymentActivity.this);
            DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, TicketPaymentActivity.this);
        } else
        {
            mDoReload = true;
            unLockUI();
            restartApp();
        }
    }

    private class TelophoneClickSpannable extends ClickableSpan
    {
        public TelophoneClickSpannable()
        {
        }

        @Override
        public void updateDrawState(TextPaint textPain)
        {
            textPain.setColor(getResources().getColor(R.color.booking_tel_link));
            textPain.setFakeBoldText(true);
            textPain.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            showCallDialog();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                SaleTime saleTime = new SaleTime();

                saleTime.setCurrentTime(response.getLong("currentDateTime"));

                long todayDailyTime = response.getLong("dailyDateTime");
                saleTime.setDailyTime(todayDailyTime);

                long shareDailyTime = mCheckInSaleTime.getDayOfDaysHotelDate().getTime();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                int shareDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(shareDailyTime)));
                int todayDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(todayDailyTime)));

                SimpleDateFormat dateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                mCSoperatingTimeMessage = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(dateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(dateFormat.format(new Date(response.getLong("closeDateTime")))));

                // 지난 날인 경우
                if (shareDailyDay < todayDailyDay)
                {
                    unLockUI();

                    DailyToast.showToast(TicketPaymentActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
                    finish();
                    return;
                }

                lockUI();
                DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, TicketPaymentActivity.this);
            } catch (Exception e)
            {
                mDoReload = true;

                onInternalError();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();

                        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, TicketPaymentActivity.this);
                        return;
                    }
                }

                mDoReload = true;

                unLockUI();
                startLoginActivity();
            } catch (Exception e)
            {
                mDoReload = true;
                onInternalError();
            }
        }
    };
    private DailyHotelJsonResponseListener mUserRegisterBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    ExLog.d("msg_code : " + msg_code);
                }

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                if (length == 0)
                {
                    mSelectedCreditCard = null;
                } else
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));

                    updatePaymentInformation(mTicketPayment, mSelectedCreditCard);

                    // final check 결제 화면을 보여준다.
                    showFinalCheckDialog();
                }
            } catch (Exception e)
            {
                // 해당 화면 에러시에는 일반 결제가 가능해야 한다.
                ExLog.e(e.toString());
            } finally
            {
                unLockUI();
            }
        }
    };
    protected DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    mDoReload = true;

                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(TicketPaymentActivity.this, msg, Toast.LENGTH_SHORT);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject jsonData = response.getJSONObject("data");

                boolean isOnSession = jsonData.getBoolean("on_session");

                switch (mState)
                {
                    case STATE_NONE:
                    {
                        if (isOnSession == true)
                        {
                            String name = jsonData.getString("user_name");
                            String phone = jsonData.getString("user_phone");
                            String email = jsonData.getString("user_email");
                            String userIndex = jsonData.getString("user_idx");
                            int bonus = jsonData.getInt("user_bonus");

                            if (bonus < 0)
                            {
                                bonus = 0;
                            }

                            mTicketPayment.bonus = bonus;

                            Customer buyer = new Customer();
                            buyer.setEmail(email);
                            buyer.setName(name);
                            buyer.setPhone(phone);
                            buyer.setUserIdx(userIndex);

                            mTicketPayment.setCustomer(buyer);

                            if (mIsEditMode == false)
                            {
                                Guest guest = new Guest();
                                guest.name = name;
                                guest.phone = phone;
                                guest.email = email;

                                mTicketPayment.setGuest(guest);
                            }

                            // 2. 화면 정보 얻기
                            requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
                        } else
                        {
                            requestLogin();
                        }
                        break;
                    }

                    case STATE_ACTIVITY_RESULT:
                    {
                        unLockUI();

                        if (isOnSession == true)
                        {
                            activityResulted(mReqCode, mResCode, mResIntent);
                        } else
                        {
                            requestLogin();
                        }
                        break;
                    }

                    case STATE_PAYMENT:
                    {
                        if (isOnSession == true)
                        {
                            int bonus = jsonData.getInt("user_bonus");

                            if (bonus < 0)
                            {
                                bonus = 0;
                            }

                            if (mTicketPayment.isEnabledBonus == true && bonus != mTicketPayment.bonus)
                            {
                                // 보너스 값이 변경된 경우
                                mTicketPayment.bonus = bonus;
                                showChangedBonusDialog();
                                return;
                            }

                            requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
                        } else
                        {
                            requestLogin();
                        }
                        break;
                    }
                }
            } catch (Exception e)
            {
                mDoReload = true;

                onInternalError();
            }
        }
    };

    protected DailyHotelJsonResponseListener mUserSessionBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    onInternalError();
                    return;
                }

                // 해당 화면은 메시지를 넣지 않는다.
                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    ExLog.d("msg_code : " + msg_code);
                }

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                if (length == 0)
                {
                    mSelectedCreditCard = null;
                } else
                {
                    if (mSelectedCreditCard == null)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
                    } else
                    {
                        boolean hasCreditCard = false;

                        for (int i = 0; i < length; i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if (mSelectedCreditCard.billingkey.equals(jsonObject.getString("billkey")) == true)
                            {
                                hasCreditCard = true;
                                break;
                            }
                        }

                        // 기존에 선택한 카드를 지우고 돌아온 경우.
                        if (hasCreditCard == false)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
                        }
                    }
                }

                checkLastChangedValue();

                updateLayout(mTicketPayment, mSelectedCreditCard);

                unLockUI();
            } catch (Exception e)
            {
                onInternalError();
            }
        }
    };
}
