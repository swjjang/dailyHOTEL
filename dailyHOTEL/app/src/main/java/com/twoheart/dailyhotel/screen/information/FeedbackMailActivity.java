package com.twoheart.dailyhotel.screen.information;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.appboy.Appboy;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class FeedbackMailActivity extends BaseActivity implements Constants, OnClickListener
{
    DailyEditText mEmailEditText, mMessageEditText;

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
        mEmailEditText = (DailyEditText) findViewById(R.id.emailEditText);
        mEmailEditText.setText(DailyPreference.getInstance(this).getUserEmail());

        final View sendFeedbackView = findViewById(R.id.sendFeedbackView);
        sendFeedbackView.setOnClickListener(this);

        mEmailEditText.setDeleteButtonVisible(null);
        mEmailEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        mMessageEditText = (DailyEditText) findViewById(R.id.messageEditText);

        mEmailEditText.setOnEditorActionListener(new OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                switch (actionId)
                {
                    case EditorInfo.IME_ACTION_NEXT:
                        mMessageEditText.requestFocus();
                        break;
                }
                return false;
            }
        });

        TextView informationTextView = (TextView) findViewById(R.id.informationTextView);
        String formText = getString(R.string.mail_base_information, String.format("Android : %s, v%s", Build.VERSION.RELEASE, Util.getAppVersionCode(this)));
        informationTextView.setText(formText);

        // 기본 정보를 태그에 넣음.
        mMessageEditText.setTag(formText);
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(this).recordScreen(AnalyticsManager.Screen.FORGOTPASSWORD);

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

        String email = mEmailEditText.getText().toString().trim();
        String message = mMessageEditText.getText().toString().trim();

        if (Util.isTextEmpty(email) == true)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.message_input_email, Toast.LENGTH_SHORT);
            return;
        } else if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
            return;
        } else if (Util.isTextEmpty(message) == true)
        {
            releaseUiComponent();

            DailyToast.showToast(this, R.string.message_input_email_body, Toast.LENGTH_SHORT);
            return;
        }

        lockUI();

        String information = (String) mMessageEditText.getTag();
        message = information + "\n" + message;

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
}
