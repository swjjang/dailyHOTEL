/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.model.TicketDetailDto;
import com.twoheart.dailyhotel.model.TicketInformation;
import com.twoheart.dailyhotel.ui.FoodnBeverageDetailLayout;
import com.twoheart.dailyhotel.ui.HotelDetailLayout;
import com.twoheart.dailyhotel.ui.PlaceDetailLayout;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.widget.DailyToast;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public abstract class PlaceDetailActivity extends BaseActivity
{
	private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;

	private PlaceDetailLayout mPlaceDetailLayout;
	private TicketDetailDto mTicketDetailDto;
	private SaleTime mCheckInSaleTime;

	private int mCurrentImage;
	private TicketInformation mSelectedTicketInformation;
	private boolean mIsStartByShare;
	private String mDefaultImageUrl;

	private Handler mImageHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (isFinishing() == true || mPlaceDetailLayout == null)
			{
				return;
			}

			int direction = msg.arg1;

			mCurrentImage = mPlaceDetailLayout.getCurrentImage();

			if (direction > 0)
			{
				mCurrentImage++;
			} else if (direction < 0)
			{
				mCurrentImage--;
			}

			mPlaceDetailLayout.setCurrentImage(mCurrentImage);

			int autoSlide = msg.arg2;

			if (autoSlide == 1)
			{
				sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
			}
		}
	};

	public interface OnUserActionListener
	{
		public void showActionBar();

		public void hideActionBar();

		public void onClickImage(TicketDetailDto ticketDetailDto);

		public void onSelectedImagePosition(int position);

		public void doBooking(TicketInformation ticketInformation);

		public void doKakaotalkConsult();

		public void showTicketInformationLayout();

		public void hideTicketInformationLayout();

		public void showMap();
	};

	public interface OnImageActionListener
	{
		public void startAutoSlide();

		public void stopAutoSlide();

		public void nextSlide();

		public void prevSlide();
	}

	protected abstract void requestPlaceDetailInformation();

	protected abstract TicketDetailDto createTicketDetailDto(Intent intent);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		if (intent == null)
		{
			finish();
			return;
		}

		if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_TYPE) == true)
		{
			mIsStartByShare = true;

			long dailyTime = intent.getLongExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, 0);
			int dayOfDays = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, -1);

			mCheckInSaleTime = new SaleTime();
			mCheckInSaleTime.setDailyTime(dailyTime);
			mCheckInSaleTime.setOffsetDailyDay(dayOfDays);

			int ticketIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_TICKETIDX, -1);
			int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);

			mTicketDetailDto = createTicketDetailDto(intent);

			initLayout(null, null);
		} else
		{
			mIsStartByShare = false;

			mCheckInSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

			int ticketIndex = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_TICKETIDX, -1);
			int nights = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, 0);

			mTicketDetailDto = createTicketDetailDto(intent);

			String placeName = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_PLACENAME);
			String imageUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_IMAGEURL);
			mDefaultImageUrl = imageUrl;

			if (mCheckInSaleTime == null || ticketIndex == -1 || placeName == null || nights < 0)
			{
				Util.restartApp(this);
				return;
			}

			initLayout(placeName, imageUrl);
		}
	}

	private void initLayout(String placeName, String imageUrl)
	{
		if (mPlaceDetailLayout == null)
		{
			mPlaceDetailLayout = new FoodnBeverageDetailLayout(this, imageUrl);
			mPlaceDetailLayout.setUserActionListener(mOnUserActionListener);
			mPlaceDetailLayout.setImageActionListener(mOnImageActionListener);

			setContentView(mPlaceDetailLayout.getLayout());
		}

		if (placeName != null)
		{
			setActionBar(placeName);
		}

		mOnUserActionListener.hideActionBar();
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(PlaceDetailActivity.this).recordScreen(Screen.HOTEL_DETAIL);
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		lockUI();

		Map<String, String> params = new HashMap<String, String>();
		params.put("timeZone", "Asia/Seoul");

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, mDateTimeJsonResponseListener, this));

		super.onResume();
	}

	@Override
	protected void onPause()
	{
		mOnImageActionListener.stopAutoSlide();

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		mOnImageActionListener.stopAutoSlide();

		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		if (mPlaceDetailLayout != null)
		{
			switch (mPlaceDetailLayout.getBookingStatus())
			{
				case HotelDetailLayout.STATUS_BOOKING:
				case HotelDetailLayout.STATUS_NONE:
					mOnUserActionListener.hideTicketInformationLayout();
					return;
			}
		}

		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		switch (requestCode)
		{
			case CODE_REQUEST_ACTIVITY_BOOKING:
			{
				setResult(resultCode);

				if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY)
				{
					finish();
				}
				break;
			}

			case CODE_REQUEST_ACTIVITY_LOGIN:
			case CODE_REQUEST_ACTIVITY_USERINFO_UPDATE:
			{
				if (resultCode == RESULT_OK)
				{
					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));
				}
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onError()
	{
		super.onError();

		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.share_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_share:
				if (mDefaultImageUrl == null)
				{
					if (mTicketDetailDto.getImageUrlList() != null && mTicketDetailDto.getImageUrlList().size() > 0)
					{
						mDefaultImageUrl = mTicketDetailDto.getImageUrlList().get(0);
					}
				}

				KakaoLinkManager.newInstance(this).shareTicket(mTicketDetailDto.name, mTicketDetailDto.index, //
				mDefaultImageUrl, //
				mCheckInSaleTime.getDailyTime(), //
				mCheckInSaleTime.getOffsetDailyDay());

				// 호텔 공유하기 로그 추가
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Label.HOTEL_NAME, mTicketDetailDto.name);
				params.put(Label.CHECK_IN, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

				SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
				params.put(Label.CURRENT_TIME, dateFormat2.format(new Date()));

				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, Label.SHARE, params);
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * 예약화면으로 넘어가기 전에 로그인이 필요함. 로그인 화면을 띄움.
	 */
	private void loadLoginProcess()
	{
		DailyToast.showToast(this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);
		Intent i = new Intent(this, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 7.2 S2에서 예약버튼 난타할 경우 여러개의 엑티비티가 생성되는것을 막음
		startActivityForResult(i, CODE_REQUEST_ACTIVITY_LOGIN);

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
	}

	private void moveToBooking(TicketInformation ticketInformation)
	{
		if (ticketInformation == null)
		{
			return;
		}

		Intent intent = new Intent(PlaceDetailActivity.this, BookingActivity.class);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETINFORMATION, ticketInformation);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_TICKETIDX, mTicketDetailDto.index);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mCheckInSaleTime);

		startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
	}

	private void moveToUserInfoUpdate(Customer user)
	{
		Intent i = new Intent(PlaceDetailActivity.this, SignupActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, user);

		startActivityForResult(i, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
	}

	private boolean isEmptyTextField(String... fieldText)
	{

		for (int i = 0; i < fieldText.length; i++)
		{
			if (Util.isTextEmpty(fieldText[i]) == true)
			{
				return true;
			}
		}

		return false;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{
		@Override
		public void showActionBar()
		{
			setActionBarBackgroundVisible(true);
		}

		@Override
		public void hideActionBar()
		{
			setActionBarBackgroundVisible(false);
		}

		@Override
		public void onClickImage(TicketDetailDto ticketDetailDto)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			if (mOnImageActionListener != null)
			{
				mOnImageActionListener.stopAutoSlide();
			}

			Intent intent = new Intent(PlaceDetailActivity.this, ImageDetailListActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, ticketDetailDto.getImageUrlList());
			intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
			startActivity(intent);
		}

		@Override
		public void onSelectedImagePosition(int position)
		{
			mCurrentImage = position;
		}

		@Override
		public void doBooking(TicketInformation ticketInformation)
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			mSelectedTicketInformation = ticketInformation;

			lockUI();

			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, PlaceDetailActivity.this));

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(Label.FNB_TICKET_NAME, ticketInformation.name);
			params.put(Label.FNB_TICKET_INDEX, String.valueOf(ticketInformation.index));
			params.put(Label.FNB_INDEX, String.valueOf(mTicketDetailDto.index));

			AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Label.BOOKING, Action.CLICK, mTicketDetailDto.name, params);
		}

		@Override
		public void doKakaotalkConsult()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			try
			{
				startActivity(new Intent(Intent.ACTION_SEND, Uri.parse("kakaolink://friend/@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94")));
			} catch (ActivityNotFoundException e)
			{
				try
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
				} catch (ActivityNotFoundException e1)
				{
					Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
					marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
					startActivity(marketLaunch);
				}
			}
		}

		@Override
		public void showTicketInformationLayout()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			if (mPlaceDetailLayout != null)
			{
				mPlaceDetailLayout.showAnimationRoomType();
			}

			releaseUiComponent();
		}

		@Override
		public void hideTicketInformationLayout()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			if (mPlaceDetailLayout != null)
			{
				mPlaceDetailLayout.hideAnimationRoomType();
			}

			releaseUiComponent();
		}

		@Override
		public void showMap()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			Intent intent = new Intent(PlaceDetailActivity.this, ZoomMapActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACENAME, mTicketDetailDto.name);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mTicketDetailDto.latitude);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mTicketDetailDto.longitude);

			startActivity(intent);

			// 호텔 공유하기 로그 추가
			SaleTime checkOutSaleTime = mCheckInSaleTime.getClone(mCheckInSaleTime.getOffsetDailyDay());
			String label = String.format("%s (%s-%s)", mTicketDetailDto.name, mCheckInSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"), checkOutSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

			AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.HOTEL_DETAIL, Action.CLICK, label, (long) mTicketDetailDto.index);
		}
	};

	private OnImageActionListener mOnImageActionListener = new OnImageActionListener()
	{
		@Override
		public void startAutoSlide()
		{
			if (Util.isOverAPI11() == false)
			{
				Message message = mImageHandler.obtainMessage();
				message.what = 0;
				message.arg1 = 1; // 오른쪽으로 이동.
				message.arg2 = 1; // 자동 

				mImageHandler.removeMessages(0);
				mImageHandler.sendMessageDelayed(message, DURATION_HOTEL_IMAGE_SHOW);
			} else
			{
				mImageHandler.removeMessages(0);
				mPlaceDetailLayout.startAnimationImageView();
			}
		}

		@Override
		public void stopAutoSlide()
		{
			if (Util.isOverAPI11() == false)
			{
				mImageHandler.removeMessages(0);
			} else
			{
				mImageHandler.removeMessages(0);
				mPlaceDetailLayout.stopAnimationImageView(false);
			}
		}

		@Override
		public void nextSlide()
		{
			if (Util.isOverAPI11() == true)
			{
				Message message = mImageHandler.obtainMessage();
				message.what = 0;
				message.arg1 = 1; // 오른쪽으로 이동.
				message.arg2 = 0; // 수동

				mImageHandler.removeMessages(0);
				mImageHandler.sendMessage(message);
			}
		}

		@Override
		public void prevSlide()
		{
			if (Util.isOverAPI11() == true)
			{
				Message message = mImageHandler.obtainMessage();
				message.what = 0;
				message.arg1 = -1; // 왼쪽으로 이동.
				message.arg2 = 0; // 수동

				mImageHandler.removeMessages(0);
				mImageHandler.sendMessage(message);
			}
		}
	};

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

				if (response.getString("login").equals("true") == false)
				{
					// 로그인 실패
					// data 초기화
					SharedPreferences.Editor ed = sharedPreference.edit();
					ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
					ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
					ed.putString(KEY_PREFERENCE_USER_ID, null);
					ed.putString(KEY_PREFERENCE_USER_PWD, null);
					ed.commit();

					unLockUI();
					loadLoginProcess();

				} else
				{
					//로그인 성공
					VolleyHttpClient.createCookie();

					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, PlaceDetailActivity.this));
				}
			} catch (JSONException e)
			{
				onError(e);
				unLockUI();
			}
		}
	};

	private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{

			unLockUI();

			String result = null;

			if (TextUtils.isEmpty(response) == false)
			{
				result = response.trim();
			}

			if ("alive".equalsIgnoreCase(result) == true)
			{
				// session alive
				// 사용자 정보 요청.
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, PlaceDetailActivity.this));

			} else if ("dead".equalsIgnoreCase(result) == true)
			{
				// session dead
				// 재로그인
				if (sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
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

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, PlaceDetailActivity.this));
				} else
				{
					loadLoginProcess();
				}

			} else
			{
				onError();
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

				Customer user = new Customer();
				user.setEmail(response.getString("email"));
				user.setName(response.getString("name"));
				user.setPhone(response.getString("phone"));
				user.setAccessToken(response.getString("accessToken"));
				user.setUserIdx(response.getString("idx"));

				// 페이스북 유저
				if (isEmptyTextField(user.getAccessToken()) == false)
				{
					if (isEmptyTextField(new String[] { user.getEmail(), user.getPhone(), user.getName() }) == false)
					{
						moveToBooking(mSelectedTicketInformation);
					} else
					{
						// 정보 업데이트 화면으로 이동.
						moveToUserInfoUpdate(user);
					}
				} else
				{
					moveToBooking(mSelectedTicketInformation);
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
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

				if (mIsStartByShare == true)
				{
					mCheckInSaleTime.setCurrentTime(response.getLong("currentDateTime"));
					mCheckInSaleTime.setOpenTime(response.getLong("openDateTime"));
					mCheckInSaleTime.setCloseTime(response.getLong("closeDateTime"));

					long shareDailyTime = mCheckInSaleTime.getDayOfDaysHotelDate().getTime();
					long todayDailyTime = response.getLong("dailyDateTime");

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
					simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

					int shareDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(shareDailyTime)));
					int todayDailyDay = Integer.parseInt(simpleDateFormat.format(new Date(todayDailyTime)));

					// 지난 날의 호텔인 경우.
					if (shareDailyDay < todayDailyDay)
					{
						DailyToast.showToast(PlaceDetailActivity.this, R.string.toast_msg_dont_past_hotelinfo, Toast.LENGTH_LONG);
						finish();
						return;
					}

					if (mCheckInSaleTime.isSaleTime() == true)
					{
						requestPlaceDetailInformation();
					} else
					{
						finish();
					}
				} else
				{
					SaleTime saleTime = new SaleTime();

					saleTime.setCurrentTime(response.getLong("currentDateTime"));
					saleTime.setOpenTime(response.getLong("openDateTime"));
					saleTime.setCloseTime(response.getLong("closeDateTime"));
					saleTime.setDailyTime(response.getLong("dailyDateTime"));

					if (saleTime.isSaleTime() == true)
					{
						requestPlaceDetailInformation();
					} else
					{
						finish();
					}
				}
			} catch (Exception e)
			{
				onError(e);
				unLockUI();

				finish();
			}
		}
	};
}
