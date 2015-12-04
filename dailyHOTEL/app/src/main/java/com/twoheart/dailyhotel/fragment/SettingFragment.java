/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * SettingFragment (설정 화면)
 * <p>
 * 어플리케이션의 설정 화면이다. 뷰는 리스트뷰처럼 보이나 리스트뷰처럼 보이도록
 * 구성된 화면일 뿐이다. 이 화면에서 현재 로그인 상태를 가져오기 위해 네트워
 * 크 작업을 하기도 한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.twoheart.dailyhotel.MainActivity;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.activity.AboutActivity;
import com.twoheart.dailyhotel.activity.CreditCardListActivity;
import com.twoheart.dailyhotel.activity.FAQActivity;
import com.twoheart.dailyhotel.activity.LoginActivity;
import com.twoheart.dailyhotel.activity.NoticeActivity;
import com.twoheart.dailyhotel.activity.ProfileActivity;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailySwitchCompat;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SettingFragment extends BaseFragment implements Constants, OnClickListener
{
    private MainActivity mHostActivity;
    private RequestQueue mQueue;

    private TextView tvNotice, tvHelp, tvMail, tvLogin, tvEmail, tvCall, tvAbout, tvVersion;
    private TextView mSettingCardTextView;
    private View mSettingCardLayout;
    private LinearLayout llVersion, llLogin;
    private String profileStr, loginStr;
    private DailySwitchCompat mSwitchCompat;
    private String mCSoperatingTimeMessage;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // ActionBar Setting
        mHostActivity = (MainActivity) getActivity();
        mQueue = VolleyHttpClient.getRequestQueue();

        tvNotice = (TextView) view.findViewById(R.id.tv_setting_notice);
        tvVersion = (TextView) view.findViewById(R.id.tv_setting_version);
        llVersion = (LinearLayout) view.findViewById(R.id.ll_setting_version);
        tvHelp = (TextView) view.findViewById(R.id.tv_setting_help);
        tvMail = (TextView) view.findViewById(R.id.tv_setting_mail);
        llLogin = (LinearLayout) view.findViewById(R.id.ll_setting_login);
        tvLogin = (TextView) view.findViewById(R.id.tv_setting_login);
        tvEmail = (TextView) view.findViewById(R.id.tv_setting_email);
        tvCall = (TextView) view.findViewById(R.id.tv_setting_call);
        tvAbout = (TextView) view.findViewById(R.id.tv_setting_introduction);
        mSwitchCompat = (DailySwitchCompat) view.findViewById(R.id.pushSwitch);

        mSettingCardLayout = view.findViewById(R.id.settingCardLayout);
        mSettingCardTextView = (TextView) view.findViewById(R.id.settingCardTextView);

        tvNotice.setOnClickListener(this);
        //        llVersion.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvMail.setOnClickListener(this);
        llLogin.setOnClickListener(this);
        tvCall.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        mSettingCardTextView.setOnClickListener(this);

        mSwitchCompat.setChecked(DailyPreference.getInstance(mHostActivity).isAllowPush());
        mSwitchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();
                DailyPreference.getInstance(mHostActivity).setAllowPush(isChecked);
                releaseUiComponent();
            }
        });

        try
        {
            String currentVersion = mHostActivity.getPackageManager().getPackageInfo(mHostActivity.getPackageName(), 0).versionName;

            tvVersion.setText(currentVersion);
        } catch (NameNotFoundException e)
        {
            onError(e);
        }

        View viewFacebook = view.findViewById(R.id.facebookLinkView);
        View viewInstagram = view.findViewById(R.id.instagramLinkView);
        View viewNaver = view.findViewById(R.id.naverLinkView);

        viewFacebook.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/Dailyhotel.Korea"));
                    startActivity(intent);
                } catch (Exception e)
                {
                    intent.setData(Uri.parse("https://www.facebook.com/dailyhotel"));
                    startActivity(intent);
                }
            }
        });

        viewInstagram.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                try
                {
                    intent.setData(Uri.parse("instagram://user?username=dailyhotel"));
                    startActivity(intent);
                } catch (Exception e)
                {
                    intent.setData(Uri.parse("https://www.instagram.com/dailyhotel"));
                    startActivity(intent);
                }
            }
        });

        viewNaver.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://blog.naver.com/dailyhotels"));
                startActivity(intent);
            }
        });

        llVersion.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(com.twoheart.dailyhotel.activity.CalendarActivity.newInstance(getContext(), PlaceMainFragment.TYPE.HOTEL, 1449211725884L));
            }
        });

        //
        //        viewFacebook.setOnClickListener(new OnClickListener()
        //        {
        //            @Override
        //            public void onClick(View v)
        //            {
        //                startActivity(com.twoheart.dailyhotel.activity.SatisfactionActivity.newInstance(getContext(), "인터컨티넨탈 서울 코엑스", 404880, 1446822000000L, 1446897600000L));
        //            }
        //        });

        return view;
    }

    @Override
    public void onStart()
    {
        AnalyticsManager.getInstance(mHostActivity).recordScreen(Screen.SETTING);
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mHostActivity.setActionBar(getString(R.string.actionbar_title_setting_frag), false);
        profileStr = getString(R.string.frag_profile);
        loginStr = getString(R.string.frag_login);

        lockUI();
        DailyNetworkAPI.getInstance().requestUserAlive(mNetworkTag, mUserAliveStringResponseListener, mHostActivity);
    }

    @Override
    public void onClick(View v)
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUiComponent();

        int id = v.getId();

        if (id == tvNotice.getId())
        {
            Intent i = new Intent(mHostActivity, NoticeActivity.class);
            startActivity(i);
            mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.NOTICE, 0L);
        } else if (id == tvHelp.getId())
        {
            Intent i = new Intent(mHostActivity, FAQActivity.class);
            startActivity(i);
            mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.FAQ, 0L);
        } else if (id == tvMail.getId())
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:help@dailyhotel.co.kr"));
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_text_subject));
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text_desc));
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(Intent.createChooser(intent, getString(R.string.mail_text_dialog_title)));

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.MAIL_CS, 0L);
        } else if (id == llLogin.getId())
        {
            if (tvLogin.getText().equals(getString(R.string.frag_profile)))
            {
                // 로그인 되어 있는 상태
                Intent i = new Intent(mHostActivity, ProfileActivity.class);
                startActivity(i);
                mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.PROFILE, 0L);
            } else
            {
                // 로그아웃 상태
                chgClickable(llLogin);
                Intent i = new Intent(mHostActivity, LoginActivity.class);
                startActivityForResult(i, CODE_REQUEST_ACTIVITY_LOGIN);
                mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.LOGIN, 0L);
            }

        } else if (id == tvCall.getId())
        {
            showCallDialog();

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.CALL_CS, 0L);
        } else if (id == tvAbout.getId())
        {
            Intent i = new Intent(mHostActivity, AboutActivity.class);
            startActivity(i);
            mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.ABOUT, 0L);
        } else if (id == mSettingCardTextView.getId())
        {
            Intent i = new Intent(mHostActivity, CreditCardListActivity.class);
            startActivity(i);
            mHostActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(mHostActivity).recordEvent(Screen.SETTING, Action.CLICK, Label.CREDITCARD, 0L);
        } else
        {
            releaseUiComponent();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == CODE_REQUEST_ACTIVITY_LOGIN)
        {
            chgClickable(llLogin);

            if (resultCode == Activity.RESULT_OK)
            {
                mHostActivity.selectMenuDrawer(mHostActivity.menuHotelListFragment);
            }
        }
    }

    private void invalidateLoginButton(boolean login, String email)
    {
        tvEmail.setText(email);

        if (login)
        {
            tvLogin.setText(profileStr);
            tvEmail.setVisibility(View.VISIBLE);
        } else
        {
            tvLogin.setText(loginStr);
            tvEmail.setVisibility(View.GONE);
        }

    }

    private void showCallDialog()
    {
        if (mHostActivity.isFinishing() == true)
        {
            return;
        }

        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                releaseUiComponent();

                if (Util.isTelephonyEnabled(mHostActivity) == true)
                {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse(new StringBuilder("tel:").append(PHONE_NUMBER_DAILYHOTEL).toString()));
                    startActivity(i);
                } else
                {
                    DailyToast.showToast(mHostActivity, R.string.toast_msg_no_call, Toast.LENGTH_LONG);
                }
            }
        };

        if (Util.isTextEmpty(mCSoperatingTimeMessage) == true)
        {
            mCSoperatingTimeMessage = getString(R.string.dialog_msg_call);
        }

        mHostActivity.showSimpleDialog(getString(R.string.dialog_notice2), mCSoperatingTimeMessage, getString(R.string.dialog_btn_call), null, positiveListener, null, null, new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                releaseUiComponent();
            }
        }, true);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (getActivity() == null)
            {
                return;
            }

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                String userEmail = response.getString("email");

                if ((userEmail != null) && !(userEmail.equals("")) && !(userEmail.equals("null")))
                {
                    invalidateLoginButton(true, userEmail);
                } else
                {
                    invalidateLoginButton(true, "");
                }

                mSettingCardLayout.setVisibility(View.VISIBLE);
            } catch (Exception e)
            {
                onError(e);
                invalidateLoginButton(true, "");
            } finally
            {
                unLockUI();
            }
        }
    };

    private DailyHotelStringResponseListener mUserAliveStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            if (getActivity() == null)
            {
                return;
            }

            String result = null;

            if (Util.isTextEmpty(response) == false)
            {
                result = response.trim();
            }

            if ("alive".equalsIgnoreCase(result) == true)
            {
                // session alive
                // 사용자 정보 요청.
                DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, mHostActivity);
            } else
            {
                mSettingCardLayout.setVisibility(View.GONE);

                invalidateLoginButton(false, "");
            }

            DailyNetworkAPI.getInstance().requestCommonDatetime(mNetworkTag, mDateTimeJsonResponseListener, mHostActivity);
        }
    };

    private DailyHotelJsonResponseListener mDateTimeJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (getActivity() == null)
            {
                return;
            }

            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH", Locale.KOREA);
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                mCSoperatingTimeMessage = getString(R.string.dialog_message_cs_operating_time //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("openDateTime")))) //
                    , Integer.parseInt(simpleDateFormat.format(new Date(response.getLong("closeDateTime")))));
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
}
