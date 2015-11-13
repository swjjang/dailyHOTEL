/**
 * Copyright (c) 2014 Daily Co., Ltd. All rights reserved.
 * <p>
 * CreditListFragment (적립금 내역 화면)
 * <p>
 * 적립금 내역 리스트를 보여주는 화면이다.
 *
 * @version 1
 * @author Mike Han(mike@dailyhotel.co.kr)
 * @since 2014-02-24
 */
package com.twoheart.dailyhotel.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.twoheart.dailyhotel.R;
import com.twoheart.dailyhotel.util.ContryCodeNumber;
import com.twoheart.dailyhotel.view.CountryCodeListLayout;

public class CountryCodeListActivity extends BaseActivity
{
    public static final String INTENT_EXTRA_COUNTRY_CODE = "countryCode";

    private CountryCodeListLayout mCountryCodeListLayout;

    public interface OnUserActionListener
    {
        public void selectCountry(String[] country);
    }

    ;

    public static Intent newInstance(Context context, String selectedCountryCode)
    {
        Intent intent = new Intent(context, CountryCodeListActivity.class);
        intent.putExtra(INTENT_EXTRA_COUNTRY_CODE, selectedCountryCode);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        if (intent != null)
        {
            String countryCode = intent.getStringExtra(INTENT_EXTRA_COUNTRY_CODE);

            initLayout(countryCode);
        } else
        {
            finish();
            return;
        }
    }

    private void initLayout(String countryCode)
    {
        mCountryCodeListLayout = new CountryCodeListLayout(this);
        setContentView(mCountryCodeListLayout.createView());
        setActionBar(R.string.label_select_country);

        mCountryCodeListLayout.setOnUserActionListener(mOnUserActionListener);

        ContryCodeNumber contryCodeNumber = new ContryCodeNumber();
        mCountryCodeListLayout.setData(contryCodeNumber.getCountryValue(), countryCode);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_out_right);
    }

    private OnUserActionListener mOnUserActionListener = new OnUserActionListener()
    {
        @Override
        public void selectCountry(String[] country)
        {
            Intent intent = new Intent();
            intent.putExtra(INTENT_EXTRA_COUNTRY_CODE, country[0] + "\n" + country[1]);
            setResult(RESULT_OK, intent);
            finish();
        }
    };
}
