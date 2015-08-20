package com.twoheart.dailyhotel.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Event;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

public class EventWebActivity extends WebViewActivity implements Constants
{
	private String URL_WEBAPI_EVENT;
	private WebView mWebView;

	@JavascriptInterface
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();

		Event event = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_EVENT);

		if (event == null)
		{
			return;
		}

		if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
		{
			URL_WEBAPI_EVENT = event.googleStoreUrl;
		} else
		{
			URL_WEBAPI_EVENT = event.tStoreUrl;
		}

		setContentView(R.layout.activity_event_web);
		setActionBar(R.string.actionbar_title_event_list_frag);

		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			cookieManager.setAcceptThirdPartyCookies(mWebView, true);
		}

		// 추가
		mWebView.addJavascriptInterface(new JavaScriptExtention(), "android");
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.EVENT_WEB);
		super.onStart();
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		mWebView.clearCache(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		setWebView(URL_WEBAPI_EVENT);
	}

	/**
	 * JavaScript
	 * 
	 * @author Dailier
	 *
	 */
	private class JavaScriptExtention
	{
		@JavascriptInterface
		public void log(String index, String params)
		{
		}

		@JavascriptInterface
		public void externalLink(String packageName, String uri)
		{
			Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
			marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

			if (marketLaunch.resolveActivity(getPackageManager()) == null)
			{
				String marketUrl = String.format("https://play.google.com/store/apps/details?id=%s", packageName);
				marketLaunch.setData(Uri.parse(marketUrl));
			}

			startActivity(marketLaunch);
		}

		@JavascriptInterface
		public void interlLink(String uri)
		{
			Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
			marketLaunch.setData(Uri.parse(Util.storeReleaseAddress()));

			if (marketLaunch.resolveActivity(getPackageManager()) == null)
			{
				marketLaunch.setData(Uri.parse(Constants.URL_STORE_GOOGLE_DAILYHOTEL_WEB));
			}

			startActivity(marketLaunch);
		}

		@JavascriptInterface
		public void finishPopup(String message)
		{

		}
	}
}
