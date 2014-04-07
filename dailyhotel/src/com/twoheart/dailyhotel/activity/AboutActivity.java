package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.view.MenuItem;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class AboutActivity extends WebViewActivity {
	
	private static final String TAG = "AboutActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("데일리호텔이란?");
		setContentView(R.layout.activity_about);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		setWebView(URL_WEB_ABOUT);
	}
	
}
