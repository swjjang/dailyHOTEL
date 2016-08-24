package com.twoheart.dailyhotel.screen.booking.detail.hotel;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

public class IssuingReceiptActivity extends BaseActivity
{
    private int mBookingIdx;
    private boolean mIsFullscreen;
    private DailyToolbarLayout mDailyToolbarLayout;

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

            DailyNetworkAPI.getInstance(this).requestHotelReceipt(mNetworkTag, Integer.toString(mBookingIdx), mReservReceiptJsonResponseListener);
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

    private boolean makeLayout(JSONObject jsonObject)
    {
        try
        {
            // 영숭증
            JSONObject receiptJSONObject = jsonObject.getJSONObject("receipt");

            String reservationIndex = jsonObject.getString("reservation_idx");
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
            int supoplyValue = receiptJSONObject.getInt("supply_value");
            String paymentName = receiptJSONObject.getString("payment_name");

            int bonus = receiptJSONObject.getInt("bonus");
            int counpon = receiptJSONObject.getInt("coupon_amount");
            int pricePayment = receiptJSONObject.getInt("price");

            // **예약 세부 정보**
            View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

            // 예약 번호
            TextView registerationTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
            registerationTextView.setText(reservationIndex);

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

            // 지불 방식
            TextView paymentTypeTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView33);
            paymentTypeTextView.setText(paymentName);

            // 소계
            TextView supplyValueTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView25);
            supplyValueTextView.setText(Util.getPriceFormat(this, supoplyValue, true));

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
                TextView couponTextView = (TextView) paymentInfoLayout.findViewById(R.id.couponTextView);
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

            String phone = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyPhoneNumber();
            String fax = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyFax();
            String memo = provider.getString("memo");
            String address = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyAddress();
            String ceoName = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyCEO();
            String registrationNo = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyBizRegNumber();
            String companyName = DailyPreference.getInstance(IssuingReceiptActivity.this).getCompanyName();

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
            mDailyToolbarLayout.setToolbarVisibility(false);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else
        {
            mDailyToolbarLayout.setToolbarVisibility(true);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mReservReceiptJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
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
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    if (makeLayout(response.getJSONObject("data")) == false)
                    {
                        finish();
                    }
                } else
                {
                    if (isFinishing() == true)
                    {
                        return;
                    }

                    String msg = response.getString("msg");

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
                // 서버 정보를 파싱하다가 에러가 남.
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            IssuingReceiptActivity.this.onErrorResponse(volleyError);
        }
    };
}
