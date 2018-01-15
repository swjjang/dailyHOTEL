package com.daily.dailyhotel.screen.common.dialog.refund;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.dailyhotel.base.BaseMultiWindowActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by android_sam on 2018. 1. 10..
 */

public class AutoRefundDialogActivity extends BaseMultiWindowActivity<AutoRefundDialogPresenter>
{
    public static final String INTENT_EXTRA_DATA_CANCEL_TYPE = "cancelType";
    public static final String INTENT_EXTRA_DATA_CANCEL_TYPE_NAME = "cancelTypeName";
    public static final String INTENT_EXTRA_DATA_CANCEL_MESSAGE = "cancelMessage";

    public static Intent newInstance(Context context, int cancelType, String cancelMessage)
    {
        Intent intent = new Intent(context, AutoRefundDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_CANCEL_TYPE, cancelType);
        intent.putExtra(INTENT_EXTRA_DATA_CANCEL_MESSAGE, cancelMessage);
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
    protected AutoRefundDialogPresenter createInstancePresenter()
    {
        return new AutoRefundDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.hold);
    }
}
