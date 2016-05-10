package com.twoheart.dailyhotel.screen.information.member;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Bundle;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
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
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.ForgotPasswordActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Crypto;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;
import com.twoheart.dailyhotel.widget.FontManager;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements Constants, OnClickListener, ErrorListener, View.OnFocusChangeListener
{
    public CallbackManager mCallbackManager;
    private EditText mEmailEditText, mPasswordEditText;
    private TextView mLoginView;
    private View mEmailView, mPasswordView;
    private com.facebook.login.widget.LoginButton mFacebookLoginView;

    private Map<String, String> mStoreParams;

    // 카카오톡
    private com.kakao.usermgmt.LoginButton mKakaoLoginView;
    private SessionCallback mKakaoSessionCallback;
    private boolean mIsSocialSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DailyHotel.setCurrentActivity(this);

        setContentView(R.layout.activity_login);

        initToolbar();
        initTopLayout();
        initEditTextsLayout();
        initButtonsLayout();

        if (Util.isOverAPI23() == true && hasPermission() == false)
        {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.REQUEST_CODE_PERMISSIONS_READ_PHONE_STATE);
        }
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_login_activity), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initTopLayout()
    {
        TextView signupView = (TextView) findViewById(R.id.signupView);
        TextView findPasswordView = (TextView) findViewById(R.id.findPasswordView);

        signupView.setPaintFlags(signupView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        findPasswordView.setPaintFlags(findPasswordView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        signupView.setOnClickListener(this);
        findPasswordView.setOnClickListener(this);
    }

    private void initEditTextsLayout()
    {
        mEmailView = findViewById(R.id.emailView);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mEmailEditText.setOnFocusChangeListener(this);

        mPasswordView = findViewById(R.id.passwordView);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mPasswordEditText.setOnFocusChangeListener(this);

        mEmailEditText.requestFocus();

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
    }

    private void initButtonsLayout()
    {
        mLoginView = (TextView) findViewById(R.id.signinView);

        mFacebookLoginView = (com.facebook.login.widget.LoginButton) findViewById(R.id.facebookLoginButton);
        mFacebookLoginView.setReadPermissions(Collections.singletonList("public_profile"));

        View facebookLoginView = findViewById(R.id.facebookLoginView);
        facebookLoginView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mFacebookLoginView.performClick();

                AnalyticsManager.getInstance(LoginActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.LOGIN_CLICKED, Label.FACEBOOK_LOGIN, null);
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

                AnalyticsManager.getInstance(LoginActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.LOGIN_CLICKED, Label.KAKAO_LOGIN, null);
            }
        });

        mKakaoSessionCallback = new SessionCallback();
        Session.getCurrentSession().addCallback(mKakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        mLoginView.setOnClickListener(this);
        mFacebookLoginView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(LoginActivity.this).recordScreen(Screen.SIGNIN, null);

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
            String deviceId = Util.getDeviceId(this);

            if (deviceId == null)
            {
                return false;
            }
        }

        return true;
    }

    private void registerFacebookUser(String id, String name, String email, String gender)
    {
        String encryptedId = Crypto.encrypt(id).replace("\n", "");
        String deviceId = Util.getDeviceId(this);

        if (mStoreParams == null)
        {
            mStoreParams = new HashMap<>();
        }

        mStoreParams.clear();
        HashMap<String, String> params = new HashMap<>();

        if (Util.isTextEmpty(email) == false)
        {
            params.put("email", email);
        }

        if (Util.isTextEmpty(id) == false)
        {
            params.put("social_id", id);
        }

        params.put("pw", encryptedId);
        params.put("user_type", Constants.FACEBOOK_USER);

        mStoreParams.putAll(params);

        if (Util.isTextEmpty(name) == false)
        {
            mStoreParams.put("name", name);
        }

        if (Util.isTextEmpty(gender) == false)
        {
            mStoreParams.put("gender", gender);
        }

        if (Util.isTextEmpty(deviceId) == false)
        {
            mStoreParams.put("device", deviceId);
        }

        mStoreParams.put("market_type", RELEASE_STORE.getName());

        DailyNetworkAPI.getInstance(this).requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, this);
    }

    private void registerKakaokUser(long id)
    {
        String index = String.valueOf(id);
        String encryptedId = Crypto.encrypt(index).replace("\n", "");
        String deviceId = Util.getDeviceId(this);

        if (mStoreParams == null)
        {
            mStoreParams = new HashMap<>();
        }

        mStoreParams.clear();
        HashMap<String, String> params = new HashMap<>();

        if (Util.isTextEmpty(index) == false)
        {
            params.put("social_id", index);
        }

        params.put("pw", encryptedId);
        params.put("pw2", encryptedId);
        params.put("user_type", Constants.KAKAO_USER);

        mStoreParams.putAll(params);

        if (Util.isTextEmpty(deviceId) == false)
        {
            mStoreParams.put("device", deviceId);
        }

        mStoreParams.put("market_type", RELEASE_STORE.getName());

        DailyNetworkAPI.getInstance(this).requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signupView:
            {
                Intent intent = SignupStep1Activity.newInstance(this);
                startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);

                AnalyticsManager.getInstance(LoginActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.REGISTRATION_CLICKED, Label.REGISTER_ACCOUNT, null);
                break;
            }

            case R.id.findPasswordView:
            {
                Intent intent = new Intent(this, ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
                break;
            }

            case R.id.signinView:
            {
                processSignin();
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus == false)
        {
            return;
        }

        setFocusTextView(v.getId());
    }

    private void resetFocus()
    {
        mEmailView.setSelected(false);
        mPasswordView.setSelected(false);
    }

    private void setFocusTextView(int id)
    {
        resetFocus();

        switch (id)
        {
            case R.id.emailEditText:
                mEmailView.setSelected(true);
                break;

            case R.id.passwordEditText:
                mPasswordView.setSelected(true);
                break;
        }
    }

    private void processSignin()
    {
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (Util.isTextEmpty(email) == true)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
            return;
        }

        if (Util.isTextEmpty(password) == true)
        {
            DailyToast.showToast(this, R.string.toast_msg_please_input_passwd, Toast.LENGTH_SHORT);
            return;
        }

        lockUI();

        String passwordEncrypt = Crypto.encrypt(password.replace("\n", ""));

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("pw", passwordEncrypt);
        params.put("social_id", "0");
        params.put("user_type", Constants.DAILY_USER);
        params.put("is_auto", "true");

        if (mStoreParams == null)
        {
            mStoreParams = new HashMap<>();
        }

        mStoreParams.clear();
        mStoreParams.putAll(params);

        DailyNetworkAPI.getInstance(this).requestUserSignin(mNetworkTag, params, mDailyUserLoginJsonResponseListener, this);

        AnalyticsManager.getInstance(getApplicationContext()).recordEvent(AnalyticsManager.Category.NAVIGATION, Action.LOGIN_CLICKED, Label.EMAIL_LOGIN, null);
    }


    public void storeLoginInfo()
    {
        String email = mStoreParams.get("email");
        String accessToken = mStoreParams.get("social_id");
        String type = mStoreParams.get("user_type");

        if (Util.isTextEmpty(accessToken) == false && "0".equals(accessToken) == false)
        {
            DailyPreference.getInstance(this).setUserEmail(null);
            DailyPreference.getInstance(this).setUserAccessToken(accessToken);
        } else
        {
            DailyPreference.getInstance(this).setUserEmail(email);
            DailyPreference.getInstance(this).setUserAccessToken(null);
        }

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
            public void onErrorResponse(VolleyError volleyError)
            {

            }

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
            DailyNetworkAPI.getInstance(this).requestUserRegisterNotification(mNetworkTag, registrationId, dailyHotelJsonResponseListener, errorListener);
        } else
        {
            DailyNetworkAPI.getInstance(this).requestUserUpdateNotification(mNetworkTag, userIndex, registrationId, Integer.toString(uid), dailyHotelJsonResponseListener, errorListener);
        }
    }

    private void requestGoogleCloudMessagingId(final String userIndex)
    {
        Util.requestGoogleCloudMessaging(this, new Util.OnGoogleCloudMessagingListener()
        {
            @Override
            public void onResult(String registrationId)
            {
                if (Util.isTextEmpty(registrationId) == false)
                {
                    registerNotificationId(registrationId, userIndex);
                } else
                {
                    unLockUI();

                    DailyToast.showToast(LoginActivity.this, R.string.toast_msg_logoined, Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
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
                int msgCode = response.getInt("msg_code");

                if (msgCode == 0)
                {
                    JSONObject jsonObject = response.getJSONObject("data");

                    boolean isSignup = jsonObject.getBoolean("is_signup");

                    if (isSignup == true)
                    {
                        // 회원가입에 성공하면 이제 로그인 절차
                        mIsSocialSignUp = true;

                        HashMap<String, String> params = new HashMap<>();

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

                        mStoreParams.put("new_user", "1");

                        DailyNetworkAPI.getInstance(LoginActivity.this).requestUserSignin(mNetworkTag, params, mSocialUserLoginJsonResponseListener, LoginActivity.this);
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            LoginActivity.this.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mUserInformationJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            String userIndex = null;

            try
            {
                // GCM 등록을 위해 값이 필요한다.
                userIndex = String.valueOf(response.getInt("idx"));

                AnalyticsManager.getInstance(LoginActivity.this).setUserIndex(userIndex);

                String name = response.getString("name");

                if (Util.isTextEmpty(name) == false)
                {
                    DailyPreference.getInstance(LoginActivity.this).setUserName(name);
                }

                if (mIsSocialSignUp == true)
                {
                    mIsSocialSignUp = false;

                    if (mStoreParams.containsKey("new_user") == true)
                    {
                        // user_type : kakao_talk. facebook
                        String userType = mStoreParams.get("user_type");

                        if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
                        {
                            userType = AnalyticsManager.UserType.KAKAO;
                        } else if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
                        {
                            userType = AnalyticsManager.UserType.FACEBOOK;
                        }

                        AnalyticsManager.getInstance(LoginActivity.this).singUpSocialUser(//
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
                requestGoogleCloudMessagingId(userIndex);
            }
        }
    };

    private DailyHotelJsonResponseListener mDailyUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

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
                        JSONObject tokenJSONObject = response.getJSONObject("token");
                        String accessToken = tokenJSONObject.getString("access_token");
                        String tokenType = tokenJSONObject.getString("token_type");

                        DailyPreference.getInstance(LoginActivity.this).setAuthorization(String.format("%s %s", tokenType, accessToken));
                        storeLoginInfo();

                        DailyNetworkAPI.getInstance(LoginActivity.this).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, LoginActivity.this);
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
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msg_code");
                JSONObject jsonObject = response.getJSONObject("data");
                boolean isSignin = jsonObject.getBoolean("is_signin");

                if (isSignin == true)
                {
                    JSONObject tokenJSONObject = response.getJSONObject("token");
                    String accessToken = tokenJSONObject.getString("access_token");
                    String tokenType = tokenJSONObject.getString("token_type");

                    DailyPreference.getInstance(LoginActivity.this).setAuthorization(String.format("%s %s", tokenType, accessToken));
                    storeLoginInfo();

                    DailyNetworkAPI.getInstance(LoginActivity.this).requestUserInformation(mNetworkTag, mUserInformationJsonResponseListener, LoginActivity.this);
                    DailyPreference.getInstance(LoginActivity.this).setCollapsekey(null);
                } else
                {
                    // 페이스북, 카카오톡 로그인 정보가 없는 경우 회원 가입으로 전환한다

                    String userType = mStoreParams.get("user_type");

                    if (Constants.FACEBOOK_USER.equalsIgnoreCase(userType) == true)
                    {
                        DailyNetworkAPI.getInstance(LoginActivity.this).requestFacebookUserSignup(mNetworkTag, mStoreParams, mSocialUserSignupJsonResponseListener);
                    } else if (Constants.KAKAO_USER.equalsIgnoreCase(userType) == true)
                    {
                        DailyNetworkAPI.getInstance(LoginActivity.this).requestKakaoUserSignup(mNetworkTag, mStoreParams, mSocialUserSignupJsonResponseListener);
                    }
                }
            } catch (Exception e)
            {
                unLockUI();
                ExLog.d(e.toString());
            }
        }
    };
}
