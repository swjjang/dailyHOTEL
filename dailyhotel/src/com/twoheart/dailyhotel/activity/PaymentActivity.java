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

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import kr.co.kcp.android.payment.standard.ResultRcvActivity;
import kr.co.kcp.util.PackageState;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Guest;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.RenewalGaManager;
import com.twoheart.dailyhotel.util.SimpleAlertDialog;
import com.twoheart.dailyhotel.util.network.VolleyHttpClient;
import com.twoheart.dailyhotel.util.network.request.DailyHotelRequest;
import com.twoheart.dailyhotel.util.ui.BaseActivity;
import com.twoheart.dailyhotel.widget.DailyToast;

@SuppressLint("NewApi")
public class PaymentActivity extends BaseActivity implements Constants
{

	public static final String TAG = "PaymentActivity";

	public static final int PROGRESS_STAT_NOT_START = 1;
	public static final int PROGRESS_STAT_IN = 2;
	public static final int PROGRESS_DONE = 3;
	public static String CARD_CD = "";
	public static String QUOTA = "";
	public int m_nStat = PROGRESS_STAT_NOT_START;

	private WebView webView;
	private final Handler handler = new Handler();

	private Pay mPay;

	// init paypal
	private static final int REQUEST_CODE_PAYMENT = 1;
	//	private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
	// note that these credentials will differ between live & sandbox
	// environments.
	private static final String CONFIG_CLIENT_ID = "AZlfxxBvLXC7iT3xDEG8oFViHYdqImvcwLB2JG6pyUhVAXb7XuHMYIuNutGI";

