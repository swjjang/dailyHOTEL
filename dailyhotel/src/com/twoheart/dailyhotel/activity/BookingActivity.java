package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.GaManager;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.Switch;

/**
 * 
 * @author jangjunho
 *
 */
@SuppressLint({ "NewApi", "ResourceAsColor" })
public class BookingActivity extends BaseActivity implements
DailyHotelStringResponseListener, DailyHotelJsonResponseListener, OnClickListener, OnCheckedChangeListener,
android.widget.CompoundButton.OnCheckedChangeListener {

	private static final String TAG = "HotelPaymentActivity";
	private static final int DIALOG_CONFIRM_PAYMENT_CARD = 0;
	private static final int DIALOG_CONFIRM_PAYMENT_HP = 1;
	private static final int DIALOG_CONFIRM_PAYMENT_ACCOUNT = 2;
	private static final int DIALOG_CONFIRM_PAYMENT_NO_RSERVE = 3;

	private ScrollView svBooking;
	private TextView tvCheckIn, tvCheckOut, tvOriginalPriceValue,
	tvCreditValue, tvOriginalPrice, tvCredit, tvPrice;
	private Button btnPay;
	private Switch swCredit;
	private TextView tvReserverName, tvReserverNumber, tvReserverEmail;
	private LinearLayout llReserverInfoLabel, llReserverInfoEditable;
	private EditText etReserverName, etReserverNumber, etReserverEmail;
	private RadioGroup rgPaymentMethod;
	private RadioButton rbPaymentAccount, rbPaymentCard, rbPaymentHp;

	private Pay mPay;

	private SaleTime saleTime;
	private int mReqCode;
	private int mResCode;
	private Intent mResIntent;
	protected String mAliveCallSource;
	
//	private String locale;

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
		rbPaymentHp = (RadioButton) findViewById(R.id.rb_payment_hp);


		rbPaymentAccount.setOnClickListener(this);
		rbPaymentCard.setOnClickListener(this);
		rbPaymentHp.setOnClickListener(this);

		rgPaymentMethod.setOnCheckedChangeListener(this);
		btnPay.setOnClickListener(this);
		swCredit.setOnCheckedChangeListener(this);

		rbPaymentCard.setChecked(true);

		saleTime = new SaleTime();
//		locale = sharedPreference.getString(KEY_PREFERENCE_LOCALE, null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 적립금 스위치 초기화
//		swCredit.setChecked(false);

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
		
//		if (locale.equals("English"))	tvOriginalPriceValue.setText(getString(R.string.currency)+comma.format(originalPrice));
//		else	tvOriginalPriceValue.setText(comma.format(originalPrice)+getString(R.string.currency));
		tvOriginalPriceValue.setText(comma.format(originalPrice)+getString(R.string.currency));

		if (applyCredit) {
			int payPrice = originalPrice - credit;
			payPrice = payPrice < 0 ? 0: payPrice;
			mPay.setPayPrice(payPrice);
			mPay.setOriginalPrice(originalPrice);
			
			if (credit >= originalPrice) credit = originalPrice;
//			if (locale.equals("English"))	tvCreditValue.setText("-"+getString(R.string.currency)+comma.format(credit));
//			else	tvCreditValue.setText("-"+comma.format(credit)+getString(R.string.currency));
			tvCreditValue.setText("-"+comma.format(credit)+getString(R.string.currency));

		}
		else {
//			if (locale.equals("English"))	tvCreditValue.setText(getString(R.string.currency)+"0");
//			else	tvCreditValue.setText("0"+getString(R.string.currency)); 
			tvCreditValue.setText("0"+getString(R.string.currency));
			mPay.setPayPrice(originalPrice);
//			mPay.setOriginalPrice(originalPrice);
		}

//		if (locale.equals("English"))	tvPrice.setText(getString(R.string.currency)+comma.format(mPay.getPayPrice()));
//		else	tvPrice.setText(comma.format(mPay.getPayPrice())+getString(R.string.currency));
		tvPrice.setText(comma.format(mPay.getPayPrice())+getString(R.string.currency));

	}

	@Override
	public void onClick(final View v) {
		if (v.getId() == btnPay.getId()) {

			if (llReserverInfoEditable.getVisibility() == View.VISIBLE) {
				Customer buyer = new Customer();

				buyer.setEmail(etReserverEmail.getText().toString());
				buyer.setPhone(etReserverNumber.getText().toString());
				buyer.setName(etReserverName.getText().toString());

				if (isEmptyTextField(new String[] {
						buyer.getEmail(),
						buyer.getPhone(),
						buyer.getName() })) {
					
					android.util.Log.e("BUYER",buyer.getEmail()+" / "+buyer.getPhone()+" / "+buyer.getName());
					showToast(getString(R.string.toast_msg_please_input_booking_user_infos), Toast.LENGTH_LONG, false);
				} else { //
					Map<String, String> updateParams =new HashMap<String, String>();
					if (etReserverEmail.isFocusable())
						updateParams.put("user_email", buyer.getEmail());
					if (etReserverName.isFocusable())
						updateParams.put("user_name", buyer.getName());
					if (etReserverNumber.isFocusable())
						updateParams.put("user_phone", buyer.getPhone());

					android.util.Log.e("FACEBOOK UPDATE", updateParams.toString());

					lockUI();
					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_UPDATE_FACEBOOK).toString(),
									updateParams, this, this));
				}

			} else if (mPay.isSaleCredit() && (mPay.getOriginalPrice() < 10000) &&
					Integer.parseInt(mPay.getCredit().getBonus().replaceAll(",", "")) != 0) {
				getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_NO_RSERVE).show();
				
			} else {
				Dialog dialog = null;
				
				if (rgPaymentMethod.getCheckedRadioButtonId() == rbPaymentCard
						.getId()) { // 신용카드를 선택했을 경우

					dialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_CARD);
				} else if (rgPaymentMethod.getCheckedRadioButtonId() == rbPaymentHp
						.getId()) { // 핸드폰을 선택했을 경우

					dialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_HP);
				} else if (rgPaymentMethod.getCheckedRadioButtonId() == rbPaymentAccount
						.getId()) { // 가상계좌 입금을 선택했을 경우

					dialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_ACCOUNT);
				}

				dialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						v.setClickable(true);
						v.setEnabled(true);
					}
				});
				
				dialog.show();
				v.setClickable(false);
				v.setEnabled(false);
			}


		} else if (v.getId() == rbPaymentAccount.getId() | v.getId() == rbPaymentCard.getId()) {
			svBooking.fullScroll(View.FOCUS_DOWN);

		}
	}
	/**
	 * 결제 수단에 알맞은 결제 동의 확인 다이얼로그를 만든다.
	 * @param type CARD, ACCOUNT, HP  세가지 타입 존재.
	 * @return 타입에 맞는 결제 동의 다이얼로그 반환.
	 */

	private Dialog getPaymentConfirmDialog(int type) {
		final Dialog dialog = new Dialog(this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);

		View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);

		TextView tvMsg = (TextView) view.findViewById(R.id.tv_confirm_payment_msg);
		Button btnProceed = (Button) view.findViewById(R.id.btn_confirm_payment_proceed);
		ImageView btnClose = (ImageView) view.findViewById(R.id.btn_confirm_payment_close);

		OnClickListener onClickProceed = null;

		String msg = "";
		if (type == DIALOG_CONFIRM_PAYMENT_HP) msg = getString(R.string.dialog_msg_payment_confirm_hp);
		else if (type == DIALOG_CONFIRM_PAYMENT_NO_RSERVE) {
			msg = getString(R.string.dialog_btn_payment_no_reserve);
			btnProceed.setVisibility(View.GONE);
		}
		else msg = getString(R.string.dialog_msg_payment_confirm);
		
		tvMsg.setText(Html.fromHtml(msg));
		btnProceed.setText(Html.fromHtml(getString(R.string.dialog_btn_payment_confirm)));

		onClickProceed = new OnClickListener() {
			@Override
			public void onClick(View v) {
				lockUI();
				mAliveCallSource = "PAYMENT"; 
				mQueue.add(new DailyHotelStringRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_ALIVE).toString(), null,
								BookingActivity.this, BookingActivity.this));
				dialog.dismiss();
			}
		};

		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		btnProceed.setOnClickListener(onClickProceed);

		dialog.setContentView(view);
		GlobalFont.apply((ViewGroup) view);

		return dialog;
	}

	private boolean isEmptyTextField(String... fieldText) {

		for (int i = 0; i < fieldText.length; i++) {
			if (fieldText[i] == null || fieldText[i].equals("") || fieldText[i].equals("null")) return true;
		}

		return false;
	}

	private void moveToPayStep() {

		android.util.Log.e("Sale credit / Pay Price ",mPay.isSaleCredit()+" / "+mPay.getPayPrice());

		Intent intent = new Intent(this, PaymentActivity.class);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);
		startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

	}

	private void moveToLoginProcess() {
		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false)) {

			String id = sharedPreference.getString(
					KEY_PREFERENCE_USER_ID, null);
			String accessToken = sharedPreference.getString(
					KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			String pw = sharedPreference.getString(
					KEY_PREFERENCE_USER_PWD, null);

			Map<String, String> loginParams = new HashMap<String, String>();

			if (accessToken != null) loginParams.put("accessToken", accessToken);
			else loginParams.put("email", id);

			loginParams.put("pw", pw);

			mQueue.add(new DailyHotelJsonRequest(Method.POST,
					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
							URL_WEBAPI_USER_LOGIN).toString(),
							loginParams, this, this));
		} else {
			unLockUI();
			showToast(getString(R.string.toast_msg_retry_login), Toast.LENGTH_LONG, false);

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
		unLockUI();

		mReqCode = requestCode;
		mResCode = resultCode;
		mResIntent = intent;

		mAliveCallSource = "ACTIVITY_RESULT";
		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET,
				new StringBuilder(URL_DAILYHOTEL_SERVER).append(
						URL_WEBAPI_USER_ALIVE).toString(), null,
						BookingActivity.this, BookingActivity.this));

	}

	private void activityResulted(int requestCode, int resultCode, Intent intent) {
		if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT) {
			Log.d(TAG, Integer.toString(resultCode));

			String title = getString(R.string.dialog_title_payment);
			String msg = "";
			String posTitle = getString(R.string.dialog_btn_text_confirm);
			android.content.DialogInterface.OnClickListener posListener = null;

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

				GaManager.getInstance(getApplicationContext()).
				purchaseComplete(
						Integer.toString(mPay.getHotelDetail().getSaleIdx()), 
						mPay.getHotelDetail().getHotel().getName(), 
						mPay.getHotelDetail().getHotel().getCategory(), 
						(double) mPay.getPayPrice()
						);

				posListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						dialog.dismiss(); // 닫기
						setResult(RESULT_OK);
						BookingActivity.this.finish();
					}
				};

				msg = getString(R.string.act_toast_payment_success);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
				msg = getString(R.string.act_toast_payment_soldout);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
				msg = getString(R.string.act_toast_payment_not_available);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
				msg = getString(R.string.act_toast_payment_network_error);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
				VolleyHttpClient.createCookie();	// 쿠키를 다시 생성 시도
				return;
			case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE:
				msg = getString(R.string.act_toast_payment_invalid_date);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
				msg = getString(R.string.act_toast_payment_fail);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_CANCELED:
				msg = getString(R.string.act_toast_payment_canceled);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY:
				/**
				 * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함.
				 * 이를 위한 정보를 셋팅. 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함.
				 * 들어간 후에는 다시 프리퍼런스를 초기화해줌.
				 * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약 리스트 갱신 후 최상단 아이템 인텐트
				 */
				Editor editor = sharedPreference.edit();
				editor.putInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
				editor.apply();

				setResult(CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY);
				finish();
				return;
			case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR:
				msg = getString(R.string.act_toast_payment_account_time_error);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE:
				msg = getString(R.string.act_toast_payment_account_duplicate);
				break;
			case CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER:
				msg = getString(R.string.act_toast_payment_account_timeover);
				break;
			default:
				return;
			}

			SimpleAlertDialog.build(this, title, msg, posTitle, posListener).show();

		} else if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN) {
			if (resultCode == RESULT_OK) moveToPayStep();	
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group.getId() == rgPaymentMethod.getId()) {
			if (checkedId == rbPaymentCard.getId()) mPay.setPayType("CARD");
			else if (checkedId == rbPaymentHp.getId()) mPay.setPayType("PHONE_PAY");
			else if (checkedId == rbPaymentAccount.getId()) mPay.setPayType("VBANK");
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
				android.util.Log.e("responSE!", response.toString());
				int sdk = android.os.Build.VERSION.SDK_INT;

				Customer buyer = new Customer();
				buyer.setEmail(obj.getString("email"));
				buyer.setName(obj.getString("name"));
				buyer.setPhone(obj.getString("phone"));
				buyer.setAccessToken(obj.getString("accessToken"));
				buyer.setUserIdx(obj.getString("idx"));

				mPay.setCustomer(buyer);
				buyer = mPay.getCustomer();

				/**
				 * 텍스트 필드가 하나라도 비어있으면 해당 정보를 입력 받도록 함.
				 */
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

					android.util.Log.e("buyer",buyer.getName()+" / " +buyer.getPhone()+" / "+buyer.getEmail() );

					if (!isEmptyTextField(buyer.getName())) {
						etReserverName.setText(buyer.getName());
						etReserverName.setKeyListener(null);
						etReserverName.setFocusable(false);
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							etReserverName.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						} else {
							etReserverName.setBackground(new ColorDrawable(Color.TRANSPARENT));
						}
					} 

					if (!isEmptyTextField(buyer.getPhone())) {
						etReserverNumber.setText(buyer.getPhone());
						etReserverNumber.setKeyListener(null);
						etReserverNumber.setFocusable(false);
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							etReserverNumber.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						} else {
							etReserverNumber.setBackground(new ColorDrawable(Color.TRANSPARENT));
						}
					} else {
						TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext()
								.getSystemService(Context.TELEPHONY_SERVICE);

						etReserverNumber.setText(telephonyManager
								.getLine1Number());
					}

					if (!isEmptyTextField(buyer.getEmail())) {
						etReserverEmail.setText(buyer.getEmail());
						etReserverEmail.setKeyListener(null);
						etReserverEmail.setFocusable(false);
						etReserverEmail.setBackgroundColor(Color.TRANSPARENT);
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							etReserverEmail.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
						} else {
							etReserverEmail.setBackground(new ColorDrawable(Color.TRANSPARENT));
						}
					} 

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
				tvCheckIn.setText("20" + in[0] + getString(R.string.frag_booking_tab_year) + in[1] + getString(R.string.frag_booking_tab_month)
						+ in[2] + getString(R.string.frag_booking_tab_day) + " " + in[3] + getString(R.string.frag_booking_tab_hour));

				String out[] = checkout.split("-");
				tvCheckOut.setText("20" + out[0] + getString(R.string.frag_booking_tab_year) + out[1] + getString(R.string.frag_booking_tab_month) 
						+ out[2] + getString(R.string.frag_booking_tab_day) + " "+ out[3] + getString(R.string.frag_booking_tab_hour));

				unLockUI();
			} catch (Exception e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_USER_LOGIN)) {
			try {
				if (response.getBoolean("login")) {
					unLockUI();
					VolleyHttpClient.createCookie();
					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
							URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME)
							.toString(), null, BookingActivity.this,
							BookingActivity.this));
				}
			} catch (JSONException e) {
				onError(e);
			}
		} else if (url.contains(URL_WEBAPI_APP_SALE_TIME)) {
			// 앱 시간이 오픈 시간보다 크고 클로즈 시간보다 작은경우 다음 스텝으로 이동
			try {
				String open = response.getString("open");
				String close = response.getString("close");

				saleTime.setOpenTime(open);
				saleTime.setCloseTime(close);

				unLockUI();

				if( saleTime.isSaleTime() ) {

					Customer buyer = mPay.getCustomer();
					if (llReserverInfoLabel.getVisibility() == View.VISIBLE) {

						buyer.setEmail(tvReserverEmail.getText().toString());
						buyer.setPhone(tvReserverNumber.getText().toString());
						buyer.setName(tvReserverName.getText().toString());

					}

					mPay.setCustomer(buyer);
					moveToPayStep();

				} else {
					android.content.DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							setResult(CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED);
							finish();
						}
					};

					SimpleAlertDialog.build(this, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_sales_closed), getString(R.string.dialog_btn_text_confirm), posListener).show();

				}

			} catch (JSONException e) {
				onError(e);
			}

		} else if (url.contains(URL_WEBAPI_USER_UPDATE_FACEBOOK)) {
			android.util.Log.e("UPDATE_FACEBOOK_RESULT",response.toString());
			unLockUI();
			try {
				if(!response.getBoolean("result")) {
					showToast(response.getString("message"), Toast.LENGTH_LONG, false);
				} else {
					llReserverInfoLabel.setVisibility(View.VISIBLE);
					llReserverInfoEditable.setVisibility(View.GONE);
					etReserverName.setVisibility(View.GONE);
					etReserverNumber.setVisibility(View.GONE);
					etReserverEmail.setVisibility(View.GONE);

					tvReserverName.setText(etReserverName.getText().toString());
					tvReserverNumber.setText(etReserverNumber.getText().toString());
					tvReserverEmail.setText(etReserverEmail.getText().toString());

					btnPay.performClick();
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
				
				int originalPrice = Integer.parseInt(mPay.getHotelDetail().getHotel()
						.getDiscount().replaceAll(",", ""));
				DecimalFormat comma = new DecimalFormat("###,##0");
				
//				if (locale.equals("English"))	{
//					tvOriginalPriceValue.setText(getString(R.string.currency)+comma.format(originalPrice));
//					tvPrice.setText(getString(R.string.currency)+comma.format(originalPrice));
//				}
//				else	{
//					tvOriginalPriceValue.setText(comma.format(originalPrice)+getString(R.string.currency));
//					tvPrice.setText(comma.format(originalPrice)+getString(R.string.currency));
//				}
				tvOriginalPriceValue.setText(comma.format(originalPrice)+getString(R.string.currency));
				tvPrice.setText(comma.format(originalPrice)+getString(R.string.currency));
					
				mPay.setPayPrice(originalPrice);
				
				swCredit.setChecked(false);
				
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_INFO).toString(), null, this,
								this));

			} catch (Exception e) {
				onError(e);
			}
		} else if(url.contains(URL_WEBAPI_APP_TIME)) {
			saleTime.setCurrentTime(response);

			mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
					URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME)
					.toString(), null, BookingActivity.this,
					this));
		} else if(url.contains(URL_WEBAPI_USER_ALIVE)) {
			android.util.Log.e("USER_ALIVE / CALL_RESOURCE",response.toString()+" / "+mAliveCallSource);
			unLockUI();
			/**
			 * ALIVE CALL은 소스가 두종류,
			 * 1. BookingActivity => PaymentActivity로 넘어갈때
			 * 2. PaymentActivity => BookingActivity로 넘어왔을때
			 */
			if (response.equals("alive")) {
				if (mAliveCallSource.equals("PAYMENT")) {//1번 
					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(
							URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_TIME)
							.toString(), null, BookingActivity.this,
							BookingActivity.this));
				} else if(mAliveCallSource.equals("ACTIVITY_RESULT")) {//2번 
					activityResulted(mReqCode, mResCode, mResIntent);	
				}

			} else {
				if (sharedPreference.getBoolean(
						KEY_PREFERENCE_AUTO_LOGIN, false)) {
					String id = sharedPreference.getString(
							KEY_PREFERENCE_USER_ID, null);
					String accessToken = sharedPreference
							.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					String pw = sharedPreference.getString(
							KEY_PREFERENCE_USER_PWD, null);

					Map<String, String> loginParams = new HashMap<String, String>();

					if (accessToken != null) loginParams.put("accessToken",accessToken);
					else loginParams.put("email", id);
					loginParams.put("pw", pw);
					android.util.Log.e("LOGIN PARAMS",loginParams.toString());

					mQueue.add(new DailyHotelJsonRequest(Method.POST,
							new StringBuilder(URL_DAILYHOTEL_SERVER).append(
									URL_WEBAPI_USER_LOGIN).toString(),
									loginParams, BookingActivity.this,
									BookingActivity.this));
				}
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.payment_wait_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_call:
			Intent i = new Intent(
					Intent.ACTION_DIAL,
					Uri.parse(new StringBuilder("tel:")
					.append(PHONE_NUMBER_DAILYHOTEL)
					.toString()));
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
