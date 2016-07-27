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
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

        mEventName = intent.getStringExtra(INTENT_EXTRA_DATA_EVENTNAME);

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
            SaleTime checkInSaleTime = saleTime.getClone(0);
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            int dailyDayOfDays;

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
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(checkInSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                if (nights <= 0 || dailyDayOfDays < 0)
                {
                    throw new NullPointerException("nights <= 0 || dailyDayOfDays < 0");
                }
            }

            checkInSaleTime.setOffsetDailyDay(dailyDayOfDays);

            Intent intent = StayDetailActivity.newInstance(EventWebActivity.this, checkInSaleTime, nights, hotelIndex, isShowCalendar);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_HOTEL_DETAIL);

            if (isShowCalendar == true)
            {
                AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.EVENT, null);
            }
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
            SaleTime gourmetSaleTime = saleTime.getClone(0);
            int fnbIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            int nights = 1;
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            int dailyDayOfDays;

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
                SimpleDateFormat format = new java.text.SimpleDateFormat("yyyyMMdd", Locale.KOREA);
                Date schemeDate = format.parse(date);
                Date dailyDate = format.parse(gourmetSaleTime.getDayOfDaysDateFormat("yyyyMMdd"));

                dailyDayOfDays = (int) ((schemeDate.getTime() - dailyDate.getTime()) / SaleTime.MILLISECOND_IN_A_DAY);

                ExLog.d(schemeDate + " / " + dailyDate + " / " + dailyDayOfDays);

                if (dailyDayOfDays < 0)
                {
                    throw new NullPointerException("dailyDayOfDays < 0");
                }
            }

            gourmetSaleTime.setOffsetDailyDay(dailyDayOfDays);

            Intent intent = GourmetDetailActivity.newInstance(EventWebActivity.this,//
                gourmetSaleTime, fnbIndex, isShowCalendar);

            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_PLACE_DETAIL);

            if (isShowCalendar == true)
            {
                AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION//
                    , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.EVENT, null);
            }
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

    private void downloadCoupon(final String couponCode, final String deepLink)
    {
        if (Util.isTextEmpty(couponCode, deepLink) == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        DailyNetworkAPI.getInstance(this).requestDownloadEventCoupon(mNetworkTag, couponCode, new DailyHotelJsonResponseListener()
        {
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

                    AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.COUPON_BOX//
                        , AnalyticsManager.Action.COUPON_DOWNLOAD_CLICKED, "Event-NULL", paramsMap);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

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

                        String validFrom = dataJSONObject.getString("validFrom");
                        String validTo = dataJSONObject.getString("validTo");

                        //                        String message = getString(R.string.message_eventweb_download_coupon//
                        //                            , Util.simpleDateFormatISO8601toFormat(validFrom, "yyyy.MM.dd")//
                        //                            , Util.simpleDateFormatISO8601toFormat(validTo, "yyyy.MM.dd"));

                        String message = getString(R.string.message_eventweb_download_coupon//
                            , DailyCalendar.convertDateFormatString(validFrom, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd")//
                            , DailyCalendar.convertDateFormatString(validTo, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));

                        recordAnalytics(couponCode, validTo);

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
                } finally
                {
                    unLockUI();
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                EventWebActivity.this.onErrorResponse(volleyError);
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

                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&dp=2";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&dp=3&n=4";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=5";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=5&ai=7";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=80&ios=1";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=80&ai=165&ios=1";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=45&cc=pension";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&d=20160710&n=2&pi=45&ai=12&ios=1&cc=pension&s=lp";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hl&dp=2&s=r";
                    //
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&dp=2";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&d=20160710";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&d=20160711&pi=5";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&d=20160712&pi=5&ai=7";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&d=20160714&s=hp";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gl&dp=2&s=lp";
                    //
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=m";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=rf";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=cl";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=su&rc=209329";

                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hebw&url=http%3A%2F%2Fm.dailyhotel.co.kr%2Fbanner%2F160701coupon%2F";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hd&i=981&d=20160718&n=5&cal=1";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=hd&i=981&d=20160718&n=3";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gd&i=50136&d=20160731&cal=1";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=5&v=gd&i=50136&d=20160721";

                    DailyDeepLink.getInstance().setDeepLink(Uri.parse(uri));

                    if (DailyDeepLink.getInstance().isValidateLink() == true)
                    {
                        AnalyticsManager.getInstance(EventWebActivity.this).recordDeepLink(DailyDeepLink.getInstance());

                        if (DailyDeepLink.getInstance().isHotelDetailView() == true)
                        {
                            if (deepLinkHotelDetail(mSaleTime) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isGourmetDetailView() == true)
                        {
                            if (deepLinkGourmetDetail(mSaleTime) == true)
                            {
                                return;
                            }
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

            if (DailyHotel.isLogin() == false)
            {
                startLogin();
            } else
            {
                EventWebActivity.this.downloadCoupon(couponCode, deepLink);
            }
        }
    }
}