	//	public static PayPalConfiguration config = new PayPalConfiguration().environment(CONFIG_ENVIRONMENT).clientId(CONFIG_CLIENT_ID)
	//	// The following are only used in PayPalFuturePaymentActivity.
	//	.merchantName("Hipster Store").merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy")).merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));
	//
	//	private PayPalPayment getThingToBuy(String paymentIntent, Pay pay)
	//	{
	//		PayPalPayment payPalPayment = null;
	//
	//		if (pay != null && pay.getHotelDetail() != null && pay.getHotelDetail().getHotel() != null)
	//		{
	//
	//			payPalPayment = new PayPalPayment(new BigDecimal(pay.getPayPrice()), "USD", pay.getHotelDetail().getHotel().getName(), paymentIntent);
	//		} else
	//		{
	//
	//			payPalPayment = new PayPalPayment(new BigDecimal("1.75"), "USD", "hipster jeans", paymentIntent);
	//		}
	//
	//		return payPalPayment;
	//	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null)
		{
			mPay = (Pay) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_PAY);
		}

		//		if (mPay.getPayType().equals("PAYPAL")) {
		//			Intent intent = new Intent(this, PayPalService.class);
		//			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		//			startService(intent);
		//			
		//			PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE, mPay);
		//			/*
		//			 * See getStuffToBuy(..) for examples of some available payment
		//			 * options.
		//			 */
		//			intent = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);
		//
		//			// send the same configuration for restart resiliency
		//			intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
		//			intent.putExtra(
		//					com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,
		//					thingToBuy);
		//
		//			startActivityForResult(intent, REQUEST_CODE_PAYMENT);
		//			return;
		//		}

		webView = (WebView) findViewById(R.id.webView);

		// TODO  setWebContentsDebuggingEnabled
		//		WebView.setWebContentsDebuggingEnabled(true);

		webView.getSettings().setSavePassword(false);
		webView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
		{
			webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		webView.addJavascriptInterface(new KCPPayBridge(), "KCPPayApp");
		// 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
		webView.addJavascriptInterface(new KCPPayCardInfoBridge(), "KCPPayCardInfo");
		webView.addJavascriptInterface(new KCPPayPinInfoBridge(), "KCPPayPinInfo"); // 페이핀 기능 추가
		webView.addJavascriptInterface(new KCPPayPinReturn(), "KCPPayPinRet"); // 페이핀
		// 기능
		// 추가
		webView.addJavascriptInterface(new JavaScriptExtention(), "android");

		webView.addJavascriptInterface(new TeleditBridge(), "TeleditApp");

		// webView.addJavascriptInterface(new HtmlObserver(), "HtmlObserver");

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

		if (mPay == null)
		{
			DailyToast.showToast(PaymentActivity.this, R.string.toast_msg_failed_to_get_payment_info, Toast.LENGTH_SHORT);
			finish();
			return;
		}

		if (mPay.getSaleRoomInformation().saleIndex == 0)
		{
			// 세션이 만료되어 재시작 요청.
			restartApp();
			return;
		}

		if (mPay.getType() == Pay.Type.EASY_CARD)
		{
			finish();
			return;
		} else
		{
			Guest guest = mPay.getGuest();

			if (mPay.getPayPrice() == 0)
			{
				// 적립금으로만 결제하기 포스트
				String url = new StringBuilder(DailyHotelRequest.getUrlDecoderEx(URL_DAILYHOTEL_SERVER)).append(DailyHotelRequest.getUrlDecoderEx(URL_WEBAPI_RESERVE_PAYMENT_DISCOUNT)).append('/').append(mPay.getSaleRoomInformation().saleIndex).toString();

				// 적립금으로만 결제하는 경우 결제창할 필요 없음
				ArrayList<String> postParameterKey = new ArrayList<String>(Arrays.asList("saleIdx", "email", "name", "phone", "guest_name", "guest_phone", "guest_email"));
				ArrayList<String> postParameterValue = new ArrayList<String>(Arrays.asList(String.valueOf(mPay.getSaleRoomInformation().saleIndex), mPay.getCustomer().getEmail(), mPay.getCustomer().getName(), mPay.getCustomer().getPhone(), guest.name, guest.phone, guest.email));

				webView.postUrl(url, parsePostParameter(postParameterKey.toArray(new String[postParameterKey.size()]), postParameterValue.toArray(new String[postParameterValue.size()])));
			} else if (mPay.isSaleCredit() == true)
			{
				// 적립금 일부 사용
				String url = new StringBuilder(DailyHotelRequest.getUrlDecoderEx(URL_DAILYHOTEL_SERVER)).append(DailyHotelRequest.getUrlDecoderEx(URL_WEBAPI_RESERVE_PAYMENT_DISCOUNT)).append('/').append(mPay.getType().name()).append("/").append(mPay.getSaleRoomInformation().saleIndex).append("/").append(mPay.getCredit().getBonus()).toString();

				ArrayList<String> postParameterKey = new ArrayList<String>(Arrays.asList("guest_name", "guest_phone", "guest_email"));
				ArrayList<String> postParameterValue = new ArrayList<String>(Arrays.asList(guest.name, guest.phone, guest.email));

				webView.postUrl(url, parsePostParameter(postParameterKey.toArray(new String[postParameterKey.size()]), postParameterValue.toArray(new String[postParameterValue.size()])));
			} else
			{
				String url = new StringBuilder(DailyHotelRequest.getUrlDecoderEx(URL_DAILYHOTEL_SERVER)).append(DailyHotelRequest.getUrlDecoderEx(URL_WEBAPI_RESERVE_PAYMENT)).append('/').append(mPay.getType().name()).append("/").append(mPay.getSaleRoomInformation().saleIndex).toString();

				ArrayList<String> postParameterKey = new ArrayList<String>(Arrays.asList("guest_name", "guest_phone", "guest_email"));
				ArrayList<String> postParameterValue = new ArrayList<String>(Arrays.asList(guest.name, guest.phone, guest.email));

				webView.postUrl(url, parsePostParameter(postParameterKey.toArray(new String[postParameterKey.size()]), postParameterValue.toArray(new String[postParameterValue.size()])));
			}
		}
	}

	@Override
	protected void onResume()
	{
		String region = sharedPreference.getString(KEY_PREFERENCE_REGION_SELECT_GA, null);
		String hotelName = sharedPreference.getString(KEY_PREFERENCE_HOTEL_NAME_GA, null);

		RenewalGaManager.getInstance(getApplicationContext()).recordScreen("paymentWeb", "/todays-hotels/" + region + "/" + hotelName + "/booking-detail/payment-web");

		super.onResume();
	}

	private byte[] parsePostParameter(String[] key, String[] value)
	{

		List<byte[]> resultList = new ArrayList<byte[]>();
		HashMap<String, byte[]> postParameters = new HashMap<String, byte[]>();

		if (key.length != value.length)
			throw new IllegalArgumentException("The length of the key arguments and " + "the length of the value arguments must be same.");

		for (int i = 0; i < key.length; i++)
		{
			postParameters.put(key[i], EncodingUtils.getBytes(value[i], "BASE64"));
		}

		for (int i = 0; i < postParameters.size(); i++)
		{

			if (resultList.size() != 0)
				resultList.add("&".getBytes());

			resultList.add(key[i].getBytes());
			resultList.add("=".getBytes());
			resultList.add(postParameters.get(key[i]));
		}

		int size = 0;
		int[] sizeOfResult = new int[resultList.size()];

		for (int i = 0; i < resultList.size(); i++)
			sizeOfResult[i] = resultList.get(i).length;
		for (int i = 0; i < sizeOfResult.length; i++)
			size += sizeOfResult[i];

		byte[] result = new byte[size];

		int currentSize = 0;

		for (int i = 0; i < resultList.size(); i++)
		{
			System.arraycopy(resultList.get(i), 0, result, currentSize, resultList.get(i).length);
			currentSize += resultList.get(i).length;
		}

		return result;
	}

	@JavascriptInterface
	private boolean url_scheme_intent(WebView view, String url)
	{
		ExLog.d("[PayDemoActivity] called__test - url=[" + url + "]");

		// chrome 버젼 방식 : 2014.01 추가
		if (url.startsWith("intent"))
		{
			// ILK 용
			if (url.contains("com.lotte.lottesmartpay"))
			{
				try
				{
					startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
				} catch (URISyntaxException e)
				{
					ExLog.d("[PayDemoActivity] URISyntaxException=[" + e.getMessage() + "]");

					return false;
				} catch (ActivityNotFoundException e)
				{
					ExLog.d("[PayDemoActivity] ActivityNotFoundException=[" + e.getMessage() + "]");

					return false;
				}
			}
			// ILK 용
			else if (url.contains("com.ahnlab.v3mobileplus"))
			{
				try
				{
					view.getContext().startActivity(Intent.parseUri(url, 0));
				} catch (URISyntaxException e)
				{
					ExLog.d("[PayDemoActivity] URISyntaxException=[" + e.getMessage() + "]");

					return false;
				} catch (ActivityNotFoundException e)
				{
					ExLog.d("[PayDemoActivity] ActivityNotFoundException=[" + e.getMessage() + "]");

					return false;
				}
			}
			// 폴라리스 용
			else
			{
				Intent intent = null;

				try
				{
					intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
				} catch (URISyntaxException ex)
				{
					ExLog.d("[PayDemoActivity] URISyntaxException=[" + ex.getMessage() + "]");

					return false;
				}

				// 앱설치 체크를 합니다.
				if (getPackageManager().resolveActivity(intent, 0) == null)
				{
					String packagename = intent.getPackage();

					if (packagename != null)
					{
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:" + packagename)));
						return true;
					}
				}

				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()));

				try
				{
					startActivity(intent);
				} catch (ActivityNotFoundException e)
				{
					ExLog.d("[PayDemoActivity] ActivityNotFoundException=[" + e.getMessage() + "]");

					return false;
				}
			}
		}

		// 기존 방식
		else
		{

			if (url.startsWith("ispmobile"))
			{ // 7.4 ISP 모듈 연동 테스트
				if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_ISP))
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_ISP)));
					view.goBack();
					return true;
				}
			} else if (url.startsWith("kftc-bankpay"))
			{ // 7.9 이니시스 모듈 연동 테스트
				if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_KFTC))
				{
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_KFTC)));
					view.goBack();
					return true;
				}
			} else if (url.startsWith("mpocket.online.ansimclick"))
			{
				if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_MPOCKET))
				{
					DailyToast.showToast(PaymentActivity.this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_MPOCKET)));
					return true;
				}
			}

			// 결제 모듈 실행.
			try
			{
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);

				int requestCode = 0;
				if (url.startsWith("kftc-bankpay"))
					requestCode = CODE_REQUEST_KFTC_BANKPAY;
				else if (url.startsWith("ispmobile"))
					requestCode = CODE_REQUEST_ISPMOBILE;

				startActivityForResult(intent, requestCode);
			} catch (ActivityNotFoundException e)
			{
				return true;
			}
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		//		if(mPay.getPayType().equals("PAYPAL")){
		//			
		//			proccessPayPalActivityResult(requestCode, resultCode, intent);
		//		} else {

		String scriptForSkip = "javascript:";
		if (requestCode == CODE_REQUEST_ISPMOBILE)
			scriptForSkip += "submitIspAuthInfo('RUNSCHEME');"; // ISP 확인 버튼 콜
		else if (requestCode == CODE_REQUEST_KFTC_BANKPAY)
			scriptForSkip += "returnUrltoMall();"; // KTFC 확인 버튼 콜
		webView.loadUrl(scriptForSkip);
		//		}
	}

	//	private void proccessPayPalActivityResult(int requestCode, int resultCode, Intent data)
	//	{
	//
	//		JavaScriptExtention javaScriptExtention = new JavaScriptExtention();
	//
	//		if (resultCode == Activity.RESULT_OK)
	//		{
	//			PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
	//			if (confirm != null)
	//			{
	//				try
	//				{
	//					ExLog.d(confirm.toJSONObject().toString(4));
	//					ExLog.d(confirm.getPayment().toJSONObject().toString(4));
	//
	//					// 성공적으로 마쳤을 경우 
	//					javaScriptExtention.feed("SUCCESS");
	//				} catch (JSONException e)
	//				{
	//					ExLog.d("an extremely unlikely failure occurred: ");
	//					javaScriptExtention.feed("NOT_AVAILABLE");
	//				}
	//			}
	//		} else if (resultCode == Activity.RESULT_CANCELED)
	//		{
	//			ExLog.d("The user canceled.");
	//			javaScriptExtention.feed("PAYMENT_CANCELED");
	//
	//		} else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID)
	//		{
	//			ExLog.d("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
	//			javaScriptExtention.feed("NOT_AVAILABLE");
	//		}
	//	}

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
			ExLog.d("[PayDemoActivity] called__shouldOverrideUrlLoading - url=[" + url + "]");

			if (url != null && !url.equals("about:blank"))
			{

				if (url.startsWith("http://") || url.startsWith("https://"))
				{
					if (url.contains("http://market.android.com") || url.contains("http://m.ahnlab.com/kr/site/download") || url.endsWith(".apk"))
					{
						return url_scheme_intent(view, url);
					} else
					{
						view.loadUrl(url);
						return false;
					}
				} else if (url.startsWith("mailto:"))
				{
					return false;
				} else if (url.startsWith("tel:"))
				{
					return false;
				} else
				{
					return url_scheme_intent(view, url);
				}
			}
			// else if isp판단 , 없으면 isp 설치페이지로 이동.

			return true;
		}

		// error 처리
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			super.onReceivedError(view, errorCode, description, failingUrl);
			ExLog.e("ErrorCode / Description / failingUrl : " + errorCode + " / " + description + " / " + failingUrl);
			webView.loadUrl("about:blank");
			if (VolleyHttpClient.isAvailableNetwork())
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
			else
				setResult(CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR);
			finish();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			super.onPageStarted(view, url, favicon);

			if (mPay.getType() == Pay.Type.EASY_CARD)
			{
				return;
			}

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

			if (mPay.getType() == Pay.Type.EASY_CARD)
			{
				return;
			}

			unLockUI();
			// view.loadUrl("javascript:window.HtmlObserver.showHTML" +
			// "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

			//			if (mPay.getType() == Pay.Type.PAYPAL)
			//			{
			//
			//				view.loadUrl("javascript:function on_cancel()" + "{ " + "var form = document.pmnt_info_form_2;" + "form.action = '/smart//etc/pay_cancel.php';" + "form.submit();" + "}");
			//				view.loadUrl("javascript:(function(){" + "var payImg = (document.getElementsByClassName('space_h_auto'))[0];" + "payImg.style.cssText = payImg.style.cssText + ';background-image: url(https://www.paypalobjects.com/webstatic/en_KR/mktg/Logo/pp_cc_mark_74x46.jpg);' +" + "'background-size: 150px;' +" + "'background-repeat: no-repeat;' +" + "'background-position: center;';" + "})();");
			//			}

			CookieSyncManager.getInstance().sync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				setSupportProgressBarIndeterminateVisibility(false);
		}

	}

	/**
	 * 다날 모바일 결제 관련 브릿지.
	 * 
	 * @author jangjunho
	 *
	 */
	private class TeleditBridge
	{
		/**
		 * 
		 * @param val
		 *            휴대폰 결제 완료 후 결과값.
		 */
		@JavascriptInterface
		public void Result(final String val)
		{
			ExLog.e("RES : " + val);
			setResult(CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE);
			finish();
		}

		/**
		 * web에서 닫기를 콜 했을때 호출.
		 */
		@JavascriptInterface
		public void BestClose()
		{
			setResult(CODE_RESULT_ACTIVITY_PAYMENT_CANCELED);
			finish();
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

	private class KCPPayPinReturn
	{
		@JavascriptInterface
		public String getConfirm()
		{
			if (ResultRcvActivity.b_type)
			{// ResultRcvActivity.b_type
				ResultRcvActivity.b_type = false;
				return "true";
			} else
			{
				return "false";
			}
		}
	}

	private class KCPPayPinInfoBridge
	{
		@JavascriptInterface
		public void getPaypinInfo(final String url)
		{
			handler.post(new Runnable()
			{
				public void run()
				{
					ExLog.d("[PayDemoActivity] KCPPayPinInfoBridge=[getPaypinInfo]");

					PackageState ps = new PackageState(PaymentActivity.this);

					if (!ps.getPackageAllInstallState("com.skp.android.paypin"))
						paypinConfim();
					else
						url_scheme_intent(null, url);
				}
			});
		}

		@JavascriptInterface
		private void paypinConfim()
		{
			if (isFinishing() == true)
			{
				return;
			}

			OnClickListener posListener = new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();

					if (!url_scheme_intent(null, "tstore://PRODUCT_VIEW/0000284061/0"))
					{
						url_scheme_intent(null, "market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ.k");
					}
				}
			};

			OnClickListener negaListener = new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
					DailyToast.showToast(PaymentActivity.this, R.string.toast_msg_cancel_payment, Toast.LENGTH_SHORT);
				}
			};

			SimpleAlertDialog.build(PaymentActivity.this, getString(R.string.dialog_btn_text_confirm), getString(R.string.dialog_msg_install_paypin), getString(R.string.dialog_btn_text_install), getString(R.string.dialog_btn_text_cancel), posListener, negaListener).show();
		}
	}

	// 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
	private class KCPPayCardInfoBridge
	{
		@JavascriptInterface
		public void getCardInfo(final String card_cd, final String quota)
		{
			handler.post(new Runnable()
			{
				public void run()
				{
					ExLog.d("[PayDemoActivity] KCPPayCardInfoBridge=[" + card_cd + ", " + quota + "]");

					CARD_CD = card_cd;
					QUOTA = quota;

					PackageState ps = new PackageState(PaymentActivity.this);

					if (!ps.getPackageDownloadInstallState("com.skt.at"))
						alertToNext();
				}
			});
		}

		@JavascriptInterface
		private void alertToNext()
		{
			if (isFinishing() == true)
			{
				return;
			}

			OnClickListener posListener = new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();

					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cert.hanaskcard.com/Ansim/HanaSKPay.apk"));

					m_nStat = PROGRESS_STAT_IN;

					startActivity(intent);
				}
			};

			SimpleAlertDialog.build(PaymentActivity.this, getString(R.string.dialog_notice2), getString(R.string.dialog_msg_install_hana_sk), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null).show();
		}
	}

	private class KCPPayBridge
	{
		@JavascriptInterface
		public void launchMISP(final String arg)
		{
			handler.post(new Runnable()
			{
				public void run()
				{
					boolean isp_app = true;
					String strUrl;
					String argUrl;

					PackageState ps = new PackageState(PaymentActivity.this);

					argUrl = arg;

					if (!arg.equals("Install") && !ps.getPackageDownloadInstallState("kvp.jjy.MispAndroid"))
						argUrl = "Install";

					strUrl = (argUrl.equals("Install") == true) ? "market://details?id=kvp.jjy.MispAndroid320" : argUrl;

					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));

					m_nStat = PROGRESS_STAT_IN;
					ExLog.d("m_nStat : " + Integer.toString(m_nStat));
					startActivity(intent);
				}
			});
		}
	}

	@Override
	protected void onRestart()
	{
		super.onRestart();

		ExLog.d("[PayDemoActivity] called__onResume + INPROGRESS=[" + m_nStat + "]");

		// 하나 SK 모듈로 결제 이후 해당 카드 정보를 가지고 오기위해 사용
		if (ResultRcvActivity.m_uriResult != null)
		{// ResultRcvActivity
			if (ResultRcvActivity.m_uriResult.getQueryParameter("realPan") != null && ResultRcvActivity.m_uriResult.getQueryParameter("cavv") != null && ResultRcvActivity.m_uriResult.getQueryParameter("xid") != null && ResultRcvActivity.m_uriResult.getQueryParameter("eci") != null)
			{
				ExLog.d("[PayDemoActivity] HANA SK Result = javascript:hanaSK('" + ResultRcvActivity.m_uriResult.getQueryParameter("realPan") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("cavv") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("xid") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("eci") + "', '" + CARD_CD + "', '" + QUOTA + "');");

				// 하나 SK 모듈로 인증 이후 승인을 하기위해 결제 함수를 호출 (주문자 페이지)
				webView.loadUrl("javascript:hanaSK('" + ResultRcvActivity.m_uriResult.getQueryParameter("realPan") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("cavv") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("xid") + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("eci") + "', '" + CARD_CD + "', '" + QUOTA + "');");
			}

			if ((ResultRcvActivity.m_uriResult.getQueryParameter("res_cd") == null ? "" : ResultRcvActivity.m_uriResult.getQueryParameter("res_cd")).equals("999"))
			{
				ExLog.d("[PayDemoActivity] HANA SK Result = cancel");

				m_nStat = 9;
			}

			if ((ResultRcvActivity.m_uriResult.getQueryParameter("isp_res_cd") == null ? "" : ResultRcvActivity.m_uriResult.getQueryParameter("isp_res_cd")).equals("0000"))
			{
				ExLog.d("[PayDemoActivity] ISP Result = 0000");

				webView.loadUrl("http://pggw.kcp.co.kr/lds/smart_phone_linux_jsp/sample/card/samrt_res.jsp?result=OK&a=" + ResultRcvActivity.m_uriResult.getQueryParameter("a"));
			} else
			{
				ExLog.d("[PayDemoActivity] ISP Result = cancel");
			}
		}

		if (m_nStat == PROGRESS_STAT_IN)
			checkFrom();
		ResultRcvActivity.m_uriResult = null;
	}

	@JavascriptInterface
	public void checkFrom()
	{
		try
		{

			if (ResultRcvActivity.m_uriResult != null)
			{
				m_nStat = PROGRESS_DONE;
				String strResultInfo = ResultRcvActivity.m_uriResult.getQueryParameter("approval_key");

				if (strResultInfo == null || strResultInfo.length() <= 4)
				{
					finishActivity(getString(R.string.act_payment_isp_error));
					return;
				}

				String strResCD = strResultInfo.substring(strResultInfo.length() - 4);

				ExLog.d("[PayDemoActivity] result=[" + strResultInfo + "]+" + "res_cd=[" + strResCD + "]");

				if (strResCD.equals("0000") == true)
				{

					String strApprovalKey = "";

					strApprovalKey = strResultInfo.substring(0, strResultInfo.length() - 4);

					ExLog.d("[PayDemoActivity] approval_key=[" + strApprovalKey + "]");

					webView.loadUrl("https://pggw.kcp.co.kr/app.do?ActionResult=app&approval_key=" + strApprovalKey);

				} else if (strResCD.equals("3001") == true)
				{
					finishActivity(getString(R.string.act_payment_isp_user_cancel));
				} else
				{
					finishActivity(getString(R.string.act_payment_isp_other_error));
				}
			}
		} catch (Exception e)
		{
			ExLog.e(e.toString());
		}
	}

	@Override
	@JavascriptInterface
	protected Dialog onCreateDialog(int id)
	{

		super.onCreateDialog(id);

		OnClickListener posListener = new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				dialog.dismiss();
				finishActivity(getString(R.string.act_payment_user_cancel));
			}
		};

		AlertDialog alertDlg = SimpleAlertDialog.build(PaymentActivity.this, getString(R.string.dialog_btn_text_cancel), getString(R.string.dialog_msg_chk_cancel_payment_progress), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null).create();

		return alertDlg;
	}

	@JavascriptInterface
	public void finishActivity(String p_strFinishMsg)
	{

		int resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;

		if (p_strFinishMsg != null)
		{
			if (p_strFinishMsg.equals("NOT_AVAILABLE"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE;
			else if (p_strFinishMsg.contains(getString(R.string.act_payment_chk_contain)))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCELED;// RESULT_CANCELED
		}
		Intent payData = new Intent();
		payData.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

		setResult(resultCode, payData);
		finish();
	}

	@Override
	public void finish()
	{
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

	}

	private class JavaScriptExtention
	{

		JavaScriptExtention()
		{
		}

		// 서버로부터 받은 결제 결과 메시지를 처리함.
		// 각각의 경우에 맞는 resultCode를 넣어 BookingActivity로 finish시킴.
		@JavascriptInterface
		public void feed(final String msg)
		{
			int resultCode = 0;
			ExLog.e("FEED : " + msg);
			if (msg == null)
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;
			else if (msg.equals("SUCCESS"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS;
			else if (msg.equals("INVALID_SESSION"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION;
			else if (msg.equals("SOLD_OUT"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT;
			else if (msg.equals("PAYMENT_COMPLETE"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE;
			else if (msg.equals("INVALID_DATE"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE;
			else if (msg.equals("PAYMENT_CANCELED"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCELED;
			else if (msg.equals("ACCOUNT_READY"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY;
			else if (msg.equals("ACCOUNT_TIME_ERROR"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_TIME_ERROR;
			else if (msg.equals("ACCOUNT_DUPLICATE"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_DUPLICATE;
			else if (msg.equals("NOT_AVAILABLE"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE;
			else if (msg.equals("PAYMENT_TIMEOVER"))
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER;
			else
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;

			Intent payData = new Intent();
			payData.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

			setResult(resultCode, payData);
			finish();
		}
	}

	@Override
	public void onBackPressed()
	{
		if (mPay.getType() == Pay.Type.EASY_CARD)
		{
			return;
		}

		if (isFinishing() == true)
		{
			return;
		}

		OnClickListener posListener = new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				finish();
			}
		};

		SimpleAlertDialog.build(PaymentActivity.this, getString(R.string.dialog_title_payment), getString(R.string.dialog_msg_chk_cancel_payment), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null).show();
	}

}
