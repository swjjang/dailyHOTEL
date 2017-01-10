/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * ProfileActivity (프로필 화면)
 * <p>
 * 로그인되어 있는 상태에서 프로필 정보를 보여주는 화면
 * 이름이나 연락처를 수정할 수 있고, 로그아웃할 수 있는 화면이다.
 */
package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyDeepLink;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

import retrofit2.Call;
import retrofit2.Response;

public class ProfileActivity extends BaseActivity
{
    private static final int REQUEST_CODE_EDIT_PROFILE = 1;
    private static final int REQUEST_CODE_EDIT_PROFILE_BIRTHDAY = 2;

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
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mProfileLayout = new ProfileLayout(this, mOnEventListener);
        mNetworkController = new ProfileNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mProfileLayout.onCreateView(R.layout.activity_profile));
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.PROFILE);

        super.onStart();

        if (DailyDeepLink.getInstance().isValidateLink() == true)
        {
            if (DailyDeepLink.getInstance().isProfileBirthdayView() == true)
            {
                if (DailyHotel.isLogin() == true)
                {
                    mOnEventListener.startEditBirthday(DailyPreference.getInstance(this).getUserBirthday());
                } else
                {
                    mOnEventListener.startEditBirthday(null);
                }
            }

            DailyDeepLink.getInstance().clear();
        } else
        {
            if (DailyHotel.isLogin() == false)
            {
                lockUI();
                showLoginDialog();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (DailyHotel.isLogin() == true)
        {
            lockUI();

            mNetworkController.requestUserProfile();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_EDIT_PROFILE:
            {
                if (resultCode == RESULT_OK)
                {
                    lockUI();

                    mNetworkController.requestUserProfile();
                }
                break;
            }

            case REQUEST_CODE_EDIT_PROFILE_BIRTHDAY:
            {
                if (resultCode == RESULT_OK)
                {
                    lockUI();

                    mNetworkController.requestUserProfile();
                } else
                {
                    finish();
                }
                break;
            }

            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                if (resultCode != RESULT_OK)
                {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private void showLoginDialog()
    {
        // 로그인 필요
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI();
                startLogin();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ProfileActivity.this.finish();
            }
        };

        String title = this.getResources().getString(R.string.dialog_notice2);
        String message = this.getResources().getString(R.string.dialog_message_profile_login);
        String positive = this.getResources().getString(R.string.dialog_btn_text_yes);
        String negative = this.getResources().getString(R.string.dialog_btn_text_no);

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                ProfileActivity.this.finish();
            }
        }, null, true);
    }

    private void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    private ProfileLayout.OnEventListener mOnEventListener = new ProfileLayout.OnEventListener()
    {
        @Override
        public void startEditEmail()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfileEmailActivity.newInstance(ProfileActivity.this, mUserIndex);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void startEditName(String name)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfileNameActivity.newInstance(ProfileActivity.this, mUserIndex, name);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void startEditPhone(String phoneNumber)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfilePhoneActivity.newInstance(ProfileActivity.this, mUserIndex, EditProfilePhoneActivity.Type.EDIT_PROFILE, phoneNumber);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void startEditPassword()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfilePasswordActivity.newInstance(ProfileActivity.this);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE);
        }

        @Override
        public void startEditBirthday(String birthday)
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = EditProfileBirthdayActivity.newInstance(ProfileActivity.this, mUserIndex, birthday);
            startActivityForResult(intent, REQUEST_CODE_EDIT_PROFILE_BIRTHDAY);
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

                    // Adjust에서 로그아웃시 기존 정보를 보냄으로 이벤트 발생후 삭제 필요.
                    AnalyticsManager.getInstance(ProfileActivity.this).recordScreen(Screen.MENU_LOGOUT_COMPLETE);
                    AnalyticsManager.getInstance(ProfileActivity.this).setUserInformation(AnalyticsManager.ValueType.EMPTY, AnalyticsManager.ValueType.EMPTY);

                    finish();
                }
            };

            showSimpleDialog(getString(R.string.act_profile_btn_logout), getString(R.string.dialog_msg_chk_wanna_login),//
                getString(R.string.dialog_btn_text_logout), getString(R.string.dialog_btn_text_cancel), posListener, null, null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        releaseUiComponent();
                    }
                }, false);
        }

        @Override
        public void doCodeCopy(String code)
        {
            Util.clipText(ProfileActivity.this, code);

            DailyToast.showToast(ProfileActivity.this, R.string.message_copy_recommender_code, Toast.LENGTH_SHORT);

            AnalyticsManager.getInstance(ProfileActivity.this).recordEvent(AnalyticsManager.Category.INVITE_FRIEND//
                , AnalyticsManager.Action.REFERRAL_CODE_COPIED, AnalyticsManager.Label.PROFILE_EDITED, null);
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
        public void onUserProfile(String userIndex, String email, String name, String phoneNumber, String birthday//
            , String referralCode, boolean isVerified, boolean isPhoneVerified, String verifiedDate)
        {
            mUserIndex = userIndex;
            String userType = DailyPreference.getInstance(ProfileActivity.this).getUserType();

            mProfileLayout.updateUserInformation(userType, email, name, Util.addHyphenMobileNumber(ProfileActivity.this, phoneNumber), birthday, referralCode, isVerified, isPhoneVerified, verifiedDate);

            if (isVerified == true)
            {
                if (isPhoneVerified == true)
                {
                    DailyPreference.getInstance(ProfileActivity.this).setVerification(true);
                } else
                {
                    // 인증 후 인증이 해지된 경우
                    if (DailyPreference.getInstance(ProfileActivity.this).isVerification() == true)
                    {
                        showSimpleDialog(null, getString(R.string.message_invalid_verification), getString(R.string.dialog_btn_text_confirm), null);
                    }

                    DailyPreference.getInstance(ProfileActivity.this).setVerification(false);
                }
            }

            mNetworkController.requestUserProfileBenefit();
        }

        @Override
        public void onUserProfileBenefit(boolean isExceedBonus)
        {
            AnalyticsManager.getInstance(ProfileActivity.this).setExceedBonus(isExceedBonus);

            unLockUI();
        }

        @Override
        public void onError(Throwable e)
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

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            ProfileActivity.this.onErrorResponse(call, response);
        }
    };
}
