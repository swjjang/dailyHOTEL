package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

public class EditProfilePasswordActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener, View.OnTouchListener
{
    private View mPasswordView, mConfirmPasswordView;
    private EditText mPasswordEditText, mConfirmPasswordEditText;
    private View mConfirmView;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, EditProfilePasswordActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_password);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_edit_password), new OnClickListener()
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
        mPasswordEditText.setOnTouchListener(this);

        mConfirmPasswordView = findViewById(R.id.confirmPasswordView);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mConfirmPasswordEditText.setOnFocusChangeListener(this);
        mConfirmPasswordEditText.setOnTouchListener(this);
        mConfirmPasswordEditText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (mPasswordEditText.length() > 0 && mConfirmPasswordEditText.length() > 0)
                {
                    mConfirmView.setEnabled(true);
                } else
                {
                    mConfirmView.setEnabled(false);
                }
            }
        });

        mConfirmPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        mConfirmView.performClick();
                        return true;

                    default:
                        return false;
                }
            }
        });

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setEnabled(false);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfilePasswordActivity.this).recordScreen(AnalyticsManager.Screen.MENU_SETPROFILE_PASSWORD);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                if (v.isEnabled() == false)
                {
                    return;
                }

                String password = mPasswordEditText.getText().toString();
                String confirmPassword = mConfirmPasswordEditText.getText().toString();

                // 패스워드 유효성 체크
                if (Util.isTextEmpty(password) == true || password.length() < 4)
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.toast_msg_please_input_password_more_than_4chars, Toast.LENGTH_SHORT);
                    return;
                }

                if (Util.isTextEmpty(password, confirmPassword) == true)
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // 패스워드가 동일하게 입력되어있는지 확인
                if (password.equals(confirmPassword) == false)
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.message_please_enter_the_same_password, Toast.LENGTH_SHORT);
                    return;
                }

                Map<String, String> params = Collections.singletonMap("pw", password);
                DailyNetworkAPI.getInstance(this).requestUserInformationUpdate(mNetworkTag, params, mDailyUserUpdateJsonResponseListener);
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.passwordEditText:
                setFocusLabelView(mPasswordView, mPasswordEditText, hasFocus);
                break;

            case R.id.confirmPasswordEditText:
                setFocusLabelView(mConfirmPasswordView, mConfirmPasswordEditText, hasFocus);
                break;
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private DailyHotelJsonResponseListener mDailyUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                boolean result = false;

                if (response.has("success") == true)
                {
                    result = response.getBoolean("success");
                }

                if (result == true)
                {
                    showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_password), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
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

                    setResult(RESULT_OK);
                } else
                {
                    String message = response.getString("msg");
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mPasswordEditText.setText(null);
                            mConfirmPasswordEditText.setText(null);
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mPasswordEditText.setText(null);
                            mConfirmPasswordEditText.setText(null);
                        }
                    });
                }
            } catch (Exception e)
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
            EditProfilePasswordActivity.this.onErrorResponse(volleyError);
        }
    };
}
