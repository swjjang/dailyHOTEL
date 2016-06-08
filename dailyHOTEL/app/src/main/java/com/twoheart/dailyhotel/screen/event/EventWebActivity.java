package com.twoheart.dailyhotel.screen.event;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.HotelDetailActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class EventWebActivity extends WebViewActivity implements Constants
{
    private static final String INTENT_EXTRA_DATA_EVENTNAME = "eventName";

    private SourceType mSourceType;
    private SaleTime mSaleTime;
    private String mEventName;

    private String mCouponCode;
    private String mDeepLinkUrl;
    private String mConfirmText;

    public enum SourceType
    {
        HOTEL_BANNER,
        GOURMET_BANNER,
        EVENT,
    }

    public static Intent newInstance(Context context, SourceType sourceType, String url, String eventName, SaleTime saleTime)
    {
        if (sourceType == null || Util.isTextEmpty(url) == true)
        {
            return null;
        }

        Intent intent = new Intent(context, EventWebActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, url);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, sourceType.name());
        intent.putExtra(NAME_INTENT_EXTRA_DATA_SALETIME, saleTime);

        if (Util.isTextEmpty(eventName) == true)
        {
            eventName = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_EVENTNAME, eventName);

        return intent;
    }

    @JavascriptInterface
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String url = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_URL);
        //        url = "http://mobile.dailyhotel.co.kr/link_test.html";

        try
        {
            mSourceType = SourceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        mSaleTime = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_SALETIME);

        if (Util.isTextEmpty(url) == true)
        {
            finish();
            return;
        }

        mEventName = intent.getParcelableExtra(INTENT_EXTRA_DATA_EVENTNAME);

        setContentView(R.layout.activity_event_web);

        initToolbar();

        WebView webView = (WebView) findViewById(R.id.webView);
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
        webView.addJavascriptInterface(new JavaScriptExtention(), "android");
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        setWebView(url);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_event_list_frag), new View.OnClickListener()
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
        if (Util.isTextEmpty(mEventName) == true)
        {
            mEventName = AnalyticsManager.ValueType.EMPTY;
        }

        Map<String, String> params = Collections.singletonMap(AnalyticsManager.KeyType.EVENT_NAME, mEventName);

        switch (mSourceType)
        {
            case HOTEL_BANNER:
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.DAILYHOTEL_BANNER_DETAIL);
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.DAILYHOTEL_BANNER_DETAIL, params);
                break;

            case GOURMET_BANNER:
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.DAILYGOURMET_BANNER_DETAIL);
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.DAILYGOURMET_BANNER_DETAIL, params);
                break;

            case EVENT:
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.EVENT_DETAIL);
                AnalyticsManager.getInstance(EventWebActivity.this).recordScreen(Screen.EVENT_DETAIL, params);
                break;
        }

        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
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
        }
    }

    private boolean deepLinkHotelDetail(SaleTime saleTime)
    {
        boolean result = true;

        try
        {
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = saleTime.getDailyTime();
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            int dailyDayOfDays = 0;

            if (Util.isTextEmpty(date) == true)
            {
                if (datePlus >= 0)
                {
                    dailyDayOfDays = datePlus;

                    if (nights <= 0 || dailyDayOfDays < 0)
                    {
                        throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
                    }
                } else
                {
                    throw new NullPointerException("datePlus < 0");
                }
            } else
            {
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (nights <= 0 || dailyDayOfDays < 0)
                {
                    throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
                }
            }

            Intent intent = new Intent(EventWebActivity.this, HotelDetailActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
            intent.putExtra(NAME_INTENT_EXTRA_DATA_HOTELIDX, hotelIndex);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);
        } catch (Exception e)
        {
            result = false;
            ExLog.d(e.toString());
        } finally
        {
            DailyDeepLink.getInstance().clear();
        }

        return result;
    }

    private boolean deepLinkGourmetDetail(SaleTime saleTime)
    {
        boolean result = true;

        try
        {
            int fnbIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            long dailyTime = saleTime.getDailyTime();
            int nights = 1;

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            int dailyDayOfDays = 0;

            if (Util.isTextEmpty(date) == true)
            {
                if (datePlus >= 0)
                {
                    dailyDayOfDays = datePlus;
                } else
                {
                    throw new NullPointerException("datePlus < 0");
                }
            } else
            {
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd");
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(saleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (dailyDayOfDays < 0)
                {
                    throw new NullPointerException("dailyDayOfDays < 0");
                }
            }

            Intent intent = new Intent(EventWebActivity.this, GourmetDetailActivity.class);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, "share");
            intent.putExtra(NAME_INTENT_EXTRA_DATA_PLACEIDX, fnbIndex);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAILYTIME, dailyTime);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_DAYOFDAYS, dailyDayOfDays);
            intent.putExtra(NAME_INTENT_EXTRA_DATA_NIGHTS, nights);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);
        } catch (Exception e)
        {
            result = false;
            ExLog.d(e.toString());
        } finally
        {
            DailyDeepLink.getInstance().clear();
        }

        return result;
    }

    private void startLogin()
    {
        showSimpleDialog(null, getString(R.string.message_eventweb_do_login_download_coupon), getString(R.string.dialog_btn_text_yes), getString(R.string.dialog_btn_text_no), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(EventWebActivity.this, LoginActivity.class);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
            }
        }, null);
    }

    private void downloadCoupon(String couponCode, final String deepLink)
    {
        if (Util.isTextEmpty(couponCode, deepLink) == true)
        {
            return;
        }

        DailyNetworkAPI.getInstance(this).requestDownloadCoupon(mNetworkTag, couponCode, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, JSONObject response)
            {
                try
                {
                    int msgCode = response.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        if (Util.isTextEmpty(mConfirmText) == true)
                        {
                            mConfirmText = getString(R.string.label_eventweb_now_used);
                        }

                        JSONObject dataJSONObject = response.getJSONObject("data");

                        String validTo = dataJSONObject.getString("validTo");
                        String validFrom = dataJSONObject.getString("validFrom");

                        String message = getString(R.string.message_eventweb_download_coupon//
                            , Util.simpleDateFormatISO8601toFormat(validTo, "yyyy.MM.dd")//
                            , Util.simpleDateFormatISO8601toFormat(validFrom, "yyyy.MM.dd"));

                        showSimpleDialog(null, message, mConfirmText, getString(R.string.dialog_btn_text_close), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                DailyDeepLink dailyDeepLink = DailyDeepLink.getInstance();
                                dailyDeepLink.setDeepLink(Uri.parse(deepLink));

                                if (dailyDeepLink.isHotelDetailView() == true)
                                {
                                    if (deepLinkHotelDetail(mSaleTime) == true)
                                    {
                                        return;
                                    }
                                } else if (dailyDeepLink.isGourmetDetailView() == true)
                                {
                                    if (deepLinkGourmetDetail(mSaleTime) == true)
                                    {
                                        return;
                                    }
                                } else
                                {
                                    Intent intent = new Intent(EventWebActivity.this, LauncherActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setData(Uri.parse(deepLink));

                                    startActivity(intent);
                                }
                            }
                        }, null);
                    } else
                    {
                        String message = response.getString("msg");
                        onErrorPopupMessage(msgCode, message, null);
                    }
                } catch (Exception e)
                {
                    onError(e);
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                onErrorResponse(volleyError);
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
        @JavascriptInterface
        public void externalLink(String packageName, String uri)
        {
            Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
            marketLaunch.setData(Uri.parse(uri));

            if (marketLaunch.resolveActivity(getPackageManager()) == null)
            {
                String marketUrl;

                if (RELEASE_STORE == Stores.PLAY_STORE || RELEASE_STORE == Stores.N_STORE)
                {
                    marketUrl = String.format("https://play.google.com/store/apps/details?id=%s", packageName);
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
        public void interlLink(String uri)
        {
            switch (mSourceType)
            {
                case HOTEL_BANNER:
                case GOURMET_BANNER:
                {
                    if (mSaleTime == null)
                    {
                        break;
                    }

                    DailyDeepLink dailyDeepLink = DailyDeepLink.getInstance();
                    dailyDeepLink.setDeepLink(Uri.parse(uri));

                    if (dailyDeepLink.isHotelDetailView() == true)
                    {
                        if (deepLinkHotelDetail(mSaleTime) == true)
                        {
                            return;
                        }
                    } else if (dailyDeepLink.isGourmetDetailView() == true)
                    {
                        if (deepLinkGourmetDetail(mSaleTime) == true)
                        {
                            return;
                        }
                    }
                    break;
                }

                case EVENT:
                    break;
            }

            Intent intent = new Intent(EventWebActivity.this, LauncherActivity.class);
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
            if (Util.isTextEmpty(couponCode, deepLink) == true)
            {
                return;
            }

            mCouponCode = couponCode;
            mDeepLinkUrl = deepLink;
            mConfirmText = confirmText;

            if (Util.isTextEmpty(DailyPreference.getInstance(EventWebActivity.this).getAuthorization()) == true)
            {
                startLogin();
            } else
            {
                EventWebActivity.this.downloadCoupon(couponCode, deepLink);
            }
        }
    }
}
