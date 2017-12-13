package com.daily.dailyhotel.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.daily.dailyhotel.storage.preference.DailyRemoteConfigPreference;
import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.databinding.LayoutReceiptProviderDataBinding;

/**
 * Created by android_sam on 2017. 12. 13..
 */

public class DailyReceiptProviderView extends ConstraintLayout
{
    private LayoutReceiptProviderDataBinding mDataBinding;

    public DailyReceiptProviderView(Context context)
    {
        super(context);
        initLayout(context);
    }

    public DailyReceiptProviderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initLayout(context);
    }

    public DailyReceiptProviderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context)
    {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context) //
            , R.layout.layout_receipt_provider_data, this, true);

        String phone = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyPhoneNumber();
        String fax = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyFax();
        String address = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyAddress();
        String ceoName = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyCEO();
        String registrationNo = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyBizRegNumber();
        String companyName = DailyRemoteConfigPreference.getInstance(getContext()).getRemoteConfigCompanyName();
        // 상호

        mDataBinding.nameTextView.setText(context.getString(R.string.label_receipt_business_license, companyName, ceoName, phone, fax));

        // 주소
        mDataBinding.addressTextView.setText(context.getString(R.string.label_receipt_address, address));

        // 등록번호
        mDataBinding.registrationNoTextView.setText(context.getString(R.string.label_receipt_registeration_number, registrationNo));
    }
}
