/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * BookingListFragment (예약 확인 화면)
 * 
 * 예약된 목록들을 보여주는 화면이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.BookingTabActivity;
import com.twoheart.dailyhotel.activity.FnBReservationDetailActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.PaymentWaitActivity;
import com.twoheart.dailyhotel.adapter.ReservationListAdapter;
import com.twoheart.dailyhotel.model.Booking;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.PinnedSectionListView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 예약한 호텔의 리스트들을 출력.
 * 
 * @author jangjunho
 *
 */
public class ReservationListFragment extends
		BaseFragment implements Constants, OnItemClickListener, OnClickListener
{
	private ReservationListAdapter mAdapter;
	private RelativeLayout mEmptyLayout;
	private PinnedSectionListView mListView;
	private View btnLogin;
	private long mCurrentTime;

	public interface OnUserActionListener
	{
		public void delete(Booking booking);
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_booking_list, container, false);
		view.setPadding(0, Util.dpToPx(container.getContext(), 56), 0, 0);

		mListView = (PinnedSectionListView) view.findViewById(R.id.listview_booking);
		mListView.setShadowVisible(false);

		mEmptyLayout = (RelativeLayout) view.findViewById(R.id.layout_booking_empty);
		btnLogin = view.findViewById(R.id.btn_booking_empty_login);

		btnLogin.setOnClickListener(this);

		return view;
	}

	@Override
	public void onStart()
	{
		AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.BOOLKING_LIST);

		super.onStart();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		baseActivity.setActionBar(getString(R.string.actionbar_title_booking_list_frag), false);

		lockUI();
		mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, baseActivity));
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnLogin.getId())
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			Intent i = new Intent(baseActivity, LoginActivity.class);
			startActivity(i);
			baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

			AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOLKING_LIST, Action.CLICK, Label.LOGIN, 0L);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parentView, View childView, int position, long id)
	{
		BaseActivity baseActivity = (BaseActivity) getActivity();

		if (baseActivity == null)
		{
			return;
		}

		if (isLockUiComponent() == true)
		{
			return;
		}

		lockUiComponent();

		Booking item = mAdapter.getItem(position);

		if (item.type == Booking.TYPE_SECTION)
		{
			releaseUiComponent();
			return;
		}

		Intent intent = null;

		if (item.payType == CODE_PAY_TYPE_CARD_COMPLETE || item.payType == CODE_PAY_TYPE_ACCOUNT_COMPLETE)
		{
			// 카드결제 완료 || 가상계좌 완료

			switch (item.placeType)
			{
				case HOTEL:
					intent = new Intent(baseActivity, BookingTabActivity.class);
					break;

				case FNB:
					intent = new Intent(baseActivity, FnBReservationDetailActivity.class);
					break;
			}

		} else if (item.payType == CODE_PAY_TYPE_ACCOUNT_WAIT)
		{
			// 가상계좌 입금대기
			intent = new Intent(baseActivity, PaymentWaitActivity.class);
		}

		if (intent != null)
		{
			intent.putExtra(NAME_INTENT_EXTRA_DATA_BOOKING, item);
			startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING_DETAIL);
		} else
		{
			releaseUiComponent();
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put(Label.TYPE, String.valueOf(item.payType));
		params.put(Label.ISUSED, String.valueOf(item.isUsed));

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		params.put(Label.CHECK_IN, simpleDateFormat.format(item.checkinTime));
		params.put(Label.CHECK_OUT, simpleDateFormat.format(item.checkoutTime));
		params.put(Label.RESERVATION_INDEX, String.valueOf(item.reservationIndex));

		AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.BOOLKING_LIST, Action.CLICK, item.placeName, params);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		if (requestCode == CODE_REQUEST_ACTIVITY_BOOKING_DETAIL)
		{
			switch (resultCode)
			{
				case CODE_RESULT_ACTIVITY_EXPIRED_PAYMENT_WAIT:
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null || baseActivity.isFinishing() == true)
					{
						return;
					}

					baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), data.getStringExtra("msg"), getString(R.string.dialog_btn_text_confirm), null);
					break;
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{
		@Override
		public void delete(final Booking booking)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			lockUI();

			// 세션 여부를 판단한다.
			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, new DailyHotelStringResponseListener()
			{
				@Override
				public void onResponse(String url, String response)
				{
					BaseActivity baseActivity = (BaseActivity) getActivity();

					if (baseActivity == null)
					{
						return;
					}

					unLockUI();

					String result = null;

					if (false == TextUtils.isEmpty(response))
					{
						result = response.trim();
					}

					if (true == "alive".equalsIgnoreCase(result))
					{
						if (baseActivity.isFinishing() == true)
						{
							return;
						}

						View.OnClickListener posListener = new View.OnClickListener()
						{
							@Override
							public void onClick(View view)
							{
								BaseActivity baseActivity = (BaseActivity) getActivity();

								if (baseActivity == null)
								{
									return;
								}

								lockUI();

								HashMap<String, String> params = new HashMap<String, String>();
								params.put("idx", String.valueOf(booking.reservationIndex));

								mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_MINE_HIDDEN).toString(), params, mReservationHiddenJsonResponseListener, baseActivity));
							}
						};

						baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_delete_booking), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null);
					} else
					{
						baseActivity.restartApp();
					}
				}
			}, baseActivity));
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				String result = null;

				if (response != null)
				{
					result = response.getString("login");
				}

				if ("true".equalsIgnoreCase(result) == false)
				{
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();
				} else
				{
					VolleyHttpClient.createCookie();
				}
			} catch (JSONException e)
			{
				onError(e);
			} finally
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				unLockUI();
			}
		}

	};

	private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			String result = null;

			if (TextUtils.isEmpty(response) == false)
			{
				result = response.trim();
			}

			if ("alive".equalsIgnoreCase(result) == true)
			{ // session alive
				Map<String, String> params = new HashMap<String, String>();
				params.put("timeZone", "Asia/Seoul");

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, baseActivity));

			} else if ("dead".equalsIgnoreCase(result) == true)
			{ // session dead
				// 재로그인
				if (true == baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
				{

					String id = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ID, null);
					String accessToken = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					String pw = baseActivity.sharedPreference.getString(KEY_PREFERENCE_USER_PWD, null);

					Map<String, String> loginParams = new HashMap<String, String>();

					if (null != accessToken)
					{
						loginParams.put("accessToken", accessToken);
					} else
					{
						loginParams.put("email", id);
					}

					loginParams.put("pw", pw);

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, baseActivity));

					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
				} else
				{
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);

					unLockUI();
				}

			} else
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);

				onError();
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				mCurrentTime = response.getLong("currentDateTime");

				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mReservationListJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null)
			{
				return;
			}

			int msg_code = -1;

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");
			} catch (Exception e)
			{
				onError(e);
				unLockUI();
				return;
			}

			try
			{
				JSONArray jsonArray = response.getJSONArray("data");
				int length = jsonArray.length();

				if (length == 0)
				{
					if (mAdapter != null)
					{
						mAdapter.clear();
					}

					//예약한 호텔이 없는 경우 
					mListView.setVisibility(View.GONE);
					mEmptyLayout.setVisibility(View.VISIBLE);
					btnLogin.setVisibility(View.INVISIBLE);
				} else
				{
					// 입금대기, 결제완료, 이용완료
					ArrayList<Booking> waitBookingList = new ArrayList<Booking>();
					ArrayList<Booking> paymentBookingList = new ArrayList<Booking>();
					ArrayList<Booking> usedBookingList = new ArrayList<Booking>();

					for (int i = 0; i < length; i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						Booking booking = new Booking(jsonObject);

						switch (booking.payType)
						{
							case CODE_PAY_TYPE_CARD_COMPLETE:
							case CODE_PAY_TYPE_ACCOUNT_COMPLETE:
								boolean isUsed = false;

								switch (booking.placeType)
								{
									case HOTEL:
										isUsed = booking.checkoutTime < mCurrentTime;
										break;

									case FNB:
									{
										Date checkinDate = new Date(booking.checkinTime);
										SimpleDateFormat sFormat = new SimpleDateFormat("yyyyMMdd");
										sFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
										int checkin = Integer.valueOf(sFormat.format(checkinDate));
										int today = Integer.valueOf(sFormat.format(mCurrentTime));

										isUsed = checkin < today ? true : false;
										break;
									}
								}

								booking.isUsed = isUsed;

								if (isUsed)
								{
									usedBookingList.add(booking);
								} else
								{
									paymentBookingList.add(booking);
								}
								break;

							case CODE_PAY_TYPE_ACCOUNT_WAIT:
								waitBookingList.add(booking);
								break;
						}
					}

					ArrayList<Booking> bookingArrayList = new ArrayList<Booking>(length + 3);

					// 입금 대기가 있는 경우.
					if (waitBookingList.size() > 0)
					{
						Booking sectionWait = new Booking(getString(R.string.frag_booking_wait_account));
						bookingArrayList.add(sectionWait);
						bookingArrayList.addAll(waitBookingList);
					}

					// 결제 완료가 있는 경우.
					if (paymentBookingList.size() > 0)
					{
						Booking sectionPay = new Booking(getString(R.string.frag_booking_complete_payment));
						bookingArrayList.add(sectionPay);
						bookingArrayList.addAll(paymentBookingList);
					}

					// 이용 완료가 있는 경우.
					if (usedBookingList.size() > 0)
					{
						Booking sectionUsed = new Booking(getString(R.string.frag_booking_use));
						bookingArrayList.add(sectionUsed);
						bookingArrayList.addAll(usedBookingList);
					}

					if (mAdapter == null)
					{
						mAdapter = new ReservationListAdapter(baseActivity, R.layout.list_row_booking, new ArrayList<Booking>());
						mAdapter.setOnUserActionListener(mOnUserActionListener);
						mListView.setOnItemClickListener(ReservationListFragment.this);
						mListView.setAdapter(mAdapter);
					}

					mAdapter.clear();
					mAdapter.addAll(bookingArrayList);
					mAdapter.notifyDataSetChanged();

					mListView.setVisibility(View.VISIBLE);
					mEmptyLayout.setVisibility(View.GONE);

					// flag가 가상계좌 입금 대기에서 날아온경우 
					SharedPreferences pref = getActivity().getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
					int flag = pref.getInt(KEY_PREFERENCE_ACCOUNT_READY_FLAG, -1);
					if (flag == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
					{
						mListView.performItemClick(null, 0, 0);
						Editor editor = pref.edit();
						editor.remove(KEY_PREFERENCE_ACCOUNT_READY_FLAG);
						editor.apply();
					}
				}
			} catch (Exception e)
			{
				mListView.setVisibility(View.GONE);
				mEmptyLayout.setVisibility(View.VISIBLE);
				btnLogin.setVisibility(View.INVISIBLE);

				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mReservationHiddenJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			BaseActivity baseActivity = (BaseActivity) getActivity();

			if (baseActivity == null || baseActivity.isFinishing() == true)
			{
				return;
			}

			int msg_code = -1;

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				// 해당 화면은 메시지를 넣지 않는다.
				msg_code = response.getInt("msg_code");

				JSONObject jsonObject = response.getJSONObject("data");
				String message = null;
				boolean result = false;

				if (jsonObject != null)
				{
					result = jsonObject.getInt("isSuccess") == 1;
				}

				// 성공 실패 여부는 팝업에서 리스너를 다르게 등록한다. 
				View.OnClickListener onClickListener;

				if (result == true)
				{
					onClickListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							BaseActivity baseActivity = (BaseActivity) getActivity();

							if (baseActivity == null)
							{
								return;
							}

							lockUI();

							mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
						}
					};
				} else
				{
					onClickListener = new View.OnClickListener()
					{
						@Override
						public void onClick(View view)
						{
							BaseActivity baseActivity = (BaseActivity) getActivity();

							if (baseActivity == null)
							{
								return;
							}

							lockUI();

							mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
						}
					};
				}

				switch (msg_code)
				{
					case 0:
						mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
						break;

					// Toast
					case 100:
					{
						message = response.getString("msg");

						if (TextUtils.isEmpty(message) == false)
						{
							DailyToast.showToast(baseActivity, message, Toast.LENGTH_SHORT);
						}

						mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
						break;
					}

						// Popup
					case 200:
					{
						message = response.getString("msg");

						if (TextUtils.isEmpty(message) == false)
						{
							unLockUI();

							if (baseActivity.isFinishing() == true)
							{
								return;
							}

							baseActivity.showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), onClickListener);
						} else
						{
							mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
						}
						break;
					}
				}
			} catch (Exception e)
			{
				onError(e);

				// credit card 요청
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_BOOKING_LIST).toString(), null, mReservationListJsonResponseListener, baseActivity));
			}
		}
	};
}
