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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Pay;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Log;
import com.twoheart.dailyhotel.util.ui.BaseActivity;

public class PaymentActivity extends BaseActivity implements Constants {

	public static final int PROGRESS_STAT_NOT_START = 1;
	public static final int PROGRESS_STAT_IN = 2;
	public static final int PROGRESS_DONE = 3;
	public static String CARD_CD = "";
	public static String QUOTA = "";
	public int m_nStat = PROGRESS_STAT_NOT_START;

	private WebView webView;
	private final Handler handler = new Handler();

	private Pay mPay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setActionBarProgressBar();
		setActionBar("결제하기");
		setContentView(R.layout.activity_payment);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mPay = (Pay) bundle.getParcelable(NAME_INTENT_EXTRA_DATA_PAY);
		}

		webView = (WebView) findViewById(R.id.webView);
		webView.getSettings().setSavePassword(false);
		webView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.addJavascriptInterface(new KCPPayBridge(), "KCPPayApp");
		// 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
		webView.addJavascriptInterface(new KCPPayCardInfoBridge(),
				"KCPPayCardInfo");
		webView.addJavascriptInterface(new KCPPayPinInfoBridge(),
				"KCPPayPinInfo"); // 페이핀 기능 추가
		webView.addJavascriptInterface(new KCPPayPinReturn(), "KCPPayPinRet"); // 페이핀
		// 기능
		// 추가
		webView.addJavascriptInterface(new JavaScriptExtention(), "android");
		webView.setWebChromeClient(new mWebChromeClient());
		webView.setWebViewClient(new mWebViewClient());

		if (mPay == null) {
			showToast("결제 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT, false);
			finish();
		}
		
		String userAccessToken = mPay.getCustomer().getAccessToken();

		ArrayList<String> postParameterKey = new ArrayList<String>(Arrays.asList("email", "name", "phone"));
		ArrayList<String> postParameterValue = new ArrayList<String>(Arrays.asList(mPay.getCustomer().getEmail(),
				mPay.getCustomer().getName(),
				mPay.getCustomer().getPhone()));

		if ((userAccessToken != null)) {
			if ((userAccessToken.equals("")) || !(userAccessToken.equals("null"))) {
				postParameterKey.add("accessToken");
				postParameterValue.add(userAccessToken);
			}
		}

		String url = new StringBuilder(URL_DAILYHOTEL_SERVER)
		.append(URL_WEBAPI_RESERVE_PAYMENT)
		.append(mPay.getHotelDetail().getSaleIdx()).toString();

		if (mPay.isSaleCredit()) {
			url = new StringBuilder(URL_DAILYHOTEL_SERVER)
			.append(URL_WEBAPI_RESERVE_PAYMENT_DISCOUNT)
			.append(mPay.getHotelDetail().getSaleIdx()).append("/")
			.append(mPay.getCredit().getBonus()).toString();

		}

		webView.postUrl(url,
				parsePostParameter(postParameterKey.toArray(new String[postParameterKey.size()]),
						postParameterValue.toArray(new String[postParameterValue.size()])));

	}

	private byte[] parsePostParameter(String[] key, String[] value) {

		List<byte[]> resultList = new ArrayList<byte[]>();
		HashMap<String, byte[]> postParameters = new HashMap<String, byte[]>();

		if (key.length != value.length)
			throw new IllegalArgumentException(
					"The length of the key arguments and "
							+ "the length of the value arguments must be same.");

		for (int i = 0; i < key.length; i++)
			postParameters.put(key[i],
					EncodingUtils.getBytes(value[i], "BASE64"));

		for (int i = 0; i < postParameters.size(); i++) {

			if (resultList.size() != 0)
				resultList.add("&".getBytes());

			resultList.add(key[i].getBytes());
			resultList.add("=".getBytes());
			resultList.add(postParameters.get(key[i]));
		}

		int size = 0;
		int[] sizeOfResult = new int[resultList.size()];

		for (int i = 0; i < resultList.size(); i++) {
			sizeOfResult[i] = resultList.get(i).length;

		}

		for (int i = 0; i < sizeOfResult.length; i++) {
			size += sizeOfResult[i];

		}

		byte[] result = new byte[size];

		int currentSize = 0;
		for (int i = 0; i < resultList.size(); i++) {

			System.arraycopy(resultList.get(i), 0, result, currentSize,
					resultList.get(i).length);

			currentSize += resultList.get(i).length;

		}

		return result;
	}

	@JavascriptInterface
	private boolean url_scheme_intent(WebView view, String url) {
		Log.d(ResultRcvActivity.m_strLogTag,
				"[PayDemoActivity] called__test - url=[" + url + "]");

		// chrome 버젼 방식 : 2014.01 추가
		if (url.startsWith("intent")) {
			// ILK 용
			if (url.contains("com.lotte.lottesmartpay")) {
				try {
					startActivity(Intent
							.parseUri(url, Intent.URI_INTENT_SCHEME));
				} catch (URISyntaxException e) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] URISyntaxException=["
									+ e.getMessage() + "]");
					return false;
				} catch (ActivityNotFoundException e) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] ActivityNotFoundException=["
									+ e.getMessage() + "]");
					return false;
				}
			}
			// ILK 용
			else if (url.contains("com.ahnlab.v3mobileplus")) {
				try {
					view.getContext().startActivity(Intent.parseUri(url, 0));
				} catch (URISyntaxException e) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] URISyntaxException=["
									+ e.getMessage() + "]");
					return false;
				} catch (ActivityNotFoundException e) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] ActivityNotFoundException=["
									+ e.getMessage() + "]");
					return false;
				}
			}
			// 폴라리스 용
			else {
				Intent intent = null;

				try {
					intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
				} catch (URISyntaxException ex) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] URISyntaxException=["
									+ ex.getMessage() + "]");
					return false;
				}

				// 앱설치 체크를 합니다.
				if (getPackageManager().resolveActivity(intent, 0) == null) {
					String packagename = intent.getPackage();

					if (packagename != null) {
						startActivity(new Intent(Intent.ACTION_VIEW,
								Uri.parse("market://search?q=pname:"
										+ packagename)));

						return true;
					}
				}

				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent
						.getDataString()));

				try {
					startActivity(intent);
				} catch (ActivityNotFoundException e) {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] ActivityNotFoundException=["
									+ e.getMessage() + "]");
					return false;
				}
			}
		}
		
		// 기존 방식
		else {
			
			if ( url.startsWith( "ispmobile" ) ) { // 7.4 ISP 모듈 연동 테스트
				if( !new PackageState(this).getPackageDownloadInstallState( "kvp.jjy.MispAndroid" ) ) { 
					startActivity( new Intent(
							Intent.ACTION_VIEW, 
							Uri.parse("market://details?id=kvp.jjy.MispAndroid320")));
					view.goBack();
					return true; 
				} 
				
			}
			/*  else if ( url.startsWith( "paypin" ) ) { if(
			 * !new PackageState( this ).getPackageDownloadInstallState(
			 * "com.skp.android.paypin" ) ) { if( !url_scheme_intent(
			 * "tstore://PRODUCT_VIEW/0000284061/0" ) ) { url_scheme_intent(
			 * "market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ.k"
			 * ); }
			 * 
			 * return true; } }
			 */

			// 삼성과 같은 경우 어플이 없을 경우 마켓으로 이동 할수 있도록 넣은 샘플 입니다.
			// 실제 구현시 업체 구현 여부에 따라 삭제 처리 하시는것이 좋습니다.
			if (url.startsWith("mpocket.online.ansimclick")) {
				if (!new PackageState(this)
				.getPackageDownloadInstallState("kr.co.samsungcard.mpocket")) {

					showToast("해당 어플을 설치 후 다시 시도해 주세요.", Toast.LENGTH_LONG, false);

					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("market://details?id=kr.co.samsungcard.mpocket")));

					return true;
				}
			}

			// try
			// {
			// startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( url ) )
			// );
			// }
			// catch(Exception e)
			// {
			// // 어플이 설치 안되어 있을경우 오류 발생. 해당 부분은 업체에 맞게 구현
			// Toast.makeText(this, "해당 어플을 설치해 주세요.",
			// Toast.LENGTH_LONG).show();
			// }

			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				return true;
			}
		}

		return true;
	}

	private class mWebChromeClient extends WebChromeClient {

		boolean isActionBarProgressBarShowing = false;

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				if (newProgress != 100)
					setActionBarProgressBar(true);
				else
					setActionBarProgressBar(false);
			}
		}

		void setActionBarProgressBar(boolean show) {
			if (show != isActionBarProgressBarShowing) {
				setSupportProgressBarIndeterminateVisibility(show);
				isActionBarProgressBarShowing = show;	
			}
		}
	}

	private class mWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			android.util.Log.e("PAYMENT_ACTIVITY / URL",url);

			Log.d(ResultRcvActivity.m_strLogTag,
					"[PayDemoActivity] called__shouldOverrideUrlLoading - url=["
							+ url + "]");

			if (url != null && !url.equals("about:blank")) {

				if (url.startsWith("http://") || url.startsWith("https://")) {
					if (url.contains("http://market.android.com")
							|| url.contains("http://m.ahnlab.com/kr/site/download")
							|| url.endsWith(".apk")) {
						return url_scheme_intent(view, url);
					} else {
						view.loadUrl(url);
						return false;
					}
				}

				else if (url.startsWith("mailto:")) {
					return false;
				} else if (url.startsWith("tel:")) {
					return false;
				} else {
					return url_scheme_intent(view, url);
				}
			}
			// else if isp판단 , 없으면 isp 설치페이지로 이동.

			return true;
		}

		// error 처리
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			webView.loadUrl("about:blank");
			setResult(CODE_RESULT_ACTIVITY_PAYMENT_NETWORK_ERROR);
			finish();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				setSupportProgressBarIndeterminateVisibility(true);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			CookieSyncManager.getInstance().sync();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
				setSupportProgressBarIndeterminateVisibility(false);
		}

	}

	private class KCPPayPinReturn {
		@JavascriptInterface
		public String getConfirm() {
			if (ResultRcvActivity.b_type) {
				ResultRcvActivity.b_type = false;

				return "true";
			} else {
				return "false";
			}
		}
	}

	private class KCPPayPinInfoBridge {
		@JavascriptInterface
		public void getPaypinInfo(final String url) {
			handler.post(new Runnable() {
				public void run() {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] KCPPayPinInfoBridge=[getPaypinInfo]");

					PackageState ps = new PackageState(PaymentActivity.this);

					if (!ps.getPackageAllInstallState("com.skp.android.paypin")) {
						paypinConfim();
					} else {
						url_scheme_intent(null, url);
					}
				}
			});
		}

		@JavascriptInterface
		private void paypinConfim() {
			AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(
					PaymentActivity.this);
			AlertDialog alertDlg;

			dlgBuilder.setTitle("확인");
			dlgBuilder
			.setMessage("PayPin 어플리케이션이 설치되어 있지 않습니다. \n설치를 눌러 진행 해 주십시요.\n취소를 누르면 결제가 취소 됩니다.");
			dlgBuilder.setCancelable(false);
			dlgBuilder.setPositiveButton("설치",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					if (
							// url_scheme_intent(
							// "https://play.google.com/store/apps/details?id=com.skp.android.paypin&feature=nav_result#?t=W10."
							// );
							// url_scheme_intent(
							// "market://details?id=com.skp.android.paypin&feature=nav_result#?t=W10."
							// );
							!url_scheme_intent(null,
									"tstore://PRODUCT_VIEW/0000284061/0")) {
						url_scheme_intent(
								null,
								"market://details?id=com.skp.android.paypin&feature=search_result#?t=W251bGwsMSwxLDEsImNvbS5za3AuYW5kcm9pZC5wYXlwaW4iXQ.k");
					}
				}
			});
			dlgBuilder.setNegativeButton("취소",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					showToast("결제를 취소하셨습니다", Toast.LENGTH_SHORT, false);

				}
			});

			alertDlg = dlgBuilder.create();
			alertDlg.show();
		}
	}

	// 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
	private class KCPPayCardInfoBridge {
		@JavascriptInterface
		public void getCardInfo(final String card_cd, final String quota) {
			handler.post(new Runnable() {
				public void run() {
					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] KCPPayCardInfoBridge=["
									+ card_cd + ", " + quota + "]");

					CARD_CD = card_cd;
					QUOTA = quota;

					PackageState ps = new PackageState(PaymentActivity.this);

					if (!ps.getPackageDownloadInstallState("com.skt.at")) {
						alertToNext();
					}
				}
			});
		}

		@JavascriptInterface
		private void alertToNext() {
			AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(
					PaymentActivity.this);
			AlertDialog alertDlg;

			dlgBuilder.setMessage("HANA SK 모듈이 설이 되어있지 않습니다.\n설치 하시겠습니까?");
			dlgBuilder.setCancelable(false);
			dlgBuilder.setPositiveButton("예",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					Intent intent = new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://cert.hanaskcard.com/Ansim/HanaSKPay.apk"));

					m_nStat = PROGRESS_STAT_IN;

					startActivity(intent);
				}
			});
			dlgBuilder.setNegativeButton("아니오",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			alertDlg = dlgBuilder.create();
			alertDlg.show();
		}
	}

	private class KCPPayBridge {
		@JavascriptInterface
		public void launchMISP(final String arg) {
			handler.post(new Runnable() {
				public void run() {
					boolean isp_app = true;
					String strUrl;
					String argUrl;

					PackageState ps = new PackageState(PaymentActivity.this);

					argUrl = arg;

					if (!arg.equals("Install")) {
						if (!ps.getPackageDownloadInstallState("kvp.jjy.MispAndroid")) {
							argUrl = "Install";
						}
					}

					strUrl = (argUrl.equals("Install") == true) ? "market://details?id=kvp.jjy.MispAndroid320" // "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
							: argUrl;

					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(strUrl));

					m_nStat = PROGRESS_STAT_IN;
					Log.d("m_nStat", Integer.toString(m_nStat));
					startActivity(intent);
				}
			});
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.d(ResultRcvActivity.m_strLogTag,
				"[PayDemoActivity] called__onResume + INPROGRESS=[" + m_nStat
				+ "]");

		// 하나 SK 모듈로 결제 이후 해당 카드 정보를 가지고 오기위해 사용
		if (ResultRcvActivity.m_uriResult != null) {
			if (ResultRcvActivity.m_uriResult.getQueryParameter("realPan") != null
					&& ResultRcvActivity.m_uriResult.getQueryParameter("cavv") != null
					&& ResultRcvActivity.m_uriResult.getQueryParameter("xid") != null
					&& ResultRcvActivity.m_uriResult.getQueryParameter("eci") != null) {
				Log.d(ResultRcvActivity.m_strLogTag,
						"[PayDemoActivity] HANA SK Result = javascript:hanaSK('"
								+ ResultRcvActivity.m_uriResult
								.getQueryParameter("realPan")
								+ "', '"
								+ ResultRcvActivity.m_uriResult
								.getQueryParameter("cavv")
								+ "', '"
								+ ResultRcvActivity.m_uriResult
								.getQueryParameter("xid")
								+ "', '"
								+ ResultRcvActivity.m_uriResult
								.getQueryParameter("eci") + "', '"
								+ CARD_CD + "', '" + QUOTA + "');");

				// 하나 SK 모듈로 인증 이후 승인을 하기위해 결제 함수를 호출 (주문자 페이지)
				webView.loadUrl("javascript:hanaSK('"
						+ ResultRcvActivity.m_uriResult
						.getQueryParameter("realPan")
						+ "', '"
						+ ResultRcvActivity.m_uriResult
						.getQueryParameter("cavv")
						+ "', '"
						+ ResultRcvActivity.m_uriResult
						.getQueryParameter("xid")
						+ "', '"
						+ ResultRcvActivity.m_uriResult
						.getQueryParameter("eci") + "', '" + CARD_CD
						+ "', '" + QUOTA + "');");
			}

			if ((ResultRcvActivity.m_uriResult.getQueryParameter("res_cd") == null ? ""
					: ResultRcvActivity.m_uriResult.getQueryParameter("res_cd"))
					.equals("999")) {
				Log.d(ResultRcvActivity.m_strLogTag,
						"[PayDemoActivity] HANA SK Result = cancel");

				m_nStat = 9;
			}

			if ((ResultRcvActivity.m_uriResult.getQueryParameter("isp_res_cd") == null ? ""
					: ResultRcvActivity.m_uriResult
					.getQueryParameter("isp_res_cd")).equals("0000")) {
				Log.d(ResultRcvActivity.m_strLogTag,
						"[PayDemoActivity] ISP Result = 0000");

				webView.loadUrl("http://pggw.kcp.co.kr/lds/smart_phone_linux_jsp/sample/card/samrt_res.jsp?result=OK&a="
						+ ResultRcvActivity.m_uriResult.getQueryParameter("a"));
				// webView.loadUrl(
				// "https://pggw.kcp.co.kr/app.do?ActionResult=app&approval_key="
				// + strApprovalKey );
			} else {
				Log.d(ResultRcvActivity.m_strLogTag,
						"[PayDemoActivity] ISP Result = cancel");
			}
		}

		if (m_nStat == PROGRESS_STAT_IN) {
			checkFrom();
		}

		ResultRcvActivity.m_uriResult = null;
	}

	@JavascriptInterface
	public void checkFrom() {
		try {

			if (ResultRcvActivity.m_uriResult != null) {
				m_nStat = PROGRESS_DONE;
				String strResultInfo = ResultRcvActivity.m_uriResult
						.getQueryParameter("approval_key");

				if (strResultInfo == null || strResultInfo.length() <= 4)
					finishActivity("ISP 결제 오류");

				String strResCD = strResultInfo.substring(strResultInfo
						.length() - 4);

				Log.d(ResultRcvActivity.m_strLogTag,
						"[PayDemoActivity] result=[" + strResultInfo + "]+"
								+ "res_cd=[" + strResCD + "]");

				if (strResCD.equals("0000") == true) {

					String strApprovalKey = "";

					strApprovalKey = strResultInfo.substring(0,
							strResultInfo.length() - 4);

					Log.d(ResultRcvActivity.m_strLogTag,
							"[PayDemoActivity] approval_key=[" + strApprovalKey
							+ "]");

					webView.loadUrl("https://pggw.kcp.co.kr/app.do?ActionResult=app&approval_key="
							+ strApprovalKey);

				} else if (strResCD.equals("3001") == true) {
					finishActivity("ISP 결제 사용자 취소");
				} else {
					finishActivity("ISP 결제 기타 오류");
				}
			}
		} catch (Exception e) {
		} finally {
		}
	}

	@Override
	@JavascriptInterface
	protected Dialog onCreateDialog(int id) {
		Log.d(ResultRcvActivity.m_strLogTag,
				"[PayDemoActivity] called__onCreateDialog - id=[" + id + "]");

		super.onCreateDialog(id);

		AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(this);
		AlertDialog alertDlg;

		dlgBuilder.setTitle("취소");
		dlgBuilder.setMessage("결제가 진행중입니다.\n취소하시겠습니까?");
		dlgBuilder.setCancelable(false);
		dlgBuilder.setPositiveButton("예",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finishActivity("사용자 취소");
			}
		});
		dlgBuilder.setNegativeButton("아니오",
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alertDlg = dlgBuilder.create();

		return alertDlg;
	}

	@JavascriptInterface
	public void finishActivity(String p_strFinishMsg) {

		int resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;

		if (p_strFinishMsg != null) {
			if (p_strFinishMsg.equals("NOT_AVAILABLE")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_NOT_AVAILABLE;
			} else if (p_strFinishMsg.contains("취소")) {
				resultCode = RESULT_CANCELED;
			}
		}
		Intent payData = new Intent();
		payData.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

		setResult(resultCode, payData);
		finish();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);

	}

	private class JavaScriptExtention {

		JavaScriptExtention() {
		}

		@JavascriptInterface
		public void feed(final String msg) {

			int resultCode = 0;

			if (msg.equals("SUCCESS")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SUCCESS;
			} else if (msg.equals("INVALID_SESSION")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION;
			} else if (msg.equals("SOLD_OUT")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_SOLD_OUT;
			} else if (msg.equals("PAYMENT_COMPLETE")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE;
			} else if (msg.equals("INVALID_DATE")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_INVALID_DATE;
			} else if (msg.equals("PAYMENT_CANCEL")) {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCEL;
			} else {
				resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;
			}

			Intent payData = new Intent();
			payData.putExtra(NAME_INTENT_EXTRA_DATA_PAY, mPay);

			setResult(resultCode, payData);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				PaymentActivity.this);
		alertDialog.setTitle("결제알림").setMessage("결제를 취소하시겠습니까?")
		.setCancelable(false)
		.setPositiveButton("예", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				return;
			}
		});
		AlertDialog alert = alertDialog.create();
		alert.show();
	}

}
