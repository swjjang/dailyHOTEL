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
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Hotel.HotelGrade;
import com.twoheart.dailyhotel.model.HotelDetail;
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

public class HotelDetailActivity extends BaseActivity implements OnClickListener
{
	private static final int DURATION_HOTEL_IMAGE_SHOW = 4000;
	
	private HotelDetail mHotelDetail;
	private SaleTime mSaleTime;

	private Button mSoldOutButton;
	private TextView mBookingButton;
	private String mRegion;
	private int mHotelIdx;
	private int mCurrentImage;

	private HotelDetailLayout mHotelDetailLayout;

	private Handler mImageHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (isFinishing() == true)
			{
				return;
			}

			mCurrentImage = mHotelDetailLayout.getCurrentImage();
			mCurrentImage++;
			
			mHotelDetailLayout.setCurrentImage(mCurrentImage);
			sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
		}
	};

	public interface OnUserActionListener
	{
		public void showActionBar();

		public void hideActionBar();

		public void onClickImage(HotelDetail hotelDetail);
		
		public void startAutoSlide();
		
		public void stopAutoSlide();
		
		public void onSelectedImagePosition(int position);
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mHotelDetail = new HotelDetail();
		Intent intent = getIntent();

		if (intent != null)
		{
			mHotelDetail.setHotel((Hotel) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_HOTEL));
			mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
			mRegion = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_REGION);
			mHotelIdx = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
		}

		Hotel hotel = mHotelDetail.getHotel();

		if (hotel == null || mSaleTime == null || mRegion == null || mHotelIdx == -1)
		{
			Util.restartApp(this);
			return;
		}

		initLayout(hotel);
	}

	private void initLayout(Hotel hotel)
	{
		try
		{
			mHotelDetailLayout = new HotelDetailLayout(this);
			mHotelDetailLayout.setUserActionListener(mOnUserActionListener);

			setContentView(mHotelDetailLayout.getView());

			setActionBar(hotel.getName());

			mOnUserActionListener.hideActionBar();

			//			btnSoldOut = (Button) findViewById(R.id.tv_hotel_tab_soldout);
			//			btnBooking = (TextView) findViewById(R.id.btn_hotel_tab_booking);
			//
			//			btnBooking.setOnClickListener(this);
			//
			//			// 호텔 sold out시
			//			if (hotel.getAvailableRoom() == 0)
			//			{
			//				btnBooking.setVisibility(View.GONE);
			//				btnSoldOut.setVisibility(View.VISIBLE);
			//			}

		} catch (Exception e)
		{
			Util.restartApp(this);
		}
	}

	@Override
	protected void onResume()
	{
		lockUI();

		// 호텔 정보를 가져온다.
		String params = String.format("?hotel_idx=%d&sday=%s", mHotelDetail.getHotel().getIdx(), mSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append(params).toString(), null, mHotelDetailJsonResponseListener, this));
		
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
	public void onClick(View v)
	{
		if (v.getId() == mBookingButton.getId())
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			lockUI();

			chgClickable(mBookingButton, false); // 7.2 난타 방지

			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestBooking", mHotelDetail.getHotel().getName(), (long) mHotelIdx);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		chgClickable(mBookingButton, true); // 7.2 난타 방지
		if (requestCode == CODE_REQUEST_ACTIVITY_BOOKING)
		{
			setResult(resultCode);

			if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_SALES_CLOSED || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_NOT_ONSALE)
			{
				finish();
			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN)
		{
			if (resultCode == RESULT_OK)
				mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// 예약화면으로 넘어가기 전에 로그인이 필요함.
	// 로그인 화면을 띄움.
	private void loadLoginProcess()
	{
		DailyToast.showToast(this, R.string.toast_msg_please_login, Toast.LENGTH_LONG);
		Intent i = new Intent(this, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // 7.2 S2에서 예약버튼 난타할 경우 여러개의 엑티비티가 생성되는것을 막음
		startActivityForResult(i, CODE_REQUEST_ACTIVITY_LOGIN);

		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
		public void onClickImage(HotelDetail hotelDetail)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			lockUiComponent();

			Intent intent = new Intent(HotelDetailActivity.this, ImageDetailListActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, hotelDetail);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_POSOTION, mCurrentImage);
			startActivity(intent);
		}

		@Override
		public void startAutoSlide()
		{
			mImageHandler.removeMessages(0);
			mImageHandler.sendEmptyMessageDelayed(0, DURATION_HOTEL_IMAGE_SHOW);
		}

		@Override
		public void stopAutoSlide()
		{
			mImageHandler.removeMessages(0);
		}

		@Override
		public void onSelectedImagePosition(int position)
		{
			mCurrentImage = position;
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

				JSONObject jsonData = response.getJSONObject("data");

				int discount = Integer.parseInt(jsonData.getString("discount"));
				int price = Integer.parseInt(jsonData.getString("price"));

				if (mHotelDetail.getHotel() == null)
				{
					mHotelDetail.setHotel(new Hotel());
				}

				Hotel hotelBasic = mHotelDetail.getHotel();

				hotelBasic.setAddress(jsonData.getString("address"));
				hotelBasic.setName(jsonData.getString("hotel_name"));
				hotelBasic.setDiscount(discount);
				hotelBasic.setPrice(price);

				if (jsonData.has("sday") == true)
				{
					hotelBasic.mSaleDay = jsonData.getString("sday");
				}

				try
				{
					hotelBasic.setCategory(jsonData.getString("cat"));
				} catch (Exception e)
				{
					hotelBasic.setCategory(HotelGrade.etc.name());
				}

				hotelBasic.setBedType(jsonData.getString("bed_type"));

				mHotelDetail.setHotel(hotelBasic);

				JSONArray imgArr = jsonData.getJSONArray("img");
				List<String> imageList = new ArrayList<String>(imgArr.length());

				for (int i = 0; i < imgArr.length(); i++)
				{
					imageList.add(imgArr.getString(i));
				}

				mHotelDetail.setImageUrl(imageList);

				JSONArray specArr = jsonData.getJSONArray("spec");
				mHotelDetail.setSpecification(specArr);

				double latitude = jsonData.getDouble("lat");
				double longitude = jsonData.getDouble("lng");

				mHotelDetail.setLatitude(latitude);
				mHotelDetail.setLongitude(longitude);

				int saleIdx = jsonData.getInt("idx");
				mHotelDetail.setSaleIdx(saleIdx);
				
				mHotelDetail.isOverseas = jsonData.getInt("is_overseas");

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
			{ // session alive

				Intent i = new Intent(HotelDetailActivity.this, BookingActivity.class);
				i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, mHotelDetail);
				i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, mHotelIdx);
				i.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, mSaleTime);

				startActivityForResult(i, CODE_REQUEST_ACTIVITY_BOOKING);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

			} else if ("dead".equalsIgnoreCase(result) == true)
			{ // session dead

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
}
