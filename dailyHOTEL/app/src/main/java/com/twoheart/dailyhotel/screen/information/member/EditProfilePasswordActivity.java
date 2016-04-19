package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

public class EditProfilePasswordActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    private View mPasswordView, mConfirmPasswordView;
    private EditText mPasswordEditText, mConfirmPasswordEditText;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, EditProfilePasswordActivity.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_password);

        initToolbar();

        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_profile_activity), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout()
    {
        mPasswordView = findViewById(R.id.passwordView);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        mPasswordEditText.setOnFocusChangeListener(this);

        mConfirmPasswordView = findViewById(R.id.confirmPasswordView);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mConfirmPasswordEditText.setOnFocusChangeListener(this);

        View confirmView = findViewById(R.id.confirmView);
        confirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfilePasswordActivity.this).recordScreen(Screen.PROFILE, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                String password = mPasswordEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString();

                if (Util.isTextEmpty(password, confirmPassword) == true)
                {
                    return;
                }

                if (password.equals(confirmPassword) == true)
                {

                } else
                {

                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus == false)
        {
            return;
        }

        resetFocus();

        switch (v.getId())
        {
            case R.id.passwordEditText:
                mPasswordView.setEnabled(true);
                break;

            case R.id.confirmPasswordEditText:
                mConfirmPasswordView.setEnabled(true);
                break;
        }
    }

    private void resetFocus()
    {
        mPasswordView.setEnabled(false);
        mConfirmPasswordView.setEnabled(false);
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private DailyHotelJsonResponseListener mUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String result = response.getString("success");
                String msg = null;

                if (response.length() > 1)
                {
                    msg = response.getString("msg");
                }

                if (result.equals("true") == true)
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                } else
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, msg, Toast.LENGTH_LONG);

                    setResult(RESULT_CANCELED);
                }
            } catch (Exception e)
            {
                onError(e);
            } finally
            {
                unLockUI();
                finish();
            }
        }
    };
}
