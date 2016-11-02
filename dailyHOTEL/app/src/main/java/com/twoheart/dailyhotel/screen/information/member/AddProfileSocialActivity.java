package com.twoheart.dailyhotel.screen.information.member;

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

import com.android.volley.VolleyError;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.model.Customer;
import com.twoheart.dailyhotel.place.base.BaseActivity;
import com.twoheart.dailyhotel.screen.information.terms.PrivacyActivity;
import com.twoheart.dailyhotel.screen.information.terms.TermActivity;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.ExLog;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.util.analytics.AnalyticsManager;
import com.twoheart.dailyhotel.widget.DailyToast;

public class AddProfileSocialActivity extends BaseActivity
{
    private static final int REQUEST_CODE_COUNTRYCODE_LIST_ACTIVITY = 1;
    private static final int REQUEST_CODE_ACTIVITY = 100;

    private String mUserIdx;
    private String mCountryCode;

    private Customer mCustomer;
    private AddProfileSocialLayout mAddProfileSocialLayout;
    private AddProfileSocialNetworkController mAddProfileSocialNetworkController;

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
        mAddProfileSocialNetworkController = new AddProfileSocialNetworkController(this, mNetworkTag, mOnNetworkControllerListener);

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

        String birthday = intent.getStringExtra(NAME_INTENT_EXTRA_DATA_BIRTHDAY);
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

        mUserIdx = mCustomer.getUserIdx();

        if (Util.isValidatePhoneNumber(mCustomer.getPhone()) == false)
        {
            mAddProfileSocialLayout.showPhoneLayout();

            mCountryCode = Util.getCountryNameNCode(this);
            mAddProfileSocialLayout.setCountryCode(mCountryCode);
        } else
        {
            mAddProfileSocialLayout.hidePhoneLayout();
        }

        if (Util.isTextEmpty(mCustomer.getEmail()) == true)
        {
            mAddProfileSocialLayout.showEmailLayout();
        } else
        {
            mAddProfileSocialLayout.hideEmailLayout();
        }

        mAddProfileSocialLayout.showNameLayout();
        mAddProfileSocialLayout.setNameText(mCustomer.getName());

        if (hasBirthday == true)
        {
            mAddProfileSocialLayout.hideBirthdayLayout();
        } else
        {
            mAddProfileSocialLayout.showBirthdaylLayout();
        }

        showSimpleDialog(getString(R.string.dialog_notice2), getString(R.string.dialog_msg_facebook_update), getString(R.string.dialog_btn_text_confirm), null, null, null);
    }

    @Override
    protected void onStart()
    {
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordScreen(AnalyticsManager.Screen.BOOKING_ACCOUNTDETAIL);

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
        AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

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

    private void showCompletedSignupDialog(boolean isBenefit, String updateDate)
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
        titleTextView.setText(DailyPreference.getInstance(this).getRemoteConfigTextSignUpText02());

        // 메시지
        TextView messageTextView = (TextView) dialogView.findViewById(R.id.messageTextView);

        try
        {
            updateDate = DailyCalendar.convertDateFormatString(updateDate, DailyCalendar.ISO_8601_FORMAT, "yyyy년 MM월 dd일");
        } catch (Exception e)
        {
            updateDate = null;
        }

        if (isBenefit == true && Util.isTextEmpty(updateDate) == false)
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
                if (dialog != null && dialog.isShowing())
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
            dialog.show();
        } catch (Exception e)
        {
            ExLog.d(e.toString());
        }
    }

    private AddProfileSocialLayout.OnEventListener mOnEventListener = new AddProfileSocialLayout.OnEventListener()
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

            Intent intent = new Intent(AddProfileSocialActivity.this, PrivacyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
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
        public void showBirthdayDatePicker()
        {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dialogView = layoutInflater.inflate(R.layout.view_dialog_birthday_layout, null, false);

            final Dialog dialog = new Dialog(AddProfileSocialActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

            datePicker.init(2000, 0, 1, new DatePicker.OnDateChangedListener()
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
                dialog.show();
            } catch (Exception e)
            {
                ExLog.d(e.toString());
            }
        }

        @Override
        public void onUpdateUserInformation(String phoneNumber, String email, String name, String recommender, String birthday, boolean isBenefit)
        {
            // 전화번호가 없거나 잘못 된경우
            if (Util.isTextEmpty(mCustomer.getPhone()) == true || Util.isValidatePhoneNumber(mCustomer.getPhone()) == false)
            {
                if (Util.isTextEmpty(phoneNumber) == true)
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
            if (Util.isTextEmpty(mCustomer.getEmail()) == true)
            {
                if (Util.isTextEmpty(email) == true)
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
            if (Util.isTextEmpty(mCustomer.getName()) == true)
            {
                if (Util.isTextEmpty(name) == true)
                {
                    DailyToast.showToast(AddProfileSocialActivity.this, R.string.toast_msg_please_input_name, Toast.LENGTH_SHORT);
                    return;
                }
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

            mAddProfileSocialNetworkController.requestUpdateSocialUserInformation(mUserIdx, phoneNumber, email, name, recommender, birthday, isBenefit);
        }

        @Override
        public void finish()
        {
            AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "BackButton", null);

            AddProfileSocialActivity.this.finish();
        }
    };

    private AddProfileSocialNetworkController.OnNetworkControllerListener mOnNetworkControllerListener = new AddProfileSocialNetworkController.OnNetworkControllerListener()
    {
        @Override
        public void onUpdateSocialUserInformation(String message, String agreedDate)
        {
            if (Util.isTextEmpty(message) == true)
            {
                boolean isBenefit = mAddProfileSocialLayout.isCheckedBenefit();

                DailyPreference.getInstance(AddProfileSocialActivity.this).setUserBenefitAlarm(isBenefit);
                AnalyticsManager.getInstance(AddProfileSocialActivity.this).setPushEnabled(isBenefit, AnalyticsManager.ValueType.OTHER);

                AnalyticsManager.getInstance(AddProfileSocialActivity.this).recordEvent(AnalyticsManager.Category.NAVIGATION, AnalyticsManager.Action.ACCOUNT_DETAIL, "Confirm", null);

                showCompletedSignupDialog(isBenefit, agreedDate);
            } else
            {
                DailyToast.showToast(AddProfileSocialActivity.this, message, Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onErrorResponse(VolleyError volleyError)
        {
            AddProfileSocialActivity.this.onErrorResponse(volleyError);
        }

        @Override
        public void onError(Exception e)
        {
            AddProfileSocialActivity.this.onError(e);
        }

        @Override
        public void onErrorPopupMessage(int msgCode, String message)
        {
            AddProfileSocialActivity.this.onErrorPopupMessage(msgCode, message);
        }

        @Override
        public void onErrorToastMessage(String message)
        {
            AddProfileSocialActivity.this.onErrorToastMessage(message);
        }
    };
}
