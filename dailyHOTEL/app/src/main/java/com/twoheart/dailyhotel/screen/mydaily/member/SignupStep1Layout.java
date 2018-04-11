package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyAutoCompleteEditText;
import com.daily.base.widget.DailyEditText;
import com.daily.base.widget.DailyToast;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.Constants;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.StringFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SignupStep1Layout extends BaseLayout implements OnClickListener, View.OnFocusChangeListener
{
    private static final int MAX_OF_RECOMMENDER = 45;

    private View mEmailView, mNameView, mBirthdayView, mPasswordView, mConfirmPasswordView;
    private DailyAutoCompleteEditText mEmailEditText;
    DailyEditText mNameEditText, mPasswordEditText;
    private DailyEditText mBirthdayEditText, mConfirmPasswordEditText;
    private TextView mSignupBalloonsTextView;
    private CheckBox mAllAgreementCheckBox;
    private CheckBox mFourteenCheckBox;
    private CheckBox mTermsOfServiceCheckBox;
    private CheckBox mTermsOfPrivacyCheckBox;
    private CheckBox mBenefitCheckBox;
    private CheckBox mYearCheckBox1;
    private CheckBox mYearCheckBox3;
    private CheckBox mYearCheckBox5;
    ScrollView mScrollView;

    public interface OnEventListener extends OnBaseEventListener
    {
        void onValidation(String email, String name, String password1, String confirmPassword //
            , String recommender, String birthday, boolean isBenefit, int privacyValidMonth);

        void showTermOfService();

        void showTermOfPrivacy();

        void showBirthdayDatePicker(int year, int month, int day);

        void onBenefitClick(boolean checked);
    }

    public SignupStep1Layout(Context context, OnEventListener mOnEventListener)
    {
        super(context, mOnEventListener);
    }

    @Override
    protected void initLayout(View view)
    {
        initToolbar(view);
        initLayoutForm(view);
        initLayoutCheckBox(view);
    }

    private void initToolbar(View view)
    {
        DailyToolbarView dailyToolbarView = view.findViewById(R.id.toolbarView);
        dailyToolbarView.setTitleText(R.string.actionbar_title_signup_1_activity);
        dailyToolbarView.setOnBackClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOnEventListener.finish();
            }
        });
    }

    private void initLayoutForm(View view)
    {
        mScrollView = view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(mScrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mSignupBalloonsTextView = view.findViewById(R.id.signupBalloonsTextView);

        mEmailView = view.findViewById(R.id.emailView);
        mEmailEditText = view.findViewById(R.id.emailEditText);
        mEmailEditText.setDeleteButtonVisible(null);
        mEmailEditText.setOnFocusChangeListener(this);

        EmailCompleteAdapter emailCompleteAdapter;

        if (Constants.DEBUG == true)
        {
            List<String> emailList = new ArrayList(Arrays.asList(mContext.getResources().getStringArray(R.array.company_email_postfix_array)));
            emailList.add("@dailyhotel.com");

            emailCompleteAdapter = new EmailCompleteAdapter(mContext, emailList);
        } else
        {
            emailCompleteAdapter = new EmailCompleteAdapter(mContext, Arrays.asList(mContext.getResources().getStringArray(R.array.company_email_postfix_array)));
        }

        mEmailEditText.setAdapter(emailCompleteAdapter);

        mNameView = view.findViewById(R.id.nameView);
        mNameEditText = view.findViewById(R.id.nameEditText);
        mNameEditText.setDeleteButtonVisible(null);
        mNameEditText.setOnFocusChangeListener(this);

        mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    mNameEditText.requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        mPasswordView = view.findViewById(R.id.passwordView);
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        mPasswordEditText.setDeleteButtonVisible(null);
        mPasswordEditText.setOnFocusChangeListener(this);

        StringFilter stringFilter1 = new StringFilter(mContext);
        InputFilter[] allowPassword1 = new InputFilter[2];
        allowPassword1[0] = stringFilter1.allowPassword;
        allowPassword1[1] = new InputFilter.LengthFilter(mContext.getResources().getInteger(R.integer.max_password) + 1);

        mPasswordEditText.setFilters(allowPassword1);

        mPasswordEditText.addTextChangedListener(new TextWatcher()
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
                if (s.length() > mContext.getResources().getInteger(R.integer.max_password))
                {
                    s.delete(s.length() - 1, s.length());

                    DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_wrong_max_password_length), Toast.LENGTH_SHORT);
                }
            }
        });

        mConfirmPasswordView = view.findViewById(R.id.confirmPasswordView);
        mConfirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        mConfirmPasswordEditText.setDeleteButtonVisible(null);
        mConfirmPasswordEditText.setOnFocusChangeListener(this);

        StringFilter stringFilter2 = new StringFilter(mContext);
        InputFilter[] allowPassword2 = new InputFilter[2];
        allowPassword2[0] = stringFilter2.allowPassword;
        allowPassword2[1] = new InputFilter.LengthFilter(mContext.getResources().getInteger(R.integer.max_password) + 1);

        mConfirmPasswordEditText.setFilters(allowPassword2);

        mConfirmPasswordEditText.addTextChangedListener(new TextWatcher()
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
                if (s.length() > mContext.getResources().getInteger(R.integer.max_password))
                {
                    s.delete(s.length() - 1, s.length());

                    DailyToast.showToast(mContext, mContext.getString(R.string.toast_msg_wrong_max_password_length), Toast.LENGTH_SHORT);
                }
            }
        });

        mBirthdayView = view.findViewById(R.id.birthdayView);
        mBirthdayEditText = view.findViewById(R.id.birthdayEditText);
        mBirthdayEditText.setDeleteButtonVisible(new DailyEditText.OnDeleteTextClickListener()
        {
            @Override
            public void onDelete(DailyEditText dailyEditText)
            {
                dailyEditText.setTag(null);
            }
        });
        mBirthdayEditText.setOnFocusChangeListener(this);
        mBirthdayEditText.setKeyListener(null);
        mBirthdayEditText.setOnClickListener(this);

        // 회원 가입시 이름 필터 적용.
        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowAlphanumericHangul = new InputFilter[2];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
        allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

        mNameEditText.setFilters(allowAlphanumericHangul);

        View nextStepView = view.findViewById(R.id.nextStepView);
        nextStepView.setOnClickListener(this);

        mEmailView.requestFocus();
    }

    private void initLayoutCheckBox(View view)
    {
        mAllAgreementCheckBox = view.findViewById(R.id.allAgreementCheckBox);
        mFourteenCheckBox = view.findViewById(R.id.fourteenCheckBox);
        mTermsOfPrivacyCheckBox = view.findViewById(R.id.personalCheckBox);
        mTermsOfServiceCheckBox = view.findViewById(R.id.termsCheckBox);
        mBenefitCheckBox = view.findViewById(R.id.benefitCheckBox);

        mYearCheckBox1 = view.findViewById(R.id.yearCheckBox1);
        mYearCheckBox3 = view.findViewById(R.id.yearCheckBox3);
        mYearCheckBox5 = view.findViewById(R.id.yearCheckBox5);

        if (VersionUtils.isOverAPI21() == false)
        {
            mAllAgreementCheckBox.setBackgroundResource(0);
            mFourteenCheckBox.setBackgroundResource(0);
            mTermsOfPrivacyCheckBox.setBackgroundResource(0);
            mTermsOfServiceCheckBox.setBackgroundResource(0);
            mBenefitCheckBox.setBackgroundResource(0);

            mYearCheckBox1.setBackgroundResource(0);
            mYearCheckBox3.setBackgroundResource(0);
            mYearCheckBox5.setBackgroundResource(0);
        }

        mAllAgreementCheckBox.setOnClickListener(this);
        mFourteenCheckBox.setOnClickListener(this);
        mTermsOfPrivacyCheckBox.setOnClickListener(this);
        mTermsOfServiceCheckBox.setOnClickListener(this);
        mBenefitCheckBox.setOnClickListener(this);

        mYearCheckBox1.setVisibility(View.GONE);

        mYearCheckBox1.setOnClickListener(this);
        mYearCheckBox3.setOnClickListener(this);
        mYearCheckBox5.setOnClickListener(this);

        TextView termsContentView = view.findViewById(R.id.termsContentView);
        termsContentView.setPaintFlags(termsContentView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsContentView.setOnClickListener(this);

        TextView personalContentView = view.findViewById(R.id.personalContentView);
        personalContentView.setPaintFlags(personalContentView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        personalContentView.setOnClickListener(this);
    }

    public boolean isCheckedFourteen()
    {
        return mFourteenCheckBox.isChecked();
    }

    public boolean isCheckedTermsOfService()
    {
        return mTermsOfServiceCheckBox.isChecked();
    }

    public boolean isCheckedTermsOfPrivacy()
    {
        return mTermsOfPrivacyCheckBox.isChecked();
    }

    public boolean isCheckedBenefit()
    {
        return mBenefitCheckBox.isChecked();
    }

    public void setBirthdayText(int year, int month, int dayOfMonth)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);

        mBirthdayEditText.setText(String.format(Locale.KOREA, "%4d.%02d.%02d", year, month + 1, dayOfMonth));
        mBirthdayEditText.setTag(calendar);
    }

    public void signUpBalloonsTextView(String text)
    {
        if (mSignupBalloonsTextView == null)
        {
            return;
        }

        mSignupBalloonsTextView.setText(text);
    }

    public void requestPasswordFocus()
    {
        if (mScrollView == null || mPasswordEditText == null)
        {
            return;
        }

        mScrollView.fullScroll(View.FOCUS_UP);
        mPasswordEditText.requestFocus();
    }

    private void nextStep()
    {
        String emailText = mEmailEditText.getText().toString().trim();
        String nameText = mNameEditText.getText().toString().trim();
        // 패스워드는 trim하지 않는다.
        String passwordText = mPasswordEditText.getText().toString();
        String confirmPasswordText = mConfirmPasswordEditText.getText().toString();

        // 생일
        String birthday = mBirthdayEditText.getText().toString().trim();

        if (DailyTextUtils.isTextEmpty(birthday) == false)
        {
            Calendar calendar = (Calendar) mBirthdayEditText.getTag();

            if (calendar == null)
            {
                birthday = null;
            } else
            {
                birthday = DailyCalendar.format(calendar.getTime(), DailyCalendar.ISO_8601_FORMAT);
            }
        }

        int month;
        if (mYearCheckBox5.isChecked() == true)
        {
            month = 60;
        } else if (mYearCheckBox3.isChecked() == true)
        {
            month = 36;
        } else
        {
            month = 12;
        }

        ((OnEventListener) mOnEventListener).onValidation(emailText, nameText, passwordText //
            , confirmPasswordText, null, birthday, mBenefitCheckBox.isChecked(), month);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.nextStepView:
                nextStep();
                break;

            case R.id.termsContentView:
                ((OnEventListener) mOnEventListener).showTermOfService();
                break;

            case R.id.personalContentView:
                ((OnEventListener) mOnEventListener).showTermOfPrivacy();
                break;

            case R.id.allAgreementCheckBox:
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                boolean isChecked = mAllAgreementCheckBox.isChecked();

                mFourteenCheckBox.setChecked(isChecked);
                mTermsOfServiceCheckBox.setChecked(isChecked);
                mTermsOfPrivacyCheckBox.setChecked(isChecked);

                if (isChecked != mBenefitCheckBox.isChecked())
                {
                    ((OnEventListener) mOnEventListener).onBenefitClick(isChecked == false);
                }

                mBenefitCheckBox.setChecked(isChecked);
                break;
            }

            case R.id.fourteenCheckBox:
            case R.id.personalCheckBox:
            case R.id.termsCheckBox:
            case R.id.benefitCheckBox:
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (mFourteenCheckBox.isChecked() == true && mTermsOfPrivacyCheckBox.isChecked() == true//
                    && mTermsOfServiceCheckBox.isChecked() == true && mBenefitCheckBox.isChecked() == true)
                {
                    mAllAgreementCheckBox.setChecked(true);
                } else
                {
                    mAllAgreementCheckBox.setChecked(false);
                }

                if (v.getId() == R.id.benefitCheckBox)
                {
                    ((OnEventListener) mOnEventListener).onBenefitClick(mBenefitCheckBox.isChecked());
                }
                break;
            }

            case R.id.birthdayEditText:
                onFocusChange(mBirthdayEditText, true);
                break;

            case R.id.yearCheckBox1:
            case R.id.yearCheckBox3:
            case R.id.yearCheckBox5:
            {
                setYearCheckBoxUnChecked(v.getId());
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        switch (v.getId())
        {
            case R.id.emailEditText:
                setFocusLabelView(mEmailView, mEmailEditText, hasFocus);
                break;

            case R.id.nameEditText:
                setFocusLabelView(mNameView, mNameEditText, hasFocus);
                break;

            case R.id.passwordEditText:
                setFocusLabelView(mPasswordView, mPasswordEditText, hasFocus);
                break;

            case R.id.confirmPasswordEditText:
                setFocusLabelView(mConfirmPasswordView, mConfirmPasswordEditText, hasFocus);
                break;

            case R.id.birthdayEditText:
                setFocusLabelView(mBirthdayView, mBirthdayEditText, hasFocus);

                if (hasFocus == true)
                {
                    Calendar calendar = (Calendar) mBirthdayEditText.getTag();

                    if (calendar == null)
                    {
                        ((OnEventListener) mOnEventListener).showBirthdayDatePicker(-1, -1, -1);
                    } else
                    {
                        ((OnEventListener) mOnEventListener).showBirthdayDatePicker(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                    }
                }
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

    private void setYearCheckBoxUnChecked(int checkBoxId)
    {
        switch (checkBoxId)
        {
            case R.id.yearCheckBox5:
                mYearCheckBox1.setChecked(false);
                mYearCheckBox3.setChecked(false);
                break;

            case R.id.yearCheckBox3:
                mYearCheckBox1.setChecked(false);
                mYearCheckBox5.setChecked(false);
                break;

            case R.id.yearCheckBox1:
            default:
                mYearCheckBox3.setChecked(false);
                mYearCheckBox5.setChecked(false);
                break;
        }
    }
}
