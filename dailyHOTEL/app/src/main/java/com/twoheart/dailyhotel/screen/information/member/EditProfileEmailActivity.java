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
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

public class EditProfileEmailActivity extends BaseActivity implements OnClickListener
{
    private static final String INTENT_EXTRA_DATA_USERINDEX = "userIndex";

    private EditText mEmailEditText;
    private View mConfirmView;
    private String mUserIndex;

    public static Intent newInstance(Context context, String userIndex)
    {
        Intent intent = new Intent(context, EditProfileEmailActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_USERINDEX, userIndex);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_email);

        Intent intent = getIntent();
        mUserIndex = intent.getStringExtra(INTENT_EXTRA_DATA_USERINDEX);

        initToolbar();
        initLayout();
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_edit_email), new OnClickListener()
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
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
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
                if (Util.isTextEmpty(email) == true || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
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

        mConfirmView = findViewById(R.id.confirmView);
        mConfirmView.setEnabled(false);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
//        AnalyticsManager.getInstance(EditProfileEmailActivity.this).recordScreen(Screen.PROFILE, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                String email = mEmailEditText.getText().toString();

                if (Util.isTextEmpty(email) == true)
                {
                    DailyToast.showToast(EditProfileEmailActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                // email 유효성 체크
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
                {
                    DailyToast.showToast(EditProfileEmailActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                Map<String, String> params = Collections.singletonMap("email", email);

                DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, params, mUserUpdateJsonResponseListener, this);
                break;
        }
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
                    DailyToast.showToast(EditProfileEmailActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                } else
                {
                    DailyToast.showToast(EditProfileEmailActivity.this, msg, Toast.LENGTH_LONG);

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
