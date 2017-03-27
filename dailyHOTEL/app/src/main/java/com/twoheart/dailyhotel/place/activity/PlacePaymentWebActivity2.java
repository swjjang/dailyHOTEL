package com.twoheart.dailyhotel.place.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.PlacePaymentInformation;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.net.URISyntaxException;

import kr.co.kcp.android.payment.standard.ResultRcvActivity;
import kr.co.kcp.util.PackageState;

/**
 * Created by android_sam on 2017. 3. 27..
 */

public abstract class PlacePaymentWebActivity2 extends BaseActivity implements Constants
{
    // KCP - PayDemoActivity
    public static final int PROGRESS_STAT_NOT_START = 1;
    public static final int PROGRESS_STAT_IN = 2;
    public static final int PROGRESS_DONE = 3;

    public static String CARD_CD = "";
    public static String QUOTA = "";
    private final Handler handler = new Handler(); // KCP Bridge 용 Handler
    public int m_nStat = PROGRESS_STAT_NOT_START; // KCP Progress state

    private boolean bankpay_auth_flag = false; // KCP - AcntPayDemoActivity
    private String bankpay_code = ""; // KCP - AcntPayDemoActivity
    private String bankpay_value = ""; // KCP - AcntPayDemoActivity

    protected WebView mWebView;

    protected PlacePaymentInformation mPlacePaymentInformation;

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

        initIntentData(intent);

        if (hasProductList() == false)
        {
            DailyToast.showToast(PlacePaymentWebActivity2.this //
                , R.string.toast_msg_failed_to_get_payment_info, Toast.LENGTH_SHORT);
            finish();
            return;
        }

        if (getProductIndex() <= 0)
        {
            // 세션이 만료되어 재시작 요청.
            restartExpiredSession();
            return;
        }

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
         * kcp 구성 - PayDemoScriptXActivity, PayDemoActivity, AcntPayDemoActivity
         * inicis 구성 - AppCallSample
         */
        mWebView.getSettings().setSavePassword(false);
        mWebView.getSettings().setAppCacheEnabled(true); // kcp 구성 - PayDemoScriptXActivity, PayDemoActivity

        /**
         * kcp 구성 - PayDemoActivity
         * Local storage 사용에 대해서 허용 설정 - 안드로이드에서 센차등을 이용해서 웹앱을 개발할때 필요한 설정
         */
        mWebView.getSettings().setDomStorageEnabled(true); // kcp 구성 - PayDemoActivity

        /**
         * 전체 공통
         * kcp 구성 - PayDemoScriptXActivity, PayDemoActivity, AcntPayDemoActivity
         * inicis 구성
         */
        mWebView.getSettings().setJavaScriptEnabled(true);

        /**
         * javaScript 의 window.open 허용
         */
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // kcp 구성 - PayDemoActivity, AcntPayDemoActivity

        /**
         * multi window 지원 여부 - default false!
         * default 가 false 이므로 주석처리함
         */
        //        mWebView.getSettings().setSupportMultipleWindows(false); // kcp 구성 - AcntPayDemoActivity

        // webview 설정 끝

