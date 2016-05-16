package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.graphics.Paint;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.EdgeEffectColor;
import com.twoheart.dailyhotel.util.PhoneNumberKoreaFormattingTextWatcher;
import com.twoheart.dailyhotel.util.StringFilter;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class AddProfileSocialLayout extends BaseLayout implements OnClickListener, View.OnFocusChangeListener
{
    private static final int MAX_OF_RECOMMENDER = 45;

    private View mPhoneLayout, mEmailLayout, mNameLayout;
    private View mEmailView, mNameView, mRecommenderView;
    private EditText mEmailEditText, mNameEditText, mRecommenderEditText;
    private CheckBox mAllAgreementCheckBox;
    private CheckBox mTermsOfServiceCheckBox;
    private CheckBox mTermsOfPrivacyCheckBox;

    private View mConfirmView;
    private View mCountryView, mPhoneView;
    private EditText mCountryEditText, mPhoneEditText;
    private TextWatcher mTextWatcher;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showTermOfService();

        void showTermOfPrivacy();

        void showCountryCodeList();

        void onUpdateUserInformation(String phoneNumber, String email, String name, String recommender);
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
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollLayout);
        EdgeEffectColor.setEdgeGlowColor(scrollView, mContext.getResources().getColor(R.color.over_scroll_edge));

        mPhoneLayout = view.findViewById(R.id.phoneLayout);
        mEmailLayout = view.findViewById(R.id.emailLayout);
        mNameLayout = view.findViewById(R.id.nameLayout);

        mCountryView = mPhoneLayout.findViewById(R.id.countryView);
        mCountryEditText = (EditText) mPhoneLayout.findViewById(R.id.countryEditText);
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
        mPhoneEditText = (EditText) mPhoneLayout.findViewById(R.id.phoneEditText);
        mPhoneEditText.setOnFocusChangeListener(this);

        mConfirmView = view.findViewById(R.id.confirmView);
        mConfirmView.setOnClickListener(this);

        mEmailView = mEmailLayout.findViewById(R.id.emailView);
        mEmailEditText = (EditText) mEmailLayout.findViewById(R.id.emailEditText);
        mEmailEditText.setOnFocusChangeListener(this);

        mNameView = mNameLayout.findViewById(R.id.nameView);
        mNameEditText = (EditText) mNameLayout.findViewById(R.id.nameEditText);
        mNameEditText.setOnFocusChangeListener(this);

        mRecommenderView = view.findViewById(R.id.recommenderView);
        mRecommenderEditText = (EditText) view.findViewById(R.id.recommenderEditText);
        mRecommenderEditText.setOnFocusChangeListener(this);

        // 회원 가입시 이름 필터 적용.
        StringFilter stringFilter = new StringFilter(mContext);
        InputFilter[] allowAlphanumericHangul = new InputFilter[2];
        allowAlphanumericHangul[0] = stringFilter.allowAlphanumericHangul;
        allowAlphanumericHangul[1] = new InputFilter.LengthFilter(20);

        mNameEditText.setFilters(allowAlphanumericHangul);

        // 추천코드 최대 길이
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(MAX_OF_RECOMMENDER);
        mRecommenderEditText.setFilters(fArray);
    }

    private void initLayoutCheckBox(View view)
    {
        mAllAgreementCheckBox = (CheckBox) view.findViewById(R.id.allAgreementCheckBox);
        mTermsOfPrivacyCheckBox = (CheckBox) view.findViewById(R.id.personalCheckBox);
        mTermsOfServiceCheckBox = (CheckBox) view.findViewById(R.id.termsCheckBox);

        mAllAgreementCheckBox.setOnClickListener(this);
        mTermsOfPrivacyCheckBox.setOnClickListener(this);
        mTermsOfServiceCheckBox.setOnClickListener(this);

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

                ((OnEventListener) mOnEventListener).onUpdateUserInformation(phoneNumber, email, name, recommender);
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
                break;
            }

            case R.id.personalCheckBox:
            case R.id.termsCheckBox:
            {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                if (mTermsOfPrivacyCheckBox.isChecked() == true && mTermsOfServiceCheckBox.isChecked() == true)
                {
                    mAllAgreementCheckBox.setChecked(true);
                } else
                {
                    mAllAgreementCheckBox.setChecked(false);
                }
                break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus == false)
        {
            return;
        }

        setFocusTextView(v.getId());
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

    public void showNameLayout()
    {
        mNameLayout.setVisibility(View.VISIBLE);
    }

    public void hideNameLayout()
    {
        mNameLayout.setVisibility(View.GONE);
    }

    private void resetFocus()
    {
        mPhoneView.setSelected(false);
        mEmailView.setSelected(false);
        mNameView.setSelected(false);
        mRecommenderView.setSelected(false);
    }

    private void setFocusTextView(int id)
    {
        resetFocus();

        switch (id)
        {
            case R.id.phoneEditText:
                mPhoneView.setSelected(true);
                break;

            case R.id.emailEditText:
                mEmailView.setSelected(true);
                break;

            case R.id.nameEditText:
                mNameView.setSelected(true);
                break;

            case R.id.recommenderEditText:
                mRecommenderView.setSelected(true);
                break;
        }
    }
}
