/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 *
 * PaymentActivity (결제화면)
 * 
 * 웹서버에서 이용하는 KCP 결제 모듈을 이용하는 화면이다. WebView를 이용
 * 해서 KCP 결제를 진행하는 웹서버 API에 POST 방식으로 요청한다. 요청 시
 * 요청 파라미터에 사용자 정보를 담는다. 이는 서버 사이드에서 Facbook 계정
 * 임인지를 확인하기 위해서이다.
 *
 * @since 2014-02-24
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 */
package com.twoheart.dailyhotel.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

@SuppressLint("NewApi")
public class RegisterCreditCardActivity extends BaseActivity implements Constants
{
	public static final int PROGRESS_STAT_NOT_START = 1;
	public static final int PROGRESS_STAT_IN = 2;
	public static final int PROGRESS_DONE = 3;
	public static String CARD_CD = "";
	public static String QUOTA = "";
	public int m_nStat = PROGRESS_STAT_NOT_START;

	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_regcreditcard);
		setActionBar(R.string.actionbar_title_reg_creditcard);

		webView = (WebView) findViewById(R.id.webView);

		// TODO  setWebContentsDebuggingEnabled
		//		WebView.setWebContentsDebuggingEnabled(true);

		webView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		webView.addJavascriptInterface(new JavaScriptExtention(), "android");

		webView.setWebChromeClient(new mWebChromeClient());
		webView.setWebViewClient(new mWebViewClient());

		webView.setOnLongClickListener(new OnLongClickListener()
		{
			@Override
			public boolean onLongClick(View v)
			{
				return true;
			}
		}); // 롱클릭 에러 방지.

		String url = DailyHotelRequest.getUrlDecoderEx(URL_DAILYHOTEL_SERVER) + DailyHotelRequest.getUrlDecoderEx(Constants.URL_REGISTER_CREDIT_CARD);

		webView.postUrl(url, null);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

	}

	@Override
	public void finish()
	{
		super.finish();

		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
	}

	private class mWebChromeClient extends WebChromeClient
	{

		boolean isActionBarProgressBarShowing = false;

		@Override
		public void onProgressChanged(WebView view, int newProgress)
		{
			super.onProgressChanged(view, newProgress);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			{
				if (newProgress != 100)
					setActionBarProgressBar(true);
				else
					setActionBarProgressBar(false);
			}
		}

		void setActionBarProgressBar(boolean show)
		{
			if (show != isActionBarProgressBarShowing)
			{
				setSupportProgressBarIndeterminateVisibility(show);
				isActionBarProgressBarShowing = show;
			}
		}
	}

	private class mWebViewClient extends WebViewClient
	{

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{

			return true;
		}

		// error 처리
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			super.onReceivedError(view, errorCode, description, failingUrl);
			webView.loadUrl("about:blank");
			if (VolleyHttpClient.isAvailableNetwork())
			{
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
			} else
			{
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR);
			}

			finish();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);

			lockUI();
			//			handler.removeCallbacks(networkCheckRunner); // 결제 완료시 항상 네트워크
			// 불안정뜨므로, 네트워크 체크는
			// 제거하도록 함.

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			super.onPageFinished(view, url);

			unLockUI();

			CookieSyncManager.getInstance().sync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				setSupportProgressBarIndeterminateVisibility(false);
		}
	}

	/**
	 * 종종 에러 발생.
	 * 
	 * @author jangjunho
	 *
	 */
	@Deprecated
	private class HtmlObserver
	{
		@JavascriptInterface
		public void showHTML(String html)
		{
			ExLog.e("WEB_VIEW : " + html);
		}
	}

	private class JavaScriptExtention
	{

		public JavaScriptExtention()
		{
		}

		@JavascriptInterface
		public void feed(String msg)
		{
			int resultCode = 0;
			Intent payData = new Intent();

			if ("PAYMENT_BILLING_SUCCSESS".equals(msg) == true)
			{
				resultCode = CODE_RESULT_PAYMENT_BILLING_SUCCSESS;
			} else if ("PAYMENT_BILLING_DUPLICATE".equals(msg) == true)
			{
				resultCode = CODE_RESULT_PAYMENT_BILLING_DUPLICATE;
			} else
			// else if ("PAYMENT_BILLING_FAIL".equals(msg) == true)
			{
				resultCode = CODE_RESULT_PAYMENT_BILLING_FAIL;

				String[] splits = msg.split("\\|");

				if (splits.length > 1)
				{
					if (TextUtils.isEmpty(splits[1]) == false)
					{
						payData.putExtra(NAME_INTENT_EXTRA_DATA_MESSAGE, splits[1]);
					}
				}
			}

			setResult(resultCode, payData);
			finish();
		}
	}

	@Override
	public void onBackPressed()
	{
		OnClickListener posListener = new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		};
		SimpleAlertDialog.build(RegisterCreditCardActivity.this, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_register_creditcard_cancel), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null).show();
	}
}
