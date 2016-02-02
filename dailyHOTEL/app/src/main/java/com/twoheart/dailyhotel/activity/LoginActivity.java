/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * <p>
 * LoginActivity (로그인화면)
 * <p>
 * 사용자 계정 로그인을 담당하는 화면이다. 사용자로부터 아이디와 패스워드를
 * 입력받으며, 이를 로그인을 하는 웹서버 API를 이용한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LoginActivity extends BaseActivity implements Constants, OnClickListener, ErrorListener
{
    public CallbackManager mCallbackManager;
    private EditText mIdEditText, mPasswordEditText;
    private SwitchCompat mAutoLoginSwitch;
    private TextView mLoginView;
    private TextView mSignupView, mFindPasswordView;
    private com.facebook.login.widget.LoginButton mFacebookLoginView;

    private Map<String, String> mStoreParams;
    private Map<String, String> mRegPushParams;

    // 카카오톡
    private com.kakao.usermgmt.LoginButton mKakaoLoginView;
    private SessionCallback mKakaoSessionCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DailyHotel.setCurrentActivity(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        initToolbar();

        mIdEditText = (EditText) findViewById(R.id.et_login_id);
        mPasswordEditText = (EditText) findViewById(R.id.et_login_pwd);
        mAutoLoginSwitch = (SwitchCompat) findViewById(R.id.cb_login_auto);
        mSignupView = (TextView) findViewById(R.id.tv_login_signup);
        mFindPasswordView = (TextView) findViewById(R.id.tv_login_forgot);
        mLoginView = (TextView) findViewById(R.id.btn_login);

        mFacebookLoginView = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebookLoginButton);
        mFacebookLoginView.setReadPermissions(Collections.singletonList("public_profile"));

        View facebookLoginView = findViewById(R.id.facebookLoginView);
        facebookLoginView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFacebookLoginView.performClick();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        mFacebookLoginView.registerCallback(mCallbackManager, facebookCallback);

        FontManager.apply(mFacebookLoginView, FontManager.getInstance(getApplicationContext()).getRegularTypeface());

        mKakaoLoginView = (com.kakao.usermgmt.LoginButton) findViewById(R.id.kakaoLoginButton);
        View kakaoLoginView = findViewById(R.id.kakaoLoginView);
        kakaoLoginView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mKakaoLoginView.performClick();
            }
        });

        mKakaoSessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        mAutoLoginSwitch.setChecked(true);
        mAutoLoginSwitch.setSwitchPadding(Util.dpToPx(LoginActivity.this, 15));

        mSignupView.setOnClickListener(this);
        mFindPasswordView.setOnClickListener(this);
        mLoginView.setOnClickListener(this);
        mFacebookLoginView.setOnClickListener(this);

        mPasswordEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mPasswordEditText.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        mLoginView.performClick();
                        break;
                }
                return false;
            }
        });

        if (Util.isOverAPI23() == true)
        {
            if (hasPermission() == false)
            {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
            }
        }
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_login_activity));
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(LoginActivity.this).recordScreen(Screen.LOGIN);
        super.onStart();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        Session.getCurrentSession().removeCallback(mKakaoSessionCallback);
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


    private void registerFacebookUser(String id, String name, String email, String gender)
    {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        String encryptedId = Crypto.encrypt(id).replace("\n", "");
        String deviceId = telephonyManager.getDeviceId();

        if (mStoreParams == null)
        {
            mStoreParams = new HashMap<String, String>();
        }

        mStoreParams.clear();
        HashMap<String, String> params = new HashMap<String, String>();

        if (Util.isTextEmpty(email) == false)
        {
            mStoreParams.put("email", email);
            params.put("email", email);
        }

        if (Util.isTextEmpty(id) == false)
        {
            mStoreParams.put("social_id", id);
            params.put("social_id", id);
        }

        if (encryptedId != null)
        {
            mStoreParams.put("pw", encryptedId);
            params.put("pw", encryptedId);
        }

        if (Util.isTextEmpty(name) == false)
        {
            mStoreParams.put("name", name);
        }

        if (Util.isTextEmpty(gender) == false)
        {
            mStoreParams.put("gender", gender);
        }

        if (deviceId != null)
        {
            mStoreParams.put("device", deviceId);
        }

        mStoreParams.put("is_auto", "true");
        params.put("is_auto", "true");

        mStoreParams.put("market_type", RELEASE_STORE.getName());
        mStoreParams.put("user_type", "facebook");
        params.put("user_type", "facebook");

        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, LoginActivity.this);
    }

    private void registerKakaokUser(long id)
    {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        String index = String.valueOf(id);
        String encryptedId = Crypto.encrypt(index).replace("\n", "");
        String deviceId = telephonyManager.getDeviceId();

        if (mStoreParams == null)
        {
            mStoreParams = new HashMap<String, String>();
        }

        mStoreParams.clear();
        HashMap<String, String> params = new HashMap<String, String>();

        if (Util.isTextEmpty(index) == false)
        {
            mStoreParams.put("social_id", index);
            params.put("social_id", index);
        }

        if (encryptedId != null)
        {
            mStoreParams.put("pw", encryptedId);
            params.put("pw", encryptedId);
        }

        if (deviceId != null)
        {
            mStoreParams.put("device", deviceId);
        }

        mStoreParams.put("is_auto", "true");
        params.put("is_auto", "true");

        mStoreParams.put("market_type", RELEASE_STORE.getName());
        mStoreParams.put("user_type", "kakao_talk");
        params.put("user_type", "kakao_talk");

        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, LoginActivity.this);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == mFindPasswordView.getId())
        {
            // 비밀번호 찾기
            Intent i = new Intent(this, ForgotPwdActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.FORGOT_PASSWORD, 0L);
        } else if (v.getId() == mSignupView.getId())
        {
            // 회원가입
            Intent i = new Intent(this, SignupActivity.class);
            startActivityForResult(i, CODE_REQEUST_ACTIVITY_SIGNUP);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.SIGNUP, 0L);
        } else if (v.getId() == mLoginView.getId())
        {
            // 일반 로그인
            if (isBlankFields() == false)
            {
                return;
            }

            lockUI();

            String md5 = Crypto.encrypt(mPasswordEditText.getText().toString().trim()).replace("\n", "");

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("email", mIdEditText.getText().toString().trim());
            params.put("pw", md5);
            params.put("social_id", "0");
            params.put("user_type", "normal");
            params.put("is_auto", mAutoLoginSwitch.isChecked() ? "true" : "false");

            if (mStoreParams == null)
            {
                mStoreParams = new HashMap<String, String>();
            }

            mStoreParams.clear();
            mStoreParams.put("email", mIdEditText.getText().toString().trim());
            mStoreParams.put("pw", md5);
            mStoreParams.put("social_id", "0");
            mStoreParams.put("user_type", "normal");

            DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mDailyUserLoginJsonResponseListener, this);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.LOGIN, 0L);
        }
    }

    public boolean isBlankFields()
    {
        if (mIdEditText.getText().toString().trim().length() == 0)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
            return false;
        }

        if (mPasswordEditText.getText().toString().trim().length() == 0)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_passwd, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    public void storeLoginInfo()
    {
        // 자동 로그인 체크시
        if (mAutoLoginSwitch.isChecked() == false)
        {
            return;
        }

        String id = mStoreParams.get("email");
        String pwd = mStoreParams.get("pw");
        String accessToken = mStoreParams.get("social_id");
        String type = mStoreParams.get("user_type");

        DailyPreference.getInstance(this).setAutoLogin(true);

        if (Util.isTextEmpty(accessToken) == false && "0".equals(accessToken) == false)
        {
            DailyPreference.getInstance(this).setUserAccessToken(accessToken);
            DailyPreference.getInstance(this).setUserId(null);
        } else
        {
            DailyPreference.getInstance(this).setUserId(id);
            DailyPreference.getInstance(this).setUserAccessToken(null);
        }

        DailyPreference.getInstance(this).setUserPassword(pwd);
        DailyPreference.getInstance(this).setUserType(type);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODE_REQEUST_ACTIVITY_SIGNUP)
        {
            if (resultCode == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        } else
        {
            lockUI();

            try
            {
                if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data) == true)
                {
                    return;
                }
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            unLockUI();

            if (mCallbackManager != null)
            {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private void registerNotificationId(final String registrationId, String userIndex)
    {
        ErrorListener errorListener = new ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError arg0)
            {
                unLockUI();

                DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                setResult(RESULT_OK);
                finish();
            }
        };

        DailyHotelJsonResponseListener dailyHotelJsonResponseListener = new DailyHotelJsonResponseListener()
        {
            @Override
            public void onResponse(String url, JSONObject response)
            {
                try
                {
                    int msg_code = response.getInt("msgCode");

                    if (msg_code == 100 && response.has("data") == true)
                    {
                        JSONObject jsonObject = response.getJSONObject("data");

                        int uid = jsonObject.getInt("uid");
                        DailyPreference.getInstance(LoginActivity.this).setNotificationUid(uid);
                        DailyPreference.getInstance(LoginActivity.this).setGCMRegistrationId(registrationId);
                    }
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                } finally
                {
                    DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        };

        int uid = DailyPreference.getInstance(LoginActivity.this).getNotificationUid();
        if (uid < 0)
        {
            Map<String, String> paramHashMap = new HashMap<>();
            paramHashMap.put("registrationId", registrationId);

            DailyNetworkAPI.getInstance().requestUserRegisterNotification(mNetworkTag, paramHashMap, dailyHotelJsonResponseListener, errorListener);
        } else
        {
            Map<String, String> paramHashMap = new HashMap<>();

            if (Util.isTextEmpty(userIndex) == false)
            {
                paramHashMap.put("userIdx", userIndex);
            }

            paramHashMap.put("changedRegistrationId", registrationId);
            paramHashMap.put("uid", Integer.toString(uid));

            DailyNetworkAPI.getInstance().requestUserUpdateNotification(mNetworkTag, paramHashMap, dailyHotelJsonResponseListener, errorListener);
        }
    }

    private void regGcmId(final String userIndex)
    {
        if (Util.isGooglePlayServicesAvailable(this) == false)
        {
            DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
            setResult(RESULT_OK);
            finish();
            return;
        }

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                GoogleCloudMessaging instance = GoogleCloudMessaging.getInstance(LoginActivity.this);
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
                // 이 값을 서버에 등록하기.
                // gcm id가 없을 경우 스킵.
                if (regId == null || regId.isEmpty())
                {
                    unLockUI();

                    DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                    return;
                }

                registerNotificationId(regId, userIndex);
            }
        }.execute();
    }

    private class SessionCallback implements ISessionCallback
    {
        @Override
        public void onSessionOpened()
        {
            lockUI();

            UserManagement.requestMe(new MeResponseCallback()
            {
                @Override
                public void onSuccess(UserProfile result)
                {
                    // id값은 특별함. kakao login
                    registerKakaokUser(result.getId());
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult)
                {
                    unLockUI();
                }

                @Override
                public void onNotSignedUp()
                {
                    unLockUI();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception)
        {
            unLockUI();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private FacebookCallback facebookCallback = new FacebookCallback<LoginResult>()
    {
        @Override
        public void onSuccess(LoginResult result)
        {
            lockUI();

            GraphRequest request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
            {
                @Override
                public void onCompleted(JSONObject jsonObject, GraphResponse response)
                {
                    try
                    {
                        String email = null;

                        if (jsonObject.has("email") == true)
                        {
                            email = jsonObject.getString("email");
                        }

                        String name = null;

                        if (jsonObject.has("name") == true)
                        {
                            name = jsonObject.getString("name");
                        }

                        String gender = null;

                        if (jsonObject.has("gender") == true)
                        {
                            gender = jsonObject.getString("gender");
                        }

                        String id = jsonObject.getString("id");

                        registerFacebookUser(id, name, email, gender);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name, email, gender");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel()
        {
        }

        @Override
        public void onError(FacebookException error)
        {
        }
    };

    private DailyHotelJsonResponseListener mSocialUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
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
                        // 회원가입에 성공하면 이제 로그인 절차

                        DailyPreference.getInstance(LoginActivity.this).setSocialSignUp(true);

                        HashMap<String, String> params = new HashMap<String, String>();

                        if (mStoreParams.containsKey("email") == true)
                        {
                            params.put("email", mStoreParams.get("email"));
                        }

                        if (mStoreParams.containsKey("pw") == true)
                        {
                            params.put("pw", mStoreParams.get("pw"));
                        }

                        if (mStoreParams.containsKey("social_id") == true)
                        {
                            params.put("social_id", mStoreParams.get("social_id"));
                        }

                        if (mStoreParams.containsKey("user_type") == true)
                        {
                            params.put("user_type", mStoreParams.get("user_type"));
                        }

                        if (mStoreParams.containsKey("is_auto") == true)
                        {
                            params.put("is_auto", mStoreParams.get("is_auto"));
                        }

                        mAutoLoginSwitch.setChecked(true);

                        mStoreParams.put("new_user", "1");

                        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, LoginActivity.this);
                        return;
                    }
                }

                unLockUI();
                mStoreParams.clear();

                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = getString(R.string.toast_msg_failed_to_signup);
                }

                DailyToast.showToast(LoginActivity.this, msg, Toast.LENGTH_LONG);

            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            }
        }
    };

    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            String userIndex = null;

            try
            {
                // GCM 등록을 위해 값이 필요한다.
                userIndex = String.valueOf(response.getInt("idx"));

                String name = response.getString("name");

                if (Util.isTextEmpty(name) == false)
                {
                    DailyPreference.getInstance(LoginActivity.this).setUserName(name);
                }

                if (DailyPreference.getInstance(LoginActivity.this).isSocialSignUp() == true)
                {
                    DailyPreference.getInstance(LoginActivity.this).setSocialSignUp(false);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(Label.CURRENT_TIME, dateFormat.format(new Date()));
                    params.put(Label.USER_INDEX, userIndex);
                    params.put(Label.TYPE, "Social");

                    AnalyticsManager.getInstance(LoginActivity.this).recordEvent(Screen.LOGIN, Action.NETWORK, Label.SIGNUP, params);

                    if (mStoreParams.containsKey("new_user") == true)
                    {
                        // user_type : kakao_talk. facebook
                        String userType = mStoreParams.get("user_type");

                        if ("kakao_talk".equalsIgnoreCase(userType) == true)
                        {
                            userType = AnalyticsManager.UserType.KAKAO;
                        } else if ("facebook".equalsIgnoreCase(userType) == true)
                        {
                            userType = AnalyticsManager.UserType.FACEBOOK;
                        }

                        AnalyticsManager.getInstance(LoginActivity.this).recordSocialRegistration(//
                            userIndex//
                            , mStoreParams.get("email"), mStoreParams.get("name")//
                            , mStoreParams.get("gender"), null, userType);
                    }
                }
            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            } finally
            {
                regGcmId(userIndex);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
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

                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, LoginActivity.this);
                        DailyPreference.getInstance(LoginActivity.this).setCollapsekey(null);
                        return;
                    }
                }

                unLockUI();

                // 로그인이 실패한 경우
                String msg = response.getString("msg");

                if (Util.isTextEmpty(msg) == true)
                {
                    msg = getString(R.string.toast_msg_failed_to_login);
                }

                DailyToast.showToast(LoginActivity.this, msg, Toast.LENGTH_LONG);
            } catch (Exception e)
            {
                unLockUI();
                ExLog.d(e.toString());
            }
        }
    };

    private DailyHotelJsonResponseListener mSocialUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msg_code = response.getInt("msg_code");
                JSONObject jsonObject = response.getJSONObject("data");
                boolean isSignin = jsonObject.getBoolean("is_signin");

                if (isSignin == true)
                {
                    VolleyHttpClient.createCookie();
                    storeLoginInfo();

                    DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, LoginActivity.this);
                    DailyPreference.getInstance(LoginActivity.this).setCollapsekey(null);
                } else
                {
                    // 페이스북, 카카오톡 로그인 정보가 없는 경우 회원 가입으로 전환한다
                    DailyNetworkAPI.getInstance().requestUserSignup(mNetworkTag, mStoreParams, mSocialUserSignupJsonResponseListener, LoginActivity.this);
                }

                //                {
                //                    // 로그인이 실패한 경우
                //                    String msg = response.getString("msg");
                //
                //                    if (Util.isTextEmpty(msg) == true)
                //                    {
                //                        msg = getString(R.string.toast_msg_failed_to_login);
                //                    }
                //
                //                    DailyToast.showToast(LoginActivity.this, msg, Toast.LENGTH_LONG);
                //
                //                    unLockUI();
                //                }
            } catch (Exception e)
            {
                unLockUI();
                ExLog.d(e.toString());
            }
        }
    };
}
