package com.daily.dailyhotel.screen.common.dialog.call.restaurant;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RestaurantCallDialogActivity extends BaseActivity<RestaurantCallDialogPresenter>
{
    public static final String INTENT_EXTRA_DATA_PHONE = "phone";

    public static Intent newInstance(Context context, String phone)
    {
        Intent intent = new Intent(context, RestaurantCallDialogActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_PHONE, phone);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected RestaurantCallDialogPresenter createInstancePresenter()
    {
        return new RestaurantCallDialogPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();
    }
}
