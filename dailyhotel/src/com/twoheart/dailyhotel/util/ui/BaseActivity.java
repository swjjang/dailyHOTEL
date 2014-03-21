package com.twoheart.dailyhotel.util.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;

public class BaseActivity extends ActionBarActivity implements Constants {

	private final static String TAG = "BaseActivity";

	public ActionBar actionBar;
	public SharedPreferences sharedPreference;
	
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
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
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
