package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.network.dto.BaseDto;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity implements Constants, OnClickListener, View.OnFocusChangeListener
{
    private View mEmailView;
    DailyEditText mEmailEditText;
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_pwd);

        initToolbar();

        mEmailView = findViewById(R.id.emailView);
        mEmailEditText = (DailyEditText) findViewById(R.id.emailEditText);

        final View forgotView = findViewById(R.id.btn_forgot_pwd);
        forgotView.setOnClickListener(this);

        mEmailEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEmailEditText.setDeleteButtonVisible(null);
        mEmailEditText.setOnFocusChangeListener(this);
        mEmailEditText.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        forgotView.performClick();
                        break;
                }
                return false;
            }
        });
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = (DailyToolbarView) findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_forgot_pwd_activity);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(this, AnalyticsManager.Screen.FORGOTPASSWORD, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        if (isLockUiComponent() == true)
        {
            return;
        }

        lockUiComponent();

        mEmail = mEmailEditText.getText().toString().trim();

        if (DailyTextUtils.isTextEmpty(mEmail) == true)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
            return;
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches() == false)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
            return;
        }

        lockUI();
        DailyMobileAPI.getInstance(this).requestUserCheckEmail(mNetworkTag, mEmail, mUserCheckEmailCallback);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.emailEditText:
                setFocusLabelView(mEmailView, mEmailEditText, hasFocus);
                break;
        }
    }

    private void setFocusLabelView(View labelView, EditText editText, boolean hasFocus)
    {
        if (hasFocus == true)
        {
            labelView.setActivated(false);
            labelView.setSelected(true);
        } else
        {
            if (editText.length() > 0)
            {
                labelView.setActivated(true);
            }

            labelView.setSelected(false);
        }
    }

    void onChangePassword(boolean isSuccess, String message)
    {
        unLockUI();

        if (isSuccess == true)
        {
            mEmailEditText.setText(null);

            showSimpleDialog(null, getString(R.string.dialog_msg_sent_email), getString(R.string.dialog_btn_text_confirm), new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            }, new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog)
                {
                    finish();
                }
            });
        } else
        {
            showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    retrofit2.Callback mUserChangePwCallback = new retrofit2.Callback<BaseDto<Object>>()
    {
        @Override
        public void onResponse(Call<BaseDto<Object>> call, Response<BaseDto<Object>> response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            if (response != null && response.isSuccessful() && response.body() != null)
            {
                BaseDto baseDto = response.body();

                onChangePassword(baseDto.msgCode == 100, baseDto.msg);
            } else
            {
                ForgotPasswordActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<BaseDto<Object>> call, Throwable t)
        {
            ForgotPasswordActivity.this.onError(call, t, false);
        }
    };

    private retrofit2.Callback mUserCheckEmailCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    String result = responseJSONObject.getString("isSuccess");

                    if ("true".equalsIgnoreCase(result) == true)
                    {
                        if (DailyTextUtils.isTextEmpty(mEmail) == true)
                        {
                            DailyToast.showToast(ForgotPasswordActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                        } else
                        {
                            DailyMobileAPI.getInstance(ForgotPasswordActivity.this).requestUserChangePassword(mNetworkTag, mEmail, mUserChangePwCallback);
                        }
                    } else
                    {
                        unLockUI();

                        if (isFinishing() == true)
                        {
                            return;
                        }

                        String message = responseJSONObject.getString("msg");
                        showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                    }
                } catch (JSONException e)
                {
                    onError(e);
                    unLockUI();
                }
            } else
            {
                ForgotPasswordActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            ForgotPasswordActivity.this.onError(call, t, false);
        }
    };
}
