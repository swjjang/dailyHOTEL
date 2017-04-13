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

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.model.LatLng;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.LauncherActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.Setting;
import com.twoheart.dailyhotel.model.Keyword;
import com.twoheart.dailyhotel.model.time.GourmetBookingDay;
import com.twoheart.dailyhotel.model.time.StayBookingDay;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.screen.common.WebViewActivity;
import com.twoheart.dailyhotel.screen.gourmet.detail.GourmetDetailActivity;
import com.twoheart.dailyhotel.screen.hotel.detail.StayDetailActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.CouponListActivity;
import com.twoheart.dailyhotel.screen.mydaily.coupon.RegisterCouponActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.stamp.StampActivity;
import com.twoheart.dailyhotel.screen.search.gourmet.result.GourmetSearchResultActivity;
import com.twoheart.dailyhotel.screen.search.stay.result.StaySearchResultActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyExternalDeepLink;
import com.twoheart.dailyhotel.util.DailyUserPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.DailyWebView;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Response;

public class EventWebActivity extends WebViewActivity implements Constants
{
    private static final String INTENT_EXTRA_DATA_EVENTNAME = "eventName";

    SourceType mSourceType;
    TodayDateTime mTodayDateTime;
    private String mEventName;

    String mCouponCode;
    String mDeepLinkUrl;
    String mConfirmText;

    JavaScriptExtention mJavaScriptExtention;

    public enum SourceType
    {
        EVENT,
        HOME_EVENT,
        STAMP,
    }

    Handler mHandler = new Handler();

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

        initToolbar(mEventName);

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

    private void initToolbar(String title)
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(title, new View.OnClickListener()
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
    }

