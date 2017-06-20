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
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class IssuingReceiptActivity extends BaseActivity
{
    private int mBookingIdx;
    String mReservationIndex;
    boolean mIsFullscreen;
    private DailyToolbarLayout mDailyToolbarLayout;
    private View mBottomLayout;

    public static Intent newInstance(Context context, int bookingIndex)
    {
        Intent intent = new Intent(context, IssuingReceiptActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKINGIDX, bookingIndex);

        return intent;
    }

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
                if (DailyTextUtils.isTextEmpty(mReservationIndex) == true)
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
        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.BOOKING_DETAIL_RECEIPT, null);

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

            DailyMobileAPI.getInstance(this).requestStayReceipt(mNetworkTag, Integer.toString(mBookingIdx), mReservationReceiptCallback);
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

    void showSendEmailDialog()
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
        emailEditTExt.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(dailyEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        emailEditTExt.setText(DailyUserPreference.getInstance(this).getEmail());
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

                if (DailyTextUtils.isTextEmpty(email) == false)
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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    boolean makeLayout(JSONObject jsonObject)
    {
        try
        {
            // 영수증
            JSONObject receiptJSONObject = jsonObject.getJSONObject("receipt");

            mReservationIndex = jsonObject.getString("reservation_idx");

            if (Constants.DEBUG == false && DailyTextUtils.isTextEmpty(mReservationIndex) == true)
            {
                Crashlytics.logException(new NullPointerException("IssuingReceiptActivity : mReservationIndex == null"));
            }

            String userName = receiptJSONObject.getString("user_name");
            String userPhone = receiptJSONObject.getString("user_phone");
            String checkIn = receiptJSONObject.getString("checkin");
            String checkOut = receiptJSONObject.getString("checkout");
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
            int coupon = receiptJSONObject.getInt("coupon_amount");
            int pricePayment = receiptJSONObject.getInt("price");

            // **예약 세부 정보**
            View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

            // 예약 번호
            TextView reservationNumberTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
            reservationNumberTextView.setText(mReservationIndex);

            // 호텔명
            TextView hotelNameTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView3);
            hotelNameTextView.setText(hotelName);

            // 호텔주소
            TextView hotelAddressTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView5);
            hotelAddressTextView.setText(hotelAddress);

            // 고객성명/번호
            TextView customerInfoTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView7);
            customerInfoTextView.setText(userName + " / " + Util.addHyphenMobileNumber(IssuingReceiptActivity.this, userPhone));

            // 체크인/아웃
            TextView checkInOutTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView9);
            checkInOutTextView.setText(checkIn + " - " + checkOut);

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

            if (DailyTextUtils.isTextEmpty(paymentType) == true)
            {
                paymentTypeLayout.setVisibility(View.GONE);
            } else
            {
                paymentTypeLayout.setVisibility(View.VISIBLE);

                TextView paymentTypeTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView33);
                paymentTypeTextView.setText(paymentType);
            }

            View saleLayout = paymentInfoLayout.findViewById(R.id.saleLayout);
            saleLayout.setVisibility(View.VISIBLE);

            // 총금액
            TextView totalPriceTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
            totalPriceTextView.setText(DailyTextUtils.getPriceFormat(this, discount, false));

            // 적립금 혹은 쿠폰 사용
            View discountLayout = paymentInfoLayout.findViewById(R.id.discountLayout);

            if (bonus > 0 || coupon > 0)
            {
                if (bonus < 0)
                {
                    bonus = 0;
                }

                if (coupon < 0)
                {
                    coupon = 0;
                }

                discountLayout.setVisibility(View.VISIBLE);
                TextView discountedTextView = (TextView) paymentInfoLayout.findViewById(R.id.discountedTextView);
                discountedTextView.setText("- " + DailyTextUtils.getPriceFormat(this, bonus + coupon, false));
            } else
            {
                discountLayout.setVisibility(View.GONE);
            }

            if (bonus > 0 || coupon > 0)
            {
                saleLayout.setVisibility(View.VISIBLE);
            } else
            {
                saleLayout.setVisibility(View.GONE);
            }

            // 총 입금(실 결제) 금액
            TextView totalPaymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
            totalPaymentTextView.setText(DailyTextUtils.getPriceFormat(this, pricePayment, false));

            // **공급자**

            JSONObject provider = jsonObject.getJSONObject("provider");

            String phone = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyPhoneNumber();
            String fax = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyFax();
            String memo = provider.getString("memo");
            String address = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyAddress();
            String ceoName = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyCEO();
            String registrationNo = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyBizRegNumber();
            String companyName = DailyRemoteConfigPreference.getInstance(IssuingReceiptActivity.this).getRemoteConfigCompanyName();

            View providerInfoLayout = findViewById(R.id.providerInfoLayout);

            // 상호
            TextView companyNameTextView = (TextView) providerInfoLayout.findViewById(R.id.companyNameTextView);
            companyNameTextView.setText(getString(R.string.label_receipt_business_license, companyName, ceoName, phone, fax));

            // 주소
            TextView addressTextView = (TextView) providerInfoLayout.findViewById(R.id.addressTextView);
            addressTextView.setText(getString(R.string.label_receipt_address, address));


            // 등록번호
            TextView registrationNoTextView = (TextView) providerInfoLayout.findViewById(R.id.registrationNoTextView);
            registrationNoTextView.setText(getString(R.string.label_receipt_registeration_number, registrationNo));

            // 코멘트
            TextView commentTextView = (TextView) findViewById(R.id.commentTextView);
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

    void updateFullscreenStatus(boolean bUseFullscreen)
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

    private retrofit2.Callback mReservationReceiptCallback = new retrofit2.Callback<JSONObject>()
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

    retrofit2.Callback mReceiptByEmailCallback = new retrofit2.Callback<JSONObject>()
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
