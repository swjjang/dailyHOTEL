package com.daily.dailyhotel.screen.common.dialog.email.receipt;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class EmailDialogActivity extends BaseActivity<EmailDialogPresenter>
{
    public static final String INTENT_EXTRA_DATA_EMAIL = "email";

    public static Intent newInstance(Context context, String email)
    {
        Intent intent = new Intent(context, EmailDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_EMAIL, email);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected EmailDialogPresenter createInstancePresenter()
    {
        return new EmailDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
