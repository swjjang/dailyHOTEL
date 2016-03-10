/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * ProfileActivity (프로필 화면)
 * <p>
 * 로그인되어 있는 상태에서 프로필 정보를 보여주는 화면
 * 이름이나 연락처를 수정할 수 있고, 로그아웃할 수 있는 화면이다.
 */
package com.twoheart.dailyhotel.screen.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.VolleyHttpClient;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.network.response.DailyHotelStringResponseListener;
import com.twoheart.dailyhotel.screen.common.BaseActivity;
import com.twoheart.dailyhotel.screen.information.member.InputMobileNumberDialogActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONObject;

public class ProfileActivity extends BaseActivity implements OnClickListener
{
    private static final int REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY = 1;

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

        setContentView(R.layout.activity_profile);

        initToolbar();

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initLayout();
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_profile_activity));
    }

    private void initLayout()
    {
        final View profileEditLayout = findViewById(R.id.ll_profile_edit);
        mEditButtonView = (TextView) findViewById(R.id.tv_profile_edit);

        // 수정시에 인터페이스 편의를 위해 [사용자 정보] 바를 터치하면 완료되도록 수정.
        findViewById(R.id.profileSectionBarLayout).setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (mEditButtonView.getText().equals(getString(R.string.dialog_btn_text_confirm)))
                {
                    profileEditLayout.performClick();
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

        mPhoneEditText.setCursorVisible(false);
        mPhoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus == true)
                {
                    showInputMobileNumberDialog(mPhoneEditText.getText().toString());
                } else
                {
                    mPhoneEditText.setSelected(false);
                }
            }
        });

        View fakeMobileEditView = findViewById(R.id.fakeMobileEditView);

        fakeMobileEditView.setFocusable(true);
        fakeMobileEditView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mPhoneEditText.isSelected() == true)
                {
                    showInputMobileNumberDialog(mPhoneEditText.getText().toString());
                } else
                {
                    mPhoneEditText.requestFocus();
                    mPhoneEditText.setSelected(true);
                }
            }
        });

        mEditProfileLayout = findViewById(R.id.ll_profile_info_editable);
        mInformationProfileLayout = findViewById(R.id.ll_profile_info_label);
    }


    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.PROFILE, null);

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // 수정 중에는 업데이트 하지 않음
        if (mEditButtonView.getText().equals(getString(R.string.act_profile_modify)))
        {
            updateTextField();
        }
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

                mPhoneEditText.setText(mobileNumber);
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
                phone = phone.replaceAll("-", "");

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

                    lockUI();
                    DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, name, phone, mUserUpdateJsonResponseListener, this);
                }
            }
        } else if (v.getId() == R.id.btn_profile_logout)
        {
            if (isLockUiComponent() == true || isFinishing() == true)
            {
                return;
            }

            /**
             * 로그 아웃시 내부 저장한 유저정보 초기화
             */
            View.OnClickListener posListener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    lockUI();
                    DailyNetworkAPI.getInstance().requestUserLogout(mNetworkTag, mUserLogoutStringResponseListener, ProfileActivity.this);

                    AnalyticsManager.getInstance(ProfileActivity.this).setUserIndex(null);
                    //                    AnalyticsManager.getInstance(ProfileActivity.this).recordEvent(Screen.PROFILE, Action.CLICK, Label.LOGOUT, 0L);
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
        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserLogInfoJsonResponseListener, this);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private void showInputMobileNumberDialog(String mobileNumber)
    {
        if (isFinishing() == true)
        {
            return;
        }

        Intent intent = InputMobileNumberDialogActivity.newInstance(ProfileActivity.this, mobileNumber);
        startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_DIALOG_ACTIVITY);
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
                String userEmail = response.getString("email");
                String userName = response.getString("name");
                String userPhone = response.getString("phone");

                if (Util.isTextEmpty(userEmail) == true)
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

                if (Util.isTextEmpty(userPhone) == true)
                {
                    prevPh = "";
                } else
                {
                    prevPh = userPhone;
                }

                if (Util.isValidatePhoneNumber(prevPh) == false)
                {
                    prevPh = "";
                } else
                {
                    prevPh = Util.addHippenMobileNumber(ProfileActivity.this, prevPh);
                }

                TextView emailTextView = (TextView) findViewById(R.id.tv_profile_email);
                emailTextView.setText(userEmail);

                TextView nameTextView = (TextView) findViewById(R.id.tv_profile_name);
                nameTextView.setText(userName);

                TextView phoneTextView = (TextView) findViewById(R.id.tv_profile_phone);
                phoneTextView.setText(prevPh);
                mPhoneEditText.setText(prevPh);

                mNameEditText.setText(prevName);

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

            DailyPreference.getInstance(ProfileActivity.this).clear();

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

            unLockUI();
            DailyToast.showToast(ProfileActivity.this, R.string.toast_msg_logouted, Toast.LENGTH_SHORT);
            finish();
        }
    };
}
