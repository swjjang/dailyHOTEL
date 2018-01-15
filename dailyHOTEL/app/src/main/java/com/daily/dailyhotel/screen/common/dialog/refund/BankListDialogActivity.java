package com.daily.dailyhotel.screen.common.dialog.refund;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.base.BaseMultiWindowActivity;
import com.daily.dailyhotel.parcel.BankParcel;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class BankListDialogActivity extends BaseMultiWindowActivity<BankListDialogPresenter>
{
    public static final String INTENT_EXTRA_DATA_SELECTED_BANK = "selectedBank";

    public static Intent newInstance(Context context, BankParcel selectedBank)
    {
        Intent intent = new Intent(context, BankListDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_SELECTED_BANK, selectedBank);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.hold, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected BankListDialogPresenter createInstancePresenter()
    {
        return new BankListDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }


}
