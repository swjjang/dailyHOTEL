/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * MainActivity (메인화면)
 * 
 * 어플리케이션의 주 화면으로서 최초 실행 시 보여지는 화면이다. 이 화면은 어플리케이션
 * 최초 실행 시 SplashActivity를 먼저 띄우며, 대부분의 어플리케이션 초기화 작업을 
 * SplashActivity에게 넘긴다. 그러나, 일부 초기화 작업도 수행하며, 로그인 세션관리와
 * 네비게이션 메뉴를 표시하는 일을 하는 화면이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.androidquery.util.AQUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;
import com.twoheart.dailyhotel.activity.PushLockDialogActivity;
import com.twoheart.dailyhotel.activity.ScreenOnPushDialogActivity;
import com.twoheart.dailyhotel.activity.SplashActivity;
import com.twoheart.dailyhotel.fragment.RatingHotelFragment;
import com.twoheart.dailyhotel.model.Hotel;
import com.twoheart.dailyhotel.model.HotelDetail;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.Constants.Stores;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.util.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.CloseOnBackPressed;

public class MainActivity extends BaseActivity implements DailyHotelStringResponseListener, DailyHotelJsonResponseListener, 
	OnItemClickListener, Constants {

	private static final String TAG = "MainActivity";

	public static final int INDEX_HOTEL_LIST_FRAGMENT = 0;
	public static final int INDEX_BOOKING_LIST_FRAGMENT = 1;
	public static final int INDEX_CREDIT_FRAGMENT = 2;
	public static final int INDEX_SETTING_FRAGMENT = 3;

	public static final String KEY_HOTEL_LIST_FRAGMENT = "hotel_list";
	public static final String KEY_BOOKING_LIST_FRAGMENT = "booking_list";
	public static final String KEY_CREDIT_FRAGMENT = "credit";
	public static final String KEY_SEETING_FRAGMENT = "setting";

	private static final String TAG_FRAGMENT_RATING_HOTEL = "rating_hotel";

	public ListView drawerList;
	public DrawerLayout drawerLayout;
	private FrameLayout mContentFrame;
	public Dialog popUpDialog;

	public ActionBarDrawerToggle drawerToggle;
	protected FragmentManager fragmentManager;
	protected List<DrawerMenu> mMenuImages;
	protected List<Fragment> mFragments;
	private DrawerMenuListAdapter mDrawerMenuListAdapter;

	// 마지막으로 머물렀던 Fragment의 index
	public int indexLastFragment;	// Error Fragment에서 다시 돌아올 때 필요.

	// SystemBarTintManager
	private SystemBarTintManager tintManager;
	public SystemBarConfig config;

	// DrawerMenu 객체들
	public DrawerMenu menuHotelListFragment;
	public DrawerMenu menuBookingListFragment;
	public DrawerMenu menuCreditFragment;
	public DrawerMenu menuSettingFragment;

	// Back 버튼을 두 번 눌러 핸들러 멤버 변수
	private CloseOnBackPressed backButtonHandler;

	protected HashMap<String, String> regPushParams;

	public Uri intentData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("GCM??", "GCM??" + sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "NOPE"));
		
//		Intent intent = new Intent(this, MainActivity.class);
//		NotificationManager mNotificationManager = (NotificationManager)
//				this.getSystemService(Context.NOTIFICATION_SERVICE);
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//				intent, PendingIntent.FLAG_UPDATE_CURRENT);
//		NotificationCompat.Builder mBuilder =
//				new NotificationCompat.Builder(this)
//		.setSmallIcon(R.drawable.img_ic_appicon_feature)
//		.setContentTitle(getString(R.string.app_name))
//		.setAutoCancel(true)
//		.setSound(null)
//		.setContentText("[테스트] 입니다.");
//
//		mBuilder.setContentIntent(contentIntent);
//		mNotificationManager.notify(1, mBuilder.build());
		
//		Intent i = new Intent(this, ScreenOnPushDialogActivity.class);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_ACCOUNT_COMPLETE);
//		i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, "[[테스트] 결제완료되었습니다");
//		startActivity(i);
		
