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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.FnBBookingLayout.UserInformationType;
import com.twoheart.dailyhotel.model.CreditCard;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.model.TicketPayment;
import com.twoheart.dailyhotel.model.TicketPayment.Type;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ResourceAsColor" })
public class FnBBookingActivity extends PlaceBookingActivity
{
	private FnBBookingLayout mFnBBookingLayout;

	public interface OnUserActionListener
	{
		public void plusTicketCount();

		public void minusTicketCount();

		public void editUserInformation();

		public void showCreditCardManager();

		public void setPaymentType(TicketPayment.Type type);

		public void pay();

		public void showCallDialog();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		System.gc();

		mTicketPayment = new TicketPayment();
		mFnBBookingLayout = new FnBBookingLayout(this, mOnUserActionListener);

		setContentView(mFnBBookingLayout.getLayout());

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			mTicketPayment.setTicketInformation((TicketInformation) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION));
			mCheckInSaleTime = bundle.getParcelable(NAME_INTENT_EXTRA_DATA_SALETIME);
		}

		if (mTicketPayment.getTicketInformation() == null)
		{
			finish();
			return;
		}

		mIsChangedPrice = false;

		setActionBar(mTicketPayment.getTicketInformation().placeName);
	}

	@Override
	protected void requestValidateTicketPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime)
	{
		if (ticketPayment == null || checkInSaleTime == null)
		{
			onInternalError();
			return;
		}

		String params = String.format("?sale_reco_idx=%d&sday=%s&ticket_count=%d", ticketPayment.getTicketInformation().index, checkInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), ticketPayment.count);
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_SESSION_TICKET_SELL_CHECK).append(params).toString(), null, mTicketSellCheckJsonResponseListener, this));
	}

	@Override
	protected void requestPayEasyPayment(TicketPayment ticketPayment, SaleTime checkInSaleTime)
	{
		String bonus = "0"; // 적립금

		if (ticketPayment.isEnabledBonus == true)
		{
			bonus = String.valueOf(ticketPayment.bonus);
		}

		Map<String, String> params = new HashMap<String, String>();

		TicketInformation ticketInformation = ticketPayment.getTicketInformation();
		Guest guest = ticketPayment.getGuest();

		params.put("sale_reco_idx", String.valueOf(ticketInformation.index));
		params.put("billkey", mSelectedCreditCard.billingkey);
		params.put("ticket_count", String.valueOf(ticketPayment.count));
		params.put("customer_name", guest.name);
		params.put("customer_phone", guest.phone);
		params.put("customer_email", guest.email);

		if (DEBUG == true)
		{
			showSimpleDialog(null, params.toString(), getString(R.string.dialog_btn_text_confirm), null);
		}

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_PAYMENT_SESSION_EASY).toString(), params, mPayEasyPaymentJsonResponseListener, this));
	}

	@Override
	protected void requestTicketPaymentInfomation(int index)
	{
		if (index < 0)
		{
			onInternalError();
			return;
		}

		String params = String.format("?sale_reco_idx=%d", index);
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_SALE_TICKET_PAYMENT_INFO).append(params).toString(), null, mTicketPaymentInformationJsonResponseListener, this));
	}

	@Override
	protected void updateLayout(TicketPayment ticketPayment, CreditCard creditCard)
	{
		if (mFnBBookingLayout == null || ticketPayment == null)
		{
			return;
		}

		mFnBBookingLayout.setTicketPayment(ticketPayment, creditCard);
	}

	@Override
	protected void updatePaymentInformation(TicketPayment ticketPayment, CreditCard creditCard)
	{
		if (mFnBBookingLayout == null || ticketPayment == null)
		{
			return;
		}

		mFnBBookingLayout.updatePaymentInformationLayout(this, ticketPayment, creditCard);
	}

	@Override
	protected void checkPaymentType(Type type)
	{
		if (mTicketPayment != null)
		{
			mTicketPayment.type = type;
		}

		if (mFnBBookingLayout != null)
		{
			mFnBBookingLayout.checkPaymentType(type);
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// User ActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{
		@Override
		public void plusTicketCount()
		{
			int count = mTicketPayment.count;
			int maxCount = mTicketPayment.maxCount;

			if (count >= maxCount)
			{
				// 더 이상 구매 불가하다. 
			} else
			{
				mTicketPayment.count = count + 1;
				mFnBBookingLayout.setTicketCount(mTicketPayment.count);

				// 결제 가격을 바꾸어야 한다.
				mFnBBookingLayout.updatePaymentInformationLayout(FnBBookingActivity.this, mTicketPayment, mSelectedCreditCard);
			}
		}

		@Override
		public void minusTicketCount()
		{
			int count = mTicketPayment.count;

			if (count <= 1)
			{
				// 최소 1장은 구매해야됨.
			} else
			{
				mTicketPayment.count = count - 1;
				mFnBBookingLayout.setTicketCount(mTicketPayment.count);

				// 결제 가격을 바꾸어야 한다.
				mFnBBookingLayout.updatePaymentInformationLayout(FnBBookingActivity.this, mTicketPayment, mSelectedCreditCard);
			}
		}

		@Override
		public void editUserInformation()
		{
			if (mIsEditMode == true)
			{
				return;
			}

			mIsEditMode = true;

			if (mFnBBookingLayout != null)
			{
				mFnBBookingLayout.enabledEditUserInformation();
			}
		}

		@Override
		public void showCreditCardManager()
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			if (mIsEditMode == true)
			{
				// 현재 수정 사항을 기억한다.
				Guest editGuest = mFnBBookingLayout.getGuest();
				mTicketPayment.setGuest(editGuest);
			}

			Intent intent = new Intent(FnBBookingActivity.this, CreditCardListActivity.class);
			intent.setAction(Intent.ACTION_PICK);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_CREDITCARD, mSelectedCreditCard);

			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_CREDITCARD_MANAGER);
			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}

		@Override
		public void setPaymentType(Type type)
		{
			checkPaymentType(type);
		}

		@Override
		public void pay()
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			// 수정 모드인 경우 데이터를 다시 받아와야 한다.
			if (mIsEditMode == true)
			{
				Guest guest = mFnBBookingLayout.getGuest();

				if (Util.isTextEmpty(guest.name) == true)
				{
					releaseUiComponent();

					mFnBBookingLayout.requestUserInformationFocus(UserInformationType.NAME);

					DailyToast.showToast(FnBBookingActivity.this, R.string.toast_msg_please_input_guest, Toast.LENGTH_SHORT);
					return;
				} else if (Util.isTextEmpty(guest.phone) == true)
				{
					releaseUiComponent();

					mFnBBookingLayout.requestUserInformationFocus(UserInformationType.PHONE);

					DailyToast.showToast(FnBBookingActivity.this, R.string.toast_msg_please_input_contact, Toast.LENGTH_SHORT);
					return;
				} else if (Util.isTextEmpty(guest.email) == true)
				{
					releaseUiComponent();

					mFnBBookingLayout.requestUserInformationFocus(UserInformationType.EMAIL);

					DailyToast.showToast(FnBBookingActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
					return;
				} else if (android.util.Patterns.EMAIL_ADDRESS.matcher(guest.email).matches() == false)
				{
					releaseUiComponent();

					mFnBBookingLayout.requestUserInformationFocus(UserInformationType.EMAIL);

					DailyToast.showToast(FnBBookingActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
					return;
				}
			}

			String gcmId = sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");

			if (mTicketPayment.type == TicketPayment.Type.VBANK && TextUtils.isEmpty(gcmId) == true)
			{
				// 가상계좌 결제시 푸쉬를 받지 못하는 경우
				String title = getString(R.string.dialog_notice2);
				String positive = getString(R.string.dialog_btn_text_confirm);
				String msg = getString(R.string.dialog_msg_none_gcmid);

				showSimpleDialog(title, msg, positive, new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						processValidatePayment();
					}
				}, new DialogInterface.OnCancelListener()
				{
					@Override
					public void onCancel(DialogInterface dialog)
					{
						unLockUI();
					}
				});
			} else
			{
				processValidatePayment();
			}
		}

		@Override
		public void showCallDialog()
		{
			FnBBookingActivity.this.showCallDialog();
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Network Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelJsonResponseListener mTicketSellCheckJsonResponseListener = new DailyHotelJsonResponseListener()
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

				JSONObject jsonObject = response.getJSONObject("data");

				boolean isOnSale = jsonObject.getBoolean("on_sale");

				int msg_code = response.getInt("msg_code");

				if (isOnSale == true && msg_code == 0)
				{
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFORMATION).toString(), null, mUserInformationJsonResponseListener, FnBBookingActivity.this));
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
			}
		}
	};

	private DailyHotelJsonResponseListener mTicketPaymentInformationJsonResponseListener = new DailyHotelJsonResponseListener()
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

				if (msg_code == 0)
				{
					JSONObject jsonObject = response.getJSONObject("data");

					//					jsonObject.getInt("fnb_sale_reco_idx");
					//					jsonObject.getInt("is_sale_time_over");
					//					jsonObject.getInt("name");
					int discountPrice = jsonObject.getInt("discount");
					long sday = jsonObject.getLong("sday");
					//					jsonObject.getInt("available_ticket_count");
					int maxCount = jsonObject.getInt("max_sale_count");

					switch (mState)
					{
						case STATE_NONE:
						{
							// 가격이 변동 되었다.
							if (mTicketPayment.getTicketInformation().discountPrice != discountPrice)
							{
								mIsChangedPrice = true;
							}

							mTicketPayment.getTicketInformation().discountPrice = discountPrice;
							mTicketPayment.maxCount = maxCount;

							Calendar calendarCheckin = DailyCalendar.getInstance();
							calendarCheckin.setTimeZone(TimeZone.getTimeZone("GMT"));
							calendarCheckin.setTimeInMillis(sday);

							SimpleDateFormat formatDay = new SimpleDateFormat("yyyy.MM.dd (EEE)", Locale.KOREA);
							formatDay.setTimeZone(TimeZone.getTimeZone("GMT"));

							mTicketPayment.checkInTime = formatDay.format(calendarCheckin.getTime());

							mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SESSION_BILLING_CARD_INFO).toString(), null, mUserSessionBillingCardInfoJsonResponseListener, FnBBookingActivity.this));
							break;
						}

						case STATE_PAYMENT:
						{
							TicketInformation ticketInformation = mTicketPayment.getTicketInformation();

							// 가격이 변동 되었다.
							if (ticketInformation.discountPrice != discountPrice)
							{
								mIsChangedPrice = true;
							}

							ticketInformation.discountPrice = discountPrice;

							if (mIsChangedPrice == true)
							{
								mIsChangedPrice = false;

								// 현재 있는 팝업을 없애도록 한다.
								if (mFinalCheckDialog != null && mFinalCheckDialog.isShowing() == true)
								{
									mFinalCheckDialog.cancel();
									mFinalCheckDialog = null;
								}

								showChangedPayDialog();
							} else
							{
								processPayment();
							}
							break;
						}
					}
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
			}
		}
	};

	private DailyHotelJsonResponseListener mPayEasyPaymentJsonResponseListener = new DailyHotelJsonResponseListener()
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

				mState = STATE_NONE;

				hidePorgressDialog();

				if (msg_code == 0)
				{
					// 결제 관련 로그 남기기
					writeLogPaid(mTicketPayment);

					showSimpleDialog(getString(R.string.dialog_title_payment), getString(R.string.act_toast_payment_success), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							setResult(RESULT_OK);
							finish();
						}
					});
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
			}
		}
	};
}
