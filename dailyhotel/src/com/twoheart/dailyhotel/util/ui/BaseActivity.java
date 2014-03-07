package com.twoheart.dailyhotel.util.ui;

import static com.twoheart.dailyhotel.util.AppConstants.SHARED_PREFERENCES_NAME;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.adapter.DrawerMenuListAdapter;
import com.twoheart.dailyhotel.obj.DrawerMenu;

public class BaseActivity extends ActionBarActivity {

	private final static String TAG = "BaseActivity";

	private static final String DRAWER_MENU_SECTION_RESERVATION = "예약";
	private static final String DRAWER_MENU_ENTRY_HOTEL = "오늘의 호텔";
	private static final String DRAWER_MENU_ENTRY_BOOKING = "예약확인";
	private static final String DRAWER_MENU_SECTION_ACCOUNT = "계정";
	private static final String DRAWER_MENU_ENTRY_CREDIT = "적립금";
	private static final String DRAWER_MENU_ENTRY_SETTING = "설정";

	public ActionBar actionBar;
	protected List<DrawerMenu> mMenuImages;
	protected DrawerLayout mDrawerLayout;
	protected DrawerMenuListAdapter drawerMenuListAdapter;
	protected ListView mDrawerList;
	protected ActionBarDrawerToggle mDrawerToggle;

	protected SharedPreferences prefs;
	protected Fragment content;

	protected DrawerMenu menuHotel;
	protected DrawerMenu menuBooking;
	protected DrawerMenu menuCredit;
	protected DrawerMenu menuSetting;
	
	private boolean isMenuItem = false;
	private String itemStr = null;

	public static Typeface mTypefaceCommon;
	public static Typeface mTypefaceBold;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
	}
	
	public void setActionBar(boolean isShowTitle) {
		actionBar = getSupportActionBar();
		
		try {
			actionBar.setBackgroundDrawable(Drawable.createFromXml(getResources(),
					getResources().getXml(R.drawable.dh_actionbar_background)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		actionBar.setDisplayShowTitleEnabled(isShowTitle);
		if (isShowTitle)
			actionBar.setIcon(R.drawable.img_ic_menu);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
	public void changeTitle(String str) throws NoActionBarException {
		
		if (actionBar == null)
			throw new NoActionBarException(actionBar);
		else  {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(str);
		}
		
	}
	
	public void setNavigationDrawer(OnItemClickListener listener) {
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
		mDrawerList.setOnItemClickListener(listener);

		setActionBar(true);
		
	}
	
	public void addMenuItem(String str) {
		isMenuItem = true;
		itemStr = str;
		supportInvalidateOptionsMenu();
	}

	public void hideMenuItem() {
		isMenuItem = false;
		supportInvalidateOptionsMenu();
	}
	
//	@Override
//	public void setContentView(int layoutResID) {
//		super.setContentView(layoutResID);
//		setGlobalFont((ViewGroup) this.getWindow().getDecorView().findViewById(
//				android.R.id.content));
//	}

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

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		if (mDrawerToggle != null)
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		
		if (mDrawerToggle != null)
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

}
