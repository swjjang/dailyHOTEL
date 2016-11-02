package com.twoheart.dailyhotel.screen.information.member;

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

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.DailyCalendar;
import com.twoheart.dailyhotel.util.DailyPreference;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.PhoneNumberKoreaFormattingTextWatcher;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyEditText;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

import java.util.Calendar;

public class AddProfileSocialLayout extends BaseLayout implements OnClickListener, View.OnFocusChangeListener
{
    private static final int MAX_OF_RECOMMENDER = 45;

    private View mPhoneLayout, mEmailLayout, mNameLayout;
    private View mEmailView, mNameView, mBirthdayView, mRecommenderView;
    private DailyEditText mEmailEditText, mNameEditText, mBirthdayEditText, mRecommenderEditText;
    private CheckBox mAllAgreementCheckBox;
    private CheckBox mTermsOfServiceCheckBox;
    private CheckBox mTermsOfPrivacyCheckBox;
    private CheckBox mBenefitCheckBox;

    private View mPhoneView;
    private DailyEditText mCountryEditText, mPhoneEditText;
    private TextWatcher mTextWatcher;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showTermOfService();

        void showTermOfPrivacy();

        void showCountryCodeList();

        void showBirthdayDatePicker(int year, int month, int day);

        void onUpdateUserInformation(String phoneNumber, String email, String name, String recommender, String birthday, boolean isBenefit);
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
        View toolbar = view.findViewById(R.id.toolbar);
        DailyToolbarLayout dailyToolbarLayout = new DailyToolbarLayout(mContext, toolbar);
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_userinfo_update_activity), new OnClickListener()
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
        final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.default_over_scroll_edge));

        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mEmailLayout = view.findViewById(R.id.emailLayout);
        mNameLayout = view.findViewById(R.id.nameLayout);

        mCountryEditText = (DailyEditText) mPhoneLayout.findViewById(R.id.countryEditText);
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
        mPhoneEditText = (DailyEditText) mPhoneLayout.findViewById(R.id.phoneEditText);
        mPhoneEditText.setDeleteButtonVisible(true, null);
        mPhoneEditText.setOnFocusChangeListener(this);

        View confirmView = view.findViewById(R.id.confirmView);
        confirmView.setOnClickListener(this);

        mEmailView = mEmailLayout.findViewById(R.id.emailView);
        mEmailEditText = (DailyEditText) mEmailLayout.findViewById(R.id.emailEditText);
        mEmailEditText.setDeleteButtonVisible(true, null);
        mEmailEditText.setOnFocusChangeListener(this);

        mNameView = mNameLayout.findViewById(R.id.nameView);
        mNameEditText = (DailyEditText) mNameLayout.findViewById(R.id.nameEditText);
        mNameEditText.setDeleteButtonVisible(true, null);
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
        mBirthdayEditText = (DailyEditText) view.findViewById(R.id.birthdayEditText);
        mBirthdayEditText.setDeleteButtonVisible(true, new DailyEditText.OnDeleteTextClickListener()
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

        mRecommenderView = view.findViewById(R.id.recommenderView);
        mRecommenderEditText = (DailyEditText) view.findViewById(R.id.recommenderEditText);
        mRecommenderEditText.setDeleteButtonVisible(true, null);
        mRecommenderEditText.setOnFocusChangeListener(this);

        // 회원 가입시 이름 필터 적용.
        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowAlphanumericHangul = new InputFilter[2];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
        allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

        mNameEditText.setFilters(allowAlphanumericHangul);

        // 추천인 코드 최대 길이
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(MAX_OF_RECOMMENDER);
        mRecommenderEditText.setFilters(fArray);
    }

    private void initLayoutCheckBox(View view)
    {
        mAllAgreementCheckBox = (CheckBox) view.findViewById(R.id.allAgreementCheckBox);
        mTermsOfPrivacyCheckBox = (CheckBox) view.findViewById(R.id.personalCheckBox);
        mTermsOfServiceCheckBox = (CheckBox) view.findViewById(R.id.termsCheckBox);
        mBenefitCheckBox = (CheckBox) view.findViewById(R.id.benefitCheckBox);

        mAllAgreementCheckBox.setOnClickListener(this);
        mTermsOfPrivacyCheckBox.setOnClickListener(this);
        mTermsOfServiceCheckBox.setOnClickListener(this);
        mBenefitCheckBox.setOnClickListener(this);

        if (DailyPreference.getInstance(mContext).isUserBenefitAlarm() == true)
        {
            mBenefitCheckBox.setVisibility(View.GONE);
        } else
        {
            mBenefitCheckBox.setVisibility(View.VISIBLE);
        }

        TextView termsContentView = (TextView) view.findViewById(R.id.termsContentView);
        termsContentView.setPaintFlags(termsContentView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        termsContentView.setOnClickListener(this);

        TextView personalContentView = (TextView) view.findViewById(R.id.personalContentView);
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
                String recommender = mRecommenderEditText.getText().toString().trim();
                String birthday = mBirthdayEditText.getText().toString().trim();

                if (mPhoneLayout.getVisibility() == View.VISIBLE)
                {
                    String tag = (String) mCountryEditText.getTag();

                    if (Util.isTextEmpty(tag) == true)
                    {
                        tag = Util.DEFAULT_COUNTRY_CODE;
                    }

                    phoneNumber = mPhoneEditText.getText().toString().trim();

                    if (Util.isTextEmpty(phoneNumber) == true)
                    {
                        phoneNumber = null;
                    } else
                    {
                        String countryCode = tag.substring(tag.indexOf('\n') + 1);
                        phoneNumber = String.format("%s %s", countryCode, phoneNumber);
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

                if (Util.isTextEmpty(birthday) == false)
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

                ((OnEventListener) mOnEventListener).onUpdateUserInformation(phoneNumber, email, name, recommender, birthday, mBenefitCheckBox.isChecked());
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

                mTermsOfServiceCheckBox.setChecked(isChecked);
                mTermsOfPrivacyCheckBox.setChecked(isChecked);

                if (mBenefitCheckBox.getVisibility() == View.VISIBLE)
                {
                    mBenefitCheckBox.setChecked(isChecked);
                }
                break;
            }

            case R.id.personalCheckBox:
            case R.id.termsCheckBox:
            case R.id.benefitCheckBox:
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (mBenefitCheckBox.getVisibility() == View.VISIBLE)
                {
                    if (mTermsOfPrivacyCheckBox.isChecked() == true && mTermsOfServiceCheckBox.isChecked() == true && mBenefitCheckBox.isChecked() == true)
                    {
                        mAllAgreementCheckBox.setChecked(true);
                    } else
                    {
                        mAllAgreementCheckBox.setChecked(false);
                    }
                } else
                {
                    if (mTermsOfPrivacyCheckBox.isChecked() == true && mTermsOfServiceCheckBox.isChecked() == true)
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

            case R.id.recommenderEditText:
                setFocusLabelView(mRecommenderView, mRecommenderEditText, hasFocus);
                break;
        }
    }

    public void setCountryCode(String countryCode)
    {
        if (Util.isTextEmpty(countryCode) == true)
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

    public void showBirthdaylLayout()
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

        mBirthdayEditText.setText(String.format("%4d.%02d.%02d", year, month + 1, dayOfMonth));
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
}
