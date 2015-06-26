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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.facebook.Session;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.fragment.HotelTabBookingFragment;
import com.twoheart.dailyhotel.fragment.TabInfoFragment;
import com.twoheart.dailyhotel.fragment.TabMapFragment;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.Hotel.HotelGrade;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.BaseFragment;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.FragmentViewPager;
import com.twoheart.dailyhotel.widget.TabIndicator;
import com.twoheart.dailyhotel.widget.TabIndicator.OnTabSelectedListener;

public class HotelTabActivity extends BaseActivity implements OnClickListener
{
	private TabIndicator mTabIndicator;
	private FragmentViewPager mFragmentViewPager;
	private ArrayList<BaseFragment> mFragmentList;

	public HotelDetail hotelDetail;
	protected SaleTime mSaleTime;

	private Button btnSoldOut;
	private TextView btnBooking;
	private String mRegion;
	private int mHotelIdx;
	private int mPosition = 0;

	private String region;
	private String hotelName;

	//	private UiLifecycleHelper uiHelper;
	private Handler mHandler = new Handler();

	public interface OnUserActionListener
	{
		public void onClickImage(HotelDetail hotelDetail, String imageUrl);
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//		uiHelper = new UiLifecycleHelper(this, null);
		//		uiHelper.onCreate(savedInstanceState);

		hotelDetail = new HotelDetail();
		Intent intent = getIntent();

