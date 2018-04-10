package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.content.DialogInterface;
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

import com.daily.base.util.DailyTextUtils;
import com.daily.base.widget.DailyAutoCompleteEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class EditProfileEmailActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    DailyAutoCompleteEditText mEmailEditText;
    View mConfirmView, mEmailView;

    ProfileRemoteImpl mProfileRemoteImpl;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, EditProfileEmailActivity.class);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_email);

        mProfileRemoteImpl = new ProfileRemoteImpl();

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_edit_email);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
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
        mEmailView = findViewById(R.id.emailView);

        mEmailEditText = findViewById(R.id.emailEditText);
        mEmailEditText.setDeleteButtonVisible(null);
        mEmailEditText.setOnFocusChangeListener(this);
        mEmailEditText.addTextChangedListener(new TextWatcher()
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
                String email = s.toString();

                // email 유효성 체크
                if (DailyTextUtils.validEmail(email) == false)
                {
                    mConfirmView.setEnabled(false);
                } else
                {
                    mConfirmView.setEnabled(true);
                }
            }
        });

        mEmailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
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

        EmailCompleteAdapter emailCompleteAdapter = new EmailCompleteAdapter(this, Arrays.asList(getResources().getStringArray(R.array.company_email_postfix_array)));
        mEmailEditText.setAdapter(emailCompleteAdapter);

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setEnabled(false);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfileEmailActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_SETPROFILE_EMAILACCOUNT, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                String email = mEmailEditText.getText().toString();

                if (DailyTextUtils.isTextEmpty(email) == true)
                {
                    DailyToast.showToast(EditProfileEmailActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // email 유효성 체크
                if (DailyTextUtils.validEmail(email) == false)
                {
                    DailyToast.showToast(EditProfileEmailActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                onChangeEmail(email);
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

    private void onChangeEmail(String email)
    {
        if (DailyTextUtils.isTextEmpty(email) == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(Collections.singletonMap("email", email)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
            {
                showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_email), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
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

                unLockUI();
            }
        }, new Consumer<Throwable>()
        {
            @Override
            public void accept(Throwable throwable) throws Exception
            {
                onHandleError(throwable);
            }
        }));
    }
}
