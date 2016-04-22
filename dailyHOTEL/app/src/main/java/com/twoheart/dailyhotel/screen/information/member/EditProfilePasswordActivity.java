package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

public class EditProfilePasswordActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    private static final String INTENT_EXTRA_DATA_USERINDEX = "userIndex";

    private View mPasswordView, mConfirmPasswordView;
    private EditText mPasswordEditText, mConfirmPasswordEditText;
    private View mConfirmView;
    private String mUserIndex;

    public static Intent newInstance(Context context, String userIndex)
    {
        Intent intent = new Intent(context, EditProfilePasswordActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_USERINDEX, userIndex);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_password);

        Intent intent = getIntent();
        mUserIndex = intent.getStringExtra(INTENT_EXTRA_DATA_USERINDEX);

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

        mConfirmPasswordView = findViewById(R.id.confirmPasswordView);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        mConfirmPasswordEditText.setOnFocusChangeListener(this);
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
        //        AnalyticsManager.getInstance(EditProfilePasswordActivity.this).recordScreen(Screen.PROFILE, null);

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
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // 패스워드가 동일하게 입력되어있는지 확인
                if (password.equals(confirmPassword) == false)
                {
                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.message_please_enter_the_same_password, Toast.LENGTH_SHORT);
                    return;
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
                mPasswordView.setSelected(true);
                break;

            case R.id.confirmPasswordEditText:
                mConfirmPasswordView.setSelected(true);
                break;
        }
    }

    private void resetFocus()
    {
        mPasswordView.setSelected(false);
        mConfirmPasswordView.setSelected(false);
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
//                    DailyToast.showToast(EditProfilePasswordActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);
//
//                    setResult(RESULT_OK);
//                } else
//                {
//                    DailyToast.showToast(EditProfilePasswordActivity.this, msg, Toast.LENGTH_LONG);
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
