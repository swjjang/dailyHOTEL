/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * HotelTabActivity (호텔 예약, 정보, 지도탭을 보여주는 화면)
 * 
 * 호텔 리스트에서 호텔 선택 시 호텔의 정보들을 보여주는 화면이다.
 * 예약, 정보, 지도 프래그먼트를 담고 있는 액티비티이다.
 * 
 */
package com.twoheart.dailyhotel.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetailEx;
import com.twoheart.dailyhotel.model.SaleRoomInformation;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.ui.HotelDetailLayout;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

public class HotelDetailActivity extends BaseActivity
{
	private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;

	private HotelDetailEx mHotelDetail;
	private SaleTime mSaleTime;

	private String mRegion;
	private int mHotelIdx;
	private int mCurrentImage;

	private HotelDetailLayout mHotelDetailLayout;

	private Handler mImageHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (isFinishing() == true || mHotelDetailLayout == null)
			{
				return;
			}

			int direction = msg.arg1;

			mCurrentImage = mHotelDetailLayout.getCurrentImage();

			if (direction > 0)
			{
				mCurrentImage++;

				//				if (mCurrentImage >= mHotelDetailLayout.getTotalImage())
				//				{
				//					mCurrentImage = 0;
				//				}
			} else if (direction < 0)
			{
				mCurrentImage--;

				//				if (mCurrentImage < 0)
				//				{
				//					mCurrentImage = mHotelDetailLayout.getTotalImage() - 1;
				//				}
			}

			mHotelDetailLayout.setCurrentImage(mCurrentImage);

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

		public void onClickImage(HotelDetailEx hotelDetail);

		public void startAutoSlide();

		public void stopAutoSlide();

		public void nextSlide();

		public void prevSlide();

		public void onSelectedImagePosition(int position);

		public void doBooking();

		public void doKakaotalkConsult();

		public void moreViewInfomation();

		public void showRoomType();

		public void hideRoomType();

		public void showMap();
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Hotel hotel = null;

		if (intent != null)
		{
			hotel = (Hotel) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_HOTEL);
			mHotelDetail = new HotelDetailEx(hotel);

			mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
			mRegion = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_REGION);
			mHotelIdx = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
		}

		if (mHotelDetail == null || mSaleTime == null || mRegion == null || mHotelIdx == -1 || hotel == null)
		{
			Util.restartApp(this);
			return;
		}

		initLayout(hotel.getName(), hotel.getImage());
	}

	private void initLayout(String hotelName, String imageUrl)
	{
		try
		{
			mHotelDetailLayout = new HotelDetailLayout(this, imageUrl);
			mHotelDetailLayout.setUserActionListener(mOnUserActionListener);

			setContentView(mHotelDetailLayout.getView());

			setActionBar(hotelName);
			mOnUserActionListener.hideActionBar();
		} catch (Exception e)
		{
			Util.restartApp(this);
		}
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
		mOnUserActionListener.stopAutoSlide();

		super.onPause();
	}

	@Override
	protected void onDestroy()
	{
		mOnUserActionListener.stopAutoSlide();

		super.onDestroy();
	}

	@Override
	public void onBackPressed()
	{
		if (mHotelDetailLayout != null)
		{
			switch (mHotelDetailLayout.getBookingStatus())
			{
				case HotelDetailLayout.STATUS_BOOKING:
				case HotelDetailLayout.STATUS_NONE:
					mOnUserActionListener.hideRoomType();
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

				if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_NOT_ONSALE)
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

	private void moveToBooking(int selectedRoomType)
	{
		ArrayList<SaleRoomInformation> arrayList = mHotelDetail.getSaleRoomList();

		if (arrayList == null || arrayList.size() <= selectedRoomType || selectedRoomType < 0)
		{
			return;
		}

		SaleRoomInformation saleRoomInformation = mHotelDetail.getSaleRoomList().get(selectedRoomType);

		if (saleRoomInformation == null)
		{
			return;
		}

		Intent intent = new Intent(HotelDetailActivity.this, BookingActivity.class);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_SALEROOMINFORMATION, saleRoomInformation);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, mHotelIdx);
		intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);

		startActivityForResult(intent, CODE_REQUEST_ACTIVITY_BOOKING);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
	}

	private void moveToUserInfoUpdate(Customer user)
	{
		Intent i = new Intent(HotelDetailActivity.this, SignupActivity.class);
		i.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, user);

		startActivityForResult(i, CODE_REQUEST_ACTIVITY_USERINFO_UPDATE);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
		public void onClickImage(HotelDetailEx hotelDetail)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			stopAutoSlide();

			Intent intent = new Intent(HotelDetailActivity.this, ImageDetailListActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_IMAGEURLLIST, hotelDetail.getImageUrlList());
			intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
			startActivity(intent);
		}

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
				mHotelDetailLayout.startAnimationImageView();
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
				mHotelDetailLayout.stopAnimationImageView(false);
			}
		}

		@Override
		public void nextSlide()
		{
			ExLog.d("nextSlide");

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
			ExLog.d("prevSlide");

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

		@Override
		public void onSelectedImagePosition(int position)
		{
			mCurrentImage = position;
		}

		@Override
		public void doBooking()
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			lockUI();

			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, HotelDetailActivity.this));

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestBooking", mHotelDetail.getHotel().getName(), (long) mHotelIdx);
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
		public void moreViewInfomation()
		{
			if (mHotelDetail.getMoreInformation() == null)
			{
				return;
			}

			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			Intent intent = new Intent(HotelDetailActivity.this, HotelDetailInfoActivity.class);
			intent.putParcelableArrayListExtra(NAME_INTENT_EXTRA_DATA_MOREINFORMATION, mHotelDetail.getMoreInformation());
			startActivity(intent);

			overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
		}

		@Override
		public void showRoomType()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			if (mHotelDetailLayout != null)
			{
				mHotelDetailLayout.showAnimationRoomType();
			}

			releaseUiComponent();
		}

		@Override
		public void hideRoomType()
		{
			if (isLockUiComponent() == true || isFinishing() == true)
			{
				return;
			}

			lockUiComponent();

			if (mHotelDetailLayout != null)
			{
				mHotelDetailLayout.hideAnimationRoomType();
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

			Intent intent = new Intent(HotelDetailActivity.this, ZoomMapActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELNAME, mHotelDetail.hotelName);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_LATITUDE, mHotelDetail.latitude);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_LONGITUDE, mHotelDetail.longitude);

			startActivity(intent);
		}
	};

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					if (response.has("msg") == true)
					{
						String msg = response.getString("msg");

						DailyToast.showToast(HotelDetailActivity.this, msg, Toast.LENGTH_SHORT);
						finish();
						return;
					} else
					{
						throw new NullPointerException("response == null");
					}
				}

				JSONObject dataJSONObject = response.getJSONObject("data");

				mHotelDetail.setData(dataJSONObject);

				if (mHotelDetailLayout != null)
				{
					mHotelDetailLayout.setHotelDetail(mHotelDetail, mCurrentImage);
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

					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, HotelDetailActivity.this));
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
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, HotelDetailActivity.this));

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

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, HotelDetailActivity.this));
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
						moveToBooking(mHotelDetailLayout.selectedRoomType());
					} else
					{
						// 정보 업데이트 화면으로 이동.
						moveToUserInfoUpdate(user);
					}
				} else
				{
					moveToBooking(mHotelDetailLayout.selectedRoomType());
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

				SaleTime saleTime = new SaleTime();

				saleTime.setCurrentTime(response.getLong("currentDateTime"));
				saleTime.setOpenTime(response.getLong("openDateTime"));
				saleTime.setCloseTime(response.getLong("closeDateTime"));
				saleTime.setDailyTime(response.getLong("dailyDateTime"));

				if (saleTime.isSaleTime() == true)
				{
					// 호텔 정보를 가져온다.
					String params = String.format("?hotel_idx=%d&sday=%s", mHotelDetail.getHotel().getIdx(), mSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_SALE_HOTEL_INFO).append(params).toString(), null, mHotelDetailJsonResponseListener, HotelDetailActivity.this));
				} else
				{
					finish();
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
