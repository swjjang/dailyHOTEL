/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingActivity (예약 화면)
 * 
 * 결제 화면으로 넘어가기 전 예약 정보를 보여주고 결제방식을 선택할 수 있는 화면 
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.ui.FinalCheckLayout;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailySignatureView;
import com.twoheart.dailyhotel.widget.DailyToast;

/**
 * 
 * @author jangjunho
 *
 */
@SuppressLint({ "NewApi", "ResourceAsColor" })
public class BookingActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener
{
	private static final int DEFAULT_AVAILABLE_RESERVES = 20000;

	private static final int DIALOG_CONFIRM_PAYMENT_CARD = 0;
	private static final int DIALOG_CONFIRM_PAYMENT_HP = 1;
	private static final int DIALOG_CONFIRM_PAYMENT_ACCOUNT = 2;
	private static final int DIALOG_CONFIRM_PAYMENT_NO_RSERVE = 3;
	private static final int DIALOG_CONFIRM_CALL = 4;
	private static final int DIALOG_CONFIRM_PAYMENT_REGCARD = 5;
	private static final int DIALOG_CONFIRM_STOP_ONSALE = 6;

	private TextView mCheckinDayTextView, mCheckinTimeTextView,
			mCheckoutDayTextView, mCheckoutTimeTextView;

	private TextView tvOriginalPriceValue, tvCreditValue, tvOriginalPrice,
			tvCredit, tvPrice;
	private TextView btnPay;
	private SwitchCompat swCredit;
	private EditText etReserverName, etReserverNumber, etReserverEmail;
	private Drawable[] mEditTextBackground;
	private RadioGroup rgPaymentMethod;
	private RadioButton rbPaymentAccount, rbPaymentCard, rbPaymentHp,
			mSimplePaymentRadioButton;
	private View mCardManagerButton;

	private Pay mPay;
	private CreditCard mSelectedCreditCard;
	private SaleTime mSaleTime;
	private boolean mIsChangedPay; // 가격이 변경된 경우.

	private SaleTime saleTime;
	private int mReqCode;
	private int mResCode;
	private Intent mResIntent;
	protected String mAliveCallSource;
	private Dialog mFinalCheckDialog;
	private ProgressDialog mProgressDialog;

	//	private String locale;
	private int mHotelIdx;
	private boolean mIsEditMode;

	private MixpanelAPI mMixpanel;

