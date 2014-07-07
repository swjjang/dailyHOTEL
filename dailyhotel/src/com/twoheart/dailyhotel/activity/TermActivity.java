package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class TermActivity extends WebViewActivity {
	
	private static final String TAG = "AgreementActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBar("이용약관");
		setContentView(R.layout.activity_term);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setWebView(URL_WEB_TERMS);
	}
	
	
}
