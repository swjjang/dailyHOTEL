package com.twoheart.dailyhotel;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.activity.SplashActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.GlobalFont;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.CloseOnBackPressed;
import com.twoheart.dailyhotel.util.ui.LoadingDialog;
import com.twoheart.dailyhotel.util.ui.OnLoadCompleteListener;

public class MainActivity extends BaseActivity implements OnItemClickListener,
		Constants, ErrorListener, OnLoadCompleteListener {

	private static final String TAG = "MainActivity";

	private static final String DRAWER_MENU_SECTION_RESERVATION = "예약";
	private static final String DRAWER_MENU_ENTRY_HOTEL = "오늘의 호텔";
	private static final String DRAWER_MENU_ENTRY_BOOKING = "예약확인";
	private static final String DRAWER_MENU_SECTION_ACCOUNT = "계정";
	private static final String DRAWER_MENU_ENTRY_CREDIT = "적립금";
	private static final String DRAWER_MENU_ENTRY_SETTING = "설정";

	public static final int INDEX_HOTEL_LIST_FRAGMENT = 0;
	public static final int INDEX_BOOKING_LIST_FRAGMENT = 1;
	public static final int INDEX_CREDIT_FRAGMENT = 2;
	public static final int INDEX_SETTING_FRAGMENT = 3;

	public static final String KEY_HOTEL_LIST_FRAGMENT = "hotel_list";
	public static final String KEY_BOOKING_LIST_FRAGMENT = "booking_list";
	public static final String KEY_CREDIT_FRAGMENT = "credit";
	public static final String KEY_SEETING_FRAGMENT = "setting";

	private DrawerMenuListAdapter mDrawerMenuListAdapter;
	protected List<DrawerMenu> mMenuImages;
	protected List<Fragment> mFragments = new LinkedList<Fragment>();

	public int indexLastFragment;

	public DrawerLayout drawerLayout;
	public ListView drawerList;
	public ActionBarDrawerToggle drawerToggle;
	protected FragmentManager fragmentManager;

	public DrawerMenu menuHotelListFragment;
	public DrawerMenu menuBookingListFragment;
	public DrawerMenu menuCreditFragment;
	public DrawerMenu menuSettingFragment;
	
	private RequestQueue mQueue;

	private CloseOnBackPressed backButtonHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		CookieSyncManager.createInstance(this);
		if (CookieManager.getInstance().getCookie(URL_DAILYHOTEL_SERVER) != null)
			VolleyHttpClient.destroyCookie();
		
		startActivityForResult(new Intent(this, SplashActivity.class),
				CODE_REQUEST_ACTIVITY_SPLASH);
		super.onCreate(savedInstanceState);

		setTheme(R.style.AppTheme);
		setContentView(R.layout.activity_main);
		setNavigationDrawer(this);

		mQueue = VolleyHttpClient.getRequestQueue();
		fragmentManager = getSupportFragmentManager();
		backButtonHandler = new CloseOnBackPressed(this);

		// 맨 처음은 호텔리스트
		selectMenuDrawer(menuHotelListFragment);

		if (DEBUG) {
			printPackageHashKey();
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CODE_REQUEST_ACTIVITY_SPLASH) {
			if (resultCode == CODE_RESULT_ACTIVITY_SPLASH_NEW_EVENT) {
				// ((HotelListFragment)
				// getFragment(INDEX_HOTEL_LIST_FRAGMENT)).notifyNewEvent();
			}
		}
	}

	public void selectMenuDrawer(DrawerMenu selectedMenu) {
		drawerList.performItemClick(
				drawerList.getAdapter().getView(
						mMenuImages.indexOf(selectedMenu), null, null),
				mMenuImages.indexOf(selectedMenu), mDrawerMenuListAdapter
						.getItemId(mMenuImages.indexOf(selectedMenu)));
	}
	
	public Fragment getFragment(int index) {
		Fragment newFragment = null;

		try {
			newFragment = mFragments.get(index);
		} catch (IndexOutOfBoundsException e) {

			switch (index) {
			case INDEX_HOTEL_LIST_FRAGMENT:
				newFragment = new HotelListFragment();
				break;
			case INDEX_BOOKING_LIST_FRAGMENT:
				newFragment = new BookingListFragment();
				break;
			case INDEX_CREDIT_FRAGMENT:
				newFragment = new CreditFragment();
				break;
			case INDEX_SETTING_FRAGMENT:
				newFragment = new SettingFragment();
				break;
			}
		}

		return newFragment;

	}

	public void replaceFragment(Fragment fragment) {
		clearFragmentBackStack();
		
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commitAllowingStateLoss();
		
	}

	public void addFragment(Fragment fragment) {
		fragmentManager
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right,
						R.anim.slide_out_right, R.anim.slide_in_right,
						R.anim.slide_out_right)
				.add(R.id.content_frame, fragment)
				.addToBackStack(null)
				.commit();
		
	}
	
	private void clearFragmentBackStack() {
		for (int i=0; i<fragmentManager.getBackStackEntryCount(); ++i)
			fragmentManager.popBackStack();
		
	}

	@Deprecated
	public void removeFragment(Fragment fragment) {
		fragmentManager.beginTransaction().remove(fragment)
				.commitAllowingStateLoss();
	}

	public void printPackageHashKey() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash: getPackageName()" + getPackageName(),
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		drawerLayout.closeDrawer(drawerList);

		int selectedMenuIconId = ((DrawerMenu) (adapterView.getAdapter()
				.getItem(position))).getIcon();

		switch (selectedMenuIconId) {
		case R.drawable.selector_drawermenu_todayshotel:
			indexLastFragment = INDEX_HOTEL_LIST_FRAGMENT;
			break;

		case R.drawable.selector_drawermenu_reservation:
			indexLastFragment = INDEX_BOOKING_LIST_FRAGMENT;
			break;

		case R.drawable.selector_drawermenu_saving:
			indexLastFragment = INDEX_CREDIT_FRAGMENT;
			break;

		case R.drawable.selector_drawermenu_setting:
			indexLastFragment = INDEX_SETTING_FRAGMENT;
			break;
		}

		replaceFragment(getFragment(indexLastFragment));

	}

	public void setNavigationDrawer(OnItemClickListener listener) {
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
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
		drawerList = (ListView) findViewById(R.id.left_drawer);

		menuHotelListFragment = new DrawerMenu(DRAWER_MENU_ENTRY_HOTEL,
				R.drawable.selector_drawermenu_todayshotel,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuBookingListFragment = new DrawerMenu(DRAWER_MENU_ENTRY_BOOKING,
				R.drawable.selector_drawermenu_reservation,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuCreditFragment = new DrawerMenu(DRAWER_MENU_ENTRY_CREDIT,
				R.drawable.selector_drawermenu_saving,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuSettingFragment = new DrawerMenu(DRAWER_MENU_ENTRY_SETTING,
				R.drawable.selector_drawermenu_setting,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);

		mMenuImages = new ArrayList<DrawerMenu>();
		mMenuImages.add(new DrawerMenu(DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO));
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_RESERVATION,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuHotelListFragment);
		mMenuImages.add(menuBookingListFragment);
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_ACCOUNT,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuCreditFragment);
		mMenuImages.add(menuSettingFragment);

		mDrawerMenuListAdapter = new DrawerMenuListAdapter(this,
				R.layout.list_row_drawer_entry, mMenuImages);

		drawerList.setAdapter(mDrawerMenuListAdapter);
		drawerList.setOnItemClickListener(listener);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (drawerToggle != null)
			drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (drawerToggle != null)
			drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void finish() {
		if (backButtonHandler.onBackPressed())
			super.finish();

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

		public int gettype() {
			return type;
		}

		public void settype(int type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getIcon() {
			return icon;
		}

		public void setIcon(int icon) {
			this.icon = icon;
		}

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
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			DrawerMenu item = list.get(position);

			switch (item.gettype()) {
			case DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO:
				convertView = inflater.inflate(R.layout.list_row_drawer_logo,
						null);
				break;

			case DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION:
				convertView = inflater.inflate(
						R.layout.list_row_drawer_section, null);

				TextView drawerMenuItemTitle = (TextView) convertView
						.findViewById(R.id.drawerMenuItemTitle);

				drawerMenuItemTitle.setText(item.getTitle());

				break;

			case DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY:
				convertView = inflater.inflate(R.layout.list_row_drawer_entry,
						null);

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
		mQueue.add(new DailyHotelJsonRequest(
				Method.GET,
				new StringBuilder(
						URL_DAILYHOTEL_SERVER)
						.append(URL_WEBAPI_USER_LOGOUT)
						.toString(), null,
				null,
				null));
		
		VolleyHttpClient.destroyCookie();
		
		super.onDestroy();
	}
	
	@Override
	public void onLoadComplete(Fragment fragment, boolean isSucceed) {
		if (!isSucceed) {
			replaceFragment(new ErrorFragment());
			Toast.makeText(this, "네트워크 상태가 좋지 않습니다.\n네트워크 연결을 다시 확인해주세요.",
					Toast.LENGTH_SHORT).show();
		}
		
		LoadingDialog.hideLoading();
		
		if (fragment != null)
			GlobalFont.apply((ViewGroup) fragment.getView().getRootView());
			
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		if (DEBUG)
			error.printStackTrace();
		
		onLoadComplete(null, false);
	}

}
