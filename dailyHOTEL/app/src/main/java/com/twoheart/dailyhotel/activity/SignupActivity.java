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
package com.twoheart.dailyhotel.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends BaseActivity implements OnClickListener
{
    private static int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 1;

    private static final int MAX_OF_RECOMMENDER = 45;

    private static final int MODE_SIGNUP = 1;
    private static final int MODE_USERINFO_UPDATE = 2;

    private EditText mPhoneTextView, mEmailEditText, mNameEditText, mPasswordEditText, mRecommenderEditText;
    private TextView mTermTextView, mPrivacyTextView;
    private TextView mSingupView;
    private View mFakeMobileView;
    private int mMode;
    private String mUserIdx;
    private int mRecommender; // 추천인 코드
    private boolean mIsDailyUser;

    private Map<String, String> mSignupParams;
    private HashMap<String, String> regPushParams;

    private boolean mFirstMobileNumberFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        Intent intent = getIntent();

        Customer user = null;
        String phoneNumber = null;
        mFirstMobileNumberFocus = true;

        boolean isVisibleRecommender = true;

        if (intent.hasExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER) == true)
        {
            mMode = MODE_USERINFO_UPDATE;

            user = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

            mRecommender = intent.getIntExtra(NAME_INTENT_EXTRA_DATA_RECOMMENDER, -1);
            mIsDailyUser = intent.getBooleanExtra(NAME_INTENT_EXTRA_DATA_ISDAILYUSER, true);

            setActionBar(R.string.actionbar_title_userinfo_update_activity);

            if (user == null)
            {
                finish();
                return;
            }

            if (isFinishing() == true)
            {
                return;
            }

            if (mIsDailyUser == true)
            {
                isVisibleRecommender = false;
            } else
            {
                // 3가지 정보가 전부 있는 경우에는 소셜 유저가 업데이트 하는 경우이다
                if (Util.isTextEmpty(user.getName()) == false && Util.isTextEmpty(user.getEmail()) == false && Util.isTextEmpty(user.getPhone()) == false)
                {
                    isVisibleRecommender = false;
                }
            }

            if (Util.isValidatePhoneNumber(user.getPhone()) == false)
            {
                user.setPhone(null);
            } else
            {
                phoneNumber = user.getPhone();
            }

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
        } else
        {
            mMode = MODE_SIGNUP;
            mIsDailyUser = true;

            phoneNumber = Util.getLine1Number(this);

            setActionBar(R.string.actionbar_title_signup_activity);
        }

        initLayout(user, phoneNumber, isVisibleRecommender);
    }

    private void initLayout(Customer user, final String mobileNumber, boolean isVisibleRecommender)
    {
        mPasswordEditText = (EditText) findViewById(R.id.et_signup_pwd);
        mEmailEditText = (EditText) findViewById(R.id.et_signup_email);
        mRecommenderEditText = (EditText) findViewById(R.id.et_signup_recommender);
        mNameEditText = (EditText) findViewById(R.id.et_signup_name);

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

        mPhoneTextView = (EditText) findViewById(R.id.et_signup_phone);
        mPhoneTextView.setCursorVisible(false);

        mTermTextView = (TextView) findViewById(R.id.tv_signup_agreement);
        mPrivacyTextView = (TextView) findViewById(R.id.tv_signup_personal_info);
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

        mTermTextView.setOnClickListener(this);
        mPrivacyTextView.setOnClickListener(this);
        mSingupView.setOnClickListener(this);

        if (Util.isOverAPI23() == true)
        {
            if (hasPermission() == false)
            {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
            }
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(SignupActivity.this).recordScreen(Screen.SIGNUP);
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
            TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = telephonyManager.getDeviceId();

            if (deviceId == null)
            {
                return false;
            }
        }

        return true;
    }

    public boolean checkInput(boolean checkPassword)
    {
        if (mEmailEditText.getText().toString().trim().equals("") == true)
        {
            return false;
        } else if (mNameEditText.getText().toString().trim().equals("") == true)
        {
            return false;
        } else if (mPhoneTextView.getText().toString().trim().equals("") == true)
        {
            return false;
        } else if (checkPassword == true && mPasswordEditText.getText().toString().trim().equals("") == true)
        {
            return false;
        } else
        {
            return true;
        }
    }

    public boolean isValidPhone(String inputStr)
    {
        Pattern p = Pattern.compile("^(01[0|1|6|7|8|9])(\\d{4}|\\d{3})(\\d{4})$");
        Matcher m = p.matcher(inputStr);
        return m.matches();
    }

    public boolean isVaildrecommend(String inputStr)
    {
        Pattern p = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher m = p.matcher(inputStr);
        return m.matches();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == mSingupView.getId())
        {
            if (mMode == MODE_SIGNUP)
            {
                // 회원가입
                // 필수 입력 check
                if (checkInput(true) == false)
                {
                    DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // email check
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

                lockUI();

                mSignupParams = new HashMap<String, String>();
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
                AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.SIGNUP, Action.CLICK, Label.SIGNUP, 0L);
            } else
            {
                // 회원 정보 업데이트
                // 필수 입력 check
                if (checkInput(false) == false)
                {
                    DailyToast.showToast(SignupActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // email check
                if (mEmailEditText.isEnabled() == true && android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches() == false)
                {
                    DailyToast.showToast(SignupActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                lockUI();

                if (mIsDailyUser == true)
                {
                    Map<String, String> updateParams = new HashMap<String, String>();
                    updateParams.put("name", mNameEditText.getText().toString().trim());

                    String phoneNumber = mPhoneTextView.getText().toString().trim();
                    phoneNumber = phoneNumber.replaceAll("-", "");
                    updateParams.put("phone", phoneNumber);

                    DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, updateParams, mUserUpdateJsonResponseListener, this);
                } else
                {
                    Map<String, String> updateParams = new HashMap<String, String>();
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
                    if (recommender.equals("") == false)
                    {
                        updateParams.put("recommendation_code", recommender);
                    }

                    DailyNetworkAPI.getInstance().requestUserUpdateInformationForSocial(mNetworkTag, updateParams, mUserUpdateFacebookJsonResponseListener, this);
                }
            }
        } else if (v.getId() == mTermTextView.getId())
        { // 이용약관

            Intent i = new Intent(this, TermActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

        } else if (v.getId() == mPrivacyTextView.getId())
        { // 개인정보 취급

            Intent i = new Intent(this, PrivacyActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }
    }

    public void storeLoginInfo()
    {
        String id = mEmailEditText.getText().toString();
        String pwd = Crypto.encrypt(mPasswordEditText.getText().toString()).replace("\n", "");
        String name = mNameEditText.getText().toString();

        SharedPreferences.Editor ed = sharedPreference.edit();
        ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);
        ed.putString(KEY_PREFERENCE_USER_ID, id);
        ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
        ed.putString(KEY_PREFERENCE_USER_TYPE, "normal");
        ed.putString(KEY_PREFERENCE_USER_NAME, name);
        ed.commit();

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

    private void regGcmId(final String idx)
    {
        if (Util.isGooglePlayServicesAvailable(this) == false)
        {
            signUpAndFinish();
            return;
        }

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(SignupActivity.this);
                String regId = "";

                try
                {
                    regId = instance.register(GCM_PROJECT_NUMBER);
                } catch (IOException e)
                {
                    ExLog.e(e.toString());
                }

                return regId;
            }

            @Override
            protected void onPostExecute(String regId)
            {
                // gcm id가 없을 경우 스킵.
                if (Util.isTextEmpty(regId) == true)
                {
                    signUpAndFinish();
                    return;
                }

                // 이 값을 서버에 등록하기.
                regPushParams = new HashMap<String, String>();
                regPushParams.put("user_idx", idx);
                regPushParams.put("notification_id", regId);
                regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

                DailyNetworkAPI.getInstance().requestUserRegisterNotification(mNetworkTag, regPushParams, mGcmRegisterJsonResponseListener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        signUpAndFinish();
                    }
                });
            }
        }.execute();
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

                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

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
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

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

    private DailyHotelJsonResponseListener mGcmRegisterJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            unLockUI();

            // 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
            try
            {
                String result = null;

                if (null != response)
                {
                    result = response.getString("result");
                }

                if (true == "true".equalsIgnoreCase(result))
                {
                    Editor editor = sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id"));
                    editor.apply();
                }
            } catch (Exception e)
            {
            } finally
            {
                signUpAndFinish();
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

                regGcmId(userIndex);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);
                Date date = new Date();
                String strDate = dateFormat.format(date);

                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Label.CURRENT_TIME, strDate);
                params.put(Label.USER_INDEX, userIndex);
                params.put(Label.TYPE, "email");

                AnalyticsManager.getInstance(SignupActivity.this).recordEvent(Screen.SIGNUP, Action.NETWORK, Label.SIGNUP, params);
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
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

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
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

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
