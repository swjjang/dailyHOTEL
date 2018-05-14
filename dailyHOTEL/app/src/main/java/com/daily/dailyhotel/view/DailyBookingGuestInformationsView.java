package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingGuestInformationDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewLeftNameRightEditMobileDataBinding;
import com.twoheart.dailyhotel.databinding.DailyViewLeftNameRightEditValueDataBinding;

import java.util.ArrayList;
import java.util.List;

public class DailyBookingGuestInformationsView extends ConstraintLayout
{
    private DailyViewBookingGuestInformationDataBinding mViewDataBinding;

    private List<Pair<InformationType, ViewDataBinding>> mInformationTypeList;

    OnGuestInformationsClickListener mOnGuestInformationsClickListener;

    public enum InformationType
    {
        NAME(20, InputType.TYPE_CLASS_TEXT),
        LAST_NAME(25, InputType.TYPE_TEXT_VARIATION_URI),
        FIRST_NAME(40, InputType.TYPE_TEXT_VARIATION_URI),
        EMAIL(50, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
        MOBILE(20, InputType.TYPE_CLASS_PHONE);

        private int mMaxLength;
        private int mInputType;

        InformationType(int maxLength, int inputType)
        {
            mMaxLength = maxLength;
            mInputType = inputType;
        }

        public int getMaxLength()
        {
            return mMaxLength;
        }

        public int getInputType()
        {
            return mInputType;
        }
    }

    public interface OnGuestInformationsClickListener
    {
        void onMobileClick(String mobile);
    }

