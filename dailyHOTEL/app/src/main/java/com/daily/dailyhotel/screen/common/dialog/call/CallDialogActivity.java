package com.daily.dailyhotel.screen.common.dialog.call;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class CallDialogActivity extends BaseActivity<CallDialogPresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, CallDialogActivity.class);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected CallDialogPresenter createInstancePresenter()
    {
        return new CallDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
