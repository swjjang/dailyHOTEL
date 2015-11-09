/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * BookingActivity (예약 화면)
 * <p/>
 * 결제 화면으로 넘어가기 전 예약 정보를 보여주고 결제방식을 선택할 수 있는 화면
 */
package com.twoheart.dailyhotel.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.FinalCheckLayout;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author jangjunho
 */
@SuppressLint({"NewApi", "ResourceAsColor"})
public class BookingActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener
{
    private static int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 10000;
    private static final int DEFAULT_AVAILABLE_RESERVES = 20000;

    private static final int DIALOG_CONFIRM_PAYMENT_CARD = 0;
    private static final int DIALOG_CONFIRM_PAYMENT_HP = 1;
    private static final int DIALOG_CONFIRM_PAYMENT_ACCOUNT = 2;

    protected String mAliveCallSource = "";
    private TextView mCheckinDayTextView, mCheckinTimeTextView, mCheckoutDayTextView, mCheckoutTimeTextView;
    private TextView tvOriginalPriceValue, tvCreditValue, tvOriginalPrice, tvCredit, tvPrice;
    private TextView btnPay;
    private SwitchCompat swCredit;
    private EditText etReserverName, etReserverNumber, etReserverEmail;
    private Drawable[] mEditTextBackground;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbPaymentAccount, rbPaymentCard, rbPaymentHp, mSimplePaymentRadioButton;
    private View mCardManagerButton;
    private Pay mPay;
    private CreditCard mSelectedCreditCard;
    private boolean mIsChangedPay; // 가격이 변경된 경우.
    private int mReqCode;
    private int mResCode;
    private Intent mResIntent;
    private Dialog mFinalCheckDialog;
    private ProgressDialog mProgressDialog;

    private SaleTime mCheckInSaleTime;
    private boolean mIsEditMode;

