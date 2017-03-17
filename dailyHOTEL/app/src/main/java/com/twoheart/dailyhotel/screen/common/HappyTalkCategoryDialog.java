package com.twoheart.dailyhotel.screen.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class HappyTalkCategoryDialog extends BaseActivity
{
    public enum CallScreen
    {
        SCREEN_STAY_DETAIL("호텔상세"),
        SCREEN_GOURMET_DETAIL("고메상세"),
        SCREEN_STAY_PAMENT_WAIT("예약내역>입금대기"),
        SCREEN_GOURMET_PAMENT_WAIT("예약내역>입금대기"),
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
    private HappyTalkCategoryDialogNetworkController mNetworkController;
    private CallScreen mCallScreen;
    private int mPlaceIndex, mBookingIndex;
    private String mPlaceType, mMainCategoryId;

    public static Intent newInstance(Context context, CallScreen callScreen, int placeIndex, int bookingIndex)
    {
        Intent intent = new Intent(context, HappyTalkCategoryDialog.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_CALL_SCREEN, callScreen.name());
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACEIDX, placeIndex);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_BOOKINGIDX, bookingIndex);

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

    private void onHappyTalkCategory(CallScreen callScreen, String category)
    {
        LinkedHashMap<String, Pair<String, String>> mainCategoryMap = new LinkedHashMap<>();
        LinkedHashMap<String, List<Pair<String, String>>> subCategoryMap = new LinkedHashMap<>();

        parseCategory(category, mainCategoryMap, subCategoryMap);

        mLayout.setVisibility(View.VISIBLE);
        mLayout.setCategory(callScreen, mainCategoryMap, subCategoryMap);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    private void startHappyTalk(String userIndex, String placeType, String mainId)
    {
        StringBuilder urlStringBuilder = new StringBuilder("https://api.happytalk.io/api/kakao/chat_open");
        urlStringBuilder.append("?yid=%40hailey099"); // 객사 옐로우 아이디
        urlStringBuilder.append("&category_id=" + mainId); // 대분류
        //            urlStringBuilder.append("&division_id=" + subId); // 중분류
        urlStringBuilder.append("&title="); // 상담제목

        if (mBookingIndex > 0)
        {
            urlStringBuilder.append("&order_number=" + mBookingIndex); // 주문번호
        }

        if (mPlaceIndex > 0)
        {
            urlStringBuilder.append("&product_number=" + mPlaceIndex); // 상품번호
        }

        urlStringBuilder.append("&parameter1=" + placeType); // 커스텀 파라미터1
        urlStringBuilder.append("&parameter2=" + userIndex); // 커스텀 파라미터2
        urlStringBuilder.append("&parameter3=" + mCallScreen.getName()); // 커스텀 파라미터3

        urlStringBuilder.append("&parameter4="); // 커스텀 파라미터4
        urlStringBuilder.append("&parameter5="); // 커스텀 파라미터5
        urlStringBuilder.append("&parameter6="); // 커스텀 파라미터6
        urlStringBuilder.append("&parameter7="); // 커스텀 파라미터7
        urlStringBuilder.append("&parameter8="); // 커스텀 파라미터8
        urlStringBuilder.append("&parameter9="); // 커스텀 파라미터9
        urlStringBuilder.append("&parameter10="); // 커스텀 파라미터10

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
    private void parseCategory(String categoryData, LinkedHashMap<String, Pair<String, String>> mainCategoryMap, LinkedHashMap<String, List<Pair<String, String>>> subCategoryMap)
    {
        if (mainCategoryMap == null || subCategoryMap == null)
        {
            return;
        }

        try
        {
            List<HappyTalkCategory> happyTalkCategoryList = LoganSquare.parseList(categoryData, HappyTalkCategory.class);

            int size = happyTalkCategoryList.size();

            final String STAY_PREFIX = "S_";
            final String GOURMET_PREFIX = "G_";

            mainCategoryMap.put(STAY_PREFIX, new Pair(STAY_PREFIX, STAY_PREFIX));
            mainCategoryMap.put(GOURMET_PREFIX, new Pair(GOURMET_PREFIX, GOURMET_PREFIX));

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
                    } else if (happyTalkCategory.name.startsWith(GOURMET_PREFIX) == true)
                    {
                        subGourmetCategoryList.add(new Pair(happyTalkCategory.id, happyTalkCategory.name.substring(GOURMET_PREFIX.length())));
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

            mNetworkController.requestUserProfie();
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
        public void onUserProfile(String userIndex)
        {
            startHappyTalk(userIndex, mPlaceType, mMainCategoryId);
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