    public DailyBookingGuestInformationsView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingGuestInformationsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingGuestInformationsView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mInformationTypeList = new ArrayList<>();
        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_guest_information_data, this, true);
    }

    public void setOnGuestInformationsClickListener(OnGuestInformationsClickListener listener)
    {
        mOnGuestInformationsClickListener = listener;
    }

    public void setTitle(CharSequence title, CharSequence condition)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(title);
        mViewDataBinding.conditionTextView.setText(condition);
    }

    public void setTitle(@StringRes int titleResId, @StringRes int conditionResId)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.titleTextView.setText(titleResId);
        mViewDataBinding.conditionTextView.setText(conditionResId);
    }

    public void setGuideTextVisible(boolean visible)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setGuideText(CharSequence text)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideTextView.setText(text);
    }

    public void setGuideText(@StringRes int resid)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.guideTextView.setText(resid);
    }

    public String getInformationTypeValue(InformationType informationType)
    {
        if (mViewDataBinding == null || informationType == null)
        {
            return null;
        }

        for (Pair<InformationType, ViewDataBinding> pair : mInformationTypeList)
        {
            if (pair.first == informationType)
            {
                if (informationType == InformationType.MOBILE)
                {
                    return ((DailyViewLeftNameRightEditMobileDataBinding) pair.second).mobileEditText.getText().toString().trim();
                } else
                {
                    return ((DailyViewLeftNameRightEditValueDataBinding) pair.second).valueEditText.getText().toString().trim();
                }
            }
        }

        return null;
    }

    public void removeAllInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mInformationTypeList.clear();

        if (mViewDataBinding.informationLayout.getChildCount() > 0)
        {
            mViewDataBinding.informationLayout.removeAllViews();
        }
    }

    /**
     * @param informationType 중복 불가 기존에 있는 경우에는 후에 들어가는 것이 무시됨.
     * @param name
     * @param value
     * @param hint
     */
    public void addInformation(InformationType informationType, String name, String value, String hint)
    {
        if (mViewDataBinding == null || informationType == null)
        {
            return;
        }

        for (Pair<InformationType, ViewDataBinding> pair : mInformationTypeList)
        {
            if (pair.first == informationType)
            {
                return;
            }
        }

        switch (informationType)
        {
            case NAME:
            case LAST_NAME:
            case FIRST_NAME:
            case EMAIL:
            {
                DailyViewLeftNameRightEditValueDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
                    , R.layout.daily_view_left_name_right_edit_value_data, mViewDataBinding.informationLayout, true);

                dataBinding.nameTextView.setText(name);
                dataBinding.valueEditText.setText(value);
                dataBinding.valueEditText.addTextChangedListener(new TextWatcher()
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
                        if (s == null || s.length() == 0)
                        {
                            dataBinding.valueHintTextView.setVisibility(View.VISIBLE);
                        } else
                        {
                            dataBinding.valueHintTextView.setVisibility(View.GONE);
                        }
                    }
                });

                dataBinding.valueHintTextView.setText(hint);
                dataBinding.valueEditText.setInputType(informationType.getInputType());

                setValueTextMaxLength(dataBinding.valueEditText, informationType.getMaxLength());

                mInformationTypeList.add(new Pair<>(informationType, dataBinding));
                break;
            }

            case MOBILE:
            {
                mInformationTypeList.add(new Pair<>(informationType, createMobileTypeView(name, value, hint)));
                break;
            }
        }
    }

    public void updateInformation(InformationType informationType, String value)
    {
        if (mViewDataBinding == null || informationType == null)
        {
            return;
        }

        ViewDataBinding viewDataBinding = null;

        for (Pair<InformationType, ViewDataBinding> pair : mInformationTypeList)
        {
            if (pair.first == informationType)
            {
                viewDataBinding = pair.second;
                break;
            }
        }

        if (viewDataBinding == null)
        {
            return;
        }

        if (informationType == InformationType.MOBILE)
        {
            ((DailyViewLeftNameRightEditMobileDataBinding) viewDataBinding).mobileEditText.setText(value);
        } else
        {
            ((DailyViewLeftNameRightEditValueDataBinding) viewDataBinding).valueEditText.setText(value);
        }
    }

    private ViewDataBinding createMobileTypeView(String name, String value, String hint)
    {
        DailyViewLeftNameRightEditMobileDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext())//
            , R.layout.daily_view_left_name_right_edit_mobile_data, mViewDataBinding.informationLayout, true);

        dataBinding.nameTextView.setText(name);

        dataBinding.mobileEditText.addTextChangedListener(new TextWatcher()
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
                if (s == null || s.length() == 0)
                {
                    dataBinding.valueHintTextView.setVisibility(View.VISIBLE);
                } else
                {
                    dataBinding.valueHintTextView.setVisibility(View.GONE);
                }
            }
        });

        dataBinding.valueHintTextView.setText(hint);
        dataBinding.mobileEditText.setText(value);
        dataBinding.mobileEditText.setInputType(InformationType.MOBILE.getInputType());

        setValueTextMaxLength(dataBinding.mobileEditText, InformationType.MOBILE.getMaxLength());

        dataBinding.mobileEditText.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus == true)
                {
                    if (mOnGuestInformationsClickListener != null)
                    {
                        mOnGuestInformationsClickListener.onMobileClick(dataBinding.mobileEditText.getText().toString());
                    }
                } else
                {
                    dataBinding.mobileEditText.setSelected(false);
                }
            }
        });

        dataBinding.fakeMobileView.setFocusable(true);
        dataBinding.fakeMobileView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (dataBinding.mobileEditText.isSelected() == true)
                {
                    if (mOnGuestInformationsClickListener != null)
                    {
                        mOnGuestInformationsClickListener.onMobileClick(dataBinding.mobileEditText.getText().toString());
                    }
                } else
                {
                    dataBinding.mobileEditText.requestFocus();
                    dataBinding.mobileEditText.setSelected(true);
                }
            }
        });

        return dataBinding;
    }

    private void setValueTextMaxLength(EditText editText, final int maxLength)
    {
        if (editText == null || maxLength < 0)
        {
            return;
        }

        InputFilter[] inputFilters = editText.getFilters();

        if (inputFilters == null)
        {
            InputFilter[] newInputFilters = new InputFilter[1];
            newInputFilters[0] = new InputFilter.LengthFilter(maxLength);
            editText.setFilters(newInputFilters);
        } else
        {
            final int length = inputFilters.length;
            boolean hasLengthFilter = false;

            for (int i = 0; i < length; i++)
            {
                if (inputFilters[i] instanceof InputFilter.LengthFilter)
                {
                    hasLengthFilter = true;
                    inputFilters[i] = new InputFilter.LengthFilter(maxLength);
                    break;
                }
            }

            if (hasLengthFilter == false)
            {
                InputFilter[] newInputFilters = new InputFilter[length + 1];
                System.arraycopy(inputFilters, 0, newInputFilters, 0, length);

                newInputFilters[newInputFilters.length - 1] = new InputFilter.LengthFilter(maxLength);

                editText.setFilters(newInputFilters);
            }
        }
    }
}
