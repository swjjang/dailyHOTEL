package com.twoheart.dailyhotel;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {
	
	private final static String TAG = "BaseActivity";
	
	
	protected DailyMenuFragment dm_fragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		setBehindContentView(R.layout.menu_frame_left);
		
//		if (savedInstanceState == null) {
//			FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
//			dm_fragment = new DailyMenuFragment();
//			getSupportFragmentManager()
//			.beginTransaction()
//			.replace(R.id.menu_frame_left, dm_fragment)
//			.commit();
//		} else {
//			dm_fragment = (DailyMenuFragment)this.getSupportFragmentManager().findFragmentById(R.id.menu_frame_left);
//		}
		
		// customize the SlidingMenu
//		SlidingMenu sm = getSlidingMenu();
//		sm.setShadowWidthRes(R.dimen.shadow_width);
//		sm.setShadowDrawable(R.drawable.shadow);
//		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset_left);
//		sm.setFadeDegree(0.35f);
//		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		
		
	}
	
	public void changeMenu() {
		dm_fragment.changeMenu();
	}
}
