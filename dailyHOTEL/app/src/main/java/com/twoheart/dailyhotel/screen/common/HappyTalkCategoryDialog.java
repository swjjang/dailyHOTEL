package com.twoheart.dailyhotel.screen.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Pair;
import android.view.View;

import com.bluelinelabs.logansquare.LoganSquare;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.model.HappyTalkCategory;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HappyTalkCategoryDialog extends BaseActivity
{
    public static final String SITE_ID = "4000000190";
    public static final String YELLOW_ID = "%40%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94"; // @데일리호텔

    public enum CallScreen
    {
        SCREEN_STAY_DETAIL("호텔상세"),
        SCREEN_GOURMET_DETAIL("고메상세"),
        SCREEN_STAY_PAYMENT_WAIT("예약내역>입금대기"),
        SCREEN_GOURMET_PAYMENT_WAIT("예약내역>입금대기"),
        SCREEN_STAY_BOOKING("예약내역>문의"),
        SCREEN_GOURMET_BOOKING("예약내역>문의"),
        SCREEN_FAQ("더보기>자주묻는질문"),
        SCREEN_CONTACT_US("더보기>문의하기"),
        SCREEN_STAY_REFUND("예약내역>환불문의");

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
            mCallScreen = CallScreen.valueOf(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN));
            mPlaceIndex = intent.getIntExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, 0);
            mBookingIndex = intent.getIntExtra(Constants.NAME_INTENT_EXTRA_DATA_BOOKINGIDX, 0);
            mPlaceName = intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACENAME);
        } else
        {
            finish();
        }

        mLayout = new HappyTalkCategoryDialogLayout(this, mOnEventListener);
        mNetworkController = new HappyTalkCategoryDialogNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mLayout.onCreateView(R.layout.activity_happytalk_category_dialog));
        mLayout.setVisibility(View.INVISIBLE);

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
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onBackPressed();
                    }
                }, new DialogInterface.OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        onBackPressed();
                    }
                }, null, true);
        }
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

    private void initCategory()
    {
        String happyTalkCategory = DailyPreference.getInstance(this).getHappyTalkCategory();

        // 해피톡 상담유형을 받은적이 없는 경우
        if (Util.isTextEmpty(happyTalkCategory) == true)
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

        // 환불인 경우 바로 연동한다.
        if (mCallScreen == CallScreen.SCREEN_STAY_REFUND)
        {
            final String STAY_REFUND = "64796";

            mOnEventListener.onHappyTalk(getString(R.string.label_daily_hotel), STAY_REFUND);
        } else
        {
            mLayout.setVisibility(View.VISIBLE);
            mLayout.setCategory(callScreen, mainCategoryMap, subCategoryMap);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    void startHappyTalk(String userIndex, String name, String phone, String email)
    {
        // https://docs.google.com/spreadsheets/d/1rB-bDASf80h8cW5lIX9kzrnuw0da-S65PJbEQ3lXeoU/edit#gid=0
        StringBuilder urlStringBuilder = new StringBuilder("https://api.happytalk.io/api/kakao/chat_open");
        urlStringBuilder.append("?yid=%" + YELLOW_ID); // 객사 옐로우 아이디
        urlStringBuilder.append("&site_id=" + SITE_ID); // 사이트 아이디
        urlStringBuilder.append("&category_id=" + mMainCategoryId); // 대분류
        urlStringBuilder.append("&division_id=" + mSubCategoryId.get(mMainCategoryId)); // 중분류는 대분류 첫번째 키로
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
        urlStringBuilder.append("&parameter2=" + URLEncoder.encode(name)); //고객명
        urlStringBuilder.append("&parameter3=" + URLEncoder.encode(phone)); // 전화번호
        urlStringBuilder.append("&parameter4=" + URLEncoder.encode(email)); // 이메일
        urlStringBuilder.append("&parameter5=" + URLEncoder.encode(mCallScreen.getName())); // 커스텀 파라미터5
        urlStringBuilder.append("&parameter6=" + mPlaceIndex); // Hotel IDX
        urlStringBuilder.append("&parameter7=" + URLEncoder.encode(mPlaceName)); // 호텔명
        urlStringBuilder.append("&parameter8=" + mPlaceType); // 카테고리 분류
        urlStringBuilder.append("&parameter9=" + URLEncoder.encode(DailyPreference.getInstance(HappyTalkCategoryDialog.this).getUserType())); // 가입방법
        urlStringBuilder.append("&parameter10="); // 커스텀 파라미터10

        urlStringBuilder.append("&app_ver=" + DailyHotel.VERSION_CODE);
        urlStringBuilder.append("&phone_os=" + "A");
        urlStringBuilder.append("&phone_model=" + URLEncoder.encode(Build.MODEL));
        urlStringBuilder.append("&phone_os_ver=" + URLEncoder.encode(Build.VERSION.RELEASE));

        TelephonyManager telephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        urlStringBuilder.append("&phone_telecomm=" + URLEncoder.encode(telephonyManager.getNetworkOperatorName()));

        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStringBuilder.toString()));
            startActivity(intent);
        } catch (ActivityNotFoundException e)
        {
            // 연결 가능한 웹 브라우저가 없습니다.

        }

        finish();
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

                if (Util.isTextEmpty(value) == true)
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
            HappyTalkCategoryDialog.this.onBackPressed();
        }

        @Override
        public void onHappyTalk(String placeType, String mainId)
        {
            mPlaceType = placeType;
            mMainCategoryId = mainId;

            mNetworkController.requestUserProfile();
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