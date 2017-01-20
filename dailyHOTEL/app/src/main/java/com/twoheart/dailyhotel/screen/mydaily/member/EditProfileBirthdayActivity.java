package com.twoheart.dailyhotel.screen.mydaily.member;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.network.DailyMobileAPI;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToast;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class EditProfileBirthdayActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    private static final String INTENT_EXTRA_DATA_USERINDEX = "userIndex";
    private static final String INTENT_EXTRA_DATA_BIRTHDAY = "birthday";

    DailyEditText mBirthdayEditText;
    View mConfirmView, mBirthdayView;
    private String mUserIndex;

    public static Intent newInstance(Context context, String userIndex, String birthday)
    {
        Intent intent = new Intent(context, EditProfileBirthdayActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_USERINDEX, userIndex);
        intent.putExtra(INTENT_EXTRA_DATA_BIRTHDAY, birthday);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_birthday);

        Intent intent = getIntent();
        mUserIndex = intent.getStringExtra(INTENT_EXTRA_DATA_USERINDEX);
        String birthday = intent.getStringExtra(INTENT_EXTRA_DATA_BIRTHDAY);

        initToolbar();
        initLayout(birthday);
    }

    private void initToolbar()
    {
        View toolbar = findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(this, toolbar);
        dailyToolbarLayout.initToolbar(getString(R.string.actionbar_title_edit_birthday), new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private void initLayout(String birthday)
    {
        mBirthdayView = findViewById(R.id.birthdayView);

        mBirthdayEditText = (DailyEditText) findViewById(R.id.birthdayEditText);
        mBirthdayEditText.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                dailyEditText.setTag(null);
            }
        });
        mBirthdayEditText.setOnFocusChangeListener(this);
        mBirthdayEditText.setOnClickListener(this);

        if (Util.isTextEmpty(birthday) == true)
        {
            mBirthdayEditText.setText(null);
        } else
        {
            setBirthdayText(birthday);
        }

        mBirthdayEditText.addTextChangedListener(new TextWatcher()
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

        mConfirmView = findViewById(R.id.confirmView);

        if (Util.isTextEmpty(birthday) == true)
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
        AnalyticsManager.getInstance(EditProfileBirthdayActivity.this).recordScreen(this, AnalyticsManager.Screen.MENU_SET_MY_BIRTHDAY, null);

        super.onStart();

        if (DailyHotel.isLogin() == false)
        {
            lockUI();
            showLoginDialog();
        } else
        {
            setResult(RESULT_OK);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
            {
                String birthday = mBirthdayEditText.getText().toString();

                if (Util.isTextEmpty(birthday) == true)
                {
                    DailyToast.showToast(EditProfileBirthdayActivity.this, R.string.act_profile_input_birthday, Toast.LENGTH_SHORT);
                    return;
                }

                if (lockUiComponentAndIsLockUiComponent() == true)
                {
                    return;
                }

                lockUI();

                birthday = (String) mBirthdayEditText.getTag();

                if (Util.isTextEmpty(birthday) == true)
                {
                    DailyToast.showToast(EditProfileBirthdayActivity.this, R.string.act_profile_input_birthday, Toast.LENGTH_SHORT);
                    return;
                }

                if (Constants.DAILY_USER.equalsIgnoreCase(DailyPreference.getInstance(EditProfileBirthdayActivity.this).getUserType()) == true)
                {
                    Map<String, String> params = Collections.singletonMap("birthday", birthday);
                    DailyMobileAPI.getInstance(this).requestUserInformationUpdate(mNetworkTag, params, mDailyUserUpdateCallback);
                } else
                {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_idx", mUserIndex);
                    params.put("birthday", birthday);

                    DailyMobileAPI.getInstance(this).requestUserUpdateInformationForSocial(mNetworkTag, params, mSocialUserUpdateCallback);
                }
                break;
            }

            case R.id.birthdayEditText:
                onFocusChange(mBirthdayEditText, true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        unLockUI();

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case CODE_REQUEST_ACTIVITY_LOGIN:
            {
                setResult(resultCode);

                if (resultCode == RESULT_OK)
                {
                    setBirthdayText(DailyPreference.getInstance(this).getUserBirthday());
                } else
                {
                    finish();
                }
                break;
            }
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
            case R.id.birthdayEditText:
            {
                if (DailyHotel.isLogin() == false)
                {
                    return;
                }

                setFocusLabelView(mBirthdayView, mBirthdayEditText, hasFocus);

                if (hasFocus == true)
                {
                    String birthday = (String) mBirthdayEditText.getTag();

                    if (Util.isTextEmpty(birthday) == false)
                    {
                        try
                        {
                            Date date = DailyCalendar.convertDate(birthday, DailyCalendar.ISO_8601_FORMAT);
                            Calendar calendar = DailyCalendar.getInstance();
                            calendar.setTime(date);
                            showBirthdayDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        } catch (Exception e)
                        {
                            showBirthdayDatePicker(-1, -1, -1);
                        }
                    } else
                    {
                        showBirthdayDatePicker(-1, -1, -1);
                    }
                }
                break;
            }
        }
    }

    private void showLoginDialog()
    {
        // 로그인 필요
        View.OnClickListener positiveListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                lockUI();
                startLogin();
            }
        };

        View.OnClickListener negativeListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditProfileBirthdayActivity.this.finish();
            }
        };

        String title = this.getResources().getString(R.string.dialog_notice2);
        String message = this.getResources().getString(R.string.dialog_message_profile_birthday_login);
        String positive = this.getResources().getString(R.string.dialog_btn_text_yes);
        String negative = this.getResources().getString(R.string.dialog_btn_text_no);

        showSimpleDialog(title, message, positive, negative, positiveListener, negativeListener, new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                EditProfileBirthdayActivity.this.finish();
            }
        }, null, true);
    }

    void startLogin()
    {
        Intent intent = LoginActivity.newInstance(this);
        startActivityForResult(intent, CODE_REQUEST_ACTIVITY_LOGIN);
    }

    void setBirthdayText(int year, int month, int dayOfMonth)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);

        mBirthdayEditText.setText(String.format("%4d.%02d.%02d", year, month + 1, dayOfMonth));
        mBirthdayEditText.setTag(DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT));
    }

    private void setBirthdayText(String birthday)
    {
        try
        {
            mBirthdayEditText.setText(DailyCalendar.convertDateFormatString(birthday, DailyCalendar.ISO_8601_FORMAT, "yyyy.MM.dd"));
            mBirthdayEditText.setTag(birthday);
        } catch (Exception e)
        {
            ExLog.d(e.toString());

            mBirthdayEditText.setText(null);
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

    private void showBirthdayDatePicker(int year, int month, int day)
    {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_birthday_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

        if (year < 0 || month < 0 || day < 0)
        {
            year = 2000;
            month = 0;
            day = 1;
        }

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {

            }
        });

        datePicker.setMaxDate(DailyCalendar.getInstance().getTimeInMillis());

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setText("생일 선택");

        // 버튼
        View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
        View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

        TextView negativeTextView = (TextView) twoButtonLayout.findViewById(R.id.negativeTextView);
        TextView positiveTextView = (TextView) twoButtonLayout.findViewById(R.id.positiveTextView);

        negativeTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        positiveTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog != null && dialog.isShowing())
                {
                    dialog.dismiss();
                }

                setBirthdayText(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
            }
        });

        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
            }
        });

        // 생일 화면 부터는 키패드를 나오지 않게 한다.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = Util.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Listener
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private retrofit2.Callback mDailyUserUpdateCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    int msgCode = responseJSONObject.getInt("msgCode");

                    if (msgCode == 100)
                    {
                        showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_birthday), getString(R.string.dialog_btn_text_confirm), new OnClickListener()
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

                        String birthday = (String) mBirthdayEditText.getTag();

                        AnalyticsManager.getInstance(EditProfileBirthdayActivity.this).setUserBirthday(birthday);

                        // 생일을 입력한 경우 체크
                        AnalyticsManager.getInstance(EditProfileBirthdayActivity.this).recordEvent(AnalyticsManager.Category.SET_MY_BIRTHDAY//
                            , AnalyticsManager.Action.REGISTRATION_CLICKED, birthday, null);
                    } else
                    {
                        onErrorPopupMessage(msgCode, responseJSONObject.getString("msg"), null);
                    }
                } catch (Exception e)
                {
                    onError(e);
                } finally
                {
                    unLockUI();
                }
            } else
            {
                EditProfileBirthdayActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            EditProfileBirthdayActivity.this.onError(t);
        }
    };

    private retrofit2.Callback mSocialUserUpdateCallback = new retrofit2.Callback<JSONObject>()
    {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response)
        {
            if (response != null && response.isSuccessful() && response.body() != null)
            {
                try
                {
                    JSONObject responseJSONObject = response.body();

                    JSONObject dataJSONObject = responseJSONObject.getJSONObject("data");

                    boolean result = dataJSONObject.getBoolean("is_success");

                    // TODO :  추후에 msgCode결과를 가지고 구분하는 코드가 필요할듯.
                    int msgCode = responseJSONObject.getInt("msg_code");

                    if (result == true)
                    {
                        showSimpleDialog(null, getString(R.string.toast_msg_profile_success_edit_birthday), getString(R.string.dialog_btn_text_confirm), new OnClickListener()
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

                        String birthday = (String) mBirthdayEditText.getTag();

                        AnalyticsManager.getInstance(EditProfileBirthdayActivity.this).setUserBirthday(birthday);

                        // 생일을 입력한 경우 체크
                        AnalyticsManager.getInstance(EditProfileBirthdayActivity.this).recordEvent(AnalyticsManager.Category.SET_MY_BIRTHDAY//
                            , AnalyticsManager.Action.REGISTRATION_CLICKED, birthday, null);
                    } else
                    {
                        String message = responseJSONObject.getString("msg");
                        showSimpleDialog(null, message, getString(R.string.dialog_btn_text_confirm), new OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                mBirthdayEditText.setText(null);
                            }
                        }, new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                mBirthdayEditText.setText(null);
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
            } else
            {
                EditProfileBirthdayActivity.this.onErrorResponse(call, response);
            }
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t)
        {
            EditProfileBirthdayActivity.this.onError(t);
        }
    };
}
