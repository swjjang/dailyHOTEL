package com.twoheart.dailyhotel.place.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import kr.co.kcp.android.payment.standard.ResultRcvActivity;
import kr.co.kcp.util.PackageState;

/**
 * Created by android_sam on 2017. 3. 17..
 * Lasted Modified by android_sam on 2017. 3. 27..
 * KCP - AcntPayDemoActivity(계좌 이체)의 경우 사용안함으로 인한 제거
 */

public abstract class BasePaymentWebActivity extends BaseActivity implements Constants
{
    // KCP - PayDemoActivity
    public static final int PROGRESS_STAT_NOT_START = 1; // KCP
    public static final int PROGRESS_STAT_IN = 2; // KCP
    public static final int PROGRESS_DONE = 3; // KCP

    public static String CARD_CD = ""; // KCP
    public static String QUOTA = ""; // KCP
    public int m_nStat = PROGRESS_STAT_NOT_START; // KCP Progress state

    protected WebView mWebView;
    final Handler handler = new Handler(); // KCP Bridge 용 Handler

    // Inicis Kcp 를 분리하는 코드
    protected PgType mPgType = PgType.ETC;

    protected abstract String getScreenName();

    protected abstract void onIntent(Intent intent);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("AddJavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        // paymant 정보 처리
        Intent intent = getIntent();
        if (intent == null)
        {
            finish();
            return;
        }

        onIntent(intent);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams( //
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 앱에서 팝업이 띄어진 상태에서 Activity를 종료하면 발생하는 현상을 막기 위해서
        Activity activity = this;
        while (activity.getParent() != null)
        {
            activity = activity.getParent();
        }

        mWebView = new WebView(activity);

        setContentView(mWebView, layoutParams);

        // payment 정보 처리 끝

        // webview 설정

        // setWebContentsDebuggingEnabled
        //        WebView.setWebContentsDebuggingEnabled(true);

        /**
         * kcp 구성 - PayDemoScriptXActivity, PayDemoActivity
         * inicis 구성 - AppCallSample
         */
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setAppCacheEnabled(true); // 7.4 캐시 정책 비활성화. kcp 구성 - PayDemoScriptXActivity, PayDemoActivity

        /**
         * 기존 paymentWebActivity 에서 사용하지 않음으로 제거 - 센차 미사용
         * kcp 구성 - PayDemoActivity
         * Local storage 사용에 대해서 허용 설정 - 안드로이드에서 센차등을 이용해서 웹앱을 개발할때 필요한 설정
         */
        //        mWebView.getSettings().setDomStorageEnabled(true); // kcp 구성 - PayDemoActivity

        /**
         * 전체 공통
         * kcp 구성 - PayDemoScriptXActivity, PayDemoActivity
         * inicis 구성 - AppCallSample
         */
        mWebView.getSettings().setJavaScriptEnabled(true);

        /**
         * javaScript 의 window.open 허용
         */
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // kcp 구성 - PayDemoActivity

        // webview 설정 끝

        /**
         * 쿠키 설정 - android 5.0 이상의 webview security 강화로 인한 처리
         * Inicis 설명 - Insecurity Page 에 대한 Access 차단으로 P_NEXT_URL 의 Scheme 을 Http 로 하는 경 우,
         * 페이지가 호출되지 않아 인증결과가 전달되지 않을 수 있습니다.
         * P_NEXT_URL 의 Scheme 이 Http 일 경우, 반드시 “Insecurity 페이지 허용” 으로 설정되어야 합니다.
         *
         * Third party cookies 사용의 차단으로 안심클릭 카드 결제 시, 보안 키보드를 불러오지 못 하는 이슈 등이 발생할 수 있으니 하기 설정을 true 로
         */
        if (VersionUtils.isOverAPI21() == true)
        {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        // 쿠키 설정 완료

        // javaScript Interface
        mWebView.addJavascriptInterface(new KCPPayBridge(), "KCPPayApp"); // kcp - PayDemoActivity
        // 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
        mWebView.addJavascriptInterface(new KCPPayCardInfoBridge(), "KCPPayCardInfo"); // kcp - PayDemoActivity
        mWebView.addJavascriptInterface(new KCPPayPinInfoBridge(), "KCPPayPinInfo"); // 페이핀 기능 추가, kcp - PayDemoActivity
        mWebView.addJavascriptInterface(new KCPPayPinReturn(), "KCPPayPinRet"); // 페이핀 기능 추가, kcp - PayDemoActivity

        mWebView.addJavascriptInterface(new JavaScriptExtension(), "android"); // 결과 전달
        mWebView.addJavascriptInterface(new TeleditBridge(), "TeleditApp"); // 다날 결제
        // webView.addJavascriptInterface(new HtmlObserver(), "HtmlObserver"); // ?????

        mWebView.setWebChromeClient(new DailyWebChromeClient());
        mWebView.setWebViewClient(new DailyWebViewClient());

        // long click 방치 코드 - 자체
        mWebView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return true;
            }
        }); // 롱클릭 에러 방지.
    }

    @Override
    protected void onStart()
    {
        String screenName = getScreenName();
        if (DailyTextUtils.isTextEmpty(screenName) == false)
        {
            AnalyticsManager.getInstance(this).recordScreen(this, screenName, null);
        }

        super.onStart();
    }

    // JavaScript Interface - KCP
    @JavascriptInterface
    boolean url_scheme_intent(WebView view, String url)
    {
        // KCP Message - chrome 버젼 방식 : 2014.01 추가
        if (url.startsWith("intent"))
        {
            //ILK 용
            if (url.contains("com.lotte.lottesmartpay"))
            {
                try
                {
                    startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
                } catch (URISyntaxException e)
                {
                    return false;
                } catch (ActivityNotFoundException e)
                {
                    Util.installPackage(this, "com.lotte.lottesmartpay");
                    return true; // return true 하지 않을 때 onReceiveError 발생으로 결제 종료 됨 - true 정상 동작
                }
            } else if (url.contains("com.ahnlab.v3mobileplus"))
            {
                //ILK 용
                try
                {
                    startActivity(Intent.parseUri(url, 0));
                } catch (URISyntaxException e)
                {
                    return false;
                } catch (ActivityNotFoundException e)
                {
                    Util.installPackage(this, "com.ahnlab.v3mobileplus");
                    return true;
                }
            } else
            {
                Intent intent;

                try
                {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex)
                {
                    return false;
                }

                // 앱설치 체크를 합니다.
                if (getPackageManager().resolveActivity(intent, 0) == null)
                {
                    String packageName = intent.getPackage();
                    if (packageName != null)
                    {
                        Util.installPackage(this, packageName);
                        return true;
                    }
                }

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()));

                try
                {
                    startActivity(intent);
                } catch (ActivityNotFoundException e)
                {
                    Util.installPackage(this, intent.getPackage());
                    return true;
                }
            }
        } else
        {
            // 기존 방식
            if (url.startsWith("ispmobile"))
            {
                if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_ISP))
                {
                    try
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG); // 이니시스 때문에 들어간 코드로 보이나 기존 처리 유지
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_ISP)));

                        //                        view.goBack();  // 이니시스 때문에 들어간 코드로 보여 주석처리 함 - 샘플에서는 처리 불필요 한것으로 판단 됨
                        return true;
                    } catch (ActivityNotFoundException e)
                    {
                        Util.installPackage(this, PACKAGE_NAME_ISP);
                        return true;
                    }
                }
            } else if (url.startsWith("paypin"))
            {
                // 기존에는 없던 항목 - 신규 추가 함
                if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_PAYPIN))
                {
                    if (!url_scheme_intent(view, "tstore://PRODUCT_VIEW/0000284061/0"))
                    {
                        url_scheme_intent(view, URL_STORE_PAYMENT_PAYPIN);
                    }
                    return true;
                }
            } else if (url.startsWith("mpocket.online.ansimclick"))
            {
                if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_MPOCKET))
                {
                    try
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_MPOCKET)));
                        return true;
                    } catch (ActivityNotFoundException e)
                    {
                        Util.installPackage(this, PACKAGE_NAME_MPOCKET);
                        return true;
                    }
                }
            }

            try
            {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch (Exception e)
            {
                // ???? kcp 한글 encording 오류로 인한 내용 확인 불가로 주석처리!
                //                // ѕоЗГАМ јіДЎ ѕИµЗѕо АЦА»°жїм їА·щ №Я»э. ЗШґз єОєРАє ѕчГјїЎ ёВ°Ф ±ёЗц
                //                Toast.makeText(this, "ЗШґз ѕоЗГА» јіДЎЗШ БЦјјїд.", Toast.LENGTH_LONG).show();

                if (url.contains("tstore://"))
                {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // 이니시스 처리 - 불필요한 웹페이지 호출 제거
        String scriptForSkip = "";
        String scriptPrefix = "javascript:";
        if (requestCode == CODE_REQUEST_ISPMOBILE)
        {
            scriptForSkip = scriptPrefix + "submitIspAuthInfo('RUNSCHEME');"; // ISP 확인 버튼 콜
        } else if (requestCode == CODE_REQUEST_KFTC_BANKPAY)
        {
            scriptForSkip = scriptPrefix + "returnUrltoMall();"; // KTFC 확인 버튼 콜
        }

        if (DailyTextUtils.isTextEmpty(scriptForSkip) == false)
        {
            mWebView.loadUrl(scriptForSkip);
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //    @Override
        //    protected void onResume()
        //    {
        //        super.onResume();

        // 기존의 경우 onRestart 이나 샘플은 restart 인지 resume 인지 알수 없음. 주석과 super.onResume() 때문에 기존 유지

        //        ExLog.d("called__onResume + INPROGRESS=[" + m_nStat + "]");

        // 하나 SK 모듈로 결제 이후 해당 카드 정보를 가지고 오기위해 사용
        if (ResultRcvActivity.m_uriResult != null)
        {
            if (ResultRcvActivity.m_uriResult.getQueryParameter("realPan") != null //
                && ResultRcvActivity.m_uriResult.getQueryParameter("cavv") != null //
                && ResultRcvActivity.m_uriResult.getQueryParameter("xid") != null //
                && ResultRcvActivity.m_uriResult.getQueryParameter("eci") != null)
            {
                //                ExLog.d("HANA SK Result = javascript:hanaSK('"
                //                    + ResultRcvActivity.m_uriResult.getQueryParameter("realPan") //
                //                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("cavv") //
                //                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("xid") //
                //                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("eci") //
                //                    + "', '" + CARD_CD + "', '" + QUOTA + "');");

                // 하나 SK 모듈로 인증 이후 승인을 하기위해 결제 함수를 호출 (주문자 페이지)
                mWebView.loadUrl("javascript:hanaSK('" + ResultRcvActivity.m_uriResult.getQueryParameter("realPan") //
                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("cavv") //
                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("xid") //
                    + "', '" + ResultRcvActivity.m_uriResult.getQueryParameter("eci") //
                    + "', '" + CARD_CD + "', '" + QUOTA + "');");
            }

            if ((ResultRcvActivity.m_uriResult.getQueryParameter("res_cd") == null //
                ? "" : ResultRcvActivity.m_uriResult.getQueryParameter("res_cd")).equals("999"))
            {
                //                ExLog.d("HANA SK Result = cancel");
                m_nStat = 9;
            }

            if ((ResultRcvActivity.m_uriResult.getQueryParameter("isp_res_cd") == null //
                ? "" : ResultRcvActivity.m_uriResult.getQueryParameter("isp_res_cd")).equals("0000"))
            {
                //                ExLog.d("ISP Result = 0000");
                mWebView.loadUrl( //
                    "http://pggw.kcp.co.kr/lds/smart_phone_linux_jsp/sample/card/samrt_res.jsp?result=OK&a=" //
                        + ResultRcvActivity.m_uriResult.getQueryParameter("a"));
            } else
            {
                //                ExLog.d("ISP Result = cancel");
            }
        }

        if (m_nStat == PROGRESS_STAT_IN)
        {
            checkFrom();

            ResultRcvActivity.b_type = false;
        }

        ResultRcvActivity.m_uriResult = null;
    }

    @JavascriptInterface
    private void checkFrom()
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
                    return; // kcp sample에는 없으나 기존 코드에 있음. 처리에 문제가 없었기에 추가 함
                }

                String strResCD = strResultInfo.substring(strResultInfo.length() - 4);
                if (strResCD.equals("0000") == true)
                {
                    String strApprovalKey = strResultInfo.substring(0, strResultInfo.length() - 4);

                    //                    ExLog.d("approval_key=[" + strApprovalKey + "]" );

                    mWebView.loadUrl("https://smpay.kcp.co.kr/app.do?ActionResult=app&approval_key=" + strApprovalKey);
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
        } finally
        {
        }
    }

    @Override
    @JavascriptInterface
    protected Dialog onCreateDialog(int id)
    {
        super.onCreateDialog(id);

        View.OnClickListener posListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finishActivity(getString(R.string.act_payment_user_cancel));
            }
        };

        return createSimpleDialog(getString(R.string.dialog_btn_text_cancel) //
            , getString(R.string.dialog_msg_chk_cancel_payment_progress) //
            , getString(R.string.dialog_btn_text_yes) //
            , getString(R.string.dialog_btn_text_no), posListener, null);
    }

    @JavascriptInterface
    public void finishActivity(String p_strFinishMsg)
    {
        // KCP - 공통 - onCreateDialog 의 해당 부분의 encording 오류로 내용이 보이지 않아 처리를 기존 paymentWebActivity 소스로 처리함
        // PlacePaymentWebActivity 의 처리
        int resultCode = CODE_RESULT_ACTIVITY_PAYMENT_FAIL;

        if (p_strFinishMsg != null && p_strFinishMsg.contains(getString(R.string.act_payment_chk_contain)) == true)
        {
            resultCode = CODE_RESULT_ACTIVITY_PAYMENT_CANCELED;// RESULT_CANCELED
        }

        setResult(resultCode);
        finish();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        if (isFinishing() == true)
        {
            return;
        }

        View.OnClickListener posListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msgCode", -104);
                    jsonObject.put("msg", getString(R.string.act_toast_payment_canceled));

                    Intent intent = new Intent();
                    intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PAYMENT_RESULT, jsonObject.toString());

                    setResult(Activity.RESULT_CANCELED, intent);
                } catch (JSONException e)
                {
                    ExLog.d(e.getMessage());
                }

                finish();
            }
        };

        showSimpleDialog(getString(R.string.dialog_title_payment) //
            , getString(R.string.dialog_msg_chk_cancel_payment) //
            , getString(R.string.dialog_btn_text_yes) //
            , getString(R.string.dialog_btn_text_no), posListener, null);
    }

    // web client - start
    private class DailyWebChromeClient extends WebChromeClient
    {
        boolean isActionBarProgressBarShowing = false;

        DailyWebChromeClient()
        {
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
        {
            if (isFinishing() == true || (VersionUtils.isOverAPI17() == true && isDestroyed() == true))
            {
                return true;
            }

            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            super.onProgressChanged(view, newProgress);

            if (VersionUtils.isOverAPI14() == true)
            {
                if (newProgress != 100)
                {
                    setActionBarProgressBar(true);
                } else
                {
                    setActionBarProgressBar(false);
                }
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

    private class DailyWebViewClient extends WebViewClient
    {
        DailyWebViewClient()
        {
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (mPgType == PgType.KCP)
            {
                return shouldOverrideUrlLoadingForKcp(view, url);
            } else
            {
                // PgType.INICIS, ETC
                return shouldOverrideUrlLoadingForInicis(view, url);
            }
        }

        // error 처리
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);

            mWebView.loadUrl("about:blank");

            if (Util.isAvailableNetwork(BasePaymentWebActivity.this) == true)
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

            // handler.removeCallbacks(networkCheckRunner);
            // 결제 완료시 항상 네트워크 불안정뜨므로, 네트워크 체크는 제거하도록 함.

            setSupportProgressBarIndeterminateVisibility(true);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            setSupportProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
        {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
        {
            super.onReceivedError(view, request, error);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
        {
            super.onReceivedSslError(view, handler, error);
        }
    }

    public boolean shouldOverrideUrlLoadingForKcp(WebView view, String url)
    {
        // KCP - PayDemoActivity
        if (url != null && !url.equals("about:blank"))
        {
            if (url.startsWith("http://") || url.startsWith("https://"))
            {
                if (url.contains("http://market.android.com") //
                    || url.contains("http://m.ahnlab.com/kr/site/download") //
                    || url.endsWith(".apk"))
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
                String noCallMessage = getResources().getString(R.string.toast_msg_no_call_web);

                if (Util.isTelephonyEnabled(BasePaymentWebActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(BasePaymentWebActivity.this, noCallMessage, Toast.LENGTH_LONG);
                        return false;
                    }
                } else
                {
                    DailyToast.showToast(BasePaymentWebActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    return false;
                }
            } else
            {
                return url_scheme_intent(view, url);
            }
        }

        return true;
    }

    public boolean shouldOverrideUrlLoadingForInicis(WebView view, String url)
    {
        /**
         * Inicis - 이니시스의 경우 ispmobile 등으로 이벤트가 들어오면 startActivity로 화면 이동하는 것을 기본으로 작성 됨
         * 때문에 기본 예제의 방식으로 구현시 기존 로직과 맞지 않음
         * mailto , tel, intent, market 의 경우를 먼저 처리하고
         * url.startsWith("http://") || url.startsWith("https://") || url.startsWith("javascript:")) 일때 우선 처리
         * 나머지 경우에만 resolveActivity로 먼저 설치여부를 검사하고 이동 하도록 수정 함
         * 또한 Android OS 4.x 대에서는 view.goBack() 으로 동작시 에러(알수없는 오류) 발생으로 finish() 하고
         * 5.0 이상에서는 view.goBack() 으로 처리 함
         */

        if (url != null && !url.equals("about:blank"))
        {
            if (url.startsWith("mailto:"))
            {
                // inicis sample 에 없어서 추가
                return false;
            } else if (url.startsWith("tel:"))
            {
                // inicis sample 에 없으나 현대카드 전화 인증 이슈로 추가 됨

                String noCallMessage = getResources().getString(R.string.toast_msg_no_call_web);

                if (Util.isTelephonyEnabled(BasePaymentWebActivity.this) == true)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
                    } catch (ActivityNotFoundException e)
                    {
                        DailyToast.showToast(BasePaymentWebActivity.this, noCallMessage, Toast.LENGTH_LONG);
                        return false;
                    }
                } else
                {
                    DailyToast.showToast(BasePaymentWebActivity.this, noCallMessage, Toast.LENGTH_LONG);
                    return false;
                }
            } else if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("javascript:"))
            {
                view.loadUrl(url);
                return false;
            } else if (url.startsWith("intent://"))
            {
                Intent intent;

                try
                {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex)
                {
                    return false;
                }

                // 앱설치 체크를 합니다.
                if (getPackageManager().resolveActivity(intent, 0) == null)
                {
                    Util.installPackage(this, intent.getPackage());
                    return true;
                }

                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(intent.getDataString()));

                try
                {
                    startActivity(intent);
                } catch (ActivityNotFoundException e)
                {
                    /**
                     > 삼성카드 안심클릭
                     - 백신앱 : 웹백신 - 인프라웨어 테크놀러지
                     - package name : kr.co.shiftworks.vguardweb
                     - 특이사항 : INTENT:// 인입될시 정상적 호출

                     > 신한카드 안심클릭
                     - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                     - package name : com.TouchEn.mVaccine.webs
                     - 특이사항 : INTENT:// 인입될시 정상적 호출

                     > 농협카드 안심클릭
                     - 백신앱 : V3 Mobile Plus 2.0
                     - package name : com.ahnlab.v3mobileplus
                     - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                     > 외환카드 안심클릭
                     - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                     - package name : com.TouchEn.mVaccine.webs
                     - 특이사항 : INTENT:// 인입될시 정상적 호출

                     > 씨티카드 안심클릭
                     - 백신앱 : TouchEn mVaccine for Web - 라온시큐어(주)
                     - package name : com.TouchEn.mVaccine.webs
                     - 특이사항 : INTENT:// 인입될시 정상적 호출

                     > 하나SK카드 안심클릭
                     - 백신앱 : V3 Mobile Plus 2.0
                     - package name : com.ahnlab.v3mobileplus
                     - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                     > 하나카드 안심클릭
                     - 백신앱 : V3 Mobile Plus 2.0
                     - package name : com.ahnlab.v3mobileplus
                     - 특이사항 : 백신 설치 버튼이 있으며, 백신 설치 버튼 클릭시 정상적으로 마켓으로 이동하며, 백신이 없어도 결제가 진행이 됨

                     > 롯데카드
                     - 백신이 설치되어 있지 않아도, 결제페이지로 이동
                     */

                    // ExLog.d("Custom URL (intent://) 로 인입될시 마켓으로 이동되는 예외 처리: ");
                    Util.installPackage(this, intent.getPackage());
                    return true;
                }
            } else if (url.startsWith("market") == true)
            {
                // 마켓 의 경우 기존 로직과 동일하게 처리
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (ActivityNotFoundException e)
                {
                    return false;
                }

                return true;
            } else
            {
                // 예제 대로 실행 시 에러 발생으로 우선적으로 설치 체크 함
                Intent intent;

                try
                {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex)
                {
                    return false;
                }

                Uri uri = Uri.parse(intent.getDataString());
                intent = new Intent(Intent.ACTION_VIEW, uri);

                if (url.startsWith("ispmobile://"))
                {
                    //                    ExLog.e("INIPAYMOBILE, ISP MOBILE ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);
                        // 웹페이지 로드 시 결제 화면으로 먼저 이동 하고 결제 취소가 보임으로 기존 설치 페이지로 이동을 사용
                        //                        String ispUrl = "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp";
                        //                        mWebView.loadUrl(ispUrl);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_ISP)));

                        // Android OS 5.0 이상에서는 back 이하에서는 결제 취소 후 엑티비티 종료 하도록 함
                        if (VersionUtils.isOverAPI21() == true)
                        {
                            view.goBack();
                        } else
                        {
                            setResult(CODE_RESULT_ACTIVITY_PAYMENT_CANCELED);
                            finish();
                        }
                        return true;
                    }
                } else if (url.startsWith("kftc-bankpay"))
                {
                    // 계좌이체 - vbank 로 인해 사용 안함 - ios 만 사용
                    //                    ExLog.d("INIPAYMOBILE, 은행공동 계좌이체 PG서비스 설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        try
                        {
                            DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_KFTC)));

                            if (VersionUtils.isOverAPI21() == true)
                            {
                                view.goBack();
                            } else
                            {
                                setResult(CODE_RESULT_ACTIVITY_PAYMENT_CANCELED);
                                finish();
                            }
                        } catch (ActivityNotFoundException e)
                        {
                            Util.installPackage(this, PACKAGE_NAME_KFTC);
                        }
                        return true;
                    }

                } else if (intent.getDataString().startsWith("hdcardappcardansimclick://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 현대앱카드설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.hyundaicard.appcard"
                        Util.installPackage(BasePaymentWebActivity.this, "com.hyundaicard.appcard");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("shinhan-sr-ansimclick://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 신한카드앱설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.shcard.smartpay"
                        Util.installPackage(BasePaymentWebActivity.this, "com.shcard.smartpay");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("mpocket.online.ansimclick://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 삼성카드앱설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=kr.co.samsungcard.mpocket"
                        Util.installPackage(BasePaymentWebActivity.this, "kr.co.samsungcard.mpocket");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("lottesmartpay://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 롯데모바일결제 설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.lotte.lottesmartpay"
                        Util.installPackage(BasePaymentWebActivity.this, "com.lotte.lottesmartpay");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("lotteappcard://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 롯데앱카드(간편결제) 설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.lcacApp"
                        Util.installPackage(BasePaymentWebActivity.this, "com.lcacApp");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("kb-acp://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, KB앱카드 설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.kbcard.cxh.appcard"
                        Util.installPackage(BasePaymentWebActivity.this, "com.kbcard.cxh.appcard");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("hanaansim://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 하나SK카드 통합안심클릭앱 설치 ");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.ilk.visa3d"
                        Util.installPackage(BasePaymentWebActivity.this, "com.ilk.visa3d");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("smshinhanansimclick://"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 신한카드 SMART신한 앱 설치");

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://details?id=com.shcard.smartpay"
                        Util.installPackage(BasePaymentWebActivity.this, "com.shcard.smartpay");
                        return true;
                    }
                } else if (intent.getDataString().startsWith("droidxantivirusweb"))
                {
                    //                    ExLog.d("INIPAYMOBILE, 현대카드 안심클릭 droidxantivirusweb:");
                    /**
                     > 현대카드 안심클릭 droidxantivirusweb://
                     - 백신앱 : Droid-x 안드로이이드백신 - NSHC
                     - package name : net.nshc.droidxantivirus
                     - 특이사항 : 백신 설치 유무는 체크를 하고, 없을때 구글마켓으로 이동한다는 이벤트는 있지만, 구글마켓으로 이동되지는 않음
                     - 처리로직 : intent.getDataString()로 하여 droidxantivirusweb 값이 오면 현대카드 백신앱으로 인식하여
                     하드코딩된 마켓 URL로 이동하도록 한다.
                     */
                    /*************************************************************************************/
                    // ExLog.d("ActivityNotFoundException, droidxantivirusweb 문자열로 인입될시 마켓으로 이동되는 예외 처리: ");
                    /*************************************************************************************/

                    // 앱설치 체크를 합니다.
                    if (getPackageManager().resolveActivity(intent, 0) == null)
                    {
                        DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                        // "market://search?q=net.nshc.droidxantivirus""
                        Util.installPackage(BasePaymentWebActivity.this, "net.nshc.droidxantivirus");
                        return true;
                    }
                }

                // 결제 모듈 및 나머지 실행
                try
                {
                    int requestCode = 0;
                    if (url.startsWith("kftc-bankpay"))
                    {
                        requestCode = CODE_REQUEST_KFTC_BANKPAY;
                    } else if (url.startsWith("ispmobile"))
                    {
                        requestCode = CODE_REQUEST_ISPMOBILE;
                    }

                    startActivityForResult(intent, requestCode);
                } catch (ActivityNotFoundException e)
                {
                    return false;
                }
            }
        }

        return true;
    }
    // web client - end

    // KCP - bridge start
    private class KCPPayBridge
    {
        KCPPayBridge()
        {
        }

        // KCP - PayDemoActivity
        @JavascriptInterface
        public void launchMISP(final String arg)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    String strUrl;
                    String argUrl;

                    PackageState ps = new PackageState(BasePaymentWebActivity.this);

                    argUrl = arg;

                    if (!arg.equals("Install"))
                    {
                        if (!ps.getPackageDownloadInstallState("kvp.jjy.MispAndroid"))
                        {
                            argUrl = "Install";
                        }
                    }

                    strUrl = (argUrl.equals("Install") == true) //
                        ? "market://details?id=kvp.jjy.MispAndroid320" : argUrl;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));

                    m_nStat = PROGRESS_STAT_IN;

                    try
                    {
                        startActivity(intent);
                    } catch (Exception e)
                    {
                        Util.installPackage(BasePaymentWebActivity.this, "kvp.jjy.MispAndroid320");
                    }
                }
            });
        }
    }

    private class KCPPayCardInfoBridge
    {
        KCPPayCardInfoBridge()
        {
        }

        @JavascriptInterface
        public void getCardInfo(final String card_cd, final String quota)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    //                    ExLog.d("KCPPayCardInfoBridge=[" + card_cd + ", " + quota + "]");
                    CARD_CD = card_cd;
                    QUOTA = quota;

                    PackageState ps = new PackageState(BasePaymentWebActivity.this);

                    if (!ps.getPackageDownloadInstallState("com.skt.at"))
                    {
                        alertToNext();
                    }
                }
            });
        }

        @JavascriptInterface
        void alertToNext()
        {
            if (isFinishing() == true)
            {
                return;
            }

            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cert.hanaskcard.com/Ansim/HanaSKPay.apk"));

                    m_nStat = PROGRESS_STAT_IN;

                    startActivity(intent);
                }
            };

            showSimpleDialog(getString(R.string.dialog_notice2), //
                getString(R.string.dialog_msg_install_hana_sk), //
                getString(R.string.dialog_btn_text_yes), //
                getString(R.string.dialog_btn_text_no), posListener, null);
        }
    }

    private class KCPPayPinInfoBridge
    {
        KCPPayPinInfoBridge()
        {
        }

        @JavascriptInterface
        public void getPaypinInfo(final String url)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    //                    ExLog.d("KCPPayPinInfoBridge=[getPaypinInfo]");
                    PackageState ps = new PackageState(BasePaymentWebActivity.this);

                    if (!ps.getPackageAllInstallState("com.skp.android.paypin"))
                    {
                        paypinConfim();
                    } else
                    {
                        url_scheme_intent(null, url);
                    }
                }
            });
        }

        @JavascriptInterface
        void paypinConfim()
        {
            if (isFinishing() == true)
            {
                return;
            }

            View.OnClickListener positiveListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if (!url_scheme_intent(null, "tstore://PRODUCT_VIEW/0000284061/0"))
                    {
                        url_scheme_intent(null, URL_STORE_PAYMENT_PAYPIN);
                    }
                }
            };

            View.OnClickListener negativeListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DailyToast.showToast(BasePaymentWebActivity.this, R.string.toast_msg_cancel_payment, Toast.LENGTH_SHORT);
                }
            };

            showSimpleDialog(getString(R.string.dialog_btn_text_confirm), //
                getString(R.string.dialog_msg_install_paypin), //
                getString(R.string.dialog_btn_text_install), //
                getString(R.string.dialog_btn_text_cancel), positiveListener, negativeListener);
        }
    }

    private class KCPPayPinReturn
    {
        KCPPayPinReturn()
        {
        }

        @JavascriptInterface
        public String getConfirm()
        {
            if (ResultRcvActivity.b_type)
            {
                ResultRcvActivity.b_type = false;

                return "true";
            } else
            {
                return "false";
            }
        }
    }

    // KCP - bridge end

    /**
     * 다날 모바일 결제 관련 브릿지.
     *
     * @author jangjunho
     */
    private class TeleditBridge
    {
        TeleditBridge()
        {
        }

        /**
         * @param val 휴대폰 결제 완료 후 결과값.
         */
        @JavascriptInterface
        public void Result(final String val)
        {
            setResult(CODE_RESULT_ACTIVITY_PAYMENT_COMPLETE);
            finish();
        }

        /**
         * web에서 닫기를 콜 했을때 호출.
         */
        @JavascriptInterface
        public void BestClose()
        {
            //            Intent intent = new Intent();
            //            intent.putExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION, mPlacePaymentInformation);
            //
            //            setResult(CODE_RESULT_ACTIVITY_PAYMENT_CANCELED, intent);
            //            finish();
            onBackPressed();
        }
    }

    // 결과 전달을 위한 interface
    private class JavaScriptExtension
    {
        JavaScriptExtension()
        {
        }

        /**
         * 서버로부터 받은 결제 결과 메시지를 처리함.
         * 각각의 경우에 맞는 resultCode를 넣어 BookingActivity로 finish시킴.
         */
        @JavascriptInterface
        public void payment(final String jsonString)
        {
            onPaymentResult(jsonString);
        }
    }

    public abstract void onPaymentResult(String jsonString);
}
