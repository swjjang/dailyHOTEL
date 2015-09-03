package com.twoheart.dailyhotel.activity;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;

import android.os.Bundle;

public class AboutActivity extends WebViewActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		setActionBar(R.string.actionbar_title_about_activity);
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(this).recordScreen(Screen.ABOUT);

		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_ABOUT));
	}
}
