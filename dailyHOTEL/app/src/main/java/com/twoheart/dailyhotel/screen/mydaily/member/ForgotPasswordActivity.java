package com.twoheart.dailyhotel.screen.mydaily.member;

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
        mEmailEditText = findViewById(R.id.emailEditText);

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
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
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
        } else if (DailyTextUtils.validEmail(mEmail) == false)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
            return;
        }

        lockUI();
        DailyMobileAPI.getInstance(ForgotPasswordActivity.this).requestUserChangePassword(mNetworkTag, mEmail, mUserChangePwCallback);
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

                showSimpleDialog(null, baseDto.msg, getString(R.string.dialog_btn_text_confirm), null);
            } else
            {
                ForgotPasswordActivity.this.onErrorResponse(call, response);
            }

            unLockUI();
        }

        @Override
        public void onFailure(Call<BaseDto<Object>> call, Throwable t)
        {
            ForgotPasswordActivity.this.onError(call, t, false);
        }
    };
}
