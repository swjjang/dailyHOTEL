package com.daily.dailyhotel.screen.common.web;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.daily.base.BaseActivity;
import com.daily.base.BaseDialogView;
import com.daily.base.util.ScreenUtils;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.ActivityWebDataBinding;
import com.twoheart.dailyhotel.util.Constants;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class DailyWebView extends BaseDialogView<DailyWebInterface.OnEventListener, ActivityWebDataBinding> implements DailyWebInterface.ViewInterface
{
    public DailyWebView(BaseActivity baseActivity, DailyWebInterface.OnEventListener listener)
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

        viewDataBinding.webView.getSettings().setJavaScriptEnabled(true);
        viewDataBinding.webView.setVerticalScrollbarOverlay(true);
        viewDataBinding.webView.getSettings().setSupportZoom(false);
        viewDataBinding.webView.setOnLongClickListener(v -> true);
        viewDataBinding.webView.setLongClickable(false);
        viewDataBinding.webView.setWebViewClient(new DailyWebViewClient());
        viewDataBinding.webView.setWebChromeClient(new DailyWebChromeClient());


        viewDataBinding.topImageView.setOnClickListener(v -> getEventListener().onScrollTop());
        viewDataBinding.topImageView.setVisibility(View.GONE);

        viewDataBinding.webView.setOnScrollListener(new com.daily.base.widget.DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    viewDataBinding.topImageView.setVisibility(View.GONE);
                } else
                {
                    viewDataBinding.topImageView.setVisibility(View.VISIBLE);
                }
            }
        });

        viewDataBinding.homeImageView.setOnClickListener(v -> getEventListener().onHomeClick());
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

    private void initToolbar(ActivityWebDataBinding viewDataBinding)
    {
        if (viewDataBinding == null)
        {
            return;
        }

        viewDataBinding.toolbarView.setOnBackClickListener(v -> getEventListener().onBackClick());
    }

    private class DailyWebViewClient extends WebViewClient
    {
        public static final String INTENT_PROTOCOL_START = "intent:";
        //        public static final String INTENT_PROTOCOL_INTENT = "#Intent;";
        //        public static final String INTENT_PROTOCOL_END = ";end;";
        //        public static final String GOOGLE_PLAY_STORE_PREFIX = "market://details?id=";

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
}
