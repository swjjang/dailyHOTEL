package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

public class AddProfileSocialActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;
    private static final int REQUEST_CODE_ACTIVITY = 100;

    private String mUserIdx;
    private String mCountryCode;

    private Customer mCustomer;
    private AddProfileSocialLayout mAddProfileSocialLayout;
    private AddProfileSocialNetworkController mAddProfileSocialNetworkController;

    public static Intent newInstance(Context context, Customer customer)
    {
        Intent intent = new Intent(context, AddProfileSocialActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, customer);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mAddProfileSocialLayout = new AddProfileSocialLayout(this, mOnEventListener);
        mAddProfileSocialNetworkController = new AddProfileSocialNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

        setContentView(mAddProfileSocialLayout.onCreateView(R.layout.activity_add_profile_social));

        initUserInformation(getIntent());
    }

    private void initUserInformation(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        mCustomer = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

        mUserIdx = mCustomer.getUserIdx();

        if (Util.isValidatePhoneNumber(mCustomer.getPhone()) == false)
        {
            mAddProfileSocialLayout.showPhoneLayout();

            mCountryCode = Util.getCountryNameNCode(this);
            mAddProfileSocialLayout.setCountryCode(mCountryCode);
        } else
        {
            mAddProfileSocialLayout.hidePhoneLayout();
        }

        if (Util.isTextEmpty(mCustomer.getEmail()) == true)
        {
            mAddProfileSocialLayout.showEmailLayout();
        } else
        {
            mAddProfileSocialLayout.hideEmailLayout();
        }

        if (Util.isTextEmpty(mCustomer.getName()) == true)
        {
            mAddProfileSocialLayout.showNameLayout();
        } else
        {
            mAddProfileSocialLayout.hideNameLayout();
        }

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_facebook_update), getString(R.string.dialog_btn_text_confirm), null, null, null);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_ACCOUNTDETAIL);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

        super.onBackPressed();
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

                mAddProfileSocialLayout.setCountryCode(mCountryCode);
            }
        }
    }

    private AddProfileSocialLayout.OnEventListener mOnEventListener = new AddProfileSocialLayout.OnEventListener()
    {
        @Override
        public void showTermOfService()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(AddProfileSocialActivity.this, TermActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void showTermOfPrivacy()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(AddProfileSocialActivity.this, PrivacyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void showCountryCodeList()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = CountryCodeListActivity.newInstance(AddProfileSocialActivity.this, mCountryCode);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY);
        }

        @Override
        public void onUpdateUserInformation(String phoneNumber, String email, String name, String recommender)
        {
            // 전화번호가 없거나 잘못 된경우
            if (Util.isTextEmpty(mCustomer.getPhone()) == true || Util.isValidatePhoneNumber(mCustomer.getPhone()) == false)
            {
                if (Util.isTextEmpty(phoneNumber) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_phone, Toast.LENGTH_SHORT);
                    return;
                }

                if (Util.isValidatePhoneNumber(phoneNumber) == false)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_wrong_phonenumber, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 이메일이 없는 경우
            if (Util.isTextEmpty(mCustomer.getEmail()) == true)
            {
                if (Util.isTextEmpty(email) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
                    return;
                }

                // email 유효성 체크
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 이름이 없는 경우
            if (Util.isTextEmpty(mCustomer.getName()) == true)
            {
                if (Util.isTextEmpty(name) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_name, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 동의 체크 확인
            if (mAddProfileSocialLayout.isCheckedTermsOfService() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_terms_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mAddProfileSocialLayout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mAddProfileSocialLayout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            mAddProfileSocialNetworkController.requestUpdateSocialUserInformation(mUserIdx, phoneNumber, email, name, recommender);
        }

        @Override
        public void finish()
        {
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

            AddProfileSocialActivity.this.finish();
        }
    };

    private AddProfileSocialNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new AddProfileSocialNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUpdateSocialUserInformation(String message)
        {
            if (Util.isTextEmpty(message) == true)
            {
                AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "Confirm", null);

                setResult(RESULT_OK);
                finish();
            } else
            {
                DailyToast.showToast(AddProfileSocialActivity.this, message, Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            AddProfileSocialActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            AddProfileSocialActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            AddProfileSocialActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            AddProfileSocialActivity.this.onErrorToastMessage(message);
        }
    };
}