		if (intent != null)
		{
			hotelDetail.setHotel((Hotel) intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_HOTEL));
			mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);
			mRegion = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_REGION);
			mHotelIdx = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, -1);
		}

		Hotel hotel = hotelDetail.getHotel();

		if (hotel == null || mSaleTime == null || mRegion == null || mHotelIdx == -1)
		{
			Util.restartApp(this);
			return;
		}

		try
		{
			setContentView(R.layout.activity_hotel_tab);
			setActionBar(hotel.getName());

			ArrayList<String> titleList = new ArrayList<String>();
			titleList.add(getString(R.string.frag_booking_tab_title));
			titleList.add(getString(R.string.frag_tab_info_title));
			titleList.add(getString(R.string.frag_tab_map_title));

			mTabIndicator = (TabIndicator) findViewById(R.id.tabindicator);
			mTabIndicator.setData(titleList, false);
			mTabIndicator.setOnTabSelectListener(mOnTabSelectedListener);

			btnSoldOut = (Button) findViewById(R.id.tv_hotel_tab_soldout);
			btnBooking = (TextView) findViewById(R.id.btn_hotel_tab_booking);

			btnBooking.setOnClickListener(this);

			// 호텔 sold out시
			if (hotel.getAvailableRoom() == 0)
			{
				btnBooking.setVisibility(View.GONE);
				btnSoldOut.setVisibility(View.VISIBLE);
			}

			region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
			hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);
		} catch (Exception e)
		{
			Util.restartApp(this);
		}
	}

	private void loadFragments()
	{
		if (mFragmentViewPager == null)
		{
			ArrayList<String> titleList = new ArrayList<String>();
			titleList.add(getString(R.string.frag_booking_tab_title));
			titleList.add(getString(R.string.frag_tab_info_title));
			titleList.add(getString(R.string.frag_tab_map_title));

			mFragmentViewPager = (FragmentViewPager) findViewById(R.id.fragmentViewPager);
			//			mFragmentViewPager.setOnPageChangeListener(mOnPageChangeListener);

			mFragmentList = new ArrayList<BaseFragment>();

			BaseFragment baseFragment01 = HotelTabBookingFragment.newInstance(hotelDetail, titleList.get(0));
			((HotelTabBookingFragment) baseFragment01).setOnUserActionListener(mOnUserActionListener);

			mFragmentList.add(baseFragment01);

			BaseFragment baseFragment02 = TabInfoFragment.newInstance(hotelDetail, titleList.get(1));
			mFragmentList.add(baseFragment02);

			BaseFragment baseFragment03 = TabMapFragment.newInstance(hotelDetail, titleList.get(2));
			mFragmentList.add(baseFragment03);

			mFragmentViewPager.setData(mFragmentList);
			mFragmentViewPager.setAdapter(getSupportFragmentManager());

			mTabIndicator.setViewPager(mFragmentViewPager.getViewPager());
			mTabIndicator.setOnPageChangeListener(mOnPageChangeListener);

			// pinkred_font
			//			GlobalFont.apply((ViewGroup) findViewById(android.R.id.content).getRootView());
		} else
		{
			if (mFragmentList != null)
			{
				for (BaseFragment baseFragment : mFragmentList)
				{
					if (baseFragment instanceof HotelTabBookingFragment)
					{
						((HotelTabBookingFragment) baseFragment).setHotelDetail(hotelDetail);
						break;
					}
					//					else if(baseFragment instanceof TabInfoFragment)
					//					{
					//						((TabInfoFragment)baseFragment).setHotelDetail(hotelDetail);
					//					} else if(baseFragment instanceof TabMapFragment)
					//					{
					//						
					//					}
				}
			}
		}
	}

	protected void onPostSetCookie()
	{
		lockUI();
		// 호텔 정보를 가져온다.

		String params = String.format("?hotel_idx=%d&sday=%s", hotelDetail.getHotel().getIdx(), mSaleTime.getDayOfDaysHotelDateFormat("yyMMdd"));

		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_HOTEL_DETAIL).append(params).toString(), null, mHotelDetailJsonResponseListener, this));
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == btnBooking.getId())
		{
			if (isLockUiComponent(true) == true)
			{
				return;
			}

			lockUI();

			chgClickable(btnBooking, false); // 7.2 난타 방지

			mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));

			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestBooking", hotelDetail.getHotel().getName(), (long) mHotelIdx);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		releaseUiComponent();

		chgClickable(btnBooking, true); // 7.2 난타 방지
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

		//		uiHelper.onActivityResult(requestCode, resultCode, data, new Callback()
		//		{
		//
		//			@Override
		//			public void onError(PendingCall pendingCall, Exception error, Bundle data)
		//			{
		//				HotelTabActivity.this.onError();
		//			}
		//
		//			@Override
		//			public void onComplete(PendingCall pendingCall, Bundle data)
		//			{
		//
		//			}
		//		});

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		/**
		 * TODO : 카카오링크 아이콘 보이기
		 */
		//		getMenuInflater().inflate(R.menu.activity_hotel_tab_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Session fbSession;
		switch (item.getItemId())
		{
			case R.id.action_share:
				/**
				 * TODO : TEST FOR HOTEL SHARE, KAKAO, FACEBOOK
				 * 
				 */
				KakaoLinkManager.newInstance(HotelTabActivity.this).shareHotelInfo(hotelDetail, mRegion);

				//			android.content.DialogInterface.OnClickListener posListener = new android.content.DialogInterface.OnClickListener() {
				//
				//				@Override
				//				public void onClick(DialogInterface dialog, int which) {
				//					KakaoLinkManager.newInstance(HotelTabActivity.this).shareHotelInfo(hotelDetail, mRegion);
				//				}
				//
				//			};
				//			SimpleAlertDialog.build(this, "(TEST)호텔 정보를 공유합니다.", "공유", posListener).show();

				/**
				 * TODO : FACEBOOK SHARED DIALOG ISSUES = 페이스북 공유를 하는 경우에 내용을 모두
				 * 보여줄수 없다는 면에서 별로임 FEED를 사용 할 경우에도 내용을 모두 보여 줄 수 없음.
				 */

				//			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
				//			.setName("데일리호텔")
				//			.setCaption(hotelDetail.getHotel().getName())
				//			.setDescription("?______________?")
				//			.setPicture(hotelDetail.getHotel().getImage())
				//			.setLink("https://play.google.com/store/apps/details?id=com.twoheart.dailyhotel&hl=ko")
				//			.build();
				//			uiHelper.trackPendingDialogCall(shareDialog.present());

				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume()
	{
		mTabIndicator.setTabEnable(true);
		onPostSetCookie();

		switch (mPosition)
		{
			case 0:
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_booking", "/todays-hotels/" + region + "/" + hotelName + "/booking");
				break;

			case 1:
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_info", "/todays-hotels/" + region + "/" + hotelName + "/info");
				break;

			case 2:
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_map", "/todays-hotels/" + region + "/" + hotelName + "/map");
				break;
		}

		//		uiHelper.onResume();

		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		//		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		//		uiHelper.onPause();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//		uiHelper.onDestroy();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// UserActionListener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////	

	private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
	{
		@Override
		public void onClickImage(HotelDetail hotelDetail, String imageUrl)
		{
			if (isLockUiComponent() == true)
			{
				return;
			}

			mTabIndicator.setTabEnable(false);

			Intent intent = new Intent(HotelTabActivity.this, ImageDetailActivity.class);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, hotelDetail);
			intent.putExtra(NAME_INTENT_EXTRA_DATA_SELECTED_IMAGE_URL, imageUrl);
			startActivity(intent);
		}
	};

	private OnTabSelectedListener mOnTabSelectedListener = new OnTabSelectedListener()
	{
		@Override
		public void onTabSelected(int position)
		{
			lockUiComponent();

			if (mFragmentViewPager == null)
			{
				releaseUiComponent();
				return;
			}

			if (mFragmentViewPager.getCurrentItem() != position)
			{
				mFragmentViewPager.setCurrentItem(position);
			}
		}
	};

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener()
	{
		@Override
		public void onPageSelected(int position)
		{
			mTabIndicator.setCurrentItem(position);

			String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
			String hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);
			mPosition = position;

			switch (mPosition)
			{
				case 0:
					RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_booking", "/todays-hotels/" + region + "/" + hotelName + "/booking");
					break;

				case 1:
					RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_info", "/todays-hotels/" + region + "/" + hotelName + "/info");
					break;

				case 2:
					RenewalGaManager.getInstance(getApplicationContext()).recordScreen("hotelDetail_map", "/todays-hotels/" + region + "/" + hotelName + "/map");
					break;
			}

			mHandler.postDelayed(new Runnable()
			{
				public void run()
				{
					releaseUiComponent();
				}
			}, 500);
		}

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
			// TODO Auto-generated method stub

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

						DailyToast.showToast(HotelTabActivity.this, msg, Toast.LENGTH_SHORT);
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

				if (hotelDetail.getHotel() == null)
				{
					hotelDetail.setHotel(new Hotel());
				}

				Hotel hotelBasic = hotelDetail.getHotel();

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

				hotelDetail.setHotel(hotelBasic);

				JSONArray imgArr = jsonData.getJSONArray("img");
				List<String> imageList = new ArrayList<String>(imgArr.length());

				for (int i = 1; i < imgArr.length(); i++)
				{
					imageList.add(imgArr.getString(i));
				}

				hotelDetail.setImageUrl(imageList);

				JSONArray specArr = jsonData.getJSONArray("spec");
				hotelDetail.setSpecification(specArr);

				double latitude = jsonData.getDouble("lat");
				double longitude = jsonData.getDouble("lng");

				hotelDetail.setLatitude(latitude);
				hotelDetail.setLongitude(longitude);

				int saleIdx = jsonData.getInt("idx");
				hotelDetail.setSaleIdx(saleIdx);

				loadFragments();
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

					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, HotelTabActivity.this));
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

				Intent i = new Intent(HotelTabActivity.this, BookingActivity.class);
				i.putExtra(NAME_INTENT_EXTRA_DATA_HOTELDETAIL, hotelDetail);
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

					mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, HotelTabActivity.this));
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
