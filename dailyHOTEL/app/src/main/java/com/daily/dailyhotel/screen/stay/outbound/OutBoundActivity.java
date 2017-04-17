package com.daily.dailyhotel.screen.stay.outbound;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.daily.base.BasePresenter;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class OutBoundActivity extends BaseActivity<OutBoundPresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, OutBoundActivity.class);
        return intent;
    }

    public static Intent newInstance(Context context, String deepLink)
    {
        Intent intent = new Intent(context, OutBoundActivity.class);
        intent.putExtra(INTENT_EXTRA_DATA_DEEPLINK, deepLink);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold);

        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    protected BasePresenter createInstancePresenter()
    {
        return new OutBoundPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
