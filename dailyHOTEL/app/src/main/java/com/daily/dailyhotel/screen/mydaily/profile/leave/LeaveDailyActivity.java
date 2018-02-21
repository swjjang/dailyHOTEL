package com.daily.dailyhotel.screen.mydaily.profile.leave;


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
public class LeaveDailyActivity extends BaseActivity<LeaveDailyPresenter>
{
    protected static final int REQUEST_CODE_REWARD = 10000;
    protected static final int REQUEST_CODE_WEB = 10001;
    protected static final int REQUEST_CODE_LEAVE_REASON = 10002;

    public static Intent newInstance(Context context)
    {
        Intent intent = new Intent(context, LeaveDailyActivity.class);

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
    protected LeaveDailyPresenter createInstancePresenter()
    {
        return new LeaveDailyPresenter(this);
    }

    @Override
    public void finish()
    {
        super.finish();

        overridePendingTransition(R.anim.hold, R.anim.slide_out_right);
    }
}
