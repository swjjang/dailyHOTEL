package com.twoheart.dailyhotel.screen.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HappyTalkCategoryDialog extends BaseActivity
{
    public enum CallScreen
    {
        SCREEN_STAY_DETAIL("스테이상세화면"),
        SCREEN_GOURMET_DETAIL("고메상세화면"),
        SCREEN_STAY_PAMENT_WAIT("스테이계좌이체결제대기"),
        SCREEN_GOURMET_PAMENT_WAIT("고메계좌이체결제대기"),
        SCREEN_STAY_BOOKING("스테이예약화면"),
        SCREEN_GOURMET_BOOKING("고메예약화면"),
        SCREEN_FAQ("자주묻는질문화면"),
        SCREEN_CONTACT_US("문의하기화면"),
        SCREEN_STAY_REFUND("스테이예약환불문의");

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

    private HappyTalkCategoryDialogLayout mHappyTalkCategoryDialogLayout;
    private CallScreen mCallScreen;
    private int mPlaceIndex, mBookingIndex;

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

        mHappyTalkCategoryDialogLayout = new HappyTalkCategoryDialogLayout(this, mOnEventListener);

        setContentView(mHappyTalkCategoryDialogLayout.onCreateView(R.layout.activity_happytalk_category_dialog));

        if (DailyHotel.isLogin() == true)
        {
            initCategoryData();
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
                    initCategoryData();
                } else
                {
                    finish();
                }
                break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initCategoryData()
    {
        String happyTalkCategory = DailyPreference.getInstance(this).getHappyTalkCategory();

        // 해피톡 상담유형을 받은적이 없는 경우
        if (Util.isTextEmpty(happyTalkCategory) == true)
        {
            lockUI();

            DailyMobileAPI.getInstance(this).requestHappyTalkCategory(mNetworkTag, new Callback<JSONObject>()
            {
                @Override
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
                {
                    if (response != null && response.isSuccessful() && response.body() != null)
                    {
                        try
                        {
                            JSONObject jsonObjectData = response.body();

                            if ("success".equalsIgnoreCase(jsonObjectData.getString("code")) == true)
                            {
                                JSONObject jsonObjectResults = jsonObjectData.getJSONObject("results");
                                JSONArray jsonArray = jsonObjectResults.getJSONArray("assign");

                                String happyTalkCategory = jsonArray.toString();
                                DailyPreference.getInstance(HappyTalkCategoryDialog.this).setHappyTalkCategory(happyTalkCategory);

                                onHappyTalkCategory(mCallScreen, happyTalkCategory);
                            }

                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }

                        unLockUI();
                    } else
                    {

                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {

                }
            });
        } else
        {
            onHappyTalkCategory(mCallScreen, happyTalkCategory);
        }
    }

    private void onHappyTalkCategory(CallScreen callScreen, String category)
    {
        mHappyTalkCategoryDialogLayout.setCategory(callScreen, category);
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

        urlStringBuilder.append("&parameter4=" + mPlaceIndex); // 커스텀 파라미터4
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

    private HappyTalkCategoryDialogLayout.OnEventListener mOnEventListener = new HappyTalkCategoryDialogLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            HappyTalkCategoryDialog.this.onBackPressed();
        }

        @Override
        public void onHappyTalk(final String placeType, final String mainId)
        {
            DailyMobileAPI.getInstance(HappyTalkCategoryDialog.this).requestUserProfile(mNetworkTag, new retrofit2.Callback<JSONObject>()
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
                                JSONObject jsonObject = responseJSONObject.getJSONObject("data");

                                String userIndex = jsonObject.getString("userIdx");

                                startHappyTalk(userIndex, placeType, mainId);
                            } else
                            {
                                String message = responseJSONObject.getString("msg");
                                HappyTalkCategoryDialog.this.onErrorPopupMessage(msgCode, message);
                            }
                        } catch (Exception e)
                        {
                            ExLog.d(e.toString());
                        }
                    } else
                    {
                        HappyTalkCategoryDialog.this.onErrorResponse(call, response);
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<JSONObject> call, Throwable t)
                {
                    HappyTalkCategoryDialog.this.onError(t);
                    finish();
                }
            });
        }
    };
}