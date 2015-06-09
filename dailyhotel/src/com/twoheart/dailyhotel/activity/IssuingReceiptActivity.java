package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class IssuingReceiptActivity extends WebViewActivity
{
	private int mBookingIdx;

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
	}

	private boolean makeLayout(JSONObject jsonObject)
	{
		try
		{
			String userName = jsonObject.getString("user_name");
			String userPhone = jsonObject.getString("user_phone");
			String checkin = jsonObject.getString("checkin");
			String checkout = jsonObject.getString("checkout");
			int nights = jsonObject.getInt("nights");
			int rooms = jsonObject.getInt("rooms");
			String hotelName = jsonObject.getString("hotel_name");
			String hotelAddress = jsonObject.getString("hotel_address");
			String valueDate = jsonObject.getString("value_date");
			String currency = jsonObject.getString("currency");
			int discount = jsonObject.getInt("discount");
			int vat = jsonObject.getInt("vat");
			int supoplyValue = jsonObject.getInt("supply_value");
			String paymentName = jsonObject.getString("payment_name");

			// **예약 세부 정보**
			View bookingInfoLayout = findViewById(R.id.bookingInfoLayout);

			// 호텔명
			TextView hotelNameTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView3);
			hotelNameTextView.setText(hotelName);

			// 호텔주소
			TextView hotelAddressTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView5);
			hotelAddressTextView.setText(hotelAddress);

			// 고객성명/번호
			TextView customerInfoTextView = (TextView) bookingInfoLayout.findViewById(R.id.textView7);
			customerInfoTextView.setText(userName + " / " + userPhone);

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

			// 소개
			TextView supplyValueTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView25);
			supplyValueTextView.setText("₩" + comma.format(supoplyValue));

			// 세금 및 수수료
			TextView vatTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView27);
			vatTextView.setText("₩" + comma.format(vat));

			// 총금액
			TextView discountTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView29);
			discountTextView.setText("₩" + comma.format(discount));

			// 지불 금액
			TextView paymentTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView31);
			paymentTextView.setText("₩" + comma.format(discount));

			// 지불 방식
			TextView paymentTypeTextView = (TextView) paymentInfoLayout.findViewById(R.id.textView33);
			paymentTypeTextView.setText(paymentName);

			// **공급자**
			View providerInfoLayout = findViewById(R.id.providerInfoLayout);

		} catch (Exception e)
		{
			return false;
		}

		return true;
	}

	@Override
	protected void onResume()
	{
		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));

		super.onResume();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

			if (TextUtils.isEmpty(response) == false)
			{
				result = response.trim();
			}

			if ("alive".equalsIgnoreCase(result) == true)
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("reservation_idx", String.valueOf(mBookingIdx));

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_RECEIPT).toString(), params, mReservReceiptJsonResponseListener, IssuingReceiptActivity.this));
			} else
			{
				finish();
			}
		}
	};

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
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code == 0)
				{
					if (makeLayout(response.getJSONObject("data")) == false)
					{
						finish();
					}
				} else
				{
					String msg = response.getString("msg");

					AlertDialog alertDialog = SimpleAlertDialog.build(IssuingReceiptActivity.this, null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null).create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener()
					{
						@Override
						public void onDismiss(DialogInterface dialog)
						{
							finish();
						}
					});

					alertDialog.show();
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
}
