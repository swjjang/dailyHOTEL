package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

public class SignupStep2Activity extends BaseActivity
{
    private static final String INTENT_EXTRA_DATA_SIGNUPKEY = "signupKey";
    private static final String INTENT_EXTRA_DATA_EMAIL = "email";
    private static final String INTENT_EXTRA_DATA_PASSWORD = "password";
    private static final String INTENT_EXTRA_DATA_RECOMMENDER = "recommender";

    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private SignupStep2Layout mSignupStep2Layout;
    private SignupStep2NetworkController mNetworkController;
    private String mCountryCode;
    private String mSignupKey, mEmail, mPassword, mRecommender;

    public static Intent newInstance(Context context, String singupKey, String email, String password, String recommmender)
    {
        Intent intent = new Intent(context, SignupStep2Activity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SIGNUPKEY, singupKey);
        intent.putExtra(INTENT_EXTRA_DATA_EMAIL, email);
        intent.putExtra(INTENT_EXTRA_DATA_PASSWORD, password);

        if (Util.isTextEmpty(recommmender) == true)
        {
            recommmender = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_RECOMMENDER, recommmender);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSignupStep2Layout = new SignupStep2Layout(this, mOnEventListener);
        mNetworkController = new SignupStep2NetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mSignupStep2Layout.onCreateView(R.layout.activity_signup_step2));

        initUserInformation(getIntent());

        mCountryCode = Util.getCountryNameNCode(this);
        mSignupStep2Layout.setCountryCode(mCountryCode);
    }

    private void initUserInformation(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        mSignupKey = intent.getStringExtra(INTENT_EXTRA_DATA_SIGNUPKEY);
        mEmail = intent.getStringExtra(INTENT_EXTRA_DATA_EMAIL);
        mPassword = intent.getStringExtra(INTENT_EXTRA_DATA_PASSWORD);
        mRecommender = intent.getStringExtra(INTENT_EXTRA_DATA_RECOMMENDER);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(SignupStep2Activity.this).recordScreen(Screen.MENU_REGISTRATION_PHONENUMBERVERIFICATION);

        super.onStart();
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
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                mCountryCode = data.getStringExtra(CountryCodeListActivity.INTENT_EXTRA_COUNTRY_CODE);

                mSignupStep2Layout.setCountryCode(mCountryCode);
            }
        }
    }

    private void signupAndFinish()
    {
        DailyPreference.getInstance(this).setUserBenefitAlarm(false);
        DailyPreference.getInstance(this).setShowBenefitAlarm(false);

        DailyToast.showToast(SignupStep2Activity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);

        setResult(RESULT_OK);
        finish();
    }

    private SignupStep2Layout.OnEventListener mOnEventListener = new SignupStep2Layout.OnEventListener()
    {
        @Override
        public void showCountryCodeList()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = CountryCodeListActivity.newInstance(SignupStep2Activity.this, mCountryCode);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void doVerification(String phoneNumber)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            mNetworkController.requestVerfication(mSignupKey, phoneNumber.replaceAll("-", ""), false);
        }

        @Override
        public void doSignUp(String verificationNumber, String phoneNumber)
        {
            if (Util.isTextEmpty(verificationNumber) == true)
            {
                DailyToast.showToast(SignupStep2Activity.this, getString(R.string.message_wrong_certificationnumber), Toast.LENGTH_SHORT);
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            lockUI();

            mNetworkController.requestSingUp(mSignupKey, verificationNumber, phoneNumber);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
                , AnalyticsManager.Action.REGISTRATION_CLICKED, AnalyticsManager.Label.AGREE_AND_REGISTER, null);
        }

        @Override
        public void finish()
        {
            SignupStep2Activity.this.finish();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private SignupStep2NetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new SignupStep2NetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onVerification(String message)
        {
            unLockUI();

            mSignupStep2Layout.showVerificationVisible();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onSignUp(int notificationUid, String gcmRegisterId)
        {
            DailyPreference.getInstance(SignupStep2Activity.this).setVerification(true);

            if (notificationUid > 0)
            {
                DailyPreference.getInstance(SignupStep2Activity.this).setNotificationUid(notificationUid);
            }

            if (Util.isTextEmpty(gcmRegisterId) == false)
            {
                DailyPreference.getInstance(SignupStep2Activity.this).setGCMRegistrationId(gcmRegisterId);
            }

            mNetworkController.requestLogin(mEmail, mPassword);
        }

        @Override
        public void onLogin(String authorization, String userIndex, String email, String name, String recommender, String userType, String phoneNumber)
        {
            unLockUI();

            DailyPreference.getInstance(SignupStep2Activity.this).setAuthorization(authorization);
            DailyPreference.getInstance(SignupStep2Activity.this).setUserInformation(userType, email, name, recommender);

            AnalyticsManager.getInstance(SignupStep2Activity.this).setUserIndex(userIndex);
            AnalyticsManager.getInstance(SignupStep2Activity.this).signUpDailyUser(userIndex, email, name, phoneNumber, Constants.DAILY_USER, mRecommender);

            showSimpleDialog(null, getString(R.string.toast_msg_success_to_signup), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    signupAndFinish();
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    signupAndFinish();
                }
            });
        }

        @Override
        public void onAlreadyVerification(final String phoneNumber)
        {
            unLockUI();

            showSimpleDialog(null, getString(R.string.message_signup_step2_already_verification)//
                , getString(R.string.dialog_btn_text_continue)//
                , getString(R.string.dialog_btn_text_input_other_phonenumber), new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        lockUI();

                        mNetworkController.requestVerfication(mSignupKey, phoneNumber, true);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mSignupStep2Layout.resetPhoneNumber();
                    }
                });
        }

        @Override
        public void onInvalidPhoneNumber(String message)
        {
            unLockUI();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onInvalidVerificationNumber(String message)
        {
            unLockUI();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            SignupStep2Activity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            SignupStep2Activity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            SignupStep2Activity.this.onErrorPopupMessage(msgCode, message, null);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            SignupStep2Activity.this.onErrorToastMessage(message);
        }
    };
}
