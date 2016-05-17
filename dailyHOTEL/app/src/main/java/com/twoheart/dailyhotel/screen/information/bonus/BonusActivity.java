package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Bonus;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupStep1Activity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BonusActivity extends BaseActivity implements View.OnClickListener
{
    private View mBeforeSigninLayout;
    private View mSigninLayout;
    private TextView mBonusTextView, mRecommenderCodeTextView;
    private String mRecommendCode;
    private List<Bonus> mBonusList;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bonus);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_credit_frag), new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        mBeforeSigninLayout = findViewById(R.id.rl_credit_not_logged_in);
        mSigninLayout = findViewById(R.id.ll_credit_logged_in);
        mRecommenderCodeTextView = (TextView) findViewById(R.id.tv_credit_recommender_code);
        mBonusTextView = (TextView) findViewById(R.id.tv_credit_money);

        View inviteFriend = findViewById(R.id.btn_credit_invite_frd);
        inviteFriend.setOnClickListener(this);

        View historyLayout = findViewById(R.id.historyLayout);
        historyLayout.setOnClickListener(this);

        View recommenderLayout = findViewById(R.id.recommenderLayout);
        recommenderLayout.setOnClickListener(this);

        View loginView = findViewById(R.id.btn_no_login_login);
        loginView.setOnClickListener(this);

        View signUpView = findViewById(R.id.btn_no_login_signup);
        signUpView.setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == true)
        {
            loadLoginProcess(false);
        } else
        {
            lockUI();
            DailyNetworkAPI.getInstance(this).requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, this);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_credit_invite_frd:
            {
                try
                {
                    // 카카오톡 패키지 설치 여부
                    getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                    String msg = getString(R.string.kakaolink_msg_invited_friend, mUserName, mRecommendCode, mRecommendCode);
                    KakaoLinkManager.newInstance(this).sendInviteKakaoLink(msg);

                    AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.INVITE_FRIEND_CLICKED, mRecommendCode, null);
                } catch (Exception e)
                {
                    try
                    {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_STORE_GOOGLE_KAKAOTALK)));
                    } catch (ActivityNotFoundException e1)
                    {
                        Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
                        marketLaunch.setData(Uri.parse(URL_STORE_GOOGLE_KAKAOTALK_WEB));
                        startActivity(marketLaunch);
                    }
                }
                break;
            }

            case R.id.historyLayout:
            {
                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.CREDIT_MANAGEMENT_CLICKED, Label.CREDIT_HISTORY_VIEW, null);

                if (mBonusList == null || mBonusList.size() == 0)
                {
                    DailyToast.showToast(this, R.string.act_history_no_details, Toast.LENGTH_SHORT);
                    return;
                }

                Intent intent = new Intent(this, BonusListActivity.class);
                intent.putParcelableArrayListExtra(BonusListActivity.KEY_BUNDLE_ARGUMENTS_CREDITLIST, (ArrayList) mBonusList);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.btn_no_login_login:
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.LOGIN, 0L);
                break;
            }

            case R.id.btn_no_login_signup:
            {
                Intent intent = SignupStep1Activity.newInstance(BonusActivity.this);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.SIGNUP, 0L);
                break;
            }

            case R.id.recommenderLayout:
            {
                Util.clipText(this, mRecommendCode);

                DailyToast.showToast(this, R.string.message_copy_recommendar_code, Toast.LENGTH_SHORT);
                break;
            }
        }
    }

    private void loadLoginProcess(boolean loginSuccess)
    {
        if (loginSuccess == true)
        {
            mBeforeSigninLayout.setVisibility(View.GONE);
            mSigninLayout.setVisibility(View.VISIBLE);

            AnalyticsManager.getInstance(this).recordScreen(Screen.BONUS);
        } else
        {
            mBeforeSigninLayout.setVisibility(View.VISIBLE);
            mSigninLayout.setVisibility(View.GONE);

            AnalyticsManager.getInstance(this).recordScreen(Screen.BONUS_BEFORE_LOGIN);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserBonusAllResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            //적립금 내역리스트
            try
            {
                if (null == mBonusList)
                {
                    mBonusList = new ArrayList<>();
                }

                mBonusList.clear();

                JSONArray jsonArray = response.getJSONArray("history");
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject historyObj = jsonArray.getJSONObject(i);

                    String content = historyObj.getString("content");
                    String expires = historyObj.getString("expires");
                    int bonus = historyObj.getInt("bonus");

                    mBonusList.add(new Bonus(content, bonus, expires));
                }

                loadLoginProcess(true);
                unLockUI();

            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
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
                mRecommendCode = response.getString("rndnum");
                mRecommenderCodeTextView.setText(mRecommendCode);
                mUserName = response.getString("name");

                // 적립금 목록 요청.
                DailyNetworkAPI.getInstance(BonusActivity.this).requestUserBonus(mNetworkTag, mUserBonusAllResponseListener, BonusActivity.this);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelStringResponseListener mReserveSavedMoneyStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            try
            {
                String result = null;

                if (false == Util.isTextEmpty(response))
                {
                    result = response.trim();
                }

                DecimalFormat comma = new DecimalFormat("###,##0");

                int bonus = 0;

                try
                {
                    bonus = Integer.parseInt(result);
                } catch (NumberFormatException e)
                {
                    ExLog.d(e.toString());
                }

                String str = comma.format(bonus);

                mBonusTextView.setText(new StringBuilder(str).append(Html.fromHtml(getString(R.string.currency))));

                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance(BonusActivity.this).requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, BonusActivity.this);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };
}