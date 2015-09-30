/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p/>
 * ProfileActivity (프로필 화면)
 * <p/>
 * 로그인되어 있는 상태에서 프로필 정보를 보여주는 화면
 * 이름이나 연락처를 수정할 수 있고, 로그아웃할 수 있는 화면이다.
 */
package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.request.DailyHotelJsonRequest;
import com.twoheart.dailyhotel.network.request.DailyHotelStringRequest;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.util.AnalyticsManager;
import com.twoheart.dailyhotel.util.AnalyticsManager.Action;
import com.twoheart.dailyhotel.util.AnalyticsManager.Label;
import com.twoheart.dailyhotel.util.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.util.DailyHotelPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.view.widget.DailyToast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity implements OnClickListener
{
    private final String INVALID_NULL = "null";

    private InputMethodManager mInputMethodManager;
    private String prevName;
    private String prevPh;
    private EditText mNameEditText, mPhoneEditText;
    private View mEditProfileLayout, mInformationProfileLayout;
    private TextView mEditButtonView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_profile);
        setActionBar(R.string.actionbar_title_profile_activity);

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initLayout();
    }

    private void initLayout()
    {
        View profileEditLayout = findViewById(R.id.ll_profile_edit);
        mEditButtonView = (TextView) findViewById(R.id.tv_profile_edit);

        // 수정시에 인터페이스 편의를 위해 [사용자 정보] 바를 터치하면 완료되도록 수정.
        findViewById(R.id.profileSectionBarLayout).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (mEditButtonView.getText().equals(getString(R.string.dialog_btn_text_confirm)))
                {
                    mEditButtonView.performClick();
                    return true;
                }

                return false;
            }
        });

        profileEditLayout.setOnClickListener(this);

        View logoutView = findViewById(R.id.btn_profile_logout);
        logoutView.setOnClickListener(this);

        mNameEditText = (EditText) findViewById(R.id.et_profile_name);
        mPhoneEditText = (EditText) findViewById(R.id.et_profile_phone);
        mPhoneEditText.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        mEditButtonView.performClick();
                        break;
                }
                return true;
            }
        });


        mEditProfileLayout = findViewById(R.id.ll_profile_info_editable);
        mInformationProfileLayout = findViewById(R.id.ll_profile_info_label);


    }


    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.PROFILE);
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateTextField();
    }

    @Override
    protected void onPause()
    {
        toggleKeyboard(false);

        super.onPause();
    }

    /**
     * 수정중인 상태에서 백버튼을 누른경우에 수정 취소 => 바꾸기 전 상태로 돌아감
     */
    @Override
    public void onBackPressed()
    {
        if (mEditButtonView.getText().equals(getString(R.string.dialog_btn_text_confirm)))
        {

            mEditProfileLayout.setVisibility(View.GONE);
            mInformationProfileLayout.setVisibility(View.VISIBLE);
            mEditButtonView.setText(R.string.act_profile_modify);

            mNameEditText.setText(prevName);
            mPhoneEditText.setText(prevPh);

            toggleKeyboard(false);
        } else
        {
            super.onBackPressed();
        }

    }

    public void setupUI(View view)
    {
        if (view.getId() == R.id.ll_profile_edit)
        {
            return;
        }

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText))
        {
            view.setOnTouchListener(new OnTouchListener()
            {
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (mEditButtonView.getText().equals(getString(R.string.dialog_btn_text_confirm)))
                    {
                        mEditButtonView.performClick();
                        return true;
                    }
                    return false;
                }

            });
        }

        // If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup)
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void toggleKeyboard(boolean show)
    {
        if (getWindow() == null || getWindow().getDecorView() == null || getWindow().getDecorView().getWindowToken() == null)
        {
            return;
        }

        if (show)
        {
            mNameEditText.requestFocus();

            StringFilter stringFilter = new StringFilter(ProfileActivity.this);
            InputFilter[] allowAlphanumericHangul = new InputFilter[1];
            allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;

            mNameEditText.setFilters(allowAlphanumericHangul);

            mInputMethodManager.showSoftInput(mNameEditText, InputMethodManager.SHOW_FORCED);

        } else
        {
            mInputMethodManager.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);

        }
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.ll_profile_edit)
        {
            if (mEditButtonView.getText().equals(getString(R.string.act_profile_modify)))
            {
                mInformationProfileLayout.setVisibility(View.GONE);
                mEditProfileLayout.setVisibility(View.VISIBLE);
                mEditButtonView.setText(R.string.dialog_btn_text_confirm);

                toggleKeyboard(true);

            } else if (mEditButtonView.getText().equals(getString(R.string.dialog_btn_text_confirm)))
            {
                if (isLockUiComponent() == true)
                {
                    return;
                }

                lockUiComponent();

                String name = mNameEditText.getText().toString().trim();
                String phone = mPhoneEditText.getText().toString().trim();

                if (Util.isTextEmpty(phone) == true)
                {
                    // 전화번호는 필수 사항으로 한다.
                    releaseUiComponent();

                    mPhoneEditText.setText("");
                    DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_please_input_phone, Toast.LENGTH_SHORT);
                } else if (Util.isTextEmpty(name) == true)
                {
                    // 이름은 필수 사항으로 입력되어야 한다.
                    releaseUiComponent();

                    mNameEditText.setText("");
                    DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_please_input_name, Toast.LENGTH_SHORT);
                } else if (name.equals(prevName) && phone.equals(prevPh))
                {
                    toggleKeyboard(false);

                    releaseUiComponent();

                    // 기존과 동일하여 서버에 요청할 필요가 없음.
                    mEditProfileLayout.setVisibility(View.GONE);
                    mInformationProfileLayout.setVisibility(View.VISIBLE);
                    mEditButtonView.setText(R.string.act_profile_modify);

                    DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_profile_not_changed, Toast.LENGTH_LONG);
                } else
                {
                    toggleKeyboard(false);

                    Map<String, String> updateParams = new HashMap<String, String>();
                    updateParams.put("name", name);
                    updateParams.put("phone", phone);

                    lockUI();
                    mQueue.add(new DailyHotelJsonRequest(Method.POST, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_UPDATE).toString(), updateParams, mUserUpdateJsonResponseListener, this));
                }
            }
        } else if (v.getId() == R.id.btn_profile_logout)
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            lockUiComponent();

            /**
             * 로그 아웃시 내부 저장한 유저정보 초기화
             */
            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mQueue.add(new DailyHotelStringRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_LOGOUT).toString(), null, mUserLogoutStringResponseListener, ProfileActivity.this));
                    AnalyticsManager.getInstance(ProfileActivity.this).recordEvent(Screen.PROFILE, Action.CLICK, Label.LOGOUT, 0L);
                }
            };

            showSimpleDialog(null, getString(R.string.dialog_msg_chk_wanna_login), getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel), posListener, null, false);

            releaseUiComponent();
        }
    }

    private void updateTextField()
    {
        lockUI();

        // 사용자 정보 요청.
        mQueue.add(new DailyHotelJsonRequest(Method.GET, new StringBuilder(URL_DAILYHOTEL_SERVER).append(URL_WEBAPI_USER_INFO).toString(), null, mUserLogInfoJsonResponseListener, this));
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserLogInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                if (response == null)
                {
                    throw new NullPointerException("response == null");
                }

                String userEmail = response.getString("email");
                String userName = response.getString("name");
                String userPhone = response.getString("phone");

                if (Util.isTextEmpty(userEmail) == true || INVALID_NULL.equalsIgnoreCase(userEmail) == true)
                {
                    userEmail = getString(R.string.act_profile_input_email);
                }

                if (Util.isTextEmpty(userName) == true)
                {
                    userName = getString(R.string.act_profile_input_name);
                    prevName = "";
                } else
                {
                    prevName = userName;
                }

                if (Util.isTextEmpty(userPhone) == true || INVALID_NULL.equalsIgnoreCase(userPhone) == true)
                {
                    userPhone = getString(R.string.act_profile_input_contact);
                    prevPh = "";
                } else
                {
                    prevPh = userPhone;
                }

                TextView emailTextView = (TextView) findViewById(R.id.tv_profile_email);
                emailTextView.setText(userEmail);

                TextView nameTextView = (TextView) findViewById(R.id.tv_profile_name);
                nameTextView.setText(userName);

                TextView phoneTextView = (TextView) findViewById(R.id.tv_profile_phone);
                phoneTextView.setText(userPhone);

                mNameEditText.setText(prevName);
                mPhoneEditText.setText(prevPh);

                mEditProfileLayout.setVisibility(View.GONE);
                mInformationProfileLayout.setVisibility(View.VISIBLE);
                mEditButtonView.setText(R.string.act_profile_modify);
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }
        }
    };
    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {

        @Override
        public void onResponse(String url, JSONObject response)
        {
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
                    unLockUI();
                    DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
                    updateTextField();
                } else
                {
                    unLockUI();
                    DailyToast.showToast(ProfileActivity.this, msg, Toast.LENGTH_LONG);
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }
    };
    private DailyHotelStringResponseListener mUserLogoutStringResponseListener = new DailyHotelStringResponseListener()
    {
        @Override
        public void onResponse(String url, String response)
        {
            VolleyHttpClient.destroyCookie();

            SharedPreferences.Editor ed = sharedPreference.edit();
            //			ed.putBoolean(KEY_PREFERENCE_AUTO_LOGIN, false);
            //			ed.putString(KEY_PREFERENCE_USER_ID, null);
            //			ed.putString(KEY_PREFERENCE_USER_PWD, null);
            //			ed.putString(KEY_PREFERENCE_GCM_ID, null);

            ed.clear();
            ed.commit();

            DailyHotelPreference.getInstance(ProfileActivity.this).clear();

            try
            {
                LoginManager.getInstance().logOut();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            try
            {
                UserManagement.requestLogout(null);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }

            DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_logouted, Toast.LENGTH_SHORT);
            finish();
        }
    };
}
