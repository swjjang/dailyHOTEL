package com.daily.dailyhotel.screen.common.web;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.JsResult;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class DailyWebPresenter extends BaseExceptionPresenter<DailyWebActivity, DailyWebInterface.ViewInterface> implements DailyWebInterface.OnEventListener
{
    protected DailyWebInterface.AnalyticsInterface mAnalytics;

    protected String mTitleText;
    protected String mUrl;

    public DailyWebPresenter(@NonNull DailyWebActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected DailyWebInterface.ViewInterface createInstanceViewInterface()
    {
        return new DailyWebView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(DailyWebActivity activity)
    {
        setContentView(R.layout.activity_web_data);

        mAnalytics = initAnalytics();

        setRefresh(false);
    }

    protected DailyWebInterface.AnalyticsInterface initAnalytics()
    {
        return new DailyWebAnalyticsImpl();
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mTitleText = intent.getStringExtra(DailyWebActivity.INTENT_EXTRA_DATA_TITLE_TEXT);
        mUrl = intent.getStringExtra(DailyWebActivity.INTENT_EXTRA_DATA_URL);

        if (DailyTextUtils.isTextEmpty(mTitleText, mUrl) == true)
        {
            return false;
        }

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mTitleText);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Os-Type", "android");
        headerMap.put("App-Version", DailyHotel.VERSION);
        headerMap.put("App-VersionCode", DailyHotel.VERSION_CODE);
        headerMap.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        getViewInterface().loadUrl(mUrl, headerMap);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        // 꼭 호출해 주세요.
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed()
    {
        if (getViewInterface().canGoBack() == true)
        {
            getViewInterface().goBack();

            return true;
        } else
        {
            return super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockAll();
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);
        screenLock(showProgress);
    }

    @Override
    public void onBackClick()
    {
        getActivity().onBackPressed();
    }

    @Override
    public void onDialog(String message, JsResult result)
    {
        if (DailyTextUtils.isTextEmpty(message) == true)
        {
            return;
        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), message//
            , getString(R.string.dialog_btn_text_confirm), null, v -> result.confirm(), null, false);
    }

    @Override
    public void onReceivedError(String message)
    {
        if (DailyTextUtils.isTextEmpty(message) == true)
        {
            return;
        }

        DailyToast.showToast(getActivity(), message, DailyToast.LENGTH_LONG);
    }

    @Override
    public void onBrowseToExternalBrowser(String url)
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
                DailyToast.showToast(getActivity(), R.string.toast_msg_dont_support_googleplay, DailyToast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onKakaoTalk(String url)
    {
        try
        {
            PackageManager packageManager = getActivity().getPackageManager();
            // if throw namenotfoundexception => go to kakaotalk install page
            packageManager.getApplicationInfo("com.kakao.talk", PackageManager.GET_META_DATA);
            startActivity(Intent.parseUri(url, Intent.URI_INTENT_SCHEME));
        } catch (URISyntaxException e)
        {
            ExLog.e(e.toString());
        } catch (PackageManager.NameNotFoundException e)
        {
            try
            {
                startActivity(Intent.parseUri("market://details?id=com.kakao.talk", Intent.URI_INTENT_SCHEME));
            } catch (URISyntaxException e1)
            {
                ExLog.e(e1.toString());
            }
        }
    }

    @Override
    public void onKakaoLink(String url)
    {
        try
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (ActivityNotFoundException e)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.kakao.talk")));
        }
    }

    @Override
    public boolean onIntent(String url)
    {
        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            return false;
        }

        if (VersionUtils.isOverAPI19() == true)
        {
            final String INTENT_PROTOCOL_START = "intent:";
            final String INTENT_PROTOCOL_INTENT = "#Intent;";

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
    }

    @Override
    public void onMailTo(String url)
    {
        if (DailyTextUtils.isTextEmpty(url) == true)
        {
            return;
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
        startActivity(Intent.createChooser(emailIntent, "Send email…"));
    }

    @Override
    public void onScrollTop()
    {
        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(getViewInterface().smoothScrollTop().subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>()
        {
            @Override
            public void accept(Boolean aBoolean) throws Exception
            {
                unLockAll();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                unLockAll();
            }
        }));
    }

    @Override
    public void onHomeClick()
    {
        if (lock() == true)
        {
            return;
        }

        startActivity(DailyInternalDeepLink.getHomeScreenLink(getActivity()));
    }
}
