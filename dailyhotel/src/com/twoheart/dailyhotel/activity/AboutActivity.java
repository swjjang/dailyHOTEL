package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

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
