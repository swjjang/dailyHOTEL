package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

public class EditProfilePhoneActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private static final String INTENT_EXTRA_DATA_USERINDEX = "userIndex";
    private static final String INTENT_EXTRA_DATA_TYPE = "type";

    private EditProfilePhoneLayout mEditProfilePhoneLayout;
    private EditProfilePhoneNetworkController mNetworkController;
    private String mCountryCode;
    private String mUserIndex; // 소셜 계정인 경우에는 userIndex, 일반 계정인 경우에는 이름이 넘어온다
    private Type mType;

    public enum Type
    {
        EDIT_PROFILE,
        WRONG_PHONENUMBER,
        NEED_VERIFICATION_PHONENUMBER
    }

    /**
     * @param context
     * @param userIndex
     * @param type
     * @return
     */
    public static Intent newInstance(Context context, String userIndex, Type type)
    {
        Intent intent = new Intent(context, EditProfilePhoneActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_USERINDEX, userIndex);
        intent.putExtra(INTENT_EXTRA_DATA_TYPE, type.name());

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mEditProfilePhoneLayout = new EditProfilePhoneLayout(this, mOnEventListener);
        mNetworkController = new EditProfilePhoneNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mEditProfilePhoneLayout.onCreateView(R.layout.activity_edit_phone));

        Intent intent = getIntent();
        mUserIndex = intent.getStringExtra(INTENT_EXTRA_DATA_USERINDEX);
        mType = Type.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_TYPE));

        mCountryCode = Util.getCountryNameNCode(this);
        mEditProfilePhoneLayout.setCountryCode(mCountryCode);

        switch (mType)
        {
            case EDIT_PROFILE:
                if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(this).getUserType()) == true)
                {
                    mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_guide));
                    mEditProfilePhoneLayout.showCertificationLayout();

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                } else
                {
                    mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_social_guide));
                    mEditProfilePhoneLayout.hideCertificationLayout();

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                break;

            case WRONG_PHONENUMBER:
                mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_social_guide));
                mEditProfilePhoneLayout.hideCertificationLayout();

                showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_register_phonenumber), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        mEditProfilePhoneLayout.showKeyPad();
                    }
                });
                break;

            case NEED_VERIFICATION_PHONENUMBER:
                mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_guide));
                mEditProfilePhoneLayout.showCertificationLayout();

                showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.message_register_phonenumber), getString(R.string.dialog_btn_text_confirm), null, new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        mEditProfilePhoneLayout.showKeyPad();
                    }
                });
                break;
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfilePhoneActivity.this).recordScreen(AnalyticsManager.Screen.MENU_SETPROFILE_PHONENUMBER);

        super.onStart();
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

                mEditProfilePhoneLayout.setCountryCode(mCountryCode);
            }
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private EditProfilePhoneLayout.OnEventListener mOnEventListener = new EditProfilePhoneLayout.OnEventListener()
    {
        @Override
        public void showCountryCodeList()
        {
            Intent intent = CountryCodeListActivity.newInstance(EditProfilePhoneActivity.this, mCountryCode);

            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void doConfirm(String phoneNumber)
        {
            mEditProfilePhoneLayout.hideKeypad();

            if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(EditProfilePhoneActivity.this).getUserType()) == true)
            {
                finish();
            } else
            {
                mNetworkController.requestUpdateSocialUserInformation(mUserIndex, phoneNumber);
            }
        }

        @Override
        public void doConfirm(String phoneNumber, String verificationNumber)
        {
            if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(EditProfilePhoneActivity.this).getUserType()) == true)
            {
                mNetworkController.requestUpdateDailyUserInformation(phoneNumber, verificationNumber);
            }
        }

        @Override
        public void doVerification(String phoneNumber)
        {
            mNetworkController.requestDailyUserVerification(phoneNumber, false);
        }

        @Override
        public void finish()
        {
            EditProfilePhoneActivity.this.finish();
        }
    };

    private EditProfilePhoneNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new EditProfilePhoneNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onVerification(String message)
        {
            unLockUI();

            mEditProfilePhoneLayout.showVerificationVisible();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onAlreadyVerification(final String phoneNumber)
        {
            unLockUI();

            showSimpleDialog(null, getString(R.string.message_signup_step2_already_verification)//
                , getString(R.string.dialog_btn_text_continue)//
                , getString(R.string.dialog_btn_text_input_other_phonenumber), new View.OnClickListener()//
                {
                    @Override
                    public void onClick(View v)
                    {
                        lockUI();

                        mNetworkController.requestDailyUserVerification(phoneNumber, true);
                    }
                }, new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mEditProfilePhoneLayout.resetPhoneNumber();
                    }
                });
        }

        @Override
        public void onConfirm()
        {
            DailyPreference.getInstance(EditProfilePhoneActivity.this).setVerification(true);

            showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_phonenumber), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setResult(RESULT_OK);
                    finish();
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    setResult(RESULT_OK);
                    finish();
                }
            });
        }

        @Override
        public void onInvalidPhoneNumber(String message)
        {
            unLockUI();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onInvalidVerificationNumber(String message)
        {
            unLockUI();

            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            EditProfilePhoneActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            EditProfilePhoneActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            EditProfilePhoneActivity.this.onErrorPopupMessage(msgCode, message, null);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            EditProfilePhoneActivity.this.onErrorToastMessage(message);
        }
    };
}
