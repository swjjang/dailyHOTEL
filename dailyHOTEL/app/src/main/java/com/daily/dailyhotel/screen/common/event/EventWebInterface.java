package com.daily.dailyhotel.screen.common.event;

import android.app.Activity;
import android.content.DialogInterface;

import com.daily.base.BaseAnalyticsInterface;
import com.daily.base.BaseDialogViewInterface;
import com.daily.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyDeepLink;

import java.util.Map;

import io.reactivex.Observable;

public interface EventWebInterface
{
    interface ViewInterface extends BaseDialogViewInterface
    {
        void loadUrl(String url, Map<String, String> headerMap);

        boolean canGoBack();

        void goBack();

        Observable<Boolean> smoothScrollTop();

        void setShareButtonVisible(boolean visible);

        void showShareDialog(DialogInterface.OnDismissListener listener);

        void onEnabledProgress(boolean enabled);
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

        void onShareClick();

        void onShareKakaoClick();

        void onCopyLinkClick();

        void onMoreShareClick();

        void onDownloadCoupon(String couponCode, String deepLink, String confirmText);

        void onExternalLink(String packageName, String uri);

        void onInternalLink(String uri);

        void onFeed(String message);

        void onEnabledBenefitAlarm();

        void onScreenLock();

        void onUnlockAll();
    }

    interface AnalyticsInterface extends BaseAnalyticsInterface
    {
        void onScreen(Activity activity, String eventName, EventWebActivity.EventType eventType);

        void onDownLoadCoupon(Activity activity, String couponCode, String validTo);

        void onRecordDeepLink(Activity activity, DailyDeepLink dailyDeepLink);
    }
}
