package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class GourmetReceiptActivity extends PlaceReceiptActivity
{
    String mReservationIndex;

    void makeLayout(JSONObject jsonObject) throws Exception
    {
        // 영수증
        mReservationIndex = jsonObject.getString("gourmetReservationIdx");

        if (Constants.DEBUG == false && Util.isTextEmpty(mReservationIndex) == true)
        {
            Crashlytics.logException(new NullPointerException("GourmetReceiptActivity : mReservationIndex == null"));
        }

        String userName = jsonObject.getString("userName");
        String userPhone = jsonObject.getString("userPhone");
        int ticketCount = jsonObject.getInt("ticketCount");
        String placeName = jsonObject.getString("restaurantName");
        String placeAddress = jsonObject.getString("restaurantAddress");
        String sday = jsonObject.getString("sday");
        String valueDate = jsonObject.getString("paidAt");
        //        String currency = receiptJSONObject.getString("currency");
        int paymentAmount = jsonObject.getInt("paymentAmount");
        int tax = jsonObject.getInt("tax");
        int supplyPrice = jsonObject.getInt("supplyPrice");
        int sellingPrice = jsonObject.getInt("sellingPrice");
        String paymentType = jsonObject.getString("paymentType");
        int coupon = jsonObject.getInt("couponAmount");
        int bonus = 0;

        // **예약 세부 정보**
        View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

        // 예약 번호
        TextView reservationNumberTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
        reservationNumberTextView.setText(mReservationIndex);

        // 이름
        TextView hotelNameTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView3);
        hotelNameTextView.setText(placeName);

        // 주소
        TextView hotelAddressTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView5);
        hotelAddressTextView.setText(placeAddress);

        // 고객성명/번호
        TextView customerInfoTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView7);
        customerInfoTextView.setText(userName + " / " + userPhone);

        // 날짜
        TextView checkInOutTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView9);
        checkInOutTextView.setText(sday.replaceAll("-", "/"));

        // 수량
        TextView nightsRoomsTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView11);
        nightsRoomsTextView.setText(getString(R.string.label_booking_count, ticketCount));

        // **결제 정보**
        View paymentInfoLayout = findViewById(R.id.paymentInfoLayout);

        // 결제일
        TextView paymentDayTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView23);
        paymentDayTextView.setText(DailyCalendar.convertDateFormatString(valueDate, DailyCalendar.ISO_8601_FORMAT, "yyyy/MM/dd"));

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
        supplyValueTextView.setText(Util.getPriceFormat(this, supplyPrice, true));

        // 세금 및 수수료
        TextView vatTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView27);
        vatTextView.setText(Util.getPriceFormat(this, tax, true));

        View saleLayout = paymentInfoLayout.findViewById(R.id.saleLayout);
        saleLayout.setVisibility(View.VISIBLE);

        // 총금액
        TextView totalPriceTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
        totalPriceTextView.setText(Util.getPriceFormat(this, sellingPrice, true));

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

        if (coupon > 0)
        {
            couponLayout.setVisibility(View.VISIBLE);
            TextView couponTextView = (TextView) couponLayout.findViewById(R.id.couponTextView);
            couponTextView.setText("- " + Util.getPriceFormat(this, coupon, true));
        } else
        {
            couponLayout.setVisibility(View.GONE);
        }

        if (bonus > 0 || coupon > 0)
        {
            saleLayout.setVisibility(View.VISIBLE);
        } else
        {
            saleLayout.setVisibility(View.GONE);
        }

        // 총 입금 금액
        TextView totalPaymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
        totalPaymentTextView.setText(Util.getPriceFormat(this, paymentAmount, true));

        // **공급자**

        String phone = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyPhoneNumber();
        String fax = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyFax();
        String receiptNotice = jsonObject.getString("receiptNotice");
        String address = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyAddress();
        String ceoName = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyCEO();
        String registrationNo = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyBizRegNumber();
        String companyName = DailyPreference.getInstance(GourmetReceiptActivity.this).getRemoteConfigCompanyName();

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
        commentTextView.setText(receiptNotice);

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

    protected View getLayout()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewGroup = inflater.inflate(R.layout.activity_place_receipt, null, false);

        LinearLayout receiptLayout = (LinearLayout) viewGroup.findViewById(R.id.receiptLayout);

        View reservationInfoLayout = inflater.inflate(R.layout.layout_gourmet_reservationinfo_receipt, null, false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Util.dpToPx(this, 165));
        receiptLayout.addView(reservationInfoLayout, 0, layoutParams);

        return viewGroup;
    }

    protected void requestReceiptDetail(int index)
    {
        DailyMobileAPI.getInstance(this).requestGourmetReceipt(mNetworkTag, index, mReservationReceiptCallback);
    }

    void showSendEmailDialog()
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_email_layout, null, false);

        final Dialog dialog = new Dialog(this);
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

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
                {
                    DailyToast.showToast(GourmetReceiptActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                DailyMobileAPI.getInstance(GourmetReceiptActivity.this).requestReceiptByEmail(mNetworkTag, "gourmet", mReservationIndex, email, mReceiptByEmailCallback);
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

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
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

                    if (msgCode == 100)
                    {
                        makeLayout(responseJSONObject.getJSONObject("data"));
                    } else
                    {
                        onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"));
                    }
                } catch (Exception e)
                {
                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                GourmetReceiptActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            GourmetReceiptActivity.this.onError(t);
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
                    GourmetReceiptActivity.this.onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                GourmetReceiptActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            GourmetReceiptActivity.this.onError(t);
        }
    };
}
