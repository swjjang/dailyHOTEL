package com.twoheart.dailyhotel.activity;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.ui.WebViewActivity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

		String url = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);

		if (Util.isTextEmpty(url) == true)
		{
			finish();
			return;
		}

		URL_WEBAPI_EVENT = url;

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
		mWebView.clearCache(true);
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		setWebView(URL_WEBAPI_EVENT);
	}

	@Override
	protected void onStart()
	{
		AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.EVENT_WEB);
		super.onStart();
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
		public void externalLink(String packageName, String uri)
		{
			Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
			marketLaunch.setData(Uri.parse(uri));

			if (marketLaunch.resolveActivity(getPackageManager()) == null)
			{
				String marketUrl;

				if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
				{
					marketUrl = String.format("https://play.google.com/store/apps/details?id=%s", packageName);
					marketLaunch.setData(Uri.parse(marketUrl));
				}
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
		public void feed(String message)
		{
			if (isFinishing() == true)
			{
				return;
			}

			ExLog.d("message : " + message);
			showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), new OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			});
		}
	}
}
