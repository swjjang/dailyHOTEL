/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * <p/>
 * LoginActivity (로그인화면)
 * <p/>
 * 사용자 계정 로그인을 담당하는 화면이다. 사용자로부터 아이디와 패스워드를
 * 입력받으며, 이를 로그인을 하는 웹서버 API를 이용한다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.kakao.auth.ErrorResult;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.FontManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LoginActivity extends BaseActivity implements Constants, OnClickListener, ErrorListener
{
    public CallbackManager mCallbackManager;
    private EditText etId, etPwd;
    private SwitchCompat cbxAutoLogin;
    private TextView btnLogin;
    private TextView tvSignUp, tvForgotPwd;
    private com.facebook.login.widget.LoginButton facebookLogin;

    private Map<String, String> loginParams;
    private Map<String, String> snsSignupParams;
    private Map<String, String> regPushParams;

    // 카카오톡
    private com.kakao.usermgmt.LoginButton mKakaoLoginButton;
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DailyHotel.setCurrentActivity(this);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        setActionBar(R.string.actionbar_title_login_activity);

        etId = (EditText) findViewById(R.id.et_login_id);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        cbxAutoLogin = (SwitchCompat) findViewById(R.id.cb_login_auto);
        tvSignUp = (TextView) findViewById(R.id.tv_login_signup);
        tvForgotPwd = (TextView) findViewById(R.id.tv_login_forgot);
        btnLogin = (TextView) findViewById(R.id.btn_login);

        facebookLogin = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebookLoginButton);
        facebookLogin.setReadPermissions(Collections.singletonList("public_profile, email"));

        View facebookLoginView = findViewById(R.id.facebookLoginView);
        facebookLoginView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                facebookLogin.performClick();
            }
        });

        mCallbackManager = CallbackManager.Factory.create();
        facebookLogin.registerCallback(mCallbackManager, facebookCallback);

        FontManager.apply(facebookLogin, FontManager.getInstance(getApplicationContext()).getRegularTypeface());

        mKakaoLoginButton = (com.kakao.usermgmt.LoginButton) findViewById(R.id.kakaoLoginButton);
        View kakaoLoginView = findViewById(R.id.kakaoLoginView);
        kakaoLoginView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mKakaoLoginButton.performClick();
            }
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();

        cbxAutoLogin.setChecked(true);
        cbxAutoLogin.setSwitchPadding(Util.dpToPx(LoginActivity.this, 15));

        tvSignUp.setOnClickListener(this);
        tvForgotPwd.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        facebookLogin.setOnClickListener(this);

        etPwd.setId(EditorInfo.IME_ACTION_DONE);
        etPwd.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        btnLogin.performClick();
                        break;
                }
                return false;
            }
        });
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

        Session.getCurrentSession().removeCallback(callback);
    }

    private void registerFacebookUser(String id, String name, String email)
    {
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        String encryptedId = Crypto.encrypt(id).replace("\n", "");
        String deviceId = telephonyManager.getDeviceId();

        snsSignupParams = new HashMap<String, String>();
        loginParams = new HashMap<String, String>();

        if (Util.isTextEmpty(email) == false)
        {
            snsSignupParams.put("email", email);
        }

        if (Util.isTextEmpty(id) == false)
        {
            snsSignupParams.put("accessToken", id);
            loginParams.put("accessToken", id);
        }

        if (encryptedId != null)
        {
            snsSignupParams.put("pw", id); // 회원가입
            loginParams.put("pw", encryptedId);
        }

        if (Util.isTextEmpty(name) == false)
        {
            snsSignupParams.put("name", name);
        }

        if (deviceId != null)
        {
            snsSignupParams.put("device", deviceId);
        }

        snsSignupParams.put("marketType", RELEASE_STORE.getName());

        mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, LoginActivity.this));
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == tvForgotPwd.getId())
        {
            // 비밀번호 찾기
            Intent i = new Intent(this, ForgotPwdActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.FORGOT_PASSWORD, 0L);
        } else if (v.getId() == tvSignUp.getId())
        {
            // 회원가입
            Intent i = new Intent(this, SignupActivity.class);
            startActivityForResult(i, CODE_REQEUST_ACTIVITY_SIGNUP);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.SIGNUP, 0L);
        } else if (v.getId() == btnLogin.getId())
        {
            // 로그인
            if (isBlankFields() == false)
            {
                return;
            }

            String md5 = Crypto.encrypt(etPwd.getText().toString()).replace("\n", "");

            loginParams = new LinkedHashMap<String, String>();
            loginParams.put("email", etId.getText().toString());
            loginParams.put("pw", md5);
            ExLog.d("email : " + loginParams.get("email") + " pw : " + loginParams.get("pw"));
            lockUI();

            mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, this));

            AnalyticsManager.getInstance(getApplicationContext()).recordEvent(Screen.LOGIN, Action.CLICK, Label.LOGIN, 0L);
        }
    }

    public boolean isBlankFields()
    {
        if (etId.getText().toString().trim().length() == 0)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
            return false;
        }

        if (etPwd.getText().toString().trim().length() == 0)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_passwd, Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    public void storeLoginInfo()
    {

        // 자동 로그인 체크시
        if (cbxAutoLogin.isChecked())
        {
            String id = loginParams.get("email");
            String pwd = loginParams.get("pw");
            String accessToken = loginParams.get("accessToken");

            SharedPreferences.Editor ed = sharedPreference.edit();
            ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, true);

            if (accessToken != null)
            {
                ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, accessToken);
                ed.putString(KEY_PREFERENCE_USER_ID, null);
            } else
            {
                ed.putString(KEY_PREFERENCE_USER_ID, id);
                ed.putString(KEY_PREFERENCE_USER_ACCESS_TOKEN, null);
            }

            ed.putString(KEY_PREFERENCE_USER_PWD, pwd);
            ed.commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data) == true)
            {
                return;
            }

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

    private String getGcmId()
    {
        return sharedPreference.getString(KEY_PREFERENCE_GCM_ID, "");
    }

    private boolean isGoogleServiceAvailable()
    {
        int resCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resCode))
            {
                if (isFinishing() == false)
                {
                    try
                    {
                        GooglePlayServicesUtil.getErrorDialog(resCode, LoginActivity.this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            } else
            {
                DailyToast.showToast(this, R.string.toast_msg_is_not_available_google_service, Toast.LENGTH_LONG);
            }
            return false;
        } else
        {
            return true;
        }
    }

    private void regGcmId(final String userIndex)
    {
        if (isGoogleServiceAvailable() == false)
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

                regPushParams = new HashMap<String, String>();
                regPushParams.put("user_idx", userIndex);
                regPushParams.put("notification_id", regId);
                regPushParams.put("device_type", GCM_DEVICE_TYPE_ANDROID);

                mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_GCM_REGISTER).toString(), regPushParams, mGcmRegisterJsonResponseListener, new ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError arg0)
                    {
                        unLockUI();

                        DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                        setResult(RESULT_OK);
                        finish();
                    }
                }));
            }
        }.execute();
    }

    private class SessionCallback implements ISessionCallback
    {
        @Override
        public void onSessionOpened()
        {
            UserManagement.requestMe(new MeResponseCallback()
            {
                @Override
                public void onSessionClosed(ErrorResult errorResult)
                {

                }

                @Override
                public void onNotSignedUp()
                {

                }

                @Override
                public void onFailure(ErrorResult errorResult)
                {

                }

                @Override
                public void onSuccess(UserProfile userProfile)
                {
                    // id값은 특별함. kakao login
                    userProfile.getId();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception)
        {
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

                        String id = jsonObject.getString("id");

                        registerFacebookUser(id, name, email);
                    } catch (Exception e)
                    {
                        ExLog.d(e.toString());
                    }
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name, email");
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

                String result = response.getString("join");
                String msg = response.getString("msg");

                ExLog.d("user/join? " + response.toString());

                if ("true".equalsIgnoreCase(result) == true)
                {
                    // 회원가입에 성공하면 이제 로그인 절차
                    Editor ed = sharedPreference.edit();
                    ed.putBoolean("Facebook SignUp", true);
                    ed.commit();

                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGIN).toString(), loginParams, mUserLoginJsonResponseListener, LoginActivity.this));
                } else
                {
                    unLockUI();

                    loginParams.clear();
                    DailyToast.showToast(LoginActivity.this, msg, Toast.LENGTH_LONG);
                }

            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            }

        }
    };

    private DailyHotelJsonResponseListener mGcmRegisterJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            // 로그인 성공 - 유저 정보(인덱스) 가져오기 - 유저의 GCM키 등록 완료 한 경우 프리퍼런스에 키 등록후 종료
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                ExLog.e("MSG : " + response.toString());

                if (response.getString("result").equals("true") == true)
                {
                    Editor editor = sharedPreference.edit();
                    editor.putString(KEY_PREFERENCE_GCM_ID, regPushParams.get("notification_id"));
                    editor.apply();
                }

                DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                setResult(RESULT_OK);
                finish();
            } catch (JSONException e)
            {
                ExLog.e(e.toString());
            } finally
            {
                unLockUI();
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
                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                // GCM 등록을 위해 값이 필요한다.
                userIndex = String.valueOf(response.getInt("idx"));

                // GCM 아이디를 등록한다.
                if (sharedPreference.getBoolean("Facebook SignUp", false) == true)
                {
                    Editor editor = sharedPreference.edit();
                    editor.putBoolean("Facebook SignUp", false);
                    editor.commit();

                    ExLog.d("facebook signup is completed.");

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(Label.CURRENT_TIME, dateFormat.format(new Date()));
                    params.put(Label.USER_INDEX, userIndex);
                    params.put(Label.TYPE, "facebook");

                    AnalyticsManager.getInstance(LoginActivity.this).recordEvent(Screen.LOGIN, Action.NETWORK, Label.SIGNUP, params);
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

    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String msg = null;

                if (response == null)
                {
                    throw new NullPointerException("response == null.");
                }

                if (response.getBoolean("login") == true)
                {
                    VolleyHttpClient.createCookie();
                    storeLoginInfo();

                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserInfoJsonResponseListener, LoginActivity.this));

                    Editor editor = sharedPreference.edit();
                    editor.putString("collapseKey", "");
                    editor.apply();
                } else
                {
                    if (loginParams.containsKey("accessToken"))
                    {
                        // SNS 로그인인데
                        // 실패했을 경우 회원가입 시도
                        cbxAutoLogin.setChecked(true); // 회원가입의 경우 기본으로 자동 로그인인
                        // 정책 상.
                        mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_SIGNUP).toString(), snsSignupParams, mUserSignupJsonResponseListener, LoginActivity.this));
                    } else if (response.length() > 1)
                    {
                        // 로그인 실패
                        // 실패 msg 출력

                        unLockUI();

                        if (isFinishing() == true)
                        {
                            return;
                        }

                        msg = response.getString("msg");
                        showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null);
                    }
                }
            } catch (Exception e)
            {
                unLockUI();
                onError(e);
            }
        }
    };
}
