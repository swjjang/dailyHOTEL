/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * SignupActivity (회원가입화면)
 * <p>
 * 새로운 사용자 가입하는 화면이다. 새로운 사용자로부터 이메일, 이름, 패스워드,
 * 추천인 코드를 입력받는다. 회원가입하는 웹서버 API를 이용한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.screen.information.member;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends BaseActivity implements OnClickListener
{
    private static final String INTENT_EXTRA_DATA_MODE = "mode";

    private static final int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 1;

    private static final int MAX_OF_RECOMMENDER = 45;

    private static final int MODE_SIGNUP = 1;
    private static final int MODE_USERINFO_UPDATE = 2;

    private EditText mPhoneTextView, mEmailEditText, mNameEditText, mPasswordEditText, mRecommenderEditText;
    private TextView mSingupView;
    private View mFakeMobileView;
    private CheckBox mAllAgreementCheckBox;
    private CheckBox mTermsCheckBox;
    private CheckBox mPersonalCheckBox;
    private int mMode;
    private String mUserIdx;
    private int mRecommender; // 추천인 코드
    private boolean mIsDailyUser;

    private Map<String, String> mSignupParams;

    private boolean mFirstMobileNumberFocus;

    public static Intent newInstance(Context context, Customer customer, int recommender, boolean isDailyUser)
    {
        Intent intent = new Intent(context, SignupActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_MODE, MODE_USERINFO_UPDATE);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, customer);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_RECOMMENDER, recommender);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_ISDAILYUSER, isDailyUser);

        return intent;
    }

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, SignupActivity.class);

        intent.putExtra(INTENT_EXTRA_DATA_MODE, MODE_SIGNUP);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();

        mFirstMobileNumberFocus = true;
        mMode = intent.getIntExtra(INTENT_EXTRA_DATA_MODE, MODE_SIGNUP);

        switch (mMode)
        {
            case MODE_SIGNUP:
                initSignupUser();
                break;

            case MODE_USERINFO_UPDATE:
                initUpdateUser(intent);
                break;
        }
    }

    private void initUpdateUser(Intent intent)
    {
        boolean isVisibleRecommender = true;
        String phoneNumber = null;
        Customer customer = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

        mRecommender = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_RECOMMENDER, -1);
        mIsDailyUser = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_ISDAILYUSER, true);

        initToolbar(getString(R.string.actionbar_title_userinfo_update_activity));

        if (mIsDailyUser == true)
        {
            isVisibleRecommender = false;
        } else
        {
            // 3가지 정보가 전부 있는 경우에는 소셜 유저가 업데이트 하는 경우이다
            if (Util.isTextEmpty(customer.getName()) == false && Util.isTextEmpty(customer.getEmail()) == false//
                && Util.isTextEmpty(customer.getPhone()) == false)
            {
                isVisibleRecommender = false;
            }
        }

        if (Util.isValidatePhoneNumber(customer.getPhone()) == false)
        {
            customer.setPhone(null);
        }

        phoneNumber = customer.getPhone();

        View.OnClickListener onClickListener = null;
        String message;

        // 전화번호만 업데이트 하는 경우
        if (isVisibleRecommender == true)
        {
            message = getString(R.string.dialog_msg_facebook_update);
        } else
        {
            message = getString(R.string.toast_msg_confirm_mobilenumber);

            onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mFakeMobileView != null)
                    {
                        mFakeMobileView.performClick();
                    }
                }
            };
        }

        showSimpleDialog(getString(R.string.dialog_notice2), message, getString(R.string.dialog_btn_text_confirm), null, onClickListener, null);

        initLayout(customer, phoneNumber, isVisibleRecommender);
    }

    private void initSignupUser()
    {
        mIsDailyUser = true;

        String phoneNumber = Util.getLine1Number(this);

        initToolbar(getString(R.string.actionbar_title_signup_activity));

        initLayout(null, phoneNumber, true);
    }

    private void initToolbar(String title)
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(title);
    }

    private void initLayout(Customer user, final String mobileNumber, boolean isVisibleRecommender)
    {
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mRecommenderEditText = (EditText) findViewById(R.id.recommenderEditText);
        mNameEditText = (EditText) findViewById(R.id.nameEditText);

        if (isVisibleRecommender == false)
        {
            mRecommenderEditText.setVisibility(View.GONE);
        }

        // 회원 가입시 이름 필터 적용.
        StringFilter stringFilter = new StringFilter(SignupActivity.this);
        InputFilter[] allowAlphanumericHangul = new InputFilter[1];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

        mNameEditText.setFilters(allowAlphanumericHangul);

        // 추천코드 최대 길이
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(MAX_OF_RECOMMENDER);
        mRecommenderEditText.setFilters(fArray);

        mPhoneTextView = (EditText) findViewById(R.id.phoneEditText);
        mPhoneTextView.setCursorVisible(false);

        mSingupView = (TextView) findViewById(R.id.btn_signup);

        if (user != null)
        {
            mUserIdx = user.getUserIdx();

            if (Util.isTextEmpty(user.getPhone()) == false)
            {
                mPhoneTextView.setText(user.getPhone());
                mPhoneTextView.setEnabled(false);
                mPhoneTextView.setFocusable(false);
            }

            if (Util.isTextEmpty(user.getEmail()) == false)
            {
                mEmailEditText.setText(user.getEmail());
                mEmailEditText.setEnabled(false);
                mEmailEditText.setFocusable(false);
            }

            if (Util.isTextEmpty(user.getName()) == false)
            {
                mNameEditText.setText(user.getName());
                mNameEditText.setEnabled(false);
                mNameEditText.setFocusable(false);
            }

            mPasswordEditText.setVisibility(View.GONE);
            mSingupView.setText(R.string.act_signup_btn_update);

            if (mRecommender >= 0)
            {
                mRecommenderEditText.setText(String.valueOf(mRecommender));
                mRecommenderEditText.setEnabled(false);
                mRecommenderEditText.setFocusable(false);
            }
        }

        mPhoneTextView.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (mFirstMobileNumberFocus == true)
                {
                    mPhoneTextView.setSelected(true);
                    mFirstMobileNumberFocus = false;
                    return;
                }

                if (hasFocus == true)
                {
                    showInputMobileNumberDialog(mobileNumber);
                } else
                {
                    mPhoneTextView.setSelected(false);
                }
            }
        });

        mFakeMobileView = findViewById(R.id.fakeMobileEditView);

        mFakeMobileView.setFocusable(true);
        mFakeMobileView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPhoneTextView.isSelected() == true)
                {
                    showInputMobileNumberDialog(mobileNumber);
                } else
                {
                    mPhoneTextView.requestFocus();
                    mPhoneTextView.setSelected(true);
                }
            }
        });

        mSingupView.setOnClickListener(this);

        if (Util.isOverAPI23() == true)
        {
            if (hasPermission() == false)
            {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
            }
        }

        initLayoutCheckBox();
    }

    private void initLayoutCheckBox()
    {
        mAllAgreementCheckBox = (CheckBox) findViewById(R.id.allAgreementCheckBox);
        mPersonalCheckBox = (CheckBox) findViewById(R.id.personalCheckBox);
        mTermsCheckBox = (CheckBox) findViewById(R.id.termsCheckBox);

        mAllAgreementCheckBox.setOnClickListener(this);
        mPersonalCheckBox.setOnClickListener(this);
        mTermsCheckBox.setOnClickListener(this);

        TextView termsContentView = (TextView) findViewById(R.id.termsContentView);
        termsContentView.setPaintFlags(termsContentView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsContentView.setOnClickListener(this);

        TextView personalContentView = (TextView) findViewById(R.id.personalContentView);
        personalContentView.setPaintFlags(personalContentView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        personalContentView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        if (mMode == MODE_SIGNUP)
        {
            AnalyticsManager.getInstance(SignupActivity.this).recordScreen(Screen.SIGNUP, null);
        }

        super.onStart();
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
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();

            if (deviceId == null)
            {
                return false;
            }
        }

        return true;
    }

    private boolean isEmptyFields(int mode)
    {
        if (Util.isTextEmpty(mEmailEditText.getText().toString().trim()) == true//
            || Util.isTextEmpty(mNameEditText.getText().toString().trim()) == true//
            || Util.isTextEmpty(mPhoneTextView.getText().toString().trim()) == true)
        {
            return false;
        }

        switch (mode)
        {
            case MODE_SIGNUP:
                break;

            case MODE_USERINFO_UPDATE:
                if (Util.isTextEmpty(mPasswordEditText.getText().toString().trim()) == true)
                {
                    return false;
                }
                break;
        }

        return true;
    }

    private boolean isCheckedAgreement()
    {
        if (mTermsCheckBox.isChecked() == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_terms_agreement, Toast.LENGTH_SHORT);
            return false;
        }

        if (mPersonalCheckBox.isChecked() == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }


    private void processSignup()
    {
        if (isEmptyFields(MODE_SIGNUP) == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
            return;
        }

        // email 유효성 체크
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches() == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
            return;
        }

        if (mPasswordEditText.length() < 4)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_password_more_than_4chars, Toast.LENGTH_SHORT);
            return;
        }

        if (isCheckedAgreement() == false)
        {
            return;
        }

        lockUI();

        mSignupParams = new HashMap<>();
        mSignupParams.put("email", mEmailEditText.getText().toString().trim());
        mSignupParams.put("pw", mPasswordEditText.getText().toString().trim());
        mSignupParams.put("name", mNameEditText.getText().toString().trim());

        String phoneNumber = mPhoneTextView.getText().toString().trim();
        phoneNumber = phoneNumber.replaceAll("-", "");

        mSignupParams.put("phone", phoneNumber);

        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mSignupParams.put("device", tManager.getDeviceId());
        mSignupParams.put("market_type", RELEASE_STORE.getName());

        String recommender = mRecommenderEditText.getText().toString().trim();
        if (Util.isTextEmpty(recommender) == false)
        {
            mSignupParams.put("recommender", recommender);
        }

        mSignupParams.put("social_id", "0");
        mSignupParams.put("user_type", "normal");

        DailyNetworkAPI.getInstance().requestUserSignup(mNetworkTag, mSignupParams, mUserSignupJsonResponseListener, this);
        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION//
            , Action.REGISTRATION_CLICKED, Label.AGREE_AND_REGISTER, null);
    }

    private void processUpdateUser()
    {
        if (isEmptyFields(MODE_USERINFO_UPDATE) == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
            return;
        }

        // email 유효성 체크
        if (mEmailEditText.isEnabled() == true && android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches() == false)
        {
            DailyToast.showToast(SignupActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
            return;
        }

        if (isCheckedAgreement() == false)
        {
            return;
        }

        lockUI();

        if (mIsDailyUser == true)
        {
            String name = mNameEditText.getText().toString().trim();
            String phone = mPhoneTextView.getText().toString().trim();
            phone = phone.replaceAll("-", "");

            DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, name, phone, mUserUpdateJsonResponseListener, this);
        } else
        {
            Map<String, String> updateParams = new HashMap<>();
            updateParams.put("user_idx", mUserIdx);

            if (mEmailEditText.isEnabled() == true)
            {
                updateParams.put("user_email", mEmailEditText.getText().toString().trim());
            }

            if (mNameEditText.isEnabled() == true)
            {
                updateParams.put("user_name", mNameEditText.getText().toString().trim());
            }

            if (mPhoneTextView.isEnabled() == true)
            {
                String phoneNumber = mPhoneTextView.getText().toString().trim();
                phoneNumber = phoneNumber.replaceAll("-", "");

                updateParams.put("user_phone", phoneNumber);
            }

            String recommender = mRecommenderEditText.getText().toString().trim();
            if (Util.isTextEmpty(recommender) == false)
            {
                updateParams.put("recommendation_code", recommender);
            }

            DailyNetworkAPI.getInstance().requestUserUpdateInformationForSocial(mNetworkTag, updateParams, mUserUpdateFacebookJsonResponseListener, this);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_signup:
                switch (mMode)
                {
                    case MODE_SIGNUP:
                        processSignup();
                        break;

                    case MODE_USERINFO_UPDATE:
                        processUpdateUser();
                        break;
                }
                break;

            case R.id.termsContentView:
            {
                Intent intent = new Intent(this, TermActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.personalContentView:
            {
                Intent intent = new Intent(this, PrivacyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.allAgreementCheckBox:
            {
                boolean isChecked = mAllAgreementCheckBox.isChecked();

                mTermsCheckBox.setChecked(isChecked);
                mPersonalCheckBox.setChecked(isChecked);
                break;
            }

            case R.id.personalCheckBox:
            case R.id.termsCheckBox:
                if (mPersonalCheckBox.isChecked() == true && mTermsCheckBox.isChecked() == true)
                {
                    mAllAgreementCheckBox.setChecked(true);
                } else
                {
                    mAllAgreementCheckBox.setChecked(false);
                }
                break;
        }
    }

    public void storeLoginInfo()
    {
        String id = mEmailEditText.getText().toString();
        String pwd = Crypto.encrypt(mPasswordEditText.getText().toString()).replace("\n", "");
        String name = mNameEditText.getText().toString();

        DailyPreference.getInstance(SignupActivity.this).setUserInformation(true, id, pwd, "normal", name);

        setResult(RESULT_OK);
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

        if (requestCode == REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                String mobileNumber = data.getStringExtra(InputMobileNumberDialogActivity.INTENT_EXTRA_MOBILE_NUMBER);

                mPhoneTextView.setText(mobileNumber);
            }
        }
    }

    private void signUpAndFinish()
    {
        unLockUI();

        DailyToast.showToast(SignupActivity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);
        finish();
    }

    private void showInputMobileNumberDialog(String mobileNumber)
    {
        String internationalMobileNumber = mobileNumber;

        if (mPhoneTextView.length() > 0)
        {
            internationalMobileNumber = mPhoneTextView.getText().toString();
        }

        Intent intent = InputMobileNumberDialogActivity.newInstance(SignupActivity.this, internationalMobileNumber);
        startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
    }

    private void requestGoogleCloudMessagingId()
    {
        Util.requestGoogleCloudMessaging(this, new Util.OnGoogleCloudMessagingListener()
        {
            @Override
            public void onResult(final String registrationId)
            {
                if (Util.isTextEmpty(registrationId) == false)
                {
                    DailyNetworkAPI.getInstance().requestUserRegisterNotification(mNetworkTag, registrationId, new DailyHotelJsonResponseListener()
                    {
                        @Override
                        public void onResponse(String url, JSONObject response)
                        {
                            try
                            {
                                int msg_code = response.getInt("msgCode");

                                if (msg_code == 0 && response.has("data") == true)
                                {
                                    JSONObject jsonObject = response.getJSONObject("data");

                                    int uid = jsonObject.getInt("uid");
                                    DailyPreference.getInstance(SignupActivity.this).setNotificationUid(uid);
                                    DailyPreference.getInstance(SignupActivity.this).setGCMRegistrationId(registrationId);
                                }
                            } catch (Exception e)
                            {
                                ExLog.d(e.toString());
                            } finally
                            {
                                signUpAndFinish();
                            }
                        }
                    }, new ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError arg0)
                        {
                            signUpAndFinish();
                        }
                    });
                } else
                {
                    signUpAndFinish();
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserUpdateFacebookJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                unLockUI();

                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = jsonObject.getBoolean("is_success");
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
                    String msg = null;

                    if (response.has("msg") == true)
                    {
                        msg = response.getString("msg");
                    }

                    switch (msgCode)
                    {
                        case 100:
                        {
                            if (msg != null)
                            {
                                DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_SHORT);
                            }

                            setResult(RESULT_OK);
                            finish();
                            break;
                        }

                        case 200:
                        {
                            if (msg != null)
                            {
                                if (isFinishing() == true)
                                {
                                    return;
                                }

                                showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }, null);
                            } else
                            {
                                setResult(RESULT_OK);
                                finish();
                            }
                            break;
                        }

                        default:
                            setResult(RESULT_OK);
                            finish();
                            break;
                    }

                } else
                {
                    String msg = null;

                    if (response.has("msg") == true)
                    {
                        msg = response.getString("msg");
                    }

                    switch (msgCode)
                    {
                        case 100:
                        {
                            if (msg != null)
                            {
                                DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_SHORT);
                            }
                            break;
                        }

                        case 200:
                        {
                            if (msg != null)
                            {
                                if (isFinishing() == true)
                                {
                                    return;
                                }

                                showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            try
            {
                String result = response.getString("success");
                String msg = null;

                if (response.length() > 1)
                {
                    msg = response.getString("msg");
                }

                if (result.equals("true") == true)
                {
                    DailyToast.showToast(SignupActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                    finish();
                } else
                {
                    DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_LONG);
                }
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
            try
            {
                String userIndex = String.valueOf(response.getInt("idx"));

                AnalyticsManager.getInstance(SignupActivity.this).setUserIndex(userIndex);
                AnalyticsManager.getInstance(SignupActivity.this).signUpDailyUser(userIndex, mSignupParams.get("email")//
                    , mSignupParams.get("name"), mSignupParams.get("phone"), AnalyticsManager.UserType.EMAIL);

                requestGoogleCloudMessagingId();
            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
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
                        storeLoginInfo();

                        lockUI();
                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, SignupActivity.this);
                        return;
                    }
                }

                // 로그인이 실패한 경우
                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = getString(R.string.toast_msg_failed_to_login);
                }

                DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_LONG);

                unLockUI();
                finish();
            } catch (Exception e)
            {
                unLockUI();
                onInternalError();
            }
        }
    };

    private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");

                if (msg_code == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignup = jsonObject.getBoolean("is_signup");

                    if (isSignup == true)
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("email", mSignupParams.get("email"));
                        params.put("pw", Crypto.encrypt(mSignupParams.get("pw")).replace("\n", ""));
                        params.put("social_id", "0");
                        params.put("user_type", "normal");
                        params.put("is_auto", "true");

                        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, SignupActivity.this);
                        return;
                    }
                }

                unLockUI();

                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = getString(R.string.toast_msg_failed_to_signup);
                }

                DailyToast.showToast(SignupActivity.this, msg, Toast.LENGTH_LONG);
            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            }
        }
    };
}
