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
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class EditProfileNameActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    private static final String INTENT_EXTRA_DATA_NAME = "name";

    DailyEditText mNameEditText;
    View mConfirmView, mNameView;

    ProfileRemoteImpl mProfileRemoteImpl;

    public static Intent newInstance(Context context, String name)
    {
        Intent intent = new Intent(context, EditProfileNameActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_NAME, name);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_name);

        mProfileRemoteImpl = new ProfileRemoteImpl(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra(INTENT_EXTRA_DATA_NAME);

        initToolbar();
        initLayout(name);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_edit_name);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(String name)
    {
        mNameView = findViewById(R.id.nameView);

        mNameEditText = findViewById(R.id.nameEditText);
        mNameEditText.setDeleteButtonVisible(null);
        mNameEditText.setOnFocusChangeListener(this);

        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            mNameEditText.setText(null);
        } else
        {
            mNameEditText.setText(name);
        }

        mNameEditText.setSelection(mNameEditText.length());

        mNameEditText.addTextChangedListener(new TextWatcher()
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
                if (s.length() > 1)
                {
                    mConfirmView.setEnabled(true);
                } else
                {
                    mConfirmView.setEnabled(false);
                }
            }
        });

        mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
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

        if (DailyTextUtils.isTextEmpty(name) == true)
        {
            mConfirmView.setEnabled(false);
        } else
        {
            mConfirmView.setEnabled(true);
        }

        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfileNameActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_SETPROFILE_NAME, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                String name = mNameEditText.getText().toString();

                if (DailyTextUtils.isTextEmpty(name) == true)
                {
                    DailyToast.showToast(EditProfileNameActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                onChangeName(name);
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
            case R.id.nameEditText:
                setFocusLabelView(mNameView, mNameEditText, hasFocus);
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

    private void onChangeName(String name)
    {
        if (DailyTextUtils.isTextEmpty(name) == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(Collections.singletonMap("name", name)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
            {
                setResult(RESULT_OK);

                showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_name), getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
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
