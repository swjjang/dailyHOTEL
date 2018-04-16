package com.daily.dailyhotel.screen.common.web;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;

import java.util.Map;

import io.reactivex.Observable;

public interface DailyWebInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void loadUrl(String url, Map<String, String> headerMap);

        boolean canGoBack();

        void goBack();

        Observable<Boolean> smoothScrollTop();
    }

    interface OnEventListener extends OnBaseEventListener
    {
        void onDialog(String message, android.webkit.JsResult result);

        void onReceivedError(String message);

        void onBrowseToExternalBrowser(String url);

        void onKakaoTalk(String url);

        void onKakaoLink(String url);

        boolean onIntent(String url);

        void onMailTo(String url);

        void onScrollTop();

        void onHomeClick();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
    }
}
