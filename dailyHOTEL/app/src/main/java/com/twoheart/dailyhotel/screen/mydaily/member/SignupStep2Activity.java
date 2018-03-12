package com.twoheart.dailyhotel.screen.mydaily.member;

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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;

import retrofit2.Call;
import retrofit2.Response;

public class SignupStep2Activity extends BaseActivity
{
    public static final int VERIFY_PHONE_NUMBER_COUNT = 4;

    private static final String INTENT_EXTRA_DATA_SIGNUPKEY = "signupKey";
    private static final String INTENT_EXTRA_DATA_EMAIL = "email";
    private static final String INTENT_EXTRA_DATA_PASSWORD = "password";
    private static final String INTENT_EXTRA_DATA_RECOMMENDER = "recommender";
    private static final String INTENT_EXTRA_DATA_AGREED_BENEFIT_DATE = "agreedBenefitDate";

    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    SignupStep2Layout mSignupStep2Layout;
    SignupStep2NetworkController mNetworkController;
    String mCountryCode;
    String mSignupKey, mEmail, mPassword, mRecommender;
    String mCallByScreen;
    String mAgreedBenefitDate;
    int mRequestVerificationCount;

    Handler mRetryHandler;

    public static Intent newInstance(Context context, String singupKey, String email, String password,//
                                     String agreedBenefitDate, String recommmender, String callByScreen)
    {
        Intent intent = new Intent(context, SignupStep2Activity.class);

        intent.putExtra(INTENT_EXTRA_DATA_SIGNUPKEY, singupKey);
        intent.putExtra(INTENT_EXTRA_DATA_EMAIL, email);
        intent.putExtra(INTENT_EXTRA_DATA_PASSWORD, password);
        intent.putExtra(INTENT_EXTRA_DATA_AGREED_BENEFIT_DATE, agreedBenefitDate);

        if (DailyTextUtils.isTextEmpty(recommmender) == true)
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

        mRequestVerificationCount = 0;
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
        AnalyticsManager.getInstance(SignupStep2Activity.this).recordScreen(SignupStep2Activity.this, Screen.MENU_REGISTRATION_PHONENUMBERVERIFICATION, null);

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

    void signupAndFinish()
    {
        DailyUserPreference.getInstance(this).setBenefitAlarm(false);
        DailyPreference.getInstance(this).setShowBenefitAlarm(false);
        DailyPreference.getInstance(this).setShowBenefitAlarmFirstBuyer(false);
        DailyPreference.getInstance(this).setLatestCouponTime("");

        DailyToast.showToast(SignupStep2Activity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);

        setResult(RESULT_OK);
        finish();
    }

    void showCompletedSignupDialog(boolean isBenefit, String updateDate)
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
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        titleTextView.setText(DailyRemoteConfigPreference.getInstance(SignupStep2Activity.this).getRemoteConfigTextSignUpText02());

        // 메시지
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);

        try
        {
            updateDate = DailyCalendar.convertDateFormatString(updateDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
        } catch (Exception e)
        {
            updateDate = null;
        }

        if (isBenefit == true && DailyTextUtils.isTextEmpty(updateDate) == false)
        {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(getString(R.string.message_signup_completed_alarm_on_format, updateDate));
        } else
        {
            messageTextView.setVisibility(View.GONE);
        }

        TextView confirmTextView = dialogView.findViewById(R.id.confirmTextView);
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

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
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

            mNetworkController.requestVerification(mSignupKey, phoneNumber.replaceAll("-", ""), false);
        }

        @Override
        public void doSignUp(String verificationNumber, String phoneNumber)
        {
            if (DailyTextUtils.isTextEmpty(verificationNumber) == true)
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

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION_//
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

            if (++mRequestVerificationCount == VERIFY_PHONE_NUMBER_COUNT)
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
        public void onSignUp()
        {
            DailyPreference.getInstance(SignupStep2Activity.this).setVerification(true);

            mNetworkController.requestLogin(mEmail, mPassword);
        }

        @Override
        public void onLogin(String authorization, final String userIndex, final String email, final String name,//
                            String birthday, String recommender, String userType, final String phoneNumber, boolean isBenefit)
        {
            unLockUI();

            DailyUserPreference.getInstance(SignupStep2Activity.this).setAuthorization(authorization);
            DailyUserPreference.getInstance(SignupStep2Activity.this).setInformation(userType, email, name, birthday, recommender);

            // 혜택 알림 체크
            DailyUserPreference.getInstance(SignupStep2Activity.this).setBenefitAlarm(isBenefit);
            AnalyticsManager.getInstance(SignupStep2Activity.this).setPushEnabled(isBenefit, AnalyticsManager.ValueType.OTHER);

            AnalyticsManager.getInstance(SignupStep2Activity.this).setUserInformation(userIndex, userType);
            AnalyticsManager.getInstance(SignupStep2Activity.this).recordScreen(SignupStep2Activity.this, Screen.MENU_REGISTRATION_CONFIRM, null);
            AnalyticsManager.getInstance(SignupStep2Activity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.SIGN_UP, AnalyticsManager.UserType.EMAIL, null);

            // 이미 가입된것이기 때문에 미리 Analytics 넣음
            AnalyticsManager.getInstance(SignupStep2Activity.this).signUpDailyUser( //
                userIndex, birthday, Constants.DAILY_USER, mRecommender, mCallByScreen);

            showCompletedSignupDialog(isBenefit, mAgreedBenefitDate);

            // 내가 추천인 코드를 넣고 회원 가입을 하는 경우
            if (DailyTextUtils.isTextEmpty(mRecommender) == false)
            {
                AnalyticsManager.getInstance(SignupStep2Activity.this).recordEvent(AnalyticsManager.Category.INVITE_FRIEND//
                    , AnalyticsManager.Action.REFERRAL_CODE, AnalyticsManager.Label.SUCCESS, null);
            }

            if (DailyTextUtils.isTextEmpty(birthday) == false)
            {
                // 생일을 입력한 경우 체크
                AnalyticsManager.getInstance(SignupStep2Activity.this).recordEvent(AnalyticsManager.Category.SET_MY_BIRTHDAY//
                    , AnalyticsManager.Action.PROFILE_CLICKED, birthday, null);
            }
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

                        mNetworkController.requestVerification(mSignupKey, phoneNumber, true);
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
            // 회원 가입후에 바로 로그인을 요청하는 경우 실패가발생하는 경우가 있다. 그래서 재시도..
            if (mRetryHandler != null)
            {
                // 한번 재시도 후에 또 로그인이 안되면 앱을 다시 재시동 시킨다
                mRetryHandler = null;

                DailyToast.showToast(SignupStep2Activity.this, R.string.toast_msg_retry_login, Toast.LENGTH_SHORT);
                Util.restartApp(SignupStep2Activity.this);
                return;
            }

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

        @Override
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            SignupStep2Activity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
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

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            SignupStep2Activity.this.onErrorResponse(call, response);
        }
    };
}