    private void initLayout(final DailyWebView dailyWebView)
    {
        final View topButtonView = findViewById(R.id.topButtonView);
        topButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                smoothScrollTop(dailyWebView);
            }
        });

        topButtonView.setVisibility(View.GONE);

        dailyWebView.setOnScrollListener(new DailyWebView.OnScrollListener()
        {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt)
            {
                if (t == 0)
                {
                    topButtonView.setVisibility(View.GONE);
                } else
                {
                    topButtonView.setVisibility(View.VISIBLE);
                }
            }
        });

        View homeButtonView = findViewById(R.id.homeButtonView);
        homeButtonView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Constants.CODE_RESULT_ACTIVITY_GO_HOME);
                finish();
            }
        });
    }

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
        if (Util.isTextEmpty(mEventName) == true)
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
            case CODE_REQUEST_ACTIVITY_STAMP:
                if (resultCode == CODE_RESULT_ACTIVITY_GO_HOME)
                {
                    setResult(resultCode);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        if (mWebView.canGoBack() == true)
        {
            mWebView.goBack();
        } else
        {
            super.onBackPressed();
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
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                // 신규 타입의 화면이동
                int hotelIndex = Integer.parseInt(externalDeepLink.getIndex());
                int nights = 1;

                try
                {
                    nights = Integer.parseInt(externalDeepLink.getNights());
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

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                boolean isShowCalendar = externalDeepLink.isShowCalendar();
                int ticketIndex = externalDeepLink.getOpenTicketIndex();

                StayBookingDay stayBookingDay = new StayBookingDay();

                if (Util.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                }

                stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);

                Intent intent = StayDetailActivity.newInstance(EventWebActivity.this, stayBookingDay, hotelIndex, ticketIndex, isShowCalendar, false);
                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAY_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                if (isShowCalendar == true)
                {
                    AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                        , AnalyticsManager.Action.HOTEL_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.EVENT, null);
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
                DailyExternalDeepLink externalDeepLink = (DailyExternalDeepLink) dailyDeepLink;

                int gourmetIndex = Integer.parseInt(externalDeepLink.getIndex());

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                boolean isShowCalendar = externalDeepLink.isShowCalendar();
                int ticketIndex = externalDeepLink.getOpenTicketIndex();

                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

                if (Util.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    gourmetBookingDay.setVisitDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime);
                }

                Intent intent = GourmetDetailActivity.newInstance(EventWebActivity.this,//
                    gourmetBookingDay, gourmetIndex, ticketIndex, isShowCalendar, false);

                startActivityForResult(intent, CODE_REQUEST_ACTIVITY_GOURMET_DETAIL);

                overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

                if (isShowCalendar == true)
                {
                    AnalyticsManager.getInstance(EventWebActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_//
                        , AnalyticsManager.Action.GOURMET_BOOKING_CALENDAR_CLICKED, AnalyticsManager.Label.EVENT, null);
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

    boolean moveDeepLinkStaySearchResult(Context context, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                String word = externalDeepLink.getSearchWord();
                DailyExternalDeepLink.SearchType searchType = externalDeepLink.getSearchLocationType();
                LatLng latLng = externalDeepLink.getLatLng();
                double radius = externalDeepLink.getRadius();

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();
                int nights = 1;

                try
                {
                    nights = Integer.parseInt(externalDeepLink.getNights());
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

                StayBookingDay stayBookingDay = new StayBookingDay();

                if (Util.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    stayBookingDay.setCheckInDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    stayBookingDay.setCheckInDay(todayDateTime.dailyDateTime);
                }

                stayBookingDay.setCheckOutDay(stayBookingDay.getCheckInDay(DailyCalendar.ISO_8601_FORMAT), nights);

                switch (searchType)
                {
                    case LOCATION:
                    {
                        if (latLng != null)
                        {
                            Intent intent = StaySearchResultActivity.newInstance(context, todayDateTime, stayBookingDay, latLng, radius, true);
                            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                        } else
                        {
                            return false;
                        }
                        break;
                    }

                    default:
                    {
                        if (Util.isTextEmpty(word) == false)
                        {
                            Intent intent = StaySearchResultActivity.newInstance(context, todayDateTime, stayBookingDay, new Keyword(0, word), SearchType.SEARCHES);
                            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                        } else
                        {
                            return false;
                        }
                        break;
                    }
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

    boolean moveDeepLinkGourmetSearchResult(Context context, TodayDateTime todayDateTime, DailyDeepLink dailyDeepLink)
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

                String word = externalDeepLink.getSearchWord();
                DailyExternalDeepLink.SearchType searchType = externalDeepLink.getSearchLocationType();
                LatLng latLng = externalDeepLink.getLatLng();
                double radius = externalDeepLink.getRadius();

                String date = externalDeepLink.getDate();
                int datePlus = externalDeepLink.getDatePlus();

                GourmetBookingDay gourmetBookingDay = new GourmetBookingDay();

                if (Util.isTextEmpty(date) == false)
                {
                    Date checkInDate = DailyCalendar.convertDate(date, "yyyyMMdd", TimeZone.getTimeZone("GMT+09:00"));
                    gourmetBookingDay.setVisitDay(DailyCalendar.format(checkInDate, DailyCalendar.ISO_8601_FORMAT));
                } else if (datePlus >= 0)
                {
                    gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime, datePlus);
                } else
                {
                    gourmetBookingDay.setVisitDay(todayDateTime.dailyDateTime);
                }

                switch (searchType)
                {
                    case LOCATION:
                    {
                        if (latLng != null)
                        {
                            Intent intent = GourmetSearchResultActivity.newInstance(context, todayDateTime, gourmetBookingDay, latLng, radius, true);
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
                            Intent intent = GourmetSearchResultActivity.newInstance(context, todayDateTime, gourmetBookingDay, new Keyword(0, word), SearchType.SEARCHES);
                            startActivityForResult(intent, CODE_REQUEST_ACTIVITY_SEARCH_RESULT);
                        } else
                        {
                            return false;
                        }
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

                if (Util.isTextEmpty(placeType) == true)
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

    boolean moveDeepLinkStamp(Context context, DailyDeepLink dailyDeepLink)
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

                if (mSourceType == SourceType.STAMP)
                {
                    finish();
                } else
                {
                    Intent intent = StampActivity.newInstance(context, StampActivity.CallScreen.EVENT);
                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_STAMP);
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
        if (Util.isTextEmpty(couponCode, deepLink) == true || lockUiComponentAndIsLockUiComponent() == true)
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
                            if (Util.isTextEmpty(mConfirmText) == true)
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
                        if (Constants.DEBUG == false)
                        {
                            Crashlytics.log("Url: " + call.request().url().toString());
                        }

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
                    if (Constants.DEBUG == false)
                    {
                        Crashlytics.log("requestDownloadEventCoupon::CouponCode: " + couponCode + ", validTo: " + validTo);
                    }

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

                if (Setting.RELEASE_STORE == Setting.Stores.PLAY_STORE)
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


                    if (dailyDeepLink != null && dailyDeepLink.isValidateLink() == true)
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
                            } else if (externalDeepLink.isHotelSearchResultView() == true)
                            {
                                if (moveDeepLinkStaySearchResult(EventWebActivity.this, mTodayDateTime, externalDeepLink) == true)
                                {
                                    return;
                                }
                            } else if (externalDeepLink.isGourmetSearchResultView() == true)
                            {
                                if (moveDeepLinkGourmetSearchResult(EventWebActivity.this, mTodayDateTime, externalDeepLink) == true)
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
                            } else if (externalDeepLink.isStampView() == true)
                            {
                                if (moveDeepLinkStamp(EventWebActivity.this, externalDeepLink) == true)
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
                                        if (Constants.DEBUG == false)
                                        {
                                            Crashlytics.log("Url: " + call.request().url().toString());
                                        }

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
