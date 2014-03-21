package com.twoheart.dailyhotel.activity;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class PrivacyActivity extends BaseActivity {
	
	private static final String TAG = "PersonalInfoActivity";
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setActionBar("개인정보취급방침");
		setContentView(R.layout.activity_personal_info);
		
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
