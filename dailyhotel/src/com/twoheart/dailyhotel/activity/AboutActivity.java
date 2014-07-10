package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class AboutActivity extends WebViewActivity {
	
	private static final String TAG = "AboutActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_about_activity);
		setContentView(R.layout.activity_about);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		setWebView(URL_WEB_ABOUT);
	}
	
}
