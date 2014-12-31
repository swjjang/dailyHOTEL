package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import android.webkit.JavascriptInterface;

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
		
		DailyHotel.getGaTracker().set(Fields.SCREEN_NAME, TAG);

		setContentView(R.layout.activity_event_web);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setWebView(URL_WEBAPI_EVENT);
		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("event", "/todays-hotels/event");
	}

	@Override
	protected void onStart() {
		super.onStart();
		DailyHotel.getGaTracker().send(MapBuilder.createAppView().build());
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
		
	}

}
