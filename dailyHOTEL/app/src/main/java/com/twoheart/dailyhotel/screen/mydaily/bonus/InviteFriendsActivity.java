package com.twoheart.dailyhotel.screen.mydaily.bonus;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.LoginActivity;
import com.twoheart.dailyhotel.screen.mydaily.member.SignupStep1Activity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener
{
    private static final int REQUEST_ACTIVITY_SIGNUP = 10000;
    private static final int REQUEST_ACTIVITY_LOGIN = 10001;

    private static final String INTENT_EXTRA_DATA_CODE = "code";
    private static final String INTENT_EXTRA_DATA_NAME = "name";

    private String mRecommendCode;
    private String mName;

    private View mSigninButtonLayout, mNoSigninButtonLayout;
    private TextView mRecommendCodeTextView;
    private TextView mExceedMessageTextView;

    public static Intent newInstance(Context context, String code, String name)
    {
        Intent intent = new Intent(context, InviteFriendsActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CODE, code);
        intent.putExtra(INTENT_EXTRA_DATA_NAME, name);

        return intent;
    }

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, InviteFriendsActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRA_DATA_CODE) == true)
        {
            mRecommendCode = intent.getStringExtra(INTENT_EXTRA_DATA_CODE);
            mName = intent.getStringExtra(INTENT_EXTRA_DATA_NAME);
        }

        if (DailyHotel.isLogin() == true && Util.isTextEmpty(mRecommendCode) == true)
        {
            Util.restartApp(this);
            return;
        }

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);

        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_invite_friends), new View.OnClickListener()
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
        mSigninButtonLayout = findViewById(R.id.signinButtonLayout);
        mNoSigninButtonLayout = findViewById(R.id.noSigninButtonLayout);
        mExceedMessageTextView = (TextView) findViewById(R.id.exceedMessageTextView);

        initNoSigninLayout(mNoSigninButtonLayout);
        initSigninLayout(mSigninButtonLayout);
    }

    private void initNoSigninLayout(View view)
    {
        View signupTextView = view.findViewById(R.id.signupTextView);
        signupTextView.setOnClickListener(this);

        View signinTextView = view.findViewById(R.id.signinTextView);
        signinTextView.setOnClickListener(this);
    }

    private void initSigninLayout(View view)
    {
        View copyCodeLayout = findViewById(R.id.copyCodeLayout);
        copyCodeLayout.setOnClickListener(this);

        View inviteKakaoTextView = view.findViewById(R.id.inviteKakaoTextView);
        inviteKakaoTextView.setOnClickListener(this);

        mRecommendCodeTextView = (TextView) view.findViewById(R.id.codeTextView);
    }

    private void updateLayout(boolean isLogin)
    {
        if (mSigninButtonLayout == null || mNoSigninButtonLayout == null || mExceedMessageTextView == null)
        {
            Util.restartApp(this);
            return;
        }

        if (isLogin == true)
        {
            mSigninButtonLayout.setVisibility(View.VISIBLE);
            mNoSigninButtonLayout.setVisibility(View.GONE);

            boolean isExceedBonus = DailyPreference.getInstance(InviteFriendsActivity.this).isUserExceedBonus();

            mExceedMessageTextView.setText(isExceedBonus == true ? //
                R.string.act_credit_line4_is_exceed_bonus : R.string.act_credit_line4);
        } else
        {
            mSigninButtonLayout.setVisibility(View.GONE);
            mNoSigninButtonLayout.setVisibility(View.VISIBLE);
            mExceedMessageTextView.setText(R.string.act_credit_line4);
        }
    }

    private void setRecommendCodeText(String recommendCode)
    {
        if (mRecommendCodeTextView == null)
        {
            Util.restartApp(this);
            return;
        }

        mRecommendCodeTextView.setText(recommendCode);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if (DailyHotel.isLogin() == true)
        {
            AnalyticsManager.getInstance(InviteFriendsActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_INVITE_FRIENDS, null);
        } else
        {
            AnalyticsManager.getInstance(InviteFriendsActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_INVITE_FRIENDS_BEFORE_LOGIN, null);
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();

        unLockUI();

        if (DailyHotel.isLogin() == false)
        {
            updateLayout(false);
        } else
        {
            updateLayout(true);
            setRecommendCodeText(mRecommendCode);
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_ACTIVITY_SIGNUP:
                if (resultCode == RESULT_OK)
                {
                    mName = DailyPreference.getInstance(this).getUserName();
                    mRecommendCode = DailyPreference.getInstance(this).getUserRecommender();

                    inviteFriendsKakao(mName, mRecommendCode);
                }
                break;

            case REQUEST_ACTIVITY_LOGIN:
                if (resultCode == RESULT_OK)
                {
                    mName = DailyPreference.getInstance(this).getUserName();
                    mRecommendCode = DailyPreference.getInstance(this).getUserRecommender();
                }
                break;
        }
    }

    private void inviteFriendsKakao(String name, String recommendCode)
    {
        try
        {
            // 카카오톡 패키지 설치 여부
            getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

            String message;

            if (Util.isTextEmpty(name) == false)
            {
                message = getString(R.string.kakaolink_msg_invited_friend, name, recommendCode, recommendCode);
            } else
            {
                message = getString(R.string.kakaolink_msg_none_name_invited_friend, recommendCode, recommendCode);
            }

            KakaoLinkManager.newInstance(this).sendInviteKakaoLink(message, recommendCode);

            AnalyticsManager.getInstance(this).recordEvent(AnalyticsManager.Category.INVITE_FRIEND, //
                Action.KAKAO_FRIEND_INVITED, mRecommendCode, null);
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
    }

    @Override
    public void onClick(View v)
    {
        lockUiComponent();

        switch (v.getId())
        {
            case R.id.inviteKakaoTextView:
            {
                inviteFriendsKakao(mName, mRecommendCode);
                break;
            }

            case R.id.signinTextView:
            {
                Intent intent = LoginActivity.newInstance(this);
                startActivityForResult(intent, REQUEST_ACTIVITY_LOGIN);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.LOGIN, 0L);
                break;
            }

            case R.id.signupTextView:
            {
                Intent intent = SignupStep1Activity.newInstance(InviteFriendsActivity.this, null);
                startActivityForResult(intent, REQUEST_ACTIVITY_SIGNUP);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.SIGNUP, 0L);
                break;
            }

            case R.id.copyCodeLayout:
            {
                Util.clipText(this, mRecommendCode);

                DailyToast.showToast(this, R.string.message_copy_recommender_code, Toast.LENGTH_SHORT);

                AnalyticsManager.getInstance(InviteFriendsActivity.this).recordEvent(AnalyticsManager.Category.INVITE_FRIEND, //
                    Action.REFERRAL_CODE_COPIED, AnalyticsManager.Label.REFERRAL_CODE_COPIED, null);

                releaseUiComponent();
                break;
            }

            default:
                releaseUiComponent();
                break;
        }
    }
}