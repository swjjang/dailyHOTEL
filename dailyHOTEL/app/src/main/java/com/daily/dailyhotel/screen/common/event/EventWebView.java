package com.daily.dailyhotel.screen.common.event;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.ScreenUtils;
import com.daily.base.util.VersionUtils;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityWebDataBinding;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class EventWebView extends BaseDialogView<EventWebInterface.OnEventListener, ActivityWebDataBinding> implements EventWebInterface.ViewInterface
{
    private JavaScriptExtension mJavaScriptExtension;

    public EventWebView(BaseActivity baseActivity, EventWebInterface.OnEventListener listener)
    {
        super(baseActivity, listener);
    }

    @Override
    protected void setContentView(final ActivityWebDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        initToolbar(viewDataBinding);
        initWebView();
    }

    @Override
    public void setToolbarTitle(String title)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().toolbarView.setTitleText(title);
    }

    private void initToolbar(ActivityWebDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EventWebView.this.getEventListener().onBackClick();
            }
        });
    }

    @Override
    public void setShareButtonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        if (visible == false)
        {
            getViewDataBinding().toolbarView.removeMenuItem(DailyToolbarView.MenuItem.SHARE);
            return;
        }

        getViewDataBinding().toolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getEventListener().onShareClick();
            }
        });
    }

    @Override
    public void setHomeButtonVisible(boolean visible)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().homeImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void initWebView()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().webView.getSettings().setJavaScriptEnabled(true);
        getViewDataBinding().webView.setVerticalScrollbarOverlay(true);
        getViewDataBinding().webView.getSettings().setSupportZoom(false);
        getViewDataBinding().webView.setOnLongClickListener(v -> true);
        getViewDataBinding().webView.setLongClickable(false);
        getViewDataBinding().webView.setWebViewClient(new DailyWebViewClient());
        getViewDataBinding().webView.setWebChromeClient(new DailyWebChromeClient());

        getViewDataBinding().webView.getSettings().setJavaScriptEnabled(true);
        getViewDataBinding().webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        if (VersionUtils.isOverAPI21())
        {
            getViewDataBinding().webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(getViewDataBinding().webView, true);
        }

        mJavaScriptExtension = new JavaScriptExtension();
        getViewDataBinding().webView.addJavascriptInterface(mJavaScriptExtension, "android");
        getViewDataBinding().webView.clearCache(true);
        getViewDataBinding().webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        getViewDataBinding().topImageView.setOnClickListener(v -> getEventListener().onScrollTop());
        getViewDataBinding().topImageView.setVisibility(View.GONE);

        getViewDataBinding().webView.setOnScrollListener(new com.daily.base.widget.DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    getViewDataBinding().topImageView.setVisibility(View.GONE);
                } else
                {
                    getViewDataBinding().topImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        getViewDataBinding().homeImageView.setOnClickListener(v -> getEventListener().onHomeClick());
        getViewDataBinding().homeImageView.setVisibility(View.GONE);
    }

    @Override
    public void loadUrl(String url, Map<String, String> headerMap)
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().webView.loadUrl(url, headerMap);
    }

    @Override
    public boolean canGoBack()
    {
        return getViewDataBinding() != null && getViewDataBinding().webView.canGoBack();
    }

    @Override
    public void goBack()
    {
        if (getViewDataBinding() == null)
        {
            return;
        }

        getViewDataBinding().webView.goBack();
    }

    @Override
    public String getCurrentUrl()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        return getViewDataBinding().webView.getUrl();
    }

    @Override
    public Observable<Boolean> smoothScrollTop()
    {
        if (getViewDataBinding() == null)
        {
            return null;
        }

        final ObjectAnimator objectAnimator = ObjectAnimator.ofInt(getViewDataBinding().webView, "scrollY", getViewDataBinding().webView.getScrollY(), 0);
        objectAnimator.setDuration(getViewDataBinding().webView.getScrollY() * 30 / ScreenUtils.getScreenHeight(getContext()));
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        getViewDataBinding().webView.flingScroll(0, 0);

        Observable<Boolean> observable = new Observable<Boolean>()
        {
            @Override
            protected void subscribeActual(Observer<? super Boolean> observer)
            {
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

                        observer.onNext(true);
                        observer.onComplete();
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
        };

        return observable;
    }

    @Override
    public void showShareDialog(DialogInterface.OnDismissListener listener)
    {
        DialogShareDataBinding dataBinding = DataBindingUtil.inflate( //
            LayoutInflater.from(getContext()), R.layout.dialog_share_data, null, false);

        dataBinding.kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onShareKakaoClick();
            }
        });

        dataBinding.copyLinkView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onCopyLinkClick();
            }
        });

        dataBinding.moreShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();

                getEventListener().onMoreShareClick();
            }
        });

        dataBinding.closeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
            }
        });

        showSimpleDialog(dataBinding.getRoot(), null, listener, true);
    }

    private class DailyWebViewClient extends WebViewClient
    {
        public static final String INTENT_PROTOCOL_START = "intent:";

        @JavascriptInterface
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, final String url)
        {
            if (url.equals("event://"))
            {
                getEventListener().onBrowseToExternalBrowser(Constants.URL_STORE_GOOGLE_DAILYHOTEL);
                getEventListener().onBackClick();
            } else if (url.equals("event://tstore"))
            {
                getEventListener().onBrowseToExternalBrowser(Constants.URL_STORE_T_DAILYHOTEL);
                getEventListener().onBackClick();
            } else if (url.contains("market://") == true)
            {
                getEventListener().onBrowseToExternalBrowser(url);
            } else if (url.contains("facebook.com") || url.contains("naver.com"))
            {
                getEventListener().onBrowseToExternalBrowser(url);
            } else if (url.contains("kakaoplus://"))
            {
                getEventListener().onKakaoTalk(url);
            } else if (url.contains("call://") == true)
            {

            } else if (url.startsWith(INTENT_PROTOCOL_START))
            {
                return getEventListener().onIntent(url);
            } else if (url.startsWith("kakaolink://") == true)
            {
                getEventListener().onKakaoLink(url);
            } else if (url.startsWith("mailto:") == true)
            {
                getEventListener().onMailTo(url);
            } else
            {
                view.loadUrl(url);
            }

            return true;
        }

        @JavascriptInterface
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
        {
            super.onReceivedError(view, errorCode, description, failingUrl);

            getEventListener().onReceivedError(getString(R.string.dialog_msg_network_unstable_retry_or_set_wifi));
            getEventListener().onBackClick();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            getEventListener().onUnlockAll();

            super.onPageFinished(view, url);
        }
    }

    private class DailyWebChromeClient extends WebChromeClient
    {
        @JavascriptInterface
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
        {
            getEventListener().onDialog(message, result);

            return true;
        }
    }

    /**
     * JavaScript
     *
     * @author Dailier
     */
    private class JavaScriptExtension
    {
        JavaScriptExtension()
        {
        }

        @JavascriptInterface
        public void externalLink(String packageName, String uri)
        {
            getEventListener().onExternalLink(packageName, uri);
        }

        @JavascriptInterface
        public void internalLink(String uri)
        {
            getEventListener().onInternalLink(uri);
        }

        @JavascriptInterface
        public void feed(String message)
        {
            getEventListener().onFeed(message);
        }

        @JavascriptInterface
        public void downloadCoupon(String couponCode, String deepLink, String confirmText)
        {
            getEventListener().onDownloadCoupon(couponCode, deepLink, confirmText);
        }

        @JavascriptInterface
        public void enabledBenefitAlarm()
        {
            getEventListener().onEnabledBenefitAlarm();
        }
    }
}
