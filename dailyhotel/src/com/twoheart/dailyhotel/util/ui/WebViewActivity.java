package com.twoheart.dailyhotel.util.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.util.EncodingUtils;

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

/**
 * Created by manjonghan on 2014. 2. 14..
 */
public class WebViewActivity extends BaseActivity implements
		OnLongClickListener {

	protected DailyHotelWebChromeClient webChromeClient;
	protected DailyHotelWebViewClient webViewClient;

	protected WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);

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

				Uri uri = Uri.parse(URL_STORE_GOOGLE_DAILYHOTEL);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				
			} else if (url.equals("event://tstore")) {
				finish();
				overridePendingTransition(R.anim.hold, R.anim.slide_out_bottom);

				Uri uri = Uri.parse(URL_STORE_T_DAILYHOTEL);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				
			} else if (url.contains("facebook.com") | url.contains("naver.com")) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				
			} else {
				view.loadUrl(url);
			}
			return true;
		}

		@JavascriptInterface
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Toast.makeText(WebViewActivity.this,
					"네트워크 상태가 원활하지 않습니다. 잠시 후 다시 시도해 주세요.", Toast.LENGTH_LONG)
					.show();

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
		overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
		
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