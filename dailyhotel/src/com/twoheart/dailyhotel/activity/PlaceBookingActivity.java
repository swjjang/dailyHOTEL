/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingActivity (예약 화면)
 * 
 * 결제 화면으로 넘어가기 전 예약 정보를 보여주고 결제방식을 선택할 수 있는 화면 
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.FinalCheckLayout;
import com.twoheart.dailyhotel.view.widget.DailySignatureView;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ResourceAsColor" })
public abstract class PlaceBookingActivity extends BaseActivity
{
	protected static final int DEFAULT_AVAILABLE_RESERVES = 20000;

	private static final int DIALOG_CONFIRM_PAYMENT_CARD = 0;
	private static final int DIALOG_CONFIRM_PAYMENT_HP = 1;
	private static final int DIALOG_CONFIRM_PAYMENT_ACCOUNT = 2;

	protected static final int STATE_NONE = 0;
	protected static final int STATE_ACTIVITY_RESULT = 1;
	protected static final int STATE_PAYMENT = 2;

	protected TicketPayment mTicketPayment;
	protected CreditCard mSelectedCreditCard;
	protected boolean mIsChangedPrice; // 가격이 변경된 경우.

	private int mReqCode;
	private int mResCode;
	private Intent mResIntent;
	protected int mState;
	protected Dialog mFinalCheckDialog;
	private ProgressDialog mProgressDialog;

	protected SaleTime mCheckInSaleTime;
	protected boolean mIsEditMode;

