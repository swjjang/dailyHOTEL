package com.twoheart.dailyhotel.screen.information.creditcard;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.IDailyNetwork;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.HashMap;
import java.util.Map;

public class RegisterCreditCardActivity extends BaseActivity implements Constants
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_regcreditcard);

        initToolbar();

        WebView webView = (WebView) findViewById(R.id.webView);

        // TODO  setWebContentsDebuggingEnabled
        //		WebView.setWebContentsDebuggingEnabled(true);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setSaveFormData(false);
        webView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSavePassword(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.addJavascriptInterface(new JavaScriptExtention(), "android");

        webView.setWebChromeClient(new DailyWebChromeClient());
        webView.setWebViewClient(new DailyWebViewClient());

        webView.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                return true;
            }
        }); // 롱클릭 에러 방지.

        String url = Crypto.getUrlDecoderEx(IDailyNetwork.URL_DAILYHOTEL_SERVER) + Crypto.getUrlDecoderEx(IDailyNetwork.URL_REGISTER_CREDIT_CARD);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Os-Type", "android");
        headerMap.put("App-Version", DailyHotel.VERSION);
        headerMap.put("App-VersionCode", DailyHotel.VERSION_CODE);
        headerMap.put("Authorization", DailyHotel.AUTHORIZATION);
        headerMap.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        webView.loadUrl(url, headerMap);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_reg_creditcard), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.CREDITCARD_ADD);

        super.onStart();
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
                finish();
            }
        };

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_register_creditcard_cancel), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), posListener, null);
    }

    private class DailyWebChromeClient extends WebChromeClient
    {
        boolean isActionBarProgressBarShowing = false;

        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            super.onProgressChanged(view, newProgress);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
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

            view.loadUrl("about:blank");

            if (Util.isAvailableNetwork(RegisterCreditCardActivity.this) == true)
            {
                if (errorCode == 401)
                {
                    setResult(CODE_RESULT_ACTIVITY_PAYMENT_INVALID_SESSION);
                } else
                {
                    setResult(CODE_RESULT_ACTIVITY_PAYMENT_FAIL);
                }
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
            {
                setSupportProgressBarIndeterminateVisibility(true);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);

            unLockUI();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            {
                setSupportProgressBarIndeterminateVisibility(false);
            }
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
            int resultCode;
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

                String[] splits = msg.split("\\^");

                if (splits.length > 1)
                {
                    if (Util.isTextEmpty(splits[1]) == false)
                    {
                        payData.putExtra(NAME_INTENT_EXTRA_DATA_MESSAGE, splits[1]);
                    }
                }
            }

            setResult(resultCode, payData);
            finish();
        }
    }
}
