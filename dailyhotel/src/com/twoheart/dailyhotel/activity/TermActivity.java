package com.twoheart.dailyhotel.activity;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.request.DailyHotelRequest;

import android.os.Bundle;

public class TermActivity extends WebViewActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_term);
		setActionBar(R.string.actionbar_title_term_activity);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_TERMS));
	}
}