//		Intent i = new Intent(this, PushLockDialogActivity.class);
//		i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_MSG, "[[[[[테스트]]  ] 결제완료되었습니다");
//		i.putExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, PUSH_TYPE_ACCOUNT_COMPLETE);
//		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | 
//				Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		this.startActivity(i);
		
//		
//		AlertDialog.Builder alert = new AlertDialog.Builder(this);
//
//		alert.setTitle("Title");
//		// Set an EditText view to get user input
//		final EditText input = new EditText(this);
//		input.setText(sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "NOPE"));
//		alert.setView(input);
//		alert.show();

		// 사용자가 선택한 언어, but 만약 사용자가 한국인인데 일본어를 선택하면 jp가 됨.
		// 영어버전 
//		String locale = Locale.getDefault().getDisplayLanguage();
//		Log.e("locale",locale);
//		
//		Editor editor = sharedPreference.edit();
//		editor.putString(KEY_PREFERENCE_LOCALE, locale);
//		editor.apply();
		
		// Intent Scheme Parameter for KakaoLink
		intentData = getIntent().getData();
		if (intentData != null) {
			/**
			 * TODO : IF(intentData!=null) url from shared link
			 */
			
			android.util.Log.e(TAG + " / onCreate", "intentData = " + intentData.toString());
		}

		// 쿠키 동기화를 초기화한다. 로그인, 로그아웃 세션 쿠키는 MainActivity의 생명주기와 동기화한다.
		CookieSyncManager.createInstance(getApplicationContext());

		// 이전의 비정상 종료에 의한 만료된 쿠키들이 있을 수 있으므로, SplashActivity에서 자동 로그인을
		// 처리하기 이전에 미리 이미 저장되어 있는 쿠키들을 정리한다.
		if (CookieManager.getInstance().getCookie(URL_DAILYHOTEL_SERVER) != null)
			VolleyHttpClient.destroyCookie();

		// 스플래시 화면을 띄운다

		startActivityForResult(new Intent(this, SplashActivity.class), CODE_REQUEST_ACTIVITY_SPLASH);

		// Anroid 4.4 이상에서 Android StatusBar와 Android NavigationBar를 Translucent하게 해주는 API를 사용하도록 한다.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTheme(R.style.AppTheme_Translucent);

			// SystemBarTintManager는 3rd Party 라이브러리로 StatusBar와 NavigationBar와 관련된 API를 쉽게 변경할 수 있도록 해준다.
			tintManager = new SystemBarTintManager(this);
			config = tintManager.getConfig();

			tintManager.setStatusBarTintEnabled(true);
			int actionBarColor = getResources().getColor(android.R.color.white);
			tintManager.setStatusBarTintColor(actionBarColor);

		} else {
			setTheme(R.style.AppTheme);
		}

		setContentView(R.layout.activity_main);
		setNavigationDrawer();

		mContentFrame = (FrameLayout) findViewById(R.id.content_frame);

		// Android 4.4 이상에서 Android StatusBar와 Android NavigationBar를 Translucent하게 
		// 할 경우 여백 계산이 필요한 케이스가 발생하므로 해당 케이스에 대해 예외 처리한다.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mContentFrame.setPadding(mContentFrame.getPaddingLeft(),
					config.getStatusBarHeight() + config.getActionBarHeight(),
					mContentFrame.getPaddingRight(),
					mContentFrame.getPaddingBottom());

			drawerList
			.setPadding(
					drawerList.getPaddingLeft(),
					config.getStatusBarHeight()
					+ config.getActionBarHeight(),
					drawerList.getPaddingRight(),
					drawerList.getPaddingBottom());

		}

		fragmentManager = getSupportFragmentManager();
		backButtonHandler = new CloseOnBackPressed(this);

		// Facebook SDK를 관리하기 위한 패키지 Hash 값 표시
		if (DEBUG)  printPackageHashKey();
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Editor editor = sharedPreference.edit();
		
		if (requestCode == CODE_REQUEST_ACTIVITY_SPLASH) {
			switch (resultCode) {
			case RESULT_OK :		// 스플래시 화면이 정상적으로 종료되었을 경우
				editor.putBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);
				editor.apply();
				break;
			case CODE_RESULT_ACTIVITY_SPLASH_NEW_EVENT :		// 스플래시가 정상적으로 종료되었는데 새로운 이벤트 알림이 있는 경우
				editor.putBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, true);
				editor.apply();
				break;
			default :		// 스플래시가 비정상적으로 종료되었을 경우
				super.finish();		// 어플리케이션(메인 화면)을 종료해버린다
				return;				// 메서드를 빠져나간다 - 호텔 평가를 수행하지 않음.
			}
			
			boolean showGuide = sharedPreference.getBoolean(KEY_PREFERENCE_SHOW_GUIDE, true);
			if (showGuide) startActivityForResult(new Intent(this, IntroActivity.class), CODE_REQUEST_ACTIVITY_INTRO);
			else {
				// Intent가 Push로 부터 온경우
				int pushType = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
				switch (pushType) {
				case PUSH_TYPE_NOTICE:
					selectMenuDrawer(menuHotelListFragment);
					break;
				case PUSH_TYPE_ACCOUNT_COMPLETE:
					selectMenuDrawer(menuBookingListFragment);
					break;
				default:
					selectMenuDrawer(menuHotelListFragment);
				}
				
				mQueue.add(new DailyHotelStringRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_ALIVE).toString(), null, this, this));
			}

			// 호텔평가를 위한 현재 로그인 여부 체크
			
		} else if (requestCode == CODE_REQUEST_ACTIVITY_INTRO) {
			selectMenuDrawer(menuHotelListFragment);
			
//			mQueue.add(new DailyHotelStringRequest(Method.GET,
//					new StringBuilder(URL_DAILYHOTEL_SERVER).append(
//							URL_WEBAPI_USER_ALIVE).toString(), null, this, this));
		}

	}
	
	@Override
	public void onResponse(String url, String response) {
		if (url.contains(URL_WEBAPI_USER_ALIVE)) {
			String result = response.trim();

			if (result.equals("alive")) { // session alive
				// 호텔 평가를 위한 사용자 정보 조회
				mQueue.add(new DailyHotelJsonRequest(Method.GET,
						new StringBuilder(URL_DAILYHOTEL_SERVER).append(
								URL_WEBAPI_USER_INFO).toString(), null, this,
								this));

			}
		}
	}

	@Override
	public void onResponse(String url, JSONObject response) {
		if (url.contains(URL_WEBAPI_USER_INFO)) {
			try {
				String loginuser_idx = response.getString("idx");

				String gcmId=getGcmId();
				// GCM 등록 시도
				android.util.Log.e("NOTE","NOTE : " + gcmId);
				if (gcmId.isEmpty()) {
					if (isGoogleServiceAvailable()) {
						regGcmId(Integer.parseInt(loginuser_idx));
					}
				}

				String buyerIdx = sharedPreference.getString(KEY_PREFERENCE_USER_IDX, null);
				if (buyerIdx != null) {
					if (loginuser_idx.equals(buyerIdx)) {
						String purchasedHotelName = sharedPreference.getString(
								KEY_PREFERENCE_HOTEL_NAME,
								VALUE_PREFERENCE_HOTEL_NAME_DEFAULT);
						int purchasedHotelSaleIdx = sharedPreference.getInt(
								KEY_PREFERENCE_HOTEL_SALE_IDX,
								VALUE_PREFERENCE_HOTEL_SALE_IDX_DEFAULT);
						String purchasedHotelCheckOut = sharedPreference.getString(
								KEY_PREFERENCE_HOTEL_CHECKOUT,
								VALUE_PREFERENCE_HOTEL_CHECKOUT_DEFAULT);

						Date today = new Date();
						Date checkOut = SaleTime.stringToDate(Util
								.dailyHotelTimeConvert(purchasedHotelCheckOut));

						//호텔 만족도 조사 
						if (!purchasedHotelName.equals(VALUE_PREFERENCE_HOTEL_NAME_DEFAULT)) {
							if (today.compareTo(checkOut) >= 0) {
								Calendar calendar = Calendar.getInstance();
								calendar.setTime(checkOut);
								calendar.add(Calendar.DATE, DAYS_DISPLAY_RATING_HOTEL_DIALOG);
								Date deadLineDay = calendar.getTime();

								if (today.compareTo(deadLineDay) < 0) {
									Hotel purchasedHotel = new Hotel();
									purchasedHotel.setName(purchasedHotelName);

									HotelDetail purchasedHotelInformation = new HotelDetail();
									purchasedHotelInformation.setHotel(purchasedHotel);
									purchasedHotelInformation.setSaleIdx(purchasedHotelSaleIdx);

									RatingHotelFragment dialog = RatingHotelFragment
											.newInstance(purchasedHotelInformation);
									dialog.show(fragmentManager, TAG_FRAGMENT_RATING_HOTEL);
								} else {
									RatingHotelFragment dialog = RatingHotelFragment
											.newInstance(null);
									dialog.destroyRatingHotelFlag();
								}
							}
						}
					}
				}

			} catch (Exception e) {
				onError(e);
			}
			unLockUI();
		} else if (url.contains(URL_GCM_REGISTER)) {
			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
			try {
				if (response.getString("result").equals("true")) {
					Editor editor = sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
					editor.apply();
				}

			} catch (Exception e) {
				onError(e);
			}
		}
	}

	private String getGcmId() {
		return sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");
	}

	private Boolean isGoogleServiceAvailable() {
		int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resCode)) {
				GooglePlayServicesUtil.getErrorDialog(resCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				showToast(getString(R.string.toast_msg_is_not_available_google_service), Toast.LENGTH_LONG, false);
				finish();
			}
			return false;
		} else {
			return true;
		}
	}

	private void regGcmId(final int idx) {
		new AsyncTask<Void, Void, String>() {

			@Override
			protected String doInBackground(Void... params) {
				GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(MainActivity.this);
				String regId = "";
				try {
					regId = instance.register(GCM_PROJECT_NUMBER);
					Log.d("regId", "regId : " + regId);
				} catch (IOException e) {e.printStackTrace();}

				return regId;
			}

			@Override
			protected void onPostExecute(String regId) {
				
				// gcm id가 없을 경우 스킵.
				if (regId == null || regId.isEmpty()) return;
				
				// 이 값을 서버에 등록하기.
				regPushParams = new HashMap<String, String>();

				regPushParams.put("user_idx", idx+"");
				regPushParams.put("notification_id", regId);
				regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

				mQueue.add(new DailyHotelJsonRequest(Method.POST,
						new StringBuilder(URL_DAILYHOTEL_SERVER)
				.append(URL_GCM_REGISTER)
				.toString(), regPushParams, MainActivity.this,
				MainActivity.this));
			}
		}.execute();		
	}

	/**
	 * 네비게이션 드로워에서 메뉴를 선택하는 효과를 내주는 메서드
	 * @param selectedMenu DrawerMenu 객체를 받는다.
	 */
	public void selectMenuDrawer(DrawerMenu selectedMenu) {
		drawerList.performItemClick(
				drawerList.getAdapter().getView(
						mMenuImages.indexOf(selectedMenu), null, null),
						mMenuImages.indexOf(selectedMenu), mDrawerMenuListAdapter
						.getItemId(mMenuImages.indexOf(selectedMenu)));
	}

	/**
	 * 드로어 메뉴 클릭시 새로고침을 하는 기능을 위해서 동적으로 프래그먼트를 생성하므로
	 * 미리 초기화 하는 해당 메서드는 필요가 없음.
	 */
	@Deprecated
	private void initializeFragments() {
		if (mFragments != null) mFragments.clear();
		else mFragments = new LinkedList<Fragment>();

		mFragments.add(new HotelListFragment());
		mFragments.add(new BookingListFragment());
		mFragments.add(new CreditFragment());
		mFragments.add(new SettingFragment());

	}

	/**
	 * 네비게이션 드로워 메뉴에서 선택할 수 있는 Fragment를 반환하는 메서드이다.
	 * @param index Fragment 리스트에 해당하는 index를 받는다.
	 * @return 요청한 index에 해당하는 Fragment를 반환한다.
	 * => 기능 변경, 누를때마다 리프레시
	 */
	public Fragment getFragment(int index) {
		switch (index) {
		case 0: return new HotelListFragment();
		case 1: return new BookingListFragment();
		case 2: return new CreditFragment();
		case 3: return new SettingFragment();
		}
		return null;

	}

	/**
	 * Fragment 컨테이너에서 해당 Fragment로 변경하여 표시한다.
	 * @param fragment Fragment 리스트에 보관된 Fragement들을 받는 것이 좋다.
	 */
	public void replaceFragment(Fragment fragment) {
		try {
//			FragmentTransaction transaction = fragmentManager.beginTransaction();
			clearFragmentBackStack();
			
//			transaction.commitAllowingStateLoss();
			
			fragmentManager.beginTransaction()
			.replace(mContentFrame.getId(), fragment)
			.commitAllowingStateLoss();
			
//			transaction.replace(mContentFrame.getId(), fragment);
//			transaction.commitAllowingStateLoss();
			
			// Android 4.4 이상일 경우 Android StatusBar와 Android NavigationBar를 모두 Translucent하는데
			// 우리 어플리케이션에서는 HotelListFragment에서만 Android NavigationBar를 Translucent하게 하였다.
			// 그래서 다른 Fragment들에서는 네비게이션 드로워가 차지하는 공간에 있어서 차이가 발생하게 되는데 해당 이슈를
			// 해결하기 위한 부분이 이 부분이다.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (fragment instanceof HotelListFragment) {
					mContentFrame.setPadding(mContentFrame.getPaddingLeft(),
							mContentFrame.getPaddingTop(),
							mContentFrame.getPaddingRight(), 0);

					Window w = getWindow();
					w.setFlags(
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
							WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

				} else {
					WindowManager.LayoutParams attrs = getWindow()
							.getAttributes();
					attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
					getWindow().setAttributes(attrs);

				}
			}
		} catch (IllegalStateException e) {
			onError();
			
//			Log.d("MainActivity", "error : " + e.toString());
		}

	}

	/**
	 * Fragment 컨테이너에서 해당 Fragement를 쌓아올린다.
	 * @param fragment Fragment 리스트에 보관된 Fragment들을 받는 것이 좋다.
	 */
	public void addFragment(Fragment fragment) {
		fragmentManager
		.beginTransaction()
		.setCustomAnimations(R.anim.slide_in_right,
				R.anim.slide_out_right, R.anim.slide_in_right,
				R.anim.slide_out_right)
				.add(R.id.content_frame, fragment).addToBackStack(null)
				.commit();

	}

	/**
	 * Fragment 컨테이너의 표시되는 Fragment를 변경할 때 Fragment 컨테이너에 적재된 Fragment들을 정리한다.
	 */
	private void clearFragmentBackStack() {
		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
			fragmentManager.popBackStackImmediate();

		}

	}

	@Deprecated
	public void removeFragment(Fragment fragment) {
		fragmentManager.beginTransaction().remove(fragment)
		.commitAllowingStateLoss();
	}

	/**
	 * 페이스북 SDK를 사용하기 위해선 개발하는 컴퓨터의 해시키를 페이스북 개발 콘솔에 등록 할 필요가 있음. 이에따라서 현재 컴퓨터의 해시키를 출력해주어 등록을 돕게함.
	 */
	public void printPackageHashKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash: getPackageName()" + getPackageName(),
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (Exception e) {
			onError(e);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		int selectedMenuIconId = ((DrawerMenu) (adapterView.getAdapter()
				.getItem(position))).getIcon();

		switch (selectedMenuIconId) {
		case R.drawable.selector_drawermenu_todayshotel:
			indexLastFragment = INDEX_HOTEL_LIST_FRAGMENT;
			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectMenu", getString(R.string.actionbar_title_hotel_list_frag), (long) position);
			break;

		case R.drawable.selector_drawermenu_reservation:
			indexLastFragment = INDEX_BOOKING_LIST_FRAGMENT;
			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectMenu", getString(R.string.actionbar_title_booking_list_frag), (long) position);
			break;

		case R.drawable.selector_drawermenu_saving:
			indexLastFragment = INDEX_CREDIT_FRAGMENT;
			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectMenu", getString(R.string.actionbar_title_credit_frag), (long) position);
			break;

		case R.drawable.selector_drawermenu_setting:
			indexLastFragment = INDEX_SETTING_FRAGMENT;
			RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "selectMenu", getString(R.string.actionbar_title_setting_frag), (long) position);
			break;
		}

		delayedReplace(indexLastFragment);
		drawerLayout.closeDrawer(drawerList);

	}
	/**
	 * 드로어 레이아웃이 닫히는데 애니메이션이 부하가 큼. 프래그먼트 전환까지 추가한다면 닫힐때 버벅거리는 현상이 발생. 따라서 0.3초 지연하여 자연스러운 애니메이션을 보여줌.
	 * @param index 프래그먼트 인덱스.
	 */
	public void delayedReplace(final int index){
		android.util.Log.e("INDEXED",index+"");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				replaceFragment(getFragment(index));
//				replaceErrorFragment(new ErrorFragment());
			}
		}, 300);
	}

	/**
	 * 네비게이션 드로워를 셋팅하는 메서드
	 */
	public void setNavigationDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, 0, 0) {

			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				supportInvalidateOptionsMenu();
				
				RenewalGaManager.getInstance(getApplicationContext()).recordScreen("menu", "/menu");
				RenewalGaManager.getInstance(getApplicationContext()).recordEvent("click", "requestMenuBar", null, null);
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		menuHotelListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_todays_hotel),
				R.drawable.selector_drawermenu_todayshotel,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuBookingListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_chk_reservation),
				R.drawable.selector_drawermenu_reservation,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuCreditFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_credit),
				R.drawable.selector_drawermenu_saving,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuSettingFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_setting),
				R.drawable.selector_drawermenu_setting,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);

		mMenuImages = new ArrayList<DrawerMenu>();
		
		mMenuImages.add(new DrawerMenu(DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO));
		mMenuImages.add(new DrawerMenu(getString(R.string.drawer_menu_pin_title_resrvation),
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuHotelListFragment);
		mMenuImages.add(menuBookingListFragment);
		
		mMenuImages.add(new DrawerMenu(getString(R.string.drawer_menu_pin_title_account),
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuCreditFragment);
		mMenuImages.add(menuSettingFragment);

		mDrawerMenuListAdapter = new DrawerMenuListAdapter(this,
				R.layout.list_row_drawer_entry, mMenuImages);

		drawerList.setAdapter(mDrawerMenuListAdapter);
		drawerList.setOnItemClickListener(this);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (drawerToggle != null) drawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (drawerToggle != null) drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item))  return true;
		else return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			toggleDrawer();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void toggleDrawer() {
		if (!drawerLayout.isDrawerOpen(drawerList)) drawerLayout.openDrawer(drawerList);
		else drawerLayout.closeDrawer(drawerList);
	}

	@Override
	public void finish() {
		if (backButtonHandler.onBackPressed()) super.finish();
	}

	private class DrawerMenu {

		public static final int DRAWER_MENU_LIST_TYPE_LOGO = 0;
		public static final int DRAWER_MENU_LIST_TYPE_SECTION = 1;
		public static final int DRAWER_MENU_LIST_TYPE_ENTRY = 2;

		private String title;
		private int icon;
		private int type;

		public DrawerMenu(int type) {
			super();
			this.type = type;
		}

		public DrawerMenu(String title, int type) {
			super();
			this.title = title;
			this.type = type;
		}

		public DrawerMenu(String title, int icon, int type) {
			super();
			this.title = title;
			this.icon = icon;
			this.type = type;
		}

		public int getType() { return type; }
		public void setType(int type) { this.type = type; }

		public String getTitle() { return title; }
		public void setTitle(String title) { this.title = title; }

		public int getIcon() { return icon; }
		public void setIcon(int icon) { this.icon = icon; }

	}

	private class DrawerMenuListAdapter extends BaseAdapter {

		private List<DrawerMenu> list;
		private LayoutInflater inflater;
		private Context context;
		private int layout;

		public DrawerMenuListAdapter(Context context, int layout,
				List<DrawerMenu> list) {
			this.context = context;
			this.layout = layout;
			this.inflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
		}

		@Override
		public int getCount() { return list.size(); }

		@Override
		public Object getItem(int position) { return list.get(position); }

		@Override
		public long getItemId(int position) { return position;}

		@Override
		public boolean isEnabled(int position) {
			return (list.get(position).getType() == DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY) ? true : false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			DrawerMenu item = list.get(position);

			switch (item.getType()) {
			case DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO:
				convertView = inflater.inflate(R.layout.list_row_drawer_logo, null);
				break;

			case DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION:
				convertView = inflater.inflate(R.layout.list_row_drawer_section, null);

				TextView drawerMenuItemTitle = (TextView) convertView.findViewById(R.id.drawerMenuItemTitle);

				drawerMenuItemTitle.setText(item.getTitle());
				break;

			case DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY:
				convertView = inflater.inflate(R.layout.list_row_drawer_entry, null);

				ImageView drawerMenuItemIcon = (ImageView) convertView
						.findViewById(R.id.drawerMenuItemIcon);
				TextView drawerMenuItemText = (TextView) convertView
						.findViewById(R.id.drawerMenuItemTitle);

				drawerMenuItemIcon.setImageResource(item.getIcon());
				drawerMenuItemText.setText(item.getTitle());

				break;
			}

			return convertView;
		}
	}

	@Override
	protected void onDestroy() {

		// 쿠키 만료를 위한 서버에 로그아웃 리퀘스트
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(
				URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGOUT)
				.toString(), null, null, null));

		VolleyHttpClient.destroyCookie();

		// AQuery의 캐시들을 정리한다.
		AQUtility.cleanCacheAsync(getApplicationContext());

		super.onDestroy();
	}

	@Override
	public void onError() {
		super.onError();

		// Error Fragment를 표시한다.
		replaceFragment(new ErrorFragment());
	}
	
