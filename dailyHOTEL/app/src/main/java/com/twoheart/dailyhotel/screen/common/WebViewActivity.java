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

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public abstract class WebViewActivity extends BaseActivity implements OnLongClickListener
{
    protected DailyHotelWebChromeClient webChromeClient;
    protected DailyHotelWebViewClient webViewClient;
    protected WebView mWebView;
    boolean mEnabledProgress = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_PROGRESS);
    }

    protected void initWebView()
    {
        webChromeClient = new DailyHotelWebChromeClient();
        webViewClient = new DailyHotelWebViewClient();
        mWebView = findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.setOnLongClickListener(this);
        mWebView.setLongClickable(false);
        mWebView.setWebViewClient(webViewClient);
        mWebView.setWebChromeClient(webChromeClient);
    }

    @JavascriptInterface
    protected void setWebView(String url)
    {
        initWebView();

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Os-Type", "android");
        headerMap.put("App-Version", DailyHotel.VERSION);
        headerMap.put("App-VersionCode", DailyHotel.VERSION_CODE);
        headerMap.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        mWebView.loadUrl(url, headerMap);
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
    public void onBackPressed()
    {
        if (mWebView.canGoBack() == true)
        {
            mWebView.goBack();
        } else
        {
            super.onBackPressed();
        }
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

    public void setEnabledProgress(boolean enabled)
    {
        mEnabledProgress = enabled;
    }

    protected void smoothScrollTop(WebView webView)
    {
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofInt(webView, "scrollY", webView.getScrollY(), 0);
        objectAnimator.setDuration(webView.getScrollY() * 30 / ScreenUtils.getScreenHeight(this));
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        webView.flingScroll(0, 0);

        objectAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {
            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                objectAnimator.removeAllListeners();
                releaseUiComponent();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });

        objectAnimator.start();
    }

    public String getWebViewVersion()
    {
        if (mWebView == null)
        {
            return null;
        }
        String webViewVersion = mWebView.getSettings().getUserAgentString();

        int startIndex = webViewVersion.indexOf("Chrome/") + "Chrome/".length();
        int endIndex = webViewVersion.indexOf(" ", startIndex);

        return webViewVersion.substring(startIndex, endIndex).trim();
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
                if (VersionUtils.isOverAPI19() == true)
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
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk")));
                        }
                        return true;
                    }
                } else
                {
                    return false;
                }
            } else if (url.startsWith("kakaolink://") == true)
            {
                try
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk")));
                }
                return true;
            } else if (url.startsWith("mailto:") == true)
            {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(Intent.createChooser(emailIntent, "Send email…"));
            } else
            {
                view.loadUrl(url);
            }
            return true;
        }

        private void browseToExternalBrowser(String url)
        {
            if (DailyTextUtils.isTextEmpty(url) == true)
            {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try
            {
                startActivity(intent);
            } catch (ActivityNotFoundException e)
            {
                final String SEARCH_WORD = "details?id=";
                int startIndex = url.indexOf(SEARCH_WORD);
                String packageName = url.substring(startIndex + SEARCH_WORD.length());

                Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName));
                intentWeb.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try
                {
                    startActivity(intentWeb);
                } catch (Exception e1)
                {
                    DailyToast.showToast(WebViewActivity.this, R.string.toast_msg_dont_support_googleplay, Toast.LENGTH_LONG);
                }
            }
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