package com.daily.dailyhotel.screen.common.event;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.JsResult;

import com.daily.base.exception.BaseException;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.base.BaseExceptionPresenter;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.DownloadCouponResult;
import com.daily.dailyhotel.entity.Notification;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.CouponRemoteImpl;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.mydaily.reward.RewardActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.screen.mydaily.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.RegisterCouponActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyInternalDeepLink;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class EventWebPresenter extends BaseExceptionPresenter<EventWebActivity, EventWebInterface.ViewInterface> implements EventWebInterface.OnEventListener
{
    EventWebInterface.AnalyticsInterface mAnalytics;

    CommonRemoteImpl mCommonRemoteImpl;
    private CouponRemoteImpl mCouponRemoteImpl;

    private EventWebActivity.EventType mEventType;
    CommonDateTime mCommonDateTime;
    String mEventName;
    String mEventDescription;
    String mEventUrl;
    String mImageUrl;

    private String mCouponCode;
    private String mDeepLinkUrl;
    String mConfirmText;

    public EventWebPresenter(@NonNull EventWebActivity activity)
    {
        super(activity);
    }

    @NonNull
    @Override
    protected EventWebInterface.ViewInterface createInstanceViewInterface()
    {
        return new EventWebView(getActivity(), this);
    }

    @Override
    public void constructorInitialize(EventWebActivity activity)
    {
        setContentView(R.layout.activity_web_data);

        mAnalytics = new EventWebAnalyticsImpl();

        mCommonRemoteImpl = new CommonRemoteImpl();
        mCouponRemoteImpl = new CouponRemoteImpl();

        setRefresh(true);
    }

    @Override
    public boolean onIntent(Intent intent)
    {
        if (intent == null)
        {
            return true;
        }

        mEventUrl = intent.getStringExtra(EventWebActivity.INTENT_EXTRA_DATA_URL);

        try
        {
            mEventType = EventWebActivity.EventType.valueOf(intent.getStringExtra(EventWebActivity.INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(getActivity());
            return false;
        }

        if (DailyTextUtils.isTextEmpty(mEventUrl) == true)
        {
            return false;
        }

        mEventName = intent.getStringExtra((EventWebActivity.INTENT_EXTRA_DATA_EVENT_NAME));
        mEventDescription = intent.getStringExtra((EventWebActivity.INTENT_EXTRA_DATA_EVENT_DESCRIPTION));
        mImageUrl = intent.getStringExtra((EventWebActivity.INTENT_EXTRA_DATA_IMAGE_URL));

        return true;
    }

    @Override
    public void onNewIntent(Intent intent)
    {

    }

    @Override
    public void onPostCreate()
    {
        getViewInterface().setToolbarTitle(mEventName);
        getViewInterface().setShareButtonVisible(DailyTextUtils.isTextEmpty(mEventUrl, mEventName) == false);

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Os-Type", "android");
        headerMap.put("App-Version", DailyHotel.VERSION);
        headerMap.put("App-VersionCode", DailyHotel.VERSION_CODE);
        headerMap.put("ga-id", DailyHotel.GOOGLE_ANALYTICS_CLIENT_ID);

        getViewInterface().loadUrl(mEventUrl, headerMap);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if (isRefresh() == true)
        {
            onRefresh(true);
        }

        try
        {
            mAnalytics.onScreen(getActivity(), mEventName, mEventType, mEventUrl);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
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

        switch (requestCode)
        {
            case Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case Constants.CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case Constants.CODE_REQUEST_ACTIVITY_COLLECTION:
            {
                setResult(resultCode);

                if (resultCode == Activity.RESULT_OK || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == Constants.CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    onDownloadCoupon(mCouponCode, mDeepLinkUrl, mConfirmText);
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_COUPONLIST:
                if (resultCode == Constants.CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
        }
    }

    @Override
    protected synchronized void onRefresh(boolean showProgress)
    {
        if (getActivity().isFinishing() == true || isRefresh() == false)
        {
            return;
        }

        setRefresh(false);

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime() //
            .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CommonDateTime>()
            {
                @Override
                public void accept(CommonDateTime commonDateTime) throws Exception
                {
                    mCommonDateTime = commonDateTime;
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    ExLog.d(throwable.toString());
                }
            }));
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

    @Override
    public void onShareClick()
    {
        if (lock())
        {
            return;
        }

        getViewInterface().showShareDialog(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                unLockAll();
            }
        });

        try
        {
            mAnalytics.onShareClick(getActivity(), mEventUrl);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onShareKakaoClick()
    {
        try
        {
            //            if (lock())
            //            {
            //                return;
            //            }

            String longUrl = mEventUrl;

            // 카카오톡 패키지 설치 여부
            getActivity().getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String shortUrl) throws Exception
                    {
                        unLockAll();

                        KakaoLinkManager.newInstance(getActivity()).shareEventWebView(mEventName //
                            , mEventDescription //
                            , shortUrl //
                            , mImageUrl);

                        try
                        {
                            mAnalytics.onShareKakaoClick(getActivity(), mEventUrl);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockAll();

                        KakaoLinkManager.newInstance(getActivity()).shareEventWebView(mEventName //
                            , mEventDescription //
                            , longUrl //
                            , mImageUrl);

                        try
                        {
                            mAnalytics.onShareKakaoClick(getActivity(), mEventUrl);
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    }
                }));
        } catch (Exception e)
        {
            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(getActivity(), "com.kakao.talk");
                    }
                }, null);

            unLockAll();
        }
    }

    @Override
    public void onCopyLinkClick()
    {
        try
        {
            String longUrl = mEventUrl;

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    DailyTextUtils.clipText(getActivity(), shortUrl);

                    DailyToast.showToast(getActivity(), R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);

                    try
                    {
                        mAnalytics.onShareCopyLinkClick(getActivity(), mEventUrl);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    DailyTextUtils.clipText(getActivity(), longUrl);

                    DailyToast.showToast(getActivity(), R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);

                    try
                    {
                        mAnalytics.onShareCopyLinkClick(getActivity(), mEventUrl);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }));
        } catch (Exception e)
        {
            unLockAll();
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onMoreShareClick()
    {
        try
        {
            String longUrl = mEventUrl;

            if (DailyTextUtils.isTextEmpty(mEventDescription) == false)
            {
                mEventDescription = mEventDescription.replace("\n" + longUrl, "");
            }

            String message = getString(R.string.message_detail_event_share_sms, mEventName, mEventDescription);
            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + shortUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);

                    try
                    {
                        mAnalytics.onShareSeeMoreClick(getActivity(), mEventUrl);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockAll();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + longUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);

                    try
                    {
                        mAnalytics.onShareSeeMoreClick(getActivity(), mEventUrl);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }));
        } catch (Exception e)
        {
            unLockAll();
            ExLog.d(e.toString());
        }
    }

    @Override
    public void onDownloadCoupon(String couponCode, String deepLink, String confirmText)
    {
        if (DailyTextUtils.isTextEmpty(couponCode, deepLink) == true)
        {
            return;
        }

        mCouponCode = couponCode;
        mDeepLinkUrl = deepLink;
        mConfirmText = confirmText;

        if (DailyHotel.isLogin() == false)
        {
            startLogin();
            return;
        }

        if (lock() == true)
        {
            return;
        }

        addCompositeDisposable(mCouponRemoteImpl.getDownloadCoupon(couponCode).observeOn(AndroidSchedulers.mainThread()) //
            .subscribe(new Consumer<DownloadCouponResult>()
            {
                @Override
                public void accept(DownloadCouponResult downloadCouponResult) throws Exception
                {
                    if (DailyTextUtils.isTextEmpty(mConfirmText) == true)
                    {
                        mConfirmText = getString(R.string.label_eventweb_now_used);
                    }

                    String message = getString(R.string.message_eventweb_download_coupon//
                        , DailyCalendar.convertDateFormatString(downloadCouponResult.validFrom, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")//
                        , DailyCalendar.convertDateFormatString(downloadCouponResult.validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                    mAnalytics.onDownLoadCoupon(getActivity(), couponCode, downloadCouponResult.validTo);

                    getViewInterface().showSimpleDialog(null, message, mConfirmText, getString(R.string.dialog_btn_text_close), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            onInternalLink(deepLink);
                        }
                    }, null);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(Throwable throwable) throws Exception
                {
                    if (throwable instanceof BaseException)
                    {
                        // 팝업 에러 보여주기
                        BaseException baseException = (BaseException) throwable;

                        getViewInterface().showSimpleDialog(null, baseException.getMessage()//
                            , getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // 이미 다운 로드 받은 경우 딥링크를 호출 한다.
                                    if (baseException.getCode() == 300)
                                    {
                                        onInternalLink(deepLink);
                                    }
                                }
                            }, null, null, new DialogInterface.OnDismissListener()
                            {
                                @Override
                                public void onDismiss(DialogInterface dialog)
                                {
                                    unLockAll();
                                }
                            }, true);
                    } else
                    {
                        onHandleError(throwable);
                    }
                }
            }));
    }

    @Override
    public void onExternalLink(String packageName, String uri)
    {
        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
        marketLaunch.setData(Uri.parse(uri));

        if (marketLaunch.resolveActivity(getActivity().getPackageManager()) == null)
        {
            String marketUrl;

            if (Setting.getStore() == Setting.Stores.PLAY_STORE)
            {
                marketUrl = String.format(Locale.KOREA, "https://play.google.com/store/apps/details?id=%s", packageName);
                marketLaunch.setData(Uri.parse(marketUrl));
            }
        }

        try
        {
            startActivity(marketLaunch);
        } catch (ActivityNotFoundException e)
        {

        }
    }

    @Override
    public void onInternalLink(String uri)
    {
        switch (mEventType)
        {
            case HOME_EVENT:
            {
                if (mCommonDateTime == null)
                {
                    break;
                }

                DailyDeepLink dailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(uri));

                if (dailyDeepLink != null)
                {
                    if (dailyDeepLink.isExternalDeepLink() == true)
                    {
                        DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                        mAnalytics.onRecordDeepLink(getActivity(), externalDeepLink);

                        if (externalDeepLink.isHotelDetailView() == true)
                        {
                            if (moveDeepLinkStayDetail(externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isGourmetDetailView() == true)
                        {
                            if (moveDeepLinkGourmetDetail(externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isStaySearchResultView() == true)
                        {
                            if (moveDeepLinkStaySearchResult(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isGourmetSearchResultView() == true)
                        {
                            if (moveDeepLinkGourmetSearchResult(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isCouponView() == true)
                        {
                            if (moveDeepLinkCouponList(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isRegisterCouponView() == true)
                        {
                            if (moveDeepLinkRegisterCoupon(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isRewardView() == true)
                        {
                            if (moveDeepLinkReward(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isRewardView() == true)
                        {
                            if (moveDeepLinkReward(getActivity(), externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
                        {
                            if (moveDeepLinkStayOutboundSearchResult(externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isPlaceDetailView() == true && DailyDeepLink.STAY_OUTBOUND.equalsIgnoreCase(externalDeepLink.getPlaceType()) == true)
                        {
                            if (moveDeepLinkStayOutboundDetail(externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isSearchHomeView() == true)
                        {
                            if (moveDeepLinkSearchHome(externalDeepLink) == true)
                            {
                                return;
                            }
                        } else if (externalDeepLink.isCampaignTagListView() == true)
                        {
                            if (moveDeepLinkCampaignTagListView(externalDeepLink) == true)
                            {
                                return;
                            }
                        }
                    } else
                    {

                    }
                }
                break;
            }

            case EVENT:
                break;
        }

        Intent intent = new Intent(getActivity(), LauncherActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(uri));

        startActivity(intent);
    }

    @Override
    public void onFeed(String message)
    {
        if (getActivity().isFinishing() == true)
        {
            return;
        }

        getViewInterface().showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
    }

    @Override
    public void onEnabledBenefitAlarm()
    {
        if (lock() == true)
        {
            return;
        }

        boolean isBenefitAlarm = DailyUserPreference.getInstance(getActivity()).isBenefitAlarm();

        if (isBenefitAlarm == false)
        {
            // 자바 스크립트 호출시에 스레드가 다른것 같다. 에러나서 수정
            new Handler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    addCompositeDisposable(mCommonRemoteImpl.updateNotification(true) //
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Notification>()
                        {
                            @Override
                            public void accept(Notification notification) throws Exception
                            {
                                unLockAll();

                                DailyUserPreference.getInstance(getActivity()).setBenefitAlarm(notification.agreed);
                                AnalyticsManager.getInstance(getActivity()).setPushEnabled(notification.agreed, AnalyticsManager.ValueType.LAUNCH);

                                if (notification.agreed == true)
                                {
                                    // 혜택 알림 설정이 off --> on 일때
                                    String title = getString(R.string.label_setting_alarm);
                                    String message = getString(R.string.message_benefit_alarm_on_confirm_format, notification.serverDate);
                                    String positive = getString(R.string.dialog_btn_text_confirm);

                                    getViewInterface().showSimpleDialog(title, message, positive, null);

                                    AnalyticsManager.getInstance(getActivity()).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                                        AnalyticsManager.Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);
                                }
                            }
                        }, new Consumer<Throwable>()
                        {
                            @Override
                            public void accept(Throwable throwable) throws Exception
                            {
                                onHandleError(throwable);
                            }
                        }));
                }
            });
        } else
        {
            // 이미 햬택을 받고 계십니다.
            unLockAll();

            getViewInterface().showSimpleDialog(null, getString(R.string.dialog_msg_already_agree_benefit_on), getString(R.string.dialog_btn_text_confirm), null);
        }
    }

    @Override
    public void onScreenLock()
    {
        screenLock(true);
    }

    @Override
    public void onUnlockAll()
    {
        unLockAll();
    }

    boolean moveDeepLinkStayDetail(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || getActivity() == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                Intent intent = StayDetailActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink());

                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkGourmetDetail(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || getActivity() == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                Intent intent = GourmetDetailActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink());

                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkStaySearchResult(Context context, DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null || context == null)
        {
            return false;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(context, externalDeepLink.getDeepLink())//
            , Constants.CODE_REQUEST_ACTIVITY_SEARCH_RESULT);

        externalDeepLink.clear();

        return true;
    }

    boolean moveDeepLinkGourmetSearchResult(Context context, DailyExternalDeepLink externalDeepLink)
    {
        if (context == null || externalDeepLink == null)
        {
            return false;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(context, externalDeepLink.getDeepLink())//
            , Constants.CODE_REQUEST_ACTIVITY_SEARCH_RESULT);

        externalDeepLink.clear();

        return true;
    }

    boolean moveDeepLinkCouponList(Context context, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || context == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                CouponListActivity.SortType sortType;

                String placeType = externalDeepLink.getPlaceType();

                if (DailyTextUtils.isTextEmpty(placeType) == true)
                {
                    sortType = CouponListActivity.SortType.ALL;
                } else
                {
                    try
                    {
                        sortType = CouponListActivity.SortType.valueOf(placeType.toUpperCase());
                    } catch (Exception e)
                    {
                        sortType = CouponListActivity.SortType.ALL;
                    }
                }

                Intent intent = CouponListActivity.newInstance(context, sortType, dailyDeepLink.getDeepLink());
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_COUPONLIST);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkRegisterCoupon(Context context, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || context == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                //                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                Intent intent = RegisterCouponActivity.newInstance(context, AnalyticsManager.Screen.EVENT_DETAIL);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_REGISTER_COUPON);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkReward(Context context, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || context == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                //                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                Intent intent = RewardActivity.newInstance(context);
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_DAILY_REWARD);
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkStayOutboundSearchResult(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || getActivity() == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(StayOutboundListActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink()));
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkStayOutboundDetail(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || getActivity() == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(StayOutboundDetailActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink()));
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkSearchHome(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null || getActivity() == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(SearchActivity.newInstance(getActivity(), dailyDeepLink.getDeepLink()));
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    boolean moveDeepLinkCampaignTagListView(DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                switch (externalDeepLink.getPlaceType())
                {
                    case DailyDeepLink.STAY:
                        moveDeepLinkStayCampaignTag(externalDeepLink);
                        break;

                    case DailyDeepLink.GOURMET:
                        moveDeepLinkGourmetCampaignTag(externalDeepLink);
                        break;
                }
            }
        } catch (Exception e)
        {
            ExLog.e(e.toString());
            return false;
        } finally
        {
            dailyDeepLink.clear();
        }

        return true;
    }

    void moveDeepLinkStayCampaignTag(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null || getActivity() == null)
        {
            return;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
    }

    void moveDeepLinkGourmetCampaignTag(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null || getActivity() == null)
        {
            return;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(getActivity(), externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void startLogin()
    {
        if (getActivity() == null)
        {
            return;
        }

        getViewInterface().showSimpleDialog(null, getString(R.string.message_eventweb_do_login_download_coupon) //
            , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no) //
            , v -> {
                Intent intent = LoginActivity.newInstance(getActivity());
                startActivityForResult(intent, Constants.CODE_REQUEST_ACTIVITY_LOGIN);
            }, null);
    }
}
