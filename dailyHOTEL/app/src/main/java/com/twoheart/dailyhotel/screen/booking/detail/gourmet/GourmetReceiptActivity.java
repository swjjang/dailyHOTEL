package com.twoheart.dailyhotel.screen.booking.detail.gourmet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

public class GourmetReceiptActivity extends PlaceReceiptActivity
{
    private void makeLayout(JSONObject jsonObject) throws Exception
    {
        JSONObject receiptJSONObject = jsonObject.getJSONObject("receipt");

        // 영숭증
        String reservationIndex = jsonObject.getString("reservation_idx");
        String userName = receiptJSONObject.getString("user_name");
        String userPhone = receiptJSONObject.getString("user_phone");
        int ticketCount = receiptJSONObject.getInt("ticket_count");
        String placeName = receiptJSONObject.getString("restaurant_name");
        String placeAddress = receiptJSONObject.getString("restaurant_address");
        String sday = receiptJSONObject.getString("sday");
        String valueDate = receiptJSONObject.getString("value_date");
        //        String currency = receiptJSONObject.getString("currency");
        int discount = receiptJSONObject.getInt("discount");
        int vat = receiptJSONObject.getInt("vat");
        int supoplyValue = receiptJSONObject.getInt("supply_value");
        String paymentName = receiptJSONObject.getString("payment_name");

        int bonus = 0;
        int counpon = 0;
        int totalPayment = 0;

        // **예약 세부 정보**
        View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

        // 예약 번호
        TextView registerationTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
        registerationTextView.setText(reservationIndex);

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
        TextView chekcinoutTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView9);
        chekcinoutTextView.setText(sday);

        // 수량
        TextView nightsRoomsTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView11);
        nightsRoomsTextView.setText(getString(R.string.label_booking_count, ticketCount));

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

        // 추후 쿠폰 지원을 위해서..
        if (bonus == 0 && counpon == 0)
        {
            saleLayout.setVisibility(View.GONE);

            // 총 입금 금액
            TextView totalPaymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
            totalPaymentTextView.setText(Util.getPriceFormat(this, discount, true));
        } else
        {
            saleLayout.setVisibility(View.VISIBLE);

            // 총금액
            TextView discountTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
            discountTextView.setText(Util.getPriceFormat(this, discount, true));

            // 적립금 사용
            TextView bonusTextView = (TextView) paymentInfoLayout.findViewById(R.id.bonusTextView);
            bonusTextView.setText(Util.getPriceFormat(this, bonus, true));

            // 할인쿠폰 사용
            TextView couponTextView = (TextView) paymentInfoLayout.findViewById(R.id.couponTextView);
            couponTextView.setText(Util.getPriceFormat(this, counpon, true));

            // 총 입금 금액
            TextView totalPaymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.totalPaymentTextView);
            totalPaymentTextView.setText(Util.getPriceFormat(this, totalPayment, true));
        }

        // **공급자**

        JSONObject providerJSONObject = jsonObject.getJSONObject("provider");

        String phone = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyPhoneNumber();
        String fax = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyFax();
        String memo = providerJSONObject.getString("memo");
        String address = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyAddress();
        String ceoName = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyCEO();
        String registrationNo = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyBizRegNumber();
        String companyName = DailyPreference.getInstance(GourmetReceiptActivity.this).getCompanyName();

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
        DailyNetworkAPI.getInstance(this).requestGourmetReceipt(mNetworkTag, index, mReservReceiptJsonResponseListener);
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

            try
            {
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    makeLayout(response.getJSONObject("data"));
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"));
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            GourmetReceiptActivity.this.onErrorResponse(volleyError);
        }
    };
}
