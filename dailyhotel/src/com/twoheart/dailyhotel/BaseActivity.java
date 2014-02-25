package com.twoheart.dailyhotel;

import static com.twoheart.dailyhotel.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.AppConstants.SHARED_PREFERENCES_NAME;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.twoheart.dailyhotel.booking.BookingListFragment;
import com.twoheart.dailyhotel.credit.CreditFragment;
import com.twoheart.dailyhotel.credit.NoLoginFragment;
import com.twoheart.dailyhotel.hotel.HotelListFragment;
import com.twoheart.dailyhotel.setting.SettingFragment;
import com.twoheart.dailyhotel.utils.DrawerMenu;
import com.twoheart.dailyhotel.utils.DrawerMenuListAdapter;

public class BaseActivity extends ActionBarActivity implements
		OnItemClickListener {

	private final static String TAG = "BaseActivity";

	private static final String DRAWER_MENU_HOTEL = "호텔예약";
	private static final String DRAWER_MENU_BOOKING = "예약확인";
	private static final String DRAWER_MENU_CREDIT = "적립금";
	private static final String DRAWER_MENU_SETTING = "설정";

	public ActionBar actionBar;
	private List<DrawerMenu> mMenuImages;
	private DrawerLayout mDrawerLayout;
	private DrawerMenuListAdapter drawerMenuListAdapter;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private SharedPreferences prefs;
	private Fragment content;
	
	private DrawerMenu menuHotel;
	private DrawerMenu menuBooking;
	private DrawerMenu menuCredit;
	private DrawerMenu menuSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		actionBar = getSupportActionBar();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, 0, 0) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
		menuHotel = new DrawerMenu(DRAWER_MENU_HOTEL,
				R.drawable.dh_menu_hotel, R.drawable.dh_menu_select);
		menuBooking = new DrawerMenu(DRAWER_MENU_BOOKING,
				R.drawable.dh_menu_booking, 0);
		menuCredit = new DrawerMenu(DRAWER_MENU_CREDIT,
				R.drawable.dh_menu_credit, 0);
		menuSetting = new DrawerMenu(DRAWER_MENU_SETTING,
				R.drawable.dh_menu_setting, 0);

		mMenuImages = new ArrayList<DrawerMenu>();
		mMenuImages.add(menuHotel);
		mMenuImages.add(menuBooking);
		mMenuImages.add(menuCredit);
		mMenuImages.add(menuSetting);

		drawerMenuListAdapter = new DrawerMenuListAdapter(this,
				R.layout.drawer_list_item, mMenuImages);

		// Set the adapter for the list view
		mDrawerList.setAdapter(drawerMenuListAdapter);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(this);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {

		Fragment newContent = null;
		SharedPreferences.Editor ed = prefs.edit();
		
		disableAllButtons();
		mMenuImages.get(position).setBackground(R.drawable.dh_menu_select);
		drawerMenuListAdapter.notifyDataSetChanged();

		switch (((DrawerMenu) (adapterView.getAdapter().getItem(position)))
				.getIcon()) {
		case R.drawable.dh_menu_hotel:
			newContent = new HotelListFragment();
			break;

		case R.drawable.dh_menu_booking:
			newContent = new BookingListFragment();
			break;

		case R.drawable.dh_menu_credit:
			if (checkLogin()) // 로그인상태
				newContent = new CreditFragment();
			else
				// 로그아웃 상태
				newContent = new NoLoginFragment();
			break;

		case R.drawable.dh_menu_setting:
			newContent = new SettingFragment();
			break;
		}

		switchFragment(newContent);
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	private void disableAllButtons() {
		for (int i=0; i<mMenuImages.size(); i++) {
			mMenuImages.get(i).setBackground(0);
		}
	}

	private void switchFragment(Fragment fragment) {
		switchContent(fragment);
	}

	private boolean checkLogin() {
		return prefs.getBoolean(PREFERENCE_IS_LOGIN, false);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		return super.onPrepareOptionsMenu(menu);
	}

	// 선택된 menu에 맞게 Fragment 변경
	// MenuFragment에서 호출됨
	public void switchContent(Fragment fragment) {
		content = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment)
				.commitAllowingStateLoss();
		// getSlidingMenu().showContent();
	}

	// @Override
	// public void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	//
	// setBehindContentView(R.layout.menu_frame_left);

	// if (savedInstanceState == null) {
	// FragmentTransaction t =
	// this.getSupportFragmentManager().beginTransaction();
	// dm_fragment = new DailyMenuFragment();
	// getSupportFragmentManager()
	// .beginTransaction()
	// .replace(R.id.menu_frame_left, dm_fragment)
	// .commit();
	// } else {
	// dm_fragment =
	// (DailyMenuFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame_left);
	// }

	// customize the SlidingMenu
	// SlidingMenu sm = getSlidingMenu();
	// sm.setShadowWidthRes(R.dimen.shadow_width);
	// sm.setShadowDrawable(R.drawable.shadow);
	// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset_left);
	// sm.setFadeDegree(0.35f);
	// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

	// }

}
