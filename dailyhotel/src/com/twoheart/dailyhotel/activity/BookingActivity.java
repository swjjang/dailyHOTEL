package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.Switch;

@SuppressLint({ "NewApi", "ResourceAsColor" })
public class BookingActivity extends BaseActivity implements
		DailyHotelStringResponseListener, DailyHotelJsonResponseListener, OnClickListener, OnCheckedChangeListener,
		android.widget.CompoundButton.OnCheckedChangeListener {

	private static final String TAG = "HotelPaymentActivity";

	private ScrollView svBooking;
	private TextView tvCheckIn, tvCheckOut, tvOriginalPriceValue,
			tvCreditValue, tvOriginalPrice, tvCredit, tvPrice;
	private Button btnPay;
	private Switch swCredit;
	private TextView tvReserverName, tvReserverNumber, tvReserverEmail;
	private LinearLayout llReserverInfoLabel, llReserverInfoEditable;
	private EditText etReserverName, etReserverNumber, etReserverEmail;
	private RadioGroup rgPaymentMethod;
	private RadioButton rbPaymentAccount, rbPaymentCard;
	private TextView tvPaymentInformation;

	private Pay mPay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);
		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		mPay = new Pay();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mPay.setHotelDetail((HotelDetail) bundle
					.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL));
		}

		setActionBar(mPay.getHotelDetail().getHotel().getName());

		svBooking = (ScrollView) findViewById(R.id.sv_booking);
		
		tvCheckIn = (TextView) findViewById(R.id.tv_hotel_payment_checkin);
		tvCheckOut = (TextView) findViewById(R.id.tv_hotel_payment_checkout);
		tvOriginalPrice = (TextView) findViewById(R.id.tv_hotel_payment_original_price);
		tvCredit = (TextView) findViewById(R.id.tv_hotel_payment_credit);
		tvOriginalPriceValue = (TextView) findViewById(R.id.tv_hotel_payment_original_price_value);
		tvCreditValue = (TextView) findViewById(R.id.tv_hotel_payment_credit_value);
		tvPrice = (TextView) findViewById(R.id.tv_hotel_payment_price);
		btnPay = (Button) findViewById(R.id.btn_hotel_payment);
		swCredit = (Switch) findViewById(R.id.btn_on_off);

		tvReserverName = (TextView) findViewById(R.id.tv_hotel_payment_reserver_name);
		tvReserverNumber = (TextView) findViewById(R.id.tv_hotel_payment_reserver_number);
		tvReserverEmail = (TextView) findViewById(R.id.tv_hotel_payment_reserver_email);

		llReserverInfoLabel = (LinearLayout) findViewById(R.id.ll_reserver_info_label);
		llReserverInfoEditable = (LinearLayout) findViewById(R.id.ll_reserver_info_editable);

		etReserverName = (EditText) findViewById(R.id.et_hotel_payment_reserver_name);
		etReserverNumber = (EditText) findViewById(R.id.et_hotel_payment_reserver_number);
		etReserverEmail = (EditText) findViewById(R.id.et_hotel_payment_reserver_email);

		rgPaymentMethod = (RadioGroup) findViewById(R.id.rg_payment_method);
		rbPaymentAccount = (RadioButton) findViewById(R.id.rb_payment_account);
		rbPaymentCard = (RadioButton) findViewById(R.id.rb_payment_card);

		tvPaymentInformation = (TextView) findViewById(R.id.tv_payment_information);
		
		rbPaymentAccount.setOnClickListener(this);
		rbPaymentCard.setOnClickListener(this);

		rgPaymentMethod.setOnCheckedChangeListener(this);
		btnPay.setOnClickListener(this);
		swCredit.setOnCheckedChangeListener(this);

		rbPaymentCard.setChecked(true);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 적립금 스위치 초기화
		swCredit.setChecked(false);
		
		lockUI();
		// credit 요청
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_SAVED_MONEY)
				.toString(), null, this, this));
	}

	private void updatePayPrice(boolean applyCredit) {

		int originalPrice = Integer.parseInt(mPay.getHotelDetail().getHotel()
				.getDiscount().replaceAll(",", ""));
		int credit = Integer.parseInt(mPay.getCredit().getBonus()
				.replaceAll(",", ""));

		DecimalFormat comma = new DecimalFormat("###,##0");
		tvOriginalPriceValue.setText("￦" + comma.format(originalPrice));

		if (applyCredit) {
			mPay.setPayPrice(originalPrice - credit);

		} else {
			mPay.setPayPrice(originalPrice);

		}

		tvPrice.setText("￦" + comma.format(mPay.getPayPrice()));

	}

	public void dialog(String str) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); // 닫기
			}
		});
		alert.setMessage(str);
		alert.show();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == btnPay.getId()) {

			if (rgPaymentMethod.getCheckedRadioButtonId() == rbPaymentAccount
					.getId()) { // 무통장 입금을 선택했을 경우

				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setPositiveButton("전화",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(
										Intent.ACTION_DIAL,
										Uri.parse(new StringBuilder("tel:")
												.append(PHONE_NUMBER_DAILYHOTEL)
												.toString()));
								startActivity(i);
							}
						});
				alert.setNegativeButton("취소",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss(); // 닫기
							}
						});

				alert.setMessage("무통장 입금은 전화 통화를 통해 진행됩니다. 입금 순서에 따라 예약되며, 예약 확정 후 문자가 도착합니다.");
				alert.show();

			} else if (rgPaymentMethod.getCheckedRadioButtonId() == rbPaymentCard
					.getId()) { // 신용카드를 선택했을 경우
				
				Customer buyer = mPay.getCustomer();

				if (llReserverInfoEditable.getVisibility() == View.VISIBLE) {

					buyer.setEmail(
							etReserverEmail.getText().toString());
					buyer.setPhone(
							etReserverNumber.getText().toString());
					buyer.setName(
							etReserverName.getText().toString());

					if (isEmptyTextField(new String[] {
							buyer.getEmail(),
							buyer.getPhone(),
							buyer.getName() })) {
						
						showToast("예약자와 연락처, 이메일을 모두 입력해주십시요.", Toast.LENGTH_LONG, true);
						
						return;
					}
					
				} else if (llReserverInfoLabel.getVisibility() == View.VISIBLE) {

					buyer.setEmail(
							tvReserverEmail.getText().toString());
					buyer.setPhone(
							tvReserverNumber.getText().toString());
					buyer.setName(
							tvReserverName.getText().toString());
					
				}

				mPay.setCustomer(buyer);
				moveToPayStep();

			}
		} else if (v.getId() == rbPaymentAccount.getId() | v.getId() == rbPaymentCard.getId()) {
			svBooking.fullScroll(View.FOCUS_DOWN);
			
		}
	}

	private boolean isEmptyTextField(String... fieldText) {

		for (int i = 0; i < fieldText.length; i++) {
			if (fieldText[i] == null || fieldText[i].equals("")
					|| fieldText[i].equals("null"))
				return true;
		}

		return false;

	}
	
	private void moveToPayStep() {
		Intent intent = new Intent(this, PaymentActivity.class);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);
		startActivityForResult(intent,
				CODE_REQUEST_ACTIVITY_PAYMENT);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		
	}
	
	private void moveToLoginProcess() {
		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN,
				false)) {

			String id = sharedPreference.getString(
					KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(
					KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(
					KEY_PREFERENCE_USER_PWD, null);

			Map<String, String> loginParams = new HashMap<String, String>();

			if (accessToken != null) {
				loginParams.put("accessToken", accessToken);
			} else {
				loginParams.put("email", id);
			}

			loginParams.put("pw", pw);

			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_LOGIN).toString(),
					loginParams, this, this));
		} else {
			unLockUI();
			showToast("다시 로그인해주세요", Toast.LENGTH_LONG, false);
			
			startActivityForResult(new Intent(this, LoginActivity.class),
					CODE_REQUEST_ACTIVITY_LOGIN);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
		
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT) {
			Log.d(TAG, Integer.toString(resultCode));

			switch (resultCode) {
			case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
			case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
				if (intent != null) {
					if (intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY) != null) {
						Pay payData = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY);

						Editor editor = sharedPreference.edit();
						editor.putString(KEY_PREFERENCE_HOTEL_NAME, payData.getHotelDetail().getHotel().getName());
						editor.putInt(KEY_PREFERENCE_HOTEL_SALE_IDX, payData.getHotelDetail().getSaleIdx());
						editor.putString(KEY_PREFERENCE_HOTEL_CHECKOUT, payData.getCheckOut());
						editor.putString(KEY_PREFERENCE_USER_IDX, payData.getCustomer().getUserIdx());
						editor.commit();
					}
				}
				
				AlertDialog.Builder alert = new AlertDialog.Builder(
						BookingActivity.this);
				alert.setPositiveButton("확인",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss(); // 닫기
								setResult(RESULT_OK);
								finish();
							}
						});
				alert.setMessage("결제가 정상적으로 이루어졌습니다");
				alert.show();

				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
				dialog("모든 객실이 판매되었습니다.\n다음에 이용해주세요.");
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
				dialog("먼저 온 손님이 예약 중입니다.\n잠시 후 다시 시도해주세요.");
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
				dialog("네트워크 오류가 발생했습니다.\n네트워크 연결을 확인해주세요.");
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
				VolleyHttpClient.createCookie();		// 쿠키를 다시 생성 시도
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
			case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
				dialog("알 수 없는 오류가 발생했습니다.\n문의해주시기 바랍니다.");
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_CANCEL:
				dialog("결제가 취소되었습니다.");
				break;
			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN) {
			if (resultCode == RESULT_OK)
				moveToPayStep();	
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group.getId() == rgPaymentMethod.getId()) {

			if (checkedId == rbPaymentAccount.getId()) {
				btnPay.setText("전화로 문의하기");
				tvPaymentInformation
						.setText("계좌정보: 206037-04-005094 | 국민은행 | (주)데일리");

			} else if (checkedId == rbPaymentCard.getId()) {
				btnPay.setText("결제하기");
				tvPaymentInformation.setText("당일 예약 특성 상 취소 및 환불이 불가합니다.");

			}

		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == swCredit.getId()) {
			if (!isChecked) { // 사용안함으로 변경
				tvOriginalPrice.setEnabled(false);
				tvCredit.setEnabled(false);
				tvOriginalPriceValue.setEnabled(false);
				tvCreditValue.setEnabled(false);

			} else { // 사용함으로 변경
				tvOriginalPrice.setEnabled(true);
				tvCredit.setEnabled(true);
				tvOriginalPriceValue.setEnabled(true);
				tvCreditValue.setEnabled(true);

			}

			mPay.setSaleCredit(isChecked);
			updatePayPrice(isChecked);
		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				JSONObject obj = response;
				
				Customer buyer = new Customer();
				buyer.setEmail(obj.getString("email"));
				buyer.setName(obj.getString("name"));
				buyer.setPhone(obj.getString("phone"));
				buyer.setAccessToken(obj.getString("accessToken"));
				buyer.setUserIdx(obj.getString("idx"));
				
				mPay.setCustomer(buyer);
				buyer = mPay.getCustomer();

				if (!isEmptyTextField(new String[] {
						buyer.getEmail(),
						buyer.getPhone(),
						buyer.getName() })) {
					llReserverInfoLabel.setVisibility(View.VISIBLE);
					llReserverInfoEditable.setVisibility(View.GONE);
					etReserverName.setVisibility(View.GONE);
					etReserverNumber.setVisibility(View.GONE);
					etReserverEmail.setVisibility(View.GONE);

					tvReserverName.setText(buyer.getName());
					tvReserverNumber.setText(buyer.getPhone());
					tvReserverEmail.setText(buyer.getEmail());

				} else {
					llReserverInfoEditable.setVisibility(View.VISIBLE);
					llReserverInfoLabel.setVisibility(View.GONE);

					if (!isEmptyTextField(buyer.getName()))
						etReserverName.setText(buyer.getName());

					if (!isEmptyTextField(buyer.getPhone()))
						etReserverNumber.setText(buyer.getPhone());
					else {
						TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext()
								.getSystemService(Context.TELEPHONY_SERVICE);

						etReserverNumber.setText(telephonyManager
								.getLine1Number());
					}

					if (!isEmptyTextField(buyer.getEmail()))
						etReserverEmail.setText(buyer.getEmail());

				}

				// 체크인 정보 요청
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER)
								.append(URL_WEBAPI_RESERVE_CHECKIN)
								.append(mPay.getHotelDetail().getSaleIdx())
								.toString(), null, this, this));

			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_RESERVE_CHECKIN)) {
			try {
				JSONObject obj = response;
				String checkin = obj.getString("checkin");
				String checkout = obj.getString("checkout");
				mPay.setCheckOut(checkout);

				String in[] = checkin.split("-");
				tvCheckIn.setText("20" + in[0] + "년 " + in[1] + "월 " + in[2]
						+ "일 " + in[3] + "시");
				String out[] = checkout.split("-");
				tvCheckOut.setText("20" + out[0] + "년 " + out[1] + "월 "
						+ out[2] + "일 " + out[3] + "시");

				unLockUI();
			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_USER_LOGIN)) { // INVALID_SESSION 오류의
															// 경우 재로그인 후 다시시도한다
			try {
				if (response.getBoolean("login")) {
					unLockUI();
					VolleyHttpClient.createCookie();
					moveToPayStep();

				} else {
					// 실패 시 재시도
					moveToLoginProcess();
					
				}
			} catch (JSONException e) {
				onError(e);
			}
		}
	}

	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_RESERVE_SAVED_MONEY)) {
			try {
				String bonus = response.trim().replaceAll(",", "");
				mPay.setCredit(new Credit(null, bonus, null));

				DecimalFormat comma = new DecimalFormat("###,##0");
				String str = comma.format(Integer.parseInt(mPay.getCredit()
						.getBonus()));
				tvCreditValue.setText(new StringBuilder(str).append("원"));

				swCredit.toggle();
				// 적립금이 없다면 한 번 더 누름 이벤트를 불러 switch를 끈다
				if (Integer.parseInt(mPay.getCredit().getBonus()) == 0) {
					swCredit.toggle();
				}

				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_INFO).toString(), null, this,
						this));

			} catch (Exception e) {
				onError(e);
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();

		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());
	}
}