	private View mClickView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_booking);

		mMixpanel = MixpanelAPI.getInstance(this, "791b366dadafcd37803f6cd7d8358373"); // 상수 등록 요망

		mPay = new Pay();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			mPay.setHotelDetail((HotelDetail) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_HOTELDETAIL));
			mHotelIdx = bundle.getInt(NAME_INTENT_EXTRA_DATA_HOTELIDX);
			mSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
		}

		mIsChangedPay = false;

		setActionBar(mPay.getHotelDetail().getHotel().getName());

		mCheckinDayTextView = (TextView) findViewById(R.id.checkinDayTextView);
		mCheckinTimeTextView = (TextView) findViewById(R.id.checkinTimeTextView);
		mCheckoutDayTextView = (TextView) findViewById(R.id.checkoutDayTextView);
		mCheckoutTimeTextView = (TextView) findViewById(R.id.checkoutTimeTextView);

		tvOriginalPrice = (TextView) findViewById(R.id.tv_hotel_payment_original_price);
		tvCredit = (TextView) findViewById(R.id.tv_hotel_payment_credit);
		tvOriginalPriceValue = (TextView) findViewById(R.id.tv_hotel_payment_original_price_value);
		tvCreditValue = (TextView) findViewById(R.id.tv_hotel_payment_credit_value);
		tvPrice = (TextView) findViewById(R.id.tv_hotel_payment_price);
		btnPay = (TextView) findViewById(R.id.btn_hotel_payment);

		swCredit = (SwitchCompat) findViewById(R.id.btn_on_off);

		//		swCredit.setSwitchMinWidth(Util.dpToPx(BookingActivity.this, 60));

		etReserverName = (EditText) findViewById(R.id.et_hotel_payment_reserver_name);
		etReserverNumber = (EditText) findViewById(R.id.et_hotel_payment_reserver_number);
		etReserverEmail = (EditText) findViewById(R.id.et_hotel_payment_reserver_email);

		mEditTextBackground = new Drawable[3];
		mEditTextBackground[0] = etReserverName.getBackground();
		mEditTextBackground[1] = etReserverNumber.getBackground();
		mEditTextBackground[2] = etReserverEmail.getBackground();

		etReserverName.setBackgroundResource(0);
		etReserverNumber.setBackgroundResource(0);
		etReserverEmail.setBackgroundResource(0);

		etReserverName.setEnabled(false);
		etReserverNumber.setEnabled(false);
		etReserverEmail.setEnabled(false);

		rgPaymentMethod = (RadioGroup) findViewById(R.id.rg_payment_method);

		mSimplePaymentRadioButton = (RadioButton) findViewById(R.id.easyPaymentRadioButton);
		rbPaymentAccount = (RadioButton) findViewById(R.id.rb_payment_account);
		rbPaymentCard = (RadioButton) findViewById(R.id.rb_payment_card);
		rbPaymentHp = (RadioButton) findViewById(R.id.rb_payment_hp);

		mCardManagerButton = findViewById(R.id.cardManagerButton);
		mCardManagerButton.setOnClickListener(this);

		rbPaymentAccount.setOnClickListener(this);
		rbPaymentCard.setOnClickListener(this);
		rbPaymentHp.setOnClickListener(this);
		mSimplePaymentRadioButton.setOnClickListener(this);

		rgPaymentMethod.setOnCheckedChangeListener(this);

		btnPay.setOnClickListener(this);
		swCredit.setOnCheckedChangeListener(this);

		rbPaymentCard.setChecked(true);

		saleTime = new SaleTime();
		//		locale = sharedPreference.getString(KEY_PREFERENCE_LOCALE, null);

		// 한글, 영문 결제 수단 지정.
		//		if (locale.equals("한국어"))
		//		{
		// 적립금 부분 기본 통화 표기.
		tvCreditValue.setText("0" + Html.fromHtml(getString(R.string.currency)));

		rgPaymentMethod.setVisibility(View.VISIBLE);
		//		} else
		//		{
		// 적립금 부분 기본 통화 표기.
		//			tvCreditValue.setText(Html.fromHtml(getString(R.string.currency)) + "0");
		//
		//			rgPaymentMethod.setVisibility(View.GONE);
		//			mPay.setType(Pay.Type.PAYPAL);
		//		}

		TextView linkTextView = (TextView) findViewById(R.id.tv_card_notice);
		SpannableStringBuilder stringBuilder = new SpannableStringBuilder();

		String info01 = getString(R.string.act_booking_payment_info01);
		String info02 = getString(R.string.act_booking_payment_info02);

		stringBuilder.append(info01);
		stringBuilder.append(info02);
		stringBuilder.append(getString(R.string.act_booking_payment_info03));

		stringBuilder.setSpan(new TelophoneClickSpannable(), info01.length(), info01.length() + info02.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		linkTextView.setText(stringBuilder);
		linkTextView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		lockUI();

		// 호텔 디테일 정보 재 요청
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append('/').append(mPay.getHotelDetail().getHotel().getIdx()).append("/").append(mSaleTime.getRequestHotelDateFormat("yy/MM/dd")).toString(), null, mHotelDetailJsonResponseListener, this));

		String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
		String hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);

		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("bookingDetail", "/todays-hotels/" + region + "/" + hotelName + "/booking-detail");
	}

	private void updatePayPrice(boolean applyCredit)
	{
		int originalPrice = mPay.getHotelDetail().getHotel().getDiscount();
		int credit = Integer.parseInt(mPay.getCredit().getBonus().replaceAll(",", ""));

		DecimalFormat comma = new DecimalFormat("###,##0");

		//		if (locale.equals("한국어"))
		tvOriginalPriceValue.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));
		//		else
		//			tvOriginalPriceValue.setText(Html.fromHtml(getString(R.string.currency)) + comma.format(originalPrice));

		if (applyCredit)
		{
			int payPrice = originalPrice - credit;
			payPrice = payPrice < 0 ? 0 : payPrice;
			mPay.setPayPrice(payPrice);
			mPay.setOriginalPrice(originalPrice);

			if (credit >= originalPrice)
			{
				credit = originalPrice;
			}

			//			if (locale.equals("한국어"))
			tvCreditValue.setText("-" + comma.format(credit) + Html.fromHtml(getString(R.string.currency)));
			//			else
			//				tvCreditValue.setText(Html.fromHtml(getString(R.string.currency)) + "-" + comma.format(credit));

		} else
		{
			//			if (locale.equals("한국어"))
			tvCreditValue.setText("0" + Html.fromHtml(getString(R.string.currency)));
			//			else
			//				tvCreditValue.setText(Html.fromHtml(getString(R.string.currency)) + "0");

			mPay.setPayPrice(originalPrice);
			//			mPay.setOriginalPrice(originalPrice);
		}

		//		if (locale.equals("한국어"))
		tvPrice.setText(comma.format(mPay.getPayPrice()) + Html.fromHtml(getString(R.string.currency)));
		//		else
		//			tvPrice.setText(Html.fromHtml(getString(R.string.currency)) + comma.format(mPay.getPayPrice()));

	}

	@Override
	public void onError()
	{
		super.onError();

		finish();
	}

	@Override
	public void onClick(final View v)
	{
		if (v.getId() == btnPay.getId())
		{
			if (mIsEditMode == true)
			{
				Customer buyer = new Customer();

				buyer.setEmail(etReserverEmail.getText().toString());
				buyer.setPhone(etReserverNumber.getText().toString());
				buyer.setName(etReserverName.getText().toString());

				if (isEmptyTextField(buyer.getName()) == true)
				{
					etReserverName.requestFocus();

					DailyToast.showToast(this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
				} else if (isEmptyTextField(buyer.getPhone()) == true)
				{
					etReserverNumber.requestFocus();

					DailyToast.showToast(this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
				} else if (isEmptyTextField(buyer.getEmail()) == true)
				{
					etReserverEmail.requestFocus();

					DailyToast.showToast(this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
				} else if (isValidEmail(buyer.getEmail()) == false)
				{
					etReserverEmail.requestFocus();

					DailyToast.showToast(BookingActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
				} else
				{ //
					Map<String, String> updateParams = new HashMap<String, String>();
					if (etReserverEmail.isFocusable())
						updateParams.put("user_email", buyer.getEmail());
					if (etReserverName.isFocusable())
						updateParams.put("user_name", buyer.getName());
					if (etReserverNumber.isFocusable())
						updateParams.put("user_phone", buyer.getPhone());

					ExLog.e("FACEBOOK UPDATE : " + updateParams.toString());

					lockUI();

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_UPDATE_FACEBOOK).toString(), updateParams, mUserUpdateFacebookJsonResponseListener, this));
				}

			} //호텔 가격이 xx 이하인 이벤트 호텔에서는 적립금 사용을 못하게 막음. 
			else if (mPay.isSaleCredit() && (mPay.getOriginalPrice() <= DEFAULT_AVAILABLE_RESERVES) && Integer.parseInt(mPay.getCredit().getBonus().replaceAll(",", "")) != 0)
			{
				v.setClickable(false);
				v.setEnabled(false);

				final Dialog dialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_NO_RSERVE, null);

				dialog.setOnDismissListener(new OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog)
					{
						v.setClickable(true);
						v.setEnabled(true);
					}
				});

				dialog.show();

			} else
			{
				// 해당 호텔이 결제하기를 못하는 경우를 처리한다.
				Map<String, String> updateParams = new HashMap<String, String>();
				updateParams.put("saleIdx", String.valueOf(mPay.getHotelDetail().getSaleIdx()));

				mClickView = v;

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_VALIDATE).toString(), updateParams, mReservValidateJsonResponseListener, BookingActivity.this));

				v.setClickable(false);
				v.setEnabled(false);

				String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
				String hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);

				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("paymentAgreement", "/todays-hotels/" + region + "/" + hotelName + "/booking-detail/payment-agreement");
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestPayment", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
			}
		} else if (v.getId() == mCardManagerButton.getId())
		{
			Intent intent = new Intent(this, CreditCardListActivity.class);
			intent.setAction(Intent.ACTION_PICK);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	}

	/**
	 * 결제 수단에 알맞은 결제 동의 확인 다이얼로그를 만든다.
	 * 
	 * @param type
	 *            CARD, ACCOUNT, HP 세가지 타입 존재.
	 * @return 타입에 맞는 결제 동의 다이얼로그 반환.
	 */

	private Dialog getPaymentConfirmDialog(int type, final View.OnClickListener onClickListener)
	{
		final Dialog dialog = new Dialog(this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);

		View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);

		TextView titleTextView = (TextView) view.findViewById(R.id.titleTextView);
		TextView tvMsg = (TextView) view.findViewById(R.id.tv_confirm_payment_msg);
		TextView btnProceed = (TextView) view.findViewById(R.id.btn_confirm_payment_proceed);
		ImageView btnClose = (ImageView) view.findViewById(R.id.btn_confirm_payment_close);

		String msg;
		String buttonText;

		OnClickListener buttonOnClickListener = null;

		switch (type)
		{
			case DIALOG_CONFIRM_PAYMENT_HP:
				msg = getString(R.string.dialog_msg_payment_confirm_hp);
				buttonText = getString(R.string.dialog_btn_payment_confirm);
				break;

			case DIALOG_CONFIRM_PAYMENT_NO_RSERVE:
				msg = getString(R.string.dialog_btn_payment_no_reserve);

				btnProceed.setVisibility(View.GONE);
				buttonText = getString(R.string.dialog_btn_payment_confirm);
				break;

			case DIALOG_CONFIRM_PAYMENT_ACCOUNT:
				msg = getString(R.string.dialog_msg_payment_confirm_account);
				buttonText = getString(R.string.dialog_btn_payment_confirm);
				break;

			case DIALOG_CONFIRM_CALL:
				titleTextView.setText(R.string.dialog_notice2);
				msg = getString(R.string.dialog_msg_call);
				buttonText = getString(R.string.dialog_btn_call);
				break;

			case DIALOG_CONFIRM_STOP_ONSALE:
				dialog.setCancelable(false);

				btnClose.setVisibility(View.INVISIBLE);
				titleTextView.setText(R.string.dialog_notice2);

				msg = getString(R.string.dialog_msg_stop_onsale);
				buttonText = getString(R.string.dialog_btn_text_confirm);
				break;

			//			case DIALOG_CONFIRM_PAYMENT_REGCARD:
			//			case DIALOG_CONFIRM_PAYMENT_CARD:
			//			case DIALOG_CONFIRM_PAYMENT_ACCOUNT:
			default:
				msg = getString(R.string.dialog_msg_payment_confirm);
				buttonText = getString(R.string.dialog_btn_payment_confirm);
				break;
		}

		tvMsg.setText(Html.fromHtml(msg));
		btnProceed.setText(Html.fromHtml(buttonText));

		if (onClickListener == null)
		{
			buttonOnClickListener = new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					synchronized (BookingActivity.this)
					{
						if (isLockUiComponent() == true)
						{
							return;
						}

						lockUI();

						mAliveCallSource = "PAYMENT";
						mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, BookingActivity.this));
						dialog.dismiss();
						RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "agreePayment", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
					}
				}
			};
		} else
		{
			buttonOnClickListener = new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
					onClickListener.onClick(v);
				}
			};
		}

		btnClose.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.cancel();
			}
		});

		btnProceed.setOnClickListener(buttonOnClickListener);

		dialog.setContentView(view);

		// pinkred_font
		//		GlobalFont.apply((ViewGroup) view);

		return dialog;
	}

	private boolean isEmptyTextField(String... fieldText)
	{

		for (int i = 0; i < fieldText.length; i++)
		{
			if (isEmptyTextField(fieldText[i]) == true)
				return true;
		}

		return false;
	}

	private boolean isEmptyTextField(String fieldText)
	{
		return (TextUtils.isEmpty(fieldText) == true || fieldText.equals("null") == true || fieldText.trim().length() == 0);
	}

	// 결제 화면으로 이동 
	private void moveToPayStep()
	{
		unLockUI();

		if (mPay.getType() == Pay.Type.EASY_CARD)
		{
			if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
			{
				mFinalCheckDialog.dismiss();
			}

			if (mProgressDialog != null)
			{
				if (mProgressDialog.isShowing() == true)
				{
					mProgressDialog.dismiss();
				}

				mProgressDialog = null;
			}

			mProgressDialog = new ProgressDialog(BookingActivity.this);
			mProgressDialog.setMessage(getString(R.string.dialog_msg_processing_payment));
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();

			String mileage = "0"; // 적립금

			if (mPay.isSaleCredit() == true)
			{
				// 적립금을 절대값으로 보냄..
				try
				{
					mileage = String.valueOf(Math.abs(Integer.parseInt(mPay.getCredit().getBonus())));
				} catch (Exception e)
				{
					ExLog.e(e.toString());
				}
			}

			Map<String, String> params = new HashMap<String, String>();

			params.put("saleIdx", String.valueOf(mPay.getHotelDetail().getSaleIdx()));
			params.put("billkey", mPay.getCustomer().mBillingKey);
			params.put("mileage", mileage);

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_PAYMENT).toString(), params, mUserSessionBillingPayment, BookingActivity.this));
		} else
		{
			Intent intent = new Intent(this, com.twoheart.dailyhotel.activity.PaymentActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);

			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	}

	//	private void moveToLoginProcess()
	//	{
	//		if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
	//		{
	//
	//			String id = sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
	//			String accessToken = sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
	//			String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);
	//
	//			Map<String, String> loginParams = new HashMap<String, String>();
	//
	//			if (accessToken != null)
	//				loginParams.put("accessToken", accessToken);
	//			else
	//				loginParams.put("email", id);
	//
	//			loginParams.put("pw", pw);
	//
	//			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, this));
	//		} else
	//		{
	//			unLockUI();
	//			DailyToast.showToast(this, R.string.toast_msg_retry_login, Toast.LENGTH_LONG);
	//
	//			startActivityForResult(new Intent(this, LoginActivity.class), CODE_REQUEST_ACTIVITY_LOGIN);
	//			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
	//		}
	//
	//	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);
		unLockUI();

		mReqCode = requestCode;
		mResCode = resultCode;
		mResIntent = intent;

		mAliveCallSource = "ACTIVITY_RESULT";

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, BookingActivity.this));
	}

	private void activityResulted(int requestCode, int resultCode, Intent intent)
	{
		//결제가 끝난 뒤 호출됨. 
		if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT)
		{
			String title = getString(R.string.dialog_title_payment);
			String msg = "";
			String posTitle = getString(R.string.dialog_btn_text_confirm);
			android.content.DialogInterface.OnClickListener posListener = null;

			switch (resultCode)
			{
			// 결제가 성공한 경우 GA와 믹스패널에 등록 
				case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
				case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
					if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_PAY) == true)
					{
						Pay payData = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY);

						Editor editor = sharedPreference.edit();
						editor.putString(KEY_PREFERENCE_HOTEL_NAME, payData.getHotelDetail().getHotel().getName());
						editor.putInt(KEY_PREFERENCE_HOTEL_SALE_IDX, payData.getHotelDetail().getSaleIdx());
						editor.putString(KEY_PREFERENCE_HOTEL_CHECKOUT, payData.getCheckOut());
						editor.putString(KEY_PREFERENCE_USER_IDX, payData.getCustomer().getUserIdx());
						editor.commit();
					}

					writeLogPaid(mPay);

					posListener = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss(); // 닫기

							try
							{
								RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "confirmPayment", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
							} catch (Exception e)
							{
								ExLog.e(e.toString());
							}

							setResult(RESULT_OK);
							finish();
						}
					};

					msg = getString(R.string.act_toast_payment_success);
					break;
				case CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT:
					msg = getString(R.string.act_toast_payment_soldout);
					break;
				case CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE:
					title = getString(R.string.dialog_notice2);
					msg = getString(R.string.act_toast_payment_not_available);
					break;
				case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
					msg = getString(R.string.act_toast_payment_network_error);
					break;
				case CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION:
					VolleyHttpClient.createCookie(); // 쿠키를 다시 생성 시도
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
					 * 가상계좌선택시 해당 가상계좌 정보를 보기위해 화면 스택을 쌓으면서 들어가야함. 이를 위한 정보를 셋팅.
					 * 예약 리스트 프래그먼트에서 찾아 들어가기 위해서 필요함. 들어간 후에는 다시 프리퍼런스를 초기화해줌.
					 * 플로우) 예약 액티비티 => 호텔탭 액티비티 => 메인액티비티 => 예약 리스트 프래그먼트 => 예약
					 * 리스트 갱신 후 최상단 아이템 인텐트
					 */
					if (intent != null)
					{
						if (intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY) != null)
						{
							Pay payData = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAY);

							Editor editor = sharedPreference.edit();
							editor.putString(KEY_PREFERENCE_USER_IDX, payData.getCustomer().getUserIdx());
							ExLog.d("category? " + payData.getHotelDetail().getHotel().getCategory());
							editor.commit();
						}
					}

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

				case CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR:
					if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_MESSAGE) == true)
					{
						msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_MESSAGE);
					} else
					{
						msg = getString(R.string.act_toast_payment_fail);
					}
					break;

				default:
					return;
			}

			SimpleAlertDialog.build(this, title, msg, posTitle, posListener).show();

			//		} else if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN)
			//		{
			//			if (resultCode == RESULT_OK)
			//			{
			//				lockUI();
			//				
			//				// 호텔 디테일 정보 재 요청
			//				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append('/').append(mPay.getHotelDetail().getHotel().getIdx()).append("/").append(mSaleTime.getRequestHotelDateFormat("yy/MM/dd")).toString(), null, mFinalCheckPayJsonResponseListener, this));
			//			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER)
		{
			// 신용카드 간편 결제 선택후
			switch (resultCode)
			{
				case Activity.RESULT_OK:
					if (intent != null)
					{
						CreditCard creditCard = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD);

						if (creditCard != null)
						{
							mSelectedCreditCard = creditCard;

							rgPaymentMethod.check(mSimplePaymentRadioButton.getId());
						}
					}
					break;

			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD)
		{
			// 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
			String msg = null;

			switch (resultCode)
			{
				case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
					lockUI();

					// credit card 요청
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserRegisterBillingCardInfoJsonResponseListener, BookingActivity.this));
					return;

				case CODE_RESULT_PAYMENT_BILLING_DUPLICATE:
					msg = getString(R.string.message_billing_duplicate);
					break;

				case CODE_RESULT_PAYMENT_BILLING_FAIL:
					msg = getString(R.string.message_billing_fail);
					break;

				case CODE_RESULT_ACTIVITY_PAYMENT_FAIL:
					msg = getString(R.string.act_toast_payment_fail);
					break;

				case CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR:
					msg = getString(R.string.act_toast_payment_network_error);
					break;
			}

			if (msg != null)
			{
				String title = getString(R.string.dialog_notice2);
				String positive = getString(R.string.dialog_btn_text_confirm);

				SimpleAlertDialog.build(BookingActivity.this, title, msg, positive, (DialogInterface.OnClickListener) null).show();
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		if (group.getId() == rgPaymentMethod.getId())
		{
			if (checkedId == mSimplePaymentRadioButton.getId())
			{
				mPay.setType(Pay.Type.EASY_CARD);
			} else if (checkedId == rbPaymentCard.getId())
			{
				mPay.setType(Pay.Type.CARD);
			} else if (checkedId == rbPaymentHp.getId())
			{
				mPay.setType(Pay.Type.PHONE_PAY);
			} else if (checkedId == rbPaymentAccount.getId())
			{
				mPay.setType(Pay.Type.VBANK);
			}
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		// 앱 메모리 삭제하고 복귀하는 경우 에러가 생기는 경우가 발생하여
		// 앱을 재부팅하는 코드 추가.
		try
		{
			if (buttonView.getId() == swCredit.getId())
			{
				if (!isChecked)
				{
					// 사용안함으로 변경
					tvOriginalPrice.setEnabled(false);
					tvCredit.setEnabled(false);
					tvOriginalPriceValue.setEnabled(false);
					tvCreditValue.setEnabled(false);
					RenewalGaManager.getInstance(getApplicationContext()).recordEvent("toggle action", "applyCredit", "off", null);

				} else
				{
					// 사용함으로 변경
					tvOriginalPrice.setEnabled(true);
					tvCredit.setEnabled(true);
					tvOriginalPriceValue.setEnabled(true);
					tvCreditValue.setEnabled(true);
					RenewalGaManager.getInstance(getApplicationContext()).recordEvent("toggle action", "applyCredit", "on", null);
				}

				mPay.setSaleCredit(isChecked);
				updatePayPrice(isChecked);
			}
		} catch (Exception e)
		{
			ExLog.d(e.toString());

			restartApp();
		}
	}

	@Override
	protected void onStart()
	{
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.payment_wait_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_call:
			{
				getPaymentConfirmDialog(DIALOG_CONFIRM_CALL, new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "callHotel", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
						Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
						startActivity(i);
					}
				}).show();

				return true;
			}

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy()
	{
		mMixpanel.flush();

		if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
		{
			mFinalCheckDialog.dismiss();
		}

		mFinalCheckDialog = null;

		if (mProgressDialog != null && mProgressDialog.isShowing() == true)
		{
			mProgressDialog.dismiss();
		}

		mProgressDialog = null;

		super.onDestroy();
	}

	private void showAgreeTermDialog(Pay.Type type)
	{
		if (mFinalCheckDialog != null)
		{
			mFinalCheckDialog.cancel();
		}

		mFinalCheckDialog = null;

		switch (type)
		{
			case EASY_CARD:
				// 나머지의 경우에는 등록된 신용 카드인 경우.
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_REGCARD, null);
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("radio", "choosePaymentWay", "등록된신용카드", (long) 4);
				break;

			case CARD:
				// 신용카드를 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_CARD, null);
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("radio", "choosePaymentWay", "신용카드", (long) 1);
				break;

			case PHONE_PAY:
				// 핸드폰을 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_HP, null);
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("radio", "choosePaymentWay", "휴대폰", (long) 2);
				break;

			case VBANK:
				// 가상계좌 입금을 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_ACCOUNT, null);
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("radio", "choosePaymentWay", "계좌이체", (long) 3);
				break;
		}

		if (null != mFinalCheckDialog)
		{
			mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					mClickView.setClickable(true);
					mClickView.setEnabled(true);
				}
			});

			mFinalCheckDialog.show();
		}
	}

	private void showFinalCheckDialog()
	{
		if (mFinalCheckDialog != null)
		{
			mFinalCheckDialog.cancel();
		}

		mFinalCheckDialog = new Dialog(this);

		mFinalCheckDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mFinalCheckDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		mFinalCheckDialog.setCanceledOnTouchOutside(false);

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		Window window = mFinalCheckDialog.getWindow();
		layoutParams.copyFrom(window.getAttributes());

		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		window.setAttributes(layoutParams);

		final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(BookingActivity.this);

		TextView tvMsg = (TextView) finalCheckLayout.findViewById(R.id.tv_confirm_payment_msg);

		final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
		final TextView btnProceed = (TextView) finalCheckLayout.findViewById(R.id.btn_confirm_payment_proceed);
		ImageView btnClose = (ImageView) finalCheckLayout.findViewById(R.id.btn_confirm_payment_close);

		btnProceed.setEnabled(false);

		tvMsg.setText(Html.fromHtml(getString(R.string.dialog_msg_payment_creditcard_confirm)));

		finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
		{
			@Override
			public void onConfirmSignature()
			{
				btnProceed.setEnabled(true);
				btnProceed.setBackgroundResource(R.drawable.shape_button_common_background);
				btnProceed.setTextColor(getResources().getColor(R.color.white));

				agreeSinatureTextView.setVisibility(View.GONE);

				btnProceed.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						synchronized (BookingActivity.this)
						{
							if (isLockUiComponent() == true)
							{
								return;
							}

							lockUI();

							mFinalCheckDialog.dismiss();

							mAliveCallSource = "PAYMENT";
							mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, BookingActivity.this));
							RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "agreePayment", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
						}
					}
				});
			}
		});

		btnClose.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mFinalCheckDialog.cancel();
			}
		});

		mFinalCheckDialog.setContentView(finalCheckLayout);
		mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				mClickView.setClickable(true);
				mClickView.setEnabled(true);
			}
		});

		mFinalCheckDialog.show();
	}

	private void showStopOnSaleDialog()
	{
		getPaymentConfirmDialog(DIALOG_CONFIRM_STOP_ONSALE, new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_NOT_ONSALE);
				finish();
			}
		}).show();
	}

	private boolean isValidEmail(String inputStr)
	{
		Pattern p = Pattern.compile("^[_a-zA-Z0-9-]+(.[_a-zA-Z0-9-]+)*@(?:\\w+\\.)+\\w+$");
		Matcher m = p.matcher(inputStr);
		return m.matches();
	}

	private void writeLogPaid(Pay pay)
	{
		try
		{
			String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
			String hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
			Date date = new Date();
			String strDate = dateFormat.format(date);
			int userIdx = Integer.parseInt(pay.getCustomer().getUserIdx());
			String userIdxStr = String.format("%07d", userIdx);
			String transId = strDate + userIdxStr;

			RenewalGaManager.getInstance(getApplicationContext()).purchaseComplete(transId, pay.getHotelDetail().getHotel().getName(), pay.getHotelDetail().getHotel().getCategory().name(), (double) pay.getPayPrice());

			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
			strDate = dateFormat2.format(date);

			mMixpanel.getPeople().identify(userIdxStr);

			JSONObject properties = new JSONObject();
			properties.put("hotelName", pay.getHotelDetail().getHotel().getName());
			properties.put("datetime", strDate); // 거래 시간 = 연-월-일T시:분:초
			ExLog.e("properties hotel name : " + pay.getHotelDetail().getHotel().getName() + " datetime : " + strDate);

			mMixpanel.getPeople().trackCharge(pay.getPayPrice(), properties); // price = 결제 금액

			JSONObject props = new JSONObject();
			props.put("hotelName", pay.getHotelDetail().getHotel().getName());
			props.put("price", pay.getPayPrice());
			props.put("datetime", strDate);
			props.put("userId", userIdxStr);
			props.put("tranId", transId);
			ExLog.e("props hotelName : " + pay.getHotelDetail().getHotel().getName() + " price : " + pay.getPayPrice() + " datetime : " + strDate);

			mMixpanel.track("transaction", props);

			RenewalGaManager.getInstance(getApplicationContext()).recordScreen("paymentConfirmation", "/todays-hotels/" + region + "/" + hotelName + "/booking-detail/payment-confirm");
		} catch (Exception e)
		{
			ExLog.e(e.toString());
		}
	}

	private class TelophoneClickSpannable extends ClickableSpan
	{
		public TelophoneClickSpannable()
		{
		}

		@Override
		public void updateDrawState(TextPaint textPain)
		{
			textPain.setColor(getResources().getColor(R.color.booking_tel_link));
			textPain.setFakeBoldText(true);
			textPain.setUnderlineText(true);
		}

		@Override
		public void onClick(View widget)
		{
			getPaymentConfirmDialog(DIALOG_CONFIRM_CALL, new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "callHotel", mPay.getHotelDetail().getHotel().getName(), (long) mHotelIdx);
					Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
					startActivity(i);
				}
			}).show();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				if (response.getBoolean("login") == true)
				{
					VolleyHttpClient.createCookie();

					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_TIME).toString(), null, mAppTimeJsonResponseListener, BookingActivity.this));
				} else
				{
					unLockUI();
				}
			} catch (JSONException e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				Customer buyer = new Customer();
				buyer.setEmail(response.getString("email"));
				buyer.setName(response.getString("name"));
				buyer.setPhone(response.getString("phone"));
				buyer.setAccessToken(response.getString("accessToken"));
				buyer.setUserIdx(response.getString("idx"));

				mPay.setCustomer(buyer);

				/**
				 * 텍스트 필드가 하나라도 비어있으면 해당 정보를 입력 받도록 함.
				 */
				if (isEmptyTextField(new String[] { buyer.getEmail(), buyer.getPhone(), buyer.getName() }) == false)
				{
					etReserverName.setEnabled(false);
					etReserverNumber.setEnabled(false);
					etReserverEmail.setEnabled(false);

					etReserverName.setBackgroundResource(0);
					etReserverNumber.setBackgroundResource(0);
					etReserverEmail.setBackgroundResource(0);

					etReserverName.setText(buyer.getName());
					etReserverNumber.setText(buyer.getPhone());
					etReserverEmail.setText(buyer.getEmail());

					mIsEditMode = false;
				} else
				{
					ExLog.e("buyer :" + buyer.getName() + " / " + buyer.getPhone() + " / " + buyer.getEmail());

					if (isEmptyTextField(buyer.getName()) == false)
					{
						etReserverName.setEnabled(false);
						etReserverName.setBackgroundResource(0);
						etReserverName.setText(buyer.getName());
					} else
					{
						etReserverName.setEnabled(true);

						// 회원 가입시 이름 필터 적용.
						StringFilter stringFilter = new StringFilter(BookingActivity.this);
						InputFilter[] allowAlphanumericHangul = new InputFilter[1];
						allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

						etReserverName.setFilters(allowAlphanumericHangul);

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
						{
							etReserverName.setBackground(mEditTextBackground[0]);
						} else
						{
							etReserverName.setBackgroundDrawable(mEditTextBackground[0]);
						}

						etReserverName.setText(buyer.getName());

						mIsEditMode = true;
					}

					if (isEmptyTextField(buyer.getPhone()) == false)
					{
						etReserverNumber.setEnabled(false);
						etReserverNumber.setBackgroundResource(0);
						etReserverNumber.setText(buyer.getPhone());
					} else
					{
						TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

						etReserverNumber.setText(telephonyManager.getLine1Number());
						etReserverNumber.setEnabled(true);

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
						{
							etReserverNumber.setBackground(mEditTextBackground[1]);
						} else
						{
							etReserverNumber.setBackgroundDrawable(mEditTextBackground[1]);
						}

						mIsEditMode = true;
					}

					if (isEmptyTextField(buyer.getEmail()) == false)
					{
						etReserverEmail.setEnabled(false);
						etReserverEmail.setBackgroundResource(0);
						etReserverEmail.setText(buyer.getEmail());
					} else
					{
						etReserverEmail.setEnabled(true);
						etReserverEmail.setText(buyer.getEmail());

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
						{
							etReserverEmail.setBackground(mEditTextBackground[2]);
						} else
						{
							etReserverEmail.setBackgroundDrawable(mEditTextBackground[2]);
						}

						mIsEditMode = true;
					}
				}

				// SailIndex가 0인 경우에 서버에 이슈가 발생할수 있다.
				// 0인 경우 아마도 메모리에서 정보가 삭제되어 발생한듯 하다.
				if (mPay.getHotelDetail().getSaleIdx() == 0)
				{
					// 세션이 만료되어 재시작 요청.
					restartApp();
				} else
				{
					// 체크인 정보 요청
					//				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_CHECKIN).append('/').append(mPay.getHotelDetail().getSaleIdx()).toString(), null, mReserveCheckInJsonResponseListener, BookingActivity.this));
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_CHECKINOUT).append('/').append(mPay.getHotelDetail().getSaleIdx()).toString(), null, mReserveCheckInJsonResponseListener, BookingActivity.this));
				}
			} catch (Exception e)
			{
				onError(e);
			}

		}
	};

	private DailyHotelJsonResponseListener mAppSaleTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{

			// 앱 시간이 오픈 시간보다 크고 클로즈 시간보다 작은경우 다음 스텝으로 이동
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				String open = response.getString("open");
				String close = response.getString("close");

				saleTime.setOpenTime(open);
				saleTime.setCloseTime(close);

				if (saleTime.isSaleTime() == true)
				{
					Customer buyer = mPay.getCustomer();

					buyer.setEmail(etReserverEmail.getText().toString());
					buyer.setPhone(etReserverNumber.getText().toString());
					buyer.setName(etReserverName.getText().toString());

					// 등록된 신용카드 인경우에는 빌링 키를 추가한다.
					if (mPay.getType() == Pay.Type.EASY_CARD && mSelectedCreditCard != null)
					{
						buyer.mBillingKey = mSelectedCreditCard.billingkey;
					}

					mPay.setCustomer(buyer);

					lockUI();

					// 호텔 디테일 정보 재 요청
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append('/').append(mPay.getHotelDetail().getHotel().getIdx()).append("/").append(mSaleTime.getRequestHotelDateFormat("yy/MM/dd")).toString(), null, mFinalCheckPayJsonResponseListener, BookingActivity.this));
				} else
				{
					unLockUI();

					android.content.DialogInterface.OnClickListener posListener = new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();
							setResult(CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED);
							finish();
						}
					};

					SimpleAlertDialog.build(BookingActivity.this, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_sales_closed), getString(R.string.dialog_btn_text_confirm), posListener).show();
				}

			} catch (JSONException e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mUserUpdateFacebookJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				ExLog.e("UPDATE_FACEBOOK_RESULT : " + response.toString());

				if (response.getBoolean("result") == false)
				{
					unLockUI();

					DailyToast.showToast(BookingActivity.this, response.getString("message"), Toast.LENGTH_LONG);
				} else
				{
					etReserverName.setEnabled(false);
					etReserverNumber.setEnabled(false);
					etReserverEmail.setEnabled(false);

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
					{
						etReserverName.setBackground(null);
						etReserverNumber.setBackground(null);
						etReserverEmail.setBackground(null);
					} else
					{
						etReserverName.setBackgroundDrawable(null);
						etReserverNumber.setBackgroundDrawable(null);
						etReserverEmail.setBackgroundDrawable(null);
					}

					etReserverName.setText(etReserverName.getText().toString());
					etReserverNumber.setText(etReserverNumber.getText().toString());
					etReserverEmail.setText(etReserverEmail.getText().toString());

					mIsEditMode = false;

					btnPay.performClick();
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mReserveCheckInJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			long checkin = 0;
			long checkout = 0;

			try
			{
				checkin = Long.valueOf(response.getString("checkin"));
				checkout = Long.valueOf(response.getString("checkout"));
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				onError(e);
				return;
			}

			if (checkin == 0 || checkout == 0)
			{
				onError();
				return;
			}

			// Check In
			Calendar calendarCheckin = DailyCalendar.getInstance();
			calendarCheckin.setTimeInMillis(checkin);

			//			if (locale.equals("한국어") == true)
			//			{
			SimpleDateFormat formatDay = new SimpleDateFormat("M월 d일 (EEE)", Locale.KOREA);
			formatDay.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));

			SimpleDateFormat formatHour = new SimpleDateFormat("HH시", Locale.KOREA);
			formatHour.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));

			mCheckinDayTextView.setText(formatDay.format(calendarCheckin.getTime()));
			mCheckinTimeTextView.setText(formatHour.format(calendarCheckin.getTime()));
			//			}

			// CheckOut
			Calendar calendarCheckout = DailyCalendar.getInstance();
			calendarCheckout.setTimeInMillis(checkout);

			SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-hh", Locale.KOREA);
			format.setTimeZone(TimeZone.getTimeZone("GMT+09:00"));
			String formatCheckout = format.format(calendarCheckout.getTime());

			mPay.setCheckOut(formatCheckout);

			//			if (locale.equals("한국어") == true)
			//			{
			mCheckoutDayTextView.setText(formatDay.format(calendarCheckout.getTime()));
			mCheckoutTimeTextView.setText(formatHour.format(calendarCheckout.getTime()));
			//			}

			// credit card 요청
			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, BookingActivity.this));
		}
	};

	private DailyHotelStringResponseListener mReserveSavedMoneyStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{
			try
			{
				String result = null;

				if (TextUtils.isEmpty(response) == true)
				{
					throw new NullPointerException("response is null.");
				} else
				{
					result = response.trim();
				}

				String bonus = result.replaceAll(",", "");
				mPay.setCredit(new Credit(null, bonus, null));

				int originalPrice = mPay.getHotelDetail().getHotel().getDiscount();
				DecimalFormat comma = new DecimalFormat("###,##0");

				//				if ("한국어".equalsIgnoreCase(locale) == true)
				//				{
				tvOriginalPriceValue.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));
				tvPrice.setText(comma.format(originalPrice) + Html.fromHtml(getString(R.string.currency)));
				//				} else
				//				{
				//					tvOriginalPriceValue.setText(Html.fromHtml(getString(R.string.currency)) + comma.format(originalPrice));
				//					tvPrice.setText(Html.fromHtml(getString(R.string.currency)) + comma.format(originalPrice));
				//				}

				mPay.setPayPrice(originalPrice);

				boolean checked = swCredit.isChecked();

				if (checked == false)
				{
					onCheckedChanged(swCredit, false);
				} else
				{
					swCredit.setChecked(false);
				}

				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, BookingActivity.this));

			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mAppTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				long time = response.getLong("time");

				saleTime.setCurrentTime(time);

				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_APP_SALE_TIME).toString(), null, mAppSaleTimeJsonResponseListener, BookingActivity.this));

			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mReservValidateJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");
				boolean result = response.getBoolean("data");

				if (result == true)
				{
					unLockUI();

					// 간편 결제를 시도하였으나 결제할 카드가 없는 경우.
					if (mPay.getType() == Pay.Type.EASY_CARD)
					{
						if (mSelectedCreditCard == null)
						{
							if (mClickView != null)
							{
								mClickView.setClickable(true);
								mClickView.setEnabled(true);
							}

							Intent intent = new Intent(BookingActivity.this, RegisterCreditCardActivity.class);
							startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
							overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
						} else
						{
							showFinalCheckDialog();
						}
					} else
					{
						showAgreeTermDialog(mPay.getType());
					}
				} else
				{
					String msg = response.getString("msg");
					String title = getString(R.string.dialog_notice2);
					String positive = getString(R.string.dialog_btn_text_confirm);

					SimpleAlertDialog.build(BookingActivity.this, title, msg, positive, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							finish();
						}
					}).show();
				}
			} catch (Exception e)
			{
				unLockUI();
				onError(e);
			}
		}
	};

	private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{
			ExLog.e("USER_ALIVE / CALL_RESOURCE : " + response.toString() + " / " + mAliveCallSource);

			/**
			 * ALIVE CALL은 소스가 두종류, 1. BookingActivity => PaymentActivity로 넘어갈때
			 * 2. PaymentActivity => BookingActivity로 넘어왔을때
			 */
			String result = null;

			if (TextUtils.isEmpty(response) == false)
			{
				result = response.trim();
			}

			if ("PAYMENT".equalsIgnoreCase(mAliveCallSource) == true)
			{
				// 임시로 강제 로그인 시킨다.
				result = "";

				if ("alive".equalsIgnoreCase(result) == true)
				{
					//1번 
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_TIME).toString(), null, mAppTimeJsonResponseListener, BookingActivity.this));
				} else
				{
					if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false) == true)
					{
						String id = sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
						String accessToken = sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
						String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);

						Map<String, String> loginParams = new HashMap<String, String>();

						if (accessToken != null)
						{
							loginParams.put("accessToken", accessToken);
						} else
						{
							loginParams.put("email", id);
						}

						loginParams.put("pw", pw);

						mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, BookingActivity.this));
					} else
					{
						unLockUI();
					}
				}
			} else if ("ACTIVITY_RESULT".equalsIgnoreCase(mAliveCallSource) == true)
			{
				unLockUI();

				//2번 
				activityResulted(mReqCode, mResCode, mResIntent);
			}

			//			
			//			// 임시로 강제 로그인 시킨다.
			//			if ("alive".equalsIgnoreCase(result) == true)
			//			{
			//				if ("PAYMENT".equalsIgnoreCase(mAliveCallSource) == true)
			//				{
			//					//1번 
			//					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_TIME).toString(), null, mAppTimeJsonResponseListener, BookingActivity.this));
			//				} else if ("ACTIVITY_RESULT".equalsIgnoreCase(mAliveCallSource) == true)
			//				{
			//					unLockUI();
			//
			//					//2번 
			//					activityResulted(mReqCode, mResCode, mResIntent);
			//				}
			//			} else
			//			{
			//				if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false) == true)
			//				{
			//					String id = sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
			//					String accessToken = sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
			//					String pw = sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);
			//
			//					Map<String, String> loginParams = new HashMap<String, String>();
			//
			//					if (accessToken != null)
			//					{
			//						loginParams.put("accessToken", accessToken);
			//					} else
			//					{
			//						loginParams.put("email", id);
			//					}
			//
			//					loginParams.put("pw", pw);
			//
			//					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, BookingActivity.this));
			//				} else
			//				{
			//					unLockUI();
			//				}
			//			}
		}
	};

	private DailyHotelJsonResponseListener mUserSessionBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			int msg_code = -1;

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					ExLog.d("msg_code : " + msg_code);
				}

				JSONArray jsonArray = response.getJSONArray("data");
				int length = jsonArray.length();

				if (length == 0)
				{
					// 카드 관리 관련 화면을 보여주지 않는다.
					mCardManagerButton.setVisibility(View.INVISIBLE);

					mSelectedCreditCard = null;
					mSimplePaymentRadioButton.setChecked(true);
					mSimplePaymentRadioButton.setText(R.string.label_booking_easypayment);
				} else
				{
					mCardManagerButton.setVisibility(View.VISIBLE);

					if (mSelectedCreditCard == null)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(0);

						mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"));
						mSimplePaymentRadioButton.setChecked(true);
					} else
					{
						boolean hasCreditCard = false;

						for (int i = 0; i < length; i++)
						{
							JSONObject jsonObject = jsonArray.getJSONObject(i);

							if (mSelectedCreditCard.billingkey.equals(jsonObject.getString("billkey")) == true)
							{
								hasCreditCard = true;
								break;
							}
						}

						// 기존에 선택한 카드를 지우고 돌아온 경우.
						if (hasCreditCard == false)
						{
							JSONObject jsonObject = jsonArray.getJSONObject(0);

							mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"));
						}
					}

					mSimplePaymentRadioButton.setText(String.format("%s %s", mSelectedCreditCard.name.replace("카드", ""), mSelectedCreditCard.number));
				}

				// 호텔 가격 정보가 변경되었습니다.
				if (mIsChangedPay == true)
				{
					mIsChangedPay = false;

					String title = getString(R.string.dialog_notice2);
					String msg = getString(R.string.dialog_msg_changed_pay);
					String positive = getString(R.string.dialog_btn_text_confirm);

					SimpleAlertDialog.build(BookingActivity.this, title, msg, positive, (DialogInterface.OnClickListener) null).show();
				}
			} catch (Exception e)
			{
				// 해당 화면 에러시에는 일반 결제가 가능해야 한다.
				ExLog.e(e.toString());
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mUserRegisterBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			int msg_code = -1;

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					ExLog.d("msg_code : " + msg_code);
				}

				JSONArray jsonArray = response.getJSONArray("data");
				int length = jsonArray.length();

				if (length == 0)
				{
					// 카드 관리 관련 화면을 보여주지 않는다.
					mCardManagerButton.setVisibility(View.INVISIBLE);

					mSelectedCreditCard = null;
					mSimplePaymentRadioButton.setText(R.string.label_booking_easypayment);

				} else
				{
					mCardManagerButton.setVisibility(View.VISIBLE);

					JSONObject jsonObject = jsonArray.getJSONObject(0);

					mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"));
					mSimplePaymentRadioButton.setText(String.format("%s %s", mSelectedCreditCard.name.replace("카드", ""), mSelectedCreditCard.number));

					// final check 결제 화면을 보여준다.
					showFinalCheckDialog();
				}
			} catch (Exception e)
			{
				// 해당 화면 에러시에는 일반 결제가 가능해야 한다.
				ExLog.e(e.toString());
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mHotelDetailJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				ExLog.d("hotel_detail : " + response.toString());
				JSONArray bookingArr = response.getJSONArray("detail");
				JSONObject detailObj = bookingArr.getJSONObject(0);

				int discount = Integer.parseInt(detailObj.getString("discount"));
				int price = Integer.parseInt(detailObj.getString("price"));

				HotelDetail hotelDetail = mPay.getHotelDetail();
				Hotel hotelBasic = hotelDetail.getHotel();

				// 가격이 변동 되었다.
				if (hotelBasic.getDiscount() != discount)
				{
					mIsChangedPay = true;
				}

				hotelBasic.setAddress(detailObj.getString("address"));
				hotelBasic.setName(detailObj.getString("hotel_name"));
				hotelBasic.setDiscount(discount);
				hotelBasic.setPrice(price);
				hotelBasic.setCategory(detailObj.getString("cat"));
				hotelBasic.setBedType(detailObj.getString("bed_type"));

				hotelDetail.setHotel(hotelBasic);

				boolean isOnSale = false; // 판매중인 상품인지.

				try
				{
					if (detailObj.has("on_sale") == true)
					{
						isOnSale = detailObj.getInt("on_sale") == 1;
					}
				} catch (Exception e)
				{
					ExLog.e(e.toString());
				}

				double latitude = detailObj.getDouble("lat");
				double longitude = detailObj.getDouble("lng");

				hotelDetail.setLatitude(latitude);
				hotelDetail.setLongitude(longitude);

				int saleIdx = detailObj.getInt("idx");
				hotelDetail.setSaleIdx(saleIdx);

				mPay.setHotelDetail(hotelDetail);

				// credit 요청
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERVE_SAVED_MONEY).toString(), null, mReserveSavedMoneyStringResponseListener, BookingActivity.this));

				// 판매 중지 상품으로 호텔 리스트로 복귀 시킨다.
				if (isOnSale == false)
				{
					showStopOnSaleDialog();
				}
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				unLockUI();

				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mFinalCheckPayJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				JSONArray bookingArr = response.getJSONArray("detail");
				JSONObject detailObj = bookingArr.getJSONObject(0);

				int discount = Integer.parseInt(detailObj.getString("discount"));
				int price = Integer.parseInt(detailObj.getString("price"));

				HotelDetail hotelDetail = mPay.getHotelDetail();
				Hotel hotelBasic = hotelDetail.getHotel();

				// 가격이 변동 되었다.
				if (hotelBasic.getDiscount() != discount)
				{
					mIsChangedPay = true;
				}

				hotelBasic.setAddress(detailObj.getString("address"));
				hotelBasic.setName(detailObj.getString("hotel_name"));
				hotelBasic.setDiscount(discount);
				hotelBasic.setPrice(price);
				hotelBasic.setCategory(detailObj.getString("cat"));
				hotelBasic.setBedType(detailObj.getString("bed_type"));

				hotelDetail.setHotel(hotelBasic);

				double latitude = detailObj.getDouble("lat");
				double longitude = detailObj.getDouble("lng");

				hotelDetail.setLatitude(latitude);
				hotelDetail.setLongitude(longitude);

				int saleIdx = detailObj.getInt("idx");
				hotelDetail.setSaleIdx(saleIdx);

				mPay.setHotelDetail(hotelDetail);

				boolean isOnSale = false; // 판매중인 상품인지.

				try
				{
					if (detailObj.has("on_sale") == true)
					{
						isOnSale = detailObj.getInt("on_sale") == 1;
					}
				} catch (Exception e)
				{
					ExLog.e(e.toString());
				}

				if (isOnSale == false)
				{
					showStopOnSaleDialog();
				} else if (mIsChangedPay == true)
				{
					mIsChangedPay = false;

					// 현재 있는 팝업을 없애도록 한다.
					if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
					{
						mFinalCheckDialog.cancel();
						mFinalCheckDialog = null;
					}

					String title = getString(R.string.dialog_notice2);
					String msg = getString(R.string.dialog_msg_changed_pay);
					String positive = getString(R.string.dialog_btn_text_confirm);

					SimpleAlertDialog.build(BookingActivity.this, title, msg, positive, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							lockUI();

							// 호텔 디테일 정보 재 요청
							mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append('/').append(mPay.getHotelDetail().getHotel().getIdx()).append("/").append(mSaleTime.getRequestHotelDateFormat("yy/MM/dd")).toString(), null, mHotelDetailJsonResponseListener, BookingActivity.this));
						}
					}).show();
				} else
				{
					moveToPayStep();
				}
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mUserSessionBillingPayment = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			if (mProgressDialog != null && mProgressDialog.isShowing() == true)
			{
				mProgressDialog.dismiss();
			}

			mProgressDialog = null;

			try
			{
				int msg_code = -1;

				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				//				---------------------------------
				//				* 기본 포맷 *
				//				{msg_code : value, msg: value, data : value}
				//				---------------------------------
				//				* 성공시 *
				//				msg_code : 0
				//				data : PAYMENT_COMPLETE
				//				---------------------------------
				//				* 실패시 *
				//				msg_code : 200
				//				msg : 에러 메시지
				//				data : PAYMENT_CANCELED, NOT_AVAILABLE, SOLD_OUT, ACCOUNT_DUPLICATE, INVALID_DATE

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");

				Intent intent = new Intent();
				intent.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

				if (msg_code == 0)
				{
					activityResulted(CODE_REQUEST_ACTIVITY_PAYMENT, CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE, intent);
				} else
				{
					String data = response.getString("data");
					String msg = null;

					if (response.has("msg") == true)
					{
						msg = response.getString("msg");
					}

					int resultCode = 0;

					if (TextUtils.isEmpty(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;
					} else if ("SOLD_OUT".equalsIgnoreCase(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT;
					} else if ("INVALID_DATE".equalsIgnoreCase(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE;
					} else if ("PAYMENT_CANCELED".equalsIgnoreCase(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCELED;
					} else if ("ACCOUNT_DUPLICATE".equalsIgnoreCase(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE;
					} else if ("NOT_AVAILABLE".equalsIgnoreCase(data) == true)
					{
						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE;
					} else
					{
						if (msg != null)
						{
							intent.putExtra(NAME_INTENT_EXTRA_DATA_MESSAGE, msg);
						}

						resultCode = CODE_RESULT_ACTIVITY_PAYMENT_UNKNOW_ERROR;
					}

					activityResulted(CODE_REQUEST_ACTIVITY_PAYMENT, resultCode, intent);
				}
			} catch (Exception e)
			{
				ExLog.e(e.toString());

				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};
}
