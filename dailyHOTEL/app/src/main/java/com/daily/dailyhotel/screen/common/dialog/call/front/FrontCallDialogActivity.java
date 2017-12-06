package com.daily.dailyhotel.screen.common.dialog.call.front;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class FrontCallDialogActivity extends BaseActivity<FrontCallDialogPresenter>
{
    public static final String INTENT_EXTRA_DATA_PHONE = "phone";
    public static final String INTENT_EXTRA_DATA_TITLE = "title";

    public static Intent newInstance(Context context, String phone, String title)
    {
        Intent intent = new Intent(context, FrontCallDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PHONE, phone);
        intent.putExtra(INTENT_EXTRA_DATA_TITLE, title);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected FrontCallDialogPresenter createInstancePresenter()
    {
        return new FrontCallDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
