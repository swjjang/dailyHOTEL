package com.twoheart.dailyhotel.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.view.widget.DailyToast;
import com.twoheart.dailyhotel.view.widget.DailyToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPasswordActivity extends BaseActivity implements Constants, OnClickListener
{
    private TextView btnForgot;
    private EditText etForgot;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_pwd);

        initToolbar();

        etForgot = (EditText) findViewById(R.id.et_forgot_pwd);
        btnForgot = (TextView) findViewById(R.id.btn_forgot_pwd);
        btnForgot.setOnClickListener(this);
        etForgot.setImeOptions(EditorInfo.IME_ACTION_DONE);
        etForgot.setOnEditorActionListener(new OnEditorActionListener()
        {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        btnForgot.performClick();
                        break;
                }
                return false;
            }
        });
    }

    private void initToolbar()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_forgot_pwd_activity));
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.FORGOTPASSWORD, null);

        super.onStart();
    }

    // Jason | Fix send email api
    @Override
    public void onClick(View v)
    {
        if (v.getId() == btnForgot.getId())
        {
            if (isLockUiComponent() == true)
            {
                return;
            }

            lockUiComponent();

            mEmail = etForgot.getText().toString().trim();

            if (mEmail.equals(""))
            {
                releaseUiComponent();

                DailyToast.showToast(this, R.string.toast_msg_please_input_email_address, Toast.LENGTH_SHORT);
                return;
            } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(mEmail).matches() == false)
            {
                releaseUiComponent();

                DailyToast.showToast(this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                return;
            }

            lockUI();

            Map<String, String> params = new HashMap<String, String>();

            params.put("userEmail", mEmail);

            DailyNetworkAPI.getInstance().requestUserCheckEmail(mNetworkTag, params, mUserCheckEmailJsonResponseListener, this);
        }
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mUserChangePwJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            if (isFinishing() == true)
            {
                return;
            }

            try
            {
                String result = null;

                if (response != null)
                {
                    result = response.getString("isSuccess");
                }

                if ("true".equalsIgnoreCase(result) == true)
                {
                    showSimpleDialog(null, getString(R.string.dialog_msg_sent_email), getString(R.string.dialog_btn_text_confirm), null);
                    etForgot.setText("");
                } else
                {
                    String message = response.getString("msg");
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                }
            } catch (JSONException e)
            {
                onError(e);
            } finally
            {
                unLockUI();
            }

        }
    };
    private DailyHotelJsonResponseListener mUserCheckEmailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String result = null;

                if (response != null)
                {
                    result = response.getString("isSuccess");
                }

                if ("true".equalsIgnoreCase(result) == true)
                {
                    if (Util.isTextEmpty(mEmail) == true)
                    {
                        DailyToast.showToast(ForgotPasswordActivity.this, R.string.toast_msg_please_input_email_address, Toast.LENGTH_SHORT);
                    } else
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userEmail", mEmail);

                        DailyNetworkAPI.getInstance().requestUserChangePassword(mNetworkTag, params, mUserChangePwJsonResponseListener, ForgotPasswordActivity.this);
                    }
                } else
                {
                    unLockUI();

                    if (isFinishing() == true)
                    {
                        return;
                    }

                    String message = response.getString("msg");
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), null);
                }
            } catch (JSONException e)
            {
                onError(e);
                unLockUI();
            }
        }
    };
}
