package com.twoheart.dailyhotel.screen.information.member;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

public class SignupStep2Activity extends BaseActivity
{
    public static final int VERIFY_PHONE_NUMBER_COUNT = 4;

    private static final String INTENT_EXTRA_DATA_SIGNUPKEY = "signupKey";
    private static final String INTENT_EXTRA_DATA_EMAIL = "email";
    private static final String INTENT_EXTRA_DATA_PASSWORD = "password";
    private static final String INTENT_EXTRA_DATA_RECOMMENDER = "recommender";
    private static final String INTENT_EXTRA_DATA_AGREED_BENEFIT_DATE = "agreedBenefitDate";

    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private SignupStep2Layout mSignupStep2Layout;
    private SignupStep2NetworkController mNetworkController;
    private String mCountryCode;
    private String mSignupKey, mEmail, mPassword, mRecommender;
    private String mCallByScreen;
    private String mAgreedBenefitDate;
    private int mRequestVerficationCount;

    private Handler mRetryHandler;

    public static Intent newInstance(Context context, String singupKey, String email, String password,//
                                     String recommmender, String agreedBenefitDate, String callByScreen)
    {
        Intent intent = new Intent(context, SignupStep2Activity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SIGNUPKEY, singupKey);
        intent.putExtra(INTENT_EXTRA_DATA_EMAIL, email);
        intent.putExtra(INTENT_EXTRA_DATA_PASSWORD, password);
        intent.putExtra(INTENT_EXTRA_DATA_AGREED_BENEFIT_DATE, agreedBenefitDate);

        if (Util.isTextEmpty(recommmender) == true)
        {
            recommmender = "";
        }

        intent.putExtra(INTENT_EXTRA_DATA_RECOMMENDER, recommmender);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN, callByScreen);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mSignupStep2Layout = new SignupStep2Layout(this, mOnEventListener);
        mNetworkController = new SignupStep2NetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mSignupStep2Layout.onCreateView(R.layout.activity_signup_step2));

        initUserInformation(getIntent());

        mCountryCode = Util.getCountryNameNCode(this);
        mSignupStep2Layout.setCountryCode(mCountryCode);

        mRequestVerficationCount = 0;
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
        mAgreedBenefitDate = intent.getStringExtra(INTENT_EXTRA_DATA_AGREED_BENEFIT_DATE);
        mRecommender = intent.getStringExtra(INTENT_EXTRA_DATA_RECOMMENDER);

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN) == true)
        {
            mCallByScreen = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_CALL_BY_SCREEN);
        }
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

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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
        DailyPreference.getInstance(this).setShowBenefitAlarmFirstBuyer(false);
        DailyPreference.getInstance(this).setLastestCouponTime("");
        AnalyticsManager.getInstance(this).setPushEnabled(false, null);

        DailyToast.showToast(SignupStep2Activity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);

        setResult(RESULT_OK);
        finish();
    }

    private void showCompletedSignupDialog(boolean isBenefit, String updateDate)
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_signup_layout, null, false);

        final Dialog dialog = new Dialog(SignupStep2Activity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setText(getString(R.string.dialog_notice2));

        // 메시지
        TextView messageTextView01 = (TextView) dialogView.findViewById(R.id.messageTextView01);
        TextView messageTextView02 = (TextView) dialogView.findViewById(R.id.messageTextView02);

        messageTextView01.setText(DailyPreference.getInstance(SignupStep2Activity.this).getRemoteConfigTextSignUpText02());

        try
        {
            updateDate = DailyCalendar.convertDateFormatString(updateDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
        } catch (Exception e)
        {
            updateDate = null;
        }

        if (isBenefit == true && Util.isTextEmpty(updateDate) == false)
        {
            messageTextView02.setVisibility(View.VISIBLE);
            messageTextView02.setText(getString(R.string.message_benefit_alarm_on_confirm_format, updateDate));
        } else
        {
            messageTextView02.setVisibility(View.GONE);
        }

        TextView confirmTextView = (TextView) dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                signupAndFinish();
            }
        });

        try
        {
            dialog.setContentView(dialogView);
            dialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
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

            if (++mRequestVerficationCount == VERIFY_PHONE_NUMBER_COUNT)
            {
                try
                {
                    String[] phoneNumber = mSignupStep2Layout.getPhoneNumber().split(" ");
                    String number = phoneNumber[1].replaceAll("\\(|\\)", "");

                    message = getString(R.string.message_signup_step2_check_your_phonenumber, number);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

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
        public void onLogin(String authorization, final String userIndex, final String email, //
                            final String name, String recommender, String userType, final String phoneNumber, boolean isBenefit)
        {
            unLockUI();

            DailyPreference.getInstance(SignupStep2Activity.this).setAuthorization(authorization);
            DailyPreference.getInstance(SignupStep2Activity.this).setUserInformation(userType, email, name, recommender);

            // 혜택 알림 체크
            DailyPreference.getInstance(SignupStep2Activity.this).setUserBenefitAlarm(isBenefit);
            AnalyticsManager.getInstance(SignupStep2Activity.this).setPushEnabled(isBenefit, AnalyticsManager.ValueType.OTHER);

            AnalyticsManager.getInstance(SignupStep2Activity.this).setUserInformation(userIndex, userType);
            AnalyticsManager.getInstance(SignupStep2Activity.this).recordScreen(Screen.MENU_REGISTRATION_CONFIRM);
            AnalyticsManager.getInstance(SignupStep2Activity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.SIGN_UP, AnalyticsManager.UserType.EMAIL, null);

            // 이미 가입된것이기 때문에 미리 Analytics 넣음
            AnalyticsManager.getInstance(SignupStep2Activity.this).signUpDailyUser( //
                userIndex, email, name, phoneNumber, Constants.DAILY_USER, mRecommender, mCallByScreen);


            showCompletedSignupDialog(isBenefit, mAgreedBenefitDate);
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
        public void onRetryDailyUserSignIn()
        {
            if (mRetryHandler != null)
            {
                mRetryHandler = null;
                ExLog.d("mRetryHandler already run");
            } else
            {
                mRetryHandler = new Handler();
                mRetryHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mNetworkController.requestLogin(mEmail, mPassword);
                    }
                }, 500);
            }
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
