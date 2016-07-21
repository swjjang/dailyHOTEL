/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * WebViewActivity
 * <p>
 * WebView를 사용하는 Activity를 위한 부모 클래스이다. 일괄적인 WebV
 * iew의 설정을 위해 설계된 클래스이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.common;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
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

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public abstract class WebViewActivity extends BaseActivity implements OnLongClickListener
{
    protected DailyHotelWebChromeClient webChromeClient;
    protected DailyHotelWebViewClient webViewClient;
    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
    }

    @JavascriptInterface
    protected void setWebView(String url)
    {
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

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Os-Type", "android");
        headerMap.put("App-Version", DailyHotel.VERSION);
        headerMap.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        webView.loadUrl(url, headerMap);
    }

    @Override
    public boolean onLongClick(View v)
    {
        return true;
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class DailyHotelWebViewClient extends WebViewClient
    {
        public static final String INTENT_PROTOCOL_START = "intent:";
        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        public static final String INTENT_PROTOCOL_END = ";end;";
        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

        @JavascriptInterface
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url)
        {
            if (url.equals("event://"))
            {
                finish();
                browseToExternalBrowser(URL_STORE_GOOGLE_DAILYHOTEL);

            } else if (url.equals("event://tstore"))
            {
                finish();
                browseToExternalBrowser(URL_STORE_T_DAILYHOTEL);

            } else if (url.contains("market://") == true)
            {
                browseToExternalBrowser(url);
            } else if (url.contains("facebook.com") || url.contains("naver.com"))
            {
                browseToExternalBrowser(url);
            } else if (url.contains("kakaoplus://"))
            {
                try
                {
                    PackageManager pm = getPackageManager();
                    // if throw namenotfoundexception => go to kakaotalk install page
                    pm.getApplicationInfo("com.kakao.talk", PackageManager.GET_META_DATA);
                    startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
                } catch (URISyntaxException e)
                {
                    ExLog.e(e.toString());
                } catch (NameNotFoundException e)
                {
                    try
                    {
                        startActivity(Intent.parseUri("market://details?id=com.kakao.talk", Intent.URI_INTENT_SCHEME));
                    } catch (URISyntaxException e1)
                    {
                        ExLog.e(e1.toString());
                    }
                }
            } else if (url.contains("call://") == true)
            {

            } else if (url.startsWith(INTENT_PROTOCOL_START))
            {
                final int customUrlStartIndex = INTENT_PROTOCOL_START.length();
                final int customUrlEndIndex = url.indexOf(INTENT_PROTOCOL_INTENT);
                if (customUrlEndIndex < 0)
                {
                    return false;
                } else
                {
                    final String customUrl = url.substring(customUrlStartIndex, customUrlEndIndex);
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(customUrl)));
                    } catch (ActivityNotFoundException e)
                    {
                        final int packageStartIndex = customUrlEndIndex + INTENT_PROTOCOL_INTENT.length();
                        final int packageEndIndex = url.indexOf(INTENT_PROTOCOL_END);

                        final String packageName = url.substring(packageStartIndex, packageEndIndex < 0 ? url.length() : packageEndIndex);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_STORE_PREFIX + packageName)));
                    }
                    return true;
                }
            } else
            {
                view.loadUrl(url);
            }
            return true;
        }

        private void browseToExternalBrowser(String url)
        {
            if (Util.isTextEmpty(url) == true)
            {
                return;
            }

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

        @JavascriptInterface
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);
            DailyToast.showToast(WebViewActivity.this, R.string.dialog_msg_network_unstable_retry_or_set_wifi, Toast.LENGTH_LONG);
            finish();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            lockUI();

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            unLockUI();

            super.onPageFinished(view, url);
        }
    }

    public class DailyHotelWebChromeClient extends WebChromeClient
    {

        @JavascriptInterface
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
        {
            if (isFinishing() == true)
            {
                return true;
            }

            showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    result.confirm();
                }
            }, null, false);

            return true;
        }

        public void onProgressChanged(WebView view, int progress)
        {
            WebViewActivity.this.setProgress(progress * 100);
        }
    }
}