package com.twoheart.dailyhotel.screen.event;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.crashlytics.android.Crashlytics;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.screen.home.gourmet.detail.GourmetDetailActivity;
import com.daily.dailyhotel.screen.home.search.SearchActivity;
import com.daily.dailyhotel.screen.home.search.gourmet.result.SearchGourmetResultTabActivity;
import com.daily.dailyhotel.screen.home.search.stay.inbound.result.SearchStayResultTabActivity;
import com.daily.dailyhotel.screen.home.stay.inbound.detail.StayDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.detail.StayOutboundDetailActivity;
import com.daily.dailyhotel.screen.home.stay.outbound.list.StayOutboundListActivity;
import com.daily.dailyhotel.screen.mydaily.reward.RewardActivity;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.databinding.DialogShareDataBinding;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.RegisterCouponActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class EventWebActivity extends WebViewActivity implements Constants
{
    private static final String INTENT_EXTRA_DATA_EVENT_NAME = "eventName";
    private static final String INTENT_EXTRA_DATA_EVENT_DESCRIPTION = "eventDescription";
    private static final String INTENT_EXTRA_DATA_IMAGE_URL = "imageUrl";

    SourceType mSourceType;
    TodayDateTime mTodayDateTime;
    String mEventName;
    String mEventDescription;
    String mImageUrl;
    String mEventUrl;

    String mCouponCode;
    String mDeepLinkUrl;
    String mConfirmText;

    private CommonRemoteImpl mCommonRemoteImpl;

    JavaScriptExtention mJavaScriptExtention;

    public enum SourceType
    {
        EVENT,
        HOME_EVENT,
    }

    Handler mHandler = new Handler();

    public static Intent newInstance(Context context, SourceType sourceType, String url, String eventName, String eventDescription, String imageUrl)
    {
        if (sourceType == null || DailyTextUtils.isTextEmpty(url) == true)
        {
            return null;
        }

        Intent intent = new Intent(context, EventWebActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, url);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, sourceType.name());

        if (DailyTextUtils.isTextEmpty(eventName) == true)
        {
            eventName = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_EVENT_NAME, eventName);

        if (DailyTextUtils.isTextEmpty(eventDescription))
        {
            eventDescription = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_EVENT_DESCRIPTION, eventDescription);

        if (DailyTextUtils.isTextEmpty(imageUrl))
        {
            imageUrl = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_IMAGE_URL, imageUrl);

        return intent;
    }

    @JavascriptInterface
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCommonRemoteImpl = new CommonRemoteImpl(EventWebActivity.this);

        Intent intent = getIntent();

        mEventUrl = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);

        try
        {
            mSourceType = SourceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        if (DailyTextUtils.isTextEmpty(mEventUrl) == true)
        {
            finish();
            return;
        }

        requestCommonDatetime(mEventUrl);

        mEventName = intent.getStringExtra(INTENT_EXTRA_DATA_EVENT_NAME);
        mEventDescription = intent.getStringExtra(INTENT_EXTRA_DATA_EVENT_DESCRIPTION);
        mImageUrl = intent.getStringExtra(INTENT_EXTRA_DATA_IMAGE_URL);

        setContentView(R.layout.activity_event_web);

        initToolbar(mEventName);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setAppCacheEnabled(false); // 7.4 캐시 정책 비활성화.
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        // 추가
        mJavaScriptExtention = new JavaScriptExtention();

        webView.addJavascriptInterface(mJavaScriptExtention, "android");
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        setWebView(mEventUrl);

        //        initLayout((DailyWebView) webView);
    }

    private void initToolbar(String title)
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(title);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mWebView.canGoBack() == true)
                {
                    mWebView.goBack();
                } else
                {
                    finish();
                }
            }
        });

        if (DailyTextUtils.isTextEmpty(mEventUrl, mEventName) == false)
        {
            dailyToolbarView.addMenuItem(DailyToolbarView.MenuItem.SHARE, null, new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showShareDialog(new DialogInterface.OnDismissListener()
                    {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface)
                        {
                            unLockUI();
                        }
                    });
                }
            });
        }
    }

    void showShareDialog(DialogInterface.OnDismissListener listener)
    {
        DialogShareDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(EventWebActivity.this), R.layout.dialog_share_data, null, false);

        dataBinding.kakaoShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
                onShareKakaoClick();
            }
        });

        // 예약 내역의 경우 상세 링크로 인하여 혼선이 있을 것으로 보여 삭제하기로 함
        dataBinding.copyLinkView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                hideSimpleDialog();
                onCopyClipboardClick();
            }
        });

        dataBinding.moreShareView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                hideSimpleDialog();
                onMoreShareClick();
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

    void onShareKakaoClick()
    {
        try
        {
            lockUI();

            String longUrl = mEventUrl;

            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String shortUrl) throws Exception
                    {
                        unLockUI();

                        KakaoLinkManager.newInstance(EventWebActivity.this).shareEventWebView(mEventName //
                            , mEventDescription //
                            , shortUrl //
                            , mImageUrl);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        unLockUI();

                        KakaoLinkManager.newInstance(EventWebActivity.this).shareEventWebView(mEventName //
                            , mEventDescription //
                            , longUrl //
                            , mImageUrl);
                    }
                }));
        } catch (Exception e)
        {
            showSimpleDialog(null, getString(R.string.dialog_msg_not_installed_kakaotalk)//
                , getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no)//
                , new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Util.installPackage(EventWebActivity.this, "com.kakao.talk");
                    }
                }, null);

            unLockUI();
        }
    }

    void onCopyClipboardClick()
    {
        try
        {
            String longUrl = mEventUrl;

            lockUI();

            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockUI();

                    DailyTextUtils.clipText(EventWebActivity.this, shortUrl);

                    DailyToast.showToast(EventWebActivity.this, R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockUI();

                    DailyTextUtils.clipText(EventWebActivity.this, longUrl);

                    DailyToast.showToast(EventWebActivity.this, R.string.toast_msg_copy_link, DailyToast.LENGTH_LONG);
                }
            }));
        } catch (Exception e)
        {
            unLockUI();
            ExLog.d(e.toString());
        }
    }

    void onMoreShareClick()
    {
        try
        {
            lockUI();

            String longUrl = mEventUrl;

            String message = getString(R.string.message_detail_event_share_sms, mEventName, mEventDescription);
            addCompositeDisposable(mCommonRemoteImpl.getShortUrl(longUrl).subscribe(new Consumer<String>()
            {
                @Override
                public void accept(@NonNull String shortUrl) throws Exception
                {
                    unLockUI();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + shortUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }, new Consumer<Throwable>()
            {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception
                {
                    unLockUI();

                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");

                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    intent.putExtra(Intent.EXTRA_TEXT, message + longUrl);
                    Intent chooser = Intent.createChooser(intent, getString(R.string.label_doshare));
                    startActivity(chooser);
                }
            }));
        } catch (Exception e)
        {
            unLockUI();
            ExLog.d(e.toString());
        }
    }

    //    private void initLayout(final DailyWebView dailyWebView)
    //    {
    //        final View topButtonView = findViewById(R.id.topButtonView);
    //        topButtonView.setOnClickListener(new View.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(View v)
    //            {
    //                smoothScrollTop(dailyWebView);
    //            }
    //        });
    //
    //        topButtonView.setVisibility(View.GONE);
    //
    //        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
    //        {
    //            @Override
    //            public void onScroll(int l, int t, int oldl, int oldt)
    //            {
    //                if (t == 0)
    //                {
    //                    topButtonView.setVisibility(View.GONE);
    //                } else
    //                {
    //                    topButtonView.setVisibility(View.VISIBLE);
    //                }
    //            }
    //        });
    //
    //        View homeButtonView = findViewById(R.id.homeButtonView);
    //        homeButtonView.setOnClickListener(new View.OnClickListener()
    //        {
    //            @Override
    //            public void onClick(View v)
    //            {
    //                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
    //                finish();
    //            }
    //        });
    //    }

    private void requestCommonDatetime(final String url)
    {
        DailyMobileAPI.getInstance(this).requestCommonDateTime(mNetworkTag, new retrofit2.Callback<BaseDto<TodayDateTime>>()
        {
            @Override
            public void onResponse(Call<BaseDto<TodayDateTime>> call, Response<BaseDto<TodayDateTime>> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        BaseDto<TodayDateTime> baseDto = response.body();

                        if (baseDto.msgCode == 100)
                        {
                            mTodayDateTime = baseDto.data;
                        } else
                        {
                        }
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseDto<TodayDateTime>> call, Throwable t)
            {
            }
        });
    }

    @Override
    protected void onStart()
    {
        if (DailyTextUtils.isTextEmpty(mEventName) == true)
        {
            mEventName = AnalyticsManager.ValueType.EMPTY;
        }

        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.EVENT_NAME, mEventName);

        switch (mSourceType)
        {
            case EVENT:
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(this, Screen.EVENT_DETAIL, null);
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(this, Screen.EVENT_DETAIL, null, params);
                break;

            case HOME_EVENT:
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(this, Screen.HOME_EVENT_DETAIL, null);
                break;
        }

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_GOURMET_DETAIL:
            case CODE_REQUEST_ACTIVITY_STAY_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
            case CODE_REQUEST_ACTIVITY_COLLECTION:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_ACCOUNT_READY || resultCode == CODE_RESULT_ACTIVITY_PAYMENT_TIMEOVER)
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == RESULT_OK)
                {
                    downloadCoupon(mCouponCode, mDeepLinkUrl);
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_COUPONLIST:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
        }
    }

    boolean moveDeepLinkStayDetail(TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                Intent intent = StayDetailActivity.newInstance(EventWebActivity.this, dailyDeepLink.getDeepLink());

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            } else
            {

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

    boolean moveDeepLinkGourmetDetail(TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
    {
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                Intent intent = GourmetDetailActivity.newInstance(EventWebActivity.this, dailyDeepLink.getDeepLink());

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);
            } else
            {

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
        if (externalDeepLink == null)
        {
            return false;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(context, externalDeepLink.getDeepLink())//
            , CODE_REQUEST_ACTIVITY_SEARCH_RESULT);

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
            , CODE_REQUEST_ACTIVITY_SEARCH_RESULT);

        externalDeepLink.clear();

        return true;
    }

    boolean moveDeepLinkCouponList(Context context, DailyDeepLink dailyDeepLink)
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
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COUPONLIST);
            } else
            {

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
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                Intent intent = RegisterCouponActivity.newInstance(context, Screen.EVENT_DETAIL);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTER_COUPON);
            } else
            {

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
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                Intent intent = RewardActivity.newInstance(context);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_DAILY_REWARD);
            } else
            {

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

    boolean moveDeepLinkStayOutboundSearchResult(TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
    {
        if (todayDateTime == null || dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(StayOutboundListActivity.newInstance(EventWebActivity.this, dailyDeepLink.getDeepLink()));
            } else
            {

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
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(StayOutboundDetailActivity.newInstance(EventWebActivity.this, dailyDeepLink.getDeepLink()));
            } else
            {

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
        if (dailyDeepLink == null)
        {
            return false;
        }

        try
        {
            if (dailyDeepLink.isExternalDeepLink() == true)
            {
                startActivity(SearchActivity.newInstance(EventWebActivity.this, dailyDeepLink.getDeepLink()));
            } else
            {

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

    boolean moveDeepLinkCampaignTagListView(TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
    {
        if (todayDateTime == null || dailyDeepLink == null)
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
            } else
            {

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
        if (externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchStayResultTabActivity.newInstance(EventWebActivity.this, externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_STAY_SEARCH_RESULT);
    }

    void moveDeepLinkGourmetCampaignTag(DailyExternalDeepLink externalDeepLink)
    {
        if (externalDeepLink == null)
        {
            return;
        }

        startActivityForResult(SearchGourmetResultTabActivity.newInstance(EventWebActivity.this, externalDeepLink.getDeepLink())//
            , SearchActivity.REQUEST_CODE_GOURMET_SEARCH_RESULT);
    }

    void startLogin()
    {
        showSimpleDialog(null, getString(R.string.message_eventweb_do_login_download_coupon), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = LoginActivity.newInstance(EventWebActivity.this);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
            }
        }, null);
    }

    void downloadCoupon(final String couponCode, final String deepLink)
    {
        if (DailyTextUtils.isTextEmpty(couponCode, deepLink) == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        DailyMobileAPI.getInstance(this).requestDownloadEventCoupon(mNetworkTag, couponCode, new retrofit2.Callback<JSONObject>()
        {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
            {
                if (response != null && response.isSuccessful() && response.body() != null)
                {
                    try
                    {
                        JSONObject responseJSONObject = response.body();

                        int msgCode = responseJSONObject.getInt("msgCode");

                        if (msgCode == 100)
                        {
                            if (DailyTextUtils.isTextEmpty(mConfirmText) == true)
                            {
                                mConfirmText = getString(R.string.label_eventweb_now_used);
                            }

                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                            String validFrom = dataJSONObject.getString("validFrom");
                            String validTo = dataJSONObject.getString("validTo");
                            String message = getString(R.string.message_eventweb_download_coupon//
                                , DailyCalendar.convertDateFormatString(validFrom, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")//
                                , DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                            recordAnalytics(couponCode, validTo);

                            showSimpleDialog(null, message, mConfirmText, getString(R.string.dialog_btn_text_close), new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    if (mJavaScriptExtention == null)
                                    {
                                        return;
                                    }

                                    mJavaScriptExtention.internalLink(deepLink);
                                }
                            }, null);
                        } else
                        {
                            String message = responseJSONObject.getString("msg");
                            onErrorPopupMessage(msgCode, message, null);
                        }

                    } catch (ParseException e)
                    {
                        Crashlytics.log("Url: " + call.request().url().toString());
                        onError(e);
                    } catch (Exception e)
                    {
                        onError(e);
                    } finally
                    {
                        unLockUI();
                    }
                } else
                {
                    EventWebActivity.this.onErrorResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t)
            {
                EventWebActivity.this.onError(t);
            }

            private void recordAnalytics(String couponCode, String validTo)
            {
                try
                {
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(AnalyticsManager.KeyType.COUPON_NAME, AnalyticsManager.ValueType.EMPTY);
                    paramsMap.put(AnalyticsManager.KeyType.COUPON_AVAILABLE_ITEM, AnalyticsManager.ValueType.EMPTY);
                    paramsMap.put(AnalyticsManager.KeyType.PRICE_OFF, "0");
                    //                    paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, Util.simpleDateFormat(new Date(), "yyyyMMddHHmm"));
                    paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_DATE, DailyCalendar.format(new Date(), "yyyyMMddHHmm"));
                    //                    paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, Util.simpleDateFormatISO8601toFormat(validTo, "yyyyMMddHHmm"));
                    paramsMap.put(AnalyticsManager.KeyType.EXPIRATION_DATE, DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyyMMddHHmm"));
                    paramsMap.put(AnalyticsManager.KeyType.DOWNLOAD_FROM, "event");
                    paramsMap.put(AnalyticsManager.KeyType.COUPON_CODE, couponCode);
                    paramsMap.put(AnalyticsManager.KeyType.KIND_OF_COUPON, AnalyticsManager.ValueType.EMPTY);

                    AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                        , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "event-NULL", paramsMap);
                } catch (ParseException e)
                {
                    Crashlytics.log("requestDownloadEventCoupon::CouponCode: " + couponCode + ", validTo: " + validTo);
                    ExLog.d(e.toString());
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }
        });
    }

    /**
     * JavaScript
     *
     * @author Dailier
     */
    private class JavaScriptExtention
    {
        JavaScriptExtention()
        {
        }

        @JavascriptInterface
        public void externalLink(String packageName, String uri)
        {
            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
            marketLaunch.setData(Uri.parse(uri));

            if (marketLaunch.resolveActivity(getPackageManager()) == null)
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

        @JavascriptInterface
        public void internalLink(String uri)
        {
            switch (mSourceType)
            {
                case HOME_EVENT:
                {
                    if (mTodayDateTime == null)
                    {
                        break;
                    }

                    DailyDeepLink dailyDeepLink = DailyDeepLink.getNewInstance(Uri.parse(uri));

                    if (dailyDeepLink != null)
                    {
                        if (dailyDeepLink.isExternalDeepLink() == true)
                        {
                            DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                            AnalyticsManager.getInstance(EventWebActivity.this).recordDeepLink(externalDeepLink);

                            if (externalDeepLink.isHotelDetailView() == true)
                            {
                                if (moveDeepLinkStayDetail(mTodayDateTime, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isGourmetDetailView() == true)
                            {
                                if (moveDeepLinkGourmetDetail(mTodayDateTime, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isStaySearchResultView() == true)
                            {
                                if (moveDeepLinkStaySearchResult(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isGourmetSearchResultView() == true)
                            {
                                if (moveDeepLinkGourmetSearchResult(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isCouponView() == true)
                            {
                                if (moveDeepLinkCouponList(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isRegisterCouponView() == true)
                            {
                                if (moveDeepLinkRegisterCoupon(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isRewardView() == true)
                            {
                                if (moveDeepLinkReward(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isRewardView() == true)
                            {
                                if (moveDeepLinkReward(EventWebActivity.this, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isStayOutboundSearchResultView() == true)
                            {
                                if (moveDeepLinkStayOutboundSearchResult(mTodayDateTime, externalDeepLink) == true)
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
                                if (moveDeepLinkCampaignTagListView(mTodayDateTime, externalDeepLink) == true)
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

            Intent intent = new Intent(EventWebActivity.this, LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(uri));

            startActivity(intent);
        }

        @JavascriptInterface
        public void feed(String message)
        {
            if (isFinishing() == true)
            {
                return;
            }

            showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finish();
                }
            });
        }

        @JavascriptInterface
        public void downloadCoupon(String couponCode, String deepLink, String confirmText)
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
            } else
            {
                EventWebActivity.this.downloadCoupon(couponCode, deepLink);
            }
        }

        @JavascriptInterface
        public void enabledBenefitAlarm()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            boolean isBenefitAlarm = DailyUserPreference.getInstance(EventWebActivity.this).isBenefitAlarm();

            if (isBenefitAlarm == false)
            {
                // 자바 스크립트 호출시에 스레드가 다른것 같다. 에러나서 수정
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        lockUI();

                        DailyMobileAPI.getInstance(EventWebActivity.this).requestUpdateBenefitAgreement(mNetworkTag, true, new retrofit2.Callback<JSONObject>()
                        {
                            @Override
                            public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
                            {
                                if (response != null && response.isSuccessful() && response.body() != null)
                                {
                                    unLockUI();

                                    try
                                    {
                                        JSONObject responseJSONObject = response.body();

                                        int msgCode = responseJSONObject.getInt("msgCode");

                                        if (msgCode == 100)
                                        {

                                            JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");
                                            String serverDate = dataJSONObject.getString("serverDate");

                                            boolean isAgreed = Boolean.parseBoolean(call.request().url().queryParameter("isAgreed"));

                                            onBenefitAgreement(isAgreed, DailyCalendar.convertDateFormatString(serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일"));
                                        } else
                                        {
                                            String message = responseJSONObject.getString("msg");
                                            EventWebActivity.this.onErrorPopupMessage(msgCode, message);
                                        }
                                    } catch (ParseException e)
                                    {
                                        Crashlytics.log("Url: " + call.request().url().toString());
                                        EventWebActivity.this.onError(e);
                                    } catch (Exception e)
                                    {
                                        EventWebActivity.this.onError(e);
                                    }
                                } else
                                {
                                    EventWebActivity.this.onErrorResponse(call, response);
                                }
                            }

                            @Override
                            public void onFailure(Call<JSONObject> call, Throwable t)
                            {
                                EventWebActivity.this.onError(t);
                            }

                            public void onBenefitAgreement(final boolean isAgree, String updateDate)
                            {
                                DailyUserPreference.getInstance(EventWebActivity.this).setBenefitAlarm(isAgree);
                                AnalyticsManager.getInstance(EventWebActivity.this).setPushEnabled(isAgree, AnalyticsManager.ValueType.LAUNCH);

                                if (isAgree == true)
                                {
                                    // 혜택 알림 설정이 off --> on 일때
                                    String title = getString(R.string.label_setting_alarm);
                                    String message = getString(R.string.message_benefit_alarm_on_confirm_format, updateDate);
                                    String positive = getString(R.string.dialog_btn_text_confirm);

                                    showSimpleDialog(title, message, positive, null);

                                    AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, //
                                        AnalyticsManager.Action.NOTIFICATION_SETTING_CLICKED, AnalyticsManager.Label.ON, null);
                                }
                            }
                        });
                    }
                });
            } else
            {
                // 이미 햬택을 받고 계십니다.
                releaseUiComponent();

                showSimpleDialog(null, getString(R.string.dialog_msg_already_agree_benefit_on), getString(R.string.dialog_btn_text_confirm), null);
            }
        }
    }
}
