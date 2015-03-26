package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class AboutActivity extends WebViewActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_about_activity);
		setContentView(R.layout.activity_about);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_ABOUT));

		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("introduction", "/settings/introduction");
	}
}
