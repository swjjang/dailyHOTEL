package com.twoheart.dailyhotel.util.ui;

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
import com.twoheart.dailyhotel.util.Constants;

public class BaseActivity extends ActionBarActivity implements Constants {

	private final static String TAG = "BaseActivity";

	public ActionBar actionBar;
	public SharedPreferences sharedPreference;
	
	protected Fragment mFragment;
	protected DrawerMenu mMenuHotelListFragment;
	protected DrawerMenu mMenuBookingListFragment;
	protected DrawerMenu mMenuCreditFragment;
	protected DrawerMenu mMenuSettingFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreference = getSharedPreferences(NAME_DAILYHOTEL_SHARED_PREFERENCE, Context.MODE_PRIVATE);
	}
	
	public void setActionBar(String title) {
		actionBar = getSupportActionBar();
		
		try {
			actionBar.setBackgroundDrawable(Drawable.createFromXml(getResources(),
					getResources().getXml(R.drawable.dh_actionbar_background)));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		actionBar.setIcon(R.drawable.img_ic_menu);
		actionBar.setTitle(title);
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}
	
//	@Override
//	public void setContentView(int layoutResID) {
//		super.setContentView(layoutResID);
//		setGlobalFont((ViewGroup) this.getWindow().getDecorView().findViewById(
//				android.R.id.content));
//	}

//	public void setGlobalFont(ViewGroup root) {
//		if (BaseActivity.mTypefaceCommon == null) {
//			BaseActivity.mTypefaceCommon = Typeface.createFromAsset(getAssets(),
//					"NanumBarunGothic.ttf.mp3");
//			
//			BaseActivity.mTypefaceBold = Typeface.createFromAsset(getAssets(),
//					"NanumBarunGothicBold.ttf.mp3");
//		}
//
//		int childCnt = root.getChildCount();
//		for (int i = 0; i < childCnt; i++) {
//			View v = root.getChildAt(i);
//			Log.d(TAG, v.toString());
//			if (v instanceof TextView) {
//				((TextView) v).setTypeface(mTypefaceCommon);
//			}
//		}
//	}

}
