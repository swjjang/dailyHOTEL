package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager.Screen;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

public class EditProfilePhoneActivity extends BaseActivity implements OnClickListener
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;

    private static final String INTENT_EXTRA_DATA_PHONE = "phone";

    private String mCountryCode;
    private EditText mPhoneEditText;

    public static Intent newInstance(Context context, String phone)
    {
        Intent intent = new Intent(context, EditProfilePhoneActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PHONE, phone);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_phone);

        Intent intent = getIntent();

        String phone = intent.getStringExtra(INTENT_EXTRA_DATA_PHONE);

        initToolbar();

        initLayout(phone);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_profile_activity), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(String phone)
    {
        View countryEditText = findViewById(R.id.countryEditText);
        countryEditText.setOnClickListener(this);

        mPhoneEditText = (EditText) findViewById(R.id.phoneEditText);
        mPhoneEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    // 인증번호 요청
                    // 번호 검증 후에 인증번호 요청
                    String phoneNumber = v.getText().toString().trim();

                }

                return false;
            }
        });
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(EditProfilePhoneActivity.this).recordScreen(Screen.PROFILE, null);

        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        releaseUiComponent();

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY)
        {
            if (resultCode == RESULT_OK && data != null)
            {
                mCountryCode = data.getStringExtra(CountryCodeListActivity.INTENT_EXTRA_COUNTRY_CODE);
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
            {
                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                String countryCode = mCountryCode.substring(mCountryCode.indexOf('\n') + 1);
                String phoneNumber = String.format("%s %s", countryCode, mPhoneEditText.getText().toString().trim());

                Map<String, String> params = Collections.singletonMap("phone", phoneNumber);

                DailyNetworkAPI.getInstance().requestUserInformationUpdate(mNetworkTag, params, mUserUpdateJsonResponseListener, this);
                break;
            }

            case R.id.countryEditText:
            {
                break;
            }
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
                    DailyToast.showToast(EditProfilePhoneActivity.this, R.string.toast_msg_profile_success_to_change, Toast.LENGTH_SHORT);

                    setResult(RESULT_OK);
                } else
                {
                    DailyToast.showToast(EditProfilePhoneActivity.this, msg, Toast.LENGTH_LONG);

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
