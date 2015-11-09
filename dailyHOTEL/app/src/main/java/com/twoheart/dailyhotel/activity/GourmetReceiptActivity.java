package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.text.DecimalFormat;

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
        String currency = receiptJSONObject.getString("currency");
        int discount = receiptJSONObject.getInt("discount");
        int vat = receiptJSONObject.getInt("vat");
        int supoplyValue = receiptJSONObject.getInt("supply_value");
        String paymentName = receiptJSONObject.getString("payment_name");

        // **예약 세부 정보**
        View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

        // 예약 번호
        TextView registerationTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView13);
        registerationTextView.setText(reservationIndex);

        // 호텔명
        TextView hotelNameTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView3);
        hotelNameTextView.setText(placeName);

        // 호텔주소
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

        DecimalFormat comma = new DecimalFormat("###,##0");

        // 소계
        TextView supplyValueTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView25);
        supplyValueTextView.setText("₩ " + comma.format(supoplyValue));

        // 세금 및 수수료
        TextView vatTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView27);
        vatTextView.setText("₩ " + comma.format(vat));

        // 총금액
        TextView discountTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
        discountTextView.setText("₩ " + comma.format(discount));

        // 지불 금액
        TextView paymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView31);
        paymentTextView.setText("₩ " + comma.format(discount));

        // 지불 방식
        TextView paymentTypeTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView33);
        paymentTypeTextView.setText(paymentName);

        // **공급자**

        JSONObject providerJSONObject = jsonObject.getJSONObject("provider");

        String phone = providerJSONObject.getString("phone");
        String fax = providerJSONObject.getString("fax");
        String memo = providerJSONObject.getString("memo");
        String address = providerJSONObject.getString("address");
        String ceoName = providerJSONObject.getString("ceoName");
        String registrationNo = providerJSONObject.getString("registrationNo");
        String companyName = providerJSONObject.getString("companyName");

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
        addressTextView.setText(address);

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
        String params = String.format("?reservation_rec_idx=%d", index);
        DailyNetworkAPI.getInstance().requestGourmetReceipt(mNetworkTag, params, mReservReceiptJsonResponseListener, this);
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
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    makeLayout(response.getJSONObject("data"));
                } else
                {
                    if (response.has("msg") == true)
                    {
                        String msg = response.getString("msg");

                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                finish();
                            }
                        }, null, false);
                        return;
                    } else
                    {
                        onInternalError();
                    }
                }
            } catch (Exception e)
            {
                onInternalError();
            } finally
            {
                unLockUI();
            }
        }
    };

}
