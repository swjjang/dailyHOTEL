package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.DailyViewBookingAgreementThirdPartyDataBinding;

public class DailyBookingAgreementThirdPartyView extends LinearLayout
{
    private DailyViewBookingAgreementThirdPartyDataBinding mViewDataBinding;

    private OnAgreementClickListener mOnAgreementClickListener;

    public interface OnAgreementClickListener
    {
        void onExpandInformationClick();

        void onCollapseInformationClick();

        void onAgreementClick(boolean isChecked);
    }

    public DailyBookingAgreementThirdPartyView(Context context)
    {
        super(context);

        initLayout(context);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initLayout(context);
    }

    public DailyBookingAgreementThirdPartyView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        initLayout(context);
    }

    private void initLayout(Context context)
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
}
