package com.twoheart.dailyhotel.screen.mydaily.member;

import android.content.Context;
import android.graphics.Paint;
import android.telephony.PhoneNumberFormattingTextWatcher;
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

import com.daily.base.util.DailyTextUtils;
import com.daily.base.util.VersionUtils;
import com.daily.base.widget.DailyAutoCompleteEditText;
import com.daily.base.widget.DailyEditText;
import com.daily.dailyhotel.storage.preference.DailyUserPreference;
import com.daily.dailyhotel.view.DailyToolbarView;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.PhoneNumberKoreaFormattingTextWatcher;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class AddProfileSocialLayout extends BaseLayout implements OnClickListener, View.OnFocusChangeListener
{
    private static final int MAX_OF_RECOMMENDER = 45;

    private View mPhoneLayout, mEmailLayout, mNameLayout;
    private View mEmailView, mNameView, mBirthdayView;
    private DailyAutoCompleteEditText mEmailEditText;
    DailyEditText mNameEditText, mBirthdayEditText;
    private CheckBox mAllAgreementCheckBox;
    private CheckBox mFourteenCheckBox;
    private CheckBox mTermsOfServiceCheckBox;
    private CheckBox mTermsOfPrivacyCheckBox;
    private CheckBox mBenefitCheckBox;
    private CheckBox mYearCheckBox1;
    private CheckBox mYearCheckBox3;
    private CheckBox mYearCheckBox5;

    private View mPhoneView;
    private DailyEditText mCountryEditText, mPhoneEditText;
    private TextWatcher mTextWatcher;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showTermOfService();

        void showTermOfPrivacy();

        void showCountryCodeList();

        void showBirthdayDatePicker(int year, int month, int day);

        void onUpdateUserInformation(String phoneNumber, String email, String name, String birthday, boolean isBenefit, int month);
    }

    public AddProfileSocialLayout(Context context, OnEventListener mOnEventListener)
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
        dailyToolbarView.setTitleText(R.string.actionbar_title_userinfo_update_activity);
        dailyToolbarView.setOnBackClickListener(new OnClickListener()
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
        final ScrollView scrollView = view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mEmailLayout = view.findViewById(R.id.emailLayout);
        mNameLayout = view.findViewById(R.id.nameLayout);

        mCountryEditText = mPhoneLayout.findViewById(R.id.countryEditText);
        mCountryEditText.setFocusable(false);
        mCountryEditText.setCursorVisible(false);
        mCountryEditText.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((OnEventListener) mOnEventListener).showCountryCodeList();
            }
        });

        mPhoneView = mPhoneLayout.findViewById(R.id.phoneView);
        mPhoneEditText = mPhoneLayout.findViewById(R.id.phoneEditText);
        mPhoneEditText.setDeleteButtonVisible(null);
        mPhoneEditText.setOnFocusChangeListener(this);

        View confirmView = view.findViewById(R.id.confirmView);
        confirmView.setOnClickListener(this);

        mEmailView = mEmailLayout.findViewById(R.id.emailView);
        mEmailEditText = mEmailLayout.findViewById(R.id.emailEditText);
        mEmailEditText.setDeleteButtonVisible(null);
        mEmailEditText.setOnFocusChangeListener(this);

        EmailCompleteAdapter emailCompleteAdapter = new EmailCompleteAdapter(mContext, Arrays.asList(mContext.getResources().getStringArray(R.array.company_email_postfix_array)));
        mEmailEditText.setAdapter(emailCompleteAdapter);

        mNameView = mNameLayout.findViewById(R.id.nameView);
        mNameEditText = mNameLayout.findViewById(R.id.nameEditText);
        mNameEditText.setDeleteButtonVisible(null);
        mNameEditText.setOnFocusChangeListener(this);

        mNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    mNameEditText.requestFocus();

                    InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mNameEditText.getWindowToken(), 0);
                    return true;
                }
                return false;
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

        if (DailyUserPreference.getInstance(mContext).isBenefitAlarm() == true)
        {
            mBenefitCheckBox.setVisibility(View.GONE);
        } else
        {
            mBenefitCheckBox.setVisibility(View.VISIBLE);
        }

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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.confirmView:
            {
                String phoneNumber = null;
                String email = null;
                String name = null;
                String birthday = mBirthdayEditText.getText().toString().trim();

                if (mPhoneLayout.getVisibility() == View.VISIBLE)
                {
                    String tag = (String) mCountryEditText.getTag();

                    if (DailyTextUtils.isTextEmpty(tag) == true)
                    {
                        tag = Util.DEFAULT_COUNTRY_CODE;
                    }

                    phoneNumber = mPhoneEditText.getText().toString().trim();

                    if (DailyTextUtils.isTextEmpty(phoneNumber) == true)
                    {
                        phoneNumber = null;
                    } else
                    {
                        String countryCode = tag.substring(tag.indexOf('\n') + 1);
                        phoneNumber = String.format(Locale.KOREA, "%s %s", countryCode, phoneNumber);
                    }
                }

                if (mEmailLayout.getVisibility() == View.VISIBLE)
                {
                    email = mEmailEditText.getText().toString().trim();
                }

                if (mNameLayout.getVisibility() == View.VISIBLE)
                {
                    name = mNameEditText.getText().toString().trim();
                }

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

                ((OnEventListener) mOnEventListener).onUpdateUserInformation(phoneNumber, email, name //
                    , birthday, mBenefitCheckBox.isChecked(), month);
                break;
            }

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

                if (mBenefitCheckBox.getVisibility() == View.VISIBLE)
                {
                    mBenefitCheckBox.setChecked(isChecked);
                }
                break;
            }

            case R.id.fourteenCheckBox:
            case R.id.personalCheckBox:
            case R.id.termsCheckBox:
            case R.id.benefitCheckBox:
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (mBenefitCheckBox.getVisibility() == View.VISIBLE)
                {
                    if (mFourteenCheckBox.isChecked() == true && mTermsOfPrivacyCheckBox.isChecked() == true//
                        && mTermsOfServiceCheckBox.isChecked() == true && mBenefitCheckBox.isChecked() == true)
                    {
                        mAllAgreementCheckBox.setChecked(true);
                    } else
                    {
                        mAllAgreementCheckBox.setChecked(false);
                    }
                } else
                {
                    if (mFourteenCheckBox.isChecked() == true && mTermsOfPrivacyCheckBox.isChecked() == true && mTermsOfServiceCheckBox.isChecked() == true)
                    {
                        mAllAgreementCheckBox.setChecked(true);
                    } else
                    {
                        mAllAgreementCheckBox.setChecked(false);
                    }
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
            case R.id.phoneEditText:
                setFocusLabelView(mPhoneView, mPhoneEditText, hasFocus);
                break;

            case R.id.emailEditText:
                setFocusLabelView(mEmailView, mEmailEditText, hasFocus);
                break;

            case R.id.nameEditText:
                setFocusLabelView(mNameView, mNameEditText, hasFocus);
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

    public void setCountryCode(String countryCode)
    {
        if (DailyTextUtils.isTextEmpty(countryCode) == true)
        {
            return;
        }

        String previousCountryCode = (String) mCountryEditText.getTag();

        // 지역이 변경되면 전화번호 초기화
        if (countryCode.equalsIgnoreCase(previousCountryCode) == false)
        {
            mPhoneEditText.setText(null);
        }

        mCountryEditText.setText(countryCode.substring(0, countryCode.indexOf('\n')));
        mCountryEditText.setTag(countryCode);

        if (mTextWatcher != null)
        {
            mPhoneEditText.removeTextChangedListener(mTextWatcher);
        }

        if (Util.DEFAULT_COUNTRY_CODE.equalsIgnoreCase(countryCode) == true)
        {
            mTextWatcher = new PhoneNumberKoreaFormattingTextWatcher(mContext);
        } else
        {
            mTextWatcher = new PhoneNumberFormattingTextWatcher();
        }

        mPhoneEditText.addTextChangedListener(mTextWatcher);
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

    public void showPhoneLayout()
    {
        mPhoneLayout.setVisibility(View.VISIBLE);
    }

    public void hidePhoneLayout()
    {
        mPhoneLayout.setVisibility(View.GONE);
    }

    public void showEmailLayout()
    {
        mEmailLayout.setVisibility(View.VISIBLE);
    }

    public void hideEmailLayout()
    {
        mEmailLayout.setVisibility(View.GONE);
    }

    public void showBirthdayLayout()
    {
        mBirthdayView.setVisibility(View.VISIBLE);
        mBirthdayEditText.setVisibility(View.VISIBLE);
    }

    public void hideBirthdayLayout()
    {
        mBirthdayView.setVisibility(View.GONE);
        mBirthdayEditText.setVisibility(View.GONE);
    }

    public void showNameLayout()
    {
        mNameLayout.setVisibility(View.VISIBLE);
    }

    public void hideNameLayout()
    {
        mNameLayout.setVisibility(View.GONE);
    }

    public void setEmailText(String email)
    {
        mEmailEditText.setText(email);
        mEmailEditText.setSelection(mEmailEditText.length());
    }

    public void setNameText(String name)
    {
        mNameEditText.setText(name);
        mNameEditText.setSelection(mNameEditText.length());
    }

    public String getName()
    {
        return mNameEditText.getText().toString();
    }

    public void setBirthdayText(int year, int month, int dayOfMonth)
    {
        Calendar calendar = DailyCalendar.getInstance();
        calendar.set(year, month, dayOfMonth, 0, 0, 0);

        mBirthdayEditText.setText(String.format(Locale.KOREA, "%4d.%02d.%02d", year, month + 1, dayOfMonth));
        mBirthdayEditText.setTag(calendar);
    }

    public Calendar getBirthday()
    {
        Object object = mBirthdayEditText.getTag();
        return object != null ? (Calendar) object : null;
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

    public int getPrivacyYear()
    {
        if (mYearCheckBox5.isChecked() == true)
        {
            return 5;
        } else if (mYearCheckBox3.isChecked() == true)
        {
            return 3;
        } else
        {
            return 1;
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
