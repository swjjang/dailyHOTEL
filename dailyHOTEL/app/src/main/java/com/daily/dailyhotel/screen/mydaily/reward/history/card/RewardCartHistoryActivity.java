package com.daily.dailyhotel.screen.mydaily.reward.history.card;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.daily.base.BaseActivity;
import com.twoheart.dailyhotel.R;

/**
 * Created by sheldon
 * Clean Architecture
 */
public class RewardCartHistoryActivity extends BaseActivity<RewardCardHistoryPresenter>
{
    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, RewardCartHistoryActivity.class);

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
    protected RewardCardHistoryPresenter createInstancePresenter()
    {
        return new RewardCardHistoryPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