    private View mClickView;
    private boolean mDoReload;
    private String mWarningDialogMessage;
    private String mCSoperatingTimeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);

        mPay = new Pay();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            mPay.setSaleRoomInformation((SaleRoomInformation) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION));
            mCheckInSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
        }

        if (mPay.getSaleRoomInformation() == null)
        {
            finish();
            return;
        }

        mIsChangedPay = false;
        mDoReload = true;
        mWarningDialogMessage = null;

        setActionBar(mPay.getSaleRoomInformation().hotelName);

        mCheckinDayTextView = (TextView) findViewById(R.id.checkinDayTextView);
        mCheckinTimeTextView = (TextView) findViewById(R.id.checkinTimeTextView);
        mCheckoutDayTextView = (TextView) findViewById(R.id.checkoutDayTextView);
        mCheckoutTimeTextView = (TextView) findViewById(R.id.checkoutTimeTextView);

        tvOriginalPrice = (TextView) findViewById(R.id.tv_hotel_payment_original_price);
        tvCredit = (TextView) findViewById(R.id.tv_hotel_payment_credit);
        tvOriginalPriceValue = (TextView) findViewById(R.id.tv_hotel_payment_original_price_value);
        tvCreditValue = (TextView) findViewById(R.id.tv_hotel_payment_credit_value);
        tvPrice = (TextView) findViewById(R.id.tv_hotel_payment_price);
        btnPay = (TextView) findViewById(R.id.btn_hotel_payment);

        swCredit = (SwitchCompat) findViewById(R.id.btn_on_off);

        //		swCredit.setSwitchMinWidth(Util.dpToPx(BookingActivity.this, 60));

        etReserverName = (EditText) findViewById(R.id.et_hotel_payment_reserver_name);
        etReserverNumber = (EditText) findViewById(R.id.et_hotel_payment_reserver_number);
        etReserverEmail = (EditText) findViewById(R.id.et_hotel_payment_reserver_email);

        rgPaymentMethod = (RadioGroup) findViewById(R.id.rg_payment_method);

        mEditTextBackground = new Drawable[3];
        mEditTextBackground[0] = etReserverName.getBackground();
        mEditTextBackground[1] = etReserverNumber.getBackground();
        mEditTextBackground[2] = etReserverEmail.getBackground();

        etReserverName.setBackgroundResource(0);
        etReserverNumber.setBackgroundResource(0);
        etReserverEmail.setBackgroundResource(0);

        etReserverName.setEnabled(false);
        etReserverNumber.setEnabled(false);
        etReserverEmail.setEnabled(false);

        mSimplePaymentRadioButton = (RadioButton) findViewById(R.id.easyPaymentRadioButton);
        rbPaymentAccount = (RadioButton) findViewById(R.id.rb_payment_account);
        rbPaymentCard = (RadioButton) findViewById(R.id.rb_payment_card);
        rbPaymentHp = (RadioButton) findViewById(R.id.rb_payment_hp);
        mCardManagerButton = findViewById(R.id.cardManagerButton);
        mCardManagerButton.setOnClickListener(this);

        rbPaymentAccount.setOnClickListener(this);
        rbPaymentCard.setOnClickListener(this);
        rbPaymentHp.setOnClickListener(this);
        mSimplePaymentRadioButton.setOnClickListener(this);

        rgPaymentMethod.setOnCheckedChangeListener(this);

        btnPay.setOnClickListener(this);
        swCredit.setOnCheckedChangeListener(this);

        rbPaymentCard.setChecked(true);

        // 적립금 부분 기본 통화 표기.
        tvCreditValue.setText("0" + Html.fromHtml(getString(R.string.currency)));
        rgPaymentMethod.setVisibility(View.VISIBLE);

        TextView linkTextView = (TextView) findViewById(R.id.tv_card_notice);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

        String info01 = getString(R.string.act_booking_payment_info01);
        String info02 = getString(R.string.act_booking_payment_info02);

        stringBuilder.append(info01);
        stringBuilder.append(info02);
        stringBuilder.append(getString(R.string.act_booking_payment_info03));

        stringBuilder.setSpan(new TelophoneClickSpannable(), info01.length(), info01.length() + info02.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        linkTextView.setText(stringBuilder);
        linkTextView.setMovementMethod(LinkMovementMethod.getInstance());

        // 수정.
        View editLinearLayout = findViewById(R.id.editLinearLayout);
        editLinearLayout.setOnClickListener(mOnEditInfoOnClickListener);

        // 객실 타입
        TextView roomTypeTextView = (TextView) findViewById(R.id.roomTypeTextView);
        roomTypeTextView.setText(mPay.getSaleRoomInformation().roomName);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (mDoReload == true)
        {
            lockUI();

            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, BookingActivity.this);
        }
    }

    private void updatePayPrice(boolean applyCredit)
    {
        int originalPrice = mPay.getSaleRoomInformation().totalDiscount;
        int payPrice = originalPrice;
        int credit = mPay.credit;

        DecimalFormat comma = new DecimalFormat("###,##0");

        tvOriginalPriceValue.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));

        if (applyCredit && credit > 0)
        {
            if (credit < originalPrice)
            {
                payPrice = originalPrice - credit;
            } else
            {
                payPrice = 0;
                credit = originalPrice;
            }

            tvCreditValue.setText("-" + comma.format(credit) + Html.fromHtml(getString(R.string.currency)));
        } else
        {
            tvCreditValue.setText("0" + Html.fromHtml(getString(R.string.currency)));
        }

        tvPrice.setText(comma.format(payPrice) + Html.fromHtml(getString(R.string.currency)));
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
    public void onClick(final View v)
    {
        Guest guest = mPay.getGuest();

        if (guest == null)
        {
            restartApp();
            return;
        }

        if (v.getId() == btnPay.getId())
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mIsEditMode == true)
            {
                guest.name = etReserverName.getText().toString().trim();
                guest.phone = etReserverNumber.getText().toString().trim();
                guest.email = etReserverEmail.getText().toString().trim();

                releaseUiComponent();

                if (Util.isTextEmpty(guest.name) == true)
                {
                    etReserverName.requestFocus();

                    if (mPay.getSaleRoomInformation().isOverseas == true)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_please_input_guest_typeoverseas, Toast.LENGTH_SHORT);
                    } else
                    {
                        DailyToast.showToast(this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
                    }
                    return;
                } else if (Util.isTextEmpty(guest.phone) == true)
                {
                    etReserverNumber.requestFocus();
                    DailyToast.showToast(this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
                    return;
                } else if (Util.isTextEmpty(guest.email) == true)
                {
                    etReserverEmail.requestFocus();
                    DailyToast.showToast(this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                    return;
                } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
                {
                    etReserverEmail.requestFocus();
                    DailyToast.showToast(BookingActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                if (mPay.getSaleRoomInformation().isOverseas == true)
                {
                    Editor editor = sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_OVERSEAS_NAME, guest.name);
                    editor.putString(KEY_PREFERENCE_OVERSEAS_PHONE, guest.phone);
                    editor.putString(KEY_PREFERENCE_OVERSEAS_EMAIL, guest.email);
                    editor.commit();
                }
            }

            //호텔 가격이 xx 이하인 이벤트 호텔에서는 적립금 사용을 못하게 막음.
            if (mPay.isSaleCredit() && (mPay.getOriginalPrice() <= DEFAULT_AVAILABLE_RESERVES) && mPay.credit != 0)
            {
                v.setClickable(false);
                v.setEnabled(false);

                String title = getString(R.string.dialog_notice2);
                String msg = getString(R.string.dialog_btn_payment_no_reserve);
                String buttonText = getString(R.string.dialog_btn_payment_confirm);

                showSimpleDialog(title, msg, buttonText, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        swCredit.setChecked(false);
                    }
                }, new OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();

                        v.setClickable(true);
                        v.setEnabled(true);
                    }
                });

                releaseUiComponent();
            } else
            {
                mClickView = v;

                String gcmId = sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");

                if (mPay.getType() == Pay.Type.VBANK && Util.isTextEmpty(gcmId) == true)
                {
                    // 가상계좌 결제시 푸쉬를 받지 못하는 경우
                    String title = getString(R.string.dialog_notice2);
                    String positive = getString(R.string.dialog_btn_text_confirm);
                    String msg = getString(R.string.dialog_msg_none_gcmid);

                    showSimpleDialog(title, msg, positive, new View.OnClickListener()
                    {

                        @Override
                        public void onClick(View view)
                        {
                            onClickPayment();
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            unLockUI();
                        }
                    });
                } else
                {
                    onClickPayment();
                }
            }
        } else if (v.getId() == mCardManagerButton.getId())
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mIsEditMode == true)
            {
                guest.name = etReserverName.getText().toString();
                guest.phone = etReserverNumber.getText().toString();
                guest.email = etReserverEmail.getText().toString();
            }

            Intent intent = new Intent(this, CreditCardListActivity.class);
            intent.setAction(Intent.ACTION_PICK);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

    }

    private void onClickPayment()
    {
        unLockUI();
        mDoReload = false;

        if (mClickView != null)
        {
            mClickView.setClickable(false);
            mClickView.setEnabled(false);
        }

        // 간편 결제를 시도하였으나 결제할 카드가 없는 경우.
        if (mPay.getType() == Pay.Type.EASY_CARD)
        {
            if (mSelectedCreditCard == null)
            {
                mDoReload = true;

                if (mClickView != null)
                {
                    mClickView.setClickable(true);
                    mClickView.setEnabled(true);
                }

                Intent intent = new Intent(BookingActivity.this, RegisterCreditCardActivity.class);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
            } else
            {
                showFinalCheckDialog();
            }
        } else
        {
            showAgreeTermDialog(mPay.getType());
        }

        String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Label.HOTEL_ROOM_INDEX, String.valueOf(mPay.getSaleRoomInformation().roomIndex));
        params.put(Label.HOTEL_ROOM_NAME, mPay.getSaleRoomInformation().roomName);
        params.put(Label.HOTEL_NAME, mPay.getSaleRoomInformation().hotelName);
        params.put(Label.AREA, region);

        AnalyticsManager.getInstance(this).recordEvent(Screen.BOOKING, Action.CLICK, Label.PAYMENT, params);
    }

    /**
     * 결제 수단에 알맞은 결제 동의 확인 다이얼로그를 만든다.
     *
     * @param type CARD, ACCOUNT, HP 세가지 타입 존재.
     * @return 타입에 맞는 결제 동의 다이얼로그 반환.
     */

    private Dialog getPaymentConfirmDialog(int type)
    {
        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);
        ViewGroup messageLayout = (ViewGroup) view.findViewById(R.id.messageLayout);

        int[] textResIds;

        switch (type)
        {
            // 핸드폰 결제
            case DIALOG_CONFIRM_PAYMENT_HP:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03//
                    , R.string.dialog_msg_hotel_payment_message04};
                break;

            // 계좌 이체
            case DIALOG_CONFIRM_PAYMENT_ACCOUNT:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03//
                    , R.string.dialog_msg_hotel_payment_message05};
                break;

            // 신용카드 일반 결제
            case DIALOG_CONFIRM_PAYMENT_CARD:
                textResIds = new int[]{R.string.dialog_msg_hotel_payment_message01//
                    , R.string.dialog_msg_hotel_payment_message02//
                    , R.string.dialog_msg_hotel_payment_message03};
                break;

            default:
                return null;
        }


        int length = textResIds.length;

        for (int i = 0; i < length; i++)
        {
            View messageRow = LayoutInflater.from(this).inflate(R.layout.row_payment_agreedialog, messageLayout, false);

            TextView messageTextView = (TextView) messageRow.findViewById(R.id.messageTextView);

            String message = getString(textResIds[i]);

            if (i == 0)
            {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message);

                int boldStartIndex = message.indexOf("예약");
                int boldLength = "예약 취소, 변경 및 환불이 불가".length();

                spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dialog_title_text)), //
                    boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
                    boldStartIndex, boldStartIndex + boldLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                messageTextView.setText(spannableStringBuilder);
            } else
            {
                messageTextView.setText(message);
            }

            messageLayout.addView(messageRow);
        }

        View agreeLayout = view.findViewById(R.id.agreeLayout);

        OnClickListener buttonOnClickListener = new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();

                synchronized (BookingActivity.this)
                {
                    if (isLockUiComponent() == true)
                    {
                        return;
                    }

                    lockUI();

                    mAliveCallSource = "PAYMENT";

                    // 1. 세션이 살아있는지 검사 시작.
                    DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, BookingActivity.this);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(Label.HOTEL_ROOM_INDEX, String.valueOf(mPay.getSaleRoomInformation().roomIndex));
                    params.put(Label.HOTEL_ROOM_NAME, mPay.getSaleRoomInformation().roomName);
                    params.put(Label.HOTEL_NAME, mPay.getSaleRoomInformation().hotelName);

                    AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.PAYMENT_AGREE_POPUP, Action.CLICK, mPay.getType().name(), params);
                }
            }
        };

        agreeLayout.setOnClickListener(buttonOnClickListener);

        dialog.setContentView(view);

        return dialog;

    }

    // 결제 화면으로 이동
    private void moveToPayStep()
    {
        unLockUI();
        mDoReload = false;

        Guest guest = mPay.getGuest();

        if (mIsEditMode == true)
        {
            guest.name = etReserverName.getText().toString().trim();
            guest.phone = etReserverNumber.getText().toString().trim();
            guest.email = etReserverEmail.getText().toString().trim();
        }

        if (mPay.getType() == Pay.Type.EASY_CARD)
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

            String bonus = "0"; // 적립금

            if (mPay.isSaleCredit() == true)
            {
                bonus = String.valueOf(mPay.credit);
            }

            Map<String, String> params = new HashMap<String, String>();

            SaleRoomInformation saleRoomInformation = mPay.getSaleRoomInformation();

            params.put("room_idx", String.valueOf(saleRoomInformation.roomIndex));
            params.put("checkin_date", mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"));
            params.put("nights", String.valueOf(saleRoomInformation.nights));
            params.put("billkey", mSelectedCreditCard.billingkey);
            params.put("bonus", bonus);
            params.put("guest_name", guest.name);
            params.put("guest_phone", guest.phone.replace("-", ""));
            params.put("guest_email", guest.email);

            if (DEBUG == true)
            {
                showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
            }

            DailyNetworkAPI.getInstance().requestHotelPayment(mNetworkTag, params, mHotelPaymentSessionEasy, BookingActivity.this);
        } else
        {
            Intent intent = new Intent(this, com.twoheart.dailyhotel.activity.PaymentActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);
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

        if (requestCode == REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY)
        {
            if (resultCode == RESULT_OK && intent != null)
            {
                String mobileNumber = intent.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);

                etReserverNumber.setText(mobileNumber);
            }

            return;
        }

        mReqCode = requestCode;
        mResCode = resultCode;
        mResIntent = intent;

        mAliveCallSource = "ACTIVITY_RESULT";

        lockUI();

        // 1. 세션이 연결되어있는지 검사.
        DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, this);
    }

    private void activityResulted(int requestCode, int resultCode, Intent intent)
    {
        //결제가 끝난 뒤 호출됨.
        if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT)
        {
            String title = getString(R.string.dialog_title_payment);
            String msg = "";
            String posTitle = getString(R.string.dialog_btn_text_confirm);
            View.OnClickListener posListener = null;

            switch (resultCode)
            {
                // 결제가 성공한 경우 GA와 믹스패널에 등록
                case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
                case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
                    writeLogPaid(mPay);

                    posListener = new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            mDoReload = true;
                            mAliveCallSource = "";

                            setResult(RESULT_OK);
                            finish();
                        }
                    };

                    msg = getString(R.string.act_toast_payment_success);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:

                    mAliveCallSource = "";
                    msg = getString(R.string.act_toast_payment_soldout);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
                    mAliveCallSource = "";

                    title = getString(R.string.dialog_notice2);
                    msg = getString(R.string.act_toast_payment_not_available);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_network_error);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
                    mAliveCallSource = "";

                    VolleyHttpClient.createCookie(); // 쿠키를 다시 생성 시도
                    return;

                case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_invalid_date);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_fail);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
                    mAliveCallSource = "";

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
                    mAliveCallSource = "";

                    /**
                     * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함. 이를 위한 정보를 셋팅.
                     * 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함. 들어간 후에는 다시 프리퍼런스를 초기화해줌.
                     * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약
                     * 리스트 갱신 후 최상단 아이템 인텐트
                     */
                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_PAY) == true)
                    {
                        Pay payData = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY);

                        Editor editor = sharedPreference.edit();
                        editor.putString(KEY_PREFERENCE_USER_IDX, payData.getCustomer().getUserIdx());
                        editor.putString(KEY_PREFERENCE_HOTEL_NAME, mPay.getSaleRoomInformation().hotelName);
                        editor.putInt(KEY_PREFERENCE_HOTEL_ROOM_IDX, payData.getSaleRoomInformation().roomIndex);
                        editor.putString(KEY_PREFERENCE_HOTEL_CHECKOUT, payData.checkOutTime);
                        editor.putString(KEY_PREFERENCE_HOTEL_CHECKIN, payData.checkInTime);
                        editor.commit();
                    }

                    Editor editor = sharedPreference.edit();
                    editor.putInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
                    editor.apply();

                    msg = getString(R.string.dialog_msg_issuing_account);

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
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_account_time_error);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE:
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_account_duplicate);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
                    mAliveCallSource = "";

                    msg = getString(R.string.act_toast_payment_account_timeover);
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR:
                    mAliveCallSource = "";

                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
                    {
                        msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                    } else
                    {
                        msg = getString(R.string.act_toast_payment_fail);
                    }
                    break;

                case CODE_RESULT_ACTIVITY_PAYMENT_PRECHECK:
                {
                    if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE))
                    {
                        String result = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
                        String[] message = result.split("\\^");

                        try
                        {
                            int msgCode = Integer.parseInt(message[0]);
                            msg = message[1];

                            // 5	판매 마감시
                            // 1000	결제 성공시
                            // 1001	결제가 취소되었을시 ( 입금전 )
                            // 1002	예약 실패시 ( 입금 후 )
                            // 1003	입금 대기중 일시
                            // 1004	모든 객실이 판매되었을시
                            // 1005	마지막 상품을 다른 고객님이 결제중 일시
                            // 1006	이니시스 에러 메시지 일시
                            switch (msgCode)
                            {
                                case 5:
                                {
                                    mAliveCallSource = "";

                                    posListener = new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            mDoReload = true;

                                            setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                            finish();
                                        }
                                    };
                                    break;
                                }

                                case 1000:
                                {
                                    writeLogPaid(mPay);

                                    posListener = new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            mDoReload = true;
                                            mAliveCallSource = "";

                                            setResult(RESULT_OK);
                                            finish();
                                        }
                                    };
                                    break;
                                }

                                case 1001:
                                case 1002:
                                case 1003:

                                    mAliveCallSource = "";

                                    posListener = new OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            mDoReload = true;
                                        }
                                    };
                                    break;

                                case 1004:
                                case 1005:
                                case 1006:
                                default:
                                    mAliveCallSource = "";
                                    break;
                            }
                        } catch (Exception e)
                        {
                            mAliveCallSource = "";

                            msg = getString(R.string.act_toast_payment_fail);
                        }
                    } else
                    {
                        mAliveCallSource = "";

                        msg = getString(R.string.act_toast_payment_fail);
                    }
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
            mAliveCallSource = "";

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

                            rgPaymentMethod.check(mSimplePaymentRadioButton.getId());
                        }
                    }
                    break;

            }
        } else if (requestCode == CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD)
        {
            mAliveCallSource = "";

            // 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
            String msg = null;

            switch (resultCode)
            {
                case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
                    lockUI();

                    // credit card 요청
                    DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserRegisterBillingCardInfoJsonResponseListener, BookingActivity.this);
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
                if (isFinishing() == true)
                {
                    return;
                }

                String title = getString(R.string.dialog_notice2);
                String positive = getString(R.string.dialog_btn_text_confirm);

                showSimpleDialog(title, msg, positive, null);
            }
        } else
        {
            mAliveCallSource = "";
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        if (group.getId() == rgPaymentMethod.getId())
        {
            if (checkedId == mSimplePaymentRadioButton.getId())
            {
                mPay.setType(Pay.Type.EASY_CARD);
            } else if (checkedId == rbPaymentCard.getId())
            {
                mPay.setType(Pay.Type.CARD);
            } else if (checkedId == rbPaymentHp.getId())
            {
                mPay.setType(Pay.Type.PHONE_PAY);
            } else if (checkedId == rbPaymentAccount.getId())
            {
                mPay.setType(Pay.Type.VBANK);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        // 앱 메모리 삭제하고 복귀하는 경우 에러가 생기는 경우가 발생하여
        // 앱을 재부팅하는 코드 추가.
        try
        {
            if (buttonView.getId() == swCredit.getId())
            {
                if (!isChecked)
                {
                    // 사용안함으로 변경
                    tvOriginalPrice.setEnabled(false);
                    tvCredit.setEnabled(false);
                    tvOriginalPriceValue.setEnabled(false);
                    tvCreditValue.setEnabled(false);

                    AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Label.USED_CREDIT, Action.CLICK, Label.ON, 0L);
                } else
                {
                    // 사용함으로 변경
                    tvOriginalPrice.setEnabled(true);
                    tvCredit.setEnabled(true);
                    tvOriginalPriceValue.setEnabled(true);
                    tvCreditValue.setEnabled(true);

                    AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Label.USED_CREDIT, Action.CLICK, Label.OFF, 0L);
                }

                mPay.setSaleCredit(isChecked);
                updatePayPrice(isChecked);
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            restartApp();
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(Screen.BOOKING);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.payment_wait_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_call:
            {
                if (isFinishing() == true)
                {
                    return super.onOptionsItemSelected(item);
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return super.onOptionsItemSelected(item);
                }

                showCallDialog();

                return true;
            }

            default:
            {
                return super.onOptionsItemSelected(item);
            }
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

    private void showProgressDialog()
    {
        hidePorgressDialog();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.dialog_msg_processing_payment));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        try
        {
            mProgressDialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void hidePorgressDialog()
    {
        if (mProgressDialog != null && mProgressDialog.isShowing() == true)
        {
            mProgressDialog.dismiss();
        }

        mProgressDialog = null;
    }

    private void showAgreeTermDialog(Pay.Type type)
    {
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

                    if (mClickView != null)
                    {
                        mClickView.setClickable(true);
                        mClickView.setEnabled(true);
                    }
                }
            });

            mFinalCheckDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    mDoReload = true;
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

    private void showFinalCheckDialog()
    {
        if (isFinishing() == true)
        {
            return;
        }

        if (mFinalCheckDialog != null)
        {
            mFinalCheckDialog.cancel();
        }

        mFinalCheckDialog = new Dialog(this);

        mFinalCheckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFinalCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mFinalCheckDialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = mFinalCheckDialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);

        int[] messageResIds = {R.string.dialog_msg_hotel_payment_message01, R.string.dialog_msg_hotel_payment_message02, R.string.dialog_msg_hotel_payment_message03};

        final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(BookingActivity.this, messageResIds);
        final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
        final View agreeLayout = finalCheckLayout.findViewById(R.id.agreeLayout);

        agreeLayout.setEnabled(false);

        finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
        {
            @Override
            public void onConfirmSignature()
            {
                agreeLayout.setEnabled(true);

                AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(500);
                animation.setFillBefore(true);
                animation.setFillAfter(true);

                animation.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation)
                    {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation)
                    {
                        agreeSinatureTextView.setAnimation(null);
                        agreeSinatureTextView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation)
                    {
                    }
                });

                TransitionDrawable transition = (TransitionDrawable) agreeLayout.getBackground();
                transition.startTransition(500);

                agreeSinatureTextView.startAnimation(animation);

                agreeLayout.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        synchronized (BookingActivity.this)
                        {
                            if (isLockUiComponent() == true)
                            {
                                return;
                            }

                            lockUI();

                            mAliveCallSource = "PAYMENT";

                            // 1. 세션이 살아있는지 검사 시작.
                            DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, BookingActivity.this);
                            mFinalCheckDialog.dismiss();

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put(Label.HOTEL_ROOM_INDEX, String.valueOf(mPay.getSaleRoomInformation().roomIndex));
                            params.put(Label.HOTEL_ROOM_NAME, mPay.getSaleRoomInformation().roomName);
                            params.put(Label.HOTEL_NAME, mPay.getSaleRoomInformation().hotelName);

                            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.PAYMENT_AGREE_POPUP, Action.CLICK, mPay.getType().name(), params);
                        }
                    }
                });
            }
        });

        mFinalCheckDialog.setContentView(finalCheckLayout);
        mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();

                if (mClickView != null)
                {
                    mClickView.setClickable(true);
                    mClickView.setEnabled(true);
                }
            }
        });

        mFinalCheckDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                mDoReload = true;
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

    private void showCallDialog()
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

                if (Util.isTelephonyEnabled(BookingActivity.this) == true)
                {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
                    startActivity(i);
                } else
                {
                    DailyToast.showToast(BookingActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.HOTEL_ROOM_INDEX, String.valueOf(mPay.getSaleRoomInformation().roomIndex));
                params.put(Label.HOTEL_ROOM_NAME, mPay.getSaleRoomInformation().roomName);
                params.put(Label.HOTEL_NAME, mPay.getSaleRoomInformation().hotelName);

                AnalyticsManager.getInstance(BookingActivity.this).recordEvent(Screen.BOOKING, Action.CLICK, Label.CALL_CS, params);
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

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_stop_onsale)//
            , getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, false);
    }

    private void showChangedPayDialog()
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

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_changed_pay) //
            , getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, false);
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

                mAliveCallSource = "";
                mDoReload = true;

                // 호텔 디테일 정보 재 요청
                String params = String.format("?room_idx=%d&checkin_date=%s&nights=%d", mPay.getSaleRoomInformation().roomIndex, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"), mPay.getSaleRoomInformation().nights);
                DailyNetworkAPI.getInstance().requestHotelPaymentInformation(mNetworkTag, params, mHotelPaymentDetailJsonResponseListener, BookingActivity.this);
            }
        }, null, false);
    }

    private void showInputMobileNumberDialog(String mobileNumber)
    {
        Intent intent = InputMobileNumberDialogActivity.newInstance(BookingActivity.this, mobileNumber);
        startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
    }

    private void writeLogPaid(Pay pay)
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
            Date date = new Date();
            String strDate = dateFormat.format(date);
            String userIndex = pay.getCustomer().getUserIdx();
            String transId = strDate + userIndex;

            double price = pay.getOriginalPrice() - (pay.isSaleCredit() ? pay.credit : 0);

            if (price < 0)
            {
                price = 0;
            }

            SaleRoomInformation saleRoomInformation = pay.getSaleRoomInformation();

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
            strDate = dateFormat2.format(date);

            AnalyticsManager.getInstance(getApplicationContext()).purchaseComplete(transId, userIndex, String.valueOf(saleRoomInformation.roomIndex), //
                saleRoomInformation.hotelName, Label.PAYMENT, pay.checkInTime, pay.checkOutTime, pay.getType().name(), strDate, price);
        } catch (Exception e)
        {
            ExLog.e(e.toString());
        }
    }

    private void requestLogin()
    {
        // 세션이 종료되어있으면 다시 로그인한다.
        if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false) == true)
        {
            HashMap<String, String> params = Util.getLoginParams(sharedPreference);

            DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, BookingActivity.this);
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
    // UI Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private View.OnClickListener mOnEditInfoOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            mIsEditMode = true;
            view.setVisibility(View.INVISIBLE);

            // 이름.
            if (etReserverName.isEnabled() == false)
            {
                etReserverName.setEnabled(true);

                if (mPay.getSaleRoomInformation().isOverseas == true)
                {
                    // 회원 가입시 이름 필터 적용.
                    StringFilter stringFilter = new StringFilter(BookingActivity.this);
                    InputFilter[] allowAlphanumericName = new InputFilter[1];
                    allowAlphanumericName[0] = stringFilter.allowAlphanumericName;

                    etReserverName.setFilters(allowAlphanumericName);
                    etReserverName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | etReserverName.getInputType());
                } else
                {
                    etReserverName.setEnabled(true);

                    // 회원 가입시 이름 필터 적용.
                    StringFilter stringFilter = new StringFilter(BookingActivity.this);
                    InputFilter[] allowAlphanumericHangul = new InputFilter[1];
                    allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

                    etReserverName.setFilters(allowAlphanumericHangul);
                    etReserverName.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                {
                    etReserverName.setBackground(mEditTextBackground[0]);
                } else
                {
                    etReserverName.setBackgroundDrawable(mEditTextBackground[0]);
                }
            }

            // 전화번호.
            etReserverNumber.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                etReserverNumber.setBackground(mEditTextBackground[1]);
            } else
            {
                etReserverNumber.setBackgroundDrawable(mEditTextBackground[1]);
            }

            etReserverNumber.setCursorVisible(false);
            etReserverNumber.setOnFocusChangeListener(new View.OnFocusChangeListener()
            {
                @Override
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (hasFocus == true)
                    {
                        showInputMobileNumberDialog(etReserverNumber.getText().toString());
                    } else
                    {
                        etReserverNumber.setSelected(false);
                    }
                }
            });

            View fakeMobileEditView = findViewById(R.id.fakeMobileEditView);

            fakeMobileEditView.setFocusable(true);
            fakeMobileEditView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (etReserverNumber.isSelected() == true)
                    {
                        showInputMobileNumberDialog(etReserverNumber.getText().toString());
                    } else
                    {
                        etReserverNumber.requestFocus();
                        etReserverNumber.setSelected(true);
                    }
                }
            });

            // 이메일.

            etReserverEmail.setEnabled(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                etReserverEmail.setBackground(mEditTextBackground[2]);
            } else
            {
                etReserverEmail.setBackgroundDrawable(mEditTextBackground[2]);
            }

            etReserverEmail.setOnEditorActionListener(new OnEditorActionListener()
            {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
                {
                    if (actionId == EditorInfo.IME_ACTION_DONE)
                    {
                        textView.clearFocus();

                        if (getWindow() == null || getWindow().getDecorView() == null || getWindow().getDecorView().getWindowToken() == null)
                        {
                            return false;
                        }

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            });
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Network Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserSessionBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            int msg_code = -1;

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

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
                    // 카드 관리 관련 화면을 보여주지 않는다.
                    mCardManagerButton.setVisibility(View.INVISIBLE);

                    mSelectedCreditCard = null;
                    mSimplePaymentRadioButton.setChecked(true);
                    mSimplePaymentRadioButton.setText(R.string.label_booking_easypayment);
                } else
                {
                    mCardManagerButton.setVisibility(View.VISIBLE);

                    if (mSelectedCreditCard == null)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
                        mSimplePaymentRadioButton.setChecked(true);
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

                    mSimplePaymentRadioButton.setText(String.format("%s %s", mSelectedCreditCard.name.replace("카드", ""), mSelectedCreditCard.number));
                }

                // 호텔 가격 정보가 변경되었습니다.
                if (mIsChangedPay == true)
                {
                    mIsChangedPay = false;
                    mWarningDialogMessage = null;

                    showChangedPayDialog();
                }


                if (mWarningDialogMessage != null && mWarningDialogMessage.length() > 0)
                {
                    showSimpleDialog(getString(R.string.dialog_notice2), mWarningDialogMessage, getString(R.string.dialog_btn_text_confirm), null, new OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                            mWarningDialogMessage = "";
                        }
                    });
                }
            } catch (Exception e)
            {
                // 해당 화면 에러시에는 일반 결제가 가능해야 한다.
                ExLog.e(e.toString());
                finish();
            } finally
            {
                onCheckedChanged(swCredit, swCredit.isChecked());
                unLockUI();
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
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

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
                    // 카드 관리 관련 화면을 보여주지 않는다.
                    mCardManagerButton.setVisibility(View.INVISIBLE);

                    mSelectedCreditCard = null;
                    mSimplePaymentRadioButton.setText(R.string.label_booking_easypayment);

                } else
                {
                    mCardManagerButton.setVisibility(View.VISIBLE);

                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
                    mSimplePaymentRadioButton.setText(String.format("%s %s", mSelectedCreditCard.name.replace("카드", ""), mSelectedCreditCard.number));

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

    private DailyHotelJsonResponseListener mHotelPaymentDetailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msgCode = response.getInt("msgCode");

                // 0	성공
                // 4	데이터가 없을시
                // 5	판매 마감시
                // 6	현재 시간부터 날짜 바뀌기 전시간(새벽 3시
                // 7    3시부터 9시까지
                switch (msgCode)
                {
                    case 6:
                    case 7:
                        if (mWarningDialogMessage == null)
                        {
                            if (response.has("msg") == true)
                            {
                                mWarningDialogMessage = response.getString("msg");
                            }
                        }
                    case 0:
                    {
                        JSONObject jsonData = response.getJSONObject("data");

                        long checkInDate = jsonData.getLong("check_in_date");
                        long checkOutDate = jsonData.getLong("check_out_date");
                        int discount = jsonData.getInt("discount_total");
                        boolean isOnSale = jsonData.getBoolean("on_sale");
                        int availableRooms = jsonData.getInt("available_rooms");

                        SaleRoomInformation saleRoomInformation = mPay.getSaleRoomInformation();

                        // 가격이 변동 되었다.
                        if (saleRoomInformation.totalDiscount != discount)
                        {
                            mIsChangedPay = true;
                        }

                        saleRoomInformation.totalDiscount = discount;

                        // Check In
                        Calendar calendarCheckin = DailyCalendar.getInstance();
                        calendarCheckin.setTimeZone(TimeZone.getTimeZone("GMT"));
                        calendarCheckin.setTimeInMillis(checkInDate);

                        SimpleDateFormat formatDay = new SimpleDateFormat("M월 d일 (EEE)", Locale.KOREA);
                        formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

                        SimpleDateFormat formatHour = new SimpleDateFormat(" HH시", Locale.KOREA);
                        formatHour.setTimeZone(TimeZone.getTimeZone("GMT"));

                        mCheckinDayTextView.setText(formatDay.format(calendarCheckin.getTime()));
                        mCheckinTimeTextView.setText(formatHour.format(calendarCheckin.getTime()));

                        // CheckOut
                        Calendar calendarCheckout = DailyCalendar.getInstance();
                        calendarCheckout.setTimeZone(TimeZone.getTimeZone("GMT"));
                        calendarCheckout.setTimeInMillis(checkOutDate);

                        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-hh", Locale.KOREA);
                        format.setTimeZone(TimeZone.getTimeZone("GMT"));

                        mPay.checkInTime = format.format(calendarCheckin.getTime());
                        mPay.checkOutTime = format.format(calendarCheckout.getTime());

                        mCheckoutDayTextView.setText(formatDay.format(calendarCheckout.getTime()));
                        mCheckoutTimeTextView.setText(formatHour.format(calendarCheckout.getTime()));

                        // 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
                        if (isOnSale == false || availableRooms == 0)
                        {
                            mWarningDialogMessage = null;

                            if (isFinishing() == true)
                            {
                                return;
                            }

                            showStopOnSaleDialog();
                        } else
                        {
                            // 3. 간편결제 credit card 요청
                            DailyNetworkAPI.getInstance().requestUserBillingCardList(mNetworkTag, mUserSessionBillingCardInfoJsonResponseListener, BookingActivity.this);
                        }
                        break;
                    }

                    case 5:
                    {
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                    finish();
                                }
                            });
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                    }

                    case 4:
                    default:
                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            DailyToast.showToast(BookingActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                }
            } catch (Exception e)
            {
                ExLog.e(e.toString());

                unLockUI();
                onError(e);

                finish();
            }
        }
    };

    private DailyHotelJsonResponseListener mHotelPaymentSessionEasy = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            hidePorgressDialog();

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                // 해당 화면은 메시지를 넣지 않는다.
                int msgCode = response.getInt("msgCode");
                String message = response.getString("msg");

                Intent intent = new Intent();
                intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);
                intent.putExtra(NAME_INTENT_EXTRA_DATA_MESSAGE, String.format("%d^%s", msgCode, message));

                mAliveCallSource = "ACTIVITY_RESULT";
                mReqCode = CODE_REQUEST_ACTIVITY_PAYMENT;

                activityResulted(CODE_REQUEST_ACTIVITY_PAYMENT, CODE_RESULT_ACTIVITY_PAYMENT_PRECHECK, intent);
            } catch (Exception e)
            {
                mDoReload = true;
                ExLog.e(e.toString());

                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mFinalCheckPayJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msgCode = response.getInt("msgCode");

                switch (msgCode)
                {
                    case 6:
                    case 7:
                    case 0:
                    {
                        JSONObject jsonData = response.getJSONObject("data");

                        long checkInDate = jsonData.getLong("check_in_date");
                        long checkOutDate = jsonData.getLong("check_out_date");
                        int discount = jsonData.getInt("discount_total");
                        boolean isOnSale = jsonData.getBoolean("on_sale");
                        int availableRooms = jsonData.getInt("available_rooms");

                        SaleRoomInformation saleRoomInformation = mPay.getSaleRoomInformation();

                        // 가격이 변동 되었다.
                        if (saleRoomInformation.totalDiscount != discount)
                        {
                            mIsChangedPay = true;
                        }

                        saleRoomInformation.totalDiscount = discount;

                        // 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
                        if (isOnSale == false || availableRooms == 0)
                        {
                            showStopOnSaleDialog();
                        } else if (mIsChangedPay == true)
                        {
                            mIsChangedPay = false;

                            // 현재 있는 팝업을 없애도록 한다.
                            if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
                            {
                                mFinalCheckDialog.cancel();
                                mFinalCheckDialog = null;
                            }

                            showChangedPayDialog();
                        } else
                        {
                            moveToPayStep();
                        }
                        break;
                    }

                    case 5:
                    {
                        mDoReload = true;

                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            showSimpleDialog(getString(R.string.dialog_notice2), msg, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    setResult(CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER);
                                    finish();
                                }
                            });
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                    }

                    case 4:
                    default:
                    {
                        mDoReload = true;

                        if (response.has("msg") == true)
                        {
                            String msg = response.getString("msg");

                            DailyToast.showToast(BookingActivity.this, msg, Toast.LENGTH_SHORT);
                            finish();
                        } else
                        {
                            throw new NullPointerException("response == null");
                        }
                        break;
                    }
                }
            } catch (Exception e)
            {
                mDoReload = true;

                ExLog.e(e.toString());

                onError(e);
                finish();
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code != 0)
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        DailyToast.showToast(BookingActivity.this, msg, Toast.LENGTH_SHORT);
                        finish();
                        return;
                    } else
                    {
                        throw new NullPointerException("response == null");
                    }
                }

                JSONObject jsonData = response.getJSONObject("data");

                boolean isOnSession = jsonData.getBoolean("on_session");

                if ("PAYMENT".equalsIgnoreCase(mAliveCallSource) == true)
                {
                    if (isOnSession == true)
                    {
                        int bonus = jsonData.getInt("user_bonus");

                        if (bonus < 0)
                        {
                            bonus = 0;
                        }

                        if (mPay.isSaleCredit() == true && bonus != mPay.credit)
                        {
                            mPay.credit = bonus;
                            showChangedBonusDialog();
                            return;
                        }

                        // 2. 마지막 가격 및 기타 이상이 없는지 검사
                        String params = String.format("?room_idx=%d&checkin_date=%s&nights=%d", mPay.getSaleRoomInformation().roomIndex, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"), mPay.getSaleRoomInformation().nights);
                        DailyNetworkAPI.getInstance().requestHotelPaymentInformation(mNetworkTag, params, mFinalCheckPayJsonResponseListener, BookingActivity.this);
                    } else
                    {
                        requestLogin();
                    }
                } else if ("ACTIVITY_RESULT".equalsIgnoreCase(mAliveCallSource) == true)
                {
                    unLockUI();

                    if (isOnSession == true)
                    {
                        activityResulted(mReqCode, mResCode, mResIntent);
                    } else
                    {
                        requestLogin();
                    }
                } else
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

                        mPay.credit = bonus;

                        if (mPay.isSaleCredit() == true)
                        {
                            updatePayPrice(true);
                        }

                        int originalPrice = mPay.getSaleRoomInformation().totalDiscount;
                        DecimalFormat comma = new DecimalFormat("###,##0");

                        tvOriginalPriceValue.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));
                        tvPrice.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));

                        mPay.setOriginalPrice(originalPrice);

                        Customer buyer = new Customer();
                        buyer.setEmail(email);
                        buyer.setName(name);
                        buyer.setPhone(phone);
                        buyer.setUserIdx(userIndex);

                        Guest guest = new Guest();
                        guest.name = name;
                        guest.phone = phone;
                        guest.email = email;

                        mPay.setCustomer(buyer);
                        mPay.setGuest(guest);

                        // 해외 호텔인 경우.
                        if (mPay.getSaleRoomInformation().isOverseas == true)
                        {
                            String overseasName = sharedPreference.getString(KEY_PREFERENCE_OVERSEAS_NAME, guest.name);
                            String overseasPhone = sharedPreference.getString(KEY_PREFERENCE_OVERSEAS_PHONE, guest.phone);
                            String overseasEmail = sharedPreference.getString(KEY_PREFERENCE_OVERSEAS_EMAIL, guest.email);

                            guest.name = overseasName;
                            guest.phone = overseasPhone;
                            guest.email = overseasEmail;

                            if (mIsEditMode == false)
                            {
                                if (Util.isNameCharacter(overseasName) == false)
                                {
                                    mIsEditMode = true;

                                    guest.name = "";
                                    etReserverName.setText("");
                                    etReserverName.setEnabled(true);

                                    // 회원 가입시 이름 필터 적용.
                                    StringFilter stringFilter = new StringFilter(BookingActivity.this);
                                    InputFilter[] allowAlphanumericName = new InputFilter[1];
                                    allowAlphanumericName[0] = stringFilter.allowAlphanumericName;

                                    etReserverName.setFilters(allowAlphanumericName);
                                    etReserverName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | etReserverName.getInputType());

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                    {
                                        etReserverName.setBackground(mEditTextBackground[0]);
                                    } else
                                    {
                                        etReserverName.setBackgroundDrawable(mEditTextBackground[0]);
                                    }
                                } else
                                {
                                    etReserverName.setText(overseasName);
                                }

                                etReserverNumber.setText(Util.addHippenMobileNumber(BookingActivity.this, overseasPhone));
                                etReserverEmail.setText(overseasEmail);
                            }
                        } else
                        {
                            if (mIsEditMode == false)
                            {
                                etReserverName.setText(guest.name);
                                etReserverNumber.setText(Util.addHippenMobileNumber(BookingActivity.this, guest.phone));
                                etReserverEmail.setText(guest.email);
                            }
                        }

                        String params = String.format("?room_idx=%d&checkin_date=%s&nights=%d", mPay.getSaleRoomInformation().roomIndex, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyyyMMdd"), mPay.getSaleRoomInformation().nights);

                        if (DEBUG == true)
                        {
                            showSimpleDialog(null, params, getString(R.string.dialog_btn_text_confirm), null);
                        }

                        // 2. 화면 정보 얻기
                        DailyNetworkAPI.getInstance().requestHotelPaymentInformation(mNetworkTag, params, mHotelPaymentDetailJsonResponseListener, BookingActivity.this);
                    } else
                    {
                        requestLogin();
                    }
                }
            } catch (Exception e)
            {
                mDoReload = true;
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                SaleTime saleTime = new SaleTime();

                saleTime.setCurrentTime(response.getLong("currentDateTime"));
                saleTime.setDailyTime(response.getLong("dailyDateTime"));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                mCSoperatingTimeMessage = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));

                lockUI();

                // 1. 세션이 연결되어있는지 검사.
                DailyNetworkAPI.getInstance().requestUserInformationForPayment(mNetworkTag, mUserInformationJsonResponseListener, BookingActivity.this);
            } catch (Exception e)
            {
                mDoReload = true;

                onError(e);
                unLockUI();

                finish();
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
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();

                        DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, BookingActivity.this);
                        return;
                    }
                }

                mDoReload = true;

                // 로그인을 실패하면 재로그인을 시도한다
                unLockUI();
                startLoginActivity();
            } catch (Exception e)
            {
                mDoReload = true;
                onError(e);
                unLockUI();

                finish();
            }
        }
    };
}
