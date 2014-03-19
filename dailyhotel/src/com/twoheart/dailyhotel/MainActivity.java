package com.twoheart.dailyhotel;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.adapter.DrawerMenuListAdapter;
import com.twoheart.dailyhotel.fragment.BookingListFragment;
import com.twoheart.dailyhotel.fragment.CreditFragment;
import com.twoheart.dailyhotel.fragment.HotelListFragment;
import com.twoheart.dailyhotel.fragment.SettingFragment;
import com.twoheart.dailyhotel.obj.DrawerMenu;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.network.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.network.DailyHotelResponseListener;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class MainActivity extends BaseActivity implements OnItemClickListener,
		Constants {

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

	private DrawerMenuListAdapter mDrawerMenuListAdapter;
	protected List<DrawerMenu> mMenuImages;
	protected List<Fragment> mFragments = new LinkedList<Fragment>();

	public DrawerLayout drawerLayout;
	public ListView drawerList;
	public ActionBarDrawerToggle drawerToggle;
	private FragmentManager mFragmentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		setNavigationDrawer(this);

		mFragmentManager = getSupportFragmentManager();

		// 맨 처음은 호텔리스트
		drawerList.setItemChecked(mMenuImages.indexOf(mMenuHotelListFragment),
				true);
		replaceFragment(getFragment(INDEX_HOTEL_LIST_FRAGMENT));

		if (DEBUG) {
			printPackageHashKey();
		}

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
		mFragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commitAllowingStateLoss();

	}

	public void addFragment(Fragment fragment) {
		mFragmentManager.beginTransaction().add(R.id.content_frame, fragment)
				.addToBackStack(null).commitAllowingStateLoss();
	}
	
	public void removeFragment(Fragment fragment) {
		mFragmentManager.beginTransaction()
				.remove(fragment).commitAllowingStateLoss();
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

		int selectedMenuIconId = ((DrawerMenu) (adapterView.getAdapter()
				.getItem(position))).getIcon();
		drawerList.setSelection(position);

		switch (selectedMenuIconId) {
		case R.drawable.selector_drawermenu_todayshotel:
			replaceFragment(getFragment(INDEX_HOTEL_LIST_FRAGMENT));
			break;

		case R.drawable.selector_drawermenu_reservation:
			replaceFragment(getFragment(INDEX_BOOKING_LIST_FRAGMENT));
			break;

		case R.drawable.selector_drawermenu_saving:
			// if (isAliveUser()) // 로그인상태
			// replaceFragment(getFragment(INDEX_CREDIT_FRAGMENT));
			// else
			// // 로그아웃 상태
			// replaceFragment(new NoLoginFragment());
			replaceFragment(getFragment(INDEX_CREDIT_FRAGMENT));
			break;

		case R.drawable.selector_drawermenu_setting:
			replaceFragment(getFragment(INDEX_SETTING_FRAGMENT));
			break;
		}

		drawerLayout.closeDrawer(drawerList);

	}

	// public boolean isAliveUser() {
	// RequestQueue queue = VolleyHttpClient.getRequestQueue();
	//
	// queue.add(new DailyHotelRequest(Method.GET, new
	// StringBuilder(URL_DAILYHOTEL_SERVER).
	// append(URL_WEBAPI_USER_ALIVE).toString(), null, this, this));
	//
	// return false;
	// }

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

		mMenuHotelListFragment = new DrawerMenu(DRAWER_MENU_ENTRY_HOTEL,
				R.drawable.selector_drawermenu_todayshotel,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		mMenuBookingListFragment = new DrawerMenu(DRAWER_MENU_ENTRY_BOOKING,
				R.drawable.selector_drawermenu_reservation,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		mMenuCreditFragment = new DrawerMenu(DRAWER_MENU_ENTRY_CREDIT,
				R.drawable.selector_drawermenu_saving,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		mMenuSettingFragment = new DrawerMenu(DRAWER_MENU_ENTRY_SETTING,
				R.drawable.selector_drawermenu_setting,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);

		mMenuImages = new ArrayList<DrawerMenu>();
		mMenuImages.add(new DrawerMenu(DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO));
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_RESERVATION,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(mMenuHotelListFragment);
		mMenuImages.add(mMenuBookingListFragment);
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_ACCOUNT,
				DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(mMenuCreditFragment);
		mMenuImages.add(mMenuSettingFragment);

		mDrawerMenuListAdapter = new DrawerMenuListAdapter(this,
				R.layout.drawer_list_item_entry, mMenuImages);

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

	// @Override
	// public void finish() {
	// if (backButtonHandler.onBackPressed())
	// super.finish();
	//
	// }

}
