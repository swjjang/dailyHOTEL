package com.twoheart.dailyhotel.screen.information;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseActivity implements Constants, OnClickListener, View.OnTouchListener, View.OnFocusChangeListener
{
    private View mEmailView;
    private EditText mEmailEditText;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_pwd);

        initToolbar();

        mEmailView = findViewById(R.id.emailView);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);

        final View forgotView = findViewById(R.id.btn_forgot_pwd);
        forgotView.setOnClickListener(this);

        mEmailEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEmailEditText.setOnTouchListener(this);
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
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_forgot_pwd_activity), new OnClickListener()
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
        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.FORGOTPASSWORD);

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

        if (Util.isTextEmpty(mEmail) == true)
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
        DailyNetworkAPI.getInstance(this).requestUserCheckEmail(mNetworkTag, mEmail, mUserCheckEmailJsonResponseListener);
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

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (v instanceof EditText == false)
        {
            return false;
        }

        EditText editText = (EditText) v;

        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;

        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            Drawable[] drawables = editText.getCompoundDrawables();

            if (drawables == null || drawables[DRAWABLE_RIGHT] == null)
            {
                return false;
            }

            int withDrawable = drawables[DRAWABLE_RIGHT].getBounds().width() + editText.getCompoundDrawablePadding();

            if (event.getRawX() >= (editText.getRight() - withDrawable))
            {
                editText.setText(null);
            }
        }

        return false;
    }

    private void setFocusLabelView(View labelView, EditText editText, boolean hasFocus)
    {
        if (hasFocus == true)
        {
            labelView.setActivated(false);
            labelView.setSelected(true);

            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search_ic_01_delete, 0);
        } else
        {
            if (editText.length() > 0)
            {
                labelView.setActivated(true);
            }

            labelView.setSelected(false);

            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
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
                String result = response.getString("isSuccess");

                if ("true".equalsIgnoreCase(result) == true)
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ForgotPasswordActivity.this.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mUserCheckEmailJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                String result = response.getString("isSuccess");

                if ("true".equalsIgnoreCase(result) == true)
                {
                    if (Util.isTextEmpty(mEmail) == true)
                    {
                        DailyToast.showToast(ForgotPasswordActivity.this, R.string.toast_msg_please_input_email, Toast.LENGTH_SHORT);
                    } else
                    {
                        DailyNetworkAPI.getInstance(ForgotPasswordActivity.this).requestUserChangePassword(mNetworkTag, mEmail, mUserChangePwJsonResponseListener);
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

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            ForgotPasswordActivity.this.onErrorResponse(volleyError);
        }
    };
}
