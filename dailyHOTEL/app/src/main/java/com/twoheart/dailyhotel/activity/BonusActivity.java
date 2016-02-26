/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * BonusActivity (적립금 화면)
 * <p>
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어
 * 해당 화면을 띄워주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 적립금 확인 페이지.
 *
 * @author jangjunho
 */
public class BonusActivity extends BaseActivity implements View.OnClickListener
{
    private RelativeLayout rlCreditNotLoggedIn;
    private LinearLayout llCreditLoggedIn, btnInvite;
    private View btnLogin, btnSignup;
    private TextView tvBonus, tvRecommenderCode;
    private TextView tvCredit;
    private String mRecommendCode;
    private List<Credit> mCreditList;
    private String mUserName;
    private String idx;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_credit_frag));
    }

    private void initLayout()
    {
        rlCreditNotLoggedIn = (RelativeLayout) findViewById(R.id.rl_credit_not_logged_in);
        llCreditLoggedIn = (LinearLayout) findViewById(R.id.ll_credit_logged_in);

        btnInvite = (LinearLayout) findViewById(R.id.btn_credit_invite_frd);
        tvCredit = (TextView) findViewById(R.id.tv_credit_history);
        tvRecommenderCode = (TextView) findViewById(R.id.tv_credit_recommender_code);
        tvBonus = (TextView) findViewById(R.id.tv_credit_money);
        btnLogin = findViewById(R.id.btn_no_login_login);
        btnSignup = findViewById(R.id.btn_no_login_signup);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        tvCredit.setOnClickListener(this);

        tvCredit.setPaintFlags(tvCredit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // underlining
    }

    @Override
    public void onResume()
    {
        super.onResume();

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == btnInvite.getId())
        {
            try
            {
                // 카카오톡 패키지 설치 여부
                getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                String userIdxStr = idx;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                Date date = new Date();

                String msg = getString(R.string.kakaolink_msg_invited_friend, mUserName, mRecommendCode);
                KakaoLinkManager.newInstance(this).sendInviteMsgKakaoLink(msg);

                AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.INVITE_FRIEND_CLICKED, mRecommendCode, null);
            } catch (Exception e)
            {
                ExLog.d(e.toString());

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
        } else if (v.getId() == tvCredit.getId())
        {
            Intent intent = new Intent(this, BonusListActivity.class);
            intent.putParcelableArrayListExtra(BonusListActivity.KEY_BUNDLE_ARGUMENTS_CREDITLIST, (ArrayList) mCreditList);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.CREDIT_MANAGEMENT_CLICKED, Label.CREDIT_HISTORY_VIEW, null);
        } else if (v.getId() == btnLogin.getId())
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.LOGIN, 0L);
        } else if (v.getId() == btnSignup.getId())
        {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.SIGNUP, 0L);
        }
    }

    private void loadLoginProcess(boolean loginSuccess)
    {
        if (loginSuccess)
        {
            rlCreditNotLoggedIn.setVisibility(View.GONE);
            llCreditLoggedIn.setVisibility(View.VISIBLE);

            AnalyticsManager.getInstance(this).recordScreen(Screen.BONUS, null);
        } else
        {
            rlCreditNotLoggedIn.setVisibility(View.VISIBLE);
            llCreditLoggedIn.setVisibility(View.GONE);

            AnalyticsManager.getInstance(this).recordScreen(Screen.BONUS_BEFORE_LOGIN, null);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserBonusAllResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            //적립금 내역리스트
            try
            {
                if (null == mCreditList)
                {
                    mCreditList = new ArrayList<Credit>();
                }

                mCreditList.clear();

                JSONArray jsonArray = response.getJSONArray("history");
                int length = jsonArray.length();

                for (int i = 0; i < length; i++)
                {
                    JSONObject historyObj = jsonArray.getJSONObject(i);

                    String content = historyObj.getString("content");
                    String expires = historyObj.getString("expires");
                    int bonus = historyObj.getInt("bonus");

                    mCreditList.add(new Credit(content, bonus, expires));
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
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                mRecommendCode = response.getString("rndnum");
                tvRecommenderCode.setText(mRecommendCode);
                mUserName = response.getString("name");

                idx = response.getString("idx");

                // 적립금 목록 요청.
                DailyNetworkAPI.getInstance().requestUserBonus(mNetworkTag, mUserBonusAllResponseListener, BonusActivity.this);
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

                tvBonus.setText(new StringBuilder(str).append(Html.fromHtml(getString(R.string.currency))));

                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, BonusActivity.this);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignin = jsonObject.getBoolean("is_signin");

                    if (isSignin == true)
                    {
                        VolleyHttpClient.createCookie();

                        // credit 요청
                        DailyNetworkAPI.getInstance().requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, BonusActivity.this);
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                DailyPreference.getInstance(BonusActivity.this).removeUserInformation();

                unLockUI();
                loadLoginProcess(false);
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                // session alive
                // credit 요청
                DailyNetworkAPI.getInstance().requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, BonusActivity.this);

            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인
                if (true == DailyPreference.getInstance(BonusActivity.this).isAutoLogin())
                {
                    HashMap<String, String> params = Util.getLoginParams(BonusActivity.this);
                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, BonusActivity.this);
                } else
                {
                    unLockUI();
                    loadLoginProcess(false);
                }

            } else
            {
                onError();
            }
        }
    };
}