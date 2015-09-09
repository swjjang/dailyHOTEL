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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.androidquery.util.AQUtility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.ExitActivity;
import com.twoheart.dailyhotel.activity.SplashActivity;
import com.twoheart.dailyhotel.fragment.BookingListFragment;
import com.twoheart.dailyhotel.fragment.CreditFragment;
import com.twoheart.dailyhotel.fragment.ErrorFragment;
import com.twoheart.dailyhotel.fragment.EventListFragment;
import com.twoheart.dailyhotel.fragment.FnBMainFragment;
import com.twoheart.dailyhotel.fragment.HotelMainFragment;
import com.twoheart.dailyhotel.fragment.PlaceMainFragment;
import com.twoheart.dailyhotel.fragment.RatingHotelFragment;
import com.twoheart.dailyhotel.fragment.SettingFragment;
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
import com.twoheart.dailyhotel.util.DailyHotelPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.CloseOnBackPressed;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity
		extends BaseActivity implements OnItemClickListener, Constants
{
	private static final int DRAWERMENU_COUNT = 6;

	public static final int INDEX_HOTEL_LIST_FRAGMENT = 0;
	public static final int INDEX_FNB_LIST_FRAGMENT = 1;
	public static final int INDEX_BOOKING_LIST_FRAGMENT = 2;
	public static final int INDEX_CREDIT_FRAGMENT = 3;
	public static final int INDEX_EVENT_FRAGMENT = 4;
	public static final int INDEX_SETTING_FRAGMENT = 5;

	private static final String TAG_FRAGMENT_RATING_HOTEL = "rating_hotel";

	public ListView drawerList;
	private View drawerView;
	public DrawerLayout drawerLayout;
	private FrameLayout mContentFrame;
	private View mNewEventView;
	public Dialog popUpDialog;

	public ActionBarDrawerToggle drawerToggle;
	protected FragmentManager fragmentManager;
	protected List<DrawerMenu> mDrawerMenuList;
	private DrawerMenuListAdapter mDrawerMenuListAdapter;

	// 마지막으로 머물렀던 Fragment의 index
	public int indexLastFragment; // Error Fragment에서 다시 돌아올 때 필요.

	// DrawerMenu 객체들
	public DrawerMenu menuHotelListFragment;
	public DrawerMenu menuFnBListFragment;
	public DrawerMenu menuBookingListFragment;
	public DrawerMenu menuCreditFragment;
	public DrawerMenu menuEventListFragment;
	public DrawerMenu menuSettingFragment;

	// Back 버튼을 두 번 눌러 핸들러 멤버 변수
	private CloseOnBackPressed backButtonHandler;

	protected HashMap<String, String> regPushParams;
	private Handler mHandler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//		com.twoheart.dailyhotel.network.request.DailyHotelRequest.makeUrlEncoder();

		// 사용자가 선택한 언어, but 만약 사용자가 한국인인데 일본어를 선택하면 jp가 됨.
		// 영어인 경우 - English, 한글인 경우 - 한국어
		Util.setLocale(this, "한국어");

		VolleyHttpClient.cookieManagerCreate();

		Editor editor = sharedPreference.edit();
		editor.remove(KEY_PREFERENCE_BY_SHARE);
		editor.apply();

		// 이전의 비정상 종료에 의한 만료된 쿠키들이 있을 수 있으므로, SplashActivity에서 자동 로그인을
		// 처리하기 이전에 미리 이미 저장되어 있는 쿠키들을 정리한다.
		// android.content.pm.PackageManager$NameNotFoundException: com.google.android.webview
		try
		{
			if (CookieManager.getInstance().getCookie(URL_DAILYHOTEL_SERVER) != null)
			{
				VolleyHttpClient.destroyCookie();
			}
		} catch (Exception e)
		{
			ExLog.d(e.toString());
		}

		initLayout();

		Uri intentData = getIntent().getData();
		checkExternalLink(intentData);

		// 스플래시 화면을 띄운다
		startActivityForResult(new Intent(this, SplashActivity.class), CODE_REQUEST_ACTIVITY_SPLASH);
	}

	private void initLayout()
	{
		setContentView(R.layout.activity_main);

		//순서 중요 
		// 1
		Toolbar toolbar = setActionBar(getString(R.string.actionbar_title_hotel_list_frag), false);

		// 2
		mNewEventView = findViewById(R.id.newEventView);
		hideActionBarNewIcon(true);

		// 3
		setNavigationDrawer(toolbar);

		mContentFrame = (FrameLayout) findViewById(R.id.content_frame);

		fragmentManager = getSupportFragmentManager();
		backButtonHandler = new CloseOnBackPressed(this);

		// Facebook SDK를 관리하기 위한 패키지 Hash 값 표시
		if (DEBUG)
		{
			printPackageHashKey();
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		Editor editor = sharedPreference.edit();
		editor.remove(KEY_PREFERENCE_BY_SHARE);
		editor.apply();

		Uri intentData = intent.getData();
		checkExternalLink(intentData);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		requestEvent();
	}

	private void checkExternalLink(Uri uri)
	{
		if (uri == null)
		{
			return;
		}

		final String KAKAOLINK = "kakaolink";
		final String DAILYHOTEL = "dailyhotel";

		ExLog.e("intentData : " + uri.toString());

		String link = uri.toString();

		DrawerMenu selectMenuDrawer = menuHotelListFragment;

		if (link.indexOf(KAKAOLINK) >= 0 || link.indexOf(DAILYHOTEL) >= 0)
		{
			writeKakaoLinkPreference(link);

			if (link.contains("hotelIndex") == true)
			{
				selectMenuDrawer = menuHotelListFragment;
			} else if (link.contains("fnbIndex") == true)
			{
				selectMenuDrawer = menuFnBListFragment;
			}
		}

		selectMenuDrawer(selectMenuDrawer);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_REQUEST_ACTIVITY_SPLASH)
		{
			switch (resultCode)
			{
				// 스플래시 화면이 정상적으로 종료되었을 경우
				case RESULT_OK:
					break;

				default: // 스플래시가 비정상적으로 종료되었을 경우
					super.finish(); // 어플리케이션(메인 화면)을 종료해버린다
					return; // 메서드를 빠져나간다 - 호텔 평가를 수행하지 않음.
			}

			// 앱을 처음 설치한 경우 가이드를 띄움. 일단 화면 보이지 않도록 수정.
			//			boolean showGuide = false;//sharedPreference.getBoolean(KEY_PREFERENCE_SHOW_GUIDE, true);
			//			if (showGuide)
			//			{
			//				startActivityForResult(new Intent(this, IntroActivity.class), CODE_REQUEST_ACTIVITY_INTRO);
			//			} else
			{
				if (sharedPreference.contains(KEY_PREFERENCE_BY_SHARE) == true)
				{
					selectMenuDrawer(menuHotelListFragment);
				} else
				{
					// Intent가 Push로 부터 온경우
					int pushType = getIntent().getIntExtra(NAME_INTENT_EXTRA_DATA_PUSH_TYPE, -1);
					switch (pushType)
					{
						case PUSH_TYPE_NOTICE:
							selectMenuDrawer(menuHotelListFragment);
							break;
						case PUSH_TYPE_ACCOUNT_COMPLETE:
							selectMenuDrawer(menuBookingListFragment);
							break;
						default:
							selectMenuDrawer(menuHotelListFragment);
							break;
					}

					mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_ALIVE).toString(), null, mUserAliveStringResponseListener, this));
				}
			}
		} else if (requestCode == CODE_REQUEST_ACTIVITY_INTRO)
		{
			selectMenuDrawer(menuHotelListFragment);
		}
	}

	private void writeKakaoLinkPreference(String link)
	{
		if (TextUtils.isEmpty(link) == true)
		{
			return;
		}

		int startIndex = link.indexOf("?") + 1;
		String param = link.substring(startIndex);

		// param 저장하기
		Editor editor = sharedPreference.edit();
		editor.putString(KEY_PREFERENCE_BY_SHARE, param);
		editor.apply();
	}

	private String getGcmId()
	{
		return sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");
	}

	private Boolean isGoogleServiceAvailable()
	{
		int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (resCode != ConnectionResult.SUCCESS)
		{
			return false;
		} else
		{
			return true;
		}
	}

	private void regGcmId(final String idx)
	{
		if (isGoogleServiceAvailable() == false)
		{
			return;
		}

		new AsyncTask<Void, Void, String>()
		{
			@Override
			protected String doInBackground(Void... params)
			{
				GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(MainActivity.this);
				String regId = "";

				try
				{
					regId = instance.register(GCM_PROJECT_NUMBER);
					ExLog.d("regId : " + regId);
				} catch (IOException e)
				{
					ExLog.e(e.toString());
				}

				return regId;
			}

			@Override
			protected void onPostExecute(String regId)
			{
				// gcm id가 없을 경우 스킵.
				if (regId == null || regId.isEmpty())
				{
					return;
				}

				// 이 값을 서버에 등록하기.
				regPushParams = new HashMap<String, String>();
				regPushParams.put("user_idx", idx);
				regPushParams.put("notification_id", regId);
				regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

				mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_GCM_REGISTER).toString(), regPushParams, mGcmRegisterJsonResponseListener, MainActivity.this));
			}
		}.execute();
	}

	/**
	 * 네비게이션 드로워에서 메뉴를 선택하는 효과를 내주는 메서드
	 * 
	 * @param selectedMenu
	 *            DrawerMenu 객체를 받는다.
	 */
	public void selectMenuDrawer(DrawerMenu selectedMenu)
	{
		drawerList.performItemClick(mDrawerMenuListAdapter.getView(mDrawerMenuList.indexOf(selectedMenu), null, null), mDrawerMenuList.indexOf(selectedMenu), mDrawerMenuListAdapter.getItemId(mDrawerMenuList.indexOf(selectedMenu)));
	}

	public void refreshMenuDrawer()
	{
		switch (indexLastFragment)
		{
			case INDEX_HOTEL_LIST_FRAGMENT:
				selectMenuDrawer(menuHotelListFragment);
				break;

			case INDEX_FNB_LIST_FRAGMENT:
				selectMenuDrawer(menuFnBListFragment);
				break;

			case INDEX_BOOKING_LIST_FRAGMENT:
				selectMenuDrawer(menuBookingListFragment);
				break;

			case INDEX_CREDIT_FRAGMENT:
				selectMenuDrawer(menuCreditFragment);
				break;

			case INDEX_EVENT_FRAGMENT:
				selectMenuDrawer(menuEventListFragment);
				break;

			case INDEX_SETTING_FRAGMENT:
				selectMenuDrawer(menuSettingFragment);
				break;
		}
	}

	/**
	 * 네비게이션 드로워 메뉴에서 선택할 수 있는 Fragment를 반환하는 메서드이다.
	 * 
	 * @param index
	 *            Fragment 리스트에 해당하는 index를 받는다.
	 * @return 요청한 index에 해당하는 Fragment를 반환한다. => 기능 변경, 누를때마다 리프레시
	 */
	public Fragment getFragment(int index)
	{
		switch (index)
		{
			case INDEX_HOTEL_LIST_FRAGMENT:
				return new HotelMainFragment();
			case INDEX_FNB_LIST_FRAGMENT:
				return new FnBMainFragment();
			case INDEX_BOOKING_LIST_FRAGMENT:
				return new BookingListFragment();
			case INDEX_CREDIT_FRAGMENT:
				return new CreditFragment();
			case INDEX_EVENT_FRAGMENT:
				return new EventListFragment();
			case INDEX_SETTING_FRAGMENT:
				return new SettingFragment();
		}
		return null;

	}

	/**
	 * Fragment 컨테이너에서 해당 Fragment로 변경하여 표시한다.
	 * 
	 * @param fragment
	 *            Fragment 리스트에 보관된 Fragement들을 받는 것이 좋다.
	 */
	public void replaceFragment(Fragment fragment)
	{
		try
		{
			clearFragmentBackStack();

			fragmentManager.beginTransaction().replace(mContentFrame.getId(), fragment).commitAllowingStateLoss();
		} catch (IllegalStateException e)
		{
			// 에러가 나는 경우 앱을 재부팅 시킨다.
			Util.restartApp(MainActivity.this);
		}

		// 액션바 위치를 다시 잡아준다.

	}

	/**
	 * Fragment 컨테이너에서 해당 Fragement를 쌓아올린다.
	 * 
	 * @param fragment
	 *            Fragment 리스트에 보관된 Fragment들을 받는 것이 좋다.
	 */
	public void addFragment(Fragment fragment)
	{
		fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_right).add(R.id.content_frame, fragment).addToBackStack(null).commitAllowingStateLoss();

	}

	/**
	 * Fragment 컨테이너의 표시되는 Fragment를 변경할 때 Fragment 컨테이너에 적재된 Fragment들을 정리한다.
	 */
	private void clearFragmentBackStack()
	{
		for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i)
		{
			fragmentManager.popBackStackImmediate();
		}
	}

	@Deprecated
	public void removeFragment(Fragment fragment)
	{
		fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
	}

	/**
	 * 페이스북 SDK를 사용하기 위해선 개발하는 컴퓨터의 해시키를 페이스북 개발 콘솔에 등록 할 필요가 있음. 이에따라서 현재 컴퓨터의
	 * 해시키를 출력해주어 등록을 돕게함.
	 */
	public void printPackageHashKey()
	{
		try
		{
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures)
			{
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
			}
		} catch (Exception e)
		{
			onError(e);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
	{
		DrawerMenu selectedDrawMenu = (DrawerMenu) (adapterView.getAdapter().getItem(position));
		int selectedMenuIconId = selectedDrawMenu.getIcon();

		switch (selectedMenuIconId)
		{
			case R.drawable.selector_drawermenu_todayshotel:
				indexLastFragment = INDEX_HOTEL_LIST_FRAGMENT;
				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_hotel_list_frag), (long) position);
				break;

			case R.drawable.selector_drawermenu_fnb:
				indexLastFragment = INDEX_FNB_LIST_FRAGMENT;

				// 이벤트 진입시에 이벤트 new를 제거한다.
				DailyHotelPreference.getInstance(this).setNewTodayFnB(false);

				hideNewFnb(true);

				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_fnb_list_frag), (long) position);
				break;

			case R.drawable.selector_drawermenu_reservation:
				indexLastFragment = INDEX_BOOKING_LIST_FRAGMENT;
				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_booking_list_frag), (long) position);
				break;

			case R.drawable.selector_drawermenu_saving:
				indexLastFragment = INDEX_CREDIT_FRAGMENT;
				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_credit_frag), (long) position);
				break;

			case R.drawable.selector_drawermenu_eventlist:
				indexLastFragment = INDEX_EVENT_FRAGMENT;

				// 이벤트 진입시에 이벤트 new를 제거한다.
				Editor editor = sharedPreference.edit();
				editor.putBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);

				long currentDateTime = sharedPreference.getLong(KEY_PREFERENCE_LOOKUP_EVENT_TIME, 0);
				editor.putLong(KEY_PREFERENCE_NEW_EVENT_TIME, currentDateTime);
				editor.commit();

				hideNewEvent(true);

				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_event_list_frag), (long) position);
				break;

			case R.drawable.selector_drawermenu_setting:
				indexLastFragment = INDEX_SETTING_FRAGMENT;
				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, getString(R.string.actionbar_title_setting_frag), (long) position);
				break;
		}

		//
		menuHotelListFragment.setSelected(false);
		menuFnBListFragment.setSelected(false);
		menuBookingListFragment.setSelected(false);
		menuCreditFragment.setSelected(false);
		menuEventListFragment.setSelected(false);
		menuSettingFragment.setSelected(false);

		selectedDrawMenu.setSelected(true);
		mDrawerMenuListAdapter.notifyDataSetChanged();

		if (drawerLayout.isDrawerOpen(GravityCompat.START) == true)
		{
			delayedReplace(indexLastFragment);
			drawerLayout.closeDrawer(drawerView);
		} else
		{
			replaceFragment(getFragment(indexLastFragment));
		}
	}

	/**
	 * 드로어 레이아웃이 닫히는데 애니메이션이 부하가 큼. 프래그먼트 전환까지 추가한다면 닫힐때 버벅거리는 현상이 발생. 따라서 0.3초
	 * 지연하여 자연스러운 애니메이션을 보여줌.
	 * 
	 * @param index
	 *            프래그먼트 인덱스.
	 */
	public void delayedReplace(final int index)
	{
		mHandler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				replaceFragment(getFragment(index));
			}
		}, 300);
	}

	/**
	 * 네비게이션 드로워를 셋팅하는 메서드
	 */
	public void setNavigationDrawer(Toolbar toolbar)
	{
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
		{
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu();

				releaseUiComponent();
			}

			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);

				supportInvalidateOptionsMenu();
				releaseUiComponent();

				AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.MENU, Action.CLICK, Label.MENU_OPENED, 0L);
			}

			@Override
			public void onDrawerStateChanged(int newState)
			{
				switch (newState)
				{
					case DrawerLayout.STATE_SETTLING:
					{
						if (drawerLayout.isDrawerOpen(drawerView) == false)
						{
							if (isLockUiComponent() == true)
							{
								drawerLayout.closeDrawer(drawerView);
								return;
							}

							lockUiComponent();
						}
						break;
					}

					case DrawerLayout.STATE_IDLE:
						releaseUiComponent();
						break;
				}

				super.onDrawerStateChanged(newState);
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset)
			{
				if (Float.compare(slideOffset, 0.0f) > 0)
				{
					setActionBarRegionEnable(false);

					if (isShowActionBarNewIcon() == true)
					{
						hideActionBarNewIcon(true);

						if (mDrawerMenuListAdapter != null)
						{
							mDrawerMenuListAdapter.notifyDataSetChanged();
						}
					}

					fragmentManager = getSupportFragmentManager();

					if (fragmentManager != null && fragmentManager.getFragments() != null)
					{
						for (Fragment fragment : fragmentManager.getFragments())
						{
							if (fragment != null && fragment.isVisible() && fragment instanceof HotelMainFragment)
							{
								((HotelMainFragment) fragment).setMenuEnabled(false);
								break;
							}
						}
					}
				} else if (Float.compare(slideOffset, 0.0f) == 0)
				{
					setActionBarRegionEnable(true);

					if (sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false) == true //
					|| DailyHotelPreference.getInstance(MainActivity.this).isNewTodayFnB() == true)
					{
						showActionBarNewIcon();
					}

					fragmentManager = getSupportFragmentManager();

					if (fragmentManager != null && fragmentManager.getFragments() != null)
					{
						for (Fragment fragment : fragmentManager.getFragments())
						{
							if (fragment != null && fragment.isVisible() && fragment instanceof HotelMainFragment)
							{
								((HotelMainFragment) fragment).setMenuEnabled(true);
								break;
							} else if (fragment != null && fragment.isVisible() && fragment instanceof PlaceMainFragment)
							{
								((PlaceMainFragment) fragment).setMenuEnabled(true);
								break;
							}
						}
					}
				}

				super.onDrawerSlide(drawerView, slideOffset);
			}
		};

		drawerLayout.post(new Runnable()
		{
			@Override
			public void run()
			{
				drawerToggle.syncState();
			}
		});

		drawerLayout.setDrawerListener(drawerToggle);

		drawerView = findViewById(R.id.left_drawer);
		drawerList = (ListView) findViewById(R.id.drawListView);

		menuHotelListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_todays_hotel), R.drawable.selector_drawermenu_todayshotel, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuFnBListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_todays_fnb), R.drawable.selector_drawermenu_fnb, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuBookingListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_chk_reservation), R.drawable.selector_drawermenu_reservation, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuCreditFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_credit), R.drawable.selector_drawermenu_saving, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuEventListFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_event), R.drawable.selector_drawermenu_eventlist, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuSettingFragment = new DrawerMenu(getString(R.string.drawer_menu_item_title_setting), R.drawable.selector_drawermenu_setting, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);

		mDrawerMenuList = new ArrayList<DrawerMenu>(DRAWERMENU_COUNT);
		mDrawerMenuList.add(menuHotelListFragment);
		mDrawerMenuList.add(menuFnBListFragment);
		mDrawerMenuList.add(menuBookingListFragment);
		mDrawerMenuList.add(menuCreditFragment);
		mDrawerMenuList.add(menuEventListFragment);
		mDrawerMenuList.add(menuSettingFragment);

		// New Icon
		if (sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false))
		{
			menuEventListFragment.hasEvent = true;
		} else
		{
			menuEventListFragment.hasEvent = false;
		}

		if (DailyHotelPreference.getInstance(this).isNewTodayFnB() == true)
		{
			menuFnBListFragment.hasEvent = true;

			showNewFnB(true);
		} else
		{
			menuFnBListFragment.hasEvent = false;
		}

		mDrawerMenuListAdapter = new DrawerMenuListAdapter(this, mDrawerMenuList);

		drawerList.setAdapter(mDrawerMenuListAdapter);
		drawerList.setOnItemClickListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		if (drawerToggle != null)
			drawerToggle.syncState();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		if (drawerToggle != null)
			drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item))
		{
			return true;
		} else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_MENU)
		{
			toggleDrawer();
			return true;
		} else
		{
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onBackPressed()
	{
		if (drawerLayout.isDrawerOpen(drawerView) == true)
		{
			drawerLayout.closeDrawer(drawerView);
			return;
		}

		if (indexLastFragment == INDEX_HOTEL_LIST_FRAGMENT || indexLastFragment == INDEX_FNB_LIST_FRAGMENT)
		{
			if (backButtonHandler.onBackPressed())
			{
				ExitActivity.exitApplication(this);

				super.onBackPressed();
			}
		} else
		{
			selectMenuDrawer(menuHotelListFragment);
		}
	}

	public void toggleDrawer()
	{
		if (drawerLayout.isDrawerOpen(drawerView) == false)
			drawerLayout.openDrawer(drawerView);
		else
			drawerLayout.closeDrawer(drawerView);
	}

	public void closeDrawer()
	{
		if (drawerLayout != null)
		{
			if (drawerLayout.isDrawerOpen(GravityCompat.START) == true)
			{
				drawerLayout.closeDrawer(drawerView);
			}
		}
	}

	private void showNewEvent(boolean isShowAuctionBar)
	{
		if (isShowAuctionBar == true)
		{
			showActionBarNewIcon();
		}

		if (menuEventListFragment != null)
		{
			menuEventListFragment.hasEvent = true;
		}
	}

	private void showNewFnB(boolean isShowAuctionBar)
	{
		if (isShowAuctionBar == true)
		{
			showActionBarNewIcon();
		}

		if (menuFnBListFragment != null)
		{
			menuFnBListFragment.hasEvent = true;
		}
	}

	private void showActionBarNewIcon()
	{
		if (mNewEventView != null)
		{
			mNewEventView.setVisibility(View.VISIBLE);
		}
	}

	private void hideActionBarNewIcon(boolean isForce)
	{
		if (isForce == false)
		{
			if (sharedPreference.getBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false) == true || //
			DailyHotelPreference.getInstance(this).isNewTodayFnB() == true)
			{
				return;
			}
		}

		if (mNewEventView != null && mNewEventView.getVisibility() != View.GONE)
		{
			mNewEventView.setVisibility(View.GONE);
		}
	}

	private boolean isShowActionBarNewIcon()
	{
		if (mNewEventView != null)
		{
			return mNewEventView.getVisibility() == View.VISIBLE ? true : false;
		}

		return false;
	}

	private void hideNewEvent(boolean isHideMenuList)
	{
		hideActionBarNewIcon(false);

		if (menuEventListFragment != null && isHideMenuList == true)
		{
			menuEventListFragment.hasEvent = false;
		}
	}

	private void hideNewFnb(boolean isHideMenuList)
	{
		hideActionBarNewIcon(false);

		if (menuFnBListFragment != null && isHideMenuList == true)
		{
			menuFnBListFragment.hasEvent = false;
		}
	}

	private void requestEvent()
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("timeZone", "Asia/Seoul");

		mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_COMMON_DATETIME).toString(), params, new DailyHotelJsonResponseListener()
		{
			@Override
			public void onResponse(String url, JSONObject response)
			{
				try
				{
					long currentDateTime = response.getLong("currentDateTime");
					long lastLookupDateTime = sharedPreference.getLong(KEY_PREFERENCE_NEW_EVENT_TIME, 0);

					Editor editor = sharedPreference.edit();
					editor.putLong(KEY_PREFERENCE_LOOKUP_EVENT_TIME, currentDateTime);
					editor.apply();

					String params = String.format("?date_time=%d", lastLookupDateTime);
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_DAILY_EVENT_COUNT).append(params).toString(), null, mDailyEventCountJsonResponseListener, null));
				} catch (Exception e)
				{
					ExLog.d(e.toString());
				}
			}
		}, null));
	}

	private class DrawerMenu
	{
		public static final int DRAWER_MENU_LIST_TYPE_LOGO = 0;
		public static final int DRAWER_MENU_LIST_TYPE_SECTION = 1;
		public static final int DRAWER_MENU_LIST_TYPE_ENTRY = 2;

		private String title;
		private int icon;
		private int type;
		private boolean mSelected;
		public boolean hasEvent;

		public DrawerMenu(String title, int type)
		{
			super();
			this.title = title;
			this.type = type;
		}

		public DrawerMenu(String title, int icon, int type)
		{
			super();
			this.title = title;
			this.icon = icon;
			this.type = type;
		}

		public int getType()
		{
			return type;
		}

		public String getTitle()
		{
			return title;
		}

		public int getIcon()
		{
			return icon;
		}

		public void setSelected(boolean selected)
		{
			mSelected = selected;
		}

		public boolean isSelected()
		{
			return mSelected;
		}
	}

	private class DrawerMenuListAdapter extends BaseAdapter
	{
		private List<DrawerMenu> list;
		private LayoutInflater inflater;
		private Context context;

		public DrawerMenuListAdapter(Context context, List<DrawerMenu> list)
		{
			this.context = context;
			this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.list = list;
		}

		@Override
		public int getCount()
		{
			return list.size();
		}

		@Override
		public Object getItem(int position)
		{
			return list.get(position);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public boolean isEnabled(int position)
		{
			return (list.get(position).getType() == DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY) ? true : false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			DrawerMenu item = list.get(position);

			switch (item.getType())
			{
				case DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO:
				{
					convertView = inflater.inflate(R.layout.list_row_drawer_logo, null);
					break;
				}

				case DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION:
				{
					convertView = inflater.inflate(R.layout.list_row_drawer_section, null);
					break;
				}

				case DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY:
				{
					int height = (Util.getLCDHeight(context) - mStatusBarHeight - Util.dpToPx(context, 58)) / DRAWERMENU_COUNT;

					convertView = inflater.inflate(R.layout.list_row_drawer_entry, null);

					AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
					convertView.setLayoutParams(layoutParams);

					ImageView drawerMenuItemIcon = (ImageView) convertView.findViewById(R.id.drawerMenuItemIcon);
					TextView drawerMenuItemText = (TextView) convertView.findViewById(R.id.drawerMenuItemTitle);
					View eventIconView = convertView.findViewById(R.id.newEventIcon);

					drawerMenuItemIcon.setImageResource(item.getIcon());
					drawerMenuItemText.setText(item.getTitle());

					if (item.isSelected() == true)
					{
						drawerMenuItemIcon.setSelected(true);
						drawerMenuItemText.setSelected(true);
					} else
					{
						drawerMenuItemIcon.setSelected(false);
						drawerMenuItemText.setSelected(false);
					}

					if (item.hasEvent)
					{
						eventIconView.setVisibility(View.VISIBLE);
					} else
					{
						eventIconView.setVisibility(View.GONE);
					}

					break;
				}
			}

			return convertView;
		}
	}

	@Override
	protected void onDestroy()
	{
		// 쿠키 만료를 위한 서버에 로그아웃 리퀘스트
		mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGOUT).toString(), null, null, null));

		VolleyHttpClient.destroyCookie();

		// AQuery의 캐시들을 정리한다.
		AQUtility.cleanCacheAsync(getApplicationContext());

		super.onDestroy();
	}

	@Override
	public void onError()
	{
		super.onError();

		// Error Fragment를 표시한다. -> stackoverflow가 발생하는 경우가 있음. 에러 원인 파악해야 함.
		replaceFragment(new ErrorFragment());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Listener
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
	{

		@Override
		public void onResponse(String url, String response)
		{

			String result = null;

			if (false == TextUtils.isEmpty(response))
			{
				result = response.trim();
			}

			if (true == "alive".equalsIgnoreCase(result))
			{
				// session alive
				// 호텔 평가를 위한 사용자 정보 조회
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, MainActivity.this));
			} else
			{
				if (getGcmId().isEmpty() == true)
				{
					regGcmId("-1");
				}
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
				if (null == response)
				{
					throw new NullPointerException();
				}

				String loginuser_idx = response.getString("idx");

				if (true == TextUtils.isEmpty(loginuser_idx))
				{
					throw new NullPointerException("loginuser_idx is empty.");
				}

				if (getGcmId().isEmpty() == true)
				{
					regGcmId(loginuser_idx);
				}

				// 호텔 평가요청
				mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_RESERV_SATISFACTION_RATION_EXIST).toString(), null, //
				mSatisfactionRatingExistJsonResponseListener, new ErrorListener()
				{
					@Override
					public void onErrorResponse(VolleyError arg0)
					{
						// TODO Auto-generated method stub

					}
				}));
			} catch (Exception e)
			{
				onError(e);
			} finally
			{
				unLockUI();
			}
		}
	};

	private DailyHotelJsonResponseListener mGcmRegisterJsonResponseListener = new DailyHotelJsonResponseListener()
	{
		@Override
		public void onResponse(String url, JSONObject response)
		{
			// 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
			try
			{
				String result = null;

				if (null != response)
				{
					result = response.getString("result");
				}

				if (true == "true".equalsIgnoreCase(result))
				{
					Editor editor = sharedPreference.edit();
					editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id").toString());
					editor.apply();
				}
			} catch (Exception e)
			{
				onError(e);
			}
		}
	};

	private DailyHotelJsonResponseListener mSatisfactionRatingExistJsonResponseListener = new DailyHotelJsonResponseListener()
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

				if (msg_code == 0 && response.has("data") == true)
				{
					JSONObject jsonObject = response.getJSONObject("data");

					//					String guestName = jsonObject.getString("guest_name");
					//					String roomName = jsonObject.getString("room_name");
					long checkInDate = jsonObject.getLong("checkin_date");
					long checkOutDate = jsonObject.getLong("checkout_date");
					String hotelName = jsonObject.getString("hotel_name");
					int reservationIndex = jsonObject.getInt("reserv_idx");

					RatingHotelFragment dialog = RatingHotelFragment.newInstance(hotelName, reservationIndex, checkInDate, checkOutDate);

					if (dialog != null && isFinishing() == false)
					{
						dialog.show(fragmentManager, TAG_FRAGMENT_RATING_HOTEL);

						// 화면이 사라지면 FnB만족도 조사를 살펴본다.
						dialog.setOnDismissListener(new OnDismissListener()
						{
							public void onDismiss(DialogInterface dialog)
							{
								mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_SESSION_RATING_EXIST).toString(), null, mFnBSatisfactionRatingExistJsonResponseListener, new ErrorListener()
								{
									public void onErrorResponse(VolleyError error)
									{

									}
								}));
							}
						});
					}
				} else
				{
					mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_FNB_RESERVATION_SESSION_RATING_EXIST).toString(), null, mFnBSatisfactionRatingExistJsonResponseListener, new ErrorListener()
					{
						public void onErrorResponse(VolleyError error)
						{

						}
					}));
				}
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}
	};

	private DailyHotelJsonResponseListener mDailyEventCountJsonResponseListener = new DailyHotelJsonResponseListener()
	{

		@Override
		public void onResponse(String url, JSONObject response)
		{
			Editor editor = sharedPreference.edit();

			try
			{
				if (response == null)
				{
					throw new NullPointerException("response == null");
				}

				int msg_code = response.getInt("msg_code");

				if (msg_code != 0)
				{
					throw new NullPointerException("msg_code != 0");
				}

				JSONObject jsonObject = response.getJSONObject("data");

				int count = jsonObject.getInt("count");

				if (count > 0)
				{
					editor.putBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, true);
					editor.commit();

					if (drawerLayout.isDrawerOpen(drawerView) == true)
					{
						showNewEvent(false);
					} else
					{
						showNewEvent(true);
					}
				} else
				{
					editor.putBoolean(RESULT_ACTIVITY_SPLASH_NEW_EVENT, false);

					long currentDateTime = sharedPreference.getLong(KEY_PREFERENCE_LOOKUP_EVENT_TIME, 0);
					editor.putLong(KEY_PREFERENCE_NEW_EVENT_TIME, currentDateTime);
					editor.commit();

					hideNewEvent(true);
				}

				// 같이 이벤트 처리
				if (DailyHotelPreference.getInstance(MainActivity.this).isNewTodayFnB() == true)
				{
					if (drawerLayout.isDrawerOpen(drawerView) == true)
					{
						showNewFnB(false);
					} else
					{
						showNewFnB(true);
					}
				}
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}
	};

	private DailyHotelJsonResponseListener mFnBSatisfactionRatingExistJsonResponseListener = new DailyHotelJsonResponseListener()
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

				if (msg_code == 0 && response.has("data") == true)
				{
					JSONObject jsonObject = response.getJSONObject("data");

					long checkInDate = jsonObject.getLong("sday");
					String ticketName = jsonObject.getString("ticket_name");
					int reservationIndex = jsonObject.getInt("reservation_rec_idx");

					RatingHotelFragment dialog = RatingHotelFragment.newInstance(ticketName, reservationIndex, checkInDate);

					if (dialog != null && isFinishing() == false)
					{
						dialog.show(fragmentManager, TAG_FRAGMENT_RATING_HOTEL);
					}
				}
			} catch (Exception e)
			{
				ExLog.d(e.toString());
			}
		}
	};
}
