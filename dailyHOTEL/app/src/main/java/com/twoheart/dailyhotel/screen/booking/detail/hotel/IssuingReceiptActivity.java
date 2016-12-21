package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class IssuingReceiptActivity extends BaseActivity
{
    private int mBookingIdx;
    private String mReservationIndex;
    private boolean mIsFullscreen;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mBottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuingreceipt);

        initToolbar();

        Intent intent = getIntent();

        mBookingIdx = -1;

        if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX) == true)
        {
            mBookingIdx = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, -1);
        }

        if (mBookingIdx < 0)
        {
            finish();
            return;
        }

        mIsFullscreen = false;

        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        mDailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        mDailyToolbarLayout.initToolbar(getString(R.string.frag_issuing_receipt), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        // 영수증 다음 버전으로
        mBottomLayout = findViewById(R.id.bottomLayout);
        View sendEmailView = mBottomLayout.findViewById(R.id.sendEmailView);
        sendEmailView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Util.isTextEmpty(mReservationIndex) == true)
                {
                    restartExpiredSession();
                } else
                {
                    showSendEmailDialog();
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.BOOKING_DETAIL_RECEIPT);

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == false)
        {
            restartExpiredSession();
        } else
        {
            lockUI();

            DailyMobileAPI.getInstance(this).requestStayReceipt(mNetworkTag, Integer.toString(mBookingIdx), mReservReceiptCallback);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mIsFullscreen == true)
        {
            mIsFullscreen = false;
            updateFullscreenStatus(false);
        } else
        {
            super.onBackPressed();
        }
    }

    private void showSendEmailDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_email_layout, null, false);

        final Dialog dialog = new Dialog(IssuingReceiptActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final DailyEditText emailEditTExt = (DailyEditText) dialogView.findViewById(R.id.emailEditTExt);
        emailEditTExt.setDeleteButtonVisible(true, new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        emailEditTExt.setText(DailyPreference.getInstance(this).getUserEmail());
        emailEditTExt.setSelection(emailEditTExt.length());

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        twoButtonLayout.setVisibility(View.VISIBLE);

        TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
        final TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = emailEditTExt.getText().toString();

                if (Util.isTextEmpty(email) == false)
                {
                    // 이메일로 영수증 전송하기

                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
                    {
                        DailyToast.showToast(IssuingReceiptActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                        return;
                    }

                    if (dialog != null && dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    DailyMobileAPI.getInstance(IssuingReceiptActivity.this).requestReceiptByEmail(mNetworkTag, "stay", mReservationIndex, email, mReceiptByEmailCallback);
                }
            }
        });

        emailEditTExt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable == null || editable.length() == 0)
                {
                    positiveTextView.setEnabled(false);
                } else
                {
                    positiveTextView.setEnabled(true);
                }
            }
        });

        dialog.setCancelable(true);

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(dialog.getWindow().getAttributes());
            params.width = Util.getLCDWidth(this) * 13 / 15 ;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.show();

            dialog.getWindow().setAttributes(params);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private boolean makeLayout(JSONObject jsonObject)
    {
        try
        {
            // 영수증
            JSONObject receiptJSONObject = jsonObject.getJSONObject("receipt");

            mReservationIndex = jsonObject.getString("reservation_idx");

            if (Constants.DEBUG == false && Util.isTextEmpty(mReservationIndex) == true)
            {
                Crashlytics.logException(new NullPointerException("IssuingReceiptActivity : mReservationIndex == null"));
            }

            String userName = receiptJSONObject.getString("user_name");
            String userPhone = receiptJSONObject.getString("user_phone");
            String checkin = receiptJSONObject.getString("checkin");
            String checkout = receiptJSONObject.getString("checkout");
            int nights = receiptJSONObject.getInt("nights");
            int rooms = receiptJSONObject.getInt("rooms");
            String hotelName = receiptJSONObject.getString("hotel_name");
            String hotelAddress = receiptJSONObject.getString("hotel_address");
            String valueDate = receiptJSONObject.getString("value_date");
            //            String currency = receiptJSONObject.getString("currency");
            int discount = receiptJSONObject.getInt("discount");
            int vat = receiptJSONObject.getInt("vat");
            int supplyValue = receiptJSONObject.getInt("supply_value");
            String paymentType = receiptJSONObject.getString("payment_name");

            int bonus = receiptJSONObject.getInt("bonus");
            int counpon = receiptJSONObject.getInt("coupon_amount");
            int pricePayment = receiptJSONObject.getInt("price");

            // **예약 세부 정보**
            View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

            // 예약 번호
            TextView registerationTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
            registerationTextView.setText(mReservationIndex);

            // 호텔명
            TextView hotelNameTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView3);
            hotelNameTextView.setText(hotelName);

            // 호텔주소
            TextView hotelAddressTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView5);
            hotelAddressTextView.setText(hotelAddress);

            // 고객성명/번호
            TextView customerInfoTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView7);
            customerInfoTextView.setText(userName + " / " + Util.addHippenMobileNumber(IssuingReceiptActivity.this, userPhone));

            // 체크인/아웃
            TextView chekcinoutTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView9);
            chekcinoutTextView.setText(checkin + " - " + checkout);

            // 숙박 일수/객실수
            TextView nightsRoomsTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView11);
            nightsRoomsTextView.setText(nights + "일/" + rooms + "객실");

            // **결제 정보**
            View paymentInfoLayout = findViewById(R.id.paymentInfoLayout);

            // 결제일
            TextView paymentDayTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView23);
            paymentDayTextView.setText(valueDate);

            // 결제수단
            View paymentTypeLayout = paymentInfoLayout.findViewById(R.id.paymentTypeLayout);

            if (Util.isTextEmpty(paymentType) == true)
            {
                paymentTypeLayout.setVisibility(View.GONE);
            } else
            {
                paymentTypeLayout.setVisibility(View.VISIBLE);

                TextView paymentTypeTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView33);
                paymentTypeTextView.setText(paymentType);
            }

            // 소계
            TextView supplyValueTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView25);
            supplyValueTextView.setText(Util.getPriceFormat(this, supplyValue, true));

            // 세금 및 수수료
            TextView vatTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView27);
            vatTextView.setText(Util.getPriceFormat(this, vat, true));

            View saleLayout = paymentInfoLayout.findViewById(R.id.saleLayout);
            saleLayout.setVisibility(View.VISIBLE);

            // 총금액
            TextView totalPriceTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
            totalPriceTextView.setText(Util.getPriceFormat(this, discount, true));

            // 적립금 사용
            View bonusLayout = paymentInfoLayout.findViewById(R.id.bonusLayout);

            if (bonus > 0)
            {
                bonusLayout.setVisibility(View.VISIBLE);
                TextView bonusTextView = (TextView) paymentInfoLayout.findViewById(R.id.bonusTextView);
                bonusTextView.setText("- " + Util.getPriceFormat(this, bonus, true));
            } else
            {
                bonusLayout.setVisibility(View.GONE);
            }

            // 할인쿠폰 사용
            View couponLayout = paymentInfoLayout.findViewById(R.id.couponLayout);

            if (counpon > 0)
            {
                couponLayout.setVisibility(View.VISIBLE);
                TextView couponTextView = (TextView) couponLayout.findViewById(R.id.couponTextView);
                couponTextView.setText("- " + Util.getPriceFormat(this, counpon, true));
            } else
            {
                couponLayout.setVisibility(View.GONE);
            }

            if (bonus > 0 || counpon > 0)
            {
                saleLayout.setVisibility(View.VISIBLE);
            } else
            {
                saleLayout.setVisibility(View.GONE);
            }

            // 총 입금(실 결제) 금액
            TextView totalPaymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
            totalPaymentTextView.setText(Util.getPriceFormat(this, pricePayment, true));

            // **공급자**

            JSONObject provider = jsonObject.getJSONObject("provider");

            String phone = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyPhoneNumber();
            String fax = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyFax();
            String memo = provider.getString("memo");
            String address = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyAddress();
            String ceoName = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyCEO();
            String registrationNo = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyBizRegNumber();
            String companyName = DailyPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyName();

            View providerInfoLayout = findViewById(R.id.providerInfoLayout);

            // 상호
            TextView companyNameTextView = (TextView) providerInfoLayout.findViewById(R.id.textView42);
            companyNameTextView.setText(getString(R.string.label_receipt_business_license, companyName));

            // 등록번호
            TextView registrationNoTextView = (TextView) providerInfoLayout.findViewById(R.id.textView43);
            registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

            // 대표자
            TextView ceoNameTextView = (TextView) providerInfoLayout.findViewById(R.id.textView44);
            ceoNameTextView.setText(getString(R.string.label_receipt_ceo, ceoName));

            // 주소
            TextView addressTextView = (TextView) providerInfoLayout.findViewById(R.id.textView46);
            addressTextView.setText(getString(R.string.label_receipt_address, address));

            // 전화번호
            TextView phoneTextView = (TextView) providerInfoLayout.findViewById(R.id.textView47);
            phoneTextView.setText(getString(R.string.label_receipt_phone, phone));

            // 팩스
            TextView faxTextView = (TextView) providerInfoLayout.findViewById(R.id.textView48);
            faxTextView.setText(getString(R.string.label_receipt_fax, fax));

            // 코멘트
            TextView commentTextView = (TextView) providerInfoLayout.findViewById(R.id.textView49);
            commentTextView.setText(memo);

        } catch (Exception e)
        {
            return false;
        }

        View view = findViewById(R.id.receiptLayout);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mIsFullscreen = !mIsFullscreen;
                updateFullscreenStatus(mIsFullscreen);
            }
        });

        return true;
    }

    private void updateFullscreenStatus(boolean bUseFullscreen)
    {
        if (bUseFullscreen)
        {
            mDailyToolbarLayout.setToolbarVisibility(false, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

            mBottomLayout.setVisibility(View.GONE);
        } else
        {
            mDailyToolbarLayout.setToolbarVisibility(true, false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            mBottomLayout.setVisibility(View.VISIBLE);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mReservReceiptCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                if (isFinishing() == true)
                {
                    return;
                }

                //			msg_code : 0
                //			data :
                //			- [String] user_name /* 유저 이름 */
                //			- [String] user_phone /* 유저 번호 */
                //			- [String] checkin /* 체크인 날짜(yyyy/mm/dd) */
                //			- [String] checkout /* 체크아웃 날짜(yyyy/mm/dd) */
                //			- [int] nights /* 연박 일수 */
                //			- [int] rooms /* 객실수 */
                //			- [String] hotel_name /* 호텔 명 */
                //			- [String] hotel_address /* 호텔 주소 */
                //			- [String] value_date(yyyy/mm/dd) /* 결제일 */
                //			- [String] currency /* 화폐 단위 */
                //			- [int] discount /* 결제 금액 */
                //			- [int] vat /* 부가세 */
                //			- [int] supply_value /* 공급가액 */
                //			- [String] payment_name /* 결제수단 */
                //			---------------------------------

                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (msgCode == 0)
                    {
                        if (makeLayout(responseJSONObject.getJSONObject("data")) == false)
                        {
                            finish();
                        }
                    } else
                    {
                        if (isFinishing() == true)
                        {
                            return;
                        }

                        String msg = responseJSONObject.getString("msg");

                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                        {
                            @Override
                            public void onDismiss(DialogInterface dialog)
                            {
                                finish();
                            }
                        });
                    }
                } catch (Exception e)
                {
                    IssuingReceiptActivity.this.onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                IssuingReceiptActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            IssuingReceiptActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mReceiptByEmailCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");
                    String message = responseJSONObject.getString("msg");

                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                } catch (Exception e)
                {
                    IssuingReceiptActivity.this.onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                IssuingReceiptActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            IssuingReceiptActivity.this.onError(t);
        }
    };
}
