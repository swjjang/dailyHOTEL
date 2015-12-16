/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * CreditFragment (적립금 화면)
 * <p>
 * 로그인 여부에 따라 적립금을 안내하는 화면이다. 적립금을 표시하며 카카오톡
 * 친구 초대 버튼이 있다. 세부 내역을 따로 표시해주는 버튼을 가지고 있어
 * 해당 화면을 띄워주기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.BaseActivity;
import com.twoheart.dailyhotel.activity.CreditListActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.SignupActivity;
import com.twoheart.dailyhotel.model.Credit;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 적립금 확인 페이지.
 *
 * @author jangjunho
 */
public class CreditFragment extends BaseFragment implements Constants, OnClickListener
{
    private RelativeLayout rlCreditNotLoggedIn;
    private LinearLayout llCreditLoggedIn, btnInvite;
    private View btnLogin, btnSignup;
    private TextView tvBonus, tvRecommenderCode;
    private TextView tvCredit;
    private String mRecommendCode;
    private ArrayList<Credit> mCreditList;
    private String mUserName;
    private String idx;
    private DailyHotelJsonResponseListener mUserBonusAllResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (getActivity() == null)
            {
                return;
            }

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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            try
            {
                mRecommendCode = response.getString("rndnum");
                tvRecommenderCode.setText(response.getString("rndnum"));
                mUserName = response.getString("name");

                idx = response.getString("idx");

                // 적립금 목록 요청.
                DailyNetworkAPI.getInstance().requestUserBonus(mNetworkTag, mUserBonusAllResponseListener, baseActivity);
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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

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
                DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, baseActivity);
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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

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
                        DailyNetworkAPI.getInstance().requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, baseActivity);
                        return;
                    }
                }

                // 로그인 실패
                // data 초기화
                SharedPreferences.Editor ed = baseActivity.sharedPreference.edit();
                ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
                ed.putString(KEY_PREFERENCE_USER_ID, null);
                ed.putString(KEY_PREFERENCE_USER_PWD, null);
                ed.putString(KEY_PREFERENCE_USER_TYPE, null);
                ed.commit();

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
            BaseActivity baseActivity = (BaseActivity) getActivity();

            if (baseActivity == null)
            {
                return;
            }

            String result = null;

            if (false == Util.isTextEmpty(response))
            {
                result = response.trim();
            }

            if (true == "alive".equalsIgnoreCase(result))
            {
                // session alive
                // credit 요청
                DailyNetworkAPI.getInstance().requestBonus(mNetworkTag, mReserveSavedMoneyStringResponseListener, baseActivity);

            } else if (true == "dead".equalsIgnoreCase(result))
            {
                // session dead
                // 재로그인
                if (true == baseActivity.sharedPreference.getBoolean(KEY_PREFERENCE_AUTO_LOGIN, false))
                {
                    HashMap<String, String> params = Util.getLoginParams(baseActivity, baseActivity.sharedPreference);
                    DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, baseActivity);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);
        view.setPadding(0, Util.dpToPx(container.getContext(), 56) + 1, 0, 0);

        rlCreditNotLoggedIn = (RelativeLayout) view.findViewById(R.id.rl_credit_not_logged_in);
        llCreditLoggedIn = (LinearLayout) view.findViewById(R.id.ll_credit_logged_in);

        btnInvite = (LinearLayout) view.findViewById(R.id.btn_credit_invite_frd);
        tvCredit = (TextView) view.findViewById(R.id.tv_credit_history);
        tvRecommenderCode = (TextView) view.findViewById(R.id.tv_credit_recommender_code);
        tvBonus = (TextView) view.findViewById(R.id.tv_credit_money);
        btnLogin = view.findViewById(R.id.btn_no_login_login);
        btnSignup = view.findViewById(R.id.btn_no_login_signup);

        btnLogin.setOnClickListener(this);
        btnSignup.setOnClickListener(this);
        btnInvite.setOnClickListener(this);
        tvCredit.setOnClickListener(this);

        tvCredit.setPaintFlags(tvCredit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); // underlining

        return view;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(getActivity()).recordScreen(Screen.CREDIT);

        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        // ActionBar Setting
        baseActivity.setActionBar(getString(R.string.actionbar_title_credit_frag), false);

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, baseActivity);
    }

    @Override
    public void onClick(View v)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (v.getId() == btnInvite.getId())
        {
            try
            {
                // 카카오톡 패키지 설치 여부
                baseActivity.getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                String userIdxStr = idx;

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                Date date = new Date();
                String strDate = dateFormat.format(date);

                String msg = getString(R.string.kakaolink_msg_invited_friend, mUserName, mRecommendCode);
                KakaoLinkManager.newInstance(getActivity()).sendInviteMsgKakaoLink(msg);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("userId", userIdxStr);
                params.put("datetime", strDate);

                AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.INVITE_KAKAO_FRIEND, params);
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
            Intent i = new Intent(baseActivity, CreditListActivity.class);
            i.putParcelableArrayListExtra(CreditListActivity.KEY_BUNDLE_ARGUMENTS_CREDITLIST, mCreditList);
            startActivity(i);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.VIEW_CREDIT_HISTORY, 0L);
        } else if (v.getId() == btnLogin.getId())
        {
            Intent i = new Intent(baseActivity, LoginActivity.class);
            startActivity(i);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.LOGIN, 0L);
        } else if (v.getId() == btnSignup.getId())
        {
            Intent i = new Intent(baseActivity, SignupActivity.class);
            startActivity(i);
            baseActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getActivity()).recordEvent(Screen.CREDIT, Action.CLICK, Label.SIGNUP, 0L);
        }
    }

    private void loadLoginProcess(boolean loginSuccess)
    {
        BaseActivity baseActivity = (BaseActivity) getActivity();

        if (baseActivity == null)
        {
            return;
        }

        if (loginSuccess)
        {
            rlCreditNotLoggedIn.setVisibility(View.GONE);
            llCreditLoggedIn.setVisibility(View.VISIBLE);
        } else
        {
            rlCreditNotLoggedIn.setVisibility(View.VISIBLE);
            llCreditLoggedIn.setVisibility(View.GONE);
        }
    }
}