package com.daily.dailyhotel.screen.mydaily.reward;


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
public class RewardActivity extends BaseActivity<RewardPresenter>
{
    static final int REQUEST_CODE_LOGIN = 10000;
    static final int REQUEST_CODE_WEB = 10001;
    static final int REQUEST_CODE_REWARD_HISTORY = 10002;
    static final int REQUEST_CODE_REWARD_CARD_HISTORY = 10003;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, RewardActivity.class);

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
    protected RewardPresenter createInstancePresenter()
    {
        return new RewardPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