        /**
         * 쿠키 설정 - android 5.0 이상의 webview security 강화로 인한 처리
         * Inicis 설명 - Insecurity Page 에 대한 Access 차단으로 P_NEXT_URL 의 Scheme 을 Http 로 하는 경 우,
         * 페이지가 호출되지 않아 인증결과가 전달되지 않을 수 있습니다.
         * P_NEXT_URL 의 Scheme 이 Http 일 경우, 반드시 “Insecurity 페이지 허용” 으로 설정되어야 합니다.
         *
         * Third party cookies 사용의 차단으로 안심클릭 카드 결제 시, 보안 키보드를 불러오지 못 하는 이슈 등이 발생할 수 있으니 하기 설정을 true 로
         */
        if (Util.isOverAPI21() == true)
        {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        // 쿠키 설정 완료

        // long click 방치 코드 - 자체
        mWebView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return true;
            }
        }); // 롱클릭 에러 방지.

        // javaScript Interface
        mWebView.addJavascriptInterface(new KCPPayBridge(), "KCPPayApp"); // kcp - PayDemoActivity, AcntPayDemoActivity
        // 하나SK 카드 선택시 User가 선택한 기본 정보를 가지고 오기위해 사용
        mWebView.addJavascriptInterface(new KCPPayCardInfoBridge(), "KCPPayCardInfo"); // kcp - PayDemoActivity
        mWebView.addJavascriptInterface(new KCPPayPinInfoBridge(), "KCPPayPinInfo"); // 페이핀 기능 추가, kcp - PayDemoActivity
        mWebView.addJavascriptInterface(new KCPPayPinReturn(), "KCPPayPinRet"); // 페이핀 기능 추가, kcp - PayDemoActivity

        mWebView.setWebChromeClient(new DailyWebChromeClient());
        mWebView.setWebViewClient(new DailyWebViewClient());
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (bankpay_auth_flag)
        {
            bankpay_auth_flag = false;

            checkFromACNT();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // KCP - AcntPayDemoActivity
        if(data != null)
        {
            ExLog.d("dataCode : " + data.getExtras().getString("bankpay_code"));
            ExLog.d("dataValue : " + data.getExtras().getString("bankpay_value"));

            bankpay_code  = data.getExtras().getString("bankpay_code");
            bankpay_value = data.getExtras().getString("bankpay_value");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initIntentData(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        mPlacePaymentInformation = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_PAYMENTINFORMATION);
    }

    protected abstract boolean hasProductList();

    protected abstract int getProductIndex();

    private void checkFromACNT()
    {
        try
        {
            ExLog.d("called__onResume { bankpay_code=[" + bankpay_code + "], bankpay_value=[" + bankpay_value + "] }");

            mWebView.loadUrl("javascript:KCP_App_script('" + bankpay_code + "','" + bankpay_value + "')");
        } catch (Exception e)
        {
            ExLog.d(e.getMessage());
        } finally
        {
        }
    }


    private class DailyWebChromeClient extends WebChromeClient
    {

    }

    private class DailyWebViewClient extends WebViewClient
    {

    }

    // JavaScript Interface
    @JavascriptInterface
    private boolean url_scheme_intent(WebView view, String url)
    {
        ExLog.d("called__test - url=[" + url + "]");

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
                    ExLog.d("URISyntaxException=[" + e.getMessage() + "]");
                    return false;
                } catch (ActivityNotFoundException e)
                {
                    ExLog.d("ActivityNotFoundException=[" + e.getMessage() + "]");
                    return false;
                }
            }
            //ILK 용
            else if (url.contains("com.ahnlab.v3mobileplus"))
            {
                try
                {
                    startActivity(Intent.parseUri(url, 0));
                } catch (URISyntaxException e)
                {
                    ExLog.d("URISyntaxException=[" + e.getMessage() + "]");
                    return false;
                } catch (ActivityNotFoundException e)
                {
                    ExLog.d("ActivityNotFoundException=[" + e.getMessage() + "]");
                    return false;
                }
            } else
            {
                Intent intent = null;

                try
                {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex)
                {
                    ExLog.d("URISyntaxException=[" + ex.getMessage() + "]");
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
                    ExLog.d("ActivityNotFoundException=[" + e.getMessage() + "]");
                    return false;

                    // Old ver
                    //                    Util.installPackage(this, intent.getPackage());
                    //                    return true;
                }
            }
        }
        // 기존 방식
        else
        {
            if (url.startsWith("ispmobile"))
            {
                if (!new PackageState(this).getPackageDownloadInstallState(PACKAGE_NAME_ISP))
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_ISP)));

                    return true;
                }
            } else if (url.startsWith("paypin"))
            {
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
                    DailyToast.showToast(this, R.string.toast_msg_retry_payment_after_install_app, Toast.LENGTH_LONG);

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_PAYMENT_MPOCKET)));

                    return true;
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

    private class KCPPayBridge
    {
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

                    PackageState ps = new PackageState(PlacePaymentWebActivity2.this);

                    argUrl = arg;

                    if (!arg.equals("Install"))
                    {
                        if (!ps.getPackageDownloadInstallState("kvp.jjy.MispAndroid"))
                        {
                            argUrl = "Install";
                        }
                    }

                    strUrl = (argUrl.equals("Install") == true) ? "market://details?id=kvp.jjy.MispAndroid320" // "http://mobile.vpay.co.kr/jsp/MISP/andown.jsp"
                        : argUrl;

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));

                    m_nStat = PROGRESS_STAT_IN;

                    //                    startActivity( intent );
                    try
                    {
                        startActivity(intent);
                    } catch (Exception e)
                    {
                        Util.installPackage(PlacePaymentWebActivity2.this, "kvp.jjy.MispAndroid320");
                    }
                }
            });
        }

        // KCP - AcntPayDemoActivity
        @JavascriptInterface
        public void launchAcnt(final String arg)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    ExLog.d("KCPPayBridge=[" + arg + "]");

                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.setComponent(new ComponentName("com.kftc.bankpay.android", "com.kftc.bankpay.android.activity.MainActivity"));
                    intent.putExtra("requestInfo", arg);

//                    startActivityForResult(intent, 1);
                    startActivityForResult(intent, ㄹㅇㅁㄴㅇㄹㅁ ㅇㄹㅁㄴㅇㄹ ㅁㅇㄹ ㅁㅇㄹㅁ ㅇㄹ);

                    bankpay_auth_flag = true;
                }
            });
        }
    }

    private class KCPPayCardInfoBridge
    {
        @JavascriptInterface
        public void getCardInfo(final String card_cd, final String quota)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    ExLog.d("KCPPayCardInfoBridge=[" + card_cd + ", " + quota + "]");

                    CARD_CD = card_cd;
                    QUOTA = quota;

                    PackageState ps = new PackageState(PlacePaymentWebActivity2.this);

                    if (!ps.getPackageDownloadInstallState("com.skt.at"))
                    {
                        alertToNext();
                    }
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
        @JavascriptInterface
        public void getPaypinInfo(final String url)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    ExLog.d("KCPPayPinInfoBridge=[getPaypinInfo]");

                    PackageState ps = new PackageState(PlacePaymentWebActivity2.this);

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
        private void paypinConfim()
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
                    DailyToast.showToast(PlacePaymentWebActivity2.this, R.string.toast_msg_cancel_payment, Toast.LENGTH_SHORT);
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
}
