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

        if (Util.isTextEmpty(DailyPreference.getInstance(this).getAuthorization()) == false//
            && Util.isTextEmpty(mRecommendCode) == true)
        {
            Util.restartApp(this);
            return;
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

            initNoSigninLayout(noSigninButtonLayout);
        } else
        {
            signinButtonLayout.setVisibility(View.VISIBLE);
            noSigninButtonLayout.setVisibility(View.GONE);

            initSigninLayout(signinButtonLayout, code);
        }
    }

    private void initNoSigninLayout(View view)
    {
        View signupTextView = view.findViewById(R.id.signupTextView);
        signupTextView.setOnClickListener(this);

        View signinTextView = view.findViewById(R.id.signinTextView);
        signinTextView.setOnClickListener(this);
    }

    private void initSigninLayout(View view, String code)
    {
        View copyCodeLayout = findViewById(R.id.copyCodeLayout);
        copyCodeLayout.setOnClickListener(this);

        View inviteKakaoTextView = view.findViewById(R.id.inviteKakaoTextView);
        inviteKakaoTextView.setOnClickListener(this);

        TextView codeTextView = (TextView) view.findViewById(R.id.codeTextView);
        codeTextView.setText(code);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        unLockUI();
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
                    Intent intent = BonusActivity.newInstance(InviteFriendsActivity.this);
                    startActivity(intent);

                    finish();
                }
                break;
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
                startActivityForResult(intent, REQUEST_ACTIVITY_SIGNUP);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                //            AnalyticsManager.getInstance(this).recordEvent(Screen.BONUS, Action.CLICK, Label.SIGNUP, 0L);
                break;
            }

            case R.id.copyCodeLayout:
            {
                Util.clipText(this, mRecommendCode);

                DailyToast.showToast(this, R.string.message_copy_recommendar_code, Toast.LENGTH_SHORT);

                releaseUiComponent();
                break;
            }

            default:
                releaseUiComponent();
                break;
        }
    }
}