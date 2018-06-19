package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class EditProfilePhoneActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private static final String INTENT_EXTRA_DATA_TYPE = "type";
    private static final String INTENT_EXTRA_DATA_PHONENUMBER = "phoneNumber";

    EditProfilePhoneLayout mEditProfilePhoneLayout;
    EditProfilePhoneNetworkController mNetworkController;
    String mCountryCode;
    int mRequestVerificationCount;

    ProfileRemoteImpl mProfileRemoteImpl;

    public enum Type
    {
        EDIT_PROFILE,
        WRONG_PHONENUMBER,
        NEED_VERIFICATION_PHONENUMBER
    }

    /**
     * @param context
     * @param type
     * @return
     */
    public static Intent newInstance(Context context, Type type, String phoneNumber)
    {
        Intent intent = new Intent(context, EditProfilePhoneActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_TYPE, type.name());

        if (DailyTextUtils.isTextEmpty(phoneNumber) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_PHONENUMBER, phoneNumber);
        }

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mEditProfilePhoneLayout = new EditProfilePhoneLayout(this, mOnEventListener);
        mNetworkController = new EditProfilePhoneNetworkController(this, mNetworkTag, mOnNetworkControllerListener);
        mProfileRemoteImpl = new ProfileRemoteImpl();

        setContentView(mEditProfilePhoneLayout.onCreateView(R.layout.activity_edit_phone));

        Intent intent = getIntent();

        Type type;

        try
        {
            type = Type.valueOf(intent.getStringExtra(INTENT_EXTRA_DATA_TYPE));
        } catch (Exception e)
        {
            Util.restartApp(this);
            return;
        }

        String phoneNumber;

        if (intent.hasExtra(INTENT_EXTRA_DATA_PHONENUMBER) == true)
        {
            phoneNumber = intent.getStringExtra(INTENT_EXTRA_DATA_PHONENUMBER);

            String[] phoneNumbers = Util.getValidatePhoneNumber(phoneNumber.replace("-", ""));

            if (phoneNumbers != null)
            {
                mCountryCode = phoneNumbers[0];
                phoneNumber = phoneNumbers[1];
            } else
            {
                mCountryCode = Util.getCountryNameNCode(this);
                phoneNumber = null;
            }
        } else
        {
            mCountryCode = Util.getCountryNameNCode(this);
            phoneNumber = null;
        }

        mEditProfilePhoneLayout.setCountryCode(mCountryCode);

        switch (type)
        {
            case EDIT_PROFILE:
                if (Constants.DAILY_USER.equalsIgnoreCase(DailyUserPreference.getInstance(this).getType()) == true)
                {
                    mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_guide));
                    mEditProfilePhoneLayout.showCertificationLayout();

                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                } else
                {
                    mEditProfilePhoneLayout.setGuideText(getString(R.string.message_edit_phone_social_guide));
                    mEditProfilePhoneLayout.hideCertificationLayout();
                    mEditProfilePhoneLayout.showConfirmButton();

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

        // showCertificationLayout() 다음으로 호출 순서가 중요함
        if (DailyTextUtils.isTextEmpty(phoneNumber) == false)
        {
            mEditProfilePhoneLayout.setPhoneNumber(phoneNumber.replaceAll("\\(|\\)|-", ""));
        }
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfilePhoneActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_SETPROFILE_PHONENUMBER, null);

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
        mEditProfilePhoneLayout.hideKeypad();
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    private EditProfilePhoneLayout.OnEventListener mOnEventListener = new EditProfilePhoneLayout.OnEventListener()
    {
        @Override
        public void showCountryCodeList()
        {
            Intent intent = CountryCodeListActivity.newInstance(EditProfilePhoneActivity.this, mCountryCode);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY);
        }

        @Override
        public void doConfirm(String phoneNumber)
        {
            mEditProfilePhoneLayout.hideKeypad();

            if (Constants.DAILY_USER.equalsIgnoreCase(DailyUserPreference.getInstance(EditProfilePhoneActivity.this).getType()) == true)
            {
                finish();
            } else
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(Collections.singletonMap("phone", phoneNumber)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
                {
                    @Override
                    public void accept(User user) throws Exception
                    {
                        mOnNetworkControllerListener.onConfirm();

                        unLockUI();
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onHandleError(throwable);
                    }
                }));
            }
        }

        @Override
        public void doConfirm(String phoneNumber, String verificationNumber)
        {
            if (Constants.DAILY_USER.equalsIgnoreCase(DailyUserPreference.getInstance(EditProfilePhoneActivity.this).getType()) == true)
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

    EditProfilePhoneNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new EditProfilePhoneNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onVerification(String message)
        {
            unLockUI();

            mEditProfilePhoneLayout.showVerificationVisible();

            if (++mRequestVerificationCount == SignupStep2Activity.VERIFY_PHONE_NUMBER_COUNT)
            {
                try
                {
                    String[] phoneNumber = mEditProfilePhoneLayout.getPhoneNumber().split(" ");
                    String number = phoneNumber[1].replaceAll("\\(|\\)", "");

                    message = getString(R.string.message_signup_step2_check_your_phonenumber, number);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }
            }

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
        public void onError(Call call, Throwable e, boolean onlyReport)
        {
            EditProfilePhoneActivity.this.onError(call, e, onlyReport);
        }

        @Override
        public void onError(Throwable e)
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

        @Override
        public void onErrorResponse(Call call, Response response)
        {
            EditProfilePhoneActivity.this.onErrorResponse(call, response);
        }
    };
}
