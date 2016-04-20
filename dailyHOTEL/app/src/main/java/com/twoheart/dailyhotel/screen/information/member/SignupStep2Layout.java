package com.twoheart.dailyhotel.screen.information.member;

import android.content.Context;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.place.base.BaseLayout;
import com.twoheart.dailyhotel.place.base.OnBaseEventListener;
import com.twoheart.dailyhotel.util.PhoneNumberKoreaFormattingTextWatcher;
import com.twoheart.dailyhotel.util.Util;
import com.twoheart.dailyhotel.widget.DailyToolbarLayout;

public class SignupStep2Layout extends BaseLayout implements OnClickListener, View.OnFocusChangeListener
{
    private View mVerificationLayout, mSignUpView, mCertificationNumberView;
    private View mCountryView, mPhoneView, mVerificationView;
    private EditText mCountryEditText, mPhoneEditText, mVerificationEditText;
    private CheckBox mSMSCheckBox;
    private TextWatcher mTextWatcher;

    public interface OnEventListener extends OnBaseEventListener
    {
        void showCountryCodeList();

        void doVerification(String phoneNumber);

        void doSignUp(String phoneNumber, String verificationNumber);
    }

    public SignupStep2Layout(Context context, OnEventListener mOnEventListener)
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
        dailyToolbarLayout.initToolbar(mContext.getString(R.string.actionbar_title_signup_2_activity), new OnClickListener()
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
        mCountryView = view.findViewById(R.id.countryView);
        mCountryEditText = (EditText) view.findViewById(R.id.countryEditText);
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

        mPhoneView = view.findViewById(R.id.phoneView);
        mPhoneEditText = (EditText) view.findViewById(R.id.phoneEditText);
        mPhoneEditText.setOnFocusChangeListener(this);
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

                    if (Util.isValidatePhoneNumber(phoneNumber) == true && mSMSCheckBox.isChecked() == true)
                    {
                        ((OnEventListener) mOnEventListener).doVerification(phoneNumber);
                    }
                }

                return false;
            }
        });

        // 알단 테스트
        mPhoneEditText.addTextChangedListener(new TextWatcher()
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
                provenCertificationButton(s.toString());
            }
        });

        mCertificationNumberView = view.findViewById(R.id.certificationNumberView);
        mCertificationNumberView.setOnClickListener(this);
        mCertificationNumberView.setEnabled(false);

        mVerificationLayout = view.findViewById(R.id.verificationLayout);
        mVerificationLayout.setVisibility(View.INVISIBLE);
        mVerificationView = mVerificationLayout.findViewById(R.id.verificationView);

        mVerificationEditText = (EditText) mVerificationLayout.findViewById(R.id.verificationEditText);
        mVerificationEditText.setOnFocusChangeListener(this);

        mSignUpView = view.findViewById(R.id.signUpView);
        mSignUpView.setOnClickListener(this);
        mSignUpView.setVisibility(View.INVISIBLE);

        mPhoneEditText.requestFocus();
    }

    private void initLayoutCheckBox(View view)
    {
        mSMSCheckBox = (CheckBox) view.findViewById(R.id.smsCheckBox);
        mSMSCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                provenCertificationButton(mPhoneEditText.getText().toString());
            }
        });
    }

    public void setCountryCode(String countryCode)
    {
        if (Util.isTextEmpty(countryCode) == true)
        {
            return;
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

        provenCertificationButton(mPhoneEditText.getText().toString());
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signUpView:
                String tag = (String) mCountryEditText.getTag();

                if (Util.isTextEmpty(tag) == true)
                {
                    tag = Util.DEFAULT_COUNTRY_CODE;
                }

                String countryCode = tag.substring(tag.indexOf('\n') + 1);
                String phoneNumber = String.format("%s %s", countryCode, mPhoneEditText.getText().toString().trim());
                String verificationNumber = mVerificationEditText.getText().toString().trim();

                ((OnEventListener) mOnEventListener).doSignUp(phoneNumber, verificationNumber);
                break;

            case R.id.certificationNumberView:
            {
                if (mSMSCheckBox.isChecked() == false)
                {
                    // SMS 수신 동의에 체크해주셔야 합니다
                    return;
                }

                // SMS 인증 요청
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

    private void resetFocus()
    {
        mPhoneView.setSelected(false);
        mVerificationView.setSelected(false);
    }

    private void setFocusTextView(int id)
    {
        resetFocus();

        switch (id)
        {
            case R.id.phoneEditText:
                mPhoneView.setSelected(true);
                break;

            case R.id.verificationEditText:
                mVerificationView.setSelected(true);
                break;
        }
    }

    private void provenCertificationButton(String phoneNumber)
    {
        String tag = (String) mCountryEditText.getTag();

        if (Util.isTextEmpty(tag) == true)
        {
            tag = Util.DEFAULT_COUNTRY_CODE;
        }

        // 입력한 전화번호가 이상이 없는 경우 인증번호 받기가 활성화 된다.
        String countryCode = tag.substring(tag.indexOf('\n') + 1);

        if (Util.isValidatePhoneNumber(countryCode + ' ' + phoneNumber) == true && mSMSCheckBox.isChecked() == true)
        {
            mCertificationNumberView.setEnabled(true);
        } else
        {
            mCertificationNumberView.setEnabled(false);
        }
    }
}
