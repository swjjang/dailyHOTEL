package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.common.PermissionManagerActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupStep1Activity extends BaseActivity
{
    private static final int REQUEST_CODE_ACTIVITY = 100;
    private static final String INTENT_EXTRA_DATA_RECOMMENDER = "recommender";

    private SignupStep1Layout mSignupStep1Layout;
    private Map<String, String> mSignupParams;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, SignupStep1Activity.class);

        return intent;
    }

    public static Intent newInstance(Context context, String recommender)
    {
        Intent intent = new Intent(context, SignupStep1Activity.class);

        if (Util.isTextEmpty(recommender) == false)
        {
            intent.putExtra(INTENT_EXTRA_DATA_RECOMMENDER, recommender);
        }

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String recommender = null;

        if (intent != null && intent.hasExtra(INTENT_EXTRA_DATA_RECOMMENDER) == true)
        {
            recommender = intent.getStringExtra(INTENT_EXTRA_DATA_RECOMMENDER);
        }

        mSignupStep1Layout = new SignupStep1Layout(this, mOnEventListener);

        setContentView(mSignupStep1Layout.onCreateView(R.layout.activity_signup_step1));

        if (Util.isTextEmpty(recommender) == false)
        {
            mSignupStep1Layout.setRecommenderText(recommender);
        }

//        Intent intentPermission = PermissionManagerActivity.newInstance(this, PermissionManagerActivity.PermissionType.READ_PHONE_STATE);
//        startActivityForResult(intentPermission, Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(SignupStep1Activity.this).recordScreen(Screen.MENU_REGISTRATION_GETINFO);

        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQEUST_ACTIVITY_SIGNUP:
            {
                if (resultCode == RESULT_OK)
                {
                    setResult(RESULT_OK);
                    finish();
                } else
                {
                    removeUserInformation();
                }
                break;
            }

            case Constants.CODE_REQUEST_ACTIVITY_PERMISSION_MANAGER:
            {
                if (resultCode == RESULT_CANCELED)
                {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;
            }
        }
    }

    public void removeUserInformation()
    {
        DailyPreference.getInstance(SignupStep1Activity.this).removeUserInformation();
    }

    private SignupStep1Layout.OnEventListener mOnEventListener = new SignupStep1Layout.OnEventListener()
    {
        @Override
        public void onValidation(final String email, final String name, final String password, final String confirmPassword, final String recommender)
        {
            if (Util.isTextEmpty(email, name, password, confirmPassword) == true)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                return;
            }

            // email 유효성 체크
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                return;
            }

            // 패스워드 유효성 체크
            if (password.length() < 4)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_please_input_password_more_than_4chars, Toast.LENGTH_SHORT);
                return;
            }

            // 패스워드가 동일하게 입력되어있는지 확인
            if (password.equals(confirmPassword) == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.message_please_enter_the_same_password, Toast.LENGTH_SHORT);
                return;
            }

            // 동의 체크 확인
            if (mSignupStep1Layout.isCheckedTermsOfService() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_terms_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mSignupStep1Layout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(SignupStep1Activity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            if (mSignupParams == null)
            {
                mSignupParams = new HashMap<>();
            }

            mSignupParams.clear();
            mSignupParams.put("email", email);
            mSignupParams.put("pw", password);
            mSignupParams.put("name", name);

            if (Util.isTextEmpty(recommender) == false)
            {
                mSignupParams.put("recommender", recommender);
            }

            mSignupParams.put("market_type", RELEASE_STORE.getName());

            DailyNetworkAPI.getInstance(SignupStep1Activity.this).requestSignupValidation(mNetworkTag, mSignupParams, mSignupValidationListener);
        }

        @Override
        public void showTermOfService()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(SignupStep1Activity.this, TermActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void showTermOfPrivacy()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(SignupStep1Activity.this, PrivacyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void finish()
        {
            SignupStep1Activity.this.finish();
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mSignupValidationListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                int msgCode = response.getInt("msgCode");

                if (msgCode == 100)
                {
                    JSONObject dataJSONObject = response.getJSONObject("data");
                    String signupKey = dataJSONObject.getString("signup_key");

                    Intent intent = SignupStep2Activity.newInstance(SignupStep1Activity.this, signupKey, mSignupParams.get("email"), mSignupParams.get("pw"), mSignupParams.get("recommender"));
                    startActivityForResult(intent, CODE_REQEUST_ACTIVITY_SIGNUP);
                } else
                {
                    onErrorPopupMessage(msgCode, response.getString("msg"), null);
                }
            } catch (Exception e)
            {
                onError(e);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            try
            {
                JSONObject jsonObject = new JSONObject(new String(volleyError.networkResponse.data));
                onErrorPopupMessage(jsonObject.getInt("msgCode"), jsonObject.getString("msg"), null);
            } catch (Exception e)
            {
                SignupStep1Activity.this.onErrorResponse(volleyError);
            }
        }
    };
}
