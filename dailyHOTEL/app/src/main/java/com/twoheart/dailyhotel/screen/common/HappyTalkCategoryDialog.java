package com.twoheart.dailyhotel.screen.common;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bluelinelabs.logansquare.LoganSquare;
import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HappyTalkCategory;
import com.twoheart.dailyhotel.network.model.TodayDateTime;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HappyTalkCategoryDialog extends BaseActivity
{
    public static final String SITE_ID = "4000000190";
    public static final String STAY_YELLOW_ID = "%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94"; // @데일리호텔
    public static final String GOURMET_YELLOW_ID = "%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94"; // @데일리고메

    public enum CallScreen
    {
        SCREEN_STAY_DETAIL("호텔상세"),
        SCREEN_GOURMET_DETAIL("고메상세"),
        SCREEN_STAY_OUTBOUND_DETAIL("해외호텔상세"),
        SCREEN_STAY_PAYMENT_WAIT("예약내역>입금대기"),
        SCREEN_GOURMET_PAYMENT_WAIT("예약내역>입금대기"),
        SCREEN_STAY_BOOKING("예약내역>문의하기"),
        SCREEN_GOURMET_BOOKING("예약내역>문의하기"),
        SCREEN_STAY_OUTBOUND_BOOKING("해외호텔예약내역>문의하기"),
        SCREEN_STAY_BOOKING_CANCEL("취소내역>문의하기"),
        SCREEN_GOURMET_BOOKING_CANCEL("취소내역>문의하기"),
        SCREEN_STAY_OUTBOUND_BOOKING_CANCEL("해외호텔취소내역>문의하기"),
        SCREEN_FAQ("더보기>자주묻는질문"),
        SCREEN_CONTACT_US("더보기>문의하기"),
        SCREEN_STAY_REFUND("예약내역>환불문의"),
        SCREEN_STAY_OUTBOUND_REFUND("해외호텔예약내역>환불문의");

        private String mName;

        CallScreen(String name)
        {
            mName = name;
        }

        public String getName()
        {
            return mName;
        }
    }

    private HappyTalkCategoryDialogLayout mLayout;
    HappyTalkCategoryDialogNetworkController mNetworkController;
    CallScreen mCallScreen;
    private int mPlaceIndex, mBookingIndex;
    String mPlaceType, mMainCategoryId, mPlaceName;
    private HashMap<String, String> mSubCategoryId;

    public static Intent newInstance(Context context, CallScreen callScreen, int placeIndex, int bookingIndex, String placeName)
    {
        Intent intent = new Intent(context, HappyTalkCategoryDialog.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN, callScreen.name());
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_BOOKINGIDX, bookingIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACENAME, placeName);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null)
        {
            try
            {
                mCallScreen = CallScreen.valueOf(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN));
            } catch (Exception e)
            {
                Util.restartApp(this);
                return;
            }

            mPlaceIndex = intent.getIntExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, 0);
            mBookingIndex = intent.getIntExtra(Constants.NAME_INTENT_EXTRA_DATA_BOOKINGIDX, 0);
            mPlaceName = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACENAME);
        } else
        {
            finish();
            return;
        }

        mLayout = new HappyTalkCategoryDialogLayout(this, mOnEventListener);
        mNetworkController = new HappyTalkCategoryDialogNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_happytalk_category_dialog));
        mLayout.setVisibility(View.INVISIBLE);

        mNetworkController.requestCommonDateTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode == RESULT_OK)
                {
                    initCategory();
                } else
                {
                    finish();
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    void initCategory()
    {
        String happyTalkCategory = DailyPreference.getInstance(this).getHappyTalkCategory();

        // 해피톡 상담유형을 받은적이 없는 경우
        if (DailyTextUtils.isTextEmpty(happyTalkCategory) == true)
        {
            lockUI();

            mNetworkController.requestHappyTalkCategory();
        } else
        {
            onHappyTalkCategory(mCallScreen, happyTalkCategory);
        }
    }

    void onHappyTalkCategory(CallScreen callScreen, String category)
    {
        LinkedHashMap<String, Pair<String, String>> mainCategoryMap = new LinkedHashMap<>();
        LinkedHashMap<String, List<Pair<String, String>>> subCategoryMap = new LinkedHashMap<>();

        parseCategory(category, mainCategoryMap, subCategoryMap);


        switch (mCallScreen)
        {
            case SCREEN_STAY_REFUND:
            case SCREEN_STAY_OUTBOUND_REFUND:
            {
                final String STAY_PREFIX = "S_";
                final String STAY_REFUND = "64796";

                mOnEventListener.onHappyTalk(STAY_PREFIX, STAY_REFUND);
                break;
            }

            case SCREEN_STAY_BOOKING_CANCEL:
            case SCREEN_STAY_OUTBOUND_BOOKING_CANCEL:
            {
                final String STAY_PREFIX = "S_";
                final String STAY_BOOKING_CANCEL = "64796";

                mOnEventListener.onHappyTalk(STAY_PREFIX, STAY_BOOKING_CANCEL);
                break;
            }

            case SCREEN_GOURMET_BOOKING_CANCEL:
            {
                final String GOURMET_PREFIX = "G_";
                final String GOURMET_BOOKING_CANCEL = "64801";

                mOnEventListener.onHappyTalk(GOURMET_PREFIX, GOURMET_BOOKING_CANCEL);
                break;
            }

            default:
                mLayout.setVisibility(View.VISIBLE);
                mLayout.setCategory(callScreen, mainCategoryMap, subCategoryMap);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
            , AnalyticsManager.Action.CLOSE_HAPPYTALK, AnalyticsManager.Label.BACKKEY, null);
    }

    void startHappyTalk(String userIndex, String name, String phone, String email)
    {
        // https://docs.google.com/spreadsheets/d/1rB-bDASf80h8cW5lIX9kzrnuw0da-S65PJbEQ3lXeoU/edit#gid=0
        StringBuilder urlStringBuilder = new StringBuilder("https://api.happytalk.io/api/kakao/chat_open");

        if (getString(R.string.label_gourmet).equalsIgnoreCase(mPlaceType) == true)
        {
            urlStringBuilder.append("?yid=" + GOURMET_YELLOW_ID);
        } else
        {
            urlStringBuilder.append("?yid=" + STAY_YELLOW_ID);
        }

        urlStringBuilder.append("&site_id=" + SITE_ID); // 사이트 아이디
        urlStringBuilder.append("&category_id=" + mMainCategoryId); // 대분류

        if (mCallScreen == CallScreen.SCREEN_STAY_BOOKING_CANCEL || mCallScreen == CallScreen.SCREEN_STAY_OUTBOUND_BOOKING_CANCEL)
        {
            urlStringBuilder.append("&division_id=" + "64833"); // 중분류는 취소문의로
        } else if (mCallScreen == CallScreen.SCREEN_GOURMET_BOOKING_CANCEL)
        {
            urlStringBuilder.append("&division_id=" + "64892"); // 중분류는 취소문의로
        } else
        {
            urlStringBuilder.append("&division_id=" + mSubCategoryId.get(mMainCategoryId)); // 중분류는 대분류 첫번째 키로
        }

        urlStringBuilder.append("&title="); // 상담제목

        if (mBookingIndex > 0)
        {
            urlStringBuilder.append("&order_number=" + mBookingIndex); // 주문번호
        }

        if (mPlaceIndex > 0)
        {
            urlStringBuilder.append("&product_number=" + mPlaceIndex); // 상품번호
        }

        urlStringBuilder.append("&parameter1=" + userIndex); // user Index

        if (DailyTextUtils.isTextEmpty(name) == false)
        {
            urlStringBuilder.append("&parameter2=" + URLEncoder.encode(name)); //고객명
        }

        if (DailyTextUtils.isTextEmpty(phone) == false)
        {
            urlStringBuilder.append("&parameter3=" + URLEncoder.encode(phone)); // 전화번호
        }

        if (DailyTextUtils.isTextEmpty(email) == false)
        {
            urlStringBuilder.append("&parameter4=" + URLEncoder.encode(email)); // 이메일
        }

        if (mCallScreen != null && DailyTextUtils.isTextEmpty(mCallScreen.getName()) == false)
        {
            urlStringBuilder.append("&parameter5=" + URLEncoder.encode(mCallScreen.getName())); // 커스텀 파라미터5
        }

        if (mPlaceIndex > 0)
        {
            urlStringBuilder.append("&parameter6=" + mPlaceIndex); // Place IDX
        }

        if (DailyTextUtils.isTextEmpty(mPlaceName) == false)
        {
            urlStringBuilder.append("&parameter7=" + URLEncoder.encode(mPlaceName)); // 호텔명
        }

        if (DailyTextUtils.isTextEmpty(mPlaceType) == false)
        {
            urlStringBuilder.append("&parameter8=" + URLEncoder.encode(mPlaceType)); // 카테고리 분류
        }

        if (DailyTextUtils.isTextEmpty(DailyUserPreference.getInstance(HappyTalkCategoryDialog.this).getType()) == false)
        {
            urlStringBuilder.append("&parameter9=" + URLEncoder.encode(DailyUserPreference.getInstance(HappyTalkCategoryDialog.this).getType())); // 가입방법
        }

        //        urlStringBuilder.append("&parameter10="); // 커스텀 파라미터10

        urlStringBuilder.append("&app_ver=" + DailyHotel.VERSION_CODE);
        urlStringBuilder.append("&phone_os=" + "A");
        urlStringBuilder.append("&phone_model=" + URLEncoder.encode(Build.MODEL));
        urlStringBuilder.append("&phone_os_ver=" + URLEncoder.encode(Build.VERSION.RELEASE));

        TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));

        if (DailyTextUtils.isTextEmpty(telephonyManager.getNetworkOperatorName()) == false)
        {
            urlStringBuilder.append("&phone_telecomm=" + URLEncoder.encode(telephonyManager.getNetworkOperatorName()));
        }

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if (url.startsWith("intent://"))
                {
                    try
                    {
                        String host = url.substring("intent://".length(), url.indexOf("#Intent"));
                        int schemeIndex = url.indexOf("scheme=");
                        String scheme = url.substring(schemeIndex + "scheme=".length(), url.indexOf(';', schemeIndex));

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme + "://" + host));
                        startActivity(intent);
                    } catch (Exception e)
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStringBuilder.toString()));
                        startActivity(intent);
                    }

                    finish();
                } else
                {
                    view.loadUrl(url);
                }

                return true;
            }
        });

        webView.loadUrl(urlStringBuilder.toString());
    }

    /**
     * @param categoryData
     * @param mainCategoryMap get Data
     * @param subCategoryMap  get Data
     */
    private void parseCategory(String categoryData, LinkedHashMap<String, Pair<String, String>> mainCategoryMap//
        , LinkedHashMap<String, List<Pair<String, String>>> subCategoryMap)
    {
        if (mainCategoryMap == null || subCategoryMap == null)
        {
            return;
        }

        try
        {
            List<HappyTalkCategory> happyTalkCategoryList = LoganSquare.parseList(categoryData, HappyTalkCategory.class);

            final String STAY_PREFIX = "S_";
            final String GOURMET_PREFIX = "G_";

            final String hotel = getString(R.string.label_daily_hotel);
            final String gourmet = getString(R.string.label_daily_gourmet);

            mSubCategoryId = new HashMap<>();

            mainCategoryMap.put(STAY_PREFIX, new Pair(STAY_PREFIX, hotel));
            mainCategoryMap.put(GOURMET_PREFIX, new Pair(GOURMET_PREFIX, gourmet));

            List<Pair<String, String>> subStayCategoryList = new ArrayList<>();
            subCategoryMap.put(STAY_PREFIX, subStayCategoryList);

            List<Pair<String, String>> subGourmetCategoryList = new ArrayList<>();
            subCategoryMap.put(GOURMET_PREFIX, subGourmetCategoryList);

            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();

            for (HappyTalkCategory happyTalkCategory : happyTalkCategoryList)
            {
                String value = linkedHashMap.get(happyTalkCategory.id);

                if (DailyTextUtils.isTextEmpty(value) == true)
                {
                    linkedHashMap.put(happyTalkCategory.id, happyTalkCategory.name);

                    if (happyTalkCategory.name.startsWith(STAY_PREFIX) == true)
                    {
                        subStayCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name.substring(STAY_PREFIX.length())));

                        if (mSubCategoryId.containsKey(happyTalkCategory.id) == false)
                        {
                            mSubCategoryId.put(happyTalkCategory.id, happyTalkCategory.id2);
                        }
                    } else if (happyTalkCategory.name.startsWith(GOURMET_PREFIX) == true)
                    {
                        subGourmetCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name.substring(GOURMET_PREFIX.length())));

                        if (mSubCategoryId.containsKey(happyTalkCategory.id) == false)
                        {
                            mSubCategoryId.put(happyTalkCategory.id, happyTalkCategory.id2);
                        }
                    } else
                    {
                        subStayCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name));
                        subGourmetCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name));
                    }
                }
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
            // 에러가 나면 특정 유형으로 상담이 되도로 하는 것이 필요할것 같음.
        }
    }

    private HappyTalkCategoryDialogLayout.OnEventListener mOnEventListener = new HappyTalkCategoryDialogLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            HappyTalkCategoryDialog.this.finish();
        }

        @Override
        public void onHappyTalk(String placeType, String mainId)
        {
            if (placeType == null || mainId == null)
            {
                return;
            }

            final String STAY_PREFIX = "S_";
            final String GOURMET_PREFIX = "G_";

            switch (placeType)
            {
                case STAY_PREFIX:
                    mPlaceType = getString(R.string.label_stay);
                    break;

                case GOURMET_PREFIX:
                    mPlaceType = getString(R.string.label_gourmet);
                    break;

                default:
                    return;
            }

            mMainCategoryId = mainId;

            lockUI();
            mNetworkController.requestUserProfile();
        }

        @Override
        public void onCancel()
        {
            HappyTalkCategoryDialog.this.finish();

            AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                , AnalyticsManager.Action.CLOSE_HAPPYTALK, AnalyticsManager.Label.CANCEL, null);
        }
    };

    private HappyTalkCategoryDialogNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new HappyTalkCategoryDialogNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onHappyTalkCategory(String happyTalkCategory)
        {
            DailyPreference.getInstance(HappyTalkCategoryDialog.this).setHappyTalkCategory(happyTalkCategory);

            HappyTalkCategoryDialog.this.onHappyTalkCategory(mCallScreen, happyTalkCategory);

            unLockUI();
        }

        @Override
        public void onUserProfile(String userIndex, String name, String phone, String email)
        {
            startHappyTalk(userIndex, name, phone, email);

            String label = null;

            switch (mCallScreen)
            {
                case SCREEN_STAY_DETAIL:
                    label = AnalyticsManager.Label.STAY_DETAIL;
                    break;

                case SCREEN_GOURMET_DETAIL:
                    label = AnalyticsManager.Label.GOURMET_DETAIL;
                    break;

                case SCREEN_STAY_PAYMENT_WAIT:
                    label = AnalyticsManager.Label.STAY_DEPOSIT_WAITING;
                    break;

                case SCREEN_GOURMET_PAYMENT_WAIT:
                    label = AnalyticsManager.Label.GOURMET_DEPOSIT_WAITING;
                    break;

                case SCREEN_STAY_BOOKING:
                    label = AnalyticsManager.Label.STAY_BOOKING_DETAIL;
                    break;

                case SCREEN_GOURMET_BOOKING:
                    label = AnalyticsManager.Label.GOURMET_BOOKING_DETAIL;
                    break;

                case SCREEN_FAQ:
                    label = AnalyticsManager.Label.MENU_FNQ;
                    break;

                case SCREEN_CONTACT_US:
                    label = AnalyticsManager.Label.MENU_INQUIRY;
                    break;

                case SCREEN_STAY_REFUND:
                    label = AnalyticsManager.Label.STAY_BOOKING_DETAIL_REFUND;
                    break;

                case SCREEN_STAY_OUTBOUND_BOOKING:
                    break;

                case SCREEN_STAY_OUTBOUND_DETAIL:
                    break;

                case SCREEN_STAY_OUTBOUND_REFUND:
                    break;

                default:
                    return;
            }

            AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                , AnalyticsManager.Action.HAPPYTALK_START, label, null);
        }

        @Override
        public void onCommonDateTime(TodayDateTime todayDateTime)
        {
            try
            {
                Calendar todayCalendar = DailyCalendar.getInstance(todayDateTime.currentDateTime, false);
                int hour = todayCalendar.get(Calendar.HOUR_OF_DAY);
                //                int minute = todayCalendar.get(Calendar.MINUTE);

                String startHourString = DailyCalendar.convertDateFormatString(todayDateTime.openDateTime, DailyCalendar.ISO_8601_FORMAT, "H");
                String endHourString = DailyCalendar.convertDateFormatString(todayDateTime.closeDateTime, DailyCalendar.ISO_8601_FORMAT, "H");

                int startHour = Integer.parseInt(startHourString);
                int endHour = Integer.parseInt(endHourString);

                //                String[] lunchTimes = DailyPreference.getInstance(HappyTalkCategoryDialog.this).getRemoteConfigOperationLunchTime().split("\\,");
                //                String[] startLunchTime = lunchTimes[0].split(":");
                //                String[] endLunchTime = lunchTimes[1].split(":");
                //
                //                int startLunchHour = Integer.parseInt(startLunchTime[0]);
                //                int startLunchMinute = Integer.parseInt(startLunchTime[1]);
                //                int endLunchHour = Integer.parseInt(endLunchTime[0]);
                //                boolean isOverStartTime = hour > startLunchHour || (hour == startLunchHour && minute >= startLunchMinute);
                //                boolean isOverEndTime = hour >= endLunchHour;

                if (hour < startHour && hour > endHour)
                {
                    // 운영 안하는 시간 03:00:01 ~ 08:59:59 - 팝업 발생

                    showNonOperatingTimeDialog(new OnCallDialogListener()
                    {
                        @Override
                        public void onShowDialog()
                        {

                        }

                        @Override
                        public void onPositiveButtonClick(View v)
                        {

                        }

                        @Override
                        public void onNativeButtonClick(View v)
                        {

                        }

                        @Override
                        public void onDismissDialog()
                        {
                            HappyTalkCategoryDialog.this.finish();
                        }
                    });
                    //                } else if (isOverStartTime == true && isOverEndTime == false)
                    //                {
                    //                    // 점심시간 11:50:01~12:59:59 - 해피톡의 경우 팝업 발생 안함
                } else
                {
                    if (DailyHotel.isLogin() == true)
                    {
                        initCategory();
                    } else
                    {
                        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_happytalk_login), //
                            getString(R.string.frag_login), getString(R.string.dialog_btn_text_close), //
                            new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = LoginActivity.newInstance(HappyTalkCategoryDialog.this, null);
                                    startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);

                                    AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                                        , AnalyticsManager.Action.LOGIN_HAPPYTALK, AnalyticsManager.Label.LOGIN, null);
                                }
                            }, new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    finish();

                                    AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                                        , AnalyticsManager.Action.LOGIN_HAPPYTALK, AnalyticsManager.Label.CLOSE, null);
                                }
                            }, new DialogInterface.OnCancelListener()
                            {
                                @Override
                                public void onCancel(DialogInterface dialog)
                                {
                                    finish();

                                    AnalyticsManager.getInstance(HappyTalkCategoryDialog.this).recordEvent(AnalyticsManager.Category.CONTACT_DAILY_CONCIERGE//
                                        , AnalyticsManager.Action.LOGIN_HAPPYTALK, AnalyticsManager.Label.CLOSE, null);
                                }
                            }, null, true);
                    }
                }

            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            HappyTalkCategoryDialog.this.onError(call, e, onlyReport);
            finish();
        }

        @Override
        public void onError(Throwable e)
        {
            HappyTalkCategoryDialog.this.onError(e);
            finish();
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            HappyTalkCategoryDialog.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            HappyTalkCategoryDialog.this.onErrorToastMessage(message);
            finish();
        }

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            HappyTalkCategoryDialog.this.onErrorResponse(call, response);
            finish();
        }
    };
}