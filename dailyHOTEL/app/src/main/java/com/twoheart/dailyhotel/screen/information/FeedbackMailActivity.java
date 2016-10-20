package com.twoheart.dailyhotel.screen.information;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.appboy.Appboy;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class FeedbackMailActivity extends BaseActivity implements Constants, OnClickListener, View.OnFocusChangeListener
{
    private View mEmailView;
    private DailyEditText mEmailEditText, mMessageEditText;
    private String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_feeback_mail);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.frag_send_mail), new OnClickListener()
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
        mEmailEditText = (DailyEditText) findViewById(R.id.emailEditText);
        mEmailEditText.setText(DailyPreference.getInstance(this).getUserEmail());

        final View sendFeedbackView = findViewById(R.id.sendFeedbackView);
        sendFeedbackView.setOnClickListener(this);

        mEmailEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mEmailEditText.setDeleteButtonVisible(true, null);
        mEmailEditText.setOnFocusChangeListener(this);
        mEmailEditText.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_DONE:
                        sendFeedbackView.performClick();
                        break;
                }
                return false;
            }
        });

        mMessageEditText = (DailyEditText) findViewById(R.id.messageEditText);

        String formText = getString(R.string.mail_text_desc, String.format("Android : %s, v%s", Build.VERSION.RELEASE, Util.getAppVersion(this)));

        mMessageEditText.setText(formText);
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

        String message = mMessageEditText.getText().toString();
        String email = mEmail;

        boolean result = Appboy.getInstance(this).submitFeedback(email, message, false);

        if (result == true)
        {
            // 성공멘트

            finish();
        } else
        {

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
}
