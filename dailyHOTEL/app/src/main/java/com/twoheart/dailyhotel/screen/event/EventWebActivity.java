package com.twoheart.dailyhotel.screen.event;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.SaleTime;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.information.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.information.coupon.RegisterCouponActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.analytics.AppboyManager;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyWebView;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

    private JavaScriptExtention mJavaScriptExtention;

    public enum SourceType
    {
        HOTEL_BANNER,
        GOURMET_BANNER,
        EVENT,
    }

    private Handler mHandler = new Handler();

    public static Intent newInstance(Context context, SourceType sourceType, String url, String eventName)
    {
        if (sourceType == null || Util.isTextEmpty(url) == true)
        {
            return null;
        }

        Intent intent = new Intent(context, EventWebActivity.class);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_URL, url);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_TYPE, sourceType.name());

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

        try
        {
            mSourceType = SourceType.valueOf(intent.getStringExtra(NAME_INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        if (Util.isTextEmpty(url) == true)
        {
            finish();
            return;
        }

        requestCommonDatetime(url);

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
        mJavaScriptExtention = new JavaScriptExtention();

        webView.addJavascriptInterface(mJavaScriptExtention, "android");
        webView.clearCache(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        setWebView(url);

        initLayout((DailyWebView) webView);
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

    private void initLayout(final DailyWebView dailyWebView)
    {
        final View topButtonView = findViewById(R.id.topButtonView);
        topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dailyWebView.setScrollY(0);
            }
        });

        topButtonView.setVisibility(View.INVISIBLE);

        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    topButtonView.setVisibility(View.INVISIBLE);
                } else
                {
                    topButtonView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void requestCommonDatetime(final String url)
    {
        DailyNetworkAPI.getInstance(this).requestCommonDatetime(mNetworkTag, new DailyHotelJsonResponseListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }

            @Override
            public void onResponse(String url, JSONObject response)
            {
                try
                {
                    long currentDateTime = response.getLong("currentDateTime");
                    long dailyDateTime = response.getLong("dailyDateTime");

                    if (mSaleTime == null)
                    {
                        mSaleTime = new SaleTime();
                    }

                    mSaleTime.setCurrentTime(currentDateTime);
                    mSaleTime.setDailyTime(dailyDateTime);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
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
            case CODE_REQUEST_ACTIVITY_PLACE_DETAIL:
            case CODE_REQUEST_ACTIVITY_HOTEL_DETAIL:
            case CODE_REQUEST_ACTIVITY_SEARCH_RESULT:
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
            // 신규 타입의 화면이동
            int hotelIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());
            int nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());

            if (nights <= 0)
            {
                nights = 1;
            }

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();

            DailyDeepLink.getInstance().clear();

            if (Util.isTextEmpty(date) == false)
            {
                saleTime = SaleTime.changeDateSaleTime(saleTime, date);
            } else if (datePlus >= 0)
            {
                saleTime.setOffsetDailyDay(datePlus);
            }

            if (saleTime == null)
            {
                return false;
            }

            Intent intent = StayDetailActivity.newInstance(EventWebActivity.this, saleTime, nights, hotelIndex, isShowCalendar);

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
            int gourmetIndex = Integer.parseInt(DailyDeepLink.getInstance().getIndex());

            String date = DailyDeepLink.getInstance().getDate();
            int datePlus = DailyDeepLink.getInstance().getDatePlus();
            boolean isShowCalendar = DailyDeepLink.getInstance().isShowCalendar();

            // date가 비어 있는 경우
            if (Util.isTextEmpty(date) == false)
            {
                saleTime = SaleTime.changeDateSaleTime(saleTime, date);
            } else if (datePlus >= 0)
            {
                saleTime.setOffsetDailyDay(datePlus);
            }

            if (saleTime == null)
            {
                return false;
            }

            Intent intent = GourmetDetailActivity.newInstance(EventWebActivity.this,//
                saleTime, gourmetIndex, isShowCalendar);

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

    private boolean moveDeepLinkStaySearchResult(Context context, SaleTime saleTime)
    {
        String word = DailyDeepLink.getInstance().getSearchWord();
        DailyDeepLink.SearchType searchType = DailyDeepLink.getInstance().getSearchLocationType();
        LatLng latLng = DailyDeepLink.getInstance().getLatLng();
        double radius = DailyDeepLink.getInstance().getRadius();

        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();
        int nights = 1;

        try
        {
            nights = Integer.parseInt(DailyDeepLink.getInstance().getNights());
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        } finally
        {
            if (nights <= 0)
            {
                nights = 1;
            }
        }

        DailyDeepLink.getInstance().clear();

        SaleTime checkInSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);

            if (checkInSaleTime == null)
            {
                return false;
            }

        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            // 날짜 정보가 없는 경우 예외 처리 추가
            try
            {
                checkInSaleTime = saleTime;
            } catch (Exception e)
            {
                return false;
            }
        }

        if (checkInSaleTime == null)
        {
            return false;
        }

        switch (searchType)
        {
            case LOCATION:
            {
                if (latLng != null)
                {
                    Intent intent = StaySearchResultActivity.newInstance(context, checkInSaleTime, nights, latLng, radius, true);
                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
            }

            default:
                if (Util.isTextEmpty(word) == false)
                {
                    Intent intent = StaySearchResultActivity.newInstance(context, checkInSaleTime, nights, new Keyword(0, word), SearchType.SEARCHES);
                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
        }

        return true;
    }

    private boolean moveDeepLinkGourmetSearchResult(Context context, SaleTime saleTime)
    {
        String word = DailyDeepLink.getInstance().getSearchWord();
        DailyDeepLink.SearchType searchType = DailyDeepLink.getInstance().getSearchLocationType();
        LatLng latLng = DailyDeepLink.getInstance().getLatLng();
        double radius = DailyDeepLink.getInstance().getRadius();

        String date = DailyDeepLink.getInstance().getDate();
        int datePlus = DailyDeepLink.getInstance().getDatePlus();

        DailyDeepLink.getInstance().clear();

        SaleTime checkInSaleTime;

        // 날짜가 있는 경우 디폴트로 3번째 탭으로 넘어가야 한다
        if (Util.isTextEmpty(date) == false)
        {
            checkInSaleTime = SaleTime.changeDateSaleTime(saleTime, date);

            if (checkInSaleTime == null)
            {
                return false;
            }

        } else if (datePlus >= 0)
        {
            try
            {
                checkInSaleTime = saleTime.getClone(datePlus);
            } catch (Exception e)
            {
                return false;
            }
        } else
        {
            // 날짜 정보가 없는 경우 예외 처리 추가
            try
            {
                checkInSaleTime = saleTime;
            } catch (Exception e)
            {
                return false;
            }
        }

        if (checkInSaleTime == null)
        {
            return false;
        }

        switch (searchType)
        {
            case LOCATION:
            {
                if (latLng != null)
                {
                    Intent intent = GourmetSearchResultActivity.newInstance(context, checkInSaleTime, latLng, radius, true);
                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
            }

            default:
                if (Util.isTextEmpty(word) == false)
                {
                    Intent intent = GourmetSearchResultActivity.newInstance(context, checkInSaleTime, new Keyword(0, word), SearchType.SEARCHES);
                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                } else
                {
                    return false;
                }
                break;
        }

        return true;
    }

    private boolean moveDeepLinkCouponList(Context context)
    {
        DailyDeepLink.getInstance().clear();

        Intent intent = CouponListActivity.newInstance(context);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_COUPONLIST);

        return true;
    }

    private boolean moveDeepLinkRegisterCoupon(Context context)
    {
        DailyDeepLink.getInstance().clear();

        Intent intent = RegisterCouponActivity.newInstance(context, Screen.EVENT_DETAIL);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_REGISTER_COUPON);

        return true;
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

                if (RELEASE_STORE == Stores.PLAY_STORE)
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
            internalLink(uri);
        }

        @JavascriptInterface
        public void internalLink(String uri)
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

                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=6&v=hsr&dp=2&n=1&w=라마다";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=6&v=hsr&d=20160825&n=1&w=라마다";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=6&v=gsr&dp=2&w=뷔페";
                    //                    uri = "dailyhotel://dailyhotel.co.kr?vc=6&v=gsr&d=20160825&w=뷔페";

                    DailyDeepLink.getInstance().setDeepLink(Uri.parse(uri));

                    if (DailyDeepLink.getInstance().isValidateLink() == true)
                    {
                        AnalyticsManager.getInstance(EventWebActivity.this).recordDeepLink(DailyDeepLink.getInstance());

                        if (DailyDeepLink.getInstance().isHotelDetailView() == true)
                        {
                            if (deepLinkHotelDetail(mSaleTime.getClone(0)) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isGourmetDetailView() == true)
                        {
                            if (deepLinkGourmetDetail(mSaleTime.getClone(0)) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isHotelSearchResultView() == true)
                        {
                            if (moveDeepLinkStaySearchResult(EventWebActivity.this, mSaleTime.getClone(0)) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isGourmetSearchResultView() == true)
                        {
                            if (moveDeepLinkGourmetSearchResult(EventWebActivity.this, mSaleTime.getClone(0)) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isCouponView() == true)
                        {
                            if (moveDeepLinkCouponList(EventWebActivity.this) == true)
                            {
                                return;
                            }
                        } else if (DailyDeepLink.getInstance().isRegisterCouponView() == true)
                        {
                            if (moveDeepLinkRegisterCoupon(EventWebActivity.this) == true)
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

        @JavascriptInterface
        public void enabledBenefitAlarm()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            boolean isBenefitAlarm = DailyPreference.getInstance(EventWebActivity.this).isUserBenefitAlarm();

            if (isBenefitAlarm == false)
            {
                // 자바 스크립트 호출시에 스레드가 다른것 같다. 에러나서 수정
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        lockUI();

                        DailyNetworkAPI.getInstance(EventWebActivity.this).requestUpdateBenefitAgreement(mNetworkTag, true, new DailyHotelJsonResponseListener()
                        {
                            @Override
                            public void onResponse(String url, JSONObject response)
                            {
                                unLockUI();

                                try
                                {
                                    int msgCode = response.getInt("msgCode");

                                    if (msgCode == 100)
                                    {

                                        JSONObject dataJSONObject = response.getJSONObject("data");
                                        String serverDate = dataJSONObject.getString("serverDate");

                                        boolean isAgreed = Uri.parse(url).getBooleanQueryParameter("isAgreed", false);

                                        onBenefitAgreement(isAgreed, DailyCalendar.convertDateFormatString(serverDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일"));
                                    } else
                                    {
                                        String message = response.getString("msg");
                                        EventWebActivity.this.onErrorPopupMessage(msgCode, message);
                                    }
                                } catch (Exception e)
                                {
                                    EventWebActivity.this.onError(e);
                                }
                            }

                            public void onBenefitAgreement(final boolean isAgree, String updateDate)
                            {
                                DailyPreference.getInstance(EventWebActivity.this).setUserBenefitAlarm(isAgree);
                                AppboyManager.setPushEnabled(EventWebActivity.this, isAgree);

                                if (isAgree == true)
                                {
                                    // 혜택 알림 설정이 off --> on 일때
                                    String title = getString(R.string.label_setting_alarm);
                                    String message = getString(R.string.message_benefit_alarm_on_confirm_format, updateDate);
                                    String positive = getString(R.string.dialog_btn_text_confirm);

                                    showSimpleDialog(title, message, positive, null);
                                } else
                                {
                                }
                            }

                            @Override
                            public void onErrorResponse(final VolleyError volleyError)
                            {
                                unLockUI();

                                EventWebActivity.this.onErrorResponse(volleyError);
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
