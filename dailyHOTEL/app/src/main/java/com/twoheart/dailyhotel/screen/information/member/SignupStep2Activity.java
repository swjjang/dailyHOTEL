package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.HashMap;
import java.util.Map;

public class SignupStep2Activity extends BaseActivity
{
    private static final String INTENT_EXTRA_DATA_EMAIL = "email";
    private static final String INTENT_EXTRA_DATA_NAME = "name";
    private static final String INTENT_EXTRA_DATA_PASSWORD = "password";
    private static final String INTENT_EXTRA_DATA_RECOMMENDER = "recommender";

    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private Map<String, String> mSignupParams;

    private SignupStep2Layout mSignupStep2Layout;
    private SignupStep2NetworkController mNetworkController;
    private String mCountryCode;

    public static Intent newInstance(Context context, String email, String name, String password, String recommender)
    {
        Intent intent = new Intent(context, SignupStep2Activity.class);

        intent.putExtra(INTENT_EXTRA_DATA_EMAIL, email);
        intent.putExtra(INTENT_EXTRA_DATA_NAME, name);
        intent.putExtra(INTENT_EXTRA_DATA_PASSWORD, password);
        intent.putExtra(INTENT_EXTRA_DATA_RECOMMENDER, recommender);

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

        mSignupParams = new HashMap<>();
        mSignupParams.put("email", intent.getStringExtra(INTENT_EXTRA_DATA_EMAIL));
        mSignupParams.put("name", intent.getStringExtra(INTENT_EXTRA_DATA_NAME));
        mSignupParams.put("pw", intent.getStringExtra(INTENT_EXTRA_DATA_PASSWORD));
        mSignupParams.put("device", Util.getDeviceId(this));
        mSignupParams.put("market_type", RELEASE_STORE.getName());

        String recommender = intent.getStringExtra(INTENT_EXTRA_DATA_RECOMMENDER);
        if (Util.isTextEmpty(recommender) == false)
        {
            mSignupParams.put("recommender", recommender);
        }

        mSignupParams.put("social_id", "0");
        mSignupParams.put("user_type", Constants.DAILY_USER);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(SignupStep2Activity.this).recordScreen(Screen.SIGNUP, null);

        super.onStart();
    }

    public void storeUserInformation(String authorization)
    {
        //        String id = mSignupParams.get("email");
        //        String pwd = Crypto.encrypt(mPasswordEditText.getText().toString()).replace("\n", "");
        //        String name = mNameEditText.getText().toString();
        //
        //        DailyPreference.getInstance(SignupStep2Activity.this).setUserInformation(true, id, pwd, Constants.DAILY_USER, name);
        //        DailyPreference.getInstance(SignupStep2Activity.this).setAuthorization(authorization);
        //
        //        setResult(RESULT_OK);
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

    private void signUpAndFinish()
    {
        unLockUI();

        DailyToast.showToast(SignupStep2Activity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);
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

        }

        @Override
        public void doSignUp(String phoneNumber, String verificationNumber)
        {
            if (Util.isTextEmpty(phoneNumber) == true || Util.isTextEmpty(verificationNumber) == true)
            {
                return;
            }

            //            lockUI();
            //
            //            mSignupParams.put("phone", phoneNumber);
            //            mSignupParams.put("verification", verificationNumber);
            //
            //            mNetworkController.requestUserSingUp(mSignupParams);
            //            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
            //                , Action.REGISTRATION_CLICKED, Label.AGREE_AND_REGISTER, null);
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
        public void onSignUp(int notificationUid, String gcmRegisterId)
        {

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
        public void onErrorMessage(int msgCode, String message)
        {
            SignupStep2Activity.this.onErrorMessage(msgCode, message);
        }
    };


    //    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                String userIndex = String.valueOf(response.getInt("idx"));
    //
    //                AnalyticsManager.getInstance(SignupStep2Activity.this).setUserIndex(userIndex);
    //                AnalyticsManager.getInstance(SignupStep2Activity.this).signUpDailyUser(userIndex, mSignupParams.get("email")//
    //                    , mSignupParams.get("name"), mSignupParams.get("phone"), AnalyticsManager.UserType.EMAIL);
    //
    //                requestGoogleCloudMessagingId();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignin = jsonObject.getBoolean("is_signin");
    //
    //                    if (isSignin == true)
    //                    {
    //                        JSONObject tokenJSONObject = response.getJSONObject("token");
    //                        String accessToken = tokenJSONObject.getString("access_token");
    //                        String tokenType = tokenJSONObject.getString("token_type");
    //
    //                        storeUserInformation(String.format("%s %s", tokenType, accessToken));
    //
    //                        lockUI();
    //                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, SignupStep2Activity.this);
    //                        return;
    //                    }
    //                }
    //
    //                // 로그인이 실패한 경우
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_login);
    //                }
    //
    //                DailyToast.showToast(SignupStep2Activity.this, msg, Toast.LENGTH_LONG);
    //
    //                unLockUI();
    //                finish();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignup = jsonObject.getBoolean("is_signup");
    //
    //                    if (isSignup == true)
    //                    {
    //                        Map<String, String> params = new HashMap<>();
    //                        params.put("email", mSignupParams.get("email"));
    //                        params.put("pw", Crypto.encrypt(mSignupParams.get("pw")).replace("\n", ""));
    //                        params.put("social_id", "0");
    //                        params.put("user_type", Constants.DAILY_USER);
    //                        params.put("is_auto", "true");
    //
    //                        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, SignupStep2Activity.this);
    //                        return;
    //                    }
    //                }
    //
    //                unLockUI();
    //
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_signup);
    //                }
    //
    //                DailyToast.showToast(SignupStep2Activity.this, msg, Toast.LENGTH_LONG);
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
}
