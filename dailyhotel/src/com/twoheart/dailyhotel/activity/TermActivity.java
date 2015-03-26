package com.twoheart.dailyhotel.activity;

import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class TermActivity extends WebViewActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setActionBar(R.string.actionbar_title_term_activity);
		setContentView(R.layout.activity_term);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setWebView(DailyHotelRequest.getUrlDecoderEx(URL_WEB_TERMS));
	}
}
