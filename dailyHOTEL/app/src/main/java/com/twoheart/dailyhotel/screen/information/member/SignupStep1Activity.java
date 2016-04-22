package com.twoheart.dailyhotel.screen.information.member;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

public class SignupStep1Activity extends BaseActivity
{
    private static final int REQUEST_CODE_ACTIVITY = 100;

    private SignupStep1Layout mSignupStep1Layout;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, SignupStep1Activity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSignupStep1Layout = new SignupStep1Layout(this, mOnEventListener);

        setContentView(mSignupStep1Layout.onCreateView(R.layout.activity_signup_step1));

        if (Util.isOverAPI23() == true && hasPermission() == false)
        {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(SignupStep1Activity.this).recordScreen(Screen.SIGNUP, null);

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (hasPermission() == false)
                    {
                        finish();
                    }
                }
                break;
        }
    }

    private boolean hasPermission()
    {
        if (Util.isOverAPI23() == true)
        {
            String deviceId = Util.getDeviceId(this);

            if (deviceId == null)
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQEUST_ACTIVITY_SIGNUP:
            {
                if (resultCode == RESULT_OK)
                {
                    setResult(RESULT_OK);
                    finish();
                } else
                {

                }
                break;
            }
        }
    }

    private SignupStep1Layout.OnEventListener mOnEventListener = new SignupStep1Layout.OnEventListener()
    {
        @Override
        public void nextStep(String email, String name, String password, String confirmPassword, String recommender)
        {
            if (Util.isTextEmpty(email, name, password, confirmPassword) == true)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                return;
            }

            // email 유효성 체크
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                return;
            }

            // 패스워드 유효성 체크
            if (password.length() < 4)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_please_input_password_more_than_4chars, Toast.LENGTH_SHORT);
                return;
            }

            // 패스워드가 동일하게 입력되어있는지 확인
            if (password.equals(confirmPassword) == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.message_please_enter_the_same_password, Toast.LENGTH_SHORT);
                return;
            }

            // 동의 체크 확인
            if (mSignupStep1Layout.isCheckedTermsOfService() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_terms_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mSignupStep1Layout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = SignupStep2Activity.newInstance(SignupStep1Activity.this//
                , email, name, Crypto.encrypt(password), recommender);
            startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
        }

        @Override
        public void showTermOfService()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(SignupStep1Activity.this, TermActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void showTermOfPrivacy()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(SignupStep1Activity.this, PrivacyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void finish()
        {
            SignupStep1Activity.this.finish();
        }
    };
}
