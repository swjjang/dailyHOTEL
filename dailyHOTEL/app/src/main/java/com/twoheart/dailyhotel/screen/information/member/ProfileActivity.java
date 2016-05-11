/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * ProfileActivity (프로필 화면)
 * <p>
 * 로그인되어 있는 상태에서 프로필 정보를 보여주는 화면
 * 이름이나 연락처를 수정할 수 있고, 로그아웃할 수 있는 화면이다.
 */
package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

public class ProfileActivity extends BaseActivity
{
    private static final int REQUEST_CODE_EDIT_PROFILE = 1;

    private ProfileLayout mProfileLayout;
    private ProfileNetworkController mNetworkController;
    private String mUserIndex;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, ProfileActivity.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mProfileLayout = new ProfileLayout(this, mOnEventListener);
        mNetworkController = new ProfileNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mProfileLayout.onCreateView(R.layout.activity_profile));

        lockUI();

        mNetworkController.requestUserInformation();
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.PROFILE);

        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_PROFILE && resultCode == RESULT_OK)
        {
            lockUI();

            mNetworkController.requestUserInformation();
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private ProfileLayout.OnEventListener mOnEventListener = new ProfileLayout.OnEventListener()
    {
        @Override
        public void showEditEmail()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfileEmailActivity.newInstance(ProfileActivity.this, mUserIndex);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void showEditName(String name)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfileNameActivity.newInstance(ProfileActivity.this, mUserIndex, name);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void showEditPhone()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfilePhoneActivity.newInstance(ProfileActivity.this, mUserIndex, EditProfilePhoneActivity.Type.EDIT_PROFILE);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void showEditPassword()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfilePasswordActivity.newInstance(ProfileActivity.this);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void doSignOut()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
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

            showSimpleDialog(null, getString(R.string.dialog_msg_chk_wanna_login), getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel), posListener, null, null, new DialogInterface.OnDismissListener()
            {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    releaseUiComponent();
                }
            }, false);
        }

        @Override
        public void finish()
        {
            ProfileActivity.this.finish();
        }
    };

    private ProfileNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new ProfileNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUserInformation(String userIndex, String email, String name, String phoneNumber, boolean isPhoneVerified, String verifiedDate)
        {
            mUserIndex = userIndex;
            String userType = DailyPreference.getInstance(ProfileActivity.this).getUserType();

            mProfileLayout.updateUserInformation(userType, email, name, Util.addHippenMobileNumber(ProfileActivity.this, phoneNumber), isPhoneVerified, verifiedDate);

            unLockUI();
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ProfileActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            ProfileActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            ProfileActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            ProfileActivity.this.onErrorToastMessage(message);
        }
    };
}
