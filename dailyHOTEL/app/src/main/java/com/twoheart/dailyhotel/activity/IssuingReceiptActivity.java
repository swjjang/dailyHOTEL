package com.twoheart.dailyhotel.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class IssuingReceiptActivity extends BaseActivity
{
    private int mBookingIdx;
    private boolean mIsFullscreen;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issuingreceipt);
        setActionBar(R.string.frag_issuing_receipt);

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

    @Override
    protected void onResume()
    {
        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, this);

        super.onResume();
    }

    @Override
    public void onBackPressed()
    {
        if (mIsFullscreen == true)
        {
            mIsFullscreen = false;
            updateFullscreenStatus(mIsFullscreen);
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
            JSONObject receipt = jsonObject.getJSONObject("receipt");

            String reservationIndex = jsonObject.getString("reservation_idx");
            String userName = receipt.getString("user_name");
            String userPhone = receipt.getString("user_phone");
            String checkin = receipt.getString("checkin");
            String checkout = receipt.getString("checkout");
            int nights = receipt.getInt("nights");
            int rooms = receipt.getInt("rooms");
            String hotelName = receipt.getString("hotel_name");
            String hotelAddress = receipt.getString("hotel_address");
            String valueDate = receipt.getString("value_date");
            String currency = receipt.getString("currency");
            int discount = receipt.getInt("discount");
            int vat = receipt.getInt("vat");
            int supoplyValue = receipt.getInt("supply_value");
            String paymentName = receipt.getString("payment_name");

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
        View actionBar = findViewById(R.id.toolbar_actionbar);
        View underLine = findViewById(R.id.toolbar_actionbarUnderLine);

        if (bUseFullscreen)
        {
            actionBar.setVisibility(View.INVISIBLE);
            underLine.setVisibility(View.INVISIBLE);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else
        {
            actionBar.setVisibility(View.VISIBLE);
            underLine.setVisibility(View.VISIBLE);

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
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    if (makeLayout(response.getJSONObject("data")) == false)
                    {
                        finish();
                        return;
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
    };
    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {

        @Override
        public void onResponse(String url, String response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            String result = null;

            if (Util.isTextEmpty(response) == false)
            {
                result = response.trim();
            }

            if ("alive".equalsIgnoreCase(result) == true)
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("reservation_idx", String.valueOf(mBookingIdx));

                if (DEBUG == true)
                {
                    showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
                }

                DailyNetworkAPI.getInstance().requestHotelReceipt(mNetworkTag, params, mReservReceiptJsonResponseListener, IssuingReceiptActivity.this);
            } else
            {
                finish();
            }
        }
    };
}