	protected abstract void requestPayEasyPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime);

	protected abstract void requestTicketPaymentInfomation(int index);

	protected abstract void requestValidateTicketPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime);

	protected abstract void updatePaymentInformation(TicketPayment ticketPayment, CreditCard creditCard);

	protected abstract void checkPaymentType(TicketPayment.PaymentType type);

	protected abstract void updateLayout(TicketPayment ticketPayment, CreditCard creditCard);

	@Override
	protected void onResume()
	{
		super.onResume();

		if ((mState == STATE_ACTIVITY_RESULT == true && mReqCode == CODE_REQUEST_ACTIVITY_PAYMENT) || mState == STATE_PAYMENT)
		{

		} else
		{
			lockUI();

			Map<String, String> params = new HashMap<String, String>();
			params.put("timeZone", "Asia/Seoul");

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, PlaceBookingActivity.this));
		}
	}

	@Override
	public void onErrorResponse(VolleyError error)
	{
		super.onErrorResponse(error);

		hidePorgressDialog();
	}

	@Override
	public void onError()
	{
		super.onError();

		showSimpleDialog(null, getString(R.string.act_toast_payment_network_error), getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		}, null, false);
	}

	/**
	 * 결제 수단에 알맞은 결제 동의 확인 다이얼로그를 만든다.
	 * 
	 * @param type
	 *            CARD, ACCOUNT, HP 세가지 타입 존재.
	 * @return 타입에 맞는 결제 동의 다이얼로그 반환.
	 */

	private Dialog getPaymentConfirmDialog(int type)
	{
		final Dialog dialog = new Dialog(this);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setCanceledOnTouchOutside(false);

		View view = LayoutInflater.from(this).inflate(R.layout.fragment_dialog_confirm_payment, null);
		LinearLayout messageLayout1 = (LinearLayout) view.findViewById(R.id.messageLayout1);
		LinearLayout messageLayout2 = (LinearLayout) view.findViewById(R.id.messageLayout2);
		LinearLayout messageLayout3 = (LinearLayout) view.findViewById(R.id.messageLayout3);
		LinearLayout messageLayout4 = (LinearLayout) view.findViewById(R.id.messageLayout4);

		TextView messageTextView1 = (TextView) messageLayout1.findViewById(R.id.messageTextView1);
		TextView messageTextView2 = (TextView) messageLayout2.findViewById(R.id.messageTextView2);
		TextView messageTextView3 = (TextView) messageLayout3.findViewById(R.id.messageTextView3);
		TextView messageTextView4 = (TextView) messageLayout4.findViewById(R.id.messageTextView4);

		messageTextView1.setTypeface(FontManager.getInstance(this).getMediumTypeface());
		String message1 = getString(R.string.dialog_msg_payment_message01);

		SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(message1);

		int startIndex = message1.indexOf("취소");
		int length = "취소, 변경, 환불이 절대 불가".length();

		spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.dialog_title_text)), //
		startIndex, startIndex + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), //
		startIndex, startIndex + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		messageTextView1.setText(spannableStringBuilder);

		messageTextView2.setTypeface(FontManager.getInstance(this).getMediumTypeface());
		messageTextView3.setTypeface(FontManager.getInstance(this).getMediumTypeface());
		messageTextView4.setTypeface(FontManager.getInstance(this).getMediumTypeface());

		if (Util.isOverAPI21() == true)
		{
			LinearLayout.LayoutParams layoutParams2 = (android.widget.LinearLayout.LayoutParams) messageLayout2.getLayoutParams();
			layoutParams2.topMargin = Util.dpToPx(PlaceBookingActivity.this, 17);

			LinearLayout.LayoutParams layoutParams3 = (android.widget.LinearLayout.LayoutParams) messageLayout3.getLayoutParams();
			layoutParams3.topMargin = Util.dpToPx(PlaceBookingActivity.this, 17);

			LinearLayout.LayoutParams layoutParams4 = (android.widget.LinearLayout.LayoutParams) messageLayout4.getLayoutParams();
			layoutParams4.topMargin = Util.dpToPx(PlaceBookingActivity.this, 17);
		}

		View agreeLayout = view.findViewById(R.id.agreeLayout);

		switch (type)
		{
			// 핸드폰 결제
			case DIALOG_CONFIRM_PAYMENT_HP:
				messageLayout4.setVisibility(View.VISIBLE);
				messageTextView4.setText(R.string.dialog_msg_payment_message04);
				break;

			// 계좌 이체
			case DIALOG_CONFIRM_PAYMENT_ACCOUNT:
				messageLayout4.setVisibility(View.VISIBLE);
				messageTextView4.setText(R.string.dialog_msg_payment_message05);
				break;

			// 신용카드 일반 결제
			case DIALOG_CONFIRM_PAYMENT_CARD:
				messageLayout4.setVisibility(View.GONE);
				break;

			default:
				return null;
		}

		OnClickListener buttonOnClickListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dialog.dismiss();

				synchronized (PlaceBookingActivity.this)
				{
					if (isLockUiComponent() == true)
					{
						return;
					}

					lockUI();

					mState = STATE_PAYMENT;

					// 1. 세션이 살아있는지 검사 시작.
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, PlaceBookingActivity.this));

					HashMap<String, String> params = new HashMap<String, String>();
					params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
					params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
					params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

					AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.PAYMENT_AGREE_POPUP, Action.CLICK, mTicketPayment.paymentType.name(), params);
				}
			}
		};

		agreeLayout.setOnClickListener(buttonOnClickListener);

		dialog.setContentView(view);

		return dialog;

	}

	protected void processPayment()
	{
		unLockUI();

		if (mTicketPayment.paymentType == TicketPayment.PaymentType.EASY_CARD)
		{
			if (isFinishing() == true)
			{
				return;
			}

			if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
			{
				mFinalCheckDialog.dismiss();
			}

			showProgressDialog();

			requestPayEasyPayment(mTicketPayment, mCheckInSaleTime);
		} else
		{
			Intent intent = new Intent(this, com.twoheart.dailyhotel.activity.TicketPaymentActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT, mTicketPayment);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mCheckInSaleTime);

			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PAYMENT);

			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}
	}

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

		mState = STATE_ACTIVITY_RESULT;

		lockUI();

		// 1. 세션이 연결되어있는지 검사.
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, this));
	}

	private void activityResulted(int requestCode, int resultCode, Intent intent)
	{
		//결제가 끝난 뒤 호출됨. 
		if (requestCode == CODE_REQUEST_ACTIVITY_PAYMENT)
		{
			String title = getString(R.string.dialog_title_payment);
			String msg = "";
			String posTitle = getString(R.string.dialog_btn_text_confirm);
			View.OnClickListener posListener = null;

			if (resultCode != CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE && resultCode != CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS)
			{
				mState = STATE_NONE;
			}

			switch (resultCode)
			{
				// 결제가 성공한 경우 GA와 믹스패널에 등록 
				case CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE:
				case CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS:
					writeLogPaid(mTicketPayment);

					posListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							mState = STATE_NONE;

							setResult(RESULT_OK);
							finish();
						}
					};

					if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
					{
						msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
					} else
					{
						msg = getString(R.string.act_toast_payment_success);
					}
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
					if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_RESULT) == true)
					{
						msg = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_RESULT);
					} else
					{
						msg = getString(R.string.act_toast_payment_fail);
					}

					posListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							finish();
						}
					};
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
					if (intent != null && intent.hasExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT) == true)
					{
						TicketPayment ticketPayment = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_TICKETPAYMENT);

						Editor editor = sharedPreference.edit();
						editor.putString(KEY_PREFERENCE_USER_IDX, ticketPayment.getCustomer().getUserIdx());
						editor.putString(KEY_PREFERENCE_PLACE_NAME, ticketPayment.getTicketInformation().placeName);
						editor.putInt(KEY_PREFERENCE_PLACE_TICKET_IDX, ticketPayment.getTicketInformation().index);
						editor.putString(KEY_PREFERENCE_PLACE_TICKET_CHECKOUT, ticketPayment.checkOutTime);
						editor.putString(KEY_PREFERENCE_PLACE_TICKET_CHECKIN, ticketPayment.checkInTime);
						editor.commit();
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

			if (isFinishing() == true)
			{
				return;
			}

			showSimpleDialog(title, msg, posTitle, posListener);
		} else if (requestCode == CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER)
		{
			mState = STATE_NONE;

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

							// 간편 결제로 체크 하기
							checkPaymentType(TicketPayment.PaymentType.EASY_CARD);
						}
					}
					break;

			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD)
		{
			mState = STATE_NONE;

			// 간편 결제 실행후 카드가 없어 등록후에 돌아온경우.
			String msg = null;

			switch (resultCode)
			{
				case CODE_RESULT_PAYMENT_BILLING_SUCCSESS:
					lockUI();

					// credit card 요청
					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserRegisterBillingCardInfoJsonResponseListener, PlaceBookingActivity.this));
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
				if (isFinishing() == true)
				{
					return;
				}

				String title = getString(R.string.dialog_notice2);
				String positive = getString(R.string.dialog_btn_text_confirm);

				showSimpleDialog(title, msg, positive, null);
			}
		} else
		{
			mState = STATE_NONE;
		}
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(this).recordScreen(Screen.BOOKING);
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
				if (isFinishing() == true)
				{
					return super.onOptionsItemSelected(item);
				}

				if (isLockUiComponent(true) == true)
				{
					return super.onOptionsItemSelected(item);
				}

				showCallDialog();

				return true;
			}

			default:
			{
				return super.onOptionsItemSelected(item);
			}
		}
	}

	@Override
	protected void onDestroy()
	{
		if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
		{
			mFinalCheckDialog.dismiss();
		}

		mFinalCheckDialog = null;

		hidePorgressDialog();

		super.onDestroy();
	}

	/**
	 * 결제 진행
	 */
	protected void processValidatePayment()
	{
		unLockUI();

		if (mTicketPayment.paymentType == TicketPayment.PaymentType.EASY_CARD)
		{
			// 간편 결제를 시도하였으나 결제할 카드가 없는 경우.
			if (mSelectedCreditCard == null)
			{
				Intent intent = new Intent(this, RegisterCreditCardActivity.class);
				startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTERCREDITCARD);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
			} else
			{
				showFinalCheckDialog();
			}
		} else
		{
			// 일반 결제 시도
			showAgreeTermDialog(mTicketPayment.paymentType);
		}

		String region = sharedPreference.getString(KEY_PREFERENCE_PLACE_REGION_SELECT_GA, null);

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
		params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
		params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);
		params.put(Label.AREA, region);

		AnalyticsManager.getInstance(this).recordEvent(Screen.BOOKING, Action.CLICK, Label.PAYMENT, params);
	}

	protected void showProgressDialog()
	{
		hidePorgressDialog();

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.dialog_msg_processing_payment));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	protected void hidePorgressDialog()
	{
		if (mProgressDialog != null)
		{
			if (mProgressDialog.isShowing() == true)
			{
				mProgressDialog.dismiss();
			}

			mProgressDialog = null;
		}
	}

	protected void showAgreeTermDialog(TicketPayment.PaymentType type)
	{
		if (type == null)
		{
			return;
		}

		if (mFinalCheckDialog != null)
		{
			mFinalCheckDialog.cancel();
		}

		mFinalCheckDialog = null;

		switch (type)
		{
			case CARD:
				// 신용카드를 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_CARD);
				break;

			case PHONE_PAY:
				// 핸드폰을 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_HP);
				break;

			case VBANK:
				// 가상계좌 입금을 선택했을 경우
				mFinalCheckDialog = getPaymentConfirmDialog(DIALOG_CONFIRM_PAYMENT_ACCOUNT);
				break;

			default:
				return;
		}

		AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.BOOKING, Action.CLICK, type.name(), 0L);

		if (null != mFinalCheckDialog)
		{
			if (isFinishing() == true)
			{
				return;
			}

			mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
			{
				@Override
				public void onDismiss(DialogInterface dialog)
				{
					releaseUiComponent();
				}
			});

			try
			{
				mFinalCheckDialog.show();
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}
	}

	protected void showFinalCheckDialog()
	{
		if (isFinishing() == true)
		{
			return;
		}

		if (mFinalCheckDialog != null)
		{
			mFinalCheckDialog.cancel();
			mFinalCheckDialog = null;
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

		final FinalCheckLayout finalCheckLayout = new FinalCheckLayout(PlaceBookingActivity.this);
		final TextView agreeSinatureTextView = (TextView) finalCheckLayout.findViewById(R.id.agreeSinatureTextView);
		final View agreeLayout = finalCheckLayout.findViewById(R.id.agreeLayout);

		agreeSinatureTextView.setTypeface(FontManager.getInstance(PlaceBookingActivity.this).getMediumTypeface());

		agreeLayout.setEnabled(false);

		finalCheckLayout.setOnUserActionListener(new DailySignatureView.OnUserActionListener()
		{
			@Override
			public void onConfirmSignature()
			{
				agreeLayout.setEnabled(true);
				agreeLayout.setBackgroundResource(R.drawable.popup_btn_on);

				agreeSinatureTextView.setVisibility(View.GONE);

				agreeLayout.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						synchronized (PlaceBookingActivity.this)
						{
							if (isLockUiComponent() == true)
							{
								return;
							}

							lockUI();

							mState = STATE_PAYMENT;

							// 1. 세션이 살아있는지 검사 시작.
							mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, PlaceBookingActivity.this));

							mFinalCheckDialog.dismiss();

							HashMap<String, String> params = new HashMap<String, String>();
							params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
							params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
							params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

							AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.PAYMENT_AGREE_POPUP, Action.CLICK, mTicketPayment.paymentType.name(), params);
						}
					}
				});
			}
		});

		mFinalCheckDialog.setContentView(finalCheckLayout);
		mFinalCheckDialog.setOnDismissListener(new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				releaseUiComponent();
			}
		});

		try
		{
			mFinalCheckDialog.show();
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}
	}

	protected void showCallDialog()
	{
		if (isFinishing() == true)
		{
			return;
		}

		View.OnClickListener positiveListener = new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				releaseUiComponent();

				if (Util.isTelephonyEnabled(PlaceBookingActivity.this) == true)
				{
					Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
					startActivity(i);
				} else
				{
					DailyToast.showToast(PlaceBookingActivity.this, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
				}

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Label.PLACE_TICKET_INDEX, String.valueOf(mTicketPayment.getTicketInformation().index));
				params.put(Label.PLACE_TICKET_NAME, mTicketPayment.getTicketInformation().name);
				params.put(Label.PLACE_NAME, mTicketPayment.getTicketInformation().placeName);

				AnalyticsManager.getInstance(PlaceBookingActivity.this).recordEvent(Screen.BOOKING, Action.CLICK, Label.CALL_CS, params);
			}
		};

		showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_call), getString(R.string.dialog_btn_call), null, positiveListener, null, null, new OnDismissListener()
		{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				releaseUiComponent();
			}
		}, true);

	}

	private void showStopOnSaleDialog()
	{
		if (isFinishing() == true)
		{
			return;
		}

		View.OnClickListener positiveListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		};

		showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_stop_onsale), getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, null, null, false);
	}

	protected void showChangedPayDialog()
	{
		if (isFinishing() == true)
		{
			return;
		}

		View.OnClickListener positiveListener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setResult(RESULT_CANCELED);
				finish();
			}
		};

		showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_changed_price), getString(R.string.dialog_btn_text_confirm), null, positiveListener, null, null, null, false);
	}

	private void showChangedBonusDialog()
	{
		// 적립금이 변동된 경우.
		if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
		{
			mFinalCheckDialog.cancel();
			mFinalCheckDialog = null;
		}

		String title = getString(R.string.dialog_notice2);
		String msg = getString(R.string.dialog_msg_changed_bonus);
		String positive = getString(R.string.dialog_btn_text_confirm);

		showSimpleDialog(title, msg, positive, new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				lockUI();

				mState = STATE_NONE;

				requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
			}
		});
	}

	protected void writeLogPaid(TicketPayment ticketPayment)
	{
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.KOREA);
			Date date = new Date();
			String strDate = dateFormat.format(date);
			String userIndex = ticketPayment.getCustomer().getUserIdx();
			String transId = strDate + userIndex;

			double price = ticketPayment.getPaymentToPay();

			TicketInformation ticketInformation = ticketPayment.getTicketInformation();

			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
			strDate = dateFormat2.format(date);

			AnalyticsManager.getInstance(getApplicationContext()).purchaseComplete(transId, userIndex, String.valueOf(ticketInformation.index), //
			ticketInformation.placeName, Label.PAYMENT, ticketPayment.checkInTime, ticketPayment.checkOutTime, ticketPayment.paymentType.name(), strDate, price);
		} catch (Exception e)
		{
			ExLog.e(e.toString());
		}
	}

	private void requestLogin()
	{
		// 세션이 종료되어있으면 다시 로그인한다.
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

			mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, PlaceBookingActivity.this));
		} else
		{
			unLockUI();
			restartApp();
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
			if (isFinishing() == true)
			{
				return;
			}

			if (isLockUiComponent(true) == true)
			{
				return;
			}

			showCallDialog();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Network Listener
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
					onInternalError();
					return;
				}

				if (response.getBoolean("login") == true)
				{
					VolleyHttpClient.createCookie();

					Map<String, String> params = new HashMap<String, String>();
					params.put("timeZone", "Asia/Seoul");

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, PlaceBookingActivity.this));
				} else
				{
					unLockUI();
				}
			} catch (Exception e)
			{
				onInternalError();
			}
		}
	};

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					onInternalError();
					return;
				}

				SaleTime saleTime = new SaleTime();

				saleTime.setCurrentTime(response.getLong("currentDateTime"));
				saleTime.setOpenTime(response.getLong("openDateTime"));
				saleTime.setCloseTime(response.getLong("closeDateTime"));
				saleTime.setDailyTime(response.getLong("dailyDateTime"));

				if (saleTime.isSaleTime() == true)
				{
					lockUI();

					requestValidateTicketPayment(mTicketPayment, mCheckInSaleTime);
				} else
				{
					unLockUI();

					View.OnClickListener posListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							setResult(CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED);
							finish();
						}
					};

					showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_sales_closed), getString(R.string.dialog_btn_text_confirm), posListener);
				}

			} catch (Exception e)
			{
				onInternalError();
			}
		}
	};

	protected DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					onInternalError();
					return;
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					if (response.has("msg") == true)
					{
						String msg = response.getString("msg");

						DailyToast.showToast(PlaceBookingActivity.this, msg, Toast.LENGTH_SHORT);
						finish();
						return;
					} else
					{
						throw new NullPointerException("response == null");
					}
				}

				JSONObject jsonData = response.getJSONObject("data");

				boolean isOnSession = jsonData.getBoolean("on_session");

				switch (mState)
				{
					case STATE_NONE:
					{
						if (isOnSession == true)
						{
							String name = jsonData.getString("user_name");
							String phone = jsonData.getString("user_phone");
							String email = jsonData.getString("user_email");
							String userIndex = jsonData.getString("user_idx");
							int bonus = jsonData.getInt("user_bonus");

							if (bonus < 0)
							{
								bonus = 0;
							}

							mTicketPayment.bonus = bonus;

							Customer buyer = new Customer();
							buyer.setEmail(email);
							buyer.setName(name);
							buyer.setPhone(phone);
							buyer.setUserIdx(userIndex);

							Guest guest = new Guest();
							guest.name = name;
							guest.phone = phone;
							guest.email = email;

							mTicketPayment.setCustomer(buyer);
							mTicketPayment.setGuest(guest);

							// 2. 화면 정보 얻기
							requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
						} else
						{
							requestLogin();
						}
						break;
					}

					case STATE_ACTIVITY_RESULT:
					{
						unLockUI();

						if (isOnSession == true)
						{
							activityResulted(mReqCode, mResCode, mResIntent);
						} else
						{
							requestLogin();
						}
						break;
					}

					case STATE_PAYMENT:
					{
						if (isOnSession == true)
						{
							int bonus = jsonData.getInt("user_bonus");

							if (bonus < 0)
							{
								bonus = 0;
							}

							if (mTicketPayment.isEnabledBonus == true && bonus != mTicketPayment.bonus)
							{
								// 보너스 값이 변경된 경우
								mTicketPayment.bonus = bonus;
								showChangedBonusDialog();
								return;
							}

							requestTicketPaymentInfomation(mTicketPayment.getTicketInformation().index);
						} else
						{
							requestLogin();
						}
						break;
					}
				}
			} catch (Exception e)
			{
				onInternalError();
			}
		}
	};

	protected DailyHotelJsonResponseListener mUserSessionBillingCardInfoJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			try
			{
				if (response == null)
				{
					onInternalError();
					return;
				}

				// 해당 화면은 메시지를 넣지 않는다.
				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					ExLog.d("msg_code : " + msg_code);
				}

				JSONArray jsonArray = response.getJSONArray("data");
				int length = jsonArray.length();

				if (length == 0)
				{
					mSelectedCreditCard = null;
				} else
				{
					if (mSelectedCreditCard == null)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(0);
						mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
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

							mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));
						}
					}
				}

				// 호텔 가격 정보가 변경되었습니다.
				if (mIsChangedPrice == true)
				{
					mIsChangedPrice = false;

					showChangedPayDialog();
				}

				updateLayout(mTicketPayment, mSelectedCreditCard);

				unLockUI();
			} catch (Exception e)
			{
				onInternalError();
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
					mSelectedCreditCard = null;
				} else
				{
					JSONObject jsonObject = jsonArray.getJSONObject(0);

					mSelectedCreditCard = new CreditCard(jsonObject.getString("card_name"), jsonObject.getString("print_cardno"), jsonObject.getString("billkey"), jsonObject.getString("cardcd"));

					updatePaymentInformation(mTicketPayment, mSelectedCreditCard);

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
}
