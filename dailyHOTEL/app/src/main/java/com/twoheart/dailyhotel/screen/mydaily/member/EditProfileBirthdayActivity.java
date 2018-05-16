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

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.CommonDateTime;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.CommonRemoteImpl;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.DailyHotel;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class EditProfileBirthdayActivity extends BaseActivity implements OnClickListener, View.OnFocusChangeListener
{
    private static final String INTENT_EXTRA_DATA_BIRTHDAY = "birthday";

    private CommonRemoteImpl mCommonRemoteImpl;
    private ProfileRemoteImpl mProfileRemoteImpl;

    DailyEditText mBirthdayEditText;
    View mConfirmView, mBirthdayView;

    public static Intent newInstance(Context context, String birthday)
    {
        Intent intent = new Intent(context, EditProfileBirthdayActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_BIRTHDAY, birthday);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_birthday);

        mCommonRemoteImpl = new CommonRemoteImpl();
        mProfileRemoteImpl = new ProfileRemoteImpl();

        Intent intent = getIntent();
        String birthday = intent.getStringExtra(INTENT_EXTRA_DATA_BIRTHDAY);

        initToolbar();
        initLayout(birthday);
    }

    private void initToolbar()
    {
        DailyToolbarView dailyToolbarView = findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_edit_birthday);
        dailyToolbarView.setOnBackClickListener(new OnClickListener()
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

        mBirthdayEditText = findViewById(R.id.birthdayEditText);
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

        if (DailyTextUtils.isTextEmpty(birthday) == true)
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

        if (DailyTextUtils.isTextEmpty(birthday) == true)
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

                if (DailyTextUtils.isTextEmpty(birthday) == true)
                {
                    DailyToast.showToast(EditProfileBirthdayActivity.this, R.string.act_profile_input_birthday, Toast.LENGTH_SHORT);
                    return;
                }

                onChangeBirthday(birthday);
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
                    setBirthdayText(DailyUserPreference.getInstance(this).getBirthday());
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

                    if (DailyTextUtils.isTextEmpty(birthday) == false)
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

        mBirthdayEditText.setText(String.format(Locale.KOREA, "%4d.%02d.%02d", year, month + 1, dayOfMonth));
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
        if (lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        addCompositeDisposable(mCommonRemoteImpl.getCommonDateTime().observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<CommonDateTime>()
        {
            @Override
            public void accept(CommonDateTime commonDateTime) throws Exception
            {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = layoutInflater.inflate(R.layout.view_dialog_birthday_layout, null, false);

                final Dialog dialog = new Dialog(EditProfileBirthdayActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setCanceledOnTouchOutside(false);

                final DatePicker datePicker = dialogView.findViewById(R.id.datePicker);

                Calendar calendar = DailyCalendar.getInstance();
                calendar.setTime(DailyCalendar.convertDate(commonDateTime.currentDateTime, DailyCalendar.ISO_8601_FORMAT));

                int startYear, startMonth, startDay;

                // 14셈 미만 가입 금지
                calendar.add(Calendar.YEAR, -14);
                datePicker.setMaxDate(calendar.getTimeInMillis());

                if (year < 0 || month < 0 || day < 0)
                {
                    startYear = 2000;
                    startMonth = 0;
                    startDay = 1;
                } else
                {
                    startYear = year;
                    startMonth = month;
                    startDay = day;
                }

                datePicker.init(startYear, startMonth, startDay, new DatePicker.OnDateChangedListener()
                {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {

                    }
                });

                // 상단
                TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
                titleTextView.setVisibility(View.VISIBLE);
                titleTextView.setText(R.string.label_sign_up_select_birthday);

                // 버튼
                View buttonLayout = dialogView.findViewById(R.id.buttonLayout);
                View twoButtonLayout = buttonLayout.findViewById(R.id.twoButtonLayout);

                TextView negativeTextView = twoButtonLayout.findViewById(R.id.negativeTextView);
                TextView positiveTextView = twoButtonLayout.findViewById(R.id.positiveTextView);

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

                    WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(EditProfileBirthdayActivity.this, dialog);

                    dialog.show();

                    dialog.getWindow().setAttributes(layoutParams);
                } catch (Exception e)
                {
                    ExLog.d(e.toString());
                }

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

    private void onChangeBirthday(String birthday)
    {
        if (DailyTextUtils.isTextEmpty(birthday) == true || lockUiComponentAndIsLockUiComponent() == true)
        {
            return;
        }

        lockUI();

        String birthdayDateTime;

        try
        {
            birthdayDateTime = DailyCalendar.convertDateFormatString(birthday, "yyyy.MM.dd", DailyCalendar.ISO_8601_FORMAT);
        } catch (Exception e)
        {
            unLockUI();
            return;
        }

        addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(Collections.singletonMap("birthday", birthdayDateTime)).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override
            public void accept(User user) throws Exception
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
