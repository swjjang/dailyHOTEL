/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * WebViewActivity
 * 
 * WebView를 사용하는 Activity를 위한 부모 클래스이다. 일괄적인 WebV
 * iew의 설정을 위해 설계된 클래스이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.util.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;

public class WebViewActivity extends BaseActivity implements
		OnLongClickListener {

	protected DailyHotelWebChromeClient webChromeClient;
	protected DailyHotelWebViewClient webViewClient;

	protected WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_PROGRESS);

	}

	@JavascriptInterface
	protected void setWebView(String url) {
		webChromeClient = new DailyHotelWebChromeClient();
		webViewClient = new DailyHotelWebViewClient();
		webView = (WebView) findViewById(R.id.webView);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVerticalScrollbarOverlay(true);
		webView.getSettings().setSupportZoom(false);
		webView.setOnLongClickListener(this);
		webView.setLongClickable(false);

		webView.setWebViewClient(webViewClient);
		webView.setWebChromeClient(webChromeClient);

		webView.loadUrl(url);

	}

	public class DailyHotelWebViewClient extends WebViewClient {

		@JavascriptInterface
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.equals("event://")) {
				finish();
				overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
				browseToExternalBrowser(URL_STORE_GOOGLE_DAILYHOTEL);

			} else if (url.equals("event://tstore")) {
				finish();
				overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);
				browseToExternalBrowser(URL_STORE_T_DAILYHOTEL);

			} else if (url.contains("facebook.com") | url.contains("naver.com")) {
				browseToExternalBrowser(url);

			} else {
				view.loadUrl(url);
			}
			return true;
		}
		
		private void browseToExternalBrowser(String url) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			
		}

		@JavascriptInterface
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			android.util.Log.e("ErrorCode / Desc / failingUrl",errorCode+" / "+description+" / "+failingUrl);
			showToast(getString(R.string.toast_msg_network_status_bad), Toast.LENGTH_LONG, false);
			finish();

		}

	}

	public class DailyHotelWebChromeClient extends WebChromeClient {

		@JavascriptInterface
		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final android.webkit.JsResult result) {
			new AlertDialog.Builder(view.getContext())
					.setTitle("알림")
					.setMessage(message)
					.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									result.confirm();
								}
							}).setCancelable(false).create().show();
			return true;
		};

		public void onProgressChanged(WebView view, int progress) {
			WebViewActivity.this.setProgress(progress * 100);
		}

	}

	@Override
	public boolean onLongClick(View v) {
		return true;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}