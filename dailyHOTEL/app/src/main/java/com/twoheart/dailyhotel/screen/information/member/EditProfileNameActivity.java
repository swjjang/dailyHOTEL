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
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

public class EditProfileNameActivity extends BaseActivity implements OnClickListener
{
    private static final String INTENT_EXTRA_DATA_USERINDEX = "userIndex";
    private static final String INTENT_EXTRA_DATA_NAME = "name";

    private EditText mNameEditText;
    private View mConfirmView;
    private String mUserIndex;

    public static Intent newInstance(Context context, String userIndex, String name)
    {
        Intent intent = new Intent(context, EditProfileNameActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_USERINDEX, userIndex);
        intent.putExtra(INTENT_EXTRA_DATA_NAME, name);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_name);

        Intent intent = getIntent();
        mUserIndex = intent.getStringExtra(INTENT_EXTRA_DATA_USERINDEX);
        String name = intent.getStringExtra(INTENT_EXTRA_DATA_NAME);

        initToolbar();
        initLayout(name);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_edit_name), new OnClickListener()
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
        mNameEditText = (EditText) findViewById(R.id.nameEditText);
        mNameEditText.setText(name);
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
        mConfirmView.setEnabled(false);
        mConfirmView.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        //        AnalyticsManager.getInstance(EditProfileNameActivity.this).recordScreen(Screen.PROFILE, null);

        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
                String name = mNameEditText.getText().toString();

                if (Util.isTextEmpty(name) == true)
                {
                    DailyToast.showToast(EditProfileNameActivity.this, R.string.toast_msg_please_input_required_infos, Toast.LENGTH_SHORT);
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                Map<String, String> params = Collections.singletonMap("name", name);

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
                    DailyToast.showToast(EditProfileNameActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                } else
                {
                    DailyToast.showToast(EditProfileNameActivity.this, msg, Toast.LENGTH_LONG);

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
