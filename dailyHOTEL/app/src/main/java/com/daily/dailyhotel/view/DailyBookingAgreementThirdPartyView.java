package com.daily.dailyhotel.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingAgreementThirdPartyDataBinding;
import com.twoheart.dailyhotel.util.Constants;

public class DailyBookingAgreementThirdPartyView extends LinearLayout
{
    DailyViewBookingAgreementThirdPartyDataBinding mViewDataBinding;

    OnAgreementClickListener mOnAgreementClickListener;

    Constants.ServiceType mServiceType;

    public interface OnAgreementClickListener
    {
        void onExpandInformationClick();

        void onCollapseInformationClick();

        void onAgreementClick(boolean isChecked);
    }

    public DailyBookingAgreementThirdPartyView(Context context)
    {
        super(context);

        initLayout(context, null);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context, attrs);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context, attrs);
    }

    private void initLayout(Context context, AttributeSet attrs)
    {
        setOrientation(VERTICAL);

        mViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.daily_view_booking_agreement_third_party_data, this, true);

        mViewDataBinding.arrowImageView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnAgreementClickListener == null)
                {
                    return;
                }

                if (mViewDataBinding.thirdPartyTermsLayout.getVisibility() == VISIBLE)
                {
                    mOnAgreementClickListener.onCollapseInformationClick();
                } else
                {
                    mOnAgreementClickListener.onExpandInformationClick();
                }
            }
        });

        mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.GONE);

        mViewDataBinding.agreeThirdPartyTermsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (mOnAgreementClickListener != null)
                {
                    mOnAgreementClickListener.onAgreementClick(isChecked);
                }
            }
        });

        mViewDataBinding.agreeThirdPartyTermsTextView.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mViewDataBinding.agreeThirdPartyTermsCheckBox.performClick();
            }
        });

        if (context != null && attrs != null)
        {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.dailyBookingAgreeThirdParty);
            if (typedArray.hasValue(R.styleable.dailyBookingAgreeThirdParty_serviceType) == true)
            {
                int serviceType = typedArray.getInt(R.styleable.dailyBookingAgreeThirdParty_serviceType, 0);
                setServiceType(serviceType);
            }
        }

        setPersonalInformationText04(mServiceType);
    }

    public void setOnAgreementClickListener(OnAgreementClickListener listener)
    {
        mOnAgreementClickListener = listener;
    }

    public void setVendorBusinessName(String businessName)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.vendorBusinessNameTextView.setText(businessName);
    }

    public void expandInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.arrowImageView.setRotation(-180);
        mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.VISIBLE);
    }


    public void collapseInformation()
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        mViewDataBinding.arrowImageView.setRotation(0);
        mViewDataBinding.thirdPartyTermsLayout.setVisibility(View.GONE);
    }

    private void setServiceType(int serviceType)
    {
        switch (serviceType)
        {
            case 2: // OB_STAY:
                mServiceType = Constants.ServiceType.OB_STAY;
                break;

            case 1: // GOURMET:
                mServiceType = Constants.ServiceType.GOURMET;
                break;

            case 0: // HOTEL:
            default:
                mServiceType = Constants.ServiceType.HOTEL;
                break;
        }
    }

    public void setPersonalInformationText04(Constants.ServiceType serviceType)
    {
        if (mViewDataBinding == null)
        {
            return;
        }

        int resId;
        if (Constants.ServiceType.OB_STAY == serviceType)
        {
            resId = R.string.message_payment_agree_personal_information04_type_ob;
        } else
        {
            resId = R.string.message_payment_agree_personal_information04;
        }

        mViewDataBinding.personalInformationTextView04.setText(resId);
    }
}
