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
import com.twoheart.dailyhotel.widget.DailyToast;

import java.util.Map;

public class AddProfileSocialActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;
    private static final int REQUEST_CODE_ACTIVITY = 100;

    private String mUserIdx;
    private String mCountryCode;

    private Map<String, String> mSignupParams;
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

        Customer customer = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

        mUserIdx = customer.getUserIdx();

        if (Util.isTextEmpty(customer.getPhone()) == true || Util.isValidatePhoneNumber(customer.getPhone()) == false)
        {
            mAddProfileSocialLayout.showPhoneLayout();

            mCountryCode = Util.getCountryNameNCode(this);
            mAddProfileSocialLayout.setCountryCode(mCountryCode);
        } else
        {
            mAddProfileSocialLayout.hidePhoneLayout();
        }

        if (Util.isTextEmpty(customer.getEmail()) == true)
        {
            mAddProfileSocialLayout.showEmailLayout();
        } else
        {
            mAddProfileSocialLayout.hideEmailLayout();
        }

        if (Util.isTextEmpty(customer.getName()) == true)
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
        //        if (mMode == MODE_SIGNUP)
        //        {
        //            AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordScreen(Screen.SIGNUP, null);
        //        }

        super.onStart();
    }

    public void storeLoginInfo()
    {
        //        String id = mEmailEditText.getText().toString();
        //        String pwd = Crypto.encrypt(mPasswordEditText.getText().toString()).replace("\n", "");
        //        String name = mNameEditText.getText().toString();
        //
        //        DailyPreference.getInstance(AddProfileSocialActivity.this).setUserInformation(true, id, pwd, Constants.DAILY_USER, name);
        //
        //        setResult(RESULT_OK);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
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

    private void signUpAndFinish()
    {
        unLockUI();

        DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_success_to_signup, Toast.LENGTH_LONG);
        finish();
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
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
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left);
        }

        @Override
        public void onUpdateUserInformation(String phoneNumber, String email, String name, String recommender)
        {
            mAddProfileSocialNetworkController.requestUpdateSocialUserInformation(mUserIdx, phoneNumber, email, name, recommender);
        }

        @Override
        public void finish()
        {
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
        public void onErrorMessage(int msgCode, String message)
        {
            AddProfileSocialActivity.this.onErrorMessage(msgCode, message);
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //    private DailyHotelJsonResponseListener mUserUpdateFacebookJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            if (isFinishing() == true)
    //            {
    //                return;
    //            }
    //
    //            try
    //            {
    //                unLockUI();
    //
    //                JSONObject jsonObject = response.getJSONObject("data");
    //
    //                boolean result = jsonObject.getBoolean("is_success");
    //                int msgCode = response.getInt("msg_code");
    //
    //                if (result == true)
    //                {
    //                    String msg = null;
    //
    //                    if (response.has("msg") == true)
    //                    {
    //                        msg = response.getString("msg");
    //                    }
    //
    //                    switch (msgCode)
    //                    {
    //                        case 100:
    //                        {
    //                            if (msg != null)
    //                            {
    //                                DailyToast.showToast(AddProfileSocialActivity.this, msg, Toast.LENGTH_SHORT);
    //                            }
    //
    //                            setResult(RESULT_OK);
    //                            finish();
    //                            break;
    //                        }
    //
    //                        case 200:
    //                        {
    //                            if (msg != null)
    //                            {
    //                                if (isFinishing() == true)
    //                                {
    //                                    return;
    //                                }
    //
    //                                showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, new OnClickListener()
    //                                {
    //                                    @Override
    //                                    public void onClick(View view)
    //                                    {
    //                                        setResult(RESULT_OK);
    //                                        finish();
    //                                    }
    //                                }, null);
    //                            } else
    //                            {
    //                                setResult(RESULT_OK);
    //                                finish();
    //                            }
    //                            break;
    //                        }
    //
    //                        default:
    //                            setResult(RESULT_OK);
    //                            finish();
    //                            break;
    //                    }
    //
    //                } else
    //                {
    //                    String msg = null;
    //
    //                    if (response.has("msg") == true)
    //                    {
    //                        msg = response.getString("msg");
    //                    }
    //
    //                    switch (msgCode)
    //                    {
    //                        case 100:
    //                        {
    //                            if (msg != null)
    //                            {
    //                                DailyToast.showToast(AddProfileSocialActivity.this, msg, Toast.LENGTH_SHORT);
    //                            }
    //                            break;
    //                        }
    //
    //                        case 200:
    //                        {
    //                            if (msg != null)
    //                            {
    //                                if (isFinishing() == true)
    //                                {
    //                                    return;
    //                                }
    //
    //                                showSimpleDialog(null, msg, getString(R.string.dialog_btn_text_confirm), null, null, null);
    //                            }
    //                            break;
    //                        }
    //                    }
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            unLockUI();
    //
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
    //                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
    //
    //                    setResult(RESULT_OK);
    //                    finish();
    //                } else
    //                {
    //                    DailyToast.showToast(AddProfileSocialActivity.this, msg, Toast.LENGTH_LONG);
    //                }
    //            } catch (Exception e)
    //            {
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserInfoJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                String userIndex = String.valueOf(response.getInt("idx"));
    //
    //                AnalyticsManager.getInstance(AddProfileSocialActivity.this).setUserIndex(userIndex);
    //                AnalyticsManager.getInstance(AddProfileSocialActivity.this).signUpDailyUser(userIndex, mSignupParams.get("email")//
    //                    , mSignupParams.get("name"), mSignupParams.get("phone"), AnalyticsManager.UserType.EMAIL);
    //
    //                requestGoogleCloudMessagingId();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserLoginJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignin = jsonObject.getBoolean("is_signin");
    //
    //                    if (isSignin == true)
    //                    {
    //                        JSONObject tokenJSONObject = response.getJSONObject("token");
    //                        String accessToken = tokenJSONObject.getString("access_token");
    //                        String tokenType = tokenJSONObject.getString("token_type");
    //
    //                        DailyPreference.getInstance(AddProfileSocialActivity.this).setAuthorization(String.format("%s %s", tokenType, accessToken));
    //                        storeLoginInfo();
    //
    //                        lockUI();
    //                        DailyNetworkAPI.getInstance().requestUserInformation(mNetworkTag, mUserInfoJsonResponseListener, AddProfileSocialActivity.this);
    //                        return;
    //                    }
    //                }
    //
    //                // 로그인이 실패한 경우
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_login);
    //                }
    //
    //                DailyToast.showToast(AddProfileSocialActivity.this, msg, Toast.LENGTH_LONG);
    //
    //                unLockUI();
    //                finish();
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
    //
    //    private DailyHotelJsonResponseListener mUserSignupJsonResponseListener = new DailyHotelJsonResponseListener()
    //    {
    //        @Override
    //        public void onResponse(String url, JSONObject response)
    //        {
    //            try
    //            {
    //                int msg_code = response.getInt("msg_code");
    //
    //                if (msg_code == 0)
    //                {
    //                    JSONObject jsonObject = response.getJSONObject("data");
    //
    //                    boolean isSignup = jsonObject.getBoolean("is_signup");
    //
    //                    if (isSignup == true)
    //                    {
    //                        Map<String, String> params = new HashMap<>();
    //                        params.put("email", mSignupParams.get("email"));
    //                        params.put("pw", Crypto.encrypt(mSignupParams.get("pw")).replace("\n", ""));
    //                        params.put("social_id", "0");
    //                        params.put("user_type", Constants.DAILY_USER);
    //                        params.put("is_auto", "true");
    //
    //                        DailyNetworkAPI.getInstance().requestUserSignin(mNetworkTag, params, mUserLoginJsonResponseListener, AddProfileSocialActivity.this);
    //                        return;
    //                    }
    //                }
    //
    //                unLockUI();
    //
    //                String msg = response.getString("msg");
    //
    //                if (Util.isTextEmpty(msg) == true)
    //                {
    //                    msg = getString(R.string.toast_msg_failed_to_signup);
    //                }
    //
    //                DailyToast.showToast(AddProfileSocialActivity.this, msg, Toast.LENGTH_LONG);
    //            } catch (Exception e)
    //            {
    //                unLockUI();
    //                onError(e);
    //            }
    //        }
    //    };
}
