package com.twoheart.dailyhotel.screen.information.member;

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

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyNetworkAPI;
import com.twoheart.dailyhotel.network.response.DailyHotelJsonResponseListener;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
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
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

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

        if (Util.isTextEmpty(name) == true)
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
        AnalyticsManager.getInstance(EditProfileNameActivity.this).recordScreen(AnalyticsManager.Screen.MENU_SETPROFILE_NAME);

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

                if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(EditProfileNameActivity.this).getUserType()) == true)
                {
                    Map<String, String> params = Collections.singletonMap("name", name);
                    DailyNetworkAPI.getInstance(this).requestUserInformationUpdate(mNetworkTag, params, mDailyUserUpdateJsonResponseListener);
                } else
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_idx", mUserIndex);
                    params.put("user_name", name);

                    DailyNetworkAPI.getInstance(this).requestUserUpdateInformationForSocial(mNetworkTag, params, mSocialUserUpdateJsonResponseListener);
                }
                break;
        }
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
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

                    setResult(RESULT_OK);
                } else
                {
                    String message = response.getString("msg");
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mNameEditText.setText(null);
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mNameEditText.setText(null);
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
            EditProfileNameActivity.this.onErrorResponse(volleyError);
        }
    };

    private DailyHotelJsonResponseListener mSocialUserUpdateJsonResponseListener = new DailyHotelJsonResponseListener()
    {
        @Override
        public void onErrorResponse(VolleyError volleyError)
        {

        }

        @Override
        public void onResponse(String url, JSONObject response)
        {
            try
            {
                JSONObject jsonObject = response.getJSONObject("data");

                boolean result = jsonObject.getBoolean("is_success");

                // TODO :  추후에 msgCode결과를 가지고 구분하는 코드가 필요할듯.
                int msgCode = response.getInt("msg_code");

                if (result == true)
                {
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

                    setResult(RESULT_OK);
                } else
                {
                    String message = response.getString("msg");
                    showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            mNameEditText.setText(null);
                        }
                    }, new DialogInterface.OnCancelListener()
                    {
                        @Override
                        public void onCancel(DialogInterface dialog)
                        {
                            mNameEditText.setText(null);
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
    };
}
