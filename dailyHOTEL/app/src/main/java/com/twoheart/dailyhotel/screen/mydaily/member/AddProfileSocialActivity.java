package com.twoheart.dailyhotel.screen.mydaily.member;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.ExLog;
import com.daily.base.util.ScreenUtils;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.entity.User;
import com.daily.dailyhotel.repository.remote.ProfileRemoteImpl;
import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.CollectPersonInformationActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

public class AddProfileSocialActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;
    private static final int REQUEST_CODE_ACTIVITY = 100;

    String mCountryCode;

    Customer mCustomer;
    AddProfileSocialLayout mAddProfileSocialLayout;
    ProfileRemoteImpl mProfileRemoteImpl;

    public static Intent newInstance(Context context, Customer customer, String birthday)
    {
        Intent intent = new Intent(context, AddProfileSocialActivity.class);

        intent.putExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER, customer);
        intent.putExtra(NAME_INTENT_EXTRA_DATA_BIRTHDAY, birthday);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);

        mAddProfileSocialLayout = new AddProfileSocialLayout(this, mOnEventListener);
        mProfileRemoteImpl = new ProfileRemoteImpl(this);

        setContentView(mAddProfileSocialLayout.onCreateView(R.layout.activity_add_profile_social));

        initUserInformation(getIntent());
    }

    private void initUserInformation(Intent intent)
    {
        if (intent == null)
        {
            return;
        }

        mCustomer = intent.getParcelableExtra(NAME_INTENT_EXTRA_DATA_CUSTOMER);

        if (mCustomer == null)
        {
            lockUI();

            addCompositeDisposable(mProfileRemoteImpl.getProfile() //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
                {
                    @Override
                    public void accept(User user) throws Exception
                    {
                        Customer customer = new Customer();
                        customer.setEmail(user.email);
                        customer.setName(user.name);
                        customer.setPhone(user.phone);
                        customer.setUserIdx(Integer.toString(user.index));

                        onUserProfile(customer, user.birthday);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onUserProfile(null, null);
                    }
                }));
            return;
        }

        String birthday = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_BIRTHDAY);

        initUserInformation(mCustomer, birthday);
    }

    void initUserInformation(Customer customer, String birthday)
    {
        if (customer == null)
        {
            return;
        }

        mCustomer = customer;
        boolean hasBirthday = false;

        try
        {
            if (DailyCalendar.convertDate(birthday, DailyCalendar.ISO_8601_FORMAT) != null)
            {
                hasBirthday = true;
            }
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }

        if (Util.isValidatePhoneNumber(customer.getPhone()) == false)
        {
            mAddProfileSocialLayout.showPhoneLayout();

            mCountryCode = Util.getCountryNameNCode(this);
            mAddProfileSocialLayout.setCountryCode(mCountryCode);
        } else
        {
            mAddProfileSocialLayout.hidePhoneLayout();
        }

        if (DailyTextUtils.isTextEmpty(customer.getEmail()) == true)
        {
            mAddProfileSocialLayout.showEmailLayout();
        } else
        {
            mAddProfileSocialLayout.hideEmailLayout();
        }

        mAddProfileSocialLayout.showNameLayout();
        mAddProfileSocialLayout.setNameText(customer.getName());

        if (hasBirthday == true)
        {
            mAddProfileSocialLayout.hideBirthdayLayout();
        } else
        {
            mAddProfileSocialLayout.showBirthdayLayout();
        }

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_facebook_update), getString(R.string.dialog_btn_text_confirm), null, null, null);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordScreen(this, AnalyticsManager.Screen.BOOKING_ACCOUNTDETAIL, null);

        super.onStart();
    }

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed()
    {
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

        super.onBackPressed();
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

                mAddProfileSocialLayout.setCountryCode(mCountryCode);
            }
        }
    }

    void showCompletedSignupDialog(boolean isBenefit, String updateDate)
    {
        if (isFinishing())
        {
            return;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.view_dialog_signup_layout, null, false);

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);

        // 상단
        TextView titleTextView = (TextView) dialogView.findViewById(R.id.titleTextView);
        titleTextView.setText(DailyRemoteConfigPreference.getInstance(this).getRemoteConfigTextSignUpText02());

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);

        try
        {
            updateDate = DailyCalendar.convertDateFormatString(updateDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
        } catch (Exception e)
        {
            updateDate = null;
        }

        if (isBenefit == true && DailyTextUtils.isTextEmpty(updateDate) == false)
        {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(getString(R.string.message_signup_completed_alarm_on_format, updateDate));
        } else
        {
            messageTextView.setVisibility(View.GONE);
        }

        TextView confirmTextView = (TextView) dialogView.findViewById(R.id.confirmTextView);
        confirmTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialog)
            {
                setResult(RESULT_OK);
                finish();
            }
        });

        try
        {
            dialog.setContentView(dialogView);

            WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(this, dialog);

            dialog.show();

            dialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private void onUserProfile(Customer customer, String birthday)
    {
        unLockUI();

        if (customer == null)
        {
            DailyToast.showToast(AddProfileSocialActivity.this, getResources().getString(R.string.act_base_network_connect), Toast.LENGTH_LONG);
            finish();
            return;
        }

        initUserInformation(customer, birthday);
    }

    private void onUpdateSocialUserInformation(boolean isSuccess, String agreedDate)
    {
        if (isSuccess == true)
        {
            boolean isBenefit = mAddProfileSocialLayout.isCheckedBenefit();

            DailyUserPreference.getInstance(AddProfileSocialActivity.this).setBenefitAlarm(isBenefit);
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).setPushEnabled(isBenefit, AnalyticsManager.ValueType.OTHER);

            AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.ACCOUNT_DETAIL, "Confirm", null);

            showCompletedSignupDialog(isBenefit, agreedDate);
        } else
        {
            DailyToast.showToast(this, getString(R.string.act_base_network_connect), DailyToast.LENGTH_LONG);
        }

        Calendar calendar = mAddProfileSocialLayout.getBirthday();

        if (calendar != null)
        {
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).setUserBirthday(DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT));
        } else
        {
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).setUserBirthday(null);
        }

        AnalyticsManager.getInstance(AddProfileSocialActivity.this).setUserName(mAddProfileSocialLayout.getName());

        int year = mAddProfileSocialLayout.getPrivacyYear();
        String label = year > 1 ? year + "yrs" : "yr";

        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.REGISTRATION //
            , AnalyticsManager.Action.PRIVACY, label, null);
    }

    final private AddProfileSocialLayout.OnEventListener mOnEventListener = new AddProfileSocialLayout.OnEventListener()
    {
        @Override
        public void showTermOfService()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = new Intent(AddProfileSocialActivity.this, TermActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void showTermOfPrivacy()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            startActivityForResult(CollectPersonInformationActivity.newInstance(AddProfileSocialActivity.this), REQUEST_CODE_ACTIVITY);
        }

        @Override
        public void showCountryCodeList()
        {
            if (lockUiComponentAndIsLockUiComponent() == true)
            {
                return;
            }

            Intent intent = CountryCodeListActivity.newInstance(AddProfileSocialActivity.this, mCountryCode);
            startActivityForResult(intent, REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY);
        }

        @Override
        public void showBirthdayDatePicker(int year, int month, int day)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.view_dialog_birthday_layout, null, false);

            final Dialog dialog = new Dialog(AddProfileSocialActivity.this);
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
                    if (dialog.isShowing())
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
                    if (dialog.isShowing())
                    {
                        dialog.dismiss();
                    }

                    mAddProfileSocialLayout.setBirthdayText(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
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

                WindowManager.LayoutParams layoutParams = ScreenUtils.getDialogWidthLayoutParams(AddProfileSocialActivity.this, dialog);

                dialog.show();

                dialog.getWindow().setAttributes(layoutParams);
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onUpdateUserInformation(String phoneNumber, String email, String name //
            , String birthday, boolean isBenefit, int month)
        {
            // 전화번호가 없거나 잘못 된경우
            if (DailyTextUtils.isTextEmpty(mCustomer.getPhone()) == true || Util.isValidatePhoneNumber(mCustomer.getPhone()) == false)
            {
                if (DailyTextUtils.isTextEmpty(phoneNumber) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_phone, Toast.LENGTH_SHORT);
                    return;
                }

                if (Util.isValidatePhoneNumber(phoneNumber) == false)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_wrong_phonenumber, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 이메일이 없는 경우
            if (DailyTextUtils.isTextEmpty(mCustomer.getEmail()) == true)
            {
                if (DailyTextUtils.isTextEmpty(email) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_id, Toast.LENGTH_SHORT);
                    return;
                }

                // email 유효성 체크
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() == false)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_wrong_email_address, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 이름이 없는 경우
            if (DailyTextUtils.isTextEmpty(mCustomer.getName()) == true)
            {
                if (DailyTextUtils.isTextEmpty(name) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_name, Toast.LENGTH_SHORT);
                    return;
                }
            }

            // 만 14세 이상
            if (mAddProfileSocialLayout.isCheckedFourteen() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_terms_fourteen, Toast.LENGTH_SHORT);
                return;
            }

            // 동의 체크 확인
            if (mAddProfileSocialLayout.isCheckedTermsOfService() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_terms_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mAddProfileSocialLayout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            if (mAddProfileSocialLayout.isCheckedTermsOfPrivacy() == false)
            {
                DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_personal_agreement, Toast.LENGTH_SHORT);
                return;
            }

            Map<String, String> paramMap = new HashMap<>();
            if (DailyTextUtils.isTextEmpty(email) == false)
            {
                paramMap.put("email", email.trim());
            }

            if (DailyTextUtils.isTextEmpty(name) == false)
            {
                paramMap.put("name", name);
            }

            if (DailyTextUtils.isTextEmpty(phoneNumber) == false)
            {
                paramMap.put("phone", phoneNumber.replaceAll("-", ""));
            }

            if (DailyTextUtils.isTextEmpty(birthday) == false)
            {
                paramMap.put("birthday", birthday);
            }

            if (month < 12)
            {
                month = 12;
            }

            paramMap.put("dataRetentionInMonth", Integer.toString(month));
            paramMap.put("willReceiveBenefitNotifications", Boolean.toString(isBenefit));

            addCompositeDisposable(mProfileRemoteImpl.updateUserInformation(paramMap) //
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
                {
                    @Override
                    public void accept(User user) throws Exception
                    {
                        onUpdateSocialUserInformation(true, user.agreedAt);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        onUpdateSocialUserInformation(false, null);
                    }
                }));
        }

        @Override
        public void finish()
        {
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION_, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

            AddProfileSocialActivity.this.finish();
        }
    };
}
