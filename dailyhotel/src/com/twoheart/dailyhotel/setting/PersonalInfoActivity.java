package com.twoheart.dailyhotel.setting;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;

public class PersonalInfoActivity extends ActionBarActivity {
	
	private static final String TAG = "PersonalInfoActivity";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_personal_info);
		
		// setTitle
		setTitle(Html.fromHtml("<font color='#050505'>개인정보 취급방침</font>"));
		// back arrow
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setIcon(R.drawable.dh_ic_menu_back);
		Drawable myDrawable;
		Resources res = getResources();
		try {
		   myDrawable = Drawable.createFromXml(res, res.getXml(R.drawable.dh_ic_home_72));
		   getSupportActionBar().setBackgroundDrawable(myDrawable);
		} catch (Exception ex) {
		   Log.e(TAG, "Exception loading drawable"); 
		}
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
}
