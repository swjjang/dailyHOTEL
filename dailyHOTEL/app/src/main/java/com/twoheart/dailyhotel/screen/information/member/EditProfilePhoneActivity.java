package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;

public class EditProfilePhoneActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private EditProfilePhoneLayout mEditProfilePhoneLayout;
    private String mCountryCode;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, EditProfilePhoneActivity.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mEditProfilePhoneLayout = new EditProfilePhoneLayout(this, mOnEventListener);

        setContentView(mEditProfilePhoneLayout.onCreateView(R.layout.activity_edit_phone));

        mCountryCode = Util.getCountryNameNCode(this);
        mEditProfilePhoneLayout.setCountryCode(mCountryCode);
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(EditProfilePhoneActivity.this).recordScreen(Screen.PROFILE, null);

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
        public void doVerification(String phoneNumber)
        {

        }

        @Override
        public void doSignUp(String phoneNumber, String verificationNumber)
        {

        }

        @Override
        public void finish()
        {
            EditProfilePhoneActivity.this.finish();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                String result = response.getString("success");
    //                String msg = null;
    //
    //                if (response.length() > 1)
    //                {
    //                    msg = response.getString("msg");
    //                }
    //
    //                if (result.equals("true") == true)
    //                {
    //                    DailyToast.showToast(EditProfilePhoneActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
    //
    //                    setResult(RESULT_OK);
    //                } else
    //                {
    //                    DailyToast.showToast(EditProfilePhoneActivity.this, msg, Toast.LENGTH_LONG);
    //
    //                    setResult(RESULT_CANCELED);
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //            } finally
    //            {
    //                unLockUI();
    //                finish();
    //            }
    //        }
    //    };
}
