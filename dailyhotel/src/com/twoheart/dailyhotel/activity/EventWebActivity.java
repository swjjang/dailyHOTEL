package com.twoheart.dailyhotel.activity;

import android.os.Bundle;


import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class EventWebActivity extends WebViewActivity implements Constants{

	private static final String TAG = "EventWebActivity";
	private String URL_WEBAPI_EVENT; //= "http://event.dailyhotel.co.kr";
	private WebView web;

	@JavascriptInterface
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarHide();
		
		if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE) {
			URL_WEBAPI_EVENT = "http://event.dailyhotel.co.kr";
		} else {
			URL_WEBAPI_EVENT = "http://eventts.dailyhotel.co.kr"; //tStore
		}
		
		setContentView(R.layout.activity_event_web);

		web = (WebView) findViewById(R.id.webView);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// 탭의 정보를 변경하는 경우 바로 적용될 수 있도록
		// 웹뷰의 캐시 삭제 설정 
		web.clearCache(true);
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		setWebView(URL_WEBAPI_EVENT);
		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("event", "/todays-hotels/event");
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		
	}

}
