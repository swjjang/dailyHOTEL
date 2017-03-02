package com.twoheart.dailyhotel.screen.common;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
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
    private HappyTalkCategoryDialogLayout mHappyTalkCategoryDialogLayout;
    private Constants.PlaceType mPlaceType;

    public static Intent newInstance(Context context, Constants.PlaceType placeType)
    {
        Intent intent = new Intent(context, HappyTalkCategoryDialog.class);
        intent.putExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE, placeType.name());

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null)
        {
            mPlaceType = Constants.PlaceType.valueOf(intent.getStringExtra(Constants.NAME_INTENT_EXTRA_DATA_PLACETYPE));
        } else
        {
            finish();
        }

        mHappyTalkCategoryDialogLayout = new HappyTalkCategoryDialogLayout(this, mOnEventListener);

        setContentView(mHappyTalkCategoryDialogLayout.onCreateView(R.layout.activity_happytalk_category_dialog));

        initCategoryData();
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

                                onHappyTalkCategory(happyTalkCategory);
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
            onHappyTalkCategory(happyTalkCategory);
        }
    }

    private void onHappyTalkCategory(String category)
    {
        mHappyTalkCategoryDialogLayout.setCategory(category);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    private HappyTalkCategoryDialogLayout.OnEventListener mOnEventListener = new HappyTalkCategoryDialogLayout.OnEventListener()
    {
        @Override
        public void finish()
        {
            HappyTalkCategoryDialog.this.onBackPressed();
        }

        @Override
        public void onHappyTalk(String mainId, String subId)
        {
            StringBuilder urlStringBuilder = new StringBuilder("https://api.happytalk.io/api/kakao/chat_open");

            switch (mPlaceType)
            {
                case HOTEL:
                    urlStringBuilder.append("?yid=@%EB%8D%B0%EC%9D%BC%EB%A6%AC%ED%98%B8%ED%85%94"); // 호텔 고객사 옐로우 아이디
                    break;

                case FNB:
                    urlStringBuilder.append("?yid=@%EB%8D%B0%EC%9D%BC%EB%A6%AC%EA%B3%A0%EB%A9%94"); // 고메 고객사 옐로우 아이디
                    break;
            }

            urlStringBuilder.append("&category_id=" + mainId); // 대분류
            urlStringBuilder.append("&division_id=" + subId); // 중분류
            urlStringBuilder.append("&title="); // 상담제목
            urlStringBuilder.append("&order_number="); // 주문번호
            urlStringBuilder.append("&product_number="); // 상품번호
            urlStringBuilder.append("&parameter1="); // 커스텀 파라미터1
            urlStringBuilder.append("&parameter2="); // 커스텀 파라미터2
            urlStringBuilder.append("&parameter3="); // 커스텀 파라미터3
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
                finish();
            }
        }
    };
}