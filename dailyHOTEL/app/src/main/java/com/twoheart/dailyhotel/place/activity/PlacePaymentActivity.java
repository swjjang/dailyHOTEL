package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Coupon;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.coupon.SelectCouponDialogActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.CreditCardListActivity;
import com.twoheart.dailyhotel.screen.information.creditcard.RegisterCreditCardActivity;
import com.twoheart.dailyhotel.screen.information.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class PlacePaymentActivity extends BaseActivity
{
    protected static final int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 10000;
    protected static final int REQUEST_CODE_PAYMETRESULT_ACTIVITY = 10001;
    protected static final int REQUEST_CODE_COUPONPOPUP_ACTIVITY = 10002;

    protected PlacePaymentInformation mPaymentInformation;
    protected CreditCard mSelectedCreditCard;
    protected Dialog mFinalCheckDialog;
    protected SaleTime mCheckInSaleTime;

    private ProgressDialog mProgressDialog;

    private boolean mDontReload;

    protected abstract void requestUserInformationForPayment();

    protected abstract void requestEasyPayment(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime);

    protected abstract void requestPlacePaymentInfomation(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime);

    protected abstract void updateSimpleCardInformation(PlacePaymentInformation paymentInformation, CreditCard selectedCreditCard);

    protected abstract void updateGuestInformation(String phoneNumber);

    protected abstract void changedPaymentType(PlacePaymentInformation.PaymentType paymentType, CreditCard creditCard);

    protected abstract boolean isChangedPrice();

    protected abstract boolean hasWarningMessage();

    protected abstract void showWarningMessageDialog();

    protected abstract void showPaymentWeb(PlacePaymentInformation paymentInformation, SaleTime checkInSaleTime);

    protected abstract void showPaymentThankyou(PlacePaymentInformation paymentInformation, String imageUrl);

    protected abstract Dialog getEasyPaymentConfirmDialog();

    protected abstract Dialog getPaymentConfirmDialog(PlacePaymentInformation.PaymentType paymentType);

    protected abstract void onActivityPaymentResult(int requestCode, int resultCode, Intent intent);

    protected abstract void recordAnalyticsAgreeTermDialog(PlacePaymentInformation paymentInformation);

    protected abstract void setCoupon(Coupon coupon);

    protected abstract void setCancelCoupon();

    @Override
    protected void onResume()
    {
        super.onResume();

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == true)
        {
            requestLogin();
        } else
        {
            if (mDontReload == true)
            {
                mDontReload = false;
            } else
            {
                lockUI();
                requestUserInformationForPayment();
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
        {
            mFinalCheckDialog.dismiss();
        }

        mFinalCheckDialog = null;
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
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

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode != Activity.RESULT_OK)
                {
                    finish();
                }
                break;
            }

            case REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY:
            {
                mDontReload = true;

                if (resultCode == RESULT_OK && intent != null)
                {
                    String mobileNumber = intent.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);
                    updateGuestInformation(mobileNumber);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER:
            {
                // 신용카드 간편 결제 선택후
                if (resultCode == Activity.RESULT_OK && intent != null)
                {
                    CreditCard creditCard = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD);

                    if (creditCard != null)
                    {
                        changedPaymentType(PlacePaymentInformation.PaymentType.EASY_CARD, creditCard);
                    }
                }
                break;
            }

            case REQUEST_CODE_PAYMETRESULT_ACTIVITY:
            {
                mDontReload = true;

                setResult(RESULT_OK);
                finish();
                return;
            }

            case CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD:
            {
                // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
                String msg = null;

                switch (resultCode)
                {
                    case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                        mDontReload = true;

                        // 신용카드 등록후에 바로 결제를 할경우.
                        DailyNetworkAPI.getInstance(this).requestUserBillingCardList(mNetworkTag, mPaymentAfterRegisterCreditCardJsonResponseListener, this);
                        return;

                    case CODE_RESULT_PAYMENT_BILLING_DUPLICATE:
                        msg = getString(R.string.message_billing_duplicate);
                        break;

                    case CODE_RESULT_PAYMENT_BILLING_FAIL:
                        msg = getString(R.string.message_billing_fail);
                        break;

                    case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                        restartExpiredSession();
                        return;

                    case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                        msg = getString(R.string.act_toast_payment_fail);
                        break;

                    case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                        msg = getString(R.string.act_toast_payment_network_error);
                        break;
                }

                if (Util.isTextEmpty(msg) == false)
                {
                    String title = getString(R.string.dialog_notice2);
                    String positive = getString(R.string.dialog_btn_text_confirm);

                    showSimpleDialog(title, msg, positive, null);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_PAYMENT:
            {
                mDontReload = true;

                onActivityPaymentResult(requestCode, resultCode, intent);
                break;
            }

            case REQUEST_CODE_COUPONPOPUP_ACTIVITY:
            {
                mDontReload = true;

                unLockUI();

                if (resultCode == Activity.RESULT_OK && intent != null)
                {
                    Coupon coupon = intent.getParcelableExtra(SelectCouponDialogActivity.INTENT_EXTRA_SELECT_COUPON);

                    setCoupon(coupon);
                } else {
                    setCancelCoupon();
                }
                break;
            }

            default:
                break;
        }
    }

    protected void showProgressDialog()
    {
        hidePorgressDialog();

        try
        {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.dialog_msg_processing_payment));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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

    /**
     * 실제 결제를 진행
     *
     * @param paymentInformation
     * @param saleTime
     */
    protected void processPayment(PlacePaymentInformation paymentInformation, SaleTime saleTime)
    {
        if (paymentInformation == null || saleTime == null)
        {
            finish();
            return;
        }

        unLockUI();

        if (paymentInformation.paymentType == PlacePaymentInformation.PaymentType.EASY_CARD)
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

            requestEasyPayment(paymentInformation, saleTime);
        } else
        {
            showPaymentWeb(paymentInformation, saleTime);
        }
    }

    protected void processAgreeTermDialog()
    {
        unLockUI();

        if (mPaymentInformation.paymentType == PlacePaymentInformation.PaymentType.EASY_CARD && mSelectedCreditCard == null)
        {
            Intent intent = new Intent(this, RegisterCreditCardActivity.class);
            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        } else
        {
            showAgreeTermDialog(mPaymentInformation.paymentType);
        }
    }

    protected void showAgreeTermDialog(PlacePaymentInformation.PaymentType paymentType)
    {
        if (paymentType == null)
        {
            return;
        }

        if (mFinalCheckDialog != null)
        {
            mFinalCheckDialog.cancel();
        }

        mFinalCheckDialog = null;

        switch (paymentType)
        {
            case EASY_CARD:
                mFinalCheckDialog = getEasyPaymentConfirmDialog();
                break;

            case CARD:
            case PHONE_PAY:
            case VBANK:
                mFinalCheckDialog = getPaymentConfirmDialog(paymentType);
                break;

            default:
                return;
        }

        if (mFinalCheckDialog == null || isFinishing() == true)
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

        mFinalCheckDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                AnalyticsManager.getInstance(PlacePaymentActivity.this).recordEvent(AnalyticsManager.Category.POPUP_BOXES//
                    , Action.PAYMENT_AGREEMENT_POPPEDUP, Label.CANCEL, null);
            }
        });

        try
        {
            mFinalCheckDialog.show();

            recordAnalyticsAgreeTermDialog(mPaymentInformation);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    protected void showCallDialog()
    {
        OnClickListener positiveListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(PlacePaymentActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PHONE_NUMBER_DAILYHOTEL)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(PlacePaymentActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                    }
                } else
                {
                    DailyToast.showToast(PlacePaymentActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        String operatingTimeMessage = DailyPreference.getInstance(this).getOperationTimeMessage(this);

        showSimpleDialog(getString(R.string.dialog_notice2), operatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    protected void showStopOnSaleDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_stop_onsale);
    }

    protected void showChangedPriceDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_changed_price);
    }

    protected void showChangedTimeDialog()
    {
        showChangedValueDialog(R.string.dialog_msg_changed_time);
    }

    protected void showChangedValueDialog(int messageResId)
    {
        OnClickListener positiveListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(messageResId), getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, null, null, false);
    }

    protected void showChangedBonusDialog()
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

        showSimpleDialog(title, msg, positive, null, new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                lockUI();

                requestPlacePaymentInfomation(mPaymentInformation, mCheckInSaleTime);
            }
        }, null, false);
    }

    protected void startCreditCardList()
    {
        Intent intent = new Intent(this, CreditCardListActivity.class);
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.HOTEL_BOOKINGS//
            , Action.EDIT_BUTTON_CLICKED, Label.PAYMENT_CARD_EDIT, null);
    }

    protected void startInputMobileNumberDialog(String mobileNumber)
    {
        Intent intent = InputMobileNumberDialogActivity.newInstance(this, mobileNumber);
        startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
    }

    private void requestLogin()
    {
        // 세션이 종료되어있으면 다시 로그인한다.
        DailyPreference.getInstance(this).removeUserInformation();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected DailyHotelJsonResponseListener mUserCreditCardListJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                int msgCode = response.getInt("msg_code");

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                if (length == 0)
                {
                    mSelectedCreditCard = null;
                    updateSimpleCardInformation(mPaymentInformation, null);
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

                    updateSimpleCardInformation(mPaymentInformation, mSelectedCreditCard);
                }

                // 호텔 가격 정보가 변경되었습니다.
                if (isChangedPrice() == true)
                {
                    showChangedPriceDialog();
                    return;
                }

                if (hasWarningMessage() == true)
                {
                    showWarningMessageDialog();
                }
            } catch (Exception e)
            {
                // 해당 화면 에러시에는 일반 결제가 가능해야 한다.
                ExLog.e(e.toString());
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mPaymentAfterRegisterCreditCardJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                // 해당 화면은 메시지를 넣지 않는다.
                int msgCode = response.getInt("msg_code");

                JSONArray jsonArray = response.getJSONArray("data");
                int length = jsonArray.length();

                if (length == 0)
                {
                    mSelectedCreditCard = null;
                    updateSimpleCardInformation(mPaymentInformation, null);
                } else
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
                    updateSimpleCardInformation(mPaymentInformation, mSelectedCreditCard);

                    // final check 결제 화면을 보여준다.
                    showAgreeTermDialog(PlacePaymentInformation.PaymentType.EASY_CARD);
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
}