//	public void replaceErrorFragment(Fragment fragment) {
//		try {
//			clearFragmentBackStack();
//			
//			fragmentManager.beginTransaction()
//			.replace(mContentFrame.getId(), fragment)
//			.commitAllowingStateLoss();
//		
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//				if (fragment instanceof HotelListFragment) {
//					mContentFrame.setPadding(mContentFrame.getPaddingLeft(),
//							mContentFrame.getPaddingTop(),
//							mContentFrame.getPaddingRight(), 0);
//
//					Window w = getWindow();
//					w.setFlags(
//							WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//							WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//
//				} else {
//					WindowManager.LayoutParams attrs = getWindow()
//							.getAttributes();
//					attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//					getWindow().setAttributes(attrs);
//
//				}
//			}
//			
//		} catch(IllegalStateException e) {
//			onError(e);
//			e.printStackTrace();
//		}
//	}
	
	public void replaceErrorFragment(Fragment fragment) {
		try {
			clearFragmentBackStack();
		
			fragmentManager.beginTransaction().replace(mContentFrame.getId(), fragment).commitAllowingStateLoss();
		
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				if (fragment instanceof HotelListFragment) {
					mContentFrame.setPadding(mContentFrame.getPaddingLeft(), mContentFrame.getPaddingTop(), mContentFrame.getPaddingRight(), 0);
				
					Window w = getWindow();
					w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
				} else {
					WindowManager.LayoutParams attrs = getWindow().getAttributes();
					attrs.flags &= (~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
					getWindow().setAttributes(attrs);
				}
			}
		} catch (IllegalStateException e) {
			onError(e);
			
		}
	}
}
