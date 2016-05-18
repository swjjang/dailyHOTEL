package com.twoheart.dailyhotel.screen.information.bonus;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.LoginActivity;
import com.twoheart.dailyhotel.screen.information.member.SignupStep1Activity;
import com.twoheart.dailyhotel.util.KakaoLinkManager;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener
{
    private static final String INTENT_EXTRA_DATA_CODE = "code";
    private static final String INTENT_EXTRA_DATA_NAME = "name";

    private String mRecommendCode;
    private String mName;

    public static final Intent newInstance(Context context, String code, String name)
    {
        Intent intent = new Intent(context, InviteFriendsActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CODE, code);
        intent.putExtra(INTENT_EXTRA_DATA_NAME, name);

        return intent;
    }

    public static final Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, InviteFriendsActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);

        Intent intent = getIntent();

        if (intent.hasExtra(INTENT_EXTRA_DATA_CODE) == true)
        {
            mRecommendCode = intent.getStringExtra(INTENT_EXTRA_DATA_CODE);
            mName = intent.getStringExtra(INTENT_EXTRA_DATA_NAME);
        }

        initToolbar();
        initLayout(mRecommendCode);
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

    private void initLayout(String code)
    {
        View signinButtonLayout = findViewById(R.id.signinButtonLayout);
        View noSigninButtonLayout = findViewById(R.id.noSigninButtonLayout);


        if (Util.isTextEmpty(code) == true)
        {
            signinButtonLayout.setVisibility(View.GONE);
            noSigninButtonLayout.setVisibility(View.VISIBLE);

            initNoSigninLayout();
        } else
        {
            signinButtonLayout.setVisibility(View.VISIBLE);
            noSigninButtonLayout.setVisibility(View.GONE);

            TextView codeTextView = (TextView) signinButtonLayout.findViewById(R.id.codeTextView);
            codeTextView.setText(code);

            initSigninLayout();
        }
    }

    private void initNoSigninLayout()
    {
        View signupTextView = findViewById(R.id.signupTextView);
        signupTextView.setOnClickListener(this);

        View signinTextView = findViewById(R.id.signinTextView);
        signinTextView.setOnClickListener(this);
    }

    private void initSigninLayout()
    {
        View copyCodeLayout = findViewById(R.id.copyCodeLayout);
        copyCodeLayout.setOnClickListener(this);

        View inviteKakaoTextView = findViewById(R.id.inviteKakaoTextView);
        inviteKakaoTextView.setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.inviteKakaoTextView:
            {
                try
                {
                    // 카카오톡 패키지 설치 여부
                    getPackageManager().getPackageInfo("com.kakao.talk", PackageManager.GET_META_DATA);

                    String msg = getString(R.string.kakaolink_msg_invited_friend, mName, mRecommendCode, mRecommendCode);
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

            case R.id.signinTextView:
            {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.LOGIN, 0L);
                break;
            }

            case R.id.signupTextView:
            {
                Intent intent = SignupStep1Activity.newInstance(InviteFriendsActivity.this);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.SIGNUP, 0L);
                break;
            }

            case R.id.copyCodeLayout:
            {
                Util.clipText(this, mRecommendCode);

                DailyToast.showToast(this, R.string.message_copy_recommendar_code, Toast.LENGTH_SHORT);
                break;
            }
        }
    }

    private BonusLayout.OnEventListener mOnEventListener = new BonusLayout.OnEventListener()
    {
        @Override
        public void onInviteFriends()
        {

        }

        @Override
        public void finish()
        {

        }
    };
}