package com.twoheart.dailyhotel.util.ui;

import static com.twoheart.dailyhotel.util.AppConstants.PREFERENCE_IS_LOGIN;
import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.DrawerMenuListAdapter;
import com.twoheart.dailyhotel.fragment.BookingListFragment;
import com.twoheart.dailyhotel.fragment.CreditFragment;
import com.twoheart.dailyhotel.fragment.HotelListFragment;
import com.twoheart.dailyhotel.fragment.NoLoginFragment;
import com.twoheart.dailyhotel.fragment.SettingFragment;
import com.twoheart.dailyhotel.obj.DrawerMenu;

public class BaseActivity extends ActionBarActivity implements
		OnItemClickListener {

	private final static String TAG = "BaseActivity";

	private static final String DRAWER_MENU_SECTION_RESERVATION = "예약";
	private static final String DRAWER_MENU_ENTRY_HOTEL = "오늘의 호텔";
	private static final String DRAWER_MENU_ENTRY_BOOKING = "예약확인";
	private static final String DRAWER_MENU_SECTION_ACCOUNT = "계정";
	private static final String DRAWER_MENU_ENTRY_CREDIT = "적립금";
	private static final String DRAWER_MENU_ENTRY_SETTING = "설정";

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

	public static Typeface mTypefaceCommon;
	public static Typeface mTypefaceBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, 0);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		setGlobalFont((ViewGroup) this.getWindow().getDecorView().findViewById(
				android.R.id.content));

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

		menuHotel = new DrawerMenu(DRAWER_MENU_ENTRY_HOTEL, 
				R.drawable.selector_drawermenu_todayshotel, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuBooking = new DrawerMenu(DRAWER_MENU_ENTRY_BOOKING,
				R.drawable.selector_drawermenu_reservation, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuCredit = new DrawerMenu(DRAWER_MENU_ENTRY_CREDIT,
				R.drawable.selector_drawermenu_saving, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);
		menuSetting = new DrawerMenu(DRAWER_MENU_ENTRY_SETTING,
				R.drawable.selector_drawermenu_setting, DrawerMenu.DRAWER_MENU_LIST_TYPE_ENTRY);

		mMenuImages = new ArrayList<DrawerMenu>();
		mMenuImages.add(new DrawerMenu(DrawerMenu.DRAWER_MENU_LIST_TYPE_LOGO));
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_RESERVATION, DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuHotel);
		mMenuImages.add(menuBooking);
		mMenuImages.add(new DrawerMenu(DRAWER_MENU_SECTION_ACCOUNT, DrawerMenu.DRAWER_MENU_LIST_TYPE_SECTION));
		mMenuImages.add(menuCredit);
		mMenuImages.add(menuSetting);

		drawerMenuListAdapter = new DrawerMenuListAdapter(this,
				R.layout.drawer_list_item_entry, mMenuImages);

		// Set the adapter for the list view
		mDrawerList.setAdapter(drawerMenuListAdapter);
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(this);

		actionBar.setIcon(R.drawable.img_ic_menu);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
                                                                                              
		Fragment newContent = null;
		SharedPreferences.Editor ed = prefs.edit();

		disableAllButtons();
		mDrawerList.setSelection(position);
		drawerMenuListAdapter.notifyDataSetChanged();

		switch (((DrawerMenu) (adapterView.getAdapter().getItem(position)))
				.getIcon()) {
		case R.drawable.selector_drawermenu_todayshotel:
			newContent = new HotelListFragment();
			break;

		case R.drawable.selector_drawermenu_reservation:
			newContent = new BookingListFragment();
			break;

		case R.drawable.selector_drawermenu_saving:
			if (checkLogin()) // 로그인상태
				newContent = new CreditFragment();
			else
				// 로그아웃 상태
				newContent = new NoLoginFragment();
			break;

		case R.drawable.selector_drawermenu_setting:
			newContent = new SettingFragment();
			break;
		}

		if (newContent != null)
			switchFragment(newContent);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private void disableAllButtons() {
//		for (int i = 0; i < mMenuImages.size(); i++) {
//			mMenuImages.get(i).setBackground(0);
//		}
	}
	
	public void setGlobalFont(ViewGroup root) {
		if (BaseActivity.mTypefaceCommon == null) {
			BaseActivity.mTypefaceCommon = Typeface.createFromAsset(getAssets(),
					"NanumBarunGothic.ttf.mp3");
			
			BaseActivity.mTypefaceBold = Typeface.createFromAsset(getAssets(),
					"NanumBarunGothicBold.ttf.mp3");
		}

		int childCnt = root.getChildCount();
		for (int i = 0; i < childCnt; i++) {
			View v = root.getChildAt(i);
			Log.d(TAG, v.toString());
			if (v instanceof TextView) {
				((TextView) v).setTypeface(mTypefaceCommon);
			}
		}
	}

	private void switchFragment(Fragment fragment) {
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
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
